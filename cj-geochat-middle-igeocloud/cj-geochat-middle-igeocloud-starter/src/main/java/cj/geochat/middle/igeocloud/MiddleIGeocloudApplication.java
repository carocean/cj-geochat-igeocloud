package cj.geochat.middle.igeocloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"cj.geochat.middle.igeocloud"})
@EnableDiscoveryClient
public class MiddleIGeocloudApplication {
    public static void main(String[] args) {
        SpringApplication.run(MiddleIGeocloudApplication.class, args);
    }
}
