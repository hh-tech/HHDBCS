package com.hh.hhdb_admin.mgr.vm_editor;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.util.TemplateUtil;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.HTabPane;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.CsMgrEnum;
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
        executeTypeBox.setValue(QueryMgr.getLang("to_query"));

        //保存到sql宝典
        saveSqlBook = new HButton(VmMgr.getLang("saveSqlBook")) {
            @Override
            public void onClick() {
                toFile("vm");
            }
        };
        saveSqlBook.setIcon(VmMgr.getIcon("book"));

        toolBar.add(executeBut, executeTypeBox, saveSqlBook);
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
                            setRightCom(VmMgr.getLang("error") + ":" + e.toString());
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
        HTabPane hTabPane = new HTabPane();
        hTabPane.addPanel("1", VmMgr.getLang("result"), lastPanel.getComp(), false);

        JSplitPane jsp = splitPane.getComp();
        jsp.setRightComponent(hTabPane.getComp());
        if (bool || jsp.getHeight() - jsp.getDividerLocation() <= 15) {
            splitPane.setSplitWeight(0.5);
            bool = false;
        } else {
            jsp.setDividerLocation(jsp.getDividerLocation());
        }
    }
}
