package com.edobry.moculus;

import com.edobry.moculus.service.image.MockObjectStorageProvider;
import com.edobry.moculus.service.image.ObjectStorageProvider;
import com.edobry.moculus.service.image.S3StorageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(MockObjectStorageProvider.MockObjectStorageProviderProperties.class)
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

	@Autowired
	private Environment environment;
}
