package com.redxun.productDataManagement.core.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.productDataManagement.core.dao.PDMOpsDevDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
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
import java.io.IOException;
import java.util.*;

import static com.redxun.rdmCommon.core.util.RdmCommonUtil.toGetParamVal;


@Service
public class PDMOpsDevManager {
    private static final Logger logger = LoggerFactory.getLogger(PDMOpsDevManager.class);
    @Autowired
    private PDMOpsDevDao pdmOpsDevDao;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    //..
    public JsonPageResult<?> queryApplyList(HttpServletRequest request, boolean doPage){
        JsonPageResult result = new JsonPageResult(true);
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
        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        List<Map<String, Object>> sulotionFileList = pdmOpsDevDao.queryApplyList(params);
        for (Map<String, Object> business : sulotionFileList) {
            if (business.get("CREATE_TIME_") != null) {
                business.put("CREATE_TIME_", DateUtil.formatDate((Date) business.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        result.setData(sulotionFileList);
        result.setTotal(sulotionFileList.size());
        return result;
    }
    // 根据id查询详情
    public Map<String, Object> queryfileById(String id) {

        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        Map<String, Object> oneInfo = pdmOpsDevDao.queryfileById(param);
        return oneInfo;
    }

    // 保存（包括新增保存、编辑保存）
    public void saveFile(JSONObject result, HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            result.put("message", "没有找到上传的参数！");
            result.put("success", false);
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
            if (fileMap == null || fileMap.isEmpty()) {
                logger.warn("没有找到上传的文件");
                result.put("message", "没有找到上传的文件！");
                result.put("success", false);
                return;
            }
            String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                    "pdmUploadPosition", "pdmSolution").getValue();
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase");
                result.put("message", "没有找到上传的pdmUploadPosition！");
                result.put("success", false);
                return;
            }

            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileType = toGetParamVal(parameters.get("fileType"));
            String fileDirection = toGetParamVal(parameters.get("fileDirection"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            // 向下载目录中写入文件
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String filePath = filePathBase + File.separator + id;
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
            fileInfo.put("fileType", fileType);
            fileInfo.put("fileDirection", fileDirection);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_",XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            pdmOpsDevDao.addFileInfos(fileInfo);


            try{

            }catch (Exception e) {
                logger.error("Exception in saveFile", e);
                result.put("message", "系统异常！");
                result.put("success", false);
            }

            result.put("message", "保存成功！");
            result.put("success", true);
        } catch (Exception e) {
            logger.error("Exception in saveFile", e);
            result.put("message", "系统异常！");
            result.put("success", false);
        }
    }
    private void addOrUpdatefile(Map<String, String[]> parameters, Map<String, Object> objBody, MultipartFile fileObj,
                                String id) throws IOException {

        //新增文件
        if (StringUtils.isBlank(id)) {
            String newId = IdUtil.getId();
            if (fileObj != null) {
                updatePublicFile2Disk(fileObj, parameters, newId);
            }
            // 文件添加
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", newId);
            fileInfo.put("fileName", parameters.get("fileName")[0]);
            fileInfo.put("fileType",parameters.get("fileType")[0]);
            fileInfo.put("fileSize",parameters.get("fileSize")[0]);
            fileInfo.put("fileDirection",parameters.get("fileDirection")[0]);
            fileInfo.put("filePosition",parameters.get("filePosition")[0]);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_",XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            pdmOpsDevDao.addFileInfos(fileInfo);

        } else {
            // 附件为空添加附件

            if (fileObj != null) {
                // 更新文件
                updatePublicFile2Disk( fileObj, parameters, id);
            } else {
                // fileObj为空，有可能是真的没有附件，也有可能是编辑场景（有fileName）
                // 如无fileName则用户前台希望删除该标准的文件，否则说明用户没处理
                String fileName = parameters.get("fileName")[0] == null ? "" : parameters.get("fileName")[0].toString();
                if (StringUtils.isBlank(fileName)) {
                    deleteFileFromDisk( parameters,id);
                }
            }

            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("fileName", parameters.get("fileName")[0]);
            fileInfo.put("fileType",parameters.get("fileType")[0]);
            fileInfo.put("fileSize",parameters.get("fileType")[0]);
            fileInfo.put("fileDirection",parameters.get("fileDirection")[0]);
            fileInfo.put("filePosition",parameters.get("filePosition")[0]);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));

            pdmOpsDevDao.updateFileInfos(fileInfo);
        }
    }

    private void updatePublicFile2Disk(MultipartFile fileObj,
                                               Map<String, String[]> parameters, String id) throws IOException {
        if (StringUtils.isBlank(id) || fileObj == null) {
            logger.warn("no fileId or fileObj");
            return;
        }

        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "pdmUploadPosition", "pdmSolution").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find filePathBase");
            return;
        }

        // 向下载目录中写入文件
        String filePath = filePathBase + File.separator + id;
        File pathFile = new File(filePath);
        // 文件存在则更新掉
        if (pathFile.exists()) {
            pathFile.delete();
            pathFile.mkdirs();
        }
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String suffix = CommonFuns.toGetFileSuffix(parameters.get("fileName")[0]);
        String fileFullPath = filePath + File.separator + id + "." + suffix;
        File file = new File(fileFullPath);

//        // 预览删除
//        String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
//        String convertPdfPath =
//                filePath + File.separator + File.separator + convertPdfDir + File.separator + id + ".pdf";
//        File pdffile = new File(convertPdfPath);
//        pdffile.delete();

        FileCopyUtils.copy(fileObj.getBytes(), file);

    }

    private void deleteFileFromDisk(Map<String, String[]> parameters,String id) {
        if (StringUtils.isBlank(id)) {
            logger.warn("fileId is blank");
            return;
        }
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "pdmUploadPosition", "pdmSolution").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find filePathBase");
            return;
        }

        String suffix = CommonFuns.toGetFileSuffix(parameters.get("fileName")[0]);
        String fileFullPath = filePathBase + File.separator + id + '.' + suffix;
        File file = new File(fileFullPath);
        file.delete();
    }
}
