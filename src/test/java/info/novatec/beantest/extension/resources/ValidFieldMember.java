package info.novatec.beantest.extension.resources;

import javax.annotation.Resource;
import javax.enterprise.inject.Vetoed;

@Vetoed
public class ValidFieldMember {
    @Resource private Void someField;
}
