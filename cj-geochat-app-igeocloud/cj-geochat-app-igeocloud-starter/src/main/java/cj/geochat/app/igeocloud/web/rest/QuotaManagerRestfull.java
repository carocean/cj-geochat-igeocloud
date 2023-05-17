package cj.geochat.app.igeocloud.web.rest;

import cj.geochat.ability.api.annotation.ApiResult;
import cj.geochat.ability.minio.INetDiskService;
import cj.geochat.app.igeocloud.restfull.IQuotaManagerRestfull;
import cj.geochat.util.minio.MinioQuotaUnit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/igeocloud/quota")
@Api(tags = {"云盘配额管理"})
@RefreshScope
@Slf4j
public class QuotaManagerRestfull implements IQuotaManagerRestfull {
    @Autowired
    INetDiskService netDiskService;

    @GetMapping("/setSystemDiskQuota")
    @ApiOperation("设置系统磁盘配额")
    @ApiResult
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    @PreAuthorize("hasAnyAuthority('igeocloud:system:admin')")
    @Override
    public void setSystemDiskQuota(long size, MinioQuotaUnit unit) {
        netDiskService.setDiskQuota("system", size, unit);
    }

    @GetMapping("/clearSystemDiskQuota")
    @ApiOperation("清除系统磁盘配额")
    @ApiResult
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    @PreAuthorize("hasAnyAuthority('igeocloud:system:admin')")
    @Override
    public void clearSystemDiskQuota() {
        netDiskService.clearDiskQuota("system");
    }

    @GetMapping("/setPublicDiskQuota")
    @ApiOperation("设置公共磁盘配额")
    @ApiResult
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    @PreAuthorize("hasAnyAuthority('igeocloud:system:admin')")
    @Override
    public void setPublicDiskQuota(long size, MinioQuotaUnit unit) {
        netDiskService.setDiskQuota("public", size, unit);
    }

    @GetMapping("/clearPublicDiskQuota")
    @ApiOperation("清除公共磁盘配额")
    @ApiResult
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    @PreAuthorize("hasAnyAuthority('igeocloud:system:admin')")
    @Override
    public void clearPublicDiskQuota() {
        netDiskService.clearDiskQuota("public");
    }

    @GetMapping("/setUserDiskQuota")
    @ApiOperation("设置用户磁盘配额")
    @ApiResult
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    @PreAuthorize("hasAnyAuthority('igeocloud:system:admin')")
    @Override
    public void setUserDiskQuota(String username, long size, MinioQuotaUnit unit) {
        netDiskService.setDiskQuota(String.format("%s@igeocloud.com", username), size, unit);
    }

    @GetMapping("/clearUserDiskQuota")
    @ApiOperation("清除用户磁盘配额")
    @ApiResult
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    @PreAuthorize("hasAnyAuthority('igeocloud:system:admin')")
    @Override
    public void clearUserDiskQuota(String username) {
        netDiskService.clearDiskQuota(String.format("%s@igeocloud.com", username));
    }
}
