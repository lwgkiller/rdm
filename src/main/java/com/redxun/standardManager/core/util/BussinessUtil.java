package com.redxun.standardManager.core.util;

import java.util.List;
import java.util.Map;

/**
 * @author zhangzhen
 */
public class BussinessUtil {
    // 查询指定类别在数组中的索引
    public static String toQuerySystemIndex(List<Map<String, Object>> systemCategorys, String targetSystemCategoryId) {
        for (int i = 0; i < systemCategorys.size(); i++) {
            Map<String, Object> onesystemCategory = systemCategorys.get(i);
            String systemCategoryId = onesystemCategory.get("systemCategoryId").toString();
            if (targetSystemCategoryId.equalsIgnoreCase(systemCategoryId)) {
                return "" + i;
            }
        }
        return "0";
    }
}
