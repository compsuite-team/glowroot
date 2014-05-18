/*
 * Copyright 2013-2014 the original author or authors.
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
package org.glowroot.sandbox.ui;

import org.glowroot.api.MessageSupplier;
import org.glowroot.api.PluginServices;
import org.glowroot.api.Span;
import org.glowroot.api.TraceMetricName;
import org.glowroot.api.TraceMetricTimer;
import org.glowroot.api.weaving.BindTraveler;
import org.glowroot.api.weaving.IsEnabled;
import org.glowroot.api.weaving.OnAfter;
import org.glowroot.api.weaving.OnBefore;
import org.glowroot.api.weaving.Pointcut;

/**
 * This is used to generate a trace with <multiple root nodes> (and with multiple trace metrics)
 * just to test this unusual situation.
 * 
 * @author Trask Stalnaker
 * @since 0.5
 */
public class ExternalJvmMainAspect {

    private static final PluginServices pluginServices = PluginServices.get("glowroot-ui-sandbox");

    @Pointcut(type = "org.glowroot.container.javaagent.JavaagentContainer",
            methodName = "main", methodArgTypes = {"java.lang.String[]"},
            traceMetric = "external jvm main")
    public static class MainAdvice {

        private static final TraceMetricName traceMetricName =
                pluginServices.getTraceMetricName(MainAdvice.class);

        @IsEnabled
        public static boolean isEnabled() {
            return pluginServices.isEnabled();
        }

        @OnBefore
        public static Span onBefore() {
            return pluginServices.startTrace("javaagent container main",
                    MessageSupplier.from("org.glowroot.container.javaagent.JavaagentContainer"
                            + ".main()"), traceMetricName);
        }

        @OnAfter
        public static void onAfter(@BindTraveler Span span) {
            span.end();
        }
    }

    @Pointcut(type = "org.glowroot.container.javaagent.JavaagentContainer",
            methodName = "metricOne", traceMetric = "metric one")
    public static class MetricOneAdvice {

        private static final TraceMetricName traceMetricName =
                pluginServices.getTraceMetricName(MetricOneAdvice.class);

        @IsEnabled
        public static boolean isEnabled() {
            return pluginServices.isEnabled();
        }

        @OnBefore
        public static TraceMetricTimer onBefore() {
            return pluginServices.startTraceMetric(traceMetricName);
        }

        @OnAfter
        public static void onAfter(@BindTraveler TraceMetricTimer metricTimer) {
            metricTimer.stop();
        }
    }

    @Pointcut(type = "org.glowroot.container.javaagent.JavaagentContainer",
            methodName = "metricTwo", traceMetric = "metric two")
    public static class MetricTwoAdvice {

        private static final TraceMetricName traceMetricName =
                pluginServices.getTraceMetricName(MetricTwoAdvice.class);

        @IsEnabled
        public static boolean isEnabled() {
            return pluginServices.isEnabled();
        }

        @OnBefore
        public static TraceMetricTimer onBefore() {
            return pluginServices.startTraceMetric(traceMetricName);
        }

        @OnAfter
        public static void onAfter(@BindTraveler TraceMetricTimer metricTimer) {
            metricTimer.stop();
        }
    }
}
