package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.dao.ZljsjdsDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

@Service
public class ZljsjdsService {
    private Logger logger = LoggerFactory.getLogger(ZljsjdsService.class);
    @Autowired
    private ZljsjdsDao zljsjdsDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    // 查询列表
    public JsonPageResult<?> getJsjdsList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> param = new HashMap<>();
            String filterParams = request.getParameter("filter");
            if (StringUtils.isNotBlank(filterParams)) {
                JSONArray jsonArray = JSONArray.parseArray(filterParams);
                for (int i = 0; i < jsonArray.size(); i++) {
                    String name = jsonArray.getJSONObject(i).getString("name");
                    String value = jsonArray.getJSONObject(i).getString("value");
                    if (StringUtils.isNotBlank(value)) {
                        if ("bmscwcTimeStart".equalsIgnoreCase(name)) {
                            value += " 00:00:00";
                        }
                        if ("bmscwcTimeEnd".equalsIgnoreCase(name)) {
                            value += " 23:59:59";
                        }
                        param.put(name, value);
                    }
                }
            }
            rdmZhglUtil.addOrder(request, param, "CREATE_TIME_", "desc");
            List<JSONObject> jsjdsTasks = zljsjdsDao.queryJsjdsTasks(param);
            for (JSONObject oneObject : jsjdsTasks) {
                oneObject.put("CREATE_TIME_",
                    DateUtil.formatDate(DateUtil.addHour(oneObject.getDate("CREATE_TIME_"), -8), "yyyy-MM-dd"));
                if (oneObject.getDate("bmscwcTime") != null) {
                    oneObject.put("bmscwcTime",
                        DateUtil.formatDate(DateUtil.addHour(oneObject.getDate("bmscwcTime"), -8), "yyyy-MM-dd"));
                }
            }
            // 向业务数据中写入任务相关的信息
            rdmZhglUtil.setTaskInfo2Data(jsjdsTasks, ContextUtil.getCurrentUserId());
            // 根据角色过滤
            jsjdsTasks = filterListByDepRole(jsjdsTasks);
            // 根据当前任务过滤
            if (param.get("taskName") != null && StringUtils.isNotBlank(param.get("taskName").toString())) {
                jsjdsTasks = filterListByTaskName(jsjdsTasks, param.get("taskName").toString());
            }
            List<JSONObject> finalSubList = new ArrayList<JSONObject>();
            // 根据分页进行subList截取
            int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
            int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
            int startSubListIndex = pageIndex * pageSize;
            int endSubListIndex = startSubListIndex + pageSize;
            if (startSubListIndex < jsjdsTasks.size()) {
                finalSubList = jsjdsTasks.subList(startSubListIndex,
                    endSubListIndex < jsjdsTasks.size() ? endSubListIndex : jsjdsTasks.size());
            }
            result.setData(finalSubList);
            result.setTotal(jsjdsTasks.size());
        } catch (Exception e) {
            logger.error("Exception in queryCxDevList", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }

    public JSONObject getJsjdsDetail(String jsjdsId) {
        JSONObject detailObj = zljsjdsDao.queryJsjdsById(jsjdsId);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    private List<JSONObject> filterListByTaskName(List<JSONObject> jsjdsTasks, String taskName) {
        List<JSONObject> result = new ArrayList<JSONObject>();
        if (jsjdsTasks == null || jsjdsTasks.isEmpty()) {
            return result;
        }
        // 过滤
        for (JSONObject oneProject : jsjdsTasks) {
            String oneDataTaskName = oneProject.getString("taskName");
            if (StringUtils.isNotBlank(oneDataTaskName) && oneDataTaskName.contains(taskName)) {
                result.add(oneProject);
            }
        }
        return result;

    }

    private List<JSONObject> filterListByDepRole(List<JSONObject> jsjdsTasks) {
        List<JSONObject> result = new ArrayList<JSONObject>();
        if (jsjdsTasks == null || jsjdsTasks.isEmpty()) {
            return result;
        }
        // 管理员查看所有的包括草稿数据
        if ("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
            return jsjdsTasks;
        }
        // 过滤
        for (JSONObject oneProject : jsjdsTasks) {
            // 自己是当前流程处理人
            if (StringUtils.isNotBlank(oneProject.getString("myTaskId"))) {
                result.add(oneProject);
            } else if (StringUtils.isNotBlank(oneProject.getString("instStatus"))
                && "DRAFTED".equals(oneProject.getString("instStatus"))) {
                if (oneProject.getString("CREATE_BY_").equals(ContextUtil.getCurrentUserId())) {
                    result.add(oneProject);
                }
            } else {
                result.add(oneProject);
            }
        }
        return result;

    }

    public JsonResult deleteJsjds(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> jsjdsIds = Arrays.asList(ids);
        List<JSONObject> files = getJsjdsFileList(jsjdsIds);
        String jsjdsFilePathBase = WebAppUtil.getProperty("zljsjdsFilePathBase");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                oneFile.getString("jsjdsId"), jsjdsFilePathBase);
        }
        for (String oneJsmmId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneJsmmId, jsjdsFilePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("jsjdsIds", jsjdsIds);
        zljsjdsDao.deleteJsjdsFile(param);
        zljsjdsDao.deleteJsjds(param);
        return result;
    }

    public void createJsjds(JSONObject formData) {
        formData.put("jsjdsId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        zljsjdsDao.insertJsjds(formData);
    }

    public void updateJsjds(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        zljsjdsDao.updateJsjds(formData);
    }

    public List<JSONObject> getJsjdsFileList(List<String> jsjdsIdList) {
        List<JSONObject> jsjdsFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("jsjdsIds", jsjdsIdList);
        jsjdsFileList = zljsjdsDao.queryJsjdsFileList(param);
        return jsjdsFileList;
    }

    public List<JSONObject> selectProjectPlan(HttpServletRequest request) {
        Map<String, Object> param = new HashMap<>();
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                param.put(name, value);

            }
        }
        List<JSONObject> planList = zljsjdsDao.selectProjectPlan(param);
        for (JSONObject msg : planList) {
            if (msg.getDate("output_time") != null) {
                msg.put("output_time", DateUtil.formatDate(msg.getDate("output_time"), "yyyy-MM-dd"));
            }
        }
        return planList;
    }

    public void saveUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("zljsjdsFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find zljsjdsFilePathBase");
            return;
        }
        try {
            String jsjdsId = toGetParamVal(parameters.get("jsjdsId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileType = toGetParamVal(parameters.get("fileType"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + jsjdsId;
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
            fileInfo.put("fileType", fileType);
            fileInfo.put("jsjdsId", jsjdsId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            zljsjdsDao.addJsjdsFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public void deleteOneJsjdsFile(String fileId, String fileName, String jsjdsId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, jsjdsId,
            WebAppUtil.getProperty("zljsjdsFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        zljsjdsDao.deleteJsjdsFile(param);
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    // 增加成果计划
    public void saveOutList(JSONObject formDataJson) {
        JSONObject oneObject = new JSONObject();
        // 项目id
        String projectId = formDataJson.getString("projectId");
        // 计划id
        String outPlanId = formDataJson.getString("planId");
        // 实际描述
        String outDescription = formDataJson.getString("reportName");
        // 专利名称
        String outName = formDataJson.getString("reportName");
        // 专利Id
        String outReferId = formDataJson.getString("zgzlId");
        oneObject.put("projectId", projectId);
        oneObject.put("outPlanId", outPlanId);
        oneObject.put("outDescription", outDescription);
        oneObject.put("outName", outName);
        oneObject.put("outReferId", outReferId);

        // 刪除原有的关联
        List<JSONObject> existObjs = xcmgProjectOtherDao.queryExistOutByReferId(oneObject);
        if (existObjs != null && !existObjs.isEmpty()) {
            JSONObject param = new JSONObject();
            List<String> ids = new ArrayList<>();
            for (JSONObject oneData : existObjs) {
                ids.add(oneData.getString("id"));
            }
            param.put("ids", ids);
            xcmgProjectOtherDao.deleteOut(param);
        }
        // 新增
        oneObject.put("id", IdUtil.getId());
        oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        // 根据类型，塞入不同的跳转url
        String outUrl = toGetOutUrlByType(outReferId);
        oneObject.put("outUrl", outUrl);
        xcmgProjectOtherDao.insertOut(oneObject);
    }

    // 对于专利，判断数据库中是否已存在成果的关联
    private boolean findExistOutLink(JSONObject oneObject) {
        List<JSONObject> existObjs = xcmgProjectOtherDao.queryExistOutByReferId(oneObject);
        if (existObjs != null && !existObjs.isEmpty()) {
            return true;
        }
        return false;
    }

    private String toGetOutUrlByType(String outReferId) {
        return "/zhgl/core/zlgl/zgzlPage.do?zgzlId=" + outReferId + "&action=detail";
    }
}
