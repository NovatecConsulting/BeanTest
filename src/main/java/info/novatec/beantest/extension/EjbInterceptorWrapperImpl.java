/*
 * Bean Testing.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.novatec.beantest.extension;

import org.jboss.weld.interceptor.proxy.DefaultInvocationContextFactory;
import org.jboss.weld.interceptor.proxy.InterceptorInvocation;
import org.jboss.weld.interceptor.proxy.SimpleInterceptionChain;
import org.jboss.weld.interceptor.proxy.SimpleInterceptorInvocation;
import org.jboss.weld.interceptor.reader.DefaultMethodMetadata;
import org.jboss.weld.interceptor.spi.metadata.MethodMetadata;
import org.jboss.weld.interceptor.spi.model.InterceptionType;

import java.lang.reflect.Method;
import java.util.*;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Inject;
import javax.interceptor.*;

/**
 * {@link EjbInterceptorWrapperImpl} proxies Interceptor (e.g. @AroundInvoke) pointcuts via modified Interceptor instances
 * i.e. the modification here relates to the injection point transformation based in {@link BaseExtension}.
 * <br> 
 * <br> 
 * <b>@SuppressWarnings</b>: this method suppresses the following warnings types: rawtypes and unchecked due the fact
 * that the retrieval of the interceptor bindings
 * via {@link Interceptors#value()} forces to use raw type {@link Class} objects. 
 * @author Qaiser Abbasi (qaiser.abbasi@novatec-gmbh.de)
 * @see BaseExtension
 * @see https://docs.jboss.org/weld/reference/latest/en-US/html/extend.html#_the_literal_injectiontarget_literal_interface
 */
@EjbInterceptorWrapper
@Interceptor
@SuppressWarnings(value = {"rawtypes", "unchecked"})
class EjbInterceptorWrapperImpl {

    @Inject BeanManager beanManager;

    private InvocationContext originInvocationContext;

    private final EjbInterceptorWrapperRepository ejbInterceptorWrapperRepository =
            EjbInterceptorWrapperRepository.getInstance();

    private Object invocationResult;

	/**
     * Defines the entry point for triggered interceptors on a modified EJB bean. The interceptor method call will be
     * delegated to the corresponding modified Interceptor instances.
	 * @param invocationContext modified {@link InvocationContext} of the intercepted EJB i.e. the context
     * (InterceptorChain) does not hold the originally defined interceptors configuration.
	 * @return invocation result from the processed EjbInterceptorWrapperBindings
     * @see info.novatec.beantest.extension.InterceptorExtension
     * @see info.novatec.beantest.extension.EjbInterceptorWrapperImpl.EjbInterceptorWrapperBinding
     * @see info.novatec.beantest.extension.EjbInterceptorWrapperImpl.InvocationContextModifier
     * @see https://docs.jboss.org/ejb3/docs/tutorial/1.0.7/html/EJB3_Interceptors.html
	 */
	@AroundInvoke
	public Object handleAroundInvokeInterception(InvocationContext invocationContext) throws Exception {
        this.originInvocationContext = invocationContext;

        if(existsInterceptorWrapperBindings()) {
            invokeWrappedInterceptors();
        }

        return invocationResult;
	}

    private boolean existsInterceptorWrapperBindings() {
        return ejbInterceptorWrapperRepository.containsInterceptorWrapperBinding(retrieveInterceptedClass());
    }

    private void invokeWrappedInterceptors() throws Exception {
        invocationResult = new InvocationContextModifier().execute();
    }

    public EjbInterceptorWrapperBinding getInterceptorWrapperBinding() {
        return ejbInterceptorWrapperRepository.getInterceptorWrapperBinding(
                retrieveInterceptedClass());
    }

    private Method getInterceptedMethodFromInvocationContext() {
        return originInvocationContext.getMethod();
    }

	private Class<?> retrieveInterceptedClass() {
		return originInvocationContext.getMethod().getDeclaringClass();
	}

	/**
	 * Represents the storage location of the modified InterceptorBindings.
	 * <br>
	 * <br>
	 * The <b>key</b> in {@link #MODIFIED_CLASS_INTERCEPTOR_BINDINGS} represents the processed EJB class.
	 * <br>
	 * The <b>value</b> in {@link #MODIFIED_CLASS_INTERCEPTOR_BINDINGS} represents modified InterceptorBindings.
	 * <br> 
	 * <br> 
	 * {@link info.novatec.beantest.extension.EjbInterceptorWrapperImpl.EjbInterceptorWrapperRepository} also holds a cache of modified Interceptor (AnnotatedType) instances in
     * {@link #interceptorWrapperCache}.
	 * @author Qaiser Abbasi (qaiser.abbasi@novatec-gmbh.de)
	 *
	 */
	static class EjbInterceptorWrapperRepository {

        private static final EjbInterceptorWrapperRepository INSTANCE = new EjbInterceptorWrapperRepository();

        private final Map<Class, EjbInterceptorWrapperBinding> interceptorWrapperBindings =
                new HashMap<Class, EjbInterceptorWrapperBinding>();

        private EjbInterceptorWrapperRepository() {}

        static EjbInterceptorWrapperRepository getInstance() {
            return INSTANCE;
        }

        public boolean containsInterceptorWrapperBinding(Class interceptedClass) {
            return interceptorWrapperBindings.containsKey(interceptedClass);
        }

        public EjbInterceptorWrapperBinding getInterceptorWrapperBinding(Class interceptedClazz) {
            return interceptorWrapperBindings.get(interceptedClazz);
        }

        public void addInterceptorWrapperBinding(Class interceptedClazz, EjbInterceptorWrapperBinding wrapperBinding) {
            interceptorWrapperBindings.put(interceptedClazz, wrapperBinding);
        }
    }

    /**
     * Holds modified interceptor instances for a given intercepted class. {@link EjbInterceptorWrapperBinding} defines
     * method and class level interceptors.
     */
    static class EjbInterceptorWrapperBinding {

        private Class interceptedClazz;

        private Set<AnnotatedType> classLevelBindings = new LinkedHashSet<AnnotatedType>();

        private Map<Method, Set<AnnotatedType>> methodLevelBindings = new HashMap<Method, Set<AnnotatedType>>();

        public void setInterceptedClazz(Class interceptedClazz) {
            this.interceptedClazz = interceptedClazz;
        }

        public void addClassLevelBinding(Set<AnnotatedType> classLevelBinding) {
            this.classLevelBindings.addAll(classLevelBinding);
        }

        public void addMethodLevelBinding(Method wrappedMethod, Set<AnnotatedType> methodLevelBindings) {
            this.methodLevelBindings.put(wrappedMethod, methodLevelBindings);
        }

        public Set<AnnotatedType> getClassLevelBindings() {
            return Collections.unmodifiableSet(classLevelBindings);
        }

        public Set<AnnotatedType> getMethodLevelBindings(Method originMethod) {
            Set<AnnotatedType> s = methodLevelBindings.get(originMethod);
            return (s != null ) ? Collections.unmodifiableSet(s) : Collections.<AnnotatedType>emptySet();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EjbInterceptorWrapperBinding ejbInterceptorWrapperBinding = (EjbInterceptorWrapperBinding) o;
            if (!interceptedClazz.equals(ejbInterceptorWrapperBinding.interceptedClazz)) return false;
            return true;
        }

        @Override
        public int hashCode() {
            return interceptedClazz.hashCode();
        }

    }

    /**
     * {@link info.novatec.beantest.extension.EjbInterceptorWrapperImpl.InvocationContextModifier} creates a modified
     * InterceptorInvocationContext object altogether with the appropriate InterceptorBindings in the correct invocation
     * order.
     * <br>
     * The inherited InterceptorChain object contains all different level of interceptor bindings (class and method).
     * Technically the approach looks like the following: generate a InterceptorChain object based on the
     * InterceptorWrapperBindings. Create a new InterceptorInvocation via the DefaultInvocationContextFactory.
     * And finally execute the newly created InterceptorInvocationContext.
     */
    private class InvocationContextModifier {

        private Object invocationResult;

        private Object interceptedClazz;

        private Method interceptedMethod;

        private Object[] methodArgs;

        private final List<DestroyInjectionTarget> destroyInjectionTargetList = new ArrayList<DestroyInjectionTarget>();

        /**
         * Executes by basically calling #proceed on the modified InterceptorInvocationContext instance.
         * @return Result from the Interception
         * @throws Exception
         */
        public Object execute() throws Exception {
            setupTargetObject();
            invokeWrappedInterceptors();
            tearDownWrappedInterceptorInstances();
            return invocationResult;
        }

        private void invokeWrappedInterceptors() throws Exception {
            InvocationContext invocationContext = new DefaultInvocationContextFactory().newInvocationContext(
                    createInterceptorChain(), interceptedClazz, interceptedMethod, methodArgs);
            invocationResult = invocationContext.proceed();
        }

        private void setupTargetObject() {
            interceptedClazz = originInvocationContext.getTarget();
            interceptedMethod = getInterceptedMethodFromInvocationContext();
            methodArgs = originInvocationContext.getParameters();
        }

        private void tearDownWrappedInterceptorInstances() {
            for (DestroyInjectionTarget interceptorInstance : destroyInjectionTargetList) {
                interceptorInstance.destroyInjectionTarget();
            }
        }

        private SimpleInterceptionChain createInterceptorChain() {
            List<InterceptorInvocation> simpleInterceptorInvocations = new ArrayList<InterceptorInvocation>();

            if (! isClassLevelInterceptionExcluded()) {
                simpleInterceptorInvocations.addAll(createClassLevelInterceptorInvocations());
            }

            simpleInterceptorInvocations.addAll(createMethodLevelInterceptorInvocations());
            return new SimpleInterceptionChain(simpleInterceptorInvocations);
        }

        private List<InterceptorInvocation> createMethodLevelInterceptorInvocations() {
            return createInterceptorInvocationList(getInterceptorWrapperBinding().getMethodLevelBindings(
                    getInterceptedMethodFromInvocationContext()));
        }

        private List<InterceptorInvocation> createClassLevelInterceptorInvocations() {
            return createInterceptorInvocationList(getInterceptorWrapperBinding().getClassLevelBindings());
        }

        private List<InterceptorInvocation> createInterceptorInvocationList(Set<AnnotatedType> bindings) {
            List<InterceptorInvocation> interceptorInvocations = new ArrayList<InterceptorInvocation>();
            final boolean isTargetClazz = false;

            for (AnnotatedType annotatedType : bindings) {
                interceptorInvocations.add(new SimpleInterceptorInvocation(injectInterceptorInstance(annotatedType),
                    InterceptionType.AROUND_INVOKE, getInterceptorMethods(annotatedType), isTargetClazz));
            }

            return interceptorInvocations;
        }

        private Set<MethodMetadata> getInterceptorMethods(AnnotatedType annotatedType) {
            Set<AnnotatedMethod> methods = annotatedType.getMethods();
            Set<MethodMetadata> interceptorMethods = new LinkedHashSet<MethodMetadata>();

            for (AnnotatedMethod annotatedMethod : methods) {
                if (annotatedMethod.isAnnotationPresent(AroundInvoke.class))
                    interceptorMethods.add(DefaultMethodMetadata.of(annotatedMethod.getJavaMember()));
            }

            return interceptorMethods;
        }

        /**
         * Produces an instance of the modified Interceptor (AnnotatedType) via the underlying CDI bean container.
         * @param injectionTarget placeholder for the injected Interceptor instance
         * @return the container produced instance of the Interceptor
         */
        private Object injectInterceptorInstance(AnnotatedType annotatedType) {
            Contextual defaultCreationContext = null;
            InjectionTarget injectionTarget = beanManager.createInjectionTarget(annotatedType);
            CreationalContext creationalContext = beanManager.createCreationalContext(defaultCreationContext);

            Object annotatedTypeInstance = injectionTarget.produce(creationalContext);
            injectionTarget.inject(annotatedTypeInstance, creationalContext);
            injectionTarget.postConstruct(annotatedTypeInstance);

            destroyInjectionTargetList.add(new DestroyInjectionTarget(annotatedTypeInstance, injectionTarget));
            return annotatedTypeInstance;
        }

        private boolean isClassLevelInterceptionExcluded() {
            return interceptedMethod.isAnnotationPresent(ExcludeClassInterceptors.class);
        }
    }

    /**
     * Holds injected InterceptorWrapper instances all together with the corresponding InjectionTarget objects. After
     * successfully invoking the interceptor point cuts the injected instance will be removed from the bean container.
     */
    private static class DestroyInjectionTarget {

        final Object injectedInstance;

        final InjectionTarget injectionTarget;

        public DestroyInjectionTarget(Object injectedInstance, InjectionTarget injectionTarget){
            this.injectedInstance = injectedInstance;
            this.injectionTarget = injectionTarget;
        }

        /**
         * Shutdown and remove container created interceptor instances from the underlying CDI bean container.
         */
        public void destroyInjectionTarget() {
            injectionTarget.preDestroy(injectedInstance);
            injectionTarget.dispose(injectedInstance);
        }
    }
}
