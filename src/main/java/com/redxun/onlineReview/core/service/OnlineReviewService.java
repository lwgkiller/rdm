package com.redxun.onlineReview.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.onlineReview.core.dao.OnlineReviewDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
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
public class OnlineReviewService {
    private Logger logger = LogManager.getLogger(OnlineReviewService.class);
    @Autowired
    private OnlineReviewDao onlineReviewDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;


    public JsonPageResult<?> queryOnlineReviewBase(HttpServletRequest request, boolean doPage) {
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
        //addOnlineReviewRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> onlineReviewList = onlineReviewDao.queryOnlineReview(params);
        for (JSONObject onlineReview : onlineReviewList) {
            if (onlineReview.get("CREATE_TIME_") != null) {
                onlineReview.put("CREATE_TIME_", DateUtil.formatDate((Date) onlineReview.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(onlineReviewList, ContextUtil.getCurrentUserId());
        result.setData(onlineReviewList);
        result.setTotal(onlineReviewDao.countOnlineReview(params));
        return result;
    }

    public void createModel(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        onlineReviewDao.createModel(formData);
        if (StringUtils.isNotBlank(formData.getString("time"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("time"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("id"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    onlineReviewDao.createTime(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    onlineReviewDao.updateTime(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("id", oneObject.getString("id"));
                    onlineReviewDao.deleteTime(param);
                }
            }
        }
        //添加机型时间项目
        List<JSONObject> timeNameList = onlineReviewDao.queryTimeName();
        for (JSONObject onedata : timeNameList) {
            String timeId = onedata.getString("timeId");
            JSONObject timedata = new JSONObject();
            timedata.put("id", IdUtil.getId());
            timedata.put("timeId", timeId);
            timedata.put("belongId", formData.getString("id"));
            timedata.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            timedata.put("CREATE_TIME_", new Date());
            if ("tzxf".equalsIgnoreCase(timeId) || "scywj".equalsIgnoreCase(timeId)
                    || "jxtc".equalsIgnoreCase(timeId) || "zxd".equalsIgnoreCase(timeId)
                    || "czqjmx".equalsIgnoreCase(timeId)) {
                timedata.put("respId", formData.getString("jszgId"));
                timedata.put("respId_name", formData.getString("jszgName"));
            } else if ("fygzsj".equalsIgnoreCase(timeId) || "fygzcg".equalsIgnoreCase(timeId)
                    || "fyfhzy".equalsIgnoreCase(timeId) || "gybom".equalsIgnoreCase(timeId)) {
                timedata.put("respId", formData.getString("gyzgId"));
                timedata.put("respId_name", formData.getString("gyzgName"));
            } else if ("ddxf".equalsIgnoreCase(timeId) || "sx".equalsIgnoreCase(timeId)
                    || "rk".equalsIgnoreCase(timeId)) {
                timedata.put("respId", formData.getString("jhyId"));
                timedata.put("respId_name", formData.getString("jhyName"));
            } else {
                timedata.put("respId", onedata.getString("respId"));
                timedata.put("respId_name", onedata.getString("respId_name"));
            }
            onlineReviewDao.createTime(timedata);
        }

        if (StringUtils.isNotBlank(formData.getString("config"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("config"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("id"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    onlineReviewDao.createConfig(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    onlineReviewDao.updateConfig(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("id", oneObject.getString("id"));
                    onlineReviewDao.deleteConfig(param);
                }
            }
        }
    }

    public void updateModel(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        onlineReviewDao.updateModel(formData);
        if (StringUtils.isNotBlank(formData.getString("time"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("time"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("id"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    onlineReviewDao.createTime(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    onlineReviewDao.updateTime(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("id", oneObject.getString("id"));
                    onlineReviewDao.deleteTime(param);
                }
            }
        }
        if (StringUtils.isNotBlank(formData.getString("config"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("config"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("id"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    onlineReviewDao.createConfig(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    onlineReviewDao.updateConfig(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("id", oneObject.getString("id"));
                    onlineReviewDao.deleteConfig(param);
                }
            }
        }
    }

    public void createOnlineReview(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        onlineReviewDao.createOnlineReview(formData);
    }

    public void updateOnlineReview(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        onlineReviewDao.updateOnlineReview(formData);
    }

    public List<JSONObject> queryTime(String belongId) {
        Map<String, Object> param = new HashMap<>();
        param.put("belongId", belongId);
        List<JSONObject> timeList = onlineReviewDao.queryTime(param);
        return timeList;
    }

    public List<JSONObject> queryConfig(String belongId) {
        Map<String, Object> param = new HashMap<>();
        param.put("belongId", belongId);
        List<JSONObject> configList = onlineReviewDao.queryConfig(param);
        return configList;
    }

    public List<JSONObject> queryModel(String belongId) {
        Map<String, Object> param = new HashMap<>();
        param.put("belongId", belongId);
        List<JSONObject> modelList = onlineReviewDao.queryModel(param);
        return modelList;
    }

    public JSONObject getOnlineReviewBase(String id) {
        JSONObject detailObj = onlineReviewDao.queryOnlineReviewBaseById(id);
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.getDate("CREATE_TIME_") != null) {
            detailObj.put("CREATE_TIME_", DateUtil.formatDate(detailObj.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
        }
        return detailObj;
    }

    public JSONObject queryModelDetailById(String id) {
        JSONObject detailObj = onlineReviewDao.queryModelDetailById(id);
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.getDate("CREATE_TIME_") != null) {
            detailObj.put("CREATE_TIME_", DateUtil.formatDate(detailObj.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
        }
        return detailObj;
    }
    

    public JsonResult deleteOnlineReview(String id) {
        JsonResult result = new JsonResult(true, "操作成功！");
        Map<String, Object> param = new HashMap<>();

        List<JSONObject> modelList = queryModel(id);
        if (modelList != null && modelList.size() > 0) {
            StringBuilder idBulider = new StringBuilder();
            for (JSONObject oneModel : modelList) {
                idBulider.append(oneModel.getString("id")).append(",");
            }
            String idString = idBulider.deleteCharAt(idBulider.length() - 1).toString();
            String[] belongIds = idString.split(",");
            param.put("ids", Arrays.asList(belongIds));
            onlineReviewDao.deleteTime(param);
            onlineReviewDao.deleteConfig(param);
        }
        param.clear();
        param.put("id", id);
        onlineReviewDao.deleteOnlineReview(param);
        param.clear();
        String[] ids = id.split(",");
        param.put("ids", Arrays.asList(ids));
        onlineReviewDao.deleteModel(param);


        return result;
    }

    public JsonResult deleteModel(String id) {
        JsonResult result = new JsonResult(true, "操作成功！");
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        onlineReviewDao.deleteModel(param);
        param.clear();
        String[] ids = id.split(",");
        param.put("ids", Arrays.asList(ids));
        onlineReviewDao.deleteTime(param);
        onlineReviewDao.deleteConfig(param);
        return result;
    }

    public JSONObject textValid(JSONObject approveValid, String id, String stageName, String action) {
        JSONObject params = new JSONObject();
        params.put("belongId", id);
        List<JSONObject> modelList = onlineReviewDao.queryModel(params);
        for (JSONObject model : modelList) {
            params.clear();
            params.put("belongId", model.getString("id"));
            List<JSONObject> configDetailList = onlineReviewDao.queryConfig(params);
            List<JSONObject> timeDetailList = onlineReviewDao.queryTime(params);
            if ("start".equalsIgnoreCase(stageName)) {
                if (StringUtils.isBlank(model.getString("saleModel"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "销售型号！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("num"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "数量！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("needTime"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "需求交货期！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("jszgId"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "技术主管！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("gyzgId"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "工艺主管！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("jhyId"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "制造部计划员！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("transportType"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "运输形式！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("transportMode"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "运输方式！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("packageMode"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "包装方式！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("location"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "发运地点！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("planRequire"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "方案需求！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("specialRequire"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "特殊要求！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("allConfig"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "全部配置信息！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("package"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "随机备件箱规格！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("colorChange"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "是否需要改色！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("colorNum"))
                        && "是".equalsIgnoreCase(model.getString("colorChange"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "改色色号！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("colorPart"))
                        && "是".equalsIgnoreCase(model.getString("colorChange"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "改色部位！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("inventoryChange"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "是否使用库存车改制！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("changeNum"))
                        && "库存车改制".equalsIgnoreCase(model.getString("inventoryChange"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "改制车号！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("initialConfig"))
                        && "库存车改制".equalsIgnoreCase(model.getString("inventoryChange"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "原车号配置！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("pfRequire"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "排放要求！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("rzRequire"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "认证要求！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("ljLanguage"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "零件图册语言！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("cbLanguage"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "操保手册语言！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("zxdLanguage"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "装箱单语言！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("ybLanguage"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "仪表语言！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("bsLanguage"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "标识语言！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("nameplate"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "铭牌！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("gps"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "GPS加装！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("oilRequire"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "油品要求！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("jjRequire"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "机具要求！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("other"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "其它（如锁车程序等）！");
                    return approveValid;
                }
                if (configDetailList.size() == 0) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "配置信息！");
                    return approveValid;
                } else {
                    for (JSONObject config : configDetailList) {
                        if (StringUtils.isBlank(config.getString("configMsg"))) {
                            approveValid.put("success", false);
                            approveValid.put("message", "请填写" + model.getString("designModel") + "需求订单配置信息！");
                            return approveValid;
                        }
                    }
                }
            }
            if (("onejsfa".equalsIgnoreCase(stageName) ||
                    "twojsfa".equalsIgnoreCase(stageName) ||
                    "threejsfa".equalsIgnoreCase(stageName))
                    && ContextUtil.getCurrentUserId().equalsIgnoreCase(model.getString("jszgId"))) {
                if (StringUtils.isBlank(model.getString("designModel"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写技术主管" + model.getString("jszgName") + "所负责的设计型号！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("materielCode"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写技术主管" + model.getString("jszgName") + "所负责的整机物料号！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("jstz"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "是否下发技术通知！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("jsNum"))
                        && "是".equalsIgnoreCase(model.getString("jstz"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "技术下发通知编号！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("zxdChange"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "装箱单是否更改！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("manualStatus"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "请填写是否提交机型制作申请！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("periodFile"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "请填写是否上传长周期件明细！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("sourceFile"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "请填写是否编制/变更手册源文件！");
                    return approveValid;
                }
                for (JSONObject config : configDetailList) {
                    if (StringUtils.isBlank(config.getString("respId"))) {
                        if (StringUtils.isBlank(config.getString("risk"))) {
                            approveValid.put("success", false);
                            approveValid.put("message", "请填写" + model.getString("designModel")+config.getString("configMsg") + "风险点！");
                            return approveValid;
                        }
                        if (StringUtils.isBlank(config.getString("control"))) {
                            approveValid.put("success", false);
                            approveValid.put("message", "请填写" + model.getString("designModel")+config.getString("configMsg") + "控制措施！");
                            return approveValid;
                        }
                        if (StringUtils.isBlank(config.getString("finishTime"))) {
                            approveValid.put("success", false);
                            approveValid.put("message", "请填写" + model.getString("designModel")+config.getString("configMsg") + "完成时间！");
                            return approveValid;
                        }
                        if (StringUtils.isBlank(config.getString("changeCode"))) {
                            approveValid.put("success", false);
                            approveValid.put("message", "请填写" + model.getString("designModel")+config.getString("configMsg") + "变化点！");
                            return approveValid;
                        }
                        if (StringUtils.isBlank(config.getString("configType"))) {
                            approveValid.put("success", false);
                            approveValid.put("message", "请填写" + model.getString("designModel") +config.getString("configMsg")+ "类型！");
                            return approveValid;
                        }
                    }
                }
            }
            if ("oneddfx".equalsIgnoreCase(stageName) ||
                    "twoddfx".equalsIgnoreCase(stageName) ||
                    "threeddfx".equalsIgnoreCase(stageName)) {
                for (JSONObject config : configDetailList) {
                    if(ContextUtil.getCurrentUserId().equalsIgnoreCase(config.getString("respId"))){
                        if (StringUtils.isBlank(config.getString("risk"))) {
                            approveValid.put("success", false);
                            approveValid.put("message", "请填写" + model.getString("designModel")+config.getString("configMsg") + "风险点！");
                            return approveValid;
                        }
                        if (StringUtils.isBlank(config.getString("control"))) {
                            approveValid.put("success", false);
                            approveValid.put("message", "请填写" + model.getString("designModel")+config.getString("configMsg") + "控制措施！");
                            return approveValid;
                        }
                        if (StringUtils.isBlank(config.getString("finishTime"))) {
                            approveValid.put("success", false);
                            approveValid.put("message", "请填写" + model.getString("designModel")+config.getString("configMsg") + "完成时间！");
                            return approveValid;
                        }
                        if (StringUtils.isBlank(config.getString("changeCode"))) {
                            approveValid.put("success", false);
                            approveValid.put("message", "请填写" + model.getString("designModel")+config.getString("configMsg") + "变化点！");
                            return approveValid;
                        }
                        if (StringUtils.isBlank(config.getString("configType"))) {
                            approveValid.put("success", false);
                            approveValid.put("message", "请填写" + model.getString("designModel")+config.getString("configMsg") + "类型！");
                            return approveValid;
                        }
                    }
                }
            }
            if (("onecpzgtime".equalsIgnoreCase(stageName) ||
                    "twocpzgtime".equalsIgnoreCase(stageName) ||
                    "threecpzgtime".equalsIgnoreCase(stageName))
            && ContextUtil.getCurrentUserId().equalsIgnoreCase(model.getString("jszgId"))) {
                for (JSONObject time : timeDetailList) {
                    if ("tzxf".equalsIgnoreCase(time.getString("timeId"))
                            && StringUtils.isBlank(time.getString("finishTime"))
                            && "是".equalsIgnoreCase(model.getString("jstz"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "技术通知下发完成时间！");
                        return approveValid;
                    }
                    if ("scywj".equalsIgnoreCase(time.getString("timeId"))
                            && StringUtils.isBlank(time.getString("finishTime"))
                            && "是".equalsIgnoreCase(model.getString("sourceFile"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "手册源文件编制/变更完成时间！");
                        return approveValid;
                    }
                    if ("jxtc".equalsIgnoreCase(time.getString("timeId"))
                            && StringUtils.isBlank(time.getString("finishTime"))
                            && "是".equalsIgnoreCase(model.getString("manualStatus"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "机型图册完成时间！");
                        return approveValid;
                    }
                    if ("zxd".equalsIgnoreCase(time.getString("timeId"))
                            && StringUtils.isBlank(time.getString("finishTime"))
                            && "是".equalsIgnoreCase(model.getString("zxdChange"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "装箱单完成时间！");
                        return approveValid;
                    }
                    if ("czqjmx".equalsIgnoreCase(time.getString("timeId"))
                            && StringUtils.isBlank(time.getString("finishTime"))
                            && "是".equalsIgnoreCase(model.getString("periodFile"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "长周期件明细上传时间！");
                        return approveValid;
                    }
                }
            }
            if (("onefyfa".equalsIgnoreCase(stageName) ||
                    "twofyfa".equalsIgnoreCase(stageName) ||
                    "threefyfa".equalsIgnoreCase(stageName))
                    && ContextUtil.getCurrentUserId().equalsIgnoreCase(model.getString("gyzgId"))){

                if (StringUtils.isBlank(model.getString("gybom"))) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "是否有工艺BOM！");
                    return approveValid;
                }
                for (JSONObject time : timeDetailList) {
                    if ("gybom".equalsIgnoreCase(time.getString("timeId"))
                            && StringUtils.isBlank(time.getString("finishTime"))
                            && "否".equalsIgnoreCase(model.getString("gybom"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "工艺BOM完成时间！");
                        return approveValid;
                    }
                    if ("fygzsj".equalsIgnoreCase(time.getString("timeId"))
                            && StringUtils.isBlank(time.getString("finishTime"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "发运工装设计完成时间！");
                        return approveValid;
                    }
                    if ("fygzcg".equalsIgnoreCase(time.getString("timeId"))
                            && StringUtils.isBlank(time.getString("finishTime"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "发运工装采购技术通知单完成时间！");
                        return approveValid;
                    }
                    if ("fyfhzy".equalsIgnoreCase(time.getString("timeId"))
                            && StringUtils.isBlank(time.getString("finishTime"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "产品发运防护作业标准书完成时间！");
                        return approveValid;
                    }
                    if (StringUtils.isBlank(time.getString("respId"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "各事项时间责任人！");
                        return approveValid;
                    }
                }
            }
            if ("onecblj".equalsIgnoreCase(stageName) ||
                    "twocblj".equalsIgnoreCase(stageName) ||
                    "threecblj".equalsIgnoreCase(stageName)) {
                for (JSONObject time : timeDetailList) {
                    if (("ljtc".equalsIgnoreCase(time.getString("timeId"))
                            && StringUtils.isBlank(time.getString("finishTime")))
                            && ContextUtil.getCurrentUserId().equalsIgnoreCase(time.getString("respId"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "零件图册完成时间！");
                        return approveValid;
                    }
                    if (("cbsc".equalsIgnoreCase(time.getString("timeId"))
                            && StringUtils.isBlank(time.getString("finishTime")))
                            && ContextUtil.getCurrentUserId().equalsIgnoreCase(time.getString("respId"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "操保手册完成时间！");
                        return approveValid;
                    }
                    if (StringUtils.isBlank(time.getString("respId"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "各事项时间责任人！");
                        return approveValid;
                    }
                }
            }
            if ("oneydbk".equalsIgnoreCase(stageName) ||
                    "twoydbk".equalsIgnoreCase(stageName) ||
                    "threeydbk".equalsIgnoreCase(stageName)) {
                for (JSONObject time : timeDetailList) {
                    if (("bztspz".equalsIgnoreCase(time.getString("timeId"))
                            && StringUtils.isBlank(time.getString("finishTime")))
                            && ContextUtil.getCurrentUserId().equalsIgnoreCase(time.getString("respId"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "编制特殊配置检验表完成时间！");
                        return approveValid;
                    }
                    if (("kzyd".equalsIgnoreCase(time.getString("timeId"))
                            && StringUtils.isBlank(time.getString("finishTime")))
                            && ContextUtil.getCurrentUserId().equalsIgnoreCase(time.getString("respId"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "控制要点完成时间！");
                        return approveValid;
                    }
                    if (StringUtils.isBlank(time.getString("respId"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "各事项时间责任人！");
                        return approveValid;
                    }
                }
            }
            if ("onexlbjgy".equalsIgnoreCase(stageName) ||
                    "twoxlbjgy".equalsIgnoreCase(stageName) ||
                    "threexlbjgy".equalsIgnoreCase(stageName)) {
                for (JSONObject time : timeDetailList) {
                    if (("xzwxj".equalsIgnoreCase(time.getString("timeId"))
                            && StringUtils.isBlank(time.getString("finishTime")))
                            && ContextUtil.getCurrentUserId().equalsIgnoreCase(time.getString("respId"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "新增外协件的布局完成时间！");
                        return approveValid;
                    }
                    if (("qdgys".equalsIgnoreCase(time.getString("timeId"))
                            && StringUtils.isBlank(time.getString("finishTime")))
                            && ContextUtil.getCurrentUserId().equalsIgnoreCase(time.getString("respId"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "确定供应商完成时间！");
                        return approveValid;
                    }
                    if (StringUtils.isBlank(time.getString("respId"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "各事项时间责任人！");
                        return approveValid;
                    }
                }
            }
            if ("onecg".equalsIgnoreCase(stageName) ||
                    "twocg".equalsIgnoreCase(stageName) ||
                    "threecg".equalsIgnoreCase(stageName)) {
                for (JSONObject time : timeDetailList) {
                    if (("wgj".equalsIgnoreCase(time.getString("timeId"))
                            && StringUtils.isBlank(time.getString("finishTime")))
                            && ContextUtil.getCurrentUserId().equalsIgnoreCase(time.getString("respId"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "外购件完成时间！");
                        return approveValid;
                    }
                    if (("wxj".equalsIgnoreCase(time.getString("timeId"))
                            && StringUtils.isBlank(time.getString("finishTime")))
                            && ContextUtil.getCurrentUserId().equalsIgnoreCase(time.getString("respId"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "外协件完成时间！");
                        return approveValid;
                    }
                    if (("jkj".equalsIgnoreCase(time.getString("timeId"))
                            && StringUtils.isBlank(time.getString("finishTime")))
                            && ContextUtil.getCurrentUserId().equalsIgnoreCase(time.getString("respId"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "进口件完成时间！");
                        return approveValid;
                    }
                    if (StringUtils.isBlank(time.getString("respId"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "各事项时间责任人！");
                        return approveValid;
                    }
                }
            }
            if ("oneddxf".equalsIgnoreCase(stageName) ||
                    "twoddxf".equalsIgnoreCase(stageName) ||
                    "threeddxf".equalsIgnoreCase(stageName)) {
                for (JSONObject time : timeDetailList) {
                    if (("ddxf".equalsIgnoreCase(time.getString("timeId"))
                            && StringUtils.isBlank(time.getString("finishTime")))
                            && ContextUtil.getCurrentUserId().equalsIgnoreCase(time.getString("respId"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "订单下发完成时间！");
                        return approveValid;
                    }
                    if ((StringUtils.isBlank(time.getString("respId")))
                            && ContextUtil.getCurrentUserId().equalsIgnoreCase(time.getString("respId"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "各事项时间责任人！");
                        return approveValid;
                    }
                }
            }
            if ("onesxrk".equalsIgnoreCase(stageName) ||
                    "twosxrk".equalsIgnoreCase(stageName) ||
                    "threesxrk".equalsIgnoreCase(stageName)) {
                for (JSONObject time : timeDetailList) {
                    if (("sx".equalsIgnoreCase(time.getString("timeId"))
                            && StringUtils.isBlank(time.getString("finishTime")))
                            && ContextUtil.getCurrentUserId().equalsIgnoreCase(time.getString("respId"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "上线完成时间！");
                        return approveValid;
                    }
                    if (("rk".equalsIgnoreCase(time.getString("timeId"))
                            && StringUtils.isBlank(time.getString("finishTime")))
                            && ContextUtil.getCurrentUserId().equalsIgnoreCase(time.getString("respId"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "入库完成时间！");
                        return approveValid;
                    }
                    if (StringUtils.isBlank(time.getString("respId"))) {
                        approveValid.put("success", false);
                        approveValid.put("message", "请填写" + model.getString("designModel") + "各事项时间责任人！");
                        return approveValid;
                    }
                }
            }
            if ("fgld".equalsIgnoreCase(stageName)) {
                // 返回当前登录人角色信息
                Map<String, Object> param1 = new HashMap<>();
                param1.put("userId", ContextUtil.getCurrentUserId());
                List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(param1);
                JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
                JSONArray userRoleKeys = new JSONArray();
                for (int index = 0; index < userRolesJsonArray.size(); index++) {
                    JSONObject oneRole = (JSONObject)userRolesJsonArray.get(index);
                    userRoleKeys.add(oneRole.getString("KEY_"));
                }
                if (StringUtils.isBlank(model.getString("gzOption"))
                        && "不满足".equalsIgnoreCase(model.getString("timeRisk"))
                        && userRoleKeys.contains("FGLD")) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "研发领导意见！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("gzReason"))
                        && "不满足".equalsIgnoreCase(model.getString("timeRisk"))
                        && "不同意".equalsIgnoreCase(model.getString("gzOption"))
                        && userRoleKeys.contains("FGLD")) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "不同意原因！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("yzOption"))
                        && "不满足".equalsIgnoreCase(model.getString("timeRisk"))
                        && userRoleKeys.contains("GYFGLD")) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "工艺领导意见！");
                    return approveValid;
                }
                if (StringUtils.isBlank(model.getString("yzReason"))
                        && "不满足".equalsIgnoreCase(model.getString("timeRisk"))
                        && "不同意".equalsIgnoreCase(model.getString("yzOption"))
                        && userRoleKeys.contains("GYFGLD")) {
                    approveValid.put("success", false);
                    approveValid.put("message", "请填写" + model.getString("designModel") + "不同意原因！");
                    return approveValid;
                }
            }
        }
        return approveValid;
    }

    public void saveOnlineReviewUploadFiles(HttpServletRequest request) {
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
        String filePathBase = SysPropertiesUtil.getGlobalProperty("onlineReviewFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find onlineReviewFilePathBase");
            return;
        }
        try {
            String belongId = toGetParamVal(parameters.get("id"));
            String fileId = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileType = toGetParamVal(parameters.get("fileType"));
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
            fileInfo.put("fileType", fileType);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            onlineReviewDao.addFileInfos(fileInfo);
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

    public List<JSONObject> getOnlineReviewFileList(List<String> idList, String fileType) {
        List<JSONObject> onlineReviewFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("ids", idList);
        param.put("fileType", fileType);
        onlineReviewFileList = onlineReviewDao.queryOnlineReviewFileList(param);
        return onlineReviewFileList;
    }

    public void deleteOneOnlineReviewFile(String fileId, String fileName, String id) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, id, SysPropertiesUtil.getGlobalProperty("onlineReviewFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("fileId", fileId);
        param.put("belongId", id);
        onlineReviewDao.deleteOnlineReviewFile(param);
    }
}
