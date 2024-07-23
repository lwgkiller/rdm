package com.redxun.productDataManagement.core.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.sys.org.manager.OsUserManager;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AttachedtoolsSpectrumItemModelApplyScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(AttachedtoolsSpectrumItemModelApplyScript.class);
    @Autowired
    private AttachedtoolsSpectrumItemModelApplyService attachedtoolsSpectrumItemModelApplyService;
    @Autowired
    private AttachedtoolsSpectrumService attachedtoolsSpectrumService;

    //..
    public void taskCreateScript(Map<String, Object> vars) throws Exception {
        try {
            //@lwgkiller:阿诺多罗 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
            JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
            String activitiId = vars.get("activityId").toString();
            jsonObject.put("businessStatus", activitiId);
            attachedtoolsSpectrumItemModelApplyService.updateBusiness(jsonObject);
        } catch (Exception e) {
            ProcessHandleHelper.addErrorMsg(e.getMessage());
            throw e;
        }
    }

    //..
    public void taskEndScript(Map<String, Object> vars) throws Exception {
        try {
            //@lwgkiller:阿诺多罗 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
            JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
            String activitiId = vars.get("activityId").toString();
            if (activitiId.equalsIgnoreCase("A")) {//第一个编辑节点通过后
                jsonObject.put("applyTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
            } else if (activitiId.equalsIgnoreCase("E")) {
                //更新最终状态
                jsonObject.put("businessStatus", "Z");
                //创建对应的属具型谱明细
                attachedtoolsSpectrumService.createItemByApply(jsonObject);
            }
            attachedtoolsSpectrumItemModelApplyService.updateBusiness(jsonObject);
        } catch (Exception e) {
            ProcessHandleHelper.addErrorMsg(e.getMessage());
            throw e;
        }
    }
}
