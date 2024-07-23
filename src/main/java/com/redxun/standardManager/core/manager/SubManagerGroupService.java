package com.redxun.standardManager.core.manager;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.StringUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.standardManager.core.dao.SubManagerGroupDao;
import com.redxun.standardManager.core.dao.SubManagerUserDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * @author zhangzhen
 */
@Service
public class SubManagerGroupService {
    private static final Logger logger = LoggerFactory.getLogger(SubManagerGroupService.class);
    @Autowired
    private SubManagerGroupDao subManagerGroupDao;
    @Autowired
    private SubManagerUserDao subManagerUserDao;

    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            if (!params.containsKey("sortField")) {
                params.put("sortField", "systemCategoryId,systemId,CREATE_TIME_");
            }
            list = subManagerGroupDao.query(params);
            CommonFuns.convertDate(list);

            params.remove("currentIndex");
            params.remove("pageSize");
            totalList = subManagerGroupDao.query(params);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in query groupList", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }

    public JSONObject add(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        String id = IdUtil.getId();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            if (parameters == null || parameters.isEmpty()) {
                logger.error("表单内容为空！");
                return ResultUtil.result(false, "操作失败，表单内容为空！", "");
            }
            // 首先判断用户组名称是否存在
            String groupName = request.getParameter("groupName");
            JSONObject paramJson = new JSONObject();
            paramJson.put("groupName", groupName);
            JSONObject groupJson = subManagerGroupDao.getObjectByName(paramJson);
            if (groupJson != null) {
                return ResultUtil.result(false, "用户组“" + groupName + "”已经存在，请更换名称！", "");
            }
            // 提取数据
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = "";
                if (entry.getValue() != null && entry.getValue().length != 0) {
                    mapValue = entry.getValue()[0];
                }
                objBody.put(mapKey, mapValue);
            }
            String recUser = StringUtil.nullToString(objBody.get("recUser"));
            if (StringUtil.isNotEmpty(recUser)) {
                String userIdArray[] = recUser.split(",", -1);
                Map<String, Object> paramMap = new HashMap<>(16);
                for (String userId : userIdArray) {
                    paramMap.put("groupId", id);
                    paramMap.put("userId", userId);
                    paramMap.put("id", IdUtil.getId());
                    paramMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    paramMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    paramMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                    subManagerUserDao.add(paramMap);
                }
            }
            objBody.put("id", id);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            subManagerGroupDao.add(objBody);
        } catch (Exception e) {
            logger.error("Exception in add group", e);
            return ResultUtil.result(false, "系统异常！", "");
        }
        resultJson.put("id", id);
        return ResultUtil.result(true, "保存成功", resultJson);
    }

    public JSONObject update(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            String id = request.getParameter("id");
            // 首先判断用户组名称是否存在
            String groupName = request.getParameter("groupName");
            JSONObject paramJson = new JSONObject();
            paramJson.put("groupName", groupName);
            JSONObject groupJson = subManagerGroupDao.getObjectByName(paramJson);
            if (groupJson != null && !id.equals(groupJson.getString("id"))) {
                return ResultUtil.result(false, "用户组“" + groupName + "”已经存在，请更换名称！", "");
            }
            // 提取数据
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = "";
                if (entry.getValue() != null && entry.getValue().length != 0) {
                    mapValue = entry.getValue()[0];
                }
                objBody.put(mapKey, mapValue);
            }

            // 人员处理先删除，再新增
            subManagerUserDao.deleteByGroupId(id);
            // 插入人员
            String recUser = StringUtil.nullToString(objBody.get("recUser"));
            if (StringUtil.isNotEmpty(recUser)) {
                String userIdArray[] = recUser.split(",");
                Map<String, Object> paramMap = new HashMap<>(16);
                for (String userId : userIdArray) {
                    paramMap.put("groupId", id);
                    paramMap.put("userId", userId);
                    paramMap.put("id", IdUtil.getId());
                    paramMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    paramMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    paramMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                    subManagerUserDao.add(paramMap);
                }
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            subManagerGroupDao.update(objBody);
        } catch (Exception e) {
            logger.error("Exception in update group", e);
            return ResultUtil.result(false, "系统异常！", "");
        }
        return ResultUtil.result(true, "更新成功", resultJson);
    }

    public JSONObject remove(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String ids = request.getParameter("ids");
            String[] idArr = ids.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            params.put("ids", idList);
            int delStatus = subManagerGroupDao.batchGroupDelete(params);
            if (delStatus == 1) {
                subManagerGroupDao.batchGroupUserDelete(params);
            }
        } catch (Exception e) {
            logger.error("Exception in remove", e);
            return ResultUtil.result(false, "系统异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }

    public JSONObject getObject(JSONObject jsonObject) {
        String id = jsonObject.getString("id");
        if (StringUtil.isEmpty(id)) {
            return new JSONObject();
        }
        // 获取用户组的信息
        JSONObject resultJSON = subManagerGroupDao.getObject(jsonObject);
        // 根据groupId 获取人员列表
        Map<String, Object> params = new HashMap<>(16);
        params.put("groupId", id);
        List<Map<String, Object>> list = subManagerUserDao.query(params);
        String recUser = "";
        String recUserNames = "";
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> map : list) {
                recUser += StringUtil.nullToString(map.get("userId")) + ",";
                recUserNames += StringUtil.nullToString(map.get("userName")) + ",";
            }
        }
        if (recUser.length() > 1) {
            recUser = recUser.substring(0, recUser.length() - 1);
        }
        if (recUserNames.length() > 1) {
            recUserNames = recUserNames.substring(0, recUserNames.length() - 1);
        }
        resultJSON.put("recUser", recUser);
        resultJSON.put("recUserNames", recUserNames);
        return resultJSON;
    }
}
