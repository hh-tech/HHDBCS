package com.hhdb.csadmin.plugin.menu;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.EventUtil;
import com.hhdb.csadmin.plugin.menu.entity.HMenu;
import com.hhdb.csadmin.plugin.menu.entity.HMenuItem;
import com.hhdb.csadmin.plugin.menu.ui.CurrentConnectPanel;
import com.hhdb.csadmin.plugin.menu.ui.CurrentDealPanel;
import com.hhdb.csadmin.plugin.menu.ui.LockInfoPanel;
import com.hhdb.csadmin.plugin.menu.ui.PropertiesPanel;
import com.hhdb.csadmin.plugin.menu.ui.ServerPanel;
import com.hhdb.csadmin.plugin.menu.util.MenuXmlUtil;
public class Hmenu extends AbstractPlugin implements ActionListener {
	private static 	JMenuBar menubar = new JMenuBar();// 菜单栏
	private static List<HMenuItem> hitems;
	private static List<JMenuItem> jitems;
	public Hmenu() {
		super();
		List<HMenu> hmenus = MenuXmlUtil.parseHMenus();
		hitems=new ArrayList<HMenuItem>();
		jitems=new ArrayList<JMenuItem>();
		for (HMenu hmenu : hmenus) {
			JMenu menu = parseMenu(hmenu);
			menubar.add(menu);
		}
	}

	private JMenuItem parseItem(HMenuItem o) {
		String text = o.getName();
		String icon = o.getIcon();
		ImageIcon logo = new ImageIcon(icon);
		JMenuItem jitem = new JMenuItem(text, logo);
		jitem.setPreferredSize(new Dimension(250, 20));
		String key=o.getKey();
		if(key!=null){
			
			key=(((key).replace("+", " ").toUpperCase().replace("CTRL", "ctrl")).replace("SHIFT", "shift")).replace("ALT", "alt");
		}
		jitem.setAccelerator(KeyStroke.getKeyStroke(key));
		//jitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA,InputEvent.CTRL_MASK,true));
		jitem.addActionListener(this);
		return jitem;

	}

	private JMenu parseMenu(HMenu hmenu) {

		String name = hmenu.getName();

		List<Object> items = hmenu.getMenuItems();
		JMenu menu = new JMenu(name);
		for (int i = 0; i < items.size(); i++) {
			Object o = items.get(i);
			if (o instanceof HMenuItem) {
				hitems.add((HMenuItem)o);
				JMenuItem jItem = parseItem((HMenuItem) o);
				jitems.add(jItem);
				menu.add(jItem);
			} else if (o instanceof HMenu) {
				JMenu menu0 = parseMenu((HMenu) o);
				menu.add(menu0);
			}
		}
		return menu;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String fromID = "com.hhdb.csadmin.plugin.menu";
		String toID = "com.hhdb.csadmin.plugin.menu";
		String cmd = e.getActionCommand();
		for(HMenuItem hItem:hitems){
			String id=hItem.getId();
			String name=hItem.getName();
			toID=hItem.getTo();
			if (cmd.equals(name)) {
				CmdEvent event = new CmdEvent(fromID, toID, id);
				sendEvent(event);
			}
		}
	}

	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent toevent = EventUtil.getReplyEvent(Hmenu.class, event);
		String type = event.getType();
		if(type.equals(EventTypeEnum.GET_OBJ.name())){
			toevent.setObj(menubar);
			return toevent;
		}
		else if(type.equals(EventTypeEnum.CMD.name())){
			CmdEvent cmdEvent=(CmdEvent)event;
			String cmd = cmdEvent.getCmd();
			if(cmd.equals("exit")){
				exit();	
			}else if(cmd.equals("options")){
				option();
			}else if(cmd.equals("about")){
				about();
			}else if(cmd.equals("dbmonitoring")){
				dbmonitoring();
			}else if(cmd.equals("dbLock")){
				dbLock();
			}else if(cmd.equals("off")){
				off(cmdEvent.getValue("PARAM"));
			}else if(cmd.equals("RemovePanelEvent")){
				toevent.addProp("COMPONENT_ID",cmdEvent.getValue("COMPONENT_ID"));
			}
		}else{
			ErrorEvent errorEvent = new ErrorEvent("com.hhdb.csadmin.plugin.menu",event.getFromID(),ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
			errorEvent.setErrorMessage("com.hhdb.csadmin.plugin.menu" + "不能接受如下事件:\n"+ event.toString());
			return errorEvent;
		}
	
		return toevent;
	}
	/**
	 * 禁用
	 * @param event 
	 */
	private void off(String param) {
		// TODO Auto-generated method stub
		 Gson gson = new Gson();  
		 JsonParser parser = new JsonParser();
		 JsonArray jsonArray = parser.parse(param).getAsJsonArray();
		 List<Map<String, String>> list=new ArrayList<Map<String, String>>();
		  for (JsonElement element : jsonArray) {
		        //使用GSON，直接转成Bean对象
			  @SuppressWarnings("unchecked")
			Map<String,String> map = gson.fromJson(element, Map.class);
			  list.add(map);
		    }
		for (int i=0;i<list.size();i++){
			 Map<String, String> map=list.get(i);
			 String off=map.get("off");
			 String name=map.get("name");
			 changeOff(name,off);
		}
		
	
	}
	/**
	 * 改变菜单项禁用
	 * @param name
	 * @param off
	 */
	private void changeOff(String name, String off) {
		for(JMenuItem jitem:jitems){
			String cmd=jitem.getActionCommand();
			if(name.equals(cmd)){
				if(off.equals("1")){
					jitem.setEnabled(false);
				}
				if(off.equals("0")){
					jitem.setEnabled(true);
				}
			}
		}
		
	}

	private void exit() {
		int option = JOptionPane.showConfirmDialog(null, "确定要退出吗？", " 提示", JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION == option) {
			// 点击了确定按钮
			System.exit(0);
		}
		
	}

	/**
	 * 数据库锁信息
	 */
	private void dbLock(){
		String fromID = "com.hhdb.csadmin.plugin.menu";
		String toID = "com.hhdb.csadmin.plugin.tabpane";
		String sertableId = UUID.randomUUID().toString();
		LockInfoPanel lockpanel = new LockInfoPanel(this, sertableId);
		/*ImageIcon imageIcon = new ImageIcon(
				EventTest.class.getResource("/icon/sstatus.png"));
		HHEvent event = new HHEvent(fromID, toID, "AddPanelEvent",
				lockpanel, imageIcon);*/
		CmdEvent event =new CmdEvent(fromID, toID, "AddPanelEvent");
		event.setObj(lockpanel);
		event.addProp("ICO", "sstatus.png");
		event.addProp("TAB_TITLE", "数据库锁信息");
		event.addProp("COMPONENT_ID", "dbLock");
		sendEvent(event);
	
	}
	/**
	 * 数据库监控
	 */
	private void dbmonitoring() {
		String fromID = "com.hhdb.csadmin.plugin.menu";
		String toID = "com.hhdb.csadmin.plugin.tabpane";
		String ConnTableId = UUID.randomUUID().toString();
		String dealTableId = UUID.randomUUID().toString();
		CurrentConnectPanel currentConnectPanel = new CurrentConnectPanel(this,
				ConnTableId);
		CurrentDealPanel currentDealPanel = new CurrentDealPanel(this,
				dealTableId);
		
		ServerPanel serpanel = new ServerPanel(currentConnectPanel,
				currentDealPanel);
		/*ImageIcon imageIcon = new ImageIcon(
				EventTest.class.getResource("/icon/sstatus.png"));*/
		/*HHEvent event = new HHEvent(fromID, toID, "AddPanelEvent", serpanel,
				imageIcon);*/
		CmdEvent event =new CmdEvent(fromID, toID, "AddPanelEvent");
		event.setObj(serpanel);
		event.addProp("ICO", "sstatus.png");
		event.addProp("TAB_TITLE", "数据库监控");
		event.addProp("COMPONENT_ID", "dbMonitor");
		sendEvent(event);
	}

	/**
	 * 关于
	 */
	private void about() {
		String fromID = "com.hhdb.csadmin.plugin.menu";
		String toID = "com.hhdb.csadmin.plugin.about";
		HHEvent event =new HHEvent(fromID, toID, EventTypeEnum.GET_OBJ.name());
		HHEvent ev = sendEvent(event);
		if(ev instanceof ErrorEvent){
			JOptionPane.showMessageDialog(null, "加载失败");
		}
	}

	/**
	 * 选项
	 */
	private void option() {

		Object[] options = { "确定", "取消" };
		final PropertiesPanel pp = new PropertiesPanel();
		final JOptionPane pane = new JOptionPane(pp, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);
		final Dialog d = pane.createDialog("选项");
		PropertyChangeListener changeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				String value = (String) pane.getValue();
				if ("取消".equals(value)) {
					pane.removePropertyChangeListener(this);
					d.dispose();
				} else if ("确定".equals(value)) {
					pp.execute();
					String cmdBackColor = pp.cmdpanel.getBackcolor();
					String cmdFontColor = pp.cmdpanel.getFontcolor();
					String fromID = Hmenu.class.getPackage().getName();
					String toID = "com.hhdb.csadmin.plugin.cmd";
					CmdEvent backEvent = new CmdEvent(fromID, toID,
							"SetBackGroudEvent");
					backEvent.addProp("backgroud_color", cmdBackColor);
					backEvent.addProp("font_color", cmdFontColor);
					try {
						sendEvent(backEvent);
					} catch (Exception e2) {
						LM.error(LM.Model.CS.name(), e2);
					}

					// 查询器
					String queryFontSize = pp.queryset.getFontsize();
					String queryBlackColor = pp.queryset.getBackColor();
					boolean queryIsLine = pp.queryset.getIsLine();
					// SetBackgroudEvent
					CmdEvent queryback = new CmdEvent(fromID,
							"com.hhdb.csadmin.plugin.query",
							"QueryBackgroudEvent");
					queryback.addProp("backColor", queryBlackColor);
					queryback.addProp("fontSize", queryFontSize);
					queryback.addProp("isNeedLineNumber", queryIsLine + "");
					sendEvent(queryback);
				
				}
			}
		};
		pane.addPropertyChangeListener(changeListener);
		d.setSize(736, 489);
		d.setVisible(true);
	}

	@Override
	public Component getComponent() {
		// TODO Auto-generated method stub
		return menubar;
	}

	public String sendToConn(String sql) {
		String fromID = Hmenu.class.getPackage().getName();
		String toID = "com.hhdb.csadmin.plugin.conn";
		CmdEvent executeEvent = new CmdEvent(fromID, toID, "ExecuteCSVBySqlEvent");
		executeEvent.addProp("sql_str", sql);
		HHEvent event = sendEvent(executeEvent);
		String csv = event.getValue("csv");
		return csv;

	}

//	/**
//	 * 解析csv获取表头
//	 * 
//	 * @param csv
//	 * @return
//	 */
//	public List<String> parseCsvColumn(String csv) {
//		String[] stringArr = csv.split("\r\n");
//		String[] columnArr = stringArr[0].split(",");
//		List<String> columnList = new ArrayList<>();
//		for (int i = 0; i < columnArr.length; i++) {
//			columnList.add(columnArr[i]);
//		}
//		return columnList;
//	}
//
//	/**
//	 * 解析行数据
//	 * 
//	 * @param csv
//	 * @param columnList
//	 * @return
//	 */
//	public List<Map<String, Object>> parseCsvRows(String csv,
//			List<String> columnList) {
//
//		String[] stringArr = csv.split("\r\n");
//		List<Map<String, Object>> rowList = new ArrayList<>();
//		if (stringArr.length > 1) {
//			for (int j = 1; j < stringArr.length; j++) {// 从第一行开始是数据
//				Map<String, Object> map = new HashMap<String, Object>();
//				String rowString = stringArr[j];
//				String[] rowParasArr = rowString.split(",\"");
//				if (rowParasArr.length > 1) {
//					String sql = rowParasArr[1];
//					sql = sql.substring(0, sql.length() - 1);
//					map.put(columnList.get(columnList.size() - 1), sql);
//				}
//
//				String rowString0 = rowParasArr[0];
//				String[] rowArr = rowString0.split(",");
//
//				for (int k = 0; k < rowArr.length; k++) {
//					String value = rowArr[k];
//					String key = columnList.get(k);
//					map.put(key, value);
//				}
//				rowList.add(map);
//			}
//		}
//		return rowList;
//
//	}

}
