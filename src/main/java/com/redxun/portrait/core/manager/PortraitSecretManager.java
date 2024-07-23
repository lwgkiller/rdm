package com.redxun.portrait.core.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.StringUtil;
import com.redxun.portrait.core.dao.PortraitDocumentDao;
import com.redxun.portrait.core.dao.PortraitSecretDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.sys.org.dao.OsUserDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * @author zhangzhen
 */
@Service
public class PortraitSecretManager {
    private static final Logger logger = LoggerFactory.getLogger(PortraitSecretManager.class);
    @Resource
    PortraitSecretDao portraitSecretDao;
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
            JSONObject resultJson = commonInfoManager.hasPermission("secretAdmin");
            if ("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
                Map<String,String> deptMap=commonInfoManager.queryDeptUnderJSZX();
                idList.addAll(deptMap.keySet());
            } else {
                if (resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY) || resultJson.getBoolean("HX-GLY")) {
                    // 分管领导看所有数据
                    Map<String,String> deptMap=commonInfoManager.queryDeptUnderJSZX();
                    idList.addAll(deptMap.keySet());
                } else if (resultJson.getBoolean("secretAdmin")) {
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
            list = portraitSecretDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            params.put("ids", idList);
            if (!resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY) && !resultJson.getBoolean("isLeader") && !resultJson.getBoolean("HX-GLY")
                && !resultJson.getBoolean("secretAdmin")
                && !"admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
                params.put("userId", ContextUtil.getCurrentUserId());
            }
            totalList = portraitSecretDao.query(params);
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

    public JSONObject asyncSecret() {
        try {
            List<JSONObject> list = portraitSecretDao.getSecretList();
            Map<String, Object> objBody = new HashMap<>(16);
            for (JSONObject temp : list) {
                objBody = new HashMap<>(16);
                objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                objBody.put("applyYear", temp.getString("applyYear"));
                objBody.put("mainId", temp.getString("jsmmId"));
                String finishUserIds = temp.getString("finishUserIds");
                if(StringUtil.isNotEmpty(finishUserIds)){
                    String []idArray = finishUserIds.split(",");
                    for(int i=0;i<idArray.length;i++){
                        if(i==0){
                            objBody.put("userId", idArray[i]);
                            objBody.put("score", 3);
                        }else if(i==1){
                            objBody.put("userId", idArray[i]);
                            objBody.put("score", 1);
                        }else{
                            objBody.put("userId", idArray[i]);
                            objBody.put("score", 0.5);
                        }
                        objBody.put("id", IdUtil.getId());
                        portraitSecretDao.addSecret(objBody);
                    }
                }
                portraitSecretDao.updateSecretStatus(temp.getString("jsmmId"));
            }
        } catch (Exception e) {
            logger.error("Exception in add asyncSecret！", e);
            return ResultUtil.result(false,"同步失败",null);
        }
        return ResultUtil.result(true,"同步成功",null);
    }
    public List<JSONObject> getPersonSecretList(JSONObject jsonObject){
        List<JSONObject> list = portraitSecretDao.getPersonSecretList(jsonObject);
        CommonFuns.convertDateJSON(list);
        return list;
    }
}
