 package com.hhdb.csadmin.plugin.tree.ui;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JOptionPane;

import com.hh.frame.common.log.LM;
import com.hh.frame.csv.writer.CsvWriter;
import com.hh.frame.dbobj.hhdb.HHdbCk;
import com.hh.frame.dbobj.hhdb.HHdbDatabase;
import com.hh.frame.dbobj.hhdb.HHdbFk;
import com.hh.frame.dbobj.hhdb.HHdbFun;
import com.hh.frame.dbobj.hhdb.HHdbIndex;
import com.hh.frame.dbobj.hhdb.HHdbPk;
import com.hh.frame.dbobj.hhdb.HHdbSchema;
import com.hh.frame.dbobj.hhdb.HHdbSeq;
import com.hh.frame.dbobj.hhdb.HHdbTabSp;
import com.hh.frame.dbobj.hhdb.HHdbTable;
import com.hh.frame.dbobj.hhdb.HHdbUk;
import com.hh.frame.dbobj.hhdb.HHdbUser;
import com.hh.frame.dbobj.hhdb.HHdbView;
import com.hh.frame.dbobj.hhdb.HHdbXk;
import com.hhdb.csadmin.common.bean.ServerBean;
import com.hhdb.csadmin.common.dao.ConnService;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.ExtendXmlLoader;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.util.TreeNodeUtil;

/**
 * 鼠标单击处理
 * 
 * @author hyz
 * 
 */
public class TreeClick {
	private HTree htree;
    public TreeClick(HTree htree){
    	this.htree = htree;
    }		
	public void execute(BaseTreeNode treeNode) {
		String type = treeNode.getType();

		if(type.equals(TreeNodeUtil.SERVER_TYPE)) {	
			CmdEvent getsbEvent = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "GetServerBean");
			HHEvent revent = htree.sendEvent(getsbEvent);
			ServerBean serverbean = (ServerBean)revent.getObj();
			
			Collection<String[]> data = new ArrayList<>();
			String[] str1 = new String[2];
			str1[0] = "属性";
			str1[1] = "值";
			String[] str2 = new String[2];
			str2[0] = "主机地址";
			str2[1] = serverbean.getHost();
			String[] str3 = new String[2];
			str3[0] = "端口号";
			str3[1] = serverbean.getPort();
			String[] str4 = new String[2];
			str4[0] = "用户名";
			str4[1] = serverbean.getUserName();
			String[] str5 = new String[2];
			str5[0] = "数据库";
			str5[1] = serverbean.getDBName();
			data.add(str1);
			data.add(str2);
			data.add(str3);
			data.add(str4);
			data.add(str5);
			CsvWriter csvWriter = new CsvWriter();
			StringBuffer sbf = new StringBuffer();
	    	try {
				csvWriter.writeSbf(sbf, data);
				htree.treeService.sendAttr(sbf.toString(),"");
			} catch (IOException e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
	    	htree.treeService.sendCreateSql("");
		}
		else if(type.equals(TreeNodeUtil.DB_TYPE)) {
			defultsend();		
		}
		else if(type.equals(TreeNodeUtil.DB_ITEM_TYPE)) {
			try {
				HHdbDatabase hhdbDb = (HHdbDatabase)treeNode.getNodeObject();
				String csvstr = htree.treeService.getAttrByMap(hhdbDb.getProp());
				String createsql = hhdbDb.getCreateSql();
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql(createsql);
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				LM.error(LM.Model.CS.name(), e);
			}			
		}else if(type.equals(TreeNodeUtil.SCHEMA_TYPE)) {
			try {
				String csvstr = htree.treeService.getAttrCollectionSchema();
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql("");
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
			
		}else if(type.equals(TreeNodeUtil.SCHEMA_ITEM_TYPE)) {
			try {
				HHdbSchema hhdbSchema = (HHdbSchema)treeNode.getNodeObject();
				String csvstr = htree.treeService.getAttrByMap(hhdbSchema.getProp());
				String createsql = hhdbSchema.getCreateSql();
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql(createsql);
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
			
		}else if(type.equals(TreeNodeUtil.TABLE_TYPE)) {
			try {
				String csvstr = htree.treeService.getAttrCollectionTable(
						treeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getName());
				htree.treeService.sendAttr(csvstr,"true");
				htree.treeService.sendCreateSql("");
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
			
		}else if(type.equals(TreeNodeUtil.TABLE_ITEM_TYPE)) {
			try {
				HHdbTable hhdbTb = (HHdbTable)treeNode.getNodeObject();
				String csvstr = htree.treeService.getAttrByMap(hhdbTb.getProp());
				String createsql = hhdbTb.getCreateSql();
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql(createsql);
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
			
		}else if(type.equals(TreeNodeUtil.COL_TYPE)) {
			try {
				String csvstr = htree.treeService.getAttrCollectionCol(
						treeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getId());
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql("");
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}else if(type.equals(TreeNodeUtil.CONSTRAINT_TYPE)) {
			try {
				String csvstr = htree.treeService.getAttrCollectionConstraints(
						treeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getId());
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql("");
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}else if(type.equals(TreeNodeUtil.INDEX_TYPE)) {
			try {
				String csvstr = htree.treeService.getAttrCollectionIndex(
						treeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getId());
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql("");
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}else if(type.equals(TreeNodeUtil.INDEX_ITEM_TYPE)) {
			try {
				HHdbIndex hhdbindex = (HHdbIndex)treeNode.getNodeObject();
				String csvstr = htree.treeService.getAttrByMap(hhdbindex.getProp());
				String createsql = hhdbindex.getCreateSql();
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql(createsql);
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(type.equals(TreeNodeUtil.CONSTRAINT_PK_ITEM_TYPE)) {
			try {
				HHdbPk hhdbpk = (HHdbPk)treeNode.getNodeObject();
				String csvstr = htree.treeService.getAttrByMap(hhdbpk.getProp());
				String createsql = hhdbpk.getCreateSql();
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql(createsql);
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(type.equals(TreeNodeUtil.CONSTRAINT_FK_ITEM_TYPE)) {
			try {
				HHdbFk hhdbfk = (HHdbFk)treeNode.getNodeObject();
				String csvstr = htree.treeService.getAttrByMap(hhdbfk.getProp());
				String createsql = hhdbfk.getCreateSql();
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql(createsql);
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(type.equals(TreeNodeUtil.CONSTRAINT_UK_ITEM_TYPE)) {
			try {
				HHdbUk hhdbuk = (HHdbUk)treeNode.getNodeObject();
				String csvstr = htree.treeService.getAttrByMap(hhdbuk.getProp());
				String createsql = hhdbuk.getCreateSql();
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql(createsql);
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(type.equals(TreeNodeUtil.CONSTRAINT_CK_ITEM_TYPE)) {
			try {
				HHdbCk hhdbck = (HHdbCk)treeNode.getNodeObject();
				String csvstr = htree.treeService.getAttrByMap(hhdbck.getProp());
				String createsql = hhdbck.getCreateSql();
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql(createsql);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(type.equals(TreeNodeUtil.CONSTRAINT_XK_ITEM_TYPE)) {
			try {
				HHdbXk hhdbxk = (HHdbXk)treeNode.getNodeObject();
				String csvstr = htree.treeService.getAttrByMap(hhdbxk.getProp());
				String createsql = hhdbxk.getCreateSql();
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql(createsql);
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}else if(type.equals(TreeNodeUtil.RULE_TYPE)) {
			try {
				String csvstr = htree.treeService.getAttrCollectionRule(
						treeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getId());
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql("");
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}else if(type.equals(TreeNodeUtil.TABLE_TRIGGER_TYPE)) {
			try {
				String csvstr = htree.treeService.getAttrCollectionTrigger(
						treeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getId());
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql("");
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}else if(type.equals(TreeNodeUtil.VIEW_TYPE)) {
			try {
				String csvstr = htree.treeService.getAttrCollectionView(
						treeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getName());
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql("");
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}else if(type.equals(TreeNodeUtil.VIEW_ITEM_TYPE)) {
			try {
				HHdbView hhdbvw = (HHdbView)treeNode.getNodeObject();
				String csvstr = htree.treeService.getAttrByMap(hhdbvw.getProp());
				String createsql = hhdbvw.getCreateSql();
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql(createsql);
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}

		}else if(type.equals(TreeNodeUtil.SEQ_TYPE)) {
			try {
				String csvstr = htree.treeService.getAttrCollectionSequence(
						treeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getName());
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql("");
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}else if(type.equals(TreeNodeUtil.SEQ_ITEM_TYPE)) {
			try {
				HHdbSeq hhdbseq = (HHdbSeq)treeNode.getNodeObject();
				String csvstr = htree.treeService.getAttrByMap(hhdbseq.getProp());
				String createsql = hhdbseq.getCreateSql();
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql(createsql);
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}else if(type.equals(TreeNodeUtil.FUN_TYPE)) {
			try {
				String csvstr = htree.treeService.getAttrCollectionFuction(
						treeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getName());
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql("");
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}else if(type.equals(TreeNodeUtil.FUN_ITEM_TYPE)) {
			try {
				HHdbFun hhdbfun = (HHdbFun)treeNode.getNodeObject();
				String csvstr = htree.treeService.getAttrByMap(hhdbfun.getProp());
				String createsql = hhdbfun.getCreateSql();
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql(createsql);
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}else if(type.equals(TreeNodeUtil.TYPE_TYPE)) {
			try {
				String csvstr = htree.treeService.getAttrCollectionTypes(
						treeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getId());
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql("");
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}else if(type.equals(TreeNodeUtil.TYPE_ITEM_TYPE)) {
			try {
				String csvstr = htree.treeService.getAttrType(
						treeNode.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getId(),
						treeNode.getMetaTreeNodeBean().getId());
				String createsql = htree.treeService.getCreateSqlType(treeNode.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName(),
						treeNode.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getId(),
						treeNode.getMetaTreeNodeBean().getId());
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql(createsql);
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}else if(type.equals(TreeNodeUtil.SELECT_TYPE)) {
			defultsend();
		}else if(type.equals(TreeNodeUtil.SELECT_ITEM_TYPE)) {
			defultsend();
		}else if(type.equals(TreeNodeUtil.EXTENSION_TYPE)) {
			try {
				CmdEvent getsbEvent = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "GetServerBean");
				HHEvent revent = htree.sendEvent(getsbEvent);
				ServerBean serverbean = (ServerBean)revent.getObj();
				serverbean.setDBName(treeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getName());
				Connection conn = ConnService.createConnection(serverbean);
				ExtendXmlLoader exl = new ExtendXmlLoader(conn);
				String csvstr = exl.getAttrExtendCollection();
				conn.close();
				String createsql = "";
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql(createsql);
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}else if(type.equals(TreeNodeUtil.EXTENSION_PLUGIN_TYPE)) {
			try {
				CmdEvent getsbEvent = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "GetServerBean");
				HHEvent revent = htree.sendEvent(getsbEvent);
				ServerBean serverbean = (ServerBean)revent.getObj();
				serverbean.setDBName(treeNode.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName());
				Connection conn = ConnService.createConnection(serverbean);
				ExtendXmlLoader exl = new ExtendXmlLoader(conn);
				String csvstr = exl.getAttrExtend(treeNode);
				conn.close();
				String createsql = "";
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql(createsql);
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}else if(type.equals(TreeNodeUtil.PERFORMANCE_MONITORING_TYPE)) {
			defultsend();
		}else if(type.equals(TreeNodeUtil.TAB_SPACE_TYPE)) {
			defultsend();
		}else if(type.equals(TreeNodeUtil.TAB_SPACE_ITEM_TYPE)) {
			try {
				HHdbTabSp hhdbtabsp = (HHdbTabSp)treeNode.getNodeObject();
				String csvstr = htree.treeService.getAttrByMap(hhdbtabsp.getProp());
				String createsql = hhdbtabsp.getCreateSql();
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql(createsql);
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}else if(type.equals(TreeNodeUtil.LOGIN_ROLE_TYPE)) {
			defultsend();
		}else if(type.equals(TreeNodeUtil.LOGIN_ROLE_ITEM_TYPE)) {
			try {
				HHdbUser hhdbuser = (HHdbUser)treeNode.getNodeObject();
				String csvstr = htree.treeService.getAttrByMap(hhdbuser.getProp());
				String createsql = hhdbuser.getCreateSql();
				htree.treeService.sendAttr(csvstr,"");
				htree.treeService.sendCreateSql(createsql);
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}else if(type.equals(TreeNodeUtil.GROUP_TYPE)) {
			defultsend();
		}else if(type.equals(TreeNodeUtil.CPU_MONITORING_TYPE)) {
			defultsend();
		}else if(type.equals(TreeNodeUtil.DISK_MONITORING_TYPE)) {
			defultsend();
		}else if(type.equals(TreeNodeUtil.MEMORY_MONITORING_TYPE)) {
			defultsend();
		}else if(type.equals(TreeNodeUtil.COURSE_MONITORING_TYPE)) {
			defultsend();
		}else if(type.equals(TreeNodeUtil.NETWORK_MONITORING_TYPE)) {
			defultsend();
		} 
	}
	
	private void defultsend(){
		htree.treeService.sendAttr("","");
		htree.treeService.sendCreateSql("");
	}

}
