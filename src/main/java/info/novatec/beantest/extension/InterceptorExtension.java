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
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.interceptor.Interceptor;
import javax.interceptor.Interceptors;
import java.util.ArrayList;
import static info.novatec.beantest.extension.InterceptorWrapperImpl.InterceptorWrapperData.*;
import java.util.List;

/**
 * InterceptorExtension changes the meta data of {@link javax.interceptor.Interceptor} (known as CDI InterceptorBinding)
 * and {@link javax.interceptor.Interceptors} (known as EJB Interceptor) instances.
 * @param <X> the type of the annotated type
 * @author Qaiser Abbasi (qaiser.abbasi@novatec-gmbh.de)
 */
public class InterceptorExtension<X> extends BaseExtension<X> {

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
        modifyInterceptorsBean(pat);
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
        modifyInterceptorBindingBean(pat);
    }

    /**
     * Adds {@link javax.inject.Inject} annotation to all the dependencies of the interceptor.
     *
     * @param pat
     *            the process annotated type.
     */
    private void modifyInterceptorBindingBean(ProcessAnnotatedType<X> pat) {
        AnnotatedTypeBuilder<X> builder = createTypeBuilderFrom(pat.getAnnotatedType());
        addInjectAnnotationOnProcessedType(pat.getAnnotatedType(), builder);
        pat.setAnnotatedType(builder.create());
    }

    /**
     * Mounts the InterceptorBinding {@link InterceptorWrapper} on the processed bean in order to be able to call modified EJB interceptor bindings.
     * <br>
     * Creates modified {@link javax.enterprise.inject.spi.AnnotatedType} instances of the corresponding {@link Interceptors} bindings and persist it into {@link InterceptorWrapperImpl}.
     * <br>
     * Ultimately the origin Interceptors annotation will be replaced with InterceptorWrapper.
     * @see {@link #modifyInterceptorBindings(javax.enterprise.inject.spi.AnnotatedType, org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder)}
     * @see {@link InterceptorWrapperImpl}
     * @param pat the processed {@link javax.enterprise.inject.spi.AnnotatedType}
     */
    private void modifyInterceptorsBean(ProcessAnnotatedType<X> pat) {
        AnnotatedType<X> annotatedType = pat.getAnnotatedType();
        AnnotatedTypeBuilder<X> typeBuilder = createTypeBuilderFrom(annotatedType);
        modifyInterceptorBindings(annotatedType, typeBuilder);
        pat.setAnnotatedType(typeBuilder.create());
    }

    /**
     * Mounts the InterceptorBinding {@link InterceptorWrapper} on the processed bean in order to be able to call modified EJB interceptor bindings.
     * <br>
     * Creates modified {@link AnnotatedType} instances of the corresponding {@link Interceptors} bindings and persist it into {@link InterceptorWrapperImpl}.
     * <br>
     * Ultimately the origin Interceptors annotation will be replaced with InterceptorWrapper.
     * @see {@link #modifyInterceptorsBean(ProcessAnnotatedType)}
     * @see {@link #getModifiedInterceptorBindings(AnnotatedType)}
     * @param annotatedType the processed {@link AnnotatedType}
     * @param typeBuilder
     */
    private void modifyInterceptorBindings(AnnotatedType<X> annotatedType, AnnotatedTypeBuilder<X> typeBuilder) {
        InterceptorWrapper globalInterceptor = AnnotationInstanceProvider.of(InterceptorWrapper.class);

        typeBuilder.removeFromClass(Interceptors.class);
        typeBuilder.addToClass(globalInterceptor);
        addInterceptedClassWithModifiedInterceptorBindings(
            annotatedType.getJavaClass(),
            getModifiedInterceptorBindings(annotatedType));
    }

    /**
     * Modifies the interceptor bindings i.e. injection points of the provided {@link AnnotatedType}.
     * In order to cut unneeded interceptor modifications, the modified interceptor bindings will be stored inside a cache in {@link InterceptorWrapperImpl}.
     * <br>
     * <br>
     * <b>@SuppressWarnings</b>: this method suppresses the following warnings types: rawtypes and unhecked due the fact that the retrieval of the interceptor bindings
     * via {@link Interceptors#value()} forces to use raw type {@link Class} objects.
     * @param annotatedType the processed {@link AnnotatedType}
     * @return list of modified interceptor bindings
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<AnnotatedType> getModifiedInterceptorBindings(AnnotatedType<X> annotatedType) {
        Interceptors interceptorsAnnotation = annotatedType.getAnnotation(Interceptors.class);
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

    private AnnotatedType createModifiedInterceptor(Class originInterceptor) {
        AnnotatedTypeBuilder typeBuilder = new AnnotatedTypeBuilder().readFromType(originInterceptor);
        AnnotatedType modifiedInterceptor = typeBuilder.create();
        addInjectAnnotationOnProcessedType(modifiedInterceptor, typeBuilder);
        return modifiedInterceptor;
    }
}
