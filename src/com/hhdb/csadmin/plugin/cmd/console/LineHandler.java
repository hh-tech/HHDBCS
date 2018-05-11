package com.hhdb.csadmin.plugin.cmd.console;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.bean.ServerBean;
import com.hhdb.csadmin.common.dao.ConnService;
import com.hhdb.csadmin.common.util.StartUtil;
import com.hhdb.csadmin.plugin.cmd.SqlCmdPlugin;

/**
 * 输入行处理
 * 
 * @author Administrator
 * 
 */
public class LineHandler {
	private ConsolePrint printC;
	private SqlCmdPlugin sqlcmdplugin;
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
	LineHandler(ConsolePrint printC,SqlCmdPlugin sqlcmdplugin) {
		this.printC = printC;
		this.sqlcmdplugin = sqlcmdplugin;
	}

	/**
	 * 切换数据库
	 * 
	 * @param line
	 * @param label
	 * @param printC
	 * @param connection
	 * @return
	 * @throws BaseException
	 */
	public Connection switchDB(String line, ConsolePrefix prefixC, Connection connection) {
		String[] arr = line.split("\\s+");
		if (arr.length != 2) {
			printC.printResponse(ConsoleConstant.MSG_INVALID_SWITCH_COMMAND);
			return connection;
		}

		Connection tempConn = connection;
		CmdEvent getsbEvent = new CmdEvent(SqlCmdPlugin.class.getPackage().getName(), "com.hhdb.csadmin.plugin.conn", "GetServerBean");
		HHEvent revent = sqlcmdplugin.sendEvent(getsbEvent);
		ServerBean sb = (ServerBean)revent.getObj();
		try {
			//connection = ConnectionHelper.getConnection(connection.getHost(), connection.getPort(), arr[1], connection.getUser(), connection.getPassword());
			connection = ConnService.createConnection(sb);
			tempConn.close();
		} catch (Exception e) {
			printC.printResponse(ConsoleConstant.MSG_SWITCH_FAILED);
			return tempConn;
		}

		prefixC.setStart();
		printC.printResponse("您现在已连接到数据库\"" + sb.getDBName() + "\", 用户\"" + sb.getUserName() + "\".\n");
		return connection;
	}

	/**
	 * 设置fetch count
	 */
	public void fetchCount(String line, Connection conn) {
		String[] split = line.split("\\s+");
		String c = split[2];
		if (c.lastIndexOf(';') > -1)
			c = c.substring(0, c.length() - 1);
		//int fetchCount = Integer.parseInt(c);
		//statement.setFetchSize(fetchCount);
		printC.printResponse("SET SUCCESS!\n");
	}

	public void restore(final String line, final Connection conn) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				new ConsoleRestore(line, conn).restore(printC);
			}
		}).start();
	}

	// /d tableName
	void doDPattern(String sql, Connection connection) throws Exception {
		String[] ss = sql.split("\\s+");
		VelocityContext ctx = new VelocityContext();
		ctx.put("cmdSql", ss[0]);

		PreparedStatement ps = connection.prepareStatement(ConsoleConstant.SQL_SELECT_OID);
		ps.setString(1, "^(" + ss[1] + ")$");
		ResultSet rs = ps.executeQuery();
		List<Map<String, Object>> rows = new ArrayList<Map<String,Object>>();
//		List<List<Object>> results=new ArrayList<List<Object>>();
//		
//		List<Object> columnList=new ArrayList<Object>();
//		
//		ResultSetMetaData rsmd = rs.getMetaData();
//		for (int i = 0; i < rsmd.getColumnCount(); i++) {
//			columnList.add(SqlUtil.lookupColumnName(rsmd, i+1));
//		}
//		results.add(columnList);

		while (rs.next()) {
//			List<Object> rowList=new ArrayList<Object>(columnList.size());
//			for(int i=0;i<columnList.size();i++) {
//				rowList.add(rs.getObject(i+1));
//			}
//			results.add(rowList);
			Map<String, Object> map = SqlUtil.getMap(rs);
			rows.add(map);
		}
		rs.close();
		ps.close();
		
		for (Map<String, Object> map : rows) {
			String oid = map.get("oid") + "";
			String relname = map.get("relname") + "";
			ctx.put("cmdSql", ss[0]);
			ctx.put("arg", relname);
			ctx.put("oid", oid);
			ctx.remove("hasindex");
			printC.printResponse("\n-- 资料表 \"" + relname + "\"\n");
			StringWriter sw = new StringWriter();
			Velocity.mergeTemplate("d.vm", "UTF-8", ctx, sw);
			try {
				sw.close();
			} catch (IOException e1) {
				LM.error(LM.Model.CS.name(), e1);
			}
			//result = statement.executeQuery(sw.toString());
			List<List<Object>> result = SqlQueryUtil.selectList(connection,sw.toString());
			printC.printResult(result,-1, 0, 0);

//			ps = connection.prepareStatement(ConsoleConstant.SQL_SELECT_REL);
//			ps.setString(1, oid);
//			result = ps.executeQuery();
//			
			
			Map<String, Object> row = SqlQueryUtil.selectOne(connection, ConsoleConstant.SQL_SELECT_REL);
			if ("t".equals(row.get("relhasindex"))) {
				printC.printResponse("-- 索引：\n");
				ctx.put("hasindex", 1);

				sw = new StringWriter();
				Velocity.mergeTemplate("d.vm", "UTF-8", ctx, sw);
				try {
					sw.close();
				} catch (IOException e1) {
					LM.error(LM.Model.CS.name(), e1);
				}
//				result = statement.executeQuery(sw.toString());
//				printC.printResult(result, 0, 0);
				result = SqlQueryUtil.selectList(connection,sw.toString());
				printC.printResult(result,-1, 0, 0);
			}
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
			cmdSql = cmdSql.substring(0, cmdSql.length());
		}
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
					LM.error(LM.Model.CS.name(), e);
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
}
