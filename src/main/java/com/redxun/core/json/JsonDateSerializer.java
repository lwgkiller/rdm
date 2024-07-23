package com.redxun.core.json;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * 
 * <pre> 
 * 描述：对日期类型进行格式化处理
 * 构建组：ent-base-core
 * 作者：csx
 * 邮箱:chensx@jee-soft.cn
 * 日期:2014年11月8日-上午11:44:14
 * 广州红迅软件有限公司（http://www.redxun.cn）
 * </pre>
 */
public class JsonDateSerializer extends JsonSerializer<Date>{
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Override
	public void serialize(Date value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		String formattedDate = dateFormat.format(value);
		jgen.writeString(formattedDate);
	}
}
