package com.hh.hhdb_admin.mgr.quick_query.ui;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 输出结果选项卡面板组件
 *
 * @author hexu
 */
public class OutputTabPanel {
    private final HTabPanel hTabPane;
    private final JdbcBean jdbc;
    //保存查询结果集信息
    private final Map<String, Integer> resultMap;


    public OutputTabPanel(JdbcBean jdbc) {
        this.jdbc = jdbc;
        resultMap = new LinkedHashMap<>();
        hTabPane = new HTabPanel();
    }

    public HTabPanel getTabPane() {
        return hTabPane;
    }

    public void showRs(Map<Integer, String> map, int maxRow, int maxSet) {
        int number = 1;
        for (Integer i : map.keySet()) {
            DataTab dataTab = new DataTab(jdbc);
            dataTab.showTable(map.get(i), maxRow, maxSet);

            resultMap.put(LangMgr.lang(Word2.RESULT) + number, i);
            hTabPane.setPanel(number + "", dataTab,DataTab.newHeaderConfig(LangMgr.lang(Word2.RESULT) + number));
            number++;
        }
        hTabPane.selectPanel(resultMap.size() + "");
    }
}
