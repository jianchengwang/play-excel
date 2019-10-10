package cn.jianchengwang.tl.storage.autoconfigure.properties;

import cn.bespinglobal.amg.common.tool.E;
import cn.bespinglobal.amg.common.tool.S;
import cn.bespinglobal.amg.common.tool.V;
import lombok.Data;

/**
 * Created by wjc on 2019/8/23
 **/
@Data
public class KODOProperties {
    private String domain; // bucket域名, http:// or https://
    private String accessKey; // access key
    private String secretKey; // secret key
    private String bucket; // bucket name
    private String permission = "public"; // public or private
    private String delimiter = "/"; // 目录分割符，默认"/"

    public void validate() {
        if (S.isEmpty(bucket)) {
            E.invalidConfiguration("Kodo bucket not found in the configuration");
        }

        if (S.isEmpty(domain) || !V.isHttpUrl(domain)) {
            E.invalidConfiguration("Kodo domain not found in the configuration or not a http url");
        }

        final String BUCKET_PUB = "public";
        final String BUCKET_PRI = "private";
        if (!S.isEmpty(permission) && !(S.eq(permission, BUCKET_PUB) || S.eq(permission, BUCKET_PRI))) {
            E.invalidConfiguration("The permission fields need 'public' or 'private'");
        }
    }

}
