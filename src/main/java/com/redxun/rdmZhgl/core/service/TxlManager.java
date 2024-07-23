
package com.redxun.rdmZhgl.core.service;

import java.util.*;

import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmZhgl.core.dao.TxlDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class TxlManager {
    private Logger logger = LoggerFactory.getLogger(TxlManager.class);

    @Autowired
    private TxlDao txlDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    public JsonPageResult<?> queryTxlList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "companyName,deptNameLevelOne,deptNameLevelTwo,id", "asc");
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    params.put(name, value);
                }
            }
        }
        // 增加分页条件
        rdmZhglUtil.addPage(request, params);
        List<JSONObject> txlList = txlDao.queryTxlList(params);
        result.setData(txlList);
        int countTxl = txlDao.countTxlList(params);
        result.setTotal(countTxl);
        return result;
    }

    public void saveTxl(JsonResult result, String postDataStr) {
        try {
            JSONObject postData = JSONObject.parseObject(postDataStr);
            if (postData == null || postData.isEmpty()) {
                logger.warn("gridData is blank");
                result.setSuccess(false);
                result.setMessage("信息为空，保存失败！");
                return;
            }
            if (StringUtils.isBlank(postData.getString("id"))) {
                postData.put("id",IdUtil.getId());
                txlDao.insertTxl(postData);
            } else {
                txlDao.updateTxl(postData);
            }
        } catch (Exception e) {
            logger.error("Exception in save saveTxl");
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
    }

    public void deleteTxl(JSONObject result, String txlIds) {
        try {
            if (StringUtils.isBlank(txlIds)) {
                result.put("message", "删除成功！");
                return;
            }
            List<String> idList = Arrays.asList(txlIds.split(",", -1));
            Map<String, Object> params = new HashMap<>();
            params.put("ids", idList);
            txlDao.deleteTxl(params);
            result.put("message", "删除成功！");
        } catch (Exception e) {
            logger.error("Exception in save saveTxl");
            result.put("message", "系统异常！");
        }
    }
}
