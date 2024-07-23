package com.redxun.serviceEngineering.core.service;

import java.util.*;

import com.redxun.saweb.context.ContextUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
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

/**
 * 再制造技术文件制作申请
 * 
 * @mh 2022年6月22日15:27:57
 */

@Service
public class JswjsqScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(JswjsqScript.class);

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    // 服务工程部门领导
    public Collection<TaskExecutor> fwLeaderInfos() {
        List<TaskExecutor> users = new ArrayList<>();
        Map<String, Object> leaderParams = new HashMap<>();
        leaderParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        leaderParams.put("groupName", "服务工程技术研究所");
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(leaderParams);
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    // 从表单获审核人员
    public Collection<TaskExecutor> getCheckUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String idStr = formDataJson.getString("checkerId");
        String nameStr = formDataJson.getString("checkerName");
        // 目前只有一个审核人员
        if (StringUtils.isNotEmpty(idStr) && StringUtils.isNotEmpty(nameStr)) {
            String[] ids = idStr.split(",");
            String[] names = nameStr.split(",");
            for (int i = 0; i < ids.length; i++) {
                TaskExecutor oneUser = new TaskExecutor(ids[i], names[i]);
                users.add(oneUser);
            }
        }
        return users;
    }

}
