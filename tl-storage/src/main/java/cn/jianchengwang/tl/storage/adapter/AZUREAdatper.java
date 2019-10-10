package cn.jianchengwang.tl.storage.adapter;

import cn.jianchengwang.tl.common.E;
import cn.jianchengwang.tl.common.exception.ConfigurationException;
import cn.jianchengwang.tl.storage.autoconfigure.properties.AZUREProperties;
import cn.jianchengwang.tl.storage.pojo.ListObject;
import cn.jianchengwang.tl.storage.pojo.SObject;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.ResultSegment;
import com.microsoft.azure.storage.blob.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by wjc on 2019/8/27
 **/
@Slf4j
@Data
public class AZUREAdatper implements StorageAdapter {

    private final AZUREProperties CONFIG;
    private String accountName;
    private String bucket;
    private CloudBlobClient blobClient;
    private CloudBlobContainer blobContainer;

    private final static String CONNECTION_PATTERN = "DefaultEndpointsProtocol={0};AccountName={1};AccountKey={2};";
    private final static String URL_PATTERN = "http://{0}.blob.core.windows.net/{1}/{2}";

    public AZUREAdatper(final AZUREProperties config) {
        CONFIG = config;

        String protocol = CONFIG.getProtocol();
        String accountKey = CONFIG.getAccountKey();
        accountName = CONFIG.getAccountName();
        connect(protocol, accountName, accountKey, bucket);
    }

    /**
     * Create a connection to Azure Blob file storage system
     */
    private void connect(String protocol, String accountName, String accountKey, String bucketName) {
        if (bucketName == null || bucketName.trim().isEmpty())
            throw new ConfigurationException("Defined Azure Blog bucket is invalid.");
        //container name MUST be lowercase
        bucketName = bucketName.toLowerCase();

        String connectionString = MessageFormat.format(CONNECTION_PATTERN, protocol, accountName, accountKey);
        try {
            CloudStorageAccount blobAccount = CloudStorageAccount.parse(connectionString);

            this.blobClient = blobAccount.createCloudBlobClient();
            this.blobContainer = blobClient.getContainerReference(bucketName);

            boolean isBucketNotExist = blobContainer.createIfNotExists();
            if (isBucketNotExist)
                log.info("New Azure Blob container created: " + bucketName);

            //Set access to public for blob resource
            BlobContainerPermissions containerPermissions = new BlobContainerPermissions();
            containerPermissions.setPublicAccess(BlobContainerPublicAccessType.CONTAINER);
            blobContainer.uploadPermissions(containerPermissions);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            throw new ConfigurationException(exception);
        }
    }

    @Override
    public Optional<SObject> doPut(String fullPath, InputStream inputStream) {
        try {
            CloudBlockBlob blob = blobContainer.getBlockBlobReference(fullPath);
            blob.upload(inputStream, -1);
            blob.uploadProperties();
            SObject object = new SObject(); object.setKey(fullPath);
            return Optional.of(object);
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    @Override
    public Optional<SObject> doGet(String fullPath) {
        try {
            CloudBlockBlob blob = blobContainer.getBlockBlobReference(fullPath);
            SObject object = new SObject(fullPath, blob.openInputStream());
            return Optional.of(object);
        } catch (Exception exception) {
            throw E.unexpected(exception);
        }
    }

    @Override
    public void doRemove(String fullPath) {
        try {
            CloudBlockBlob blob = blobContainer.getBlockBlobReference(fullPath);
            blob.deleteIfExists();
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    @Override
    public void doBatchRemove(List<String> fullPathList) {
        fullPathList.forEach(this::doRemove);
    }

    @Override
    public Optional<ListObject> doList(String prefix, Integer limit, String marker) {
        try {
            ResultSegment<ListBlobItem> result = blobContainer.listBlobsSegmented(prefix, false, null, limit, null, null, null);
            ListObject listObject = ListObject.builder()
                    .prefix(prefix).nextMarker("").limit(result.getPageSize()).commonPrefixes(null)
                    .objectList(result.getResults().stream().map(SObject::new).collect(Collectors.toList()))
                    .build();
            return Optional.of(listObject);
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }
}
