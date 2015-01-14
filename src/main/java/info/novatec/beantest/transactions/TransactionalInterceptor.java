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
package info.novatec.beantest.transactions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.LockTimeoutException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.QueryTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transactional interceptor to provide basic transaction propagation.
 * <p> 
 * <b>Note</b> This implementation is intentionally not thread-safe, because unit tests are usually run in one thread. <br>
 * If you try to run unit tests in parallel, unexpected behavior may occur.
 * <p>
 * Alternatively the Apache Deltaspike JPA module can be used. The JPA module provides more advanced transaction handling. 
 * However, this implementation should suffice for testing purposes.
 *
 * @author Carlos Barragan (carlos.barragan@novatec-gmbh.de)
 */
@Interceptor
@Transactional
public class TransactionalInterceptor {
    
    /**
     * Exceptions that should not cause the transaction to rollback according to Java EE Documentation. 
     * (http://docs.oracle.com/javaee/6/api/javax/persistence/PersistenceException.html)
     */
    private static final Set<Class<?>> NO_ROLLBACK_EXCEPTIONS = new HashSet<Class<?>>(Arrays.<Class<?>>asList(
            NonUniqueResultException.class,
            NoResultException.class,
            QueryTimeoutException.class,
            LockTimeoutException.class));
    

    @Inject
    @PersistenceContext
    EntityManager em;

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalInterceptor.class);

    private static int INTERCEPTOR_COUNTER = 0;

    @AroundInvoke
    public Object manageTransaction(InvocationContext ctx) throws Exception {
        
        EntityTransaction transaction = em.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
            LOGGER.debug("Transaction started");
        }

        INTERCEPTOR_COUNTER++;
        Object result = null;
        try {
            result = ctx.proceed();

        } catch (Exception e) {
            if (isFirstInterceptor()) {
                markRollbackTransaction(e);
            }
            throw e;
        } finally {
            processTransaction();
        }

        return result;
    }
    
     

    /**
     * Commits the current transaction if it is not already marked as rollback via the {@link EntityTransaction#getRollbackOnly()} method.
     * In that case, a rollback will be executed.
     */
    private void processTransaction() throws Exception {
        EntityTransaction transaction = em.getTransaction();
        try {
            
            if (em.isOpen() && transaction.isActive() && isFirstInterceptor()) {
                if (transaction.getRollbackOnly()) {
                    transaction.rollback();
                    LOGGER.debug("Transaction was rollbacked");
                } else {
                    transaction.commit();
                    LOGGER.debug("Transaction committed");
                }
                em.clear();
            }
        } catch (Exception e) {
            LOGGER.warn("Error when trying to commit transaction: {0}", e);
            throw e;
        } finally {
            INTERCEPTOR_COUNTER--;
        }

    }

    /**
     * Marks the transaction for rollback via {@link EntityTransaction#setRollbackOnly()}.
     */
    private void markRollbackTransaction(Exception exception) throws Exception {
        try {
            if (em.isOpen() && em.getTransaction().isActive() && shouldExceptionCauseRollback(exception)) {
                em.getTransaction().setRollbackOnly();
            }
        } catch (Exception e) {
            LOGGER.warn("Error when trying to roll back the  transaction: {0}", e);
            throw e;
        }

    }

    private static boolean isFirstInterceptor() {
        return INTERCEPTOR_COUNTER -1 == 0;
    }
    
    private static boolean shouldExceptionCauseRollback(Exception e ) {
        return ! NO_ROLLBACK_EXCEPTIONS.contains(e.getClass());
    }

}
