package com.redxun.gkgf.core.manager;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.redxun.core.json.JsonResult;
import com.redxun.gkgf.core.dao.GkgfWorkTypeDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;

/**
 * @author zhangzhen
 */
@Service
public class GkgfWorkTypeManager {
    private static final Logger logger = LoggerFactory.getLogger(GkgfWorkTypeManager.class);
    @Autowired
    GkgfWorkTypeDao gkgfWorkTypeDao;

    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            list = gkgfWorkTypeDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            totalList = gkgfWorkTypeDao.query(params);
           CommonFuns.convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 物料查询异常", e);
            result.setSuccess(false);
            result.setMessage("物料查询异常");
        }
        return result;
    }
    public void saveOrUpdate(JsonResult result, String changeGridDataStr) {
        try {
            JSONArray changeGridDataJson = JSONObject.parseArray(changeGridDataStr);
            if (changeGridDataJson == null || changeGridDataJson.isEmpty()) {
                logger.warn("gridData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return;
            }
            for(int i=0;i<changeGridDataJson.size();i++) {
                JSONObject oneObject=changeGridDataJson.getJSONObject(i);
                String state=oneObject.getString("_state");
                String id=oneObject.getString("id");
                if("add".equals(state)|| StringUtils.isBlank(id)) {
                    //新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    JSONObject resultJson = verifyCode(oneObject.getString("workName"));
                    if(resultJson != null){
                        result.setSuccess(false);
                        result.setMessage("名称已存在");
                        return;
                    }
                    gkgfWorkTypeDao.add(oneObject);
                } else if ("modified".equals(state)) {
                    //修改
                    JSONObject resultJson = verifyCode(oneObject.getString("workName"));
                    if(resultJson != null&&!resultJson.getString("id").equals(oneObject.getString("id"))){
                        result.setSuccess(false);
                        result.setMessage("名称已存在");
                        return;
                    }
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    gkgfWorkTypeDao.update(oneObject);
                } else if("removed".equals(state)) {
                    //删除
                    gkgfWorkTypeDao.del(id);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("保存计量单位信息异常");
            result.setSuccess(false);
            result.setMessage("保存计量单位信息异常");
            return;
        }
    }
    public JSONObject verifyCode(String workName){
        JSONObject resultJson = gkgfWorkTypeDao.getItemIdByName(workName);
        return resultJson;
    }

    public  List<JSONObject> getDictWorkType() {
        Map<String, Object> paraMap = new HashMap<>(16);
        return gkgfWorkTypeDao.getDictWorkType(paraMap);
    }
}
