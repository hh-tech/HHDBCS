package com.hh.hhdb_admin.mgr.table_open.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HImgButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.hhdb_admin.common.CsL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

/**
 * 翻页按钮栏
 *
 * @author hhxd
 */
public class HHPagePanel extends HBarPanel {
	private LabelInput statusLabel;
	//第一页
	private HImgButton firstPage;
	//上一页
	private HImgButton prePage;
	//当前页数
	private TextInput curPageNum;
	//设置页大小
	private HImgButton pageSizeBut;
	//下一页
	private HImgButton nextPage;
	//最后一页
	private HImgButton lastPage;
	//获取总行数
	private HImgButton totalPageBtn;
	//总行数显示label
	private LabelInput pageTotalLabel;
//	//总页码数记录
//	private int pageCount;
	//总行数加载进度条
	private ProgressBar progressBar;

	private long pageSizeNum;

	private final int curPageNumColumns = 5;

	private List<AbsHComp> compList;

	public HHPagePanel(long pageSizeNum) {
		this.pageSizeNum = pageSizeNum;
		init();
	}

	private void init() {
		statusLabel = new LabelInput();

		totalPageBtn = new HImgButton();
		totalPageBtn.setMouseExitedIcon(FaSolidEnum.MAGNIFYING_GLASS_CHART,16);
		totalPageBtn.setMouseEnteredIcon(FaSolidEnum.MAGNIFYING_GLASS_CHART,16);
		totalPageBtn.setToolTipText(LangMgr.sLang(CsL.GET_HEAD_OFFICE));
		progressBar = new ProgressBar();
		progressBar.getComp().setPreferredSize(new Dimension(80, progressBar.getComp().getPreferredSize().height));
		progressBar.getComp().setVisible(false);
		
		pageTotalLabel = new LabelInput(pageSizeNum + "+", AlignEnum.LEFT);

		firstPage = new HImgButton();
		firstPage.setMouseExitedIcon(FaSolidEnum.ANGLES_LEFT,16);
		firstPage.setMouseEnteredIcon(FaSolidEnum.ANGLES_LEFT,16);
		firstPage.setToolTipText(LangMgr.lang(Word3.FIRST_PAGE));

		prePage = new HImgButton();
		prePage.setMouseExitedIcon(FaSolidEnum.ANGLE_LEFT,16);
		prePage.setMouseEnteredIcon(FaSolidEnum.ANGLE_LEFT,16);
		prePage.setToolTipText(LangMgr.lang(Word3.PREVIOUS));

		curPageNum = new TextInput("1", "1") {
			@Override
			public void setValue(String value) {
				super.setValue(value);
				setCurPageNumTextLength(value.trim().length());
			}
		};
		curPageNum.setEnabled(false);
		curPageNum.getComp().setColumns(curPageNumColumns);
		curPageNum.getComp().setHorizontalAlignment(JTextField.CENTER);
		curPageNum.getComp().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				int length = curPageNum.getComp().getText().trim().length();
				setCurPageNumTextLength(length);
			}
		});
		//设置页大小按钮
        pageSizeBut=new HImgButton();
		pageSizeBut.setMouseExitedIcon(FaSolidEnum.SCREWDRIVER_WRENCH,16);
		pageSizeBut.setMouseEnteredIcon(FaSolidEnum.SCREWDRIVER_WRENCH,16);
		nextPage = new HImgButton();
		nextPage.setMouseExitedIcon(FaSolidEnum.ANGLE_RIGHT,16);
		nextPage.setMouseEnteredIcon(FaSolidEnum.ANGLE_RIGHT,16);
		nextPage.setToolTipText(LangMgr.lang(Word3.NEXT));

		lastPage = new HImgButton();
		lastPage.setMouseExitedIcon(FaSolidEnum.ANGLES_RIGHT,16);
		lastPage.setMouseEnteredIcon(FaSolidEnum.ANGLES_RIGHT,16);
		lastPage.setToolTipText(LangMgr.lang(Word4.LAST_PAGE));

		LabelInput label1 = new LabelInput(LangMgr.lang(Word1.FIRST));
		LabelInput label2 = new LabelInput(LangMgr.lang(Word1.PAGE));

		add(statusLabel, totalPageBtn, pageTotalLabel);
		add(firstPage, prePage);
		add(label1, curPageNum, label2);
		add(pageSizeBut, nextPage, lastPage);

		prePage.setEnabled(false);
		firstPage.setEnabled(false);

		compList = Arrays.asList(statusLabel, totalPageBtn, pageTotalLabel, firstPage, prePage, label1, curPageNum, label2, pageSizeBut, nextPage, lastPage);
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

	public HImgButton getFirstPage() {
		return firstPage;
	}

	public HImgButton getLastPage() {
		return lastPage;
	}

	public LabelInput getStatusLabel() {
		return statusLabel;
	}

	public HImgButton getPageSizeBut() {
		return pageSizeBut;
	}

	public HImgButton getTotalPageBtn() {
		return totalPageBtn;
	}

	public LabelInput getPageTotalLabel() {
		return pageTotalLabel;
	}

	private void setCurPageNumTextLength(int length) {
		if (length > curPageNumColumns && curPageNum.getComp().getColumns() == curPageNumColumns) {
			curPageNum.getComp().setColumns(7);
			curPageNum.getComp().updateUI();
			this.getComp().updateUI();
			curPageNum.getComp().setCaretPosition(length);
		}
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public List<AbsHComp> getCompList() {
		return compList;
	}
}
