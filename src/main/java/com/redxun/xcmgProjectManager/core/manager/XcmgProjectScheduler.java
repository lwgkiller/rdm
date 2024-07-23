package com.redxun.xcmgProjectManager.core.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.ProductService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.core.dao.SysPropertiesDao;
import com.redxun.sys.core.entity.SysProperties;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectSchedulerDao;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

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
 * @since 2020/7/6 18:50
 */
@Service
@EnableScheduling
public class XcmgProjectScheduler {
    private static Logger logger = LoggerFactory.getLogger(XcmgProjectScheduler.class);
    @Autowired
    private XcmgProjectSchedulerDao xcmgProjectSchedulerDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private SysPropertiesDao sysPropertiesDao;
    @Resource
    private ProductService productService;

    /**
     * 每天早上9:10进行项目延迟的扫描处理
     */
    @Scheduled(cron = "0 10 9 * * *")
    public void projectDelayNotice() {
        String webappName = WebAppUtil.getProperty("webappName", "rdm");
        if (!"rdm".equalsIgnoreCase(webappName)) {
            return;
        }
        String developOrProduce = SysPropertiesUtil.getGlobalProperty("developOrProduce");
        if ("develop".equals(developOrProduce)) {
            return;
        }

        SysProperties sysProperties = sysPropertiesDao.getPropertiesByName("sendProjectDelayNotice");
        String switchValue = sysProperties.getValue();
        if (StringUtils.isBlank(switchValue) || "no".equalsIgnoreCase(switchValue)) {
            return;
        }
        // 查询所有running中的项目(去掉进度追赶状态的)
        Map<String, Object> params = new HashMap<>();
        params.put("instStatus", ConstantUtil.RUNNING);
        List<JSONObject> runningProjects = xcmgProjectSchedulerDao.queryNormalRunningProjectList(params);
        if (runningProjects == null || runningProjects.isEmpty()) {
            return;
        }
        // 查询所有运行中项目的当前阶段的计划结束时间(有可能有的项目还没有，目前没到策划阶段)
        Map<String, JSONObject> projectId2Obj = new HashMap<>();
        List<Map<String, Object>> queryPlanParams = new ArrayList<>();
        for (JSONObject oneProject : runningProjects) {
            String projectId = oneProject.getString("projectId");
            projectId2Obj.put(projectId, oneProject);
            String currentStageId = oneProject.getString("currentStageId");
            Map<String, Object> oneParam = new HashMap<>();
            oneParam.put("projectId", projectId);
            oneParam.put("currentStageId", currentStageId);
            queryPlanParams.add(oneParam);
        }
        List<JSONObject> projectCurrentPlanEndTimes = queryProjectPlanTimes(queryPlanParams);
        for (JSONObject onePlan : projectCurrentPlanEndTimes) {
            String planEndTime = onePlan.getString("planEndTime");
            if (StringUtils.isBlank(planEndTime)) {
                continue;
            }
            projectId2Obj.get(onePlan.getString("projectId")).put("planEndTime", planEndTime);
        }
        // 判断每一个项目当前是否有延误、延误的等级
        List<JSONObject> delayProjects = judgeProjectDelay(runningProjects);
        if (delayProjects.isEmpty()) {
            return;
        }
        // 判断上步延误的项目是否已经在这个阶段发过特定等级的提醒通知
        List<JSONObject> needSendNoticeProjects = filterNeedSendNoticeProjects(delayProjects);
        if (needSendNoticeProjects.isEmpty()) {
            return;
        }
        // 查询出需要发送通知的项目的负责人和指导人、分管领导
        queryNoticeSenderInfos(needSendNoticeProjects);
        // 向数据库消息表中插入消息，调用接口发送钉钉消息
        sendDelayNotice(needSendNoticeProjects);
    }

    private List<JSONObject> queryProjectPlanTimes(List<Map<String, Object>> queryPlanParams) {
        List<JSONObject> projectCurrentPlanEndTimes = new ArrayList<>();
        List<Map<String, Object>> tempList = new ArrayList<>();
        for (Map<String, Object> oneParam : queryPlanParams) {
            tempList.add(oneParam);
            if (tempList.size() == 20) {
                List<JSONObject> oneResult = xcmgProjectSchedulerDao.queryProjectCurrentPlanEndTime(tempList);
                projectCurrentPlanEndTimes.addAll(oneResult);
                tempList.clear();
            }
        }
        if (!tempList.isEmpty()) {
            List<JSONObject> oneResult = xcmgProjectSchedulerDao.queryProjectCurrentPlanEndTime(tempList);
            projectCurrentPlanEndTimes.addAll(oneResult);
            tempList.clear();
        }

        return projectCurrentPlanEndTimes;
    }

    /**
     * 构造消息体发送消息（RDM中和钉钉中的）
     *
     * @param needSendNoticeProjects
     */
    private void sendDelayNotice(List<JSONObject> needSendNoticeProjects) {
        List<JSONObject> rdmMessageList = new ArrayList<>();
        List<JSONObject> dingdingMessageList = new ArrayList<>();
        List<JSONObject> insertDelayNotices = new ArrayList<>();
        for (JSONObject oneObj : needSendNoticeProjects) {
            // 需要插入的已发送通知的记录
            JSONObject delayNoticeObj = generateDelayNotice(oneObj);
            insertDelayNotices.add(delayNoticeObj);
            // 拼接RDM中的消息
            String delayLevel = oneObj.getString("delayLevel");
            String projectName = oneObj.getString("projectName");
            String planEndTime = oneObj.getString("planEndTime").split(" ", -1)[0];
            switch (delayLevel) {
                case "level0":
                    // 只发给项目负责人
                    JSONObject rdmMessageObjRespLevel0 = new JSONObject();
                    rdmMessageObjRespLevel0.put("title", "科技项目延误提醒");
                    rdmMessageObjRespLevel0.put("typeId", "personal");
                    String contentLevel0 = "您所负责的科技项目《" + projectName + "》即将延误，计划结束时间是" + planEndTime
                        + ",请及时跟进处理。延误超过5天上报项目指导人，延误超过10天上报分管领导！";
                    JSONObject level1UserInfos0 =
                        toGetArrayUserInfos(oneObj.getJSONObject("members").getJSONArray("resp"));
                    if (level1UserInfos0 != null) {
                        rdmMessageObjRespLevel0.put("recUserIds", level1UserInfos0.getString("userIds"));
                        rdmMessageObjRespLevel0.put("recUserCertNos", level1UserInfos0.getString("userCertNos"));
                        rdmMessageObjRespLevel0.put("content", contentLevel0);
                        rdmMessageList.add(rdmMessageObjRespLevel0);
                    }
                    break;
                case "level1":
                    // 只发给项目负责人
                    JSONObject rdmMessageObjRespLevel1 = new JSONObject();
                    rdmMessageObjRespLevel1.put("title", "科技项目延误提醒");
                    rdmMessageObjRespLevel1.put("typeId", "personal");
                    String contentLevel1 = "您所负责的科技项目《" + projectName + "》已经延误，计划结束时间是" + planEndTime
                        + ",请及时跟进处理。延误超过5天上报项目指导人，延误超过10天上报分管领导！";
                    JSONObject level1UserInfos =
                        toGetArrayUserInfos(oneObj.getJSONObject("members").getJSONArray("resp"));
                    if (level1UserInfos != null) {
                        rdmMessageObjRespLevel1.put("recUserIds", level1UserInfos.getString("userIds"));
                        rdmMessageObjRespLevel1.put("recUserCertNos", level1UserInfos.getString("userCertNos"));
                        rdmMessageObjRespLevel1.put("content", contentLevel1);
                        rdmMessageList.add(rdmMessageObjRespLevel1);
                    }
                    break;
                case "level2":
                    // 发给项目负责人
                    JSONObject rdmMessageObjRespLevel2 = new JSONObject();
                    rdmMessageObjRespLevel2.put("title", "科技项目延误提醒");
                    rdmMessageObjRespLevel2.put("typeId", "personal");
                    String contentLevel2Resp =
                        "您所负责的科技项目《" + projectName + "》已经延误超过5天，计划结束时间是" + planEndTime + ",请及时跟进处理。延误超过10天上报分管领导！";
                    JSONObject level2RespInfos =
                        toGetArrayUserInfos(oneObj.getJSONObject("members").getJSONArray("resp"));
                    if (level2RespInfos != null) {
                        rdmMessageObjRespLevel2.put("recUserIds", level2RespInfos.getString("userIds"));
                        rdmMessageObjRespLevel2.put("recUserCertNos", level2RespInfos.getString("userCertNos"));
                        rdmMessageObjRespLevel2.put("content", contentLevel2Resp);
                        rdmMessageList.add(rdmMessageObjRespLevel2);
                    }
                    // 发给项目指导人
                    JSONObject rdmMessageObjGuidLevel2 = new JSONObject();
                    rdmMessageObjGuidLevel2.put("title", "科技项目延误提醒");
                    rdmMessageObjGuidLevel2.put("typeId", "personal");
                    String contentLevel2Guid =
                        "您所指导的科技项目《" + projectName + "》已经延误超过5天，计划结束时间是" + planEndTime + ",请及时跟进处理。延误超过10天上报分管领导！";
                    JSONObject level2GuidInfos =
                        toGetArrayUserInfos(oneObj.getJSONObject("members").getJSONArray("guid"));
                    if (level2GuidInfos != null) {
                        rdmMessageObjGuidLevel2.put("recUserIds", level2GuidInfos.getString("userIds"));
                        rdmMessageObjGuidLevel2.put("recUserCertNos", level2GuidInfos.getString("userCertNos"));
                        rdmMessageObjGuidLevel2.put("content", contentLevel2Guid);
                        rdmMessageList.add(rdmMessageObjGuidLevel2);
                    }
                    break;
                case "level3":
                    // 发给项目负责人
                    JSONObject rdmMessageObjRespLevel3 = new JSONObject();
                    rdmMessageObjRespLevel3.put("title", "科技项目延误提醒");
                    rdmMessageObjRespLevel3.put("typeId", "personal");
                    String contentLevel3Resp =
                        "您所负责的科技项目《" + projectName + "》已经延误超过10天，计划结束时间是" + planEndTime + ",请及时跟进处理。本延误消息已经上报分管领导！";
                    JSONObject level3RespInfos =
                        toGetArrayUserInfos(oneObj.getJSONObject("members").getJSONArray("resp"));
                    if (level3RespInfos != null) {
                        rdmMessageObjRespLevel3.put("recUserIds", level3RespInfos.getString("userIds"));
                        rdmMessageObjRespLevel3.put("recUserCertNos", level3RespInfos.getString("userCertNos"));
                        rdmMessageObjRespLevel3.put("content", contentLevel3Resp);
                        rdmMessageList.add(rdmMessageObjRespLevel3);
                    }
                    // 发给项目指导人
                    JSONObject rdmMessageObjGuidLevel3 = new JSONObject();
                    rdmMessageObjGuidLevel3.put("title", "科技项目延误提醒");
                    rdmMessageObjGuidLevel3.put("typeId", "personal");
                    String contentLevel3Guid =
                        "您所指导的科技项目《" + projectName + "》已经延误超过10天，计划结束时间是" + planEndTime + ",请及时跟进处理。本延误消息已经上报分管领导！";
                    JSONObject level3GuidInfos =
                        toGetArrayUserInfos(oneObj.getJSONObject("members").getJSONArray("guid"));
                    if (level3GuidInfos != null) {
                        rdmMessageObjGuidLevel3.put("recUserIds", level3GuidInfos.getString("userIds"));
                        rdmMessageObjGuidLevel3.put("recUserCertNos", level3GuidInfos.getString("userCertNos"));
                        rdmMessageObjGuidLevel3.put("content", contentLevel3Guid);
                        rdmMessageList.add(rdmMessageObjGuidLevel3);
                    }
                    // 发给分管领导
                    JSONObject rdmMessageObjLeadLevel3 = new JSONObject();
                    rdmMessageObjLeadLevel3.put("title", "科技项目延误提醒");
                    rdmMessageObjLeadLevel3.put("typeId", "personal");
                    String contentLevel3Lead =
                        oneObj.getString("mainDepName") + "，" + level3RespInfos.getString("userNames") + "，负责的科技项目《"
                            + projectName + "》已经延误超过10天，计划结束时间是" + planEndTime + ",请您关注！";
                    JSONObject level3LeadInfos =
                        toGetArrayUserInfos(oneObj.getJSONObject("members").getJSONArray("lead"));
                    if (level3LeadInfos != null) {
                        rdmMessageObjLeadLevel3.put("recUserIds", level3LeadInfos.getString("userIds"));
                        rdmMessageObjLeadLevel3.put("recUserCertNos", level3LeadInfos.getString("userCertNos"));
                        rdmMessageObjLeadLevel3.put("content", contentLevel3Lead);
                        rdmMessageList.add(rdmMessageObjLeadLevel3);
                    }
                    break;
            }
        }
        // 发送RDM消息，并转化为钉钉消息。不再向RDM平台发送消息，2021-02-16
        for (JSONObject oneRdmMessage : rdmMessageList) {
            JSONObject oneDDMessage = new JSONObject();
            oneDDMessage.put("message",
                "《科技项目管理》" + oneRdmMessage.getString("title") + ": " + oneRdmMessage.getString("content"));
            oneDDMessage.put("userNos", oneRdmMessage.getString("recUserCertNos"));
            dingdingMessageList.add(oneDDMessage);
        }

        // 分组批量发送钉钉消息
        String url = WebAppUtil.getProperty("dd_url");
        JSONObject ddObj = new JSONObject();
        ddObj.put("agentId", WebAppUtil.getProperty("dd_agentId"));
        ddObj.put("appKey", WebAppUtil.getProperty("dd_appKey"));
        ddObj.put("appSecret", WebAppUtil.getProperty("dd_appSecret"));
        JSONArray array = new JSONArray();
        ddObj.put("content", array);
        logger.warn("Start send dingding notice");
        for (JSONObject oneDDMessage : dingdingMessageList) {
            array.add(oneDDMessage);
            if (array.size() == 20) {
                SendDDNoticeManager.httpSendNotices(ddObj, url);
                array.clear();
            }
        }
        if (!array.isEmpty()) {
            SendDDNoticeManager.httpSendNotices(ddObj, url);
            array.clear();
        }
        logger.warn("End send dingding notice");
        // 更新项目的发送状态
        List<JSONObject> tempInsertList = new ArrayList<>();
        for (JSONObject oneObject : insertDelayNotices) {
            tempInsertList.add(oneObject);
            if (tempInsertList.size() == 20) {
                xcmgProjectSchedulerDao.batchInsertDelayNotice(tempInsertList);
                tempInsertList.clear();
            }
        }
        if (!tempInsertList.isEmpty()) {
            xcmgProjectSchedulerDao.batchInsertDelayNotice(tempInsertList);
            tempInsertList.clear();
        }
    }

    private JSONObject generateDelayNotice(JSONObject projectObj) {
        String delayLevel = projectObj.getString("delayLevel");
        String stageId = projectObj.getString("currentStageId");
        String projectId = projectObj.getString("projectId");
        JSONObject delayNoticeObj = new JSONObject();
        delayNoticeObj.put("id", IdUtil.getId());
        delayNoticeObj.put("projectId", projectId);
        delayNoticeObj.put("stageId", stageId);
        delayNoticeObj.put("sendLevel", delayLevel);
        delayNoticeObj.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        return delayNoticeObj;
    }

    /**
     * 将一组用户的id，name，certNo拼接
     *
     * @param users
     * @return
     */
    private JSONObject toGetArrayUserInfos(JSONArray users) {
        if (users == null || users.isEmpty()) {
            return null;
        }
        JSONObject result = new JSONObject();
        String userIds = "";
        String userNames = "";
        String userCertNos = "";
        for (int index = 0; index < users.size(); index++) {
            userIds += users.getJSONObject(index).getString("userId") + ",";
            userNames += users.getJSONObject(index).getString("userName") + ",";
            userCertNos += users.getJSONObject(index).getString("userCertNo") + ",";
        }
        result.put("userIds", userIds.substring(0, userIds.length() - 1));
        result.put("userNames", userNames.substring(0, userNames.length() - 1));
        result.put("userCertNos", userCertNos.substring(0, userCertNos.length() - 1));
        return result;
    }

    /**
     * 查询项目的负责人、指导人和分管领导
     *
     * @param needSendNoticeProjects
     */
    private void queryNoticeSenderInfos(List<JSONObject> needSendNoticeProjects) {
        // 查询技术分管领导
        Map<String, Object> params = new HashMap<>();
        params.put("groupName", "分管领导");
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, String>> jszxLeaderInfos = xcmgProjectOtherDao.queryUserByGroupName(params);
        JSONArray jszxLeaderArray = new JSONArray();
        for (Map<String, String> oneLeader : jszxLeaderInfos) {
            JSONObject oneLeaderObj = new JSONObject();
            oneLeaderObj.put("userId", oneLeader.get("USER_ID_"));
            oneLeaderObj.put("userName", oneLeader.get("FULLNAME_"));
            oneLeaderObj.put("userCertNo", oneLeader.get("CERT_NO_"));
            jszxLeaderArray.add(oneLeaderObj);
        }
        // 查询工艺分管领导
        params.put("groupName", RdmConst.GY_FGLD);
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, String>> gyLeaderInfos = xcmgProjectOtherDao.queryUserByGroupName(params);
        JSONArray gyLeaderArray = new JSONArray();
        for (Map<String, String> oneLeader : gyLeaderInfos) {
            JSONObject oneLeaderObj = new JSONObject();
            oneLeaderObj.put("userId", oneLeader.get("USER_ID_"));
            oneLeaderObj.put("userName", oneLeader.get("FULLNAME_"));
            oneLeaderObj.put("userCertNo", oneLeader.get("CERT_NO_"));
            gyLeaderArray.add(oneLeaderObj);
        }

        List<String> projectIds = new ArrayList<>();
        Map<String, JSONObject> projectId2Obj = new HashMap<>();
        for (JSONObject oneProject : needSendNoticeProjects) {
            projectIds.add(oneProject.getString("projectId"));
            projectId2Obj.put(oneProject.getString("projectId"), oneProject);

            JSONObject memObj = new JSONObject();
            memObj.put("resp", new JSONArray());
            memObj.put("guid", new JSONArray());
            memObj.put("lead", new JSONArray());
            oneProject.put("members", memObj);

            // 赋值分管领导
            String mainDepName = oneProject.getString("mainDepName");
            if (mainDepName.contains("工艺")) {
                memObj.getJSONArray("lead").addAll(gyLeaderArray);
            } else {
                memObj.getJSONArray("lead").addAll(jszxLeaderArray);
            }
        }

        // 查询指导人和负责人
        List<JSONObject> projectMems = batchQueryProjectMembers(projectIds);
        for (JSONObject oneMem : projectMems) {
            String projectId = oneMem.getString("projectId");
            String userId = oneMem.getString("userId");
            String roleName = oneMem.getString("roleName");
            String userName = oneMem.getString("userName");
            String userCertNo = oneMem.getString("userCertNo");

            JSONObject projectObj = projectId2Obj.get(projectId);
            JSONObject memObj = projectObj.getJSONObject("members");
            JSONArray array =
                "项目指导人".equalsIgnoreCase(roleName) ? memObj.getJSONArray("guid") : memObj.getJSONArray("resp");
            JSONObject oneUserObj = new JSONObject();
            oneUserObj.put("userId", userId);
            oneUserObj.put("userName", userName);
            oneUserObj.put("userCertNo", userCertNo);
            array.add(oneUserObj);
        }
    }

    private List<JSONObject> batchQueryProjectMembers(List<String> projectIds) {
        Map<String, Object> queryParams = new HashMap<>();
        List<String> roleNames = new ArrayList<>();
        roleNames.add("项目指导人");
        roleNames.add("项目负责人");
        queryParams.put("roleNames", roleNames);
        List<String> tempProjectIdList = new ArrayList<>();
        List<JSONObject> result = new ArrayList<>();
        for (String projectId : projectIds) {
            tempProjectIdList.add(projectId);
            if (tempProjectIdList.size() == 20) {
                queryParams.put("projectIds", tempProjectIdList);
                List<JSONObject> oneResult = xcmgProjectSchedulerDao.queryProjectMemsByRoleName(queryParams);
                result.addAll(oneResult);
                tempProjectIdList.clear();
            }
        }
        if (!tempProjectIdList.isEmpty()) {
            queryParams.put("projectIds", tempProjectIdList);
            List<JSONObject> oneResult = xcmgProjectSchedulerDao.queryProjectMemsByRoleName(queryParams);
            result.addAll(oneResult);
            tempProjectIdList.clear();
        }

        return result;
    }

    /**
     * 过滤出需要发送消息通知的项目
     *
     * @param projectLists
     * @return
     */
    private List<JSONObject> filterNeedSendNoticeProjects(List<JSONObject> projectLists) {
        List<JSONObject> result = new ArrayList<>();
        if (projectLists == null || projectLists.isEmpty()) {
            return result;
        }
        // 查询这些项目在当前阶段是否发送过相应级别的通知消息(整理成projectId-stageId--sendLevel)
        Map<String, Map<String, String>> projectId2StageId2Level = new HashMap<>();
        List<Map<String, Object>> queryNoticeRecordParams = new ArrayList<>();
        for (JSONObject oneProject : projectLists) {
            Map<String, Object> oneParam = new HashMap<>();
            String projectId = oneProject.getString("projectId");
            String currentStageId = oneProject.getString("currentStageId");
            String sendLevel = oneProject.getString("delayLevel");
            oneParam.put("projectId", projectId);
            oneParam.put("stageId", currentStageId);
            oneParam.put("sendLevel", sendLevel);
            queryNoticeRecordParams.add(oneParam);
        }
        List<JSONObject> sendNoticeRecord = queryDelayNoticeRecords(queryNoticeRecordParams);
        for (JSONObject oneRecord : sendNoticeRecord) {
            String projectId = oneRecord.getString("projectId");
            String stageId = oneRecord.getString("stageId");
            String sendLevel = oneRecord.getString("sendLevel");

            if (!projectId2StageId2Level.containsKey(projectId)) {
                projectId2StageId2Level.put(projectId, new HashMap<String, String>());
            }
            Map<String, String> oneProjectSendLevelInfos = projectId2StageId2Level.get(projectId);
            oneProjectSendLevelInfos.put(stageId, sendLevel);
        }
        // 过滤得到真正需要发送消息的项目
        for (JSONObject oneProject : projectLists) {
            String projectId = oneProject.getString("projectId");
            String currentStageId = oneProject.getString("currentStageId");
            String delayLevel = oneProject.getString("delayLevel");
            if (projectId2StageId2Level.containsKey(projectId)
                && projectId2StageId2Level.get(projectId).containsKey(currentStageId)
                && projectId2StageId2Level.get(projectId).get(currentStageId).equalsIgnoreCase(delayLevel)) {
                continue;
            }
            result.add(oneProject);
        }

        return result;
    }

    private List<JSONObject> queryDelayNoticeRecords(List<Map<String, Object>> queryNoticeRecordParams) {
        List<JSONObject> sendNoticeRecord = new ArrayList<>();
        List<Map<String, Object>> tempList = new ArrayList<>();
        for (Map<String, Object> oneParam : queryNoticeRecordParams) {
            tempList.add(oneParam);
            if (tempList.size() == 20) {
                List<JSONObject> oneResult = xcmgProjectSchedulerDao.queryDelayNoticeRecord(tempList);
                sendNoticeRecord.addAll(oneResult);
                tempList.clear();
            }
        }
        if (!tempList.isEmpty()) {
            List<JSONObject> oneResult = xcmgProjectSchedulerDao.queryDelayNoticeRecord(tempList);
            sendNoticeRecord.addAll(oneResult);
            tempList.clear();
        }

        return sendNoticeRecord;
    }

    /**
     * 判断项目是否延误，延误的等级。返回延误的项目列表
     *
     * @param projectLists
     * @return
     */
    private List<JSONObject> judgeProjectDelay(List<JSONObject> projectLists) {
        List<JSONObject> result = new ArrayList<>();
        try {
            long todayStartTime =
                DateFormatUtil.parseDate(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL)).getTime();
            // 即将延误时间
            String projectWillRiskMillSecStr = WebAppUtil.getProperty("projectWillRiskMillSec");
            if (StringUtils.isBlank(projectWillRiskMillSecStr)) {
                projectWillRiskMillSecStr = "432000000";
            }
            long willDelayPardonTime = Long.parseLong(projectWillRiskMillSecStr);

            // 一级延误时间
            String projectRiskPardonMillSecStr = WebAppUtil.getProperty("projectRiskPardonMillSec");
            if (StringUtils.isBlank(projectRiskPardonMillSecStr)) {
                projectRiskPardonMillSecStr = "432000000";
            }
            long delayPardonTime = Long.parseLong(projectRiskPardonMillSecStr);
            // 二级延误时间
            String projectRiskLevel2MillSec = WebAppUtil.getProperty("projectRiskLevel2MillSec");
            if (StringUtils.isBlank(projectRiskLevel2MillSec)) {
                projectRiskLevel2MillSec = "864000000";
            }
            long delayLevel2Time = Long.parseLong(projectRiskLevel2MillSec);

            for (JSONObject oneProject : projectLists) {
                String planEndTimeStr = oneProject.getString("planEndTime");
                if (StringUtils.isBlank(planEndTimeStr)) {
                    continue;
                }
                long planEndTime = DateUtil.parseDate(planEndTimeStr, DateUtil.DATE_FORMAT_FULL).getTime();
                if (planEndTime - todayStartTime >= 0 && planEndTime - todayStartTime <= willDelayPardonTime) {
                    oneProject.put("delayLevel", "level0");
                } else if (todayStartTime - planEndTime > 0 && todayStartTime - planEndTime <= delayPardonTime) {
                    oneProject.put("delayLevel", "level1");
                } else if (todayStartTime - planEndTime > delayPardonTime
                    && todayStartTime - planEndTime <= delayLevel2Time) {
                    oneProject.put("delayLevel", "level2");
                } else if (todayStartTime - planEndTime > delayLevel2Time) {
                    oneProject.put("delayLevel", "level3");
                } else {
                    continue;
                }
                result.add(oneProject);
            }
        } catch (Exception e) {
            logger.error("Exception in judgeProjectDelay");
        }

        return result;
    }

    /**
     * 每天22:30 自动同步PDM项目交付物信息，用于推送到新品试制
     */
    @Scheduled(cron = "0 30 22 * * *")
    public void asyncPdmDelivery() throws Exception {
        String webappName = WebAppUtil.getProperty("webappName", "rdm");
        if (!"rdm".equalsIgnoreCase(webappName)) {
            return;
        }
        String developOrProduce = SysPropertiesUtil.getGlobalProperty("developOrProduce");
        if ("develop".equals(developOrProduce)) {
            return;
        }
        productService.asyncPdmDelivery();
    }
}
