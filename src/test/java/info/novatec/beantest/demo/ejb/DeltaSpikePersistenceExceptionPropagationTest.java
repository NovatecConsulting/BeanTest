package info.novatec.beantest.demo.ejb;

import info.novatec.beantest.demo.entities.MyEntityWithConstraints;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.PersistenceException;

import static org.junit.Assert.fail;

@RunWith(CdiTestRunner.class)
public class DeltaSpikePersistenceExceptionPropagationTest {

    @Inject
    private MyEJBService myEJBService;

    @Test(expected = PersistenceException.class)
    public void shouldCauseExceptionBecuaseUniquenessViolation() {
        MyEntityWithConstraints entity = new MyEntityWithConstraints("123");
        myEJBService.save(entity);
        entity = new MyEntityWithConstraints("123");
        myEJBService.save(entity);
        fail("Should have failed because uniqueness violation");
    }

}
