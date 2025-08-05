package com.hh.hhdb_admin.mgr.quick_query.ui;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.dbobj3.qcmd.QuickCmdTool;
import com.hh.frame.dbobj3.qcmd.core.CmdRs;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import org.apache.commons.lang3.StringUtils;

import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据tab页
 *
 * @author hexu
 */
public class DataTab extends LastPanel {
    private final JdbcBean jdbc;
    private final HTabPanel hTabPane = new HTabPanel();

    public DataTab(JdbcBean jdbc) {
        super(false);
        this.jdbc = jdbc;
    }

    /**
     * 组装数据显示
     */
    public void showTable(String sql, int maxRow, int maxSet) {
        Connection conn = null;
        LastPanel lastPanel;
        try {
            QuickCmdTool cmd = new QuickCmdTool(jdbc,CsVarUtil.newConn(jdbc), sql);
            List<CmdRs> lists = cmd.getDataSet(maxRow, maxSet);
            for (int i = 0; i < lists.size(); i++) {
                CmdRs cmdRs = lists.get(i);
                if (cmdRs.getData() != null && cmdRs.getData().size() > 0) {
                    lastPanel = getLastPanel(cmdRs.getData());
                    if (StringUtils.isNotBlank(cmdRs.getDes())) {
                        HTabPanel tabPane = new HTabPanel();
                        
                        tabPane.setPanel("0", lastPanel, newHeaderConfig("DATA:"));
                        TextAreaInput textArea = new TextAreaInput();
                        textArea.setEnabled(false);
                        textArea.setValue(cmdRs.getDes());
                        LastPanel lastp = new LastPanel();
                        lastp.set(textArea.getComp());
                        tabPane.setPanel("1", lastp, newHeaderConfig("MESSAGE:"));
    
                        LastPanel las = new LastPanel();
                        las.set(tabPane.getComp());
                        hTabPane.setPanel(i + "",las, newHeaderConfig(cmdRs.getTitle()));
                    } else {
                        hTabPane.setPanel(i + "",lastPanel, newHeaderConfig(cmdRs.getTitle()));
                    }
                } else {
                    TextAreaInput textArea = new TextAreaInput();
                    textArea.setEnabled(false);
                    String title = cmdRs.getTitle();
                    if ("\\?".equals(sql)) {
                        title = LangMgr.lang(Word2.HELP);
                        textArea.setValue(LangMgr.sLang(CsL.HELP_EXAMPLE));
                    } else {
                        textArea.setValue(String.format(LangMgr.sLang(CsL.NOT_SUPPORT), sql));
                    }
                    LastPanel lastp = new LastPanel();
                    lastp.set(textArea.getComp());
                    hTabPane.setPanel(i + "",lastp, newHeaderConfig(title));
                }
            }
            set(hTabPane.getComp());
        } catch (Exception e) {
            TextAreaInput textArea = new TextAreaInput();
            textArea.setEnabled(false);
            textArea.setValue(sql + LangMgr.sLang(CsL.AN_UNLAWFUL_ORDER));
            set(textArea.getComp());
        } finally {
            ConnUtil.close(conn);
        }
    }

    private LastPanel getLastPanel(List<List<String>> list) {
        LastPanel lastPanel = new LastPanel(false);
        HTable tab = new HTable();
        tab.setRowHeight(25);
        tab.setRowStyle(true);
        List<String> header = list.get(0);
        header.forEach(col -> tab.addCols(new DataCol(col, col)));
        List<Map<String, String>> data = new ArrayList<>();
        for (int j = 1; j < list.size(); j++) {
            Map<String, String> itemData = new HashMap<>();
            for (int x = 0; x < list.get(j).size(); x++) {
                itemData.put(header.get(x), list.get(j).get(x));
            }
            data.add(itemData);
        }
        tab.setCellEditable(true);
        //显示形式
        SelectBox typeBox = new SelectBox("schemaBox") {
            @Override
            public void onItemChange(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    tab.setRowStyle("transverse".equals(getValue()));
                }
            }
        };
        typeBox.addOption(LangMgr.lang(Word4.LATERAL_DISPLAY), "transverse");
        typeBox.addOption(LangMgr.lang(Word4.PORTRAIT_DISPLAY), "vertical");
        SearchToolBar stb = new SearchToolBar(tab);
        stb.add(typeBox);
        lastPanel.setHead(stb.getComp());
        lastPanel.setWithScroll(tab.getComp());
        tab.load(data, 1);
        return lastPanel;
    }
    
    public static HeaderConfig newHeaderConfig(String name){
        HeaderConfig headerConfig = new HeaderConfig(name);
        headerConfig.setTitleEditable(false);
        headerConfig.setFixTab(true);
        headerConfig.setDetachEnabled(false);
        return headerConfig;
    }
}
