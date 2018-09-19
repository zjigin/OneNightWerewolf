package com.onw.game.controller.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//@EnableEurekaClient
@ComponentScan("com.onw.game.controller")
@EnableJpaRepositories("com.onw.game.controller")
@EntityScan("com.onw.game.controller")
public class GameControllerMain {

    public static void main(String[] args) {
        SpringApplication.run(GameControllerMain.class, args);
    }

}
