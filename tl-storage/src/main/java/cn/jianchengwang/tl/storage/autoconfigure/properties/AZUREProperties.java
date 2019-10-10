package cn.jianchengwang.tl.storage.autoconfigure.properties;

import lombok.Data;

/**
 * Created by wjc on 2019/8/27
 **/
@Data
public class AZUREProperties {
    private String protocol;
    private String accountName;
    private String accountKey;
    private String bucket;

    public void validate() {

    }
}
