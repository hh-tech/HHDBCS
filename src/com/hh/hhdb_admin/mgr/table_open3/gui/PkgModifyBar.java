package com.hh.hhdb_admin.mgr.table_open3.gui;

import java.util.HashMap;
import java.util.List;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HImgButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.comp.SqlViewDialog;

/**
 * 数据操作
 */
class PkgModifyBar extends HBarPanel {
	// 增加数据
	public HImgButton addRow;
	// 删除数据
	public HImgButton delRow;
	// 保存数据
	public HImgButton saveData;
	//取消更改
	public HImgButton cancel;
	// 刷新数据
	public HImgButton refreshData;
	//预览sql
	private HImgButton sqlView;
	
	
	private SqlViewDialog dialog;

	private OpenTableComp opc;
	public PkgModifyBar(OpenTableComp opc) {
		super(getBarLayout(AlignEnum.LEFT));
		this.opc = opc;
		init();
		this.dialog = new SqlViewDialog(opc.getComp());
	}

	
	public void setCompEnabled(boolean enabled) {
		List<AbsHComp> abs = getAllHComp();
		for(AbsHComp c:abs) {
			if(c instanceof HImgButton) {
				HImgButton hib = (HImgButton )c;
				hib.setEnabled(enabled);
			}
		}
	}
	
	
	public void refresh() {
		boolean change = this.opc.oTable.isDataChanged();
		boolean isRowStyle = this.opc.oTable.isRowStyle();
		List<HTabRowBean> selRows = this.opc.oTable.getSelectedRowBeans();
		delRow.setEnabled(!selRows.isEmpty());
		if(change) {
			setCompEnabled(true);
			addRow.setEnabled(isRowStyle);
			return;
		}else {
			setCompEnabled(false);
			delRow.setEnabled(!selRows.isEmpty());
			refreshData.setEnabled(true);
		}
		addRow.setEnabled(isRowStyle);
	}
	
	
	private void init() {
		addRow = new HImgButton() {
			@Override
			protected void onClick() {
				opc.oTable.stopEdit();
				opc.oTable.add(opc.oTable.getRowCount(), new HashMap<>());
				refresh();
			}
		};
		addRow.setMouseExitedIcon(FaSolidEnum.PLUS,16);
		addRow.setMouseEnteredIcon(FaSolidEnum.PLUS,16);

		delRow = new HImgButton() {
			@Override
			protected void onClick() {
				 delRow();
			}
		};
		delRow.setMouseExitedIcon(FaSolidEnum.MINUS,16);
		delRow.setMouseEnteredIcon(FaSolidEnum.MINUS,16);
		
		
		saveData = new HImgButton() {
			@Override
			protected void onClick() {
				opc.saveData();
			}
		};
		saveData.setMouseExitedIcon(FaSolidEnum.CHECK,16);
		saveData.setMouseEnteredIcon(FaSolidEnum.CHECK,16);

		cancel = new HImgButton() {
			@Override
			protected void onClick() {
				opc.oTable.reloadOldData();
				refresh();
			}
		};
		cancel.setMouseExitedIcon(FaSolidEnum.XMARK,16);
		cancel.setMouseEnteredIcon(FaSolidEnum.XMARK,16);


		refreshData = new HImgButton() {
			@Override
			protected void onClick() {
				if(opc.oTable!=null&&!opc.oTable.isDataChanged()) {
					opc.loadData();
				}else {
					if(opc.isCancelSaveData()) {
						opc.oTable.reloadOldData();
						refresh();
					}
				}
				
			}
		};
		refreshData.setMouseExitedIcon(FaSolidEnum.ROTATE_LEFT,16);
		refreshData.setMouseEnteredIcon(FaSolidEnum.ROTATE_LEFT,16);

		sqlView = new HImgButton() {
			@Override
			protected void onClick() {
				dialog.setSql(opc.genSql());
				dialog.show();
			}
		};
		sqlView.setMouseExitedIcon(FaSolidEnum.MAGNIFYING_GLASS,16);
		sqlView.setMouseEnteredIcon(FaSolidEnum.MAGNIFYING_GLASS,16);

		//获取不到多语言 ToolTipText暂时写死
		addRow.getComp().setToolTipText(LangMgr.lang(Word2.ADD));
		delRow.setToolTipText(LangMgr.lang(Word2.DELETE));
		saveData.setToolTipText(LangMgr.lang(Word4.SUBMIT_MODIFICATION));
		cancel.setToolTipText(LangMgr.lang(Word2.CANCEL));
		sqlView.setToolTipText(LangMgr.lang(Word2.PREVIEW));
		refreshData.setToolTipText(LangMgr.lang(Word2.REFRESH));

		add(addRow, delRow, saveData, cancel, sqlView, refreshData);
	}

	
	
	protected void delRow() {
		try {
			PkgOpenTable tab = opc.oTable;
			if (tab.getComp().isEditing()) {
				tab.getComp().getCellEditor().stopCellEditing();
			}
			int select = tab.getComp().getSelectedRows().length;
			if (select > 0) {
				if (PopDlgUtil.confirm(String.format(LangMgr.sLang(CsL.CONFIRM_DELETION), select))) {
					tab.deleteSelectRow();
					refresh();
				}
			} else {
				PopDlgUtil.info(LangMgr.sLang(CsL.SELECT_DELETION_RECORD));
			}
		} catch (Exception exception) {
			Lg.error(PkgModifyBar.class, exception);
			PopDlgUtil.error(exception);
			refresh();
		}
	}
	
	
	private static HBarLayout getBarLayout(AlignEnum alignEnum) {
		HBarLayout hBarLayout = new HBarLayout();
		hBarLayout.setAlign(alignEnum);
		hBarLayout.setTopHeight(5);
		hBarLayout.setBottomHeight(5);
		return hBarLayout;
	}

	
	
	

	
	
}

