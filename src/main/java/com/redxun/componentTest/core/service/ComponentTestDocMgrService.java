package com.redxun.componentTest.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.componentTest.core.dao.ComponentTestDocMgrDao;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.sys.core.manager.SysDicManager;
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
public class ComponentTestDocMgrService {
    private static Logger logger = LoggerFactory.getLogger(ComponentTestDocMgrService.class);
    @Autowired
    private ComponentTestDocMgrDao componentTestDocMgrDao;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    //..
    public List<JSONObject> dataListQuery(JSONObject params) {
        List<JSONObject> fileList = componentTestDocMgrDao.dataListQuery(params);
        return fileList;
    }

    //..
    public void deleteOneFile(String fileId, String fileName, String mainId) {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "componentTestUploadPosition", "commonFile").getValue();
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, mainId, filePathBase);
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        componentTestDocMgrDao.deleteFile(param);
    }

    //..
    public void saveUploadFiles(HttpServletRequest request) {
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
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "componentTestUploadPosition", "commonFile").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find FilePathBase");
            return;
        }
        try {
            String mainKanbanId = toGetParamVal(parameters.get("mainKanbanId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            //--文件名相同的如果存在就先删除
            JSONObject params = new JSONObject();
            params.put("docName", fileName);
            List<JSONObject> fileList = this.dataListQuery(params);
            if (fileList.size() > 0) {
                for (JSONObject file : fileList) {
                    this.deleteOneFile(file.getString("id"), file.getString("fileName"), file.getString("mainKanbanId"));
                }
            }
            //--
            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + mainKanbanId;
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
            fileInfo.put("mainKanbanId", mainKanbanId);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            componentTestDocMgrDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    //..p
    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }
}
