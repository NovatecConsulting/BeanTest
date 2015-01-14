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
import info.novatec.beantest.utils.entities.MyEntityWithConstraints;
import org.junit.Test;

import javax.persistence.PersistenceException;

import static org.junit.Assert.fail;

/**
 * This test verifies that persistence exceptions are correctly propagated.
 * 
 * @author Carlos Barragan (carlos.barragan@novatec-gmbh.de)
 */
public class TestPersistenceExceptionPropagation extends BaseBeanTest {
      

    
    @Test(expected = PersistenceException.class)
    public void shouldCauseExceptionBecuaseUniquenessViolation() {
        MyEJBService myEJBService = getBean(MyEJBService.class);
        MyEntityWithConstraints entity=new MyEntityWithConstraints("123");
        myEJBService.save(entity);
        entity=new MyEntityWithConstraints("123");
        myEJBService.save(entity);
        fail("Should have failed because uniqueness violation");
    }
}
