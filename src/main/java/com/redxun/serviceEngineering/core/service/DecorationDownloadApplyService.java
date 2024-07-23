package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.dao.DecorationDownloadApplyDao;
import com.redxun.serviceEngineering.core.dao.ManualFileDownloadApplyDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

@Service
public class DecorationDownloadApplyService {
    private static final Logger logger = LoggerFactory.getLogger(DecorationDownloadApplyService.class);
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private DecorationDownloadApplyDao decorationDownloadApplyDao;
    @Autowired
    private SysDicManager sysDicManager;

    //..
    public JsonPageResult<?> queryApplyList(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "service_engineering_decoration_downloadapply.CREATE_BY_", "desc");
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
        //增加角色过滤的条件
        addRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> applyList = decorationDownloadApplyDao.queryApplyList(params);
        for (JSONObject oneApply : applyList) {
            if (oneApply.get("CREATE_TIME_") != null) {
                oneApply.put("CREATE_TIME_", DateUtil.formatDate((Date) oneApply.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        //查询当前处理人--非会签
        //xcmgProjectManager.setTaskCurrentUser(businessList);
        //查询当前处理人--会签
        rdmZhglUtil.setTaskInfo2Data(applyList, ContextUtil.getCurrentUserId());
        result.setData(applyList);
        int countApplyList = decorationDownloadApplyDao.countApplyList(params);
        result.setTotal(countApplyList);
        return result;
    }

    //..
    private void addRoleParam(Map<String, Object> params, String userId, String userNo) {
        if (userNo.equalsIgnoreCase("admin")) {
            return;
        }
        params.put("currentUserId", userId);
        params.put("roleName", "other");
    }

    //..
    public JSONObject queryApplyDetail(String id) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(id)) {
            return result;
        }
        JSONObject params = new JSONObject();
        params.put("id", id);
        JSONObject obj = decorationDownloadApplyDao.queryApplyDetail(params);
        if (obj == null) {
            return result;
        }
        return obj;
    }

    //..
    public void saveInProcess(JsonResult result, String data) {
        JSONObject object = JSONObject.parseObject(data);
        if (object == null || object.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        if (StringUtils.isBlank(object.getString("id"))) {
            createApply(object);
            result.setData(object.getString("id"));
        } else {
            updateApply(object);
            result.setData(object.getString("id"));
        }
    }

    //..
    public void createApply(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("creatorDeptId", ContextUtil.getCurrentUser().getMainGroupId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        //基本信息
        decorationDownloadApplyDao.insertApply(formData);
        //成员信息
        demandProcess(formData.getString("id"), formData.getJSONArray("changeDemandGrid"));

    }

    //..
    public void updateApply(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        // 基本信息
        decorationDownloadApplyDao.updateApply(formData);
        // 成员信息
        demandProcess(formData.getString("id"), formData.getJSONArray("changeDemandGrid"));
    }

    //..
    private void demandProcess(String applyId, JSONArray demandArr) {
        if (demandArr == null || demandArr.isEmpty()) {
            return;
        }
        JSONObject param = new JSONObject();
        param.put("ids", new JSONArray());
        for (int index = 0; index < demandArr.size(); index++) {
            JSONObject oneObject = demandArr.getJSONObject(index);
            String id = oneObject.getString("id");
            String state = oneObject.getString("_state");
            if ("added".equals(state) || StringUtils.isBlank(id)) {
                // 新增
                oneObject.put("id", IdUtil.getId());
                oneObject.put("applyId", applyId);
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                decorationDownloadApplyDao.insertDemand(oneObject);
            } else if ("removed".equals(state)) {
                // 删除
                param.getJSONArray("ids").add(oneObject.getString("id"));
            }
        }
        if (!param.getJSONArray("ids").isEmpty()) {
            decorationDownloadApplyDao.deleteDemand(param);
        }
    }

    //..
    public JsonResult delApplys(String[] applyIdArr) {
        List<String> applyIdList = Arrays.asList(applyIdArr);
        JsonResult result = new JsonResult(true, "操作成功！");
        if (applyIdArr.length == 0) {
            return result;
        }
        JSONObject param = new JSONObject();
        param.put("applyIds", applyIdList);
        //删除需求子表
        decorationDownloadApplyDao.deleteDemand(param);
        //查询附件信息
        List<JSONObject> files = decorationDownloadApplyDao.getApplyFiles(param);
        if (files.size() > 0) {
            //删除数据库中的附件信息
            decorationDownloadApplyDao.deleteFile(param);
            //删除磁盘中的附件
            for (JSONObject oneFile : files) {
                deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                        oneFile.getString("applyId"));
            }
            for (String applyId : applyIdList) {
                deleteOneFormDirFromDisk(applyId);
            }
        }
        // 删除主表
        param.put("ids", applyIdList);
        decorationDownloadApplyDao.deleteApply(param);
        return result;
    }

    //..
    public void saveUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        String filePathBase = sysDicManager
                .getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "decorationDownloadApply").getValue();

        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find decorationDownloadApply");
            return;
        }
        try {
            String applyId = toGetParamVal(parameters.get("applyId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

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
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("applyId", applyId);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            decorationDownloadApplyDao.insertFile(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }

    }

    //..
    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    //..
    public void deleteOneFileFromDisk(String fileId, String fileName, String formId) {
        String filePathBase = sysDicManager
                .getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "decorationDownloadApply").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find filePathBase");
            return;
        }
        try {
            // 删除下载目录中文件
            String filePath = filePathBase + File.separator + formId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                return;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + fileId + "." + suffix;
            File file = new File(fileFullPath);
            file.delete();
            // 删除预览目录中pdf文件
            String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
            String filePathPreviewPdf = filePath + File.separator + convertPdfDir;
            File pathFilePreviewPdf = new File(filePathPreviewPdf);
            if (!pathFilePreviewPdf.exists()) {
                return;
            }
            String fileFullPathPreviewPdf = filePathPreviewPdf + File.separator + fileId + ".pdf";
            File previewFilePdf = new File(fileFullPathPreviewPdf);
            previewFilePdf.delete();
        } catch (Exception e) {
            logger.error("Exception in deleteOneFileFromDisk", e);
        }
    }

    //..
    public void deleteOneFormDirFromDisk(String formId) {
        String filePathBase = sysDicManager
                .getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "decorationDownloadApply").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find filePathBase");
            return;
        }
        try {
            // 删除预览目录下的tmp文件夹
            String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
            String filePathPreviewPdf = filePathBase + File.separator + formId + File.separator + convertPdfDir;
            File pathFilePreviewPdf = new File(filePathPreviewPdf);
            pathFilePreviewPdf.delete();

            // 删除下载目录
            String filePath = filePathBase + File.separator + formId;
            File pathFile = new File(filePath);
            pathFile.delete();
        } catch (Exception e) {
            logger.error("Exception in deleteOneFormDirFromDisk", e);
        }
    }

    //..
    public List<JSONObject> queryDemandList(JSONObject params) {
        List<JSONObject> demandList = decorationDownloadApplyDao.queryDemandList(params);
        return demandList;
    }

    //..
    public List<JSONObject> queryFileList(JSONObject params) {
        List<JSONObject> files = decorationDownloadApplyDao.getApplyFiles(params);
        for (JSONObject oneFile : files) {
            if (oneFile.get("CREATE_TIME_") != null) {
                oneFile.put("CREATE_TIME_", DateUtil.formatDate((Date) oneFile.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return files;
    }

}
