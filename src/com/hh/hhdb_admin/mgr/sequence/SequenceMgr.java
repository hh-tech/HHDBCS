package com.hh.hhdb_admin.mgr.sequence;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.LM;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangMgr;
import com.hh.frame.lang.LangUtil;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.engine.GuiMsgType;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.login.LoginMgr;
import com.hh.hhdb_admin.mgr.sequence.common.SeqCompUtil;
import com.hh.hhdb_admin.mgr.sequence.comp.SeqComp;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.sql.Connection;

public class SequenceMgr extends AbsGuiMgr{
	public static Connection conn;
	public static final String CMD_CREATE="CREATE_SEQ";
	public static final String CMD_DESIGN="DESIGN_SEQ";
	public static final String CMD_DELETE="DELETE_SEQ";
	public static final String CMD_RENAME="RENAME_SEQ";
	public static final String SEQ_NAME="SEQ_NAME";
	public static  String domainName=SequenceMgr.class.getName();
	private SeqComp seqC;

	static {
		LangMgr.merge(domainName, LangUtil.loadLangRes(SequenceMgr.class));
    }

	@Override
	public void init(JsonObject jObj) {

	}
	@Override
	public String getHelp() {
		StringBuilder sb=new StringBuilder();
		sb.append(GuiJsonUtil.genCmdHelp(CMD_CREATE, "新增序列,需要传入模式名",  GuiMsgType.RECE));
		sb.append(GuiJsonUtil.genCmdHelp(CMD_DESIGN, "设计序列，需要传入序列名及其模式名",  GuiMsgType.RECE));
		sb.append(GuiJsonUtil.genCmdHelp(CMD_DELETE, "删除序列，需要传入序列名及其模式名",  GuiMsgType.RECE));
		sb.append(GuiJsonUtil.genCmdHelp(CMD_RENAME, "重命名序列，需要传入序列名及其模式名",  GuiMsgType.RECE));
		return sb.toString();
	}

	@Override
	public CsMgrEnum getType() {

		return CsMgrEnum.SEQUENCE;

	}

	/**
	 * 处理事务
	 */
	@Override
	public void doPush(JsonObject msg) throws Exception {

		String cmd=GuiJsonUtil.toStrCmd(msg);
		String schemaName =GuiJsonUtil.toPropValue(msg,StartUtil.PARAM_SCHEMA);
		String seqName = GuiJsonUtil.toPropValue(msg,SEQ_NAME);
		if(conn==null||conn.isClosed()) {
			conn=getBean().getConn();
		}

		switch(cmd) {
		case SequenceMgr.CMD_CREATE:
			String newSeqName=JOptionPane.showInputDialog(StartUtil.parentFrame.getWindow(), getLang("inputSeq"),
			getLang("add"), JOptionPane.PLAIN_MESSAGE);
			if(newSeqName!=null) {
				if(StringUtils.isBlank(newSeqName)) {
					JOptionPane.showMessageDialog(null,SequenceMgr.getLang("notNull"));
				}else {
				seqC =getSeqComp(DriverUtil.getDbType(conn),schemaName,newSeqName,false);
				seqC.showCreate(getDialog());
				}
			}
			break;
		case SequenceMgr.CMD_DESIGN:
			seqC =getSeqComp(DriverUtil.getDbType(conn),schemaName,seqName,true);
			seqC.showDesgin(getDialog());
			break;
		case SequenceMgr.CMD_DELETE:
				seqC =getSeqComp(DriverUtil.getDbType(conn),schemaName,seqName,false);
				seqC.deleteSeq();
			break;
		case SequenceMgr.CMD_RENAME:
			//弹窗重命名
			Object newName = JOptionPane.showInputDialog(StartUtil.parentFrame.getWindow(), getLang("newSeqName"),
					getLang("rename"), JOptionPane.PLAIN_MESSAGE, null, null, seqName);
				if(newName != null){
					seqC =getSeqComp(DriverUtil.getDbType(conn),schemaName,seqName,false);
					seqC.reNameSeq(newName.toString());
				}
				break;
		default:
			LM.info(getClass(), cmd);
		}

	}

	@Override
	public JsonObject doCall(JsonObject msg) throws Exception {

		return null;
	}

	/**
	 * 多语言
	 * @author HuBingBing
	 * @date 2020年11月4日下午4:58:10
	 * @param key
	 * @return
	 * String
	 *
	 */
	 public static String getLang(String key) {
	       return LangMgr.getValue(domainName, key);
	 }

	 /**
	  * 序列主窗口弹窗
	  * @author HuBingBing
	  * @date 2020年11月4日下午4:58:29
	  * @return
	  * @throws Exception
	  * HDialog
	  *
	  */
	private HDialog getDialog() throws Exception {
		HDialog diaLog=StartUtil.getMainDialog();
		SeqCompUtil.setDiaLogSize(diaLog,DriverUtil.getDbType(conn));
		return diaLog;
	}
	/**
	 * 获取连接
	 * @author HuBingBing
	 * @date 2020年11月4日下午4:58:22
	 * @return
	 * @throws Exception
	 * Connection
	 *
	 */
	 public static LoginBean getBean(){

	        JsonObject jsonObj;
	        String loginId;
			try {
				jsonObj = StartUtil.eng.doCall(CsMgrEnum.LOGIN,
						GuiJsonUtil.genGetShareIdMsg(LoginMgr.ObjType.LOGIN_BEAN));
				loginId = GuiJsonUtil.toStrSharedId(jsonObj);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
	        return (LoginBean) StartUtil.eng.getSharedObj(loginId);

	    }
	 /**
	  * 刷新序列
	  * @author HuBingBing
	  * @date 2020年12月21日下午5:24:31
	  * void
	  *
	  */
	 public void refresh() {

			StartUtil.eng.doPush(CsMgrEnum.TREE,
										GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH)
										.add(TreeMgr.PARAM_NODE_TYPE,
										TreeMrType.SEQUENCE_GROUP.name()));

	 }
	 private SeqComp getSeqComp(DBTypeEnum type,String schema,String seq,boolean isEdit) {

		 SeqComp  se = new SeqComp(type, schema, seq, isEdit) {
			@Override
			protected void refreshSeq() {
				if(!isEdit)
				refresh();
			}
		};
		return se;
	 }

}
