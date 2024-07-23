package com.redxun.info.core.util;

import java.util.StringTokenizer;

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
 * @since 2020/12/7 9:39
 */
public class InfoUtil {

    /**
     * 将字符串前后空格去掉，当中的制表符、回车、换行转为空格，多个空格变成1个
     * 
     * @param str
     * @return
     */
    public static String trimBlankForStr(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        str = str.trim();
        str = str.replaceAll("\t|\r|\n", " ");
        StringTokenizer pas = new StringTokenizer(str, " ");
        while (pas.hasMoreTokens()) {
            stringBuilder.append(pas.nextToken()).append(" ");
        }
        return stringBuilder.toString();
    }
}
