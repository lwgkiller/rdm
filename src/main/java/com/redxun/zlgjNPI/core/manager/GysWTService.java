package com.redxun.zlgjNPI.core.manager;

import java.io.File;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.util.RdmCommonUtil;
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
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.zlgjNPI.core.dao.GysWTDao;

@Service
public class GysWTService {
    private Logger logger = LoggerFactory.getLogger(GysWTService.class);
    @Autowired
    private GysWTDao gysWTDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;


    public void createZlgj(JSONObject formData) {
        formData.put("wtId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        gysWTDao.insertZlgj(formData);
    }

    public void updateZlgj(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        gysWTDao.updateZlgj(formData);
        // 问题可能原因
        if (StringUtils.isNotBlank(formData.getString("changeReasonData"))) {
            JSONArray reasonDataJson = JSONObject.parseArray(formData.getString("changeReasonData"));
            for (int i = 0; i < reasonDataJson.size(); i++) {
                JSONObject oneObject = reasonDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String yyId = oneObject.getString("yyId");
                if ("added".equals(state) || StringUtils.isBlank(yyId)) {
                    // 新增
                    oneObject.put("yyId", IdUtil.getId());
                    oneObject.put("wtId", formData.getString("wtId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    gysWTDao.addWtyy(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    gysWTDao.updatWtyy(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    gysWTDao.delWtyy(oneObject.getString("yyId"));
                }
            }
        }
        // 临时措施
        if (StringUtils.isNotBlank(formData.getString("changeLscsData"))) {
            JSONArray linshiCSDataJson = JSONObject.parseArray(formData.getString("changeLscsData"));
            for (int i = 0; i < linshiCSDataJson.size(); i++) {
                JSONObject oneObject = linshiCSDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String csId = oneObject.getString("csId");
                if ("added".equals(state) || StringUtils.isBlank(csId)) {
                    oneObject.put("csId", IdUtil.getId());
                    oneObject.put("wtId", formData.getString("wtId"));
                    if (StringUtils.isBlank(oneObject.getString("dhTime"))) {
                        oneObject.put("dhTime", null);
                    }
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    gysWTDao.addLscs(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    gysWTDao.updatLscs(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    gysWTDao.delLscs(oneObject.getString("csId"));
                }
            }
        }
        // 根本原因
        if (StringUtils.isNotBlank(formData.getString("changeGbyyData"))) {
            JSONArray gbyyDataJson = JSONObject.parseArray(formData.getString("changeGbyyData"));
            for (int i = 0; i < gbyyDataJson.size(); i++) {
                JSONObject oneObject = gbyyDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String gbyyId = oneObject.getString("gbyyId");
                if ("added".equals(state) || StringUtils.isBlank(gbyyId)) {
                    // 新增
                    oneObject.put("gbyyId", IdUtil.getId());
                    oneObject.put("wtId", formData.getString("wtId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    gysWTDao.addGbyy(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    gysWTDao.updateGbyy(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    gysWTDao.delGbyy(oneObject.getString("gbyyId"));
                }
            }
        }
        // 方案验证
        if (StringUtils.isNotBlank(formData.getString("changeFayzData"))) {
            JSONArray fayzDataJson = JSONObject.parseArray(formData.getString("changeFayzData"));
            for (int i = 0; i < fayzDataJson.size(); i++) {
                JSONObject oneObject = fayzDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String faId = oneObject.getString("faId");
                if ("added".equals(state) || StringUtils.isBlank(faId)) {
                    // 新增
                    oneObject.put("faId", IdUtil.getId());
                    oneObject.put("wtId", formData.getString("wtId"));
                    if (StringUtils.isBlank(oneObject.getString("sjTime"))) {
                        oneObject.put("sjTime", null);
                    }
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    gysWTDao.addFayz(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    gysWTDao.updatFayz(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    gysWTDao.delFayz(oneObject.getString("faId"));
                }
            }
        }
        // 永久解决方案
        if (StringUtils.isNotBlank(formData.getString("changeJjfaData"))) {
            JSONArray jjfaDataJson = JSONObject.parseArray(formData.getString("changeJjfaData"));
            for (int i = 0; i < jjfaDataJson.size(); i++) {
                JSONObject oneObject = jjfaDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String faId = oneObject.getString("faId");
                if ("added".equals(state) || StringUtils.isBlank(faId)) {
                    // 新增
                    oneObject.put("faId", IdUtil.getId());
                    oneObject.put("wtId", formData.getString("wtId"));
                    if (StringUtils.isBlank(oneObject.getString("wcTime"))) {
                        oneObject.put("wcTime", null);
                    }
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    gysWTDao.addJjfa(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    gysWTDao.updatJjfa(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    gysWTDao.delJjfa(oneObject.getString("faId"));
                    // 删除对应的附件
                    deleteFilesByJjfa(formData.getString("wtId"), oneObject.getString("faId"));
                }
            }
        }
    }

    private void deleteFilesByJjfa(String wtId, String faId) {
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(wtId)) {
            params.put("wtId", wtId);
        }
        if (StringUtils.isNotBlank(faId)) {
            params.put("jjfaId", faId);
        }
        List<JSONObject> fileInfos = gysWTDao.getFileList(params);
        if (fileInfos == null || fileInfos.isEmpty()) {
            return;
        }
        params.clear();
        params.put("wtIds", Arrays.asList(wtId));
        params.put("faIds", Arrays.asList(faId));
        gysWTDao.deleteZlgjFile(params);

        // 删磁盘
        for (JSONObject oneFile : fileInfos) {
            String id = oneFile.getString("id");
            String fileName = oneFile.getString("fileName");
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            deleteFileOnDisk(wtId, id, suffix);
        }
    }

    private void deleteFilesByWtIds(List<String> wtIds) {
        if (wtIds == null || wtIds.isEmpty()) {
            return;
        }
        List<JSONObject> fileInfos = getZlgjFileList(wtIds);
        if (fileInfos == null || fileInfos.isEmpty()) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("wtIds", wtIds);
        gysWTDao.deleteZlgjFile(params);

        // 删磁盘
        for (JSONObject oneFile : fileInfos) {
            String id = oneFile.getString("id");
            String fileName = oneFile.getString("fileName");
            String wtId = oneFile.getString("wtId");
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            deleteFileOnDisk(wtId, id, suffix);
        }
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
            String id = IdUtil.getId();
            String wtId = toGetParamVal(parameters.get("wtId"));
            String faId = toGetParamVal(parameters.get("faId"));
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String filePathBase = WebAppUtil.getProperty("zlgjFilePathBase");
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase");
                return;
            }
            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + wtId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);
            // 写入数据库
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("id", id);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", toGetParamVal(parameters.get("fileSize")));
            fileInfo.put("wtId", wtId);
            fileInfo.put("jjfaId", faId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            gysWTDao.addFileInfos(fileInfo);
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

    public List<JSONObject> getFileListByTypeId(HttpServletRequest request) {
        String wtId = RequestUtil.getString(request, "wtId");
        String faId = RequestUtil.getString(request, "faId");
        if (StringUtils.isBlank(wtId)) {
            return Collections.emptyList();
        }
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(wtId)) {
            params.put("wtId", wtId);
        }
        if (StringUtils.isNotBlank(faId)) {
            params.put("jjfaId", faId);
        }
        List<JSONObject> fileInfos = gysWTDao.getFileList(params);
        return fileInfos;
    }

    public JsonPageResult<?> getZlgjList(HttpServletRequest request, HttpServletResponse response, boolean doPage,
        Map<String, Object> params, boolean queryTaskUser) {
        JsonPageResult result = new JsonPageResult(true);
        String type = RequestUtil.getString(request, "type", "");
        params.put("type", type);
        rdmZhglUtil.addOrder(request, params, "gyswt_baseInfo.CREATE_TIME_", "desc");
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
        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        // 增加角色过滤的条件
        RdmCommonUtil.addListAllQueryRoleExceptDraft(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> zlgjList = gysWTDao.queryZlgjList(params);
        for (JSONObject zlgj : zlgjList) {
            if (zlgj.get("CREATE_TIME_") != null) {
                zlgj.put("CREATE_TIME_", DateUtil.formatDate(zlgj.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        // 查询当前处理人
        if (queryTaskUser) {
            xcmgProjectManager.setTaskCurrentUserJSON(zlgjList);
        }
        result.setData(zlgjList);
        int countZlgjDataList = gysWTDao.countZlgjList(params);
        result.setTotal(countZlgjDataList);
        return result;
    }


    public JSONObject getZlgjDetail(String wtId) {
        JSONObject result = new JSONObject();
        JSONObject baseInfoObj = gysWTDao.queryZlgjById(wtId);
        if (baseInfoObj == null) {
            return null;
        }
        result.put("baseInfo", baseInfoObj);
        Map<String, Object> params = new HashMap<>();
        params.put("wtId", wtId);
        // 可能原因
        List<JSONObject> reasonList = gysWTDao.getReasonList(params);
        result.put("reason", reasonList);
        // 临时措施
        List<JSONObject> lscsList = gysWTDao.getLscsList(params);
        result.put("lscs", lscsList);
        formatListTime("dhTime", lscsList);
        // 根本原因
        List<JSONObject> gbyyList = gysWTDao.getGbyyList(params);
        result.put("gbyy", gbyyList);
        // 方案验证
        List<JSONObject> fayzList = gysWTDao.getFayzList(params);
        result.put("fayz", fayzList);
        formatListTime("sjTime", fayzList);
        // 永久解决方案
        List<JSONObject> jjfaList = gysWTDao.getJjfaList(params);
        result.put("jjfa", jjfaList);
        formatListTime("wcTime", jjfaList);
        return result;
    }

    public void deleteFileOnDisk(String mainId, String fileId, String suffix) {
        String fullFileName = fileId + "." + suffix;
        StringBuilder fileBasePath = new StringBuilder(WebAppUtil.getProperty("zlgjFilePathBase"));
        String fullPath =
            fileBasePath.append(File.separator).append(mainId).append(File.separator).append(fullFileName).toString();
        File file = new File(fullPath);
        if (file.exists()) {
            file.delete();
        }
        // 删除预览目录中pdf文件
        String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
        String convertPdfPath = WebAppUtil.getProperty("zlgjFilePathBase") + File.separator + mainId + File.separator
            + convertPdfDir + File.separator + fileId + ".pdf";
        File pdffile = new File(convertPdfPath);
        if (pdffile.exists()) {
            pdffile.delete();
        }
    }

    private void formatListTime(String keyName, List<JSONObject> dataList) {
        if (dataList != null && !dataList.isEmpty()) {
            for (JSONObject object : dataList) {
                if (object.get(keyName) != null) {
                    object.put(keyName, DateUtil.formatDate((Date)object.get(keyName), "yyyy-MM-dd"));
                }
            }
        }
    }

    public JsonResult deleteZlgj(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> wtIds = Arrays.asList(ids);
        // 删除文件
        deleteFilesByWtIds(wtIds);
        String zlgjFilePathBase = WebAppUtil.getProperty("zlgjFilePathBase");
        for (String oneZlgjId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneZlgjId, zlgjFilePathBase);
        }

        Map<String, Object> param = new HashMap<>();
        param.put("wtIds", wtIds);
        gysWTDao.deleteZlgj(param);
        gysWTDao.deleteWtyy(param);
        gysWTDao.deleteLscs(param);
        gysWTDao.deleteGbyy(param);
        gysWTDao.deleteFayz(param);
        gysWTDao.deleteJjfa(param);
        return result;
    }

    public List<JSONObject> getZlgjFileList(List<String> zlgjIdList) {
        Map<String, Object> param = new HashMap<>();
        param.put("wtIds", zlgjIdList);
        return gysWTDao.queryZlgjFileList(param);
    }

    public List<JSONObject> reasonList(String[] ids) {
        List<String> yyIds = Arrays.asList(ids);
        JSONObject paramJson = new JSONObject();
        paramJson.put("yyIds", yyIds);
        List<JSONObject> result = gysWTDao.reasonList(paramJson);
        return result;
    }

    private List<Map<String, Object>> filterList(List<Map<String, Object>> JstbList) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (JstbList == null || JstbList.isEmpty()) {
            return result;
        }
        // 管理员查看所有的包括草稿数据
        if ("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
            return JstbList;
        }
        // 分管领导的查看权限等同于项目管理人员
        boolean showAll = false;
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUser().getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        for (Map<String, Object> oneRole : currentUserRoles) {
            if (oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-BELONG")) {
                if ("分管领导".equalsIgnoreCase(oneRole.get("NAME_").toString())) {
                    showAll = true;
                    break;
                }
            }
        }
        String currentUserId = ContextUtil.getCurrentUserId();
        for (Map<String, Object> oneProject : JstbList) {
            if (showAll) {
                // 分管领导查看非草稿状态的所有数据
                if (oneProject.get("status") != null && !"DRAFTED".equals(oneProject.get("status").toString())) {
                    result.add(oneProject);
                } else {
                    if (oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneProject);
                    }
                }
            } else {
                // 自己是当前流程处理人
                if (oneProject.get("status") != null && !"DRAFTED".equals(oneProject.get("status").toString())) {
                    if (oneProject.get("currentProcessUserId") != null
                        && oneProject.get("currentProcessUserId").toString().contains(currentUserId)) {
                        result.add(oneProject);
                    }
                }
                // 自己的查看所有
                if (oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                    result.add(oneProject);
                }
            }

        }
        return result;
    }

    public List<JSONObject> querySmallTypeByBig(String bigTypeName) {
        Map<String, Object> params = new HashMap<>();
        params.put("bigTypeName", bigTypeName);
        return gysWTDao.querySmallType(params);
    }
}
