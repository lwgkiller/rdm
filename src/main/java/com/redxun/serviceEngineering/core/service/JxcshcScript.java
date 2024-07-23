package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.serviceEngineering.core.dao.JxcshcDao;
import com.redxun.serviceEngineering.core.dao.WrongPartsDao;
import com.redxun.sys.org.dao.OsUserDao;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JxcshcScript implements GroovyScript {

    private Logger logger = LoggerFactory.getLogger(JxcshcScript.class);

    @Autowired
    private JxcshcDao jxcshcDao;

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;


    // 获取批准人员
    public Collection<TaskExecutor> getPzry() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String applyUserId = formDataJson.getString("CREATE_BY_");
        if(StringUtils.isBlank(applyUserId)) {
            applyUserId=ContextUtil.getCurrentUserId();
        }
        JSONObject dept = jxcshcDao.queryCreatorDep(applyUserId);
        String groupName = dept.getString("groupName");
        Map<String, Object> roleParams = new HashMap<>();
        roleParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        if (groupName.equals(RdmConst.ZLBZB_NAME)) {
            roleParams.put("groupName", "质保部测试数据回传批准人员");
        } else if (groupName.equals("测试研究所")) {
            roleParams.put("groupName", "测试所测试数据回传批准人员");
        }
        List<Map<String, String>> userInfos = xcmgProjectOtherDao.queryUserByGroupName(roleParams);
        if (userInfos != null && !userInfos.isEmpty()) {
            for (Map<String, String> depRespMan : userInfos) {
                users.add(new TaskExecutor(depRespMan.get("USER_ID_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

}
