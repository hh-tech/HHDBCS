package com.hhdb.csadmin.plugin.mouse_menu;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.HHEvent;

public class MouserMenuFrame extends Frame {
	private JPopupMenu menu;
	private static final long serialVersionUID = 1L;
	private String returnStrParent = null;
	 public MouserMenuFrame(JPopupMenu menu,List<RightMenu> menuList){  
	        menu.setVisible(true);  
	        this.menu = menu;
	    } 
	public void show(final MouserMenu plugin, final HHEvent events,Component invoker,int x,int y){
		menu.show(invoker,x, y);
		MenuElement[] menuList = menu.getSubElements();
		for(MenuElement item:menuList){
			final JMenuItem items = (JMenuItem) item;
			items.addActionListener(new ActionListener() {     
		           public void actionPerformed(ActionEvent e) {  
			        	returnStrParent = e.paramString();
			            //对于调用者，哪个组件的监听发的，返回的也是哪个组件的右键菜单点击事件。
			            String[] str = returnStrParent.split(",");
			            for(int i = 0; i<str.length;i++){
			            	if(str[i].startsWith("cmd")){
			            		returnStrParent = str[i].substring(4);
			            		//写回的时候fromid和toid需要调转过来
			            		//MouseRightEvent mouseEvents = new MouseRightEvent(returnStrParent, events.getToID(),events.getFromID(), null, null);
			            		CmdEvent event = new CmdEvent(events.getToID(), events.getFromID(), "mouserMenuEvent");
			            		Map<String, String> propMap = new HashMap<String, String>();
			            		propMap.put("select", returnStrParent);
			            		event.setPropMap(propMap);
			            		plugin.sendEvent(event);
			            	}
			            }
		           }  
		          });
			
			MenuElement[] childMenuList = items.getSubElements();//子菜单还是一个JPopupMenu，所以必须要将子菜单再进行一次items.getSubElements()
			if(childMenuList.length>0){
				childMenuList = childMenuList[0].getSubElements();
				if(childMenuList.length>0){//有子菜单
					for(MenuElement childItem:childMenuList){
						 JMenuItem childItems = (JMenuItem) childItem;
						childItems.addActionListener(new ActionListener() {     
					           public void actionPerformed(ActionEvent e) {  
						        	String returnStr = e.paramString();
						            //对于调用者，哪个组件的监听发的，返回的也是哪个组件的右键菜单点击事件。
						        	returnStrParent = items.getText();
						            String[] str = returnStr.split(",");
						            for(int i = 0; i<str.length;i++){
						            	if(str[i].startsWith("cmd")){
						            		returnStr = str[i].substring(4);
						            		//写回的时候fromid和toid需要调转过来
						            		CmdEvent event = new CmdEvent(events.getToID(), events.getFromID(), "mouserMenuEvent");
						            		Map<String, String> propMap = new HashMap<String, String>();
						            		propMap.put("select", returnStrParent+"/"+returnStr);
						            		event.setPropMap(propMap);
						            		plugin.sendEvent(event);
						            	}
						            }
					           }  
					          });
					}
				}
			}//子菜单结束
		}
	}
}
