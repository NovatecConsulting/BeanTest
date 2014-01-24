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

package info.novatec.beantest.api;

import java.lang.annotation.Annotation;
import org.junit.After;
import org.junit.Before;

/**
 * Base class for initializing the {@link  BeanProviderHelper}
 *
 * @author Carlos Barragan <carlos.barragan@novatec-gmbh.de>
 */
public abstract class BaseBeanTest {
    
    private BeanProviderHelper bm;
    
    @Before
    public void initilaize() {
        bm = BeanProviderHelper.getInstance();
    }

    @After
    public void cleanUp() {
        bm.shutdown();
    }
    
    protected <T> T getBean(Class<T> beanClass, Annotation... qualifiers) {
        return bm.getBean(beanClass, qualifiers);
    }
    
}
