package com.redxun.xcmgJsjl.core.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgJsjl.core.dao.XcmgJsjlConfigDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class XcmgJsjlConfigManager {
    private static Logger logger = LoggerFactory.getLogger(XcmgJsjlConfigManager.class);
    @Autowired
    private XcmgJsjlConfigDao xcmgJsjlConfigDao;

    public List<JSONObject> dimensionList(HttpServletRequest request) {
        JSONObject param= new JSONObject();
        String scene= RequestUtil.getString(request,"scene","");
        if(StringUtils.isNotBlank(scene)) {
            param.put("descp",scene);
        }
        List<JSONObject> result = xcmgJsjlConfigDao.dimensionList(param);
        for (JSONObject oneObj : result) {
            if (StringUtils.isNotBlank(oneObj.getString("CREATE_TIME_"))) {
                oneObj.put("CREATE_TIME_",
                    DateFormatUtil.format(oneObj.getDate("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
            }
            if (StringUtils.isNotBlank(oneObj.getString("UPDATE_TIME_"))) {
                oneObj.put("UPDATE_TIME_",
                        DateFormatUtil.format(oneObj.getDate("UPDATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
            }
        }
        return result;
    }

    public void saveDimension(JSONObject result, String changeGridDataStr) {
        try {
            JSONArray changeGridDataJson = JSONObject.parseArray(changeGridDataStr);
            if (changeGridDataJson == null || changeGridDataJson.isEmpty()) {
                logger.warn("gridDataArray is blank");
                result.put("message", "保存失败，数据为空！");
                return;
            }

            Set<String> needDelId = new HashSet<>();
            for (int i = 0; i < changeGridDataJson.size(); i++) {
                JSONObject oneObject = changeGridDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    xcmgJsjlConfigDao.saveDimension(oneObject);
                } else if ("modified".equals(state)) {
                    // 修改
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    xcmgJsjlConfigDao.updateDimension(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除时需要先判断是否有关联的标准，有的话不能删除
                    needDelId.add(id);
                }
            }
            if (!needDelId.isEmpty()) {
                Map<String, Object> params = new HashMap<>();
                params.put("ids", needDelId);
                List<JSONObject> usingIdInfos = xcmgJsjlConfigDao.queryUsingDimensions(params);
                Set<String> usingIds = new HashSet<>();
                if (usingIdInfos != null && !usingIdInfos.isEmpty()) {
                    for (JSONObject oneData : usingIdInfos) {
                        usingIds.add(oneData.getString("dimensionId"));
                    }
                    result.put("message", "已关联业务交流数据的维度不会被删除！");
                }
                needDelId.removeAll(usingIds);
                if (!needDelId.isEmpty()) {
                    xcmgJsjlConfigDao.delDimensionByIds(params);
                }
            }

        } catch (Exception e) {
            logger.error("Exception in saveDimension");
            result.put("message", "保存失败，系统异常！");
            return;
        }
    }
}
