package com.redxun.serviceEngineering.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.sys.core.manager.SysDicManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
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
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.ZdwjDao;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Service
public class ZdwjManager {
    private static final Logger logger = LoggerFactory.getLogger(ZdwjManager.class);
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private ZdwjDao zdwjDao;
    @Autowired
    private SysDicManager sysDicManager;


    public JsonPageResult<?> queryApplyList(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "zdwj_info.CREATE_TIME_", "desc");
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

        List<JSONObject> applyList = zdwjDao.queryApplyList(params);
        for (JSONObject oneApply : applyList) {
            if (oneApply.get("CREATE_TIME_") != null) {
                oneApply.put("CREATE_TIME_", DateUtil.formatDate((Date)oneApply.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(applyList, ContextUtil.getCurrentUserId());

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
        JSONObject obj = zdwjDao.queryApplyDetail(params);
        if (obj == null) {
            return result;
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

    public void createZdwj(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("creatorDeptId", ContextUtil.getCurrentUser().getMainGroupId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("creatorName", ContextUtil.getCurrentUser().getFullname());

        formData.put("CREATE_TIME_", new Date());
        // 基本信息
        zdwjDao.insertZdwj(formData);

    }

    public void updateZdwj(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        // 基本信息
        zdwjDao.updateZdwj(formData);
    }

    public JsonResult delApplys(String[] applyIdArr) {
        List<String> applyIdList = Arrays.asList(applyIdArr);
        JsonResult result = new JsonResult(true, "操作成功！");
        if (applyIdArr.length == 0) {
            return result;
        }
        JSONObject param = new JSONObject();
        param.put("applyIds", applyIdList);

        // 删除主表
        param.put("ids", applyIdList);
        // 删除文件
        String id = applyIdList.get(0);
        JSONObject jsonObject = zdwjDao.queryDataById(id);
        String fileName = jsonObject.getString("fileName");
        if (StringUtil.isNotEmpty(fileName)) {
            deleteFileFromDisk(id, jsonObject.getString("fileName"));
        }

        zdwjDao.deleteZdwj(param);
        return result;
    }

    public JSONObject getUserInfoByPartsType(String partsType) {
        Map<String, Object> params = new HashMap<>();
        params.put("partsType", partsType);
        JSONObject userMapInfo = zdwjDao.getUserInfoByPartsType(params);
        return userMapInfo;
    }

    public void saveBusiness(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            Map<String, String[]> parameters = multipartRequest.getParameterMap();
            MultipartFile fileObj = multipartRequest.getFile("businessFile");
            if (parameters == null || parameters.isEmpty()) {
                logger.error("表单内容为空！");
                result.put("message", "操作失败，表单内容为空！");
                result.put("success", false);
                return;
            }

            Map<String, Object> objBody = new HashMap<>();
            constructBusinessParam(parameters, objBody);
            addOrUpdateBusiness(objBody, fileObj);
            result.put("message", "保存成功！");
            result.put("id", objBody.get("id"));
            result.put("success", true);
        } catch (Exception e) {
            logger.error("Exception in saveZdwj", e);
            result.put("message", "系统异常！");
            result.put("success", false);
        }
    }

    private void constructBusinessParam(Map<String, String[]> parameters, Map<String, Object> objBody) {
        if (parameters.get("id") != null && parameters.get("id").length != 0
            && StringUtils.isNotBlank(parameters.get("id")[0])) {
            objBody.put("id", parameters.get("id")[0]);
        }
        if (parameters.get("instId") != null && parameters.get("instId").length != 0
            && StringUtils.isNotBlank(parameters.get("instId")[0])) {
            objBody.put("instId", parameters.get("instId")[0]);
        }

    }

    private void addOrUpdateBusiness(Map<String, Object> objBody, MultipartFile fileObj) throws IOException {
        String id = objBody.get("id") == null ? "" : objBody.get("id").toString();
        if (StringUtils.isBlank(id)) {
            // 从保存草稿进来，不会存在没有id的情况
            // 新增文件
            String newId = IdUtil.getId();
            if (fileObj != null) {
                updateFile2Disk(newId, fileObj);

            }

        } else {
            if (fileObj != null) {
                updateFile2Disk(id, fileObj);
                objBody.put("fileName", fileObj.getOriginalFilename());

            } else {
                // 前台清除文件后，删除磁盘中的文件
                JSONObject jsonObject = zdwjDao.queryDataById(id);
                String fileName = jsonObject.getString("fileName");
                if (StringUtil.isNotEmpty(fileName)) {
                    deleteFileFromDisk(id, jsonObject.getString("fileName"));
                }
                objBody.put("fileName", "");
            }
//             objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
//             objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            zdwjDao.updateBusiness(objBody);
        }
    }

    // ..
    private void updateFile2Disk(String id, MultipartFile fileObj) throws IOException {
        if (StringUtils.isBlank(id) || fileObj == null) {
            logger.warn("no id or fileObj");
            return;
        }
        String filePathBase =
            sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "zdwj").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find filePathBase");
            return;
        }

        // 处理下载目录的更新
        File pathFile = new File(filePathBase);
        // 目录不存在则创建
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String fileName = fileObj.getOriginalFilename();
        String suffix = CommonFuns.toGetFileSuffix(fileName);

        String fileFullPath = filePathBase + File.separator + id + "." + suffix;
        File file = new File(fileFullPath);
        // 文件存在则更新掉
        if (file.exists()) {
            logger.warn("File " + fileFullPath + " will be deleted");
            file.delete();
        }
        // 已存在不同类型的文件则删除
        JSONObject jsonObject = zdwjDao.queryDataById(id);
        if (jsonObject != null) {
            String orgFileName = jsonObject.getString("fileName");
            if (StringUtil.isNotEmpty(orgFileName) && (CommonFuns.toGetFileSuffix(orgFileName) != suffix)) {
                deleteFileFromDisk(id, jsonObject.getString("fileName"));
            }
        }
        FileCopyUtils.copy(fileObj.getBytes(), file);
    }

    private void deleteFileFromDisk(String id, String fileName) {
        if (StringUtils.isBlank(id)) {
            logger.warn("id is blank");
            return;
        }
        String filePathBase =
            sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "zdwj").getValue();
        // 处理下载目录的删除
        String fileFullPath = filePathBase + File.separator + id + "." + CommonFuns.toGetFileSuffix(fileName);
        File file = new File(fileFullPath);
        file.delete();
    }

    public ResponseEntity<byte[]> Download(HttpServletRequest request, String id, String description) {
        try {
            if (StringUtils.isBlank(id)) {
                logger.error("id is blank");
                return null;
            }
            String filePathBase =
                sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "zdwj").getValue();
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase");
                return null;
            }
            String fileName = id + "." + CommonFuns.toGetFileSuffix(description);
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
            logger.error("Exception in zdwjDownload", e);
            return null;
        }
    }

    private File findFile(String id) {
        if (StringUtils.isBlank(id)) {
            logger.warn("id is blank");
            return null;
        }
        String filePathBase =
            sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "zdwj").getValue();
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

    public void Preview(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        File file = findFile(id);
        if (file != null && file.exists()) {
            byte[] data = null;
            try {
                FileInputStream input = new FileInputStream(file);
                data = new byte[input.available()];
                input.read(data);
                response.getOutputStream().write(data);
                input.close();
            } catch (Exception e) {
                logger.error(id + "文件处理异常：" + e.getMessage());
            }
        }
    }
}
