package com.redxun.info.core.manager;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.core.script.GroovyScript;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

// 项目流程节点的触发事件
@Service
public class QbgzScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(QbgzScript.class);

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;


    public boolean noFg(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        cmd =(AbstractExecutionCmd) ProcessHandleHelper.getProcessCmd();
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
        String qbLevel = formDataJson.getString("approve");
        if("1".equals(qbLevel)||"2".equals(qbLevel)||"3".equals(qbLevel)){
            return false;
        }
        return true;
    }

    public boolean yesFg(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String qbLevel = formDataJson.getString("qbLevel");
        if("4".equals(qbLevel)||"5".equals(qbLevel)){
            return true;
        }
        return false;
    }
}
