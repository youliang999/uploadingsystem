package com.youliang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.youliang"})
public class    MallsystemApplication {


    public static void main(String[] args) {
        SpringApplication.run(MallsystemApplication.class, args);
        System.out.println("===>>> upload system start success. ");
    }

}

