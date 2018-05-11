package com.hhdb.csadmin.plugin.mouse_menu;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.EventUtil;

/**
 * event中的字符串解析规则：
 * 一个map对象包含一层父菜单以及该父菜单的子菜单,例如
 * map的key为 “新建/1” value为“新建txt文件/1|新建pdf文件/1|新建其他文件/0”
 * 其中/前面为菜单内容，/后面的1或者0分别表示可用与禁用,|表示多子菜单分隔
 * @author Administrator
 *
 */
public class MouserMenu extends AbstractPlugin{
	private List<RightMenu> menuList;

	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent replyEvent = EventUtil.getReplyEvent(MouserMenu.class, event);
		if (event.getType().equals(EventTypeEnum.CMD.name())) {
			CmdEvent cmdEvent = (CmdEvent) event;
			if (cmdEvent.getCmd().equals("mouserMenuEvent")) {
				int coordinateX = 0;
				int coordinateY = 0;
				menuList = new ArrayList<>();
				Map<String, String> map = event.getPropMap();
				for (Map.Entry<String, String> entry : map.entrySet()) {
					if (entry.getKey().equals("coordinateX")) {
						coordinateX = Integer.parseInt(entry.getValue());
						continue;
					}
					if (entry.getKey().equals("coordinateY")) {
						coordinateY = Integer.parseInt(entry.getValue());
						continue;
					}
					String parents = entry.getKey();
					RightMenu rightMenu = new RightMenu();
					String[] parentsMenu = parents.split("/");
					if (parentsMenu.length == 2) {
						rightMenu.setParentMenu(parentsMenu[0].trim());
						rightMenu.setIsAvailable(parentsMenu[1].trim().equals(
								"1") ? true : false);
					} else {// 传入参数错误

					}
					if (entry.getValue() == null || entry.getValue().equals("")) {// 当前父菜单没有子菜单
						rightMenu.setHaveChildren(false);
					} else {
						rightMenu.setHaveChildren(true);
						Map<String, Boolean> childMap = new HashMap<String, Boolean>();
						String childs = entry.getValue();
						String[] childsMenu = childs.split("\\|");
						for (int i = 0; i < childsMenu.length; i++) {
							String[] childMenus = childsMenu[i].split("/");
							if (childMenus.length == 2) {
								String childMenu = childMenus[0];
								boolean isAvailablechildMenu = childMenus[1]
										.trim().equals("1") ? true : false;
								childMap.put(childMenu, isAvailablechildMenu);
							} else {// 参数传入错误

							}
						}
						rightMenu.setChildrenMap(childMap);
					}
					menuList.add(rightMenu);
				}

				JPopupMenu menu = new JPopupMenu();// 第一层菜单
				for (RightMenu rightMenu : menuList) {
					if (rightMenu.getHaveChildren()) {
						JMenu m = new JMenu(rightMenu.getParentMenu());
						for (Map.Entry<String, Boolean> entry : rightMenu
								.getChildrenMap().entrySet()) {
							JMenuItem item = m.add(entry.getKey());
							if (!entry.getValue()) {
								item.setEnabled(false);
							}
						}
						menu.add(m);
					} else {
						JMenuItem item = menu.add(rightMenu.getParentMenu());
						if (!rightMenu.getIsAvailable()) {
							item.setEnabled(false);
						}
					}
				}
				MouserMenuFrame menuFram = new MouserMenuFrame(menu, menuList);
				menuFram.show(this, cmdEvent, (Component) cmdEvent.getObj(),coordinateX, coordinateY);
			}
		}
		   return replyEvent;
	}

	@Override
	public Component getComponent() {
		// TODO Auto-generated method stub
		return null;
	}

}
