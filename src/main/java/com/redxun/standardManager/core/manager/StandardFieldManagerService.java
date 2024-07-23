package com.redxun.standardManager.core.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.standardManager.core.dao.StandardFieldManagerDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author zhangzhen
 */
@Service
public class StandardFieldManagerService {
    private static final Logger logger = LoggerFactory.getLogger(StandardFieldManagerService.class);
    @Autowired
    private StandardFieldManagerDao standardFieldManagerDao;

    // 查询专业领域并统计每个领域中的标准数量(启用状态的)
    public List<JSONObject> fieldStandardCountList(JSONObject params) {
        List<JSONObject> fieldList = queryFieldObject(params);
        if (fieldList == null || fieldList.isEmpty()) {
            return fieldList;
        }
        Set<String> fieldSet = new HashSet<>();
        for (JSONObject oneField : fieldList) {
            fieldSet.add(oneField.getString("fieldId"));
        }
        params.put("fieldIds", new ArrayList<>(fieldSet));
        params.put("standardStatus", "enable");
        List<JSONObject> standardNumGroupByField = standardFieldManagerDao.groupStandardByField(params);
        Map<String, Integer> fieldId2Num = new HashMap<>();
        for (JSONObject oneNum : standardNumGroupByField) {
            Integer standardNum =
                oneNum.getInteger("fieldStandardNum") == null ? 0 : oneNum.getInteger("fieldStandardNum");
            fieldId2Num.put(oneNum.getString("fieldId"), standardNum);
        }
        for (JSONObject oneField : fieldList) {
            String fieldId = oneField.getString("fieldId");
            oneField.put("fieldStandardNum", fieldId2Num.get(fieldId) == null ? 0 : fieldId2Num.get(fieldId));
        }
        return fieldList;
    }

    public List<JSONObject> queryFieldObject(JSONObject params) {
        List<JSONObject> fieldObjs = standardFieldManagerDao.queryFieldObject(params);
        if (fieldObjs == null || fieldObjs.isEmpty()) {
            return new ArrayList<>();
        }
        CommonFuns.convertDateJSON(fieldObjs);
        return fieldObjs;
    }

    public JSONObject deleteField(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            JSONObject params = new JSONObject();
            String ids = request.getParameter("ids");
            String[] idArr = ids.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            params.put("fieldIds", idList);
            standardFieldManagerDao.deleteField(params);
            standardFieldManagerDao.deleteStandardFieldRelaByField(params);
            standardFieldManagerDao.deleteUserFieldRelaByField(params);
        } catch (Exception e) {
            logger.error("Exception in deleteField", e);
            return ResultUtil.result(false, "系统异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }

    public JSONObject addField(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        String fieldId = IdUtil.getId();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            if (parameters == null || parameters.isEmpty()) {
                logger.error("表单内容为空！");
                return ResultUtil.result(false, "操作失败，表单内容为空！", "");
            }
            // 首先判断名称是否存在
            String fieldName = request.getParameter("fieldName");
            List<JSONObject> existSameNameFields = standardFieldManagerDao.queryFieldByName(fieldName);
            if (existSameNameFields != null && !existSameNameFields.isEmpty()) {
                return ResultUtil.result(false, "专业领域名称“" + fieldName + "”已经存在，请更换名称！", "");
            }
            // 提取数据
            JSONObject objBody = new JSONObject();
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = "";
                if (entry.getValue() != null && entry.getValue().length != 0) {
                    mapValue = entry.getValue()[0];
                }
                objBody.put(mapKey, mapValue);
            }
            objBody.put("fieldId", fieldId);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            standardFieldManagerDao.saveField(objBody);
        } catch (Exception e) {
            logger.error("Exception in add field", e);
            return ResultUtil.result(false, "系统异常！", "");
        }
        resultJson.put("id", fieldId);
        return ResultUtil.result(true, "保存成功", resultJson);
    }

    public JSONObject updateField(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            String id = request.getParameter("fieldId");
            // 首先判断用户组名称是否存在
            String fieldName = request.getParameter("fieldName");
            List<JSONObject> existSameNameFields = standardFieldManagerDao.queryFieldByName(fieldName);
            if (existSameNameFields != null && !existSameNameFields.isEmpty()
                && !id.equals(existSameNameFields.get(0).getString("fieldId"))) {
                return ResultUtil.result(false, "专业领域名称“" + fieldName + "”已经存在，请更换名称！", "");
            }
            // 提取数据
            JSONObject objBody = new JSONObject();
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = "";
                if (entry.getValue() != null && entry.getValue().length != 0) {
                    mapValue = entry.getValue()[0];
                }
                objBody.put(mapKey, mapValue);
            }

            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            standardFieldManagerDao.updateField(objBody);
        } catch (Exception e) {
            logger.error("Exception in update field", e);
            return ResultUtil.result(false, "系统异常！", "");
        }
        return ResultUtil.result(true, "更新成功", resultJson);
    }

    /**
     * 先删除该领域下对应的用户，然后再新插入
     *
     * @param result
     * @param gridDataStr
     * @return
     */
    public void fieldUserSave(JSONObject result, String gridDataStr, String fieldId) {
        List<String> idList = Arrays.asList(fieldId);
        JSONObject params = new JSONObject();
        params.put("fieldIds", idList);
        standardFieldManagerDao.deleteUserFieldRelaByField(params);
        if (StringUtils.isNotBlank(gridDataStr)) {
            JSONArray gridDataArr = JSONArray.parseArray(gridDataStr);
            List<JSONObject> gridDataList = gridDataArr.toJavaList(JSONObject.class);
            standardFieldManagerDao.batchInsertUserFieldRela(gridDataList);
        }

    }

    public JsonPageResult<?> fieldUserList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, true);
            if (!params.containsKey("sortField")) {
                params.put("sortField", "userDepName");
            }
            List<JSONObject> list = standardFieldManagerDao.queryFieldUserRela(params);
            int total = standardFieldManagerDao.countFieldUserRela(params);
            // 返回结果
            result.setData(list);
            result.setTotal(total);
        } catch (Exception e) {
            logger.error("Exception in query fieldUserList", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }
}
