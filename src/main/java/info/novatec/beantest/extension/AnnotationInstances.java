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

import info.novatec.beantest.transactions.Transactional;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.apache.deltaspike.core.util.metadata.AnnotationInstanceProvider;

/**
 * Class that contains constants of annotation instances.
 * 
 * @author Carlos Barragan (carlos.barragan@novatec-gmbh.de)
 */
public final class AnnotationInstances {
    
    private AnnotationInstances() {
    }
    
    public static final Transactional TRANSACTIONAL = AnnotationInstanceProvider.of(Transactional.class);
    public static final RequestScoped REQUEST_SCOPED = AnnotationInstanceProvider.of(RequestScoped.class);
    public static final Inject INJECT = AnnotationInstanceProvider.of(Inject.class);
    public static final Singleton SINGLETON = AnnotationInstanceProvider.of(Singleton.class);
    public static final ApplicationScoped APPLICATION_SCOPED = AnnotationInstanceProvider.of(ApplicationScoped.class);
    
}
