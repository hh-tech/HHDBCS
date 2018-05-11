package com.hhdb.csadmin.common.util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreePath;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.hh.frame.common.log.LM;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.csv.writer.CsvWriter;
import com.hh.frame.swingui.util.XmlFileUtil;
import com.hhdb.csadmin.common.bean.SqlBean;
import com.hhdb.csadmin.plugin.tree.been.MetaTreeNodeBean;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;
import com.hhdb.csadmin.plugin.tree.util.TreeNodeUtil;

public class ExtendXmlLoader {
	private NodeList extnodelist = null;
	private Connection conn = null;

	public ExtendXmlLoader(Connection conn) {
		this.conn = conn;
		String path = "com/hhdb/csadmin/plugin/tree/xml/"+StartUtil.prefix+"_ext.xml";
		try {
			Document doc = XmlFileUtil.getResXmlDoc(path);
			NodeList allList = doc.getElementsByTagName("all");
			Element allElement = (Element) allList.item(0);
			extnodelist = allElement.getElementsByTagName("ext");
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
		}
	}

	public List<Map<String, Object>> getMenuItemsList() throws Exception {
		List<Map<String, Object>> li = getExtendList();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < extnodelist.getLength(); i++) {
			Element extElement = (Element) extnodelist.item(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", extElement.getAttribute("id"));
			map.put("name", extElement.getAttribute("name"));
			NodeList cneList = extElement.getElementsByTagName("check");
			if (isTrue(li, extElement.getAttribute("name"))) {
				map.put("icon", extElement.getAttribute("icon"));
				if (cneList.getLength() > 0) {
					boolean fl = true;
					for (int j = 0; j < cneList.getLength(); j++) {
						Element cneElement = (Element) cneList.item(j);
						Map<String, Object> maps = SqlQueryUtil.select(conn,
								cneElement.getAttribute("rule")).get(0);
						if (!cneElement.getAttribute("res").equals(
								maps.get(cneElement.getAttribute("key")))) {
							fl = false;
						}
					}
					if (!fl) {
						map.put("icon", "extend3.png");
					}
				}
			} else {
				map.put("icon", "extend2.png");
			}
			map.put("comment", extElement.getAttribute("comment"));
			list.add(map);
		}
		return list;
	}

	public boolean isLoadExtend(String extenName) throws Exception {
		List<Map<String, Object>> li = getExtendList();
		boolean bl = false;
		for (int i = 0; i < extnodelist.getLength(); i++) {
			Element extElement = (Element) extnodelist.item(0);
			if (extenName.equals(extElement.getAttribute("name"))) {
				NodeList cneList = extElement.getElementsByTagName("check");
				if (isTrue(li, extElement.getAttribute("name"))) {
					bl = true;
					if (cneList.getLength() > 0) {
						for (int j = 0; j < cneList.getLength(); j++) {
							Element cneElement = (Element) cneList.item(j);
							Map<String, Object> maps = SqlQueryUtil.select(
									conn, cneElement.getAttribute("rule")).get(
									0);
							if (cneElement.getAttribute("res").equals(
									maps.get(cneElement.getAttribute("key")))) {
								bl = true;
							} else {
								bl = false;
							}
						}
					}
				}
			}
		}
		return bl;
	}

	public boolean isTrue(List<Map<String, Object>> li, String name) {
		boolean bb = false;
		for (Map<String, Object> mp : li) {
			if (mp.get("name").equals(name)) {
				bb = true;
			}
		}
		return bb;
	}

	/**
	 * 获取所有安装的扩展
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getExtendList()
			throws Exception {
		SqlBean sb = HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.EXTEND,
				"prop_coll");
		List<Map<String, Object>> rs = SqlQueryUtil.select(conn, sb.getSql());
		return rs;
	}

	/**
	 * 获得扩展集合的属性
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getAttrExtendCollection() throws Exception {
		List<Map<String, Object>> exllsit = getMenuItemsList();
		Collection<String[]> data = new ArrayList<>();
		String[] str1 = new String[3];
		str1[0] = "扩展名称";
		str1[1] = "是否安装";
		str1[2] = "扩展描述";
		data.add(str1);

		for (Map<String, Object> map : exllsit) {
			String[] str2 = new String[3];
			str2[0] = map.get("name").toString();
			String icon = map.get("icon").toString();
			if (icon.equals("extend.png")) {
				str2[1] = "已安装";
			} else if (icon.equals("extend.png3")) {
				str2[1] = "未正确安装";
			} else {
				str2[1] = "未安装";
			}
			str2[2] = map.get("comment").toString();
			data.add(str2);
		}
		CsvWriter csvWriter = new CsvWriter();
		StringBuffer sbf = new StringBuffer();
		csvWriter.writeSbf(sbf, data);
		return sbf.toString();
	}
	
	/**
	 * 获得扩展插件的属性
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getAttrExtend(BaseTreeNode treeNode) throws Exception {		
		String extendName = treeNode.getMetaTreeNodeBean().getName();
		List<Map<String, Object>> exllsit = getExtendList();
		if(!isTrue(exllsit, extendName)){
			return "";
		}
		Collection<String[]> data = new ArrayList<>();
		String[] str1 = new String[3];
		str1[0] = "扩展ID";
		str1[1] = "扩展名称";
		str1[2] = "扩展内容";
		data.add(str1);
		
		SqlBean sb = HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.EXTEND,
				"prop");
		Object[] params = new Object[1];
		params[0] = "'"+extendName+"'";
		List<Map<String, Object>> rs = SqlQueryUtil.select(conn, sb.replaceParams(params));
		for (Map<String, Object> map : rs) {
			String[] str2 = new String[3];
			str2[0] = map.get("refobjid").toString();
			str2[1] = extendName;
			str2[2] = map.get("desc").toString();
			data.add(str2);
		}
		CsvWriter csvWriter = new CsvWriter();
		StringBuffer sbf = new StringBuffer();
		csvWriter.writeSbf(sbf, data);
		return sbf.toString();
	}
	
	/**
	 * 刷新扩展集合
	 * @param treeNode
	 * @throws Exception
	 */
	public void refreshExtendCollection(BaseTreeNode treeNode) throws Exception{
		try {
			List<Map<String, Object>> exllsit = getMenuItemsList();
			treeNode.removeAllChildren();
			for(Map<String,Object> map:exllsit){
				BaseTreeNode dbtreenode = new BaseTreeNode(treeNode.getHtree());
				MetaTreeNodeBean dbmtn = new MetaTreeNodeBean();
				dbmtn.setName(map.get("name").toString());
				dbmtn.setOpenIcon(map.get("icon").toString());
				dbmtn.setType(TreeNodeUtil.EXTENSION_PLUGIN_TYPE);
				dbmtn.setUnique(false);
				dbtreenode.setMetaTreeNodeBean(dbmtn);
				dbtreenode.setParentBaseTreeNode(treeNode);	
				treeNode.addChildNode(dbtreenode);
			}
			// 加上这两句是可以让节点刷新后直接展开
			TreePath path = new TreePath(treeNode.getHtree().getTree()
					.getTreeModel().getPathToRoot(treeNode));
			treeNode.getHtree().getTree().expandPath(path);
			// 重新加载节点
			treeNode.getHtree().getTree().getTreeModel().reload(treeNode);
			
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
			throw e;
		}
	}
	
	/**
	 * 安装扩展插件
	 * @param treeNode
	 * @throws Exception
	 */
	public void installExtend(String extendName) throws Exception{
		SqlBean sb = HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.EXTEND,
				"create");
		Object[] params = new Object[1];
		params[0] = extendName;
		SqlExeUtil.executeUpdate(conn, sb.replaceParams(params));
	}
	
	/**
	 * 删除扩展插件
	 * @param treeNode
	 * @throws Exception
	 */
	public void dropExtend(String extendName) throws Exception{
		SqlBean sb = HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.EXTEND,
				"drop");
		Object[] params = new Object[1];
		params[0] = extendName;
		SqlExeUtil.executeUpdate(conn, sb.replaceParams(params));
	}

}
