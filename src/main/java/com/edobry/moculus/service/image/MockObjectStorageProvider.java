package com.edobry.moculus.service.image;

import akka.http.scaladsl.Http;
import io.findify.s3mock.S3Mock;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

public class MockObjectStorageProvider extends S3StorageProvider {
    private final Http.ServerBinding serverBinding;

//    @ConstructorBinding
    @ConfigurationProperties("mock-backend")
    @Data
    public static class MockObjectStorageProviderProperties {
        public final Boolean enabled;

        public final String path;

        public final Integer port;
    }

    public MockObjectStorageProvider(MockObjectStorageProviderProperties props) {
        super(String.format("http://localhost:%d", props.port));

        S3Mock api = new S3Mock.Builder().withPort(props.port).withFileBackend(props.path).build();
        this.serverBinding = api.start();
        this.clear();
    }

    public void shutdown() {
        this.serverBinding.terminate(
            FiniteDuration.apply(10, TimeUnit.SECONDS));
    }
}

