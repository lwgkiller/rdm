package com.redxun.digitization.core.service;

import java.io.File;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.digitization.core.dao.RdmDevTaskDao;
import com.redxun.rdmCommon.core.util.RdmCommonUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;

import groovy.util.logging.Slf4j;

@Service
@Slf4j
public class RdmDevTaskService {
    private Logger logger = LogManager.getLogger(RdmDevTaskService.class);
    @Autowired
    private RdmDevTaskDao rdmDevTaskDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;

    public JsonPageResult<?> getDevList(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "CREATE_TIME_", "desc");
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
        params.put("currentUserId", ContextUtil.getCurrentUserId());
        List<JSONObject> devTaskList = rdmDevTaskDao.queryDevTaskList(params);
        RdmCommonUtil.formatCreateTime(devTaskList, "");
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUserJSON(devTaskList);
        result.setData(devTaskList);
        int countDevTaskList = rdmDevTaskDao.countDevTaskFileList(params);
        result.setTotal(countDevTaskList);
        return result;
    }

    public void createDevInfo(JSONObject formData) {
        formData.put("applyId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        rdmDevTaskDao.createRdmDevTask(formData);
    }

    public void updateDevInfo(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        rdmDevTaskDao.updateRdmDevTask(formData);
    }

    public JSONObject getDevInfoById(String applyId) {
        JSONObject detailObj = rdmDevTaskDao.getDevInfoById(applyId);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    public List<JSONObject> getDevFileList(String applyId) {
        if (StringUtils.isBlank(applyId)) {
            return Collections.emptyList();
        }
        Map<String, Object> param = new HashMap<>();
        param.put("applyId", applyId);
        List<JSONObject> fileList = rdmDevTaskDao.queryDevTaskFileList(param);
        RdmCommonUtil.formatCreateTime(fileList, "");
        return fileList;
    }

    public void saveUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        String filePathBase = WebAppUtil.getProperty("rdmDevTaskFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find rdmDevTaskFilePathBase");
            return;
        }
        try {
            String applyId = RdmCommonUtil.toGetParamVal(parameters.get("applyId"));
            String id = IdUtil.getId();
            String fileName = RdmCommonUtil.toGetParamVal(parameters.get("fileName"));
            String fileSize = RdmCommonUtil.toGetParamVal(parameters.get("fileSize"));

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
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            rdmDevTaskDao.addDevTaskFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public void delDevTaskFile(String fileId, String fileName, String formId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, formId,
            WebAppUtil.getProperty("rdmDevTaskFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        rdmDevTaskDao.deleteDevTaskFile(param);
    }

    public JsonResult deleteDevTask(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> applyIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("applyIds", applyIds);
        List<JSONObject> files = rdmDevTaskDao.queryDevTaskFileList(param);
        String rdmDevTaskFilePathBase = WebAppUtil.getProperty("rdmDevTaskFilePathBase");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                oneFile.getString("applyId"), rdmDevTaskFilePathBase);
        }
        for (String oneId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneId, rdmDevTaskFilePathBase);
        }
        rdmDevTaskDao.deleteDevTaskFile(param);
        rdmDevTaskDao.deleteDevTask(param);
        return result;
    }

}
