package com.edobry.moculus;

import com.edobry.moculus.service.StatusReporter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.statsd.StatsdMeterRegistry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StatusReporterTests {
    private static SimpleMeterRegistry makeReporter() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        StatusReporter statusReporter = new StatusReporter(registry);

        return registry;
    }

    private static Boolean checkMeterExists(MeterRegistry registry, String name) {
        return registry.getMeters().stream()
            .map(x -> x.getId().getName())
            .anyMatch(name::equals);
    }

    @Test
    public void batteryIsReported() {
        assertTrue(checkMeterExists(makeReporter(), "battery"));
    }

    @Test
    public void cpuTempIsReported(){
        assertTrue(checkMeterExists(makeReporter(), "cpu_temp"));
    }
    
    @Test
    public void cpuUtilIsReported(){
        assertTrue(checkMeterExists(makeReporter(), "cpu_util"));
    }
    
    @Test
    public void diskFreeIsReported(){
        assertTrue(checkMeterExists(makeReporter(), "disk_free"));
    }
}


