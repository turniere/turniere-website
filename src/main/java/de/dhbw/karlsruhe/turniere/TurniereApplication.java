package de.dhbw.karlsruhe.turniere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TurniereApplication {

    public static void main(String[] args) {
        SpringApplication.run(TurniereApplication.class, args);
    }
}
