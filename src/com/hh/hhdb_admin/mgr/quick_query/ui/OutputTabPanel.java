package com.hh.hhdb_admin.mgr.quick_query.ui;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.swingui.view.container.HTabPane;
import com.hh.hhdb_admin.mgr.quick_query.QuickQueryMgr;

import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 输出结果选项卡面板组件
 *
 * @author hexu
 */
public class OutputTabPanel {
    private final HTabPane hTabPane;
    private final JdbcBean jdbc;
    //保存查询结果集信息
    private final Map<String, Integer> resultMap;


    public OutputTabPanel(JdbcBean jdbc) {
        this.jdbc = jdbc;
        resultMap = new LinkedHashMap<>();
        hTabPane = new HTabPane();
        // table页切换事件
        ((JTabbedPane) hTabPane.getComp()).addChangeListener(e -> highlighted());
    }

    public HTabPane getTabPane() {
        return hTabPane;
    }

    public void showRs(Map<Integer, String> map, int maxRow, int maxSet) {
        int number = 1;
        for (Integer i : map.keySet()) {
            DataTab dataTab = new DataTab(jdbc);
            dataTab.showTable(map.get(i), maxRow, maxSet);

            resultMap.put(QuickQueryMgr.getLang("result") + number, i);
            hTabPane.addPanel(number + "", QuickQueryMgr.getLang("result") + number, dataTab.getComp(), false);
            number++;
        }
        hTabPane.selectPanel(resultMap.size() + "");
    }


    /**
     * 高亮显示对应行sql
     */
    protected void highlighted() {
    }
}
