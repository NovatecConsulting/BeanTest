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

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Qaiser Abbasi (qaiser.abbasi@novatec-gmbh.de)
 */
public class Interceptor2SharingTransaction {

    @PersistenceContext
    EntityManager entityManager;

    @AroundInvoke
    public Object handle(InvocationContext context) throws Exception {
        entityManager.persist(new MyEntity());
        return context.proceed();
    }
}
