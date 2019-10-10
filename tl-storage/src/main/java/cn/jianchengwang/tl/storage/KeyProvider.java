package cn.jianchengwang.tl.storage;

import cn.bespinglobal.amg.common.tool.S;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by wjc on 2019/8/28
 **/
public interface KeyProvider {

    /**
     * Generate key name using random UUID
     */

    KeyProvider DEF_PROVIDER = new KeyProvider() {

        @Override
        public String newKeyName(String fileName) {
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            return UUID.randomUUID().toString() + "." + suffix;
        }

        @Override
        public String newPrefixName(String module, String prefix) {

            // 默认module作为资源的顶级目录
            String prefix_new = module;
            if(S.isNotEmpty(prefix)) {
                prefix_new += "/" + prefix;
            }
            return qualifyPrefixName(prefix_new);
        }
    };

    /**
     * qualify prefix, remove blank and so on
     * @param prefix 前缀
     * @return 处理前缀后的前缀字符串
     */
    default String qualifyPrefixName(String prefix) {
        prefix = Arrays.stream(prefix.split("/")).filter(S::isNotBlank).collect(Collectors.joining("/"));
        prefix = prefix.replaceAll("//", "/");
        return prefix;
    }

    /**
     * Returns a unique key name.
     * of the {@link KeyGenerator}
     * @param fileName 文件名 可以对文件名进行处理，hash等算法得到key name
     * @return the key name
     */
    String newKeyName(String fileName);

    /**
     * return a key prefix like user/0001/hello/
     * @param module 模块名
     * @param prefix 前缀
     * @return 新的前缀
     */
    String newPrefixName(String module, String prefix);
}
