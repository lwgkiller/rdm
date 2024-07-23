package com.redxun.world.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.world.core.dao.WorldFitnessImproveDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
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
import com.redxun.world.core.dao.CkddDao;
/**
 * 适应性改进
 * @author mh
 * @date 2022/4/19 10:40
 */
@Service
public class WorldFitnessImproveHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(WorldFitnessImproveHandler.class);

    @Autowired
    private WorldFitnessImproveManager worldFitnessImproveManager;
    @Autowired
    private WorldFitnessImproveDao worldFitnessImproveDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String applyId = createOrUpdateByFormData(processStartCmd);
        if (StringUtils.isNotBlank(applyId)) {
            processStartCmd.setBusinessKey(applyId);
        }
    }

    // 第一个任务创建之后流程的后置处理器（进行编号的生成和更新） 类别-基地-时间-流水
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
                                          BpmInst bpmInst) {
        String applyId = processStartCmd.getBusinessKey();

        // 获取部门简称 默认其他
        String DepCode = "QT";
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("USER_ID_", ContextUtil.getCurrentUserId());

        List<Map<String, String>> departCode = xcmgProjectOtherDao.getDepRespManById(params);
        if (departCode != null  && !departCode.isEmpty()) {
            for (Map<String, String> depart : departCode) {
                if (depart.get("DEPT_CODE_")!=null) {
                    DepCode = depart.get("DEPT_CODE_");
                    break;
                }
            }
        }

        String applyNumber =
                "HWGJ-" + DepCode +"-"+ XcmgProjectUtil.getNowLocalDateStr("yyyyMMdd") +"-"+ (int)(Math.random() * 100);
        JSONObject param = new JSONObject();
        param.put("applyNumber", applyNumber);
        param.put("id", applyId);
        worldFitnessImproveDao.updateApplyNumber(param);
        return applyId;
    }




    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateByFormData(processNextCmd);
    }

    // 流程成功结束之后的处理  【暂无】
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            String applyId = bpmInst.getBusKey();
            Map<String, Object> param = new HashMap<>();
            param.put("applyId", applyId);
        }
    }



    // 驳回场景cmd中没有表单数据
    private String createOrUpdateByFormData(AbstractExecutionCmd cmd) {
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
        if (StringUtils.isBlank(formDataJson.getString("id"))) {
            worldFitnessImproveManager.createApply(formDataJson);
        } else {
            worldFitnessImproveManager.updateApply(formDataJson);
        }
        return formDataJson.getString("id");
    }
}
