package com.redxun.info.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2020 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2020/12/5 15:00
 */
public class BeanReflectUtil {
    private static Logger logger = LoggerFactory.getLogger(BeanReflectUtil.class);

    public static Object newObj(String className, Object... args) {
        Class[] classes = new Class[args.length];
        for (int i = 0; i < classes.length; i++) {
            classes[i] = args[i].getClass();
        }
        Object obj = null;
        try {
            obj = Class.forName(className).getConstructor(classes).newInstance(args);
        } catch (Exception e) {
            logger.error("Exception in reflect bean " + className, e);
        }
        return obj;
    }

}
