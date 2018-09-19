package com.onw.game.current.role.controller.main;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.onw.game.current.role.controller")
@EnableJpaRepositories("com.onw.game.current.role.controller")
@EntityScan("com.onw.game.current.role.controller")
public class CurrentRoleControllerMain {
    public static void main(String[] args) {
        SpringApplication.run(CurrentRoleControllerMain.class, args);
    }
}
