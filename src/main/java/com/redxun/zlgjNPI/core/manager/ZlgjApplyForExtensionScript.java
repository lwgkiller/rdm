package com.redxun.zlgjNPI.core.manager;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.sys.org.manager.OsUserManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.zlgjNPI.core.dao.ZlgjApplyForExtensionDao;
import com.redxun.zlgjNPI.core.dao.ZlgjWTDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ZlgjApplyForExtensionScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(ZlgjHandler.class);
    @Autowired
    private ZlgjApplyForExtensionDao zlgjApplyForExtensionDao;
    @Autowired
    private ZlgjWTDao zlgjWTDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Resource
    CommonInfoDao commonInfoDao;
    @Resource
    CommonInfoManager commonInfoManager;
    @Autowired
    private OsUserManager osUserManager;

    //获取质量改进的创建人
    public Collection<TaskExecutor> getZlglCreator() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONObject zlgj = zlgjWTDao.queryZlgjById(formDataJson.getString("wtId"));
        String userId = "";
        userId = zlgj.getString("CREATE_BY_");
        OsUser osUser = osUserManager.get(userId);
        users.add(new TaskExecutor(osUser.getUserId(), osUser.getFullname()));
        return users;
    }

    //是新品路试
    public boolean isXPLS(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONObject zlgj = zlgjWTDao.queryZlgjById(formDataJson.getString("wtId"));
        if (zlgj.getString("wtlx").equalsIgnoreCase(ZlgjConstant.XPLS)) {
            return true;
        }
        return false;
    }

    //不是新品路试
    public boolean isNotXPLS(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONObject zlgj = zlgjWTDao.queryZlgjById(formDataJson.getString("wtId"));
        if (!zlgj.getString("wtlx").equalsIgnoreCase(ZlgjConstant.XPLS)) {
            return true;
        }
        return false;
    }
}
