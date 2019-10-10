package cn.jianchengwang.tl.poi;

import cn.jianchengwang.tl.common.C;
import cn.jianchengwang.tl.poi.vo.Worker;
import cn.jianchengwang.tl.poi.word.DocxBuilder;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

/**
 * Created by wjc on 2019/8/30
 **/
public class DocxBuilderTest {

    @Test
    public void testSimpleTemplate() throws Exception {
        String templatePath = "C:\\uploads\\template\\工程师简历1.docx";
        FileInputStream fileInputStream = new FileInputStream(templatePath);

        String savePath = "C:\\uploads\\template\\工程师简历-Test1.docx";

        Worker worker = new Worker("王二狗", "3", "JAVA开发", "研发部", "一二三四五上山打老虎", "项目介绍：\n公司零售系统的子系统模块供应链系统，仓储相关，涉及要货，采购，调拨，销售，盘点，加工等功能模块\n技术栈简单的ssm，git版本管理，maven依赖管理，nginx实现负载\n部署在阿里云服务器。");
        Map<String, Object> mappings = C.object2Map(worker);

       DocxBuilder.create().template(templatePath)
               .putAll(mappings)
               .password("123456")
               .out(new File(savePath));
    }
}
