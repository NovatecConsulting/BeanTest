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
import info.novatec.beantest.extension.resources.EJBInterceptedByMultipleInterceptors;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * The purpose of this test is ensure that intercepted methods (holding multiple interceptor bindings) are invoked
 * only time. Further this test asserts that multiple interceptor bindings can join and reuse active transaction.
 * @author Qaiser Abbasi (qaiser.abbasi@novatec-gmbh.de)
 * @see info.novatec.beantest.transactions.TransactionalInterceptor
 */
public class TestEJBInterceptedByMultipleInterceptors extends BaseBeanTest {

    @Test
    public void business_service_call_should_invoke_surrounding_interceptors() {
        EJBInterceptedByMultipleInterceptors bean = getBean(EJBInterceptedByMultipleInterceptors.class);
        bean.business();
        assertThat(bean.getPersistedEntitiesCount(), is(4));
        assertThat(bean.getBusinessInvocationCount(), is(1));
    }

}
