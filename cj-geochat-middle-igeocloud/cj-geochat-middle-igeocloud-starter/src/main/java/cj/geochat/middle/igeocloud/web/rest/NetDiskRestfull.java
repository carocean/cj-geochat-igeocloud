package cj.geochat.middle.igeocloud.web.rest;

import cj.geochat.ability.api.annotation.ApiResult;
import cj.geochat.ability.api.exception.ApiException;
import cj.geochat.ability.minio.INetDiskService;
import cj.geochat.ability.util.GeochatException;
import cj.geochat.middle.igeocloud.rest.INetDiskRestfull;
import cj.geochat.util.minio.FilePath;
import cj.geochat.util.minio.MinioQuotaUnit;
import io.minio.StatObjectResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/igeocloud")
@Api(tags = {"云盘管理"})
@RefreshScope
@Slf4j
public class NetDiskRestfull implements INetDiskRestfull {
    @Autowired
    INetDiskService netDiskService;

    @GetMapping("/createDisk")
    @ApiOperation("创建云盘")
    @ApiResult
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    @Override
    public void createDisk(String diskName, @ApiParam(value = "云盘大小，如果<=0表示不受限制") long size, @ApiParam(value = "菜用的单位，默认为M", required = false) MinioQuotaUnit unit) {
        if (unit == null) {
            unit = MinioQuotaUnit.MB;
        }
        netDiskService.createDisk(diskName, size, unit);
    }

    @GetMapping("/setDiskQuota")
    @ApiOperation("设置云盘配额")
    @ApiResult
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    @Override
    public void setDiskQuota(String diskName, long size, MinioQuotaUnit unit) {
        if (unit == null) {
            unit = MinioQuotaUnit.MB;
        }
        netDiskService.setDiskQuota(diskName, size, unit);
    }

    @GetMapping("/clearDiskQuota")
    @ApiOperation("清除云盘配额")
    @ApiResult
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    @Override
    public void clearDiskQuota(String diskName) {
        netDiskService.clearDiskQuota(diskName);
    }

    @GetMapping("/queryDiskPolicy")
    @ApiOperation("查看云盘策略")
    @ApiResult
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    @Override
    public String queryDiskPolicy(String diskName) {
        return netDiskService.queryDiskPolicy(diskName);
    }

    @GetMapping("/getDiskQuota")
    @ApiOperation("获取云盘配额")
    @ApiResult
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    @Override
    public long getDiskQuota(String diskName) {
        return netDiskService.getDiskQuota(diskName);
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
    public void mkdir(@RequestParam String path) {
        netDiskService.mkdir(path);
    }

    @Override
    @GetMapping("/listChildren")
    @ApiOperation("列出目录内容")
    @ApiResult
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    public List<String> listChildren(@RequestParam String path) {
        return netDiskService.listChildren(path, false);
    }

    @Override
    @GetMapping("/listChildrenByRecursive")
    @ApiOperation("列出目录内容【递归子目录】")
    @ApiResult
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    public List<String> listChildrenByRecursive(@RequestParam String path, @RequestParam boolean recursive) {
        return netDiskService.listChildren(path, recursive);
    }

    @Override
    @PostMapping("/upload")
    @ApiOperation("上传文件")
    @ApiResult
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    public void upload(@RequestPart MultipartFile file, @RequestParam String path) {
        netDiskService.writeFile(file, path);
    }

    @GetMapping("/{file}")
    @ApiOperation("请求文件")
    @ApiResult
    //浏览器请求文件
    @Override
    public void igeocloud(@PathVariable("file") String file, HttpServletRequest request, HttpServletResponse response) throws GeochatException, IOException {
        if (!StringUtils.hasText(file)) {
            throw new ApiException("404", "Missing file parameter. ");
        }
        if (file.indexOf("-") < 1) {
            throw new ApiException("500", "The request address format is incorrect. ");
        }
        int dirEndPos = file.indexOf("-+-");
        if (dirEndPos <= 0) {
            throw new ApiException("500", "The request address format is incorrect, No terminator.");
        }
        String dir = file.substring(0, dirEndPos);
        String fn = file.substring(dirEndPos + "-+-".length());
        file = "igeocloud://" + dir.replace("-", "/") +"/"+ fn;
        InputStream inputStream = netDiskService.readFile(file);
        StatObjectResponse objectResponse = netDiskService.getFileInfo(FilePath.parse(file));
        if (StringUtils.hasText(request.getContentType())) {
            response.setContentType(request.getContentType());
        } else {
            response.setContentType(objectResponse.contentType());
        }
//        response.setContentLength((int) objectResponse.size());
        byte[] buf = new byte[1024 * 512];//每次0.5m
        int readLen = 0;
        while ((readLen = inputStream.read(buf, 0, buf.length)) > -1) {
            response.getOutputStream().write(buf, 0, readLen);
            response.getOutputStream().flush();
        }
    }

    @Override
    @GetMapping("/download")
    @ApiOperation("下载文件")
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    public void download(@RequestParam String path, HttpServletResponse response) throws GeochatException {
        FilePath filePath = FilePath.parse(path);
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
    public void seekDownload(@RequestParam String path, @RequestParam long offset, @RequestParam long length, HttpServletResponse response) throws GeochatException {
        FilePath filePath = FilePath.parse(path);
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
    public void delete(@RequestParam String path) {
        netDiskService.delete(path);
    }

    @Override
    @GetMapping("/empty")
    @ApiOperation("清空目录")
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    @ApiResult
    public void empty(@RequestParam String path) {
        netDiskService.empty(path);
    }

    @Override
    @GetMapping("/accessUrl")
    @ApiOperation(value = "获取目录或文件访问路径", notes = "该接口没啥用：- 返回的是容器内ip且地址加签无法改；- 地址动态且有七天限制，用到界面需先生成影响性能。故需使用永久地址，要在minio管理台配制桶的access rule，使之开放即可使用静态链接。")
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    @ApiResult
    public String accessUrl(@RequestParam String path, @ApiParam(required = true, example = "失效时间：-1表示无限制。") int expirySeconds) {
        return netDiskService.accessUrl(path, expirySeconds);
    }

    @Override
    @GetMapping("/exists")
    @ApiOperation("判断文件或目录是否存在")
    @ApiResponses({
            @ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "1002", description = "null_parameter"),
    })
    @ApiResult
    public boolean exists(@RequestParam String path) {
        return netDiskService.exists(path);
    }
}
