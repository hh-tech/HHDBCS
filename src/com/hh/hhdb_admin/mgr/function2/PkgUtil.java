package com.hh.hhdb_admin.mgr.function2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.text.BadLocationException;

import org.apache.commons.lang3.StringUtils;
import org.fife.ui.rtextarea.GutterIconInfo;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.ClassLoadUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.db_sql.ProcFunSqlUtil;
import com.hh.frame.db_sql.conn.ProFunConnUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.FrameL;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;

class PkgUtil {
	
	
	

	
	public static void checkFunction(DBTypeEnum dbtype, DBTypeEnum.Version ver, Connection conn,
			String schema,String oid) throws Exception {
		if(DBTypeEnum.isPgSeries(dbtype,ver)) {
			 String check = ProFunConnUtil.checkFunction(dbtype, ver, conn, schema, oid);
			 if(StringUtils.isBlank(check)) {
				 check = LangMgr.sLang(FrameL.VALIDATION_SUCCESS);
			 }
			 PopDlgUtil.info(CsVarUtil.parentFrame, check);
		}
		
	}
	
	public static boolean supportDebug(DBTypeEnum dbtype, DBTypeEnum.Version ver, Connection conn,
			
			String schema,String name,String objId,boolean isFun) throws Exception {
		if(DBTypeEnum.isPgSeries(dbtype,ver)) {
			String sql = ProFunConnUtil.getProcFunSql(CsVarUtil.getDbtype(), CsVarUtil.jdbc.getCurrentVersion(), CsVarUtil.conn, schema, name, objId, isFun);
			if(!hasContent(verifyFun(sql))){
				throw new Exception(LangMgr.sLang(CsL.NOT_DEBUG) + ",函数/过程方法体为空或缺少BEGIN/END关键字");
			};
			
		}
		return true;
	}
	 /**
     * 验证函数是否有内容
     * @param str
     * @return
     */
    public static String verifyFun(String str) {
        String result = "";
        str = str.trim().toUpperCase();
        if (str.contains("BEGIN")) {
            String[] s2 = str.split("BEGIN")[1].split("END");
            if (s2 != null) {
                result = s2[0];
            }
        }
        
       
        return result.trim();
    }
    
    
    public static boolean hasContent(String str) {
    	if(StringUtils.isBlank(str)) {
    		return false;
    	}
    	 String[] s = str.split("\n");
         boolean hasContent = false;
         for(String line:s) {
         	line.replaceAll("\t", "");
         	if(line.trim().startsWith("--")) {
         		continue;
         	}else {
         		hasContent = true;
         	}
         }
    	return hasContent;
    }
	
	
	private static void installAdpDebug(Connection conn) throws IOException, SQLException {
		String sql= ClassLoadUtil.loadTextRes(FunctionMgr.class, "adp_debug.sql");
        SqlExeUtil.batchExecute(conn, Arrays.asList(sql.split("//")));
	}
	
	public static void examine(DBTypeEnum dbtype, DBTypeEnum.Version ver, Connection conn,
			String schema) throws Exception {
		
		String schemaStr = schema==null?CsVarUtil.jdbc.getUser():schema;
		String str = ProcFunSqlUtil.getExamineSql(dbtype, ver, schemaStr);
		if(!StringUtils.isBlank(str)) {
			List<Map<String, String>> list = SqlQueryUtil.selectStrMapList(conn, str);
			boolean notIstalled = list == null||list.isEmpty();
			if(notIstalled) {
				if(DBTypeEnum.isOraSeries(dbtype, ver)) {
					installAdpDebug(conn);
				}else if(DBTypeEnum.isPgSeries(dbtype, ver)) {
					throw new Exception("请安装调试扩展pldbgapi！");
				}
			}
			
		}
		
		
		
	}
	
	public static String getReturnType(String defineText) {
		int index = defineText.toLowerCase().indexOf("return");
		if(index!=-1) {
			String left = defineText.substring(index+6).trim();
			return left.split("\\s")[0].trim();
		}
		return null;
	}
	
	
	
	/**
     * 获取所有标上书签的行
     * @return
	 * @throws BadLocationException 
     */
    public static List<Integer> getbookmaskLines(HTextArea area) throws BadLocationException {
        List<Integer> lines = new ArrayList<Integer>();
        for (GutterIconInfo info : area.getGutter().getBookmarks()) {
                lines.add(area.getRSyntaxTextArea().getLineOfOffset(info.getMarkedOffset()) + 1);
        }
        return lines;
    }
    
    
    

    
}
