package info.novatec.beantest.api;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * In order to invoke the test methods of the underlying JUnit test class on a container managed CDI Bean,
 * it is necessary to integrate the CDI container bootstrap in the JUnit execution process.
 * Due to this integration, it is now possible to retrieve container managed beans inside the test class (and other
 * known container services) via commonly used annotations such as '@Inject'.
 *
 * @author Qaiser Abbasi (qaiser.abbasi@novatec-gmbh.de)
 */
public class BeanTestingRunner extends BlockJUnit4ClassRunner {

    private BeanProviderHelper beanProviderHelper;

    public BeanTestingRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        initializeBeanContainer();
    }

    private void initializeBeanContainer() {
        beanProviderHelper = BeanProviderHelper.getInstance();
    }

    private void shutdownBeanContainer() {
        beanProviderHelper.shutdown();
    }

    @Override
    protected Statement methodBlock(FrameworkMethod method) {
        return decorateMethodBlock(method);
    }

    @Override
    protected Object createTest() throws Exception {
        return produceInjectedTestInstance();
    }

    private Object produceInjectedTestInstance() {
        return beanProviderHelper.getBean(getTestClass().getJavaClass());
    }

    private Statement decorateMethodBlock(FrameworkMethod method) {
        final Statement originStatement = super.methodBlock(method);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    originStatement.evaluate();
                } finally {
                    shutdownBeanContainer();
                }
            }
        };
    }
}
