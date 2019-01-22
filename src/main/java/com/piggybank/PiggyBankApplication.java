package com.piggybank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@EntityScan(
        basePackageClasses = {PiggyBankApplication.class, Jsr310JpaConverters.class}
)
@SpringBootApplication
public class PiggyBankApplication {
    public static void main(final String[] args) {
        SpringApplication.run(PiggyBankApplication.class, args);
    }
}