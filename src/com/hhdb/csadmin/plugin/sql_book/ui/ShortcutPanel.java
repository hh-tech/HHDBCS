package com.hhdb.csadmin.plugin.sql_book.ui;

import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

import com.hhdb.csadmin.common.ui.BaseFrame;
import com.hhdb.csadmin.plugin.sql_book.BooksPanel;

/**
 * 弹出窗口
 * @author hhxd
 * 
 */
public class ShortcutPanel extends JDialog {
	private static final long serialVersionUID = 2805619451637353405L;
	private BooksPanel book;
	
	/**
	 * 路径选择页面
	 * 
	 * @param bp
	 * @param myDirId
	 *            源文件id
	 * @param title
	 *            面板标题
	 * @param width
	 *            宽
	 * @param height
	 *            长
	 * @param comp
	 *            添加的组件
	 * @param bf
	 *            管理工具主面板
	 */
	public ShortcutPanel(BooksPanel bp, Integer myDirId, String title,int width, int height, BaseFrame bf) {
		super(bf, title);
		this.book = bp;
		add(book.sqls.hbook.getTreePanel(myDirId,this,book));
		setSize(width, height);
		setModal(true);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	/**
	 * 参数替换窗口
	 * @param title  
	 * 面板标题 
	 * @param bf
	 * 管理工具主面板
	 * @param bp
	 * 树面板
	 * @param width
	 *  宽
	 * @param height
	 * 长
	 * @param setstr
	 * 需要替换参数
	 * @param txt
	 * sql
	 */
	public ShortcutPanel(String title,BaseFrame bf,BooksPanel bp,int width, int height,Set<String> setstr,String txt) {
		super(bf, title);
		this.book = bp;
		add(new JScrollPane(new SqlOperationPanel(book,setstr,txt)));
		book.shp = this;
		setSize(width, height);
		setModal(true);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
}
