package com.hh.hhdb_admin.mgr.table_open.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HImgButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;

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
		super(newBarPan(AlignEnum.LEFT));
		init();
	}
	private static HBarLayout newBarPan(AlignEnum alignEnum) {
		HBarLayout hBarLayout = new HBarLayout();
		hBarLayout.setAlign(alignEnum);
		hBarLayout.setTopHeight(5);
		hBarLayout.setBottomHeight(5);
		return hBarLayout;
	}
	
	private void init() {
		addRow = new HImgButton();
		addRow.setMouseExitedIcon(FaSolidEnum.PLUS,16);
		addRow.setMouseEnteredIcon(FaSolidEnum.PLUS,16);

		delRow = new HImgButton();
		delRow.setMouseExitedIcon(FaSolidEnum.MINUS,16);
		delRow.setMouseEnteredIcon(FaSolidEnum.MINUS,16);
		saveData = new HImgButton();
		saveData.setMouseExitedIcon(FaSolidEnum.CHECK,16);
		saveData.setMouseEnteredIcon(FaSolidEnum.CHECK,16);

		cancel = new HImgButton();
		cancel.setMouseExitedIcon(FaSolidEnum.XMARK,16);
		cancel.setMouseEnteredIcon(FaSolidEnum.XMARK,16);


		refreshData = new HImgButton();
		refreshData.setMouseExitedIcon(FaSolidEnum.ROTATE_LEFT,16);
		refreshData.setMouseEnteredIcon(FaSolidEnum.ROTATE_LEFT,16);

		sqlView = new HImgButton();
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
