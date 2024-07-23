package com.redxun.bjjszc.core.service;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bjjszc.core.dao.KfsqDao;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.JsmmDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;

// 项目流程节点的触发事件
@Service
public class JsxxScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(JsxxScript.class);

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    public List<Map<String, String>> queryFwsRespUser() {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", RdmConst.FWGCS_NAME);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        return depRespMans;
    }

    public Collection<TaskExecutor> getFwsRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        List<Map<String, String>> depRespMans = queryFwsRespUser();
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }


}
