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

import info.novatec.beantest.demo.mocks.ExternalServicesMockProducer;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(CdiTestRunner.class)
public class DeltaSpikeMockProducerTest {

    @Inject
    private MyEjbServiceThatCallsAnExternalService service;

    @Test
    public void shouldCallExternalServiceMock() {
        MyExternalService externalService = ExternalServicesMockProducer.getExternalService();
        //Since the ExternalServicesMockProducer returns a Mockito mock, we can initialize it
        Mockito.when(externalService.doSomething()).thenReturn("Hello World");

        assertThat(service.callExternalService(), is("Hello World"));
    }
}
