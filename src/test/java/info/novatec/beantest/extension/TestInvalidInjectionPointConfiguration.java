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

package info.novatec.beantest.extension;

import info.novatec.beantest.api.BaseBeanTest;
import info.novatec.beantest.extension.resources.InvalidInjectionPointConfigurationEJB;
import org.jboss.weld.exceptions.DefinitionException;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This test stress the correct deployment functionality of {@link BaseExtension} regarding injection point definitions of an given EJB.
 * Due the fact that the injected bean holds an invalid dependency configuration {@link BaseExtension} will throw an {@link org.jboss.weld.exceptions.DeploymentException}.
 * @author Qaiser Abbasi (qaiser.abbasi@novatec-gmbh.de)
 * @see https://github.com/NovaTecConsulting/BeanTest/pull/6
 */
public class TestInvalidInjectionPointConfiguration extends BaseBeanTest {

	/**
	 * Ignore reason: described in {@link info.novatec.beantest.demo.api.extension.ejb.resources.InvalidInjectionPointConfigurationEJB}
	 */
	@Test(expected = DefinitionException.class)
	@Ignore
	public void shouldThrowDeploymentException() {
		getBean(InvalidInjectionPointConfigurationEJB.class);
	}
}
