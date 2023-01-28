package com.edobry.moculus.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.awt.image.BufferedImage;

@Data
@RequiredArgsConstructor
public class Signup {
    public final BufferedImage iris;
    public final String id;
}
