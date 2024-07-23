package com.redxun.portrait.core.manager;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.portrait.core.dao.PortraitDocumentDao;
import com.redxun.portrait.core.dao.PortraitFilesDao;
import com.redxun.portrait.core.dao.PortraitManageDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.sys.org.dao.OsUserDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * @author zhangzhen
 */
@Service
public class PortraitManageManager {
    private static final Logger logger = LoggerFactory.getLogger(PortraitManageManager.class);
    @Resource
    PortraitManageDao portraitManageDao;
    @Autowired
    OsUserDao osUserDao;
    @Resource
    PortraitFilesDao portraitFilesDao;
    @Resource
    private RdmZhglFileManager rdmZhglFileManager;
    @Resource
    CommonInfoManager commonInfoManager;
    @Resource
    PortraitDocumentDao portraitDocumentDao;
    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            String ids = "";
            //用户权限
          /*  JSONObject resultJson = commonInfoManager.hasPermission("HX-GLY");
            if("admin".equals(ContextUtil.getCurrentUser().getUserNo())||resultJson.getBoolean("FGLD")||resultJson.getBoolean("HX-GLY")) {
            }else if(resultJson.getBoolean("isLeader")){
                //部门领导看自己部门的
                ids = ContextUtil.getCurrentUser().getMainGroupId();
            }else{
                //普通员工看自己的
                params.put("userId",ContextUtil.getCurrentUserId());
                ids = ContextUtil.getCurrentUser().getMainGroupId();
            }
            if(StringUtil.isNotEmpty(ids)){
                String[] idArr = ids.split(",", -1);
                List<String> idList = Arrays.asList(idArr);
                params.put("ids", idList);
            }*/
            list = portraitManageDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, true);
//            if(StringUtil.isNotEmpty(ids)){
//                String[] idArr = ids.split(",", -1);
//                List<String> idList = Arrays.asList(idArr);
//                params.put("ids", idList);
//            }
//            if(!resultJson.getBoolean("FGLD")&&!resultJson.getBoolean("isLeader")&&!resultJson.getBoolean("HX-GLY")
//                    &&!"admin".equals(ContextUtil.getCurrentUser().getUserNo())){
//                params.put("userId",ContextUtil.getCurrentUserId());
//            }
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            totalList = portraitManageDao.query(params);
            convertDate(list);
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
    public JSONObject add(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
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
                if (mapKey.equals("rewardDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            String id = IdUtil.getId();
            objBody.put("id", id);
            resultJson.put("id",id);
            portraitManageDao.addObject(objBody);
        } catch (Exception e) {
            logger.error("Exception in add 添加管理内参！", e);
            return ResultUtil.result(false, "保存更新管理内参信息异常！", "");
        }
        return ResultUtil.result(true, "新增成功", resultJson);
    }
    public JSONObject update(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                if (mapKey.equals("rewardDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_",XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            portraitManageDao.updateObject(objBody);
        } catch (Exception e) {
            logger.error("Exception in update 更新管理内参异常", e);
            return ResultUtil.result(false, "更新管理内参异常！", "");
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
            String portraitFileUrl = SysPropertiesUtil.getGlobalProperty("portraitFileUrl");
            for (int i = 0; i < idArr.length; i++) {
                String mainId = idArr[i];
                List<JSONObject> files = portraitFilesDao.getFileListByMainId(mainId);
                for (JSONObject oneFile : files) {
                    rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"),
                            oneFile.getString("fileName"), oneFile.getString("mainId"), portraitFileUrl);
                    portraitFilesDao.delFileById(oneFile.getString("id"));
                }
                rdmZhglFileManager.deleteDirFromDisk(mainId, portraitFileUrl);
            }
            params.put("ids", idList);
            portraitManageDao.batchDelete(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除管理内参", e);
            return ResultUtil.result(false, "删除管理内参异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }
    public JSONObject getObjectById(String id){
        return portraitManageDao.getObjectById(id);
    }
    public static void convertDate(List<Map<String, Object>> list) {
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> oneApply : list) {
                for (String key : oneApply.keySet()) {
                    if (key.endsWith("_TIME_") || key.endsWith("_time") || key.endsWith("_date")) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                    if ("conformDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd HH:mm:ss"));
                        }
                    }
                    if ("rewardDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                }
            }
        }
    }
}
