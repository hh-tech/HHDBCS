package com.hh.hhdb_admin.mgr.table2.panel;

import com.hh.frame.create_dbobj.table2.CreateNewTabFk;
import com.hh.frame.create_dbobj.table2.CreateNewTabTool;
import com.hh.frame.create_dbobj.table2.db.AbsCreateNewTab;
import com.hh.frame.json.Json;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.ListCol;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.mgr.table2.TableUtil;
import com.hh.hhdb_admin.mgr.table2.column.FKColumn;
import com.hh.hhdb_admin.mgr.table2.column.SelectColumn;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class FKPanel extends AbsCodePanel {
	private static final String COL_NAME = "colName";
	private static final String FOREIGN_TABLE_COL_NAME = "foreignTableColName";
	private static final String FOREIGN_ON_DELETE = "foreignOnDelete";
	private static final String FOREIGN_ON_UPDATE = "foreignOnUpdate";

	
	public FKPanel(HTable colTable, AbsCreateNewTab newTab) {
		table = new HTable();
		table.addCols(new SelectColumn(COL_NAME, LangMgr.lang(Word1.COLUMN), colTable));
		table.addCols(new FKColumn(FOREIGN_TABLE_COL_NAME, LangMgr.lang(Word2.REFLEVANCE)));
		
		if (newTab.getOnUpdateArray().length > 0) {
			table.addCols(new ListCol(FOREIGN_ON_UPDATE, LangMgr.sLang(CsL.ON_UPDATING), Arrays.asList(newTab.getOnUpdateArray())));
		}
		if (newTab.getOnDelArray().length > 0) {
			table.addCols(new ListCol(FOREIGN_ON_DELETE, LangMgr.sLang(CsL.ON_DELETION), Arrays.asList(newTab.getOnDelArray())));
		}
		table.setRowHeight(30);
		table.hideSeqCol();
		table.setEvenBgColor(table.getOddBgColor());
		
		createAddButton(LangMgr.sLang(CsL.ADD_FK));
		createDelButton(LangMgr.sLang(CsL.DELETE_FK));
		initBar();
	}
	
	@Override
	public void build(CreateNewTabTool tabTool) {
		TableUtil.stopEditing(table);
		List<CreateNewTabFk> fks = new ArrayList<>();
		List<HTabRowBean> row = null != table.getRowBeans(null) ? table.getRowBeans(null) : new LinkedList<>();
		for (HTabRowBean be : row) {
			String colNames = TableUtil.getTableCol(be,COL_NAME);
			String foreignKeys = TableUtil.getTableCol(be,FOREIGN_TABLE_COL_NAME);
			String onUpdate = TableUtil.getTableCol(be,FOREIGN_ON_UPDATE);
			String onDelete = TableUtil.getTableCol(be,FOREIGN_ON_DELETE);
			if (StringUtils.isBlank(colNames) || StringUtils.isBlank(foreignKeys)) {
				continue;
			}
			colNames = Json.parse(colNames).asObject().getString(JsonCol.__TEXT);
			foreignKeys = Json.parse(foreignKeys).asObject().getString(JsonCol.__TEXT);
			
			String refTabFullName = StringUtils.substringBefore(foreignKeys, "(");
			List<String> fColNames = Arrays.asList(StringUtils.split(StringUtils.substringBetween(foreignKeys, "(", ")"), ","));
			CreateNewTabFk fk = new CreateNewTabFk(Arrays.stream(colNames.split(",")).collect(Collectors.toList()),refTabFullName,fColNames);
			if (StringUtils.isNotBlank(onUpdate)) {
				fk.setOnUpdate(onUpdate);
			}
			if (StringUtils.isNotBlank(onDelete)) {
				fk.setOnDelete(onDelete);
			}
			fks.add(fk);
		}
		tabTool.setFks(fks);
	}
}
