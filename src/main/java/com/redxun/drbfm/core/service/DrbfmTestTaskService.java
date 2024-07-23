package com.redxun.drbfm.core.service;

import static com.redxun.rdmCommon.core.util.RdmCommonUtil.toGetParamVal;

import java.io.File;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.camel.util.FileUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.drbfm.core.dao.DrbfmSingleDao;
import com.redxun.drbfm.core.dao.DrbfmTestTaskDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2021 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2021/2/23 10:43
 */
@Service
public class DrbfmTestTaskService {
    private Logger logger = LoggerFactory.getLogger(DrbfmTestTaskService.class);

    @Autowired
    private DrbfmTestTaskDao drbfmTestTaskDao;
    @Autowired
    private DrbfmSingleDao drbfmSingleDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    public JsonPageResult<?> getTestTaskList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        String singleId = RequestUtil.getString(request, "singleId");
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "drbfm_single_testTask.CREATE_TIME_", "desc");
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
        if (StringUtil.isNotEmpty(singleId)) {
            params.put("belongSingleId", singleId);
        }
        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }

        // 增加角色过滤的条件
        addRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<Map<String, Object>> singleList = drbfmTestTaskDao.queryTestTaskList(params);
        // 得到的都是id，在这里转换成名称
        if (singleList.isEmpty()) {
            return result;
        }

        // 关联的指标逗号分割
        for (Map<String, Object> mp : singleList) {
            JSONObject params2 = new JSONObject();
            params2.put("applyId", mp.get("id"));
            List<JSONObject> demandList = drbfmTestTaskDao.queryTestTaskDemandList(params2);
            if (!demandList.isEmpty()) {
                StringBuffer tmpStr = new StringBuffer();
                for (JSONObject obj : demandList) {
                    tmpStr.append(obj.get("quotaName")).append("，<br>");
                }
                tmpStr.deleteCharAt(tmpStr.length() - 5);
                mp.put("relQuotaNames", tmpStr.toString());
            }
            if (mp.get("CREATE_TIME_") != null) {
                mp.put("CREATE_TIME_", DateUtil.formatDate((Date)mp.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(singleList);
        result.setData(singleList);
        int countTestTaskDataList = drbfmTestTaskDao.countTestTaskList(params);
        result.setTotal(countTestTaskDataList);
        return result;
    }

    public JSONObject queryApplyDetail(String id) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(id)) {
            return result;
        }
        JSONObject params = new JSONObject();
        params.put("id", id);
        JSONObject obj = drbfmTestTaskDao.queryApplyDetail(params);
        return obj;
    }

    public void createTestTask(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        // 基本信息
        drbfmTestTaskDao.insertTestTask(formData);

        // 需求信息
        demandProcess(formData.getString("id"), formData.getString("belongSingleId"),
            formData.getJSONArray("changeDemandGrid"));

    }

    public void updateTestTask(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        // 基本信息
        drbfmTestTaskDao.updateTestTask(formData);

        // 需求信息
        demandProcess(formData.getString("id"), formData.getString("belongSingleId"),
            formData.getJSONArray("changeDemandGrid"));

    }

    // 常规附带验证任务保存
    public void noFlowTestTaskSave(JSONObject formDataJson, JsonResult saveResult) {
        String id = formDataJson.getString("id");
        if (StringUtils.isBlank(id)) {
            id = IdUtil.getId();
            formDataJson.put("id", id);
            formDataJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            formDataJson.put("CREATE_TIME_", new Date());
            String applyNumber =
                "T-" + XcmgProjectUtil.getNowLocalDateStr("yyyyMMddHHmmssSSS") + (int)(Math.random() * 100);
            formDataJson.put("testNumber", applyNumber);
            drbfmTestTaskDao.insertTestTask(formDataJson);
        } else {
            formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            formDataJson.put("UPDATE_TIME_", new Date());
            drbfmTestTaskDao.updateTestTask(formDataJson);
        }
        // 指标及验证数据保存
        demandProcess(formDataJson.getString("id"), formDataJson.getString("belongSingleId"),
            formDataJson.getJSONArray("changeDemandGrid"));
        saveResult.setData(id);
    }

    private void demandProcess(String applyId, String belongSingleId, JSONArray demandArr) {
        if (demandArr == null || demandArr.isEmpty()) {
            return;
        }
        JSONObject param = new JSONObject();
        param.put("ids", new JSONArray());
        for (int index = 0; index < demandArr.size(); index++) {
            JSONObject oneObject = demandArr.getJSONObject(index);
            JSONObject quotaJson = new JSONObject();
            quotaJson.put("id", oneObject.getString("relQuotaId"));
            quotaJson.put("sjStandardIds", oneObject.getString("sjStandardIds"));
            quotaJson.put("testStandardIds", oneObject.getString("testStandardIds"));
            quotaJson.put("evaluateStandardIds", oneObject.getString("evaluateStandardIds"));
            String id = oneObject.getString("id");
            String state = oneObject.getString("_state");
            if ("added".equals(state) || StringUtils.isBlank(id)) {
                // 新增
                oneObject.put("id", IdUtil.getId());
                oneObject.put("belongSingleId", belongSingleId);
                oneObject.put("relTestTaskId", applyId);
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                drbfmTestTaskDao.insertTestTaskDemand(oneObject);
                quotaJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                quotaJson.put("UPDATE_TIME_", new Date());
                drbfmSingleDao.updateStandard(quotaJson);
            } else if ("modified".equals(state)) {
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", new Date());
                drbfmTestTaskDao.updateTestTaskDemand(oneObject);
                quotaJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                quotaJson.put("UPDATE_TIME_", new Date());
                drbfmSingleDao.updateStandard(quotaJson);
            } else if ("removed".equals(state)) {
                // 删除
                param.getJSONArray("ids").add(id);
            }
        }
        if (!param.getJSONArray("ids").isEmpty()) {
            drbfmTestTaskDao.deleteTestTaskDemand(param);
        }
    }

    // 文件列表
    public List<JSONObject> queryDemandList(JSONObject params) {
        List<JSONObject> demandList = drbfmTestTaskDao.queryDemandList(params);
        for (JSONObject demand : demandList) {
            if (demand.get("CREATE_TIME_") != null) {
                demand.put("CREATE_TIME_", DateUtil.formatDate((Date)demand.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return demandList;
    }

    // 子表列表
    public List<JSONObject> queryTestTaskDemandList(JSONObject params) {
        List<JSONObject> demandList = drbfmTestTaskDao.queryTestTaskDemandList(params);
        List<String> fullList = new ArrayList<>();
        for (Map<String, Object> mp : demandList) {
            if (mp.get("sjStandardIds") != null) {
                fullList.addAll(Arrays.asList(mp.get("sjStandardIds").toString().split(",")));
            }
            if (mp.get("testStandardIds") != null) {
                fullList.addAll(Arrays.asList(mp.get("testStandardIds").toString().split(",")));
            }
            if (mp.get("evaluateStandardIds") != null) {
                fullList.addAll(Arrays.asList(mp.get("evaluateStandardIds").toString().split(",")));
            }
        }
        // 用id查询标准名称

        JSONObject params1 = new JSONObject();
        params1.put("ids", fullList);
        if (fullList.isEmpty()) {
            return demandList;
        }
        List<JSONObject> standardRes = drbfmTestTaskDao.queryStandardIds(params1);
        Map<String, String> standardMap = new HashMap<>();
        // 转换成id-标准字典
        for (JSONObject res : standardRes) {
            standardMap.put(res.getString("id"),
                    "【" + res.getString("standardNumber") + "】" + res.getString("standardName"));
        }

        // 再次遍历结果，id转换成名称
        for (Map<String, Object> mp : demandList) {
            if (mp.get("sjStandardIds") != null && !"".equalsIgnoreCase(mp.get("sjStandardIds").toString())) {
                StringBuffer tmpStr = new StringBuffer();
                for (String item : mp.get("sjStandardIds").toString().split(",")) {
                    tmpStr.append(standardMap.get(item)).append("，<br>");
                }
                tmpStr.deleteCharAt(tmpStr.length() - 5);
                mp.put("sjStandardNames", tmpStr.toString());
            }
            if (mp.get("testStandardIds") != null && !"".equalsIgnoreCase(mp.get("testStandardIds").toString())) {
                StringBuffer tmpStr = new StringBuffer();
                for (String item : mp.get("testStandardIds").toString().split(",")) {
                    tmpStr.append(standardMap.get(item)).append("，<br>");
                }
                tmpStr.deleteCharAt(tmpStr.length() - 5);
                mp.put("testStandardNames", tmpStr.toString());
            }
            if (mp.get("evaluateStandardIds") != null
                && !"".equalsIgnoreCase(mp.get("evaluateStandardIds").toString())) {
                StringBuffer tmpStr = new StringBuffer();
                for (String item : mp.get("evaluateStandardIds").toString().split(",")) {
                    tmpStr.append(standardMap.get(item)).append("，<br>");
                }
                tmpStr.deleteCharAt(tmpStr.length() - 5);
                mp.put("evaluateStandardNames", tmpStr.toString());
            }
        }
        return demandList;
    }

    // 附件上传
    public void saveUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到文件上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        try {
            String id = IdUtil.getId();
            String applyId = toGetParamVal(parameters.get("applyId"));
            String belongSingleId = toGetParamVal(parameters.get("belongSingleId"));
            String fileType = toGetParamVal(parameters.get("fileType"));
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String filePathBase = SysPropertiesUtil.getGlobalProperty("drbfm");
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase");
                return;
            }
            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + applyId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);
            // 写入数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("fileType", fileType);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("relTestTaskId", applyId);
            fileInfo.put("belongSingleId", belongSingleId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("creatorName", ContextUtil.getCurrentUser().getFullname());
            fileInfo.put("CREATE_TIME_", new Date());
            drbfmTestTaskDao.insertDemand(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JsonResult delApplys(String[] applyIdArr) {
        List<String> applyIdList = Arrays.asList(applyIdArr);
        JsonResult result = new JsonResult(true, "操作成功！");
        if (applyIdArr.length == 0) {
            return result;
        }
        JSONObject param = new JSONObject();
        param.put("applyIds", applyIdList);

        // 删除试验数据表
        drbfmTestTaskDao.deleteTestTaskDemand(param);

        // 根据目录删除文件
        String filePathBase = SysPropertiesUtil.getGlobalProperty("drbfm");
        for (String applyId : applyIdList) {
            String filePath = filePathBase + File.separator + applyId;
            File fileDir = new File(filePath);
            FileUtil.removeDir(fileDir);
        }

        // 删除文件表
        drbfmTestTaskDao.deleteDemand(param);

        // 删除主表
        param.put("ids", applyIdList);
        drbfmTestTaskDao.deleteTestTask(param);
        return result;
    }

    private void addRoleParam(Map<String, Object> params, String userId, String userNo) {
        if (userNo.equalsIgnoreCase("admin")) {
            return;
        }
        params.put("currentUserId", userId);
        params.put("roleName", "other");
    }
}
