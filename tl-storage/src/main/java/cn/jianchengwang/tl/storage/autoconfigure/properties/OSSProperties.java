package cn.jianchengwang.tl.storage.autoconfigure.properties;

import lombok.Data;

/**
 * Created by wjc on 2019/8/23
 **/
@Data
public class OSSProperties {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String delimiter = "/"; // 目录分割符，默认"/"

    public void validate() {

    }
}
