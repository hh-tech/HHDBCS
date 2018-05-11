package com.hhdb.csadmin.plugin.view.ui;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import com.hhdb.csadmin.common.util.IconUtilities;
import com.hhdb.csadmin.plugin.table_open.ui.BaseButton;

/**
 * 翻页按钮栏
 * @author hhxd
 *
 */
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
	// 总页数显示
	private JLabel totalPageNum;
	//总页码数记录
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
//		curPageNum.setColumns(1);
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

	/**
	 * 按钮首页
	 */
	public BaseButton getFirstPage() {
		return firstPage;
	}

	public void setFirstPage(BaseButton firstPage) {
		this.firstPage = firstPage;
	}
	/**
	 * 前一页
	 */
	public BaseButton getPrePage() {
		return prePage;
	}

	public void setPrePage(BaseButton prePage) {
		this.prePage = prePage;
	}
	
	/**
	 * 当前页数
	 */
	public JTextField getCurPageNum() {
		return curPageNum;
	}

	public void setCurPageNum(JTextField curPageNum) {
		this.curPageNum = curPageNum;
	}
	/**
	 * 下一页
	 */
	public BaseButton getNextPage() {
		return nextPage;
	}

	public void setNextPage(BaseButton nextPage) {
		this.nextPage = nextPage;
	}
	/**
	 * 末页
	 */
	public BaseButton getLastPage() {
		return lastPage;
	}

	public void setLastPage(BaseButton lastPage) {
		this.lastPage = lastPage;
	}
	/**
	 * 总页码数记录
	 * @return
	 */
	public int getPageCount() {
		return pageCount;
	}
	/**
	 * 总页码数记录
	 * @return
	 */
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	/**
	 * 页数显示
	 */
	public JLabel getTotalPageNum() {
		return totalPageNum;
	}
	/**
	 * 页数显示
	 */
	public void setTotalPageNum(JLabel totalPageNum) {
		this.totalPageNum = totalPageNum;
	}
}
