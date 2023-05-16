package cj.geochat.app.igeocloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"cj.geochat.app.igeocloud"})
@EnableDiscoveryClient
public class AppIGeocloudApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppIGeocloudApplication.class, args);
    }
}
