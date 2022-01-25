package com.hh.hhdb_admin.mgr.quick_query.ui;

import java.util.LinkedHashMap;
import java.util.Map;
import com.hh.frame.common.base.JdbcBean;
import com.hh.hhdb_admin.common.csTabPanel.CSTablePanel;
import com.hh.hhdb_admin.mgr.quick_query.QuickQueryMgr;

/**
 * 输出结果选项卡面板组件
 *
 * @author hexu
 */
public class OutputTabPanel {
    private final CSTablePanel hTabPane;
    private final JdbcBean jdbc;
    //保存查询结果集信息
    private final Map<String, Integer> resultMap;


    public OutputTabPanel(JdbcBean jdbc) {
        this.jdbc = jdbc;
        resultMap = new LinkedHashMap<>();
        hTabPane = new CSTablePanel() {

			@Override
			public void selectPanel(String id) {
				highlighted();
			}
        	
        };
        // table页切换事件
//        ((JTabbedPane) hTabPane.getComp()).addChangeListener(e -> highlighted());
    }

    public CSTablePanel getTabPane() {
        return hTabPane;
    }

    public void showRs(Map<Integer, String> map, int maxRow, int maxSet) {
        int number = 1;
        for (Integer i : map.keySet()) {
            DataTab dataTab = new DataTab(jdbc);
            dataTab.showTable(map.get(i), maxRow, maxSet);

            resultMap.put(QuickQueryMgr.getLang("result") + number, i);
            hTabPane.addPanel(number + "", QuickQueryMgr.getLang("result") + number, dataTab.getComp());
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
