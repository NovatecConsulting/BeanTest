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
import info.novatec.beantest.extension.resources.MyOtherEJBService;
import info.novatec.beantest.utils.entities.MyEntity;
import org.junit.Test;

import javax.persistence.NoResultException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests that the exceptions documented in http://docs.oracle.com/javaee/6/api/javax/persistence/PersistenceException.html don't cause a rollback.
 * 
 * @author Carlos Barragan (carlos.barragan@novatec-gmbh.de)
 */
public class TestNoRollbackException extends BaseBeanTest {
    
  @Test
  public void shouldExceptionNoCauseRollback() {
      MyEJBService myEJBService = getBean(MyEJBService.class);
      MyOtherEJBService myOtherEJBService = getBean(MyOtherEJBService.class);
      
      assertThat(myOtherEJBService.getAllEntities(), hasSize(0));
      
      MyEntity entity = new MyEntity();
      entity.setName("some name");
      try {
          myEJBService.saveEntityAndCausePersistenceExceptionWithoutRollback(entity);
          fail("Should have thrown PersistenceException");
      } catch (NoResultException exception) {
          assertThat(exception, not(nullValue()));
      }
      
      
      //Entity should have been saved
      assertThat(myOtherEJBService.getAllEntities(), hasSize(1));
      
  }
}
