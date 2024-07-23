package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmSolutionManager;
import com.redxun.core.util.ExceptionUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.serviceEngineering.core.dao.JxbzzbshDao;
import com.redxun.serviceEngineering.core.dao.StandardvalueShipmentnotmadeDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JxbzzbshHandler
        implements ProcessStartPreHandler, TaskPreHandler, ProcessStartAfterHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(JxbzzbshHandler.class);
    @Autowired
    private JxbzzbshService jxbzzbshService;

    @Autowired
    private StandardvalueShipmentnotmadeDao standardDao;

    @Autowired
    private JxbzzbshDao jxbzzbshDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private UserService userService;
    @Resource
    private BpmSolutionManager bpmSolutionManager;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private BpmInstManager bpmInstManager;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String jxbzzbshId = createOrUpdateCjzgByFormData(processStartCmd);
        if (StringUtils.isNotBlank(jxbzzbshId)) {
            processStartCmd.setBusinessKey(jxbzzbshId);
        }
    }


    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd) iExecutionCmd;
        createOrUpdateCjzgByFormData(processNextCmd);
    }


    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            JSONObject newData = new JSONObject();
            JSONObject oldData = new JSONObject();
            String id = bpmInst.getBusKey();
            JSONObject formDataJson = jxbzzbshDao.queryJxbzzbshById(id);
            String oldId =formDataJson.getString("oldId");
            JSONObject oldJX = jxbzzbshDao.queryJxbzzbshById(oldId);
            if (StringUtils.isNotBlank(oldId)) {
                char num = oldJX.getString("versionNum").charAt(0);
                num++;
                oldData.put("id",oldId);
                oldData.put("note","历史版本");
                jxbzzbshDao.updateStatus(oldData);
                newData.put("id",id);
                newData.put("versionNum",String.valueOf(num));
                newData.put("note","最新版本");
                jxbzzbshDao.updateStatus(newData);
            }else {
                newData.put("id",id);
                newData.put("versionNum","A");
                newData.put("note","最新版本");
                jxbzzbshDao.updateStatus(newData);
            }
            //发送钉钉消息
            Map<String, Object> roleParams = new HashMap<>();
            roleParams.put("groupName", "检修标准值表归档通知");
            roleParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            List<Map<String, String>> userInfos = xcmgProjectOtherDao.queryUserByGroupName(roleParams);
            if (userInfos != null && !userInfos.isEmpty()) {
                StringBuilder idBuilder = new StringBuilder();
                for (Map<String, String> oneLeader : userInfos) {
                    idBuilder.append(oneLeader.get("USER_ID_")).append(",");
                }
                if (idBuilder.length() > 0) {
                    idBuilder.deleteCharAt(idBuilder.length() - 1);
                }
                String title = "检修标准值表归档通知";
                String plainTxt = "销售型号:"+formDataJson.getString("salesModel")+"检修标准值表已归档发布,请登录RDM后市场技术-检修标准值表-编制及审核查看";
                String sendMsg = "【RDM平台消息】标题：" + title + ",内容：" + plainTxt + "。";
                JSONObject taskJson = new JSONObject();
                taskJson.put("content", sendMsg);
                sendDDNoticeManager.sendNoticeForCommon(idBuilder.toString(), taskJson);
            }
            //如果是完整版流程,则启动一个变更流程
            if("是".equalsIgnoreCase(formDataJson.getString("testChange"))){
                //查询完整版对应的测试版信息
                JSONObject testJson = jxbzzbshDao.getTestJxbzzbshDetailByMaterialCode(formDataJson.getString("materialCode"));
                IUser user = userService.getByUserId(testJson.getString("principalId"));
                ContextUtil.setCurUser(user);
                // 查找solutionId
                BpmSolution bpmSol = bpmSolutionManager.getByKey("JXBZZBSH", "1");
                String solId = "";
                if (bpmSol != null) {
                    solId = bpmSol.getSolId();
                }
                ProcessMessage handleMessage = new ProcessMessage();
                try {
                    ProcessHandleHelper.setProcessMessage(handleMessage);
                    ProcessStartCmd startCmd = new ProcessStartCmd();
                    startCmd.setSolId(solId);
                    testJson.put("autoProcess", "yes");
                    testJson.put("changeReason", formDataJson.getString("changeReason"));
                    testJson.put("oldId", testJson.getString("oldId"));
                    testJson.put("CREATE_BY_", testJson.getString("principalId"));
                    testJson.put("CREATE_TIME", new Date());
                    startCmd.setJsonData(testJson.toJSONString());
                    // 启动流程
                    bpmInstManager.doStartProcess(startCmd);
                } catch (Exception ex) {
                    // 把具体的错误放置在内部处理，以显示正确的错误信息提示，在此不作任何的错误处理
                    logger.error(ExceptionUtil.getExceptionMessage(ex));
                }
            }

        }
    }

    // 驳回场景cmd中没有表单数据
    private String createOrUpdateCjzgByFormData(AbstractExecutionCmd cmd) {
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
        if (StringUtils.isBlank(formDataJson.getString("id"))) {
            jxbzzbshService.createJxbzzbsh(formDataJson);
        } else {
            jxbzzbshService.updateJxbzzbsh(formDataJson);
        }
        return formDataJson.getString("id");
    }

    /**
     * 任务创建后将制作状态改为制作中
     */
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
        JSONObject standard = standardDao.queryById(formDataJson.getString("shipmentnotmadeId"));
        standard.put("betaCompletion", "zzing");
        standard.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        standard.put("UPDATE_TIME_", new Date());
        standardDao.updateData(standard);

        return formDataJson.getString("id");
    }
}
