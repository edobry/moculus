package com.edobry.moculus.service.image;

import java.net.URL;

public interface ObjectStorageProvider {
    URL add(String name, byte[] media);

    void clear();
}

