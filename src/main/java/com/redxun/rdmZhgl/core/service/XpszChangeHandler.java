package com.redxun.rdmZhgl.core.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.rdmZhgl.core.dao.ProductConfigDao;
import com.redxun.rdmZhgl.core.dao.ProductDao;
import com.redxun.rdmZhgl.core.dao.XpszChangeDao;
import com.redxun.saweb.util.IdUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;

/**
 * @author zz
 * */
@Service
public class XpszChangeHandler implements ProcessStartPreHandler, TaskPreHandler, ProcessStartAfterHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(XpszChangeHandler.class);
    @Resource
    private XpszChangeService xpszChangeService;
    @Resource
    private XpszChangeDao xpszChangeDao;
    @Resource
    private ProductDao productDao;
    @Resource
    private ProductConfigDao productConfigDao;

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
            xpszChangeService.add(applyObj);
        } else {
            xpszChangeService.update(applyObj);
        }
        return applyObj;
    }
    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
//        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
//            String id = bpmInst.getBusKey();
//            Map<String,Object> changeObj = xpszChangeDao.getObjectById(id);
//            String mainId = CommonFuns.nullToString(changeObj.get("mainIds"));
//            String itemId = CommonFuns.nullToString(changeObj.get("itemId"));
//            JSONObject productObj = productConfigDao.getItemObjById(itemId);
//            String itemCode = productObj.getString("itemCode");
//            //先将最新的数据复制一份 作为 历史数据，然后再修改数据
//            JSONObject paramJson = new JSONObject();
//            paramJson.put("mainId",mainId);
//            paramJson.put("itemType","3");
//            Map<String, Object> resultMap = productDao.getItemObject(paramJson);
//            resultMap.put("id", IdUtil.getId());
//            resultMap.put("itemType","2");
//            productDao.addItem(resultMap);
//            //更新最新记录
//            JSONObject param = new JSONObject();
//            param.put("mainId", mainId);
//            param.put(itemCode, changeObj.get("itemDate"));
//            param.put("itemType","3");
//            productDao.updatePlanDate(param);
//        }
    }

    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd cmd, BpmInst bpmInst) {
        return null;
    }
}
