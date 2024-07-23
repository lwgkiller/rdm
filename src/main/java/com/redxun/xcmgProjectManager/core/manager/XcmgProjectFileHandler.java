package com.redxun.xcmgProjectManager.core.manager;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmZhgl.core.dao.ProductConfigDao;
import com.redxun.rdmZhgl.core.dao.ProductDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectFileDao;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * @author zz
 */
@Service
public class XcmgProjectFileHandler
    implements ProcessStartPreHandler, TaskPreHandler, ProcessStartAfterHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(XcmgProjectFileHandler.class);
    @Autowired
    private XcmgProjectFileManager xcmgProjectFileManager;
    @Resource
    private XcmgProjectFileDao xcmgProjectFileDao;
    @Resource
    private ProductConfigDao productConfigDao;
    @Resource
    private ProductDao productDao;
    @Resource
    private XcmgProjectDao xcmgProjectDao;

    /**
     * 整个流程启动之前的处理，草稿也会调用这里
     */
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
     */
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        logger.info("taskPreHandle");
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        addOrUpdateApplyInfo(processNextCmd);
    }

    /**
     * 驳回场景cmd中没有表单数据
     */
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
            xcmgProjectFileManager.add(applyObj);
        } else {
            xcmgProjectFileManager.update(applyObj);
        }
        return applyObj;
    }

    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            String id = bpmInst.getBusKey();
            JSONObject applyObj = xcmgProjectFileDao.getFileApplyObj(id);
            // 先找到项目信息，只有研发类的才允许推送
            String projectId = applyObj.getString("projectId");
            JSONObject xcmgProject = xcmgProjectDao.queryProjectById(projectId);
            if (ConstantUtil.PRODUCT_CATEGORY.equals(xcmgProject.getString("categoryId"))) {
                String fileIds = applyObj.getString("fileIds");
                if (StringUtil.isNotEmpty(fileIds)) {
                    String[] fileArry = fileIds.split(",");
                    JSONObject fileObj;
                    String deliveryId;
                    String productIds;
                    String[] productIdArry;
                    List<JSONObject> stageList;
                    for (String fileId : fileArry) {
                        fileObj = xcmgProjectFileDao.getFileObj(fileId);
                        if (fileObj != null) {
                            deliveryId = fileObj.getString("deliveryId");
                            productIds = fileObj.getString("productIds");
                            if (StringUtil.isNotEmpty(productIds)) {
                                productIdArry = productIds.split(",");
                                // 根据交付物id获取绑定的新品试制节点
                                stageList = productConfigDao.getStageList(deliveryId);
                                if (!stageList.isEmpty()) {
                                    for (String productId : productIdArry) {
                                        JSONObject newProductObj = productDao.getNewProductInfo(productId);
                                        if (newProductObj != null) {
                                            String mainId = newProductObj.getString("id");
                                            JSONObject paramJson = new JSONObject();
                                            paramJson.put("mainId", mainId);
                                            paramJson.put("itemType", "4");
                                            int count = 0;
                                            for (JSONObject temp : stageList) {
                                                // 先判断阶段时间是否已经填写，已填写则不更新
                                                paramJson.put("stageName", temp.getString("itemCode"));
                                                String finishDate = productDao.getStageFinishDate(paramJson);
                                                if (finishDate == null) {
                                                    paramJson.put(temp.getString("itemCode"),
                                                        XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                                                    count++;
                                                }
                                            }
                                            if (count > 0) {
                                                productDao.updateOrgDate(paramJson);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd cmd, BpmInst bpmInst) {
        return null;
    }
}
