package com.hhdb.csadmin.plugin.table_importandexport;

import java.awt.Component;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * 
 * @author hwj
 * @createTime:2017年12月19日14:07:52
 * @remark: 导入导出确认面板
 * @version 1.0
 */
public class BaseOptionPaneInstance extends JOptionPane {
	private static final long serialVersionUID = 1L;
	private JDialog dlg; // 对话框

	/**
	 * 
	 * @param parent
	 *            基准面，以此面板为基准
	 * @param content
	 *            操作面板
	 * @param options
	 *            选项
	 * @param title
	 *            标题
	 * @param width
	 *            面板宽度
	 * @param height
	 *            面板高度
	 */
	public BaseOptionPaneInstance(Component parent, Component content,
			Object[] options, String title, int width, int height) {
		super(content, JOptionPane.PLAIN_MESSAGE, JOptionPane.CLOSED_OPTION,
				null, options, options[0]);

		dlg = createDialog(null, title);
		dlg.setModal(true);
		dlg.setSize(width, height);
		dlg.setLocationRelativeTo(null);
		dlg.setVisible(true); // 是否显示
	}

	/**
	 * 取消
	 */
	public void dispose() {
		dlg.dispose();
	}
}
