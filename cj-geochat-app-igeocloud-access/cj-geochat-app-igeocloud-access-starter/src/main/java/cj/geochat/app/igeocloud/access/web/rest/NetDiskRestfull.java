package cj.geochat.app.igeocloud.access.web.rest;

import cj.geochat.ability.api.annotation.ApiResult;
import cj.geochat.ability.api.exception.ApiException;
import cj.geochat.ability.minio.INetDiskService;
import cj.geochat.ability.oauth2.app.DefaultAppAuthentication;
import cj.geochat.ability.oauth2.app.DefaultAppPrincipal;
import cj.geochat.ability.oauth2.userdetails.GeochatUser;
import cj.geochat.ability.util.GeochatException;
import cj.geochat.ability.util.GeochatRuntimeException;
import cj.geochat.app.igeocloud.access.rest.INetDiskRestfull;
import cj.geochat.util.minio.FilePath;
import io.minio.StatObjectResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/v1/igeocloud")
@Api(tags = {"云盘管理"})
@RefreshScope
@Slf4j
public class NetDiskRestfull implements INetDiskRestfull {
    @Autowired
    INetDiskService netDiskService;


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
        checkRights(file);

        String dir = file.substring(0, dirEndPos);
        String fn = file.substring(dirEndPos + "-+-".length());
        file = "igeocloud://" + dir.replace("-", "/") + "/" + fn;
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

    //file=18023457655-pictures-+-d2af59aa6bf606a239b89f13fd9aff13.jpg
    private void checkRights(String file) {
        int pos = file.indexOf("-");
        if (pos < 0) {
            throw new GeochatRuntimeException("500", "Incorrect address format");
        }
        String diskname = file.substring(0, pos);
        if ("public".equals(diskname)) {
            return;
        }
        if ("system".equals(diskname)) {
            return;
        }
        DefaultAppAuthentication authentication = (DefaultAppAuthentication) SecurityContextHolder.getContext().getAuthentication();
        DefaultAppPrincipal principal = (DefaultAppPrincipal) authentication.getPrincipal();
        //下面是用户
        String remaing = file.substring(pos + 1);
        if (remaing.startsWith("+-")) {
            //说明是文件在根目录，只充许用户自己查看
            if (diskname.equals(principal.getUserid())) {
                return;
            }
            throw new GeochatRuntimeException("801", "Refuse viewing.");
        }
        pos = remaing.indexOf("-");
        if (pos < 0) {
            throw new GeochatRuntimeException("500", "Incorrect address format");
        }
        String firstDir = remaing.substring(0, pos);
        //用户的公共目录任何人都可查看
        if ("public".equals(firstDir)) {
            return;
        }
        //其它目录则只充许自己查看
        if (diskname.equals(principal.getUserid())) {
            return;
        }
        throw new GeochatRuntimeException("801", "Refuse viewing.");
    }

}
