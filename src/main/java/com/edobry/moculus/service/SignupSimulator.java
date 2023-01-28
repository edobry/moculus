package com.edobry.moculus.service;

import com.edobry.moculus.domain.Signup;
import com.edobry.moculus.service.image.ObjectStorageProvider;
import com.edobry.moculus.service.image.S3StorageProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignupSimulator {
    private final ObjectStorageProvider s3Client;

    @Scheduled(fixedRate = 5000)
    public void submitSignup() throws IOException {
        Signup signup = makeSignup();

        log.info("Uploading to S3...");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(signup.iris, "png", outputStream);
        outputStream.close();

        URL irisUrl = s3Client.add(signup.id, outputStream.toByteArray());

        log.info("simulating signup: id {} url {}", signup.id, irisUrl);
    }
    public Signup makeSignup() throws IOException {
        //signup has image and id
        return new Signup(this.makeIrisImage(), UUID.randomUUID().toString());
    }

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
