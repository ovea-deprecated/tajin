/**
 * Copyright (C) 2011 Ovea <dev@ovea.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ovea.tajin.framework.support.guice;

import com.google.inject.*;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import net.sf.cglib.core.Signature;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.objectweb.asm.Type;

import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class TajinGuice {
    private final Binder binder;

    private TajinGuice(Binder binder) {
        this.binder = binder;
    }

    public TajinGuice bindInterceptor(Class<?> interf, String methodName, Class<?>[] params, MethodInterceptor... interceptors) {
        try {
            return bindInterceptor(Matchers.subclassesOf(interf), new SignatureMatcher(interf.getMethod(methodName, params)), interceptors);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public TajinGuice bindInterceptor(Matcher<? super Class<?>> classMatcher,
                                       Matcher<? super Method> methodMatcher,
                                       MethodInterceptor... interceptors) {
        for (MethodInterceptor interceptor : interceptors)
            requestInjection(interceptor);
        binder.bindInterceptor(classMatcher, methodMatcher, interceptors);
        return this;
    }

    public <A extends Annotation> TajinGuice bindAnnotationInjector(Class<A> annotationType, Class<? extends KeyProvider<A>> providerClass) {
        binder.bindListener(Matchers.any(), requestInjection(new MemberInjectorTypeListener<A>(annotationType, providerClass)));
        return this;
    }

    public <A extends Annotation> TajinGuice handleMethodAfterInjection(Class<A> annotationType, Class<? extends MethodHandler<A>> providerClass) {
        binder.bindListener(Matchers.any(), requestInjection(new MethodHandlerTypeListener<A>(annotationType, providerClass)));
        return this;
    }

    public <A extends Annotation> TajinGuice handleFieldAfterInjection(Class<A> annotationType, Class<? extends FieldHandler<A>> providerClass) {
        binder.bindListener(Matchers.any(), requestInjection(new FieldHandlerTypeListener<A>(annotationType, providerClass)));
        return this;
    }

    public <A extends Annotation> TajinGuice handleAfterInjection(Class<A> annotationType, Class<? extends AnnotatedMemberHandler<A>> providerClass) {
        binder.bindListener(Matchers.any(), requestInjection(new AnnotatedMemberHandlerTypeListener<A>(annotationType, providerClass)));
        return this;
    }

    public <T> TajinGuice bind(Class<T> type, T instance) {
        binder.bind(type).toInstance(requestInjection(instance));
        return this;
    }

    public TajinGuice install(Module module) {
        binder.install(requestInjection(module));
        return this;
    }

    public <T> T requestInjection(T object) {
        binder.requestInjection(object);
        return object;
    }

    public MethodInterceptor createDelegatingInterceptor(Class<? extends MethodInterceptor> type) {
        return createDelegatingInterceptor(Key.get(type));
    }

    public MethodInterceptor createDelegatingInterceptor(Key<? extends MethodInterceptor> type) {
        Provider<? extends MethodInterceptor> provider = binder.getProvider(type);
        return new DelegatingInterceptor(provider);
    }

    /* static */

    public static TajinGuice in(Binder binder) {
        return new TajinGuice(binder);
    }

    /**
     * Returns true if annotations of the specified type are binding annotations.
     */
    public static boolean isBindingAnnotation(Class<? extends Annotation> annotationType) {
        return annotationType.isAnnotationPresent(BindingAnnotation.class)
            || annotationType.isAnnotationPresent(Qualifier.class);
    }

    private static final class DelegatingInterceptor implements MethodInterceptor {

        private final Provider<? extends MethodInterceptor> provider;

        private DelegatingInterceptor(Provider<? extends MethodInterceptor> provider) {
            this.provider = provider;
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            return provider.get().invoke(invocation);
        }

        @Override
        public String toString() {
            return provider.get().toString();
        }
    }

    static final class SignatureMatcher extends AbstractMatcher<Method> {
        private final Signature signature;

        private SignatureMatcher(Method method) {
            this.signature = new Signature(method.getName(), Type.getReturnType(method), Type.getArgumentTypes(method));
        }

        @Override
        public boolean matches(Method method) {
            return this.signature.equals(new Signature(method.getName(), Type.getReturnType(method), Type.getArgumentTypes(method)));
        }
    }
}
