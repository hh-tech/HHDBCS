package com.hh.hhdb_admin.mgr.table.comp;

import com.hh.frame.create_dbobj.table.CreateTableTool;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.hhdb_admin.mgr.table.TableComp;
import com.hh.hhdb_admin.mgr.table.column.CheckColumn;
import com.hh.hhdb_admin.mgr.table.common.TableCreatePanel;

/**
 * @author oyx
 * @Description: 约束管理
 * @date 2020-10-14  0014 16:26:27
 */
public class CheckPanel extends TableCreatePanel {


	public CheckPanel() {
		this.table = initTable();
		initButtons();
		initBar();
	}


	@Override
	public void build(CreateTableTool createTableTool) {
	}

	public HTable initTable() {
		HTable table = new HTable();
		CheckColumn checkColumn = new CheckColumn("checkJson", TableComp.getLang("constraintExpression"), TableComp.getLang("ConstraintMgr"));
		table.addCols(checkColumn);
		table.setRowHeight(27);
		table.hideSeqCol();
		return table;
	}

	private void initButtons() {
		addButton = createAddButton(TableComp.getLang("addConstraint"));
		addButton.addActionListener(new AddListener(table, this));
		delButton = createDelButton(TableComp.getLang("deleteConstraint"), table);
	}
}
