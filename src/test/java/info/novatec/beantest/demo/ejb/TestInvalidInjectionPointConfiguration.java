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

package info.novatec.beantest.demo.ejb;

import info.novatec.beantest.api.BaseBeanTest;
import info.novatec.beantest.extension.BeanTestExtension;

import org.jboss.weld.exceptions.DefinitionException;
import org.jboss.weld.exceptions.DeploymentException;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This test stress the correct deployment functionality of {@link BeanTestExtension} regarding injection point definitions of an given EJB.
 * Due the fact that the injected bean holds an invalid dependency configuration {@link BeanTestExtension} will throw an {@link DeploymentException}.
 * @author Qaiser Abbasi (qaiser.abbasi@novatec-gmbh.de)
 * @see https://github.com/NovaTecConsulting/BeanTest/pull/6
 */
public class TestInvalidInjectionPointConfiguration extends BaseBeanTest {

	/**
	 * Ignore reason: described in {@link InvalidInjectionPointConfigurationEJB}
	 */
	@Test(expected = DefinitionException.class)
	@Ignore
	public void shouldThrowDeploymentException() {
		getBean(InvalidInjectionPointConfigurationEJB.class);
	}
}