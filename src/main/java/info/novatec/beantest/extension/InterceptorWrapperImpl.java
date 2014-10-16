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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Inject;
import javax.interceptor.*;

/**
 * {@link InterceptorWrapperImpl} proxies Interceptor (e.g. @AroundInvoke) pointcuts via modified Interceptor instances i.e. the modification here relates to the
 * injection point transformation based in {@link BaseExtension}.
 * <br> 
 * <br> 
 * <b>@SuppressWarnings</b>: this method suppresses the following warnings types: rawtypes and unhecked due the fact
 * that the retrieval of the interceptor bindings
 * via {@link Interceptors#value()} forces to use raw type {@link Class} objects. 
 * @author Qaiser Abbasi (qaiser.abbasi@novatec-gmbh.de)
 * @see BaseExtension
 * @see https://docs.jboss.org/weld/reference/latest/en-US/html/extend.html#_the_literal_injectiontarget_literal_interface
 */
@InterceptorWrapper
@Interceptor
@SuppressWarnings(value = {"rawtypes", "unchecked", "JavadocReference"})
class InterceptorWrapperImpl {

	@Inject BeanManager beanManager;
	
	/**
	 * Proxies the @AroundInvoke call on an EJB via internally held (modified) InterceptorBindings. The origin method call will be delegated to the corresponding modified Interceptor instance. 
	 * @param invocationContext the origin {@link InvocationContext}
	 * @return if the invoked modified interceptor returns a result then result will be returned otherwise the origin {@link InvocationContext} will be returned.
	 */
	@AroundInvoke
	public Object handleAroundInvokeInterception(InvocationContext invocationContext) throws Exception {
        Object alteredInvocationContext = null;
        Class interceptedClazz = retrieveInterceptedClazzFor(invocationContext);
        if(! interceptedMethodExcludesInterception(invocationContext) && interceptorBindingsExistsFor(interceptedClazz)) {
			for (AnnotatedType annotatedType : InterceptorWrapperData.MODIFIED_INTERCEPTOR_BINDINGS.get(interceptedClazz)) {
				InjectionTarget injectionTarget = beanManager.createInjectionTarget(annotatedType);
				Object injectedInterceptorInstance = instantiateInterceptorInstanceIn(injectionTarget);
                alteredInvocationContext = delegateInterceptorCall(injectedInterceptorInstance, invocationContext);
				destroyInjectionTarget(injectedInterceptorInstance, injectionTarget);
			}
		}
		return (alteredInvocationContext != null) ? alteredInvocationContext : invocationContext.proceed();
	}

    private boolean interceptedMethodExcludesInterception(InvocationContext invocationContext) {
        return invocationContext.getMethod().isAnnotationPresent(ExcludeClassInterceptors.class);
    }

    /**
	 * Shuts down the container produced Interceptor instance. 
	 * @param annotatedTypeInstance
	 * @param injectionTarget
	 */
	private void destroyInjectionTarget(Object annotatedTypeInstance,
			InjectionTarget injectionTarget) {
		injectionTarget.preDestroy(annotatedTypeInstance);
		injectionTarget.dispose(annotatedTypeInstance);
	}
	
	/**
	 * Produces an instance of the modified Interceptor (AnnotatedType).
	 * @param injectionTarget placeholder for the injected Interceptor instance
	 * @return the container produced instance of the Interceptor
	 */
	private Object instantiateInterceptorInstanceIn(InjectionTarget injectionTarget) {
		/* The creational context of the created Interceptor object is non-contextual */
		CreationalContext creationalContext = beanManager.createCreationalContext(null);
		Object annotatedTypeInstance = injectionTarget.produce(creationalContext);
		injectionTarget.inject(annotatedTypeInstance, creationalContext);
		injectionTarget.postConstruct(annotatedTypeInstance);
		return annotatedTypeInstance;
	}

	/**
	 * Call the Interceptor point cut @AroundInvoke on the modified Interceptor instance.
	 * @param annotatedTypeInstance the container produced Interceptor instance
	 * @param invocationContext the origin {@link InvocationContext}
     * @return if the interceptor invocation comprises a result then the result will be returned.
     * Otherwise the origin {@link InvocationContext} will be returned
	 */
	private Object delegateInterceptorCall(Object annotatedTypeInstance,
			InvocationContext invocationContext) throws Exception {
		for (Method method : annotatedTypeInstance.getClass().getMethods()) {
			if (method.isAnnotationPresent(AroundInvoke.class)) {
				try {
                    Object invocationResult = method.invoke(annotatedTypeInstance, invocationContext);
                    if (invocationResult != null) return invocationResult;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
        }
        return invocationContext.proceed();
    }

	private boolean interceptorBindingsExistsFor(Class interceptedClazz) {
		return InterceptorWrapperData.MODIFIED_INTERCEPTOR_BINDINGS.containsKey(interceptedClazz);
	}

	private Class<?> retrieveInterceptedClazzFor(InvocationContext invocationContext) {
		return invocationContext.getMethod().getDeclaringClass();
	}

	/**
	 * Represents the storage location of the modified InterceptorBindings.
	 * <br>
	 * <br>
	 * The <b>key</b> in {@link #MODIFIED_INTERCEPTOR_BINDINGS} represents the processed EJB class.
	 * <br>
	 * The <b>value</b> in {@link #MODIFIED_INTERCEPTOR_BINDINGS} represents modified InterceptorBindings.
	 * <br> 
	 * <br> 
	 * {@link InterceptorWrapperData} also holds a cache of modified Interceptor (AnnotatedType) instances in {@link #PROCESSED_INTERCEPTOR_TYPES_CACHE}.
	 * @author Qaiser Abbasi (qaiser.abbasi@novatec-gmbh.de)
	 *
	 */
	static class InterceptorWrapperData {
		private static final Map<Class, List<AnnotatedType>> MODIFIED_INTERCEPTOR_BINDINGS = new HashMap<Class, List<AnnotatedType>>();
		private static final Map<Class, AnnotatedType> PROCESSED_INTERCEPTOR_TYPES_CACHE = new HashMap<Class, AnnotatedType>();
		
		static void addInterceptedClassWithModifiedInterceptorBindings(Class interceptedClazz,
				List<AnnotatedType> modifiedInterceptorBindings) {
			if(! MODIFIED_INTERCEPTOR_BINDINGS.containsKey(interceptedClazz)) {
				MODIFIED_INTERCEPTOR_BINDINGS.put(interceptedClazz, modifiedInterceptorBindings);
			}
		}

		static boolean isInterceptorAlreadyModified(Class originInterceptor) {
			return PROCESSED_INTERCEPTOR_TYPES_CACHE.containsKey(originInterceptor);
		}

		static AnnotatedType getModifiedInterceptorFor(Class originInterceptor) {
			return PROCESSED_INTERCEPTOR_TYPES_CACHE.get(originInterceptor);
		}

		static void addOriginInterceptorWithModifiedInterceptor(Class originInterceptor,
				AnnotatedType processedInterceptorType) {
			if(! isInterceptorAlreadyModified(originInterceptor)) {
				PROCESSED_INTERCEPTOR_TYPES_CACHE.put(originInterceptor, processedInterceptorType);
			}
		}
	}
}
