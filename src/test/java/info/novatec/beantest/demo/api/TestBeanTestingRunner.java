package info.novatec.beantest.demo.api;

import info.novatec.beantest.api.BeanTestingRunner;
import info.novatec.beantest.demo.ejb.MyEJBService;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(BeanTestingRunner.class)
public class TestBeanTestingRunner {

    @Inject
    MyEJBService myEJBService;

    @Test
    public void assert_that_class_fields_are_injected() throws Exception {
        assertThat(myEJBService, is(notNullValue()));
    }

}
