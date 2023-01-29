package com.edobry.moculus.service;

import com.amazonaws.util.StringUtils;
import com.amazonaws.util.json.Jackson;
import com.edobry.moculus.domain.Signup;
import com.edobry.moculus.service.image.ObjectStorageProvider;
import com.edobry.moculus.service.image.S3StorageProvider;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignupSimulator {
    private final ObjectStorageProvider s3Client;
    public final SignupSimulatorProperties properties;

    @Value("${server.port}")
    public String serverPort;

    @Data
    @ConfigurationProperties("signup")
    public static class SignupSimulatorProperties {
        public final String backend;

//        public final Integer port;
    }

    @Scheduled(fixedRate = 10000)
    public void submitSignup() throws IOException, InterruptedException {
        BufferedImage irisImage = this.makeIrisImage();
        String id = UUID.randomUUID().toString();
//        Signup signup = makeSignup();

        log.info("Uploading to S3...");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(irisImage, "png", outputStream);
        outputStream.close();

        URL irisUrl = s3Client.add(id, outputStream.toByteArray());

        Signup signup = new Signup(irisUrl, id);

        log.info("simulating signup: id {} url {}", id, irisUrl);
        HttpClient httpClient = HttpClient.newHttpClient();
        String backendHost = !StringUtils.isNullOrEmpty(properties.backend) ? properties.backend :
            String.format("localhost:%s", serverPort);

        URI backendUrl = null;
        try {
            backendUrl = new URI(String.format("http://%s/signup", backendHost));
        } catch (URISyntaxException e) {
            log.error("bad backend URL! {}", backendUrl);
            throw new RuntimeException(e);
        }
        HttpRequest request = HttpRequest.newBuilder(backendUrl)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(
                Jackson.toJsonString(signup))).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Received response: {}", response.body());
    }

//    public Signup makeSignup() throws IOException {
//        //signup has image and id
//        return new Signup(this.makeIrisImage(), UUID.randomUUID().toString());
//    }

    private final int IMAGE_SIZE = 500;

    public BufferedImage makeIrisImage() throws IOException {
        BufferedImage img = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < IMAGE_SIZE; y++) {
            for (int x = 0; x < IMAGE_SIZE; x++) {
                int r = (int) (Math.random() * 256); //red
                int g = (int) (Math.random() * 256); //green
                int b = (int) (Math.random() * 256); //blue

                // blatantly stole this
                int p = (r << 16) | (g << 8) | b; //pixel

                img.setRGB(x, y, p);
            }
        }

        return img;
    }
}
