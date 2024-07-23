package com.redxun.rdmZhgl.core.service;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmZhgl.core.dao.YdgztbDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;


// 项目流程节点的触发事件
@Service
public class YdgztbScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(YdgztbScript.class);

    @Autowired
    private CommonInfoDao commonInfoDao;
    @Autowired
    private YdgztbDao ydgztbDao;

    // 从表单获取创建人，获取其部门负责人,如无返回管理员
    public Collection<TaskExecutor> getManagerUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String userId = formDataJson.getString("CREATE_BY_");
        List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(userId);
        if (deptResps != null && !deptResps.isEmpty()) {
            for (JSONObject depRespMan : deptResps) {
                users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
            }
        } else {
            users.add(new TaskExecutor("1", "管理员"));
        }
        return users;
    }
    // 提交时比对当前时间和 年月
    public boolean isOverTimeYes(AbstractExecutionCmd cmd) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String yearMonth = formDataJson.getString("yearMonth");
        String currentYearMonth = XcmgProjectUtil.getNowLocalDateStr("yyyy-MM");
        String currentDay = XcmgProjectUtil.getNowLocalDateStr("dd");
        //年月一致，日期小于等于26则未超时
        if (currentYearMonth.equalsIgnoreCase(yearMonth) && currentDay.compareTo("26") < 0) {
            return false;
        } else{
            return true;
        }
    }

    // 提交时比对当前时间和 年月
    public boolean isOverTimeNo(AbstractExecutionCmd cmd) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String yearMonth = formDataJson.getString("yearMonth");
        String currentYearMonth = XcmgProjectUtil.getNowLocalDateStr("yyyy-MM");
        String currentDay = XcmgProjectUtil.getNowLocalDateStr("dd");
        //年月一致，日期小于等于26则未超时
        if (currentYearMonth.equalsIgnoreCase(yearMonth) && currentDay.compareTo("26") < 0) {
            return true;
        } else{
            return false;
        }
    }

    public void setOverTimeTrue(AbstractExecutionCmd cmd) {
        JSONObject params = new JSONObject();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String id = formDataJson.getString("id");
        params.put("id", id);
        params.put("isOverTime", "yes");
        ydgztbDao.setOverTime(params);
    }

    public void setOverTimeFalse(AbstractExecutionCmd cmd) {
        JSONObject params = new JSONObject();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String id = formDataJson.getString("id");
        params.put("id", id);
        params.put("isOverTime", "no");
        ydgztbDao.setOverTime(params);
    }



}
