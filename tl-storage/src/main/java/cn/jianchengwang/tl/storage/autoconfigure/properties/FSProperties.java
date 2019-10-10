package cn.jianchengwang.tl.storage.autoconfigure.properties;

import cn.jianchengwang.tl.common.E;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by wjc on 2019/8/23
 **/
@Data
public class FSProperties {
    private String rootPath = "/uploads";

    public void validate() {

        Path path = Paths.get(rootPath);

        if (StringUtils.isEmpty(rootPath)
                || Files.notExists(path, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})
                || !Files.isDirectory(path, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})
                || !Files.isExecutable(path)
                || !Files.isReadable(path)
                || !Files.isWritable(path)
        ) {
            throw E.invalidConfiguration("Cannot create file adapter, because rootPath not found or rootPath is not directory or cant access, read or write");
        }

    }
}
