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

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.inject.Inject;
import javax.persistence.PersistenceContext;
import org.apache.deltaspike.core.util.metadata.AnnotationInstanceProvider;
import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

/**
 * This class provides general convenience methods for injection and validation.
 * 
 * @author Carlos Barragan (carlos.barragan@novatec-gmbh.de)
 */
public final class InjectionHelper {
    
    private static final Inject INJECT_ANNOTATION = AnnotationInstanceProvider.of(Inject.class);
    
    private static final Set<Class<? extends Annotation>> JAVA_EE_ANNOTATIONS = createJavaEEAnnotationSet();
            
    private static Set<Class<? extends Annotation>> createJavaEEAnnotationSet() {
        Set<Class<? extends Annotation>> javaEEAnnotations = new HashSet<Class<? extends Annotation>>();
        javaEEAnnotations.add(Resource.class);
        javaEEAnnotations.add(EJB.class);
        javaEEAnnotations.add(PersistenceContext.class);
        return Collections.unmodifiableSet(javaEEAnnotations);
    }
    
    private InjectionHelper() {
        // Empty on purpose.
    }

    /**
     * Returns <code>true</code> if the member is NOT annotated with {@link Inject} and is annotated with one of the following annotations:
     * <ul>
     * <li> {@link EJB}
     * <li> {@link PersistenceContext}
     * <li> {@link Resource}
     * </ul>
     * Otherwise, it returns <code>false</code>.
     *
     * @param <X>
     *            the type of the annotated member
     * @param member
     *            the annotated member whose annotations should be verified.
     * @return <code>true</code> if the member is NOT annotated with {@link Inject} and is annotated with {@link EJB},
     *         {@link PersistenceContext} or {@link Resource}
     */
    public static <X>  boolean shouldInjectionAnnotationBeAddedToMember(AnnotatedMember<? super X> member) {
        return !member.isAnnotationPresent(Inject.class) && hasJavaEEAnnotations(member);
    }
    
    /**
     * Returns <code>true</code> if at least one of the following Java EE annotations is present in the given member:
     * <ul>
     * <li> {@link EJB}
     * <li> {@link PersistenceContext}
     * <li> {@link Resource}
     * </ul>
     * Otherwise, it returns <code>false</code>.
     * @param <X> the type of the annotated member.
     * @param member the member whose annotations should be verified.
     * @return <code>true</code> if the member is at least annotated with one of the following annotations: {@link EJB}, {@link PersistenceContext} or {@link Resource}.
     */
    private static <X> boolean hasJavaEEAnnotations(AnnotatedMember<? super X> member) {
         for(Class<? extends Annotation> javaEEannotation : JAVA_EE_ANNOTATIONS) {
             if (member.isAnnotationPresent(javaEEannotation)) {
                 return true;
             }
         }
         return false;
    }

    /**
     * Adds the {@link Inject} annotation to the fields and setters of the annotated type if required.
     *
     * @param <X>
     *            the type of the annotated type
     * @param annotatedType
     *            the annotated type whose fields and setters the inject annotation should be added to
     * @param builder
     *            the builder that should be used to add the annotation.
     * @see #shouldInjectionAnnotationBeAddedToMember(AnnotatedMember)
     */
    public static <X> void addInjectAnnotation(final AnnotatedType<X> annotatedType, AnnotatedTypeBuilder<X> builder) {
        for (AnnotatedField<? super X> field : annotatedType.getFields()) {
            if (shouldInjectionAnnotationBeAddedToMember(field)) {
                builder.addToField(field, INJECT_ANNOTATION);
            }
        }
        for (AnnotatedMethod<? super X> method : annotatedType.getMethods()) {
            if (shouldInjectionAnnotationBeAddedToMember(method)) {
                builder.addToMethod(method, INJECT_ANNOTATION);
            }
        }
    }

    
}
