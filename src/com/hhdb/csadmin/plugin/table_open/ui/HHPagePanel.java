package com.hhdb.csadmin.plugin.table_open.ui;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import com.hhdb.csadmin.common.util.IconUtilities;
public class HHPagePanel extends JToolBar{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 按钮首页
	private BaseButton firstPage;
	// 前一页
	private BaseButton prePage;
	// 当前页数
	private JTextField curPageNum;
	// 下一页
	private BaseButton nextPage;
	// 末页
	private BaseButton lastPage;
	// 总页数
	private JLabel totalPageNum;
	
	private int pageCount;

	public HHPagePanel() {
		init();
	}

	private void init() {
		this.setFloatable(false);
		firstPage = new BaseButton(IconUtilities.loadIcon("first.png"));
		prePage = new BaseButton(IconUtilities.loadIcon("pre.png"));
		curPageNum = new JTextField("1");
		nextPage = new BaseButton(IconUtilities.loadIcon("next.png"));
		lastPage = new BaseButton(IconUtilities.loadIcon("last.png"));
		totalPageNum = new JLabel("");
		firstPage.setToolTipText("第一页");
		prePage.setToolTipText("上一页");
		nextPage.setToolTipText("下一页");
		lastPage.setToolTipText("最后一页");
		curPageNum.setBorder(null);
		curPageNum.setColumns(4);
	    Dimension d = curPageNum.getPreferredSize();
		d.width=25;
		curPageNum.setMinimumSize(d);
	    curPageNum.setHorizontalAlignment(JTextField.CENTER);
		this.add(firstPage);
		this.add(prePage);
		this.add(new JLabel(" 第 "));
		this.add(curPageNum);
		this.add(new JLabel(" 页 "));
		this.add(nextPage);
		this.add(lastPage);
		this.add(new JLabel(" (共 "));
		this.add(totalPageNum);
		this.add(new JLabel(" 页) "));
		
		firstPage.setEnabled(false);
		prePage.setEnabled(false);
		
	}


	public BaseButton getFirstPage() {
		return firstPage;
	}

	public void setFirstPage(BaseButton firstPage) {
		this.firstPage = firstPage;
	}

	public BaseButton getPrePage() {
		return prePage;
	}

	public void setPrePage(BaseButton prePage) {
		this.prePage = prePage;
	}

	public JTextField getCurPageNum() {
		return curPageNum;
	}

	public void setCurPageNum(JTextField curPageNum) {
		this.curPageNum = curPageNum;
	}

	public BaseButton getNextPage() {
		return nextPage;
	}

	public void setNextPage(BaseButton nextPage) {
		this.nextPage = nextPage;
	}

	public BaseButton getLastPage() {
		return lastPage;
	}

	public void setLastPage(BaseButton lastPage) {
		this.lastPage = lastPage;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public JLabel getTotalPageNum() {
		return totalPageNum;
	}

	public void setTotalPageNum(JLabel totalPageNum) {
		this.totalPageNum = totalPageNum;
	}


	
	
}
