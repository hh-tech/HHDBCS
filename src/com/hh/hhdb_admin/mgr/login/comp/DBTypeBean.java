package com.hh.hhdb_admin.mgr.login.comp;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;

import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonArray;
import com.hh.frame.json.JsonObject;
import com.hh.frame.json.JsonValue;

public class DBTypeBean extends BaseJsonBean{
	public static final String NAME="name";
	public static final String URL="url";
	public static final String LOGO="logo";
	public static final String CLASS="class";
	public static final String DRIVER="driver";
	
	public DBTypeBean(){
	}
	
	public DBTypeBean(JsonObject obj){
		this.jobj = obj;
	}
	
	public void setDriver(JsonArray driver) {
		jobj.set(DRIVER, driver);
	}

	public JsonArray getDriver() {
		try {
			return jobj.get(DRIVER).asArray();
		}catch(Exception e) {
			return new JsonArray();
		}
	}
	
	
	public void setClass(String claz) {
		jobj.set(CLASS, claz);
	}

	public String getClazz() {
		return jobj.getString(CLASS);
	}
	
	
	public void setUrl(String name) {
		jobj.set(URL, name);
	}

	public String getUrl() {
		return jobj.getString(URL);
	}
	
	public void setLogo(String logo) {
		jobj.set(LOGO, logo);
	}

	public String getLogo() {
		return jobj.getString(LOGO);
	}
	
	public void setName(String name) {
		jobj.set(NAME, name);
	}

	public String getName() {
		return jobj.getString(NAME);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof DBTypeBean)) {
			return false;
		}
		DBTypeBean b = (DBTypeBean) obj;
		if(b.getJobj().equals(jobj)) {
			return true;
		}
		
		if(!ObjectUtils.equals(b.getName(), getName())) {
			return false;
		}
		
		if(!ObjectUtils.equals(b.getUrl(), getUrl())) {
			return false;
		}
		if(!ObjectUtils.equals(b.getClazz(), getClazz())) {
			return false;
		}
		if(!ObjectUtils.equals(b.getLogo(), getLogo())) {
			return false;
		}
		
		if(b.getDriver().size()!=getDriver().size()) {
			return false;
		}
		
		List<String> arr = new ArrayList<>();
		for(JsonValue c:b.getDriver()) {
			arr.add(c.asString());
		}
		for(JsonValue c:getDriver()) {
			if(!arr.contains(c.asString())) {
				return false;
			}
		}
		
		return true;
	}
}
