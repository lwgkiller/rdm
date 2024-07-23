
package com.redxun.xcmgProjectManager.core.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.redxun.productDataManagement.core.dao.AttachedtoolsSpectrumDao;
import com.redxun.standardManager.core.util.ResultUtil;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectAPIDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Service
public class XcmgProjectAPIManager {
    @Autowired
    private XcmgProjectAPIDao xcmgProjectAPIDao;
    @Autowired
    private AttachedtoolsSpectrumDao attachedtoolsSpectrumDao;

    /**
     * 职业发展平台获取项目经验。通过请求体中的身份证号，找到userId，查询项目
     *
     * @param result
     * @param postData
     */
    public void xcmghrGetProjects(JSONObject result, String postData) {
        JSONObject postObj = JSONObject.parseObject(postData);
        /*   if (postObj.isEmpty() || StringUtils.isBlank(postObj.getString("certNo"))) {
            logger.error("certNo is blank!");
            result.put("result", false);
            result.put("message", "身份证号为空！");
            return;
        }*/
        String certNo = postObj.getString("certNo");
        String projectName = postObj.getString("projectName");
        String projectId = postObj.getString("projectId");
        Map<String, Object> params = new HashMap<>();
        params.put("certNo", certNo);
        params.put("projectName", projectName);
        params.put("projectId", projectId);
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, String>> projects = xcmgProjectAPIDao.xcmghrGetProjects(params);
        JSONArray projectArray = XcmgProjectUtil.convertListMap2JsonArrString(projects);
        result.put("result", true);
        result.put("data", projectArray);
    }

    public JSONObject getApiList(JSONObject result, String searchValue) {

        // 以searchValue为查询条件 返回查询到的列表
        String resUrlBase =
                "http://wjrdm.xcmg.com/rdm/xcmgProjectManager/core/xcmgProject/edit.do?action=detail&detailHideRoleRatio=false&projectId=";
        List<JSONObject> resList = xcmgProjectAPIDao.queryApiList(searchValue);
        for (JSONObject oneRes : resList) {
            oneRes.put("url", resUrlBase + oneRes.getString("projectId"));
        }
        if (resList.size() > 0) {
            result.put("result", resList);
        } else {
            result.put("success", false);
            result.put("message", "无查询结果，请检查搜索条件！");
        }
        return result;
    }

    public JSONObject getCpxhApiList(JSONObject result, String searchValue) {
        Map<String, JSONObject> temp = new HashedMap();//由于产品型谱里面有部分属具型谱未清除，因此需要清除
        // 以searchValue为查询条件 返回查询到的列表
        List<JSONObject> resList = xcmgProjectAPIDao.queryCpxhApiList(searchValue);
        for (JSONObject jsonObject : resList) {
            temp.put(jsonObject.getString("productName"), jsonObject);
        }
        //属具型谱
        JSONObject params = new JSONObject().fluentPut("designModel", searchValue);
        List<JSONObject> attachedtools = attachedtoolsSpectrumDao.itemListQuery(params).stream()
                .map(jsonObject -> {
                    JSONObject newJsonObject = new JSONObject();
                    newJsonObject.put("id", jsonObject.get("id"));
                    newJsonObject.put("productName", jsonObject.get("designModel"));
                    return newJsonObject;
                }).collect(Collectors.toList());
        for (JSONObject attachedtool : attachedtools) {
            temp.put(attachedtool.getString("productName"), attachedtool);
        }
        resList = new ArrayList<>(temp.values());
        if (resList.size() > 0) {
            result.put("result", resList);
        } else {
            result.put("success", false);
            result.put("message", "无查询结果，请检查搜索条件！");
        }
        return result;
    }

    public JSONObject getProductModelList(JSONObject paramJson) {
        Map<String, JSONObject> temp = new HashedMap();//由于产品型谱里面有部分属具型谱未清除，因此需要清除
        List<JSONObject> resList = new ArrayList<>();
        try {
            resList = xcmgProjectAPIDao.queryProductApiList(paramJson);
            for (JSONObject jsonObject : resList) {
                temp.put(jsonObject.getString("designModel"), jsonObject);
            }
            //属具型谱
            List<JSONObject> attachedtools = attachedtoolsSpectrumDao.itemListQuery(paramJson).stream()
                    .map(jsonObject -> {
                        JSONObject newJsonObject = new JSONObject();
                        newJsonObject.put("id", jsonObject.get("id"));
                        newJsonObject.put("designModel", jsonObject.get("designModel"));
                        newJsonObject.put("saleModel", jsonObject.get("salesModel"));
                        newJsonObject.put("materialCode", jsonObject.get("materialCode"));
                        return newJsonObject;
                    }).collect(Collectors.toList());
            for (JSONObject attachedtool : attachedtools) {
                temp.put(attachedtool.getString("designModel"), attachedtool);
            }
            resList = new ArrayList<>(temp.values());
        } catch (Exception e) {
            return ResultUtil.result(false, "查询产品型谱信息失败！", resList);
        }
        return ResultUtil.result(true, "查询产品型谱信息成功！", resList);
    }

}
