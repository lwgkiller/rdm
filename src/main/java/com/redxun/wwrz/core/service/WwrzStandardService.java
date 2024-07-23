package com.redxun.wwrz.core.service;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.wwrz.core.dao.WwrzFilesDao;
import com.redxun.wwrz.core.dao.WwrzStandardDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * @author zhangzhen
 */
@Service
public class WwrzStandardService {
    private static final Logger logger = LoggerFactory.getLogger(WwrzStandardService.class);
    @Resource
    private WwrzStandardDao wwrzStandardDao;
    @Resource
    private WwrzFilesDao wwrzFilesDao;
    @Resource
    private WwrzFilesService wwrzFilesService;
    @Resource
    private RdmZhglFileManager rdmZhglFileManager;

    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            list = wwrzStandardDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            totalList = wwrzStandardDao.query(params);
            convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
        }
        return result;
    }

    public JSONObject add(JSONObject formDataJson) {
        JSONObject resultJson = new JSONObject();
        try {
            String id = IdUtil.getId();
            formDataJson.put("id", id);
            formDataJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            formDataJson.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            formDataJson.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            resultJson.put("id", id);
            wwrzStandardDao.addObject(formDataJson);
            if (StringUtils.isNotBlank(formDataJson.getString("SUB_sector"))) {
                JSONArray sectorDataJson = JSONObject.parseArray(formDataJson.getString("SUB_sector"));
                for (int i = 0; i < sectorDataJson.size(); i++) {
                    JSONObject oneObject = sectorDataJson.getJSONObject(i);
                    String state = oneObject.getString("_state");
                    String productId = oneObject.getString("id");
                    if ("added".equals(state) || StringUtils.isBlank(productId)) {
                        // 新增
                        oneObject.put("id", IdUtil.getId());
                        oneObject.put("mainId", id);
                        oneObject.put("reason", oneObject.getString("reason"));
                        oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                        oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                        wwrzStandardDao.addStandardDetail(oneObject);
                    } else if ("modified".equals(state)) {
                        oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                        wwrzStandardDao.updateStandardDetail(oneObject);
                    } else if ("removed".equals(state)) {
                        // 删除
                        wwrzStandardDao.delStandardDetail(oneObject.getString("id"));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Exception in add 添加异常！", e);
            return ResultUtil.result(false, "添加异常！", "");
        }
        return ResultUtil.result(true, "新增成功", resultJson);
    }

    public JSONObject update(JSONObject formDataJson) {
        JSONObject resultJson = new JSONObject();
        try {
            resultJson.put("id",formDataJson.getString("id"));
            formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            formDataJson.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            wwrzStandardDao.updateObject(formDataJson);
            if (StringUtils.isNotBlank(formDataJson.getString("sectorData"))) {
                JSONArray sectorDataJson = JSONObject.parseArray(formDataJson.getString("sectorData"));
                for (int i = 0; i < sectorDataJson.size(); i++) {
                    JSONObject oneObject = sectorDataJson.getJSONObject(i);
                    String state = oneObject.getString("_state");
                    String productId = oneObject.getString("id");
                    if ("added".equals(state) || StringUtils.isBlank(productId)) {
                        // 新增
                        oneObject.put("id", IdUtil.getId());
                        oneObject.put("mainId", formDataJson.getString("id"));
                        oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                        oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                        wwrzStandardDao.addStandardDetail(oneObject);
                    } else if ("modified".equals(state)) {
                        oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                        wwrzStandardDao.updateStandardDetail(oneObject);
                    } else if ("removed".equals(state)) {
                        // 删除
                        wwrzStandardDao.delStandardDetail(oneObject.getString("id"));
                        delSectorFiles(oneObject.getString("id"));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Exception in update 更新异常", e);
            return ResultUtil.result(false, "更新异常！", "");
        }
        return ResultUtil.result(true, "更新成功", resultJson);
    }
    /**
     * 删除章节附件信息
     * */
    public void delSectorFiles(String sectorId){
        try{
            List<JSONObject> list = wwrzFilesDao.getFileListByMainId(sectorId);
            for (JSONObject file : list) {
                wwrzFilesService.deleteOneFile(file.getString("id"), file.getString("fileName"),
                        file.getString("mainId"));
            }
            rdmZhglFileManager.deleteDirFromDisk(sectorId, SysPropertiesUtil.getGlobalProperty("wwrzFileUrl"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public JSONObject remove(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String ids = request.getParameter("ids");
            String[] idArr = ids.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            params.put("ids", idList);
            //删除附件信息
            delFile(idList);
            wwrzStandardDao.batchDelete(params);
            wwrzStandardDao.batchDeleteDetail(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除信息异常", e);
            return ResultUtil.result(false, "删除信息异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }
    public void delFile(List<String> idList){
        try {
            for(String standardId:idList){
                List<JSONObject> sectorList = wwrzStandardDao.getStandardDetailList(standardId);
                for(JSONObject sectorObj:sectorList){
                    String sectorId = sectorObj.getString("id");
                    List<JSONObject> list = wwrzFilesDao.getFileListByMainId(sectorId);
                    for (JSONObject file : list) {
                        wwrzFilesService.deleteOneFile(file.getString("id"), file.getString("fileName"),
                                file.getString("mainId"));
                    }
                    rdmZhglFileManager.deleteDirFromDisk(sectorId, SysPropertiesUtil.getGlobalProperty("wwrzFileUrl"));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public List<JSONObject> getStandardList(){
        return wwrzStandardDao.getStandardList();
    }

    public JSONObject getObjectById(String id) {
        JSONObject jsonObject = wwrzStandardDao.getObjectById(id);
        return jsonObject;
    }
    public static void convertDate(List<Map<String, Object>> list) {
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> oneApply : list) {
                for (String key : oneApply.keySet()) {
                    if (key.endsWith("_TIME_") || key.endsWith("_time") || key.endsWith("_date")) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd  HH:mm:ss"));
                        }
                    }
                    if ("startDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                    if ("endDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                }
            }
        }
    }
}
