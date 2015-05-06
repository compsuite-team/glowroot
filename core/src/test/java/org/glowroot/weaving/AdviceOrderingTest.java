/*
 * Copyright 2015 the original author or authors.
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

import com.google.common.collect.Ordering;
import org.junit.Test;
import org.objectweb.asm.Type;

import org.glowroot.api.weaving.Pointcut;

import static org.assertj.core.api.Assertions.assertThat;

public class AdviceOrderingTest {

    private final Pointcut pointcutPriority1 =
            OnlyForThePriority1.class.getAnnotation(Pointcut.class);

    private final Pointcut pointcutPriority2 =
            OnlyForThePriority2.class.getAnnotation(Pointcut.class);

    private final Pointcut pointcutTimerNameA =
            OnlyForTheTimerNameA.class.getAnnotation(Pointcut.class);

    private final Pointcut pointcutTimerNameB =
            OnlyForTheTimerNameB.class.getAnnotation(Pointcut.class);

    private final Pointcut pointcutTimerNameEmpty1 =
            OnlyForTheTimerNameEmpty1.class.getAnnotation(Pointcut.class);

    private final Pointcut pointcutTimerNameEmpty2 =
            OnlyForTheTimerNameEmpty2.class.getAnnotation(Pointcut.class);

    private final Advice advicePriority1 = Advice.builder()
            .pointcut(pointcutPriority1)
            .adviceType(Type.getType(AdviceOrderingTest.class))
            .reweavable(false)
            .build();

    private final Advice advicePriority2 = Advice.builder()
            .pointcut(pointcutPriority2)
            .adviceType(Type.getType(AdviceOrderingTest.class))
            .reweavable(false)
            .build();

    private final Advice adviceTimerNameA = Advice.builder()
            .pointcut(pointcutTimerNameA)
            .adviceType(Type.getType(AdviceOrderingTest.class))
            .reweavable(false)
            .build();

    private final Advice adviceTimerNameB = Advice.builder()
            .pointcut(pointcutTimerNameB)
            .adviceType(Type.getType(AdviceOrderingTest.class))
            .reweavable(false)
            .build();

    private final Advice adviceTimerNameEmpty1 = Advice.builder()
            .pointcut(pointcutTimerNameEmpty1)
            .adviceType(Type.getType(AdviceOrderingTest.class))
            .reweavable(false)
            .build();

    private final Advice adviceTimerNameEmpty2 = Advice.builder()
            .pointcut(pointcutTimerNameEmpty2)
            .adviceType(Type.getType(AdviceOrderingTest.class))
            .reweavable(false)
            .build();

    @Test
    public void shouldCompare() {
        Ordering<Advice> ordering = AdviceBase.ordering;
        assertThat(ordering.compare(advicePriority1, advicePriority2)).isNegative();
        assertThat(ordering.compare(advicePriority2, advicePriority1)).isPositive();
        assertThat(ordering.compare(adviceTimerNameA, adviceTimerNameB)).isNegative();
        assertThat(ordering.compare(adviceTimerNameB, adviceTimerNameA)).isPositive();
        assertThat(ordering.compare(adviceTimerNameA, adviceTimerNameEmpty1)).isNegative();
        assertThat(ordering.compare(adviceTimerNameEmpty1, adviceTimerNameA)).isPositive();
        assertThat(ordering.compare(adviceTimerNameEmpty1, adviceTimerNameEmpty2)).isZero();
        assertThat(ordering.compare(adviceTimerNameEmpty2, adviceTimerNameEmpty1)).isZero();
    }

    @Pointcut(className = "dummy", methodName = "dummy", methodParameterTypes = {},
            timerName = "b", priority = 1)
    private static class OnlyForThePriority1 {}

    @Pointcut(className = "dummy", methodName = "dummy", methodParameterTypes = {},
            timerName = "a", priority = 2)
    private static class OnlyForThePriority2 {}

    @Pointcut(className = "dummy", methodName = "dummy", methodParameterTypes = {},
            timerName = "a")
    private static class OnlyForTheTimerNameA {}

    @Pointcut(className = "dummy", methodName = "dummy", methodParameterTypes = {},
            timerName = "b")
    private static class OnlyForTheTimerNameB {}

    @Pointcut(className = "dummy", methodName = "dummy", methodParameterTypes = {})
    private static class OnlyForTheTimerNameEmpty1 {}

    @Pointcut(className = "dummy", methodName = "dummy", methodParameterTypes = {})
    private static class OnlyForTheTimerNameEmpty2 {}
}
