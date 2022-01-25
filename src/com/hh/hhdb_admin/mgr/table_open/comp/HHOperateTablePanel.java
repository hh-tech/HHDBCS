package com.hh.hhdb_admin.mgr.table_open.comp;

import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HImgButton;
import com.hh.hhdb_admin.mgr.table.common.TableUtil;
import com.hh.hhdb_admin.mgr.table_open.common.ModifyConstant;
import com.hh.hhdb_admin.mgr.table_open.common.ModifyTabTool;

import java.util.Arrays;
import java.util.List;

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

	private List<AbsHComp> compList;

	public HHOperateTablePanel() {
		super(TableUtil.newBarPan());
		init();
	}

	private void init() {
		addRow = new HImgButton();
		addRow.setMouseExitedIconIcon(ModifyTabTool.getIcon(ModifyConstant.ADD_ICON));
		addRow.setMouseEnteredIcon(ModifyTabTool.getIcon(ModifyConstant.ADD_ICON));

		delRow = new HImgButton();
		delRow.setMouseExitedIconIcon(ModifyTabTool.getIcon(ModifyConstant.DEL_ICON));
		delRow.setMouseEnteredIcon(ModifyTabTool.getIcon(ModifyConstant.DEL_ICON));
		saveData = new HImgButton();
		saveData.setMouseExitedIconIcon(ModifyTabTool.getIcon(ModifyConstant.SUBMIT_ICON));
		saveData.setMouseEnteredIcon(ModifyTabTool.getIcon(ModifyConstant.SUBMIT_ICON));

		cancel = new HImgButton();
		cancel.setMouseExitedIconIcon(ModifyTabTool.getIcon(ModifyConstant.CANCEL_ICON));
		cancel.setMouseEnteredIcon(ModifyTabTool.getIcon(ModifyConstant.CANCEL_ICON));


		refreshData = new HImgButton();
		refreshData.setMouseExitedIconIcon(ModifyTabTool.getIcon(ModifyConstant.REFRESH_ICON));
		refreshData.setMouseEnteredIcon(ModifyTabTool.getIcon(ModifyConstant.REFRESH_ICON));

		sqlView = new HImgButton();
		sqlView.setMouseExitedIconIcon(ModifyTabTool.getIcon(ModifyConstant.SQL_VIEW_ICON));
		sqlView.setMouseEnteredIcon(ModifyTabTool.getIcon(ModifyConstant.SQL_VIEW_ICON));

		//获取不到多语言 ToolTipText暂时写死
		addRow.getComp().setToolTipText("添加一行");
		delRow.setToolTipText("删除选中行");
		saveData.setToolTipText("提交修改");
		cancel.setToolTipText("放弃修改");
		sqlView.setToolTipText("预览SQL");
		refreshData.setToolTipText("刷新");

		add(addRow, delRow, saveData, cancel, sqlView, refreshData);
		compList = Arrays.asList(addRow, delRow, saveData, cancel, sqlView, refreshData);
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

	public List<AbsHComp> getCompList() {
		return compList;
	}
}
