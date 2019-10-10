package cn.jianchengwang.tl.storage.adapter;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import cn.jianchengwang.tl.storage.autoconfigure.properties.S3Properties;
import cn.jianchengwang.tl.storage.pojo.ListObject;
import cn.jianchengwang.tl.storage.pojo.SObject;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by wjc on 2019/8/26
 **/
public class S3Adapter implements StorageAdapter {

    private final S3Properties CONFIG;

    private final String awsKeyId;
    private final String awsKeySecret;
    private final String bucket;
    private final String delimiter;

    private static AmazonS3 s3;

    public S3Adapter(final S3Properties config) {
        CONFIG = config;
        awsKeyId = CONFIG.getAwsKeyId();
        awsKeySecret = CONFIG.getAwsKeySecret();
        bucket = CONFIG.getBucket();
        delimiter = CONFIG.getDelimiter();

        AWSCredentials cred = new BasicAWSCredentials(awsKeyId, awsKeySecret);
        ClientConfiguration cc = new ClientConfiguration();
        if (CONFIG.getMaxErrorRetry() != null) {
            cc = cc.withMaxErrorRetry(CONFIG.getMaxErrorRetry());
        }
        if (CONFIG.getConnectionTimeout() != null) {
            cc = cc.withConnectionTimeout(CONFIG.getConnectionTimeout());
        }
        if (CONFIG.getMaxConnection() != null) {
            cc = cc.withMaxConnections(CONFIG.getMaxConnection());
        }
        if (CONFIG.getTcpKeepAlive() != null) {
            cc = cc.withTcpKeepAlive(CONFIG.getTcpKeepAlive());
        }
        if (CONFIG.getSocketTimeout() != null) {
            cc = cc.withSocketTimeout(CONFIG.getSocketTimeout());
        }
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(cred);
        AmazonS3ClientBuilder clientBuilder = AmazonS3ClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withClientConfiguration(cc);

        s3 = clientBuilder.build();
    }

    @Override
    public Optional<SObject> doPut(String fullPath, InputStream inputStream) {
        PutObjectRequest req = new PutObjectRequest(bucket, fullPath, inputStream, new ObjectMetadata());
        req.withCannedAcl(CannedAccessControlList.PublicRead);
        PutObjectResult putObjectResult = s3.putObject(req);
        SObject object = new SObject(); object.setKey(fullPath); object.setHash(putObjectResult.getContentMd5());
        return Optional.of(object);
    }

    @Override
    public Optional<SObject> doGet(String fullPath) {
        GetObjectRequest req = new GetObjectRequest(bucket, fullPath);
        S3Object s3obj = s3.getObject(req);
        SObject object = new SObject(fullPath, s3obj.getObjectContent());
        return Optional.of(object);
    }

    @Override
    public void doRemove(String fullPath) {
        s3.deleteObject(new DeleteObjectRequest(bucket, fullPath));
    }

    @Override
    public void doBatchRemove(List<String> fullPathList) {
        DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(bucket)
                .withKeys(fullPathList.toArray(new String[0]))
                .withQuiet(false);
        s3.deleteObjects(multiObjectDeleteRequest);
    }

    @Override
    public Optional<ListObject> doList(String prefix, Integer limit, String marker) {
        VersionListing result = s3.listVersions(bucket, prefix, marker, null, delimiter, limit);
        ListObject listObject = ListObject.builder()
                .prefix(prefix).nextMarker(result.getNextKeyMarker()).limit(result.getMaxKeys()).commonPrefixes(result.getCommonPrefixes().stream().toArray(String[]::new))
                .objectList(result.getVersionSummaries().stream().map(SObject::new).collect(Collectors.toList()))
                .build();
        return Optional.of(listObject);
    }
}
