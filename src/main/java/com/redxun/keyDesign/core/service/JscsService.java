package com.redxun.keyDesign.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.keyDesign.core.dao.JscsDao;
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
import org.apache.commons.io.FileUtils;
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
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class JscsService {
    private Logger logger = LogManager.getLogger(JscsService.class);
    @Autowired
    private JscsDao jscsDao;
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

    public JsonPageResult<?> queryJscs(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String belongbj = RequestUtil.getString(request, "belongbj");
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
                    if ("codeNames".equalsIgnoreCase(name)) {
                        JSONArray systemIdArr = JSONArray.parseArray(value);
                        if (systemIdArr != null && !systemIdArr.isEmpty()) {
                            params.put(name, systemIdArr.toJavaList(String.class));
                        }
                    } else {
                        params.put(name, value);
                    }
                }
            }
        }
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        params.put("currentUserId", ContextUtil.getCurrentUserId());
        params.put("belongbj", belongbj);
        // addJscsRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> jscsList = jscsDao.queryJscs(params);
        for (JSONObject jscs : jscsList) {
            if (jscs.get("CREATE_TIME_") != null) {
                jscs.put("CREATE_TIME_", DateUtil.formatDate((Date)jscs.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        result.setData(jscsList);
        int countJscsDataList = jscsDao.countJscsList(params);
        result.setTotal(countJscsDataList);
        return result;
    }

    public void createJscs(JSONObject formData) {
        formData.put("jsId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        jscsDao.createJscs(formData);
    }

    public void updateJscs(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        jscsDao.updateJscs(formData);
    }

    public void copyJscs(JSONObject formData) {
        formData.put("jsId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        jscsDao.createJscs(formData);
        Map<String, Object> param = new HashMap<>();
        param.put("belongJs", formData.getString("oldjsId"));
        List<JSONObject> jscsDetailList = jscsDao.queryJscsDetail(param);
        for(JSONObject oneObject:jscsDetailList){
            String oldcsId = oneObject.getString("csId");
            String newcsId = IdUtil.getId();
            oneObject.put("csId",newcsId);
            oneObject.put("belongJs", formData.getString("jsId"));
            oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            oneObject.put("CREATE_TIME_", new Date());
            jscsDao.createJscsDetail(oneObject);
            copySaveFile(oldcsId,newcsId);
        }
    }

    private void copySaveFile(String oldcsId, String newcsId) {
        String bfileBasePath = WebAppUtil.getProperty("jscsFilePathBase");
        Map<String, Object> param = new HashMap<>();
        param.put("belongCs", oldcsId);
        List<JSONObject> jscsDetailFileList = jscsDao.queryJscsDetailFileList(param);
        for(JSONObject fileJson:jscsDetailFileList){
            String bfileName = fileJson.getString("fileName");
            String bfileId = fileJson.getString("fileId");
            String belongCs = fileJson.getString("belongCs");
            String bsuffix = CommonFuns.toGetFileSuffix(bfileName);
            String relativeFilePath = "";
            if (StringUtils.isNotBlank(belongCs)) {
                relativeFilePath = File.separator + belongCs;
            }
            String brealFileName = bfileId + "." + bsuffix;
            String bfullFilePath = bfileBasePath + relativeFilePath + File.separator + brealFileName;
            File bfile = new File(bfullFilePath);
            String fileId = IdUtil.getId();
            String fileSize = fileJson.getString("fileSize");
            // 向下载目录中写入文件
            String filePath = bfileBasePath + File.separator + newcsId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String fileFullPath = filePath + File.separator + fileId + "." + bsuffix;
            File file = new File(fileFullPath);
            try {
                FileCopyUtils.copy(FileUtils.readFileToByteArray(bfile), file);
                JSONObject fileInfo = new JSONObject();
                fileInfo.put("fileId", fileId);
                fileInfo.put("fileName", bfileName);
                fileInfo.put("fileSize", fileSize);
                fileInfo.put("belongCs", newcsId);
                fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                fileInfo.put("CREATE_TIME_", new Date());
                jscsDao.addFileInfos(fileInfo);
            } catch (IOException e) {
                logger.error("文件加载失败", e.getMessage());
            }
        }
    }


    public void saveJscsUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("jscsFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find jscsFilePathBase");
            return;
        }
        try {
            String belongCs = toGetParamVal(parameters.get("csId"));
            String fileId = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + belongCs;
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
            fileInfo.put("belongCs", belongCs);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            jscsDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getJscsById(String jsId) {
        JSONObject detailObj = jscsDao.queryJscsById(jsId);
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

    public List<JSONObject> getJscsDetailFileList(HttpServletRequest request) {
        String belongCs = RequestUtil.getString(request, "belongCs", "");
        Map<String, Object> param = new HashMap<>();
        param.put("belongCs", belongCs);
        List<JSONObject> JscsDetailFileList = jscsDao.queryJscsDetailFileList(param);
        return JscsDetailFileList;
    }

    public void createJscsDetail(JSONObject formData) {
        formData.put("csId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        jscsDao.createJscsDetail(formData);
    }

    public void updateJscsDetail(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        jscsDao.updateJscsDetail(formData);
    }

    public JSONObject getJscsDetail(String csId) {
        JSONObject detailObj = jscsDao.queryJscsDetailById(csId);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    public List<JSONObject> getJscsDetailList(HttpServletRequest request) {
        String belongJs = RequestUtil.getString(request, "belongJs", "");
        String belongOldJs = RequestUtil.getString(request, "belongOldJs", "");
        String jstype = RequestUtil.getString(request, "jstype", "");
        Map<String, Object> param = new HashMap<>();
        if ("old".equals(jstype) && StringUtils.isBlank(belongJs)) {
            param.put("belongJs", belongOldJs);
        } else {
            param.put("belongJs", belongJs);
        }
        List<JSONObject> jscsDetailList = jscsDao.queryJscsDetail(param);
        for (JSONObject jscs : jscsDetailList) {
            if (jscs.get("CREATE_TIME_") != null) {
                jscs.put("CREATE_TIME_", DateUtil.formatDate((Date)jscs.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if ("old".equals(jstype) && StringUtils.isBlank(belongJs)) {
                param.put("belongJs", belongOldJs);
            }
        }
        return jscsDetailList;
    }

    public void deleteOneJscsFile(String fileId, String fileName, String jsId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, jsId, WebAppUtil.getProperty("jscsFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("fileId", fileId);
        jscsDao.deleteJscsDetailFile(param);
    }

    public JsonResult deleteOneJscsDetail(String detailId) {
        String jscsFilePathBase = WebAppUtil.getProperty("jscsFilePathBase");
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        param.put("csId", detailId);
        jscsDao.deleteJscsDetail(param);
        param.clear();
        param.put("belongCs", detailId);
        List<JSONObject> oneDetailFiles = jscsDao.queryJscsDetailFileList(param);
        if (oneDetailFiles == null || oneDetailFiles.isEmpty()) {
            return result;
        }
        jscsDao.deleteJscsDetailFile(param);
        for (JSONObject oneFile : oneDetailFiles) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"),
                detailId, WebAppUtil.getProperty("jscsFilePathBase"));
        }
        rdmZhglFileManager.deleteDirFromDisk(detailId, jscsFilePathBase);
        return result;
    }

    public JsonResult deleteJscs(String[] jsIdsArr) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        for (String jsId : jsIdsArr) {
            // 删除基本信息
            param.clear();
            param.put("jsId", jsId);
            jscsDao.deleteJscs(param);
            // 查询明细
            param.clear();
            param.put("belongJs", jsId);
            List<JSONObject> jscsDetailList = jscsDao.queryJscsDetail(param);
            if (jscsDetailList != null && !jscsDetailList.isEmpty()) {
                // 删除明细和附件
                for (JSONObject oneDetail : jscsDetailList) {
                    deleteOneJscsDetail(oneDetail.getString("csId"));
                }
            }
            rdmZhglFileManager.deleteDirFromDisk(jsId, WebAppUtil.getProperty("jscsFilePathBase"));
        }
        return result;
    }

}
