package com.redxun.world.core.service;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.redxun.core.json.JsonPageResult;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.world.core.dao.WorldResearchDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;

/**
 * @author zhangzhen
 */
@Service
public class WorldResearchService {
    private static final Logger logger = LoggerFactory.getLogger(WorldResearchService.class);
    @Resource
    WorldResearchDao worldResearchDao;
    @Resource
    CommonInfoManager commonInfoManager;
    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            String menuType = request.getParameter("menuType");
            String fileType = request.getParameter("fileType");
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            params.put("menuType",menuType);
            params.put("fileType",fileType);
            list = worldResearchDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            params.put("menuType",menuType);
            params.put("fileType",fileType);
            totalList = worldResearchDao.query(params);
            CommonFuns.convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
        }
        return result;
    }

    /**
     * 新增保存文件到磁盘和数据库
     *
     * @param request
     */
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
            String fileName = toGetParamVal(parameters.get("fileName"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String worldResearchDir = SysPropertiesUtil.getGlobalProperty("worldResearch");
            String filePathBase = worldResearchDir;
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase or filePathBase_preview");
                return;
            }
            // 向下载目录中写入文件
            String menuType = toGetParamVal(parameters.get("menuType"));
            String filePath = filePathBase + File.separator+ menuType;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);

            // 写入数据库
            Map<String, Object> fileInfo = new HashMap<>(16);
            fileInfo.put("id", id);
            fileInfo.put("fileName", fileName);
            fileInfo.put("menuType", toGetParamVal(parameters.get("menuType")));
            fileInfo.put("fileType", toGetParamVal(parameters.get("fileType")));
            fileInfo.put("fileDesc", toGetParamVal(parameters.get("fileDesc")));
            fileInfo.put("fileSize", toGetParamVal(parameters.get("fileSize")));
            fileInfo.put("fileCategory", toGetParamVal(parameters.get("fileCategory")));
            fileInfo.put("deptId",ContextUtil.getCurrentUser().getMainGroupId());
            fileInfo.put("CREATE_TIME_", new Date());
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            fileInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("UPDATE_TIME_", new Date());
            worldResearchDao.addObject(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
        return;
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    public void deleteFileOnDisk(String menuType, String fileId, String suffix) {
        String fullFileName = fileId + "." + suffix;
        StringBuilder fileBasePath = new StringBuilder(SysPropertiesUtil.getGlobalProperty("worldResearch"));
        String fullPath =
            fileBasePath.append(File.separator).append(menuType).append(File.separator).append(fullFileName).toString();
        File file = new File(fullPath);
        if (file.exists()) {
            file.delete();
        }
    }
    public JSONObject submit(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String ids = request.getParameter("ids");
            String[] idArr = ids.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            params.put("ids", idList);
            worldResearchDao.submit(params);
        } catch (Exception e) {
            logger.error("Exception in submit 提交", e);
            return ResultUtil.result(false, "提交异常！", "");
        }
        return ResultUtil.result(true, "提交成功", resultJson);
    }
    public JSONObject batchAudit(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String ids = request.getParameter("ids");
            String applyStatus = request.getParameter("applyStatus");
            String[] idArr = ids.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            params.put("ids", idList);
            params.put("applyStatus", applyStatus);
            worldResearchDao.batchAudit(params);
        } catch (Exception e) {
            logger.error("Exception in submit 提交", e);
            return ResultUtil.result(false, "提交异常！", "");
        }
        return ResultUtil.result(true, "提交成功", resultJson);
    }
    public JSONObject audit(JSONObject jsonObject) {
        JSONObject resultJson = new JSONObject();
        try {
            jsonObject.put("auditUserId",ContextUtil.getCurrentUserId());
            jsonObject.put("auditDate",new Date());
            worldResearchDao.audit(jsonObject);
        } catch (Exception e) {
            logger.error("Exception in audit 审批", e);
            return ResultUtil.result(false, "审批异常！", "");
        }
        return ResultUtil.result(true, "审批成功", resultJson);
    }
}
