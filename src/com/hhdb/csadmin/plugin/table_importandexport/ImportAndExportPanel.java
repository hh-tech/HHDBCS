package com.hhdb.csadmin.plugin.table_importandexport;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.hh.frame.swingui.event.HHEvent;



/**
 * @createTime:2017年12月19日14:14:20
 * @remark: 表格导入导出方法调用以及结果显示
 * @author hwj
 * @version 1.0
 */
public class ImportAndExportPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JRadioButton radio1, radio2;
	private int type;
	private int flag;
	private OperateExcel operateExcel = new OperateExcel();

	/**
	 * 构造方法，初始化面板选项
	 * 
	 * @param type
	 *            导入导出类型 1导出 2导入
	 */
	public ImportAndExportPanel(int type) {
		this.type = type;
		if (type == 1) {
			radio1 = new JRadioButton("导出csv");
			radio2 = new JRadioButton("导出excel");
		} else if (type == 2) {
			radio1 = new JRadioButton("导入csv");
			radio2 = new JRadioButton("导入excel");
		}
		// 默认选中第二个单选按钮
		radio2.setSelected(true);
		add(radio2);
		add(radio1);
		// 创建单选按钮组
		ButtonGroup group = new ButtonGroup();
		group.add(radio2);
		group.add(radio1);
	}

	/**
	 * 导入导出方法调用入口及调用结果
	 * 
	 * @param event
	 *            事件
	 */
	public void excuteData(HHEvent event) {
		try {
			if (radio1.isSelected()) { // 如果radio1被选中，则是关于csv格式的调用
				if (type == 1) {
					flag = OperateCsv.ExportCSV(event);
				} else if (type == 2) {
					flag = OperateCsv.ImportCSV(event);
				}
			} else if (radio2.isSelected()) {
				if (type == 1) {
					flag = operateExcel.exportExcle(event);
				} else if (type == 2) {
					flag = operateExcel.importExcel(event);
				}
			}
			if (type == 1 && flag == 1) {
				JOptionPane.showMessageDialog(null, "导出成功", "提示",
						JOptionPane.WARNING_MESSAGE, null);
			} else if (type == 2 && flag == 1) {
				JOptionPane.showMessageDialog(null, "导入成功", "提示",
						JOptionPane.WARNING_MESSAGE, null);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "提示",
					JOptionPane.WARNING_MESSAGE, null);
		}
	}

	public JRadioButton getRadio1() {
		return radio1;
	}

	public JRadioButton getRadio2() {
		return radio2;
	}
}
