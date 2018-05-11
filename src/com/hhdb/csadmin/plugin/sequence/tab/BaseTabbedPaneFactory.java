package com.hhdb.csadmin.plugin.sequence.tab;

/**
 * 恒辉面板工厂工具类 考虑到面板的重用性，获取面板时，应该面板设定指定的标识
 * 
 * @author tsp
 * 
 */
public class BaseTabbedPaneFactory {
	/**
	 * 获取面板
	 * 
	 * @param hhTabbedPaneLogo
	 *            面板标识
	 * @param closeEnabled
	 *            面板是否可关闭 true 表示带可关闭， false 表示不可关闭
	 * @return
	 */
	public static BaseTabbedPane getShrTabbedPane(final String hhTabbedPaneLogo, final boolean closeEnabled) {
		BaseTabbedPane tabbedPane = new BaseTabbedPane(closeEnabled);
		return tabbedPane;
	}

	/**
	 * 获取是否可关闭面板
	 * 
	 * @param closeEnabled
	 *            面板是否可关闭 true 表示带可关闭， false 表示不可关闭
	 * @return
	 */
	public static BaseTabbedPane getShrTabbedPane(boolean closeEnabled) {
		BaseTabbedPane tabbedPane = new BaseTabbedPane(closeEnabled);
		return tabbedPane;
	}

	/**
	 * 获取默认面板,默认为不可关闭面板
	 * 
	 * @return
	 */
	public static BaseTabbedPane getShrTabbedPane() {
		return getShrTabbedPane(false);
	}

}
