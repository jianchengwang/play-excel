package cn.jianchengwang.tl.storage.adapter;

import cn.bespinglobal.amg.common.tool.E;
import cn.jianchengwang.tl.storage.pojo.ListObject;
import cn.jianchengwang.tl.storage.pojo.SObject;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;

public interface StorageAdapter {

    default String encodeFullPath(String fullPath) {
        try {
            return URLEncoder.encode(fullPath, "utf-8").replace("+", "%20");
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    default void encodeFullPath(List<String> fullPathList) {
       fullPathList.forEach(fullPath -> {
           fullPath = encodeFullPath(fullPath);
       });
    }

    Optional<SObject> doPut(String fullPath, InputStream inputStream);

    Optional<SObject> doGet(String fullPath);

    void doRemove(String fullPath);

    void doBatchRemove(List<String> fullPathList);

    /**
     *
     * @param prefix 文件前缀
     * @param limit 文件限制数量
     * @param marker 上次文件标识符，如果使用文件系统，则marker表示分页的页数，从0开始
     * @return 文件集合
     */
    Optional<ListObject> doList(String prefix, Integer limit, String marker);

}
