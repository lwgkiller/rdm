package com.redxun.materielextend.core.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.materielextend.core.dao.MaterielApplyDao;

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
 * @since 2020/6/9 16:32
 */
@Service
public class CommonService {
    @Autowired
    private MaterielApplyDao materielApplyDao;

    /**
     * 通过用户ID查询用户名称，返回id--name对应关系
     *
     * @param userIds
     * @return
     */
    public Map<String, String> queryUserNameByIds(Set<String> userIds) {
        Map<String, String> userId2Name = new HashMap<>();
        if (userIds == null || userIds.isEmpty()) {
            return userId2Name;
        }
        List<JSONObject> userInfos = materielApplyDao.queryUserInfosByIds(new ArrayList<>(userIds));
        if (userInfos == null || userInfos.isEmpty()) {
            return userId2Name;
        }
        for (JSONObject oneUserInfo : userInfos) {
            userId2Name.put(oneUserInfo.getString("userId"),
                oneUserInfo.getString("userName") + " " + oneUserInfo.getString("mobile"));
        }
        return userId2Name;
    }
}
