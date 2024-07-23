package com.redxun.digitization.core.service;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.alibaba.fastjson.JSONArray;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.menu.UIMenu;
import com.redxun.saweb.util.WebAppUtil;

@Service
public class RdmI18nUtil {
    // 菜单的国际化替换
    public static void oneMenuI18n(String language, UIMenu menu) {
        if (StringUtils.isNotBlank(language) && menu != null) {
            if (StringUtils.isNotBlank(menu.getKey())) {
                if (language.contains("en")) {
                    menu.setDisplayName(WebAppUtil.getI18nEn("menu." + menu.getKey(), menu.getName()));
                } else {
                    menu.setDisplayName(menu.getName());
                }
            }
        }
    }

    // 模块国际化
    public static void rootMenuI18n(HttpServletRequest request, JSONArray rootMenus) {
        if (rootMenus != null && !rootMenus.isEmpty()) {
            String language=toGetLanguage(request);
            for(int index=0;index<rootMenus.size();index++) {
                UIMenu oneMenu= (UIMenu) rootMenus.get(index);
                if (StringUtils.isNotBlank(oneMenu.getKey())) {
                    if (language.contains("en")) {
                        oneMenu.setDisplayName(WebAppUtil.getI18nEn("subsys." + oneMenu.getKey(), oneMenu.getName()));
                    } else {
                        oneMenu.setDisplayName(oneMenu.getName());
                    }
                }
            }
        }
    }

    // 查询某个key的值（根据语言环境）
    public static String toGetValueByKey(HttpServletRequest request, String key) {
        String result = "";
        if (StringUtils.isNotBlank(key)) {
            String language = toGetLanguage(request);
            if (language.contains("en")) {
                result = WebAppUtil.getI18nEn(key, key);
            } else {
                result = WebAppUtil.getI18nZh(key, key);
            }
        }
        return result;
    }

    // 获取语言环境
    public static String toGetLanguage(HttpServletRequest request) {
        String language = RdmConst.I18N_ZH_CN;
        Locale locale = (Locale)request.getSession().getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        if (locale != null) {
            language = locale.getLanguage();
        }
        return language;
    }
}
