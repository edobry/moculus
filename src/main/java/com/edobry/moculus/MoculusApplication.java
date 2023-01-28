package com.edobry.moculus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MoculusApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoculusApplication.class, args);
		System.out.println("test");
	}

}
