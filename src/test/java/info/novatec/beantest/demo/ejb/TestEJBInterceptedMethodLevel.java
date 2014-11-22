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

package info.novatec.beantest.demo.ejb;

import info.novatec.beantest.api.BaseBeanTest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class TestEJBInterceptedMethodLevel extends BaseBeanTest {

    @Test
    public void business_service_call_should_trigger_method_level_interception() throws Exception {
        EJBInterceptedMethodLevel ejbInterceptedMethodLevel = getBean(EJBInterceptedMethodLevel.class);
        ejbInterceptedMethodLevel.businessService();
        assertThat(MyMethodLevelInterceptor.isInvoked, is(true));
    }
}