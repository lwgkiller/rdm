package com.redxun.environment.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.environment.core.dao.RjscDao;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
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
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Service
@Slf4j
public class RjscService {
    private Logger logger = LogManager.getLogger(RjscService.class);
    @Autowired
    private RjscDao rjscDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    public JsonPageResult<?> queryRjsc(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "rjNo");
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
        // 增加分页条件
        List<JSONObject> rjscList = rjscDao.queryRjsc(params);
        for (JSONObject rjsc : rjscList) {
            if (rjsc.getDate("CREATE_TIME_") != null) {
                rjsc.put("CREATE_TIME_", DateUtil.formatDate(rjsc.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        result.setData(rjscList);
        int countQbgzDataList = rjscDao.countRjscList(params);
        result.setTotal(countQbgzDataList);
        return result;
    }
    public void insertRjsc(JSONObject formData) {
        formData.put("rjId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        rjscDao.insertRjsc(formData);
    }

    public void updateRjsc(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        rjscDao.updateRjsc(formData);
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }
    public void saveRjscUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("rjscFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find rjscFilePathBase");
            return;
        }
        try {
            String belongId = toGetParamVal(parameters.get("rjId"));
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
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("belongId", belongId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            rjscDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getRjscDetail(String standardId) {
        JSONObject detailObj = rjscDao.queryRjscById(standardId);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    public List<JSONObject> getRjscFileList(List<String> rjIdList) {
        List<JSONObject> rjscFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("rjIds", rjIdList);
        rjscFileList = rjscDao.queryRjscFileList(param);
        return rjscFileList;
    }

    public void deleteOneRjscFile(String fileId, String fileName, String rjId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, rjId, WebAppUtil.getProperty("rjscFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        param.put("belongId", rjId);
        rjscDao.deleteRjscFile(param);
    }

    public JsonResult deleteRjsc(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> rjIds = Arrays.asList(ids);
        List<JSONObject> files = getRjscFileList(rjIds);
        String rjscFilePathBase = WebAppUtil.getProperty("rjscFilePathBase");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"),
                    oneFile.getString("rjId"), rjscFilePathBase);
        }
        for (String onerjsc : ids) {
            rdmZhglFileManager.deleteDirFromDisk(onerjsc, rjscFilePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("rjIds", rjIds);
        rjscDao.deleteRjscFile(param);
        rjscDao.deleteRjsc(param);
        return result;
    }



}
