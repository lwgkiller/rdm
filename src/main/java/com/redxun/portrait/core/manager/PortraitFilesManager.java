package com.redxun.portrait.core.manager;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.portrait.core.dao.PortraitFilesDao;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.sys.core.util.SysPropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.util.*;


/**
 * @author zhangzhen
 */
@Service
public class PortraitFilesManager {
    private static final Logger logger = LoggerFactory.getLogger(PortraitFilesManager.class);
    @Resource
    PortraitFilesDao portraitFilesDao;
    @Resource
    private RdmZhglFileManager rdmZhglFileManager;
    public JsonPageResult<?> getFileList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            String mainId = request.getParameter("mainId");
            String fileType = request.getParameter("fileType");
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            params.put("mainId", mainId);
            params.put("fileType", fileType);
            list = portraitFilesDao.getFileList(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            params.put("detailId", mainId);
            params.put("fileType", fileType);
            totalList = portraitFilesDao.getFileList(params);
            CommonFuns.convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }
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
        String filePathBase = SysPropertiesUtil.getGlobalProperty("portraitFileUrl");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find portraitFileUrl");
            return;
        }
        try {
            String mainId = CommonFuns.toGetParamVal(parameters.get("mainId"));
            String id = IdUtil.getId();
            String fileName = CommonFuns.toGetParamVal(parameters.get("fileName"));
            String fileSize = CommonFuns.toGetParamVal(parameters.get("fileSize"));
            String fileDesc = CommonFuns.toGetParamVal(parameters.get("fileDesc"));
            String fileType = CommonFuns.toGetParamVal(parameters.get("fileType"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + mainId;
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
            fileInfo.put("mainId", mainId);
            fileInfo.put("fileType", fileType);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            portraitFilesDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }
    public void deleteOneSaleFile(String fileId, String fileName, String mainId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, mainId,
                SysPropertiesUtil.getGlobalProperty("portraitFileUrl"));
        Map<String, Object> param = new HashMap<>(16);
        portraitFilesDao.delFileById(fileId);
    }
}
