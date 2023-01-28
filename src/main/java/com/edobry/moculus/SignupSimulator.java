package com.edobry.moculus;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

@Component
public class SignupSimulator {
    @Scheduled(fixedRate = 5000)
    public void submitSignup() throws IOException {
        Signup signup = makeSignup();
        System.out.println(signup);
    }
    public Signup makeSignup() throws IOException {
        //signup has image and id
        return new Signup(this.makeIrisImage(), UUID.randomUUID().toString());
    }

    private final int IMAGE_SIZE = 500;

    public String makeIrisImage() throws IOException {
        BufferedImage img = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);

        File f = null;

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

        f = new File("/home/edobry/Projects/moculus/img" + File.separator
                + Math.abs(new Random().nextInt()) + ".png");
        ImageIO.write(img, "png", f);

//        System.out.println("Generated file: " + f.getAbsolutePath());

        return f.getAbsolutePath();
    }
}
