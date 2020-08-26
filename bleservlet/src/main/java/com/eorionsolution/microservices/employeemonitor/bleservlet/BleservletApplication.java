package com.eorionsolution.microservices.employeemonitor.bleservlet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class BleservletApplication {

    public static void main(String[] args) {
        SpringApplication.run(BleservletApplication.class, args);
    }

}
