package cj.geochat.open.igeocloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"cj.geochat.open.igeocloud"})
@EnableDiscoveryClient
public class OpenIGeocloudApplication {
    public static void main(String[] args) {
        SpringApplication.run(OpenIGeocloudApplication.class, args);
    }
}
