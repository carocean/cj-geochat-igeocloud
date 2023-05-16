package cj.geochat.middle.igeocloud.service;

import cj.geochat.ability.minio.INetDiskService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InitIGeocloudService implements InitializingBean {
    @Autowired
    INetDiskService netDiskService;

    @Override
    public void afterPropertiesSet() throws Exception {
        checkSystemDisk();
        checkPublicDisk();
    }

    private void checkSystemDisk() {
        if (!netDiskService.existsDisk("system")) {
            netDiskService.createDisk("system", 0, null);
        }
        initDirs("igeocloud://system");
    }

    private void checkPublicDisk() {
        if (!netDiskService.existsDisk("public")) {
            netDiskService.createDisk("public", 0, null);
        }
        initDirs("igeocloud://public");
    }

    private void initDirs(String root) {
        String publicDir = String.format("%s/public/", root);
        if (!netDiskService.exists(publicDir)) {
            netDiskService.mkdir(publicDir);
        }
        String documents = String.format("%s/documents/", root);
        if (!netDiskService.exists(documents)) {
            netDiskService.mkdir(documents);
        }
        String library = String.format("%s/library/", root);
        if (!netDiskService.exists(library)) {
            netDiskService.mkdir(library);
        }
        String pictures = String.format("%s/pictures/", root);
        if (!netDiskService.exists(pictures)) {
            netDiskService.mkdir(pictures);
        }
        String movies = String.format("%s/movies/", root);
        if (!netDiskService.exists(movies)) {
            netDiskService.mkdir(movies);
        }
        String music = String.format("%s/music/", root);
        if (!netDiskService.exists(music)) {
            netDiskService.mkdir(music);
        }
    }

}
