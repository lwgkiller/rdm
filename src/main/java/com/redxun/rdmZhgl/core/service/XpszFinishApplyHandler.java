package com.redxun.rdmZhgl.core.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.redxun.core.util.StringUtil;
import com.redxun.rdmZhgl.core.dao.XpszFinishApplyDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.rdmZhgl.core.dao.ProductConfigDao;
import com.redxun.rdmZhgl.core.dao.ProductDao;
import com.redxun.rdmZhgl.core.dao.XpszChangeDao;

/**
 * @author zz
 * */
@Service
public class XpszFinishApplyHandler implements ProcessStartPreHandler, TaskPreHandler, ProcessStartAfterHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(XpszFinishApplyHandler.class);
    @Resource
    private XpszFinishApplyService xpszFinishApplyService;
    @Resource
    private XpszFinishApplyDao xpszFinishApplyDao;
    @Resource
    private ProductDao productDao;
    @Resource
    private ProductService productService;
    @Resource
    ProductConfigDao productConfigDao;
    /**
     * 整个流程启动之前的处理，草稿也会调用这里
     * */
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        logger.info("ProcessApplyHandler processStartPreHandle");
        Map<String, Object> changeApply = addOrUpdateApplyInfo(processStartCmd);
        if (changeApply != null) {
            processStartCmd.setBusinessKey(changeApply.get("id").toString());
        }
    }

    /**
     * 任务审批之后的前置处理器id
     * */
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        logger.info("taskPreHandle");
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        addOrUpdateApplyInfo(processNextCmd);
    }

    /**
     * 驳回场景cmd中没有表单数据
     * */
    private Map<String, Object> addOrUpdateApplyInfo(AbstractExecutionCmd cmd) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return null;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return null;
        }
        Map<String, Object> applyObj = JSONObject.parseObject(formDataJson.toJSONString(), Map.class);
        if (applyObj.get("id") == null || StringUtils.isBlank(applyObj.get("id").toString())) {
            xpszFinishApplyService.add(applyObj);
        } else {
            xpszFinishApplyService.update(applyObj);
        }
        return applyObj;
    }
    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            String id = bpmInst.getBusKey();
            Map<String,Object> changeObj = xpszFinishApplyDao.getObjectById(id);
            String mainId = CommonFuns.nullToString(changeObj.get("mainId"));
            String item = CommonFuns.nullToString(changeObj.get("item"));
            JSONObject param = new JSONObject();
            param.put("itemType","4");
            param.put("mainId",mainId);
            param.put(item,changeObj.get("finishDate"));
            param.put("produceNotice",changeObj.get("produceNotice"));
            productDao.updateOrgDate(param);
            reloadStatus(mainId);
        }
    }
    public void reloadStatus(String mainId){
        JSONObject json = new JSONObject();
        json.put("mainId", mainId);
        json.put("type", "type");
        List<JSONObject> resultArray = productDao.getItemList(json);
        List<JSONObject> itemList = productConfigDao.getItemList(new JSONObject());
        if (resultArray != null && resultArray.size() == 2) {
            JSONObject planJson = resultArray.get(0);
            JSONObject actJson = resultArray.get(1);
            String field = "";
            String stage = "";
            int sort = 0;
            for (String key : actJson.keySet()) {
                if (key.endsWith("_date")) {
                    Object planDate = actJson.get(key);
                    if (planDate != null) {
                        int sortIndex = productService.getSort(key);
                        if (sortIndex > sort) {
                            sort = sortIndex;
                            field = key;
                        }
                    }
                }
            }
            if (StringUtil.isNotEmpty(field)) {
                for (int i = 0; i < itemList.size(); i++) {
                    JSONObject jsonObject1 = itemList.get(i);
                    if (field.equals(jsonObject1.getString("itemCode"))) {
                        int index = jsonObject1.getInteger("sort");
                        if (index == itemList.size()) {
                            stage = jsonObject1.getString("itemCode");
                            break;
                        } else {
                            stage = itemList.get(index).getString("itemCode");
                            break;
                        }
                    }
                }
            } else {
                // 返回第一个节点
                stage = itemList.get(0).getString("itemCode");
            }
            Date planDate = planJson.getDate(stage);
            if (planDate != null) {
                long diff = System.currentTimeMillis() - planDate.getTime();
                long days = diff / (1000 * 60 * 60 * 24);
                String processStatus = "1";
                if (days >= 4 && days <= 8) {
                    processStatus = "2";
                } else if (days > 8) {
                    processStatus = "3";
                }
                Map<String, Object> objBody = new HashMap<>(16);
                objBody.put("id", mainId);
                objBody.put("processStatus", processStatus);
                objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                productDao.updateObject(objBody);
            }
        }
    }
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd cmd, BpmInst bpmInst) {
        return null;
    }
}
