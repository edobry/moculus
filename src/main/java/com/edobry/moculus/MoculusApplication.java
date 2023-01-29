package com.edobry.moculus;

import com.edobry.moculus.service.SignupSimulator;
import com.edobry.moculus.service.image.MockObjectStorageProvider;
import com.edobry.moculus.service.image.ObjectStorageProvider;
import com.edobry.moculus.service.image.S3StorageProvider;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.micrometer.statsd.StatsdConfig;
import io.micrometer.statsd.StatsdMeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.export.statsd.StatsdProperties;
import org.springframework.boot.actuate.autoconfigure.metrics.export.statsd.StatsdPropertiesConfigAdapter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({
	MockObjectStorageProvider.MockObjectStorageProviderProperties.class,
	SignupSimulator.SignupSimulatorProperties.class,
	StatsdProperties.class
})
public class MoculusApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoculusApplication.class, args);
	}

	@Bean
	public ObjectStorageProvider storageProvider(MockObjectStorageProvider.MockObjectStorageProviderProperties props) {
		return props.enabled
			? new MockObjectStorageProvider(props)
			: new S3StorageProvider();
	}

	@Bean
	public MeterRegistry meterRegistry(PrometheusConfig prometheusConfig, StatsdProperties statsdProperties, Clock clock) {
		CompositeMeterRegistry composite = new CompositeMeterRegistry();
		composite.add(new PrometheusMeterRegistry(prometheusConfig));
		composite.add(new StatsdMeterRegistry(new StatsdPropertiesConfigAdapter(statsdProperties), clock));

		return composite;
	}

	@Autowired
	private Environment environment;
}
