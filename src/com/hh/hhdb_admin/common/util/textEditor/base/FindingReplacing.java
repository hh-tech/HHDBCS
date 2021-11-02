package com.hh.hhdb_admin.common.util.textEditor.base;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonArray;
import com.hh.frame.json.JsonObject;
import com.hh.frame.json.JsonValue;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.*;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.textEditor.rSyntaxTextArea.ui.rtextarea.*;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 查找替换对象
 */
public class FindingReplacing {
    public HDialog dlog;
    private JTextArea textArea;
    private HPanel panels = new HPanel(new HDivLayout(GridSplitEnum.C8));
    
    private SelectBox findBox = new SelectBox("find");
    private TextInput replacedata = new TextInput();
	private CheckGroupInput cInput = new CheckGroupInput("type");
	private LabelInput sumLabel = new LabelInput();
    
    private SearchContext context;
    
    private JsonObject fileJsonArr;
    
    /**
     * 查找/替换面板
     * @param qed
     */
    public FindingReplacing(JTextArea textArea) {
        this.textArea = textArea;
        sumLabel.setColor(new Color(0,0, 255));
        sumLabel.setAlign(AlignEnum.LEFT);
        findBox.getComp().setEditable(true);
    
        HDivLayout hd = new HDivLayout(10,10);
        hd.setTopHeight(15);
        HPanel panel = new HPanel(hd);
        panel.add(new WithLabelInput(new HPanel(new HDivLayout(GridSplitEnum.C3)), "查找内容：", findBox));
        panel.add(new WithLabelInput(new HPanel(new HDivLayout(GridSplitEnum.C3)), "替换为：", replacedata));
    
        cInput.setId("type");
        cInput.add("size", "区分大小写");
        cInput.add("loop", "循环搜索");
        cInput.add("regular", "正则匹配");
        cInput.add("whole", "全词匹配");
        cInput.setValue("loop");
        panel.add(cInput,sumLabel);
    
        panels.add(panel, getToolBar());
    }
    
    public void show(String find) {
        try {
            fileJsonArr = Json.parse(FileUtils.readFileToString(StartUtil.defaultJsonFile, StandardCharsets.UTF_8)).asObject();
            fileJsonArr.get("findHistory").asArray().forEach(a-> findBox.addOption(a.asString(), a.asString()));
            findBox.getComp().setSelectedItem(StringUtils.isNotEmpty(find) ? find : "");
    
            if (null == dlog) {
                dlog = new HDialog(StartUtil.parentFrame, 600, 290,false){
                    @Override
                    protected void closeEvent() {
                        cancel();
                        dispose();
                    }
                };
                dlog.setIconImage(IconFileUtil.getLogo(IconSizeEnum.SIZE_16));
                dlog.setWindowTitle("查找/替换");
                dlog.setRootPanel(panels);
            }
            dlog.show();
        }catch (Exception e){
            e.printStackTrace();
            PopPaneUtil.error(e);
        }
    }
    
    /**
     * 取消高亮标记
     */
    private void cancel(){
        try {
            SearchContext  sea = new SearchContext();
            sea.setSearchFor("");
            SearchEngine.markAll((RTextArea) textArea, sea);
            sumLabel.setValue("");
            findBox.removeAllItems();
            fileJsonArr = Json.parse(FileUtils.readFileToString(StartUtil.defaultJsonFile, StandardCharsets.UTF_8)).asObject();
            fileJsonArr.get("findHistory").asArray().forEach(a-> findBox.addOption(a.asString(), a.asString()));
        }catch (Exception e){
            e.printStackTrace();
            PopPaneUtil.error(e);
        }
    }
    
    private HPanel getToolBar() {
		HDivLayout hd = new HDivLayout(10,10);
		hd.setTopHeight(15);
        HPanel panel = new HPanel(hd);
        HButton nextButton = new HButton("下一个") {
            @Override
            public void onClick() {
				operation("next");
            }
        };
        HButton lastButton = new HButton("上一个") {
            @Override
            public void onClick() {
				operation("last");
            }
        };
        HButton replacebt = new HButton("替换") {
            @Override
            public void onClick() {
				operation("replace");
            }
        };
        HButton replaceallbt = new HButton("替换全部") {
            @Override
            public void onClick() {
				operation("all");
            }
        };
        HButton cancel = new HButton("关闭") {
            @Override
            public void onClick() {
                cancel();
                dlog.dispose();
            }
        };
        panel.add(nextButton, lastButton, replacebt, replaceallbt, cancel);
        return panel;
    }
    
    /**
     * 执行操作
     * @param type
     */
	private void operation(String type) {
        BufferedWriter writer = null;
        try {
            Object o=findBox.getComp().getSelectedItem();
            String find = o==null ? "" : String.valueOf(o);
    
            context = new SearchContext();
            context.setSearchFor(find);	//查找文本
            context.setMatchCase(cInput.getValue().contains("size"));	//大小写区分
            context.setWholeWord(false);
            context.setSearchForward(true); //上下搜索
            context.setRegularExpression(cInput.getValue().contains("regular")); //正则匹配
            context.setWholeWord(cInput.getValue().contains("whole"));   //全词匹配
    
            RTextAreaHighlighter h = (RTextAreaHighlighter)textArea.getHighlighter();
            if (cInput.getValue().contains("regular") && find.equals("\\n")) {
                //正常情况下匹配的\n显示不出来，改用高亮剩余位置显示\n
                h.setDrawsLayeredHighlights( false );
            } else {
                h.setDrawsLayeredHighlights( true );
            }
            
            SearchResult sr = null;
            if (type.equals("replace") || type.equals("all")) {
                context.setReplaceWith(replacedata.getValue());	//替换文本
                if (type.equals("all")){
                    sr = SearchEngine.replaceAll((RTextArea) textArea, context);
                    JOptionPane.showMessageDialog(null, sr.wasFound() ? "替换成功，替换数量："+sr.getCount() : "没有匹配内容！");
                }else {
                    sr = SearchEngine.replace((RTextArea) textArea, context);
                    if (!sr.wasFound() && cInput.getValue().contains("loop")) {
                        //初始化光标位置
                        textArea.setCaretPosition(0);
                        sr = SearchEngine.replace((RTextArea) textArea, context);
                    }
                    JOptionPane.showMessageDialog(null, sr.wasFound() ? "替换成功" : "没有匹配内容！");
                }
                sumLabel.setValue(sr.getMarkedCount() >1 ? "匹配数量：" + (sr.getMarkedCount()-sr.getCount()) : "");
            } else {
                if (type.equals("last")) context.setSearchForward(false);
                sr = SearchEngine.find(textArea, context);
        
                if (!sr.wasFound() && cInput.getValue().contains("loop")) {
                    //初始化光标位置
                    textArea.setCaretPosition(type.equals("last") ? Integer.max(textArea.getText().length(),0) : 0);
                    sr = SearchEngine.find(textArea, context);
                }
                if (!sr.wasFound()) JOptionPane.showMessageDialog(null, "没有匹配内容！");
                sumLabel.setValue(sr.getMarkedCount() >0 ? "匹配数量：" + sr.getMarkedCount() : "");
            }
    
            //保存查找字符记录
            if (StringUtils.isNotBlank(find)) {
                JsonObject jsob = new JsonObject();
                fileJsonArr.forEach(a -> {
                    JsonValue obj = a.getValue();
                    switch (a.getName()) {
                        case "findHistory":
                            Set<String> set = new LinkedHashSet<String>(Collections.singleton(find));
                            obj.asArray().forEach(b -> set.add(b.asString()));
                    
                            JsonArray jsa= new JsonArray();
                            for (String s : set) {      //默认保存最新20个记录
                                if (jsa.size()>=20) break;
                                jsa.add(s);
                            }
                    
                            jsob.add(a.getName(), jsa);
                            break;
                        default:
                            jsob.add(a.getName(), obj);
                            break;
                    }
                });
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(StartUtil.defaultJsonFile), StandardCharsets.UTF_8));
                jsob.writeTo(writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(e);
        } finally {
            try {
                if (writer != null) writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
}