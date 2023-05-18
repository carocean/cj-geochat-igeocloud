package cj.geochat.app.igeocloud.access.rest;

import cj.geochat.ability.util.GeochatException;
import cj.geochat.util.minio.MinioQuotaUnit;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface INetDiskRestfull {


    //浏览器请求文件
    void igeocloud(String file, HttpServletRequest request, HttpServletResponse response) throws GeochatException, IOException;


}
