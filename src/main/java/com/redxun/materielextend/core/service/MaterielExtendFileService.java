package com.redxun.materielextend.core.service;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.DateUtil;
import com.redxun.materielextend.core.dao.MaterielExtendFileDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2020 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2020/6/6 15:07
 */
@Service
public class MaterielExtendFileService {
    private static final Logger logger = LoggerFactory.getLogger(MaterielExtendFileService.class);

    @Autowired
    private MaterielExtendFileDao materielExtendFileDao;

    public List<JSONObject> listData() {
        List<JSONObject> data = materielExtendFileDao.listData();
        // 格式化时间
        for (JSONObject oneData : data) {
            if (StringUtils.isNotBlank(oneData.getString("CREATE_TIME_"))) {
                oneData.put("CREATE_TIME_", DateUtil.formatDate(oneData.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return data;
    }

    public void saveCommonUploadFiles(HttpServletRequest request) {
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
            Map<String, String> fileInfo = new HashMap<>();
            String fileName = parameters.get("fileName")[0];
            fileInfo.put("fileName", fileName);
            fileInfo.put("size", parameters.get("fileSize")[0]);
            fileInfo.put("id", IdUtil.getId());
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            fileInfo.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String materielExtendFileDir = WebAppUtil.getProperty("materielExtendFileDir");
            File pathFile = new File(materielExtendFileDir);
            // 目录不存在则创建
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = "";
            String[] arr = fileName.split("\\.", -1);
            if (arr.length > 1) {
                suffix = arr[arr.length - 1];
            }
            String fileFullPath = materielExtendFileDir + File.separator + fileInfo.get("id") + "." + suffix;
            File file = new File(fileFullPath);
            // 文件存在则更新掉
            if (file.exists()) {
                logger.warn("File " + fileFullPath + " will be deleted");
                file.delete();
            }
            FileCopyUtils.copy(mf.getBytes(), file);

            // 插入数据库
            materielExtendFileDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveCommonUploadFiles", e);
        }
        return;
    }

    public void deleteOneFile(String fileId, String fileName) {
        try {
            String projectFilePathBase = WebAppUtil.getProperty("materielExtendFileDir");
            String suffix = "";
            String[] arr = fileName.split("\\.", -1);
            if (arr.length > 1) {
                suffix = arr[arr.length - 1];
            }
            String fullFilePath = projectFilePathBase + File.separator + fileId + "." + suffix;
            // 先删除磁盘中的文件
            File file = new File(fullFilePath);
            if (file.exists()) {
                logger.info("File " + fullFilePath + " will be deleted!");
                file.delete();
            }
            // 删除数据库中的数据
            materielExtendFileDao.deleteFileById(fileId);
        } catch (Exception e) {
            logger.error("Exception in deleteOneFile", e);
        }
    }
}
