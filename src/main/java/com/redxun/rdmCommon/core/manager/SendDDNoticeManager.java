package com.redxun.rdmCommon.core.manager;

import java.util.*;

import javax.annotation.Resource;

import com.redxun.sys.core.util.SysPropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.HttpClientUtil;
import com.redxun.rdmCommon.core.dao.RdmDao;
import com.redxun.saweb.util.WebAppUtil;

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
 * @since 2020/8/14 16:06
 */
@Service
public class SendDDNoticeManager {
    private static final Logger logger = LoggerFactory.getLogger(SendDDNoticeManager.class);

    @Resource
    RdmDao rdmDao;

    /**
     * 为待办任务发送钉钉通知
     *
     * @param userList
     * @param taskObj
     */
    public void sendNoticeForTask(List<JSONObject> userList, JSONObject taskObj) {
        if (userList == null || userList.isEmpty()) {
            return;
        }
        String developOrProduce = SysPropertiesUtil.getGlobalProperty("developOrProduce");
        if ("develop".equals(developOrProduce)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (JSONObject oneUser : userList) {
            String certNo = oneUser.getString("userCertNo");
            if (StringUtils.isNotBlank(certNo)) {
                sb.append(certNo).append(",");
            }
        }
        String userCertNos="";
        if (sb.length()>0) {
            userCertNos = sb.substring(0, sb.length() - 1);
        }
        if (StringUtils.isBlank(userCertNos)) {
            logger.info("钉钉待办通知人的身份证号为空！");
            return;
        }
        JSONObject oneDDMessage = new JSONObject();
        oneDDMessage.put("message", "您有一条RDM系统待办：" + taskObj.getString("content"));
        oneDDMessage.put("userNos", userCertNos);
        JSONArray array = new JSONArray();
        array.add(oneDDMessage);
        String url = WebAppUtil.getProperty("dd_url");
        JSONObject ddObj = new JSONObject();
        ddObj.put("agentId", WebAppUtil.getProperty("dd_agentId"));
        ddObj.put("appKey", WebAppUtil.getProperty("dd_appKey"));
        ddObj.put("appSecret", WebAppUtil.getProperty("dd_appSecret"));
        ddObj.put("content", array);
        httpSendNotices(ddObj, url);
        logger.info("待办钉钉发送至：" + oneDDMessage.getString("userNos") + "，内容：" + oneDDMessage.getString("message"));
    }

    /**
     * 钉钉通知 公共
     *
     * @param taskObj
     */
    public void sendNoticeForCommon(String userIds, JSONObject taskObj) {
        String developOrProduce = SysPropertiesUtil.getGlobalProperty("developOrProduce");
        if ("develop".equals(developOrProduce)) {
            return;
        }
        String url = WebAppUtil.getProperty("dd_url");
        if (StringUtils.isBlank(userIds) || StringUtils.isBlank(url)) {
            return;
        }
        JSONObject oneDDMessage = new JSONObject();
        oneDDMessage.put("message", taskObj.getString("content"));
        String userNos = getUserCertNoById(userIds);
        if (StringUtils.isBlank(userNos)) {
            return;
        }
        oneDDMessage.put("userNos", userNos);

        JSONObject ddObj = new JSONObject();
        ddObj.put("agentId", WebAppUtil.getProperty("dd_agentId"));
        ddObj.put("appKey", WebAppUtil.getProperty("dd_appKey"));
        ddObj.put("appSecret", WebAppUtil.getProperty("dd_appSecret"));
        JSONArray array = new JSONArray();
        ddObj.put("content", array);
        array.add(oneDDMessage);
        logger.info("待办钉钉发送至：" + oneDDMessage.getString("userNos") + "，内容：" + oneDDMessage.getString("message"));
        httpSendNotices(ddObj, url);
    }

    /**
     * 通过http调用钉钉
     *
     * @param sendObj
     */
    public static void httpSendNotices(JSONObject sendObj, String url) {
        try {
            Map<String, String> reqHeaders = new HashMap<>();
            reqHeaders.put("Content-Type", "application/json;charset=utf-8");
            String rtnCode = HttpClientUtil.postJson(url, sendObj.toJSONString(), reqHeaders);
        } catch (Exception e) {
            logger.error("Exception in httpSendNotices", e);
        }
    }

    /**
     * 根据用户id 转换为用户身份证号
     *
     */
    public String getUserCertNoById(String userIds) {
        String userCertNos = "";
        try {
            String[] userIdArray = userIds.split(",");
            Map paramJson = new HashMap(16);
            paramJson.put("ids", Arrays.asList(userIdArray));
            List<Map> resultArray = rdmDao.queryUserInfoByIds(paramJson);
            Set<String> certNoSet = new HashSet<>();
            for (Map oneUser : resultArray) {
                certNoSet.add(CommonFuns.nullToString(oneUser.get("certNo")));
            }

            StringBuilder sb = new StringBuilder();
            for (String certNo : certNoSet) {
                if (StringUtils.isNotBlank(certNo)) {
                    sb.append(certNo).append(",");
                }
            }
            if (sb.length() == 0) {
                return "";
            }
            userCertNos = sb.substring(0, sb.length() - 1);
        } catch (Exception e) {
            logger.error("Exception in getUserCertNoById", e);
        }
        return userCertNos;
    }
}
