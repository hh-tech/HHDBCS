package com.hhdb.csadmin.plugin.query.util;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.hh.frame.common.log.LM;
import com.hhdb.csadmin.common.util.StartUtil;
import com.hhdb.csadmin.plugin.cmd.console.CommonsHelper;

public class Pgsqlcmd {
	static List<Map<String, String>> pgCmdList;
	static{
		try {
			//pg命令匹配集合
			pgCmdList = parsePgCmdXml();
			//初始化模板
			initVelocity(CommonsHelper.getClassPath() + StartUtil.prefix+"_vm/");
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
		}
	}
	/**
	 * 处理PG SQL命令
	 * 
	 * @param cmdSql
	 *            \?
	 * @return sql
	 */
	public static String pgCommandHandler(String cmdSql) {
		if(pgCmdList==null){
			return null;
		}
		if(cmdSql.lastIndexOf(";")>-1){
			cmdSql = cmdSql.substring(0, cmdSql.length()-1);
		}
		cmdSql = cmdSql.trim();
		boolean b = false;
		for (Map<String, String> map : pgCmdList) {
			String temp = cmdSql.substring(1);
			String regex = map.get("regex");
			b = temp.matches(regex);
			if (b) {
				VelocityContext ctx = new VelocityContext();
				String[] ss = cmdSql.split("\\s+");
				ctx.put("cmdSql", ss[0]);

				String arg = "";
				if (ss.length == 2) {
					arg = ss[1].toLowerCase().replace(".", "\\.").replace("?", ".?").replace("*", ".*?");
					//处理 \d tableName, 要执行多次查询, 返回不同的result
					if ("d".equals(map.get("key"))) {
						return temp;
					}
					
					ctx.put("arg", arg);
				}

				StringWriter sw = new StringWriter();
				try {
					Velocity.mergeTemplate(map.get("key") + ".vm", "UTF-8", ctx, sw);
				} catch (Exception e) {
					if (e instanceof ResourceNotFoundException)
						throw new RuntimeException("找不到模板文件" + map.get("key") + ".vm");
				}
				try {
					sw.close();
				} catch (IOException e) {
					LM.error(LM.Model.CS.name(), e);
					throw new RuntimeException(e);
				}
				cmdSql = sw.toString();
				break;
			}
		}

		if (!b)
			throw new RuntimeException("无效的命令 " + cmdSql);

		return cmdSql;
	}
	
	public static List<Map<String,String>> parsePgCmdXml() throws ParserConfigurationException, SAXException, IOException{
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
        String classPath = CommonsHelper.getClassPath();
        Document doc = dbBuilder.parse(new File(URLDecoder.decode(classPath+"pg_cmd.xml","utf-8")));
        NodeList nList = doc.getElementsByTagName("cmd");
        List<Map<String,String>> list = new ArrayList<Map<String,String>>();
        
        for(int i = 0; i< nList.getLength() ; i ++){
        	Element node = (Element)nList.item(i);
            Map<String,String> map = new HashMap<String,String>();
            map.put("key", node.getAttribute("key"));
            map.put("regex", node.getAttribute("regex"));
            list.add(map);
        }
        return list;
    }
	
	/**
	 * 初始化模板引擎
	 * 
	 * @return 模板目录
	 */
	private static void initVelocity(String path)  {
		Properties p = new Properties();
		File vmdir = new File(path);
		if (vmdir.exists() && vmdir.isDirectory()) {
			p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, path);
			Velocity.init(p);
		} else {
			throw new RuntimeException("vm模板目录不存在: " + path);
		}
	}

}
