package com.redxun.standardManager.core.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.DateUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.dao.StandardConfigDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import groovy.util.logging.Slf4j;
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
@Slf4j
public class StandardConfigManager {
    private static Logger logger = LoggerFactory.getLogger(StandardConfigManager.class);
    @Autowired
    private StandardConfigDao standardConfigDao;

    public List<Map<String, Object>> categoryQuery() {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            List<Map<String, Object>> result = standardConfigDao.queryStandardCategory(params);
            if (result != null && !result.isEmpty()) {
                for (Map<String, Object> oneInfo : result) {
                    if (oneInfo.get("CREATE_TIME_") != null) {
                        oneInfo.put("CREATE_TIME_",
                            DateUtil.formatDate((Date)oneInfo.get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneInfo.get("UPDATE_TIME_") != null) {
                        oneInfo.put("UPDATE_TIME_",
                            DateUtil.formatDate((Date)oneInfo.get("UPDATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                }
            }
            return result;
        } catch (Exception e) {
            logger.error("Exception in categoryQuery", e);
            return null;
        }
    }

    public void saveStandardCategory(JSONObject result, String changeGridDataStr) {
        try {
            JSONArray changeGridDataJson = JSONObject.parseArray(changeGridDataStr);
            if (changeGridDataJson == null || changeGridDataJson.isEmpty()) {
                logger.warn("gridDataArray is blank");
                result.put("message", "保存失败，数据为空！");
                return;
            }

            Set<String> needDelCategoryId = new HashSet<>();
            for (int i = 0; i < changeGridDataJson.size(); i++) {
                JSONObject oneObject = changeGridDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    oneObject.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                    standardConfigDao.saveStandardCategory(oneObject);
                } else if ("modified".equals(state)) {
                    // 修改
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    standardConfigDao.updateStandardCategory(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除时需要先判断是否有关联的标准，有的话不能删除
                    needDelCategoryId.add(id);
                }
            }
            if (!needDelCategoryId.isEmpty()) {
                Map<String, Object> params = new HashMap<>();
                params.put("ids", needDelCategoryId);
                params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                List<Map<String, Object>> usingCategoryIdInfos = standardConfigDao.queryUsingCategoryIds(params);
                Set<String> usingCategoryIds = new HashSet<>();
                if (usingCategoryIdInfos != null && !usingCategoryIdInfos.isEmpty()) {
                    for (Map<String, Object> oneMap : usingCategoryIdInfos) {
                        usingCategoryIds.add(oneMap.get("standardCategoryId").toString());
                    }
                    result.put("message", "关联标准的数据不会被删除");
                }
                needDelCategoryId.removeAll(usingCategoryIds);
                standardConfigDao.delStandardCategoryByIds(params);
            }

        } catch (Exception e) {
            logger.error("Exception in saveStandardCategory");
            result.put("message", "保存失败，系统异常！");
            return;
        }
    }

    public List<Map<String, Object>> belongDepQuery() {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            List<Map<String, Object>> result = standardConfigDao.queryStandardBelongDep(params);
            if (result != null && !result.isEmpty()) {
                for (Map<String, Object> oneInfo : result) {
                    if (oneInfo.get("CREATE_TIME_") != null) {
                        oneInfo.put("CREATE_TIME_",
                            DateUtil.formatDate((Date)oneInfo.get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneInfo.get("UPDATE_TIME_") != null) {
                        oneInfo.put("UPDATE_TIME_",
                            DateUtil.formatDate((Date)oneInfo.get("UPDATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                }
            }
            return result;
        } catch (Exception e) {
            logger.error("Exception in belongDepQuery", e);
            return null;
        }
    }

    public void saveBelongDep(JSONObject result, String changeGridDataStr) {
        try {
            JSONArray changeGridDataJson = JSONObject.parseArray(changeGridDataStr);
            if (changeGridDataJson == null || changeGridDataJson.isEmpty()) {
                logger.warn("gridDataArray is blank");
                result.put("message", "保存失败，数据为空！");
                return;
            }

            Set<String> needDelId = new HashSet<>();
            for (int i = 0; i < changeGridDataJson.size(); i++) {
                JSONObject oneObject = changeGridDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    oneObject.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                    standardConfigDao.saveStandardBelongDep(oneObject);
                } else if ("modified".equals(state)) {
                    // 修改
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    standardConfigDao.updateStandardBelongDep(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除时需要先判断是否有关联的标准，有的话不能删除
                    needDelId.add(id);
                }
            }
            if (!needDelId.isEmpty()) {
                Map<String, Object> params = new HashMap<>();
                params.put("ids", needDelId);
                params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                List<Map<String, Object>> usingIdInfos = standardConfigDao.queryUsingBelongDepIds(params);
                Set<String> usingIds = new HashSet<>();
                if (usingIdInfos != null && !usingIdInfos.isEmpty()) {
                    for (Map<String, Object> oneMap : usingIdInfos) {
                        usingIds.add(oneMap.get("belongDepId").toString());
                    }
                    result.put("message", "关联标准的数据不会被删除");
                }
                needDelId.removeAll(usingIds);
                if (!needDelId.isEmpty()) {
                    standardConfigDao.delStandardBelongDepByIds(params);
                }
            }

        } catch (Exception e) {
            logger.error("Exception in saveBelongDep");
            result.put("message", "保存失败，系统异常！");
            return;
        }
    }

    // 从数据库中查询文档信息
    public List<Map<String, Object>> queryStandardTemplateList(Map<String, Object> params) {
        try {
            List<Map<String, Object>> fileInfos = standardConfigDao.queryStandardTemplateList(params);
            // 格式化时间
            if (fileInfos != null && !fileInfos.isEmpty()) {
                for (Map<String, Object> oneFile : fileInfos) {
                    if (oneFile.get("CREATE_TIME_") != null) {
                        oneFile.put("CREATE_TIME_",
                            DateUtil.formatDate((Date)oneFile.get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                }
            }
            return fileInfos;
        } catch (Exception e) {
            logger.error("Exception in queryStandardTemplateList", e);
            return null;
        }
    }

    // 保存文件到后台和数据库
    public void saveStandardTemplate(HttpServletRequest request) {
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
        try {
            // 记录成功写入磁盘的文件信息
            Map<String, Object> fileInfo = new HashMap<>();
            String fileName = parameters.get("fileName")[0];
            fileInfo.put("templateName", fileName);
            fileInfo.put("size", parameters.get("fileSize")[0]);
            fileInfo.put("relativePath", "");
            fileInfo.put("downloadNum", 0);
            fileInfo.put("description", parameters.get("description")[0]);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            fileInfo.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String standardTemplatePathBase = WebAppUtil.getProperty("standardTemplatePathBase");
            if (StringUtils.isBlank(standardTemplatePathBase)) {
                logger.error("can't find standardTemplatePathBase");
                return;
            }
            File pathFile = new File(standardTemplatePathBase);
            // 目录不存在则创建
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String fileFullPath = pathFile + File.separator + fileInfo.get("templateName").toString();
            File file = new File(fileFullPath);
            // 文件存在则更新掉
            if (file.exists()) {
                logger.warn("File " + fileFullPath + " will be updated");
                file.delete();
            }
            FileCopyUtils.copy(mf.getBytes(), file);

            // 查找数据库中这个目录下是否有同名的文件，如果有则更新，没有则新增
            Map<String, Object> params = new HashMap<>();
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            params.put("templateName", fileInfo.get("templateName").toString());
            List<Map<String, Object>> sameNameTemplate = standardConfigDao.queryStandardTemplateByName(params);
            if (sameNameTemplate != null && !sameNameTemplate.isEmpty()) {
                logger.warn("Template " + fileInfo.get("templateName").toString() + " in db will be update");
                fileInfo.put("id", sameNameTemplate.get(0).get("id").toString());
                standardConfigDao.updateStandardTemplate(fileInfo);
            } else {
                fileInfo.put("id", IdUtil.getId());
                standardConfigDao.createStandardTemplate(fileInfo);
            }
        } catch (Exception e) {
            logger.error("Exception in saveStandardTemplate", e);
        }
    }

    // 删除一个文件
    public void deleteStandardTemplate(String id, String relativeFilePath, String fileName) {
        try {
            String fullFilePath = "";
            if (StringUtils.isBlank(fileName)) {
                logger.error("fileName is blank");
                return;
            }
            String standardTemplatePathBase = WebAppUtil.getProperty("standardTemplatePathBase");
            if (StringUtils.isBlank(standardTemplatePathBase)) {
                logger.error("can't find standardTemplatePathBase");
                return;
            }
            fullFilePath = standardTemplatePathBase + File.separator + fileName;
            // 先删除磁盘中的，删除成功则删除DB中的
            File file = new File(fullFilePath);
            boolean deleteResult = true;
            if (file.exists()) {
                logger.info("File " + fullFilePath + " will be deleted!");
                deleteResult = file.delete();
            }
            if (!deleteResult) {
                logger.error("File " + fullFilePath + " delete failed!");
                return;
            }
            Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            params.put("templateName", fileName);
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            standardConfigDao.deleteStandardTemplate(params);
        } catch (Exception e) {
            logger.error("Exception in deleteStandardTemplate", e);
        }
    }

    public void updateTemplateDownloadNum(String id) {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("id", id);
        standardConfigDao.updateTemplateDownloadNum(params);
    }
}
