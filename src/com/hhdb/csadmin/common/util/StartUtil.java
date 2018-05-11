package com.hhdb.csadmin.common.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.hh.frame.swingui.util.XmlFileUtil;
import com.hhdb.csadmin.common.bean.ServerBean;

public class StartUtil {
	/**
	 * hh:hhdb数据库
	 * pg:postgresql数据库
	 */
	public static String prefix = "hh";
	public static void main(String[] args) throws Exception {
		initConnConfig("hh");
	}
	public static Map<String,ServerBean> initConnConfig(String px) throws Exception{
		Map<String,ServerBean> map = new HashMap<String, ServerBean>();
		Document doc = XmlFileUtil.getResXmlDoc(px+"_conn_config.xml");

		NodeList connList = doc.getElementsByTagName("conn");
		for(int i=0;i<connList.getLength();i++){
			Element connElement = (Element) connList.item(i);
			ServerBean serverbean = new ServerBean();
			serverbean.setHost(connElement.getAttribute("host"));
			serverbean.setPort(connElement.getAttribute("port"));
			serverbean.setDBName(connElement.getAttribute("database"));
			serverbean.setUserName(connElement.getAttribute("username"));
			serverbean.setPassword(connElement.getAttribute("password"));
			String key = serverbean.getHost()+":"+serverbean.getPort()+":"+serverbean.getDBName()+":"
					+serverbean.getUserName();
			map.put(key, serverbean);
		}
		return map;
	}
	private static boolean equelsSb(ServerBean sb1,ServerBean sb2){
		return sb1.getHost().equals(sb2.getHost())&&
				sb1.getPort().equals(sb2.getPort())&&
				sb1.getDBName().equals(sb2.getDBName())&&
				sb1.getUserName().equals(sb2.getUserName());
	}	
	public static void updateConnXml(ServerBean sb,String px) throws Exception{
		Map<String, ServerBean> map = initConnConfig(px);
		ServerBean sbflag = new ServerBean();
		boolean flag = false;
		for(String key:map.keySet()){
			sbflag = map.get(key);
			if(flag=equelsSb(sbflag, sb)){
				break;
			}
		}
		if(flag){
			if(!sbflag.getPassword().equals(sb.getPassword())){
				SAXReader reader = new SAXReader();  
		        org.dom4j.Document doc = reader.read(new FileInputStream(System.getProperty("user.dir")+"/etc/"+prefix+"_conn_config.xml"));  
		        org.dom4j.Element root = doc.getRootElement();  
		        List<?> books = root.elements();
		        for (int i = 0; i < books.size(); i++) {
		        	org.dom4j.Element book = (org.dom4j.Element) books.get(i);
		            if (sb.getHost().equals(book.attributeValue("host"))&&
		            		sb.getPort().equals(book.attributeValue("port"))&&
		            		sb.getDBName().equals(book.attributeValue("database"))&&
		            		sb.getUserName().equals(book.attributeValue("username"))
		            		) {
		                book.addAttribute("password", sb.getPassword());
		            }
		        }
		        FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir")+"/etc/"+prefix+"_conn_config.xml");  
		        OutputStreamWriter osw = new OutputStreamWriter(fos,"utf-8");  
		        OutputFormat of = new OutputFormat();  
		        of.setEncoding("utf-8");  
//		        of.setIndent(true);  
//		        of.setNewlines(true);  
		        XMLWriter writer = new XMLWriter(osw, of);  
		        writer.write(doc);  
		        writer.close(); 
			}
		}else{
			SAXReader reader = new SAXReader();  
	        org.dom4j.Document doc = reader.read(new FileInputStream(System.getProperty("user.dir")+"/etc/"+prefix+"_conn_config.xml"));  
	        org.dom4j.Element root = doc.getRootElement();  
	        org.dom4j.Element e1 = root.addElement("conn");  
	        e1.addAttribute("host",sb.getHost());  
	        e1.addAttribute("port",sb.getPort());  
	        e1.addAttribute("database",sb.getDBName());  
	        e1.addAttribute("username",sb.getUserName());  
	        e1.addAttribute("password",sb.getPassword());  
	        FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir")+"/etc/"+prefix+"_conn_config.xml");  
	        OutputStreamWriter osw = new OutputStreamWriter(fos,"utf-8");  
	        OutputFormat of = new OutputFormat();  
	        of.setEncoding("utf-8");  
	        of.setIndent(true);  
	        of.setNewlines(true);
	        XMLWriter writer = new XMLWriter(osw, of);  
	        writer.write(doc);  
	        writer.close();  
		}
    }  
}
