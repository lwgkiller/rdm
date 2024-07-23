package com.redxun.rdmZhgl.core.service;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.dao.DelayApplyDao;
import com.redxun.rdmZhgl.core.dao.DelayApplyDetailDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;

/**
 * @author zhangzhen
 */
@Service
public class DelayApplyDetailService {
    private static final Logger logger = LoggerFactory.getLogger(DelayApplyDetailService.class);
    @Autowired
    DelayApplyDetailDao delayApplyDetailDao;
    @Resource
    CommonInfoManager commonInfoManager;
    @Resource
    DelayApplyDao delayApplyDao;

    @Autowired
    private OsGroupManager osGroupManager;

    public JsonPageResult<?> query(HttpServletRequest request, Boolean flag) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, flag);
            if(params.get("sortField")==null) {
                params.put("sortField","CONVERT(userName using GBK),workDate");
            }
            list = delayApplyDetailDao.query(params);
            convertDate(list);
            // 返回结果，补充未申报的
            String scene = RequestUtil.getString(request, "scene", "");
            if (StringUtils.isNotBlank(scene) && scene.equalsIgnoreCase("addNoApply")
                && (params.get("userName") == null || StringUtils.isBlank(params.get("userName").toString()))) {
                List<Map<String, Object>> addNotApply =
                    delayApplyDao.getNotApplyUserInfos(params.get("mainId").toString());
                if (addNotApply != null) {
                    list.addAll(addNotApply);
                }
            }
            result.setData(list);
        } catch (Exception e) {
            logger.error("加班申请数据异常", e);
            result.setSuccess(false);
            result.setMessage("加班申请数据异常");
        }
        return result;
    }

    public void saveOrUpdate(JsonResult result, JSONArray changeGridDataJson, String mainId) {
        try {
            if (changeGridDataJson == null || changeGridDataJson.isEmpty()) {
                logger.warn("gridData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return;
            }
            for (int i = 0; i < changeGridDataJson.size(); i++) {
                JSONObject oneObject = changeGridDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                oneObject.put("mainId", mainId);
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    delayApplyDetailDao.add(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    delayApplyDetailDao.update(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    delayApplyDetailDao.del(id);
                }
            }
        } catch (Exception e) {
            logger.error("保存加班申请信息异常", e);
            result.setSuccess(false);
            result.setMessage("保存加班申请信息异常");
        }
    }

    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> params = new HashMap<>(16);
        params = CommonFuns.getSearchParam(params, request, false);
        List<Map<String, Object>> list = delayApplyDetailDao.query(params);
        String mainId = CommonFuns.nullToString(params.get("mainId"));
        JSONObject jsonObject = delayApplyDao.getJsonObject(mainId);
        convertDate(list);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(jsonObject.getString("deptName")).append("：").append(jsonObject.getString("title"));
        String title = stringBuffer.toString();
        String[] fieldNames = {"加班人员", "加班日期", "加班时长", "加班工作事项"};
        String[] fieldCodes = {"userName", "workDate", "workTime", "reason"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(list, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(title + "_人员明细表", wbObj, response);
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
                    if ("workDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                }
            }
        }
    }

    public void exportDelayApply(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> params = new HashMap<>();
        // 传入条件(不包括分页)
        params = CommonFuns.getSearchParam(params, request, false);
        String[] mainIds = params.get("mainIds").toString().split(",");
        Map<String, Object> exportParams = new HashMap<>();
        exportParams.put("mainIds", mainIds);
        List<Map<String, Object>> list = delayApplyDetailDao.exportDelayApply(exportParams);
        convertDate(list);
        String title = "加班人员明细表";
        String[] fieldNames = {"部门", "加班人员", "加班日期", "加班时长", "加班工作事项"};
        String[] fieldCodes = {"deptName", "userName", "workDate", "workTime", "reason"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(list, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(title, wbObj, response);
    }
}
