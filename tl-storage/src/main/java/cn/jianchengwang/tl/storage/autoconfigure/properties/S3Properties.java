package cn.jianchengwang.tl.storage.autoconfigure.properties;

import lombok.Data;

/**
 * Created by wjc on 2019/8/26
 **/
@Data
public class S3Properties {
    private String awsKeyId;
    private String awsKeySecret;
    private String bucket;
    private String delimiter = "/"; // 目录分割符，默认"/"

    private Integer maxErrorRetry;
    private Integer connectionTimeout;
    private Integer maxConnection;
    private Boolean tcpKeepAlive;
    private Integer socketTimeout;

    public void validate() {

    }
}
