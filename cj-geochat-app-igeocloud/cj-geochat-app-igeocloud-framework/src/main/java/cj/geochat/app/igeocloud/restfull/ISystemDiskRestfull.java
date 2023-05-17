package cj.geochat.app.igeocloud.restfull;

import cj.geochat.ability.util.GeochatException;
import cj.geochat.util.minio.MinioQuotaUnit;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ISystemDiskRestfull {

    public long getDiskQuota();

    public Map<String, Object> getDataUsageInfo();

    void mkdir(String path);

    List<String> listChildren(String path);

    List<String> listChildrenByRecursive(String path, boolean recursive);

    void upload(MultipartFile file, String path);


    void download(String path, HttpServletResponse response) throws GeochatException;

    void seekDownload(String path, long offset, long length, HttpServletResponse response) throws GeochatException;

    void delete(String path);

    void empty(String path);

    String accessUrl(String path, HttpServletRequest request);

    boolean exists(String path);
}
