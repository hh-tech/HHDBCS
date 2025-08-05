package com.hh.hhdb_admin.mgr.table2.panel;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.create_dbobj.table2.CreateNewTabTool;
import com.hh.frame.create_dbobj.table2.db.GreenPlumCreateNewTab;
import com.hh.frame.db_sql.greenplum.GreenPlumParSqlUtil;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;
import com.hh.frame.swingui.view.with_label.WithLabelComp;
import com.hh.frame.swingui.view.with_label.WithLabelInput;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.mgr.table2.TableUtil;
import com.hh.hhdb_admin.mgr.table2.column.SelectColumn;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GreenPlumPartitionPanel extends AbsCodePanel {

    private static final String PAR_NAME = "parName";
    private static final String UPPER_LIMIT = "upperLimit";
    private static final String LOWER_LIMIT = "lowerLimit";
    protected final TextInput intervalInput;
    protected final TextInput colNameInput;
    protected final SelectBox typeSelectBox;

    public GreenPlumPartitionPanel(HTable colTable) {
        table = new HTable();
        table.addCols(new DataCol(PAR_NAME, LangMgr.lang(Word4.PARTITION_NAME)));
        table.addCols(new DataCol(LOWER_LIMIT, LangMgr.lang(Word2.LOWER_LIMIT) + "/" + LangMgr.lang(Word1.VALUE)));
        table.addCols(new DataCol(UPPER_LIMIT, LangMgr.lang(Word2.UPPER_LIMIT) + "/" + LangMgr.lang(Word1.VALUE)));
        table.setRowHeight(30);
        table.hideSeqCol();
        table.setEvenBgColor(table.getOddBgColor());

        createAddButton(LangMgr.lang(Word4.ADD_PARTITION));
        createDelButton(LangMgr.lang(Word4.REMOVE_PARTITION));
        initBar();

        //选类型
        List<String> values = new ArrayList<>();
        values.add("");
        for (GreenPlumCreateNewTab.ParTabType value : GreenPlumCreateNewTab.ParTabType.values()) {
            values.add(value.name());
        }

        //选择列
        colNameInput = new TextInput();
        HButton selColBtn = new HButton(LangMgr.lang(Word3.SELECT_COLUMN));
        SelectColumn selColColumn = new SelectColumn("", "", colTable);
        selColColumn.setTab(table);
        selColBtn.addActionListener(e -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.set(JsonCol.__TEXT, getParColName());
            String colName = selColColumn.onClick(jsonObject, 0, 0).getString(JsonCol.__TEXT);
            colNameInput.setValue(colName);
        });
        HBarPanel parColPanel = new HBarPanel(HBarLayout.newLayout(AlignEnum.RIGHT));
        parColPanel.add(selColBtn);
        parColPanel.setLastHComp(colNameInput);

        //间隔
        intervalInput = new TextInput();
        intervalInput.setEnabled(false);
        CheckBoxInput IntervalCk = new CheckBoxInput("") {
            @Override
            protected void onSelected(boolean value) {
                intervalInput.setEnabled(value);
            }
        };
        IntervalCk.setEnabled(false);
        HBarPanel itvPanel = new HBarPanel(HBarLayout.newLayout(AlignEnum.RIGHT));
        itvPanel.add(IntervalCk);
        itvPanel.setLastHComp(intervalInput);

        typeSelectBox = new SelectBox(values) {
            @Override
            protected void onItemSelected(String value) {
                boolean isRange = GreenPlumCreateNewTab.ParTabType.RANGE.name().equals(value);
                intervalInput.setEnabled(isRange && IntervalCk.isChecked());
                IntervalCk.setEnabled(isRange);
            }
        };

        //基础设置
        HDivLayout layout = new HDivLayout(0, 5, GridSplitEnum.C12);
        int margin = 300;
        layout.setRightWidth(margin);
        layout.setLeftWidth(margin);
        HPanel inforPanel = new HPanel(layout);
        inforPanel.setTitle(LangMgr.sLang(CsL.BASIC_SETTING));
        inforPanel.add(new WithLabelInput(LangMgr.lang(Word2.TYPE) + ":", typeSelectBox));
        inforPanel.add(new WithLabelComp(LangMgr.lang(Word2.COLUMN_NAME) + ":", parColPanel));
        inforPanel.add(new WithLabelComp(LangMgr.lang(Word2.INTERVAL2) + ":", itvPanel));
        hPanel.add(inforPanel);
    }

    @Override
    public void build(CreateNewTabTool tabTool) {
        TableUtil.stopEditing(table);
        List<HTabRowBean> rows = null != table.getRowBeans(null) ? table.getRowBeans(null) : new LinkedList<>();

        String type = getParType();
        String colName = getParColName();
        if (StringUtils.isAnyBlank(type, colName))
            return;

        ArrayList<String> parList = new ArrayList<>();
        for (HTabRowBean row : rows) {
            String name = TableUtil.getTableCol(row, PAR_NAME);
            String value1 = TableUtil.getTableCol(row, LOWER_LIMIT);
            String value2 = TableUtil.getTableCol(row, UPPER_LIMIT);
            parList.add(GreenPlumParSqlUtil.getCreateParSql(type, name, value1, value2));
        }

        String interval = getInterval();

        GreenPlumCreateNewTab pgTab = (GreenPlumCreateNewTab) tabTool.getNewTab();
        pgTab.setParType(GreenPlumCreateNewTab.ParTabType.valueOf(type));
        pgTab.setParColName(colName);
        pgTab.setPartitions(parList);
        pgTab.setInterval(interval);
    }

    public String getParType() {
        return typeSelectBox.getValue();
    }

    public String getParColName() {
        return colNameInput.getValue();
    }

    public String getInterval() {
        if (intervalInput.isEnabled())
            return intervalInput.getValue();
        return "";
    }
}
