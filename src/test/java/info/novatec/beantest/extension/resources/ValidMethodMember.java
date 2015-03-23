package info.novatec.beantest.extension.resources;

import javax.annotation.Resource;
import javax.enterprise.inject.Vetoed;

@Vetoed
public class ValidMethodMember {
    private Void someField;

    @Resource
    public void setSomeField(Void argument) {
        this.someField = argument;
    }
}
