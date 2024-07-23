package com.redxun.productDataManagement.core.manager;

import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmSolutionManager;
import com.redxun.core.util.ExceptionUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.productDataManagement.core.dao.CpxhsqDao;
import com.redxun.productDataManagement.core.dao.ProductSpectrumDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
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
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class CpxhsqHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(CpxhsqHandler.class);
    @Autowired
    private CpxhsqManager cpxhsqManager;
    @Autowired
    private CpxhsqDao cpxhsqDao;
    @Autowired
    private ProductSpectrumDao productSpectrumDao;
    @Autowired
    private UserService userService;
    @Resource
    private BpmSolutionManager bpmSolutionManager;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private ProductSpectrumService productSpectrumService;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String applyId = createOrUpdateByFormData(processStartCmd);
        if (StringUtils.isNotBlank(applyId)) {
            processStartCmd.setBusinessKey(applyId);
        }
    }

    // 第一个任务创建之后流程的后置处理器（进行编号的生成和更新）
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
        BpmInst bpmInst) {
        String applyId = processStartCmd.getBusinessKey();
        String applyNumber =
            "cpxhsq-" + XcmgProjectUtil.getNowLocalDateStr("yyyyMMddHHmmssSSS") + (int)(Math.random() * 100);
        JSONObject param = new JSONObject();
        param.put("applyNumber", applyNumber);
        param.put("id", applyId);

        cpxhsqDao.updateCpxhsqNumber(param);
        return applyId;
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateByFormData(processNextCmd);
    }

    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {

            IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
            String formData = cmd.getJsonData();
            JSONObject formDataJson = JSONObject.parseObject(formData);

            // 要从这个流程结束发起产品型谱发布流程
            IUser user = userService.getByUserId("1");
            ContextUtil.setCurUser(user);
            // 查找solutionId
            BpmSolution bpmSol = bpmSolutionManager.getByKey("CPXPFB", "1");
            String solId = "";
            if (bpmSol != null) {
                solId = bpmSol.getSolId();
            }
            ProcessMessage handleMessage = new ProcessMessage();
            try {
                ProcessHandleHelper.setProcessMessage(handleMessage);
                ProcessStartCmd startCmd = new ProcessStartCmd();
                startCmd.setSolId(solId);
                JSONObject objData = new JSONObject();
                // 拼接主表数据
                objData.put("designModel", formDataJson.getString("designModel"));
                // 2023年3月30日09:09:40 表单中增加了产品主管字段，弃用创建人
                // 2023年3月31日15:38:04 产品主管为空的 用创建人
                String productManagerId = formDataJson.getString("productManagerId");
                //2023年10月23日09:52:46 带入产品主管的部门信息
                JSONObject deptInfo = commonInfoManager.queryDeptByUserId(productManagerId);
                String deptId = deptInfo.getString("deptId");
                String deptName = deptInfo.getString("deptName");
                objData.put("departName", deptName);
                objData.put("departId", deptId);
                if (StringUtils.isEmpty(productManagerId)) {
                    objData.put("productManagerId", formDataJson.getString("CREATE_BY_"));
                    objData.put("productManagerName", formDataJson.getString("creatorName"));
                } else {
                    objData.put("productManagerId", formDataJson.getString("productManagerId"));
                    objData.put("productManagerName", formDataJson.getString("productManagerName"));
                }
                objData.put("region", formDataJson.getString("region"));
                objData.put("dischargeStage", formDataJson.getString("dischargeStage"));
                // 2023年3月2日08:58:28 增加新字段 技术规格模板类型 国内外
                objData.put("abroad", formDataJson.getString("abroad"));
                String jsggType = formDataJson.getString("jsggType");
                objData.put("jsggType", jsggType);
                objData.put("CREATE_BY_", "1");
                objData.put("CREATE_TIME", new Date());

                // 拼接配置表数据
                JSONObject settingsParams = new JSONObject();
                settingsParams.put("type", "mainSettingInit");
                List<JSONObject> settingsList = productSpectrumDao.settingListQuery(settingsParams);
                objData.put("mainSettingGrid", settingsList);

                // 拼接参数表数据

                JSONObject params = new JSONObject();
                if ("中大挖".equalsIgnoreCase(jsggType)) {
                    params.put("type", "mainParamZDW");
                } else if ("微小挖".equalsIgnoreCase(jsggType)) {
                    params.put("type", "mainParamWXW");
                } else if ("轮挖".equalsIgnoreCase(jsggType)) {
                    params.put("type", "mainParamLW");
                } else {
                    // 默认中大挖吧
                    params.put("type", "mainParamZDW");
                }
                // 这里要用传过来的类型
                List<JSONObject> paramList = productSpectrumDao.paramListQuery(params);
                objData.put("mainParamGrid", paramList);
                startCmd.setJsonData(objData.toJSONString());
                // 启动流程
                bpmInstManager.doStartProcess(startCmd);
            } catch (Exception ex) {
                // 把具体的错误放置在内部处理，以显示正确的错误信息提示，在此不作任何的错误处理
                logger.error(ExceptionUtil.getExceptionMessage(ex));
            }
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
            cpxhsqManager.createCpxhsq(formDataJson);
        } else {
            cpxhsqManager.updateCpxhsq(formDataJson);
        }
        return formDataJson.getString("id");
    }
}
