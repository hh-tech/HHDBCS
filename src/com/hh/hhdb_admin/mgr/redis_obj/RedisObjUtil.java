package com.hh.hhdb_admin.mgr.redis_obj;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.redis.base.RedisAbsKeyObj;
import com.hh.frame.redis.base.RedisObjType;
import com.hh.frame.redis.db.*;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HWindow;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.with_label.LabelConf;
import com.hh.frame.swingui.view.with_label.WithLabelInput;
import com.hh.hhdb_admin.common.CsL;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.*;
import java.util.regex.Pattern;

public class RedisObjUtil {

    public static WithLabelInput getLabelInput(String label, AbsInput input) {
        LabelConf labelConf = new LabelConf(GridSplitEnum.C3, AlignEnum.RIGHT,AlignEnum.LEFT);
        return new WithLabelInput(labelConf, new LabelInput(label), input);
    }
	
	public static HDivLayout getLayout(Integer xGap, Integer yGap,Integer top,Integer bottom, GridSplitEnum... grids){
		HDivLayout divLayout = new HDivLayout(xGap, yGap, grids);
		divLayout.setTopHeight(top);
		divLayout.setBottomHeight(bottom);
		return divLayout;
	}
	
	/**
	 * 判断是否为浮点数
	 * @param value
	 * @param dialog
     */
	public static boolean isNumeric(String value, HWindow dialog) {
		Pattern pattern = Pattern.compile("^[-+]?(0|[1-9]\\d*)(\\.\\d+)?$");
		if (StringUtils.isNotBlank(value) && pattern.matcher(value).matches()) {
		    return true;
        } else {
			PopDlgUtil.info(dialog, LangMgr.sLang(CsL.CORRECT_FIGURE));
			return false;
		}
    }
	
	public static RedisAbsKeyObj getRedisObj(Connection conn,String key){
        return new RedisAbsKeyObj(conn, key) {
            @Override
            public RedisObjType getType() {
                return null;
            }

			@Override
			public String getAddCmd() {
				return null;
			}
        };
    }
    
    /**
     * 保存
     * @param conn
     * @param name  键名
     * @param text  文本框的值
     * @param type  对象类型
     * @param listRowBean
     * @throws Exception
     */
    public static void saveRdisData(Connection conn,String name,String text,RedisObjType type,List<HTabRowBean> listRowBean) throws Exception{
        switch (type) {
            case string:
                RedisStringObj strObj = new RedisStringObj(conn, name);
                strObj.setValue(text);
                break;
            case hash:
                RedisHashObj hashObj = new RedisHashObj(conn, name);
                listRowBean.forEach(e -> {
                    Map<String,String> hashMap=new HashMap<>();
                    hashMap.put(e.getCurrRow().get("Value"), e.getCurrRow().get("Key"));
                    hashObj.setMap(hashMap);
                });
                break;
            case zset:
                RedisZsetObj zsetObj = new RedisZsetObj(conn, name);
                listRowBean.forEach(e -> {
                    Map<String,Double> zsetMap=new HashMap<>();
                    zsetMap.put(e.getCurrRow().get("Value"), Double.valueOf(e.getCurrRow().get("Key")));
                    zsetObj.setValues(zsetMap);
                });
                break;
            case set:
                RedisSetObj setObj = new RedisSetObj(conn, name);
                listRowBean.forEach(e -> {
                    Set<String> set = new HashSet<>();
                    set.add(e.getCurrRow().get("Value"));
                    setObj.setValues(set);
                });
                break;
            case list:
                RedisListObj listObj = new RedisListObj(conn, name);
                listRowBean.forEach(e -> {
                    List<String> list = new ArrayList<>();
                    list.add(e.getCurrRow().get("Value"));
                    listObj.setValues(list);
                });
                break;
            case json:
                RedisJsonObj jsonObj=new RedisJsonObj(conn, name);
                jsonObj.setJsonValue(text);
                break;
            default:
        }
    }
    
    public static void addCol(HTable table) {
        Map<String, String> line = new HashMap<>();
        line.put("Key","");
        line.put("Value","");
        line.put("Operation","");
        table.add(line);
    }
}
