package com.hhdb.csadmin.plugin.menu.util;

import java.util.List;
import java.util.Map;
import java.util.Vector;

public class VectorUtil {
	public static Vector<Object> initVector(List<String> os) {
		Vector<Object> colNames = new Vector<Object>();
		for (String key : os) {
			colNames.add(key);
		}
		return colNames;
	}
	
	public static Vector<Object> initVectorData(List<List<String>> data){
		Vector<Object> vectdatas = new Vector<Object>();
		for (int i=1;i<data.size();i++) {
			Vector<Object> item = new Vector<Object>();
			for (String itemdata : data.get(i)) {
				item.add(itemdata);
			}
			vectdatas.add(item);
		}
		return vectdatas;
	}

	public static Vector<Object> initVector(Map<String, Object> map, List<String> keys, List<String> values) {
		Vector<Object> colNames = new Vector<Object>();
		int i = 0;
		for (String key : keys) {
			Vector<Object> item = new Vector<Object>();
			item.add(key);
			item.add(map.get(values.get(i)));
			colNames.add(item);
			i++;
		}
		return colNames;
	}

	public static Vector<Object> initVector(String... os) {
		Vector<Object> colNames = new Vector<Object>();
		for (String key : os) {
			colNames.add(key);
		}
		return colNames;
	}

	public static Vector<Object> initVector(List<Map<String, Object>> list, List<String> os) {
		Vector<Object> colNames = new Vector<Object>();
		for (Map<String, Object> map : list) {
			Vector<Object> item = new Vector<Object>();
			for (String key : os) {
				item.add(map.get(key));
			}
			colNames.add(item);
		}
		return colNames;
	}
}
