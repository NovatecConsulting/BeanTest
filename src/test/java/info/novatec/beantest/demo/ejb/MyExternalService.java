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

/**
 * Represents an external service whose implementation is located somewhere else.
 * <p>
 * It is common to have dependencies to external services or modules in a JEE Application. 
 * Usually it is possible to access those external services via a shared interface. The implementation of
 * such interface is normally located in another module or application.
 * @author Carlos Barragan <carlos.barragan@novatec-gmbh.de>
 */
public interface MyExternalService {
    
    String doSomething();
    
}
