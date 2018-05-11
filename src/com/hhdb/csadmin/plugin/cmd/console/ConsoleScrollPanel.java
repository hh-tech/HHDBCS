package com.hhdb.csadmin.plugin.cmd.console;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Connection;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.bean.DefaultSet;
import com.hhdb.csadmin.common.bean.DefaultSetting;
import com.hhdb.csadmin.common.bean.ServerBean;
import com.hhdb.csadmin.plugin.cmd.SqlCmdPlugin;

/**
 * 命令行滚动面板,实现键盘监听接口
 * 
 * @author 张涛
 * @version 2017年4月19日
 */
public class ConsoleScrollPanel extends JScrollPane{
	private static final long serialVersionUID = -7067115262193201550L;
	// 样式配置
	private DefaultSet tpset;
	// 滚动条
	private JScrollBar scrollBar;
	// 最底层panel, 包含上下两部分
	private JPanel containPanel;
	// 上面部分, 不可编辑文本panel,显示历史命令和执行结果
	private ConsolePrint printC;
	/* 下面部分 */
	//prefix容器
	private JPanel prefixCPanel;
	//input和prefixCPanel的容器
	private JPanel botPanel;
	// hhdb =#|-#
	private ConsolePrefix prefixC;
	// 命令输入部分
	private ConsoleInput inputC;
	
	private SqlCmdPlugin sqlcmdplugin;

	public ConsoleScrollPanel(Connection connection,SqlCmdPlugin sqlcmdplugin) {
		this.sqlcmdplugin = sqlcmdplugin;
		init(connection);
	}

	private void init(Connection connection) {
		// 配置文件默认样式
		tpset = DefaultSetting.loadFontSettings();

		// 水平滚动条
		this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		// 垂直滚动条
		this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		// 设置滚动速度
		scrollBar = this.getVerticalScrollBar();
		scrollBar.setUnitIncrement(30);// 点击上下箭头(包括滑轮)的移动距离
		// scrollBar.setBlockIncrement(20);//点击空白处的移动距离

		/*    布局         */
		//容器
		containPanel = new JPanel(new BorderLayout());
		botPanel = new JPanel(new BorderLayout());
		prefixCPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 5));
		//文本组件
		printC = new ConsolePrint();
		printC.setEditable(false);
		
		CmdEvent getsbEvent = new CmdEvent(SqlCmdPlugin.class.getPackage().getName(), "com.hhdb.csadmin.plugin.conn", "GetServerBean");
		HHEvent revent = sqlcmdplugin.sendEvent(getsbEvent);
		ServerBean sb = (ServerBean)revent.getObj();
		
		prefixC = new ConsolePrefix(sb.getDBName());
		inputC = new ConsoleInput();
		//嵌入
		prefixCPanel.add(prefixC);
		botPanel.add(prefixCPanel, BorderLayout.WEST);
		botPanel.add(inputC, BorderLayout.CENTER);
		containPanel.add(printC, BorderLayout.NORTH);
		containPanel.add(botPanel, BorderLayout.CENTER);

		// 设置字体大小，颜色，背景色
		formatComponent(printC, ConsoleConstant.FONT_SIZE_14);
		formatComponent(prefixCPanel, ConsoleConstant.FONT_SIZE);
		formatComponent(inputC, ConsoleConstant.FONT_SIZE);
		formatComponent(prefixC, ConsoleConstant.FONT_SIZE);
		// 加载视图
		this.setViewportView(containPanel);
		// 键盘监听
		inputC.addKeyListener(new KeyHandler(printC, prefixC, inputC, scrollBar, connection,sqlcmdplugin));
	}

	/**
	 * 格式化组件
	 * 
	 * @param c
	 */
	private void formatComponent(Component c, int fontSize) {
		c.setBackground(DefaultSetting.strToColor(tpset.getCmdbackcolor()));
		c.setForeground(DefaultSetting.strToColor(tpset.getCmdfontcolor()));
		c.setFont(new Font(ConsoleConstant.FONT_NAME, Font.PLAIN, fontSize));
	}

	public Component[] getTextComponent() {
		return new Component[] {printC, prefixCPanel, inputC, prefixC };
	}
}

