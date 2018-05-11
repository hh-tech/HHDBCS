package com.hhdb.csadmin.plugin.function;

import java.awt.Component;

import javax.swing.JOptionPane;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.EventUtil;
import com.hhdb.csadmin.plugin.function.service.SqlOperationService;

/**
 * @Description: 函数
 * @date: 2017年11月6日
 * @Company: H2 Technology
 * @author: Liziyan
 * @version 1.0
 */
public class FunctionTab extends AbstractPlugin {
	
	public String PLUGIN_ID = FunctionTab.class.getPackage().getName();
	
	public FunctionPanel fp;
	
	public SqlOperationService sosi;
	
	public String schemaName = "";		// 模式名
	public String functionName = "";	//函数名
	public String treeNode;  //树id
	
	public FunctionTab(){
		sosi = new SqlOperationService(this);
	}
	


	/**
	 * 操作函数
	 */
	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent hevent= EventUtil.getReplyEvent(FunctionTab.class, event);
		if(event.getType().equals(EventTypeEnum.CMD.name())){
			try {
				schemaName = event.getPropMap().get("schemaName");
				String name = event.getPropMap().get("functionName") == null ? "" : event.getPropMap().get("functionName");
				if(name.contains("(")){
					functionName = name.substring(0,name.lastIndexOf("("));
				}
				treeNode = event.getPropMap().get("treeNode");
				if (event.getValue("CMD").equalsIgnoreCase("RemovePanelEvent")) { //关闭打开页面事件
					
				} else if (event.getValue("CMD").equals("FunctionCreateMainEvent")) { //新建
					fp = new FunctionPanel(this);
					fp.schemaName =schemaName;
					fp.functionName= functionName;
					fp.treeNode= treeNode;
					
					fp.isEdit = false;
					fp.initFunctionTab();
					sosi.getTabPanelTable("创建函数（模式：" + event.getPropMap().get("schemaName") + "）", fp);
				}else if(event.getValue("CMD").equals("FunctionEditMainEvent")){   //打开
					fp = new FunctionPanel(this);
					fp.schemaName =schemaName;
					fp.functionName= functionName;
					fp.treeNode= treeNode;
					
					fp.isEdit = true;
					fp.initFunctionTab();
					sosi.getTabPanelTable("修改函数（函数：" + event.getPropMap().get("functionName") + "）", fp);
				}else if(event.getValue("CMD").equals("FunctionCheckEvent")){	//检查函数
					fp = new FunctionPanel(this);
					fp.schemaName =schemaName;
					fp.functionName= functionName;
					fp.treeNode= treeNode;
					
					fp.isEdit = true;
					fp.initFunctionTab();
					sosi.getTabPanelTable("修改函数（函数：" + event.getPropMap().get("functionName") + "）", fp);
					fp.checkFunc();
				}else if(event.getValue("CMD").equals("FunctionRunEvent")){	//运行函数
					fp = new FunctionPanel(this);
					fp.schemaName =schemaName;
					fp.functionName= functionName;
					fp.treeNode= treeNode;
					
					fp.isEdit = true;
					fp.initFunctionTab();
					sosi.getTabPanelTable("修改函数（函数：" + event.getPropMap().get("functionName") + "）", fp);
					fp.execFunction();
				}
				return hevent;
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null,"错误信息：" + e.getMessage(), "错误",JOptionPane.ERROR_MESSAGE);
				return hevent;
			}
		}else{
			ErrorEvent errorEvent = new ErrorEvent(PLUGIN_ID,event.getFromID(),ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
			errorEvent.setErrorMessage(PLUGIN_ID + "不能接受如下事件:\n"+ event.toString());
			return errorEvent;
		}
	}

	
	
	@Override
	public Component getComponent() {
		return null;
	}
}
