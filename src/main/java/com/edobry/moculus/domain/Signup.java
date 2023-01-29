package com.edobry.moculus.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.net.URL;

@Data
@RequiredArgsConstructor
public class Signup {
    @NotNull
    public final URL irisUrl;

    @NotNull
    public final String id;
}
