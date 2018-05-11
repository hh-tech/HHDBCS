package com.hhdb.csadmin.plugin.tree.ui.script;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.swingcontrol.textEdit.QueryEditorTextArea;
import com.hhdb.csadmin.plugin.tree.service.ScriptService;

public class ScriptPanelUpdate extends JPanel {
	private static final long serialVersionUID = 1L;
	QueryEditorTextArea textArea = new QueryEditorTextArea(20, 60);
	RTextScrollPane scrollpane;
	private JTextArea beizhu = new JTextArea("编写规则：这里编写的都是脚本模板\n1、参数统一用${参数名}表示。"
			+ "\n2、有以下几个特定的参数需要用规定的名称,因为这几个参数在运行脚本时可以自动添加,不需要手动输入.\n需要手动输入的参数,参数名不能为中文且不能与以下几个参数重复."
			+ "\n(1)数据库节点：${database}"
			+ "\n(2)模式节点：${schema}"
			+ "\n(3)表节点：${table}"
			+ "\n(4)服务器地址：${host}"
			+ "\n(5)端口号：${port}"
			+ "\n(6)用户名：${username}"
			+ "\n(7)密码：${password}"
			);
	
	public ScriptPanelUpdate(final String oldname,final String scripttype,final MarScriptPanel mp) throws SQLException{

		Map<String,Object> olddata = ScriptService.queryScriptByNameAndType(oldname, scripttype);
		if(olddata==null){
			add(new JLabel("该条数据已经不存在！"));
			return;
		}
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH);
		textArea.setCodeFoldingEnabled(true);
		textArea.setText(olddata.get("TXT").toString());
		scrollpane = new RTextScrollPane(textArea);
		//填充检查
		setBackground(Color.WHITE);
		setLayout(new GridBagLayout());
		JLabel labletit = new JLabel("修改脚本页面");
		labletit.setFont(new Font("", WIDTH, 14));
		JLabel labelname = new JLabel("脚本名称:");
		JLabel labeltxt=new JLabel("脚本内容:");
		beizhu.setEditable(false);
		final JTextField texfName = new JTextField();
		texfName.setPreferredSize(new Dimension(400, 25));
		texfName.setText(oldname);
		JButton savBtn = new JButton("保存");
		
		add(labletit, new GridBagConstraints(0 , 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 20, 0, 0), 0, 0));
		add(labelname, new GridBagConstraints(0 , 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(15, 20, 0, 0), 0, 0));
		add(texfName, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(labeltxt, new GridBagConstraints(0 , 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(10, 20, 0, 0), 0, 0));
		add(scrollpane, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(savBtn, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(beizhu, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));

		JPanel jpl = new JPanel();
		jpl.setBackground(Color.WHITE);
		add(jpl, new GridBagConstraints(0, 10, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
		
		savBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = texfName.getText();
				String text = textArea.getText();
				if(name.trim().isEmpty()){
					JOptionPane.showMessageDialog(null, "脚本名称不能为空", "错误", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					if(!name.equals(oldname)){
						if(ScriptService.extScriptByNameAndType(name, scripttype)){
							JOptionPane.showMessageDialog(null, "脚本名称已经存在", "错误", JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
				} catch (SQLException e1) {
					LM.error(LM.Model.CS.name(), e1);
					JOptionPane.showMessageDialog(null, e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(text.trim().isEmpty()){
					JOptionPane.showMessageDialog(null, "脚本内容不能为空", "错误", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					ScriptService.updateScript(name, text, scripttype,oldname);
					JOptionPane.showMessageDialog(null, "修改成功！", "消息", JOptionPane.INFORMATION_MESSAGE);
					mp.refMarScript();
				} catch (SQLException e1) {
					LM.error(LM.Model.CS.name(), e1);
					JOptionPane.showMessageDialog(null, "修改失败："+e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}
	
	
}
