package cj.geochat.app.igeocloud.disk.web;

import cj.geochat.ability.minio.INetDiskService;
import cj.geochat.util.minio.MinioQuotaUnit;

public class AbstractIGeocloudRestfull {
    protected void checkDisk(INetDiskService netDiskService, String rootPath) {
        String diskName=rootPath.substring(rootPath.indexOf("://")+3,rootPath.length());
        while (diskName.endsWith("/")) {
            diskName = diskName.substring(0, diskName.length() - 1);
        }
        if (netDiskService.existsDisk(diskName)) {
            return;
        }
        netDiskService.createDisk(diskName, 5, MinioQuotaUnit.GB);
        String publicDir = String.format("%s/public/", rootPath);
        if (!netDiskService.exists(publicDir)) {
            netDiskService.mkdir(publicDir);
        }
        String documents = String.format("%s/documents/", rootPath);
        if (!netDiskService.exists(documents)) {
            netDiskService.mkdir(documents);
        }
        String library = String.format("%s/library/", rootPath);
        if (!netDiskService.exists(library)) {
            netDiskService.mkdir(library);
        }
        String pictures = String.format("%s/pictures/", rootPath);
        if (!netDiskService.exists(pictures)) {
            netDiskService.mkdir(pictures);
        }
        String movies = String.format("%s/movies/", rootPath);
        if (!netDiskService.exists(movies)) {
            netDiskService.mkdir(movies);
        }
        String music = String.format("%s/music/", rootPath);
        if (!netDiskService.exists(music)) {
            netDiskService.mkdir(music);
        }
    }
}
