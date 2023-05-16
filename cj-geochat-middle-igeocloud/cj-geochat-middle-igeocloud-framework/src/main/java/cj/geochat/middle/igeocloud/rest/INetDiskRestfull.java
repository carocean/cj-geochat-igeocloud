package cj.geochat.middle.igeocloud.rest;

import cj.geochat.ability.util.GeochatException;
import cj.geochat.util.minio.MinioQuotaUnit;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface INetDiskRestfull {
    public void createDisk(String diskName, long size, MinioQuotaUnit unit);
    void setDiskQuota(String diskName, long size, MinioQuotaUnit unit);
    void clearDiskQuota(String diskName);

    public String queryDiskPolicy(String diskName);

    public long getDiskQuota(String diskName);

    public Map<String, Object> getDataUsageInfo();

    void mkdir(String path);

    List<String> listChildren(String path);

    List<String> listChildrenByRecursive(String path, boolean recursive);

    void upload(MultipartFile file, String path);

    //浏览器请求文件
    void igeocloud(String file, HttpServletRequest request, HttpServletResponse response) throws GeochatException, IOException;

    void download(String path, HttpServletResponse response) throws GeochatException;

    void seekDownload(String path, long offset, long length, HttpServletResponse response) throws GeochatException;

    void delete(String path);

    void empty(String path);

    String accessUrl(String path, int expirySeconds);

    boolean exists(String path);
}
