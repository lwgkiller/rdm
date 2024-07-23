package com.redxun.core.util;

import javax.xml.stream.XMLInputFactory;

/**
 * XML解析工具
 * @author csx
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public class XmlUtil {
	
	public static XMLInputFactory createSafeXmlInputFactory() {
		XMLInputFactory xif = XMLInputFactory.newInstance();
		if (xif.isPropertySupported(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES)) {
			xif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
		}

		if (xif.isPropertySupported(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES)) {
			xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		}

		if (xif.isPropertySupported(XMLInputFactory.SUPPORT_DTD)) {
			xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		}
		return xif;
	}
}
