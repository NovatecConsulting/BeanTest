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

import info.novatec.beantest.api.BaseBeanTest;
import info.novatec.beantest.extension.resources.DummyInterceptor;
import info.novatec.beantest.extension.resources.DummyInterceptor2;
import info.novatec.beantest.extension.resources.EJBInterceptedMethodAndClassLevel;
import info.novatec.beantest.extension.resources.EJBInterceptedMethodLevel;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Qaiser Abbasi (qaiser.abbasi@novatec-gmbh.de)
 */
public class TestEJBInterceptedMethodLevel extends BaseBeanTest {

    @Before
    public void setUp() throws Exception {
        DummyInterceptor.isInvoked = false;
        DummyInterceptor2.isInvoked = false;
    }

    @Test
    public void business_service_call_should_trigger_method_level_interception() throws Exception {
        EJBInterceptedMethodLevel bean = getBean(EJBInterceptedMethodLevel.class);
        bean.businessService();
        assertThat(DummyInterceptor.isInvoked, is(true));
    }

    @Test
    public void business_service_call_should_trigger_method_and_class_level_interception() throws Exception {
        EJBInterceptedMethodAndClassLevel bean = getBean(EJBInterceptedMethodAndClassLevel.class);
        bean.business();
        assertThat(DummyInterceptor.isInvoked, is(true));
        assertThat(DummyInterceptor2.isInvoked, is(true));
    }
}