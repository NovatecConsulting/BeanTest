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

import info.novatec.beantest.demo.entities.MyEntity;
import info.novatec.beantest.demo.exceptions.MyException;
import java.util.Collection;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demo EJB Service.
 *
 * @author Carlos Barragan <carlos.barragan@novatec-gmbh.de>
 */
@Stateless
public class MyOtherEJBService {
    private static final Logger LOGGER=LoggerFactory.getLogger(MyOtherEJBService.class);

    @PersistenceContext(unitName = "db2")
    EntityManager entityManager;

    /**
     * It just logs a message.
     */
    public void doSomething() {
        LOGGER.info("MyOtherEJBService did something");
    }

    public Collection<MyEntity> getAllEntities() {
        return entityManager.createQuery("Select E from MyEntity as E", MyEntity.class).getResultList();
    }
    
    /**
     * Throws <code>MyException</code> when called.
     * <p>
     * See the corresponding test to better understand its purpose.
     */
    public void throwException() {
        throw new MyException("Oops, exception was thrown");
    }
}
