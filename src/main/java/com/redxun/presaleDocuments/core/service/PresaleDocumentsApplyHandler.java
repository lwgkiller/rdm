package com.redxun.presaleDocuments.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aspose.words.Run;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.core.util.StringUtil;
import com.redxun.presaleDocuments.core.dao.PresaleDocumentsApplyDao;
import com.redxun.presaleDocuments.core.util.PresaleDocumentsConst;
import com.redxun.rdmCommon.core.controller.RdmApiController;
import com.redxun.rdmCommon.core.manager.CommonBpmMediator;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.sys.core.manager.SysSeqIdManager;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PresaleDocumentsApplyHandler implements
        ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(PresaleDocumentsApplyHandler.class);
    @Autowired
    private PresaleDocumentsApplyService presaleDocumentsApplyService;
    @Autowired
    private PresaleDocumentsApplyDao presaleDocumentsApplyDao;
    @Autowired
    private SysSeqIdManager sysSeqIdManager;
    @Autowired
    private CommonBpmMediator commonBpmMediator;

    //..整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        JSONObject formDataJson = JSONObject.parseObject(processStartCmd.getJsonData());
        formDataJson.put("INST_ID_", processStartCmd.getBpmInstId());
        processStartCmd.setJsonData(formDataJson.toJSONString());
        String businessId = createOrUpdateBusinessByFormData(processStartCmd);
        //技术资料附件类型的，要在创建或更新完成后进行判重，重复的抛出运行时错误，自动回滚
        if (formDataJson.getString("businessType").equalsIgnoreCase(
                PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_JISHUWENJIANFUJIAN) &&
                formDataJson.containsKey("productSpectrum")) {
            //如果是技术资料附件申请，则判断有没有REF_ID_是空，物料号和设计型号一致的技术资料附件申请，有证明重复创建，不允许。
            JSONArray productSpectrums = formDataJson.getJSONArray("productSpectrum");
            if (productSpectrums.size() > 0) {
                JSONObject productSpectrum = (JSONObject) productSpectrums.get(0);
                JSONObject params = new JSONObject().
                        fluentPut("designModel", productSpectrum.getString("designModel_item")).
                        fluentPut("businessType", PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_JISHUWENJIANFUJIAN).
                        fluentPut("REF_ID_EMPTY", "true");
                if (presaleDocumentsApplyDao.countDataListQuery(params) > 1) {
                    throw new RuntimeException("‘技术文件资料附件’类型的申请单，相同产品型号的不能重复新增。需要扩充请到全系文件列表里发起变更！");
                }
            }
        }
        if (StringUtils.isNotBlank(businessId)) {
            processStartCmd.setBusinessKey(businessId);
            //@lwgkiller:映射下面的话，一个流程事务中的cmd.getJsonData是单例的，如果不想从数据库取数据，就必须保证过程中单例
            //的任何改变都要写回单例中，这个bug到现在才发现，以前是因为没出现过不保存草稿直接提交的场景用这种写法
            formDataJson.put("id", businessId);
            processStartCmd.setJsonData(formDataJson.toJSONString());
        }
    }

    //..
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd cmd, BpmInst bpmInst) {
        JSONObject formDataJson = JSONObject.parseObject(cmd.getJsonData());
        //@lwgkiller:在这设个启动后置处理器，只是重新回填下INST_ID_，防止草稿直接启动没有INST_ID_，这里并没有进行update操作，但是会通过taskCreateScript的
        //update操作体现出来，也就是说一个流程事务中的cmd.getJsonData是单例的。这样processStartPreHandle里的update操作可以省略了
        formDataJson.put("INST_ID_", cmd.getBpmInstId());
        if (StringUtil.isEmpty(formDataJson.getString("businessNo"))) {
            formDataJson.put("businessNo", sysSeqIdManager.genSequenceNo("PresaleDocumentApply", "1"));
        }
        cmd.setJsonData(formDataJson.toJSONString());
        return null;
    }

    //..任务审批前置处理器,只有第一个编辑节点需要，因为要更新业务单据的
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd) iExecutionCmd;
        createOrUpdateBusinessByFormData(processNextCmd);
    }

    //..
    @Override
    public void endHandle(BpmInst bpmInst) {
    }

    //..
    private String createOrUpdateBusinessByFormData(AbstractExecutionCmd cmd) {
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
            presaleDocumentsApplyService.createBusiness(formDataJson);
        } else {
            presaleDocumentsApplyService.updateBusiness(formDataJson);
        }
        return formDataJson.getString("id");
    }
}
