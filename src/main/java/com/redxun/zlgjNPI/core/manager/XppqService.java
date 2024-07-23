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
import com.redxun.core.util.DateFormatUtil;
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
import com.redxun.zlgjNPI.core.dao.XppqDao;

@Service
public class XppqService {
    private Logger logger = LoggerFactory.getLogger(XppqService.class);
    @Autowired
    private XppqDao xppqDao;
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
        if (StringUtils.isBlank(formData.getString("pqjhwcTime"))) {
            formData.put("pqjhwcTime", null);
        }
        xppqDao.insertZlgj(formData);
    }

    public void updateZlgj(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        if (StringUtils.isBlank(formData.getString("pqjhwcTime"))) {
            formData.put("pqjhwcTime", null);
        }
        xppqDao.updateZlgj(formData);
        // 剖切执行
        if (StringUtils.isNotBlank(formData.getString("changeProcessData"))) {
            JSONArray changeProcessData = JSONObject.parseArray(formData.getString("changeProcessData"));
            for (int i = 0; i < changeProcessData.size(); i++) {
                JSONObject oneObject = changeProcessData.getJSONObject(i);
                String state = oneObject.getString("_state");
                String zxId = oneObject.getString("zxId");
                if ("added".equals(state) || StringUtils.isBlank(zxId)) {
                    // 新增
                    oneObject.put("zxId", IdUtil.getId());
                    oneObject.put("wtId", formData.getString("wtId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    if (StringUtils.isBlank(oneObject.getString("yjscTime"))) {
                        oneObject.put("yjscTime", null);
                    }
                    if (StringUtils.isBlank(oneObject.getString("pqjhTime"))) {
                        oneObject.put("pqjhTime", null);
                    }
                    if (StringUtils.isBlank(oneObject.getString("hjwcTime"))) {
                        oneObject.put("hjwcTime", null);
                    }
                    if (StringUtils.isBlank(oneObject.getString("pqwcTime"))) {
                        oneObject.put("pqwcTime", null);
                    }
                    xppqDao.addWtyy(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    if (StringUtils.isBlank(oneObject.getString("yjscTime"))) {
                        oneObject.put("yjscTime", null);
                    }
                    if (StringUtils.isBlank(oneObject.getString("pqjhTime"))) {
                        oneObject.put("pqjhTime", null);
                    }
                    if (StringUtils.isBlank(oneObject.getString("hjwcTime"))) {
                        oneObject.put("hjwcTime", null);
                    }
                    if (StringUtils.isBlank(oneObject.getString("pqwcTime"))) {
                        oneObject.put("pqwcTime", null);
                    }
                    xppqDao.updatWtyy(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    xppqDao.delWtyy(oneObject.getString("zxId"));
                    // 删除对应的附件
                    deleteFilesByZxId(formData.getString("wtId"), oneObject.getString("zxId"));
                }
            }
        }

    }

    private void deleteFilesByZxId(String wtId, String zxId) {
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(wtId)) {
            params.put("wtId", wtId);
        }
        if (StringUtils.isNotBlank(zxId)) {
            params.put("zxId", zxId);
        }
        List<JSONObject> fileInfos = xppqDao.getFileList(params);
        if (fileInfos == null || fileInfos.isEmpty()) {
            return;
        }
        params.clear();
        params.put("wtIds", Arrays.asList(wtId));
        params.put("zxIds", Arrays.asList(zxId));
        xppqDao.deleteZlgjFile(params);

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
        // 删除数据库
        Map<String, Object> params = new HashMap<>();
        params.put("wtIds", wtIds);
        xppqDao.deleteZlgjFile(params);

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
            String zxId = toGetParamVal(parameters.get("zxId"));
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileType = toGetParamVal(parameters.get("fileType"));
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
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("fileType", fileType);
            fileInfo.put("wtId", wtId);
            fileInfo.put("zxId", zxId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            xppqDao.addFileInfos(fileInfo);
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
        String zxId = RequestUtil.getString(request, "zxId");
        String fileType = RequestUtil.getString(request, "fileType");
        if (StringUtils.isBlank(wtId) || StringUtils.isBlank(zxId) || StringUtils.isBlank(fileType)) {
            return Collections.emptyList();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("wtId", wtId);
        params.put("zxId", zxId);
        params.put("fileType", fileType);
        List<JSONObject> fileInfos = xppqDao.getFileList(params);
        if (fileInfos != null && !fileInfos.isEmpty()) {
            for (JSONObject oneFile : fileInfos) {
                oneFile.put("CREATE_TIME_", DateFormatUtil.format(oneFile.getDate("CREATE_TIME_")));
            }
        }
        return fileInfos;
    }

    public JsonPageResult<?> getZlgjList(HttpServletRequest request, HttpServletResponse response, boolean doPage,
        Map<String, Object> params, boolean queryTaskUser) {
        JsonPageResult result = new JsonPageResult(true);
        rdmZhglUtil.addOrder(request, params, "xppq_baseInfo.CREATE_TIME_", "desc");
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
        List<JSONObject> zlgjList = xppqDao.queryZlgjList(params);
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
        int countZlgjDataList = xppqDao.countZlgjList(params);
        result.setTotal(countZlgjDataList);
        return result;
    }


    public JSONObject getZlgjDetail(String wtId) {
        JSONObject result = new JSONObject();
        JSONObject baseInfoObj = xppqDao.queryZlgjById(wtId);
        if (baseInfoObj == null) {
            return null;
        }
        if (baseInfoObj.getDate("pqjhwcTime") != null) {
            baseInfoObj.put("pqjhwcTime", DateFormatUtil.formatDate(baseInfoObj.getDate("pqjhwcTime")));
        }
        result.put("baseInfo", baseInfoObj);
        Map<String, Object> params = new HashMap<>();
        params.put("wtId", wtId);
        // 执行数据
        List<JSONObject> reasonList = xppqDao.getProcessList(params);
        if (reasonList != null && !reasonList.isEmpty()) {
            for (JSONObject oneReason : reasonList) {
                if (oneReason.getDate("yjscTime") != null) {
                    oneReason.put("yjscTime", DateFormatUtil.formatDate(oneReason.getDate("yjscTime")));
                }
                if (oneReason.getDate("pqjhTime") != null) {
                    oneReason.put("pqjhTime", DateFormatUtil.formatDate(oneReason.getDate("pqjhTime")));
                }
                if (oneReason.getDate("hjwcTime") != null) {
                    oneReason.put("hjwcTime", DateFormatUtil.formatDate(oneReason.getDate("hjwcTime")));
                }
                if (oneReason.getDate("pqwcTime") != null) {
                    oneReason.put("pqwcTime", DateFormatUtil.formatDate(oneReason.getDate("pqwcTime")));
                }
            }
        }
        result.put("process", reasonList);

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

    public JsonResult deleteZlgj(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> wtIds = Arrays.asList(ids);
        // 删除文件
        deleteFilesByWtIds(wtIds);
        // 删除目录
        String zlgjFilePathBase = WebAppUtil.getProperty("zlgjFilePathBase");
        for (String oneZlgjId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneZlgjId, zlgjFilePathBase);
        }

        Map<String, Object> param = new HashMap<>();
        param.put("wtIds", wtIds);
        xppqDao.deleteZlgj(param);
        xppqDao.deleteWtyy(param);
        return result;
    }

    public List<JSONObject> getZlgjFileList(List<String> zlgjIdList) {
        Map<String, Object> param = new HashMap<>();
        param.put("wtIds", zlgjIdList);
        return xppqDao.queryZlgjFileList(param);
    }
}
