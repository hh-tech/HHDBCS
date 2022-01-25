package com.hh.hhdb_admin.mgr.vm_editor;

import com.alee.extended.button.SplitButtonAdapter;
import com.alee.extended.button.WebSplitButton;
import com.alee.managers.style.StyleId;
import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.util.ClassLoadUtil;
import com.hh.frame.common.util.TemplateUtil;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.hmenu.HMenuItem;
import com.hh.frame.swingui.view.hmenu.HPopMenu;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.textEditor.QueryEditUtil;
import com.hh.hhdb_admin.common.util.textEditor.QueryEditorTextArea;
import com.hh.hhdb_admin.mgr.query.QueryMgr;
import com.hh.hhdb_admin.mgr.sql_book.SqlBookMgr;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;

/**
 * 模板编辑器
 *
 * @author hexu
 */
public class VmComp extends AbsHComp {
    private LastPanel lastPanel = new LastPanel(false);
    private HSplitPanel splitPane = new HSplitPanel(false);
    private QueryEditorTextArea textArea;
    private HButton executeBut, saveSqlBook;
    private SelectBox executeTypeBox;

    private boolean bool = true;

    public VmComp(String text) throws Exception {
        textArea = QueryEditUtil.getVMEditor(true);
        if (StringUtils.isNotBlank(text)) textArea.setText(text);
        lastPanel.setHead(getBarPanel().getComp());
        splitPane.setPanelOne(textArea);
        splitPane.setSplitWeight(0.5);
        lastPanel.set(splitPane.getComp());
        comp = lastPanel.getComp();
    }

    public LastPanel getLastPanel() throws Exception {
        return lastPanel;
    }

    private HBarPanel getBarPanel() throws Exception {
        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        HBarPanel toolBar = new HBarPanel(l);
        //执行按钮
        executeBut = new HButton(VmMgr.getLang("execute")) {
            @Override
            public void onClick() {
                execute();
            }
        };
        executeBut.setIcon(VmMgr.getIcon("start"));

        executeTypeBox = new SelectBox("executeTypeBox");
        executeTypeBox.addOption(VmMgr.getLang("to_window"), VmMgr.getLang("to_window"));
        executeTypeBox.addOption(VmMgr.getLang("to_query"), VmMgr.getLang("to_query"));
        executeTypeBox.addOption(VmMgr.getLang("to_file"), VmMgr.getLang("to_file"));
        executeTypeBox.setValue(VmMgr.getLang("to_query"));

        //保存到sql宝典
        saveSqlBook = new HButton(VmMgr.getLang("saveSqlBook")) {
            @Override
            public void onClick() {
                toFile("vm");
            }
        };
        saveSqlBook.setIcon(VmMgr.getIcon("book"));
    
        toolBar.add(executeBut, executeTypeBox, saveSqlBook,new SplitButton());
        return toolBar;
    }

    private void toFile(String type) {
        try {
            String sql = StringUtils.isNotEmpty(textArea.getSelectedText()) ? textArea.getSelectedText() : textArea.getText();
            if (!StringUtils.isNotBlank(sql)) return;

            JFileChooser chooser = new JFileChooser();
            if (type.equals("vm")) {
                JsonObject o = StartUtil.eng.doCall(CsMgrEnum.SQL_BOOK, GuiJsonUtil.genGetShareIdMsg(SqlBookMgr.ObjType.SHARE_PATH));
                if (null != o) chooser.setCurrentDirectory(new File(GuiJsonUtil.toStrSharedId(o)));
            }
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            FileFilter fileFilter = new FileNameExtensionFilter(type.toUpperCase() + "文件(*." + type + ")", type);
            chooser.setFileFilter(fileFilter);
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                new Thread(new SwingWorker<String, String>() {
                    File file = null;

                    @Override
                    protected String doInBackground() throws Exception {
                        setEnab(false);
                        String url = chooser.getSelectedFile().getCanonicalPath();
                        file = new File(url.endsWith("." + type) ? url : url + "." + type);
                        if (type.equals("vm")) {
                            FileUtils.writeStringToFile(file, textArea.getText(), "utf-8");
                        } else {
                            TemplateUtil.varMap2File(new HashMap<String, Object>(), sql, file);
                        }
                        JOptionPane.showMessageDialog(null, VmMgr.getLang("success"), VmMgr.getLang("hint"), JOptionPane.INFORMATION_MESSAGE);
                        return "";
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                        } catch (Exception e) {
                            e.printStackTrace();
                            setRightCom(VmMgr.getLang("error") + ":" + e);
                            FileUtils.deleteQuietly(file);
                        } finally {
                            setEnab(true);
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            setRightCom(e.getMessage());
        }
    }

    private void execute() {
        if (executeTypeBox.getValue().equals(VmMgr.getLang("to_file"))) {
            toFile("sql");
        } else {
            new Thread(new SwingWorker<String, String>() {
                @Override
                protected String doInBackground() throws Exception {
                    setEnab(false);
                    String sql = StringUtils.isNotEmpty(textArea.getSelectedText()) ? textArea.getSelectedText() : textArea.getText();
                    if (!StringUtils.isNotBlank(sql)) return "";

                    if (executeTypeBox.getValue().equals(VmMgr.getLang("to_window"))) {
                        setRightCom(TemplateUtil.strVm2str(new HashMap<String, Object>(), sql));
                    } else if (executeTypeBox.getValue().equals(VmMgr.getLang("to_query"))) {
                        String s = TemplateUtil.strVm2str(new HashMap<String, Object>(), sql);
                        if (StringUtils.isNotBlank(s)) {
                            StartUtil.eng.doPush(CsMgrEnum.QUERY, GuiJsonUtil.toJsonCmd(QueryMgr.CMD_SHOW_QUERY).add("text", s));
                        }
                    }
                    return "";
                }

                @Override
                protected void done() {
                    try {
                        get();
                    } catch (Exception e) {
                        e.printStackTrace();

                        StringBuffer sb = new StringBuffer();
                        sb.append(VmMgr.getLang("error") + ":");
                        sb.append(e.getMessage().indexOf("Java heap space") != -1 ? VmMgr.getLang("nimiety") : e.toString());
                        setRightCom(sb.toString());
                    } finally {
                        setEnab(true);
                    }
                }
            }).start();
        }
    }

    private void setEnab(boolean bool) {
        executeBut.setEnabled(bool);
        executeTypeBox.setEnabled(bool);
        saveSqlBook.setEnabled(bool);
    }

    private void setRightCom(String text) {
        TextAreaInput textAr = new TextAreaInput();
        textAr.setEnabled(false);
        textAr.setValue(text);
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.set(textAr.getComp());
        HTabPanel hTabPane = new HTabPanel();
        hTabPane.addPanel("1", lastPanel, new HeaderConfig(VmMgr.getLang("result")).setFixTab(true));

        JSplitPane jsp = splitPane.getComp();
        jsp.setRightComponent(hTabPane.getComp());
        if (bool || jsp.getHeight() - jsp.getDividerLocation() <= 15) {
            splitPane.setSplitWeight(0.5);
            bool = false;
        } else {
            jsp.setDividerLocation(jsp.getDividerLocation());
        }
    }
    
    //分割按钮
    private final class SplitButton extends AbsHComp {
        private SplitButton(){
            HPopMenu toolPopupMenu = new HPopMenu();
            HMenuItem tpccItem = new HMenuItem(VmMgr.getLang("tpccVm"), VmMgr.getIcon("sql_format")){
                @Override
                protected void onAction() {
                    try {
                        executeTypeBox.setValue(VmMgr.getLang("to_file"));
                        textArea.setText(ClassLoadUtil.loadTextRes(VmComp.class,"util/tpccVM.sql"));
                        textArea.getTextArea().setCaretPosition(0);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            HMenuItem crTableItem = new HMenuItem(VmMgr.getLang("crTable"), VmMgr.getIcon("sql_format")){
                @Override
                protected void onAction() {
                    try {
                        executeTypeBox.setValue(VmMgr.getLang("to_file"));
                        textArea.setText(ClassLoadUtil.loadTextRes(VmComp.class,"util/crTable.sql"));
                        textArea.getTextArea().setCaretPosition(0);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            toolPopupMenu.addItem(tpccItem,crTableItem);
    
            WebSplitButton wb = new WebSplitButton(StyleId.splitbuttonIconHover, VmMgr.getIcon("help"));
            wb.addSplitButtonListener(new SplitButtonAdapter() {
                @Override
                public void buttonClicked(ActionEvent e) {
                    HDialog helpDialog = new HDialog(StartUtil.parentFrame, 500, 300);
                    HPanel panel = new HPanel();
                    TextAreaInput areaInput = new TextAreaInput();
                    areaInput.setEnabled(true);
                    areaInput.setValue("按alt+k可以弹出模版选择提示框，可以结合sql语句来使用。\n\n" +
                            "例如使用for循环用来批量生成SQL语句：\n" +
                            "#foreach( $item  in [1..10] )\n" +
                            " create table tab$item (id number(10),name varchar2(100));\n" +
                            "#end");
                    LastPanel lastPanel = new LastPanel();
                    lastPanel.set(areaInput.getComp());
                    panel.setLastPanel(lastPanel);
                    helpDialog.setRootPanel(panel);
                    helpDialog.setIconImage(IconFileUtil.getLogo());
                    helpDialog.setWindowTitle(VmMgr.getLang("help"));
                    helpDialog.show();
                }
            });
            wb.setText(VmMgr.getLang("help"));
            wb.setPopupMenu(toolPopupMenu.getComp());
            wb.setBorderPainted(false);
            comp = wb;
        }
    }
}
