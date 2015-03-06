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
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 * This test verifies that an EJB Singleton is instantiated just once and is able to call other EJBs. 
 * 
 * @author Carlos Barragan (carlos.barragan@novatec-gmbh.de)
 */
public class TestSingletonInjection extends BaseBeanTest {
    
    @Test
    public void shouldBeInstantiatedOnce() {
        MyEjbSingleton singleton= getBean(MyEjbSingleton.class);
        assertThat(singleton.wasEjbCalled(), is(false));
        singleton.callAnEjb();
        assertThat(singleton.wasEjbCalled(), is(true));
    }
    
    
}
