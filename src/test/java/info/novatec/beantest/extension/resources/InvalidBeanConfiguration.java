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
package info.novatec.beantest.extension.resources;

import javax.annotation.Resource;
import javax.enterprise.inject.Vetoed;

/**
 * Represents an invalid DI configuration due to the duplicate injection point configuration for field and method.
 * @author Qaiser Abbasi (qaiser.abbasi@novatec-gmbh.de)
 */
@Vetoed
public class InvalidBeanConfiguration {
    @Resource private Object someField;

    @Resource public void setSomeField(Object argument) {
        this.someField = argument;
    }
}
