package com.redxun.standardManager.core.manager;

import java.util.*;

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
import com.redxun.saweb.context.ContextUtil;
import com.redxun.standardManager.core.util.StandardConstant;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;

// 项目流程节点的触发事件
@Service
public class StandardFlowConfigScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(StandardFlowConfigScript.class);
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    /**
     * 获取标准预览或者下载的审批人员，按照申请标准的体系类别进行相应群组审批人员的查询
     * 
     * @return
     */
    public Collection<TaskExecutor> toGetStandardManager() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.error("formData is blank");
            return users;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String systemCategoryId = formDataJson.getString("systemCategoryId");
        if (StringUtils.isBlank(systemCategoryId)) {
            logger.error("systemCategoryId is blank");
            return users;
        }
        String groupName = "";
        if (StandardConstant.SYSTEMCATEGORY_GL.equalsIgnoreCase(systemCategoryId)) {
            groupName = "管理标准审批人员";
        } else if (StandardConstant.SYSTEMCATEGORY_JS.equalsIgnoreCase(systemCategoryId)) {
            groupName = "技术标准审批人员";
        } else if (StandardConstant.SYSTEMCATEGORY_NK.equalsIgnoreCase(systemCategoryId)) {
            groupName = "内控标准审批人员";
        }
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", groupName);
        List<Map<String, String>> userInfos = xcmgProjectOtherDao.queryUserByGroupName(params);
        if (userInfos != null && !userInfos.isEmpty()) {
            for (Map<String, String> oneUser : userInfos) {
                users.add(new TaskExecutor(oneUser.get("USER_ID_"), oneUser.get("FULLNAME_")));
            }
        }
        return users;
    }
}
