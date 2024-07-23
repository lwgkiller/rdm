package com.redxun.zlgjNPI.core.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.zlgjNPI.core.dao.ProductRequireDao;
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
import java.io.File;
import java.util.*;

@Service
@Slf4j
public class ProductRequireService {
    private Logger logger = LogManager.getLogger(ProductRequireService.class);
    @Autowired
    private ProductRequireDao productRequireDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private BpmInstManager bpmInstManager;


    public JsonPageResult<?> queryProductRequire(HttpServletRequest request, boolean doPage) {
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
        // addProductRequireRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> productRequireList = productRequireDao.queryProductRequire(params);
        for (JSONObject productRequire : productRequireList) {
            if (productRequire.get("CREATE_TIME_") != null) {
                productRequire.put("CREATE_TIME_", DateUtil.formatDate((Date)productRequire.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(productRequireList, ContextUtil.getCurrentUserId());
        result.setData(productRequireList);
        int countProductRequireDataList = productRequireDao.countProductRequireList(params);
        result.setTotal(countProductRequireDataList);
        return result;
    }

    public void createProductRequire(JSONObject formData) {
        formData.put("cpkfId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        productRequireDao.createProductRequire(formData);
    }

    public void updateProductRequire(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        productRequireDao.updateProductRequire(formData);
    }

    public void saveProductRequireUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("cpkfFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find cpkfFilePathBase");
            return;
        }
        try {
            String belongId = toGetParamVal(parameters.get("cpkfId"));
            String fileId = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileModel = CommonFuns.toGetParamVal(parameters.get("fileModel"));
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
            fileInfo.put("fileModel", fileModel);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            productRequireDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getProductRequireDetail(String cpkfId) {
        JSONObject detailObj = productRequireDao.queryProductRequireById(cpkfId);
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.getDate("CREATE_TIME_") != null) {
            detailObj.put("CREATE_TIME_", DateUtil.formatDate(detailObj.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
        }
        return detailObj;
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    public List<JSONObject> getProductRequireFileList(String cpkfId,String fileModel) {
        Map<String, Object> param = new HashMap<>();
        param.put("cpkfId", cpkfId);
        param.put("fileModel", fileModel);
        List<JSONObject> productRequireFileList = productRequireDao.queryProductRequireFileList(param);
        for(JSONObject productRequireFile : productRequireFileList){
            if (productRequireFile.getDate("CREATE_TIME_") != null) {
                productRequireFile.put("CREATE_TIME_", DateUtil.formatDate(productRequireFile.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return productRequireFileList;
    }

    public void deleteOneProductRequireFile(String fileId, String fileName, String cpkfId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, cpkfId, WebAppUtil.getProperty("cpkfFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("fileId", fileId);
        productRequireDao.deleteProductRequireFile(param);
    }

    public JsonResult deleteProductRequire(String[] cpkfIdsArr, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功！");
        Map<String, Object> param = new HashMap<>();
        for (String cpkfId : cpkfIdsArr) {
            // 删除基本信息
            param.clear();
            String fileModel=null;
            param.put("cpkfId", cpkfId);

            // 删除附件
            List<JSONObject> files = getProductRequireFileList(cpkfId,fileModel);
            String wjFilePathBase = WebAppUtil.getProperty("cpkfFilePathBase");
            for (JSONObject oneFile : files) {
                rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"),
                    cpkfId, wjFilePathBase);
            }
            rdmZhglFileManager.deleteDirFromDisk(cpkfId, wjFilePathBase);
            productRequireDao.deleteProductRequire(param);
            productRequireDao.deleteProductRequireFile(param);
        }
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }


}
