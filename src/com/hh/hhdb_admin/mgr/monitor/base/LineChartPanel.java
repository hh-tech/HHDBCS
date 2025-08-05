package com.hh.hhdb_admin.mgr.monitor.base;

import com.hh.frame.chart.option.ChartOption;
import com.hh.frame.chart.option.SeriesOption;
import com.hh.frame.chart.option.XAxisOption;
import com.hh.frame.chart.option.em.ChartType;
import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.util.SleepUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.json.JsonArray;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lg.Lg;
import com.hh.frame.ssh.SshExecOutMsg;
import com.hh.frame.ssh.jsch.JschExecTool;
import com.hh.frame.swingui.view.chart.HChartGui;
import com.hh.frame.swingui.view.chart.legend.HLegendGui;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.LabelInput;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LineChartPanel extends HPanel implements Runnable {
    private String script;

    private final Connection conn;

    private JschExecTool tool;

    private boolean isJsch;

    private int section = 10;

    private int refreshTime = 1000;

    private final Map<String, SeriesOption> seriesOptionMap = new LinkedHashMap<>();

    private List<TimeData> timeDataLists = new ArrayList<>();

    private final ChartOption chartOption;

    private final HChartGui chartGui;

    private final HLegendGui legendGui;

    private ScheduledExecutorService executor;

    private boolean isAddLegend = true;

    /**
     * 设置横轴数
     *
     * @param section 横轴数
     */
    public void setSection(int section) {
        this.section = section;
    }

    public void setRefreshTime(int refreshTime) {
        this.refreshTime = refreshTime * 1000;
    }

    LineChartPanel(String title, Connection conn) {
        this.conn = conn;
        this.chartOption = new ChartOption(getDefaultJson());
        this.chartGui = new HChartGui(chartOption);
        this.legendGui = new HLegendGui(chartOption.getLegend());
        LabelInput titleInput = new LabelInput(title, AlignEnum.CENTER);
        titleInput.setIcon(FaSolidEnum.BOLT_LIGHTNING,16);
        add(titleInput);
        LastPanel lastPanel = new LastPanel();
        lastPanel.setHead(legendGui.getBarPanel().getComp());
        lastPanel.set(chartGui.getComp());
        setLastPanel(lastPanel);
    }

    public LineChartPanel(String script, Connection conn, String title) {
        this(title, conn);
        this.isJsch = false;
        this.script = script;
    }

    public LineChartPanel(String script, JschExecTool tool, String title) {
        this(title, null);
        this.isJsch = true;
        this.script = script;
        this.tool = tool;
    }

    /**
     * 添加折线
     *
     * @param key          key
     * @param seriesOption seriesOption
     */
    public void addSeriesOption(String key, SeriesOption seriesOption) {
        seriesOptionMap.put(key, seriesOption);
    }

    /**
     * 调用执行
     */
    public void execute() throws Exception {
        run();
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this, 1000, refreshTime, TimeUnit.MILLISECONDS);
    }

    /**
     * 调用中断
     */
    public void shutdown() {
        if (null != executor) {
            executor.shutdown();
        }
    }

    @Override
    public void run() {
        Map<String, Object> map = getSeriesOption();
        chartOption.setXAxis((XAxisOption) map.get("xAxis"));
        if (isAddLegend) {
            isAddLegend = false;
            getLegends().forEach(legendGui::add);
        }
        Arrays.stream((SeriesOption[]) map.get("series")).forEach(chartGui::addOrUpdate);
    }

    private Map<String, String> getData() {
        try {
            if (isJsch) {
                List<String> columnNames = new ArrayList<>();
                List<Map<String, String>> columnValueList = new ArrayList<>();
                if (tool.isConnected()) {
                    SshExecOutMsg message = tool.run(script);
                    MonitorUtil.getColumnAndData(false, message.toOutText(), columnNames, columnValueList);
                }
                return columnValueList.size() == 0 ? new HashMap<>() : columnValueList.get(0);
            } else {
                return SqlQueryUtil.selectOneStrMap(conn, script);
            }
        } catch (Exception e) {
            Lg.error(LineChartPanel.class, e);
            executor.shutdown();
        }
        return new HashMap<>();
    }

    private JsonObject getDefaultJson() {

        JsonArray seriesArray = new JsonArray();
        JsonObject seriesJson = new JsonObject();
        seriesJson.add("data", new JsonArray());
        seriesJson.add("type", ChartType.line.name());
        seriesJson.add("name", "");

        JsonObject xAxisJson = new JsonObject();
        xAxisJson.set("type", "category");
        xAxisJson.set("data", new JsonArray());

        JsonObject yAxisJson = new JsonObject();
        yAxisJson.add("type", "value");

        JsonObject legendJson = new JsonObject();
        legendJson.add("data", new JsonArray());

        JsonObject defaultJson = new JsonObject();
        defaultJson.add("series", seriesArray);
        defaultJson.add("xAxis", xAxisJson);
        defaultJson.add("yAxis", yAxisJson);
        defaultJson.add("legend", legendJson);
        return defaultJson;
    }

    private Map<String, Object> getSeriesOption() {
        SleepUtil.sleep(refreshTime);
        SimpleDateFormat smf = new SimpleDateFormat("mm:ss");
        timeDataLists.add(new TimeData(smf.format(new Date()), getData()));
        if (timeDataLists.size() > section) {
            timeDataLists = timeDataLists.subList(timeDataLists.size() - section, timeDataLists.size());
        }
        SeriesOption[] seriesOptions = new SeriesOption[seriesOptionMap.size()];
        Map<String, Object> map = new HashMap<>();
        int index = 0;
        for (String key : seriesOptionMap.keySet()) {
            SeriesOption seriesOption = seriesOptionMap.get(key);
            SeriesOption newSeriesOption = new SeriesOption();
            newSeriesOption.setType(seriesOption.getType());
            newSeriesOption.setName(seriesOption.getName());
            Double[] data = new Double[timeDataLists.size()];
            JsonArray xArray = new JsonArray();
            for (int i = 0; i < timeDataLists.size(); i++) {
                data[i] = Double.parseDouble(timeDataLists.get(i).getData().get(key).replace("M", ""));
                xArray.add(timeDataLists.get(i).getTime());
            }
            newSeriesOption.addData(data);
            JsonObject xJson = new JsonObject();
            xJson.add("data", xArray);
            map.put("xAxis", new XAxisOption(xJson));
            seriesOptions[index] = newSeriesOption;
            index++;
        }
        map.put("series", seriesOptions);
        return map;
    }

    private List<String> getLegends() {
        List<String> legends = new ArrayList<>();
        for (String key : seriesOptionMap.keySet()) {
            legends.add(seriesOptionMap.get(key).getName());
        }
        return legends;
    }

}
