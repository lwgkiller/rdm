package com.redxun.world.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.world.core.dao.PtsyDao;
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
public class PtsyService {
    private Logger logger = LogManager.getLogger(PtsyService.class);
    @Autowired
    private PtsyDao ptsyDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    public JsonPageResult<?> queryPtsy(HttpServletRequest request, boolean doPage) {
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
        //addPtsyRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> ptsyList = ptsyDao.queryPtsy(params);
        for (JSONObject ptsy : ptsyList) {
            if (ptsy.get("CREATE_TIME_") != null) {
                ptsy.put("CREATE_TIME_", DateUtil.formatDate((Date)ptsy.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (ptsy.get("timeNode") != null) {
                ptsy.put("timeNode", DateUtil.formatDate((Date)ptsy.get("timeNode"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(ptsyList, ContextUtil.getCurrentUserId());
        result.setData(ptsyList);
        int countPtsyDataList = ptsyDao.countPtsyList(params);
        result.setTotal(countPtsyDataList);
        return result;
    }




    public void createPtsy(JSONObject formData) {
        formData.put("ptsyId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        ptsyDao.createPtsy(formData);
    }

    public void updatePtsy(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        ptsyDao.updatePtsy(formData);
    }



    public JSONObject getPtsyById(String ptsyId) {
        JSONObject detailObj = ptsyDao.queryPtsyById(ptsyId);
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.get("CREATE_TIME_") != null) {
            detailObj.put("CREATE_TIME_", DateUtil.formatDate((Date)detailObj.get("CREATE_TIME_"), "yyyy-MM-dd"));
        }
        if (detailObj.get("timeNode") != null) {
            detailObj.put("timeNode", DateUtil.formatDate((Date)detailObj.get("timeNode"), "yyyy-MM-dd"));
        }
        return detailObj;
    }


    public List<JSONObject> getPtsyFileList(String belongId,String fileType) {
        List<JSONObject> ptsyFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("ptsyId", belongId);
        param.put("fileType", fileType);
        ptsyFileList = ptsyDao.queryPtsyFileList(param);
        return ptsyFileList;
    }


    public void deleteOnePtsyFile(String fileId, String fileName, String ptsyId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, ptsyId, WebAppUtil.getProperty("ptsyFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("fileId", fileId);
        ptsyDao.deletePtsyFile(param);
    }

    public JsonResult deletePtsy(String[] gyqrIdsArr, String[] instIds) {
        JsonResult result = new JsonResult(true, "operation is successful！");
        Map<String, Object> param = new HashMap<>();
        String fileType=null;
        for (String ptsyId : gyqrIdsArr) {
            param.put("fileType", fileType);
            param.put("ptsyId", ptsyId);
            List<JSONObject> files = getPtsyFileList(ptsyId,fileType);
            String wjFilePathBase = WebAppUtil.getProperty("ptsyFilePathBase");
            for (JSONObject oneFile : files) {
                rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"),
                        oneFile.getString("belongId"), wjFilePathBase);
            }
            rdmZhglFileManager.deleteDirFromDisk(ptsyId, wjFilePathBase);
            ptsyDao.deletePtsyFile(param);
            ptsyDao.deletePtsy(param);
        }
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }


    public void savePtsyUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("ptsyFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find ptsyFilePathBase");
            return;
        }
        try {
            String belongId = toGetParamVal(parameters.get("ptsyId"));
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
            ptsyDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }
    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }
}
