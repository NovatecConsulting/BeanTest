package info.novatec.beantest.demo.ejb;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(CdiTestRunner.class)
public class DeltaSpikeSingletonInjectionTest {

    @Inject
    private MyEjbSingleton singleton;

    @Test
    public void shouldBeInstantiatedOnce() {
        assertThat(singleton.wasEjbCalled(), is(false));
        singleton.callAnEjb();
        assertThat(singleton.wasEjbCalled(), is(true));
    }
}
