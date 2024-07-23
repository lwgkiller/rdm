package com.redxun.world.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.world.core.dao.CkddDao;
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
public class CkddService {
    private Logger logger = LogManager.getLogger(CkddService.class);
    @Autowired
    private CkddDao ckddDao;
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

    public JsonPageResult<?> queryCkdd(HttpServletRequest request, boolean doPage) {
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
        //addCkddRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> ckddList = ckddDao.queryCkdd(params);
        for (JSONObject ckdd : ckddList) {
            if (ckdd.get("CREATE_TIME_") != null) {
                ckdd.put("CREATE_TIME_", DateUtil.formatDate((Date) ckdd.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (ckdd.getDate("ckMonth") != null) {
                ckdd.put("ckMonth", DateUtil.formatDate(ckdd.getDate("ckMonth"), "yyyy-MM-dd"));
            }

        }
        rdmZhglUtil.setTaskInfo2Data(ckddList, ContextUtil.getCurrentUserId());
        result.setData(ckddList);
        int countCkddDataList = ckddDao.countCkddList(params);
        result.setTotal(countCkddDataList);
        return result;
    }




    public void createCkdd(JSONObject formData) {
        formData.put("ckddId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        ckddDao.createCkdd(formData);
        if(StringUtils.isNotBlank(formData.getString("ckdd"))){
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("ckdd"));
            for (int i=0;i<tdmcDataJson.size();i++){
                JSONObject oneObject=tdmcDataJson.getJSONObject(i);
                String state=oneObject.getString("_state");
                String timeId=oneObject.getString("spId");
                if("added".equals(state)|| StringUtils.isBlank(timeId)){
                    oneObject.put("spId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("ckddId"));
                    oneObject.put("config", oneObject.getString("config"));
                    oneObject.put("risk", oneObject.getString("risk"));
                    oneObject.put("measures", oneObject.getString("measures"));
                    oneObject.put("solveId", oneObject.getString("solveId"));
                    oneObject.put("solve", oneObject.getString("solve"));
                    oneObject.put("finishTime", oneObject.getString("finishTime"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    ckddDao.createDetail(oneObject);
                }
            }
        }
        if(StringUtils.isNotBlank(formData.getString("time"))){
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("time"));
            for (int i=0;i<tdmcDataJson.size();i++){
                JSONObject oneObject=tdmcDataJson.getJSONObject(i);
                String state=oneObject.getString("_state");
                String timeId=oneObject.getString("timeId");
                if("added".equals(state)|| StringUtils.isBlank(timeId)){
                    oneObject.put("timeId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("ckddId"));
                    oneObject.put("timeSelect", oneObject.getString("timeSelect"));
                    oneObject.put("timeType", oneObject.getString("timeType"));
                    oneObject.put("total", oneObject.getString("total"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    ckddDao.createTime(oneObject);
                }
            }
        }
    }

    public void updateCkdd(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        ckddDao.updateCkdd(formData);
        if(StringUtils.isNotBlank(formData.getString("ckdd"))){
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("ckdd"));
            for (int i=0;i<tdmcDataJson.size();i++){
                JSONObject oneObject=tdmcDataJson.getJSONObject(i);
                String state=oneObject.getString("_state");
                String timeId=oneObject.getString("spId");
                if("added".equals(state)|| StringUtils.isBlank(timeId)){
                    oneObject.put("spId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("ckddId"));
                    oneObject.put("config", oneObject.getString("config"));
                    oneObject.put("risk", oneObject.getString("risk"));
                    oneObject.put("measures", oneObject.getString("measures"));
                    oneObject.put("solveId", oneObject.getString("solveId"));
                    oneObject.put("solve", oneObject.getString("solve"));
                    oneObject.put("finishTime", oneObject.getString("finishTime"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    ckddDao.createDetail(oneObject);
                }else if("modified".equals(state)){
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    ckddDao.updateDetail(oneObject);
                }else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("spId", oneObject.getString("spId"));
                    ckddDao.deleteDetail(param);
                }
            }
        }
        if(StringUtils.isNotBlank(formData.getString("time"))){
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("time"));
            for (int i=0;i<tdmcDataJson.size();i++){
                JSONObject oneObject=tdmcDataJson.getJSONObject(i);
                String state=oneObject.getString("_state");
                String timeId=oneObject.getString("timeId");
                if("added".equals(state)|| StringUtils.isBlank(timeId)){
                    oneObject.put("timeId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("ckddId"));
                    oneObject.put("timeSelect", oneObject.getString("timeSelect"));
                    oneObject.put("timeType", oneObject.getString("timeType"));
                    oneObject.put("total", oneObject.getString("total"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    ckddDao.createTime(oneObject);
                }else if("modified".equals(state)){
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    ckddDao.updateTime(oneObject);
                }else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("timeId", oneObject.getString("timeId"));
                    ckddDao.deleteTime(param);
                }
            }
        }
    }

    public void saveCkddUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("ckddFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find ckddFilePathBase");
            return;
        }
        try {
            String belongId = toGetParamVal(parameters.get("ckddId"));
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
            ckddDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getCkddDetail(String ckddId) {
        JSONObject detailObj = ckddDao.queryCkddById(ckddId);
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.getDate("ckMonth") != null) {
            detailObj.put("ckMonth", DateUtil.formatDate(detailObj.getDate("ckMonth"), "yyyy-MM-dd"));
        }
        return detailObj;
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }


    public List<JSONObject> getCkddFileList(String belongId) {
        List<JSONObject> ckddFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("ckddId", belongId);
        ckddFileList = ckddDao.queryCkddFileList(param);
        return ckddFileList;
    }

    public List<JSONObject> getTimeList(String belongId) {
        List<JSONObject> ckddCnList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("ckddId", belongId);
        ckddCnList = ckddDao.queryTimeList(param);
        for(JSONObject ckdd:ckddCnList){
            if (ckdd.getDate("timeSelect") != null) {
                ckdd.put("timeSelect", DateUtil.formatDate(ckdd.getDate("timeSelect"), "yyyy-MM-dd"));
            }
        }
        return ckddCnList;
    }

    public List<JSONObject> getDetailList(String belongId) {
        List<JSONObject> ckddCnList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("ckddId", belongId);
        ckddCnList = ckddDao.queryCkddDetail(param);
        for(JSONObject ckdd:ckddCnList){
            if (ckdd.getDate("finishTime") != null) {
                ckdd.put("finishTime", DateUtil.formatDate(ckdd.getDate("finishTime"), "yyyy-MM-dd"));
            }
        }
        return ckddCnList;
    }

    public void deleteOneCkddFile(String fileId, String fileName, String ckddId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, ckddId, WebAppUtil.getProperty("ckddFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("fileId", fileId);
        ckddDao.deleteCkddFile(param);
    }

    public JsonResult deleteCkdd(String ckddId) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<JSONObject> files = getCkddFileList(ckddId);
        String wjFilePathBase = WebAppUtil.getProperty("ckddFilePathBase");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"),
                    oneFile.getString("belongId"), wjFilePathBase);
        }
        rdmZhglFileManager.deleteDirFromDisk(ckddId, wjFilePathBase);
        Map<String, Object> param = new HashMap<>();
        param.put("ckddId", ckddId);
        ckddDao.deleteCkddFile(param);
        ckddDao.deleteCkdd(param);
        ckddDao.deleteDetail(param);
        return result;
    }


    public void insertTime(JSONObject formData) {
        formData.put("timeId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        ckddDao.createTime(formData);
    }

    public void updateTime(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        ckddDao.updateCkdd(formData);
    }



    public JsonResult deleteTime(String[] YdjxIdsArr) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        for (String timeId : YdjxIdsArr) {
            // 删除基本信息
            param.clear();
            param.put("timeId", timeId);
            ckddDao.deleteTime(param);
        }
        return result;
    }
    public JsonResult deleteDetail(String[] YdjxIdsArr) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        for (String spId : YdjxIdsArr) {
            // 删除基本信息
            param.clear();
            param.put("spId", spId);
            ckddDao.deleteDetail(param);
        }
        return result;
    }

    //结构化文档接口

    public JSONObject getApiList(JSONObject result, String searchValue) {

        // 以searchValue为查询条件 返回查询到的列表
      //  String resUrlBase = "http://wjrdm.xcmg.com/rdm/Ckdd/editPage.do?action=detail&ckddId=";
        List<JSONObject> resList = ckddDao.queryApiList(searchValue);
//        for (JSONObject oneRes : resList) {
//            oneRes.put("url", resUrlBase + oneRes.getString("id"));
//        }
        if (resList.size() > 0) {
            result.put("result", resList);
        } else {
            result.put("success", false);
            result.put("message", "无查询结果，请检查搜索条件！");
        }
        return result;
    }
}
