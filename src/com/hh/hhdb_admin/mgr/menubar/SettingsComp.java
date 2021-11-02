package com.hh.hhdb_admin.mgr.menubar;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.json.JsonValue;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.RadioGroupInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.frame.swingui.view.util.VerifyUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.textEditor.QueryEditUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 查询器基础信息设置
 *
 * @author hexu
 */
public class SettingsComp {
    private HDialog dialog;
    private TextInput rowInput;
    private TextInput nullInput;
    private final HPanel keyhPanel = new HPanel(new HDivLayout(15, 10, GridSplitEnum.C12));
    private final HPanel VMkeyhPanel = new HPanel(new HDivLayout(15, 10, GridSplitEnum.C12));
    private final HPanel generalPanel = new HPanel(new HDivLayout(15, 10, GridSplitEnum.C12));

    private JsonObject fileJsonArr;

    public SettingsComp() {
        try {
            fileJsonArr = Json.parse(FileUtils.readFileToString(StartUtil.defaultJsonFile, StandardCharsets.UTF_8)).asObject();
            rowInput = new TextInput("varPageSize", fileJsonArr.get("varPageSize").asInt()+"");
            rowInput.setInputVerifier(VerifyUtil.getTextIntVerifier(MenubarComp.getLang("rowsnumber"), 1, 2147483647));
            nullInput = new TextInput("null");
            dialog = new HDialog(StartUtil.parentFrame, 650, 640);
            dialog.setRootPanel(init());
            dialog.setIconImage(MenubarComp.getIcon("setting"));
            dialog.setWindowTitle(MenubarComp.getLang("setting"));
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
        }
    }

    private HPanel init() {
        //快捷键设置面板
        keyhPanel.setTitle(MenubarComp.getLang("quer_settings"));
        keyhPanel.add(getLabelInput("  ", new LabelInput(MenubarComp.getLang("frontkey")), new LabelInput(MenubarComp.getLang("backkey")), new LabelInput(MenubarComp.getLang("combination"))));
        keyhPanel.add(getKeyWordPanel(MenubarComp.getLang("key"), QueryEditUtil.KEYWORD));
        keyhPanel.add(getKeyWordPanel(MenubarComp.getLang("table_name"), QueryEditUtil.TABLE_KEYWORD));
        keyhPanel.add(getKeyWordPanel(MenubarComp.getLang("view_name"), QueryEditUtil.VIEW_KEYWORD));
        keyhPanel.add(getKeyWordPanel(MenubarComp.getLang("fun_name"), QueryEditUtil.FUN_KEYWORD));
        keyhPanel.add(getKeyWordPanel(MenubarComp.getLang("synonyms-for"), QueryEditUtil.SYNONYM_KEYWORD));
        keyhPanel.add(getKeyWordPanel(MenubarComp.getLang("package"), QueryEditUtil.PACKAGE_KEYWORD));
        keyhPanel.add(new HeightComp(5));

        VMkeyhPanel.setTitle(MenubarComp.getLang("vm_setting"));
        VMkeyhPanel.add(new HeightComp(5));
        VMkeyhPanel.add(getKeyWordPanel(MenubarComp.getLang("key"), QueryEditUtil.VMKEYWORD));
        VMkeyhPanel.add(new HeightComp(5));

        //一般设置面板
        String str = fileJsonArr.get("qkeys").asObject().asObject().get(QueryEditUtil.AUTOMATIC).asString();
        str = StringUtils.isNotBlank(str) ? str : "true";
        generalPanel.setTitle(MenubarComp.getLang("general_setting"));
        HGridPanel rowsnumber = getLabelInput(MenubarComp.getLang("rowsnumber") + ":", rowInput);
        rowsnumber.setId("varPageSize");
        generalPanel.add(rowsnumber);
        nullInput.setValue(fileJsonArr.get("null").asString());
        HGridPanel nulGrid = getLabelInput(MenubarComp.getLang("null_view"), nullInput);
        nulGrid.setId("null");
        generalPanel.add(nulGrid);
        RadioGroupInput rInput = new RadioGroupInput(QueryEditUtil.AUTOMATIC, new HPanel(new HDivLayout(GridSplitEnum.C6)));
        rInput.setId(QueryEditUtil.AUTOMATIC);
        rInput.add("true", MenubarComp.getLang("automatic"));
        rInput.add("false", MenubarComp.getLang("manual"));
        rInput.setSelected(str);
        HGridPanel gridPanel = getLabelInput(MenubarComp.getLang("prompt_mode") + ":", rInput);
        gridPanel.setId(QueryEditUtil.AUTOMATIC);
        generalPanel.add(gridPanel);

        HPanel hPanel = new HPanel(new HDivLayout(15, 10, GridSplitEnum.C12));
        hPanel.add(keyhPanel);
        hPanel.add(VMkeyhPanel);
        hPanel.add(generalPanel);
        hPanel.add(initHButton());
        return hPanel;
    }

    private HGridPanel getKeyWordPanel(String type, String name) {
        TextInput key = new TextInput(name);
        key.setEnabled(false);
        TextInput keyInput = new TextInput();
        SelectBox keyBox = new SelectBox() {
            @Override
            public void onItemChange(ItemEvent e) {
                if (!keyInput.getValue().trim().equals("")) key.setValue(getValue() + "+" + keyInput.getValue().trim());
            }
        };
        keyBox.addOption("Alt", "Alt");
        keyBox.addOption("Ctrl", "Ctrl");
//        keyBox.addOption("command ", "command");
        keyBox.setValue("Alt");

        keyInput.getComp().addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {}
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 16) return;
                char keychar = e.getKeyChar();
                if (!("" + keychar + "").trim().equals("") && keychar < 127) {
                    keyInput.setValue("" + keychar + "");
                    key.setValue(keyBox.getValue() + "+" + keychar);
                } else {
                    key.setValue(MenubarComp.getLang("invalid"));
                    keyInput.setValue("");
                }
            }
        });
        String keyStr = fileJsonArr.get("qkeys").asObject().asObject().get(name).asString();
        if (StringUtils.isNotBlank(keyStr) && !keyStr.equals(MenubarComp.getLang("invalid"))) {
            keyBox.setValue(keyStr.substring(0, keyStr.indexOf("+")));
            keyInput.setValue(keyStr.charAt(keyStr.length() - 1) + "");
            key.setValue(keyStr);
        } else {
            key.setValue(MenubarComp.getLang("invalid"));
        }
        HGridPanel hPanel = getLabelInput(type + ":", keyBox, keyInput, key);
        hPanel.setId(name);
        return hPanel;
    }

    private HBarPanel initHButton() {
        HButton savebtn = new HButton(MenubarComp.getLang("Ok")) {
            @Override
            public void onClick() {
                String keyText = ((HGridPanel) keyhPanel.getHComp(QueryEditUtil.KEYWORD)).getInputValue(QueryEditUtil.KEYWORD);
                String tabText = ((HGridPanel) keyhPanel.getHComp(QueryEditUtil.TABLE_KEYWORD)).getInputValue(QueryEditUtil.TABLE_KEYWORD);
                String viewText = ((HGridPanel) keyhPanel.getHComp(QueryEditUtil.VIEW_KEYWORD)).getInputValue(QueryEditUtil.VIEW_KEYWORD);
                String funText = ((HGridPanel) keyhPanel.getHComp(QueryEditUtil.FUN_KEYWORD)).getInputValue(QueryEditUtil.FUN_KEYWORD);
                String synonymText = ((HGridPanel) keyhPanel.getHComp(QueryEditUtil.SYNONYM_KEYWORD)).getInputValue(QueryEditUtil.SYNONYM_KEYWORD);
                String packageText = ((HGridPanel) keyhPanel.getHComp(QueryEditUtil.PACKAGE_KEYWORD)).getInputValue(QueryEditUtil.PACKAGE_KEYWORD);
                String vmkeyText = ((HGridPanel) VMkeyhPanel.getHComp(QueryEditUtil.VMKEYWORD)).getInputValue(QueryEditUtil.VMKEYWORD);
                String varPageSize = ((HGridPanel) generalPanel.getHComp("varPageSize")).getInputValue("varPageSize");
                String automatic = ((HGridPanel) generalPanel.getHComp(QueryEditUtil.AUTOMATIC)).getInputValue(QueryEditUtil.AUTOMATIC);
                String nul = ((HGridPanel) generalPanel.getHComp("null")).getInputValue("null");

                if (checkQkey(keyText,tabText,viewText,funText,synonymText,packageText,vmkeyText)) return;
                BufferedWriter writer = null;
                try {
                    //保存快捷方式到json
                    JsonObject jsob = new JsonObject();
                    fileJsonArr.forEach(a -> {
                        JsonValue obj = a.getValue();
                        switch (a.getName()) {
                            case "qkeys":
                                JsonObject newObj = new JsonObject();
                                newObj.add(QueryEditUtil.AUTOMATIC, automatic);
                                newObj.add(QueryEditUtil.KEYWORD, keyText);
                                newObj.add(QueryEditUtil.TABLE_KEYWORD, tabText);
                                newObj.add(QueryEditUtil.VIEW_KEYWORD, viewText);
                                newObj.add(QueryEditUtil.FUN_KEYWORD, funText);
                                newObj.add(QueryEditUtil.SYNONYM_KEYWORD, synonymText);
                                newObj.add(QueryEditUtil.PACKAGE_KEYWORD, packageText);
                                newObj.add(QueryEditUtil.VMKEYWORD, vmkeyText);
                                jsob.add(a.getName(), newObj);
                                break;
                            case "null":
                                jsob.add(a.getName(), nul);
                                break;
                            case "varPageSize":
                                jsob.add(a.getName(), Integer.parseInt(varPageSize));
                                break;
                            default:
                                jsob.add(a.getName(), obj);
                                break;
                        }
                    });
                    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(StartUtil.defaultJsonFile), StandardCharsets.UTF_8));
                    jsob.writeTo(writer);
                    dialog.dispose();
                    PopPaneUtil.info(StartUtil.parentFrame.getWindow(), MenubarComp.getLang("success"));
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        savebtn.setIcon(MenubarComp.getIcon("submit"));
        HButton cancelBtn = new HButton(MenubarComp.getLang("cancel")) {
            @Override
            protected void onClick() {
                dialog.dispose();
            }
        };
        cancelBtn.setIcon(MenubarComp.getIcon("cancel"));
        HBarLayout layout = new HBarLayout();
        layout.setAlign(AlignEnum.CENTER);
        HBarPanel barPanel = new HBarPanel(layout);
        barPanel.add(savebtn, cancelBtn);
        return barPanel;
    }

    /**
     * 快捷键合法验证
     */
    private boolean checkQkey(String keyText,String tabText,String viewText,String funText,String synonymText,String packageText,String vmkeyText) {
        for (String s : Arrays.asList(keyText, tabText, viewText,funText,synonymText,vmkeyText)) {
            if (s.equals(MenubarComp.getLang("invalid")) || s.equals("Ctrl+f")) {
                JOptionPane.showMessageDialog(null, MenubarComp.getLang("settingError") + s, MenubarComp.getLang("error"), JOptionPane.ERROR_MESSAGE);
                return true;
            }
        }
        if (verify(keyText,tabText)) return true;
        if (verify(keyText,viewText)) return true;
        if (verify(keyText,funText)) return true;
        if (verify(keyText,synonymText)) return true;
        if (verify(keyText,packageText)) return true;
        if (verify(tabText,viewText)) return true;
        if (verify(tabText,funText)) return true;
        if (verify(tabText,synonymText)) return true;
        if (verify(tabText,packageText)) return true;
        if (verify(viewText,funText)) return true;
        if (verify(viewText,synonymText)) return true;
        if (verify(viewText,packageText)) return true;
        if (verify(funText,synonymText)) return true;
        if (verify(funText,packageText)) return true;
        if (verify(synonymText,packageText)) return true;
        return false;
    }

    private boolean verify(String str,String str1){
        if (str.equals(str1)) {
            JOptionPane.showMessageDialog(null, MenubarComp.getLang("settingError") + str, MenubarComp.getLang("error"), JOptionPane.ERROR_MESSAGE);
            return true;
        }
        return false;
    }

    private HGridPanel getLabelInput(String name, AbsInput... intputs) {
        HGridPanel gridPanel;
        if (intputs.length > 1) {
            gridPanel = new HGridPanel(new HGridLayout(GridSplitEnum.C3, GridSplitEnum.C3, GridSplitEnum.C3));
        } else {
            gridPanel = new HGridPanel(new HGridLayout(GridSplitEnum.C3));
        }
        gridPanel.setComp(1, new LabelInput(name, AlignEnum.RIGHT));
        int i = 2;
        for (AbsInput absInput : intputs) {
            if (absInput instanceof LabelInput) ((LabelInput) absInput).setAlign(AlignEnum.CENTER);
            gridPanel.setComp(i, absInput);
            i++;
        }
        return gridPanel;
    }

}
