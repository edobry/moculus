package com.edobry.moculus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TestUtil {
    public static HttpResponse<byte[]> downloadImage(URI uri) throws IOException, InterruptedException, URISyntaxException {
        HttpClient httpClient = HttpClient.newHttpClient();

        // default to embedded server
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
    }
}
