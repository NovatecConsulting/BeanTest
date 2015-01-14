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
import info.novatec.beantest.extension.resources.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * This test verifies that EJBs with EJB interceptor bindings are processed correctly by BeanTestExtension.
 * @see EjbInterceptorWrapperImpl
 * @see BaseExtension
 * @author Qaiser Abbasi (qaiser.abbasi@novatec-gmbh.de)
 * 
 */
public class TestInterceptedBean extends BaseBeanTest {

    @Before
    public void setUp() throws Exception {
        MyInterceptor.isInvoked = false;
        MyInterceptor2.isInvoked = false;
    }

    @Test
	public void shouldInjectDependenciesInSurroundingInterceptorOnStatelessBean() {
		MyInterceptedStatelessBean bean = getBean(MyInterceptedStatelessBean.class);
		Assert.assertNotNull(bean);
		bean.businessMethod();
        assertThat(MyInterceptor.isInvoked, is(true));
	}
	
	@Test
	public void shouldInjectDependenciesInSurroundingInterceptorOnDefaultScopedBean() {
		MyInterceptedDefaultScopeBean bean = getBean(MyInterceptedDefaultScopeBean.class);
		Assert.assertNotNull(bean);
		bean.businessMethod();
        assertThat(MyInterceptor.isInvoked, is(true));
	}
	
	@Test
	public void testMultipleInterceptorBindings() {
		MyEJBServiceWithMultipleInterceptorBinding bean = getBean(MyEJBServiceWithMultipleInterceptorBinding.class);
		Assert.assertNotNull(bean);
		bean.businessMethod();
        assertThat(MyInterceptor2.isInvoked, is(true));
	}
	
}
