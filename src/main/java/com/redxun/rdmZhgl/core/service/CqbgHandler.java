package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.rdmZhgl.core.dao.CqbgDao;
import com.redxun.rdmZhgl.core.dao.ZlglmkDao;
import com.redxun.rdmZhgl.core.dao.ZljsjdsDao;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CqbgHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(CqbgHandler.class);
    @Autowired
    private CqbgService cqbgService;
    @Autowired
    private CqbgDao cqbgDao;
    @Autowired
    private ZlglmkDao zlglmkDao;
    @Autowired
    private ZljsjdsDao zljsjdsDao;
    @Autowired
    private ZljsjdsService zljsjdsService;
    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String cqbgId = createOrUpdateCqbgByFormData(processStartCmd);
        if (StringUtils.isNotBlank(cqbgId)) {
            processStartCmd.setBusinessKey(cqbgId);
        }
    }

    // 第一个任务创建之后流程的后置处理器（进行编号的生成和更新）
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
        BpmInst bpmInst) {
        String cqbgId = processStartCmd.getBusinessKey();
        JSONObject param = new JSONObject();
        param.put("cqbgId", cqbgId);
        cqbgService.updateCqbg(param);
        return cqbgId;
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateCqbgByFormData(processNextCmd);
    }

    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            String cqbgId = bpmInst.getBusKey();
            JSONObject param = new JSONObject();
            JSONObject detailObj = cqbgDao.queryCqbgById(cqbgId);
            String flowType = detailObj.getString("flowType");
            if("person".equalsIgnoreCase(flowType)){
                param.put("jsjdsId",detailObj.getString("jsjdsId"));
                param.put("fmsjr",detailObj.getString("nfmsjr"));
                param.put("myfmsjId",detailObj.getString("nmyfmsjId"));
                param.put("myfmsjName",detailObj.getString("nmyfmsjName"));
                param.put("zlsqr",detailObj.getString("nzlsqr"));
                cqbgDao.updateJsjds(param);
            }else if("project".equalsIgnoreCase(flowType)){
                param.put("jsjdsId",detailObj.getString("jsjdsId"));
                param.put("projectId",detailObj.getString("projectId"));
                param.put("projectName",detailObj.getString("projectName"));
                param.put("planId",detailObj.getString("planId"));
                param.put("planName",detailObj.getString("planName"));
                param.put("sflx","是");
                JSONObject jdsObj = zljsjdsDao.queryJsjdsById(detailObj.getString("jsjdsId"));
                if(StringUtils.isBlank(jdsObj.getString("instStatus"))||
                        "SUCCESS_END".equalsIgnoreCase(jdsObj.getString("instStatus"))){
                    cqbgDao.updateZgzl(param);
                    JSONObject oneObject = new JSONObject();
                    //生成成果计划记录
                    List<JSONObject> checJdskList = cqbgDao.queryCqbgByJds(detailObj.getString("jsjdsId"));
                    // 实际描述
                    String reportName = checJdskList.get(0).getString("reportName");
                    // 专利Id
                    String zgzlId = checJdskList.get(0).getString("zgzlId");
                    if(StringUtils.isNotBlank(detailObj.getString("projectId"))
                            &&StringUtils.isNotBlank(detailObj.getString("planId"))) {
                        oneObject.put("projectId", detailObj.getString("projectId"));
                        oneObject.put("planId", detailObj.getString("planId"));
                        oneObject.put("reportName", reportName);
                        oneObject.put("zgzlId", zgzlId);
                        zljsjdsService.saveOutList(oneObject);
                    }
                }
                zlglmkDao.updateJdsProject(param);
            }
        }
    }

    // 驳回场景cmd中没有表单数据
    private String createOrUpdateCqbgByFormData(AbstractExecutionCmd cmd) {
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
        if (StringUtils.isBlank(formDataJson.getString("cqbgId"))) {
            cqbgService.createCqbg(formDataJson);
        } else {
            cqbgService.updateCqbg(formDataJson);
        }
        return formDataJson.getString("cqbgId");
    }
}
