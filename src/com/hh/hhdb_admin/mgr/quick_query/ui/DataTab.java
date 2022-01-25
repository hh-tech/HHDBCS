package com.hh.hhdb_admin.mgr.quick_query.ui;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.dbobj2.QuickCmdTool;
import com.hh.frame.dbobj2.qcmd.core.CmdRs;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.hhdb_admin.common.csTabPanel.CSTablePanel;
import com.hh.hhdb_admin.mgr.quick_query.QuickQueryMgr;
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
    private final CSTablePanel hTabPane = new CSTablePanel();

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
            QuickCmdTool cmd = new QuickCmdTool(jdbc, conn = ConnUtil.getConn(jdbc), sql);
            List<CmdRs> lists = cmd.getDataSet(maxRow, maxSet);
            for (int i = 0; i < lists.size(); i++) {
                CmdRs cmdRs = lists.get(i);
                if (cmdRs.getData() != null && cmdRs.getData().size() > 0) {
                    HTabPanel tabPane = new HTabPanel();
                    lastPanel = getLastPanel(cmdRs.getData());
                    if (StringUtils.isNotBlank(cmdRs.getDes())) {
                        tabPane.addPanel("0",lastPanel, new HeaderConfig("DATA:").setFixTab(true));
                        TextAreaInput textArea = new TextAreaInput();
                        textArea.setEnabled(false);
                        textArea.setValue(cmdRs.getDes());
                        LastPanel lastp = new LastPanel();
                        lastp.set(textArea.getComp());
                        tabPane.addPanel("1", lastp, new HeaderConfig("MESSAGE:").setFixTab(true));
                        hTabPane.addPanel(i + "", cmdRs.getTitle(), tabPane.getComp());
                    } else {
                        hTabPane.addPanel(i + "", cmdRs.getTitle(), lastPanel.getComp());
                    }
                } else {
                    TextAreaInput textArea = new TextAreaInput();
                    textArea.setEnabled(false);
                    String title = cmdRs.getTitle();
                    if ("\\?".equals(sql)) {
                        title = QuickQueryMgr.getLang("useHelp");
                        textArea.setValue(QuickQueryMgr.getLang("help"));
                    } else {
                        textArea.setValue(String.format(QuickQueryMgr.getLang("notSupport"), sql));
                    }
                    hTabPane.addPanel(i + "", title, textArea.getComp());
                }
            }
            set(hTabPane.getComp());
        } catch (Exception e) {
            TextAreaInput textArea = new TextAreaInput();
            textArea.setEnabled(false);
            textArea.setValue(sql + QuickQueryMgr.getLang("exception"));
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
        typeBox.addOption(QuickQueryMgr.getLang("transverse"), "transverse");
        typeBox.addOption(QuickQueryMgr.getLang("vertical"), "vertical");
        SearchToolBar stb = new SearchToolBar(tab);
        stb.add(typeBox);
        lastPanel.setHead(stb.getComp());
        lastPanel.setWithScroll(tab.getComp());
        tab.load(data, 1);
        return lastPanel;
    }
}
