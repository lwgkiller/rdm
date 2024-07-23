package com.redxun.info.core.manager;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.redxun.info.core.dao.InfoAuthConfigDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
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
public class InfoAuthConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(InfoAuthConfigManager.class);
    @Autowired
    OsUserDao osUserDao;
    @Resource
    InfoAuthConfigDao infoAuthConfigDao;
    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            list = infoAuthConfigDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            totalList = infoAuthConfigDao.query(params);
            CommonFuns.convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
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
            //先根据业务方向和情报分类 验证是否存在
            String infoTypeId = CommonFuns.nullToString(objBody.get("infoTypeId"));
            String busTypeId =  CommonFuns.nullToString(objBody.get("busTypeId"));
            JSONObject paramJson = new JSONObject();
            paramJson.put("infoTypeId",infoTypeId);
            paramJson.put("busTypeId",busTypeId);
            JSONObject jsonObject = infoAuthConfigDao.getObjectByType(paramJson);
            if(jsonObject!=null){
                return ResultUtil.result(false, "系统中已经存在", resultJson);
            }
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            String id = IdUtil.getId();
            objBody.put("id", id);
            infoAuthConfigDao.addObject(objBody);
        } catch (Exception e) {
            logger.error("Exception in add 添加指标异常！", e);
            return ResultUtil.result(false, "添加指标异常！", "");
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
            String infoTypeId = CommonFuns.nullToString(objBody.get("infoTypeId"));
            String busTypeId =  CommonFuns.nullToString(objBody.get("busTypeId"));
            String id =  CommonFuns.nullToString(objBody.get("id"));
            JSONObject paramJson = new JSONObject();
            paramJson.put("infoTypeId",infoTypeId);
            paramJson.put("busTypeId",busTypeId);
            JSONObject jsonObject = infoAuthConfigDao.getObjectByType(paramJson);
            if(jsonObject!=null&&!id.equals(jsonObject.getString("id"))){
                return ResultUtil.result(false, "系统中已经存在", resultJson);
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            infoAuthConfigDao.updateObject(objBody);
        } catch (Exception e) {
            logger.error("Exception in update 更新指标异常", e);
            return ResultUtil.result(false, "更新指标异常！", "");
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
            infoAuthConfigDao.batchDelete(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除指标", e);
            return ResultUtil.result(false, "删除指标异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }
    public JSONObject getObjectById(String id){
        return infoAuthConfigDao.getObjectById(id);
    }
}
