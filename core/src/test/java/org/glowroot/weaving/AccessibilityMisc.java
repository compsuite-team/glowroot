/*
 * Copyright 2012-2014 the original author or authors.
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
package org.glowroot.weaving;

import checkers.nullness.quals.Nullable;

/**
 * @author Trask Stalnaker
 * @since 0.5
 */
public class AccessibilityMisc implements Misc {

    @Override
    public void execute1() {
        new PrivateMisc();
    }

    @Override
    public String executeWithReturn() {
        return "xyz";
    }

    @Override
    public void executeWithArgs(String one, int two) {}

    private static class PrivateMisc implements Misc {
        @Override
        public void execute1() {}
        @Override
        @Nullable
        public String executeWithReturn() {
            return null;
        }
        @Override
        public void executeWithArgs(String one, int two) {}
    }
}
