package com.hhdb.csadmin.plugin.function;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.hhdb.csadmin.common.bean.SqlBean;
import com.hhdb.csadmin.common.ui.textEdit.QueryTextPane;
import com.hhdb.csadmin.common.util.HHSqlUtil;
import com.hhdb.csadmin.common.util.IconUtilities;
import com.hhdb.csadmin.plugin.function.component.BaseTabbedPaneCustom;
import com.hhdb.csadmin.plugin.function.component.MessagePanel;
import com.hhdb.csadmin.plugin.function.component.button.BaseButton;
import com.hhdb.csadmin.plugin.function.handle.HandleFunctionPanel;

/**
 * 面板
 * @author hhxd
 *
 */
public class FunctionPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	public FunctionTab fit ;

	private MessagePanel msgPanel = new MessagePanel();  //显示结果面板
	private JSplitPane vSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);	//分割面板
	public JToolBar toolBar = new JToolBar();// 按钮栏

	private BaseTabbedPaneCustom tab;// tab页
	private HandleFunctionPanel functionPanel;// 函数 面板
	public QueryTextPane jtps;			//sql预览面板
	public boolean isEdit;// 是否编辑标识
	public String schemaName = "";		// 模式名
	public String functionName = "";	//函数名
	public String treeNode;  //树id
	public boolean results = true;   //控制结果面板初始化
	
	public FunctionPanel(FunctionTab fit){
		this.fit = fit;
		this.setLayout(new GridBagLayout());
	}
	
	
	/**
	 * 初始化函数面板
	 */
	public void initFunctionTab() throws Exception {
		tab = new BaseTabbedPaneCustom(JTabbedPane.TOP);
		initFunctionTool();
		
		//常规面板
		functionPanel = new HandleFunctionPanel(fit, schemaName, functionName, isEdit,this);
		//为修改时获取函数
		if(isEdit){
			functionPanel.jtp.setText(getFunctionCode(treeNode));
		}
		JScrollPane scroll = new JScrollPane(functionPanel);
		tab.addTab("常规", scroll);
		
		jtps = new QueryTextPane();
		tab.addTab("SQL预览", new JScrollPane(jtps));
		
		initFunctionTool();
		
		// 工具栏事件
		tab.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
				int selectedIndex = tabbedPane.getSelectedIndex();
				if (selectedIndex == 1) {
					jtps.setText(functionPanel.functionChanged());
				}

			}
		});
		add(toolBar, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(tab, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));

	}
	
	/**
	 * 创建按钮
	 * 
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
	 * 创建函数 工具栏
	 */
	private void initFunctionTool() {
		toolBar.removeAll();
		toolBar.repaint();
		createBtn("保存", IconUtilities.loadIcon("save.png"), "save");
		toolBar.addSeparator();
		if(isEdit){
			createBtn("检查", IconUtilities.loadIcon("checkfun.png"), "check");
			createBtn("运行", IconUtilities.loadIcon("runfun.png"), "run");
		}else{
			createBtn("添加参数", IconUtilities.loadIcon("addparkey.png"), "addparam");
			createBtn("删除参数", IconUtilities.loadIcon("delparkey.png"), "delparam");
		}
		toolBar.setFloatable(false);
	}

	/**
	 * 获取函数
	 * @param treeNode
	 * @return
	 */
	public String getFunctionCode(String treeNode) throws Exception{
		Object[] params = new Object[1];
		params[0] = "'" + treeNode + "'";
		SqlBean sql = HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.FUNCTION, "source");
		List<Map<String, Object>> list= fit.sosi.getListMap(sql.replaceParams(params));
		String result="";
		for(Map<String, Object> map:list){
			result = "CREATE OR REPLACE FUNCTION \"" + schemaName+"\".\""+map.get("proname")+"\"("+map.get("arguments")+")";
			result = result + " RETURNS " + map.get("result_type");
			result = result + " AS $BODY$ ";
			result = result + map.get("prosrc");
			result = result + "$BODY$ LANGUAGE " + map.get("lanname") + " VOLATILE;";
		}
		return result;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("save")) {
			System.out.println("保存");
			boolean flag = functionPanel.saveFunction();
			toolBar.getComponentAtIndex(0).setEnabled(flag);
		} else if (e.getActionCommand().equals("addparam")) {
			functionPanel.addRow();
		} else if (e.getActionCommand().equals("delparam")) {
			functionPanel.delRow();
		}else if(e.getActionCommand().equals("run")){
			execFunction();
		} else if(e.getActionCommand().equals("check")){
			checkFunc();
		}
	}
	
	/**
	 * 运行
	 */
	public void execFunction(){
		String parms = JOptionPane.showInputDialog(null, "输入运行参数用逗号隔开", "", JOptionPane.PLAIN_MESSAGE);
		if(parms!=null)
		{
			try{
				resultsInitialize();
				//获取数据
				List<List<Object>> dbtable= fit.sosi.getListList("select \""+functionName+"\"("+parms+")");
				Vector<Object> data = new Vector<Object>();
				Vector<String> colname = new Vector<String>();
				for (Object field : dbtable.get(0)) {
					colname.add(field.toString());
				}
				for (int i = 1; i < dbtable.size(); i++) {
					List<Object> list = dbtable.get(i);
					Vector<Object> data1 = new Vector<Object>();
					for (Object ob : list) {
						data1.add(ob);
					}
					data.add(data1);
				}
				msgPanel.showData(data, colname);
			} catch (Exception e) {
				msgPanel.setMessage(e.getMessage());
			}finally{
				msgPanel.setVisible(true);
				vSplitPane.getParent().doLayout();  //进行布局
			}
		}
	}
	
	/**
	 * 检查
	 */
	public void checkFunc(){
		try{
			resultsInitialize();
			String msg=fit.sosi.checkFunctionToStr(treeNode);
			if("".equals(msg)){
				msg="验证成功,无错误发现!";
			}
			msgPanel.setMessage(msg);
		} catch (Exception e) {
			msgPanel.setMessage(e.getMessage());
		}finally{
			msgPanel.setVisible(true);
			vSplitPane.getParent().doLayout();  //进行布局
		}
	}
	
	/**
	 * 结果面板初始化
	 */
	public void resultsInitialize(){
		msgPanel.cleanTab();
		vSplitPane.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				vSplitPane.setDividerLocation(0.5);
			}
		});
		vSplitPane.setBottomComponent(msgPanel);
		vSplitPane.setTopComponent(tab);
		vSplitPane.setDividerLocation(0.5);
		vSplitPane.setIgnoreRepaint(true);
		add(vSplitPane, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,GridBagConstraints.NORTH, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0), 0);
	}

}
