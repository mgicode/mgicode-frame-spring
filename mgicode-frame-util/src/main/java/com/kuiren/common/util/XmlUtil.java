package com.kuiren.common.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


/**
 * @author pengrk
 * @email:sjkjs155@126.com
 * @wetsite:www.mgicode.com
 * @license:GPL
 */
public class XmlUtil {

	public static File readFile(String filename) throws IOException {
		Resource r = (Resource) new ClassPathResource(filename);
		return r.getFile();

	}

	public static Document loadXml(String filename) throws Exception {

		SAXReader reader = new SAXReader();
		Document doc = reader.read(readFile(filename));
		return doc;
	}

	public static Element getRootElement(Document doc) {
		return doc.getRootElement();

	}

	public static Element selectElement(Element element, String xpath) {
		List list = element.selectNodes(xpath);
		if (list == null || list.size() < 1) {
			return null;
		}

		Element elem = (Element) list.get(0);
		return elem;
	}

	public static List<Element> selectElements(Element element, String xpath) {
		if (element == null)
			return null;
		List list = element.selectNodes(xpath);
		return list;
	}

	public static String[] getItems(String type, String filename)
			throws Exception {

		Element element = selectElement(getRootElement(loadXml(filename)), type);
		if (element == null || StringUtil.IsNullOrEmpty(element.getTextTrim())) {
			return null;
		}
		return element.getTextTrim().split(",");
	}

	public static String getItem(String type, String filename) throws Exception {

		Element element = selectElement(getRootElement(loadXml(filename)), type);
		if (element == null || StringUtil.IsNullOrEmpty(element.getTextTrim())) {
			return null;
		}
		return element.getTextTrim();
	}
}