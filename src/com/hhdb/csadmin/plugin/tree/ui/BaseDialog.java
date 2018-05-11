package com.hhdb.csadmin.plugin.tree.ui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;
import com.hhdb.csadmin.common.util.IconUtilities;
import com.hhdb.csadmin.plugin.tree.util.BaseChangeInterface;
import com.hhdb.csadmin.plugin.tree.util.UIUtils;

public class BaseDialog {

	public static final String OK = "确认";
	public static final String CANCEL = "取消";
	private final JDialog dlg;

	/**
	 * 带 确定 取消按钮的弹出框
	 * 
	 * @param jFrame
	 *            父级控件
	 * @param content
	 *            内容控件 必须实现BaseChangeInterface 接口
	 * @param title
	 *            弹出框标题
	 * @param img
	 *            弹出框图标
	 */
	public BaseDialog(JFrame jFrame, final JComponent content, String title, String img) {
		Object[] options = { OK, CANCEL };
		final JOptionPane pane = new JOptionPane(content, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

		final JPanel mainPanel = new JPanel();
		mainPanel.add(pane, BorderLayout.CENTER);

		JOptionPane p = new JOptionPane();
		dlg = p.createDialog(jFrame, title);
		if (StringUtils.isEmpty(img)) {
			dlg.setIconImage(IconUtilities.loadIcon(img).getImage());
		}
		dlg.setModal(true);
		dlg.pack();
		dlg.setResizable(false);
		dlg.setContentPane(mainPanel);
		dlg.setLocationRelativeTo(null);
		PropertyChangeListener changeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				String value = (String) pane.getValue();
				if (CANCEL.equals(value)) {
					pane.removePropertyChangeListener(this);
					dlg.dispose();
				} else if (OK.equals(value)) {
					BaseChangeInterface bc = (BaseChangeInterface) content;
					if(bc.execute()){
						pane.removePropertyChangeListener(this);
						dlg.dispose();
					}else{
						pane.setValue("ss");
						pane.setValue("OK");
					}
				}
			}
		};
		pane.addPropertyChangeListener(changeListener);
	}

	public void showDialog() {
		dlg.setLocation(UIUtils.getPointToCenter(dlg.getOwner(), dlg.getSize()));
		dlg.setVisible(true);
		dlg.toFront();
		dlg.requestFocus();
	}

	public void setSize(int width, int height) {
		dlg.setSize(width, height);
	}
}
