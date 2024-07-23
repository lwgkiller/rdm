package com.redxun.serviceEngineering.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import org.apache.camel.util.FileUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.TopicStandardDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

import static com.redxun.rdmCommon.core.util.RdmCommonUtil.toGetParamVal;

@Service
public class TopicStandardService {
    private static Logger logger = LoggerFactory.getLogger(TopicStandardService.class);
    @Autowired
    private TopicStandardDao topicStandardDao;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    // ..
    public JsonPageResult<?> queryApplyList(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "service_engineering_maintenance_manualfile_standard_archive_base.CREATE_TIME_",
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

        List<JSONObject> applyList = topicStandardDao.dataListQuery(params);
        for (JSONObject oneApply : applyList) {
            if (oneApply.get("CREATE_TIME_") != null) {
                oneApply.put("CREATE_TIME_", DateUtil.formatDate((Date)oneApply.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (oneApply.get("applyTime") != null) {
                oneApply.put("applyTime", DateUtil.formatDate((Date)oneApply.get("applyTime"), "yyyy-MM-dd HH:mm:ss"));
            }
        }

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

    private void addRoleParam(Map<String, Object> params, String userId, String userNo) {
        if (userNo.equalsIgnoreCase("admin")) {
            return;
        }
        params.put("currentUserId", userId);
        params.put("roleName", "other");
    }




    // ..
    public void deleteBusiness(JSONObject result, String id) {
        try {
            String filePathBase = WebAppUtil.getProperty("topicStandardFilePathBase");
            String filePath = filePathBase + File.separator + id;
            File fileDir = new File(filePath);
            FileUtil.removeDir(fileDir);
            topicStandardDao.deleteBusiness(id);
            result.put("message", "删除成功！");
        } catch (Exception e) {
            logger.error("Exception in delete", e);
            result.put("message", "系统异常！");
        }
    }


    // ..

    public List<JSONObject> getStandardTopicInfo(JSONObject params) {
        List<JSONObject> standardList = topicStandardDao.getStandardTopicInfo(params);
        for (JSONObject oneObject : standardList) {
            if (oneObject.get("CREATE_TIME_") != null) {
                oneObject.put("CREATE_TIME_", DateUtil.formatDate((Date)oneObject.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return standardList;
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
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String filePathBase = WebAppUtil.getProperty("topicStandardFilePathBase");
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
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            topicStandardDao.insertFile(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }


    public List<JSONObject> queryFileList(JSONObject params) {
        List<JSONObject> demandList = topicStandardDao.queryFileList(params);
        return demandList;
    }

    public String createTopic(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("creatorDeptId", ContextUtil.getCurrentUser().getMainGroupId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("creatorName", ContextUtil.getCurrentUser().getFullname());
        formData.put("status", "DRAFT");

        formData.put("CREATE_TIME_", new Date());
        // 基本信息
        topicStandardDao.insertBusiness(formData);
        return formData.getString("id");



    }

    public void updateTopic(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        // 基本信息
        topicStandardDao.updateBusiness(formData);


    }

    public JSONObject queryApplyDetail(String id) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(id)) {
            return result;
        }
        JSONObject params = new JSONObject();
        params.put("id", id);
        JSONObject obj = topicStandardDao.queryApplyDetail(params);
        if (obj == null) {
            return result;
        }
//        if (obj.get("applyTime") != null) {
//            obj.put("applyTime", DateUtil.formatDate((Date)obj.get("applyTime"), "yyyy-MM-dd HH:mm:ss"));
//        }
        return obj;
    }


    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        // 这个导出不分页

        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "service_engineering_maintenance_manualfile_standard_archive_base.CREATE_TIME_",
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

        List<JSONObject> attachList = topicStandardDao.queryExportApplyList(params);


        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "合规性文件贯标清单";
        String excelName = nowDate + title;
        String[] fieldNames = {
                "法规标准解读类别", "适用范围","编号", "名称", "年版","章节名称", "topic编号", "topic文本名称", "topic类别"
               };
        String[] fieldCodes = {
                "type", "region","standardIdNumber","standardName", "year","topicName", "topicId", "topicTextName", "topicType"
                };
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(attachList, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);

    }


    public JsonResult confirmComplete(String[] applyIdArr) {
        List<String> applyIdList = Arrays.asList(applyIdArr);
        JsonResult result = new JsonResult(true, "操作成功！");
        if (applyIdArr.length == 0) {
            return result;
        }
        JSONObject param = new JSONObject();
        param.put("applyIds", applyIdList);

        param.put("ids", applyIdList);
        param.put("isComplete", "1");
        topicStandardDao.confirmComplete(param);


        return result;
    }





}
