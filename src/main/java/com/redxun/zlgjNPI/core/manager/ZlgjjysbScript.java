package com.redxun.zlgjNPI.core.manager;

import java.util.*;

import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.saweb.context.ContextUtil;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.zlgjNPI.core.dao.ZlgjjysbDao;

@Service
public class ZlgjjysbScript implements GroovyScript {

    private Logger logger = LoggerFactory.getLogger(ZlgjjysbScript.class);

    @Autowired
    private ZlgjjysbDao zlgjjysbDao;

    @Autowired
    private CommonInfoDao commonInfoDao;

    @Autowired
    private ZlgjjysbService zlgjjysbService;

    // 查询本部门的质量兼职人员
    public Collection<TaskExecutor> queryQualityPerson() {
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        List<TaskExecutor> users = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("deptId", formDataJson.getString("applicationUnitId"));
        param.put("REL_TYPE_KEY_", "GROUP-USER-ZLJZRY");
        List<Map<String, String>> userInfos = commonInfoDao.queryUserByGroupId(param);
        if (userInfos != null && !userInfos.isEmpty()) {
            for (Map<String, String> user : userInfos) {
                users.add(new TaskExecutor(user.get("USER_ID_"), user.get("FULLNAME_")));
            }
        }
        return users;
    }

    // 室主任
    public Collection<TaskExecutor> getSectionChief() {
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        List<TaskExecutor> users = new ArrayList<>();
        String sectionChiefId = formDataJson.getString("sectionChiefId");
        String sectionChief = formDataJson.getString("sectionChief");
        String[] sectionChiefIds = sectionChiefId.split(",");
        String[] sectionChiefs = sectionChief.split(",");
        for (int i = 0; i < sectionChiefIds.length; i++) {
            String userId = sectionChiefIds[i];
            String userName = sectionChiefs[i];
            users.add(new TaskExecutor(userId, userName));
        }
        return users;
    }

    // 对策人员
    public Collection<TaskExecutor> getDcry() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String jsonData = cmd.getJsonData();
        JSONObject formData = JSONObject.parseObject(jsonData);
        JSONArray dcryGrid = formData.getJSONArray("SUB_dcryGrid");
        for (int i = 0; i < dcryGrid.size(); i++) {
            JSONObject dcryGridObject = dcryGrid.getJSONObject(i);
            String dcryId = dcryGridObject.getString("dcryId");
            String dcryName = dcryGridObject.getString("dcryId_name");
            users.add(new TaskExecutor(dcryId, dcryName));
        }
        return users;
    }

    // 等级评定人员
    public Collection<TaskExecutor> getDjpdry() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String jsonData = cmd.getJsonData();
        JSONObject formData = JSONObject.parseObject(jsonData);
        String applicationUnit = formData.getString("applicationUnit");
        String wtdl = formData.getString("wtdl");
        String jxlb = formData.getString("jxlb");
        JSONObject param = new JSONObject();
        param.put("jytcbm", applicationUnit);
        param.put("dl", wtdl);
        param.put("jxlb", jxlb);
        List<JSONObject> djpdry = zlgjjysbDao.getDjpdry(param);
        if (djpdry != null && !djpdry.isEmpty()) {
            users.add(new TaskExecutor(djpdry.get(0).getString("pdryId"), djpdry.get(0).getString("pdry")));
        }
        return users;
    }

    //..
    public void taskEndScript(Map<String, Object> vars) throws Exception {
        String businessId = vars.get("busKey").toString();
        List<JSONObject> list = zlgjjysbDao.queryDcry(businessId);
        StringBuilder stringBuilder = new StringBuilder();
        for (JSONObject dcry : list) {
            stringBuilder.append("对策人员：")
                    .append(dcry.getString("dcryName"))
                    .append("，")
                    .append("实施状态：")
                    .append(getImplementationStatus(dcry.getString("implementationStatus")))
                    .append("，")
                    .append("实施内容描述：")
                    .append(dcry.getString("ssContent"))
                    .append("，")
                    .append("审核意见：")
                    .append(dcry.getString("auditSuggest")).append(";");
        }
        String zlshshyjMock = stringBuilder.substring(0, stringBuilder.length() - 1);
        JSONObject zlgjjysbDetail = zlgjjysbService.getZlgjjysbDetail(businessId);
        String rating = zlgjjysbDetail.getString("rating");
        String gjssqryj = zlgjjysbDetail.getString("gjssqryj");
        gjssqryj = "实施确认意见："+gjssqryj;
        JsonResult jsonResult = zlgjjysbService.doCallBackOutSystemFromzlgjjysb(businessId, 1, zlshshyjMock, rating,gjssqryj);
        if (!jsonResult.getSuccess()) {
            throw new Exception(jsonResult.getMessage());
        }
    }

    private String getImplementationStatus(String key) {
        Map<String, String> map = new HashedMap();
        map.put("ljss", "立即实施");
        map.put("xydjxss", "下一代机型实施");
        map.put("yss", "已实施");
        return map.get(key);
    }

	// 质量审核人员
    public Collection<TaskExecutor> getChecker() {
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        List<TaskExecutor> users = new ArrayList<>();
        String checkId = formDataJson.getString("checkId");
        String checkName = formDataJson.getString("checkName");
        String[] checkIds = checkId.split(",");
        String[] checkNames = checkName.split(",");
        for (int i = 0; i < checkIds.length; i++) {
            String userId = checkIds[i];
            String userName = checkNames[i];
            users.add(new TaskExecutor(userId, userName));
        }
        return users;
    }
}
