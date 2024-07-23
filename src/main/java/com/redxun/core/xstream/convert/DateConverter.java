package com.redxun.core.xstream.convert;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.redxun.core.util.DateUtil;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
/**
 * XStream 日期转化类
 * @author mansan
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public class DateConverter implements Converter{

	@Override
	public boolean canConvert(Class type) {
		boolean is= type.equals(Date.class) || type.equals(Timestamp.class);
		return is;
	}

	@Override
	public void marshal(Object object, HierarchicalStreamWriter writer, MarshallingContext context) {
		if(object==null) writer.setValue("");
		Date date = (Date) object;  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);  
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        String str=format.format(calendar.getTime());
        writer.setValue(str);
		  
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		String val=reader.getValue();
		return DateUtil.parseDate(val);
	}

}
