package com.edobry.moculus.service;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.statsd.StatsdConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.ToDoubleFunction;

@Component
public class StatusReporter {
    private final MeterRegistry meterRegistry;

    public StatusReporter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        List<Tag> tags = List.of(Tag.of("simulated", "true"));
        this.meterRegistry.gauge("battery", tags, this, randomInRange(0., 100.));
        this.meterRegistry.gauge("cpu_temp", tags, this, randomInRange(40., 90.));
        this.meterRegistry.gauge("cpu_util", tags, this, randomInRange(0., 100.));
        this.meterRegistry.gauge("disk_free", tags, this, randomInRange(0., 100.));
    }

    public static <T> ToDoubleFunction<T> randomInRange(Double min, Double max) {
        return (x) -> {
            // stolen from docs of Math.random
            double f = Math.random() / Math.nextDown(1.0);
            return min * (1.0 - f) + max * f;
        };
    }
}
