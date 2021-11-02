package com.hh.hhdb_admin.common.util.textEditor;

import com.hh.frame.dbobj2.kword.KeyWordUtil;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.textEditor.rSyntaxTextArea.ui.rsyntaxtextarea.RSyntaxTextArea;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.textEditor.tooltip.Tooltip;
import com.hh.hhdb_admin.mgr.quick_query.QuickQueryMgr;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
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
    public static String PACKAGE_KEYWORD = "package_keyword";
    //对象图标
    public static ImageIcon tableIcon = QuickQueryMgr.getIcon("table");
    public static ImageIcon viewIcon = QuickQueryMgr.getIcon("view");
    public static ImageIcon keyIcon = QuickQueryMgr.getIcon("keys");
    public static ImageIcon functionIcon = QuickQueryMgr.getIcon("function");
    public static ImageIcon synonymIcon = QuickQueryMgr.getIcon("list");
    public static ImageIcon packIcon = QuickQueryMgr.getIcon("packages");
    public static ImageIcon columnIcon = QuickQueryMgr.getIcon("column");
    public static ImageIcon sequenceIcon = QuickQueryMgr.getIcon("sequence");
    public static ImageIcon triggerIcon = QuickQueryMgr.getIcon("trigger");
    public static ImageIcon typeIcon = QuickQueryMgr.getIcon("type");
    

    /**
     * 创建查获器编辑器
     * @param bool  是否可以编辑
     * @return 编辑器
     * @throws Exception 异常
     */
    public static QueryEditorTextArea getQueryEditor(Boolean bool)throws Exception {
        //初始化编辑器
        QueryEditorTextArea textArea = new QueryEditorTextArea(bool);
        JsonObject fileJsonArr = Json.parse(FileUtils.readFileToString(StartUtil.defaultJsonFile, StandardCharsets.UTF_8)).asObject();
        String[] colors = fileJsonArr.get("textpane").asObject().asObject().get("background").asString().split(",");
        textArea.setBackground(new Color(Integer.parseInt(colors[0]), Integer.parseInt(colors[1]), Integer.parseInt(colors[2])));
        return textArea;
    }
    
    /**
     * 创建查获器编辑器提示工具
     * @param textArea  编辑器
     * @param type      编辑器类型：q(查询器编辑器),v(模板编辑器)
     * @return
     * @throws Exception
     */
    public static Tooltip getQueryTooltip(RSyntaxTextArea textArea, String type)throws Exception {
        //初始化提示框
        JsonObject fileJsonArr = Json.parse(FileUtils.readFileToString(StartUtil.defaultJsonFile, StandardCharsets.UTF_8)).asObject();
        JsonObject subObj=fileJsonArr.get("qkeys").asObject();
        String str = subObj.asObject().get(AUTOMATIC).asString();
        Tooltip tip = new Tooltip(textArea,type,Boolean.parseBoolean(StringUtils.isNotBlank(str) ? str.trim() : "true"));
        tip.setKeyPressed(subObj.asObject().get(KEYWORD).asString(),subObj.asObject().get(TABLE_KEYWORD).asString(),
                subObj.asObject().get(VIEW_KEYWORD).asString(),subObj.asObject().get(FUN_KEYWORD).asString(),subObj.asObject().get(SYNONYM_KEYWORD).asString()
                ,subObj.asObject().get(PACKAGE_KEYWORD).asString());
        return tip;
    }

    /**
     * 创建模板编辑器
     * @param bool  是否可以编辑
     * @return 板编辑器
     * @throws Exception 异常
     */
    public static QueryEditorTextArea getVMEditor(Boolean bool)throws Exception {
        //初始化编辑器
        QueryEditorTextArea textArea = new QueryEditorTextArea(bool);
        JsonObject fileJsonArr = Json.parse(FileUtils.readFileToString(StartUtil.defaultJsonFile, StandardCharsets.UTF_8)).asObject();
        JsonObject subObj=fileJsonArr.get("qkeys").asObject();
        textArea.hTextArea.showBookMask(false);
        textArea.setType("v");
        //初始化提示框
        Tooltip tip = new Tooltip(textArea.getTextArea(),textArea.type,false);
        tip.setkeyword(KeyWordUtil.getVMkeyWordsJson());
        tip.setKeyPressed(subObj.asObject().get(VMKEYWORD).asString());
        return textArea;
    }
}
