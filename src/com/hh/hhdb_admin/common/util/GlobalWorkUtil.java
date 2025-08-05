package com.hh.hhdb_admin.common.util;

import java.awt.Component;
import java.io.File;
import java.sql.Connection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.FileEncodingBean;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.parser.sql_fmt2.SqlFmtUtil;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.data2sql.DataToSqlComp;
import com.hh.frame.swingui.view.fileChooser.HFileChooser;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.dlg.HTAreaFRDlg;
import com.hh.frame.swingui.view.textarea2.dlg.HTAreaGotoDlg;
import com.hh.frame.swingui.view.textarea2.sql.HSqlArea;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.bean.BaseOpEnum;
import com.hh.hhdb_admin.common.bean.SqlBookBean;
import com.hh.hhdb_admin.comp.SqlExeDlgTool;
/**
 * 
 * GlobalWorkUtil中的所有方法，必须在插件或者控件中多出使用
 * GlobalWorkUtil的方法都涉及StartUtil中的全局变量
 */
public class GlobalWorkUtil {
	private static HFileChooser saveTextChooser = new HFileChooser(HFileChooser.FileType.BOTH);
	private static PkgEncodingDlg encodingDlg=new PkgEncodingDlg();
	private static HDialog data2sqlDialog;
	private static DataToSqlComp data2sqlComp;
	private static HTAreaFRDlg findDlg;
	private static HTAreaGotoDlg gotoDlg;
	private static SqlExeDlgTool exeDlg=new SqlExeDlgTool();
	
	/**
	 * 用于查询器格式化和未登录工具中的格式化
	 * @param textArea -- 格式化文本域
	 */
	public static void formatSql(HTextArea textArea) {
		if (!StringUtils.isNotBlank(textArea.getText())) return;
		int breakWidth = CsVarUtil.CONF.getSqlFormatWith() == 0 ? 50 : CsVarUtil.CONF.getSqlFormatWith();
		
		StringBuffer sb = new StringBuffer();
		try {
			if (StringUtils.isNotEmpty(textArea.getSelectedText())) {
				sb.append(SqlFmtUtil.fmt2Str(textArea.getSelectedText(), breakWidth, 0).trim());
				textArea.getRSyntaxTextArea().replaceRange(sb.toString(), textArea.getRSyntaxTextArea().getSelectionStart(), textArea.getRSyntaxTextArea().getSelectionEnd());
			} else {
				sb.append(SqlFmtUtil.fmt2Str(textArea.getText(), breakWidth, 0).trim());
				textArea.getRSyntaxTextArea().replaceRange(sb.toString(), 0, textArea.getText().length());
			}
		} catch (Exception e) {
			PopDlgUtil.error(CsVarUtil.parentFrame, new Exception(LangMgr.lang(Word4.FORMAT_FAILED)));
		}
	}
	/**
	 * 保存文本到StartUtil指定的位置，并且提示保存成功
	 * 查询和命令窗口都可以调用该方法，进行保存
	 * @param txt
	 */
	public static void saveText(String txt){
		try {
			if (!StringUtils.isNotBlank(txt)) return;

			
			if (null != CsVarUtil.eng2) {
				SqlBookBean sqlBookBean = (SqlBookBean) CsVarUtil.eng2.doCall(CsMgrEnum.SQL_BOOK,new BaseOpBean(BaseOpEnum.GET_PATH));
				saveTextChooser.setPath(new File(sqlBookBean.getPath()));
			}
			saveTextChooser.addFilter("sql|txt文件","sql","txt");
			
			saveTextChooser.setDialogTitle(LangMgr.lang(Word2.SAVE));
			saveTextChooser.setApproveButtonText(LangMgr.lang(Word2.SAVE));
			if ( saveTextChooser.showOpenDialog() == HFileChooser.APPROVE_OPTION){
				FileUtils.writeStringToFile( saveTextChooser.getSelectedFile(), txt, "utf-8");
				PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.lang(Word4.SAVED_SUCCESSFULLY));
			}
		}catch (Exception e){
			err(e);
		}
	}
	
	public static HTAreaFRDlg getFrDlg() {
		if(findDlg==null) {
			findDlg=new HTAreaFRDlg(CsVarUtil.parentFrame.getWindow());
		}
		return findDlg;
	}
	
	public static HTAreaGotoDlg getAreaGotoDlg() {
		if(gotoDlg==null) {
			gotoDlg=new HTAreaGotoDlg(CsVarUtil.parentFrame.getWindow());
		}
		return gotoDlg;
	}

	private static void err(Exception e) {
		err(GlobalWorkUtil.class,e);
	}
	
	public static void err(Class<?> c,Exception e) {
		err(c, e,CsVarUtil.parentFrame.getWindow());
	}
	
	public static void err(Class<?> c, Exception e, Component parent) {
		Lg.error(c, e);
		PopDlgUtil.error(parent, e);
	}
	
	public static FileEncodingBean getTextFile(){
		encodingDlg.getWindow().setLocationRelativeTo(null);
		encodingDlg.show();
		return encodingDlg.getFileEncodingBean();
	
	}
	
	public static void checkConn() {
		if(!CsVarUtil.isConnected()) {
			boolean confirmed=PopDlgUtil.confirm(CsVarUtil.parentFrame, "连接已经断开，是否重新连接？");
			if(confirmed)
				CsVarUtil.doConn();
		}
	}
	
	public static boolean exeSql(JdbcBean jdbc,Connection conn,String sql){
		 return exeDlg.exeSql(jdbc,conn, sql);
	}
	
	public static void showDataToSqlDialog(HSqlArea codeArea,DataToSqlComp.Type type) {
		if(data2sqlDialog == null) {
			data2sqlDialog = new HDialog(CsVarUtil.parentFrame, 900);
			data2sqlComp = new DataToSqlComp();
			data2sqlDialog.setRootPanel(data2sqlComp);
			data2sqlDialog.setStatusBar(null);
		}
		data2sqlComp.setCodeArea(codeArea);
		data2sqlComp.doLoad(type);
		data2sqlDialog.show();
		
		
	}
	
}
