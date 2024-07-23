package com.redxun.serviceEngineering.core.service;

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
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.serviceEngineering.core.dao.WrongPartsDao;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.sys.org.manager.OsGroupManager;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.apache.http.annotation.Obsolete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class WgjzlsjHandler
        implements ProcessStartPreHandler, TaskPreHandler, ProcessStartAfterHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(WgjzlsjHandler.class);
    @Autowired
    private WgjzlsjService wgjzlsjService;
    @Autowired
    private OsGroupManager osGroupManager;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;

    //..整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String wgjzlsjId = createOrUpdateCjzgByFormData(processStartCmd);
        if (StringUtils.isNotBlank(wgjzlsjId)) {
            processStartCmd.setBusinessKey(wgjzlsjId);
        }
    }

    //..整个流程启动后初始化反馈日期,预计归档完成时间,预计制作完成时间,物料所属部门
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd cmd, BpmInst bpmInst) {
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
            formDataJson.put("id", cmd.getBusinessKey());
        }
        Date date = new Date();
        formDataJson.put("submitDate", date);
        Date filingTimeExp = DateUtil.addDay(date, 28);
        formDataJson.put("filingTimeExp", filingTimeExp);
        Date yjwcsj = DateUtil.addDay(date, 42);
        formDataJson.put("yjwcsj", yjwcsj);
        String cpsPrincipalId = formDataJson.getString("cpsPrincipalId");
        OsGroup mainDeps = osGroupManager.getMainDeps(cpsPrincipalId, ContextUtil.getCurrentTenantId());
        formDataJson.put("materialDepartmentId", mainDeps.getGroupId());
        formDataJson.put("materialDepartment", mainDeps.getName());
        wgjzlsjService.updateWgjzlsj(formDataJson);
        return formDataJson.getString("id");
    }

    //..任务审批前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd) iExecutionCmd;
        createOrUpdateCjzgByFormData(processNextCmd);
    }

    //..流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            // 获取流程中表单信息
            IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
            String formData = cmd.getJsonData();
            JSONObject formDataJson = JSONObject.parseObject(formData);
            // 给创建人发钉钉通知，提醒查看
            JSONObject noticeObj = new JSONObject();
            noticeObj.put("content", "物料编码" + formDataJson.getString("materialCode") + formDataJson.getString("dataType")
                    + "外购件资料制作完成，请及时处理");
            sendDDNoticeManager.sendNoticeForCommon(formDataJson.getString("CREATE_BY_"), noticeObj);
        }
    }

    //..
    private String createOrUpdateCjzgByFormData(AbstractExecutionCmd cmd) {
        String jumpType = cmd.getJumpType();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String submitDate = formDataJson.getString("submitDate");
        if (StringUtils.isNotBlank(submitDate)) {
            Date date = formDataJson.getDate("submitDate");
            formDataJson.put("submitDate", date);
        }
        String filingTimeExp = formDataJson.getString("filingTimeExp");
        if (StringUtils.isNotBlank(filingTimeExp)) {
            Date date = formDataJson.getDate("filingTimeExp");
            formDataJson.put("filingTimeExp", date);
        }
        String filingTime = formDataJson.getString("filingTime");
        if (StringUtils.isNotBlank(filingTime)) {
            Date date = formDataJson.getDate("filingTime");
            formDataJson.put("filingTime", date);
        }
        String yjwcsj = formDataJson.getString("yjwcsj");
        if (StringUtils.isNotBlank(yjwcsj)) {
            Date date = formDataJson.getDate("yjwcsj");
            formDataJson.put("yjwcsj", date);
        }
        String makeTimePlan = formDataJson.getString("makeTimePlan");
        if (StringUtils.isNotBlank(makeTimePlan)) {
            Date date = formDataJson.getDate("makeTimePlan");
            formDataJson.put("makeTimePlan", date);
        }
        String makeTime = formDataJson.getString("makeTime");
        if (StringUtils.isNotBlank(makeTime)) {
            Date date = formDataJson.getDate("makeTime");
            formDataJson.put("makeTime", date);
        }
        //..从表单强写节点状态,延续这种做法吧，但似乎用taskScript更优雅一些
        String step = formDataJson.getString("step");
        if (!StringUtils.isBlank(step)) {
            if (step.equals("B") && jumpType.equals("AGREE")) {//B资料确认节点有可能转办，以此通过后要更新一下服务工程负责人
//                formDataJson.put("fwgcPrincipalId", ContextUtil.getCurrentUserId());
//                formDataJson.put("fwgcPrincipal", ContextUtil.getCurrentUser().getFullname());
            } else if (step.equals("C") && jumpType.equals("AGREE")) {//C所内收集节点有可能转办，以此通过后要更新一下产品所责任人
//                formDataJson.put("cpsPrincipalId", ContextUtil.getCurrentUserId());
//                formDataJson.put("cpsPrincipal", ContextUtil.getCurrentUser().getFullname());
            } else if (step.equals("G") && jumpType.equals("AGREE") &&
                    formDataJson.getString("thirdMake").equalsIgnoreCase("yes")) {//G制作更新节点更新制作完成时间
                formDataJson.put("makeTime", new Date());
            }
        }
        if (StringUtils.isBlank(formDataJson.getString("id"))) {
            wgjzlsjService.createWgjzlsj(formDataJson);
        } else {
            wgjzlsjService.updateWgjzlsj(formDataJson);
        }
        return formDataJson.getString("id");
    }

    @Obsolete
    private String createOrUpdateCjzgByFormData_obsolete(AbstractExecutionCmd cmd) {
        String jumpType = cmd.getJumpType();
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
        String submitDate = formDataJson.getString("submitDate");
        if (StringUtils.isNotBlank(submitDate)) {
            Date date = formDataJson.getDate("submitDate");
            formDataJson.put("submitDate", date);
        }
        String firstDeadline = formDataJson.getString("firstDeadline");
        if (StringUtils.isNotBlank(firstDeadline)) {
            Date date = formDataJson.getDate("firstDeadline");
            formDataJson.put("firstDeadline", date);
        }
        String secondDeadline = formDataJson.getString("secondDeadline");
        if (StringUtils.isNotBlank(secondDeadline)) {
            Date date = formDataJson.getDate("secondDeadline");
            formDataJson.put("secondDeadline", date);
        }
        String makeTime = formDataJson.getString("makeTime");
        if (StringUtils.isNotBlank(makeTime)) {
            Date date = formDataJson.getDate("makeTime");
            formDataJson.put("makeTime", date);
        }
        String filingTime = formDataJson.getString("filingTime");
        if (StringUtils.isNotBlank(filingTime)) {
            Date date = formDataJson.getDate("filingTime");
            formDataJson.put("filingTime", date);
        }
        String step = formDataJson.getString("step");
        if (!StringUtils.isBlank(step)) {
            if (step.equals("isAssign")) {
                if (jumpType.equals("AGREE")) {
                    String createBy = formDataJson.getString("CREATE_BY_");
                    OsGroup mainDeps = osGroupManager.getMainDeps(createBy, ContextUtil.getCurrentTenantId());
                    String mainGroupName = mainDeps.getName();
                    if (!mainGroupName.equals("服务工程技术研究所")) {
                        String firstProvide = formDataJson.getString("firstProvide");
                        if (firstProvide.equals("no")) {
                            formDataJson.put("responseLevel", "second");
                        }
                    }
                }
            } else if (step.equals("secondaryResponse")) {
                String secondProvide = formDataJson.getString("secondProvide");
                if (secondProvide.equals("no")) {
                    formDataJson.put("responseLevel", "third");
                }
            } else if (step.equals("firstFwzcrygz")) {
                String filing = formDataJson.getString("filing");
                if (filing.equals("no")) {
                    formDataJson.put("responseLevel", "second");
                }
            } else if (step.equals("secondFwzcrygz")) {
                String filing = formDataJson.getString("filing");
                if (filing.equals("no")) {
                    formDataJson.put("responseLevel", "third");
                }
            } else if (step.equals("cpszrrtx")) {
                String firstProvide = formDataJson.getString("firstProvide");
                if (firstProvide.equals("no")) {
                    formDataJson.put("responseLevel", "second");
                }
            }
        }

        if (StringUtils.isBlank(formDataJson.getString("id"))) {
            wgjzlsjService.createWgjzlsj(formDataJson);
        } else {
            wgjzlsjService.updateWgjzlsj(formDataJson);
        }
        return formDataJson.getString("id");
    }
}
