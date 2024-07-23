package com.redxun.wwrz.core.service;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.redxun.saweb.util.WebAppUtil;
import com.redxun.wwrz.core.dao.WwrzStandardMngDao;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.wwrz.core.dao.WwrzCompanyDao;

/**
 * @author zhangzhen
 */
@Service
public class WwrzStandardMngService {
    private static final Logger logger = LoggerFactory.getLogger(WwrzStandardMngService.class);
    @Autowired
    WwrzStandardMngDao wwrzStandardMngDao;
    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            String standardType = request.getParameter("standardType");
            params = CommonFuns.getSearchParam(params, request, true);
            params.put("standardType",standardType);
            list = wwrzStandardMngDao.query(params);
            if (list != null && list.size()>0) {
                Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    Map<String, Object> oneInfo = (Map<String, Object>)iterator.next();
                    // 查看对应的标准全文是否存在
                    boolean existFile = checkStandardFileExist(oneInfo.get("id").toString(), false);
                    oneInfo.put("existFile", existFile);
                }
            }
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            params.put("standardType",standardType);
            totalList = wwrzStandardMngDao.query(params);
            convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            result.setSuccess(false);
            result.setMessage("查询异常");
        }
        return result;
    }
    public JSONObject add(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        String id = IdUtil.getId();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            if (parameters == null || parameters.isEmpty()) {
                logger.error("表单内容为空！");
                return ResultUtil.result(false, "操作失败，表单内容为空！", "");
            }
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                objBody.put(mapKey, mapValue);
            }
            JSONObject standardObj = wwrzStandardMngDao.getStandardByInfo(objBody);
            if(standardObj!=null){
                return ResultUtil.result(false, "此标准已经添加，无需重复添加！", "");
            }
            objBody.put("id", id);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", new Date());
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", new Date());
            wwrzStandardMngDao.addObject(objBody);
        } catch (Exception e) {
            logger.error("Exception in add 保存标准信息", e);
            return ResultUtil.result(false, "保存标准信息异常！", "");
        }
        resultJson.put("id", id);
        return ResultUtil.result(true, "保存成功", resultJson);
    }
    public JSONObject update(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                if (mapKey.equals("paymentDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", new Date());
            wwrzStandardMngDao.updateObject(objBody);
        } catch (Exception e) {
            logger.error("Exception in update 更新标准信息", e);
            return ResultUtil.result(false, "更新标准信息异常！", "");
        }
        return ResultUtil.result(true, "更新成功", resultJson);
    }
    public JSONObject remove(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String ids = request.getParameter("ids");
            String[] idArr = ids.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            params.put("ids", idList);
            wwrzStandardMngDao.batchDelete(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除标准信息", e);
            return ResultUtil.result(false, "删除标准信息！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }
    private boolean checkStandardFileExist(String standardId, boolean isPublicStandard) {
        File file = null;
        if (!isPublicStandard) {
            file = findStandardFile(standardId, ConstantUtil.DOWNLOAD);
        } else {
            file = findPublicStandardFile(standardId);
        }
        if (file == null || !file.exists()) {
            return false;
        }
        return true;
    }
    private File findStandardFile(String standardId, String action) {
        if (StringUtils.isBlank(standardId)) {
            logger.warn("standardId is blank");
            return null;
        }
        String filePathBase = ConstantUtil.DOWNLOAD.equalsIgnoreCase(action)
                ? WebAppUtil.getProperty("standardFilePathBase") : WebAppUtil.getProperty("standardFilePathBase_preview");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find filePathBase");
            return null;
        }
        String fileFullPath = filePathBase + File.separator + standardId + ".pdf";
        File file = new File(fileFullPath);
        if (!file.exists()) {
            return null;
        }
        return file;
    }
    private File findPublicStandardFile(String standardId) {
        if (StringUtils.isBlank(standardId)) {
            logger.warn("standardId is blank");
            return null;
        }
        String filePathBase = WebAppUtil.getProperty("publicStandardFilePath");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find filePathBase");
            return null;
        }
        String fileFullPath = filePathBase + File.separator + standardId + ".pdf";
        File file = new File(fileFullPath);
        if (!file.exists()) {
            return null;
        }
        return file;
    }



    /**
     * 时间格式转换
     */
    public static void convertDate(List<Map<String, Object>> list) {
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> oneApply : list) {
                for (String key : oneApply.keySet()) {
                    if (key.endsWith("_TIME_") || key.endsWith("_time") || key.endsWith("_date")) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                    if ("publishTime".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                }
            }
        }
    }
}
