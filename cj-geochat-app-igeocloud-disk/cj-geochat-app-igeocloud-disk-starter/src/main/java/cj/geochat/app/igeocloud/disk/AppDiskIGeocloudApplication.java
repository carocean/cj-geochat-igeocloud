package cj.geochat.app.igeocloud.disk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"cj.geochat.app.igeocloud.disk"})
@EnableDiscoveryClient
public class AppDiskIGeocloudApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppDiskIGeocloudApplication.class, args);
    }
}
