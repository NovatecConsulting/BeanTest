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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.inject.spi.AnnotatedType;
import javax.interceptor.InterceptorBinding;
import javax.interceptor.Interceptors;

/**
 * Defines a {@link InterceptorBinding} for processed {@link AnnotatedType} beans with {@link Interceptors} bindings.
 * The origin EJB Interceptors bindings will be removed on the processed bean.
 * <br>
 * This is done due to the fact that the origin Interceptors bindings respectively the declared injection points
 * in the corresponding Interceptors bindings are not modifiable.
 * <br>
 * @author Qaiser Abbasi (qaiser.abbasi@novatec-gmbh.de)
 * @see InterceptorWrapperImpl
 * @see https://github.com/NovaTecConsulting/BeanTest/issues/3
 */
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface InterceptorWrapper {
}