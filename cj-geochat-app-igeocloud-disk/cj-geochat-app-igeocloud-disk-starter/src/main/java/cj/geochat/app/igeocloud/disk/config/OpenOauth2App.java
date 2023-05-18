package cj.geochat.app.igeocloud.disk.config;

import cj.geochat.ability.oauth2.app.annotation.EnableCjOAuth2App;
import cj.geochat.ability.oauth2.app.config.AppSecurityWorkbin;
import org.springframework.context.annotation.Configuration;

@EnableCjOAuth2App
@Configuration
public class OpenOauth2App extends AppSecurityWorkbin {
}
