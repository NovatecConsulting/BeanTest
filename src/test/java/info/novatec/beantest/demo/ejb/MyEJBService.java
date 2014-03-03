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
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demo EJB Service.
 * <p>
 * This EJB serves as a facade. It calls {@link MyOtherEJBService} to simulate transaction propagation. Furthermore, it provides
 * some methods to test proper transaction handling when exceptions are thrown.
 *
 * @author Carlos Barragan (carlos.barragan@novatec-gmbh.de)
 */
@Stateless
public class MyEJBService {

    private static final Logger LOGGER=LoggerFactory.getLogger(MyEJBService.class);
    
    @EJB
    MyOtherEJBService otherService;

    @PersistenceContext(unitName = "db2")
    EntityManager em;

    /**
     * This method calls {@link MyOtherEJBService#doSomething() } and saves a new {@link MyEntity}.
     */
    public void callOtherServiceAndPersistAnEntity() {
        otherService.doSomething();
        MyEntity entity = new MyEntity();
        entity.setName("Hello");
        em.persist(entity);
        LOGGER.info("Entity persisted!");
    }
    
    /**
     * Saves the given entity even though an exception is thrown when calling {@link MyOtherEJBService#throwException()}.
     * 
     * @param entity the entity that should be saved.
     */
    public void saveEntityAndHandleException(MyEntity entity) {
        try {
            em.persist(entity);
            otherService.throwException();
        } catch(MyException e) {
            //Empty on purpose. Entity was persisted in spite of the exception.
        }
    }
    
    /**
     * Tries to persist the given entity but an exception is thrown instead because {@link MyOtherEJBService#throwException()} is called.
     * 
     * @param entity the entity that should be saved.
     */
     public void attemptToSaveEntityAndThrowException(MyEntity entity) {
            em.persist(entity);
            otherService.throwException();
            //Entity should have not been persisted.
    }
     
     /**
      * Persists the given entity and throws a {@link PersistenceException} that should not rollback the transaction<p>
      * 
      * @param entity the entity that should be persisted
      * 
      * @see http://docs.oracle.com/javaee/6/api/javax/persistence/PersistenceException.html
      */
     public void saveEntityAndCausePersistenceExceptionWithoutRollback(MyEntity entity) {
         em.persist(entity);
         //Throw a NoResultFoundException.
         em.createQuery("Select e from MyEntity as e where e.id = :nonExistentId").setParameter("nonExistentId", -42L).getSingleResult();
     }
}
