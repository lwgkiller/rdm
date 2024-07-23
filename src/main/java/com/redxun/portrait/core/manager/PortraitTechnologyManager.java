package com.redxun.portrait.core.manager;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.redxun.portrait.core.dao.PortraitTechnologyDao;
import com.redxun.rdmCommon.core.util.RdmConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.portrait.core.dao.PortraitDocumentDao;
import com.redxun.portrait.core.dao.PortraitInformationDao;
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
public class PortraitTechnologyManager {
    private static final Logger logger = LoggerFactory.getLogger(PortraitTechnologyManager.class);
    @Resource
    PortraitTechnologyDao portraitTechnologyDao;
    @Autowired
    OsUserDao osUserDao;
    @Resource
    CommonInfoManager commonInfoManager;
    @Resource
    PortraitDocumentDao portraitDocumentDao;

    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            List<String> idList = new ArrayList<>();
            // 用户权限
            JSONObject resultJson = commonInfoManager.hasPermission("technologyAdmin");
            if ("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
                Map<String,String> deptMap=commonInfoManager.queryDeptUnderJSZX();
                idList.addAll(deptMap.keySet());
            } else {
                if (resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY) || resultJson.getBoolean("HX-GLY")) {
                    // 分管领导看所有数据
                    Map<String,String> deptMap=commonInfoManager.queryDeptUnderJSZX();
                    idList.addAll(deptMap.keySet());
                } else if (resultJson.getBoolean("technologyAdmin")) {
                    Map<String,String> deptMap=commonInfoManager.queryDeptUnderJSZX();
                    idList.addAll(deptMap.keySet());
                } else if (resultJson.getBoolean("isLeader")) {
                    // 部门领导看自己部门的
                    idList.add(ContextUtil.getCurrentUser().getMainGroupId());
                } else {
                    // 普通员工看自己的
                    params.put("userId", ContextUtil.getCurrentUserId());
                    idList.add(ContextUtil.getCurrentUser().getMainGroupId());
                }
            }
            params.put("ids", idList);
            list = portraitTechnologyDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            params.put("ids", idList);
            if (!resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY) && !resultJson.getBoolean("isLeader") && !resultJson.getBoolean("HX-GLY")
                && !resultJson.getBoolean("technologyAdmin")
                && !"admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
                params.put("userId", ContextUtil.getCurrentUserId());
            }
            totalList = portraitTechnologyDao.query(params);
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

    public JSONObject asyncTechnology() {
        try {
            List<JSONObject> list = portraitTechnologyDao.getTechnologyList();
            Map<String, Object> objBody = new HashMap<>(16);
            List<Map<String, Object>> tempInsert = new ArrayList<>();
            int score = 1;
            for (JSONObject temp : list) {
                objBody = new HashMap<>(16);
                objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                objBody.put("id", IdUtil.getId());
                objBody.put("userId", temp.getString("CREATE_BY_"));
                objBody.put("applyYear", temp.getString("applyYear"));
                objBody.put("mainId", temp.getString("jssjkId"));
                objBody.put("score", score);
                tempInsert.add(objBody);
                portraitTechnologyDao.updateTechnologyStatus(temp.getString("jssjkId"));
                if (tempInsert.size() % 20 == 0) {
                    portraitTechnologyDao.batchInsert(tempInsert);
                    tempInsert.clear();
                }
            }
            if (!tempInsert.isEmpty()) {
                portraitTechnologyDao.batchInsert(tempInsert);
                tempInsert.clear();
            }
        } catch (Exception e) {
            logger.error("Exception in add asyncTechnology！", e);
            return ResultUtil.result(false,"同步失败",null);
        }
        return ResultUtil.result(true,"同步成功",null);
    }
    public List<JSONObject> getPersonTechnologyList(JSONObject jsonObject){
        List<JSONObject> list = portraitTechnologyDao.getPersonTechnologyList(jsonObject);
        CommonFuns.convertDateJSON(list);
        return list;
    }
}
