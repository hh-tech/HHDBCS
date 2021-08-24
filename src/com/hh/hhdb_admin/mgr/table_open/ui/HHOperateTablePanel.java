package com.hh.hhdb_admin.mgr.table_open.ui;

import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HImgButton;
import com.hh.hhdb_admin.mgr.table.common.TableUtil;
import com.hh.hhdb_admin.mgr.table_open.common.ModifyTabDataUtil;

/**
 * 操作表格数据按钮面板
 *
 * @author hw
 */
public class HHOperateTablePanel extends HBarPanel {
	// 增加数据
	public HImgButton addRow;
	// 删除数据
	public HImgButton delRow;
	// 保存数据
	public HImgButton saveData;
	/**
	 * 放弃更改
	 */
	public HImgButton cancel;
	// 刷新数据
	public HImgButton refreshData;

	//预览sql
	private HImgButton sqlView;


	public HHOperateTablePanel() {
		super(TableUtil.newBarPan());
		init();
	}

	private void init() {
		addRow = new HImgButton();
		addRow.setMouseExitedIconIcon(ModifyTabDataUtil.getIcon(ModifyTabDataUtil.ADD_ICON));
		addRow.setMouseEnteredIcon(ModifyTabDataUtil.getIcon(ModifyTabDataUtil.ADD_ICON));

		delRow = new HImgButton();
		delRow.setMouseExitedIconIcon(ModifyTabDataUtil.getIcon(ModifyTabDataUtil.DEL_ICON));
		delRow.setMouseEnteredIcon(ModifyTabDataUtil.getIcon(ModifyTabDataUtil.DEL_ICON));
		saveData = new HImgButton();
		saveData.setMouseExitedIconIcon(ModifyTabDataUtil.getIcon(ModifyTabDataUtil.SUBMIT_ICON));
		saveData.setMouseEnteredIcon(ModifyTabDataUtil.getIcon(ModifyTabDataUtil.SUBMIT_ICON));

		cancel = new HImgButton();
		cancel.setMouseExitedIconIcon(ModifyTabDataUtil.getIcon(ModifyTabDataUtil.CANCEL_ICON));
		cancel.setMouseEnteredIcon(ModifyTabDataUtil.getIcon(ModifyTabDataUtil.CANCEL_ICON));


		refreshData = new HImgButton();
		refreshData.setMouseExitedIconIcon(ModifyTabDataUtil.getIcon(ModifyTabDataUtil.REFRESH_ICON));
		refreshData.setMouseEnteredIcon(ModifyTabDataUtil.getIcon(ModifyTabDataUtil.REFRESH_ICON));

		sqlView = new HImgButton();
		sqlView.setMouseExitedIconIcon(ModifyTabDataUtil.getIcon(ModifyTabDataUtil.SQL_VIEW_ICON));
		sqlView.setMouseEnteredIcon(ModifyTabDataUtil.getIcon(ModifyTabDataUtil.SQL_VIEW_ICON));

		//获取不到多语言 ToolTipText暂时写死
		addRow.setToolTipText("添加一行");
		delRow.setToolTipText("删除选中行");
		saveData.setToolTipText("提交修改");
		cancel.setToolTipText("放弃修改");
		sqlView.setToolTipText("预览SQL");
		refreshData.setToolTipText("刷新");

		add(addRow, delRow, saveData, cancel, sqlView, refreshData);
	}

	public HImgButton getAddRow() {
		return addRow;
	}

	public HImgButton getDelRow() {
		return delRow;
	}

	public HImgButton getSaveData() {
		return saveData;
	}

	public HImgButton getRefreshData() {
		return refreshData;
	}

	public HImgButton getSqlView() {
		return sqlView;
	}

	public HImgButton getCancel() {
		return cancel;
	}
}
