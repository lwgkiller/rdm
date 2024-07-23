package com.redxun.world.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.core.manager.SysSeqIdManager;
import com.redxun.world.core.dao.CkddDao;
import com.redxun.world.core.dao.LccpDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import groovy.util.logging.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Service
@Slf4j
public class LccpService {
    private Logger logger = LogManager.getLogger(LccpService.class);
    @Autowired
    private CkddDao ckddDao;
    @Autowired
    private LccpDao lccpDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    SysSeqIdManager sysSeqIdManager;

    public JsonPageResult<?> queryLccp(HttpServletRequest request, boolean doPage) {
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
        List<JSONObject> lccpList = lccpDao.queryLccp(params);
        for (JSONObject lccp : lccpList) {
            if (lccp.get("CREATE_TIME_") != null) {
                lccp.put("CREATE_TIME_", DateUtil.formatDate((Date) lccp.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (lccp.getDate("ckMonth") != null) {
                lccp.put("ckMonth", DateUtil.formatDate(lccp.getDate("ckMonth"), "yyyy-MM-dd"));
            }

        }
        rdmZhglUtil.setTaskInfo2Data(lccpList, ContextUtil.getCurrentUserId());
        result.setData(lccpList);
        int countLccpDataList = lccpDao.countLccpList(params);
        result.setTotal(countLccpDataList);
        return result;
    }

    public void createLccp(JSONObject formData) {
        formData.put("lccpId", IdUtil.getId());
        formData.put("ddNumber", sysSeqIdManager.genSequenceNo("LCCP", ContextUtil.getCurrentTenantId()));
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        lccpDao.createLccp(formData);
    }

    public void updateLccp(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        lccpDao.updateLccp(formData);
    }

    public void saveLccpUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("lccpFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find lccpFilePathBase");
            return;
        }
        try {
            String belongId = toGetParamVal(parameters.get("lccpId"));
            String fileId = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String location = toGetParamVal(parameters.get("loc"));
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
            fileInfo.put("location", location);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            lccpDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getLccpDetail(String lccpId) {
        JSONObject detailObj = lccpDao.queryLccpById(lccpId);
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


    public List<JSONObject> getLccpFileList(String belongId,String location) {
        List<JSONObject> lccpFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("lccpId", belongId);
        param.put("location", location);
        lccpFileList = lccpDao.queryLccpFileList(param);
        return lccpFileList;
    }

    public List<JSONObject> getLccpFileLists(String belongId) {
        List<JSONObject> lccpFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("lccpId", belongId);
        lccpFileList = lccpDao.queryLccpFileList(param);
        return lccpFileList;
    }

    public void deleteOneLccpFile(String fileId, String fileName, String lccpId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, lccpId, WebAppUtil.getProperty("lccpFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("fileId", fileId);
        lccpDao.deleteLccpFile(param);
    }

    public JsonResult deleteLccp(String lccpId) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<JSONObject> files = getLccpFileLists(lccpId);
        String wjFilePathBase = WebAppUtil.getProperty("lccpFilePathBase");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"),
                    oneFile.getString("belongId"), wjFilePathBase);
        }
        rdmZhglFileManager.deleteDirFromDisk(lccpId, wjFilePathBase);
        Map<String, Object> param = new HashMap<>();
        param.put("lccpId", lccpId);
        lccpDao.deleteLccpFile(param);
        lccpDao.deleteLccp(param);
        return result;
    }

    /**
     * 查询国家列表
     */
    public JsonPageResult<?> getCountryList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            JSONObject requestObj = new JSONObject();
            Map<String, Object> params = new HashMap<>();
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
            requestObj.putAll(params);
            //查询项目基础信息
            List<Map<String, Object>> personProjectList = lccpDao.queryCountryList(requestObj);
            result.setData(personProjectList);
            List<Map<String, Object>> allData = result.getData();
            // 总数设置并分页
            result.setTotal(allData.size());
            List<Map<String, Object>> finalSubProjectList = new ArrayList<Map<String, Object>>();
            if (doPage) {
                // 根据分页进行subList截取
                int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
                int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
                int startSubListIndex = pageIndex * pageSize;
                int endSubListIndex = startSubListIndex + pageSize;
                if (startSubListIndex < allData.size()) {
                    finalSubProjectList = allData.subList(startSubListIndex,
                            endSubListIndex < allData.size() ? endSubListIndex : allData.size());
                }
            } else {
                finalSubProjectList = allData;
            }
            result.setData(finalSubProjectList);

        } catch (Exception e) {
            logger.error("Exception in getCountryList", e);
            result.setSuccess(false);
            result.setMessage("系统异常！");
        }
        return result;
    }

    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "液压挖掘机新市场导入评估表.xls";
            // 创建文件实例
            File file = new File(LccpService.class.getClassLoader().getResource("templates/lccp/" + fileName).toURI());
            String finalDownloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 finalDownloadFileName
            headers.setContentDispositionFormData("attachment", finalDownloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in importTemplateDownload", e);
            return null;
        }
    }
    
}
