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

import info.novatec.beantest.transactions.Transactional;
import org.apache.deltaspike.core.util.metadata.AnnotationInstanceProvider;
import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

import javax.ejb.MessageDriven;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;

/**
 * This extension adds and changes the bean meta data in order to convert EJB injection points into CDI injection points.
 * Therefore the extension changes the meta data of Beans annotated with {@link javax.ejb.EJB}<br>
 * @param <X> the type of the annotated type
 * @author Qaiser Abbasi (qaiser.abbasi@novatec-gmbh.de)
 */
public class EJBExtension<X> extends BaseExtension<X> {

    /**
     * Replaces the meta data of the {@link ProcessAnnotatedType}.
     *
     * <p>
     * The ProcessAnnotatedType's meta data will be replaced, if the annotated type has one of the following annotations:
     * <ul>
     * <li> {@link javax.ejb.Stateless}
     * <li> {@link javax.ejb.MessageDriven}
     * </ul>
     *
     * @param pat the annotated type representing the class being processed
     */
    public void onProcessStatelessOrMessageDrivenBean(
            @Observes @WithAnnotations({Stateless.class, MessageDriven.class}) ProcessAnnotatedType<X> pat) {
        processAnnotatedType = pat;
        modifyAnnotatedTypeMetaData();
    }

    /**
     * Adds {@link info.novatec.beantest.transactions.Transactional} and {@link javax.enterprise.context.RequestScoped} to the given annotated type and converts
     * its EJB injection points into CDI injection points (i.e. it adds the {@link javax.inject.Inject})
     * <br>
     * Further modifies the interceptor bindings if the given {@link javax.enterprise.inject.spi.AnnotatedType} holds an {@link javax.interceptor.Interceptors} annotation.
     * The modified interceptor bindings will be backed on the processed {@link javax.enterprise.inject.spi.AnnotatedType} via a custom {@link javax.interceptor.InterceptorBinding} interceptor: {@link EjbInterceptorWrapper}.
     * @param pat the process annotated type.
     * @see {@link #modifyInterceptorBindings(ProcessAnnotatedType)}
     * @see {@link EjbInterceptorWrapper}
     */
    private void modifyAnnotatedTypeMetaData() {
        AnnotatedType<X> annotatedType = getAnnotatedTypeFromProcessedType();
        AnnotatedTypeBuilder<X> builder = createTypeBuilderFromProcessedType();
        builder.addToClass(AnnotationInstanceProvider.of(Transactional.class))
               .addToClass(AnnotationInstanceProvider.of(RequestScoped.class));
        addInjectAnnotationInAnnotatedType(annotatedType, builder);
        processAnnotatedType.setAnnotatedType(builder.create());
    }
}
