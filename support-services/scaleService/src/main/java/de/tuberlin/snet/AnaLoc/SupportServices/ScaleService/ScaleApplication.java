package de.tuberlin.snet.AnaLoc.SupportServices.ScaleService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAutoConfiguration
@EnableScheduling
@SpringBootApplication
@EnableDiscoveryClient
public class ScaleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScaleApplication.class, args);
    }
}