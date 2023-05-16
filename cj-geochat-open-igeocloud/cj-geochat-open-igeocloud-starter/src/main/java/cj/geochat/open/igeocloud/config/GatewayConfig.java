package cj.geochat.open.igeocloud.config;

import cj.geochat.ability.oauth2.gateway.ICheckPermission;
import cj.geochat.ability.oauth2.gateway.annotation.EnableCjOAuth2Gateway;
import cj.geochat.ability.oauth2.gateway.config.SecurityWorkbin;
import cj.geochat.ability.redis.annotation.EnableCjRedis;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

@EnableCjOAuth2Gateway
@EnableCjRedis
@Configuration
public class GatewayConfig extends SecurityWorkbin {
    @Override
    protected ICheckPermission createCheckPermission() {
        //所有中台的服务一律拦截掉，网关只充许geochat app通过，
        return (antPathMatcher,username, role,resourceId, accessUrl) ->
                !antPathMatcher.match("/cj-geochat-middle-*/*/**",accessUrl);
    }

}
