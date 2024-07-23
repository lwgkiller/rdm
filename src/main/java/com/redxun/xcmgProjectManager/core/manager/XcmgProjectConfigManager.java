
package com.redxun.xcmgProjectManager.core.manager;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectConfigDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * <pre>
 * 描述：project_baseInfo 处理接口
 * 作者:x
 * 日期:2019-08-22 08:59:49
 * 版权：xx
 * </pre>
 */
@Service
public class XcmgProjectConfigManager {
    private Logger logger = LoggerFactory.getLogger(XcmgProjectConfigManager.class);
    @Resource
    private XcmgProjectConfigDao xcmgProjectConfigDao;

    public List<Map<String, Object>> levelDivideList() {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            List<Map<String, Object>> result = xcmgProjectConfigDao.levelDivideList(params);
            if (result != null && !result.isEmpty()) {
                for (Map<String, Object> oneInfo : result) {
                    if (oneInfo.get("CREATE_TIME_") != null) {
                        oneInfo.put("CREATE_TIME_",
                            DateUtil.formatDate((Date)oneInfo.get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneInfo.get("UPDATE_TIME_") != null) {
                        oneInfo.put("UPDATE_TIME_",
                            DateUtil.formatDate((Date)oneInfo.get("UPDATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                }
            }
            return result;
        } catch (Exception e) {
            logger.error("Exception in levelDivideList", e);
            return null;
        }
    }

    public void saveLevelDivide(JsonResult result, String formDataStr) {
        try {
            JSONArray formDataJson = JSONObject.parseArray(formDataStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return;
            }
            for (int i = 0; i < formDataJson.size(); i++) {
                JSONObject oneObject = formDataJson.getJSONObject(i);
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                xcmgProjectConfigDao.updateLevelDivide(oneObject);
            }

        } catch (Exception e) {
            logger.error("Exception in save LevelDivide", e);
            result.setSuccess(false);
            result.setMessage("Exception in save LevelDivide");
            return;
        }
    }

    public List<Map<String, Object>> standardScoreList(String categoryId) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            params.put("categoryId", categoryId);
            List<Map<String, Object>> result = xcmgProjectConfigDao.standardScoreList(params);
            if (result != null && !result.isEmpty()) {
                for (Map<String, Object> oneInfo : result) {
                    if (oneInfo.get("CREATE_TIME_") != null) {
                        oneInfo.put("CREATE_TIME_",
                            DateUtil.formatDate((Date)oneInfo.get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneInfo.get("UPDATE_TIME_") != null) {
                        oneInfo.put("UPDATE_TIME_",
                            DateUtil.formatDate((Date)oneInfo.get("UPDATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                }
            }
            return result;
        } catch (Exception e) {
            logger.error("Exception in standardScoreList", e);
            return null;
        }
    }

    public void saveStandardScore(JsonResult result, String formDataStr) {
        try {
            JSONArray formDataJson = JSONObject.parseArray(formDataStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return;
            }
            for (int i = 0; i < formDataJson.size(); i++) {
                JSONObject oneObject = formDataJson.getJSONObject(i);
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                xcmgProjectConfigDao.updateStandardScore(oneObject);
            }

        } catch (Exception e) {
            logger.error("Exception in save StandardScore");
            result.setSuccess(false);
            result.setMessage("Exception in save StandardScore");
            return;
        }
    }

    public List<Map<String, Object>> deliveryList(String categoryId) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            params.put("categoryId", categoryId);
            List<Map<String, Object>> result = xcmgProjectConfigDao.deliveryList(params);
            if (result != null && !result.isEmpty()) {
                for (Map<String, Object> oneInfo : result) {
                    if (oneInfo.get("CREATE_TIME_") != null) {
                        oneInfo.put("CREATE_TIME_",
                            DateUtil.formatDate((Date)oneInfo.get("CREATE_TIME_"), "yyyy-MM-dd"));
                    }
                    if (oneInfo.get("UPDATE_TIME_") != null) {
                        oneInfo.put("UPDATE_TIME_",
                            DateUtil.formatDate((Date)oneInfo.get("UPDATE_TIME_"), "yyyy-MM-dd"));
                    }
                }
            }
            return result;
        } catch (Exception e) {
            logger.error("Exception in deliveryList", e);
            return null;
        }
    }

    public void saveDelivery(JsonResult result, String formDataStr) {
        try {
            JSONObject deliveryJson = JSONObject.parseObject(formDataStr);
            if (deliveryJson == null || deliveryJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return;
            }
            if (StringUtils.isBlank(deliveryJson.getString("deliveryId"))) {
                // 新增
                deliveryJson.put("deliveryId", IdUtil.getId());
                deliveryJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                deliveryJson.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                deliveryJson.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                xcmgProjectConfigDao.saveDelivery(deliveryJson);
            } else {
                // 更新
                deliveryJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                deliveryJson.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                xcmgProjectConfigDao.updateDelivery(deliveryJson);
            }

        } catch (Exception e) {
            logger.error("Exception in saveDelivery");
            result.setSuccess(false);
            result.setMessage("Exception in saveDelivery");
            return;
        }
    }

    public List<Map<String, Object>> ratingScoreList() {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            List<Map<String, Object>> result = xcmgProjectConfigDao.ratingScoreList(params);
            if (result != null && !result.isEmpty()) {
                for (Map<String, Object> oneInfo : result) {
                    if (oneInfo.get("CREATE_TIME_") != null) {
                        oneInfo.put("CREATE_TIME_",
                            DateUtil.formatDate((Date)oneInfo.get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneInfo.get("UPDATE_TIME_") != null) {
                        oneInfo.put("UPDATE_TIME_",
                            DateUtil.formatDate((Date)oneInfo.get("UPDATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                }
            }
            return result;
        } catch (Exception e) {
            logger.error("Exception in ratingScoreList", e);
            return null;
        }
    }

    public List<Map<String, Object>> achievementTypeList() {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            List<Map<String, Object>> result = xcmgProjectConfigDao.achievementTypeList(params);
            if (result != null && !result.isEmpty()) {
                for (Map<String, Object> oneInfo : result) {
                    if (oneInfo.get("CREATE_TIME_") != null) {
                        oneInfo.put("CREATE_TIME_",
                            DateUtil.formatDate((Date)oneInfo.get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneInfo.get("UPDATE_TIME_") != null) {
                        oneInfo.put("UPDATE_TIME_",
                            DateUtil.formatDate((Date)oneInfo.get("UPDATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                }
            }
            return result;
        } catch (Exception e) {
            logger.error("Exception in achievementTypeList", e);
            return null;
        }
    }

    public void saveRatingScore(JsonResult result, String changeGridDataStr) {
        try {
            JSONArray changeGridDataJson = JSONObject.parseArray(changeGridDataStr);
            if (changeGridDataJson == null || changeGridDataJson.isEmpty()) {
                logger.warn("gridData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return;
            }
            for (int i = 0; i < changeGridDataJson.size(); i++) {
                JSONObject oneObject = changeGridDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String ratingId = oneObject.getString("ratingId");
                if ("added".equals(state) || StringUtils.isBlank(ratingId)) {
                    // 新增
                    oneObject.put("ratingId", IdUtil.getId());
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    oneObject.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                    xcmgProjectConfigDao.saveRatingScore(oneObject);
                } else if ("modified".equals(state)) {
                    // 修改
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    xcmgProjectConfigDao.updateRatingScore(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    xcmgProjectConfigDao.delRatingScore(ratingId);
                }
            }

        } catch (Exception e) {
            logger.error("Exception in save RatingScore");
            result.setSuccess(false);
            result.setMessage("Exception in save RatingScore");
            return;
        }
    }

    public void saveAchievement(JsonResult result, String changeGridDataStr) {
        try {
            JSONArray changeGridDataJson = JSONObject.parseArray(changeGridDataStr);
            if (changeGridDataJson == null || changeGridDataJson.isEmpty()) {
                logger.warn("gridData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return;
            }
            for (int i = 0; i < changeGridDataJson.size(); i++) {
                JSONObject oneObject = changeGridDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    oneObject.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                    xcmgProjectConfigDao.saveAchievementType(oneObject);
                } else if ("modified".equals(state)) {
                    // 修改
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    xcmgProjectConfigDao.updateAchievementType(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    xcmgProjectConfigDao.delAchievementType(id);
                }
            }

        } catch (Exception e) {
            logger.error("Exception in save saveAchievement");
            result.setSuccess(false);
            result.setMessage("Exception in save saveAchievement");
            return;
        }
    }

    public List<Map<String, Object>> memRoleRatioList() {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            List<Map<String, Object>> result = xcmgProjectConfigDao.memRoleRatioList(params);
            if (result != null && !result.isEmpty()) {
                for (Map<String, Object> oneInfo : result) {
                    if (oneInfo.get("CREATE_TIME_") != null) {
                        oneInfo.put("CREATE_TIME_",
                            DateUtil.formatDate((Date)oneInfo.get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneInfo.get("UPDATE_TIME_") != null) {
                        oneInfo.put("UPDATE_TIME_",
                            DateUtil.formatDate((Date)oneInfo.get("UPDATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                }
            }
            return result;
        } catch (Exception e) {
            logger.error("Exception in memRoleRatioList", e);
            return null;
        }
    }

    public void updatememRoleRatio(JsonResult result, String formDataStr) {
        try {
            JSONArray formDataJson = JSONObject.parseArray(formDataStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return;
            }
            for (int i = 0; i < formDataJson.size(); i++) {
                JSONObject oneObject = formDataJson.getJSONObject(i);
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                xcmgProjectConfigDao.updatememRoleRatio(oneObject);
            }

        } catch (Exception e) {
            logger.error("Exception in updatememRoleRatio");
            result.setSuccess(false);
            result.setMessage("Exception in updatememRoleRatio");
            return;
        }
    }

    public List<Map<String, Object>> memRoleRankList() {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            List<Map<String, Object>> result = xcmgProjectConfigDao.memRoleRankList(params);
            if (result != null && !result.isEmpty()) {
                for (Map<String, Object> oneInfo : result) {
                    if (oneInfo.get("CREATE_TIME_") != null) {
                        oneInfo.put("CREATE_TIME_",
                            DateUtil.formatDate((Date)oneInfo.get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneInfo.get("UPDATE_TIME_") != null) {
                        oneInfo.put("UPDATE_TIME_",
                            DateUtil.formatDate((Date)oneInfo.get("UPDATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                }
            }
            return result;
        } catch (Exception e) {
            logger.error("Exception in memRoleRankList", e);
            return null;
        }
    }

    public void updatememRoleRank(JsonResult result, String formDataStr) {
        try {
            JSONArray formDataJson = JSONObject.parseArray(formDataStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return;
            }
            for (int i = 0; i < formDataJson.size(); i++) {
                JSONObject oneObject = formDataJson.getJSONObject(i);
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                xcmgProjectConfigDao.updatememRoleRank(oneObject);
            }

        } catch (Exception e) {
            logger.error("Exception in updatememRoleRank");
            result.setSuccess(false);
            result.setMessage("Exception in updatememRoleRank");
            return;
        }
    }

    public List<Map<String, Object>> getBpmSolutions(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Map<String, Object>> resultList = xcmgProjectConfigDao.getBpmSolutions();
            if (resultList != null) {
                Map map = new HashMap(16);
                map.put("id", "pdmApprove");
                map.put("text", "PDM文件目录说明");
                resultList.add(map);
            }
            return resultList;
        } catch (Exception e) {
            logger.error("getBpmSolutions");
            return null;
        }

    }

}
