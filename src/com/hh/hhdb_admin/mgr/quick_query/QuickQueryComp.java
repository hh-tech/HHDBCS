package com.hh.hhdb_admin.mgr.quick_query;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaRegularEnum;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.util.VerifyUtil;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import com.hh.hhdb_admin.mgr.quick_query.ui.OutputTabPanel;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;


public class QuickQueryComp extends AbsHComp {
    private final JdbcBean jdbc;
    private final HWindow window;
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

    public QuickQueryComp(JdbcBean jdbcBean, HWindow window) {
        Connection conn = null;
        this.jdbc = jdbcBean;
        this.window = window;
        try {
            //初始化编辑器
            HPanel panel = new HPanel();
            textArea = new HTextArea();
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
            comp = lastPanel.getComp();
            textArea.setFRDlg(GlobalWorkUtil.getFrDlg());
            textArea.setGotoDlg(GlobalWorkUtil.getAreaGotoDlg());
            //添加快捷键
            textArea.getRSyntaxTextArea().addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == (KeyEvent.VK_F8)) {
                        execute();
                    }
                }
            });
        } catch (Exception e) {
            Lg.error(QuickQueryComp.class, e);
        } finally {
            ConnUtil.close(conn);
        }
    }

    public LastPanel getLastPanel() throws Exception {
        return lastPanel;
    }

    public String getTitle() throws Exception {
        return LangMgr.lang(Word4.QUICK_WINDOWS);
    }

    private HBarPanel initToolBar()throws Exception {
        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        HBarPanel hTool = new HBarPanel(l);
        //执行按钮
        executeBut = new HButton(LangMgr.lang(Word2.EXECUTE)) {
            @Override
            public void onClick() {
                execute();
            }
        };
        executeBut.setIcon(FaSolidEnum.PLAY,16);
        executeBut.setToolTipText("F8");
        hTool.add(executeBut);
        hTool.add(new LabelInput(LangMgr.lang(Word4.MAXIMUM_QUANTITY) + ":"));
        setInput.setInputVerifier(VerifyUtil.getTextIntVerifier(LangMgr.lang(Word4.MAXIMUM_QUANTITY), 1, 10000));
        hTool.add(setInput, 100);
        hTool.add(new LabelInput(LangMgr.sLang(CsL.MAXIMUM_ROW) + ":"));
        rowInput.setInputVerifier(VerifyUtil.getTextIntVerifier(LangMgr.sLang(CsL.MAXIMUM_ROW), 1, 10000));
        hTool.add(rowInput, 100);
        HButton helpBut = new HButton(LangMgr.lang(Word2.HELP)) {
            @Override
            protected void onClick() {
                HDialog helpDialog = new HDialog(window, 500, 500);
                helpDialog.setRootPanel(getHelpPanel());
                helpDialog.setIconImage(IconFileUtil.getLogo());
                helpDialog.setWindowTitle(LangMgr.lang(Word2.HELP));
                helpDialog.removeCancleBtn();
                helpDialog.show();
            }
        };
        helpBut.setIcon(FaRegularEnum.CIRCLE_QUESTION,16);
        hTool.add(helpBut);
        return hTool;
    }

    private void execute() {
        if (!StringUtils.isNotBlank(textArea.getText())) {
            return;
        }
        new Thread(new SwingWorker<String, String>() {
            @Override
            public String doInBackground() {
                executeBut.setEnabled(false);
                String selected = textArea.getSelectedText();
                String text = textArea.getText();
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
                outputTabPanel = new OutputTabPanel(jdbc);
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
                    Lg.error(QuickQueryComp.class, e);
                    PopDlgUtil.error(window, e);
                } finally {
                    executeBut.setEnabled(true);
                }
            }
        }).start();
    }
    
    private HPanel getHelpPanel() {
        HPanel panel = new HPanel();
        TextAreaInput areaInput = new TextAreaInput();
        areaInput.setEnabled(false);
        areaInput.setValue(LangMgr.sLang(CsL.HELP_EXAMPLE));
        LastPanel lastPanel = new LastPanel();
        lastPanel.set(areaInput.getComp());
        panel.setLastPanel(lastPanel);
        return panel;
    }
}
