package info.novatec.beantest.extension.resources;

import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;

@Vetoed
public class PreExistingCdiMember {
    @Inject private Void someField;
}
