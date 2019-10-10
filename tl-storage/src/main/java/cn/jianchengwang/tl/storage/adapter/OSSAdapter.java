package cn.jianchengwang.tl.storage.adapter;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import cn.jianchengwang.tl.common.E;
import cn.jianchengwang.tl.storage.autoconfigure.properties.OSSProperties;
import cn.jianchengwang.tl.storage.pojo.ListObject;
import cn.jianchengwang.tl.storage.pojo.SObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Data
public class OSSAdapter implements StorageAdapter {

    private final OSSProperties CONFIG;
    private final String bucketName;
    private final String delimiter;
    private final OSS client;

    public OSSAdapter(final OSSProperties config) {
        CONFIG = config;
        bucketName = CONFIG.getBucketName();
        delimiter = CONFIG.getDelimiter();
        client = new OSSClientBuilder().build(CONFIG.getEndpoint(), CONFIG.getAccessKeyId(), CONFIG.getAccessKeySecret());
    }

    @Override
    public Optional<SObject> doPut(String fullPath, InputStream inputStream) {
        try {
            client.putObject(bucketName, fullPath, inputStream);
            SObject object = new SObject(); object.setKey(fullPath);
            return Optional.of(object);
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    @Override
    public Optional<SObject> doGet(String fullPath) {
        try {
            OSSObject ossObject = client.getObject(bucketName, fullPath);
            SObject object = new SObject(fullPath, ossObject.getObjectContent());
            return Optional.of(object);
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    @Override
    public void doRemove(String fullPath) {
        try {
            encodeFullPath(fullPath);
            client.deleteObject(bucketName, fullPath);
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    @Override
    public void doBatchRemove(List<String> fullPathList) {
        try {
            client.deleteObjects(new DeleteObjectsRequest(bucketName).withKeys(fullPathList));
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    @Override
    public Optional<ListObject> doList(String prefix, Integer limit, String marker) {
        ObjectListing objectListing;
        try {
            if(limit == null) {
                objectListing =  client.listObjects(new ListObjectsRequest(bucketName).withPrefix(prefix).withMarker(marker).withDelimiter("/"));
            } else {
                objectListing = client.listObjects(new ListObjectsRequest(bucketName).withPrefix(prefix).withMarker(marker).withMaxKeys(limit).withDelimiter("/"));
            }
            ListObject listObject = ListObject.builder()
                    .prefix(prefix).nextMarker(objectListing.getNextMarker()).limit(objectListing.getMaxKeys()).commonPrefixes(objectListing.getCommonPrefixes().toArray(new String[0]))
                    .objectList(objectListing.getObjectSummaries().stream().map(SObject::new).collect(Collectors.toList()))
                    .build();
            return Optional.of(listObject);
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }
}
