package com.redxun.info.core.manager;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.redxun.info.core.dao.InfoBusTypeConfigDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;

/**
 * @author zhangzhen
 */
@Service
public class InfoBusTypeConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(InfoBusTypeConfigManager.class);
    @Autowired
    InfoBusTypeConfigDao infoBusTypeConfigDao;

    public JSONArray getDicInfoType(){
        return infoBusTypeConfigDao.getDicBusType();
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
                if("added".equals(state)|| StringUtils.isBlank(id)) {
                    //新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    JSONObject resultJson = verifyTypeName(oneObject.getString("busTypeName"));
                    if(resultJson != null){
                        result.setSuccess(false);
                        result.setMessage("类别已存在");
                        return;
                    }
                    infoBusTypeConfigDao.add(oneObject);
                } else if ("modified".equals(state)) {
                    //修改
                    JSONObject resultJson = verifyTypeName(oneObject.getString("busTypeName"));
                    if(resultJson != null&&!resultJson.getString("id").equals(oneObject.getString("id"))){
                        result.setSuccess(false);
                        result.setMessage("类别已存在");
                        return;
                    }
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    infoBusTypeConfigDao.update(oneObject);
                } else if("removed".equals(state)) {
                    //删除
                    infoBusTypeConfigDao.del(id);
                }
            }

        } catch (Exception e) {
            logger.error("Exception in saveOrUpdate ");
            result.setSuccess(false);
            result.setMessage("Exception in saveOrUpdate");
            return;
        }
    }
    public JSONObject verifyTypeName(String busTypeName){
        JSONObject resultJson = infoBusTypeConfigDao.getObjectByName(busTypeName);
        return resultJson;
    }
    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            list = infoBusTypeConfigDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            totalList = infoBusTypeConfigDao.query(params);
            CommonFuns.convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            result.setSuccess(false);
            result.setMessage("查询异常");
        }
        return result;
    }
}
