package com.redxun.sys.core.util;

import org.apache.commons.lang.StringUtils;

import com.redxun.core.cache.CacheUtil;
import com.redxun.core.util.AppBeanUtil;
import com.redxun.core.util.BeanUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.sys.core.entity.SysProperties;
import com.redxun.sys.core.manager.SysPrivatePropertiesManager;
import com.redxun.sys.core.manager.SysPropertiesManager;
import com.redxun.sys.org.entity.OsUser;

/**
 * 系统参数工具类。
 *
 */
public class SysPropertiesUtil {

    /**
     * 获取全局的属性key。
     * 
     * @param key
     * @return
     */
    private static String getGlobalKey(String key) {
        String proKey = "property_" + key;
        return proKey;
    }

    /**
     * 取得租户参数key。
     * 
     * @param tenantId
     * @param proId
     * @return
     */
    private static String getTenantKey(String tenantId, String proId) {
        String proKey = "tenant_prop_" + tenantId + "_" + proId;
        return proKey;
    }

    /**
     * 根据key获取全局参数对象。
     * 
     * @param key
     * @return
     * @throws Exception
     */
    public static SysProperties getGlobalPropertyObj(String key) throws Exception {
        String proKey = getGlobalKey(key);
        SysPropertiesManager sysPropertiesManager = AppBeanUtil.getBean(SysPropertiesManager.class);

        /*if (CacheUtil.containKey(proKey)) {
            SysProperties properties = (SysProperties)CacheUtil.getCache(proKey);
            if (BeanUtil.isNotEmpty(properties)) {
                return properties;
            }
        }*/
        SysProperties sysProperties = sysPropertiesManager.getPropertiesByName(key);
        /*if (sysProperties != null) {
            CacheUtil.addCache(proKey, sysProperties);
        }*/
        return sysProperties;

    }

    /**
     * 通过名称获取参数
     * 
     * @param name
     * @return
     * @throws Exception
     */
    public static String getGlobalProperty(String key) {
        try {
            SysProperties sysProperties = getGlobalPropertyObj(key);
            if (sysProperties == null)
                return "";
            return sysProperties.getVal();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";

    }

    public static String getGlobalProperty(String key, String defaultValue) {
        SysProperties sysProperties;
        try {
            sysProperties = getGlobalPropertyObj(key);
            if (sysProperties == null)
                return defaultValue;
            return sysProperties.getVal();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    /**
     * 通过key查找 转换成integer的参数
     * 
     * @param key
     * @return
     * @throws Exception
     */
    public static Integer getGlobalPropertyInt(String key) {
        try {
            String globalProperties = getGlobalProperty(key);
            if (StringUtils.isNotBlank(globalProperties)) {
                Integer rtn = Integer.parseInt(globalProperties);
                if (rtn != null) {
                    return rtn;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 通过key 返回参数值。
     * 
     * @param key
     * @param defaultVal
     * @return
     * @throws Exception
     */
    public static Integer getGlobalPropertyInt(String key, Integer defaultVal) {
        try {
            String globalProperties = getGlobalProperty(key);
            if (StringUtils.isNotBlank(globalProperties)) {
                Integer rtn = Integer.parseInt(globalProperties);
                if (rtn != null) {
                    return rtn;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultVal;
    }

    /**
     * 通过key查找 转换成Boolean的参数
     * 
     * @param key
     * @return
     * @throws Exception
     */
    public static Boolean getGlobalPropertyBool(String key) {
        try {
            String globalProperties = getGlobalProperty(key);
            if (StringUtils.isNotBlank(globalProperties)) {
                Boolean rtn = Boolean.parseBoolean(globalProperties);
                if (rtn != null) {
                    return rtn;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 通过key查找 转换成Long的参数
     * 
     * @param key
     * @return
     * @throws Exception
     */
    public static Long getGlobalPropertyLong(String key) {
        try {
            String globalProperties = getGlobalProperty(key);
            if (StringUtils.isNotBlank(globalProperties)) {
                Long rtn = Long.parseLong(globalProperties);
                if (rtn != null) {
                    return rtn;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 通过key和tenantId查找机构的参数值
     * 
     * @param key
     * @param tenantId
     * @return
     * @throws Exception
     */
    public static String getTenantProperty(String key, String tenantId) throws Exception {
        SysProperties sysProperties = getGlobalPropertyObj(key);
        if (sysProperties != null) {// 先通过key获得公共的key以后续获取对应的私有参数

            String proKey = getTenantKey(tenantId, sysProperties.getProId());
            if (CacheUtil.containKey(proKey)) {
                return (String)CacheUtil.getCache(proKey);
            }
            SysPrivatePropertiesManager sysPrivatePropertiesManager =
                AppBeanUtil.getBean(SysPrivatePropertiesManager.class);
            String val = sysPrivatePropertiesManager.getValByProId(sysProperties.getProId(), tenantId);
            if (val != null) {
                CacheUtil.addCache(proKey, val);
            } else {
                val = sysProperties.getVal();
            }
            return val;
        }
        return null;
    }

    /**
     * 通过key和tenantId查找机构的参数值,并且转换成Long型
     * 
     * @param key
     * @param tenantId
     * @return
     * @throws Exception
     */
    public static Long getTenantPropertyLong(String key, String tenantId) throws Exception {
        String tenantProperties = getTenantProperty(key, tenantId);
        if (tenantProperties != null) {
            Long rtn = Long.parseLong(tenantProperties);
            if (rtn != null) {
                return rtn;
            }
        }
        return null;
    }

    /**
     * 通过key和tenantId查找机构的参数值,并且转换成Long型
     * 
     * @param key
     * @param tenantId
     * @return
     * @throws Exception
     */
    public static Integer getTenantPropertyInteger(String key, String tenantId) throws Exception {
        String tenantProperties = getTenantProperty(key, tenantId);
        if (tenantProperties != null) {
            Integer rtn = Integer.parseInt(tenantProperties);
            if (rtn != null) {
                return rtn;
            }
        }
        return null;
    }

    /**
     * 通过key和tenantId查找机构的参数值,并且转换成Boolean型
     * 
     * @param key
     * @param tenantId
     * @return
     * @throws Exception
     */
    public static Boolean getTenantPropertyBoolean(String key, String tenantId) throws Exception {
        String tenantProperties = getTenantProperty(key, tenantId);
        if (tenantProperties != null) {
            Boolean rtn = Boolean.parseBoolean(tenantProperties);
            if (rtn != null) {
                return rtn;
            }
        }
        return null;
    }

    /**
     * 获得租户的管理员账号
     * 
     * @return
     */
    public static String getTenantAdminAccount() {
        try {
            String account = SysPropertiesUtil.getGlobalProperty("tenantAdminAccount");
            if (StringUtil.isNotEmpty(account)) {
                return account;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return OsUser.TADMIN;
    }

    /**
     * 取得系统超管帐号。
     * 
     * @return
     */
    public static String getAdminAccount() {
        try {
            String account = SysPropertiesUtil.getGlobalProperty("adminAccount");
            if (StringUtil.isNotEmpty(account)) {
                return account;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "admin";

    }

}
