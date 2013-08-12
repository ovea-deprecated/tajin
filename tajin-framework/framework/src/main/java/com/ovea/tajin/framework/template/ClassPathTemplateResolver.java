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
package com.ovea.tajin.framework.template;

import com.ovea.tajin.framework.core.Resource;

import java.net.URL;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class ClassPathTemplateResolver extends TemplateResolverSkeleton {
    private final ClassLoader classLoader;

    public ClassPathTemplateResolver(TemplateCompiler compiler, ClassLoader classLoader) {
        super(compiler);
        this.classLoader = classLoader;
    }

    @Override
    protected Resource tryPath(String path) {
        URL tmpl = classLoader.getResource(path);
        return tmpl != null ? Resource.url(tmpl) : null;
    }
}
