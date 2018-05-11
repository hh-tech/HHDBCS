package com.hhdb.csadmin.plugin.tree.ui.script;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import com.hh.frame.common.log.LM;
import com.hh.frame.common.util.TemplateUtil;
import com.hh.frame.exec.ExecRunner;
import com.hhdb.csadmin.common.util.IconUtilities;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;
import com.hhdb.csadmin.plugin.type_create.component.BaseButton;
import com.hhdb.csadmin.plugin.type_create.component.BaseToolBar;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class ExescriptPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 按钮栏
	private BaseToolBar toolBar;
	private JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private JScrollPane scrp;
	private ScriptPanelLeftExe sple;
	private ScriptPanelRightExe spre;
	public ExescriptPanel(final HTree htree,final BaseTreeNode treeNode, final String scripttype) {
		setLayout(new GridBagLayout());
		toolBar = new BaseToolBar();
		BaseButton refbtn = new BaseButton("刷新",
				IconUtilities.loadIcon("refresh.png"));
		BaseButton cleanbtn = new BaseButton("清空结果",
				IconUtilities.loadIcon("clear.png"));
		BaseButton runbtn = new BaseButton("执行",
				IconUtilities.loadIcon("start.png"));
		toolBar.add(refbtn);
		toolBar.add(cleanbtn);
		toolBar.add(runbtn);
		add(toolBar, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));
		// 签页
		add(jSplitPane, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));
		sple = new ScriptPanelLeftExe(htree,treeNode, scripttype);
		scrp = new JScrollPane(sple);
		jSplitPane.setLeftComponent(scrp);
		spre = new ScriptPanelRightExe();
		JScrollPane flagscrp = new JScrollPane(spre);
		jSplitPane.setRightComponent(flagscrp);
		jSplitPane.setDividerLocation(650);
		jSplitPane.setDividerSize(3);

		refbtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sple = new ScriptPanelLeftExe(htree,treeNode, scripttype);
				scrp = new JScrollPane(sple);
				jSplitPane.setLeftComponent(scrp);
				spre.infoArea.setText("");
				jSplitPane.setDividerLocation(650);
			}
		});
		
		cleanbtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				spre.infoArea.setText("");
			}
		});

		runbtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				runScript();
			}
		});
	}

	private void runScript() {
		HashMap<String, Object> proMap = new HashMap<String, Object>();
		for (String pro : sple.textfileldmap.keySet()) {
			JTextField jtf = sple.textfileldmap.get(pro);
			if (jtf.getText().isEmpty()) {
				JOptionPane.showMessageDialog(null, "参数" + pro + "没有填写", "错误",
						JOptionPane.ERROR_MESSAGE);
				return;
			} else {
				proMap.put(pro, jtf.getText());
			}
		}
		try {
			String runtxt = TemplateUtil.strVm2str(proMap,
					sple.textArea.getText());
			ExecRunner runner = new ExecRunner(runtxt);
			runner.exec();
			int count = 0;
			while (!runner.isDone()) {
				try {
					if (count++ > 100) {
						runner.kill();
					}
					Thread.sleep(10);
				} catch (InterruptedException e) {
					LM.error(LM.Model.CS.name(), e);
				}
			}
//			System.out.println(runner.getOutput());
			spre.infoArea.append(runner.getOutput());
			try{
				runner.clean();
			}catch(Exception e){	
				LM.error(LM.Model.CS.name(), e);
			}
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
			JOptionPane.showMessageDialog(null, e.getMessage(), "错误",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
