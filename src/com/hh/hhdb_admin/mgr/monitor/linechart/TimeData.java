package com.hh.hhdb_admin.mgr.monitor.linechart;

import org.jfree.data.time.Second;

import java.util.Map;

public class TimeData {
	private final Second second;
	private Map<String,Object> value;
	
	TimeData(Second second, Map<String,Object> value){
		this.second=second;
		this.value=value;
	}
	public Second getSecond() {
		return second;
	}

	public Map<String, Object> getValue() {
		return value;
	}
	public void setValue(Map<String, Object> value) {
		this.value = value;
	}
}
