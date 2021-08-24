package com.hh.hhdb_admin.mgr.table_open.ui;

import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HImgButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.hhdb_admin.mgr.table_open.common.ModifyTabDataUtil;

import javax.swing.*;

/**
 * 翻页按钮栏
 *
 * @author hhxd
 */
public class HHPagePanel extends HBarPanel {
	// 前一页
	private HImgButton prePage;
	// 当前页数
	private TextInput curPageNum;
	// 下一页
	private HImgButton nextPage;
//	// 总页数显示
//	private LabelInput totalPageNum;
//	//总页码数记录
//	private int pageCount;


	public HHPagePanel() {
//		super(TableUtil.newBarPan(AlignEnum.RIGHT));
//		super(AlignEnum.RIGHT, new BorderSpace(5, 0, 0, 5), false);
		init();
	}


	private void init() {
		prePage = new HImgButton();
		prePage.setMouseExitedIconIcon(ModifyTabDataUtil.getIcon(ModifyTabDataUtil.PREVIOUS_PAGE_ICON));
		prePage.setMouseEnteredIcon(ModifyTabDataUtil.getIcon(ModifyTabDataUtil.PREVIOUS_PAGE_ICON));
		prePage.setToolTipText("上一页");

		curPageNum = new TextInput("1", "1");
		((JTextField) curPageNum.getComp()).setColumns(4);
		((JTextField) curPageNum.getComp()).setHorizontalAlignment(JTextField.CENTER);

		nextPage = new HImgButton();
		nextPage.setMouseExitedIconIcon(ModifyTabDataUtil.getIcon(ModifyTabDataUtil.NEXT_PAGE_ICON));
		nextPage.setMouseEnteredIcon(ModifyTabDataUtil.getIcon(ModifyTabDataUtil.NEXT_PAGE_ICON));
		nextPage.setToolTipText("下一页");

//		totalPageNum = new LabelInput("");

		//获取不到多语言 LabelInput暂时写死
		add(prePage);
		add(new LabelInput("第"));
		add(curPageNum);
		add(new LabelInput("页"));
		add(nextPage);
//		add(new LabelInput("(" + "共"), totalPageNum, new LabelInput("页" + ")"));

		prePage.setEnabled(false);
	}

	/***前一页*/
	public HImgButton getPrePage() {
		return prePage;
	}

	/***当前页数*/
	public TextInput getCurPageNum() {
		return curPageNum;
	}

	/***下一页*/
	public HImgButton getNextPage() {
		return nextPage;
	}

	//	/***总页数*/
//	public int getPageCount() {
//		return pageCount;
//	}
//
//	public void setPageCount(int pageCount) {
//		this.pageCount = pageCount;
//	}
//
//	/***总页数显示*/
//	public LabelInput getTotalPageNum() {
//		return totalPageNum;
//	}
}
