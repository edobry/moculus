package com.edobry.moculus;

import com.edobry.moculus.domain.Signup;
import com.edobry.moculus.service.SignupSimulator;
import com.edobry.moculus.service.StatusReporter;
import com.edobry.moculus.service.image.MockObjectStorageProvider;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class SignupSimulatorTests {
    private static Integer randomInRange(Integer min, Integer max) {
        // stolen from docs of Math.random
        double f = Math.random() / Math.nextDown(1);
        return (int) (min * (1 - f) + max * f);
    }

//- signups contain a URL
    private static SignupSimulator makeSignupSimulator() {
        return new SignupSimulator(
            new MockObjectStorageProvider(
                new MockObjectStorageProvider.MockObjectStorageProviderProperties(true, "test", randomInRange(8000, 9000))),
            new SignupSimulator.SignupSimulatorProperties(""));
    }

    @Test
    public void signupsContainUrl() throws IOException {
        SignupSimulator signupSimulator = makeSignupSimulator();
        Signup signup = signupSimulator.generateSignup();

        assertNotNull(signup.irisUrl);
    }

    @Test
    public void signupUrlsDownloadable() throws IOException, URISyntaxException, InterruptedException {
        SignupSimulator signupSimulator = makeSignupSimulator();
        Signup signup = signupSimulator.generateSignup();

        HttpResponse<byte[]> httpResponse = TestUtil.downloadImage(signup.irisUrl.toURI());
        assert httpResponse.body().length > 0;
    }

    @Test
    public void signupImagesArePngs() throws IOException, URISyntaxException, InterruptedException {
        SignupSimulator signupSimulator = makeSignupSimulator();
        Signup signup = signupSimulator.generateSignup();

        HttpResponse<byte[]> httpResponse = TestUtil.downloadImage(signup.irisUrl.toURI());

        ByteArrayInputStream inputStream = new ByteArrayInputStream(httpResponse.body());
        String format = null;

        // stolen from https://stackoverflow.com/a/36770963/265680
        try (ImageInputStream iis = ImageIO.createImageInputStream(inputStream)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                format = reader.getFormatName();
                reader.setInput(iis);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error determining image type", e);
        }

        assertEquals("png", format);
    }

    @Test
    public void signupImagesAreUnique() throws IOException, URISyntaxException, InterruptedException {
        SignupSimulator signupSimulator = makeSignupSimulator();

        Signup signup1 = signupSimulator.generateSignup();
        HttpResponse<byte[]> httpResponse1 = TestUtil.downloadImage(signup1.irisUrl.toURI());
        byte[] digest1 = DigestUtils.getSha1Digest().digest(httpResponse1.body());

        Signup signup2 = signupSimulator.generateSignup();
        HttpResponse<byte[]> httpResponse2 = TestUtil.downloadImage(signup2.irisUrl.toURI());
        byte[] digest2 = DigestUtils.getSha1Digest().digest(httpResponse2.body());

        assertNotEquals(digest1, digest2);
    }

    @Test
    public void signupsContainId() throws IOException {
        SignupSimulator signupSimulator = makeSignupSimulator();
        Signup signup = signupSimulator.generateSignup();

        assertNotNull(signup.id);
    }

    @Test
    public void signupIdsAreUnique() throws IOException, URISyntaxException, InterruptedException {
        SignupSimulator signupSimulator = makeSignupSimulator();

        Signup signup1 = signupSimulator.generateSignup();
        Signup signup2 = signupSimulator.generateSignup();

        assertNotEquals(signup1.id, signup2.id);
    }
}
