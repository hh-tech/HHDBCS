package com.hh.hhdb_admin.common.util.textEditor;

import com.hh.frame.dbobj2.kword.KeyWordUtil;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.hhdb_admin.common.util.StartUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.nio.charset.StandardCharsets;

/**
 * 查询器编辑面板工具类
 *
 * @author hexu
 */
public class QueryEditUtil {
    //是否自动开始提示
    public static String AUTOMATIC = "automatic";
    //编辑器提示快捷键标识
    public static String KEYWORD = "keyword";
    public static String TABLE_KEYWORD = "table_keyword";
    public static String VIEW_KEYWORD = "view_keyword";
    public static String FUN_KEYWORD = "fun_keyword";
    public static String VMKEYWORD = "vm_keyword";
    public static String SYNONYM_KEYWORD = "synonym_keyword";


    /**
     * 创建编辑器，并初始化提示快捷等
     * @param bool  是否可以编辑
     * @return 编辑器
     * @throws Exception 异常
     */
    public static QueryEditorTextArea getQueryEditor(Boolean bool)throws Exception {
        QueryEditorTextArea textArea = new QueryEditorTextArea(bool);
        //初始化编辑器
        JsonObject fileJsonArr = Json.parse(FileUtils.readFileToString(StartUtil.defaultJsonFile, StandardCharsets.UTF_8)).asObject();
        String[] colors = fileJsonArr.get("textpane").asObject().asObject().get("background").asString().split(",");
        textArea.setBackground(new Color(Integer.parseInt(colors[0]), Integer.parseInt(colors[1]), Integer.parseInt(colors[2])));
        JsonObject subObj=fileJsonArr.get("qkeys").asObject();
        textArea.setKeyPressed(subObj.asObject().get(KEYWORD).asString(),subObj.asObject().get(TABLE_KEYWORD).asString(),
                subObj.asObject().get(VIEW_KEYWORD).asString(),subObj.asObject().get(FUN_KEYWORD).asString(),subObj.asObject().get(SYNONYM_KEYWORD).asString());
        String str = subObj.asObject().get(AUTOMATIC).asString();
        textArea.setAutomatic(Boolean.parseBoolean(StringUtils.isNotBlank(str) ? str.trim() : "true"));
        return textArea;
    }

    /**
     * 创建模板编辑器，并初始化提示等
     * @param bool  是否可以编辑
     * @return 板编辑器
     * @throws Exception 异常
     */
    public static QueryEditorTextArea getVMEditor(Boolean bool)throws Exception {
        QueryEditorTextArea textArea = new QueryEditorTextArea(bool);
        JsonObject fileJsonArr = Json.parse(FileUtils.readFileToString(StartUtil.defaultJsonFile, StandardCharsets.UTF_8)).asObject();
        JsonObject subObj=fileJsonArr.get("qkeys").asObject();
        textArea.hTextArea.showBookMask(false);
        textArea.setType("v");
        textArea.setKeyPressed(subObj.asObject().get(VMKEYWORD).asString());
        textArea.setAutomatic(false);
        textArea.setkeyword(KeyWordUtil.getVMkeyWordsJson());
        return textArea;
    }
}
