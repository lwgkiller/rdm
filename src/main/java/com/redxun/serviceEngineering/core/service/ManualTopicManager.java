package com.redxun.serviceEngineering.core.service;

import static com.redxun.rdmCommon.core.util.RdmCommonUtil.toGetParamVal;

import java.io.File;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.util.DateFormatUtil;
import com.redxun.serviceEngineering.core.dao.ManualTopicDao;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import org.apache.camel.util.FileUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.dao.ZxdpsDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;

/**
 * 操保手册Toipc
 *
 * @mh 2022年8月8日08:52:13
 */
@Service
public class ManualTopicManager {
    private static final Logger logger = LoggerFactory.getLogger(ManualTopicManager.class);
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private ManualTopicDao manualTopicDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    public JsonPageResult<?> queryApplyList(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "service_engineering_maintenance_manualfile_topic_module.CREATE_TIME_",
            "desc");
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

        // 增加角色过滤的条件
        addRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());

        List<JSONObject> applyList = manualTopicDao.queryApplyList(params);
        for (JSONObject oneApply : applyList) {
            if (oneApply.get("CREATE_TIME_") != null) {
                oneApply.put("CREATE_TIME_", DateUtil.formatDate((Date)oneApply.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (oneApply.get("applyTime") != null) {
                oneApply.put("applyTime", DateUtil.formatDate((Date)oneApply.get("applyTime"), "yyyy-MM-dd HH:mm:ss"));
            }
        }
        // rdmZhglUtil.setTaskInfo2Data(applyList, ContextUtil.getCurrentUserId());

        // 根据分页进行subList截取
        List<JSONObject> finalSubList = new ArrayList<JSONObject>();
        if (doPage) {
            int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
            int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
            int startSubListIndex = pageIndex * pageSize;
            int endSubListIndex = startSubListIndex + pageSize;
            if (startSubListIndex < applyList.size()) {
                finalSubList = applyList.subList(startSubListIndex,
                    endSubListIndex < applyList.size() ? endSubListIndex : applyList.size());
            }
        } else {
            finalSubList = applyList;
        }
        result.setData(finalSubList);
        result.setTotal(applyList.size());
        return result;
    }

    public JSONObject queryApplyDetail(String id) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(id)) {
            return result;
        }
        JSONObject params = new JSONObject();
        params.put("id", id);
        JSONObject obj = manualTopicDao.queryApplyDetail(params);
        if (obj == null) {
            return result;
        }
        if (obj.get("applyTime") != null) {
            obj.put("applyTime", DateUtil.formatDate((Date)obj.get("applyTime"), "yyyy-MM-dd HH:mm:ss"));
        }
        return obj;
    }

    private void addRoleParam(Map<String, Object> params, String userId, String userNo) {
        if (userNo.equalsIgnoreCase("admin")) {
            return;
        }
        params.put("currentUserId", userId);
        params.put("roleName", "other");
    }

    public String createTopic(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("creatorDeptId", ContextUtil.getCurrentUser().getMainGroupId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("creatorName", ContextUtil.getCurrentUser().getFullname());
        formData.put("status", "DRAFT");

        formData.put("CREATE_TIME_", new Date());
        // 基本信息
        manualTopicDao.insertTopic(formData);

        // 标准信息
        demandProcess("standard", formData.getString("id"), formData.getJSONArray("changeStandardGrid"));

        // 对标信息
        demandProcess("benchmarking", formData.getString("id"), formData.getJSONArray("changeBenchmarkingGrid"));

        return formData.getString("id");

    }

    public void updateTopic(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        // 基本信息
        manualTopicDao.updateTopic(formData);

        // 标准信息
        demandProcess("standard", formData.getString("id"), formData.getJSONArray("changeStandardGrid"));

        // 对标信息
        demandProcess("benchmarking", formData.getString("id"), formData.getJSONArray("changeBenchmarkingGrid"));

    }

    public JsonResult delApplys(String[] applyIdArr) {
        List<String> applyIdList = Arrays.asList(applyIdArr);
        JsonResult result = new JsonResult(true, "操作成功！");
        if (applyIdArr.length == 0) {
            return result;
        }
        JSONObject param = new JSONObject();
        param.put("applyIds", applyIdList);

        // 删除子表
        manualTopicDao.deleteStandard(param);
        manualTopicDao.deleteBenchmarking(param);

        // 删除主表
        param.put("ids", applyIdList);
        manualTopicDao.deleteTopic(param);

        // 删除主表对应文件目录【直接将目录中内容遍历删除】
        String applyId = applyIdList.get(0);
        String filePathBase = WebAppUtil.getProperty("manualTopicFilePathBase");
        String filePath = filePathBase + File.separator + applyId;
        File fileDir = new File(filePath);
        FileUtil.removeDir(fileDir);
        return result;
    }

    public JsonResult confirmApplys(String[] applyIdArr) {
        List<String> applyIdList = Arrays.asList(applyIdArr);
        JsonResult result = new JsonResult(true, "操作成功！");
        if (applyIdArr.length == 0) {
            return result;
        }
        JSONObject param = new JSONObject();
        param.put("applyIds", applyIdList);

        param.put("ids", applyIdList);
        param.put("status", "READY");
        manualTopicDao.confirmTopic(param);


        return result;
    }

    public JsonResult confirmApplysCancel(String[] applyIdArr) {
        List<String> applyIdList = Arrays.asList(applyIdArr);
        JsonResult result = new JsonResult(true, "操作成功！");
        if (applyIdArr.length == 0) {
            return result;
        }
        JSONObject param = new JSONObject();
        param.put("applyIds", applyIdList);

        param.put("ids", applyIdList);
        param.put("status", "DRAFT");
        manualTopicDao.confirmTopic(param);


        return result;
    }

    public JsonResult updateVersion(String id) {

        JsonResult result = new JsonResult(true, "操作成功！");

        JSONObject param = new JSONObject();
        param.put("id", id);
        param.put("versionStatus", "history");
        manualTopicDao.updateVersion(param);


        return result;
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
            String detailId = toGetParamVal(parameters.get("detailId"));
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String filePathBase = WebAppUtil.getProperty("manualTopicFilePathBase");
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
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("applyId", applyId);
            fileInfo.put("detailId", detailId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            manualTopicDao.insertFile(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public List<JSONObject> queryDemandList(JSONObject params) {
        List<JSONObject> demandList = manualTopicDao.queryFileList(params);
        return demandList;
    }

    public List<JSONObject> queryStandardList(JSONObject params) {
        List<JSONObject> standardList = manualTopicDao.queryStandardList(params);
        return standardList;
    }

    public List<JSONObject> queryBenchmarkingList(JSONObject params) {
        List<JSONObject> benchmarkingList = manualTopicDao.queryBenchmarkingList(params);
        return benchmarkingList;
    }

    private void demandProcess(String gridType, String applyId, JSONArray demandArr) {
        // gridType: 表格类型 共两种名称，对应两个子表

        if (demandArr == null || demandArr.isEmpty()) {
            return;
        }
        JSONObject param = new JSONObject();
        param.put("ids", new JSONArray());
        for (int index = 0; index < demandArr.size(); index++) {
            JSONObject oneObject = demandArr.getJSONObject(index);
            String id = oneObject.getString("id");
            String state = oneObject.getString("_state");

            if ("added".equals(state) || StringUtils.isBlank(state) || StringUtils.isBlank(id)) {
                // 新增 复制时，会把主表id清空，并把子表的各id也清空，故都会走这里新增
                oneObject.put("id", IdUtil.getId());
                oneObject.put("applyId", applyId);
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                if ("standard".equalsIgnoreCase(gridType)) {
                    manualTopicDao.insertStandard(oneObject);
                } else if ("benchmarking".equalsIgnoreCase(gridType)) {
                    manualTopicDao.insertBenchmarking(oneObject);
                }
            } else if ("removed".equals(state)) {
                // 删除
                removeSubListFiles(id);

                param.getJSONArray("ids").add(oneObject.getString("id"));
            } else if ("modified".equals(state)) {
                // 更新
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                if ("standard".equalsIgnoreCase(gridType)) {
                    manualTopicDao.updateStandard(oneObject);
                } else if ("benchmarking".equalsIgnoreCase(gridType)) {
                    manualTopicDao.updateBenchmarking(oneObject);
                }
            }
        }
        if (!param.getJSONArray("ids").isEmpty()) {
            if ("standard".equalsIgnoreCase(gridType)) {
                manualTopicDao.deleteStandard(param);
            } else if ("benchmarking".equalsIgnoreCase(gridType)) {
                manualTopicDao.deleteBenchmarking(param);
            }
        }
    }

    public List<JSONObject> checkFileList(JSONObject params) {
        List<JSONObject> files = manualTopicDao.queryFileList(params);
        for (JSONObject oneFile : files) {
            if (oneFile.get("CREATE_TIME_") != null) {
                oneFile.put("CREATE_TIME_", DateUtil.formatDate((Date)oneFile.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return files;
    }

    // 根据子表id清除所有关联的文件
    public void removeSubListFiles(String id) {
        JSONObject params = new JSONObject();
        params.put("detailId", id);
        String filePathBase = WebAppUtil.getProperty("manualTopicFilePathBase");
        List<JSONObject> files = manualTopicDao.queryFileList(params);
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                oneFile.getString("applyId"), filePathBase);
        }
    }

    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        // 这个导出不分页

        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "service_engineering_maintenance_manualfile_topic_module.CREATE_TIME_",
            "desc");
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
        // 增加角色过滤的条件
        addRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());

        List<JSONObject> attachList = manualTopicDao.queryExportApplyList(params);

        // List<Map<String, Object>> listData = result.getData();
        // List<JSONObject> attachList = new ArrayList<>();
        //
        // for (Map<String, Object> data : applyList) {
        // JSONObject jsonObject = new JSONObject(data);
        // attachList.add(jsonObject);
        // }

        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "操保手册topic清单";
        String excelName = nowDate + title;
        String[] fieldNames = {"章节名称", "topic编号", "topic文本名称", "topic类别", "区域市场", "产品线", "产品系列", "产品配置", "版本", "版本状态",
            "状态", "法规标准解读类别", "编号", "名称", "年版", "类别", "解读条款", "条款内容", "解读要点总结", "法规状态", "适用范围", "竞品企业", "文件名称", "章节",
            "章节内容", "对标要点总结"};
        String[] fieldCodes = {"topicName", "topicId", "topicTextName", "topicType", "region", "productLine",
            "productSeries", "productSettings", "version", "versionStatus", "status", "type", "standardIdNumber",
            "standardName", "year", "categoryName", "clause", "clauseDetail", "standardConclusion", "standardStatus",
            "standardRegion", "aimCompany", "fileName", "topicInfo", "topicDetail", "aimConclusion"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(attachList, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);

    }

}
