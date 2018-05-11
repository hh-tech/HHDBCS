package com.hhdb.csadmin.plugin.monitor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.EventUtil;
import com.hhdb.csadmin.common.util.ExtendXmlLoader;
import com.hhdb.csadmin.plugin.monitor.nature.BioStatsSeries;
import com.hhdb.csadmin.plugin.monitor.nature.SessionStatsSeries;
import com.hhdb.csadmin.plugin.monitor.nature.TiStatsSeries;
import com.hhdb.csadmin.plugin.monitor.nature.ToStatsSeries;
import com.hhdb.csadmin.plugin.monitor.nature.TpsStatsSeries;

/**
 * 性能监控
 * 
 * @author hh
 */
public class DBMonitor extends AbstractPlugin {
	private JPanel jPanel = new JPanel(new GridBagLayout());
	// 性能监控
	private SessionStatsSeries sessionSeries;
	private TpsStatsSeries tpsSeries;
	private TiStatsSeries tiSeries;
	private ToStatsSeries toSeries;
	private BioStatsSeries bioSeries;
	private ScheduledExecutorService executor = null;
	private CPUMinitor monitorCpu;
	private MemMinitor monitorMem;
	private DiskMinitor monitorDisk;
	private NetMonitor monitorNet;

	private static String COMPONENT_ID_CPUMINITOR = "CPUMinitor";
	private static String COMPONENT_ID_MEMMINITOR = "MemMinitor";
	private static String COMPONENT_ID_DBMONITOR = "DBMonitor";
	private static String COMPONENT_ID_DISKMINITOR = "DiskMinitor";
	private static String COMPONENT_ID_NETMONITOR = "NetMonitor";

	public DBMonitor() {
	}

	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent reply = EventUtil.getReplyEvent(DBMonitor.class, event);
		if (event.getType().equalsIgnoreCase(EventTypeEnum.CMD.name())) {
			Connection conn = null;
			CmdEvent getconnEvent = new CmdEvent("com.hhdb.csadmin.plugin.monitor","com.hhdb.csadmin.plugin.conn", "GetConn");
			HHEvent refevent = sendEvent(getconnEvent);
			if(!(refevent instanceof ErrorEvent)){
				conn = (Connection)refevent.getObj();
			}
			if(conn!=null){
				ExtendXmlLoader exl = new ExtendXmlLoader(conn);
				try {
					if(!exl.isTrue(exl.getExtendList(),"sys_util")){
						exl.installExtend("sys_util");
					}
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
					JOptionPane.showMessageDialog(null, "所连接的数据库中不能安装sys_util插件，不能进行监控。\n"+e.getMessage(), "警告", JOptionPane.WARNING_MESSAGE);
					return refevent;
				}
			}else{
				return refevent;
			}
			if (event.getValue(EventTypeEnum.CMD.name()).equalsIgnoreCase(
					"RemovePanelEvent")) {
				String componentId = event.getValue("COMPONENT_ID");
				if (componentId.equalsIgnoreCase(COMPONENT_ID_DBMONITOR)) {
					if (executor != null) {
						executor.shutdownNow();
						executor = null;
						sessionSeries.closeConnection();
						sessionSeries = null;
						tpsSeries.closeConnection();
						tpsSeries = null;
						tiSeries.closeConnection();
						tiSeries = null;
						toSeries.closeConnection();
						toSeries = null;
						bioSeries.closeConnection();
						bioSeries = null;
					}
				} else if (componentId
						.equalsIgnoreCase(COMPONENT_ID_CPUMINITOR)) {
					if (monitorCpu != null) {
						monitorCpu.closeRunable();
						monitorCpu = null;
					}

				} else if (componentId
						.equalsIgnoreCase(COMPONENT_ID_MEMMINITOR)) {
					if (monitorMem != null) {
						monitorMem.closeRunable();
						monitorMem = null;
					}

				} else if (componentId
						.equalsIgnoreCase(COMPONENT_ID_DISKMINITOR)) {
					if (monitorDisk != null) {
						monitorDisk.closeRunable();
						monitorDisk = null;
					}

				} else if (componentId
						.equalsIgnoreCase(COMPONENT_ID_NETMONITOR)) {
					if (monitorNet != null) {
						monitorNet.closeRunable();
						monitorNet = null;
					}
				}

			} else if (event.getValue(EventTypeEnum.CMD.name())
					.equalsIgnoreCase("CPUMinitor")) {
				
				if (monitorCpu == null) {
					jPanel = new JPanel(new GridBagLayout());
					jPanel.setBackground(Color.white);
					monitorCpu = new CPUMinitor(this);
					jPanel.add(monitorCpu, new GridBagConstraints(0, 0, 1, 1, 1.0,
							0.9, GridBagConstraints.NORTHWEST,
							GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0,
							0));
				}
				
				String fromID = DBMonitor.class.getPackage().getName();
				String toID = "com.hhdb.csadmin.plugin.tabpane";
				CmdEvent addPanelEvent = new CmdEvent(fromID, toID,
						"AddPanelEvent");
				addPanelEvent.addProp("TAB_TITLE", "CPU监控");
				addPanelEvent.addProp("COMPONENT_ID", COMPONENT_ID_CPUMINITOR);
				addPanelEvent.addProp("ICO", "chart.png");
				addPanelEvent.setObj(jPanel);
				sendEvent(addPanelEvent);
			} else if (event.getValue(EventTypeEnum.CMD.name())
					.equalsIgnoreCase("MemMinitor")) {
				
				if (monitorMem == null) {
					jPanel = new JPanel(new GridBagLayout());
					jPanel.setBackground(Color.white);
					monitorMem = new MemMinitor(this);
					jPanel.add(monitorMem, new GridBagConstraints(0, 0, 1, 1, 1.0,
							0.9, GridBagConstraints.NORTHWEST,
							GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0,
							0));
				}
				
				String fromID = DBMonitor.class.getPackage().getName();
				String toID = "com.hhdb.csadmin.plugin.tabpane";

				CmdEvent addPanelEvent = new CmdEvent(fromID, toID,
						"AddPanelEvent");
				addPanelEvent.addProp("TAB_TITLE", "内存监控");
				addPanelEvent.addProp("COMPONENT_ID", COMPONENT_ID_MEMMINITOR);
				addPanelEvent.addProp("ICO", "chart.png");
				addPanelEvent.setObj(jPanel);
				sendEvent(addPanelEvent);
			} else if (event.getValue(EventTypeEnum.CMD.name())
					.equalsIgnoreCase("DiskMinitor")) {
				
				if (monitorDisk == null) {
					jPanel = new JPanel(new GridBagLayout());
					jPanel.setBackground(Color.white);
					monitorDisk = new DiskMinitor(this);
					jPanel.add(monitorDisk, new GridBagConstraints(0, 0, 1, 1, 1.0,
							0.9, GridBagConstraints.NORTHWEST,
							GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0,
							0));
				}
				
				String fromID = DBMonitor.class.getPackage().getName();
				String toID = "com.hhdb.csadmin.plugin.tabpane";

				CmdEvent addPanelEvent = new CmdEvent(fromID, toID,
						"AddPanelEvent");
				addPanelEvent.addProp("TAB_TITLE", "硬盘监控");
				addPanelEvent.addProp("COMPONENT_ID", COMPONENT_ID_DISKMINITOR);
				addPanelEvent.addProp("ICO", "chart.png");
				addPanelEvent.setObj(jPanel);
				sendEvent(addPanelEvent);
			} else if (event.getValue(EventTypeEnum.CMD.name())
					.equalsIgnoreCase("NetMonitor")) {
				
				if (monitorNet == null) {
					jPanel = new JPanel(new GridBagLayout());
					jPanel.setBackground(Color.white);
					monitorNet = new NetMonitor(this);
					jPanel.add(monitorNet, new GridBagConstraints(0, 0, 1, 1, 1.0,
							0.9, GridBagConstraints.NORTHWEST,
							GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0,
							0));
				}
				
				String fromID = DBMonitor.class.getPackage().getName();
				String toID = "com.hhdb.csadmin.plugin.tabpane";

				CmdEvent addPanelEvent = new CmdEvent(fromID, toID,
						"AddPanelEvent");
				addPanelEvent.addProp("TAB_TITLE", "网络监控");
				addPanelEvent.addProp("COMPONENT_ID", COMPONENT_ID_NETMONITOR);
				addPanelEvent.addProp("ICO", "chart.png");
				addPanelEvent.setObj(jPanel);
				sendEvent(addPanelEvent);
			} else if (event.getValue(EventTypeEnum.CMD.name())
					.equalsIgnoreCase("DBMonitor")) {
				boolean flag = false;
				if(executor==null){
					executor = Executors.newScheduledThreadPool(5);
					flag = true;
				}
				
				if(flag){

					jPanel = new JPanel(new GridBagLayout());
					if (sessionSeries == null) {
						sessionSeries = new SessionStatsSeries(executor, this);
					}
					if (tpsSeries == null) {
						tpsSeries = new TpsStatsSeries(executor, this);
					}
					if (tiSeries == null) {
						tiSeries = new TiStatsSeries(executor, this);
					}
					if (toSeries == null) {
						toSeries = new ToStatsSeries(executor, this);
					}
					if (bioSeries == null) {
						bioSeries = new BioStatsSeries(executor, this);
					}
					jPanel.setBackground(Color.WHITE);
					sessionSeries.setBackground(Color.WHITE);
					tpsSeries.setBackground(Color.WHITE);
					tiSeries.setBackground(Color.WHITE);
					toSeries.setBackground(Color.WHITE);
					bioSeries.setBackground(Color.WHITE);

					JPanel panel1 = new JPanel();
					panel1.setBackground(Color.WHITE);
					panel1.setLayout(new GridBagLayout());
					panel1.add(sessionSeries,
							new GridBagConstraints(0, 0, 1, 1, 0.5, 1.0,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.BOTH, new Insets(10, 10, 0,
											0), 0, 0));
					panel1.add(tpsSeries,
							new GridBagConstraints(1, 0, 1, 1, 0.5, 1.0,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.BOTH, new Insets(10, 10, 0,
											0), 0, 0));

					JPanel panel2 = new JPanel();
					panel2.setBackground(Color.WHITE);
					panel2.setLayout(new GridBagLayout());
					panel2.add(tiSeries,
							new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.BOTH, new Insets(10, 10, 0,
											0), 0, 0));
					panel2.add(toSeries,
							new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.BOTH, new Insets(10, 10, 0,
											0), 0, 0));
					panel2.add(bioSeries,
							new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.BOTH, new Insets(10, 10, 0,
											0), 0, 0));

					jPanel.add(panel1, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.5,
							GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
							new Insets(10, 10, 0, 0), 0, 0));
					jPanel.add(panel2, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.5,
							GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
							new Insets(10, 10, 0, 0), 0, 0));
										
					executor.scheduleAtFixedRate(sessionSeries, 1000, 1000,
							TimeUnit.MILLISECONDS);
					executor.scheduleAtFixedRate(tpsSeries, 1000, 1000,
							TimeUnit.MILLISECONDS);
					executor.scheduleAtFixedRate(tiSeries, 1000, 1000,
							TimeUnit.MILLISECONDS);
					executor.scheduleAtFixedRate(toSeries, 1000, 1000,
							TimeUnit.MILLISECONDS);
					executor.scheduleAtFixedRate(bioSeries, 1000, 1000,
							TimeUnit.MILLISECONDS);
				}
				
				String fromID = DBMonitor.class.getPackage().getName();
				String toID = "com.hhdb.csadmin.plugin.tabpane";

				CmdEvent addPanelEvent = new CmdEvent(fromID, toID,
						"AddPanelEvent");
				addPanelEvent.addProp("TAB_TITLE", "库监控");
				addPanelEvent.addProp("COMPONENT_ID", COMPONENT_ID_DBMONITOR);

				addPanelEvent.addProp("ICO", "chart.png");
				addPanelEvent.setObj(jPanel);
				sendEvent(addPanelEvent);
			}
		}	
		return reply;
	}

	@Override
	public Component getComponent() {
		return jPanel;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("asdfdafs");
		DBMonitor eventTest = new DBMonitor();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1024, 768);
		frame.getContentPane().add((Component) eventTest.getComponent(),
				BorderLayout.CENTER);
		frame.setVisible(true);
	}

}
