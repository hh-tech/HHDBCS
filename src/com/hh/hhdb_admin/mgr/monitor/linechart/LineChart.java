package com.hh.hhdb_admin.mgr.monitor.linechart;

/**
 * @author YuSai
 */
public class LineChart {

    private String name;

    private String value;

    LineChart(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
