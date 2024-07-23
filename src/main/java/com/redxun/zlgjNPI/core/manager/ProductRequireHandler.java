package com.redxun.zlgjNPI.core.manager;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.*;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.zlgjNPI.core.dao.ProductRequireDao;
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
public class ProductRequireHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, TaskAfterHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(ProductRequireHandler.class);
    @Autowired
    private ProductRequireService productRequireService;
    @Autowired
    private ProductRequireDao productRequireDao;


    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String cpkfId = createOrUpdateProductRequireByFormData(processStartCmd);
        if (StringUtils.isNotBlank(cpkfId)) {
            processStartCmd.setBusinessKey(cpkfId);
        }
    }

    // 第一个任务创建之后流程的后置处理器
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
        BpmInst bpmInst) {
        String cpkfId = processStartCmd.getBusinessKey();
        String nowYearStart = DateFormatUtil.getNowByString("yyyy") + "-01-01";
        String nowYearStartUTC = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(nowYearStart), -8));
        Map<String, Object> param = new HashMap<>();
        param.put("applyTimeStart", nowYearStartUTC);
        param.put("countnumInfo", "yes");
//        int existNumber = secretDao.countSecretNumber(param);
//        int thisNumber = existNumber + 1;
        String lcNum="";
        String dateNum = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String newDate = dateNum.substring(0,4)+dateNum.substring(5,7)+dateNum.substring(8,10);
        List<JSONObject> maxNumberList = productRequireDao.queryMaxNumber(param);
        if (maxNumberList == null || maxNumberList.isEmpty()) {
            lcNum = "SP-" + newDate +  "0001";
        }else {
            int existNumber = Integer.parseInt(maxNumberList.get(0).getString("lcNum").substring(11));
            int thisNumber = existNumber + 1;
            if (thisNumber < 10) {
                lcNum= "SP-" + newDate + "000" + thisNumber;
            }else if(thisNumber < 100&&thisNumber >= 10) {
                lcNum= "SP-" + newDate + "00" +thisNumber;
            }else if(thisNumber < 1000&&thisNumber >= 100) {
                lcNum= "SP-" + newDate + "0" +thisNumber;
            }else if(thisNumber < 10000&&thisNumber >= 1000) {
                lcNum= "SP-" + newDate + thisNumber;
            }
        }
        param.clear();
        param.put("cpkfId",cpkfId);
        param.put("lcNum",lcNum);
        productRequireDao.updateNum(param);
        return cpkfId;
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateProductRequireByFormData(processNextCmd);
    }

    @Override
    public void taskAfterHandle(IExecutionCmd iExecutionCmd, String nodeId, String busKey) {
    }

    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {

    }

    // 驳回场景cmd中没有表单数据
    private String createOrUpdateProductRequireByFormData(AbstractExecutionCmd cmd) {
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
        if (StringUtils.isBlank(formDataJson.getString("cpkfId"))) {
            productRequireService.createProductRequire(formDataJson);
        } else {
            productRequireService.updateProductRequire(formDataJson);
        }
        return formDataJson.getString("cpkfId");
    }


}
