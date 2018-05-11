package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.HHSqlUtil;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;

/**
 * 函数集合右键菜单
 * 
 * @author ZL
 * 
 */
public class FunctionMenu extends BasePopupMenu {

	private static final long serialVersionUID = 1L;
	private  final String REFRESH = "refresh";
	private  final String CREATEFUNC= "createFunc";
	private  final String EXECFUNC= "execFunc";
	private  final String DesignFunc= "DesignFunc";
	private  final String DELFunc= "DelFunc";
	private  final String CheckFunc= "CheckFunc";
	private  final String DebugFunc= "DebugFunc";
	private  final String CopyFunc= "CopyFunc";
	private  final String PasteFunc= "PasteFunc";
	private  BaseTreeNode treeNode;
//	private  BaseMenuItem debug;
	
	private HTree htree = null;
	
	public FunctionMenu(HTree htree){
		this.htree = htree;
		add(createMenuItem("新建函数", CREATEFUNC, this));
		add(createMenuItem("设计函数", DesignFunc, this));
		addSeparator();
		add(createMenuItem("验证函数", CheckFunc, this));
		add(createMenuItem("运行函数", EXECFUNC, this));
//		debug=createMenuItem("调试函数", DebugFunc, this);
//		add(debug);
		addSeparator();
		add(createMenuItem("复制函数", CopyFunc, this));
		add(createMenuItem("粘贴函数", PasteFunc, this));
		addSeparator();
		add(createMenuItem("删除函数", DELFunc, this));
	}
	
	public  FunctionMenu getInstance(BaseTreeNode node) {
		treeNode = node;
		return this;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		if(actionCmd.equals(CopyFunc)){
			try {
				List<Map<String, Object>> list =htree.treeService.getListMapBySql
						(HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.FUNCTION, "source")
								.replaceParams(new String[]{treeNode.getMetaTreeNodeBean().getId()+""}));
				htree.treeService.copyfunctionlist = list;
				JOptionPane.showMessageDialog(null, "函数复制成功", "消息", JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception e1) {
				LM.error(LM.Model.CS.name(), e1);
				JOptionPane.showMessageDialog(null, e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}else if(actionCmd.equals(PasteFunc)){
			if(htree.treeService.copyfunctionlist==null){
				JOptionPane.showMessageDialog(null, "没有复制的函数", "提示：", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			String result="";
			for(Map<String, Object> map:htree.treeService.copyfunctionlist){
				result = "CREATE OR REPLACE FUNCTION \"" + 
						treeNode.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName()+
						"\".\""+map.get("proname")+"_copy\""+"("+map.get("arguments")+")";
				result = result + " RETURNS " + map.get("result_type");
				result = result + " AS $BODY$ ";
				result = result + map.get("prosrc");
				result = result + "$BODY$ LANGUAGE " + map.get("lanname") + " VOLATILE;";
			}
			try {
				htree.treeService.executeSql(result);
				htree.treeService.refreshFunction(treeNode.getParentBaseTreeNode());
			} catch (Exception e1) {
				LM.error(LM.Model.CS.name(), e1);
				JOptionPane.showMessageDialog(null, e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
			
		}else if (actionCmd.equals(REFRESH)) {

		} else if (actionCmd.equals(CREATEFUNC)) {
			String toID = "com.hhdb.csadmin.plugin.function";
			CmdEvent event = new CmdEvent(htree.PLUGIN_ID, toID, "FunctionCreateMainEvent");
			event.addProp("schemaName", treeNode.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName());
			HHEvent revent = htree.sendEvent(event);
			if(revent instanceof ErrorEvent){
				JOptionPane.showMessageDialog(null, ((ErrorEvent)revent).getErrorMessage(), "错误",
						JOptionPane.ERROR_MESSAGE);
			}
		} else if(actionCmd.equals(DesignFunc)){
			String toID = "com.hhdb.csadmin.plugin.function";
			CmdEvent event = new CmdEvent(htree.PLUGIN_ID, toID, "FunctionEditMainEvent");
			event.addProp("schemaName", treeNode.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName());
			event.addProp("functionName", treeNode.getMetaTreeNodeBean().getName());
			event.addProp("treeNode", treeNode.getMetaTreeNodeBean().getId()+"");
			HHEvent revent = htree.sendEvent(event);
			if(revent instanceof ErrorEvent){
				JOptionPane.showMessageDialog(null, ((ErrorEvent)revent).getErrorMessage(), "错误",
						JOptionPane.ERROR_MESSAGE);
			}
		} else if(actionCmd.equals(DELFunc)){
			int n = JOptionPane.showConfirmDialog(null, "这将删除此函数，你确定想要继续吗？", "温馨提示", JOptionPane.YES_NO_OPTION);
			if (n == 0) {
				String fun = treeNode.getMetaTreeNodeBean().getName();
				String name = fun.substring(0,fun.indexOf('('));
				String pra = fun.substring(fun.indexOf('('));
				
				StringBuffer command = new StringBuffer("DROP function \"" + 
						treeNode.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName()
						+"\".\""+name+"\""+pra+";");
				try {
					htree.treeService.executeSql(command.toString());
					htree.treeService.refreshFunction(treeNode.getParentBaseTreeNode());
				} catch (Exception e1) {
					LM.error(LM.Model.CS.name(), e1);
					JOptionPane.showMessageDialog(null, e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		}else if(actionCmd.equals(EXECFUNC)){
			String toID = "com.hhdb.csadmin.plugin.function";
			CmdEvent event = new CmdEvent(htree.PLUGIN_ID, toID, "FunctionRunEvent");
			event.addProp("schemaName", treeNode.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName());
			event.addProp("functionName", treeNode.getMetaTreeNodeBean().getName());
			event.addProp("treeNode", treeNode.getMetaTreeNodeBean().getId()+"");
			HHEvent revent = htree.sendEvent(event);
			if(revent instanceof ErrorEvent){
				JOptionPane.showMessageDialog(null, ((ErrorEvent)revent).getErrorMessage(), "错误",
						JOptionPane.ERROR_MESSAGE);
			}
		}else if(actionCmd.equals(CheckFunc)){
			String toID = "com.hhdb.csadmin.plugin.function";
			CmdEvent event = new CmdEvent(htree.PLUGIN_ID, toID, "FunctionCheckEvent");
			event.addProp("schemaName", treeNode.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName());
			event.addProp("functionName", treeNode.getMetaTreeNodeBean().getName());
			event.addProp("treeNode", treeNode.getMetaTreeNodeBean().getId()+"");
			HHEvent revent = htree.sendEvent(event);
			if(revent instanceof ErrorEvent){
				JOptionPane.showMessageDialog(null, ((ErrorEvent)revent).getErrorMessage(), "错误",
						JOptionPane.ERROR_MESSAGE);
			}
		}else if(actionCmd.equals(DebugFunc)){
//			ServerBean serverBean = treeNode.getServerBean();
//			String sql="SELECT proname,oid,hh_catalog.hh_get_function_arguments(oid) AS arguments FROM hh_proc where oid="+treeNode.getMetaTreeNodeBean().getId()+";";
//			Map<String, Object> map=CommonDAO.executeMap(treeNode, sql);
//			String[] parms=null;
//			if(!"".equals(map.get("arguments").toString().trim())){
//				parms=map.get("arguments").toString().split(",");
//			}
//			List<Map<String, Object>> list=new ArrayList<Map<String, Object>>();
//			if(parms!=null){
//				for(String str:parms){
//					Map<String, Object> dparma=new HashMap<String, Object>();
//					String[] ss=str.trim().split(" ");
//					if(ss.length==1||str.trim().equals("character varying")){
//						dparma.put("参数名", "");
//						dparma.put("参数类型", str.trim());
//					}else{
//						dparma.put("参数名", ss[0]);
//						dparma.put("参数类型", ss[1]);
//					}
//					list.add(dparma);
//				}
//			}
//			BaseTablePanel dparameter=new BaseTablePanel(new int[]{0,1});
//			List<String> lists=new ArrayList<String>();
//			lists.add("参数名");
//			lists.add("参数类型");
//			lists.add("参数值");
//			dparameter.setDataList(lists, list);
//			
//			Object[] options = {"调试","关闭"};
//			final JOptionPane pane = new JOptionPane(dparameter, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[1]);
//			final JDialog dlg = pane.createDialog(ApplicationLauncher.getFrame(),"调试");
//			dlg.setModal(true);
//			dlg.setSize(500, 300);
//			dlg.setLocationRelativeTo(null);
//			dlg.setVisible(true);
//			String value = (String) pane.getValue();
//			if ("关闭".equals(value)) {
//				dlg.dispose();
//			}else if ("调试".equals(value)){
//				BaseTable parmtable=dparameter.getBaseTable();
//				if (parmtable.isEditing()) {
//					parmtable.getCellEditor().stopCellEditing();
//				}
//				parmtable.isEditing();
//				List<Map<String,Object>> pars=new ArrayList<Map<String,Object>>();
//				String parmss=map.get("proname")+" (";
//				String parmsvalue=map.get("proname")+" (";
//				int rows=parmtable.getRowCount();
//				for(int i=0;i<rows;i++){
//					Map<String,Object> maps=new HashMap<String,Object>();
//					maps.put("名称", parmtable.getValueAt(i, 0));
//					maps.put("类型", parmtable.getValueAt(i, 1));
//					maps.put("值", parmtable.getValueAt(i, 2));
//					pars.add(maps);
//					parmss+=parmtable.getValueAt(i, 1);
//					parmsvalue+=parmtable.getValueAt(i, 2)+"::"+parmtable.getValueAt(i, 1);
//					if(i==(rows-1)){
//						parmss+=")";
//						parmsvalue+=")";
//					}else{
//						parmss+=",";
//						parmsvalue+=",";
//					}
//				}
//				if(rows==0){
//					parmss+=")";
//					parmsvalue+=")";
//				}
//				BaseTabbedPane tPane = MainTabbedPaneInit.getInstance().getTabbedPane();
//				String idention=serverBean.getDbid()+serverBean.getDBName()+serverBean.getSchema()+treeNode.getMetaTreeNodeBean().getName()+"debug";
//				String name=treeNode.getMetaTreeNodeBean().getName();
//				String title=name.substring(0,name.indexOf("("))+" @" + serverBean.getDBName()+"."+serverBean.getSchema();
//				tPane.addTab(title, IconUtilities.loadIcon("editfunction.png"), new DebugPanel(treeNode,parmss,parmsvalue,pars), true,idention,false);
//			}
		}
	}
}
