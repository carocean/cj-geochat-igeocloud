package cj.geochat.app.igeocloud.access;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"cj.geochat.app.igeocloud.access"})
@EnableDiscoveryClient
public class AppAccessGeocloudApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppAccessGeocloudApplication.class, args);
    }
}
