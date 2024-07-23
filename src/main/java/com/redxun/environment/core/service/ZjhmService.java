package com.redxun.environment.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.environment.core.dao.ZjhmDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import groovy.util.logging.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

@Service
@Slf4j
public class ZjhmService {
    private Logger logger = LogManager.getLogger(ZjhmService.class);

    @Autowired
    private ZjhmDao zjhmDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Resource
    private BpmInstManager bpmInstManager;
    @Resource
    private RdmZhglFileManager rdmZhglFileManager;

    public JSONObject getZjhmDetail(String projectId) {
        JSONObject detailObj = zjhmDao.queryZjhmById(projectId);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    public JsonPageResult<?> queryZjhm(HttpServletRequest request, boolean doPage) {
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
        List<JSONObject> zjhmL = zjhmDao.queryZjhm(params);
        //数据过滤(过滤草稿，流程发起人可见全程，流程处理人可在处理节点处理)
        List<JSONObject> zjhmList = filterListByLogic(zjhmL);

        for (JSONObject zjhm : zjhmList) {
            if (zjhm.get("CREATE_TIME_") != null) {
                zjhm.put("CREATE_TIME_", DateUtil.formatDate((Date) zjhm.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(zjhmList, ContextUtil.getCurrentUserId());
        result.setData(zjhmList);
        int countZjhmDataList = zjhmDao.countZjhmList(params);
        result.setTotal(countZjhmDataList);
        return result;
    }

    public JsonResult deleteZjhm(HttpServletRequest request) {
        JsonResult result = new JsonResult(true, "操作成功！");
        String uIdStr = RequestUtil.getString(request, "ids");
        String instIdStr = RequestUtil.getString(request, "instIds");
        if (StringUtils.isNotEmpty(uIdStr) && StringUtils.isNotEmpty(instIdStr)) {
            String[] ids = uIdStr.split(",");
            String[] instIds = instIdStr.split(",");
            for (int i = 0; i < ids.length; i++) {
                // 删除实例
                bpmInstManager.deleteCascade(instIds[i], "");
                // 删除任务表
                zjhmDao.delTaskData(instIds[i]);
            }
        }
        String[] ids = uIdStr.split(",");
        List<String> projectIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("projectIds", projectIds);
        zjhmDao.deleteZjhmFile(param);
        zjhmDao.deleteZjhm(param);
        return result;
    }

    public void createZjhm(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("projectId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        zjhmDao.createZjhm(formData);
    }

    public void updateZjhm(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        zjhmDao.updateZjhm(formData);
    }

    public List <JSONObject> getZjhmFileList(Map <String, Object> params) {
        return zjhmDao.getZjhmFileList(params);
    }

    public void saveUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
        }
        String filePathBase = WebAppUtil.getProperty("zjhmFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find zjhmFilePathBase");
        }
        try {
            String projectId = toGetParamVal(parameters.get("projectId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + projectId;
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
            fileInfo.put("belongId", projectId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            zjhmDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    public void delOAUploadFile(String id, String fileName, String projectId) {
        String zjhmFilePathBase = WebAppUtil.getProperty("zjhmFilePathBase");
        rdmZhglFileManager.deleteOneFileFromDisk(id,fileName,projectId,zjhmFilePathBase);
        zjhmDao.deleteFileById(id);
    }

    //数据过滤
    public List<JSONObject> filterListByLogic(List<JSONObject> zjhmList) {
        List<JSONObject> result = new ArrayList<JSONObject>();
        if (zjhmList == null || zjhmList.isEmpty()) {
            return result;
        }
        // 管理员查看所有的包括草稿数据
        if ("admin".equalsIgnoreCase(ContextUtil.getCurrentUser().getUserNo())) {
            return zjhmList;
        }
        // 当前用户id
        String currentUserId = ContextUtil.getCurrentUserId();

        // 依次过滤每个数据
        for (JSONObject oneProject : zjhmList) {
            // 创建人
            if (oneProject.get("CREATE_BY_") != null && oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                result.add(oneProject);
                continue;
            }
            // 如果是草稿状态，后面角色跳过，都不可见
            if (oneProject.get("instStatus") == null || "DRAFTED".equalsIgnoreCase(oneProject.get("instStatus").toString())) {
                continue;
            }
            // 任务处理人
            if (oneProject.get("currentProcessUserId") != null && oneProject.get("currentProcessUserId").toString().contains(currentUserId)) {
                result.add(oneProject);
                continue;
            }
        }
        return result;
    }
}
