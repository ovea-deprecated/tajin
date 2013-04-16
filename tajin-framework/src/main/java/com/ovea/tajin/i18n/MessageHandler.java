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
package com.ovea.tajin.i18n;

import com.google.common.collect.Iterables;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.ovea.tajin.util.Reflect;

import javax.inject.Provider;
import java.lang.reflect.Field;

public final class MessageHandler implements TypeListener {

    @Override
    public <I> void hear(final TypeLiteral<I> type, TypeEncounter<I> encounter) {
        final Iterable<Field> fields = Reflect.findFields(type.getRawType(), Reflect.annotatedBy(Message.class));
        if (!Iterables.isEmpty(fields)) {
            final Provider<I18NServiceFactory> factory = encounter.getProvider(I18NServiceFactory.class);
            encounter.register(new MembersInjector<I>() {
                @Override
                public void injectMembers(I instance) {
                    for (Field field : fields) {
                        Message annotation = field.getAnnotation(Message.class);
                        String bundleName = annotation.value();
                        if (bundleName.length() == 0)
                            bundleName = type.getRawType().getName().replace('.', '/');
                        if (bundleName.startsWith("/"))
                            bundleName = bundleName.substring(1);
                        I18NService service = factory.get().forBundle(bundleName);
                        if (!field.getType().isAssignableFrom(I18NService.class))
                            throw new IllegalStateException("Field " + field + " must be of type " + I18NService.class.getName());
                        if (!field.isAccessible())
                            field.setAccessible(true);
                        try {
                            field.set(instance, service);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e.getMessage(), e);
                        }
                    }
                }
            });
        }
    }

}