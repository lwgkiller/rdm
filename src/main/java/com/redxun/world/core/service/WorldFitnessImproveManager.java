package com.redxun.world.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.world.core.dao.WorldFitnessImproveDao;
import com.redxun.world.core.dao.WorldResearchDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

import static com.redxun.rdmCommon.core.util.RdmCommonUtil.toGetParamVal;

/**
 * 适应性改进
 * @author mh
 * @date 2022/4/19 10:40
 */
@Service
public class WorldFitnessImproveManager {
    private static final Logger logger = LoggerFactory.getLogger(WorldFitnessImproveManager.class);
    @Resource
    WorldResearchDao worldResearchDao;
    @Resource
    CommonInfoManager commonInfoManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private WorldFitnessImproveDao worldFitnessImproveDao;

    public JsonPageResult<?> queryApplyList(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "world_fitnessimprove_base.CREATE_TIME_", "desc");
        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
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
        // 增加角色过滤的条件
        addRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> applyList = worldFitnessImproveDao.queryApplyList(params);
        for (JSONObject oneApply : applyList) {
            if (oneApply.get("CREATE_TIME_") != null) {
                oneApply.put("CREATE_TIME_", DateUtil.formatDate((Date)oneApply.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(applyList, ContextUtil.getCurrentUserId());
        result.setData(applyList);
        int countZlgjDataList = worldFitnessImproveDao.countApplyList(params);
        result.setTotal(countZlgjDataList);
        return result;
    }

    private void addRoleParam(Map<String, Object> params, String userId, String userNo) {
        if (userNo.equalsIgnoreCase("admin")) {
            return;
        }
        params.put("currentUserId", userId);
        params.put("roleName", "other");
    }

    public void saveInProcess(JsonResult result, String data) {
        JSONObject object = JSONObject.parseObject(data);
        if (object == null || object.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        if (StringUtils.isBlank(object.getString("id"))) {
            createApply(object);
            result.setData(object.getString("id"));
        } else {
            updateApply(object);
            result.setData(object.getString("id"));
        }
    }

    public void createApply(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("departId", ContextUtil.getCurrentUser().getMainGroupId());
        formData.put("departName", ContextUtil.getCurrentUser().getMainGroupName());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        // 基本信息
        worldFitnessImproveDao.insertApply(formData);
    }

    public void updateApply(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        // 基本信息
        worldFitnessImproveDao.updateApply(formData);
    }

    public JsonResult delApplys(String[] applyIdArr) {
        List<String> applyIdList = Arrays.asList(applyIdArr);
        JsonResult result = new JsonResult(true, "操作成功！");
        if (applyIdArr.length == 0) {
            return result;
        }
        JSONObject param = new JSONObject();

        // 删除文件子表
        param.put("baseInfoIds", applyIdList);
        worldFitnessImproveDao.deleteApplyFile(param);

        // 删除主表
        param.put("applyIds", applyIdList);
        param.put("ids", applyIdList);
        worldFitnessImproveDao.deleteApply(param);
        return result;
    }

    public JSONObject queryApplyDetail(String id) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(id)) {
            return result;
        }
        JSONObject params = new JSONObject();
        params.put("id", id);
        JSONObject obj = worldFitnessImproveDao.queryApplyDetail(params);
        if (obj.get("CREATE_TIME_") != null) {
            obj.put("CREATE_TIME_", DateUtil.formatDate((Date)obj.get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
        }

        if (obj == null) {
            return result;
        }
        return obj;
    }

    // 附件上传
    public void saveUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到文件上传的参数");
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
            String id = IdUtil.getId();
            String baseInfoId = toGetParamVal(parameters.get("baseInfoId"));
            String fileType = toGetParamVal(parameters.get("fileType"));
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String filePathBase = WebAppUtil.getProperty("worldFitnessImproveFilePathBase");
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase");
                return;
            }
            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + baseInfoId;
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
            fileInfo.put("fileType", fileType);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("baseInfoId", baseInfoId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            worldFitnessImproveDao.insertApplyFile(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public List<JSONObject> queryFileList(JSONObject params) {
        // 查找得根据type去查
        List<JSONObject> files = worldFitnessImproveDao.queryApplyListFile(params);
        for (JSONObject oneFile : files) {
            if (oneFile.get("CREATE_TIME_") != null) {
                oneFile.put("CREATE_TIME_", DateUtil.formatDate((Date)oneFile.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return files;
    }

    public JsonResult statusChange(String applyId,String technical) {
        JsonResult result = new JsonResult(true, "操作成功！");
        JSONObject idJson = new JSONObject();
        idJson.put("id", applyId);
        if("Yes".equals(technical)){
            idJson.put("technical", "No");
        }else if ("No".equals(technical)) {
            idJson.put("technical", "Yes");
        }else {
            idJson.put("technical", "No");
        }
        // 基本信息
        worldFitnessImproveDao.statusChange(idJson);
        return result;
    }
}
