package info.novatec.beantest.demo.ejb;

import info.novatec.beantest.demo.entities.MyEntity;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.NoResultException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(CdiTestRunner.class)
public class DeltaSpikeRollbackExceptionTest {

    @Inject
    private MyEJBService myEJBService;

    @Test
    public void shouldExceptionNoCauseRollback() {
        MyOtherEJBService myOtherEJBService = myEJBService.getOtherService2();

        assertThat(myOtherEJBService.getAllEntities(), hasSize(0));

        MyEntity entity = new MyEntity();
        entity.setName("some name");
        try {
            myEJBService.saveEntityAndCausePersistenceExceptionWithoutRollback(entity);
            fail("Should have thrown PersistenceException");
        } catch (NoResultException exception) {
            assertThat(exception, not(nullValue()));
        }


        //Entity should have been saved
        assertThat(myOtherEJBService.getAllEntities(), hasSize(1));

    }

}
