package com.hhdb.csadmin.plugin.cmd;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.bean.ServerBean;
import com.hhdb.csadmin.common.dao.ConnService;
import com.hhdb.csadmin.common.util.EventUtil;
import com.hhdb.csadmin.plugin.cmd.console.ConsoleScrollPanel;

/**
 * <p>
 * Description: 字符sql插件类
 * </p>
 * <p>
 * Company: 恒辉
 * </p>
 * 
 * @author 张涛
 * @version 创建时间：2017年10月30日 上午9:59:33
 */

public class SqlCmdPlugin extends AbstractPlugin {
	// 字符sql窗口集合
	private Map<String, JPanel> jMap = new HashMap<>();
	// 连接集合
	private Map<String, Connection> cMap = new HashMap<>();
	// 窗口序号
	private int consoleNum = 1;

	// 事件接受方法实例化
	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent relevent = EventUtil.getReplyEvent(SqlCmdPlugin.class, event);
		if(event.getType().equals(EventTypeEnum.GET_OBJ.name())){
			CmdEvent getsbEvent = new CmdEvent(SqlCmdPlugin.class.getPackage().getName(), "com.hhdb.csadmin.plugin.conn", "GetServerBean");
			HHEvent revent = sendEvent(getsbEvent);
			ServerBean sb = (ServerBean)revent.getObj();
			Connection conn = null;
			try {
				conn = ConnService.createConnection(sb);
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
			}
			JPanel jPanel = new JPanel(new GridBagLayout());
			jPanel.add(new ConsoleScrollPanel(conn,this), new GridBagConstraints(0, 0,
					1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			relevent.setObj(jPanel);
			return relevent;
		}
		else if(event.getType().equals(EventTypeEnum.COMMON.name())){

				String componentId = initPlugin(null);
				String fromID = SqlCmdPlugin.class.getPackage().getName();
				String toID = "com.hhdb.csadmin.plugin.tabpane";
				CmdEvent addPanelEvent = new CmdEvent(fromID, toID, "AddPanelEvent");
				addPanelEvent.addProp("ICO", "cmdicon.png");
				addPanelEvent.setObj(jMap.get(componentId));
				addPanelEvent.addProp("TAB_TITLE", "命令行");
				addPanelEvent.addProp("COMPONENT_ID", "cmd_" + consoleNum);
				sendEvent(addPanelEvent);
				consoleNum++;
		}else if(event.getType().equals(EventTypeEnum.CMD.name())){
			// 窗口颜色设置事件
			if (event.getValue(EventTypeEnum.CMD.name()).equals("SetBackGroudEvent")) {
				if (jMap.size() > 0) {
					// 获取菜单栏设置的颜色
					String backgroud_olor = event.getValue(
							"backgroud_color");
					String font_color = event.getValue("font_color");
					// 遍历，改变颜色
					Set<Map.Entry<String, JPanel>> entrySet = jMap.entrySet();
					for (Map.Entry<String, JPanel> entry : entrySet) {
						ConsoleScrollPanel csp = getCSPComponent(entry.getValue());
						modifyColor(csp, strToColor(backgroud_olor),
								strToColor(font_color));
					}
				}
			}
			// 关闭字符sql窗口事件
			else if (event.getValue(EventTypeEnum.CMD.name()).equals("RemovePanelEvent")) {
					String componentId = event.getValue("COMPONENT_ID");
					Connection conn = cMap.get(componentId);
					try {
						conn.close();
					} catch (Exception e) {
						LM.error(LM.Model.CS.name(), e);
					}
					cMap.remove(componentId);
					jMap.remove(componentId);
				}
		}
		else{
			ErrorEvent errorEvent = new ErrorEvent(SqlCmdPlugin.class.getPackage().getName(),
					event.getFromID(),
					ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
			errorEvent.setErrorMessage(SqlCmdPlugin.class.getPackage().getName() + "不能接受如下类型的事件:\n"
					+ event.toString());
			return errorEvent;
		}
		
		return relevent;
	}

	/**
	 * 
	 * @param serverBean
	 *            连接对象参数
	 * @return
	 */
	private String initPlugin(ServerBean serverBean) {
		String componentId = "cmd_" + consoleNum;
		
		CmdEvent getsbEvent = new CmdEvent(SqlCmdPlugin.class.getPackage().getName(), "com.hhdb.csadmin.plugin.conn", "GetServerBean");
		HHEvent revent = sendEvent(getsbEvent);
		ServerBean sb = (ServerBean)revent.getObj();
		Connection conn = null;
		try {
			conn = ConnService.createConnection(sb);
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
		}
		JPanel jPanel = new JPanel(new GridBagLayout());
		jPanel.add(new ConsoleScrollPanel(conn,this), new GridBagConstraints(0, 0,
				1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		cMap.put(componentId, conn);
		jMap.put(componentId, jPanel);
		return componentId;
	}

	/**
	 * 
	 * @param csp
	 *            命令行sql容器
	 * @param cmdBackColor
	 *            背景色
	 * @param cmdFontColor
	 *            字体色
	 */
	private void modifyColor(ConsoleScrollPanel csp, Color cmdBackColor,
			Color cmdFontColor) {
		Component[] cpts = csp.getTextComponent();
		for (Component cpt : cpts) {
			cpt.setBackground(cmdBackColor);
			cpt.setForeground(cmdFontColor);
		}
	}

	private ConsoleScrollPanel getCSPComponent(JPanel jPanel) {
		Component cpt = jPanel.getComponent(0);
		ConsoleScrollPanel csp = (ConsoleScrollPanel) cpt;
		return csp;
	}

	@Override
	public Component getComponent() {
		return null;
	}

	private static Color strToColor(String color) {
		String[] colors = color.split(",");
		return new Color(Integer.parseInt(colors[0]),
				Integer.parseInt(colors[1]), Integer.parseInt(colors[2]));
	}
}
