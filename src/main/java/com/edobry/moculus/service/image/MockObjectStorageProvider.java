package com.edobry.moculus.service.image;

import akka.http.scaladsl.Http;
import io.findify.s3mock.S3Mock;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import scala.concurrent.duration.FiniteDuration;
import scala.jdk.javaapi.CollectionConverters;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MockObjectStorageProvider extends S3StorageProvider {
    private final Http.ServerBinding serverBinding;

    @Data
    @ConfigurationProperties("mock-backend")
    public static class MockObjectStorageProviderProperties {
        public final Boolean enabled;

        public final String path;

        public final Integer port;
    }

    public MockObjectStorageProvider(MockObjectStorageProviderProperties props) {
        super(String.format("http://localhost:%d", props.port));

        S3Mock api = new S3Mock.Builder().withPort(props.port).withFileBackend(props.path).build();
        this.serverBinding = api.start();

        // library provides scala list, needs conversion
        CollectionConverters.asJava(api.p().listBuckets().buckets()).stream()
            .filter(x -> !x.name().contains(".metadata"))
            .forEach(bucket -> api.p().deleteBucket(bucket.name()));
    }

    public void shutdown() {
        this.serverBinding.terminate(
            FiniteDuration.apply(10, TimeUnit.SECONDS));
    }
}

