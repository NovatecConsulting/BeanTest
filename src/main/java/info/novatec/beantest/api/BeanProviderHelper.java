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
import javax.enterprise.inject.spi.BeanManager;
import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.apache.deltaspike.core.api.provider.BeanProvider;

/**
 * Entry point to obtain bean references.
 *
 * @author Carlos Barragan <carlos.barragan@novatec-gmbh.de>
 */
public class BeanProviderHelper {

    private  CdiContainer cdiContainer;
    private static final BeanProviderHelper INSTANCE= new BeanProviderHelper();

    public static BeanProviderHelper getInstance() {
        return INSTANCE;
    }
    
    private BeanProviderHelper() {
    }
    
    /**
     * Starts the CDI Container and initializes its contexts.
     */
    private void bootstrapCdiContainer() {
        cdiContainer = CdiContainerLoader.getCdiContainer();
        cdiContainer.boot();
        cdiContainer.getContextControl().startContexts();

    }


    /**
     * Returns a reference of the given bean class.
     * 
     * @param <T>
     *            the type of the bean.
     * @param beanClass
     *            the class of the bean whose reference should be returned.
     * @param qualifiers
     *            qualifiers for narrowing the bean instance. This attribute is not required.
     * @return the reference of the given bean class.
     */
    public <T> T getBean(Class<T> beanClass, Annotation... qualifiers) {
        if (cdiContainer == null) {
            bootstrapCdiContainer();
        }
        return BeanProvider.getContextualReference(beanClass, qualifiers);
    }

     /**
     * Returns the reference of the {@link BeanManager}.
     * 
     * @return the reference of the {@link BeanManager}.
     */
    public BeanManager getBeanManager() {
        if (cdiContainer == null) {
            bootstrapCdiContainer();
        }

        return cdiContainer.getBeanManager();
    }

    /**
     * Shuts down the underlying container.
     */
    public void shutdown() {
        if (cdiContainer != null) {
            try {
                fireShutdownEvent();
            } finally {
                cdiContainer.shutdown();
                cdiContainer = null;
            }

        }
    }
    
    /**
     * Fires a {@link ContainerShutdown} CDI event before the CDI container shuts down in order to clean up resources (for example an
     * EntityManager).
     */
    private void fireShutdownEvent() {
        CdiContainerShutdown containerShutdown = new CdiContainerShutdown();
        getBeanManager().fireEvent(containerShutdown);
    }

    
}
