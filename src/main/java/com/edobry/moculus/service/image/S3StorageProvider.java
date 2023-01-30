package com.edobry.moculus.service.image;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;
import java.net.URL;

@Slf4j
public class S3StorageProvider implements ObjectStorageProvider {
    private final S3Client client;

    @Data
    @ConfigurationProperties("aws")
    public static class AwsProperties {
        public final String accessKeyId;

        public final String secretAccessKey;
    }


    private S3ClientBuilder buildClient(AwsProperties properties) {
        return S3Client.builder().region(Region.US_EAST_1)
            .credentialsProvider(() ->
                AwsBasicCredentials.create(properties.accessKeyId, properties.secretAccessKey))
            .serviceConfiguration(
                S3Configuration.builder()
                    .pathStyleAccessEnabled(true).build());
    }

    public S3StorageProvider(AwsProperties properties) {
        this.client = buildClient(properties).build();
    }

    public S3StorageProvider(String endpoint) {
        this.client = buildClient(
            // fake credentials for mock backend
            new AwsProperties("test", "test")
        ).endpointOverride(
            URI.create(endpoint)
        ).build();
    }

    private static final String BUCKET_NAME = "iris";

    public URL add(String name, byte[] bytes) {
        this.client.createBucket(x -> x.bucket(BUCKET_NAME));

        var response = this.client.putObject(
            builder -> builder.bucket(BUCKET_NAME).key(name),
            RequestBody.fromBytes(bytes));

        return getUrlForKey(name);
    }

    private URL getUrlForKey(String name) {
        return this.client.utilities().getUrl(x -> x.bucket(BUCKET_NAME).key(name));
    }

    @Override
    public void clear() {
        log.info("Clearing object storage...");

        this.client.listBuckets().buckets().forEach(bucket ->
            this.client.deleteBucket(builder ->
                builder.bucket(bucket.name())));
    }
}
