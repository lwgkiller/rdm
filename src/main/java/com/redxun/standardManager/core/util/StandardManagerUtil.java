package com.redxun.standardManager.core.util;

import javax.servlet.http.HttpServletRequest;
import com.redxun.core.util.AppBeanUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

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
 * @since 2020/5/26 16:58
 */
public class StandardManagerUtil {
    private static Logger logger = LoggerFactory.getLogger(StandardManagerUtil.class);

    /**
     * 判断是否是管理网登录,判断技术网段,如果是vpn则看职级是否有管理字样,没有返回true
     * 
     * @param request
     * @return
     */
    public static boolean judgeGLNetwork(HttpServletRequest request) {
        String clientIp = getIpAddr(request);
        // logger.error("****************client IP is {}", clientIp);
        if (StringUtils.isBlank(clientIp)) {
            return true;
        }
        String[] ipArr = clientIp.split("\\.", -1);
        if (clientIp.startsWith("10.15")) {
            // 此网段内为技术网
            int thirdIpInt = Integer.parseInt(ipArr[2]);
            if (thirdIpInt >= 4 && thirdIpInt <= 7) {
                return false;
            }
        } else if (clientIp.startsWith("10.19")) {
            // 此网段内为技术网
            int thirdIpInt = Integer.parseInt(ipArr[2]);
            if (thirdIpInt >= 4 && thirdIpInt <= 7) {
                return false;
            }
        } else if (clientIp.equalsIgnoreCase(SysPropertiesUtil.getGlobalProperty("crossNetWhiteIp"))) {
            // 白名单->能获取上下文->能获取到用户职级->不含管理则是技术
            // 要求白名单ip为单一
            String currentUserId = null;
            try {
                currentUserId = ContextUtil.getCurrentUserId();
                if (currentUserId == null) {
                    // 获取不到上下文直接返回管理网
                    return true;
                }
            } catch (Exception e) {
                logger.error("Exception in judgeGLNetwork get userId", e);
                return true;
            }

            Map<String, Object> userInfos = new HashMap<>();
            // 查找用户的部门、职级信息 获取用户的职级
            try {
                AppBeanUtil.getBean(XcmgProjectManager.class).getUserInfoById(currentUserId, userInfos);
            } catch (Exception e) {
                logger.error("Exception in judgeGLNetwork get userInfos", e);
                return true;
            }
            String zjName = "管理";
            if (userInfos.get("ZJ") != null && StringUtils.isNotBlank(userInfos.get("ZJ").toString())) {
                zjName = userInfos.get("ZJ").toString();
            }
            if (!zjName.contains("管理")) {
                return false;
            }
        }
        return true;
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("PRoxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (StringUtils.isNotBlank(ip)) {
            if (ip.contains(",") || ip.contains("，")) {
                String[] ipArr = ip.split(",|，", -1);
                for (String oneIp : ipArr) {
                    if (!oneIp.equalsIgnoreCase("unknown")) {
                        ip = oneIp;
                        break;
                    }
                }
            }
        }
        return ip;
    }
}
