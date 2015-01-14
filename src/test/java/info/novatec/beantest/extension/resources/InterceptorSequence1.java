/*
 *
 *  * Bean Testing.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package info.novatec.beantest.extension.resources;

import info.novatec.beantest.extension.TestEjbInterceptionSequence;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class InterceptorSequence1 {

    @AroundInvoke
    public Object handle(InvocationContext invocationContext) throws Exception {
        TestEjbInterceptionSequence.INTERCEPTOR_SEQUENCE.add("InterceptorSequence1");
        return invocationContext.proceed();
    }
}
