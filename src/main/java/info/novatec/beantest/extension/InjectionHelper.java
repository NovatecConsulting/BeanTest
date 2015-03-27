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
import java.util.*;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.inject.spi.*;
import javax.inject.Inject;
import javax.persistence.PersistenceContext;
import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

/**
 * This class provides general convenience methods for injection and validation.
 * 
 * @author Carlos Barragan (carlos.barragan@novatec-gmbh.de)
 */
public final class InjectionHelper {

    private InjectionHelper() {
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
     */
    public static <X> void addInjectAnnotation(final AnnotatedType<X> annotatedType, AnnotatedTypeBuilder<X> builder) {
        new InjectionPointReplacement<X>(annotatedType, builder).performReplacements();
    }

    /**
     * This class is responsible for transforming {@link EJB}, {@link PersistenceContext} or {@link Resource} injection
     * points into correlating {@link Inject} dependency definitions.
     * <p>
     * Furthermore this class ensures that the processed bean holds valid dependency injection points for its member
     * i.e. the processed bean may hold exclusively field injection points or setter injection points for a particular
     * member.
     * <p>
     * By way of example the {@link InvalidInjectionPointConfigurationEJB} holds an invalid dependency configuration.
     * @author Qaiser Abbasi (qaiser.abbasi@novatec-gmbh.de)
     *
     */
    private static class InjectionPointReplacement<X> {

        private static final Set<Class<? extends Annotation>> JAVA_EE_ANNOTATIONS = createJavaEEAnnotationSet();

        private static final String SETTER_METHOD_PREFIX = "set";

        private static final int FIELD_NAME_INDEX = 1;

        private static final String INVALID_BEAN_DEFINITION = "Invalid dependency definition in declaring class: %s."
                + " Found duplicate injection points for method %s and corresponding field.";

        private List<String> processedFieldInjections = new ArrayList<String>();

        private final AnnotatedType<X> annotatedType;

        private final AnnotatedTypeBuilder<X> builder;

        public InjectionPointReplacement(AnnotatedType<X> annotatedType, AnnotatedTypeBuilder<X> builder) {
            this.annotatedType = annotatedType;
            this.builder = builder;
        }

        private static Set<Class<? extends Annotation>> createJavaEEAnnotationSet() {
            Set<Class<? extends Annotation>> javaEEAnnotations = new HashSet<Class<? extends Annotation>>();
            javaEEAnnotations.add(Resource.class);
            javaEEAnnotations.add(EJB.class);
            javaEEAnnotations.add(PersistenceContext.class);
            return Collections.unmodifiableSet(javaEEAnnotations);
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
        private static <X> boolean shouldInjectionAnnotationBeAddedToMember(AnnotatedMember<? super X> member) {
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
         * This method performs the actual injection point transformation while ensuring that the processed bean
         * comprise a valid dependency configuration.
         */
        public void performReplacements() {
            for (AnnotatedField<? super X> field : annotatedType.getFields()) {
                if (shouldInjectionAnnotationBeAddedToMember(field)) {
                    builder.addToField(field, AnnotationInstances.INJECT);
                    processedFieldInjections.add(field.getJavaMember().getName());
                }
            }

            for (AnnotatedMethod<? super X> method : annotatedType.getMethods()) {
                if (shouldInjectionAnnotationBeAddedToMember(method)) {
                    validateDependencyConfiguration(method);
                    builder.addToMethod(method, AnnotationInstances.INJECT);
                }
            }
        }

        /**
         * Check if there is already a processed field injection point for the corresponding member.
         * If so, the setter injection method for the given member is not legal. The invalid dependency configuration
         * lead towards to a deployment exception.
         * @param method Represent the suffix-name of the corresponding setter injection method in the processed bean.
         * @throws DefinitionException Occurs if there is already a processed field for the given member.
         */
        private void validateDependencyConfiguration(AnnotatedMethod<? super X> method) {
            if(method.getJavaMember().getName().startsWith(SETTER_METHOD_PREFIX)){
                String methodSuffixName = method.getJavaMember().getName().split(SETTER_METHOD_PREFIX)[FIELD_NAME_INDEX];

                if(isInjectionPointAlreadyProcessed(methodSuffixName)) {
                    throw new DefinitionException(String.format(INVALID_BEAN_DEFINITION, annotatedType,
                            method.getJavaMember().getName()));
                }
            }
        }

        private boolean isInjectionPointAlreadyProcessed(String methodSuffixName) {
            for (String processedField : processedFieldInjections) {
                if (processedField.equalsIgnoreCase(methodSuffixName)) {
                    return true;
                }
            }
            return false;
        }
    }

}
