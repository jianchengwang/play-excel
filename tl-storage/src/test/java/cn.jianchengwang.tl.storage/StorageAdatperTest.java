package cn.jianchengwang.tl.storage;

import cn.jianchengwang.tl.storage.adapter.StorageAdapter;
import cn.jianchengwang.tl.storage.pojo.ListObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

/**
 * Created by wjc on 2019/8/29
 **/
@SpringBootTest(classes={Application.class})// 指定启动类
public class StorageAdatperTest {

    @Autowired
    StorageAdapter storageAdapter;

    @Test
    public void doList() {
        Optional<ListObject> listObjectOptional = storageAdapter.doList("", 100, null);
        listObjectOptional.get().getObjectList().forEach(sObject -> {
            System.out.println(sObject.getKey());
        });
    }
}
