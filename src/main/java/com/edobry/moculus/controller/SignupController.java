package com.edobry.moculus.controller;

import com.edobry.moculus.domain.Signup;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class SignupController {
    @PostMapping("signup")
    public boolean signup(@Valid @RequestBody Signup signup) {
        log.info("Received signup: id {} url {}", signup.id, signup.irisUrl);
        return true;
    }
}
