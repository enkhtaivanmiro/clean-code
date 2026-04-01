package com.pos.branch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BranchServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BranchServerApplication.class, args);
    }
}
