package com.redxun.powerApplicationTechnology.core.service;

import java.io.File;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.powerApplicationTechnology.core.dao.PartsResearchDirectionDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.sys.core.manager.SysDicManager;

@Service
public class PartsResearchDirectionService {
    private static Logger logger = LoggerFactory.getLogger(PartsResearchDirectionService.class);
    @Autowired
    private PartsResearchDirectionDao partsResearchDirectionDao;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = partsResearchDirectionDao.dataListQuery(params);
        for (JSONObject business : businessList) {
            if (business.get("CREATE_TIME_") != null) {
                business.put("CREATE_TIME_", DateUtil.formatDate((Date) business.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (business.get("UPDATE_TIME_") != null) {
                business.put("UPDATE_TIME_", DateUtil.formatDate((Date) business.get("UPDATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        int businessListCount = partsResearchDirectionDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    private void getListParams(Map<String, Object> params, HttpServletRequest request) {
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
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
//                    if ("communicateStartTime".equalsIgnoreCase(name)) {
//                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
//                    }
//                    if ("communicateEndTime".equalsIgnoreCase(name)) {
//                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
//                    }
                    params.put(name, value);
                }
            }
        }

        // 增加分页条件
        params.put("startIndex", 0);
        params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
    }

    //..
    public JsonResult deleteData(String id) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = new ArrayList<>();
        businessIds.add(id);
        List<JSONObject> files = this.getFileList(businessIds);
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "powerApplicationTechnologyUploadPosition", "PartsResearchDirection").getValue();
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("businessId"), filePathBase);
        }
        rdmZhglFileManager.deleteDirFromDisk(id, filePathBase);
        partsResearchDirectionDao.deleteItemsByBusinessId(id);
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        partsResearchDirectionDao.deleteFileInfos(param);
        partsResearchDirectionDao.deleteData(param);
        return result;
    }

    //..
    public void saveData(JsonResult result, String dataStr) {
        JSONObject dataObj = JSONObject.parseObject(dataStr);
        if (dataObj == null || dataObj.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        if (StringUtils.isBlank(dataObj.getString("id"))) {
            dataObj.put("id", IdUtil.getId());
            dataObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            dataObj.put("CREATE_TIME_", new Date());
            partsResearchDirectionDao.insertData(dataObj);
            result.setData(dataObj.getString("id"));
        } else {
            dataObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            dataObj.put("UPDATE_TIME_", new Date());
            partsResearchDirectionDao.updateData(dataObj);
            result.setData(dataObj.getString("id"));
        }
    }

    //..
    public JSONObject queryDataById(String businessId) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(businessId)) {
            return result;
        }
        JSONObject businessObj = partsResearchDirectionDao.queryDataById(businessId);
        if (businessObj == null) {
            return result;
        }
        return businessObj;
    }

    //..
    public List<JSONObject> getFileList(List<String> businessIdList) {
        List<JSONObject> fileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIdList);
        fileList = partsResearchDirectionDao.queryFileList(param);
        return fileList;
    }

    //..
    public List<JSONObject> getItemList(String businessId) {
        List<JSONObject> itemList = partsResearchDirectionDao.queryItemList(businessId);
        return itemList;
    }

    //..
    public void deleteOneFile(String fileId, String fileName, String businessId) {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "powerApplicationTechnologyUploadPosition", "PartsResearchDirection").getValue();
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, businessId, filePathBase);
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        partsResearchDirectionDao.deleteFileInfos(param);
    }

    //..
    public void saveItems(JsonResult result, String itemsDataStr) {
        JSONArray itemsObjs = JSONObject.parseArray(itemsDataStr);
        if (itemsObjs == null || itemsObjs.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        for (Object object : itemsObjs) {
            JSONObject itemObj = (JSONObject) object;
            if (StringUtils.isBlank(itemObj.getString("id"))) {
                itemObj.put("id", IdUtil.getId());
                itemObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                itemObj.put("CREATE_TIME_", new Date());
                partsResearchDirectionDao.insertItemData(itemObj);
            } else {
                itemObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                itemObj.put("UPDATE_TIME_", new Date());
                partsResearchDirectionDao.updateItemData(itemObj);
            }
        }
    }

    //..
    public JsonResult deleteItem(String id) {
        JsonResult result = new JsonResult(true, "删除成功！");
        partsResearchDirectionDao.deleteItem(id);
        return result;
    }

    //..
    public void saveUploadFiles(HttpServletRequest request) {
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
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "powerApplicationTechnologyUploadPosition", "PartsResearchDirection").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find FilePathBase");
            return;
        }
        try {
            String businessId = toGetParamVal(parameters.get("businessId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + businessId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);

            // 写入数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("businessId", businessId);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            partsResearchDirectionDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    //..
    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }
}
