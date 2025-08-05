package com.hh.hhdb_admin.mgr.table2.column;

import com.hh.frame.json.JsonObject;
import com.hh.frame.json.JsonUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.bool.BoolCol;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.mgr.table2.panel.ColPanel;
import com.hh.hhdb_admin.mgr.table2.TableUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


public class SelectColumn extends JsonCol {
	public static final String COL_NAME = "name";
	public static final String COL_SELECT = "select";
	
	private HTable colTable;
	private HTable selectTable = new HTable();
	
	
    public SelectColumn(String name, String value, HTable colTable) {
        super(name, value);
        this.colTable = colTable;
	
		selectTable = new HTable();
		selectTable.addCols(new DataCol(COL_NAME, LangMgr.lang(Word2.COLUMN_NAME)), new BoolCol(COL_SELECT, LangMgr.lang(Word2.CHOOSE)));
		selectTable.setRowHeight(30);
		selectTable.hideSeqCol();
    }
    
    @Override
    public JsonObject onClick(JsonObject json, int row, int column) {
		JsonObject res = new JsonObject();
		String selCol = json != null ? json.getString(__TEXT) : null;
		Set<String> selectCol = new LinkedHashSet<>();
		Set<String> colNames = new LinkedHashSet<>();
		
        if (null != colTable.getRowBeans(null)) colTable.getRowBeans(null).forEach(a -> colNames.add(TableUtil.getTableCol(a,ColPanel.TAB_COL_NAME)));
        if (colNames == null || colNames.size() <= 0) {
            PopDlgUtil.info(colTable.getComp(), LangMgr.sLang(CsL.ADD_COLUMN_FIRST));
        } else {
			loadTable(colNames,selCol);
			HDialog dialog = new HDialog(getTab().getComp(), 500,400,true) {
				@Override
				protected void onConfirm() {
					for (HTabRowBean rowBean : selectTable.getRowBeans(null)) {
						if (Boolean.parseBoolean(TableUtil.getTableCol(rowBean,COL_SELECT))) {
							selectCol.add(TableUtil.getTableCol(rowBean,COL_NAME));
						}
					}
				}
				@Override
				protected void onCancel() {
					if (selCol != null) selectCol.addAll(Arrays.stream(selCol.split(",")).collect(Collectors.toCollection(LinkedHashSet::new)));
				}
			};
			LastPanel lastPanel = new LastPanel(false);
			lastPanel.setWithScroll(selectTable.getComp());
			dialog.setRootPanel(lastPanel);
			dialog.setWindowTitle(LangMgr.sLang(CsL.SELECT_COLUMN));
			dialog.show();
        	
            if (selectCol != null && selectCol.size() > 0) {
                res.add(JsonCol.__TEXT, StringUtils.join(selectCol, ","));
                res.add(COL_NAME, JsonUtil.parseArray(selectCol.toArray()));
            }
        }
        return res;
    }
	
	private void loadTable(Set<String> colNames,String colName) {
		List<Map<String, String>> data = new ArrayList<>();
		Set<String> selectNameSet = new HashSet<>();
		if (StringUtils.isNoneBlank(colName)) {
			selectNameSet = Arrays.stream(colName.split(",")).collect(Collectors.toSet());
		}
		for (String name : colNames) {
			Map<String, String> map = new HashMap<>();
			map.put(COL_NAME, name);
			map.put(COL_SELECT, String.valueOf(selectNameSet.contains(name)));
			data.add(map);
		}
		selectTable.load(data, 0);
	}
}
