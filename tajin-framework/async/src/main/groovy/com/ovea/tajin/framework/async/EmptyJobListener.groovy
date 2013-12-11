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
package com.ovea.tajin.framework.async

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2013-09-07
 */
 class EmptyJobListener implements JobListener {
     @Override
     void onJobTriggered(TriggeredScheduledJob e) {
     }

     @Override
     void onJobFailure(TriggeredScheduledJob job, Throwable err) {

     }

     @Override
     Lock tryLock(TriggeredScheduledJob job) {
         return new EmptyLock()
     }

     static class EmptyLock implements Lock {
         @Override
         void unlock() {
         }
     }
 }
