package com.onw.edge.api.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@ComponentScan("com.onw.edge.api")
public class EdgeAPIMain {

    public static void main(String[] args) {
        SpringApplication.run(EdgeAPIMain.class, args);
    }

}
