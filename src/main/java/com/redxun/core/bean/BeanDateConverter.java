package com.redxun.core.bean;

import java.util.Date;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * <pre>
 * 描述：日期格式的转换处理
 * 构建组：ent-base-core
 * 作者：csx
 * 邮箱:chensx@jee-soft.cn
 * 日期:2014年10月20日-上午11:54:55
 * 广州红迅软件有限公司（http://www.redxun.cn）
 * </pre>
 */
public class BeanDateConverter implements Converter {

    protected Logger logger=LogManager.getLogger(BeanDateConverter.class);

    public BeanDateConverter() {
        
    }
    /**
     * 值转化
     * @param type
     * @param value
     * @return 
     */
    @Override
    public Object convert(Class type, Object value) {
        
        if(value==null) return null;
        
        if(type.getName().equals("java.util.Date") && value instanceof Date){
        	return value;
        }
        
        String dateStr = value.toString();
        if (dateStr.length() > 19) {
            dateStr = dateStr.substring(0, 19);
        }
       
        try {
            return (Object) DateUtils.parseDate(dateStr, new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"});
        } catch (Exception ex) {
            logger.debug("parse date " + dateStr + " error" + ex.getMessage());
        }

        return null;
    }
}
