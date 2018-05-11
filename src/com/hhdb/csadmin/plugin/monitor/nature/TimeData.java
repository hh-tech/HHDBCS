package com.hhdb.csadmin.plugin.monitor.nature;

import java.util.Map;

import org.jfree.data.time.Second;

public class TimeData {
	private Second second;
	private Map<String,Object> value;
	
	public TimeData(Second second,Map<String,Object> value){
		this.second=second;
		this.value=value;
	}
	public Second getSecond() {
		return second;
	}
	public void setSecond(Second second) {
		this.second = second;
	}
	public Map<String, Object> getValue() {
		return value;
	}
	public void setValue(Map<String, Object> value) {
		this.value = value;
	}
}
