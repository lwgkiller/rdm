package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.dao.ZlglmkDao;
import com.redxun.rdmZhgl.core.dao.ZljsjdsDao;
import com.redxun.saweb.util.IdUtil;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JsjdsHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(JsjdsHandler.class);
    @Autowired
    private ZljsjdsService zljsjdsService;
    @Autowired
    private ZlglmkDao zlglmkDao;
    @Autowired
    private ZljsjdsDao zljsjdsDao;


    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String jsjdsId = createOrUpdateJsmmByFormData(processStartCmd);
        if (StringUtils.isNotBlank(jsjdsId)) {
            processStartCmd.setBusinessKey(jsjdsId);
        }
    }

    // 第一个任务创建之后流程的后置处理器（进行编号的生成和更新）
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
        BpmInst bpmInst) {
        // String rjzzId = processStartCmd.getBusinessKey();
        // String rjzzNum = rjzzService.toGetRjzzNum();
        // Map<String, Object> param = new HashMap<>();
        // param.put("rjzzId", rjzzId);
        // param.put("rjzzNum", rjzzNum);
        // rjzzDao.updateRjzzNum(param);
        String jsjdsId = processStartCmd.getBusinessKey();
        String nowYearStart = DateFormatUtil.getNowByString("yyyy") + "-01-01";
        String nowYearStartUTC = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(nowYearStart), -8));
        Map<String, Object> param = new HashMap<>();
        param.put("applyTimeStart", nowYearStartUTC);
        param.put("countnumInfo", "yes");
        String jdsNum="";
        String dateNum = DateFormatUtil.getNowByString("yyyy");
        List<JSONObject> maxNumberList = zljsjdsDao.queryMaxJdsNumber(param);
        if (maxNumberList == null || maxNumberList.isEmpty()) {
            jdsNum = "ZLJD-" + dateNum +  "-001";
        }else {
            int existNumber = Integer.parseInt(maxNumberList.get(0).getString("jdsNum").substring(10));
            int thisNumber = existNumber + 1;
            if (thisNumber < 10) {
                jdsNum= "ZLJD-" + dateNum + "-00" + thisNumber;
            }else if(thisNumber < 100&&thisNumber >= 10) {
                jdsNum= "ZLJD-" + dateNum + "-0" +thisNumber;
            }else if(thisNumber < 1000&&thisNumber >= 100) {
                jdsNum= "ZLJD-" + dateNum +"-"+thisNumber;
            }
        }
        param.clear();
        param.put("jsjdsId",jsjdsId);
        param.put("jdsNum",jdsNum);
        zljsjdsDao.updateJdsNum(param);
        return jsjdsId;
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateJsmmByFormData(processNextCmd);
    }

    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        logger.info("ProcessEndHandler");
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
            String formData = cmd.getJsonData();
            JSONObject formDataJson = JSONObject.parseObject(formData);

            // 生成一条数据到中国专利台账记录中
            logger.error("start sync to zgzl");
            JSONObject zgzlObj = toGetZltzFromJds(formDataJson);
            if (zgzlObj != null) {
                logger.error("insert into zgzl");
                zlglmkDao.insertZgzlByJsjds(zgzlObj);
            }
            //生成成果计划记录
            if(StringUtils.isNotBlank(zgzlObj.getString("projectId"))
                    &&StringUtils.isNotBlank(zgzlObj.getString("planId"))) {
                zljsjdsService.saveOutList(zgzlObj);
            }
        }
    }

    private JSONObject toGetZltzFromJds(JSONObject jsjdsObj) {
        // 查找申请人的部门
        Map<String, Object> param = new HashMap<>();
        param.put("USER_ID_", jsjdsObj.getString("CREATE_BY_"));
        List<JSONObject> deptInfos = zlglmkDao.getUserDeptInfoById(param);
        if (deptInfos == null || deptInfos.isEmpty()) {
            logger.error("技术交底申请人的部门找不到！");
            return null;
        }
        // 枚举值
        List<JSONObject> enumList = zlglmkDao.getEnumList(null);
        Map<String, String> enumName2Id = new HashMap<>();
        if (enumList != null && !enumList.isEmpty()) {
            for (JSONObject oneEnum : enumList) {
                enumName2Id.put(oneEnum.getString("enumName"), oneEnum.getString("id"));
            }
        }
        // 专利类型Id
        String zllx = jsjdsObj.getString("zllx");
        String zllxId = "";
        switch (zllx) {
            case "FM":
                zllxId = enumName2Id.get("发明");
                break;
            case "SYXX":
                zllxId = enumName2Id.get("实用新型");
                break;
            case "WGSJ":
                zllxId = enumName2Id.get("外观设计");
                break;
            default:
        }
        if (StringUtils.isBlank(zllxId)) {
            logger.error("专利类型找不到！");
            return null;
        }
        String gnztId = enumName2Id.get("内部审批通过");
        if (StringUtils.isBlank(gnztId)) {
            logger.error("“内部审批通过”状态找不到！");
            return null;
        }
        JSONObject zgzlObj = new JSONObject();
        zgzlObj.put("zgzlId", IdUtil.getId());
        zgzlObj.put("CREATE_BY_", "1");
        zgzlObj.put("departmentId", deptInfos.get(0).getString("groupId"));
        zgzlObj.put("reportName", jsjdsObj.getString("zlName"));
        zgzlObj.put("zllxId", zllxId);
        zgzlObj.put("theInventors", jsjdsObj.getString("fmsjr"));
        zgzlObj.put("myCompanyUserIds", jsjdsObj.getString("myfmsjId"));
        zgzlObj.put("myCompanyUserNames", jsjdsObj.getString("myfmsjName"));
        zgzlObj.put("thepatentee", jsjdsObj.getString("zlsqr"));
        zgzlObj.put("zhuanYe", jsjdsObj.getString("zyType"));
        zgzlObj.put("gnztId", gnztId);
        zgzlObj.put("productName",jsjdsObj.getString("zlyyxm"));
        zgzlObj.put("examinationApproval",jsjdsObj.getDate("bmscwcTime"));
        zgzlObj.put("jsjdsId", jsjdsObj.getString("jsjdsId"));
        zgzlObj.put("projectId", jsjdsObj.getString("projectId"));
        zgzlObj.put("projectName", jsjdsObj.getString("projectName"));
        zgzlObj.put("planId", jsjdsObj.getString("planId"));
        zgzlObj.put("planName", jsjdsObj.getString("planName"));
        return zgzlObj;
    }

    // 驳回场景cmd中没有表单数据
    private String createOrUpdateJsmmByFormData(AbstractExecutionCmd cmd) {
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
        if (StringUtils.isBlank(formDataJson.getString("jsjdsId"))) {
            zljsjdsService.createJsjds(formDataJson);
        } else {
            zljsjdsService.updateJsjds(formDataJson);
        }
        return formDataJson.getString("jsjdsId");
    }
}
