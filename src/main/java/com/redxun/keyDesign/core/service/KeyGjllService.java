package com.redxun.keyDesign.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.keyDesign.core.dao.KeyGjllDao;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import groovy.util.logging.Slf4j;
import org.apache.commons.io.FileUtils;
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
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class KeyGjllService {
    private Logger logger = LogManager.getLogger(KeyGjllService.class);
    @Autowired
    private KeyGjllDao keyGjllDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Resource
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;


    public JsonPageResult<?> queryGjll(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
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
        String belongbj = RequestUtil.getString(request, "belongbj");
        params.put("belongbj",belongbj);
        // 增加分页条件
        List<JSONObject> gjllList = keyGjllDao.queryGjll(params);
        for (JSONObject gjll : gjllList) {
            if (gjll.getDate("CREATE_TIME_") != null) {
                gjll.put("CREATE_TIME_", DateUtil.formatDate(gjll.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (gjll.getDate("changeTime") != null) {
                gjll.put("changeTime", DateUtil.formatDate(gjll.getDate("changeTime"), "yyyy-MM-dd"));
            }
        }
        result.setData(gjllList);
        int countQbgzDataList = keyGjllDao.countGjllList(params);
        result.setTotal(countQbgzDataList);
        return result;
    }
    
    public void insertGjll(JSONObject formData) {
        formData.put("gjId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("【关键零部件改进履历】");
        stringBuilder.append("‘").append(formData.getString("model")).append("’").append("改进履历已添加，请查看");
        noticeObj.put("content", stringBuilder.toString());
        if(StringUtils.isNotBlank(formData.getString("noticeId"))){
            sendDDNoticeManager.sendNoticeForCommon(formData.getString("noticeId"), noticeObj);
        }
        keyGjllDao.createGjll(formData);
    }

    public void updateGjll(JSONObject formData) {
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("【关键零部件改进履历】");
        stringBuilder.append("零部件型号“").append(formData.getString("model")).append("”改进履历已添加，请查看")
        .append(" 发布时间：" ).append(XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        noticeObj.put("content", stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon(formData.getString("noticeId"), noticeObj);
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        keyGjllDao.updateGjll(formData);
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }
    public void saveGjllUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("keyGjllFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find keyGjllFilePathBase");
            return;
        }
        try {
            String belongId = toGetParamVal(parameters.get("gjId"));
            String fileId = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileType = toGetParamVal(parameters.get("fileType"));
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
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("fileType", fileType);
            fileInfo.put("belongId", belongId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            keyGjllDao.createFile(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getGjllDetail(String standardId) {
        JSONObject detailObj = keyGjllDao.queryGjllById(standardId);
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.getDate("changeTime") != null) {
            detailObj.put("changeTime", DateUtil.formatDate(detailObj.getDate("changeTime"), "yyyy-MM-dd"));
        }
        return detailObj;
    }

    public List<JSONObject> getGjllFileList(List<String> gjIdList) {
        List<JSONObject> gjllFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("gjIds", gjIdList);
        gjllFileList = keyGjllDao.queryGjllFileList(param);
        return gjllFileList;
    }

    public void deleteOneGjllFile(String fileId, String fileName, String gjId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, gjId, WebAppUtil.getProperty("keyGjllFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("fileId", fileId);
        keyGjllDao.deleteGjllFile(param);
    }

    public JsonResult deleteGjll(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> gjIds = Arrays.asList(ids);
        List<JSONObject> files = getGjllFileList(gjIds);
        String keyGjllFilePathBase = WebAppUtil.getProperty("keyGjllFilePathBase");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"),
                    oneFile.getString("gjId"), keyGjllFilePathBase);
        }
        for (String onegjll : ids) {
            rdmZhglFileManager.deleteDirFromDisk(onegjll, keyGjllFilePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("gjIds", gjIds);
        keyGjllDao.deleteGjllFile(param);
        keyGjllDao.deleteGjll(param);
        return result;
    }

    public void linkGjll(JSONObject formData,String oldgjId) {
        String newgjId = IdUtil.getId();
        formData.put("gjId", newgjId);
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        keyGjllDao.createGjll(formData);
        copySaveFile(oldgjId,newgjId);
    }

    private void copySaveFile(String oldgjId, String newgjId) {
        String bfileBasePath = WebAppUtil.getProperty("gjllFilePathBase");
        String newfileBasePath = WebAppUtil.getProperty("keyGjllFilePathBase");
        Map<String, Object> param = new HashMap<>();
        param.put("belongId", oldgjId);
        List<JSONObject> gjllDetailFileList = keyGjllDao.queryOldGjllFileList(param);
        for(JSONObject fileJson:gjllDetailFileList){
            String bfileName = fileJson.getString("fileName");
            String bfileId = fileJson.getString("fileId");
            String belongId = fileJson.getString("belongId");
            String bsuffix = CommonFuns.toGetFileSuffix(bfileName);
            String relativeFilePath = "";
            if (StringUtils.isNotBlank(belongId)) {
                relativeFilePath = File.separator + belongId;
            }
            String brealFileName = bfileId + "." + bsuffix;
            String bfullFilePath = bfileBasePath + relativeFilePath + File.separator + brealFileName;
            File bfile = new File(bfullFilePath);
            String fileId = IdUtil.getId();
            String fileSize = fileJson.getString("fileSize");
            // 向下载目录中写入文件
            String filePath = newfileBasePath + File.separator + newgjId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String fileFullPath = filePath + File.separator + fileId + "." + bsuffix;
            File file = new File(fileFullPath);
            try {
                FileCopyUtils.copy(FileUtils.readFileToByteArray(bfile), file);
                JSONObject fileInfo = new JSONObject();
                fileInfo.put("fileId", fileId);
                fileInfo.put("fileName", bfileName);
                fileInfo.put("fileSize", fileSize);
                fileInfo.put("belongId", newgjId);
                fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                fileInfo.put("CREATE_TIME_", new Date());
                keyGjllDao.createFile(fileInfo);
            } catch (IOException e) {
                logger.error("文件加载失败", e.getMessage());
            }
        }
    }


}
