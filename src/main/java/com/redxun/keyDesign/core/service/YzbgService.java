package com.redxun.keyDesign.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.keyDesign.core.dao.YzbgDao;
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
public class YzbgService {
    private Logger logger = LogManager.getLogger(YzbgService.class);
    @Autowired
    private YzbgDao yzbgDao;
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

    public JsonPageResult<?> queryYzbg(HttpServletRequest request, boolean doPage) {
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
        //addYzbgRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> yzbgList = yzbgDao.queryYzbg(params);
        for (JSONObject yzbg : yzbgList) {
            if (yzbg.get("CREATE_TIME_") != null) {
                yzbg.put("CREATE_TIME_", DateUtil.formatDate((Date) yzbg.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        result.setData(yzbgList);
        int countYzbgDataList = yzbgDao.countYzbgList(params);
        result.setTotal(countYzbgDataList);
        return result;
    }


    public void createYzbg(JSONObject formData) {
        formData.put("bgId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        yzbgDao.createYzbg(formData);
    }

    public void updateYzbg(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        yzbgDao.updateYzbg(formData);
    }

    public void saveYzbgUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("yzbgFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find yzbgFilePathBase");
            return;
        }
        try {
            String belongId = toGetParamVal(parameters.get("detailId"));
            String fileId = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
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
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            yzbgDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getYzbgById(String bgId) {
        JSONObject detailObj = yzbgDao.queryYzbgById(bgId);
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


    public List<JSONObject> getYzbgDetailFileList(HttpServletRequest request) {
        String belongId = RequestUtil.getString(request, "belongId", "");
        Map<String, Object> param = new HashMap<>();
        param.put("belongId", belongId);
        List<JSONObject> YzbgDetailFileList = yzbgDao.queryYzbgDetailFileList(param);
        return YzbgDetailFileList;
    }

    public void createYzbgDetail(JSONObject formData) {
        formData.put("detailId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        yzbgDao.createYzbgDetail(formData);
    }

    public void updateYzbgDetail(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        yzbgDao.updateYzbgDetail(formData);
    }

    public JSONObject getYzbgDetail(String detailId) {
        JSONObject detailObj = yzbgDao.queryYzbgDetailById(detailId);
        if (detailObj == null) {
            return new JSONObject();
        }

        if (detailObj.get("checkTime") != null) {
            detailObj.put("checkTime", DateUtil.formatDate((Date) detailObj.get("checkTime"), "yyyy-MM-dd"));
        }
        return detailObj;
    }

    public List<JSONObject> getYzbgDetailList(HttpServletRequest request) {
        String belongBg = RequestUtil.getString(request, "belongBg", "");
        Map<String, Object> param = new HashMap<>();
        param.put("belongBg", belongBg);
        List<JSONObject> yzbgDetailList = yzbgDao.queryYzbgDetail(param);
        for (JSONObject yzbg : yzbgDetailList) {
            if (yzbg.get("CREATE_TIME_") != null) {
                yzbg.put("CREATE_TIME_", DateUtil.formatDate((Date) yzbg.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (yzbg.get("checkTime") != null) {
                yzbg.put("checkTime", DateUtil.formatDate((Date) yzbg.get("checkTime"), "yyyy-MM-dd"));
            }
        }
        return yzbgDetailList;
    }

    public void deleteOneYzbgFile(String fileId, String fileName, String bgId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, bgId, WebAppUtil.getProperty("yzbgFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("fileId", fileId);
        yzbgDao.deleteYzbgDetailFile(param);
    }

    public JsonResult deleteOneYzbgDetail(String detailId) {
        String yzbgFilePathBase = WebAppUtil.getProperty("yzbgFilePathBase");
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        param.put("detailId", detailId);
        yzbgDao.deleteYzbgDetail(param);
        param.clear();
        param.put("belongId", detailId);
        List<JSONObject> oneDetailFiles = yzbgDao.queryYzbgDetailFileList(param);
        if (oneDetailFiles == null || oneDetailFiles.isEmpty()) {
            return result;
        }
        yzbgDao.deleteYzbgDetailFile(param);
        for (JSONObject oneFile : oneDetailFiles) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"), detailId,
                    WebAppUtil.getProperty("yzbgFilePathBase"));
        }
        rdmZhglFileManager.deleteDirFromDisk(detailId, yzbgFilePathBase);
        return result;
    }

    public JsonResult deleteYzbg(String[] bgIdsArr) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        for (String bgId : bgIdsArr) {
            // 删除基本信息
            param.clear();
            param.put("bgId", bgId);
            yzbgDao.deleteYzbg(param);
            // 查询明细
            param.clear();
            param.put("belongBg", bgId);
            List<JSONObject> yzbgDetailList = yzbgDao.queryYzbgDetail(param);
            if (yzbgDetailList != null && !yzbgDetailList.isEmpty()) {
                // 删除明细和附件
                for (JSONObject oneDetail : yzbgDetailList) {
                    deleteOneYzbgDetail(oneDetail.getString("detailId"));
                }
            }
            rdmZhglFileManager.deleteDirFromDisk(bgId, WebAppUtil.getProperty("yzbgFilePathBase"));
        }
        return result;
    }


}
