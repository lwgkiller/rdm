package com.redxun.zlgjNPI.core.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.zlgjNPI.core.dao.FsjDao;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
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
public class FsjService {
    private Logger logger = LogManager.getLogger(FsjService.class);
    @Autowired
    private FsjDao fsjDao;
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

    public JsonPageResult<?> queryFsj(HttpServletRequest request, boolean doPage) {
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
        //addFsjRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> fsjList = fsjDao.queryFsj(params);
        for (JSONObject fsj : fsjList) {
            if (fsj.get("CREATE_TIME_") != null) {
                fsj.put("CREATE_TIME_", DateUtil.formatDate((Date)fsj.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(fsjList, ContextUtil.getCurrentUserId());
        result.setData(fsjList);
        int countFsjDataList = fsjDao.countFsjList(params);
        result.setTotal(countFsjDataList);
        return result;
    }




    public void createFsj(JSONObject formData) {
        formData.put("fsjId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        fsjDao.createFsj(formData);
    }

    public void updateFsj(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        fsjDao.updateFsj(formData);
    }

    public void saveFsjUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("fsjFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find fsjFilePathBase");
            return;
        }
        try {
            String belongDetailId = toGetParamVal(parameters.get("detailId"));
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
            fsjDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getFsjById(String fsjId) {
        JSONObject detailObj = fsjDao.queryFsjById(fsjId);
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


    public List<JSONObject> getFsjDetailFileList(HttpServletRequest request) {
        String belongDetailId = RequestUtil.getString(request, "belongDetailId", "");
        Map<String, Object> param = new HashMap<>();
        param.put("belongDetailId", belongDetailId);
        List<JSONObject> FsjDetailFileList = fsjDao.queryFsjDetailFileList(param);
        return FsjDetailFileList;
    }

    public void createFsjDetail(JSONObject formData) {
        formData.put("detailId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        fsjDao.createFsjDetail(formData);
    }
    public void updateFsjDetail(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        fsjDao.updateFsjDetail(formData);
    }

    public JSONObject getFsjDetail(String detailId) {
        JSONObject detailObj = fsjDao.queryFsjDetailById(detailId);
        if (detailObj.get("wcrq") != null) {
            detailObj.put("wcrq", DateUtil.formatDate((Date)detailObj.get("wcrq"), "yyyy-MM-dd"));
        }
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    public List<JSONObject> getFsjDetailList(HttpServletRequest request) {
        String belongId = RequestUtil.getString(request, "belongId", "");
        Map<String, Object> param = new HashMap<>();
        param.put("belongId", belongId);
        List<JSONObject> fsjDetailList = fsjDao.queryFsjDetail(param);
        for (JSONObject fsj : fsjDetailList) {
            if (fsj.get("wcrq") != null) {
                fsj.put("wcrq", DateUtil.formatDate((Date)fsj.get("wcrq"), "yyyy-MM-dd"));
            }
            if (fsj.get("CREATE_TIME_") != null) {
                fsj.put("CREATE_TIME_", DateUtil.formatDate((Date)fsj.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return fsjDetailList;
    }

    public void deleteOneFsjFile(String fileId, String fileName, String fsjId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, fsjId, WebAppUtil.getProperty("fsjFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("fileId", fileId);
        fsjDao.deleteFsjDetailFile(param);
    }

    public JsonResult deleteOneFsjDetail(String detailId) {
        String fsjFilePathBase = WebAppUtil.getProperty("fsjFilePathBase");
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        param.put("detailId", detailId);
        fsjDao.deleteFsjDetail(param);
        param.clear();
        param.put("belongDetailId", detailId);
        List<JSONObject> oneDetailFiles = fsjDao.queryFsjDetailFileList(param);
        if (oneDetailFiles == null || oneDetailFiles.isEmpty()) {
            return result;
        }
        fsjDao.deleteFsjDetailFile(param);
        for (JSONObject oneFile : oneDetailFiles) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"), detailId,
                    WebAppUtil.getProperty("fsjFilePathBase"));
        }
        rdmZhglFileManager.deleteDirFromDisk(detailId, fsjFilePathBase);
        return result;
    }
    public JsonResult deleteFsj(String[] fsjIdsArr) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        for (String fsjId : fsjIdsArr) {
            // 删除基本信息
            param.clear();
            param.put("fsjId", fsjId);
            fsjDao.deleteFsj(param);
            // 查询明细
            param.clear();
            param.put("belongId", fsjId);
            List<JSONObject> fsjDetailList = fsjDao.queryFsjDetail(param);
            if (fsjDetailList != null && !fsjDetailList.isEmpty()) {
                // 删除明细和附件
                for (JSONObject oneDetail : fsjDetailList) {
                    deleteOneFsjDetail(oneDetail.getString("detailId"));
                }
            }
            rdmZhglFileManager.deleteDirFromDisk(fsjId, WebAppUtil.getProperty("fsjFilePathBase"));
        }
        return result;
    }


}
