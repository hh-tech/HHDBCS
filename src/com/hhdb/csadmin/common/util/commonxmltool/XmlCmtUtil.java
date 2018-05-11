package com.hhdb.csadmin.common.util.commonxmltool;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class XmlCmtUtil {
	/**
	 * 获取xmlbuilder
	 * 
	 * @return
	 * @throws Exception
	 */
	public static DocumentBuilder getDocumentBuilder() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder;
	}

	/**
	 * 将doc生成到file
	 * 
	 * @param doc
	 * @param f
	 * @throws Exception
	 */
	public static void generateXmlFile(Document doc, File f) throws Exception {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();// 得到转换器
		// 设置换行
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		// 写入文件
		transformer.transform(new DOMSource(doc), new StreamResult(f));
	}
}

