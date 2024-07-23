package com.redxun.environment.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.environment.core.dao.RjssDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import groovy.util.logging.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

@Service
@Slf4j
public class RjssService {
    private Logger logger = LogManager.getLogger(RjssService.class);
    @Autowired
    private RjssDao rjssDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;


    public JsonPageResult<?> queryRjss(HttpServletRequest request, boolean doPage) {
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
        params.put("currentUserId", ContextUtil.getCurrentUserId());
        //addRjssRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> rjssList = rjssDao.queryRjss(params);
        for (JSONObject rjss : rjssList) {
            if (rjss.get("CREATE_TIME_") != null) {
                rjss.put("CREATE_TIME_", DateUtil.formatDate((Date) rjss.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(rjssList, ContextUtil.getCurrentUserId());
        result.setData(rjssList);
        int countRjssDataList = rjssDao.countRjssList(params);
        result.setTotal(countRjssDataList);
        return result;
    }



    public void createRjss(JSONObject formData) {
        formData.put("rjssId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        rjssDao.createRjss(formData);
        if (StringUtils.isNotBlank(formData.getString("reason"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("detail"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String reasonId = oneObject.getString("reasonId");
                if ("added".equals(state) || StringUtils.isBlank(reasonId)) {
                    oneObject.put("reasonId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("rjssId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    rjssDao.createReason(oneObject);
                }
            }
        }
    }

    public void updateRjss(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        rjssDao.updateRjss(formData);
        if (StringUtils.isNotBlank(formData.getString("reason"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("reason"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String reasonId = oneObject.getString("reasonId");
                if ("added".equals(state) || StringUtils.isBlank(reasonId)) {
                    oneObject.put("reasonId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("rjssId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    rjssDao.createReason(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    rjssDao.updateReason(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("reasonId", oneObject.getString("reasonId"));
                    rjssDao.deleteReason(param);
                }
            }
        }
        if (StringUtils.isNotBlank(formData.getString("detail"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("detail"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String detailId = oneObject.getString("detailId");
                if ("added".equals(state) || StringUtils.isBlank(detailId)) {
                    oneObject.put("detailId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("rjssId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    rjssDao.createDetail(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    rjssDao.updateDetail(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("detailId", oneObject.getString("detailId"));
                    rjssDao.deleteDetail(param);
                }
            }
        }
    }

    public List<JSONObject> queryReason(String belongId) {
        Map<String, Object> param = new HashMap<>();
        param.put("belongId", belongId);
        List<JSONObject> reasonList = rjssDao.queryReason(param);
        return reasonList;
    }

    public List<JSONObject> queryDetail(String belongId) {
        Map<String, Object> param = new HashMap<>();
        param.put("belongId", belongId);
        List<JSONObject> reasonList = rjssDao.queryDetail(param);
        return reasonList;
    }

    public void saveRjssUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("rjssFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find rjssFilePathBase");
            return;
        }
        try {
            String belongId = toGetParamVal(parameters.get("rjssId"));
            String fileId = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileType = toGetParamVal(parameters.get("fileType"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + belongId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + fileId + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);

            // 写入数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("fileId", fileId);
            fileInfo.put("fileName", fileName);
            fileInfo.put("belongId", belongId);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("fileType", fileType);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            rjssDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getRjssDetail(String rjssId) {
        JSONObject detailObj = rjssDao.queryRjssById(rjssId);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }


    public List<JSONObject> getRjssFileList(List<String> rjssIdList,String fileType) {
        List<JSONObject> rjssFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("rjssIds", rjssIdList);
        param.put("fileType", fileType);
        rjssFileList = rjssDao.queryRjssFileList(param);
        return rjssFileList;
    }



    public void deleteOneRjssFile(String fileId, String fileName, String rjssId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, rjssId, WebAppUtil.getProperty("rjssFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("fileId", fileId);
        param.put("belongId", rjssId);
        rjssDao.deleteRjssFile(param);
    }

    public JsonResult deleteRjss(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> rjssIds = Arrays.asList(ids);
        String fileType=null;
        List<JSONObject> files = getRjssFileList(rjssIds,fileType);
        String rjssFilePathBase = WebAppUtil.getProperty("rjssFilePathBase");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"),
                    oneFile.getString("belongId"), rjssFilePathBase);
        }
        for (String oneJstbId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneJstbId, rjssFilePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("rjssIds", rjssIds);
        rjssDao.deleteRjssFile(param);
        rjssDao.deleteRjss(param);
        rjssDao.deleteReason(param);
        return result;
    }


}
