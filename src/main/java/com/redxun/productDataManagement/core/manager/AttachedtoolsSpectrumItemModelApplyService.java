package com.redxun.productDataManagement.core.manager;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.StringUtil;
import com.redxun.productDataManagement.core.dao.AttachedtoolsSpectrumItemModelApplyDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class AttachedtoolsSpectrumItemModelApplyService {
    private static final Logger logger = LoggerFactory.getLogger(AttachedtoolsSpectrumItemModelApplyService.class);
    @Autowired
    private AttachedtoolsSpectrumItemModelApplyDao attachedtoolsSpectrumItemModelApplyDao;
    @Autowired
    private AttachedtoolsSpectrumService attachedtoolsSpectrumService;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, true);
        List<JSONObject> businessList = attachedtoolsSpectrumItemModelApplyDao.dataListQuery(params);
        // 查询当前处理人--会签不用这个
        xcmgProjectManager.setTaskCurrentUserJSON(businessList);
//        rdmZhglUtil.setTaskInfo2Data(businessList, ContextUtil.getCurrentUserId());
        int businessListCount = attachedtoolsSpectrumItemModelApplyDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public JsonResult deleteBusiness(String[] ids, String[] instIds) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> idList = Arrays.asList(ids);
        JSONObject params = new JSONObject();
        params.put("ids", idList);
        attachedtoolsSpectrumItemModelApplyDao.deleteBusiness(params);
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }

    //..
    public JSONObject getDataById(String id) {
        JSONObject result = new JSONObject();
        if (StringUtil.isEmpty(id)) {
            return result;
        }
        JSONObject jsonObject = attachedtoolsSpectrumItemModelApplyDao.getDataById(id);
        if (jsonObject == null) {
            return result;
        }
        return jsonObject;
    }

    //..
    public JsonResult saveBusiness(JSONObject jsonObject) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        if (StringUtil.isEmpty(jsonObject.getString("id"))) {
            this.createBusiness(jsonObject);
        } else {
            this.updateBusiness(jsonObject);
        }
        result.setData(jsonObject.getString("id"));
        return result;
    }

    //..
    public void createBusiness(JSONObject formData) {
        try {
            formData.put("id", IdUtil.getId());
            //@lwgkiller:此处是因为(草稿状态和空状态)无节点，提交后首节点会跳过，因此默认将首节点（编制中）的编号进行初始化写入
            formData.put("businessStatus", "A");
            formData.put("applyUserId", ContextUtil.getCurrentUserId());
            formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            formData.put("CREATE_TIME_", new Date());
            attachedtoolsSpectrumItemModelApplyDao.insertBusiness(formData);
        } catch (Exception e) {
            throw e;
        }
    }

    //..
    public void updateBusiness(JSONObject formData) {
        try {
            formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            formData.put("UPDATE_TIME_", new Date());
            attachedtoolsSpectrumItemModelApplyDao.updateBusiness(formData);
        } catch (Exception e) {
            throw e;
        }
    }

    //..
    public void checkDesignModelValid(JSONObject param, JsonResult result) {
        // 先检查设计型号申请表中有无重复数据
        List<JSONObject> temp = attachedtoolsSpectrumItemModelApplyDao.queryDesignModelValid(param);
        if (temp != null && !temp.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("当前已存在相同设计型号的申请，不允许重复申请！");
            return;
        }
        // 再检查型谱中有没有相同设计型号的数据
        param.remove("id");
        boolean productSpectrumExist = attachedtoolsSpectrumService.checkDesignModelExist(param);
        if (productSpectrumExist) {
            result.setSuccess(false);
            result.setMessage("属具型谱中已经存在该设计型号，不允许重复申请！");
            return;
        }
    }
}
