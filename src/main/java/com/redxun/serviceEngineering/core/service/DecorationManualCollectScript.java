package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.BpmTask;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmTaskManager;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.serviceEngineering.core.dao.DecorationManualCollectDao;
import com.redxun.serviceEngineering.core.dao.DecorationManualFileDao;
import com.redxun.sys.core.manager.SysSeqIdManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DecorationManualCollectScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(DecorationManualCollectScript.class);
    @Autowired
    private DecorationManualCollectDao decorationManualCollectDao;
    @Autowired
    private DecorationManualCollectService decorationManualCollectService;
    @Autowired
    private DecorationManualFileDao decorationManualFileDao;
    @Autowired
    private DecorationManualDemandService decorationManualDemandService;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private BpmTaskManager bpmTaskManager;
    @Autowired
    private SysSeqIdManager sysSeqIdManager;

    //..
    public Collection<TaskExecutor> getCollector() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String userId = formDataJson.getString("collectorId");
        String userName = formDataJson.getString("collector");
        users.add(new TaskExecutor(userId, userName));
        return users;
    }

    //..
    public Collection<TaskExecutor> getCollector2() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String userId = formDataJson.getString("collectorId2");
        String userName = formDataJson.getString("collector2");
        users.add(new TaskExecutor(userId, userName));
        return users;
    }

    //..
    public void taskCreateScript(Map<String, Object> vars) {
        try {
            JSONObject jsonObject = decorationManualCollectService.getDetail(vars.get("busKey").toString());
            String activitiId = vars.get("activityId").toString();
            jsonObject.put("businessStatus", activitiId);
            decorationManualCollectService.updateBusiness(jsonObject);
        } catch (Exception e) {
            logger.error("Exception in taskCreateScript", e);
        }
    }

    //..
    public void taskEndScript(Map<String, Object> vars) {
        try {
            JSONObject jsonObject = decorationManualCollectService.getDetail(vars.get("busKey").toString());
            String activitiId = vars.get("activityId").toString();
            if (activitiId.equalsIgnoreCase("A")) {
                jsonObject.put("applyUserId", ContextUtil.getCurrentUserId());
                jsonObject.put("applyUser", ContextUtil.getCurrentUser().getFullname());
                jsonObject.put("applyDepId", ContextUtil.getCurrentUser().getMainGroupId());
                jsonObject.put("applyDep", ContextUtil.getCurrentUser().getMainGroupName());
                if (StringUtil.isEmpty(jsonObject.getString("busunessNo"))) {
                    jsonObject.put("busunessNo", sysSeqIdManager.genSequenceNo("decorationManualCollect", ContextUtil.getCurrentTenantId()));
                }
                jsonObject.put("applyTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
                decorationManualCollectService.updateBusiness(jsonObject);
            } else if (activitiId.equalsIgnoreCase("B")) {
                jsonObject.put("businessStatus", "Z");
                decorationManualCollectService.updateBusiness(jsonObject);
                //todo:更新需求某个明细的状态,->“待确认”，太无厘头了，直接写，不拆方法了
                JSONObject demandItemDetail = decorationManualDemandService.getItemDetail(jsonObject.getString("demandItemId"));
                if (demandItemDetail != null) {
                    demandItemDetail.put("businessStatus", DecorationManualDemandService.ITEM_STATUS_EXE);
                    decorationManualDemandService.updateItem(demandItemDetail);
                }
            } else if (activitiId.equalsIgnoreCase("D")) {
                //不收集力矩，直接结束
                if (!jsonObject.getString("collectType").equalsIgnoreCase(DecorationManualCollectService.COLLECTTYPE_LJGJ)) {
                    jsonObject.put("businessStatus", "Z");
                    decorationManualCollectService.updateBusiness(jsonObject);
                    //todo:更新需求某个明细的状态,->只剩一个就“待确认”，太无厘头了，直接写，不拆方法了
                    JSONObject demandItemDetail = decorationManualDemandService.getItemDetail(jsonObject.getString("demandItemId"));
                    if (demandItemDetail != null && demandItemDetail.containsKey("businessStatus") &&
                            demandItemDetail.getString("businessStatus").split(",").length == 1) {
                        demandItemDetail.put("businessStatus", DecorationManualDemandService.ITEM_STATUS_EXE);
                        decorationManualDemandService.updateItem(demandItemDetail);
                    } else if (demandItemDetail != null && demandItemDetail.containsKey("businessStatus") &&
                            demandItemDetail.getString("businessStatus").split(",").length > 1) {
                        String collectIC = jsonObject.getString("instructions") + "-" + jsonObject.getString("collectType");
                        String[] statuses = demandItemDetail.getString("businessStatus").split(",");
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String status : statuses) {
                            if (!status.equalsIgnoreCase(collectIC)) {
                                stringBuilder.append(status).append(",");
                            }
                        }
                        demandItemDetail.put("businessStatus", stringBuilder.substring(0, stringBuilder.length() - 1));
                        decorationManualDemandService.updateItem(demandItemDetail);
                    }
                }
            } else if (activitiId.equalsIgnoreCase("F")) {
                jsonObject.put("businessStatus", "Z");
                decorationManualCollectService.updateBusiness(jsonObject);
                //todo:更新需求某个明细的状态,->只剩一个就“待确认”，太无厘头了，直接写，不拆方法了
                JSONObject demandItemDetail = decorationManualDemandService.getItemDetail(jsonObject.getString("demandItemId"));
                if (demandItemDetail != null && demandItemDetail.containsKey("businessStatus") &&
                        demandItemDetail.getString("businessStatus").split(",").length == 1) {
                    demandItemDetail.put("businessStatus", DecorationManualDemandService.ITEM_STATUS_EXE);
                    decorationManualDemandService.updateItem(demandItemDetail);
                } else if (demandItemDetail != null && demandItemDetail.containsKey("businessStatus") &&
                        demandItemDetail.getString("businessStatus").split(",").length > 1) {
                    String collectIC = jsonObject.getString("instructions") + "-" + jsonObject.getString("collectType");
                    String[] statuses = demandItemDetail.getString("businessStatus").split(",");
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String status : statuses) {
                        if (!status.equalsIgnoreCase(collectIC)) {
                            stringBuilder.append(status).append(",");
                        }
                    }
                    demandItemDetail.put("businessStatus", stringBuilder.substring(0, stringBuilder.length() - 1));
                    decorationManualDemandService.updateItem(demandItemDetail);
                }
            }

        } catch (Exception e) {
            logger.error("Exception in taskEndScript", e);
        }
    }

    //..是否翻译
    public boolean isTranslation(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        if (formDataJson.getString("instructions").equalsIgnoreCase(DecorationManualCollectService.INSTRUCTIONS_TRANSLATION)) {
            return true;
        } else {
            return false;
        }
    }

    //..是否不是翻译
    public boolean isNotTranslation(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        if (!formDataJson.getString("instructions").equalsIgnoreCase(DecorationManualCollectService.INSTRUCTIONS_TRANSLATION)) {
            return true;
        } else {
            return false;
        }
    }

    //..是否力矩
    public boolean isMoment(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        if (formDataJson.getString("collectType").equalsIgnoreCase(DecorationManualCollectService.COLLECTTYPE_LJGJ)) {
            return true;
        } else {
            return false;
        }
    }

    //..是否不是力矩
    public boolean isNotMoment(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        if (!formDataJson.getString("collectType").equalsIgnoreCase(DecorationManualCollectService.COLLECTTYPE_LJGJ)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @lwgkiller:阿诺多罗。此处控制执行中的办理页面 1.要想打开执行中办理页面，并点击办理，本脚本必须通过，
     * 条件是必须存在6属性匹配的manualfile
     */
    public boolean isManualFileOk(Map<String, Object> vars, BpmTask task) {
        //@lwgkiller:阿诺多罗 本来想的下面这个cmd可以取代getDetail，而且传来的是表单的最新数据，但是这个执行判断脚本就是获取不到，坑
        //IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        //String formData = cmd.getJsonData();
        //JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONObject collect = decorationManualCollectService.getDetail(vars.get("busKey").toString());
        if (collect != null) {
            String businessStatus = collect.getString("businessStatus");
            String instructions = collect.getString("instructions");
            String collectType = collect.getString("collectType");
            if (businessStatus.equalsIgnoreCase("B") ||
                    businessStatus.equalsIgnoreCase("F") ||
                    (businessStatus.equalsIgnoreCase("D") &&
                            !collectType.equalsIgnoreCase(DecorationManualCollectService.COLLECTTYPE_LJGJ))) {
                JSONObject params = new JSONObject();
                params.put("salesModel", collect.getString("salesModel"));
                params.put("designModel", collect.getString("designModel"));
                params.put("materialCode", collect.getString("materialCode"));
                params.put("manualLanguage", collect.getString("manualLanguage"));
                params.put("manualType", "装修手册");
                params.put("manualStatus", DecorationManualFileService.MANUAL_STATUS_READY);
                List<JSONObject> manualFileList = decorationManualFileDao.dataListQuery(params);
                if (manualFileList != null && manualFileList.size() > 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
