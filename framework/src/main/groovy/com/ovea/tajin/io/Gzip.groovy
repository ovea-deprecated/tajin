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
package com.ovea.tajin.io

import java.util.zip.GZIPOutputStream

/**
 * @author Mathieu Carbou
 */
class Gzip {

    static byte[] compress(byte[] data) {
        try {
            ByteArrayOutputStream compressed = new ByteArrayOutputStream();
            GZIPOutputStream gzipper = new GZIPOutputStream(compressed);
            gzipper.write(data);
            gzipper.close(); // finish and close
            return compressed.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}