package com.hh.hhdb_admin.mgr.monitor.base;

import java.util.Map;

public class TimeData {

	private String time;

	private Map<String, String> data;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public TimeData(String time, Map<String, String> data) {
		this.time = time;
		this.data = data;
	}

	@Override
	public String toString() {
		return "TimeData{" +
				"time='" + time + '\'' +
				", data=" + data +
				'}';
	}
}
