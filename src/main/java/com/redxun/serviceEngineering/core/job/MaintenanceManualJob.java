package com.redxun.serviceEngineering.core.job;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.scheduler.BaseJob;
import com.redxun.core.util.AppBeanUtil;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.dao.CcbgDao;
import com.redxun.rdmZhgl.core.dao.CxyProjectDao;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualDao;
import com.redxun.serviceEngineering.core.dao.PartsAtlasDao;
import com.redxun.serviceEngineering.core.service.MaintenanceManualService;
import com.redxun.xcmgJsjl.core.manager.XcmgJsjlManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MaintenanceManualJob extends BaseJob {
    private static Logger logger = LoggerFactory.getLogger(MaintenanceManualJob.class);
    private SendDDNoticeManager sendDDNoticeManager;
    private MaintenanceManualDao maintenanceManualDao;
    private CcbgDao ccbgDao;
    private RdmZhglUtil rdmZhglUtil;
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    private PartsAtlasDao partsAtlasDao;

    @Override
    public void executeJob(JobExecutionContext context) {
        try {
            maintenanceManualDao = AppBeanUtil.getBean(MaintenanceManualDao.class);
            partsAtlasDao = AppBeanUtil.getBean(PartsAtlasDao.class);
            HashMap<String, Object> params = new HashMap<>();
            List<JSONObject> jsonObjectList = maintenanceManualDao.dataListQuery(params);
            if (jsonObjectList.size() > 0) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                for (JSONObject jsonObject : jsonObjectList) {
                    //入库/发运数量按物料编码提取零件图册表中的PIN码数量
                    jsonObject.put("storageQuantity", partsAtlasDao.
                            getStorageCountByMaterialCode(jsonObject.get("materialCode").toString()));
                    jsonObject.put("shipmentQuantity", partsAtlasDao.
                            getShipmentCountByMaterialCode(jsonObject.get("materialCode").toString()));
                    maintenanceManualDao.updateData(jsonObject);
                    //判断和更新状态
                    if (jsonObject.getString("manualCode").equalsIgnoreCase("")) {
                        if ((jsonObject.getIntValue("storageQuantity") > 10 ||
                                jsonObject.getIntValue("shipmentQuantity") > 0) &&
                                simpleDateFormat.parse(jsonObject.getString("estimatedPrintTime")).
                                        before(simpleDateFormat.parse(simpleDateFormat.format(new Date())))) {
                            long endTime = simpleDateFormat.parse(simpleDateFormat.format(new Date())).getTime();
                            long startTime = simpleDateFormat.parse(jsonObject.getString("estimatedPrintTime")).getTime();
                            int i = (int) ((endTime - startTime) / (1000 * 60 * 60 * 24));
                            if (i <= 15) {
                                jsonObject.put("el", "蓝");
                                maintenanceManualDao.updateData(jsonObject);
                                sendDDCpzgSz(jsonObject);
                            } else if (i > 15 && i <= 30) {
                                jsonObject.put("el", "橙");
                                maintenanceManualDao.updateData(jsonObject);
                                sendDDFgzr(jsonObject);
                            } else if (i > 30) {
                                jsonObject.put("el", "红");
                                maintenanceManualDao.updateData(jsonObject);
                                sendDDFgld(jsonObject);
                            }
                        }
                    } else if (jsonObject.getString("isPrint").equalsIgnoreCase("否") &&
                            jsonObject.getString("manualStatus").equalsIgnoreCase("已发放")) {
                        jsonObject.put("el", "蓝");
                        maintenanceManualDao.updateData(jsonObject);
                        sendDDCpzgSz(jsonObject);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("MaintenanceManualJob任务执行失败");
        }
    }

    private void sendDDCpzgSz(JSONObject businessJsonObject) {
        sendDDNoticeManager = AppBeanUtil.getBean(SendDDNoticeManager.class);
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("【操保手册异常通知】：");
        stringBuilder.append("操保手册-设计型号：").
                append(businessJsonObject.getString("designModel")).
                append("，销售型号：").
                append(businessJsonObject.getString("designModel")).
                append("，进度有异常，请抓紧处理");
        noticeObj.put("content", stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon(businessJsonObject.getString("productSupervisorId"), noticeObj);
        //部门负责人
        xcmgProjectOtherDao = AppBeanUtil.getBean(XcmgProjectOtherDao.class);
        Map<String, Object> params = new HashMap<>();
        params.put("GROUP_ID_", businessJsonObject.getString("departmentId"));
        List<Map<String, String>> deptResps = xcmgProjectOtherDao.getDepRespManById(params);
        for (Map<String, String> oneDeptResp : deptResps) {
            sendDDNoticeManager.sendNoticeForCommon(oneDeptResp.get("USER_ID_"), noticeObj);
        }
    }

    private void sendDDFgzr(JSONObject businessJsonObject) {
        sendDDNoticeManager = AppBeanUtil.getBean(SendDDNoticeManager.class);
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("【操保手册异常通知】：");
        stringBuilder.append("操保手册-设计型号：").
                append(businessJsonObject.getString("designModel")).
                append("，销售型号：").
                append(businessJsonObject.getString("designModel")).
                append("，产品主管：").
                append(businessJsonObject.getString("productSupervisor")).
                append("，进度延期超过15天，已通知分管主任，请知悉");
        noticeObj.put("content", stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon(businessJsonObject.getString("productSupervisorId"), noticeObj);
        //部门负责人
        xcmgProjectOtherDao = AppBeanUtil.getBean(XcmgProjectOtherDao.class);
        Map<String, Object> params = new HashMap<>();
        params.put("GROUP_ID_", businessJsonObject.getString("departmentId"));
        List<Map<String, String>> deptResps = xcmgProjectOtherDao.getDepRespManById(params);
        for (Map<String, String> oneDeptResp : deptResps) {
            sendDDNoticeManager.sendNoticeForCommon(oneDeptResp.get("USER_ID_"), noticeObj);
        }
        //分管主任
        ccbgDao = AppBeanUtil.getBean(CcbgDao.class);
        List<JSONObject> deptFgzrs = ccbgDao.queryFgzrByUserId(businessJsonObject.getString("productSupervisorId"));
        for (JSONObject jsonObject : deptFgzrs) {
            sendDDNoticeManager.sendNoticeForCommon(jsonObject.getString("USER_ID_"), noticeObj);
        }
    }

    private void sendDDFgld(JSONObject businessJsonObject) {
        sendDDNoticeManager = AppBeanUtil.getBean(SendDDNoticeManager.class);
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("【操保手册异常通知】：");
        stringBuilder.append("操保手册-设计型号：").
                append(businessJsonObject.getString("designModel")).
                append("，销售型号：").
                append(businessJsonObject.getString("designModel")).
                append("，产品主管：").
                append(businessJsonObject.getString("productSupervisor")).
                append("，进度延期超过30天，已通知分管领导，请知悉");
        noticeObj.put("content", stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon(businessJsonObject.getString("productSupervisorId"), noticeObj);
        //部门负责人
        xcmgProjectOtherDao = AppBeanUtil.getBean(XcmgProjectOtherDao.class);
        Map<String, Object> params = new HashMap<>();
        params.put("GROUP_ID_", businessJsonObject.getString("departmentId"));
        List<Map<String, String>> deptResps = xcmgProjectOtherDao.getDepRespManById(params);
        for (Map<String, String> oneDeptResp : deptResps) {
            sendDDNoticeManager.sendNoticeForCommon(oneDeptResp.get("USER_ID_"), noticeObj);
        }
        //分管主任
        ccbgDao = AppBeanUtil.getBean(CcbgDao.class);
        List<JSONObject> deptFgzrs = ccbgDao.queryFgzrByUserId(businessJsonObject.getString("productSupervisorId"));
        for (JSONObject jsonObject : deptFgzrs) {
            sendDDNoticeManager.sendNoticeForCommon(jsonObject.getString("USER_ID_"), noticeObj);
        }
        //分管领导
        rdmZhglUtil = AppBeanUtil.getBean(RdmZhglUtil.class);
        List<Map<String, String>> leaderInfos = rdmZhglUtil.queryFgld();
        for (Map<String, String> oneLeader : leaderInfos) {
            sendDDNoticeManager.sendNoticeForCommon(oneLeader.get("USER_ID_"), noticeObj);
        }
    }
}
