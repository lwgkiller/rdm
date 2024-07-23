package com.redxun.core.seq;

import org.springframework.beans.factory.InitializingBean;

/**
 * ID产生器接口类
 * @author mansan
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public interface IdGenerator extends InitializingBean{
    /**
     * 获取唯一的ID标识
     * @return 
     */
    public Long getLID();
    /**
     * 获取唯一ID字符串
     * @return 
     */
    public String getSID();
}
