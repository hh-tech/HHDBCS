package com.hhdb.csadmin.plugin.menu.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hh.frame.common.log.LM;
import com.hhdb.csadmin.plugin.menu.entity.HMenu;
import com.hhdb.csadmin.plugin.menu.entity.HMenuItem;

public class MenuXmlUtil {
	private static String configPath = "etc/xml/menu.xml";
	private static Document document;
	
	static {
		File file = new File(configPath);
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			document = db.parse(file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LM.error(LM.Model.CS.name(), e);
		}
	}

	public static List<HMenu> parseHMenus() {
		List<HMenu> hmenus = new ArrayList<HMenu>();
		NodeList nodes = document.getElementsByTagName("hmenus");
		for(int i=0;i<nodes.getLength();i++){
			try {
				HMenu hmenu=parseMenu(nodes.item(i));
				hmenus.add(hmenu);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		}
		return hmenus;

	}
	

	
	/**解析menu
	 * 
	 * @param node
	 * @return
	 */
	private static HMenu parseMenu(Node node) {
		HMenu hmenu=new HMenu();
		NamedNodeMap map=node.getAttributes();
		String id=map.getNamedItem("id").getNodeValue();
		String name=map.getNamedItem("name").getNodeValue();
		
		hmenu.setId(id);
		
		hmenu.setName(name);
		NodeList nodes=node.getChildNodes();
		List<Object> hmenuItems=parseItem(nodes);
		hmenu.setMenuItems(hmenuItems);
		
	
		return hmenu;
	}


	private static List<Object> parseItem(NodeList nodes) {
		List<Object> hmenuItems=new ArrayList<Object>();
		 String itemId=null;
		 String itemName=null;
		 String itemOff=null;
		 String itemIcon=null;
		 String itemTo=null;
		 String itemKey=null;
		for(int i=0;i<nodes.getLength();i++){
			HMenuItem hmenuItem=new HMenuItem();
			Node node =nodes.item(i);
			if(node.getNodeType()==1){
				NamedNodeMap map=node.getAttributes();
				String nodeName=node.getNodeName();
				if(nodeName.endsWith("jmenuItem")){
					itemId=map.getNamedItem("id").getNodeValue();
					itemName=map.getNamedItem("name").getNodeValue();
					itemOff=map.getNamedItem("off").getNodeValue();
					itemIcon=map.getNamedItem("icon").getNodeValue();
					itemTo=map.getNamedItem("to").getNodeValue();
			
					if(map.getNamedItem("key")!=null){
						 itemKey=map.getNamedItem("key").getNodeValue();
					}
					
					hmenuItem.setId(itemId);
					hmenuItem.setIcon(itemIcon);
					hmenuItem.setName(itemName);
					hmenuItem.setOff(itemOff);
					hmenuItem.setTo(itemTo);
					hmenuItem.setKey(itemKey);
					hmenuItems.add(hmenuItem);
				}else if(nodeName.endsWith("hmenu")){
					
					HMenu menu=parseMenu(node);
					hmenuItems.add(menu);
				}
				
				
			}
			
		}
		return hmenuItems;

	}


	public static void main(String[] args) {
		parseHMenus();
	}
}
