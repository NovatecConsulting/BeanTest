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

import javax.ejb.Stateless;
import javax.enterprise.inject.Vetoed;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * This EJB holds an invalid dependency configuration in order to test the deployment functionality of {@link info.novatec.beantest.extension.BaseExtension}.
 * <p>
 * This bean is currently vetoed due to the fact that this bean definition causes a deployment exception during the bean container bootstrapping phase.
 * As soon there is a solution to enable alternative beans per test case this bean altogether with its corresponding test case will be reactivated.
 * @see info.novatec.beantest.demo.api.extension.ejb.TestInvalidInjectionPointConfiguration
 * @see https://www.github.com/NovaTecConsulting/BeanTest/pull/6
 * @author Qaiser Abbasi (qaiser.abbasi@novatec-gmbh.de)
 */
@Stateless
@Vetoed
public class InvalidInjectionPointConfigurationEJB {

	@PersistenceContext
	EntityManager em;
	
	@PersistenceContext
	public void setEm(EntityManager em) {
		this.em = em;
	}
}
