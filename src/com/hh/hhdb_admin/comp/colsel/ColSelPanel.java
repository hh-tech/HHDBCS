package com.hh.hhdb_admin.comp.colsel;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.db_sql.conn.TabConnUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.RowStatus;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.bool.BoolCol;
import com.hh.hhdb_admin.common.CsL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * 表字段选择Panel组件
 *
 * @author Yangxianhui
 */
public class ColSelPanel extends LastPanel {
    private static final String COL_NAME = "col_name";
    private static final String SELECTED = "__selected__";
    private List<String> colNames;
    private HTable table;
    private BoolCol selected;
    private List<Map<String, String>> oldData;

    public ColSelPanel() {
        table = new HTable();
        selected = new BoolCol(SELECTED, LangMgr.sLang(CsL.SELECT_COLUMN));
        selected.setMaxWidth(100);
        table.addCols(new DataCol(COL_NAME, LangMgr.lang(Word2.COLUMN_NAME)), selected);
        table.setRowHeight(25);
        table.setRowPopMenu(new PkgSelMenu(this));


        PkgSelTableCheckBox box = new PkgSelTableCheckBox(table, selected);
        SearchToolBar searchToolBar = new SearchToolBar(table);
        HBarPanel barPanel = new HBarPanel(getLayout());

        barPanel.add(new HButton(LangMgr.lang(Word2.RESET)) {
            @Override
            protected void onClick() {

                if (table.getRowBeans(RowStatus.UPDATE).isEmpty()) {
                    return;
                }
                if (oldData != null) {
                    table.load(oldData, 1);
                }


            }
        });
        barPanel.add(box);
        barPanel.setLastHComp(searchToolBar);


        setHead(barPanel.getComp());
        setWithScroll(table.getComp());


    }


    /**
     * 加载数据
     *
     * @param jdbc
     * @param conn
     * @param schemaName       schema名称
     * @param tabName          表名称
     * @param selectedColNames 默认选中的列名,如果没有传null
     * @throws SQLException
     */
    public void loadData(JdbcBean jdbc, Connection conn, String schemaName, String tabName, Set<String> selectedColNames) throws Exception {
        DBTypeEnum type = DriverUtil.getDbType(jdbc);
        DBTypeEnum.Version v = jdbc.getCurrentVersion();
        colNames = TabConnUtil.getSelColNames(type, v, conn, schemaName, tabName);
        List<Map<String, String>> maps = new ArrayList<>();

        for (String col : colNames) {
            String colName = col.trim();
            Map<String, String> map = new HashMap<>();
            map.put(COL_NAME, colName);
            if (selectedColNames != null) {
                map.put(SELECTED, String.valueOf(selectedColNames.contains(colName)));
            } else {
                map.put(SELECTED, String.valueOf(Boolean.FALSE));
            }

            maps.add(map);
        }
        oldData = maps;
        table.load(maps, 1);
    }

    public HTable getTable() {
        return table;
    }


    public List<String> getSelected() {
        List<String> checkedNames = new ArrayList<>();
        List<HTabRowBean> beanList = table.getRowBeans(null);
        if (null != beanList) {
            for (HTabRowBean b : beanList) {
                if ((b.getCurrRow() != null && Boolean.valueOf(b.getCurrRow().getOrDefault(SELECTED, "false")))
                        || (b.getOldRow() != null && Boolean.valueOf(b.getOldRow().getOrDefault(SELECTED, "false")))
                ) {
                    checkedNames.add(b.getOldRow().get(COL_NAME) == null ? b.getCurrRow().get(COL_NAME) : b.getOldRow().get(COL_NAME));
                }
            }
        }
        return checkedNames;
    }


    void addSelection(int index0, int index1) {
        int col = getRealColIndex();
        if (col == -1) {
            return;
        }
        for (int i = index0; i <= index1; i++) {
            table.getComp().setValueAt(true, i, col);
        }
        table.getComp().updateUI();
    }

    void cancelSelection(int index0, int index1) {
        int col = getRealColIndex();
        if (col == -1) {
            return;
        }
        for (int i = index0; i <= index1; i++) {
            table.getComp().setValueAt(false, i, col);
        }
        table.getComp().updateUI();
    }


    List<HTabRowBean> getSelectionBeans() {
        List<HTabRowBean> list = this.table.getRowBeans(null);
        List<HTabRowBean> ret = new ArrayList<HTabRowBean>();
        for (HTabRowBean r : list) {
            if (r.getStatus() == RowStatus.DEL) {
                continue;
            } else if (isSelected(r)) {
                ret.add(r);
            }
        }
        return ret;
    }

    private boolean isSelected(HTabRowBean r) {
        if (r.getStatus() == RowStatus.ADD) {
            String value = r.getCurrRow().get(selected.getName());
            return Boolean.valueOf(value);
        } else if (r.getStatus() == RowStatus.OLD) {
            String value = r.getOldRow().get(selected.getName());
            return Boolean.valueOf(value);
        } else if (r.getStatus() == RowStatus.UPDATE) {
            if (r.getCurrRow().containsKey(selected.getName())) {
                return Boolean.valueOf(r.getCurrRow().get(selected.getName()));
            } else {
                return Boolean.valueOf(r.getOldRow().get(selected.getName()));
            }

        }

        return false;
    }


    private int getRealColIndex() {
        if (!selected.isShow()) {
            return -1;
        }
        int index = selected.getColIndex();
        int colCount = table.getComp().getColumnCount();
        for (int i = 0; i < colCount; i++) {
            String name = table.getComp().getColumnName(i);
            if (name.equals(selected.getName())) {
                index = i;
            }
        }
        return index;
    }

    private HBarLayout getLayout() {
        HBarLayout barLayout = HBarLayout.newLayout(AlignEnum.RIGHT);
        barLayout.setTopHeight(5);
        barLayout.setBottomHeight(5);
        return barLayout;
    }

    public List<String> getColNames() {
        return colNames;
    }
}
