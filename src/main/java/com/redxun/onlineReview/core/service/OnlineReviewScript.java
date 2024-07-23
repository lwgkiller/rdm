package com.redxun.onlineReview.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.bpm.core.manager.BpmTaskManager;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.DateUtil;
import com.redxun.onlineReview.core.dao.OnlineReviewDao;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

// 项目流程节点的触发事件
@Service
public class OnlineReviewScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(OnlineReviewScript.class);

    @Autowired
    private CommonInfoDao commonInfoDao;
    @Autowired
    private OnlineReviewDao onlineReviewDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private BpmTaskManager bpmTaskManager;

    // 制造部计划员
    public Collection<TaskExecutor> getjhy() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String id = formDataJson.getString("id");
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> param = new HashMap<>();
            param.put("belongId", id);
            param.put("timeRisk", "yes");
            List<JSONObject> modelList = onlineReviewDao.queryModel(param);
            for (JSONObject onedata : modelList) {
                String jhyId = onedata.getString("jhyId");
                String jhyName = onedata.getString("jhyName");
                users.add(new TaskExecutor(jhyId, jhyName));
            }
        }
        return users;
    }

    // 技术主管
    public Collection<TaskExecutor> getjszg() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String id = formDataJson.getString("id");
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> param = new HashMap<>();
            param.put("belongId", id);
            param.put("timeRisk", "yes");
            List<JSONObject> modelList = onlineReviewDao.queryModel(param);
            for (JSONObject onedata : modelList) {
                String jhyId = onedata.getString("jszgId");
                String jhyName = onedata.getString("jszgName");
                users.add(new TaskExecutor(jhyId, jhyName));
            }
        }
        return users;
    }

    // 技术主管
    public Collection<TaskExecutor> getjszgtime() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String id = formDataJson.getString("id");
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> param = new HashMap<>();
            param.put("belongId", id);
            param.put("timeRisk", "yes");
            List<JSONObject> modelList = onlineReviewDao.queryModel(param);
            for (JSONObject onedata : modelList) {
                String jhyId = onedata.getString("jszgId");
                String jhyName = onedata.getString("jszgName");
                users.add(new TaskExecutor(jhyId, jhyName));
            }
        }
        return users;
    }

    // 配置会签
    public Collection<TaskExecutor> getpzhq() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String id = formDataJson.getString("id");
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> param = new HashMap<>();
            param.put("belongId", id);
            param.put("timeRisk", "yes");
            List<JSONObject> modelList = onlineReviewDao.queryModel(param);
            for (JSONObject onedata : modelList) {
                String modelId = onedata.getString("id");
                param.put("belongId", modelId);
                List<JSONObject> configList = onlineReviewDao.queryConfig(param);
                for (JSONObject oneconfig : configList) {
                    String pzyId = oneconfig.getString("respId");
                    String pzyName = oneconfig.getString("respId_name");
                    if (StringUtils.isNotBlank(pzyId)) {
                        users.add(new TaskExecutor(pzyId, pzyName));
                    }
                }
            }
        }
        return users;
    }

    // 配置会签
    public Collection<TaskExecutor> getpzhqLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String id = formDataJson.getString("id");
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> param = new HashMap<>();
            param.put("belongId", id);
            param.put("timeRisk", "yes");
            List<JSONObject> modelList = onlineReviewDao.queryModel(param);
            for (JSONObject onedata : modelList) {
                String modelId = onedata.getString("id");
                param.put("belongId", modelId);
                List<JSONObject> configList = onlineReviewDao.queryConfig(param);
                for (JSONObject oneconfig : configList) {
                    String pzyId = oneconfig.getString("respId");
                    if (StringUtils.isNotBlank(pzyId)) {
                        List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(pzyId);
                        if (deptResps != null && !deptResps.isEmpty()) {
                            for (JSONObject depRespMan : deptResps) {
                                users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
                            }
                        }
                    }
                }
            }
        }
        return users;
    }

    // 工艺主管
    public Collection<TaskExecutor> getgyzg() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String id = formDataJson.getString("id");
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> param = new HashMap<>();
            param.put("belongId", id);
            param.put("timeRisk", "yes");
            List<JSONObject> modelList = onlineReviewDao.queryModel(param);
            for (JSONObject onedata : modelList) {
                String jhyId = onedata.getString("gyzgId");
                String jhyName = onedata.getString("gyzgName");
                users.add(new TaskExecutor(jhyId, jhyName));
            }
        }
        return users;
    }


    // 技术主管所长
    public Collection<TaskExecutor> getjszgLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String id = formDataJson.getString("id");
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> param = new HashMap<>();
            param.put("belongId", id);
            param.put("timeRisk", "yes");
            List<JSONObject> modelList = onlineReviewDao.queryModel(param);
            for (JSONObject onedata : modelList) {
                String jszgId = onedata.getString("jszgId");
                List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(jszgId);
                if (deptResps != null && !deptResps.isEmpty()) {
                    for (JSONObject depRespMan : deptResps) {
                        users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
                    }
                }
            }
        }
        return users;
    }

    public Collection<TaskExecutor> getGfLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        List<Map<String, String>> depRespMans = rdmZhglUtil.queryGffzbRespUser();
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    public Collection<TaskExecutor> getZzLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        List<Map<String, String>> depRespMans = rdmZhglUtil.queryZzglbRespUser();
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    public Collection<TaskExecutor> getGyLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        List<Map<String, String>> depRespMans = rdmZhglUtil.queryGybmfzr();
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    public Collection<TaskExecutor> getCgLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        List<Map<String, String>> depRespMans = rdmZhglUtil.queryCgglbfzr();
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    public Collection<TaskExecutor> getZlLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        List<Map<String, String>> depRespMans = rdmZhglUtil.queryZlbzbfzr();
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    public Collection<TaskExecutor> getFwLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        List<Map<String, String>> depRespMans = rdmZhglUtil.queryFwgcs();
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    public boolean isZdxm(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String contractType = formDataJson.getString("contractType");
        if (StringUtils.isNotBlank(contractType)) {
            return true;
        }
        return false;
    }

    public boolean noZdxm(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String contractType = formDataJson.getString("contractType");
        if (StringUtils.isNotBlank(contractType)) {
            return false;
        }
        return true;
    }

    public boolean needConfig(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String id = formDataJson.getString("id");
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> param = new HashMap<>();
            param.put("belongId", id);
            param.put("timeRisk", "yes");
            List<JSONObject> modelList = onlineReviewDao.queryModel(param);
            for (JSONObject onedata : modelList) {
                String modelId = onedata.getString("id");
                param.put("belongId", modelId);
                List<JSONObject> configList = onlineReviewDao.queryConfig(param);
                for (JSONObject oneconfig : configList) {
                    if (StringUtils.isNotBlank(oneconfig.getString("respId"))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean noneedConfig(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String id = formDataJson.getString("id");
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> param = new HashMap<>();
            param.put("belongId", id);
            param.put("timeRisk", "yes");
            List<JSONObject> modelList = onlineReviewDao.queryModel(param);
            for (JSONObject onedata : modelList) {
                String modelId = onedata.getString("id");
                param.put("belongId", modelId);
                List<JSONObject> configList = onlineReviewDao.queryConfig(param);
                for (JSONObject oneconfig : configList) {
                    if (StringUtils.isNotBlank(oneconfig.getString("respId"))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    public boolean isTimeOk(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String id = formDataJson.getString("id");
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> param = new HashMap<>();
            param.put("belongId", id);
            param.put("timeRisk", "yes");
            List<JSONObject> modelList = onlineReviewDao.queryModel(param);
            for (JSONObject onedata : modelList) {
                Date planTime = onedata.getDate("planTime");
                Date needTime = onedata.getDate("needTime");
                if (planTime.getTime() > needTime.getTime()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean noTimeOk(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String id = formDataJson.getString("id");
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> param = new HashMap<>();
            param.put("belongId", id);
            param.put("timeRisk", "yes");
            List<JSONObject> modelList = onlineReviewDao.queryModel(param);
            for (JSONObject onedata : modelList) {
                Date planTime = onedata.getDate("planTime");
                Date needTime = onedata.getDate("needTime");
                if (planTime.getTime() > needTime.getTime()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isLeaderAgree(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String id = formDataJson.getString("id");
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> param = new HashMap<>();
            param.put("belongId", id);
            List<JSONObject> modelList = onlineReviewDao.queryModel(param);
            for (JSONObject onedata : modelList) {
                String gzOption = onedata.getString("gzOption");
                String yzOption = onedata.getString("yzOption");
                if ("不同意".equalsIgnoreCase(gzOption) || "不同意".equalsIgnoreCase(yzOption)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean noLeaderAgree(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String id = formDataJson.getString("id");
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> param = new HashMap<>();
            param.put("belongId", id);
            List<JSONObject> modelList = onlineReviewDao.queryModel(param);
            for (JSONObject onedata : modelList) {
                String gzOption = onedata.getString("gzOption");
                String yzOption = onedata.getString("yzOption");
                if ("不同意".equalsIgnoreCase(gzOption) || "不同意".equalsIgnoreCase(yzOption)) {
                    return true;
                }
            }
        }
        return false;
    }

    // 更新订单下发时间
    public void getddxfTime() {
        Calendar calendar = new GregorianCalendar();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String id = formDataJson.getString("id");
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> param = new HashMap<>();
            param.put("belongId", id);
            param.put("timeRisk", "yes");
            List<JSONObject> modelList = onlineReviewDao.queryModel(param);
            for (JSONObject onedata : modelList) {
                Date ddxfTime = null;
                Date gybomTime = null;
                Date tzxfTime = null;
                String timeId = null;
                String gybom = onedata.getString("gybom");
                String jstz = onedata.getString("jstz");
                String modelId = onedata.getString("id");
                param.put("belongId", modelId);
                List<JSONObject> timeList = onlineReviewDao.queryTime(param);
                for (JSONObject onetime : timeList) {
                    if ("ddxf".equalsIgnoreCase(onetime.getString("timeId"))) {
                        timeId = onetime.getString("id");
                    }

                    if ("gybom".equalsIgnoreCase(onetime.getString("timeId"))
                            && StringUtils.isNotBlank(onetime.getString("finishTime"))
                            && "否".equalsIgnoreCase(gybom)) {
                        gybomTime = onetime.getDate("finishTime");
                        calendar.setTime(gybomTime);
                        calendar.add(calendar.DATE, 1); //把日期往后增加一天,整数  往后推,负数往前移动
                        ddxfTime = calendar.getTime();
                    } else if ("tzxf".equalsIgnoreCase(onetime.getString("timeId"))
                            && StringUtils.isNotBlank(onetime.getString("finishTime"))
                            && "是".equalsIgnoreCase(jstz)) {
                        tzxfTime = onetime.getDate("finishTime");
                        calendar.setTime(tzxfTime);
                        calendar.add(calendar.DATE, 6); //把日期往后增加一天,整数  往后推,负数往前移动
                        ddxfTime = calendar.getTime();
                    }
                }
                JSONObject ddxfTimeJson = new JSONObject();
                if (ddxfTime != null) {
                    ddxfTimeJson.put("finishTime", DateUtil.formatDate(ddxfTime, "yyyy-MM-dd"));
                } else {
                    ddxfTimeJson.put("finishTime", "");
                }
                ddxfTimeJson.put("id", timeId);
                ddxfTimeJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                ddxfTimeJson.put("UPDATE_TIME_", new Date());
                onlineReviewDao.updateOneTime(ddxfTimeJson);
            }
        }
    }

    // 更新零件图册时间
    public void getljtcTime() {
        Calendar calendar = new GregorianCalendar();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String id = formDataJson.getString("id");
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> param = new HashMap<>();
            param.put("belongId", id);
            param.put("timeRisk", "yes");
            List<JSONObject> modelList = onlineReviewDao.queryModel(param);
            for (JSONObject onedata : modelList) {
                Date ljtcTime = null;
                Date rkTime = null;
                String timeId = null;
                String modelId = onedata.getString("id");
                param.put("belongId", modelId);
                List<JSONObject> timeList = onlineReviewDao.queryTime(param);
                for (JSONObject onetime : timeList) {
                    if ("ljtc".equalsIgnoreCase(onetime.getString("timeId"))) {
                        timeId = onetime.getString("id");
                    }

                    if ("rk".equalsIgnoreCase(onetime.getString("timeId"))
                            && StringUtils.isNotBlank(onetime.getString("finishTime"))) {
                        rkTime = onetime.getDate("finishTime");
                        calendar.setTime(rkTime);
                        calendar.add(calendar.DATE, 7); //把日期往后增加一天,整数  往后推,负数往前移动
                        ljtcTime = calendar.getTime();
                    }
                }
                JSONObject ljtcTimeJson = new JSONObject();
                if (ljtcTime != null) {
                    ljtcTimeJson.put("finishTime", DateUtil.formatDate(ljtcTime, "yyyy-MM-dd"));
                } else {
                    ljtcTimeJson.put("finishTime", "");
                }
                ljtcTimeJson.put("id", timeId);
                ljtcTimeJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                ljtcTimeJson.put("UPDATE_TIME_", new Date());
                onlineReviewDao.updateOneTime(ljtcTimeJson);
            }
        }
    }

    // 更新预计交货时间
    public void getplanTime() {
        Calendar calendar = new GregorianCalendar();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String id = formDataJson.getString("id");
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> param = new HashMap<>();
            param.put("belongId", id);
            param.put("timeRisk", "yes");
            List<JSONObject> modelList = onlineReviewDao.queryModel(param);
            for (JSONObject onedata : modelList) {
                Date needTime = onedata.getDate("needTime");
                Date ljtcTime = null;
                Date cbscTime = null;
                String modelId = onedata.getString("id");
                param.put("belongId", modelId);
                List<JSONObject> timeList = onlineReviewDao.queryTime(param);
                for (JSONObject onetime : timeList) {
                    if ("ljtc".equalsIgnoreCase(onetime.getString("timeId"))) {
                        ljtcTime = onetime.getDate("finishTime");
                    }
                    if ("cbsc".equalsIgnoreCase(onetime.getString("timeId"))) {
                        cbscTime = onetime.getDate("finishTime");
                    }
                }
                Date planTime = ljtcTime.compareTo(cbscTime) > 0 ? ljtcTime : cbscTime;
                JSONObject planTimeJson = new JSONObject();
                planTimeJson.put("id", modelId);
                planTimeJson.put("planTime", DateUtil.formatDate(planTime, "yyyy-MM-dd"));
                planTimeJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                planTimeJson.put("UPDATE_TIME_", new Date());
                onlineReviewDao.updatePlanTime(planTimeJson);
                JSONObject timeRiskJson = new JSONObject();
                timeRiskJson.put("id", modelId);
                timeRiskJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                timeRiskJson.put("UPDATE_TIME_", new Date());
                if (planTime.compareTo(needTime) > 0) {
                    timeRiskJson.put("timeRisk", "不满足");
                    onlineReviewDao.updateTimeRisk(timeRiskJson);
                } else {
                    timeRiskJson.put("timeRisk", "满足");
                    onlineReviewDao.updateTimeRisk(timeRiskJson);
                }
            }
        }
    }

//    public void taskCreateScript(Map<String, Object> vars) {
//        logger.info("taskCreateScript");
//        TaskEntity taskMap = (TaskEntity)vars.get("task");
//        AbstractExecutionCmd cmd = (AbstractExecutionCmd) vars.get("cmd");
//        String formData = cmd.getJsonData();
//        JSONObject formDataJson = JSONObject.parseObject(formData);
//        boolean allblank = true;
//        try {
//            String id = formDataJson.getString("id");
//            if (StringUtils.isNotBlank(id)) {
//                Map<String, Object> param = new HashMap<>();
//                param.put("belongId", id);
//                param.put("timeRisk", "yes");
//                List<JSONObject> modelList = onlineReviewDao.queryModel(param);
//                for (JSONObject onedata : modelList) {
//                    String modelId = onedata.getString("id");
//                    param.put("belongId", modelId);
//                    List<JSONObject> configList = onlineReviewDao.queryConfig(param);
//                    for (JSONObject oneconfig : configList) {
//                        if (StringUtils.isNotBlank(oneconfig.getString("respId"))) {
//                            allblank = false;
//                            break;
//                        }
//                    }
//                }
//                if(allblank){
//                    ProcessNextCmd processNextCmd = new ProcessNextCmd();
//                    processNextCmd.setTaskId(taskMap.getId());
//                    processNextCmd.setJumpType("AGREE");
//                    processNextCmd.setJsonData(cmd.getJsonData());
//                    processNextCmd.setAgentToUserId("1");
//                    bpmTaskManager.doNext(processNextCmd);
//                }
//            }
//        } catch (Exception e) {
//            logger.error("Exception in manualSkipFirst", e);
//
//        }
//    }
}
