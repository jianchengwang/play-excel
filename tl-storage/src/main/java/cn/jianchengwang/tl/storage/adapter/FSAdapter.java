package cn.jianchengwang.tl.storage.adapter;

import cn.jianchengwang.tl.common.E;
import cn.jianchengwang.tl.common.FileTool;
import cn.jianchengwang.tl.common.S;
import cn.jianchengwang.tl.storage.autoconfigure.properties.FSProperties;
import cn.jianchengwang.tl.storage.pojo.ListObject;
import cn.jianchengwang.tl.storage.pojo.SObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by wjc on 2019/8/23
 **/

@Slf4j
@Data
public class FSAdapter implements StorageAdapter {

    private final FSProperties CONFIG;
    private final String rootPath;

    public FSAdapter(final FSProperties config) {
        CONFIG = config;
        rootPath = CONFIG.getRootPath();
    }

    @Override
    public Optional<SObject> doPut(String fullPath, InputStream inputStream) {
        try {
            Path path = getRootFullPath(fullPath);
            Path dir = Paths.get(path.toString().substring(0, path.toString().lastIndexOf(File.separator)));
            if(Files.notExists(dir)) {
                Files.createDirectories(dir);
            }
            if(Files.notExists(path)) {
                Files.createFile(path);
            }

            byte[] buff = FileTool.toByteArray(inputStream);
            Files.write(path, buff, StandardOpenOption.CREATE);
            SObject object = new SObject(); object.setKey(fullPath);
            return Optional.of(object);
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    @Override
    public Optional<SObject> doGet(String fullPath) {
        try {
            fullPath = fullPath.replace(File.separatorChar, '/');
            Path path = getRootFullPath(fullPath);
            byte[] bytes = Files.readAllBytes(path);
            InputStream inputStream = new ByteArrayInputStream(bytes);
            SObject object = new SObject(fullPath, inputStream);
            return Optional.of(object);
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    @Override
    public void doRemove(String fullPath) {
        try {
            Path path = getRootFullPath(fullPath);
            Files.deleteIfExists(path);
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    @Override
    public void doBatchRemove(List<String> fullPathList) {
        try {
            fullPathList.forEach(this::doRemove);
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    @Override
    public Optional<ListObject> doList(String prefix, Integer limit, String marker) {
        try {
            List<Path> pathList;
            Path path;
            if(S.isNotEmpty(prefix)) {
                path = getRootFullPath(prefix.split(File.pathSeparator));
            } else {
                path = getRootFullPath();
            }

            if(limit!=null && limit>0 && S.isNotEmpty(marker)) {
                int pageSize = Integer.parseInt(marker);
                pathList = Files.list(path).skip(pageSize*limit).limit(limit).collect(Collectors.toList());
            } else {
                pathList = Files.list(path).collect(Collectors.toList());
            }

            String[] commonPrefix = pathList.stream().filter(p -> Files.isDirectory(p)).map(
                    p-> p.toString()
                            .substring(p.toString().lastIndexOf(File.separator))
                            .replace(File.separator, "/"))
                    .toArray(String[]::new);
            List<Path> fileList = pathList.stream().filter(p -> Files.isRegularFile(p)).collect(Collectors.toList());
            ListObject listObject = ListObject.builder()
                    .prefix(prefix).nextMarker("").limit(limit).commonPrefixes(commonPrefix)
                    .objectList(fileList.stream().map(obj -> {
                        try {
                            return new SObject(obj, getFullPath(obj));
                        } catch (Exception e) {
                            throw E.unexpected(e);
                        }
                    }).collect(Collectors.toList()))
                    .build();
            return Optional.of(listObject);
        } catch (Exception e) {
            e.printStackTrace();
            throw E.unexpected(e);
        }
    }

    private Path getRootFullPath(String... fullPath) {
        return Paths.get(rootPath, fullPath);
    }

    private String getFullPath(Path path) {
        String fullPath = path.toString().replace(rootPath, "");
        if(fullPath.startsWith(File.separator)) {
            fullPath = fullPath.substring(File.separator.length());
        }
        return fullPath.replace(File.separator, "/");
    }
}
