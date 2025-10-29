package com.beyond.qiin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class QiinApplication {

    public static void main(String[] args) {
        SpringApplication.run(QiinApplication.class, args);
    }
}
