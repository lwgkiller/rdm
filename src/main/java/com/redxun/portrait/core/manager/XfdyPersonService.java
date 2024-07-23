package com.redxun.portrait.core.manager;

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
import com.redxun.portrait.core.dao.XfdyPersonDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
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
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class XfdyPersonService {
    private Logger logger = LogManager.getLogger(XfdyPersonService.class);
    @Autowired
    private XfdyPersonDao xfdyPersonDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;


    public JsonPageResult<?> queryXfdyPerson(HttpServletRequest request, boolean doPage) {
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
        params.put("currentUserId", ContextUtil.getCurrentUserId());
        List<JSONObject> xfdyPersonList = xfdyPersonDao.queryXfdyPerson(params);
        for (JSONObject xfdyPerson : xfdyPersonList) {
            if (xfdyPerson.get("CREATE_TIME_") != null) {
                xfdyPerson.put("CREATE_TIME_", DateUtil.formatDate((Date)xfdyPerson.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        result.setData(xfdyPersonList);
        return result;
    }


    public JSONObject getXfdyPersonDetail(String xfdyId) {
        JSONObject detailObj = xfdyPersonDao.queryXfdyPersonById(xfdyId);
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


    public JsonResult deleteXfdyPerson(String[] xfdyIdsArr) {
        JsonResult result = new JsonResult(true, "操作成功！");
        Map<String, Object> param = new HashMap<>();
        for (String xfdyId : xfdyIdsArr) {
            // 删除基本信息
            param.clear();
            param.put("xfdyId", xfdyId);
            JSONObject detailObj = xfdyPersonDao.queryXfdyPersonById(xfdyId);
            // 删除附件
            String filePathBase = WebAppUtil.getProperty("xfdyFilePathBase");
            String suffix = CommonFuns.toGetFileSuffix(detailObj.getString("fileName"));
            String fileFullPath = filePathBase + File.separator + xfdyId + "."+suffix;
            File file = new File(fileFullPath);
            file.delete();
            xfdyPersonDao.deleteXfdyPerson(param);
        }
        return result;
    }


    //..
    public void saveBusiness(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            Map<String, String[]> parameters = multipartRequest.getParameterMap();
            MultipartFile fileObj = multipartRequest.getFile("businessFile");
            if (parameters == null || parameters.isEmpty()) {
                logger.error("表单内容为空！");
                result.put("message", "操作失败，表单内容为空！");
                result.put("success", false);
                return;
            }
            JSONObject objBody = new JSONObject();
            constructBusinessParam(parameters, objBody);
            addOrUpdateBusiness(objBody, fileObj);
            result.put("message", "保存成功！");
            result.put("xfdyId", objBody.get("xfdyId"));
            result.put("success", true);
        } catch (Exception e) {
            logger.error("Exception in savePublicStandard", e);
            result.put("message", "系统异常！");
            result.put("success", false);
        }
    }

    //..
    private void constructBusinessParam(Map<String, String[]> parameters, Map<String, Object> objBody) {
        if (parameters.get("xfdyId") != null && parameters.get("xfdyId").length != 0
                && StringUtils.isNotBlank(parameters.get("xfdyId")[0])) {
            objBody.put("xfdyId", parameters.get("xfdyId")[0]);
        }
        if (parameters.get("applyId") != null && parameters.get("applyId").length != 0
                && StringUtils.isNotBlank(parameters.get("applyId")[0])) {
            objBody.put("applyId", parameters.get("applyId")[0]);
        }
        if (parameters.get("applyName") != null && parameters.get("applyName").length != 0
                && StringUtils.isNotBlank(parameters.get("applyName")[0])) {
            objBody.put("applyName", parameters.get("applyName")[0]);
        }
        if (parameters.get("deptName") != null && parameters.get("deptName").length != 0
                && StringUtils.isNotBlank(parameters.get("deptName")[0])) {
            objBody.put("deptName", parameters.get("deptName")[0]);
        }
        if (parameters.get("deptId") != null && parameters.get("deptId").length != 0
                && StringUtils.isNotBlank(parameters.get("deptId")[0])) {
            objBody.put("deptId", parameters.get("deptId")[0]);
        }
        if (parameters.get("info") != null && parameters.get("info").length != 0
                && StringUtils.isNotBlank(parameters.get("info")[0])) {
            objBody.put("info", parameters.get("info")[0]);
        }
        if (parameters.get("fileName") != null && parameters.get("fileName").length != 0
                && StringUtils.isNotBlank(parameters.get("fileName")[0])) {
            objBody.put("fileName", parameters.get("fileName")[0]);
        }
    }

    //..
    private void addOrUpdateBusiness(JSONObject objBody, MultipartFile fileObj) throws IOException {
        String xfdyId = objBody.get("xfdyId") == null ? "" : objBody.get("xfdyId").toString();
        if (StringUtils.isBlank(xfdyId)) {
            //新增文件
            String newId = IdUtil.getId();
            if (fileObj != null) {
                updateFile2Disk(newId, fileObj);
                objBody.put("fileName", fileObj.getOriginalFilename());
            }
            objBody.put("xfdyId", newId);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            xfdyPersonDao.createXfdyPerson(objBody);
        } else {
            if (fileObj != null) {
                updateFile2Disk(xfdyId, fileObj);
                objBody.put("fileName", fileObj.getOriginalFilename());
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            xfdyPersonDao.updateXfdyPerson(objBody);
        }
    }

    //..
    private void updateFile2Disk(String id, MultipartFile fileObj) throws IOException {
        if (StringUtils.isBlank(id) || fileObj == null) {
            logger.warn("no id or fileObj");
            return;
        }
        String filePathBase = WebAppUtil.getProperty("xfdyFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find filePathBase");
            return;
        }

        // 处理下载目录的更新
        File pathFile = new File(filePathBase);
        // 目录不存在则创建
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String suffix = CommonFuns.toGetFileSuffix(fileObj.getOriginalFilename());
        String fileFullPath = filePathBase + File.separator + id +"."+ suffix;
        File file = new File(fileFullPath);
        // 文件存在则更新掉
        if (file.exists()) {
            logger.warn("File " + fileFullPath + " will be deleted");
            file.delete();
        }
        FileCopyUtils.copy(fileObj.getBytes(), file);
    }

}
