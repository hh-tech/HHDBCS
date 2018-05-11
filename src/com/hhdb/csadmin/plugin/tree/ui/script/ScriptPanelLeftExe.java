package com.hhdb.csadmin.plugin.tree.ui.script;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.swingcontrol.textEdit.QueryEditorTextArea;
import com.hhdb.csadmin.common.util.VmUtil;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.service.ScriptService;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;

public class ScriptPanelLeftExe extends JPanel {
	private static final long serialVersionUID = 1L;
	public QueryEditorTextArea textArea = new QueryEditorTextArea(20, 60);
	private RTextScrollPane scrollpane;
	private JComboBox<String> scriptNamebox;
	private List<String> proNames = new ArrayList<String>();
	private Map<String, JLabel> lablemap = new HashMap<String, JLabel>();
	public Map<String, JTextField> textfileldmap = new HashMap<String, JTextField>();
	private JPanel jpl = new JPanel();
	private String scripttype;
	private BaseTreeNode treeNode;
	private HTree htree;

	public ScriptPanelLeftExe(HTree htree,BaseTreeNode treeNode, String scripttype) {
		this.htree = htree;
		this.treeNode = treeNode;
		this.scripttype = scripttype;
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH);
		textArea.setCodeFoldingEnabled(true);
		textArea.setEditable(false);
		scrollpane = new RTextScrollPane(textArea);
		scriptNamebox = new JComboBox<String>();
		scriptNamebox.setPreferredSize(new Dimension(400, 25));
		// 填充检查
		setBackground(Color.WHITE);
		setLayout(new GridBagLayout());

		JLabel label1 = new JLabel("选择脚本:");
		JLabel label2 = new JLabel("脚本内容:");
		JLabel labelMsg = new JLabel("需要填写的参数");
		labelMsg.setFont(new Font(null, WIDTH, 12));

		add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(10, 30, 5, 5), 0, 0));
		add(scriptNamebox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(10, 10, 5, 5), 0, 0));
		add(label2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(10, 30, 5, 5), 0, 0));
		add(scrollpane, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(10, 10, 5, 5), 0, 0));
		add(Box.createHorizontalGlue(), new GridBagConstraints(2, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(10, 0, 5, 0), 0, 0));
		add(labelMsg, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(10, 0, 5, 0), 0, 0));
		try {
			initPro();
		} catch (SQLException e) {
			LM.error(LM.Model.CS.name(), e);
			JOptionPane.showMessageDialog(null, e.getMessage(), "错误",
					JOptionPane.ERROR_MESSAGE);
		}
		// 监听选中
		scriptNamebox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// 如果选中了一个
				if (e.getStateChange() == ItemEvent.SELECTED) {
					// 这里写你的任务 ，比如取到现在的值
					String pron = (String) scriptNamebox.getSelectedItem();
					clearPro();
					try {
						showChage(pron);
						updateUI();
					} catch (SQLException e1) {
						LM.error(LM.Model.CS.name(), e1);
						JOptionPane.showMessageDialog(null, e1.getMessage(),
								"错误", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
	}

	private void clearPro() {

		for (String key : proNames) {
			remove(lablemap.get(key));
			remove(textfileldmap.get(key));
		}
		remove(jpl);
		proNames.clear();
		lablemap.clear();
		textfileldmap.clear();
	}

	private void showChage(String initSlectName) throws SQLException {
		Map<String, Object> scriptdata = ScriptService
				.queryScriptByNameAndType(initSlectName, scripttype);
		String txt = scriptdata.get("TXT").toString();
		textArea.setText(txt);
		Set<String> pros = VmUtil.getProNameByVmStr(txt);
		int fg = 0;
		for (String pro : pros) {
			JLabel label = new JLabel(pro + ":");
			JTextField text = new JTextField();
			text.setText(ScriptService.getInitProValue(htree,pro, treeNode));
			text.setPreferredSize(new Dimension(400, 25));
			add(label, new GridBagConstraints(0, fg + 3, 1, 1, 0.0, 0.0,
					GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
					new Insets(10, 0, 0, 0), 0, 0));
			add(text, new GridBagConstraints(1, fg + 3, 1, 1, 0.0, 0.0,
					GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
					new Insets(10, 10, 0, 0), 0, 0));
			proNames.add(pro);
			lablemap.put(pro, label);
			textfileldmap.put(pro, text);
			fg++;
		}
		jpl.setBackground(Color.WHITE);
		add(jpl, new GridBagConstraints(0, 5 + fg, 2, 1, 1.0, 1.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 5), 0, 0));
	}

	private void initPro() throws SQLException {
		List<List<String>> Names = ScriptService.listScriptByType(scripttype);
		if (Names.size() > 1) {
			for (int i = 1; i < Names.size(); i++) {
				scriptNamebox.addItem(Names.get(i).get(0));
			}
			String initSlectName = Names.get(1).get(0);
			showChage(initSlectName);
		} else {
			jpl.setBackground(Color.WHITE);
			add(jpl, new GridBagConstraints(0, 5, 2, 1, 1.0, 1.0,
					GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 5), 0, 0));
		}
	}

}
