package com.redxun.matPriceReview.core.service;

import java.io.File;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.redxun.core.query.Page;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.matPriceReview.core.dao.MatPriceReviewDao;
import com.redxun.materielextend.core.dao.MaterielApplyDao;
import com.redxun.materielextend.core.util.CommonUtil;
import com.redxun.materielextend.core.util.ResultData;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2020 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2020/6/6 15:07
 */
@Service
public class MatPriceReviewService {
    private static final Logger logger = LoggerFactory.getLogger(MatPriceReviewService.class);
    @Autowired
    private MatPriceReviewDao matPriceReviewDao;
    @Autowired
    private MaterielApplyDao materielApplyDao;
    @Autowired
    private MatPriceCallOAService matPriceCallOAService;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    public JsonPageResult<?> getReviewList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            JSONObject params = new JSONObject();
            // 传入条件的构建
            toGetApplyListParams(params, request, doPage);
            params.put("reviewCategory", RequestUtil.getString(request, "reviewCategory", "common"));
            // 查询申请单
            List<JSONObject> applyList = matPriceReviewDao.queryMatPriceReviewList(params);
            for (JSONObject oneApply : applyList) {
                String sqCommitTime = oneApply.getString("CREATE_TIME_");
                if (StringUtils.isNotBlank(sqCommitTime)) {
                    oneApply.put("CREATE_TIME_", DateFormatUtil.formatDateTime(oneApply.getDate("CREATE_TIME_")));
                }
            }
            // 查询申请单总数
            int applyListTotal = matPriceReviewDao.countMatPriceReviewList(params);
            result.setData(applyList);
            result.setTotal(applyListTotal);
        } catch (Exception e) {
            logger.error("Exception in getReviewList", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }

    public JSONObject getObjDetailById(String id) {
        List<JSONObject> result = matPriceReviewDao.queryMatPriceReviewById(id);
        if (result == null || result.isEmpty()) {
            return new JSONObject();
        } else {
            return result.get(0);
        }
    }

    public ResultData applyDelete(List<String> applyIds) {
        ResultData resultData = new ResultData();
        if (applyIds == null || applyIds.isEmpty()) {
            return resultData;
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("ids", applyIds);
            // 删除基本信息
            matPriceReviewDao.deleteApplyBaseInfos(params);
            // 删除物料
            params.clear();
            params.put("reviewIds", applyIds);
            matPriceReviewDao.deleteMatDetails(params);
            // 删除信息记录
            matPriceReviewDao.deleteRecords(params);
            // 查出附件基本信息
            List<JSONObject> fileInfos = matPriceReviewDao.getFileList(params);
            // 删除附件基本信息
            matPriceReviewDao.deleteFiles(params);
            // 删除磁盘附件
            if (fileInfos != null && !fileInfos.isEmpty()) {
                for (JSONObject oneFile : fileInfos) {
                    rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                        oneFile.getString("reviewId"), WebAppUtil.getProperty("jgspFilePathBase"));
                }
            }
        } catch (Exception e) {
            logger.error("Exception in applyDelete", e);
            resultData.setSuccess(false);
            resultData.setMessage("系统异常");
        }
        return resultData;
    }

    public ResultData applySaveOrCommit(String postBody, String scene) {
        ResultData resultData = new ResultData();
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        if (postBodyObj == null || postBodyObj.isEmpty()) {
            resultData.setSuccess(false);
            resultData.setMessage("操作失败，发送内容为空！");
            return resultData;
        }
        try {
            // 调用oa创建
            if ("commit".equalsIgnoreCase(scene)) {
                String oaId = matPriceCallOAService.callOaAddReview(postBodyObj, resultData);
                postBodyObj.put("oaFormId", oaId);
            }
            saveApply(postBodyObj, resultData);
            resultData.setMessage("操作成功!");
        } catch (Exception e) {
            logger.error("Exception in applySaveOrCommit", e);
            resultData.setSuccess(false);
            resultData.setMessage("操作失败，系统异常!");
        }
        return resultData;
    }

    private void saveApply(JSONObject postBodyObj, ResultData resultData) {
        String mobile = postBodyObj.getString("applyUserMobile");
        if (StringUtils.isNotBlank(mobile)) {
            // 更新申请人的联系电话
            Map<String, Object> params = new HashMap<>();
            params.put("userId", postBodyObj.getString("applyUserId"));
            params.put("mobile", mobile);
            materielApplyDao.updateApplyerMobile(params);
        }

        String id = postBodyObj.getString("id");
        // 新增申请单
        if (StringUtils.isBlank(id)) {
            id = IdUtil.getId();
            postBodyObj.put("id", id);
            // 生成单号
            String applyNo = CommonUtil.toGetMatPriceReviewApplyNo();
            postBodyObj.put("applyNo", applyNo);
            postBodyObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            postBodyObj.put("CREATE_TIME_", new Date());
            matPriceReviewDao.addMatPriceReviewApply(postBodyObj);
        } else {
            // 更新申请单
            postBodyObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            postBodyObj.put("UPDATE_TIME_", new Date());
            matPriceReviewDao.updateMatPriceReviewApply(postBodyObj);
        }
        resultData.setData(id);
    }

    public List<JSONObject> getFileListByFormId(HttpServletRequest request) {
        String formId = RequestUtil.getString(request, "formId");
        Map<String, Object> params = new HashMap<>();
        params.put("reviewId", formId);
        List<JSONObject> fileInfos = matPriceReviewDao.getFileList(params);
        for (JSONObject oneFile : fileInfos) {
            oneFile.put("CREATE_TIME_", DateUtil.formatDate(oneFile.getDate("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
        }
        return fileInfos;
    }

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
            String formId = toGetParamVal(parameters.get("formId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String filePathBase = WebAppUtil.getProperty("jgspFilePathBase");
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase");
                return;
            }
            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + formId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);

            // 写入数据库
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("id", id);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", toGetParamVal(parameters.get("fileSize")));
            fileInfo.put("reviewId", formId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            matPriceReviewDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public List<JSONObject> getMatDetailList(HttpServletRequest request, HttpServletResponse response) {
        try {
            JSONObject params = new JSONObject();
            // 传入条件的构建
            String reviewId = RequestUtil.getString(request, "reviewId", "");
            if (StringUtils.isBlank(reviewId)) {
                return new ArrayList<>();
            }
            params.put("reviewId", reviewId);
            List<JSONObject> matDetailList = matPriceReviewDao.queryMatDetailList(params);
            return matDetailList;
        } catch (Exception e) {
            logger.error("Exception in getMatDetailList", e);
        }
        return new ArrayList<>();
    }

    public ResultData matDetailSave(String postBody) {
        ResultData resultData = new ResultData();
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        if (postBodyObj == null || postBodyObj.isEmpty()) {
            resultData.setSuccess(false);
            resultData.setMessage("操作失败，发送内容为空！");
            return resultData;
        }
        try {
            String id = postBodyObj.getString("id");
            // 新增申请单
            if (StringUtils.isBlank(id)) {
                id = IdUtil.getId();
                postBodyObj.put("id", id);
                postBodyObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                postBodyObj.put("CREATE_TIME_", new Date());
                matPriceReviewDao.addMatDetail(postBodyObj);
            } else {
                // 更新申请单
                postBodyObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                postBodyObj.put("UPDATE_TIME_", new Date());
                matPriceReviewDao.updateMatDetail(postBodyObj);
            }
            resultData.setMessage("操作成功!");
        } catch (Exception e) {
            logger.error("Exception in matDetailSave", e);
            resultData.setSuccess(false);
            resultData.setMessage("操作失败，系统异常!");
        }
        return resultData;
    }

    /**
     * 根据物料号，从物料扩充中查询信息
     * 
     * @param matCode
     * @return
     */
    public ResultData syncMatInfo(String matCode) {
        ResultData resultData = new ResultData();
        try {
            // 从物料扩充中查找该物料最后一次的扩充记录(最后一次需要是success)
            JSONObject params = new JSONObject();
            params.put("wlhm", matCode);
            List<JSONObject> matInfos = matPriceReviewDao.queryMatExtendSuccess(params);
            if (matInfos == null || matInfos.isEmpty()) {
                resultData.setSuccess(false);
                resultData.setMessage("同步失败，未找到已扩充物料信息!");
                return resultData;
            }
            resultData.setData(matInfos.get(0));
            resultData.setMessage("同步成功!");
        } catch (Exception e) {
            logger.error("Exception in syncJclx", e);
            resultData.setSuccess(false);
            resultData.setMessage("同步失败，系统异常!");
        }
        return resultData;
    }

    public ResultData deleteMatDetailByIds(List<String> materielIds) {
        ResultData resultData = new ResultData();
        if (materielIds == null || materielIds.isEmpty()) {
            return resultData;
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("ids", materielIds);
            matPriceReviewDao.deleteMatDetails(params);
        } catch (Exception e) {
            logger.error("Exception in deleteMatDetailByIds", e);
            resultData.setSuccess(false);
            resultData.setMessage("系统异常");
        }
        return resultData;
    }

    public List<JSONObject> getRecordList(HttpServletRequest request, HttpServletResponse response) {
        try {
            JSONObject params = new JSONObject();
            String reviewId = RequestUtil.getString(request, "reviewId", "");
            if (StringUtils.isBlank(reviewId)) {
                return new ArrayList<>();
            }
            // 传入条件的构建
            params.put("reviewId", reviewId);
            params.put("jclx", RequestUtil.getString(request, "jclx", "fjc"));
            List<JSONObject> recordList = matPriceReviewDao.queryRecordList(params);
            return recordList;
        } catch (Exception e) {
            logger.error("Exception in getRecordList", e);
        }
        return new ArrayList<>();
    }

    public JSONObject getRecordDetailById(String id) {
        JSONObject params = new JSONObject();
        params.put("id", id);
        List<JSONObject> result = matPriceReviewDao.queryRecordList(params);
        if (result == null || result.isEmpty()) {
            return new JSONObject();
        } else {
            return result.get(0);
        }
    }

    public ResultData recordSave(String postBody) {
        ResultData resultData = new ResultData();
        JSONObject postBodyObj = JSONObject.parseObject(postBody);

        try {
            String id = postBodyObj.getString("id");
            // 新增申请单
            if (StringUtils.isBlank(id)) {
                id = IdUtil.getId();
                postBodyObj.put("id", id);
                postBodyObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                postBodyObj.put("CREATE_TIME_", new Date());
                matPriceReviewDao.addRecord(postBodyObj);
            } else {
                // 更新申请单
                postBodyObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                postBodyObj.put("UPDATE_TIME_", new Date());
                matPriceReviewDao.updateRecord(postBodyObj);
            }
            resultData.setMessage("操作成功!");
        } catch (Exception e) {
            logger.error("Exception in recordSave", e);
            resultData.setSuccess(false);
            resultData.setMessage("操作失败，系统异常!");
        }
        return resultData;
    }

    public ResultData deleteRecordByIds(List<String> ids) {
        ResultData resultData = new ResultData();
        if (ids == null || ids.isEmpty()) {
            return resultData;
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("ids", ids);
            matPriceReviewDao.deleteRecords(params);
        } catch (Exception e) {
            logger.error("Exception in deleteRecordByIds", e);
            resultData.setSuccess(false);
            resultData.setMessage("系统异常");
        }
        return resultData;
    }

    public ResultData syncRecordInfo(String matCode, String reviewId, String jclx) {
        ResultData resultData = new ResultData();
        try {
            JSONObject recordObj = new JSONObject();

            JSONObject params = new JSONObject();
            params.put("reviewId", reviewId);
            params.put("matCode", matCode);

            // 从物料扩充中查找该物料最后一次的扩充记录(最后一次需要是success)
            params.put("wlhm", matCode);
            List<JSONObject> matInfos = matPriceReviewDao.queryMatExtendSuccess(params);
            if (matInfos != null && !matInfos.isEmpty()) {
                recordObj = matInfos.get(0);
            }
            // 从申请单中查找物料名称
            List<JSONObject> matDetailList = matPriceReviewDao.queryMatDetailList(params);
            if (matDetailList != null && !matDetailList.isEmpty()) {
                if (StringUtils.isNotBlank(matDetailList.get(0).getString("matName"))) {
                    recordObj.put("wlms", matDetailList.get(0).getString("matName"));
                }
            }
            String mrpkzz = recordObj.getString("mrpkzz");
            if (StringUtils.isNotBlank(mrpkzz) && ("502".equalsIgnoreCase(mrpkzz) || "503".equalsIgnoreCase(mrpkzz))) {
                recordObj.put("recordType", "1");
            } else {
                recordObj.put("recordType", "0");
            }
            resultData.setData(recordObj);
            resultData.setMessage("同步成功!");
        } catch (Exception e) {
            logger.error("Exception in syncRecordInfo", e);
            resultData.setSuccess(false);
            resultData.setMessage("同步失败，系统异常!");
        }
        return resultData;
    }

    /**
     * 自动生成集采、非集采信息记录
     * 
     * @param reviewId
     * @param jclx
     * @return
     */
    public ResultData generateRecord(String reviewId, String jclx) {
        ResultData resultData = new ResultData();
        try {
            JSONObject recordObj = new JSONObject();

            JSONObject params = new JSONObject();
            params.put("reviewId", reviewId);
            // 从物料扩充中查找该物料最后一次的扩充记录(最后一次需要是success)
            List<JSONObject> matInfos = matPriceReviewDao.queryMatExtendSuccess(params);
            if (matInfos != null && !matInfos.isEmpty()) {
                recordObj = matInfos.get(0);
            }
            // 从申请单中查找物料名称
            List<JSONObject> matDetailList = matPriceReviewDao.queryMatDetailList(params);
            if (matDetailList != null && !matDetailList.isEmpty()) {
                if (StringUtils.isNotBlank(matDetailList.get(0).getString("matName"))) {
                    recordObj.put("wlms", matDetailList.get(0).getString("matName"));
                }
            }
            String mrpkzz = recordObj.getString("mrpkzz");
            if (StringUtils.isNotBlank(mrpkzz) && ("502".equalsIgnoreCase(mrpkzz) || "503".equalsIgnoreCase(mrpkzz))) {
                recordObj.put("recordType", "1");
            } else {
                recordObj.put("recordType", "0");
            }
            resultData.setMessage("操作成功!");
        } catch (Exception e) {
            logger.error("Exception in generateRecord", e);
            resultData.setSuccess(false);
            resultData.setMessage("操作失败，系统异常!");
        }
        return resultData;
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    private void toGetApplyListParams(JSONObject params, HttpServletRequest request, boolean doPage) {
        // 排序
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "CREATE_TIME_ desc");
        }
        // 分页
        if (doPage) {
            String pageIndex = RequestUtil.getString(request, "pageIndex", "0");
            String pageSize = RequestUtil.getString(request, "pageSize", String.valueOf(Page.DEFAULT_PAGE_SIZE));
            String startIndex = String.valueOf(Integer.parseInt(pageIndex) * Integer.parseInt(pageSize));
            params.put("startIndex", startIndex);
            params.put("pageSize", pageSize);
        }

        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    if ("startTime".equalsIgnoreCase(name)) {
                        value += " 00:00:00";
                    }
                    if ("endTime".equalsIgnoreCase(name)) {
                        value += " 23:59:59";
                    }
                    params.put(name, value);
                }
            }
        }
    }

}
