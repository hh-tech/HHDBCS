package com.hh.hhdb_admin.mgr.function2.debug.panel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.hh.frame.db_sql.info.SqlParamBean;
import com.hh.frame.db_sql.info.SqlParamBean.ParamType;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.hhdb_admin.mgr.function2.debug.base.ColNameCons;

class PkgUtil {
	
//	public static ExeInter getExeInter(DBTypeEnum type,DBTypeEnum.Version version,DebugPanel dp) {
//		if(DBTypeEnum.isOraSeries(type, version)) {
//			return new OrDebugTool(dp);
//		}else {
//			return  new PgDebugTool(dp);
//		}
//	}
//	
	
	
	public static boolean containResult(String sql2) {
		if(sql2.contains("RESULT := ")) {
			return true;
		}
		return false;
	};
	
	/**
	 * 获取source中没有的元素
	 * @param source
	 * @param target
	 * @return
	 */
	 public static List<Integer> getAdd(List<Integer> source, List<Integer> target){
	       List<Integer> result = new ArrayList<>();
	       for(Integer i:target) {
	    	   if(!source.contains(i)) {
	    		   result.add(i);
	    	   }
	       }
	       
	       return result;
	 }
	

	public static  List<Map<String, String>> toParamsTableData(List<SqlParamBean> beans) {
		List<Map<String, String>> maps = new ArrayList<>();
		for (SqlParamBean b : beans) {
			if (b.getParamType() == ParamType.IN || b.getParamType() == ParamType.INOUT) {
				Map<String, String> m = new HashMap<>();
				m.put(ColNameCons.NAME, b.getParamName());
				m.put(ColNameCons.TYPE, b.getDataType());
				m.put(ColNameCons.VALUE, "");
				maps.add(m);
			}

		}
		return maps;
	}

    
	protected static HeaderConfig getConfig(String name) {
		HeaderConfig config = new HeaderConfig(name);
		config.setDetachEnabled(false);
		config.setFixTab(true);
		config.setTitleEditable(false);
		return config;
	}
	
	 /**
     * 记录行与内容的map
     */
    static Map<Integer, int[]> getRowMap(String text) {
    	Map<Integer, int[]> map = new LinkedHashMap<>();
        String[] ss = text.split("\n");
        int js = 0;
        for (int i = 0; i < ss.length; i++) {
            int s = text.indexOf(ss[i], js);
            js = s + ss[i].length();
            int[] posi = {s, js};
            map.put((i + 1), posi);   //保存行号，以及起始结束位置
        }
        return map;
    }
	
    /**
     * 将oracle查询出来的堆载信息转换成表格数据
     * @param oracleStackInfo
     * @return
     */
    public static List<Map<String, String>> toStackTableData(String oracleStackInfo) {
    	 //1: name=,type=CURSOR,line=6  2: name=FN_MYMONTH,type=FUNCTION,line=8  3: name=PKG_DBGD,type=PACKAGE_BODY,line=2
        List<Map<String,String>> liMap = new LinkedList<>();
        List<String> vl = Arrays.asList(oracleStackInfo.split(":"));
        for (int i = 1; i < vl.size(); i++) {
            Map<String,String> map = new LinkedHashMap<>();
            String ss = vl.get(i).split(" {2}")[0].trim();
            String[] list = ss.split(",");
            String[] name = list[0].split("=");

            map.put(ColNameCons.NAME,name.length > 1 ? name[1].trim() : "");
            map.put(ColNameCons.TYPE,list[1].split("=")[1].trim());
            map.put(ColNameCons.ROW,list[2].split("=")[1].trim());
            map.put(ColNameCons.VALUE, "");
            liMap.add(map);
        }
        return liMap;
    }
	
	public static HeaderConfig newHeaderConfig(String name){
		HeaderConfig headerConfig = new HeaderConfig(name);
		headerConfig.setTitleEditable(false);
		headerConfig.setFixTab(true);
		headerConfig.setDetachEnabled(false);
		return headerConfig;
	}
}
