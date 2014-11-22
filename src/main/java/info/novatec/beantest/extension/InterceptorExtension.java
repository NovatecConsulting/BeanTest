/*
 *
 *  * Bean Testing.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package info.novatec.beantest.extension;

import org.apache.deltaspike.core.util.metadata.AnnotationInstanceProvider;
import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.interceptor.Interceptor;
import javax.interceptor.Interceptors;
import java.lang.reflect.Method;
import java.util.ArrayList;
import static info.novatec.beantest.extension.InterceptorWrapperImpl.InterceptorWrapperData.*;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * InterceptorExtension changes the meta data of {@link javax.interceptor.Interceptor} (known as CDI InterceptorBinding)
 * and {@link javax.interceptor.Interceptors} (known as EJB Interceptor) instances.
 * @param <X> the type of the annotated type
 * @author Qaiser Abbasi (qaiser.abbasi@novatec-gmbh.de)
 */
//TODO testcase: EJB with @Interceptor and own @InterceptorBinding
public class InterceptorExtension<X> extends BaseExtension<X> {

    private ProcessAnnotatedType<X> processAnnotatedType;
    private static final InterceptorWrapper INTERCEPTOR_WRAPPER = AnnotationInstanceProvider.of(InterceptorWrapper.class);

    /**
     * Replaces the meta data of the {@link javax.enterprise.inject.spi.ProcessAnnotatedType}.
     *
     * <p>
     * The ProcessAnnotatedType's meta data will be replaced, if the annotated type has one of the following annotations:
     * <ul>
     * <li> {@link javax.interceptor.Interceptors}
     * </ul>
     *
     * @param pat the annotated type representing the class being processed
     */
    public void processInterceptorsBeans(
            @Observes @WithAnnotations(Interceptors.class) ProcessAnnotatedType<X> pat) {
        processAnnotatedType = pat;
        modifyInterceptorsBean();
    }

    /**
     * Replaces the meta data of the {@link javax.enterprise.inject.spi.ProcessAnnotatedType}.
     *
     * <p>
     * The ProcessAnnotatedType's meta data will be replaced, if the annotated type has one of the following annotations:
     * <ul>
     * <li> {@link javax.interceptor.Interceptor}
     * </ul>
     *
     * @param pat the annotated type representing the class being processed
     */
    public void processInterceptorBindingBeans(
            @Observes @WithAnnotations(Interceptor.class) ProcessAnnotatedType<X> pat) {
        processAnnotatedType = pat;
        modifyInterceptorBindingBean();
    }

    /**
     * Adds {@link javax.inject.Inject} annotation to all the dependencies of the interceptor.
     *
     * @param pat
     *            the process annotated type.
     */
    private void modifyInterceptorBindingBean() {
        AnnotatedTypeBuilder<X> builder = createTypeBuilderFromProcessedType(processAnnotatedType);
        addInjectAnnotationOnProcessedType(processAnnotatedType.getAnnotatedType(), builder);
        processAnnotatedType.setAnnotatedType(builder.create());
    }

    /**
     * Mounts the InterceptorBinding {@link InterceptorWrapper} on the processed bean in order to be able to call
     * modified EJB interceptor bindings.
     * <br>
     * Creates modified {@link javax.enterprise.inject.spi.AnnotatedType} instances of the corresponding
     * {@link Interceptors} bindings and persist it into {@link InterceptorWrapperImpl}.
     * <br>
     * Ultimately the origin Interceptors annotation will be replaced with InterceptorWrapper.
     * @see {@link #modifyInterceptorBindings(javax.enterprise.inject.spi.AnnotatedType,
     * org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder)}
     * @see {@link InterceptorWrapperImpl}
     */
    private void modifyInterceptorsBean() {
        AnnotatedTypeBuilder<X> annotatedTypeBuilder = createTypeBuilderFromProcessedType(processAnnotatedType);

        if (isClassLevelAnnotationPresent()) {
            modifyClassLevelInterceptorBindings(annotatedTypeBuilder);
        }
        
        if (isMethodLevelAnnotationPresent()) {
            modifyMethodLevelInterceptorBindings(annotatedTypeBuilder);
        }

        //TODO handle ctor level + test case

        processAnnotatedType.setAnnotatedType(annotatedTypeBuilder.create());
    }

    private boolean isMethodLevelAnnotationPresent() {
        Set<AnnotatedMethod<? super X>> methods = processAnnotatedType.getAnnotatedType().getMethods();
        for (AnnotatedMethod annotatedMethod: methods) {
            if (annotatedMethod.isAnnotationPresent(Interceptors.class)) {
                return true;
            }
        }
        return false;
    }

    private void modifyMethodLevelInterceptorBindings(AnnotatedTypeBuilder<X> annotatedTypeBuilder) {
        Set<AnnotatedMethod<? super X>> methods = processAnnotatedType.getAnnotatedType().getMethods();

        for (final AnnotatedMethod annotatedMethod: methods) {
            if (annotatedMethod.isAnnotationPresent(Interceptors.class)) {
                annotatedTypeBuilder.removeFromMethod(annotatedMethod, Interceptors.class);
                annotatedTypeBuilder.addToMethod(annotatedMethod, INTERCEPTOR_WRAPPER);

                InterceptorWrapperImpl.InterceptorWrapperData.addInterceptedClassWithModifiedMethodInterceptorBindings(
                        getJavaClassFromAnnotatedType(),
                        new HashMap<Method, List<AnnotatedType>>(){
                            {
                                put(annotatedMethod.getJavaMember(),
                                    getModifiedInterceptorBindings(annotatedMethod.getAnnotation(Interceptors.class)));
                            }
                        }
                );
            }
        }

    }

    private Class<X> getJavaClassFromAnnotatedType() {
        return processAnnotatedType.getAnnotatedType().getJavaClass();
    }

    private boolean isClassLevelAnnotationPresent() {
        return processAnnotatedType.getAnnotatedType().isAnnotationPresent(Interceptors.class);
    }

    /**
     * Mounts the InterceptorBinding {@link InterceptorWrapper} on the processed bean in order to be able to call
     * modified EJB interceptor bindings.
     * <br>
     * Creates modified {@link AnnotatedType} instances of the corresponding {@link Interceptors} bindings and persist
     * it into {@link InterceptorWrapperImpl}.
     * <br>
     * Ultimately the origin Interceptors annotation will be replaced with InterceptorWrapper.
     * @see {@link #modifyInterceptorsBean()}
     * @see {@link #getModifiedInterceptorBindings(AnnotatedType)}
     * @param annotatedTypeBuilder
     */
    private void modifyClassLevelInterceptorBindings(AnnotatedTypeBuilder<X> annotatedTypeBuilder) {
        annotatedTypeBuilder.removeFromClass(Interceptors.class);
        annotatedTypeBuilder.addToClass(INTERCEPTOR_WRAPPER);
        addInterceptedClassWithModifiedInterceptorBindings(
                getJavaClassFromAnnotatedType(),
                getModifiedInterceptorBindings());
    }

    /**
     * Modifies the interceptor bindings i.e. injection points of the provided {@link AnnotatedType}.
     * In order to cut unneeded interceptor modifications, the modified interceptor bindings will be stored inside
     * a cache in {@link InterceptorWrapperImpl}.
     * <br>
     * <br>
     * <b>@SuppressWarnings</b>: this method suppresses the following warnings types: rawtypes and unchecked due the
     * fact that the retrieval of the interceptor bindings
     * via {@link Interceptors#value()} forces to use raw type {@link Class} objects.
     * @param annotatedType the processed {@link AnnotatedType}
     * @return list of modified interceptor bindings
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<AnnotatedType> getModifiedInterceptorBindings() {
        Interceptors interceptorsAnnotation = processAnnotatedType.getAnnotatedType().getAnnotation(Interceptors.class);
        Class[] interceptorBindings = interceptorsAnnotation.value();
        List<AnnotatedType> modifiedInterceptorClasses = new ArrayList<AnnotatedType>();
        AnnotatedType modifiedInterceptorInstance;

        for (Class originInterceptor : interceptorBindings) {
            if (isInterceptorAlreadyModified(originInterceptor)) {
                modifiedInterceptorInstance = getModifiedInterceptorFor(originInterceptor);
            } else {
                modifiedInterceptorInstance = createModifiedInterceptor(originInterceptor);
                addOriginInterceptorWithModifiedInterceptor(originInterceptor, modifiedInterceptorInstance);
            }
            modifiedInterceptorClasses.add(modifiedInterceptorInstance);
        }
        return modifiedInterceptorClasses;
    }

    private List<AnnotatedType> getModifiedInterceptorBindings(Interceptors interceptorsAnnotation ) {
        Class[] interceptorBindings = interceptorsAnnotation.value();
        List<AnnotatedType> modifiedInterceptorClasses = new ArrayList<AnnotatedType>();
        AnnotatedType modifiedInterceptorInstance;

        for (Class originInterceptor : interceptorBindings) {
            modifiedInterceptorInstance = createModifiedInterceptor(originInterceptor);
            addOriginInterceptorWithModifiedInterceptor(originInterceptor, modifiedInterceptorInstance);
            modifiedInterceptorClasses.add(modifiedInterceptorInstance);
        }
        return modifiedInterceptorClasses;
    }

    private AnnotatedType createModifiedInterceptor(Class originInterceptor) {
        AnnotatedTypeBuilder typeBuilder = new AnnotatedTypeBuilder().readFromType(originInterceptor);
        AnnotatedType modifiedInterceptor = typeBuilder.create();
        addInjectAnnotationOnProcessedType(modifiedInterceptor, typeBuilder);
        return modifiedInterceptor;
    }
}
