package com.redxun.secret.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.secret.core.dao.SecretDao;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import groovy.util.logging.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SecretService {
    private Logger logger = LogManager.getLogger(SecretService.class);
    @Autowired
    private SecretDao secretDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private BpmInstManager bpmInstManager;

    public JsonPageResult<?> querySecret(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "CREATE_TIME_");
            params.put("sortOrder", "desc");
        }
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
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        params.put("currentUserId",ContextUtil.getCurrentUserId());
        addSecretRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> secretList = secretDao.querySecret(params);
        for (JSONObject secret : secretList) {
            if (secret.get("CREATE_TIME_") != null) {
                secret.put("CREATE_TIME_", DateUtil.formatDate((Date)secret.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(secretList, ContextUtil.getCurrentUserId());
        result.setData(secretList);
        int countSecretDataList = secretDao.countSecretList(params);
        result.setTotal(countSecretDataList);
        return result;
    }




    public void createSecret(JSONObject formData) {
        formData.put("secretId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        secretDao.createSecret(formData);
    }

    public void updateSecret(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        secretDao.updateSecret(formData);
    }



    public JSONObject getSecretById(String secretId) {
        JSONObject detailObj = secretDao.querySecretById(secretId);
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.get("hzdates") != null) {
            detailObj.put("hzdates", DateUtil.formatDate((Date) detailObj.get("hzdates"), "yyyy-MM-dd"));
        }
        if (detailObj.get("hzdatee") != null) {
            detailObj.put("hzdatee", DateUtil.formatDate((Date) detailObj.get("hzdatee"), "yyyy-MM-dd"));
        }
        if (detailObj.getDate("qddate") != null) {
            detailObj.put("qddate", DateUtil.formatDate(detailObj.getDate("qddate"), "yyyy-MM-dd"));
        }

        return detailObj;
    }


    public JsonResult deleteSecret(String[] secretIdsArr, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        for (String secretId : secretIdsArr) {
            // 删除基本信息
            param.clear();
            param.put("secretId", secretId);
            secretDao.deleteSecret(param);
        }
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }
    public void exportSecretList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = querySecret(request, false);
        List<Map<String, Object>> listData = result.getData();
        for(Map<String, Object> data:listData){
            data.put("onfile","true".equalsIgnoreCase(data.get("onfile").toString())?"已归档":"未归档");
            if (data.get("hzdates") != null) {
                data.put("hzdates", DateUtil.formatDate((Date) data.get("hzdates"), "yyyy-MM-dd"));
            }
            if (data.get("hzdatee") != null) {
                data.put("hzdatee", DateUtil.formatDate((Date) data.get("hzdatee"), "yyyy-MM-dd"));
            }
            if (data.get("qddate") != null) {
                data.put("qddate", DateUtil.formatDate((Date)data.get("qddate"), "yyyy-MM-dd"));
            }
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "保密协议";
        String excelName = nowDate + title;
        String[] fieldNames = {"编号", "项目", "合作相关方", "项目合作开始日期", "项目合作结束日期","保密日期", "签订日期",
                "经办人", "部门负责人", "纸质是否归档","备注", "创建时间"};
        String[] fieldCodes = {"numinfo", "projectName", "partner", "hzdates", "hzdatee","bmdate",
                "qddate", "applyName", "resName","onfile", "note", "CREATE_TIME_"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
    private void addSecretRoleParam(Map<String, Object> params, String userId, String userNo) {
        params.put("currentUserId", userId);
        if (userNo.equalsIgnoreCase("admin")) {
            return;
        }
        boolean isFgld = rdmZhglUtil.judgeIsPointRoleUser(userId,"保密管理查看权限");
        if (isFgld) {
            params.put("roleName", "allcheck");
            return;
        }
        params.put("roleName", "other");
    }
}
