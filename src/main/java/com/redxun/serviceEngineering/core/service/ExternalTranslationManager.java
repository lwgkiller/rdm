package com.redxun.serviceEngineering.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.microsvc.util.Util;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.dao.ExternalTranslationDao;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import static com.redxun.rdmCommon.core.util.RdmCommonUtil.toGetParamVal;

@Service
public class ExternalTranslationManager {
    private static final Logger logger = LoggerFactory.getLogger(ExternalTranslationManager.class);
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    @Autowired
    private ExternalTranslationDao externalTranslationDao;

    @Autowired
    private SysDicManager sysDicManager;

    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    public JsonPageResult<?> queryApplyList(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "service_engineering_exernal_trans_base.CREATE_TIME_", "desc");
        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
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
        // 在主流程中展示的列表queryType=subQuery，增加主表id过滤条件
        String baseInfoId = RequestUtil.getString(request, "baseInfoId", "");
        String queryType = RequestUtil.getString(request, "queryType", "");
        if (StringUtil.isNotEmpty(queryType) && "subQuery".equalsIgnoreCase(queryType)) {
            params.put("baseInfoId", baseInfoId);
            if (StringUtil.isEmpty(baseInfoId)) {
                // 这里为了使mybatis能查询到该条件，在主表Id为空时，给一个用不到的初始值
                params.put("baseInfoId", "udf");
            }
        }

        // 增加角色过滤的条件
        addRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> applyList = externalTranslationDao.queryApplyList(params);
        for (JSONObject oneApply : applyList) {
            if (oneApply.get("CREATE_TIME_") != null) {
                oneApply.put("CREATE_TIME_", DateUtil.formatDate((Date) oneApply.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(applyList, ContextUtil.getCurrentUserId());
        result.setData(applyList);
        int countDataList = externalTranslationDao.countApplyList(params);
        result.setTotal(countDataList);
        return result;
    }

    private void addRoleParam(Map<String, Object> params, String userId, String userNo) {
        if (userNo.equalsIgnoreCase("admin")) {
            return;
        }
        params.put("currentUserId", userId);
        params.put("roleName", "other");
    }

    public JSONObject queryApplyDetail(String id) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(id)) {
            return result;
        }
        JSONObject params = new JSONObject();
        params.put("id", id);
        JSONObject obj = externalTranslationDao.queryApplyDetail(params);
        if (obj == null) {
            return result;
        }
        return obj;
    }

    public void createApply(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("departId", ContextUtil.getCurrentUser().getMainGroupId());
        formData.put("departName", ContextUtil.getCurrentUser().getMainGroupName());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        // 基本信息
        externalTranslationDao.insertApply(formData);
        // 成员信息
        demandProcess(formData.getString("id"), formData.getJSONArray("changeDemandGrid"));

    }

    public void updateApply(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        // 基本信息
        externalTranslationDao.updateApply(formData);
        // 成员信息
        demandProcess(formData.getString("id"), formData.getJSONArray("changeDemandGrid"));
    }

    private void demandProcess(String applyId, JSONArray demandArr) {
        if (demandArr == null || demandArr.isEmpty()) {
            return;
        }
        String fileBasePath = WebAppUtil.getProperty("externalTranslationFilePathBase");
        JSONObject param = new JSONObject();
        param.put("ids", new JSONArray());
        for (int index = 0; index < demandArr.size(); index++) {
            JSONObject oneObject = demandArr.getJSONObject(index);
            String id = oneObject.getString("id");
            String state = oneObject.getString("_state");
            // 这里新增用不到暂且放着
            if ("added".equals(state) || StringUtils.isBlank(id)) {
                // 新增
                oneObject.put("id", IdUtil.getId());
                oneObject.put("applyId", applyId);
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                externalTranslationDao.insertDemand(oneObject);
            } else if ("removed".equals(state)) {
                // 删除
                // 先删除外发文件
                rdmZhglFileManager.deleteOneFileFromDisk(oneObject.getString("outFileId"),
                        oneObject.getString("outFileName"), oneObject.getString("applyId"), fileBasePath);
                if (StringUtils.isNotBlank(oneObject.getString("returnFileId"))) {
                    rdmZhglFileManager.deleteOneFileFromDisk(oneObject.getString("returnFileId"),
                            oneObject.getString("returnFileName"), oneObject.getString("applyId"), fileBasePath);
                }
                param.getJSONArray("ids").add(oneObject.getString("id"));
            } else if ("modified".equals(state)) {
                // 更新
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                externalTranslationDao.updateDemand(oneObject);

            }
        }
        if (!param.getJSONArray("ids").isEmpty()) {
            externalTranslationDao.deleteDemand(param);
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
        String fileBasePath = WebAppUtil.getProperty("externalTranslationFilePathBase");
        // 删除附件
        JSONObject params2 = new JSONObject();
        params2.put("applyId", applyIdList.get(0));
        List<JSONObject> demandList = queryDemandList(params2);
        for (JSONObject oneObject : demandList) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneObject.getString("outFileId"),
                    oneObject.getString("outFileName"), oneObject.getString("applyId"), fileBasePath);
        }
        // 删除需求子表及附件
        externalTranslationDao.deleteDemand(param);
        // 删除主表
        param.put("ids", applyIdList);
        externalTranslationDao.deleteApply(param);
        return result;
    }

    public List<JSONObject> queryDemandList(JSONObject params) {
        List<JSONObject> demandList = externalTranslationDao.queryDemandList(params);
        return demandList;
    }

    private void deleteFileFromDisk(String id) {
        if (StringUtils.isBlank(id)) {
            logger.warn("id is blank");
            return;
        }
        String filePathBase =
                sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "partsAtlasArchive").getValue();
        // 处理下载目录的删除
        String fileFullPath = filePathBase + File.separator + id + ".pdf";
        File file = new File(fileFullPath);
        file.delete();
    }

    private boolean checkFileExist(String id) {
        File file = null;
        file = findFile(id);
        if (file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    // ..
    private File findFile(String id) {
        if (StringUtils.isBlank(id)) {
            logger.warn("id is blank");
            return null;
        }
        String filePathBase =
                sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "partsAtlasArchive").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find filePathBase");
            return null;
        }
        String fileFullPath = filePathBase + File.separator + id + ".pdf";
        File file = new File(fileFullPath);
        if (!file.exists()) {
            return null;
        }
        return file;
    }

//    // todo 预览和下载可能只是pdf的需要全文件
//    public void Preview(HttpServletRequest request, HttpServletResponse response) {
//        String id = RequestUtil.getString(request, "id");
//        File file = findFile(id);
//        if (file != null && file.exists()) {
//            byte[] data = null;
//            try {
//                FileInputStream input = new FileInputStream(file);
//                data = new byte[input.available()];
//                input.read(data);
//                response.getOutputStream().write(data);
//                input.close();
//            } catch (Exception e) {
//                logger.error(id + "文件处理异常：" + e.getMessage());
//            }
//        }
//    }

    // ..
    public ResponseEntity<byte[]> Download(HttpServletRequest request, String id, String description) {
        try {
            if (StringUtils.isBlank(id)) {
                logger.error("id is blank");
                return null;
            }
            String filePathBase = sysDicManager
                    .getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "partsAtlasArchive").getValue();
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase");
                return null;
            }
            String fileName = id + ".pdf";
            String originalPdfFullFilePath = filePathBase + File.separator + fileName;
            File originalPdfFile = new File(originalPdfFullFilePath);
            if (!originalPdfFile.exists()) {
                logger.error("can't find originalPdfFile " + originalPdfFullFilePath);
                return null;
            }
            byte[] fileByteArr = new byte[0];
            fileByteArr = FileUtils.readFileToByteArray(originalPdfFile);

            // 下载文件的名字强制为“编号 标准名.pdf”修改文件名的编码格式
            String downloadFileName = description;
            String finalDownloadFileName = new String(downloadFileName.getBytes("UTF-8"), "ISO8859-1");
            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 finalDownloadFileName
            headers.setContentDispositionFormData("attachment", finalDownloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(fileByteArr, headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in publicDownload", e);
            return null;
        }
    }

    // 附件上传
    public void saveUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到文件上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        try {
            String id = IdUtil.getId();
            String outFileId = IdUtil.getId();
            String applyId = toGetParamVal(parameters.get("applyId"));
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String outFileNum = toGetParamVal(parameters.get("outFileNum"));
            String outRate = toGetParamVal(parameters.get("outRate"));
            String outDesc = toGetParamVal(parameters.get("outDesc"));
            String outFileType = toGetParamVal(parameters.get("outFileType"));
            String outFileTotal = toGetParamVal(parameters.get("outFileTotal"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String filePathBase = WebAppUtil.getProperty("externalTranslationFilePathBase");
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
            String fileFullPath = filePath + File.separator + outFileId + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);
            // 写入数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("outFileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("applyId", applyId);
            fileInfo.put("outFileId", outFileId);
            fileInfo.put("outFileNum", outFileNum);
            fileInfo.put("outRate", outRate);
            fileInfo.put("outDesc", outDesc);
            fileInfo.put("outFileType", outFileType);
            fileInfo.put("outFileTotal", outFileTotal);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            externalTranslationDao.insertDemand(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public void saveReturnFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到文件上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        try {
            // id 要获取表单中的id
            String returnFileId = IdUtil.getId();
            String id = toGetParamVal(parameters.get("detailId"));
            String applyId = toGetParamVal(parameters.get("applyId"));
            String fileName = toGetParamVal(parameters.get("fileName"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String filePathBase = WebAppUtil.getProperty("externalTranslationFilePathBase");
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
            String fileFullPath = filePath + File.separator + returnFileId + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);
            // 更新数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("returnFileName", fileName);
            fileInfo.put("returnFileId", returnFileId);
            fileInfo.put("returnFileTime", new Date());
            fileInfo.put("UPDATE_TIME_", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            //externalTranslationDao.updateDemand(fileInfo);
            //todo:用updateDemandReturnFile替换
            externalTranslationDao.updateReturnFile(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    //..新页面，只显示外发翻译文件
    public JsonPageResult<?> externalTranslationFileListQuery(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "applyTime", "desc");
        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
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
        List<JSONObject> businessList = externalTranslationDao.externalTranslationFileListQuery(params);
        List<JSONObject> multiBusinessList = externalTranslationDao.externalMultilanguagebuildFileListQuery(params);

        businessList.addAll(multiBusinessList);
        int businessListCount = externalTranslationDao.externalTranslationFileListQueryCount(params);
        int multilanguagebusinessListCount =externalTranslationDao.externalMultilanguagebuildFileListQueryCount(params);
        result.setData(businessList);
        int totalNumber =businessListCount +multilanguagebusinessListCount;
        result.setTotal(totalNumber);
        return result;
    }

    //..新功能，保存需求信息
    public void saveDemand(JsonResult result, String businessDataStr) {
        JSONArray businessObjs = JSONObject.parseArray(businessDataStr);
        if (businessObjs == null || businessObjs.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("内容为空，操作失败！");
            return;
        }
        for (Object object : businessObjs) {
            JSONObject businessObj = (JSONObject) object;
            businessObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            businessObj.put("UPDATE_TIME_", new Date());
            externalTranslationDao.updateDemand(businessObj);
        }
    }

    //..新功能，导出外发翻译文件属性信息，关联翻译主流程编号
    public void exportFileListInfo(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "service_engineering_attacheddoctrans_apply.transApplyId", "desc");
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
        List<JSONObject> businessList = externalTranslationDao.externalTranslationFileListQuery(params);

        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "外发翻译清单";
        String excelName = nowDate + title;
        String[] fieldNames = {"翻译申请单号", "外发文件名称", "文件类型", "文件数量", "外发字符数", "重复率", "备注说明"};
        String[] fieldCodes = {"transApplyId", "outFileName", "outFileType", "outFileTotal", "outFileNum", "outRate", "outDesc"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(businessList, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    //..新功能，保存4属性信息
    public void update4Attribute(JsonResult result, String dataStr) {
        JSONArray jsonArray = JSONObject.parseArray(dataStr);
        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
            jsonObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            jsonObject.put("UPDATE_TIME_", new Date());
            externalTranslationDao.update4Attribute(jsonObject);
        }
    }
}
