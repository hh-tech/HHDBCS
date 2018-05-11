package com.hhdb.csadmin.common.util.commonxmltool;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * xml解析引擎
 * 
 * @author chenyong
 * 
 */
public class XmlEnger {
	private String encode = "UTF-8";
	private NodeEntity rootEntity;
	private Document document;

	public String getEncode() {
		return encode;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}

	public NodeEntity getRootEntity() {
		return rootEntity;
	}

	public void setRootEntity(NodeEntity rootEntity) {
		this.rootEntity = rootEntity;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public void setXml(String xml) throws SAXException, IOException, Exception {
		StringReader sr = new StringReader(xml);
		InputSource is = new InputSource(sr);
		document = XmlCmtUtil.getDocumentBuilder().parse(is);
		paserEntity();
	}

	public void setXmlFile(File f) throws SAXException, IOException, Exception {
		document = XmlCmtUtil.getDocumentBuilder().parse(f);
		paserEntity();
	}

	public void setXmlInputStream(InputStream is) throws SAXException, IOException, Exception {
		document = XmlCmtUtil.getDocumentBuilder().parse(is);
		paserEntity();
	}

	/**
	 * 解析xml
	 */
	public void paserEntity() {
		Element e = document.getDocumentElement();
		rootEntity = new NodeEntity();
		parse(e, rootEntity);
	}

	/**
	 * 解析xml
	 * 
	 * @param e
	 * @param node
	 */
	private static void parse(Element e, NodeEntity node) {
		String name = e.getNodeName();
		String text = "";
		if (e.getFirstChild() != null) {
			text = e.getFirstChild().getTextContent();
		}

		node.setName(name);
		node.setText(text);

		NodeEntity parentNode = node.getParentNode();
		if (parentNode != null) {
			node.setPath(parentNode.getPath() + XmlTools.NODEPATH_SPLIT + name);
		} else {
			node.setPath(name);
		}
		NamedNodeMap attriMap = e.getAttributes();
		for (int i = 0; i < attriMap.getLength(); i++) {
			Node n = attriMap.item(i);

			String key = n.getNodeName();
			String value = n.getNodeValue();
			node.addAttri(key, value);
		}

		NodeList nList = e.getChildNodes();
		if (nList.getLength() > 0) {
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element nodeElement = (Element) nNode;
					NodeEntity newNode = new NodeEntity();
					newNode.setParentNode(node);
					node.addNode(newNode);

					parse(nodeElement, newNode);
				}
			}
		} else {
			return;
		}
	}

	/**
	 * 获取单个节点，如果存在多个，返回第一个
	 * 
	 * @param nodePath
	 * @return
	 */
	public NodeEntity getNode(String nodePath) {
		List<NodeEntity> nodeList = getNodeList(nodePath);
		if (nodeList.size() > 0) {
			return nodeList.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 获取单个节点，返回第一个key相等的节点
	 * 
	 * @param nodePath
	 * @return
	 */
	public NodeEntity getNodeByKey(String nodePath, String key, String value) {
		List<NodeEntity> nodeList = getNodeList(nodePath);

		if (nodeList.size() <= 0) {
			return null;
		}

		for (NodeEntity node : nodeList) {
			if (node.getAttri(key, "").equals(value)) {
				return node;
			}
		}

		return null;
	}

	/**
	 * 获取多个节点，返回key相等的节点
	 * 
	 * @param nodePath
	 * @return
	 */
	public List<NodeEntity> getNodeListByKey(String nodePath, String key, String value) {
		List<NodeEntity> nodeList = getNodeList(nodePath);

		if (nodeList.size() <= 0) {
			return null;
		}

		List<NodeEntity> retList = new ArrayList<NodeEntity>();

		for (NodeEntity node : nodeList) {
			if (node.getAttri(key, "").equals(value)) {
				retList.add(node);
			}
		}

		return retList;
	}

	/**
	 * 获取节点列表
	 * 
	 * @param nodePath
	 * @return
	 */
	public List<NodeEntity> getNodeList(String nodePath) {
		List<NodeEntity> nodeList = new ArrayList<NodeEntity>();
		parseNodeList(rootEntity, nodeList, nodePath);
		return nodeList;
	}

	/**
	 * 根据key-value获取节点，默认获取第一个
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public NodeEntity getChildByKey(NodeEntity node, String key, String value) {
		List<NodeEntity> childList = node.getChildList();
		if (childList.size() <= 0) {
			return null;
		}
		for (NodeEntity c : childList) {
			if (c.getAttri(key, "").equals(value)) {
				return c;
			}
		}

		return null;
	}

	/**
	 * 根据子名称获取节点，默认获取第一个
	 * 
	 * @param name
	 * @return
	 */
	public NodeEntity getChildByName(NodeEntity node, String name) {
		List<NodeEntity> childList = node.getChildList();
		if (childList.size() <= 0) {
			return null;
		}
		for (NodeEntity c : childList) {
			if (c.getName().equals(name)) {
				return c;
			}
		}

		return null;
	}

	/**
	 * 根据子名称获取节点列表
	 * 
	 * @param name
	 * @return
	 */
	public List<NodeEntity> getChildsByName(NodeEntity node, String name) {
		List<NodeEntity> childList = node.getChildList();
		List<NodeEntity> nodeList = new ArrayList<NodeEntity>();
		if (childList.size() <= 0) {
			return nodeList;
		}

		for (NodeEntity c : childList) {
			if (c.getName().equals(name)) {
				nodeList.add(c);
			}
		}

		return nodeList;
	}

	/**
	 * 根据相对路径获取节点
	 * 
	 * @param path
	 * @return
	 */
	public List<NodeEntity> getNodeListRePath(NodeEntity node, String path) {
		List<NodeEntity> childList = node.getChildList();
		List<NodeEntity> nodeList = new ArrayList<NodeEntity>();
		if (childList.size() <= 0) {
			return nodeList;
		}
		String cpath = node.getPath() + XmlTools.NODEPATH_SPLIT + path;
		parseNodeList(node, nodeList, cpath);
		return nodeList;
	}

	/**
	 * 递归解析
	 * 
	 * @param nodeEntity
	 * @param nodeList
	 * @param path
	 */
	private void parseNodeList(NodeEntity nodeEntity, List<NodeEntity> nodeList, String path) {
		if (nodeEntity.getPath().equals(path)) {
			nodeList.add(nodeEntity);
		}

		List<NodeEntity> childNodeList = nodeEntity.getChildList();

		if (childNodeList.size() <= 0) {
			return;
		} else {
			for (NodeEntity node : childNodeList) {
				parseNodeList(node, nodeList, path);
			}
		}
	}

	/**
	 * 生成xml
	 * 
	 * @param nodeEntity
	 * @param filePath
	 * @throws Exception
	 */
	public void generateXml(NodeEntity nodeEntity, String filePath) throws Exception {
		if (nodeEntity == null) {
			return;
		}

		DocumentBuilder builder = XmlCmtUtil.getDocumentBuilder();
		Document doc = builder.newDocument();

		if (null != doc) {
			Element e = doc.createElement(nodeEntity.getName());
			// 处理根节点
			Map<String, String> attrMap = nodeEntity.getAttrMap();
			if (attrMap != null && attrMap.size() > 0) {
				Set<String> attrKeySet = attrMap.keySet();
				for (String attrkey : attrKeySet) {
					e.setAttribute(attrkey, XmlTools.getString(attrMap, attrkey));
				}
			}

			if (!XmlTools.isEmpty(nodeEntity.getText())) {
				e.setTextContent(nodeEntity.getText());
			}

			List<NodeEntity> nodeList = nodeEntity.getChildList();
			if (nodeList != null && nodeList.size() > 0) {
				for (NodeEntity subNode : nodeList) {
					parseGenerateXml(doc, e, subNode);
				}
			}

			doc.appendChild(e);
		}

		File f = new File(filePath);
		XmlCmtUtil.generateXmlFile(doc, f);
	}

	private void parseGenerateXml(Document doc, Element parentElement, NodeEntity curNode) {
		Element curElement = doc.createElement(curNode.getName());

		Map<String, String> attrMap = curNode.getAttrMap();
		if (attrMap != null && attrMap.size() > 0) {
			Set<String> attrKeySet = attrMap.keySet();
			for (String attrkey : attrKeySet) {
				curElement.setAttribute(attrkey, XmlTools.getString(attrMap, attrkey));
			}
		}

		if (!XmlTools.isEmpty(curNode.getText())) {
			curElement.setTextContent(curNode.getText());
		}

		parentElement.appendChild(curElement);

		List<NodeEntity> nodeList = curNode.getChildList();
		if (nodeList != null && nodeList.size() > 0) {
			for (NodeEntity nodeEntity : nodeList) {
				parseGenerateXml(doc, curElement, nodeEntity);
			}
		} else {
			return;
		}
	}
}
