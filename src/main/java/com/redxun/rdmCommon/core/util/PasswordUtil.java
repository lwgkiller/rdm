package com.redxun.rdmCommon.core.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

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
 * @since 2020/3/18 9:29
 */
public class PasswordUtil {
    // 将明文密码加密为密文
    public static String encodePwd(String plainText) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte digest[] = md.digest(plainText.trim().getBytes("UTF-8"));
        String pwd = new String(Base64.encodeBase64(digest));
        return pwd;
    }
}
