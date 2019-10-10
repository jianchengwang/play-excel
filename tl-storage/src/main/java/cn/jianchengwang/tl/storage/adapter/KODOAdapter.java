package cn.jianchengwang.tl.storage.adapter;

import com.google.gson.Gson;
import cn.jianchengwang.tl.common.E;
import cn.jianchengwang.tl.common.S;
import cn.jianchengwang.tl.storage.autoconfigure.properties.KODOProperties;
import cn.jianchengwang.tl.storage.pojo.ListObject;
import cn.jianchengwang.tl.storage.pojo.SObject;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FileListing;
import com.qiniu.util.Auth;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Data
public class KODOAdapter implements StorageAdapter {

    private final KODOProperties CONFIG;
    private final String bucket;
    private final String domain;
    private final String permission;
    private final String delimiter;

    private final long expireInSeconds = 3600; //1小时，可以自定义链接过期时间
    private final String BUCKET_PRI = "private";
    private final String BUCKET_PUB = "public";

    private final Auth auth;
    private final Configuration cfg;
    private final BucketManager bucketManager;
    private final UploadManager uploadManager;

    public KODOAdapter(final KODOProperties config) {
        CONFIG = config;

        bucket = CONFIG.getBucket();
        domain = CONFIG.getDomain();
        permission = CONFIG.getPermission();
        delimiter = CONFIG.getDelimiter();

        cfg = new Configuration(Zone.autoZone());
        auth = Auth.create(CONFIG.getAccessKey(), CONFIG.getSecretKey());
        bucketManager = new BucketManager(auth, cfg);
        uploadManager = new UploadManager(cfg);
    }

    @Override
    public Optional<SObject> doPut(String fullPath, InputStream inputStream) {

        try {
            Response response = uploadManager.put(inputStream, fullPath, getUploadToken(),null, null);
            //解析上传成功的结果
            DefaultPutRet putObjectResult = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            SObject object = new SObject(); object.setKey(putObjectResult.key); object.setHash(putObjectResult.hash);
            return Optional.of(object);
        } catch (QiniuException ex) {
            throw E.unexpected(ex);
        }
    }

    @Override
    public Optional<SObject> doGet(@NonNull String fullPath) {
        try {
            encodeFullPath(fullPath);
            String downloadUrl = S.msgFmt("{0}/{1}", domain,  fullPath);
            if (isPrivate()) {
                downloadUrl = auth.privateDownloadUrl(downloadUrl);
            }
            BufferedInputStream inputStream = new BufferedInputStream(new URL(downloadUrl).openStream());
            SObject object = new SObject(fullPath, inputStream);
            return Optional.of(object);
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    @Override
    public void doRemove(String fullPath) {
        try {
            encodeFullPath(fullPath);
            bucketManager.delete(bucket, fullPath);
        } catch (QiniuException e) {
            throw E.unexpected(e);
        }
    }

    @Override
    public void doBatchRemove(List<String> fullPathList) {
        try {
            encodeFullPath(fullPathList);
            BucketManager.Batch batchOperations = new BucketManager.Batch();
            batchOperations.delete(bucket, fullPathList.toArray(new String[0]));
            bucketManager.batch(batchOperations);
        } catch (QiniuException e) {
            throw E.unexpected(e);
        }
    }

    @Override
    public Optional<ListObject> doList(String prefix, Integer limit, String marker) {
        FileListing fileListing;
        try {
            if(limit == null) {
                fileListing = bucketManager.listFiles(bucket, prefix, marker, 100, delimiter);
            } else {
                fileListing = bucketManager.listFiles(bucket, prefix, marker, limit, delimiter);
            }
            ListObject listObject = ListObject.builder()
                    .prefix(prefix).nextMarker(fileListing.marker).limit(limit).commonPrefixes(fileListing.commonPrefixes)
                    .objectList(Arrays.stream(fileListing.items).map(SObject::new).collect(Collectors.toList()))
                    .build();
            return Optional.of(listObject);
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }


    private String getUploadToken() {
        return auth.uploadToken(bucket);
    }

    private boolean isPrivate() {
        return BUCKET_PRI.equals(CONFIG.getPermission());
    }
}
