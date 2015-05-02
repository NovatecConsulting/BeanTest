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
