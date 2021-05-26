package com.xuliang.tc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.xuliang.*"})
public class TCClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(TCClientApplication.class, args);
    }

}
