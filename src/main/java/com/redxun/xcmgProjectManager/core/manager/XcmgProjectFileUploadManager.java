
package com.redxun.xcmgProjectManager.core.manager;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
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
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.core.util.StringUtil;
import com.redxun.fzsj.core.dao.FzsjDao;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmCommon.core.util.StringProcessUtil;
import com.redxun.rdmZhgl.core.dao.ProductConfigDao;
import com.redxun.rdmZhgl.core.dao.ProductDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectConfigDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectDeliveryProductDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.zlgjNPI.core.dao.ProductRequireDao;

@Service
public class XcmgProjectFileUploadManager {
    private Logger logger = LoggerFactory.getLogger(XcmgProjectFileUploadManager.class);

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private FzsjDao fzsjDao;
    @Autowired
    private XcmgProjectConfigDao xcmgProjectConfigDao;
    @Resource
    private XcmgProjectDao xcmgProjectDao;
    @Resource
    private XcmgProjectDeliveryProductDao xcmgProjectDeliveryProductDao;
    @Resource
    private ProductConfigDao productConfigDao;
    @Resource
    private ProductDao productDao;
    @Resource
    private ProductRequireDao productRequireDao;

    // 保存文件到后台和数据库
    public void saveUploadFiles(HttpServletRequest request) {
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
            String deliveryId = parameters.get("fileDeliveryId")[0];
            String projectId = parameters.get("projectId")[0];
            fileInfo.put("deliveryId", deliveryId);
            String fileName = parameters.get("fileName")[0];
            // 如果是变更评审材料，则在文件名最前面加上(yyyyMMddHHmmssSSS),此处已废止，不会进来
            if ("0".equals(fileInfo.get("deliveryId"))) {
                String prefix = XcmgProjectUtil.getNowLocalDateStr("yyyyMMddHHmmssSSS");
                fileName = "(" + prefix + ")" + fileName;
            }
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", parameters.get("fileSize")[0]);
            fileInfo.put("pid", parameters.get("pid")[0]);
            fileInfo.put("relativeFilePath", parameters.get("relativeFilePath")[0]);
            fileInfo.put("isFolder", "0");
            fileInfo.put("projectId", projectId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            fileInfo.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            if (parameters.get("productIds") != null) {
                String productIds = parameters.get("productIds")[0];
                fileInfo.put("productIds", productIds);
            }
            if (parameters.get("productNames") != null) {
                String productNames = parameters.get("productNames")[0];
                fileInfo.put("productNames", productNames);
            }

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String projectFilePathBase = WebAppUtil.getProperty("projectFilePathBase");
            String projectFilePathBase_preview = WebAppUtil.getProperty("projectFilePathBase_preview");
            if (StringUtils.isBlank(projectFilePathBase) || StringUtils.isBlank(projectFilePathBase_preview)) {
                logger.error("can't find projectFilePathBase or projectFilePathBase_preview");
                return;
            }
            // 处理下载目录中的文件
            String filePath = projectFilePathBase
                + (StringUtils.isNotBlank(fileInfo.get("relativeFilePath")) ? fileInfo.get("relativeFilePath") : "");
            File pathFile = new File(filePath);
            // 目录不存在则创建
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String fileFullPath = filePath + File.separator + fileInfo.get("fileName");
            File file = new File(fileFullPath);
            // 文件存在则更新掉
            if (file.exists()) {
                logger.warn("File " + fileFullPath + " will be deleted");
                file.delete();
            }
            FileCopyUtils.copy(mf.getBytes(), file);

            // 处理预览目录中的文件
            String filePath_preview = projectFilePathBase_preview
                + (StringUtils.isNotBlank(fileInfo.get("relativeFilePath")) ? fileInfo.get("relativeFilePath") : "");
            File pathFile_preview = new File(filePath_preview);
            // 目录不存在则创建
            if (!pathFile_preview.exists()) {
                pathFile_preview.mkdirs();
            }
            String fileFullPath_preview = filePath_preview + File.separator + fileInfo.get("fileName");
            File file_preview = new File(fileFullPath_preview);
            // 文件存在则更新掉
            if (file_preview.exists()) {
                logger.warn("File " + fileFullPath_preview + " will be deleted");
                file_preview.delete();
            }
            // 删除预览文件夹中生成的临时pdf文件
            String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
            String convertPdfPath = filePath_preview + File.separator + convertPdfDir + File.separator
                + OfficeDocPreview.generateConvertPdfFileName(fileName);
            File tmpPdfFile = new File(convertPdfPath);
            tmpPdfFile.delete();
            FileCopyUtils.copy(mf.getBytes(), file_preview);

            // 查找数据库中这个目录下是否有同名的文件，如果有则更新，没有则新增
            Map<String, Object> params = new HashMap<>();
            params.put("TENANT_ID_", fileInfo.get("TENANT_ID_"));
            params.put("relativeFilePath", fileInfo.get("relativeFilePath"));
            params.put("fileName", fileInfo.get("fileName"));
            params.put("projectId", fileInfo.get("projectId"));
            List<Map<String, Object>> sameNameFilesUnderPath = queryProjectFileInfos(params);
            if (sameNameFilesUnderPath != null && !sameNameFilesUnderPath.isEmpty()) {
                logger.warn("File " + fileFullPath + " is db will be update");
                fileInfo.put("id", sameNameFilesUnderPath.get(0).get("id").toString());
                xcmgProjectOtherDao.updateProjectFileInfos(fileInfo);
            } else {
                fileInfo.put("id", IdUtil.getId());
                xcmgProjectOtherDao.createProjectFileInfos(fileInfo);
            }
            /**
             * 1.如果是产品研发类and 无需审核的，有适用产品的，则推送到新品试制模块的完成日期
             */
            // 1.获取项目信息
            JSONObject projectObj = xcmgProjectDao.queryProjectById(projectId);
            if (ConstantUtil.PRODUCT_CATEGORY.equals(projectObj.getString("categoryId"))) {
                // 根据项目交付物查询交付物是否需要审批
                JSONObject deliveryObj = xcmgProjectDeliveryProductDao.getDeliveryObj(deliveryId);
                // 无需审批的直接推送
                if (StringUtil.isEmpty(deliveryObj.getString("fileType"))) {
                    if (parameters.get("productIds") != null) {
                        String productIds = parameters.get("productIds")[0];
                        // 无对应产品的不推送
                        if (StringUtil.isNotEmpty(productIds)) {
                            sendInfoToNewProduct(deliveryId, productIds);
                        }
                    }

                }
            }
        } catch (Exception e) {
            logger.error("Exception in getFilesFromRequest", e);
        }
        return;
    }

    public void sendInfoToNewProduct(String deliveryId, String productIds) {
        try {
            String[] productIdArry = productIds.split(",");
            // 根据交付物id获取绑定的新品试制节点
            List<JSONObject> stageList = productConfigDao.getStageList(deliveryId);
            if (!stageList.isEmpty()) {
                for (String productId : productIdArry) {
                    JSONObject newProductObj = productDao.getNewProductInfo(productId);
                    if (newProductObj != null) {
                        String mainId = newProductObj.getString("id");
                        JSONObject paramJson = new JSONObject();
                        paramJson.put("mainId", mainId);
                        paramJson.put("itemType", "4");
                        int count = 0;
                        for (JSONObject temp : stageList) {
                            // 先判断阶段时间是否已经填写，已填写则不更新
                            paramJson.put("stageName", temp.getString("itemCode"));
                            String finishDate = productDao.getStageFinishDate(paramJson);
                            if (finishDate == null) {
                                paramJson.put(temp.getString("itemCode"),
                                    XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                                count++;
                            }
                        }
                        if (count > 0) {
                            productDao.updateOrgDate(paramJson);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("sendInfoToNewProduct", e);
        }
    }

    // 从数据库中查询文档信息
    public List<Map<String, Object>> queryProjectFileInfos(Map<String, Object> params) {
        try {
            return xcmgProjectOtherDao.queryProjectFileInfos(params);
        } catch (Exception e) {
            logger.error("Exception in queryProjectFileInfos", e);
            return null;
        }
    }

    /**
     * 创建某个项目的所有文件夹，包括外层和里面各个阶段的。由保存草稿或提交流程时调用，原有的创建文件夹触发机制短期内保留。 某个文件夹如果已存在则跳过，同时将各层文件夹信息保存到数据库中
     * 
     * @param param
     */
    public void createProjectAllFolders(JSONObject param) {
        String projectId = param.getString("projectId");
        String projectName = param.getString("projectName");
        String categoryId = param.getString("categoryId");
        if (StringUtils.isBlank(projectId) || StringUtils.isBlank(projectName) || StringUtils.isBlank(categoryId)) {
            logger.error("projectId or projectName or categoryId is blank");
            return;
        }
        String projectFilePathBase = WebAppUtil.getProperty("projectFilePathBase");
        String projectFilePathBase_preview = WebAppUtil.getProperty("projectFilePathBase_preview");
        if (StringUtils.isBlank(projectFilePathBase) || StringUtils.isBlank(projectFilePathBase_preview)) {
            logger.error("can't find projectFilePathBase or projectFilePathBase_preview");
            return;
        }
        // 查询对应的阶段
        Map<String, Object> params = new HashMap<>();
        params.put("categoryId", categoryId);
        List<Map<String, String>> stageInfos = xcmgProjectOtherDao.queryStage(params);
        if (stageInfos == null || stageInfos.isEmpty()) {
            logger.error("can't find stageInfos, projectId is " + projectId);
            return;
        }
        // 根目录
        Map<String, String> rootInfos = new HashMap<>();
        rootInfos.put("id", projectId);
        rootInfos.put("projectId", projectId);
        rootInfos.put("fileName", projectName);
        rootInfos.put("pid", "-1");
        rootInfos.put("isFolder", "1");
        createOneFloderAndInsertDB(rootInfos, projectFilePathBase, projectFilePathBase_preview);
        // 阶段目录
        for (Map<String, String> oneStage : stageInfos) {
            oneStage.put("id", oneStage.get("stageId"));
            oneStage.put("projectId", projectId);
            oneStage.put("fileName", oneStage.get("stageName"));
            oneStage.put("pid", projectId);
            oneStage.put("relativeFilePath", "/" + projectId);
            oneStage.put("isFolder", "1");
            createOneFloderAndInsertDB(oneStage, projectFilePathBase, projectFilePathBase_preview);
        }

    }

    private void createOneFloderAndInsertDB(Map<String, String> fileInfo, String projectFilePathBase,
        String projectFilePathBase_preview) {
        String relativeFilePath =
            StringUtils.isNotBlank(fileInfo.get("relativeFilePath")) ? fileInfo.get("relativeFilePath") : "";
        String id = fileInfo.get("id");
        // 父目录
        String filePath_preview = projectFilePathBase_preview + relativeFilePath;
        String filePath = projectFilePathBase + relativeFilePath;
        File pathFile_preview = new File(filePath_preview);
        if (!pathFile_preview.exists()) {
            pathFile_preview.mkdirs();
        }
        File pathFile = new File(filePath);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }

        // 要创建的目录
        String fileFullPath_preview = filePath_preview + File.separator + id;
        File file_preview = new File(fileFullPath_preview);
        if (!file_preview.exists()) {
            file_preview.mkdirs();
        }
        String fileFullPath = filePath + File.separator + id;
        File file = new File(fileFullPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        // 存储到数据库
        fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        fileInfo.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        fileInfo.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        xcmgProjectOtherDao.createProjectFileInfos(fileInfo);
    }

    // 创建文件夹，如果已经存在则不再创建（/projectId/stageId/），由于根目录（projectId）的执行比taskCreate晚
    // 所以根目录需要写入DB（因为根目录有可能是子目录创建的时候连带创建的，但并没有写入sql数据库）
    public void createFolder(Map<String, String> fileInfo, boolean isRoot) {
        String projectFilePathBase = WebAppUtil.getProperty("projectFilePathBase");
        String projectFilePathBase_preview = WebAppUtil.getProperty("projectFilePathBase_preview");
        if (StringUtils.isBlank(projectFilePathBase) || StringUtils.isBlank(projectFilePathBase_preview)) {
            logger.error("can't find projectFilePathBase or projectFilePathBase_preview");
            return;
        }
        // 创建预览目录
        String filePath_preview = projectFilePathBase_preview
            + (StringUtils.isNotBlank(fileInfo.get("relativeFilePath")) ? fileInfo.get("relativeFilePath") : "");
        File pathFile_preview = new File(filePath_preview);
        if (!pathFile_preview.exists()) {
            pathFile_preview.mkdirs();
        }
        String fileFullPath_preview = filePath_preview + File.separator + fileInfo.get("id");
        File file_preview = new File(fileFullPath_preview);
        if (!file_preview.exists()) {
            file_preview.mkdirs();
        }

        // 处理下载目录
        String filePath = projectFilePathBase
            + (StringUtils.isNotBlank(fileInfo.get("relativeFilePath")) ? fileInfo.get("relativeFilePath") : "");
        File pathFile = new File(filePath);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String fileFullPath = filePath + File.separator + fileInfo.get("id");
        File file = new File(fileFullPath);
        if (file.exists()) {
            if (isRoot) {
                xcmgProjectOtherDao.createProjectFileInfos(fileInfo);
                return;
            }
            logger.info("Folder " + fileFullPath + " already exists!");
            return;
        }
        // 创建文件夹
        file.mkdirs();
        xcmgProjectOtherDao.createProjectFileInfos(fileInfo);
    }

    // 删除一个文件
    public void deleteProjectOneFile(String projectId, String id, String relativeFilePath, String fileName,
        boolean isCommonFile, String action) {
        try {
            String fullFilePath = "";
            if (StringUtils.isBlank(fileName)) {
                logger.error("fileName is blank");
                return;
            }
            String projectFilePathBase = null;
            if (ConstantUtil.PREVIEW.equalsIgnoreCase(action)) {
                projectFilePathBase = isCommonFile ? WebAppUtil.getProperty("commonFilePathBase_preview")
                    : WebAppUtil.getProperty("projectFilePathBase_preview");
            } else {
                projectFilePathBase = isCommonFile ? WebAppUtil.getProperty("commonFilePathBase")
                    : WebAppUtil.getProperty("projectFilePathBase");
            }
            if (StringUtils.isBlank(projectFilePathBase)) {
                logger.error("can't find projectFilePathBase");
                return;
            }
            String filePath = projectFilePathBase + (StringUtils.isNotBlank(relativeFilePath) ? relativeFilePath : "");
            fullFilePath = filePath + File.separator + fileName;
            // 先删除磁盘中的文件
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
            // 删除数据库中的数据，只需删除一遍
            if (ConstantUtil.DOWNLOAD.equalsIgnoreCase(action)) {
                Map<String, Object> params = new HashMap<>();
                params.put("id", id);
                params.put("projectId", projectId);
                params.put("fileName", fileName);
                params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                xcmgProjectOtherDao.deleteProjectFileInfos(params);
            }
            // 删除预览文件夹中生成的临时pdf文件
            if (ConstantUtil.PREVIEW.equalsIgnoreCase(action)) {
                String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
                String convertPdfPath = filePath + File.separator + convertPdfDir + File.separator
                    + OfficeDocPreview.generateConvertPdfFileName(fileName);
                File tmpPdfFile = new File(convertPdfPath);
                tmpPdfFile.delete();
            }
        } catch (Exception e) {
            logger.error("Exception in deleteProjectFiles", e);
        }
    }

    // 删除项目
    public void deleteProject(String projectId) {
        // 需要先从DB中读取所有的文件和文件夹，然后从底层开始删除，最后删除项目
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("projectId", projectId);
        List<Map<String, Object>> projectFileInfos = queryProjectFileInfos(params);
        if (projectFileInfos == null || projectFileInfos.isEmpty()) {
            return;
        }
        String projectFilePathBase = WebAppUtil.getProperty("projectFilePathBase");
        String projectFilePathBase_preview = WebAppUtil.getProperty("projectFilePathBase_preview");
        if (StringUtils.isBlank(projectFilePathBase) || StringUtils.isBlank(projectFilePathBase_preview)) {
            logger.error("can't find projectFilePathBase or projectFilePathBase_preview");
            return;
        }
        // 删除下载目录中的文件
        deleteProjectFiles(projectId, projectFilePathBase, projectFileInfos);
        // 删除数据库中的数据
        xcmgProjectOtherDao.deleteProjectFileInfos(params);

        // 删除预览目录中的文件
        deleteProjectFiles(projectId, projectFilePathBase_preview, projectFileInfos);
    }

    // 先删除文件，然后是阶段文件夹下的tmp，然后是阶段文件夹，最后是项目文件夹
    private void deleteProjectFiles(String projectId, String pathBase, List<Map<String, Object>> projectFileInfos) {
        List<File> files = new ArrayList<>();
        List<File> stageTmpFiles = new ArrayList<>();
        List<File> stageFolder = new ArrayList<>();
        File projectFolder = null;
        for (Map<String, Object> oneFile : projectFileInfos) {
            // 文件
            if ("0".equals(oneFile.get("isFolder").toString())) {
                String filePath = pathBase + (StringUtils.isNotBlank(oneFile.get("relativeFilePath").toString())
                    ? oneFile.get("relativeFilePath").toString() : "");
                String fullFilePath = filePath + File.separator + oneFile.get("fileName").toString();
                File file = new File(fullFilePath);
                files.add(file);
            } else if (projectId.equals(oneFile.get("pid").toString())) {
                // 阶段文件夹
                String filePath = pathBase + (StringUtils.isNotBlank(oneFile.get("relativeFilePath").toString())
                    ? oneFile.get("relativeFilePath").toString() : "");
                String fullFilePath = filePath + File.separator + oneFile.get("id").toString();
                File file = new File(fullFilePath);
                stageFolder.add(file);
                // 删除阶段文件夹下的tmp文件夹
                String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
                String tmpDirPath = fullFilePath + File.separator + convertPdfDir;
                File tmpDirFile = new File(tmpDirPath);
                if (tmpDirFile.exists()) {
                    stageTmpFiles.add(tmpDirFile);
                    // 删除阶段文件夹下的tmp文件夹下的文件
                    getTmpFileUnderStage(files, tmpDirFile);
                }
            } else if ("-1".equals(oneFile.get("pid").toString())) {
                // 项目文件夹
                projectFolder = new File(pathBase + File.separator + oneFile.get("id").toString());
            }
        }

        // 文件删除
        for (File file : files) {
            try {
                file.delete();
            } catch (Exception e) {
                logger.error("Exception in delete file, file name is " + file.getAbsolutePath(), e);
            }
        }
        // 阶段下的tmp删除
        for (File file : stageTmpFiles) {
            try {
                file.delete();
            } catch (Exception e) {
                logger.error("Exception in delete file, file name is " + file.getAbsolutePath(), e);
            }
        }
        // 阶段删除
        for (File file : stageFolder) {
            try {
                file.delete();
            } catch (Exception e) {
                logger.error("Exception in delete file, file name is " + file.getAbsolutePath(), e);
            }
        }
        // 项目删除
        try {
            projectFolder.delete();
        } catch (Exception e) {
            logger.error("Exception in delete file, file name is " + projectFolder.getAbsolutePath(), e);
        }
    }

    // 将tmpDir文件夹下的文件添加到要删除的文件list中
    private void getTmpFileUnderStage(List<File> files, File tmpDirFile) {
        File[] filesUnderTmp = tmpDirFile.listFiles();
        if (filesUnderTmp != null && filesUnderTmp.length != 0) {
            files.addAll(Arrays.asList(filesUnderTmp));
        }
    }

    // 从数据库中查询文档信息
    public List<Map<String, Object>> queryXcmgDocMgrList(Map<String, Object> params) {
        try {
            return xcmgProjectOtherDao.queryXcmgDocMgrList(params);
        } catch (Exception e) {
            logger.error("Exception in queryXcmgDocMgrList", e);
            return null;
        }
    }

    // 保存文件到后台和数据库
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
            fileInfo.put("fileSize", parameters.get("fileSize")[0]);
            fileInfo.put("pid", "");
            fileInfo.put("relativeFilePath", "");
            fileInfo.put("isFolder", "0");
            fileInfo.put("projectId", "");
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            fileInfo.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String commonFilePathBase = WebAppUtil.getProperty("commonFilePathBase");
            String commonFilePathBase_preview = WebAppUtil.getProperty("commonFilePathBase_preview");
            if (StringUtils.isBlank(commonFilePathBase) || StringUtils.isBlank(commonFilePathBase_preview)) {
                logger.error("can't find commonFilePathBase or commonFilePathBase_preview");
                return;
            }
            String relativeFilePath =
                StringUtils.isNotBlank(fileInfo.get("relativeFilePath")) ? fileInfo.get("relativeFilePath") : "";
            // 处理下载目录中的文件
            String filePath = commonFilePathBase + relativeFilePath;
            File pathFile = new File(filePath);
            // 目录不存在则创建
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String fileFullPath = filePath + File.separator + fileInfo.get("fileName");
            File file = new File(fileFullPath);
            // 文件存在则更新掉
            if (file.exists()) {
                logger.warn("File " + fileFullPath + " will be deleted");
                file.delete();
            }
            FileCopyUtils.copy(mf.getBytes(), file);

            // 处理预览目录中的文件
            String filePath_preview = commonFilePathBase_preview + relativeFilePath;
            File pathFile_preview = new File(filePath_preview);
            // 目录不存在则创建
            if (!pathFile_preview.exists()) {
                pathFile_preview.mkdirs();
            }
            String fileFullPath_preview = filePath_preview + File.separator + fileInfo.get("fileName");
            File file_preview = new File(fileFullPath_preview);
            // 文件存在则更新掉
            if (file_preview.exists()) {
                logger.warn("File " + fileFullPath_preview + " will be deleted");
                file_preview.delete();
            }
            // 删除预览文件夹中生成的临时pdf文件
            String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
            String convertPdfPath = filePath_preview + File.separator + convertPdfDir + File.separator
                + OfficeDocPreview.generateConvertPdfFileName(fileName);
            File tmpPdfFile = new File(convertPdfPath);
            tmpPdfFile.delete();
            FileCopyUtils.copy(mf.getBytes(), file_preview);

            // 查找数据库中这个目录下是否有同名的文件，如果有则更新，没有则新增
            Map<String, Object> params = new HashMap<>();
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            params.put("fileName", fileInfo.get("fileName"));
            List<Map<String, Object>> sameNameFilesUnderPath = xcmgProjectOtherDao.queryXcmgDocMgrSameNameList(params);
            if (sameNameFilesUnderPath != null && !sameNameFilesUnderPath.isEmpty()) {
                logger.warn("File " + fileInfo.get("fileName") + " in db will be update");
                fileInfo.put("id", sameNameFilesUnderPath.get(0).get("id").toString());
                xcmgProjectOtherDao.updateProjectFileInfos(fileInfo);
            } else {
                fileInfo.put("id", IdUtil.getId());
                xcmgProjectOtherDao.createProjectFileInfos(fileInfo);
            }
        } catch (Exception e) {
            logger.error("Exception in getFilesFromRequest", e);
        }
        return;
    }

    // 查找关联某个科技项目的仿真申请（running/success_end），并组装成交付物格式
    public List<Map<String, Object>> queryFzsjByProjectId(String projectId, String stageId) {
        if (StringUtils.isBlank(projectId)) {
            return Collections.emptyList();
        }
        // 查询“仿真申请书”的交付物类型信息
        Map<String, Object> param = new HashMap<>();
        param.put("projectId", projectId);
        param.put("deliveryName", RdmConst.PROJECT_DELIVERY_FZSQS);
        if (StringUtils.isNotBlank(stageId)) {
            param.put("stageId", stageId);
        }
        List<JSONObject> deliveryInfos = xcmgProjectConfigDao.queryDeliveryByNameAndProjectId(param);
        if (deliveryInfos == null || deliveryInfos.isEmpty()) {
            return Collections.emptyList();
        }
        // 查询仿真申请流程
        List<JSONObject> fzsjList = fzsjDao.queryFzsjByProject(param);
        if (fzsjList == null || fzsjList.isEmpty()) {
            return Collections.emptyList();
        }

        // 组装成交付物
        List<Map<String, Object>> fzsjDeliveryList = new ArrayList<>();
        for (JSONObject oneFzsj : fzsjList) {
            Map<String, Object> oneFzsjFile = new HashMap<>();
            oneFzsjFile.put("fileSource", "sdm");
            oneFzsjFile.put("creator", StringProcessUtil.toGetStringNotNull(oneFzsj.getString("creator")));
            String createTime = "";
            if (StringUtils.isNotBlank(oneFzsj.getString("CREATE_TIME_"))) {
                createTime = oneFzsj.getString("CREATE_TIME_").split(" ", -1)[0];
            }
            oneFzsjFile.put("CREATE_TIME_", createTime);
            oneFzsjFile.put("fileName", StringProcessUtil.toGetStringNotNull(oneFzsj.getString("questName")));
            oneFzsjFile.put("fileSize", "");
            oneFzsjFile.put("id", StringProcessUtil.toGetStringNotNull(oneFzsj.getString("id")));
            oneFzsjFile.put("isFolder", "0");
            oneFzsjFile.put("projectId", projectId);
            oneFzsjFile.put("deliveryId", deliveryInfos.get(0).getString("deliveryId"));
            oneFzsjFile.put("deliveryName", deliveryInfos.get(0).getString("deliveryName"));
            oneFzsjFile.put("approvalStatus",
                "yes".equalsIgnoreCase(oneFzsj.getString("sqlcsfjs")) ? ConstantUtil.SDM_APPROVAL_FINAL : "审批中");
            oneFzsjFile.put("filePath", StringProcessUtil.toGetStringNotNull(oneFzsj.getString("fzNumber")));
            oneFzsjFile.put("pid", deliveryInfos.get(0).getString("stageId"));
            fzsjDeliveryList.add(oneFzsjFile);
        }
        return fzsjDeliveryList;
    }

    // 查找关联某个科技项目的产品开发需求（running/success_end），并组装成交付物格式
    public List<Map<String, Object>> queryCpjyByProjectId(String projectId, String stageId) {
        if (StringUtils.isBlank(projectId)) {
            return Collections.emptyList();
        }
        // 查询“产品开发需求”的交付物类型信息
        Map<String, Object> param = new HashMap<>();
        param.put("fileType", "产品开发建议书");
        param.put("projectId", projectId);
        param.put("deliveryName", RdmConst.PROJECT_DELIVERY_CPJY);
        if (StringUtils.isNotBlank(stageId)) {
            param.put("stageId", stageId);
        }
        //通过项目id和交付物类型名称查询交付物类型信息
        List<JSONObject> deliveryInfos = xcmgProjectConfigDao.queryDeliveryByNameAndProjectId(param);
        if (deliveryInfos == null || deliveryInfos.isEmpty()) {
            return Collections.emptyList();
        }
        // 查询申请流程
        List<JSONObject> fzsjList = productRequireDao.queryProductRequireByProjectId(param);
        if (fzsjList == null || fzsjList.isEmpty()) {
            return Collections.emptyList();
        }

        // 组装成交付物
        List<Map<String, Object>> fzsjDeliveryList = new ArrayList<>();
        for (JSONObject oneFzsj : fzsjList) {
            Map<String, Object> oneFzsjFile = new HashMap<>();
            oneFzsjFile.put("fileSource", "cpkf");
            oneFzsjFile.put("creator", StringProcessUtil.toGetStringNotNull(oneFzsj.getString("creator")));
            String createTime = "";
            if (StringUtils.isNotBlank(oneFzsj.getString("CREATE_TIME_"))) {
                createTime = oneFzsj.getString("CREATE_TIME_").split(" ", -1)[0];
            }
            oneFzsjFile.put("CREATE_TIME_", createTime);
            oneFzsjFile.put("fileName", oneFzsj.getString("fileType"));
            oneFzsjFile.put("fileSize", "");
            oneFzsjFile.put("cpkfId", StringProcessUtil.toGetStringNotNull(oneFzsj.getString("cpkfId")));
            oneFzsjFile.put("isFolder", "0");
            oneFzsjFile.put("projectId", projectId);
            oneFzsjFile.put("deliveryId", deliveryInfos.get(0).getString("deliveryId"));
            oneFzsjFile.put("deliveryName", deliveryInfos.get(0).getString("deliveryName"));
            oneFzsjFile.put("approvalStatus",
                "SUCCESS_END".equalsIgnoreCase(oneFzsj.getString("taskStatus")) ? "审批完成" : "审批中");
            oneFzsjFile.put("filePath", StringProcessUtil.toGetStringNotNull(oneFzsj.getString("lcNum")));
            oneFzsjFile.put("pid", deliveryInfos.get(0).getString("stageId"));
            fzsjDeliveryList.add(oneFzsjFile);
        }
        return fzsjDeliveryList;
    }

    public List<Map<String, Object>> queryScfxByProjectId(String projectId, String stageId) {
        if (StringUtils.isBlank(projectId)) {
            return Collections.emptyList();
        }
        // 查询“产品开发需求”的交付物类型信息
        Map<String, Object> param = new HashMap<>();
        param.put("fileType", "产品竞争力分析报告");
        param.put("projectId", projectId);
        param.put("deliveryName", RdmConst.PROJECT_DELIVERY_SCFX);
        if (StringUtils.isNotBlank(stageId)) {
            param.put("stageId", stageId);
        }
        List<JSONObject> deliveryInfos = xcmgProjectConfigDao.queryDeliveryByNameAndProjectId(param);
        if (deliveryInfos == null || deliveryInfos.isEmpty()) {
            return Collections.emptyList();
        }
        // 查询申请流程
        List<JSONObject> fzsjList = productRequireDao.queryProductRequireByProjectId(param);
        if (fzsjList == null || fzsjList.isEmpty()) {
            return Collections.emptyList();
        }

        // 组装成交付物
        List<Map<String, Object>> fzsjDeliveryList = new ArrayList<>();
        for (JSONObject oneFzsj : fzsjList) {
            Map<String, Object> oneFzsjFile = new HashMap<>();
            oneFzsjFile.put("fileSource", "cpkf");
            oneFzsjFile.put("creator", StringProcessUtil.toGetStringNotNull(oneFzsj.getString("creator")));
            String createTime = "";
            if (StringUtils.isNotBlank(oneFzsj.getString("CREATE_TIME_"))) {
                createTime = oneFzsj.getString("CREATE_TIME_").split(" ", -1)[0];
            }
            oneFzsjFile.put("CREATE_TIME_", createTime);
            oneFzsjFile.put("fileName", oneFzsj.getString("fileType"));
            oneFzsjFile.put("fileSize", "");
            oneFzsjFile.put("cpkfId", StringProcessUtil.toGetStringNotNull(oneFzsj.getString("cpkfId")));
            oneFzsjFile.put("isFolder", "0");
            oneFzsjFile.put("projectId", projectId);
            oneFzsjFile.put("deliveryId", deliveryInfos.get(0).getString("deliveryId"));
            oneFzsjFile.put("deliveryName", deliveryInfos.get(0).getString("deliveryName"));
            oneFzsjFile.put("approvalStatus",
                "SUCCESS_END".equalsIgnoreCase(oneFzsj.getString("taskStatus")) ? "审批完成" : "审批中");
            oneFzsjFile.put("filePath", StringProcessUtil.toGetStringNotNull(oneFzsj.getString("lcNum")));
            oneFzsjFile.put("pid", deliveryInfos.get(0).getString("stageId"));
            fzsjDeliveryList.add(oneFzsjFile);
        }
        return fzsjDeliveryList;
    }
}
