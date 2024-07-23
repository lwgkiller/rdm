package com.redxun.serviceEngineering.core.job;

import java.util.*;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.dao.CcbgDao;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.*;
import com.redxun.serviceEngineering.core.service.SeGeneralKanBanNewService;
import com.redxun.serviceEngineering.core.service.SeGeneralKanbanCompletenessService;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.org.manager.OsUserManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Service
@EnableScheduling
public class ServiceEngineeringScheduler {
    private static Logger logger = LoggerFactory.getLogger(ServiceEngineeringScheduler.class);

    @Resource
    private SeGeneralKanBanNewService seGeneralKanBanNewService;
    @Resource
    private SeGeneralKanbanCompletenessService seGeneralKanbanCompletenessService;
    @Resource
    private SeGeneralKanbanCompletenessDao seGeneralKanbanCompletenessDao;
    @Resource
    private PartsAtlasDao partsAtlasDao;
    @Resource
    private SysDicManager sysDicManager;
    @Resource
    private OsUserManager osUserManager;
    @Resource
    private SendDDNoticeManager sendDDNoticeManager;
    @Resource
    private MaintenanceManualfileDao maintenanceManualfileDao;
    @Resource
    private DecorationManualFileDao decorationManualFileDao;
    @Resource
    private StandardvalueShipmentnotmadeDao standardvalueShipmentnotmadeDao;
    @Resource
    private MaintenanceManualDao maintenanceManualDao;
    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private CcbgDao ccbgDao;
    @Resource
    private RdmZhglUtil rdmZhglUtil;

    /**
     * 每天23点执行，服务工程看板数据接口缓存刷新
     */
    @Scheduled(cron = "0 0 23 * * *")
    public void seGeneralKanbanNewCache() {
        try {
            logger.info("start seGeneralKanbanNewCache");
            // ..刷新缓存-总体情况
            seGeneralKanBanNewService.refreshCacheGenSitu();
            // ..刷新缓存-发运产品随机文件完备性
            seGeneralKanBanNewService.refreshCacheAtDocAcpRate(String.valueOf(DateUtil.getYear(new Date())));
            // ..刷新缓存-补发信息
            seGeneralKanBanNewService.refreshCacheAtDocReDisRate();
        } catch (Exception e) {
            logger.error("SeGeneralKanbanNewCacheJob任务执行失败", e);
        }
    }

    /**
     * 每天0:10执行，售后资料文件完备性计算
     */
    @Scheduled(cron = "0 10 0 * * *")
    public void seGeneralKanbanCompletenessJob() {
        try {
            logger.info("start seGeneralKanbanCompletenessJob");
            HashMap<String, Object> params = new HashMap<>();
            List<JSONObject> partsAtlasList = partsAtlasDao.dataListQuery(params);
            HashMap<String, JSONObject> partsAtlasMap = new HashMap<>();
            // 用map稀释一下，只保留年份+物料唯一的记录，减轻含数据库操作的遍历执行的时间复杂度
            for (JSONObject jsonObject : partsAtlasList) {
                partsAtlasMap.put(
                    jsonObject.getString("storageTime").substring(0, 4) + "-" + jsonObject.getString("materialCode"),
                    jsonObject);
            }
            // 遍历map，如果看板里有，则更新；如果没有，则新增
            Iterator<String> iterator = partsAtlasMap.keySet().iterator();
            while (iterator.hasNext()) {
                String s = iterator.next();
                params.clear();
                params.put("signYear", s.split("-")[0]);
                params.put("materialCode", s.split("-")[1]);
                if (params.get("materialCode").toString().length() == 9) {// 屏蔽掉异常信息
                    // 找同物料同年的记录,一般情况下有也只有1条
                    List<JSONObject> seGeneralKanbanCompletenessList =
                        seGeneralKanbanCompletenessDao.dataListQuery(params);
                    if (seGeneralKanbanCompletenessList.size() > 0) {// 找到了,一般情况下有也只有1条
                        JSONObject businessObj = seGeneralKanbanCompletenessList.get(0);
                        // 在零件图册库，以同年，同物料为条件更新output,partsAtlas,maintenancePartsList,wearingPartsList的值
                        this.processByPartsAtlasDao(businessObj, params);
                        // 在操保手册库找同物料，manualStatus='可打印'的记录，根据这些记录里isCE值更新regularEdition，CEEdition的值
                        params.put("manualStatus", "可打印");
                        this.processByMaintenanceManualDao(businessObj, params);
                        // 在装修手册库找同物料，manualStatus='可转出'的记录，有记录则更新decorationManual的值
                        params.put("manualStatus", "可转出");
                        this.processByDecorationManualDao(businessObj, params);
                        // 在检修标准值库找同物料，betaCompletion='zzwc'的记录，根据这些记录里versionType更新maintenanceStandardValueTable的值
                        params.put("betaCompletion", "zzwc");
                        this.processByStandardvalueDao(businessObj, params);
                        // 自动修正完备性标记
                        seGeneralKanbanCompletenessService.completenessEvaluationCorrect(businessObj);
                        businessObj.put("UPDATE_BY_", "job");
                        businessObj.put("UPDATE_TIME_", new Date());
                        seGeneralKanbanCompletenessDao.updateData(businessObj);
                    } else {
                        JSONObject businessObj = new JSONObject();
                        businessObj.put("id", IdUtil.getId());
                        // 新增情况下设计型号按物料编码提取零件图册表中的物料描述
                        businessObj.put("designModel", partsAtlasDao.getMaterialDescription(params).split(" ")[0]);
                        businessObj.put("signYear", params.get("signYear").toString());
                        businessObj.put("materialCode", params.get("materialCode").toString());
                        // 在零件图册库，以同年，同物料为条件更新output,partsAtlas,maintenancePartsList,wearingPartsList的值
                        this.processByPartsAtlasDao(businessObj, params);
                        // 在操保手册库找同物料，manualStatus='可打印'的记录，根据这些记录里isCE值更新regularEdition，CEEdition的值
                        params.put("manualStatus", "可打印");
                        this.processByMaintenanceManualDao(businessObj, params);
                        // 在装修手册库找同物料，manualStatus='可转出'的记录，有记录则更新decorationManual的值
                        params.put("manualStatus", "可转出");
                        this.processByDecorationManualDao(businessObj, params);
                        // 在检修标准值库找同物料，betaCompletion='zzwc'的记录，根据这些记录里versionType更新maintenanceStandardValueTable的值
                        params.put("betaCompletion", "zzwc");
                        this.processByStandardvalueDao(businessObj, params);
                        params.remove("signYear");
                        // 找到已存在的任一条同类物料记录，按照任一条更新自己的其他非自动状态信息
                        // partsAtlas,maintenancePartsList,decorationManual,wearingPartsList,regularEdition，CEEdition,maintenanceStandardValueTable除外
                        this.processByThisDao(businessObj, params);
                        // 自动修正完备性标记
                        seGeneralKanbanCompletenessService.completenessEvaluationCorrect(businessObj);
                        businessObj.put("CREATE_BY_", "job");
                        businessObj.put("CREATE_TIME_", new Date());
                        seGeneralKanbanCompletenessDao.insertData(businessObj);
                        // 发送通知到相关人员
                        this.processSendDingDing(businessObj);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("seGeneralKanbanCompletenessJob任务执行失败", e);
        }
    }

    // ..发送通知到相关人员
    private void processSendDingDing(JSONObject businessObj) {
        String receiverNoString =
            sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringGroups", "completenessKaban").getValue();
        String[] receiverNos = receiverNoString.split(",");
        StringBuilder stringBuilder = new StringBuilder();
        if (receiverNos.length > 0) {
            for (String userNo : receiverNos) {
                stringBuilder.append(osUserManager.getByUserName(userNo).getUserId());
                stringBuilder.append(",");
            }
        }
        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        sendDingDing(businessObj, stringBuilder.toString());
    }

    // ..p
    private void sendDingDing(JSONObject jsonObject, String userIds) {
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("完备性看板有新机型加入:销售型号[");
        stringBuilder.append(jsonObject.getString("salesModel"));
        stringBuilder.append("],物料编码[");
        stringBuilder.append(jsonObject.getString("materialCode"));
        stringBuilder.append("],设计型号[");
        stringBuilder.append(jsonObject.getString("designModel"));
        stringBuilder.append("],产量[");
        stringBuilder.append(jsonObject.getString("output")).append("]");
        stringBuilder.append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
        noticeObj.put("content", stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon(userIds, noticeObj);
    }

    // ..在零件图册库，以同年，同物料为条件更新output,partsAtlas,maintenancePartsList,wearingPartsList的值
    private void processByPartsAtlasDao(JSONObject businessObj, HashMap<String, Object> params) {
        businessObj.put("output", partsAtlasDao.getStorageCountByMaterialCodeAndStorageTime(params));
        String modelStatus = partsAtlasDao.getModelStatusByMaterialCodeAndStorageTime(params);
        if (StringUtil.isNotEmpty(modelStatus) && modelStatus.equalsIgnoreCase("已发布")) {
            businessObj.put("partsAtlas", "√");
            businessObj.put("maintenancePartsList", "√");
            businessObj.put("wearingPartsList", "√");
        }
    }

    // ..在操保手册库找同物料，manualStatus='可打印'的记录，根据这些记录里isCE值更新regularEdition，CEEdition的值
    private void processByMaintenanceManualDao(JSONObject businessObj, HashMap<String, Object> params) {
        List<JSONObject> jsonObjects = maintenanceManualfileDao.dataListQuery(params);
        boolean isCE = false, isNotCE = false;
        for (JSONObject jsonObject : jsonObjects) {
            if (jsonObject.getString("isCE").equalsIgnoreCase("是")) {
                isCE = true;
            } else if (jsonObject.getString("isCE").equalsIgnoreCase("否")) {
                isNotCE = true;
            }
        }
        if (isCE) {
            businessObj.put("CEEdition", "√");
        }
        if (isNotCE) {
            businessObj.put("regularEdition", "√");
        }
    }

    // ..在装修手册库找同物料，manualStatus='可转出'的记录，有记录则更新decorationManual的值
    private void processByDecorationManualDao(JSONObject businessObj, HashMap<String, Object> params) {
        List<JSONObject> jsonObjects = decorationManualFileDao.dataListQuery(params);
        if (jsonObjects.size() > 0) {
            businessObj.put("decorationManual", "√");
        }
    }

    // ..在检修标准值库找同物料，betaCompletion='zzwc'的记录，根据这些记录里versionType更新maintenanceStandardValueTable的值
    private void processByStandardvalueDao(JSONObject businessObj, HashMap<String, Object> params) {
        List<JSONObject> jsonObjects = standardvalueShipmentnotmadeDao.dataListQuery(params);
        boolean csb = false, cgb = false, wzb = false;
        for (JSONObject jsonObject : jsonObjects) {
            if (jsonObject.getString("versionType").equalsIgnoreCase("csb")) {
                csb = true;
            } else if (jsonObject.getString("versionType").equalsIgnoreCase("cgb")) {
                cgb = true;
            } else if (jsonObject.getString("versionType").equalsIgnoreCase("wzb")) {
                wzb = true;
            }
        }
        if (csb) {
            businessObj.put("maintenanceStandardValueTable", "测试版");
        }
        if (cgb) {
            businessObj.put("maintenanceStandardValueTable", "常规版");
        }
        if (wzb) {
            businessObj.put("maintenanceStandardValueTable", "完整版");
        }
    }

    // 找到已存在的任一条同类物料记录，按照任一条更新自己的其他非自动状态信息
    // partsAtlas,maintenancePartsList,decorationManual,wearingPartsList,regularEdition，CEEdition,maintenanceStandardValueTable除外
    private void processByThisDao(JSONObject businessObj, HashMap<String, Object> params) {
        List<JSONObject> already = seGeneralKanbanCompletenessDao.dataListQuery(params);
        if (already.size() > 0) {
            businessObj.put("completenessEvaluation", already.get(0).getString("completenessEvaluation"));
            // businessObj.put("partsAtlas", already.get(0).getString("partsAtlas"));
            // businessObj.put("maintenancePartsList", already.get(0).getString("maintenancePartsList"));
            // businessObj.put("wearingPartsList", already.get(0).getString("wearingPartsList"));
            // businessObj.put("regularEdition", already.get(0).getString("regularEdition"));
            // businessObj.put("CEEdition", already.get(0).getString("CEEdition"));
            businessObj.put("packingList", already.get(0).getString("packingList"));
            // businessObj.put("decorationManual", already.get(0).getString("decorationManual"));
            businessObj.put("disassAndAssManual", already.get(0).getString("disassAndAssManual"));
            businessObj.put("structurefunctionAndPrincipleManual",
                already.get(0).getString("structurefunctionAndPrincipleManual"));
            businessObj.put("testAndAdjustmentManual", already.get(0).getString("testAndAdjustmentManual"));
            businessObj.put("troubleshootingManual", already.get(0).getString("troubleshootingManual"));
            businessObj.put("torqueAndToolStandardValueTable",
                already.get(0).getString("torqueAndToolStandardValueTable"));
            // businessObj.put("maintenanceStandardValueTable",
            // already.get(0).getString("maintenanceStandardValueTable"));
            businessObj.put("engineManual", already.get(0).getString("engineManual"));
            businessObj.put("lifeCycleCostList", already.get(0).getString("lifeCycleCostList"));
            businessObj.put("airconditioningUseAndMaintenanceManual",
                already.get(0).getString("airconditioningUseAndMaintenanceManual"));
        }
    }

    /**
     * 每天12:30执行，操保手册异常提醒
     */
    /*@Scheduled(cron = "0 30 12 * * *")
    public void maintenanceManualException() {
        try {
            logger.info("start maintenanceManualException");
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
            logger.error("maintenanceManualException任务执行失败");
        }
    }*/

    private void sendDDCpzgSz(JSONObject businessJsonObject) {
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("【操保手册异常通知】：");
        stringBuilder.append("操保手册-设计型号：").append(businessJsonObject.getString("designModel")).append("，销售型号：")
            .append(businessJsonObject.getString("designModel")).append("，进度有异常，请抓紧处理");
        noticeObj.put("content", stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon(businessJsonObject.getString("productSupervisorId"), noticeObj);
        // 部门负责人
        Map<String, Object> params = new HashMap<>();
        params.put("GROUP_ID_", businessJsonObject.getString("departmentId"));
        List<Map<String, String>> deptResps = xcmgProjectOtherDao.getDepRespManById(params);
        for (Map<String, String> oneDeptResp : deptResps) {
            sendDDNoticeManager.sendNoticeForCommon(oneDeptResp.get("USER_ID_"), noticeObj);
        }
    }

    private void sendDDFgzr(JSONObject businessJsonObject) {
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("【操保手册异常通知】：");
        stringBuilder.append("操保手册-设计型号：").append(businessJsonObject.getString("designModel")).append("，销售型号：")
            .append(businessJsonObject.getString("designModel")).append("，产品主管：")
            .append(businessJsonObject.getString("productSupervisor")).append("，进度延期超过15天，已通知分管主任，请知悉");
        noticeObj.put("content", stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon(businessJsonObject.getString("productSupervisorId"), noticeObj);
        // 部门负责人
        Map<String, Object> params = new HashMap<>();
        params.put("GROUP_ID_", businessJsonObject.getString("departmentId"));
        List<Map<String, String>> deptResps = xcmgProjectOtherDao.getDepRespManById(params);
        for (Map<String, String> oneDeptResp : deptResps) {
            sendDDNoticeManager.sendNoticeForCommon(oneDeptResp.get("USER_ID_"), noticeObj);
        }
        // 分管主任
        List<JSONObject> deptFgzrs = ccbgDao.queryFgzrByUserId(businessJsonObject.getString("productSupervisorId"));
        for (JSONObject jsonObject : deptFgzrs) {
            sendDDNoticeManager.sendNoticeForCommon(jsonObject.getString("USER_ID_"), noticeObj);
        }
    }

    private void sendDDFgld(JSONObject businessJsonObject) {
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("【操保手册异常通知】：");
        stringBuilder.append("操保手册-设计型号：").append(businessJsonObject.getString("designModel")).append("，销售型号：")
            .append(businessJsonObject.getString("designModel")).append("，产品主管：")
            .append(businessJsonObject.getString("productSupervisor")).append("，进度延期超过30天，已通知分管领导，请知悉");
        noticeObj.put("content", stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon(businessJsonObject.getString("productSupervisorId"), noticeObj);
        // 部门负责人
        Map<String, Object> params = new HashMap<>();
        params.put("GROUP_ID_", businessJsonObject.getString("departmentId"));
        List<Map<String, String>> deptResps = xcmgProjectOtherDao.getDepRespManById(params);
        for (Map<String, String> oneDeptResp : deptResps) {
            sendDDNoticeManager.sendNoticeForCommon(oneDeptResp.get("USER_ID_"), noticeObj);
        }
        // 分管主任
        List<JSONObject> deptFgzrs = ccbgDao.queryFgzrByUserId(businessJsonObject.getString("productSupervisorId"));
        for (JSONObject jsonObject : deptFgzrs) {
            sendDDNoticeManager.sendNoticeForCommon(jsonObject.getString("USER_ID_"), noticeObj);
        }
        // 分管领导
        List<Map<String, String>> leaderInfos = rdmZhglUtil.queryFgld();
        for (Map<String, String> oneLeader : leaderInfos) {
            sendDDNoticeManager.sendNoticeForCommon(oneLeader.get("USER_ID_"), noticeObj);
        }
    }

}
