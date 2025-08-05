package com.hh.hhdb_admin.mgr.table2.panel;


import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.create_dbobj.table2.CreateNewTabCol;
import com.hh.frame.create_dbobj.table2.CreateNewTabTool;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.bool.BoolCol;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.comp.column.DataTypePanel;
import com.hh.hhdb_admin.mgr.table2.TableUtil;
import com.hh.hhdb_admin.mgr.table2.column.TypeColumn;
import com.hh.hhdb_admin.mgr.table2.comp.TableComp;
import org.apache.commons.lang3.StringUtils;

import java.util.*;


public class ColPanel extends AbsCodePanel {
	public static final String TAB_COL_NAME = "colName";
	private static final String TAB_DATA_TYPE = "dataType";
	private static final String TAB_DEFAULT = "default";
	private static final String TAB_IS_NOT_NULL = "isNotNull";
	private static final String TAB_IS_PRIMARY_KEY = "isPrimaryKey";
	private static final String TAB_ANNOTATE = "annotate";
	
	public ColPanel() {
		initTable();
		createAddButton(LangMgr.sLang(CsL.ADD_COLUMN));
		createDelButton(LangMgr.sLang(CsL.DELETE_COLUMN));
		initBar();
	}

	public void addCol() {
		DataTypePanel typePanel = new DataTypePanel(TableComp.dbType,TableComp.ver);
        String colName = "column_" + (table.getRowCount() + 1);
        Map<String, String> map = new HashMap<>(6);
        JsonObject obj = new JsonObject();
        obj.set(JsonCol.__TEXT, typePanel.getFullTypeDefine());
        map.put(TAB_DATA_TYPE, obj.toString());
        map.put(TAB_COL_NAME, colName);
        map.put(TAB_DEFAULT, "");
        map.put(TAB_IS_NOT_NULL, "false");
        map.put(TAB_IS_PRIMARY_KEY, "false");
        map.put(TAB_ANNOTATE, "");
        table.add(map);
	}
    
    @Override
    public void build(CreateNewTabTool tabTool) {
        TableUtil.stopEditing(table);
        List<CreateNewTabCol> cols = new ArrayList<>();
        List<HTabRowBean> row =  null != table.getRowBeans(null) ? table.getRowBeans(null) : new LinkedList<>();
        for (HTabRowBean be : row) {
            String colName =  TableUtil.getTableCol(be,TAB_COL_NAME);
            if (StringUtils.isBlank(colName)) {
                continue;
            }
            String dataType = Json.parse(TableUtil.getTableCol(be,TAB_DATA_TYPE)).asObject().getString(JsonCol.__TEXT);
            String defaultValue = TableUtil.getTableCol(be,TAB_DEFAULT);
            boolean isNotNull = Boolean.parseBoolean(TableUtil.getTableCol(be,TAB_IS_NOT_NULL));
            boolean isPrimaryKey = Boolean.parseBoolean(TableUtil.getTableCol(be,TAB_IS_PRIMARY_KEY));
            String annotate = TableUtil.getTableCol(be,TAB_ANNOTATE);
            
            CreateNewTabCol tabCol=new CreateNewTabCol(colName,dataType);
            if (StringUtils.isNotBlank(defaultValue)) tabCol.setDefautValue(defaultValue);
            tabCol.setIsNull(!isNotNull);
            tabCol.setPriKey(isPrimaryKey);
            if (StringUtils.isNotBlank(annotate)) tabCol.setComment(annotate);
            cols.add(tabCol);
        }
        tabTool.getNewTab().setCols(cols);
    }
    
    private void initTable() {
        table = new HTable();
        table.addCols(new DataCol(TAB_COL_NAME, LangMgr.lang(Word2.COLUMN_NAME)));
        TypeColumn typeCol = new TypeColumn(TAB_DATA_TYPE, LangMgr.lang(Word2.TYPE));
        typeCol.setWidth(140);
        table.addCols(typeCol);
        if (TableComp.dbType != DBTypeEnum.hive) {
            if (TableComp.dbType != DBTypeEnum.databricks) table.addCols(new DataCol(TAB_DEFAULT, LangMgr.lang(Word3.DEFAULT_VALUE)));
            BoolCol notNullCol = new BoolCol(TAB_IS_NOT_NULL, LangMgr.lang(Word4.NONEMPTY_CONSTRAINT));
            notNullCol.setWidth(80);
            table.addCols(notNullCol);
            BoolCol primaryCol = new BoolCol(TAB_IS_PRIMARY_KEY, LangMgr.lang(Word2.PRIMARY_KEY));
            primaryCol.setWidth(60);
            if (TableComp.dbType != DBTypeEnum.clickhouse && TableComp.dbType != DBTypeEnum.gbase8a) table.addCols(primaryCol);
            if (TableComp.dbType != DBTypeEnum.informix && TableComp.dbType != DBTypeEnum.sqlite && TableComp.dbType != DBTypeEnum.derby) {
                table.addCols(new DataCol(TAB_ANNOTATE, LangMgr.lang(Word2.COMMENT)));
            }
        }
        
        table.setRowHeight(30);
        table.hideSeqCol();
        table.setEvenBgColor(table.getOddBgColor());
        table.setRowHeight(30);
    }
}
