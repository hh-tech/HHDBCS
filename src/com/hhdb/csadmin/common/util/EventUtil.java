package com.hhdb.csadmin.common.util;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hh.frame.swingui.util.XmlFileUtil;

public class EventUtil {
	
	public static HHEvent toEvent(String eventStr) throws ParserConfigurationException, SAXException, IOException{
		Document  doc=XmlFileUtil.getXmlDoc(eventStr);
		NodeList eventNodeList=doc.getElementsByTagName("event");
		Element nodeElement=(Element)eventNodeList.item(0);
		String fromID=nodeElement.getAttribute("from");
		String toID=nodeElement.getAttribute("to");
		String type=nodeElement.getAttribute("type");
		
		HHEvent event;
		if(type.equals(EventTypeEnum.CMD.name())){
			event = new CmdEvent(fromID, toID, "");			
		}else{
			event =new HHEvent(fromID,toID,type);
		}		
		NodeList propNodeList=nodeElement.getElementsByTagName("property");
		
		for(int i=0;i<propNodeList.getLength();i++){
			Element propNode=(Element)propNodeList.item(i);
			String propName=propNode.getAttribute("name");
			String propContent =propNode.getTextContent().trim();
			event.addProp(propName, propContent);
		}
		return event;
	}

	public static HHEvent getReplyEvent(Class<?> clazz,HHEvent fromEvent){
		return new HHEvent(clazz.getPackage().getName(),fromEvent.getFromID(),EventTypeEnum.REPLY.name());
	}
	
}
