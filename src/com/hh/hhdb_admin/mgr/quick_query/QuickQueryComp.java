package com.hh.hhdb_admin.mgr.quick_query;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.textEditor.HTextArea;
import com.hh.frame.swingui.view.textEditor.base.ConstantsEnum;
import com.hh.frame.swingui.view.textEditor.base.ThemesEnum;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.frame.swingui.view.util.VerifyUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.mgr.quick_query.ui.OutputTabPanel;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 查询器插件
 *
 * @author hexu
 */
public class QuickQueryComp {
    private final JdbcBean jdbc;
    private static final String LOG_NAME = QuickQueryComp.class.getSimpleName();
    private final LastPanel lastPanel = new LastPanel(false);
    //输出结果选项卡面板
    private OutputTabPanel outputTabPanel;
    //分割面板，上面是查询面板，下面是输出选项卡面板
    private final HSplitPanel splitPane = new HSplitPanel(false);
    //编辑器
    public HTextArea textArea;

    private HButton executeBut;

    private boolean bool = true;

    private final TextInput rowInput = new TextInput(null, "100");
    private final TextInput setInput = new TextInput(null, "10");


    public QuickQueryComp(JdbcBean jdbcBean) {
        Connection conn = null;
        try {
            this.jdbc = jdbcBean;
            //初始化编辑器
            HPanel panel = new HPanel();
            textArea = new HTextArea(false, true);
            if(HHSwingUi.isDarkSkin()) {
            	textArea.setTheme(ThemesEnum.monokai);
            }
//            textArea.setTheme(ThemesEnum.quick_cmd);
            textArea.showBookMask(false);
            textArea.setConstants(ConstantsEnum.SYNTAX_STYLE_NONE);
            LastPanel areaLast = new LastPanel();
            areaLast.set(textArea.getComp());
            panel.setLastPanel(areaLast);
            lastPanel.setHead(initToolBar().getComp());
            splitPane.setPanelOne(panel);
            splitPane.setPanelTwo(getHelpPanel());
            splitPane.setDividerLocation(380);
            lastPanel.set(splitPane.getComp());
            LabelInput position = new LabelInput();
            position.setAlign(AlignEnum.RIGHT);
            lastPanel.setFoot(position.getComp());

        } finally {
            ConnUtil.close(conn);
        }
    }

    public LastPanel getLastPanel() throws Exception {
        return lastPanel;
    }

    public String getTitle() throws Exception {
        return QuickQueryMgr.getLang("quickCMD");
    }

    private HBarPanel initToolBar() {
        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        HBarPanel hTool = new HBarPanel(l);
        //执行按钮
        executeBut = new HButton(QuickQueryMgr.getLang("execute")) {
            @Override
            public void onClick() {
                if (!StringUtils.isNotBlank(textArea.getArea().getTextArea().getText())) {
                    return;
                }
                new Thread(new SwingWorker<String, String>() {
                    @Override
                    public String doInBackground() {
                        executeBut.setEnabled(false);
                        String selected = textArea.getArea().getTextArea().getSelectedText();
                        String text = textArea.getArea().getTextArea().getText();
                        String sql = StringUtils.isEmpty(selected) ? text : selected;
                        //拆分sql
                        Map<Integer, String> sqlMap = new LinkedHashMap<>();
                        String[] str = sql.split("\n");
                        for (int i = 0; i < str.length; i++) {
                            if (StringUtils.isNotBlank(str[i])) {
                                String val = str[i].trim();
                                if (StringUtils.endsWith(val, ";")) {
                                    val = val.substring(0, val.length() - 1);
                                }
                                sqlMap.put(i, val.trim());
                            }
                        }
                        outputTabPanel =  new OutputTabPanel(jdbc);
                        outputTabPanel.showRs(sqlMap, Integer.parseInt(rowInput.getValue()), Integer.parseInt(setInput.getValue()));
                        JSplitPane jsp = splitPane.getComp();
                        jsp.setRightComponent(outputTabPanel.getTabPane().getComp());
                        if (bool) {
                            splitPane.setSplitWeight(0.5);
                            bool = false;
                        } else {
                            jsp.setDividerLocation(jsp.getDividerLocation());
                        }
                        return "";
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                        } catch (Exception e) {
                            e.printStackTrace();
                            logUtil.error(LOG_NAME, e);
                            JOptionPane.showMessageDialog(null, QuickQueryMgr.getLang("error") + ":" + e, QuickQueryMgr.getLang("error"), JOptionPane.ERROR_MESSAGE);
                        } finally {
                            executeBut.setEnabled(true);
                        }
                    }
                }).start();
            }
        };
        executeBut.setIcon(QuickQueryMgr.getIcon("start"));
        hTool.add(executeBut);
        hTool.add(new LabelInput(QuickQueryMgr.getLang("maxSet") + ":"));
        setInput.setInputVerifier(VerifyUtil.getTextIntVerifier(QuickQueryMgr.getLang("maxSet"), 1, 10000));
        hTool.add(setInput, 100);
        hTool.add(new LabelInput(QuickQueryMgr.getLang("maxRow") + ":"));
        rowInput.setInputVerifier(VerifyUtil.getTextIntVerifier(QuickQueryMgr.getLang("maxRow"), 1, 10000));
        hTool.add(rowInput, 100);
        HButton helpBut = new HButton(QuickQueryMgr.getLang("useHelp")) {
            @Override
            protected void onClick() {
                HDialog helpDialog = new HDialog(StartUtil.parentFrame, 500, 500);
                helpDialog.setRootPanel(getHelpPanel());
                helpDialog.setIconImage(QuickQueryMgr.getIcon("help1").getImage());
                helpDialog.setWindowTitle(QuickQueryMgr.getLang("useHelp"));
                helpDialog.show();
            }
       };
        helpBut.setIcon(QuickQueryMgr.getIcon("help"));
        hTool.add(helpBut);
        return hTool;
    }

    private HPanel getHelpPanel() {
        HPanel panel = new HPanel();
        TextAreaInput areaInput = new TextAreaInput();
        areaInput.setEnabled(false);
        areaInput.setValue(QuickQueryMgr.getLang("help"));
        LastPanel lastPanel = new LastPanel();
        lastPanel.set(areaInput.getComp());
        panel.setLastPanel(lastPanel);
        return panel;
    }

}
