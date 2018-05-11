package com.hhdb.csadmin.plugin.tree.ui.script;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.hh.frame.common.log.LM;
import com.hhdb.csadmin.common.util.IconUtilities;
import com.hhdb.csadmin.plugin.tree.service.ScriptService;
import com.hhdb.csadmin.plugin.type_create.component.BaseButton;
import com.hhdb.csadmin.plugin.type_create.component.BaseToolBar;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class MarScriptPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 按钮栏
	private BaseToolBar toolBar;
	private JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private String scripttype = "";
	private JScrollPane marscrolpane;
	private ScriptTableModel table;
	
	public MarScriptPanel(final String scripttype) throws SQLException {
		this.scripttype = scripttype;
		setLayout(new GridBagLayout());
		toolBar = new BaseToolBar();
		BaseButton refbtn = new BaseButton("刷新", IconUtilities.loadIcon("refresh.png"));
		BaseButton addbtn = new BaseButton("增加脚本", IconUtilities.loadIcon("adddata.png"));
		BaseButton delbtn = new BaseButton("删除脚本", IconUtilities.loadIcon("deldata.png"));
		toolBar.add(addbtn);
		toolBar.add(delbtn);
		toolBar.add(refbtn);
		add(toolBar, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(jSplitPane, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
		table = new ScriptTableModel(ScriptService.listScriptByType(scripttype));
		tableClick();
		
		marscrolpane = new JScrollPane(table);
		jSplitPane.setLeftComponent(marscrolpane);
		jSplitPane.setRightComponent(new JPanel());
		jSplitPane.setDividerLocation(0.5);
		jSplitPane.setDividerSize(3);		
		
		refbtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				refMarScript();
			}
		});
		addbtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showAddPanel();
			}
		});
		
		delbtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = table.getSelectedRow();
				if(r==-1){
					JOptionPane.showMessageDialog(null, "请先选中需要删除的行再进行删除操作。", "消息", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				String name = table.getValueAt(r, 0).toString();
				int n = JOptionPane.showConfirmDialog(null, "确定要删除"+name+"脚本吗？", "温馨提示", JOptionPane.YES_NO_OPTION);
				if (n == 0) {
					try {
						ScriptService.delScript(name, scripttype);
						JOptionPane.showMessageDialog(null, "成功删除！", "消息", JOptionPane.INFORMATION_MESSAGE);
						refMarScript();
					} catch (SQLException e1) {
						LM.error(LM.Model.CS.name(), e1);
						JOptionPane.showMessageDialog(null, e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
	}
	
	private void showAddPanel(){
		JScrollPane sp = new JScrollPane(new ScriptPanelAdd(scripttype,this));
//		JFrame jf = new JFrame();
//		jf.add(sp);
//		jf.setVisible(true);
		jSplitPane.setRightComponent(sp);
		jSplitPane.setDividerLocation(jSplitPane.getDividerLocation());
	}
	
	private void showUpdatePanel(String name) throws SQLException{
		JScrollPane sp = new JScrollPane(new ScriptPanelUpdate(name,scripttype,this));
		jSplitPane.setRightComponent(sp);
		jSplitPane.setDividerLocation(jSplitPane.getDividerLocation());
	}
	
	public void refMarScript(){
		try {
			table = new ScriptTableModel(ScriptService.listScriptByType(scripttype));
			tableClick();
			marscrolpane = new JScrollPane(table);
			jSplitPane.setLeftComponent(marscrolpane);
			jSplitPane.setRightComponent(new JPanel());
			jSplitPane.setDividerLocation(jSplitPane.getDividerLocation());
		} catch (SQLException e1) {
			LM.error(LM.Model.CS.name(), e1);
			JOptionPane.showMessageDialog(null, e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void tableClick(){
		table.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int r = table.getSelectedRow();
				String name = table.getValueAt(r, 0).toString();
				try {
					showUpdatePanel(name);
				} catch (SQLException e1) {
					LM.error(LM.Model.CS.name(), e1);
					JOptionPane.showMessageDialog(null, e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
		});
	}
}
