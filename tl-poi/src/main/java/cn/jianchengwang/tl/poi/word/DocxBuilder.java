package cn.jianchengwang.tl.poi.word;

import cn.jianchengwang.tl.common.E;
import cn.jianchengwang.tl.common.S;
import cn.jianchengwang.tl.poi.common.ResponseWrapper;
import cn.jianchengwang.tl.poi.word.enums.WordType;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.Docx4J;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.ProtectDocument;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by wjc on 2019/9/2
 **/
@Slf4j
public class DocxBuilder {
    private final String PDF_STUFF = ".pdf";
    private final org.docx4j.wml.ObjectFactory factory;

    private WordType wordType; // 文件类型
    private Map<String, Object> context; // 参数map
    private InputStream template; // 模板文件输入流
    private OutputStream out; // 输出流
    private String saveFileName; // 保存文件名字
    private String password; // 密码
    private boolean convert2PDF; // 转换成pdf

    public DocxBuilder(WordType wordType) {
        this.wordType = wordType!=null?wordType:WordType.DOCX;
        factory = Context.getWmlObjectFactory();
    }

    public static DocxBuilder create() {
        return new DocxBuilder(WordType.DOCX);
    }

    public static DocxBuilder create(WordType wordType) {
        return new DocxBuilder(wordType);
    }

    public DocxBuilder template(InputStream in) {
        this.template = in;
        this.context = new HashMap<>();
        return this;
    }

    public DocxBuilder template(String fullPath) {
        return template(new File(fullPath));
    }

    public DocxBuilder template(File templateFile) {
        try {
            return this.template(new FileInputStream(templateFile));
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    public void out(File outFile) {
        try {
            saveFileName(outFile.getName());
            if(!this.saveFileName.equalsIgnoreCase(outFile.getName())) {
                outFile = new File(outFile.getParent() + File.separator + this.saveFileName);
            }
            if(!outFile.exists()) {
                outFile.createNewFile();
            }
            this.out(new FileOutputStream(outFile));
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    public void out(OutputStream out) {
        this.out = out;
        build();
    }

    public void out(HttpServletResponse servletResponse, String fileName) {
        saveFileName(fileName);
        this.out(ResponseWrapper.create(servletResponse, this.saveFileName));
    }

    public DocxBuilder saveFileName(String fileName) {
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(S.isNotEmpty(suffix)) {
            if(this.convert2PDF) {
                suffix = PDF_STUFF;
            } else {
                suffix = this.wordType.getValue();
            }
            fileName = fileName.substring(0, fileName.lastIndexOf(".")) + suffix;
        }
        this.saveFileName = fileName;
        return this;
    }


    public DocxBuilder putVar(String name, Object value) {

        if(value !=null) {
            if(value instanceof Collection && !((Collection) value).isEmpty()) {
                context.put(name, value);
            } else {
                context.put(name, value);
            }
        }
        return this;
    }
    public DocxBuilder putAll(Map<String, Object> map) {
        for (String key : map.keySet()) {
            putVar(key, map.get(key));
        }
        return this;
    }
    public DocxBuilder removeVar(String name) {
        context.remove(name);
        return this;
    }
    public Object getVar(String name) {
        return context.get(name);
    }

    public DocxBuilder password(String password) {
        this.password = password;
        return this;
    }

    public DocxBuilder convert2PDF(boolean convert2PDF) {
        this.convert2PDF = convert2PDF;
        return this;
    }

    private void build() {
        try {
            WordprocessingMLPackage wordMLPackage = createWordprocessingMLPackageFromTemplate();
            encrypt(wordMLPackage);
            if(this.convert2PDF) {
                Docx4J.toPDF(wordMLPackage, this.out);
            } else {
                Docx4J.save(wordMLPackage, this.out, Docx4J.FLAG_SAVE_ZIP_FILE);
            }
        } catch (Exception e) {

            throw E.unexpected(e);
        }

    }


    /**
     * 创建Docx的主方法
     *
     * @return
     */
    private WordprocessingMLPackage createWordprocessingMLPackageFromTemplate()
            throws Exception {
        @Cleanup InputStream docxStream = this.template;
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(docxStream);
        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

        // replaceParameters
        if (context != null) {
            Map<String, String> parameters = convert2Parameters(context);
            replaceParameters(documentPart, parameters);
        }
        return wordMLPackage;
    }

    /**
     * 转换成符合api接口参数
   * @param map
     * @return
     */
    private Map<String, String> convert2Parameters(Map<String, Object> map) {
        return map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> newlineToBreakHack((String)e.getValue())));
    }

    /**
     * Hack to convert a new line character into w:br.
     * If you need this sort of thing, consider using
     * OpenDoPE content control data binding instead.
     *
     * @param r
     * @return
     */
    private String newlineToBreakHack(String r) {

        StringTokenizer st = new StringTokenizer(r, "\n\r\f"); // tokenize on the newline character, the carriage-return character, and the form-feed character
        StringBuilder sb = new StringBuilder();

        boolean firsttoken = true;
        while (st.hasMoreTokens()) {
            String line = (String) st.nextToken();
            if (firsttoken) {
                firsttoken = false;
            } else {
                sb.append("</w:t><w:br/><w:t>");
            }
            sb.append(line);
        }
        return sb.toString();
    }

    /**
     * 替换模板中的参数
     *
     * @param documentPart
     * @param parameters
     * @throws JAXBException
     * @throws Docx4JException
     */
    private void replaceParameters(MainDocumentPart documentPart,
                                          Map<String, String> parameters)
            throws JAXBException, Docx4JException {
        documentPart.variableReplace(parameters);
    }

    private void encrypt(WordprocessingMLPackage wordMLPackage) {
        if(S.isNotEmpty(this.password)) {
            //加密
            ProtectDocument protection = new ProtectDocument(wordMLPackage);
            protection.restrictEditing(STDocProtect.READ_ONLY, this.password);
        }
    }

}
