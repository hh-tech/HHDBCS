package com.hh.hhdb_admin.mgr.table_open.comp;

import com.alee.managers.style.StyleId;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.painter.PainterSupport;
import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HImgButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.tab.menu.sort.comparator.AlphaNumComparator;
import com.hh.hhdb_admin.mgr.table_open.common.ModifyConstant;
import com.hh.hhdb_admin.mgr.table_open.common.ModifyTabTool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 翻页按钮栏
 *
 * @author hhxd
 */
public class HHPagePanel extends HBarPanel {
	private LabelInput statusLabel;
	/**
	 * 第一页
	 */
	private HImgButton firstPage;

	/**
	 * 上一页
	 */
	private HImgButton prePage;

	/**
	 * 当前页数
	 */
	private TextInput curPageNum;

	private PageSelect pageSizeComp;
	/**
	 * 下一页
	 */
	private HImgButton nextPage;

	/**
	 * 最后一页
	 */
	private HImgButton lastPage;

	/**
	 * 获取总行数
	 */
	private HImgButton totalPageBtn;

	/**
	 * 总行数显示label
	 */
	private LabelInput pageTotalLabel;
//	//总页码数记录
//	private int pageCount;

	/**
	 * 总行数加载进度条
	 */
	private ProgressBar progressBar;


	private long pageSizeNum;

	private final int curPageNumColumns = 5;

	private List<AbsHComp> compList;

	public HHPagePanel(long pageSizeNum) {
//		super(TableUtil.newBarPan(AlignEnum.RIGHT));
//		super(AlignEnum.RIGHT, new BorderSpace(5, 0, 0, 5), false);
		this.pageSizeNum = pageSizeNum;
		init();
	}


	private void init() {

		statusLabel = new LabelInput();
		PainterSupport.setMargin(statusLabel.getComp(), 0, 10, 0, 15);

		totalPageBtn = new HImgButton();
		pageTotalLabel = new LabelInput(pageSizeNum + "+", AlignEnum.LEFT);
		totalPageBtn.setMouseExitedIconIcon(ModifyTabTool.getIcon(ModifyConstant.TOTAL_PAGE_ICON));
		totalPageBtn.setMouseEnteredIcon(ModifyTabTool.getIcon(ModifyConstant.TOTAL_PAGE_ICON));
		progressBar = new ProgressBar();
		progressBar.getComp().setPreferredSize(new Dimension(80, progressBar.getComp().getPreferredSize().height));
		progressBar.getComp().setVisible(false);

		TooltipManager.setTooltip(totalPageBtn.getComp(), "点击获取总行数");
		PainterSupport.setMargin(pageTotalLabel.getComp(), 0, 0, 0, 15);

		firstPage = new HImgButton();
		firstPage.setMouseExitedIconIcon(ModifyTabTool.getIcon(ModifyConstant.FIRST_PAGE_ICON));
		firstPage.setMouseEnteredIcon(ModifyTabTool.getIcon(ModifyConstant.FIRST_PAGE_ICON));
		TooltipManager.setTooltip(firstPage.getComp(), "第一页");

		prePage = new HImgButton();
		prePage.setMouseExitedIconIcon(ModifyTabTool.getIcon(ModifyConstant.PREVIOUS_PAGE_ICON));
		prePage.setMouseEnteredIcon(ModifyTabTool.getIcon(ModifyConstant.PREVIOUS_PAGE_ICON));
		TooltipManager.setTooltip(prePage.getComp(), "上一页");


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
		curPageNum.getComp().putClientProperty(StyleId.STYLE_PROPERTY, StyleId.textfieldNonOpaque);
		curPageNum.getComp().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				int length = curPageNum.getComp().getText().trim().length();
				setCurPageNumTextLength(length);
			}
		});
		PainterSupport.setPadding(curPageNum.getComp(), 0, 2, 0, 2);
		TooltipManager.setTooltip(curPageNum.getComp(), "当前页数");

		List<String> asList = Arrays.asList("10", "30", "50", "100", "1000", "自定义", String.valueOf(pageSizeNum));
		asList = asList.stream().distinct().sorted(new AlphaNumComparator()).collect(Collectors.toList());
		pageSizeComp = new PageSelect(asList, String.valueOf(pageSizeNum));
		pageSizeComp.setValue(String.valueOf(pageSizeNum));
		TooltipManager.setTooltip(pageSizeComp.getComp(), "分页设置");

		nextPage = new HImgButton();
		nextPage.setMouseExitedIconIcon(ModifyTabTool.getIcon(ModifyConstant.NEXT_PAGE_ICON));
		nextPage.setMouseEnteredIcon(ModifyTabTool.getIcon(ModifyConstant.NEXT_PAGE_ICON));
		TooltipManager.setTooltip(nextPage.getComp(), "下一页");

		lastPage = new HImgButton();
		lastPage.setMouseExitedIconIcon(ModifyTabTool.getIcon(ModifyConstant.LAST_PAGE_ICON));
		lastPage.setMouseEnteredIcon(ModifyTabTool.getIcon(ModifyConstant.LAST_PAGE_ICON));
		TooltipManager.setTooltip(lastPage.getComp(), "最后一页");

		LabelInput label1 = new LabelInput("第");
		LabelInput label2 = new LabelInput("页");

//		totalPageNum = new LabelInput("");

		//获取不到多语言 LabelInput暂时写死
		add(statusLabel, totalPageBtn, pageTotalLabel);
		add(firstPage, prePage);
		add(label1, curPageNum, label2);
		add(pageSizeComp, nextPage, lastPage);
//		add(new LabelInput("(" + "共"), totalPageNum, new LabelInput("页" + ")"));

		prePage.setEnabled(false);
		firstPage.setEnabled(false);

		compList = Arrays.asList(statusLabel, totalPageBtn, pageTotalLabel, firstPage, prePage, label1, curPageNum, label2, pageSizeComp, nextPage, lastPage);
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

	public long getPageSizeNum() {
		return pageSizeNum;
	}

	public void setPageSizeNum(int pageSizeNum) {
		this.pageSizeNum = pageSizeNum;
	}

	public PageSelect getPageSizeComp() {
		return pageSizeComp;
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
