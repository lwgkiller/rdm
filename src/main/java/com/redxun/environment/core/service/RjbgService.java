package com.redxun.environment.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.environment.core.dao.RjbgDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
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
public class RjbgService {
    private Logger logger = LogManager.getLogger(RjbgService.class);
    @Autowired
    private RjbgDao rjbgDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    public JsonPageResult<?> queryRjbg(HttpServletRequest request, boolean doPage) {
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
        // addRjbgRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> rjbgList = rjbgDao.queryRjbg(params);
        for (JSONObject rjbg : rjbgList) {
            if (rjbg.get("CREATE_TIME_") != null) {
                rjbg.put("CREATE_TIME_", DateUtil.formatDate((Date)rjbg.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(rjbgList, ContextUtil.getCurrentUserId());
        result.setData(rjbgList);
        int countRjbgDataList = rjbgDao.countRjbgList(params);
        result.setTotal(countRjbgDataList);
        return result;
    }

    public void createRjbg(JSONObject formData) {
        formData.put("rjbgId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        rjbgDao.createRjbg(formData);
        if (StringUtils.isNotBlank(formData.getString("reason"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("reason"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String reasonId = oneObject.getString("reasonId");
                if ("added".equals(state) || StringUtils.isBlank(reasonId)) {
                    oneObject.put("reasonId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("rjbgId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    rjbgDao.createReason(oneObject);
                }
            }
        }
    }

    public void updateRjbg(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        rjbgDao.updateRjbg(formData);
        if (StringUtils.isNotBlank(formData.getString("reason"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("reason"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String reasonId = oneObject.getString("reasonId");
                if ("added".equals(state) || StringUtils.isBlank(reasonId)) {
                    oneObject.put("reasonId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("rjbgId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    rjbgDao.createReason(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    rjbgDao.updateReason(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("reasonId", oneObject.getString("reasonId"));
                    rjbgDao.deleteReason(param);
                }
            }
        }
    }

    public List<JSONObject> queryReason(String belongId) {
        Map<String, Object> param = new HashMap<>();
        param.put("belongId", belongId);
        List<JSONObject> reasonList = rjbgDao.queryReason(param);
        return reasonList;
    }

    public void saveRjbgUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("rjbgFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find rjbgFilePathBase");
            return;
        }
        try {
            String belongId = toGetParamVal(parameters.get("rjbgId"));
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
            rjbgDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getRjbgDetail(String rjbgId) {
        JSONObject detailObj = rjbgDao.queryRjbgById(rjbgId);
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

    public List<JSONObject> getRjbgFileList(List<String> rjbgIdList, String fileType) {
        List<JSONObject> rjbgFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("rjbgIds", rjbgIdList);
        param.put("fileType", fileType);
        rjbgFileList = rjbgDao.queryRjbgFileList(param);
        return rjbgFileList;
    }

    public void deleteOneRjbgFile(String fileId, String fileName, String rjbgId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, rjbgId, WebAppUtil.getProperty("rjbgFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("fileId", fileId);
        param.put("belongId", rjbgId);
        rjbgDao.deleteRjbgFile(param);
    }

    public JsonResult deleteRjbg(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> rjbgIds = Arrays.asList(ids);
        String fileType = null;
        List<JSONObject> files = getRjbgFileList(rjbgIds, fileType);
        String rjbgFilePathBase = WebAppUtil.getProperty("rjbgFilePathBase");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"),
                oneFile.getString("belongId"), rjbgFilePathBase);
        }
        for (String oneJstbId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneJstbId, rjbgFilePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("rjbgIds", rjbgIds);
        rjbgDao.deleteRjbgFile(param);
        rjbgDao.deleteRjbg(param);
        rjbgDao.deleteReason(param);
        return result;
    }

    public JsonResult checkModel(String fdjModel) {
        JsonResult result = new JsonResult(true, "成功");
        Map<String, Object> param = new HashMap<>();
        param.put("fdjModel", fdjModel);
        List<JSONObject> modelInfo = rjbgDao.checkModel(param);
        if (!modelInfo.isEmpty() && modelInfo != null) {
            result.setMessage("该发动机型号存在已提交的流程,确认继续提交?");
            result.setSuccess(false);
            return result;
        }
        return result;
    }
}
