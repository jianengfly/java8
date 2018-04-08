package com.rograndec.jianeng.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

@SpringBootApplication
@EnableAutoConfiguration
@EnableSpringHttpSession
public class Application {
    public static void main (String args[]) {
        SpringApplication.run(Application.class, args);
    }
}
