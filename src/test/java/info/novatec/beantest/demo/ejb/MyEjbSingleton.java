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

import javax.ejb.EJB;
import javax.ejb.Singleton;

/**
 * Demo EJB Singleton.
 * @author Carlos Barragan (carlos.barragan@novatec-gmbh.de)
 */
@Singleton
public class MyEjbSingleton {
    
    @EJB
    MyOtherEJBService ejbService;
    
    private boolean wasEjbCalled=false;
    
    public void callAnEjb() {
        ejbService.doSomething();
        wasEjbCalled = true;
    }
    
    public boolean wasEjbCalled() {
        return this.wasEjbCalled;
    }
    
}
