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
import com.redxun.world.core.dao.ZlcdDao;
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
public class ZlcdService {
    private Logger logger = LogManager.getLogger(ZlcdService.class);
    @Autowired
    private ZlcdDao zlcdDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private BpmInstManager bpmInstManager;

    public JsonPageResult<?> queryZlcd(HttpServletRequest request, boolean doPage) {
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
        // addZlcdRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> zlcdList = zlcdDao.queryZlcd(params);
        for (JSONObject zlcd : zlcdList) {
            if (zlcd.get("CREATE_TIME_") != null) {
                zlcd.put("CREATE_TIME_", DateUtil.formatDate((Date)zlcd.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(zlcdList, ContextUtil.getCurrentUserId());
        result.setData(zlcdList);
        int countZlcdDataList = zlcdDao.countZlcdList(params);
        result.setTotal(countZlcdDataList);
        return result;
    }

    public void createZlcd(JSONObject formData) {
        formData.put("zlcdId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        zlcdDao.createZlcd(formData);
        if (StringUtils.isNotBlank(formData.getString("detail"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("detail"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String timeId = oneObject.getString("detailId");
                if ("added".equals(state) || StringUtils.isBlank(timeId)) {
                    oneObject.put("detailId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("zlcdId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    zlcdDao.createDetail(oneObject);
                }
            }
        }
    }

    public void updateZlcd(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        zlcdDao.updateZlcd(formData);
        if (StringUtils.isNotBlank(formData.getString("detail"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("detail"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String timeId = oneObject.getString("detailId");
                if ("added".equals(state) || StringUtils.isBlank(timeId)) {
                    oneObject.put("detailId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("zlcdId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    zlcdDao.createDetail(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    zlcdDao.updateDetail(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("detailId", oneObject.getString("detailId"));
                    zlcdDao.deleteDetail(param);
                }
            }
        }
    }

    public void saveZlcdUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("zlcdFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find zlcdFilePathBase");
            return;
        }
        try {
            String belongId = toGetParamVal(parameters.get("belongd"));
            String fileId = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileModel = toGetParamVal(parameters.get("fileModel"));
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
            fileInfo.put("fileModel", fileModel);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            zlcdDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getZlcdDetail(String zlcdId) {
        JSONObject detailObj = zlcdDao.queryZlcdById(zlcdId);
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.getDate("CREATE_TIME_") != null) {
            detailObj.put("CREATE_TIME_", DateUtil.formatDate(detailObj.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
        }
        return detailObj;
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    public List<JSONObject> getZlcdFileList(String belongId) {
        List<JSONObject> zlcdFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("zlcdId", belongId);
        zlcdFileList = zlcdDao.queryZlcdFileList(param);
        return zlcdFileList;
    }


    public List<JSONObject> getDetailList(String belongId) {
        List<JSONObject> zlcdCnList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("zlcdId", belongId);
        zlcdCnList = zlcdDao.queryZlcdDetail(param);

        return zlcdCnList;
    }

    public void deleteOneZlcdFile(String fileId, String fileName, String zlcdId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, zlcdId, WebAppUtil.getProperty("zlcdFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("fileId", fileId);
        zlcdDao.deleteZlcdFile(param);
    }

    public JsonResult deleteZlcd(String[] zlcdIdsArr, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功！");
        Map<String, Object> param = new HashMap<>();
        for (String zlcdId : zlcdIdsArr) {
            // 删除基本信息
            param.clear();
            param.put("zlcdId", zlcdId);
            zlcdDao.deleteZlcd(param);
            // 查询并删除明细
            param.clear();
            param.put("zlcdId", zlcdId);
            zlcdDao.deleteDetail(param);
            List<JSONObject> zlcdDetailList = zlcdDao.queryZlcdDetail(param);
            if (zlcdDetailList != null && !zlcdDetailList.isEmpty()) {
                // 删除明细附件
                for (JSONObject oneDetail : zlcdDetailList) {
                    String detailId = oneDetail.getString("detailId");
                    List<JSONObject> files = getZlcdFileList(detailId);
                    String wjFilePathBase = WebAppUtil.getProperty("zlcdFilePathBase");
                    for (JSONObject oneFile : files) {
                        rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"),
                                detailId, wjFilePathBase);
                    }
                    rdmZhglFileManager.deleteDirFromDisk(detailId, wjFilePathBase);
                }
            }
        }
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }

    public JsonResult deleteDetail(String[] YdjxIdsArr) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        for (String detailId : YdjxIdsArr) {
            // 删除基本信息
            param.clear();
            param.put("detailId", detailId);
            zlcdDao.deleteDetail(param);
        }
        return result;
    }

}
