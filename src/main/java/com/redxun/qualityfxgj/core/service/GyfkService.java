package com.redxun.qualityfxgj.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.qualityfxgj.core.dao.GyfkDao;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
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
public class GyfkService {
    private Logger logger = LogManager.getLogger(GyfkService.class);
    @Autowired
    private GyfkDao gyfkDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private LoginRecordManager loginRecordManager;

    public JsonPageResult<?> queryGyfk(HttpServletRequest request, boolean doPage) {
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
        params.put("currentUserId",ContextUtil.getCurrentUserId());
        //addGyfkRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> gyfkList = gyfkDao.queryGyfk(params);
        for (JSONObject gyfk : gyfkList) {
            if (gyfk.get("CREATE_TIME_") != null) {
                gyfk.put("CREATE_TIME_", DateUtil.formatDate((Date)gyfk.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(gyfkList, ContextUtil.getCurrentUserId());
        result.setData(gyfkList);
        int countGyfkDataList = gyfkDao.countGyfkList(params);
        result.setTotal(countGyfkDataList);
        return result;
    }




    public void createGyfk(JSONObject formData) {
        formData.put("gyfkId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        gyfkDao.createGyfk(formData);
    }

    public void updateGyfk(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        gyfkDao.updateGyfk(formData);
    }

    public void saveGyfkUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("gyfkFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find gyfkFilePathBase");
            return;
        }
        try {
            String belongDetailId = toGetParamVal(parameters.get("gyxjId"));
            String fileId = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + belongDetailId;
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
            fileInfo.put("belongDetailId", belongDetailId);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            gyfkDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getGyfkById(String gyfkId) {
        JSONObject detailObj = gyfkDao.queryGyfkById(gyfkId);
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


    public List<JSONObject> getGyfkDetailFileList(HttpServletRequest request) {
        String belongDetailId = RequestUtil.getString(request, "belongDetailId", "");
        Map<String, Object> param = new HashMap<>();
        param.put("belongDetailId", belongDetailId);
        List<JSONObject> GyfkDetailFileList = gyfkDao.queryGyfkDetailFileList(param);
        return GyfkDetailFileList;
    }

    public void createGyfkDetail(JSONObject formData) {
        formData.put("gyxjId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        gyfkDao.createGyfkDetail(formData);
    }
    public void updateGyfkDetail(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        gyfkDao.updateGyfkDetail(formData);
    }

    public JSONObject getGyfkDetail(String gyxjId) {
        JSONObject detailObj = gyfkDao.queryGyfkDetailById(gyxjId);
        if (detailObj.get("finishTime") != null) {
            detailObj.put("finishTime", DateUtil.formatDate((Date)detailObj.get("finishTime"), "yyyy-MM-dd"));
        }
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    public List<JSONObject> getGyfkDetailList(HttpServletRequest request) {
        String belongGyfkId = RequestUtil.getString(request, "belongGyfkId", "");
        Map<String, Object> param = new HashMap<>();
        param.put("belongGyfkId", belongGyfkId);
        List<JSONObject> gyfkDetailList = gyfkDao.queryGyfkDetail(param);
        for (JSONObject gyfk : gyfkDetailList) {
            if (gyfk.get("finishTime") != null) {
                gyfk.put("finishTime", DateUtil.formatDate((Date)gyfk.get("finishTime"), "yyyy-MM-dd"));
            }
            if (gyfk.get("CREATE_TIME_") != null) {
                gyfk.put("CREATE_TIME_", DateUtil.formatDate((Date)gyfk.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return gyfkDetailList;
    }

    public void deleteOneGyfkFile(String fileId, String fileName, String gyfkId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, gyfkId, WebAppUtil.getProperty("gyfkFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("fileId", fileId);
        gyfkDao.deleteGyfkDetailFile(param);
    }

    public JsonResult deleteOneGyfkDetail(String detailId) {
        String gyfkFilePathBase = WebAppUtil.getProperty("gyfkFilePathBase");
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        param.put("gyxjId", detailId);
        gyfkDao.deleteGyfkDetail(param);
        param.clear();
        param.put("belongDetailId", detailId);
        List<JSONObject> oneDetailFiles = gyfkDao.queryGyfkDetailFileList(param);
        if (oneDetailFiles == null || oneDetailFiles.isEmpty()) {
            return result;
        }
        gyfkDao.deleteGyfkDetailFile(param);
        for (JSONObject oneFile : oneDetailFiles) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"), detailId,
                    WebAppUtil.getProperty("gyfkFilePathBase"));
        }
        rdmZhglFileManager.deleteDirFromDisk(detailId, gyfkFilePathBase);
        return result;
    }
    public JsonResult deleteGyfk(String[] gyfkIdsArr) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        for (String gyfkId : gyfkIdsArr) {
            // 删除基本信息
            param.clear();
            param.put("gyfkId", gyfkId);
            gyfkDao.deleteGyfk(param);
            // 查询明细
            param.clear();
            param.put("belongGyfkId", gyfkId);
            List<JSONObject> gyfkDetailList = gyfkDao.queryGyfkDetail(param);
            if (gyfkDetailList != null && !gyfkDetailList.isEmpty()) {
                // 删除明细和附件
                for (JSONObject oneDetail : gyfkDetailList) {
                    deleteOneGyfkDetail(oneDetail.getString("gyxjId"));
                }
            }
            rdmZhglFileManager.deleteDirFromDisk(gyfkId, WebAppUtil.getProperty("gyfkFilePathBase"));
        }
        return result;
    }


}
