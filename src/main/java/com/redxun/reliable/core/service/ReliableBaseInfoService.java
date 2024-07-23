package com.redxun.reliable.core.service;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.reliable.core.dao.ReliableBaseInfoDao;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.StringUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.standardManager.core.util.ResultUtil;

/**
 * @author zhangzhen
 */
@Service
public class ReliableBaseInfoService {
    private static final Logger logger = LoggerFactory.getLogger(ReliableBaseInfoService.class);
    @Autowired
    ReliableBaseInfoDao reliableBaseInfoDao;

    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            params.put("userId",ContextUtil.getCurrentUserId());
            list = reliableBaseInfoDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            params.put("userId",ContextUtil.getCurrentUserId());
            totalList = reliableBaseInfoDao.query(params);
            CommonFuns.convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 可靠性提升查询异常", e);
            result.setSuccess(false);
            result.setMessage("可靠性提升查询异常");
        }
        return result;
    }
    public JsonPageResult<?> getAllData(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            list = reliableBaseInfoDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            totalList = reliableBaseInfoDao.query(params);
            CommonFuns.convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 可靠性提升查询异常", e);
            result.setSuccess(false);
            result.setMessage("可靠性提升查询异常");
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
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                objBody.put(mapKey, mapValue);
            }
            objBody.put("id", id);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", new Date());
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", new Date());
            reliableBaseInfoDao.add(objBody);
        } catch (Exception e) {
            logger.error("Exception in add 保存可靠性提升", e);
            return ResultUtil.result(false, "可靠性提升息异常！", "");
        }
        resultJson.put("id", id);
        return ResultUtil.result(true, "保存成功", resultJson);
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
            objBody.put("UPDATE_TIME_", new Date());
            reliableBaseInfoDao.update(objBody);
        } catch (Exception e) {
            logger.error("Exception in update 更新可靠性提升", e);
            return ResultUtil.result(false, "更新可靠性提升信息异常！", "");
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
            reliableBaseInfoDao.batchDelete(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除可靠性提升", e);
            return ResultUtil.result(false, "删除可靠性提升！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }

    public JSONObject getItemObject(HttpServletRequest request) {
        String id = request.getParameter("id");
        if (StringUtil.isEmpty(id)) {
            return ResultUtil.result(HttpStatus.SC_BAD_REQUEST, "id不能为空", "");
        }
        JSONObject resultJSON = reliableBaseInfoDao.getObjectById(id);
        return ResultUtil.result(HttpStatus.SC_OK, "调用成功", resultJSON);
    }

}
