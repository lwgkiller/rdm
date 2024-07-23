package com.redxun.environment.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.environment.core.dao.YjbgDao;
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
public class YjbgService {
    private Logger logger = LogManager.getLogger(YjbgService.class);
    @Autowired
    private YjbgDao yjbgDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    public JsonPageResult<?> queryYjbg(HttpServletRequest request, boolean doPage) {
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
        List<JSONObject> yjbgList = yjbgDao.queryYjbg(params);
        for (JSONObject yjbg : yjbgList) {
            if (yjbg.get("CREATE_TIME_") != null) {
                yjbg.put("CREATE_TIME_", DateUtil.formatDate((Date)yjbg.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }

        rdmZhglUtil.setTaskInfo2Data(yjbgList, ContextUtil.getCurrentUserId());
        result.setData(yjbgList);
        int countYjbgDataList = yjbgDao.countYjbgList(params);
        result.setTotal(countYjbgDataList);
        return result;
    }
    

    public void createYjbg(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        yjbgDao.createYjbg(formData);
    }

    public void updateYjbg(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        yjbgDao.updateYjbg(formData);
    }

    public void saveYjbgUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("yjbgFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find yjbgFilePathBase");
            return;
        }
        try {
            String belongId = toGetParamVal(parameters.get("id"));
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
            yjbgDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getYjbgDetail(String id) {
        JSONObject detailObj = yjbgDao.queryYjbgById(id);
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


    public List<JSONObject> getYjbgFileList(String id,String fileType) {
        List<JSONObject> wjFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("wjId", id);
        param.put("fileType", fileType);
        wjFileList = yjbgDao.queryYjbgFileList(param);
        for (Map<String, Object> yjbg : wjFileList) {
            if (yjbg.get("CREATE_TIME_") != null) {
                yjbg.put("CREATE_TIME_", DateUtil.formatDate((Date)yjbg.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return wjFileList;
    }

    public void deleteOneYjbgFile(String fileId, String fileName, String id) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, id, WebAppUtil.getProperty("yjbgFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("fileId", fileId);
        param.put("belongId", id);
        yjbgDao.deleteYjbgFile(param);
    }

    public JsonResult deleteYjbg(String id) {
        JsonResult result = new JsonResult(true, "操作成功！");
        String fileType=null;
        List<JSONObject> files = getYjbgFileList(id,fileType);
        String wjFilePathBase = WebAppUtil.getProperty("yjbgFilePathBase");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"),
                    oneFile.getString("belongId"), wjFilePathBase);
        }
        rdmZhglFileManager.deleteDirFromDisk(id, wjFilePathBase);
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        yjbgDao.deleteYjbgFile(param);
        yjbgDao.deleteYjbg(param);
        return result;
    }


}
