package com.hhdb.csadmin.plugin.table_open.ui;

import javax.swing.JToolBar;

import com.hhdb.csadmin.common.util.IconUtilities;

/**
 * 操作表格数据面板
 * 
 * @author hw
 * 
 */
public class HHOperateTablePanel extends JToolBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 增加数据
	public BaseButton addRow;
	// 删除数据
	public BaseButton delRow;
	// 保存数据
	public BaseButton saveData;
	// 刷新数据
	public BaseButton refreshData;

	public HHOperateTablePanel() {
		init();
	}

	private void init() {
		this.setFloatable(false);
		addRow = new BaseButton(IconUtilities.loadIcon("adddata.png"));
		delRow = new BaseButton(IconUtilities.loadIcon("deldata.png"));
		saveData = new BaseButton(IconUtilities.loadIcon("usedata.png"));
		refreshData = new BaseButton(IconUtilities.loadIcon("tabreflash.png"));
		
		addRow.setToolTipText("添加一行");
		delRow.setToolTipText("删除");
		saveData.setToolTipText("保存");
		refreshData.setToolTipText("刷新");

		this.add(addRow);
		this.add(delRow);
		this.add(saveData);
		this.add(refreshData);
		
	}

	public BaseButton getAddRow() {
		return addRow;
	}

	public void setAddRow(BaseButton addRow) {
		this.addRow = addRow;
	}

	public BaseButton getDelRow() {
		return delRow;
	}

	public void setDelRow(BaseButton delRow) {
		this.delRow = delRow;
	}

	public BaseButton getSaveData() {
		return saveData;
	}

	public void setSaveData(BaseButton saveData) {
		this.saveData = saveData;
	}

	public BaseButton getRefreshData() {
		return refreshData;
	}

	public void setRefreshData(BaseButton refreshData) {
		this.refreshData = refreshData;
	}

}
