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

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.google.common.collect.Iterables.filter;
import static com.ovea.tajin.framework.support.guice.Reflect.*;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class AnnotatedMemberHandlerTypeListener<A extends Annotation> implements TypeListener {
    private final Class<A> annotationType;
    private final Class<? extends AnnotatedMemberHandler<A>> handlerClass;

    public AnnotatedMemberHandlerTypeListener(Class<A> annotationType, Class<? extends AnnotatedMemberHandler<A>> handlerClass) {
        this.annotationType = annotationType;
        this.handlerClass = handlerClass;
    }

    @Override
    public <I> void hear(final TypeLiteral<I> type, TypeEncounter<I> encounter) {
        final Provider<? extends AnnotatedMemberHandler<A>> provider = encounter.getProvider(handlerClass);
        encounter.register(new InjectionListener<I>() {
            @Override
            public void afterInjection(I injectee) {
                AnnotatedMemberHandler<A> handler = provider.get();
                for (Field field : findFields(type.getRawType(), annotatedBy(annotationType)))
                    handler.handle(type, injectee, new AnnotatedMember<Field>(field), field.getAnnotation(annotationType));
                for (Method method : filter(findMethods(type.getRawType()), annotatedBy(annotationType)))
                    handler.handle(type, injectee, new AnnotatedMember<Method>(method), method.getAnnotation(annotationType));
            }
        });
    }
}
