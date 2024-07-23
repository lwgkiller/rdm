package com.redxun.rdmZhgl.core.service;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.YfjbBaseInfoDao;
import com.redxun.rdmZhgl.core.dao.YfjbMonthDataDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;

/**
 * @author zhangzhen
 */
@Service
public class YfjbMonthDataService {
    private static final Logger logger = LoggerFactory.getLogger(YfjbMonthDataService.class);
    @Resource
    YfjbMonthDataDao yfjbMonthDataDao;
    @Resource
    YfjbBaseInfoDao yfjbBaseInfoDao;
    @Resource
    CommonInfoManager commonInfoManager;

    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            JSONObject resultJson = commonInfoManager.hasPermission("YFJB-GLY");
            JSONObject jbzyJson = commonInfoManager.hasPermission("JBZY");
            params = CommonFuns.getSearchParam(params, request, true);
            if (resultJson.getBoolean("YFJB-GLY") || resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)
                || "admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
            } else if (jbzyJson.getBoolean("JBZY") || resultJson.getBoolean("isLeader")) {
            } else {
                result.setData(list);
                result.setTotal(totalList.size());
                return result;
            }
            list = yfjbMonthDataDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            if (resultJson.getBoolean("YFJB-GLY") || resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)
                || "admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
            } else if (jbzyJson.getBoolean("JBZY") || resultJson.getBoolean("isLeader")) {
            } else {
                result.setData(list);
                result.setTotal(totalList.size());
                return result;
            }
            totalList = yfjbMonthDataDao.query(params);
            CommonFuns.convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
        }
        return result;
    }

    public JSONObject saveOrUpdateItem(String changeGridDataStr) {
        JSONObject resultJson = new JSONObject();
        StringBuffer resultStr = new StringBuffer();
        try {
            JSONArray changeGridDataJson = JSONObject.parseArray(changeGridDataStr);
            for (int i = 0; i < changeGridDataJson.size(); i++) {
                JSONObject oneObject = changeGridDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if (oneObject.get("yearMonth") != null ) {
                    String yearMonth = oneObject.get("yearMonth").toString().substring(0,7);
                    oneObject.put("yearMonth",yearMonth);
                }
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    oneObject.put("deptId", ContextUtil.getCurrentUser().getMainGroupId());
                    // 根据年月判断是否已经存在
                    JSONObject objJson = yfjbMonthDataDao.getObjByInfo(oneObject);
                    if (objJson != null) {
                        resultStr.append(oneObject.getString("monthYear")).append(",");
                        continue;
                    }
                    yfjbMonthDataDao.addItem(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    JSONObject objJson = yfjbMonthDataDao.getObjByInfo(oneObject);
                    if (objJson != null && !objJson.getString("id").equals(oneObject.getString("id"))) {
                        resultStr.append(oneObject.getString("monthYear")).append(",");
                        continue;
                    }
                    yfjbMonthDataDao.updateItem(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    yfjbMonthDataDao.delItem(oneObject.getString("id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("保存信息异常", e);
            return ResultUtil.result(false, "保存信息异常！", null);
        }
        if (StringUtil.isNotEmpty(resultStr.toString())) {
            return ResultUtil.result(false, resultStr.toString() + "数据已存在！", resultStr.toString());
        }
        return ResultUtil.result(true, "保存成功！", null);
    }

    public JsonPageResult<?> queryQuarter(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            List<JSONObject> list = yfjbMonthDataDao.getQuarterDataList();
            // 返回结果
            result.setData(list);
            result.setTotal(list.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
        }
        return result;
    }

    public JSONObject saveOrUpdateQuarter(String changeGridDataStr) {
        try {
            JSONArray changeGridDataJson = JSONObject.parseArray(changeGridDataStr);
            for (int i = 0; i < changeGridDataJson.size(); i++) {
                JSONObject oneObject = changeGridDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    yfjbMonthDataDao.addQuarterItem(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    yfjbMonthDataDao.updateQuarterItem(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    yfjbMonthDataDao.delQuarterItem(oneObject.getString("id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("保存信息异常", e);
            return ResultUtil.result(false, "保存信息异常！", null);
        }
        return ResultUtil.result(true, "保存成功！", null);
    }
}
