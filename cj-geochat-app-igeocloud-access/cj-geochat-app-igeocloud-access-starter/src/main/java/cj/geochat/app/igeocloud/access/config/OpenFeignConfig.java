package cj.geochat.app.igeocloud.access.config;

import cj.geochat.ability.feign.annotation.EnableCjFeign;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@EnableCjFeign
@EnableFeignClients(basePackages = "cj.geochat.app.igeocloud.access")
@Configuration
public class OpenFeignConfig {
}
