package com.redxun.standardManager.core.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.dao.StandardChangeDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
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
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Service
@Slf4j
public class StandardChangeService {
    private Logger logger = LogManager.getLogger(StandardChangeService.class);
    @Autowired
    private StandardChangeDao standardChangeDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    public JsonPageResult<?> queryStandardChange(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "spNumber");
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
        // 增加分页条件
        List<Map<String, Object>> StandardChangeList = standardChangeDao.queryStandardChange(params);
        for (Map<String, Object> jstb : StandardChangeList) {
            if (jstb.get("releaseTime") != null) {
                jstb.put("releaseTime", DateUtil.formatDate((Date) jstb.get("releaseTime"), "yyyy-MM"));
            }
        }
        List<Map<String, Object>> finalSubList = new ArrayList<Map<String, Object>>();
        if (doPage) {
            // 根据分页进行subList截取
            int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
            int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
            int startSubListIndex = pageIndex * pageSize;
            int endSubListIndex = startSubListIndex + pageSize;
            if (startSubListIndex < StandardChangeList.size()) {
                finalSubList = StandardChangeList.subList(startSubListIndex,
                        endSubListIndex < StandardChangeList.size() ? endSubListIndex : StandardChangeList.size());
            }
        } else {
            finalSubList = StandardChangeList;
        }
        result.setData(finalSubList);
        result.setTotal(StandardChangeList.size());
        return result;
    }

    public void insertstandardChange(JSONObject formData) {
        formData.put("standardId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        standardChangeDao.insertstandardChange(formData);
    }

    public void updatestandardChange(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        standardChangeDao.updatestandardChange(formData);
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }
    public void savestandardChangeUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("standardChangeFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find standardChangeFilePathBase");
            return;
        }
        try {
            String belongId = toGetParamVal(parameters.get("standardId"));
            String fileId = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileDeliveryId = toGetParamVal(parameters.get("fileDeliveryId"));
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
            fileInfo.put("fileDeliveryId", fileDeliveryId);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("belongId", belongId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            standardChangeDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getstandardChangeDetail(String standardId) {
        JSONObject detailObj = standardChangeDao.querystandardChangeById(standardId);
        if (detailObj == null) {
            return new JSONObject();
        }

        return detailObj;
    }

    public List<JSONObject> getstandardChangeFileList(List<String> standardIdList) {
        List<JSONObject> standardChangeFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("standardIds", standardIdList);
        standardChangeFileList = standardChangeDao.querystandardChangeFileList(param);
        return standardChangeFileList;
    }

    public void deleteOnestandardChangeFile(String fileId, String fileName, String standardId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, standardId, WebAppUtil.getProperty("standardChangeFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        param.put("belongId", standardId);
        standardChangeDao.deletestandardChangeFile(param);
    }

    public JsonResult deletestandardChange(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> standardIds = Arrays.asList(ids);
        List<JSONObject> files = getstandardChangeFileList(standardIds);
        String standardChangeFilePathBase = WebAppUtil.getProperty("standardChangeFilePathBase");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"),
                    oneFile.getString("standardId"), standardChangeFilePathBase);
        }
        for (String onestandardChange : ids) {
            rdmZhglFileManager.deleteDirFromDisk(onestandardChange, standardChangeFilePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("standardIds", standardIds);
        standardChangeDao.deletestandardChangeFile(param);
        standardChangeDao.deletestandardChange(param);
        return result;
    }

}
