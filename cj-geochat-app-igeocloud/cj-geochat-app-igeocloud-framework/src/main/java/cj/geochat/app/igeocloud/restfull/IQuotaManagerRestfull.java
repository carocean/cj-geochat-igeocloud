package cj.geochat.app.igeocloud.restfull;

import cj.geochat.util.minio.MinioQuotaUnit;

public interface IQuotaManagerRestfull {
    void setSystemDiskQuota(long size, MinioQuotaUnit unit);

    void clearSystemDiskQuota();

    void setPublicDiskQuota(long size, MinioQuotaUnit unit);

    void clearPublicDiskQuota();

    void setUserDiskQuota(String userName, long size, MinioQuotaUnit unit);

    void clearUserDiskQuota(String userName);
}
