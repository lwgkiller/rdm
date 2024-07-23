package com.redxun.rdmZhgl.core.service;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.redxun.rdmZhgl.core.dao.ProductConfigDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.sys.org.dao.OsUserDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * @author zhangzhen
 */
@Service
public class ProductConfigService {
    private static final Logger logger = LoggerFactory.getLogger(ProductConfigService.class);
    @Resource
    ProductConfigDao productConfigDao;
    @Autowired
    OsUserDao osUserDao;


    public List<Map<String, Object>> query(HttpServletRequest request) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Map<String, Object> params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            result = productConfigDao.query(params);
            convertDate(result);
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
        }
        return result;
    }
    public JSONObject add(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            if (parameters == null || parameters.isEmpty()) {
                logger.error("表单内容为空！");
                return ResultUtil.result(false, "操作失败，表单内容为空！", "");
            }
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                objBody.put(mapKey, mapValue);
            }
            String id = IdUtil.getId();
            objBody.put("id", id);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("deptId", ContextUtil.getCurrentUser().getMainGroupId());
            resultJson.put("id",id);
            productConfigDao.addObject(objBody);
        } catch (Exception e) {
            logger.error("Exception in add 添加异常！", e);
            return ResultUtil.result(false, "添加异常！", "");
        }
        return ResultUtil.result(true, "新增成功", resultJson);
    }
    public JSONObject update(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                objBody.put(mapKey, mapValue);
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            productConfigDao.updateObject(objBody);
            resultJson.put("id",objBody.get("id"));
        } catch (Exception e) {
            logger.error("Exception in update 更新异常", e);
            return ResultUtil.result(false, "更新异常！", "");
        }
        return ResultUtil.result(true, "更新成功", resultJson);
    }
    public void saveOrUpdateItem(String changeGridDataStr) {
        JSONObject resultJson = new JSONObject();
        try {
            JSONArray changeGridDataJson = JSONObject.parseArray(changeGridDataStr);
            for(int i=0;i<changeGridDataJson.size();i++) {
                JSONObject oneObject=changeGridDataJson.getJSONObject(i);
                String state=oneObject.getString("_state");
                String id=oneObject.getString("id");
                if("added".equals(state)|| StringUtils.isBlank(id)) {
                    //新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    //根据负责人id 获取对应部门
                    productConfigDao.addItem(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    productConfigDao.updateItem(oneObject);
                }else if ("removed".equals(state)) {
                    // 删除
                    productConfigDao.delItem(oneObject.getString("id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("保存信息异常",e);
            return;
        }
    }
    public JSONObject remove(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String ids = request.getParameter("ids");
            String[] idArr = ids.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            params.put("ids", idList);
            productConfigDao.batchDelete(params);
            productConfigDao.batchDeleteItems(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除科技项目", e);
            return ResultUtil.result(false, "删除科技项目异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }
    public JSONObject getObjectById(String id){
        JSONObject jsonObject =  productConfigDao.getObjectById(id);
        return jsonObject;
    }
    public List<JSONObject> getItemList(HttpServletRequest request) {
        List<JSONObject>  resultArray = new ArrayList<>();
        try {
            String mainId = request.getParameter("mainId");
            JSONObject paramJson = new JSONObject();
            paramJson.put("mainId",mainId);
            resultArray = productConfigDao.getItemList(paramJson);
        } catch (Exception e) {
            logger.error("Exception in getItemList", e);
        }
        return resultArray;
    }
    public List<JSONObject> getDeliverList(HttpServletRequest request) {
        List<JSONObject>  resultArray = new ArrayList<>();
        try {
            String categoryId = request.getParameter("categoryId");
            JSONObject paramJson = new JSONObject();
            paramJson.put("categoryId",categoryId);
            resultArray = productConfigDao.getDeliverList(paramJson);
        } catch (Exception e) {
            logger.error("Exception in getDeliverList", e);
        }
        return resultArray;
    }

    public static void convertDate(List<Map<String, Object>> list) {
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> oneApply : list) {
                for (String key : oneApply.keySet()) {
                    if (key.endsWith("_TIME_") || key.endsWith("_time") || key.endsWith("_date")) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd  HH:mm:ss"));
                        }
                    }
                }
            }
        }
    }

}
