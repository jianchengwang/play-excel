package cn.jianchengwang.tl.storage.autoconfigure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wjc on 2019/8/23
 **/
@Data
@ConfigurationProperties("storage")
@PropertySource(value = {"classpath:application-${spring.profiles.active}.yml"}, encoding = "UTF-8")
public class StorageProperties {

    private boolean enable = true; // 是否启用
    private StoreType storeType = StoreType.FS; // 存储类型，目前支持fs,oss,kodo,s3,azure...
    private Map<String, String> moduleDir = new HashMap<>(); // 模块对应目录

    @NestedConfigurationProperty
    private FSProperties fs;
    @NestedConfigurationProperty
    private OSSProperties oss;
    @NestedConfigurationProperty
    private KODOProperties kodo;
    @NestedConfigurationProperty
    private S3Properties s3;
    @NestedConfigurationProperty
    private AZUREProperties azure;
}
