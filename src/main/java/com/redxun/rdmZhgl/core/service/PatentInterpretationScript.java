package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.service.ActInstService;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmTaskManager;
import com.redxun.core.json.JsonResult;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.DateUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.dao.PatentInterpretationDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.db.manager.SysSqlCustomQueryUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PatentInterpretationScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(PatentInterpretationScript.class);
    @Autowired
    private PatentInterpretationDao patentInterpretationDao;
    @Autowired
    private PatentInterpretationService patentInterpretationService;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private BpmTaskManager bpmTaskManager;
    @Autowired
    private ActInstService actInstService;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private UserService userService;

    //..根据解读人获取解读人
    public Collection<TaskExecutor> getInterpreterUsers() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String interpreterUserId = formDataJson.getString("interpreterUserId");
        String interpreterUser = formDataJson.getString("interpreterUser");
        users.add(new TaskExecutor(interpreterUserId, interpreterUser));
        return users;
    }

    //..根据专业获取解读助手
    public Collection<TaskExecutor> getInterpreterUserAsses() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String key = "解读助手-" + formDataJson.getString("professionalCategory");
        String[] interpreterUserAssNos = sysDicManager.getBySysTreeKeyAndDicKey(
                "patentInterpretationManGroups", key).getValue().split(",");
        for (String userNo : interpreterUserAssNos) {
            IUser user = userService.getByUsername(userNo);
            users.add(new TaskExecutor(user.getUserId(), user.getFullname()));
        }
        return users;
    }

    //..根据专业获取解读牵头人
    public Collection<TaskExecutor> getLeaderUsers() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String key = "专业牵头人-" + formDataJson.getString("professionalCategory");
        String[] interpreterUserAssNos = sysDicManager.getBySysTreeKeyAndDicKey(
                "patentInterpretationManGroups", key).getValue().split(",");
        for (String userNo : interpreterUserAssNos) {
            IUser user = userService.getByUsername(userNo);
            users.add(new TaskExecutor(user.getUserId(), user.getFullname()));
        }
        return users;
    }

    //..根据明细表里的所长获取所长
    public Collection<TaskExecutor> getSuoZhangs() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String key = formDataJson.getString("id");
        JSONObject params = new JSONObject();
        params.put("mainId", key);
        try {
            JsonResult result = SysSqlCustomQueryUtil.queryForJson("列出某个专利解读的明细的相关人员", params.toJSONString());
            for (Object object : (ArrayList) result.getData()) {
                Map<String, String> map = (HashMap) object;
                if ((map.containsKey("suoZhangId") && !map.get("suoZhangId").toString().equals("")) &&
                        (!map.containsKey("isNothing") || map.get("isNothing").toString().equals("否"))) {
                    users.add(new TaskExecutor(map.get("suoZhangId").toString(), map.get("suoZhang").toString()));
                }
            }

        } catch (Exception E) {
            logger.error(E.getMessage());
        }
        return users;
    }

    //..根据明细表里的所长获取所长，因为是第一次到找所长的节点，不进行无关判断，
    public Collection<TaskExecutor> getSuoZhangsWithOutIsNothing() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String key = formDataJson.getString("id");
        JSONObject params = new JSONObject();
        params.put("mainId", key);
        try {
            JsonResult result = SysSqlCustomQueryUtil.queryForJson("列出某个专利解读的明细的相关人员", params.toJSONString());
            for (Object object : (ArrayList) result.getData()) {
                Map<String, String> map = (HashMap) object;
                if ((map.containsKey("suoZhangId") && !map.get("suoZhangId").toString().equals(""))) {
                    users.add(new TaskExecutor(map.get("suoZhangId").toString(), map.get("suoZhang").toString()));
                }
            }

        } catch (Exception E) {
            logger.error(E.getMessage());
        }
        return users;
    }

    //..根据明细表里的创新人获取创新人
    public Collection<TaskExecutor> getChuangXinRens() throws Exception {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String key = formDataJson.getString("id");
        JSONObject params = new JSONObject();
        params.put("mainId", key);
        try {
            JsonResult result = SysSqlCustomQueryUtil.queryForJson("列出某个专利解读的明细的相关人员", params.toJSONString());
            for (Object object : (ArrayList) result.getData()) {
                Map<String, String> map = (HashMap) object;
                if ((map.containsKey("chuangXinRenId") && !map.get("chuangXinRenId").toString().equals("")) &&
                        (!map.containsKey("isNothing") || map.get("isNothing").toString().equals("否"))) {
                    users.add(new TaskExecutor(map.get("chuangXinRenId").toString(), map.get("chuangXinRen").toString()));
                }
            }
        } catch (Exception E) {
            logger.error(E.getMessage());
        }
        return users;
    }

    //..所有节点：以任何形式进入
    public void taskCreateScript(Map<String, Object> vars) {
        try {
            //@lwgkiller:阿诺多罗 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
            JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
            String activitiId = vars.get("activityId").toString();
            jsonObject.put("businessStatus", activitiId);
            if (activitiId.equalsIgnoreCase("B-assigning")) {
                //sendDingDing(jsonObject, jsonObject.getString("interpreterUserAssId"));
            } else if (activitiId.equalsIgnoreCase("C-interpreting")) {
                //sendDingDing(jsonObject, jsonObject.getString("interpreterUserId"));
            } else if (activitiId.equalsIgnoreCase("D-evaluating")) {
                //sendDingDing(jsonObject, jsonObject.getString("leaderUserId"));
            }
            patentInterpretationService.updateBusiness(jsonObject);
        } catch (Exception e) {
            logger.error("Exception in taskCreateScript", e);
        }
    }

    //..技术创新办公室主任审核中：以通过的形式结束,记录最终状态
    public void taskEndScript(Map<String, Object> vars) {
        try {
            //@lwgkiller:阿诺多罗 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
            JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
            String activitiId = vars.get("activityId").toString();
            if (activitiId.equalsIgnoreCase("B-assigning")) {//解读助手干完活，冗余存储一下人员
                jsonObject.put("interpreterUserAssId", ContextUtil.getCurrentUserId());
                jsonObject.put("interpreterUserAss", ContextUtil.getCurrentUser().getFullname());
            } else if (activitiId.equalsIgnoreCase("C-interpreting")) {//解读人干完活，冗余存储一下人员，其实没必要，这个是在表单里选的
                jsonObject.put("interpreterUserId", ContextUtil.getCurrentUserId());
                jsonObject.put("interpreterUser", ContextUtil.getCurrentUser().getFullname());
            } else if (activitiId.equalsIgnoreCase("D-evaluating")) {//牵头人干完活，冗余存储一下人员
                jsonObject.put("leaderUserId", ContextUtil.getCurrentUserId());
                jsonObject.put("leaderUser", ContextUtil.getCurrentUser().getFullname());
            } else if (activitiId.equalsIgnoreCase("H-reviewing2")) {
                jsonObject.put("businessStatus", "I-close");
            }
            patentInterpretationService.updateBusiness(jsonObject);
        } catch (Exception e) {
            logger.error("Exception in taskEndScript", e);
        }
    }

    //..应用价值是无
    public boolean isNotValuable(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        if (formDataJson.getString("applicationValue").equalsIgnoreCase("无")) {
            return true;
        } else {
            return false;
        }
    }

    //..应用价值不是无
    public boolean isValuable(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        if (!formDataJson.getString("applicationValue").equalsIgnoreCase("无")) {
            return true;
        } else {
            return false;
        }
    }

    //..
    public void sendDingDing(JSONObject jsonObject, String userIds) {
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("【专利解读任务通知】:名为 ");
        stringBuilder.append(jsonObject.getString("patentName"));
        stringBuilder.append(" 的专利解读任务需要你处理").append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
        noticeObj.put("content", stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon(userIds, noticeObj);
    }
}
