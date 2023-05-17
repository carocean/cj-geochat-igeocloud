package cj.geochat.app.igeocloud.web.rest;

import cj.geochat.ability.api.annotation.ApiResult;
import cj.geochat.ability.minio.INetDiskService;
import cj.geochat.ability.util.GeochatException;
import cj.geochat.app.igeocloud.AbstractIGeocloudRestfull;
import cj.geochat.app.igeocloud.restfull.IPublicDiskRestfull;
import cj.geochat.util.minio.FilePath;
import io.minio.StatObjectResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/igeocloud/public")
@Api(tags = {"公共云盘"})
@RefreshScope
@Slf4j
public class PublicDiskRestfull extends AbstractIGeocloudRestfull implements IPublicDiskRestfull, InitializingBean {
    @Autowired
    INetDiskService netDiskService;

    private String getDiskName() {
        return "public";
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String diskName = getDiskName();
        checkDisk(netDiskService, String.format("igeocloud://%s", diskName));
    }

    @GetMapping("/getDiskQuota")
    @ApiOperation("获取云盘配额")
    @ApiResult
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    @Override
    public long getDiskQuota() {
        return netDiskService.getDiskQuota(getDiskName());
    }

    @GetMapping("/getDataUsageInfo")
    @ApiOperation("获取数据使用情况")
    @ApiResult
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    @Override
    public Map<String, Object> getDataUsageInfo() {
        return netDiskService.getDataUsageInfo();
    }


    @Override
    @GetMapping("/mkdir")
    @ApiOperation("创建目录")
    @ApiResult
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    @PreAuthorize("hasAnyAuthority('igeocloud:public:writeonly','igeocloud:public:readwrite')")
    public void mkdir(@RequestParam String relativeUrl) {
        String fullPath = String.format("igeocloud://%s", getDiskName(), relativeUrl);
        netDiskService.mkdir(fullPath);
    }


    @Override
    @GetMapping("/listChildren")
    @ApiOperation("列出目录内容")
    @ApiResult
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    public List<String> listChildren(@RequestParam String relativeUrl) {
        String fullPath = String.format("igeocloud://%s", getDiskName(), relativeUrl);
        return netDiskService.listChildren(fullPath, false);
    }

    @Override
    @GetMapping("/listChildrenByRecursive")
    @ApiOperation("列出目录内容【递归子目录】")
    @ApiResult
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    public List<String> listChildrenByRecursive(@RequestParam String relativeUrl, @RequestParam boolean recursive) {
        String fullPath = String.format("igeocloud://%s", getDiskName(), relativeUrl);
        return netDiskService.listChildren(fullPath, recursive);
    }

    @Override
    @PostMapping("/upload")
    @ApiOperation("上传文件")
    @ApiResult
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    public void upload(@RequestPart MultipartFile file, @RequestParam String relativeUrl) {
        String fullPath = String.format("igeocloud://%s", getDiskName(), relativeUrl);
        netDiskService.writeFile(file, fullPath);
    }

    @Override
    @GetMapping("/download")
    @ApiOperation("下载文件")
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    public void download(@RequestParam String relativeUrl, HttpServletResponse response) throws GeochatException {
        String fullPath = String.format("igeocloud://%s", getDiskName(), relativeUrl);
        FilePath filePath = FilePath.parse(fullPath);
        InputStream inputStream = netDiskService.readFile(filePath);
        doWrite(filePath, inputStream, response);
    }

    @Override
    @GetMapping("/seekDownload")
    @ApiOperation("下载文件【随机读取】")
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    public void seekDownload(@RequestParam String relativeUrl, @RequestParam long offset, @RequestParam long length, HttpServletResponse response) throws GeochatException {
        String fullPath = String.format("igeocloud://%s", getDiskName(), relativeUrl);
        FilePath filePath = FilePath.parse(fullPath);
        InputStream inputStream = netDiskService.readFile(filePath, offset, length);
        doWrite(filePath, inputStream, response);
    }

    @SneakyThrows
    private void doWrite(FilePath filePath, InputStream inputStream, HttpServletResponse response) {
        StatObjectResponse objectResponse = netDiskService.getFileInfo(filePath);
        response.setContentType(objectResponse.contentType());
        response.setContentLength((int) objectResponse.size());
        response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", filePath.getFilename()));
        byte[] buf = new byte[1024 * 512];//每次0.5m
        int readLen = 0;
        while ((readLen = inputStream.read(buf, 0, buf.length)) > -1) {
            response.getOutputStream().write(buf, 0, readLen);
        }
    }

    @Override
    @GetMapping("/delete")
    @ApiOperation("删除目录或文件")
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    @ApiResult
    @PreAuthorize("hasAuthority('igeocloud:public:delete')")
    public void delete(@RequestParam String relativeUrl) {
        String fullPath = String.format("igeocloud://%s", getDiskName(), relativeUrl);
        netDiskService.delete(fullPath);
    }

    @Override
    @GetMapping("/empty")
    @ApiOperation("清空目录")
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    @ApiResult
    @PreAuthorize("hasAuthority('igeocloud:public:delete')")
    public void empty(@RequestParam String relativeUrl) {
        String fullPath = String.format("igeocloud://%s", getDiskName(), relativeUrl);
        netDiskService.empty(fullPath);
    }

    @Override
    @GetMapping("/accessUrl")
    @ApiOperation(value = "获取文件访问路径", notes = "")
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    @ApiResult
    public String accessUrl(@RequestParam String relativeUrl, HttpServletRequest request) {
        String fullPath = String.format("igeocloud://%s", getDiskName(), relativeUrl);
        FilePath filePath = FilePath.parse(fullPath);
        return String.format("/api/v1/igeocloud/%s-%s-+-%s",
                 filePath.getBucketName(), filePath.getRelativePath(), filePath.getFilename());
    }

    @Override
    @GetMapping("/exists")
    @ApiOperation("判断文件或目录是否存在")
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    @ApiResult
    public boolean exists(@RequestParam String relativeUrl) {
        String fullPath = String.format("igeocloud://%s", getDiskName(), relativeUrl);
        return netDiskService.exists(fullPath);
    }
}
