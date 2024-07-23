package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.service.ActInstService;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmTaskManager;
import com.redxun.core.script.GroovyScript;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.dao.CcbgDao;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.MaintainabilityDisassemblyProposalDao;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualDemandDao;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualMctDao;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualfileDao;
import com.redxun.sys.org.manager.GroupServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MaintenanceManualMctScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(MaintenanceManualMctScript.class);
    @Autowired
    private MaintenanceManualMctDao maintenanceManualMctDao;
    @Autowired
    private MaintenanceManualMctService maintenanceManualMctService;
    @Autowired
    private MaintenanceManualfileDao maintenanceManualfileDao;
    @Autowired
    private MaintenanceManualDemandDao maintenanceManualDemandDao;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private BpmTaskManager bpmTaskManager;
    @Autowired
    private CommonInfoManager commonInfoManager;

    //..
    public Collection<TaskExecutor> getCreviewingUsers() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        List<Map<String, String>> deptResps = commonInfoManager.queryJszxAllSuoZhang();
        if (deptResps != null && !deptResps.isEmpty()) {
            for (Map<String, String> depRespMan : deptResps) {
                users.add(new TaskExecutor(depRespMan.get("USER_ID_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    //..
    public Collection<TaskExecutor> getDreviewingServiceUsers() {
        List<TaskExecutor> users = new ArrayList<>();
        JSONObject params = new JSONObject();
        params.put("TENANT_ID_", "1");
        params.put("GROUP_ID_", "618713695671535246");//服务所id
        List<JSONObject> deptResps = maintenanceManualMctDao.getDeptRespByGroupId(params);
        if (deptResps != null && !deptResps.isEmpty()) {
            for (JSONObject depRespMan : deptResps) {
                users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
            }
        }
        return users;
    }

    //..编制：以任何形式进入
    //..服务工程校对：以任何形式进入
    //..产品所长审核：以任何形式进入
    //..服务工程所长审核:以任何形式进入
    //..分管领导审批：以任何形式进入
    //..执行中：以任何形式进入
    public void taskCreateScript(Map<String, Object> vars) {
        try {
            //@lwgkiller:阿诺多罗 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
            JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
//            JSONObject jsonObject = maintenanceManualMctService.getDetail(vars.get("busKey").toString());
            String activitiId = vars.get("activityId").toString();
            jsonObject.put("businessStatus", activitiId);
            maintenanceManualMctService.updateBusiness(jsonObject);
            //todo:mark！！！如果进入的是执行中状态，驱动需求流程继续，根据demandInstid找taskId，将taskId给往下办了
            if (activitiId.equalsIgnoreCase("F-executing")) {
                String demandInstid = jsonObject.getString("demandInstid");
                BpmInst bpmInst = bpmInstManager.get(demandInstid);
                if (bpmInst != null) {
                    List<BpmTask> bpmTaskList = bpmTaskManager.getByInstId(bpmInst.getInstId());
                    for (BpmTask bpmTask : bpmTaskList) {
                        ProcessNextCmd processNextCmd = new ProcessNextCmd();
                        processNextCmd.setTaskId(bpmTask.getId());
                        processNextCmd.setJumpType("AGREE");
                        JSONObject manualDemand = maintenanceManualDemandDao.queryDetailById(jsonObject.getString("demandId"));
                        processNextCmd.setJsonData(manualDemand.toJSONString());
                        processNextCmd.setAgentToUserId("1");
                        bpmTaskManager.doNext(processNextCmd);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Exception in F-executing taskCreateScript", e);
        }
    }

    //..编制：以通过的形式结束
    //..执行中：以通过的形式结束-
    //..翻译中：以通过的形式结束-由于执行中可走向翻译中和end节点，taskEndScript判断不出来到底走的哪个，因此此处要转移到流程结束处理器
    public void taskEndScript(Map<String, Object> vars) {
        try {
            //@lwgkiller:阿诺多罗 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
            JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
//            JSONObject jsonObject = maintenanceManualMctService.getDetail(vars.get("busKey").toString());
            String activitiId = vars.get("activityId").toString();
            if (activitiId.equalsIgnoreCase("A-editing")) {
                jsonObject.put("applyUserId", ContextUtil.getCurrentUserId());
                jsonObject.put("applyUser", ContextUtil.getCurrentUser().getFullname());
                jsonObject.put("applyDepId", ContextUtil.getCurrentUser().getMainGroupId());
                jsonObject.put("applyDep", ContextUtil.getCurrentUser().getMainGroupName());
                maintenanceManualMctService.updateBusiness(jsonObject);
//            } else if (activitiId.equalsIgnoreCase("F-executing")) {//F2-translation
//                jsonObject.put("businessStatus", "G-close");
//                jsonObject.put("BconfirmingId", ContextUtil.getCurrentUserId());//取代自动传过来的确认人
//                jsonObject.put("Bconfirming", ContextUtil.getCurrentUser().getFullname());//取代自动传过来的确认人
//                maintenanceManualMctService.updateBusiness(jsonObject);
//                //1.将MCT自己的匹配明细更新到对应Demand的匹配明细
//                JSONObject manualDemand = maintenanceManualDemandDao.queryDetailById(jsonObject.getString("demandId"));//Demand主信息
//                if (manualDemand != null) {//如果找到了Demand主信息
//                    JSONObject params = new JSONObject();
//                    params.put("businessId", jsonObject.getString("id"));
//                    List<JSONObject> manualMatchList = maintenanceManualDemandDao.getManualMatchList(params);//这个方法是共用的，此处找的是MCT的匹配明细
//                    if (manualMatchList != null && manualMatchList.size() == 1) {//MCT的匹配明细是唯一的候进行替换(前端控制，到这里只要有肯定是唯一)
//                        params.put("businessId", jsonObject.getString("demandId"));
//                        maintenanceManualDemandDao.deleteManualMatch(params);//删掉Demand原有的匹配明细
//                        JSONObject DemandMatch = new JSONObject();
//                        DemandMatch.put("id", IdUtil.getId());
//                        DemandMatch.put("businessId", jsonObject.getString("demandId"));
//                        DemandMatch.put("manualfileId", manualMatchList.get(0).getString("manualFileId"));//此处找的是MCT的匹配明细的manualFileId
//                        DemandMatch.put("CREATE_BY_", ContextUtil.getCurrentUserId());
//                        DemandMatch.put("CREATE_TIME_", new Date());
//                        maintenanceManualDemandDao.insertManualMatch(DemandMatch);//重新新增Demand的匹配明细
//                    }
//                }
//                //2.驱动需求流程继续，根据demandInstid找taskId，将taskId给往下办了
//                BpmInst bpmInst = bpmInstManager.get(jsonObject.getString("demandInstid"));
//                if (bpmInst != null) {
//                    List<BpmTask> bpmTaskList = bpmTaskManager.getByInstId(bpmInst.getInstId());
//                    for (BpmTask bpmTask : bpmTaskList) {
//                        ProcessNextCmd processNextCmd = new ProcessNextCmd();
//                        processNextCmd.setTaskId(bpmTask.getId());
//                        processNextCmd.setJumpType("AGREE");
//                        processNextCmd.setJsonData(manualDemand.toJSONString());
//                        processNextCmd.setAgentToUserId("1");
//                        bpmTaskManager.doNext(processNextCmd);
//                    }
//                }
            }
        } catch (Exception e) {
            logger.error("Exception in taskEndScript", e);
        }
    }

    //..是否新增
    public boolean isMake(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        if (formDataJson.getString("instructions").equalsIgnoreCase("新增")) {
            return true;
        } else {
            return false;
        }
    }

    //..是否不是新增
    public boolean isNotMake(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        if (!formDataJson.getString("instructions").equalsIgnoreCase("新增")) {
            return true;
        } else {
            return false;
        }
    }

    //..是否CE
    public boolean isCE(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        if (formDataJson.getString("isCE").equalsIgnoreCase("是")) {
            return true;
        } else {
            return false;
        }
    }

    //..是否不是CE
    public boolean isNotCE(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        if (formDataJson.getString("isCE").equalsIgnoreCase("否")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @lwgkiller:阿诺多罗。此处控制执行中的办理页面 似乎用不到了，因为匹配验证机制变了，Demand最后记录引用日志，以其明细中的唯一一条为准，通过MCT回调Demand时，也要求有且一条
     * 因此，永远以这一条为准了，这个锁就可以不检查了！！！
     */
    public boolean isZhiXingZhongOk(Map<String, Object> vars, BpmTask task) {
        //@lwgkiller:阿诺多罗 本来想的下面这个cmd可以取代getDetail，而且传来的是表单的最新数据，但是这个执行判断脚本就是获取不到，坑
        //IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        //String formData = cmd.getJsonData();
        //JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONObject mct = maintenanceManualMctService.getDetail(vars.get("busKey").toString());
        if (mct != null) {
            JSONObject params = new JSONObject();
            params.put("salesModel", mct.getString("salesModel"));
            params.put("designModel", mct.getString("designModel"));
            params.put("materialCode", mct.getString("materialCode"));
            params.put("manualLanguage", mct.getString("manualLanguage"));
            params.put("isCE", mct.getString("isCE"));
            params.put("manualStatus", "可打印");
            List<JSONObject> manualFileList = maintenanceManualfileDao.dataListQuery(params);
            if (manualFileList != null && manualFileList.size() > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
