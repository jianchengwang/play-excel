package cn.jianchengwang.tl.storage.pojo;

import cn.jianchengwang.tl.common.S;
import com.aliyun.oss.model.OSSObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.microsoft.azure.storage.blob.ListBlobItem;
import com.qiniu.storage.model.FileInfo;
import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by wjc on 2019/8/27
 **/
@Data
@Builder
public class SObject {
    private String name; // 文件名
    private String key; // 文件key or fullPath
    private String hash; // hash
    private Long size; // 大小
    private Long lastModified; // 最后一次修改时间

    private String mimeType; // mime类型
    private InputStream inputStream; // 文件流

    public SObject() {

    }

    public SObject(String name, String key, String hash, Long size, Long lastModified, String mimeType, InputStream inputStream) {
        this.name = name;
        this.key = key;
        this.hash = hash;
        this.size = size;
        this.lastModified = lastModified;
        this.mimeType = mimeType;
        this.inputStream = inputStream;
    }

    public SObject(String key, String hash, Long size, Long lastModified, String mineType) {
        this.setKey(key);
        this.setHash(hash);
        this.setSize(size);
        this.setLastModified(lastModified);
        this.setMimeType(mineType);
    }

    public SObject(FileInfo kodoObj) {
        this(kodoObj.key, kodoObj.hash, kodoObj.fsize, kodoObj.putTime, kodoObj.mimeType);
    }

    public SObject(OSSObjectSummary ossObj) {
        this(ossObj.getKey(), ossObj.getETag(), ossObj.getSize(), ossObj.getLastModified().getTime(), null);
    }

    public SObject(S3VersionSummary s3Obj) {
        this(s3Obj.getKey(), s3Obj.getETag(), s3Obj.getSize(), s3Obj.getLastModified().getTime(), null);
    }

    public SObject(ListBlobItem azureObj) {
        this(azureObj.getUri().getPath(), null, 0L, null, null);
    }

    public SObject(Path fsObj, String fullPath) throws IOException {
        this(fullPath!=null?fullPath:fsObj.toString(), String.valueOf(fsObj.hashCode()),  Files.size(fsObj), Files.getLastModifiedTime(fsObj).toMillis(), null);
    }

    public SObject(String key, InputStream inputStream) {
        this.setKey(key);
        this.setInputStream(inputStream);
    }

    // 设置key的时候自动设置文件名
    public void setKey(String key) {
        this.key = key;

        String name = key;
        if(key.contains(File.pathSeparator)) {
            name = key.substring(key.lastIndexOf(File.separator) + 1);
        } else {
            name = key.substring(key.lastIndexOf("/") + 1);
        }
        this.setName(name);

        if(S.isEmpty(this.mimeType)) {
            String suffix = name.substring(name.lastIndexOf(".") + 1);
            if(S.isNotEmpty(suffix)) {
                this.setMimeType(mimeType);
            }
        }

    }
}
