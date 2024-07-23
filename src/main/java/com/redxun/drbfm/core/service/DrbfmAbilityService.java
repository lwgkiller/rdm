package com.redxun.drbfm.core.service;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.drbfm.core.dao.DrbfmAbilityDao;
import com.redxun.drbfm.core.dao.DrbfmTestTaskDao;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;

@Service
public class DrbfmAbilityService {
    private Logger logger = LoggerFactory.getLogger(DrbfmAbilityService.class);
    @Autowired
    private DrbfmAbilityDao drbfmAbilityDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private DrbfmTestTaskDao drbfmTestTaskDao;

    public JsonPageResult<?> getAbilityList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "drbfm_verifyAbilityList.respDeptId,CONVERT(abilityName using GBK)",
            null);
        rdmZhglUtil.addPage(request, params);
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    params.put(name, value);
                }
            }
        }
        List<JSONObject> abilityList = drbfmAbilityDao.queryAbilityList(params);
        for (Map<String, Object> oneData : abilityList) {
            if (oneData.get("CREATE_TIME_") != null) {
                oneData.put("CREATE_TIME_", DateUtil.formatDate((Date)oneData.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (oneData.get("UPDATE_TIME_") != null) {
                oneData.put("UPDATE_TIME_", DateUtil.formatDate((Date)oneData.get("UPDATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        result.setData(abilityList);
        int total = drbfmAbilityDao.countAbilityList(params);
        result.setTotal(total);
        return result;
    }

    /**
     * 需要判断是否有关联的验证任务，有的话不允许删除
     * 
     * @param ids
     * @return
     */
    public JsonResult deleteAbility(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        if (ids == null || ids.length == 0) {
            return result;
        }
        // 判断有无关联的验证任务
        Map<String, Object> params = new HashMap<>();
        List<String> idList = new ArrayList<>(Arrays.asList(ids));
        params.put("verifyAbilityIds", idList);
        List<Map<String, Object>> testTaskList = drbfmTestTaskDao.queryTestTaskList(params);
        Set<String> relTestTaskIdSet = new HashSet<>();
        for (Map<String, Object> oneTestTask : testTaskList) {
            relTestTaskIdSet.add(oneTestTask.get("verifyAbilityId").toString());
        }
        idList.removeAll(relTestTaskIdSet);
        if (idList.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("要删除的验证能力已关联验证任务，不能删除！");
            return result;
        }

        drbfmAbilityDao.deleteAbility(params);
        return result;
    }

    public void saveAbility(JsonResult result, JSONObject formJSON) {
        String formId = formJSON.getString("id");
        if (StringUtils.isBlank(formId)) {
            // 新增基本信息
            formId = IdUtil.getId();
            formJSON.put("id", formId);
            formJSON.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            formJSON.put("CREATE_TIME_", new Date());
            drbfmAbilityDao.createAbility(formJSON);
        } else {
            // 更新基本信息
            formJSON.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            formJSON.put("UPDATE_TIME_", new Date());
            drbfmAbilityDao.updateAbility(formJSON);
        }
        result.setData(formId);
    }

}
