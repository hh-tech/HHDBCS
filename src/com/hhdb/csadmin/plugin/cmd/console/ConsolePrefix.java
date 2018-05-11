package com.hhdb.csadmin.plugin.cmd.console;

import javax.swing.JLabel;

/**
 * 
* <p>Description: 字符sql窗口输入前缀组件对象</p>
* <p>Company: 恒辉</p> 
* @author 张涛
* @version 创建时间：2017年10月30日 上午11:07:09
 */
public class ConsolePrefix extends JLabel {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX_START = "=# ";
	private static final String PREFIX_CONTINUE = "-# ";
	private String dbName;

	ConsolePrefix(String dbName) {
		super(dbName + PREFIX_START);
		this.dbName = dbName;
	}
	
	//准备重新输入
	void setStart() {
		setText(dbName + PREFIX_START);
	}
	
	//未输入完，继续输入
	void setContinue() {
		setText(dbName + PREFIX_CONTINUE);
	}
}
