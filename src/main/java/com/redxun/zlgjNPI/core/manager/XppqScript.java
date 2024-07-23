package com.redxun.zlgjNPI.core.manager;

import java.util.*;

import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import org.apache.commons.lang.StringUtils;
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
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.zlgjNPI.core.dao.XppqDao;

@Service
public class XppqScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(ZlgjHandler.class);
    @Autowired
    private CommonInfoDao commonInfoDao;
    @Autowired
    private XppqService xppqService;
    @Autowired
    private XppqDao xppqDao;

    /**
     * 工艺工程师
     */
    public Collection<TaskExecutor> getGygcs() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        users.add(new TaskExecutor(formDataJson.getString("gygcsId"), formDataJson.getString("gygcsName")));
        return users;
    }

    // 查询指定分厂的质量兼职人员(没有剖切实际完成日期的那一条)
    public Collection<TaskExecutor> getZljzry() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONArray xppqProcessArr = formDataJson.getJSONArray("SUB_xppqProcess");
        String pqfcId = "";
        if (xppqProcessArr != null && !xppqProcessArr.isEmpty()) {
            for (int index = 0; index < xppqProcessArr.size(); index++) {
                String ifGd = xppqProcessArr.getJSONObject(index).getString("ifGd");
                if (!"yes".equalsIgnoreCase(ifGd)) {
                    pqfcId = xppqProcessArr.getJSONObject(index).getString("pqfcId");
                    break;
                }
            }
        }
        if (StringUtils.isNotBlank(pqfcId)) {
            Map<String, Object> param = new HashMap<>();
            param.put("deptId", pqfcId);
            param.put("REL_TYPE_KEY_", "GROUP-USER-ZLJZRY");
            List<Map<String, String>> userInfos = commonInfoDao.queryUserByGroupId(param);
            if (userInfos != null && !userInfos.isEmpty()) {
                for (Map<String, String> user : userInfos) {
                    users.add(new TaskExecutor(user.get("USER_ID_"), user.get("FULLNAME_")));
                }
            }
        }
        return users;
    }

    /**
     * 工艺工程师组织评审后的驳回
     */
    public void zzpsBack(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        xppqService.updateZlgj(formDataJson);
        // 设置所有执行数据为归档
        Map<String, Object> param = new HashMap<>();
        param.put("wtId", formDataJson.getString("wtId"));
        xppqDao.updateProcessStatus(param);
    }
}
