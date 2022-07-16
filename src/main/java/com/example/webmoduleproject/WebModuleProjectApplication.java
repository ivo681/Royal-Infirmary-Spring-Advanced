package com.example.webmoduleproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAsync
@SpringBootApplication
public class WebModuleProjectApplication {
    //v16/07/2022

    public static void main(String[] args) {
        SpringApplication.run(WebModuleProjectApplication.class, args);
    }

}
