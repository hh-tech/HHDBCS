package com.hh.hhdb_admin.common.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.driver.ExtDriverMgr;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonArray;
import com.hh.frame.json.JsonObject;
import com.hh.frame.json.JsonValue;
import com.hh.frame.lg.Lg;
import com.hh.hhdb_admin.mgr.login.comp.DBTypeBean;

public class ExtDbTypeUtil {
	public static File DBTYPE_FILE = new File(CsVarUtil.getEtcFile(), "dbTypes.json");
	public static File EXT_FILE = new File(CsVarUtil.getEtcFile(), "ext");
	public static File LIB_FILE = new File(EXT_FILE,"lib");
	public static File ICON_FILE = new File(EXT_FILE,"icon");
	//通用的自己添加的可用数据库类型
	public static Map<String,DBTypeBean> selfDefinedDbTypeMaps = new HashMap<String,DBTypeBean>();
	
	
	
	/**
	 * 加载自己配置的DBType，初始化的时候加载一次
	 * @return
	 * @throws IOException
	 */
	public static void loadSelfDefineDbTypes() throws Exception {
		selfDefinedDbTypeMaps.clear();
		File f = DBTYPE_FILE;
		if(!f.exists()) {
			FileUtils.forceMkdirParent(f);
			f.createNewFile();
			FileUtils.write(f, "[]",StandardCharsets.UTF_8);
		}
		
		String s = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
		if(StringUtils.isBlank(s)) {
			return;
		}else {
			JsonArray arr = Json.parse(s).asArray();
			for(JsonValue v:arr) {
				JsonObject o = v.asObject();
				DBTypeBean bean = new DBTypeBean(o);
				selfDefinedDbTypeMaps.put(bean.getName(), bean);
				loadTypeJar(bean);
			}
		}
	};
	
	
	
	

	/**
	 * 保存自定义数据库类型到文件，并自动加载对应的jar
	 * @param bean 数据库类型对象
	 * @throws IOException 
	 */
	public static void saveAndLoadTypeJar(DBTypeBean bean) throws Exception {
		if(isSameExist(bean)) {
			return;
		};
		
		selfDefinedDbTypeMaps.put(bean.getName(), bean);
		loadTypeJar(bean);
		saveSelfDefineDbTypes();
	};
	
	

	/**
	 * 删除自定义数据库类型到文件
	 * @param bean 数据库类型对象
	 * @throws IOException 
	 */
	public static void removeTypeAndJar(String name) throws Exception {
		if(!selfDefinedDbTypeMaps.containsKey(name)) {
			return;
		}
		selfDefinedDbTypeMaps.remove(name);
		Lg.info(ExtDbTypeUtil.class, "移除驱动:" + name );
		ExtDriverMgr.unLoadJar(name);
		saveSelfDefineDbTypes();
	};
	
	
	
	/**
	 * java bean中的jar
	 * @param bean 数据库类型对象
	 * @throws IOException 
	 */
	private static void loadTypeJar(DBTypeBean bean) throws Exception {
		ArrayList<File> files = new ArrayList<File>();
		for(JsonValue name:bean.getDriver()) {
			Lg.info(ExtDbTypeUtil.class, "加载驱动文件:" + new File(LIB_FILE,name.asString()).getAbsolutePath());
			files.add(new File(LIB_FILE,name.asString()));
		}
		Lg.info(ExtDbTypeUtil.class, "加载驱动:" + bean.getName());
		ExtDriverMgr.loadJar(bean.getName(), files);
		
	};
	/**
	 * 是否存在相同的DBTypeBean
	 * @param bean
	 * @return
	 */
	private static boolean isSameExist(DBTypeBean bean) {
		if(selfDefinedDbTypeMaps.containsKey(bean.getName())) {
			if(selfDefinedDbTypeMaps.get(bean.getName()).equals(bean)) {
				Lg.debug(ExtDbTypeUtil.class, "驱动类型无变动不修改");
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 将selfDefinedDbTypeMaps的类型保存到文件
	 * @return
	 * @throws IOException
	 */
	private static void saveSelfDefineDbTypes() throws IOException {
		JsonArray arr = new JsonArray();
		for(Entry<String, DBTypeBean> b:selfDefinedDbTypeMaps.entrySet()) {
			arr.add(b.getValue().getJobj());
		}
		FileUtils.write(DBTYPE_FILE, arr.toPrettyString(),StandardCharsets.UTF_8);
		
	};
	
}
