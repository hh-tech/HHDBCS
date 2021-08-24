package com.hh.hhdb_admin.mgr.table.comp;

import com.hh.frame.create_dbobj.table.CreateTableTool;
import com.hh.frame.create_dbobj.table.base.impl.CreateUk;
import com.hh.frame.create_dbobj.table.base.impl.DefCreateCol;
import com.hh.frame.create_dbobj.table.base.type.AbsCreateCol;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonArray;
import com.hh.frame.json.JsonValue;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.HTipTable;
import com.hh.hhdb_admin.mgr.table.TableComp;
import com.hh.hhdb_admin.mgr.table.column.SelectColumn;
import com.hh.hhdb_admin.mgr.table.common.TableCreatePanel;
import org.apache.commons.lang3.StringUtils;

import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author oyx
 * @Description: 唯一键管理
 * @date 2020-9-25 14:28:41
 */
public class UniquePanel extends TableCreatePanel {
	public static final String UNIQUE_COL_NAME = "uniqueColName";
	private final ColumnPanel columnPanel;

	public UniquePanel(  ColumnPanel columnPanel) {
//		super.baseComp = baseComp;
		this.columnPanel = columnPanel;
		this.table = initTable();
		initButtons();
		initBar();

	}


	public HTable initTable() {
		HTable table = new HTipTable();
		table.setNullSymbol("");
		SelectColumn uniqueColumn = new SelectColumn(UNIQUE_COL_NAME,
				TableComp.getLang("column"), table, TableComp.getLang("selectColumn"), columnPanel.getTable());
		table.addCols(uniqueColumn);
		table.setRowHeight(30);
		table.hideSeqCol();
		table.setEvenBgColor(table.getOddBgColor());
		return table;
	}

	private void initButtons() {
		addButton = createAddButton(TableComp.getLang("addUniqueKey"));
		addButton.addActionListener(new AddListener(table, this));
		delButton = createDelButton(TableComp.getLang("deleteUniqueKey"), table);
	}


	@Override
	public void build(CreateTableTool createTableTool) throws Exception {
		int rowCount = table.getComp().getRowCount();
		TableModel model = table.getComp().getModel();

		List<AbsCreateCol> cloList = new ArrayList<>();
		for (int i = 0; i < rowCount; i++) {
			Object nameObj = model.getValueAt(i, 0);
			if (nameObj != null && StringUtils.isNoneBlank(nameObj.toString().trim())) {
				Object valueAt = model.getValueAt(i, 0);
				if (valueAt != null) {
					JsonValue jsonValue = Json.parse(valueAt.toString()).asObject().get(SelectColumn.COL_NAMES);
					if (jsonValue == null) {
						continue;
					}
					JsonArray array = jsonValue.asArray();
					for (int j = 0; j < array.size(); j++) {
						DefCreateCol createCol = new DefCreateCol(array.get(j).asString());
						cloList.add(createCol);
					}
				}
			}
		}
		if (cloList.size() > 0) {
			createTableTool.addKey(new CreateUk(cloList));
		}
	}

}
