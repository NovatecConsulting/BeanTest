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
import info.novatec.beantest.extension.resources.MyEJBService;
import info.novatec.beantest.extension.resources.MyEJBServiceWithEntityManagerSetter;
import info.novatec.beantest.extension.resources.MyOtherEJBService;
import info.novatec.beantest.utils.entities.MyEntity;
import info.novatec.beantest.utils.exceptions.MyException;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * This test verifies that all dependencies are properly resolved as well as the
 * transaction is properly propagated among calls.
 * <p>
 * The database schema is recreated for every test method.
 *
 * @author Carlos Barragan (carlos.barragan@novatec-gmbh.de)
 */ 
public class TestEJBInjection extends BaseBeanTest {
    
    @Test
    public void shouldInjectEJBAsCDIBean() {
        MyEJBService myService = getBean(MyEJBService.class);
        //An Entity should be persisted and you should see a message logged in the console.
        myService.callOtherServiceAndPersistAnEntity();
        //Let's create a reference of another EJB to query the database.
        MyOtherEJBService myOtherService = getBean(MyOtherEJBService.class);
        
        assertThat(myOtherService.getAllEntities(), hasSize(1));

    }
    
    /**
     * Verifies that an entity is saved when an exception is caught by its caller.
     */
    @Test
    public void shouldPersistEntityInSpiteOfException() {
        MyEJBService myService = getBean(MyEJBService.class);
        MyEntity myEntity=new MyEntity();
        myEntity.setName("Foo");
        //An exception is thrown within the following method call, but because it is caught, the entity should have benn saved.
        myService.saveEntityAndHandleException(myEntity);
        
        MyOtherEJBService myOtherService = getBean(MyOtherEJBService.class);
        assertThat(myOtherService.getAllEntities(), hasSize(1));
        
    }
    
    /**
     * Verifies that the transaction is rolled back properly when an Exception is thrown and not handled.
     */
    @Test
    public void shouldNotPersistEntityBecauseOfException() {
        MyEJBService myService = getBean(MyEJBService.class);
        MyEntity myEntity=new MyEntity();
        myEntity.setName("Foo");
        try {
             myService.attemptToSaveEntityAndThrowException(myEntity);
             fail("Should have thrown an exception");
        } catch(MyException e) {
            MyOtherEJBService myOtherService = getBean(MyOtherEJBService.class);
            assertThat(myOtherService.getAllEntities(), is(empty()));
        }
       
        
        
    }
    
	@Test
	public void shouldInjectEJBAsCDIBeanUsingSetter() {
		MyEJBService myService = getBean(MyEJBService.class);
		
		assertNotNull(myService.getOtherService2());
	}

	@Test
	public void shouldInjectPersistenceContextUsingSetter() {
		MyEJBServiceWithEntityManagerSetter myService = getBean(MyEJBServiceWithEntityManagerSetter.class);
		
		assertNotNull(myService.getEm());
	}

}
