package com.hhdb.csadmin.plugin.table_operate;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.hh.frame.common.log.LM;
import com.hhdb.csadmin.common.util.IconUtilities;
import com.hhdb.csadmin.plugin.table_operate.component.BaseTabbedPaneCustom;
import com.hhdb.csadmin.plugin.table_operate.component.button.BaseButton;
import com.hhdb.csadmin.plugin.table_operate.handle.HandleCheckPanel;
import com.hhdb.csadmin.plugin.table_operate.handle.HandleCommentPanel;
import com.hhdb.csadmin.plugin.table_operate.handle.HandleForeignPanel;
import com.hhdb.csadmin.plugin.table_operate.handle.HandleIndexPanel;
import com.hhdb.csadmin.plugin.table_operate.handle.HandleRulePanel;
import com.hhdb.csadmin.plugin.table_operate.handle.HandleTablePanel;
import com.hhdb.csadmin.plugin.table_operate.handle.HandleTriggerPanel;
import com.hhdb.csadmin.plugin.table_operate.handle.HandleUniquePanel;
import com.hhdb.csadmin.plugin.table_operate.service.SqlOperationService;

/**
 * 修改数据表页面
 */
public class TableEditPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;

	public TableEdit tbe ;
	public SqlOperationService sqls;

	// 按钮栏
	private JToolBar toolBar = new JToolBar();
	// 分页tab
	private BaseTabbedPaneCustom tab;
	// 列控件
	private HandleTablePanel tablePanel;
	// 索引控件
	private HandleIndexPanel indexPanel;
	// 外键控件
	private HandleForeignPanel foreignPanel;
	// 唯一约束控件
	private HandleUniquePanel uniquePanel;
	// 检查控件
	private HandleCheckPanel checkPanel;
	// 规则控件
	private HandleRulePanel rulePanel;
	// 触发器控件
	private HandleTriggerPanel triggerPanel;
	// 注释
	private HandleCommentPanel commentPanel;
	// SQL预览控件
	private FitTableSQL fitsql;
	
	private Boolean sign;  //标记：true新建

	private String schemaName;  //模式名
	private String tableName;	//表名
	private String tableoId;		//id
	
	/*** 控制保存按钮的启用与否*/
	public Boolean controlButton = false; 
	
	/**
	 * 
	 * @param tbe
	 * @param schemaName
	 * @param tableName
	 * @param tableoId
	 * @param bool true：新建,false修改
	 * @throws Exception
	 */
	public TableEditPanel(TableEdit tbe,String schemaName,String tableName,String tableoId,Boolean bool) throws Exception {
		this.tbe = tbe;
		sqls = new SqlOperationService(tbe);
		this.setLayout(new GridBagLayout());
		this.setSchemaName(schemaName);
		this.setTableName(tableName);
		this.setTableoId(tableoId);
		this.setSign(bool);
		initTableCreate();
	}
	/**
	 * 
	 * @param tbe
	 * @param schemaName
	 * @param bool true：新建,false修改
	 * @throws Exception
	 */
	public TableEditPanel(TableEdit tbe,String schemaName,Boolean bool) throws Exception {
		this.tbe = tbe;
		sqls = new SqlOperationService(tbe);
		this.setLayout(new GridBagLayout());
		this.setSchemaName(schemaName);
		this.setSign(bool);
		initTableCreate();
	}
	
	/**
	 * 初始化新建面板
	 * @throws Exception
	 */
	public void initTableCreate() throws Exception {
		tab = new BaseTabbedPaneCustom(JTabbedPane.TOP);
		initTableTool();
		toolBar.getComponentAtIndex(0).setEnabled(controlButton);
		tablePanel = new HandleTablePanel(this);
		indexPanel = new HandleIndexPanel(this, tablePanel);
		foreignPanel = new HandleForeignPanel(this, tablePanel);
		uniquePanel = new HandleUniquePanel(this, tablePanel);
		checkPanel = new HandleCheckPanel(this);
		rulePanel = new HandleRulePanel(this);
		triggerPanel = new HandleTriggerPanel(this, tablePanel);
		commentPanel = new HandleCommentPanel(this, tablePanel);

		fitsql = new FitTableSQL(this);
		fitsql.setTablePanel(tablePanel);
		fitsql.setIndexPanel(indexPanel);
		fitsql.setUniquePanel(uniquePanel);
		fitsql.setCheckPanel(checkPanel);
		fitsql.setRulePanel(rulePanel);
		fitsql.setTriggerPanel(triggerPanel);
		fitsql.setForeignPanel(foreignPanel);
		fitsql.setCommentPanel(commentPanel);

		tab.addTab("列", new JScrollPane(tablePanel));
		tab.addTab("索引", new JScrollPane(indexPanel));
		tab.addTab("外键", new JScrollPane(foreignPanel));
		tab.addTab("唯一约束", new JScrollPane(uniquePanel));
		tab.addTab("检查", new JScrollPane(checkPanel));
		tab.addTab("规则", new JScrollPane(rulePanel));
		tab.addTab("触发器", new JScrollPane(triggerPanel));
		tab.addTab("注释", commentPanel);
		tab.addTab("SQL预览", new JScrollPane(fitsql));

		// 工具栏事件
		tab.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
				int selectedIndex = tabbedPane.getSelectedIndex();
				if (selectedIndex == 0) {
					initTableTool();
				}
				if (selectedIndex == 1) {
					initIndexTool();
				}
				if (selectedIndex == 2) {
					initForeignTool();
				}
				if (selectedIndex == 3) {
					initUniqueTool();
				}
				if (selectedIndex == 4) {
					initCheckTool();
				}
				if (selectedIndex == 5) {
					initRuleTool();
				}
				if (selectedIndex == 6) {
					initTriggerTool();
				}
				if (selectedIndex == 7) {
					initPreViewTool();
				}
				if (selectedIndex == 8) {
					initPreViewTool();
					fitsql.sqlChange();
				}
				toolBar.getComponentAtIndex(0).setEnabled(controlButton);
			}
		});
		add(toolBar, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH,GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(tab, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH,new Insets(0, 0, 0, 0), 0, 0));
	}

	/**
	 * 按钮点击处理
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("save")) {
			try {
				fitsql.saveTable(tablePanel,this);
			} catch (Exception e1) {
				LM.error(LM.Model.CS.name(), e1);
			}
		} else if (e.getActionCommand().equals("addfield")) {
			tablePanel.addRows();
		} else if (e.getActionCommand().equals("delfield")) {
			tablePanel.delRow();
		} else if (e.getActionCommand().equals("parkey")) {
			tablePanel.updateKeyName();
		} else if (e.getActionCommand().equals("addindex")) {
			indexPanel.addRows();
		} else if (e.getActionCommand().equals("delindex")) {
			indexPanel.delRow();
		} else if (e.getActionCommand().equals("addunique")) {
			uniquePanel.addRows();
		} else if (e.getActionCommand().equals("delunique")) {
			uniquePanel.delRow();
		} else if (e.getActionCommand().equals("addcheck")) {
			checkPanel.addRows();
		} else if (e.getActionCommand().equals("delcheck")) {
			checkPanel.delRow();
		} else if (e.getActionCommand().equals("addrule")) {
			rulePanel.addRows();
		} else if (e.getActionCommand().equals("delrule")) {
			rulePanel.delRow();
		} else if (e.getActionCommand().equals("addtrigger")) {
			triggerPanel.addRows();
		} else if (e.getActionCommand().equals("deltrigger")) {
			triggerPanel.delRow();
		} else if (e.getActionCommand().equals("addforeign")) {
			foreignPanel.addRows();
		} else if (e.getActionCommand().equals("delforeign")) {
			foreignPanel.delRow();
		}else if(e.getActionCommand().equals("refresh")){    //刷新
			if(getSign()){
				tbe.refresh(null,getSchemaName(),null,"create",true);
			}else{
				tbe.refresh(getTableName(),getSchemaName(),getTableoId(),"edit",false);
			}
		}
	}
	
	
	/**
	 * 创建按钮
	 * @param text
	 * @param icon
	 * @param comand
	 */
	private void createBtn(String text, Icon icon, String comand) {
		BaseButton basebtn = new BaseButton(text, icon);
		basebtn.setActionCommand(comand);
		basebtn.addActionListener(this);
		toolBar.add(basebtn);
	}
	
	/**
	 * 表格工具栏
	 */
	private void initTableTool() {
		toolBar.removeAll();
		toolBar.repaint();
		createBtn("保存", IconUtilities.loadIcon("save.png"), "save");
		toolBar.addSeparator();
		createBtn("添加栏位", IconUtilities.loadIcon("addfield.png"), "addfield");
		createBtn("删除栏位", IconUtilities.loadIcon("delfield.png"), "delfield");
		toolBar.addSeparator();
		createBtn("主键名", IconUtilities.loadIcon("parkey.png"), "parkey");
		toolBar.addSeparator();
		createBtn("刷新", IconUtilities.loadIcon("flash.png"), "refresh");
		toolBar.setFloatable(false);
	}

	/**
	 * 索引工具栏
	 */
	private void initIndexTool() {
		toolBar.removeAll();
		toolBar.repaint();
		createBtn("保存", IconUtilities.loadIcon("save.png"), "save");
		toolBar.addSeparator();
		createBtn("添加索引", IconUtilities.loadIcon("addindex.png"), "addindex");
		createBtn("删除索引", IconUtilities.loadIcon("delindex.png"), "delindex");
		toolBar.addSeparator();
		createBtn("刷新", IconUtilities.loadIcon("flash.png"), "refresh");
		toolBar.setFloatable(false);
	}

	/**
	 * 外键工具栏
	 */
	private void initForeignTool() {
		toolBar.removeAll();
		toolBar.repaint();
		createBtn("保存", IconUtilities.loadIcon("save.png"), "save");
		toolBar.addSeparator();
		createBtn("添加外键", IconUtilities.loadIcon("addforeign_key.png"), "addforeign");
		createBtn("删除外键", IconUtilities.loadIcon("delforeign_key.png"), "delforeign");
		toolBar.addSeparator();
		createBtn("刷新", IconUtilities.loadIcon("flash.png"), "refresh");
		toolBar.setFloatable(false);
	}

	/**
	 * 唯一键工具栏
	 */
	private void initUniqueTool() {
		toolBar.removeAll();
		toolBar.repaint();
		createBtn("保存", IconUtilities.loadIcon("save.png"), "save");
		toolBar.addSeparator();
		createBtn("添加唯一键", IconUtilities.loadIcon("addunique.png"), "addunique");
		createBtn("删除唯一键", IconUtilities.loadIcon("delunique.png"), "delunique");
		toolBar.addSeparator();
		createBtn("刷新", IconUtilities.loadIcon("flash.png"), "refresh");
		toolBar.setFloatable(false);
	}

	/**
	 * 检查工具栏
	 */
	private void initCheckTool() {
		toolBar.removeAll();
		toolBar.repaint();
		createBtn("保存", IconUtilities.loadIcon("save.png"), "save");
		toolBar.addSeparator();
		createBtn("添加检查", IconUtilities.loadIcon("addcheck.png"), "addcheck");
		createBtn("删除检查", IconUtilities.loadIcon("delcheck.png"), "delcheck");
		toolBar.addSeparator();
		createBtn("刷新", IconUtilities.loadIcon("flash.png"), "refresh");
		toolBar.setFloatable(false);
	}

	/**
	 * 规则工具栏
	 */
	private void initRuleTool() {
		toolBar.removeAll();
		toolBar.repaint();
		createBtn("保存", IconUtilities.loadIcon("save.png"), "save");
		toolBar.addSeparator();
		createBtn("添加规则", IconUtilities.loadIcon("addrule.png"), "addrule");
		createBtn("删除规则", IconUtilities.loadIcon("delrule.png"), "delrule");
		toolBar.addSeparator();
		createBtn("刷新", IconUtilities.loadIcon("flash.png"), "refresh");
		toolBar.setFloatable(false);
	}

	/**
	 * 触发器工具栏
	 */
	private void initTriggerTool() {
		toolBar.removeAll();
		toolBar.repaint();
		createBtn("保存", IconUtilities.loadIcon("save.png"), "save");
		toolBar.addSeparator();
		createBtn("添加触发器", IconUtilities.loadIcon("addtrigger.png"), "addtrigger");
		createBtn("删除触发器", IconUtilities.loadIcon("deltrigger.png"), "deltrigger");
		toolBar.addSeparator();
		createBtn("刷新", IconUtilities.loadIcon("flash.png"), "refresh");
		toolBar.setFloatable(false);
	}

	/**
	 * 预览，注释工具栏
	 */
	private void initPreViewTool() {
		toolBar.removeAll();
		toolBar.repaint();
		createBtn("保存", IconUtilities.loadIcon("save.png"), "save");
		toolBar.addSeparator();
		createBtn("刷新", IconUtilities.loadIcon("flash.png"), "refresh");
		toolBar.setFloatable(false);
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableoId() {
		return tableoId;
	}

	public void setTableoId(String tableoId) {
		this.tableoId = tableoId;
	}
	/*** 新建还是修改*/
	public Boolean getSign() {
		return sign;
	}
	
	public void setSign(Boolean sign) {
		this.sign = sign;
	}
	public JToolBar getToolBar() {
		return toolBar;
	}
	public void setToolBar(JToolBar toolBar) {
		this.toolBar = toolBar;
	}
	
	
}
