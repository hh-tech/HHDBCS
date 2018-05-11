package com.hhdb.csadmin.common.bean;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.base.IEventRoute;
import com.hh.frame.swingui.base.PluginBean;
import com.hh.frame.swingui.event.HHEvent;
//import com.hh.frame.swingui.util.LogDefault;
import com.hh.frame.swingui.util.XmlFileUtil;

public class HHEventRoute implements IEventRoute {
	private  Map<String,AbstractPlugin> pluginMap= new Hashtable<String,AbstractPlugin>();
	
	public HHEventRoute(){
	}

	@Override
	public HHEvent processEvent(HHEvent event) {
		LM.debug(LM.Model.CS.name(), event.toString());
		String toID=event.getToID();
		if(StringUtils.isBlank(toID)){
			LM.info(LM.Model.CS.name(), "otID不能为空");
			return null;
		}
		if(!pluginMap.containsKey(toID)){
			boolean isSuc=addPlugin(toID);
			if(!isSuc){
				LM.info(LM.Model.CS.name(),"找不到%s,请调用addPlugin添加",toID);
				return null;
			}
		}
		AbstractPlugin plugin=pluginMap.get(toID);
		HHEvent replyEvent=plugin.receEvent(event);
		LM.debug(LM.Model.CS.name(), replyEvent.toString());
		return replyEvent;
	}



	@Override
	public boolean addPlugin(String id) {
		if(StringUtils.isBlank(id)){
			LM.info(LM.Model.CS.name(), "ID不能为空");
			return false;
		}
		// 如果plugin没有存在
		try {
			PluginBean pluginBean=getBean(id);
			Class<?>  c = Class.forName(id+"."+pluginBean.getClazz());
			AbstractPlugin plugin = (AbstractPlugin) c.newInstance();
			plugin.init(this,pluginBean);
			pluginMap.put(id, plugin);
			return true;
			
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | ParserConfigurationException | SAXException | IOException | URISyntaxException e) {
			LM.error(LM.Model.CS.name(), e);
			return false;
		} 
	}
	
	private PluginBean getBean(String id) throws ParserConfigurationException, SAXException, IOException, URISyntaxException{
		
		PluginBean pluginBean=new PluginBean();
		String pluginXmlPath=id.replace('.', '/')+"/plugin.xml";
		Document pluginDoc=null;
		pluginDoc = XmlFileUtil.getResXmlDoc(pluginXmlPath);

		NodeList nodeList=pluginDoc.getChildNodes();
		Element pluginDetail=(Element)nodeList.item(0);
		pluginBean.setClazz(pluginDetail.getAttribute("class"));
		pluginBean.setAuthor(pluginDetail.getAttribute("author"));
		pluginBean.setVersion(pluginDetail.getAttribute("version"));
		pluginBean.setId(id);
		return pluginBean;
	}
}
