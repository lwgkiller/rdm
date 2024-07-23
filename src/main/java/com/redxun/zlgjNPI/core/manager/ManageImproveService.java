package com.redxun.zlgjNPI.core.manager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.zlgjNPI.core.dao.ManageImproveDao;
/**
 * 管理改进建议提报
 */
@Service
public class ManageImproveService {
    private static final Logger logger = LoggerFactory.getLogger(ManageImproveService.class);

    @Autowired
    private ManageImproveDao manageImproveDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    public JsonPageResult<?> manageImproveListQuery(HttpServletRequest request, HttpServletResponse response, boolean doPage){
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    params.put(name, value);
                }
            }
        }
        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }

        List<JSONObject> manageImproveList = manageImproveDao.manageImproveListQuery(params);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        for (JSONObject manageImproveObject : manageImproveList) {
            if ( manageImproveObject.get("planFinishTime") != null) {
                manageImproveObject.put("planFinishTime", format.format(manageImproveObject.get("planFinishTime")));
            }
            if ( manageImproveObject.get("CREATE_TIME_") != null) {
                manageImproveObject.put("CREATE_TIME_", sdf.format(manageImproveObject.get("CREATE_TIME_")));
            }
        }
        // 向业务数据中写入任务相关的信息
        rdmZhglUtil.setTaskInfo2Data(manageImproveList, ContextUtil.getCurrentUserId());
        result.setData(manageImproveList);
        int count = manageImproveDao.countManageImproveQuery(params);
        result.setTotal(count);
        return result;
    }

    public JSONObject getManageImproveDetail(String manageImproveId) {
        JSONObject manageImproveDetail = manageImproveDao.getManageImproveDetail(manageImproveId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date planFinishTime = manageImproveDetail.getDate("planFinishTime");
        if (planFinishTime != null) {
            manageImproveDetail.put("planFinishTime", sdf.format(planFinishTime));
        }

        return manageImproveDetail;
    }

    public List<JSONObject> querySubTypeByCoreType(String coreTypeName) {
        List<JSONObject> subTypeList = new ArrayList<>();
        JSONObject params = new JSONObject();
        params.put("coreTypeId", coreTypeName);
        subTypeList = manageImproveDao.subTypeList(params);
        return subTypeList;
    }

    public void createManageImprove(JSONObject formDataJson) {
        formDataJson.put("id", IdUtil.getId());
        formDataJson.put("CREATE_TIME_", new Date());
        manageImproveDao.createManageImprove(formDataJson);
    }

    public void updateManageImprove(JSONObject formDataJson) {
        formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formDataJson.put("UPDATE_TIME_", new Date());
        manageImproveDao.updateManageImprove(formDataJson);
    }

    public List<JSONObject> queryManageImproveFileList(JSONObject params) {
        List<JSONObject> fileList = manageImproveDao.queryManageImproveFileList(params);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (fileList.size() > 0) {
            for (JSONObject file : fileList) {
                if (file.get("CREATE_TIME_") != null) {
                    file.put("CREATE_TIME_", sdf.format(file.get("CREATE_TIME_")));
                }
            }
        }
        return fileList;
    }

    public void delManageImproveFileById(String id, String fileName, String manageImproveId) {
        String filePath = WebAppUtil.getProperty("manageImprovePathBase");
        rdmZhglFileManager.deleteOneFileFromDisk(id, fileName, manageImproveId, filePath);
        manageImproveDao.delManageImproveFileById(id);
    }

    public void manageImproveUpload(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("manageImprovePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find manageImprovePathBase");
            return;
        }
        try {
            String belongId = toGetParamVal(parameters.get("belongId"));
            String filetype = toGetParamVal(parameters.get("filetype"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
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
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);
            // 写入数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("belongId", belongId);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("filetype", filetype);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("creator", ContextUtil.getCurrentUser().getFullname());
            fileInfo.put("CREATE_TIME_", new Date());
            manageImproveDao.addFiles(fileInfo);
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
    //..
    public JsonResult deleteManageImprove(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        JSONObject param = new JSONObject();
        param.put("belongIds", businessIds);
        List<JSONObject> files = manageImproveDao.queryManageImproveFileList(param);
        String filePath = WebAppUtil.getProperty("manageImprovePathBase");
        if (files.size() > 0) {
            for (JSONObject oneFile : files) {
                rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                        oneFile.getString("belongId"), filePath);
            }
            for (String id : ids) {
                rdmZhglFileManager.deleteDirFromDisk(id, filePath);
            }
            manageImproveDao.delFileByBelongId(param);
        }
        manageImproveDao.deleteManageImprove(param);


        return result;
    }

}

