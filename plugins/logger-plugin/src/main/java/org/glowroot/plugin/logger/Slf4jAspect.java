/*
 * Copyright 2014 the original author or authors.
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
package org.glowroot.plugin.logger;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import org.glowroot.api.ErrorMessage;
import org.glowroot.api.MessageSupplier;
import org.glowroot.api.PluginServices;
import org.glowroot.api.Span;
import org.glowroot.api.TraceMetricName;
import org.glowroot.api.weaving.BindMethodArg;
import org.glowroot.api.weaving.BindMethodName;
import org.glowroot.api.weaving.BindTraveler;
import org.glowroot.api.weaving.IsEnabled;
import org.glowroot.api.weaving.OnAfter;
import org.glowroot.api.weaving.OnBefore;
import org.glowroot.api.weaving.Pointcut;

/**
 * @author Trask Stalnaker
 * @since 0.5
 */
public class Slf4jAspect {

    private static final String METRIC_NAME = "logging";

    private static final PluginServices pluginServices = PluginServices.get("logger");

    private static boolean markTraceAsError(boolean warn, boolean throwable) {
        boolean traceErrorOnErrorWithNoThrowable =
                pluginServices.getBooleanProperty("traceErrorOnErrorWithNoThrowable");
        boolean traceErrorOnWarn = pluginServices.getBooleanProperty("traceErrorOnWarn");
        return (!warn || traceErrorOnWarn) && (throwable || traceErrorOnErrorWithNoThrowable);
    }

    private static LogAdviceTraveler onBefore(FormattingTuple formattingTuple, String methodName,
            TraceMetricName traceMetricName) {
        String formattedMessage = formattingTuple.getMessage();
        Throwable throwable = formattingTuple.getThrowable();
        if (markTraceAsError(methodName.equals("warn"), throwable != null)) {
            pluginServices.setTraceError(formattedMessage);
        }
        Span span = pluginServices.startSpan(
                MessageSupplier.from("log {}: {}", methodName, formattedMessage),
                traceMetricName);
        return new LogAdviceTraveler(span, formattedMessage, throwable);
    }

    private static void onAfter(LogAdviceTraveler traveler) {
        Throwable t = traveler.throwable;
        if (t == null) {
            traveler.span.endWithError(ErrorMessage.from(traveler.formattedMessage));
        } else {
            traveler.span.endWithError(ErrorMessage.from(t.getMessage(), t));
        }
    }

    @Pointcut(type = "org.slf4j.Logger", methodName = "warn|error",
            methodArgTypes = {"java.lang.String"}, traceMetric = METRIC_NAME)
    public static class LogNoArgAdvice {
        private static final TraceMetricName traceMetricName =
                pluginServices.getTraceMetricName(LogNoArgAdvice.class);
        @IsEnabled
        public static boolean isEnabled() {
            return pluginServices.isEnabled() && !LoggerPlugin.inAdvice.get();
        }
        @OnBefore
        public static Span onBefore(@BindMethodArg String message,
                @BindMethodName String methodName) {
            LoggerPlugin.inAdvice.set(true);
            if (markTraceAsError(methodName.equals("warn"), false)) {
                pluginServices.setTraceError(message);
            }
            return pluginServices.startSpan(
                    MessageSupplier.from("log {}: {}", methodName, message),
                    traceMetricName);
        }
        @OnAfter
        public static void onAfter(@BindTraveler Span span, @BindMethodArg String message) {
            LoggerPlugin.inAdvice.set(false);
            span.endWithError(ErrorMessage.from(message));
        }
    }

    @Pointcut(type = "org.slf4j.Logger", methodName = "warn|error",
            methodArgTypes = {"java.lang.String", "java.lang.Object"}, traceMetric = METRIC_NAME)
    public static class LogOneArgAdvice {
        private static final TraceMetricName traceMetricName =
                pluginServices.getTraceMetricName(LogOneArgAdvice.class);
        @IsEnabled
        public static boolean isEnabled() {
            return pluginServices.isEnabled() && !LoggerPlugin.inAdvice.get();
        }
        @OnBefore
        public static LogAdviceTraveler onBefore(@BindMethodArg String format,
                @BindMethodArg Object arg, @BindMethodName String methodName) {
            LoggerPlugin.inAdvice.set(true);
            FormattingTuple formattingTuple = MessageFormatter.format(format, arg);
            return Slf4jAspect.onBefore(formattingTuple, methodName, traceMetricName);
        }
        @OnAfter
        public static void onAfter(@BindTraveler LogAdviceTraveler traveler) {
            LoggerPlugin.inAdvice.set(false);
            Slf4jAspect.onAfter(traveler);
        }
    }

    @Pointcut(type = "org.slf4j.Logger", methodName = "warn|error",
            methodArgTypes = {"java.lang.String", "java.lang.Throwable"}, traceMetric = METRIC_NAME)
    public static class LogOneArgThrowableAdvice {
        private static final TraceMetricName traceMetricName =
                pluginServices.getTraceMetricName(LogOneArgThrowableAdvice.class);
        @IsEnabled
        public static boolean isEnabled() {
            return pluginServices.isEnabled() && !LoggerPlugin.inAdvice.get();
        }
        @OnBefore
        public static LogAdviceTraveler onBefore(@BindMethodArg String format,
                @BindMethodArg Object arg, @BindMethodName String methodName) {
            LoggerPlugin.inAdvice.set(true);
            FormattingTuple formattingTuple = MessageFormatter.format(format, arg);
            return Slf4jAspect.onBefore(formattingTuple, methodName, traceMetricName);
        }
        @OnAfter
        public static void onAfter(@BindTraveler LogAdviceTraveler traveler) {
            LoggerPlugin.inAdvice.set(false);
            Slf4jAspect.onAfter(traveler);
        }
    }

    @Pointcut(type = "org.slf4j.Logger", methodName = "warn|error",
            methodArgTypes = {"java.lang.String", "java.lang.Object", "java.lang.Object"},
            traceMetric = METRIC_NAME)
    public static class LogTwoArgsAdvice {
        private static final TraceMetricName traceMetricName =
                pluginServices.getTraceMetricName(LogTwoArgsAdvice.class);
        @IsEnabled
        public static boolean isEnabled() {
            return pluginServices.isEnabled() && !LoggerPlugin.inAdvice.get();
        }
        @OnBefore
        public static LogAdviceTraveler onBefore(@BindMethodArg String format,
                @BindMethodArg Object arg1, @BindMethodArg Object arg2,
                @BindMethodName String methodName) {
            LoggerPlugin.inAdvice.set(true);
            FormattingTuple formattingTuple = MessageFormatter.format(format, arg1, arg2);
            return Slf4jAspect.onBefore(formattingTuple, methodName, traceMetricName);
        }
        @OnAfter
        public static void onAfter(@BindTraveler LogAdviceTraveler traveler) {
            LoggerPlugin.inAdvice.set(false);
            Slf4jAspect.onAfter(traveler);
        }
    }

    @Pointcut(type = "org.slf4j.Logger", methodName = "warn|error",
            methodArgTypes = {"java.lang.String", "java.lang.Object[]"}, traceMetric = METRIC_NAME)
    public static class LogAdvice {
        private static final TraceMetricName traceMetricName =
                pluginServices.getTraceMetricName(LogAdvice.class);
        @IsEnabled
        public static boolean isEnabled() {
            return pluginServices.isEnabled() && !LoggerPlugin.inAdvice.get();
        }
        @OnBefore
        public static LogAdviceTraveler onBefore(@BindMethodArg String format,
                @BindMethodArg Object[] arguments, @BindMethodName String methodName) {
            LoggerPlugin.inAdvice.set(true);
            FormattingTuple formattingTuple = MessageFormatter.arrayFormat(format, arguments);
            return Slf4jAspect.onBefore(formattingTuple, methodName, traceMetricName);
        }
        @OnAfter
        public static void onAfter(@BindTraveler LogAdviceTraveler traveler) {
            LoggerPlugin.inAdvice.set(false);
            Slf4jAspect.onAfter(traveler);
        }
    }

    private static class LogAdviceTraveler {
        private final Span span;
        private final String formattedMessage;
        private final Throwable throwable;
        private LogAdviceTraveler(Span span, String formattedMessage, Throwable throwable) {
            this.span = span;
            this.formattedMessage = formattedMessage;
            this.throwable = throwable;
        }
    }
}
