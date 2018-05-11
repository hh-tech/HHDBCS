package com.hhdb.csadmin.plugin.tabpane;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.EventUtil;
import com.hhdb.csadmin.common.util.StringUtil;

public class TabbedPaneManager extends AbstractPlugin {
	private  JTabbedPane jTabbedPane = new JTabbedPane();
	static final String PLUGIN_ID = TabbedPaneManager.class.getPackage()
			.getName();
	private Map<String, JPanel> componentIds = new HashMap<String, JPanel>();
	private Map<String,String> fromidmap = new HashMap<String,String>();
	private List<String> componentlist = new ArrayList<String>();
	
	private JPanel  attribute = new JPanel();
	public TabbedPaneManager() {
		jTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		jTabbedPane.add(attribute,"属性");
		jTabbedPane.addMouseListener(new MouseListener() {			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.getButton() == 3){
					showPopupMenu(e);
				}			
			}			
			@Override
			public void mousePressed(MouseEvent e) {
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		attribute.setLayout(new BorderLayout());
	}
	
	@Override
	public synchronized HHEvent receEvent(HHEvent event) {
		HHEvent replyE=EventUtil.getReplyEvent(TabbedPaneManager.class, event);
		if(event.getType().equals(EventTypeEnum.GET_OBJ.name())){
			replyE.setObj(jTabbedPane);
			return replyE;
		}else if(event.getType().equals(EventTypeEnum.CMD.name())){
			CmdEvent cmdevent = (CmdEvent)event;
			
			if (cmdevent.getCmd().equals("AddPanelEvent")) {
				String componentID = cmdevent.getValue("COMPONENT_ID");
				if (componentIds.keySet().contains(componentID)) {
//					jTabbedPane.setSelectedComponent(componentIds.get(componentID));
//					return EventUtil.getReplyEvent(TabbedPaneManager.class, event);
					closePane(componentID);
				}
	            
				/***单独测试时使用的****************************/
				JPanel jPanel = (JPanel)cmdevent.getObj();
				if (jPanel == null) {
					jPanel = new JPanel();
					JLabel a = new JLabel(cmdevent.getValue("TAB_TITLE"));
					JLabel b = new JLabel(
							event.getValue("COMPONENT_ID"));
					jPanel.add(a);
					jPanel.add(b);
				}
				/**********************************************/
				String icoName = cmdevent.getValue("ICO");
				ImageIcon icon=new ImageIcon(StringUtil.getProIcoPath(icoName));
				String tabTitle = cmdevent.getValue("TAB_TITLE");
				TabbedHeader tabbedHeader = new TabbedHeader(this, icon, tabTitle,
						componentID, event.getFromID());
				jTabbedPane.add(jPanel);
				jTabbedPane.setTabComponentAt(jTabbedPane.getTabCount() - 1,
						tabbedHeader);
				jTabbedPane.setSelectedComponent(jPanel);
				componentlist.add(componentID);
				componentIds.put(componentID, jPanel);
				fromidmap.put(componentID, event.getFromID());
				return replyE;
				
			}else if(cmdevent.getCmd().equals("flushAttributeEvent")){
				jTabbedPane.setSelectedComponent(attribute);
				replyE.setObj(attribute);
				replyE.addProp("flag", "response");
				replyE.setToID(cmdevent.getFromID());
				replyE.setFromID(PLUGIN_ID);
				return replyE;
				
			}else if(cmdevent.getCmd().equals("CleanEvent")){
				cleanAllCom();
				return replyE;
			}else if(cmdevent.getCmd().equals("closePane")){
				closePane(event.getValue("id"));
				return replyE;
			}
			return replyE;
		}else {
			ErrorEvent errorEvent = new ErrorEvent(PLUGIN_ID,
					event.getFromID(),
					ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
			errorEvent.setErrorMessage(PLUGIN_ID + "不能接受如下事件:\n"
					+ event.toString());
			return errorEvent;
		}
	}
	/**
	 * 清空tablepanel里面的所有控件
	 */
	private void cleanAllCom(){
		List<String> tmplist = new ArrayList<String>();
		for(int listindex=0;listindex<componentlist.size();listindex++){
			jTabbedPane.remove(1);
			String componentid = componentlist.get(listindex);
			
			CmdEvent removePanelEvent = new CmdEvent(TabbedPaneManager.PLUGIN_ID, fromidmap.get(componentid), "RemovePanelEvent");
            removePanelEvent.addProp("COMPONENT_ID", componentid);
            sendEvent(removePanelEvent);
            
            componentIds.remove(componentid);
			fromidmap.remove(componentid);
			tmplist.add(componentid);
		}
		for(String tmp:tmplist){
			componentlist.remove(tmp);
		}
	}
	
	/**
	 * 关闭单个面板
	 */
	private void closePane(String componentid){
		jTabbedPane.remove(componentIds.get(componentid));
		CmdEvent removePanelEvent = new CmdEvent(TabbedPaneManager.PLUGIN_ID, fromidmap.get(componentid), "RemovePanelEvent");
        removePanelEvent.addProp("COMPONENT_ID", componentid);
        sendEvent(removePanelEvent);
        
        componentIds.remove(componentid);
		fromidmap.remove(componentid);
		componentlist.remove(componentid);
	}
	
    /**
     * 显示右键菜单的方法
     * @param event
     */
	private void showPopupMenu(final MouseEvent event) {
		JPopupMenu pop = new JPopupMenu();
		JMenuItem closeCurrent = new JMenuItem("关闭当前");
		closeCurrent.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				int index = jTabbedPane.getSelectedIndex();
				if (index != 0) {
					jTabbedPane.remove(index);
					int listindex = index-1;
					String componentid = componentlist.get(listindex);
					
					CmdEvent removePanelEvent = new CmdEvent(TabbedPaneManager.PLUGIN_ID, fromidmap.get(componentid), "RemovePanelEvent");
		            removePanelEvent.addProp("COMPONENT_ID", componentid);
		            sendEvent(removePanelEvent);
		            
		            componentIds.remove(componentid);
					componentlist.remove(listindex);
					fromidmap.remove(componentid);
				}
			}
		});
		pop.add(closeCurrent);
		JMenuItem closeLeft = new JMenuItem("关闭左侧标签");
		closeLeft.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				int jtabindex = jTabbedPane.getSelectedIndex();
				if (jtabindex > 0) {
					List<String> tmplist = new ArrayList<String>();
					for(int index=1;index<jtabindex;index++){
						jTabbedPane.remove(1);
						int listindex = index-1;
						String componentid = componentlist.get(listindex);
						
						CmdEvent removePanelEvent = new CmdEvent(TabbedPaneManager.PLUGIN_ID, fromidmap.get(componentid), "RemovePanelEvent");
			            removePanelEvent.addProp("COMPONENT_ID", componentid);
			            sendEvent(removePanelEvent);
			            
			            componentIds.remove(componentid);
						fromidmap.remove(componentid);
						tmplist.add(componentid);
					}
					for(String tmp:tmplist){
						componentlist.remove(tmp);
					}
				}
				
			}
		});
		pop.add(closeLeft);
		JMenuItem closeRight = new JMenuItem("关闭右侧标签");
		closeRight.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				int jtabindex = jTabbedPane.getSelectedIndex();
				List<String> tmplist = new ArrayList<String>();
					for(int index=jtabindex+1;index<=componentlist.size();index++){
						jTabbedPane.remove(jtabindex+1);
						int listindex = index-1;
						String componentid = componentlist.get(listindex);
						
						CmdEvent removePanelEvent = new CmdEvent(TabbedPaneManager.PLUGIN_ID, fromidmap.get(componentid), "RemovePanelEvent");
			            removePanelEvent.addProp("COMPONENT_ID", componentid);
			            sendEvent(removePanelEvent);
			            componentIds.remove(componentid);
						fromidmap.remove(componentid);
						tmplist.add(componentid);
					}
					for(String tmp:tmplist){
						componentlist.remove(tmp);
					}
			}
		});
		pop.add(closeRight);
		JMenuItem closeAll = new JMenuItem("全部关闭");
		closeAll.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				cleanAllCom();
			}
		});
		pop.add(closeAll);
		pop.show(event.getComponent(), event.getX(), event.getY());
	}

	
	@Override
	public Component getComponent() {
		return jTabbedPane;
	}

	public  JTabbedPane getTabPane() {
		return this.jTabbedPane;
	}

	public Map<String, JPanel> getComponentIds() {
		return componentIds;
	}

	public List<String> getComponentlist() {
		return componentlist;
	}

	public Map<String, String> getFromidmap() {
		return fromidmap;
	}	
	
}
