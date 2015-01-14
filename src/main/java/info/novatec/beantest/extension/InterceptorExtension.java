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

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * InterceptorExtension changes the meta data of {@link javax.interceptor.Interceptor} (known as CDI InterceptorBinding)
 * and {@link javax.interceptor.Interceptors} (known as EJB Interceptor) instances.
 * @param <X> the type of the annotated type
 * @author Qaiser Abbasi (qaiser.abbasi@novatec-gmbh.de)
 */
public class InterceptorExtension<X> extends BaseExtension<X> {

    private static final EjbInterceptorWrapper INTERCEPTOR_WRAPPER_ANNOTATION_INSTANCE =
            AnnotationInstanceProvider.of(EjbInterceptorWrapper.class);

    /**
     * Replaces the meta data of the {@link javax.enterprise.inject.spi.ProcessAnnotatedType}.
     *
     * <p>
     * The ProcessAnnotatedType's meta data will be replaced, if the annotated type hold the following annotation:
     * <ul>
     * <li> {@link javax.interceptor.Interceptors}
     * </ul>
     *
     * @param pat the annotated type representing the class being processed
     */
    public void onProcessEjbInterceptor(
            @Observes @WithAnnotations(Interceptors.class) ProcessAnnotatedType<X> pat) {
        processAnnotatedType = pat;
        new EjbInterceptorModification().modifyEjbInterceptor();
    }

    /**
     * Replaces the meta data of the {@link javax.enterprise.inject.spi.ProcessAnnotatedType}.
     *
     * <p>
     * The ProcessAnnotatedType's meta data will be replaced, if the annotated type hold the following annotation
     * <ul>
     * <li> {@link javax.interceptor.Interceptor}
     * </ul>
     *
     * @param pat the annotated type representing the class being processed
     */
    public void onProcessInterceptorBindingBean(
            @Observes @WithAnnotations(Interceptor.class) ProcessAnnotatedType<X> pat) {
        processAnnotatedType = pat;
        modifyInterceptorBindingBean();
    }

    /**
     * Adds {@link javax.inject.Inject} annotation to all the dependencies declarations in the interceptor binding.
     *
     * @param pat
     *            the process annotated type.
     */
    private void modifyInterceptorBindingBean() {
        AnnotatedTypeBuilder<X> builder = createTypeBuilderFromProcessedType();
        addInjectAnnotationInAnnotatedType(processAnnotatedType.getAnnotatedType(), builder);
        processAnnotatedType.setAnnotatedType(builder.create());
    }

    /**
     * Comprises the transformation logic regarding the modification of {@link javax.interceptor.Interceptors}
     * bindings.
     */
    private class EjbInterceptorModification {

        private final AnnotatedTypeBuilder<X> annotatedTypeBuilder = createTypeBuilderFromProcessedType();

        private final EjbInterceptorWrapperImpl.EjbInterceptorWrapperRepository ejbInterceptorWrapperRepository =
                EjbInterceptorWrapperImpl.EjbInterceptorWrapperRepository.getInstance();

        private final EjbInterceptorWrapperImpl.EjbInterceptorWrapperBinding ejbInterceptorWrapperBinding =
                initializeInterceptorWrapperBinding();

        /**
         * Mounts the InterceptorBinding {@link EjbInterceptorWrapper} on the processed bean in order to be able to call
         * modified EJB interceptor bindings.
         * <br>
         * Creates modified {@link javax.enterprise.inject.spi.AnnotatedType} instances of the corresponding
         * {@link Interceptors} bindings and persist it into {@link EjbInterceptorWrapperImpl}.
         * <br>
         * Ultimately the origin Interceptors annotation will be replaced with InterceptorWrapper.
         * @see {@link #modifyInterceptorBindings(javax.enterprise.inject.spi.AnnotatedType,
         * org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder)}
         * @see {@link EjbInterceptorWrapperImpl}
         */
        public void modifyEjbInterceptor() {
            if (isClassLevelAnnotationPresent()) {
                modifyClassLevelInterceptor();
            }

            if (isMethodLevelAnnotationPresent()) {
                modifyMethodLevelInterceptor();
            }

            saveInterceptorWrapperBinding();

            processAnnotatedType.setAnnotatedType(annotatedTypeBuilder.create());
        }

        private void saveInterceptorWrapperBinding() {
            ejbInterceptorWrapperRepository.addInterceptorWrapperBinding(getJavaClassFromProcessedTyped(),
                    ejbInterceptorWrapperBinding);
        }

        private EjbInterceptorWrapperImpl.EjbInterceptorWrapperBinding initializeInterceptorWrapperBinding() {
            EjbInterceptorWrapperImpl.EjbInterceptorWrapperBinding wrapperBinding = new EjbInterceptorWrapperImpl
                    .EjbInterceptorWrapperBinding();
            wrapperBinding.setInterceptedClazz(processAnnotatedType.getClass());
            return wrapperBinding;
        }

        private boolean isClassLevelAnnotationPresent() {
            return getAnnotatedTypeFromProcessedType().isAnnotationPresent(Interceptors.class);
        }

        private boolean isMethodLevelAnnotationPresent() {
            Set<AnnotatedMethod<? super X>> methods = getAnnotatedTypeFromProcessedType().getMethods();
            for (AnnotatedMethod annotatedMethod: methods) {
                if (annotatedMethod.isAnnotationPresent(Interceptors.class)) {
                    return true;
                }
            }
            return false;
        }

        private void modifyMethodLevelInterceptor() {
            Set<AnnotatedMethod<? super X>> methodsFromAnnotatedType = getAnnotatedTypeFromProcessedType().getMethods();

            for (final AnnotatedMethod originMethod: methodsFromAnnotatedType) {
                if (originMethod.isAnnotationPresent(Interceptors.class)) {
                    annotatedTypeBuilder.removeFromMethod(originMethod, Interceptors.class);
                    annotatedTypeBuilder.addToMethod(originMethod, INTERCEPTOR_WRAPPER_ANNOTATION_INSTANCE);
                    ejbInterceptorWrapperBinding.addMethodLevelBinding(originMethod.getJavaMember(),
                            createModifiedInterceptorBindings(retrieveMethodLevelInterceptorAnnotation(originMethod)));
                }
            }
        }

        private Interceptors retrieveMethodLevelInterceptorAnnotation(AnnotatedMethod originMethod) {
            return originMethod.getAnnotation(Interceptors.class);
        }

        /**
         * Mounts the InterceptorBinding {@link EjbInterceptorWrapper} on the processed bean in order to be able to call
         * modified EJB interceptor bindings.
         * <br>
         * Creates modified {@link AnnotatedType} instances of the corresponding {@link Interceptors} bindings and persist
         * it into {@link EjbInterceptorWrapperImpl}.
         * <br>
         * Ultimately the origin Interceptors annotation will be replaced with InterceptorWrapper.
         * @see {@link #modifyEjbInterceptor()}
         * @see {@link #getModifiedInterceptorBindings(AnnotatedType)}
         */
        private void modifyClassLevelInterceptor() {
            annotatedTypeBuilder.removeFromClass(Interceptors.class);
            annotatedTypeBuilder.addToClass(INTERCEPTOR_WRAPPER_ANNOTATION_INSTANCE);
            ejbInterceptorWrapperBinding.addClassLevelBinding(createModifiedInterceptorBindings(
                    retrieveClassLevelInterceptorAnnotation()));
        }

        private Interceptors retrieveClassLevelInterceptorAnnotation() {
            return getAnnotatedTypeFromProcessedType().getAnnotation(Interceptors.class);
        }

        /**
         * Modifies the interceptor bindings i.e. injection points of the provided {@link AnnotatedType}.
         * In order to cut unneeded interceptor modifications, the modified interceptor bindings will be stored inside
         * a cache in {@link EjbInterceptorWrapperImpl}.
         * <br>
         * <br>
         * <b>@SuppressWarnings</b>: this method suppresses the following warnings types: rawtypes and unchecked due the
         * fact that the retrieval of the interceptor bindings
         * via {@link Interceptors#value()} forces to use raw type {@link Class} objects.
         * @param annotatedType the processed {@link AnnotatedType}
         * @return list of modified interceptor bindings
         */
        @SuppressWarnings({ "rawtypes", "unchecked" })
        private Set<AnnotatedType> createModifiedInterceptorBindings(Interceptors interceptorsAnnotation) {
            Class[] interceptorBindings = interceptorsAnnotation.value();
            Set<AnnotatedType> modifiedInterceptorClasses = new LinkedHashSet<AnnotatedType>();
            AnnotatedType modifiedInterceptorInstance;

            for (Class originInterceptor : interceptorBindings) {
                modifiedInterceptorInstance = addInjectAnnotationInRawClass(originInterceptor);
                modifiedInterceptorClasses.add(modifiedInterceptorInstance);
            }

            return modifiedInterceptorClasses;
        }

    }
}
