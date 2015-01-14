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
import info.novatec.beantest.extension.resources.EJBWithExcludedClassInterception;
import info.novatec.beantest.extension.resources.ExcludedClassLevelWithMethodInterception;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Asserting different level of interceptor excluding functionality.
 * @see info.novatec.beantest.extension.EjbInterceptorWrapperImpl
 * @author Qaiser Abbasi (qaiser.abbasi@novatec-gmbh.de)
 */
public class TestExcludedInterceptors extends BaseBeanTest {

    @Before
    public void setUp() throws Exception {
        DummyInterceptor.isInvoked = false;
        DummyInterceptor2.isInvoked = false;
    }

    @Test
    public void shouldNotInvokeSurroundingInterceptor() {
        EJBWithExcludedClassInterception ejbWithExcludedClassInterception = getBean(
                EJBWithExcludedClassInterception.class);
        ejbWithExcludedClassInterception.business();
        Assert.assertThat(DummyInterceptor.isInvoked, is(false));
    }

    @Test
    public void assertThatClassLevelInterceptorIsExcludedAndMethodLevelInterceptorIsCalled() throws Exception {
        ExcludedClassLevelWithMethodInterception bean = getBean(ExcludedClassLevelWithMethodInterception.class);
        bean.business();
        Assert.assertThat(DummyInterceptor.isInvoked, is(false));
        Assert.assertThat(DummyInterceptor2.isInvoked, is(true));
    }
}
