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
package info.novatec.beantest.extension;

import info.novatec.beantest.extension.resources.*;
import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;
import org.junit.Test;
import org.mockito.Mockito;

import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Verifies the correct bean manipulation i.e. expected replacements of Java EE DI declarations with CDI @Inject annotation
 *
 * @author Qaiser Abbasi (qaiser.abbasi@novatec-gmbh.de)
 */
public class InjectionHelperTest {

    @Test
    public void should_not_add_inject_annotation_to_non_java_ee_member() throws Exception {
        AnnotatedTypeBuilder builder = new AnnotatedTypeBuilder().readFromType(NonJavaEEMember.class);
        AnnotatedType annotatedType = builder.create();
        AnnotatedTypeBuilder spyBuilder = Mockito.spy(builder);

        InjectionHelper.addInjectAnnotation(annotatedType, spyBuilder);
        assertThat(annotatedType.getFields().size(), is(1));

        AnnotatedField annotatedField = (AnnotatedField) annotatedType.getFields().toArray()[0];
        assertThat(annotatedField.getAnnotations(), hasSize(0));

        verify(spyBuilder, times(0)).addToField(annotatedField, AnnotationInstances.INJECT);
    }

    @Test
    public void should_not_add_inject_annotation_to_preexisting_cdi_member() throws Exception {
        AnnotatedTypeBuilder builder = new AnnotatedTypeBuilder().readFromType(PreExistingCdiMember.class);
        AnnotatedType annotatedType = builder.create();
        AnnotatedTypeBuilder spyBuilder = Mockito.spy(builder);

        InjectionHelper.addInjectAnnotation(annotatedType, spyBuilder);
        assertThat(annotatedType.getFields().size(), is(1));

        AnnotatedField annotatedField = (AnnotatedField) annotatedType.getFields().toArray()[0];
        assertThat(annotatedField.getAnnotations(), hasSize(1));

        verify(spyBuilder, times(0)).addToField(annotatedField, AnnotationInstances.INJECT);
    }

    @Test
    public void should_add_inject_annotation_to_field_member() throws Exception {
        AnnotatedTypeBuilder builder = new AnnotatedTypeBuilder().readFromType(ValidFieldMember.class);
        AnnotatedType annotatedType = builder.create();
        AnnotatedTypeBuilder spyBuilder = Mockito.spy(builder);

        InjectionHelper.addInjectAnnotation(annotatedType, spyBuilder);
        assertThat(annotatedType.getFields().size(), is(1));

        AnnotatedField annotatedField = (AnnotatedField) annotatedType.getFields().toArray()[0];
        assertThat(annotatedField.getAnnotations(), hasSize(2));

        verify(spyBuilder, times(1)).addToField(annotatedField, AnnotationInstances.INJECT);
    }

    @Test
    public void should_add_inject_annotation_to_method_member() throws Exception {
        AnnotatedTypeBuilder builder = new AnnotatedTypeBuilder().readFromType(ValidMethodMember.class);
        AnnotatedType annotatedType = builder.create();
        AnnotatedTypeBuilder spyBuilder = Mockito.spy(builder);

        InjectionHelper.addInjectAnnotation(annotatedType, spyBuilder);
        assertThat(annotatedType.getMethods().size(), is(1));

        AnnotatedMethod annotatedMethod = (AnnotatedMethod) annotatedType.getMethods().toArray()[0];
        assertThat(annotatedMethod.getAnnotations(), hasSize(2));

        verify(spyBuilder, times(1)).addToMethod(annotatedMethod, AnnotationInstances.INJECT);
    }
}