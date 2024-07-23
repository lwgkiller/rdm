package com.redxun.rdmZhgl.core.service;

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
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.dao.GjjsDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgJsjl.core.manager.XcmgJsjlManager;
import com.redxun.xcmgjssjk.core.service.JssjkFileManager;

@Service
public class GjjsService {
    private static Logger logger = LoggerFactory.getLogger(XcmgJsjlManager.class);
    @Autowired
    private GjjsDao gjjsDao;

    @Autowired
    private JssjkFileManager jssjkFileManager;

    public void saveOrCommitGjjsData(JsonResult result, String newGjjsDataStr) {
        JSONObject newGjjsObj = JSONObject.parseObject(newGjjsDataStr);
        if (newGjjsObj == null || newGjjsObj.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        if (StringUtils.isBlank(newGjjsObj.getString("pId"))) {
            newGjjsObj.put("pId", IdUtil.getId());
            newGjjsObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            newGjjsObj.put("CREATE_TIME_", new Date());
            gjjsDao.insertGjjsData(newGjjsObj);
            result.setData(newGjjsObj.getString("pId"));
        } else {
            newGjjsObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            newGjjsObj.put("UPDATE_TIME_", new Date());
            gjjsDao.updateGjjsData(newGjjsObj);
            result.setData(newGjjsObj.getString("pId"));
            JSONArray jiShuArr = newGjjsObj.getJSONArray("jishuArr");
            if (jiShuArr != null && jiShuArr.size() > 0) {
                for (int index = 0; index < jiShuArr.size(); index++) {
                    JSONObject jiShuObj = jiShuArr.getJSONObject(index);
                    jiShuObj.put("zId", newGjjsObj.getString("pId"));
                    saveOrUpdateOneJishu(jiShuObj);
                }
            }
        }
    }

    public void saveJiShu(JsonResult result, String jiShuDataStr, String meetingId) {
        JSONArray jiShuObjs = JSONObject.parseArray(jiShuDataStr);
        if (jiShuObjs == null || jiShuObjs.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        for (Object object : jiShuObjs) {
            JSONObject jiShuObj = (JSONObject)object;
            jiShuObj.put("zId", meetingId);
            saveOrUpdateOneJishu(jiShuObj);
        }
    }

    private void saveOrUpdateOneJishu(JSONObject jiShuObj) {
        if (StringUtils.isBlank(jiShuObj.getString("xId"))) {
            jiShuObj.put("xId", IdUtil.getId());
            jiShuObj.put("jsName", jiShuObj.getString("jsName"));
            jiShuObj.put("jssp", jiShuObj.getString("jssp"));
            jiShuObj.put("beizhu", jiShuObj.getString("beizhu"));
            jiShuObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            jiShuObj.put("CREATE_TIME_", new Date());
            gjjsDao.insertJiShuData(jiShuObj);
        } else {
            jiShuObj.put("jsName", jiShuObj.getString("jsName"));
            jiShuObj.put("jssp", jiShuObj.getString("jssp"));
            jiShuObj.put("beizhu", jiShuObj.getString("beizhu"));
            jiShuObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            jiShuObj.put("UPDATE_TIME_", new Date());
            gjjsDao.updateJiShuData(jiShuObj);
        }
    }

    public JsonPageResult<?> queryListData(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        addOrder(request, params, "proName,jdTime", "desc");
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if ("jdTimeStart".equalsIgnoreCase(name)) {
                    if (StringUtils.isNotBlank(value)) {
                        params.put("jdTimeStart", DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8)));
                    }
                } else if ("jdTimeEnd".equalsIgnoreCase(name)) {
                    if (StringUtils.isNotBlank(value)) {
                        params.put("jdTimeEnd", DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16)));
                    }
                } else {
                    params.put(name, value);
                }

            }
        }
        // 增加分页条件
        if (doPage) {
            addPage(request, params);
        }
        List<JSONObject> dataList = gjjsDao.queryList(params);
        int countList = gjjsDao.countList(params);
        result.setData(dataList);
        result.setTotal(countList);
        return result;
    }

    public void addOrder(HttpServletRequest request, Map<String, Object> params, String defaultSortField,
        String defaultSortOrder) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", defaultSortField);
            params.put("sortOrder", StringUtils.isBlank(defaultSortOrder) ? "asc" : defaultSortOrder);
        }
    }

    public void addPage(HttpServletRequest request, Map<String, Object> params) {
        params.put("startIndex", 0);
        params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
    }

    public List<JSONObject> getJiShuList(String meetingId) {
        JSONObject paramJson = new JSONObject();
        paramJson.put("meetingId", meetingId);
        List<JSONObject> jiShuList = gjjsDao.getJiShuList(paramJson);
        return jiShuList;
    }

    public JsonResult deleteJiShu(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> jiShuId = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("jiShuId", jiShuId);
        gjjsDao.deleteJiShu(param);
        return result;
    }

    public void saveUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到文件上传的参数");
            return;
        }

        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        try {
            String standardId = toGetParamVal(parameters.get("standardId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fjlx = toGetParamVal(parameters.get("fjlx"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String filePathBase = WebAppUtil.getProperty("gjjsmkFilePathBase");
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase or filePathBase_preview");
                return;
            }
            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + standardId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);
            // 写入数据库-证书附件表
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("zId", standardId);
            fileInfo.put("fjlx", fjlx);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", toGetParamVal(parameters.get("fileSize")));
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            fileInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("UPDATE_TIME_", new Date());
            gjjsDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONArray getFiles(HttpServletRequest request) {
        String standardId = RequestUtil.getString(request, "standardId");
        String fileName = RequestUtil.getString(request, "fileName");
        String fjlx = RequestUtil.getString(request, "fjlx");
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(standardId)) {
            params.put("standardId", standardId);
        }
        if (StringUtils.isNotBlank(fileName)) {
            params.put("fileName", fileName);
        }
        if (StringUtils.isNotBlank(fjlx)) {
            params.put("fjlx", fjlx);
        }
        JSONArray fileArray = gjjsDao.getFiles(params);
        return fileArray;
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    public JsonResult deleteGjjs(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> gjjsIds = Arrays.asList(ids);
        List<JSONObject> files = getGjjsFileList(gjjsIds);
        String gjjsFilePathBase = WebAppUtil.getProperty("gjjsmkFilePathBase");
        for (JSONObject oneFile : files) {
            jssjkFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                oneFile.getString("zId"), gjjsFilePathBase);
        }
        for (String oneGjjsId : ids) {
            jssjkFileManager.deleteDirFromDisk(oneGjjsId, gjjsFilePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("gjjsIds", gjjsIds);
        gjjsDao.deleteGjjsFile(param);
        gjjsDao.deleteJishu(param);
        gjjsDao.deleteGjjs(param);
        return result;
    }

    public List<JSONObject> getGjjsFileList(List<String> gjjsIdList) {
        List<JSONObject> gjjsFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("gjjsIds", gjjsIdList);
        gjjsFileList = gjjsDao.queryGjjsFileList(param);
        return gjjsFileList;
    }
}
