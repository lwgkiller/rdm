package com.redxun.drbfm.core.service;

import static com.redxun.rdmCommon.core.util.RdmCommonUtil.toGetParamVal;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.camel.util.FileUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.BpmInst;
import com.redxun.bpm.core.entity.BpmSolution;
import com.redxun.bpm.core.entity.ProcessMessage;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmSolutionManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExceptionUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.drbfm.core.dao.DrbfmSingleDao;
import com.redxun.drbfm.core.dao.DrbfmTestTaskDao;
import com.redxun.drbfm.core.dao.DrbfmTotalDao;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.util.CommonExcelUtils;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Service
public class DrbfmSingleService {
    private Logger logger = LoggerFactory.getLogger(DrbfmSingleService.class);
    @Autowired
    private DrbfmSingleDao drbfmSingleDao;
    @Autowired
    private DrbfmTotalDao drbfmTotalDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private DrbfmTestTaskDao drbfmTestTaskDao;
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private DrbfmTestTaskService drbfmTestTaskService;
    @Autowired
    private UserService userService;
    @Resource
    private BpmSolutionManager bpmSolutionManager;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    public JsonPageResult<?> getSingleList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "drbfm_single_baseInfo.CREATE_TIME_", "desc");
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
        String structIds = RequestUtil.getString(request, "structIds", "");
        if (StringUtils.isNotBlank(structIds)) {
            params.put("structIds", Arrays.asList(structIds));
        }
        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }

        // TODO 增加角色过滤的条件（需要自己办理的目前已包含在下面的条件中）
        List<Map<String, Object>> singleList = drbfmSingleDao.querySingleList(params);
        for (Map<String, Object> oneData : singleList) {
            if (oneData.get("CREATE_TIME_") != null) {
                oneData.put("CREATE_TIME_", DateUtil.formatDate((Date)oneData.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(singleList);
        result.setData(singleList);
        int countJsmmDataList = drbfmSingleDao.countSingleList(params);
        result.setTotal(countJsmmDataList);
        return result;
    }

    public void insertSingleBase(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        drbfmSingleDao.createSingleBase(formData);
    }

    public void updateSingleBase(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        drbfmSingleDao.updateSingleBase(formData);
    }

    public JSONObject getSingleDetail(String singleId) {
        JSONObject detailObj = drbfmSingleDao.querySingleBaseById(singleId);
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.getDate("CREATE_TIME_") != null) {
            detailObj.put("CREATE_TIME_", DateUtil.formatDate(detailObj.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
        }
        return detailObj;
    }

    // 单一部件项目部门需求
    public List<JSONObject> getSingleDeptDemandList(String belongSingleId, String filterParams) {
        Map<String, Object> param = new HashMap<>();
        if (StringUtils.isBlank(belongSingleId)) {
            return Collections.emptyList();
        }
        param.put("belongSingleId", belongSingleId);
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    param.put(name, value);
                }
            }
        }
        List<JSONObject> deptDemandList = drbfmSingleDao.getSingleDeptDemandList(param);
        return deptDemandList;
    }

    public void insertDeptDemand(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        drbfmSingleDao.createDeptDemand(formData);
    }

    public void updateDeptDemand(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        drbfmSingleDao.updateDeptDemand(formData);
    }

    public JSONObject getSingleDeptDemandDetail(String id) {
        JSONObject detailObj = drbfmSingleDao.querySingleDeptDemandById(id);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    public JsonResult deleteDeptDemand(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> idList = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("ids", idList);
        drbfmSingleDao.deleteDeptDemand(param);
        // 删除附件
        String fileBasePath = SysPropertiesUtil.getGlobalProperty("drbfm");
        for (String relDemandId : ids) {
            JSONObject params = new JSONObject();
            params.put("relDemandId", relDemandId);
            List<JSONObject> fileList = drbfmTestTaskService.queryDemandList(params);
            for (JSONObject oneFile : fileList) {
                JSONObject fileIdJson = new JSONObject();
                rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                        oneFile.getString("relDemandId"), fileBasePath);
                fileIdJson.put("id", oneFile.getString("id"));
                drbfmTestTaskDao.deleteDemand(fileIdJson);
            }
            rdmZhglFileManager.deleteDirFromDisk(relDemandId, fileBasePath);
        }

        return result;
    }

    // 功能分解
    public List<JSONObject> getFunctionList(String belongId, String filterParams) {
        List<JSONObject> ckddCnList = new ArrayList<>();
        List<JSONObject> ckddCnCollectList = new ArrayList<>();
        List<JSONObject> ckddEmptyCollectList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("belongSingleId", belongId);
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    param.put(name, value);
                }
            }
        }
        // 单一流程表单查询
        ckddCnList = drbfmSingleDao.getFunctionList(param);
        // 单一流程表单补充查询+接口需求收集（关联单一流程）
        ckddCnCollectList = drbfmSingleDao.getFunctionCollectList(param);
        ckddCnList.addAll(ckddCnCollectList);
        return ckddCnList;
    }

    // 功能分解
    public List<JSONObject> getFunctionListByCollectId(String belongCollectFlowId, String filterParams) {
        List<JSONObject> ckddCnList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("belongCollectFlowId", belongCollectFlowId);
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    param.put(name, value);
                }
            }
        }
        ckddCnList = drbfmSingleDao.getFunctionListByCollectId(param);
        return ckddCnList;
    }

    // 功能分解
    public List<JSONObject> getFunctionListByJSId(String jsId, String filterParams) {
        List<JSONObject> ckddCnList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("jsId", jsId);
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    param.put(name, value);
                }
            }
        }
        ckddCnList = drbfmSingleDao.getFunctionListByJSId(param);
        return ckddCnList;
    }

    public void insertFunction(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        drbfmSingleDao.createFunction(formData);
    }

    public void updateFunction(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        drbfmSingleDao.updateFunction(formData);
    }

    public JSONObject getFunctionDetail(String id) {
        JSONObject detailObj = drbfmSingleDao.queryFunctionById(id);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    public JsonResult deleteFunction(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> idList = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("ids", idList);
        drbfmSingleDao.deleteFunction(param);
        return result;
    }

    // 要求描述
    public List<JSONObject> getRequestList(String belongId, String filterParams) {
        Map<String, Object> param = new HashMap<>();
        param.put("belongSingleId", belongId);
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    param.put(name, value);
                }
            }
        }
        List<JSONObject> ckddCnList = drbfmSingleDao.getRequestList(param);
        List<JSONObject> ckddCnCollectList = drbfmSingleDao.getRequestCollectList(param);
        ckddCnList.addAll(ckddCnCollectList);
        return ckddCnList;
    }
    // 验证改进后处理
    public List<JSONObject> getFinalProcessList(String belongId, String filterParams) {
        Map<String, Object> param = new HashMap<>();
        param.put("belongSingleId", belongId);
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    param.put(name, value);
                }
            }
        }
        List<JSONObject> ckddCnList = drbfmSingleDao.getFinalProcessList(param);
        List<JSONObject> ckddCnCollectList = drbfmSingleDao.getRequestCollectList(param);
        ckddCnList.addAll(ckddCnCollectList);
        return ckddCnList;
    }
    public List<JSONObject> getRequestListByCollectId(String belongCollectFlowId, String filterParams) {
        Map<String, Object> param = new HashMap<>();
        param.put("belongCollectFlowId", belongCollectFlowId);
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    param.put(name, value);
                }
            }
        }
        List<JSONObject> ckddCnList = drbfmSingleDao.getRequestListByCollectId(param);
        return ckddCnList;
    }

    public List<JSONObject> getRequestListByJSId(String jsId, String filterParams) {
        Map<String, Object> param = new HashMap<>();
        param.put("jsId", jsId);
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    param.put(name, value);
                }
            }
        }
        List<JSONObject> ckddCnList = drbfmSingleDao.getRequestListByJSId(param);
        return ckddCnList;
    }

    public void insertRequest(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        drbfmSingleDao.createRequest(formData);
        sxmsProcess(formData.getString("id"), formData.getString("belongSingleId"),
                formData.getJSONArray("sxmsGridData"));
        dimensionProcess(formData.getString("id"), formData.getString("belongSingleId"),
                formData.getJSONArray("riskGridData"));

    }

    public void updateRequest(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        drbfmSingleDao.updateRequest(formData);
        sxmsProcess(formData.getString("id"), formData.getString("belongSingleId"),
                formData.getJSONArray("sxmsGridData"));
        dimensionProcess(formData.getString("id"), formData.getString("belongSingleId"),
                formData.getJSONArray("riskGridData"));

    }

    private void sxmsProcess(String applyId, String belongSingleId, JSONArray demandArr) {
        if (demandArr == null || demandArr.isEmpty()) {
            return;
        }
        JSONObject param = new JSONObject();
        param.put("ids", new JSONArray());
        for (int index = 0; index < demandArr.size(); index++) {
            JSONObject oneObject = demandArr.getJSONObject(index);
            String id = oneObject.getString("id");
            String state = oneObject.getString("_state");
            if (StringUtils.isBlank(id)) {
                oneObject.put("id", IdUtil.getId());
                oneObject.put("yqId", applyId);
                oneObject.put("partId", belongSingleId);
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                drbfmSingleDao.insertSxms(oneObject);
            } else if ("modified".equals(state)) {
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", new Date());
                drbfmSingleDao.updateSxms(oneObject);
            } else if ("removed".equals(state)) {
                // 删除
                param.getJSONArray("ids").add(oneObject.getString("id"));
            }
        }
        if (!param.getJSONArray("ids").isEmpty()) {
            drbfmSingleDao.deleteSxms(param);
            //失效模式删除后将变化点关联的失效模式id设置为空
            drbfmSingleDao.updateChangeRelSxms(param);
        }
    }

    private void dimensionProcess(String applyId, String belongSingleId, JSONArray demandArr) {
        if (demandArr == null || demandArr.isEmpty()) {
            return;
        }
        JSONObject param = new JSONObject();
        param.put("ids", new JSONArray());
        for (int index = 0; index < demandArr.size(); index++) {
            JSONObject oneObject = demandArr.getJSONObject(index);
            String id = oneObject.getString("id");
            String state = oneObject.getString("_state");
            if (StringUtils.isBlank(id)) {
                oneObject.put("id", IdUtil.getId());
                oneObject.put("yqId", applyId);
                oneObject.put("partId", belongSingleId);
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                drbfmSingleDao.insertDimension(oneObject);
            } else if ("modified".equals(state)) {
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", new Date());
                drbfmSingleDao.updateDimension(oneObject);
            } else if ("removed".equals(state)) {
                // 删除
                param.getJSONArray("ids").add(oneObject.getString("id"));
            }
        }
        if (!param.getJSONArray("ids").isEmpty()) {
            drbfmSingleDao.deleteDimension(param);
        }
    }

    public JSONObject getRequestDetail(String standardId) {
        JSONObject detailObj = drbfmSingleDao.queryRequestById(standardId);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    public JsonResult deleteRequest(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> idList = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("ids", idList);
        // 风险列表全删
        drbfmSingleDao.deleteRequest(param);
        // 这里要删其他
        JSONObject params = new JSONObject();
        params.put("ids", idList);
        List<String> sxmsIdList = drbfmSingleDao.querySxmsIdByRequestIds(params);
        params.clear();
        params.put("applyIds", idList);
        drbfmSingleDao.deleteDimension(params);
        if (sxmsIdList.size() == 0) {
            return result;
        }

        params.clear();
        params.put("ids", sxmsIdList);

        // 1.删失效模式列表 查出来所有失效模式的列表 需要id
        drbfmSingleDao.deleteSxms(params);
        // 2.删关系表
        params.clear();
        params.put("baseIds", sxmsIdList);
        drbfmSingleDao.deleteSxmsRel(params);
        params.clear();
        params.put("relIds", sxmsIdList);
        drbfmSingleDao.deleteSxmsRel(params);

        params.clear();
        params.put("ids", sxmsIdList);
        params.put("sxyyIds", sxmsIdList);
        // 3.删数值表
        drbfmSingleDao.deleteRiskAnalysisBySxms(params);

        return result;
    }

    // 指标分解
    public List<JSONObject> getQuotaList(String belongId, String filterParams) {
        Map<String, Object> param = new HashMap<>();
        param.put("belongSingleId", belongId);
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    param.put(name, value);
                }
            }
        }
        List<JSONObject> ckddCnList = drbfmSingleDao.getQuotaList(param);
        List<JSONObject> ckddCnCollectList = drbfmSingleDao.getQuotaCollectList(param);
        ckddCnList.addAll(ckddCnCollectList);
        for (JSONObject oneData : ckddCnList) {
            oneData.put("CREATE_TIME_", DateFormatUtil.format(oneData.getDate("CREATE_TIME_"), "yyyy-MM-dd HH:mm"));
            if (oneData.getDate("stopTime") != null) {
                oneData.put("stopTime", DateFormatUtil.format(oneData.getDate("stopTime"), "yyyy-MM-dd HH:mm"));
            }
        }
        queryStandardNamesForQuota(ckddCnList, true);
        return ckddCnList;
    }

    // 指标分解
    public List<JSONObject> getQuotaListByJSList(String jsId, String filterParams) {
        Map<String, Object> param = new HashMap<>();
        param.put("jsId", jsId);
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    param.put(name, value);
                }
            }
        }
        List<JSONObject> ckddCnList = drbfmSingleDao.getQuotaListByJSList(param);
        for (JSONObject oneData : ckddCnList) {
            oneData.put("CREATE_TIME_", DateFormatUtil.format(oneData.getDate("CREATE_TIME_"), "yyyy-MM-dd HH:mm"));
            if (oneData.getDate("stopTime") != null) {
                oneData.put("stopTime", DateFormatUtil.format(oneData.getDate("stopTime"), "yyyy-MM-dd HH:mm"));
            }
        }
        queryStandardNamesForQuota(ckddCnList, true);
        return ckddCnList;
    }

    private void queryStandardNamesForQuota(List<JSONObject> quotaList, boolean forPage) {
        if (quotaList == null || quotaList.isEmpty()) {
            return;
        }
        List<String> fullList = new ArrayList<>();
        for (Map<String, Object> mp : quotaList) {
            if (mp.get("sjStandardIds") != null) {
                fullList.addAll(Arrays.asList(mp.get("sjStandardIds").toString().split(",")));
            }
            if (mp.get("testStandardIds") != null) {
                fullList.addAll(Arrays.asList(mp.get("testStandardIds").toString().split(",")));
            }
            if (mp.get("evaluateStandardIds") != null) {
                fullList.addAll(Arrays.asList(mp.get("evaluateStandardIds").toString().split(",")));
            }
        }
        if (fullList.isEmpty()) {
            return;
        }
        // 用id查询标准名称
        JSONObject params1 = new JSONObject();
        params1.put("ids", fullList);
        List<JSONObject> standardRes = drbfmTestTaskDao.queryStandardIds(params1);
        Map<String, String> standardMap = new HashMap<>();
        // 转换成id-标准字典
        for (JSONObject res : standardRes) {
            standardMap.put(res.getString("id"), res.getString("standardNumber") + " " + res.getString("standardName"));
        }

        // 再次遍历结果，id转换成名称
        for (Map<String, Object> mp : quotaList) {
            if (mp.get("sjStandardIds") != null && !"".equalsIgnoreCase(mp.get("sjStandardIds").toString())) {
                StringBuffer tmpStr = new StringBuffer();
                for (String item : mp.get("sjStandardIds").toString().split(",")) {
                    tmpStr.append(standardMap.get(item));
                    if (forPage) {
                        tmpStr.append("，<br>");
                    } else {
                        tmpStr.append("\n");
                    }
                }
                if (forPage) {
                    mp.put("sjStandardNames", tmpStr.substring(0, tmpStr.length() - 5));
                } else {
                    mp.put("sjStandardNames", tmpStr.toString());
                }
            }
            if (mp.get("testStandardIds") != null && !"".equalsIgnoreCase(mp.get("testStandardIds").toString())) {
                StringBuffer tmpStr = new StringBuffer();
                for (String item : mp.get("testStandardIds").toString().split(",")) {
                    tmpStr.append(standardMap.get(item));
                    if (forPage) {
                        tmpStr.append("，<br>");
                    } else {
                        tmpStr.append("\n");
                    }
                }
                if (forPage) {
                    mp.put("testStandardNames", tmpStr.substring(0, tmpStr.length() - 5));
                } else {
                    mp.put("sjStandardNames", tmpStr.toString());
                }
            }
            if (mp.get("evaluateStandardIds") != null
                    && !"".equalsIgnoreCase(mp.get("evaluateStandardIds").toString())) {
                StringBuffer tmpStr = new StringBuffer();
                for (String item : mp.get("evaluateStandardIds").toString().split(",")) {
                    tmpStr.append(standardMap.get(item));
                    if (forPage) {
                        tmpStr.append("，<br>");
                    } else {
                        tmpStr.append("\n");
                    }
                }
                if (forPage) {
                    mp.put("evaluateStandardNames", tmpStr.substring(0, tmpStr.length() - 5));
                } else {
                    mp.put("sjStandardNames", tmpStr.toString());
                }
            }
        }
    }

    public void insertQuota(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("validStatus", "有效");
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        drbfmSingleDao.createQuota(formData);
    }

    public void updateQuota(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        drbfmSingleDao.updateQuota(formData);
    }

    public JSONObject getQuotaDetail(String standardId) {
        JSONObject detailObj = drbfmSingleDao.queryQuotaById(standardId);
        if (detailObj == null) {
            return new JSONObject();
        }
        // 查询各标准的Ids的名称
        // 如果都空直接返回
        if (StringUtil.isEmpty(detailObj.getString("sjStandardIds"))
                && StringUtil.isEmpty(detailObj.getString("testStandardIds"))
                && StringUtil.isEmpty(detailObj.getString("evaluateStandardIds"))) {
            return detailObj;
        }

        List<String> fullList = new ArrayList<>();
        List<String> sjbz = null;
        List<String> csbz = null;
        List<String> pjbz = null;
        if (StringUtil.isNotEmpty(detailObj.getString("sjStandardIds"))) {
            sjbz = Arrays.asList(detailObj.getString("sjStandardIds").split(","));
            fullList.addAll(sjbz);
        }
        if (StringUtil.isNotEmpty(detailObj.getString("testStandardIds"))) {
            csbz = Arrays.asList(detailObj.getString("testStandardIds").split(","));
            fullList.addAll(csbz);
        }
        if (StringUtil.isNotEmpty(detailObj.getString("evaluateStandardIds"))) {
            pjbz = Arrays.asList(detailObj.getString("evaluateStandardIds").split(","));
            fullList.addAll(pjbz);
        }

        JSONObject params1 = new JSONObject();
        params1.put("ids", fullList);
        List<JSONObject> standardRes = drbfmTestTaskDao.queryStandardIds(params1);
        // 标准名称拼接
        StringBuffer sjStr = new StringBuffer();
        StringBuffer csStr = new StringBuffer();
        StringBuffer pjStr = new StringBuffer();
        Map<String, String> standardMap = new HashMap<>();
        for (JSONObject res : standardRes) {
            standardMap.put(res.getString("id"), res.getString("standardNumber") + " " + res.getString("standardName"));
        }
        if (sjbz != null) {
            for (String key : sjbz) {
                sjStr.append(standardMap.get(key)).append("，");
            }
            sjStr.deleteCharAt(sjStr.length() - 1);
        }

        if (csbz != null) {
            for (String key : csbz) {
                csStr.append(standardMap.get(key)).append("，");
            }
            csStr.deleteCharAt(csStr.length() - 1);
        }
        if (pjbz != null) {
            for (String key : pjbz) {
                pjStr.append(standardMap.get(key)).append("，");
            }
            pjStr.deleteCharAt(pjStr.length() - 1);
        }
        if (StringUtil.isNotEmpty(sjStr.toString()) && !sjStr.toString().equalsIgnoreCase("null")) {
            detailObj.put("sjStandardNames", sjStr.toString());

        }
        if (StringUtil.isNotEmpty(csStr.toString()) && !csStr.toString().equalsIgnoreCase("null")) {
            detailObj.put("testStandardNames", csStr.toString());

        }
        if (StringUtil.isNotEmpty(pjStr.toString()) && !pjStr.toString().equalsIgnoreCase("null")) {
            detailObj.put("evaluateStandardNames", pjStr.toString());
        }
        return detailObj;
    }

    public JsonResult deleteQuota(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> idList = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("ids", idList);
        drbfmSingleDao.deleteQuota(param);
        // 根据目录删除文件
        String filePathBase = SysPropertiesUtil.getGlobalProperty("drbfm");
        for (String applyId : ids) {
            String filePath = filePathBase + File.separator + applyId;
            File fileDir = new File(filePath);
            FileUtil.removeDir(fileDir);
        }

        // 删除文件表
        JSONObject paramObj = new JSONObject();
        paramObj.put("relQuotaIds", idList);
        drbfmTestTaskDao.deleteDemand(paramObj);
        return result;
    }

    // 检查风险分析分解内容是否填写完成
    public JsonResult checkRiskDecompose(String singleId) {
        JsonResult result = new JsonResult(true, "成功");
        JSONObject params = new JSONObject();
        params.put("belongSingleId", singleId);
        List<JSONObject> requestList = drbfmSingleDao.getRequestList(params);
        List<JSONObject> quotaList = drbfmSingleDao.getQuotaList(params);
        JSONObject detailObj = drbfmSingleDao.querySingleBaseById(singleId);
        boolean base = (StringUtils.isBlank(detailObj.getString("structType"))
                || StringUtils.isBlank(detailObj.getString("checkUserId"))) ? false : true;
        boolean request = requestList == null || requestList.isEmpty() ? false : true;
        boolean quota = quotaList == null || quotaList.isEmpty() ? false : true;
        if (!base) {
            result.setMessage("请填写“基本信息”页签中的“部件/接口类型”、“校对室主任”!");
            result.setSuccess(false);
            return result;
        }
        // 验证是否有未结束的部门需求收集流程
        List<JSONObject> demandCollectList = drbfmSingleDao.getDeptDemandCollectList(params);
        if (demandCollectList != null && demandCollectList.size() > 0) {
            for (JSONObject oneData : demandCollectList) {
                String oneDataStatus = oneData.getString("instStatus");
                if (!BpmInst.STATUS_SUCCESS_END.equals(oneDataStatus)
                        && !BpmInst.STATUS_DISCARD.equals(oneDataStatus)) {
                    result.setMessage("当前存在不是“已完成”或“作废”状态的需求收集流程，请跟进流程处理后重试！");
                    result.setSuccess(false);
                    return result;
                }
            }
        }

        if (!request) {
            result.setMessage("请在“功能&特性要求”页签中，添加“特性要求”!");
            result.setSuccess(false);
            return result;
        }
        // 检查要求中的必填项
        for (JSONObject oneRequest : requestList) {
            if (StringUtils.isBlank(oneRequest.getString("requestDesc"))) {
                result.setMessage("请在“功能&特性要求”页签中，填写“特性要求”中的“特性要求”!");
                result.setSuccess(false);
                return result;
            }


//      20240603汤中连要求改为非必填
//            if (StringUtils.isBlank(oneRequest.getString("compareToJP"))) {
//                result.setMessage("请在“功能&特性要求”页签中，填写“特性要求”中的“与竞品对比”!");
//                result.setSuccess(false);
//                return result;
//            }

        }

        if (!quota) {
            result.setMessage("请在“特性值”页签中，添加“特性值”!");
            result.setSuccess(false);
            return result;
        }
        return result;
    }

    // 带有综合评价的指标列表
    public List<JSONObject> getQuotaWithEvaluateList(String belongSingleId, String filterParams) {
        Map<String, Object> param = new HashMap<>();
        param.put("belongSingleId", belongSingleId);
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    param.put(name, value);
                }
            }
        }
        List<JSONObject> quotaWithEvaluateList = drbfmSingleDao.getQuotaWithEvaluateList(param);
        // 查询这些指标所属的有效试验验证任务
        JSONObject params = new JSONObject();
        params.put("belongSingleId", belongSingleId);
        params.put("instStatuses",
                Arrays.asList(BpmInst.STATUS_DRAFTED, BpmInst.STATUS_RUNNING, BpmInst.STATUS_SUCCESS_END));
        List<JSONObject> testDataList = drbfmTestTaskDao.queryQuotaTestData(params);
        Set<String> hasValidTestTaskQuotaIds = new HashSet<>();
        for (JSONObject oneData : testDataList) {
            hasValidTestTaskQuotaIds.add(oneData.getString("relQuotaId"));
        }
        for (JSONObject oneQuota : quotaWithEvaluateList) {
            String quotaId = oneQuota.getString("id");
            if (hasValidTestTaskQuotaIds.contains(quotaId)) {
                oneQuota.put("hasValidTestTask", true);
            } else {
                oneQuota.put("hasValidTestTask", false);
            }
            oneQuota.put("CREATE_TIME_", DateFormatUtil.format(oneQuota.getDate("CREATE_TIME_"), "yyyy-MM-dd HH:mm"));
            if (oneQuota.getDate("stopTime") != null) {
                oneQuota.put("stopTime", DateFormatUtil.format(oneQuota.getDate("stopTime"), "yyyy-MM-dd HH:mm"));
            }

        }
        queryStandardNamesForQuota(quotaWithEvaluateList, true);
        for (JSONObject oneQ : quotaWithEvaluateList){
            if((oneQ.getString("keyCTQ") != null) && StringUtils.isNotBlank(oneQ.getString("keyCTQ")) ){
                continue;
            }else {
                oneQ.put("keyCTQ","是") ;
            }

        }
        return quotaWithEvaluateList;
    }

    // 查询某个指标的综合评价数据
    public List<JSONObject> getOneQuotaEvaluateList(String belongSingleId, String belongQuotaId, String filterParams) {
        Map<String, Object> param = new HashMap<>();
        param.put("belongSingleId", belongSingleId);
        param.put("belongQuotaId", belongQuotaId);
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    param.put(name, value);
                }
            }
        }
        List<JSONObject> oneQuotaEvaluateList = drbfmSingleDao.getOneQuotaEvaluateList(param);
        for (JSONObject oneData : oneQuotaEvaluateList) {
            oneData.put("UPDATE_TIME_", DateFormatUtil.format(oneData.getDate("UPDATE_TIME_"), "yyyy-MM-dd"));
        }
        return oneQuotaEvaluateList;
    }

    public void updateQuotaEvaluate(JSONObject formDataJson) {
        // 更新指标CTQ
        JSONObject updateQuotaObj = new JSONObject();
        updateQuotaObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        updateQuotaObj.put("UPDATE_TIME_", new Date());
        updateQuotaObj.put("keyCTQ", formDataJson.getString("keyCTQ"));
        updateQuotaObj.put("id", formDataJson.getString("belongQuotaId"));
        drbfmSingleDao.updateQuotaCTQ(updateQuotaObj);

        // 更新指标综合评价
        if (StringUtils.isNotBlank(formDataJson.getString("changeQuotaEvaluateData"))) {
            JSONArray evaluateDataJson = JSONObject.parseArray(formDataJson.getString("changeQuotaEvaluateData"));
            for (int i = 0; i < evaluateDataJson.size(); i++) {
                JSONObject oneObject = evaluateDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("belongSingleId", formDataJson.getString("belongSingleId"));
                    oneObject.put("belongQuotaId", formDataJson.getString("belongQuotaId"));
                    oneObject.put("reason", oneObject.getString("reason"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    drbfmSingleDao.createQuotaEvaluate(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    drbfmSingleDao.updateQuotaEvaluate(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    drbfmSingleDao.deleteQuotaEvaluate(oneObject);
                }
            }
        }
    }

    // 作废原有的
    public void stopOldQuota(String quotaId, JsonResult result) {
        if (StringUtils.isBlank(quotaId)) {
            result.setSuccess(false);
            result.setMessage("操作失败，指标id为空！");
            return;
        }
        // 作废原指标
        JSONObject oldParam = new JSONObject();
        oldParam.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        oldParam.put("UPDATE_TIME_", new Date());
        oldParam.put("stopTime", new Date());
        oldParam.put("validStatus", "作废");
        oldParam.put("id", quotaId);
        drbfmSingleDao.updateQuotaStatus(oldParam);
    }

    // 作废原有的，创建新的
    public void stopOldCreateNewQuota(JSONObject formDataJson, JsonResult result) {
        String replaceQuotaId = formDataJson.getString("replaceQuotaId");
        if (StringUtils.isBlank(replaceQuotaId)) {
            result.setSuccess(false);
            result.setMessage("操作失败，原指标id为空！");
            return;
        }
        // 作废原指标
        stopOldQuota(replaceQuotaId, result);

        // 创建新的
        insertQuota(formDataJson);
    }

    public void saveNextWork(JSONObject formData) {
        String id = formData.getString("id");
        String finishTime = formData.getString("finishTime");
        if (StringUtils.isBlank(finishTime)) {
            formData.put("finishTime", null);
        }
        if (StringUtils.isBlank(id)) {
            formData.put("id", IdUtil.getId());
            formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            formData.put("CREATE_TIME_", new Date());
            drbfmSingleDao.createNextWork(formData);
        } else {
            formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            formData.put("UPDATE_TIME_", new Date());
            drbfmSingleDao.updateNextWork(formData);
        }
    }

    // 1、是否存在“作废”之外的验证任务；2、是否存在草稿和运行中的验证任务；3、是否存在未包含到验证任务中的指标和未进行综合评价的指标
    public JSONObject checkTestTaskAndResult(String singleId) {
        JSONObject result = new JSONObject();
        result.put("result", "true");
        Map<String, Object> params = new HashMap<>();
        params.put("belongSingleId", singleId);
        params.put("instStatuses",
                Arrays.asList(BpmInst.STATUS_DRAFTED, BpmInst.STATUS_RUNNING, BpmInst.STATUS_SUCCESS_END));
        List<Map<String, Object>> testTaskList = drbfmTestTaskDao.queryTestTaskList(params);
        if (testTaskList == null || testTaskList.isEmpty()) {
            result.put("result", "confirm");
            result.put("message", "当前尚未创建任何有效的验证任务，确认继续提交？");
            return result;
        }
        for (Map<String, Object> oneTest : testTaskList) {
            if (oneTest.containsKey("status") && (BpmInst.STATUS_DRAFTED.equals(oneTest.get("status").toString())
                    || BpmInst.STATUS_RUNNING.equals(oneTest.get("status").toString()))) {
                result.put("result", "false");
                result.put("message", "当前存在流程未结束的验证任务，请待流程结束后提交！");
                return result;
            }
        }
        List<JSONObject> quotaList = getQuotaWithEvaluateList(singleId, null);
        if (quotaList != null && !quotaList.isEmpty()) {
            boolean hasNoValidTestTask = false;
            boolean hasNoEvaluate = false;
            for (JSONObject oneQuota : quotaList) {
                if (!oneQuota.getBooleanValue("hasValidTestTask")) {
                    hasNoValidTestTask = true;
                }
                if (StringUtils.isBlank(oneQuota.getString("overallResult"))) {
                    hasNoEvaluate = true;
                }
            }
            if (hasNoEvaluate || hasNoValidTestTask) {
                result.put("result", "confirm");
                String message = "";
                if (hasNoValidTestTask) {
                    message = "当前存在未被任何有效验证任务关联的指标；<br>";
                }
                if (hasNoEvaluate) {
                    message += "当前存在未进行综合评价的指标；<br>";
                }
                result.put("message", message + "确认继续提交？");
            }
        }
        return result;
    }

    public JsonResult checkFinalProcess(String singleId) {
        JsonResult result = new JsonResult(true, "成功");
        List<JSONObject> requestList = getRequestList(singleId, null);
        if (requestList == null || requestList.isEmpty()) {
            return result;
        }
        for (JSONObject oneData : requestList) {
            String riskLevelFinal = oneData.getString("riskLevelFinal");
            if (StringUtils.isBlank(riskLevelFinal)) {
                result.setMessage("请填写“改进后处理”页签中要求“" + oneData.getString("requestDesc") + "”的“改进后风险评级”!");
                result.setSuccess(false);
                return result;
            }
        }
        return result;
    }

    public JsonResult deleteSingles(String[] singleIds, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功！");
        Map<String, Object> params = new HashMap<>();
        logger.info("开始删除DFEMA单一项目"+singleIds);
        for (int index = 0; index < singleIds.length; index++) {
            // 查询是否包含成功结束或运行中的验证任务
            params.clear();
            params.put("belongSingleId", singleIds[index]);
            params.put("instStatuses", Arrays.asList(BpmInst.STATUS_SUCCESS_END, BpmInst.STATUS_RUNNING));
            List<Map<String, Object>> testTaskList = drbfmTestTaskDao.queryTestTaskList(params);
            if (testTaskList != null && !testTaskList.isEmpty()) {
                continue;
            }
            // 删除单一部件项目关联的所有数据
            // 流程实例
            bpmInstManager.deleteCascade(instIds[index], "");
            // 部门需求
            Map<String, Object> param = new HashMap<>();
            param.put("belongSingleId", singleIds[index]);
            drbfmSingleDao.deleteDeptDemand(param);
            // 功能
            drbfmSingleDao.deleteFunction(param);
            // 要求
            drbfmSingleDao.deleteRequest(param);

            // 删除失效模式
            JSONObject delParam = new JSONObject();
            delParam.put("partId", singleIds[index]);
            drbfmSingleDao.deleteSxmsBySingleId(delParam);

            // 删除失效模式网
            drbfmSingleDao.deleteSxmsRelBySingleId(delParam);

            //删除分析网
            drbfmSingleDao.deleteRiskAnalysisBySingleId(delParam);

            //删除变化点
            drbfmSingleDao.deleteDimensionBySingleId(delParam);


            // 删除指标文件
            List<JSONObject> quotaList = drbfmSingleDao.getQuotaList(param);
            if (quotaList != null && !quotaList.isEmpty()) {
                String filePathBase = SysPropertiesUtil.getGlobalProperty("drbfm");
                List<String> quotaIds = new ArrayList<>();
                for (JSONObject oneQuota : quotaList) {
                    String filePath = filePathBase + File.separator + oneQuota.getString("id");
                    File fileDir = new File(filePath);
                    FileUtil.removeDir(fileDir);
                    quotaIds.add(oneQuota.getString("id"));
                }
                // 删除文件表
                JSONObject paramObj = new JSONObject();
                paramObj.put("relQuotaIds", quotaIds);
                drbfmTestTaskDao.deleteDemand(paramObj);
            }
            // 指标
            drbfmSingleDao.deleteQuota(param);
            // 指标评价
            JSONObject paramObj = new JSONObject();
            paramObj.put("belongSingleId", singleIds[index]);
            drbfmSingleDao.deleteQuotaEvaluate(paramObj);
            // 下一步工作
            drbfmSingleDao.deleteNextWork(paramObj);
            // 基本信息
            paramObj.put("id", singleIds[index]);
            drbfmSingleDao.deleteSingleBaseInfo(paramObj);
            // 验证任务
            List<Map<String, Object>> testTaskListDel = drbfmTestTaskDao.queryTestTaskList(param);
            if (testTaskListDel == null || testTaskListDel.isEmpty()) {
                continue;
            }
            List<String> testTaskIdList = new ArrayList<>();
            for (Map<String, Object> oneTestTask : testTaskListDel) {
                String id = "";
                String instId = "";
                if (oneTestTask.get("id") != null) {
                    id = oneTestTask.get("id").toString();
                }
                if (oneTestTask.get("instId") != null) {
                    instId = oneTestTask.get("instId").toString();
                }
                testTaskIdList.add(id);
                bpmInstManager.deleteCascade(instId, "");
            }
            drbfmTestTaskService.delApplys(testTaskIdList.toArray(new String[0]));
        }
        logger.info("删除DFEMA单一项目"+singleIds+"完成");
        return result;
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
            String requestId = toGetParamVal(parameters.get("requestId"));
            String relQuotaId = toGetParamVal(parameters.get("relQuotaId"));
            String belongSingleId = toGetParamVal(parameters.get("belongSingleId"));
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String filePathBase = SysPropertiesUtil.getGlobalProperty("drbfm");
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase");
                return;
            }
            String filePath = "";
            // 向下载目录中写入文件
            if (StringUtils.isNotEmpty(relQuotaId)) {
                filePath = filePathBase + File.separator + relQuotaId;
            }
            if (StringUtils.isNotEmpty(requestId)) {
                filePath = filePathBase + File.separator + requestId;
            }

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
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("relQuotaId", relQuotaId);
            fileInfo.put("requestId", requestId);
            fileInfo.put("belongSingleId", belongSingleId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("creatorName", ContextUtil.getCurrentUser().getFullname());
            fileInfo.put("CREATE_TIME_", new Date());
            drbfmTestTaskDao.insertDemand(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JsonResult createAndStartDemandCollect(HttpServletRequest request, JsonResult result) {
        String singleId = RequestUtil.getString(request, "singleId", "");
        String feedbackUserIds = RequestUtil.getString(request, "feedbackUserIds", "");
        if (StringUtils.isBlank(singleId)) {
            result.setSuccess(false);
            result.setMessage("流程创建失败，单一项目id为空");
            return result;
        }
        if (StringUtils.isBlank(feedbackUserIds)) {
            result.setSuccess(false);
            result.setMessage("流程创建失败，需求反馈人id为空");
            return result;
        }
        // 查找solution
        BpmSolution bpmSol = bpmSolutionManager.getByKey("drbfmSingleDemandCollect", "1");
        if (bpmSol == null) {
            result.setSuccess(false);
            result.setMessage("流程创建失败，需求反馈流程为空");
            return result;
        }
        // 根据singleId查询部件验证项目
        JSONObject detailObj = drbfmSingleDao.querySingleBaseById(singleId);
        if (detailObj == null) {
            result.setSuccess(false);
            result.setMessage("流程创建失败，部件验证项目为空");
            return result;
        }
        String currentUserId = ContextUtil.getCurrentUserId();
        IUser user = userService.getByUserId(currentUserId);
        if (user == null) {
            result.setSuccess(false);
            result.setMessage("流程创建失败，当前用户 ‘" + currentUserId + "’找不到对应的用户信息");
            return result;
        }
        ContextUtil.setCurUser(user);
        String solId = bpmSol.getSolId();
        Set<String> feedBackUserIdSet = new HashSet<>(Arrays.asList(feedbackUserIds.split(",", -1)));
        for (String oneUserId : feedBackUserIdSet) {
            // 加上处理的消息提示
            ProcessMessage handleMessage = new ProcessMessage();
            try {
                ProcessHandleHelper.setProcessMessage(handleMessage);
                ProcessStartCmd startCmd = new ProcessStartCmd();
                startCmd.setSolId(solId);
                JSONObject formData = new JSONObject();
                formData.put("belongSingleId", singleId);
                // 根据用户获取主部门
                JSONObject deptInfo = commonInfoManager.queryDeptByUserId(oneUserId);
                formData.put("demandFeedBackUserId", oneUserId);
                formData.put("demandFeedBackUserName", deptInfo.getString("userName"));
                formData.put("demandFeedBackDeptId", deptInfo.getString("deptId"));
                formData.put("demandFeedBackDeptName", deptInfo.getString("deptName"));
                formData.put("CREATE_BY_", currentUserId);
                formData.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                JSONArray varsArr = new JSONArray();
                JSONObject jixingObj = new JSONObject();
                jixingObj.put("key", "jixing");
                jixingObj.put("val", detailObj.getString("jixing"));
                varsArr.add(jixingObj);
                JSONObject structNameObj = new JSONObject();
                structNameObj.put("key", "structName");
                structNameObj.put("val", detailObj.getString("structName"));
                varsArr.add(structNameObj);
                formData.put("vars", varsArr);
                formData.put("bos", new JSONArray());
                startCmd.setJsonData(formData.toJSONString());
                // 启动流程
                bpmInstManager.doStartProcess(startCmd);
            } catch (Exception ex) {
                // 把具体的错误放置在内部处理，以显示正确的错误信息提示，在此不作任何的错误处理
                logger.error(ExceptionUtil.getExceptionMessage(ex));
                if (handleMessage.getErrorMsges().size() == 0) {
                    handleMessage.addErrorMsg(ex.getMessage());
                }
            } finally {
                // 在处理过程中，是否有错误的消息抛出
                if (handleMessage.getErrorMsges().size() > 0) {
                    result.setSuccess(false);
                    result.setMessage("流程创建失败，启动流程异常!");
                    result.setData(handleMessage.getErrorMsges());
                }
                ProcessHandleHelper.clearProcessCmd();
                ProcessHandleHelper.clearProcessMessage();
            }
        }
        return result;
    }

    public void createDemandCollectFlow(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        drbfmSingleDao.createDemandCollectFlow(formData);
    }

    public JsonPageResult<?> getDeptDemandCollectList(HttpServletRequest request, HttpServletResponse response,
                                                      boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "drbfm_single_deptDemandCollectFlow.CREATE_TIME_", "desc");
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
        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }

        // TODO 增加角色过滤的条件（需要自己办理的目前已包含在下面的条件中）
        List<JSONObject> demandCollectList = drbfmSingleDao.getDeptDemandCollectList(params);
        for (Map<String, Object> oneData : demandCollectList) {
            if (oneData.get("CREATE_TIME_") != null) {
                oneData.put("CREATE_TIME_", DateUtil.formatDate((Date)oneData.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUserJSON(demandCollectList);
        result.setData(demandCollectList);
        int countDeptDemandCollectList = drbfmSingleDao.countDeptDemandCollectList(params);
        result.setTotal(countDeptDemandCollectList);
        return result;
    }

    public JSONObject getDeptDemandCollectJson(String id) {
        JSONObject obj = drbfmSingleDao.getDeptDemandCollectJson(id);
        return obj;
    }

    public JsonResult deleteDemandCollect(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        Map<String, Object> params = new HashMap<>();
        params.put("demandCollectIds", Arrays.asList(ids));
        // 先删除需求子表信息
        drbfmSingleDao.deleteDeptDemand(params);
        // 再删除需求收集流程
        drbfmSingleDao.deleteDemandCollectFlow(params);
        return result;
    }

    // 某个部门需求收集流程中的需求列表
    public List<JSONObject> getOneCollectDemandList(String belongCollectFlowId, String filterParams) {
        Map<String, Object> param = new HashMap<>();
        if (StringUtils.isBlank(belongCollectFlowId)) {
            return Collections.emptyList();
        }
        param.put("belongCollectFlowId", belongCollectFlowId);
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    param.put(name, value);
                }
            }
        }
        List<JSONObject> oneCollectDeptDemandList = drbfmSingleDao.getOneCollectDemandList(param);
        return oneCollectDeptDemandList;
    }

    // 功能分解
    public List<JSONObject> getFunctionExport(String belongId, String filterParams) {
        List<JSONObject> ckddCnList = new ArrayList<>();
        List<JSONObject> ckddCnCollectList = new ArrayList<>();
        List<JSONObject> ckddEmptyCollectList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("belongSingleId", belongId);
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    param.put(name, value);
                }
            }
        }
        ckddCnList = drbfmSingleDao.getFunctionExport(param);
        // 单一流程表单补充查询+接口需求收集（关联单一流程）
        ckddCnCollectList = drbfmSingleDao.getFunctionCollectList(param);
        ckddCnList.addAll(ckddCnCollectList);
        return ckddCnList;
    }

    public void exportFunction(String singleId, HttpServletRequest request, HttpServletResponse response) {
        List<JSONObject> list = getFunctionExport(singleId, null);

        JSONObject jsonObject = getSingleDetail(singleId);
        // 部件、接口名称
        String structName = jsonObject.getString("structName");
        if (!jsonObject.containsKey("structName")) {
            structName = "";
        } else {
            structName = "【" + structName + "】";
        }
        // 项目流水号
        String singleNumber = jsonObject.getString("singleNumber");
        if (!jsonObject.containsKey("singleNumber")) {
            singleNumber = "";
        } else {
            singleNumber = "【" + singleNumber + "】";
        }
        // 机型
        String jixing = jsonObject.getString("jixing");
        if (!jsonObject.containsKey("jixing")) {
            jixing = "";
        } else {
            jixing = "【" + jixing + "】";
        }
        // 流程名称
        String flowName = "部件验证项目";
        // 当前时间
        // String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        // nowDate = "【" +nowDate +"】";

        int i = 0;
        for (JSONObject one : list) {
            one.put("index", i + 1);
            i++;
        }

        String title = flowName + singleNumber + structName + jixing + "功能描述";
        String[] fieldNames = {"序号", "所属维度", "功能描述", "关联部门需求描述", "接口名称"};
        String[] fieldCodes = {"index", "relDimensionNames", "functionDesc", "demandDesc", "structName"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(list, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(title, wbObj, response);
    }

    public List<JSONObject> getRequestExport(String belongId, String filterParams) {
        Map<String, Object> param = new HashMap<>();
        param.put("belongSingleId", belongId);
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    param.put(name, value);
                }
            }
        }
        List<JSONObject> ckddCnList = drbfmSingleDao.getRequestExport(param);
        List<JSONObject> ckddCnCollectList = drbfmSingleDao.getRequestCollectList(param);
        ckddCnList.addAll(ckddCnCollectList);
        return ckddCnList;
    }

    public void exportRequest(String singleId, HttpServletRequest request, HttpServletResponse response) {
        List<JSONObject> list = getRequestExport(singleId, null);

        JSONObject jsonObject = getSingleDetail(singleId);
        // 部件、接口名称
        String structName = jsonObject.getString("structName");
        if (!jsonObject.containsKey("structName")) {
            structName = "";
        } else {
            structName = "【" + structName + "】";
        }
        // 项目流水号
        String singleNumber = jsonObject.getString("singleNumber");
        if (!jsonObject.containsKey("singleNumber")) {
            singleNumber = "";
        } else {
            singleNumber = "【" + singleNumber + "】";
        }
        // 机型
        String jixing = jsonObject.getString("jixing");
        if (!jsonObject.containsKey("jixing")) {
            jixing = "";
        } else {
            jixing = "【" + jixing + "】";
        }
        // 流程名称
        String flowName = "部件验证项目";
        // 当前时间
        // String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        // nowDate = "【" +nowDate +"】";

        int i = 0;
        for (JSONObject one : list) {
            one.put("index", i + 1);
            i++;
        }

        String title = flowName + singleNumber + structName + jixing + "要求描述";
        String[] fieldNames = {"序号", "所属维度", "关联功能描述", "要求描述", "较上一代或相似产品变化点", "变化点带来的与该要求相关的客观影响", "与竞品对比", "存在风险",
                "风险评级", "接口名称"};
        String[] fieldCodes = {"index", "relDimensionNames", "relFunctionDesc", "requestDesc", "requestChanges",
                "relEffect", "compareToJP", "riskDesc", "riskLevel", "structName"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(list, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(title, wbObj, response);
    }


    // 导出全部
    public List<JSONObject> getDfmeaExport(String belongId) {

        JSONObject params = new JSONObject();
        params.put("partId", belongId);

        List<JSONObject> dfmeaRes = drbfmSingleDao.getDfmeaExport(params);

        for (JSONObject oneObj : dfmeaRes) {
            String gnfxgzys = oneObj.getString("gnfxgzys").replace("特性要求","\n特性要求");
            String gnfxxj = oneObj.getString("gnfxxj").replace("特性要求","\n特性要求");
            oneObj.put("gnfxgzys", gnfxgzys);
            oneObj.put("gnfxxj", gnfxxj);
        }
        // 要拼接向上关联的信息：部件名 功能和要求
        List<JSONObject> sxms2SjList = drbfmSingleDao.querySxms2SjList(params);
        // 名称字典
        Map<String, String> sxms2SjbjMap = new HashMap<>();
        Map<String, String> sxms2SjgnMap = new HashMap<>();
        for (JSONObject oneObj : sxms2SjList) {
            String sxmsName = oneObj.getString("sxmsName");
            // 拼接了部件名称叫 sxyxAllName
            // 要求名
            String yqName = oneObj.getString("requestDesc");
            String gnName = oneObj.getString("functionDesc");
            String structName = oneObj.getString("structName");
            if (!sxms2SjbjMap.containsKey(sxmsName)) {
                sxms2SjbjMap.put(sxmsName, structName);
            } else {
                sxms2SjbjMap.put(sxmsName, sxms2SjbjMap.get(sxmsName) + "\n\n" + structName);
            }
            if (!sxms2SjgnMap.containsKey(sxmsName)) {
                sxms2SjgnMap.put(sxmsName, "功能:" + gnName + "\n" + "特性要求:" + yqName);
            } else {
                sxms2SjgnMap.put(sxmsName,
                        sxms2SjgnMap.get(sxmsName) + "\n\n" + "功能:" + gnName + "\n" + "特性要求:" + yqName);
            }
        }

        List<JSONObject> sxms2SxyxList = drbfmSingleDao.querySxms2SxyxList(params);

        // 名称字典
        Map<String, String> sxms2SxyxMap = new HashMap<>();
        for (JSONObject oneObj : sxms2SxyxList) {
            String sxmsName = oneObj.getString("sxmsName");
            // 拼接了部件名称叫 sxyxAllName
            String sxyxName = oneObj.getString("sxyxAllName");
            // String sxyxName = oneObj.getString("sxyxName");
            if (!sxms2SxyxMap.containsKey(sxmsName)) {
                sxms2SxyxMap.put(sxmsName, sxyxName);
            } else {
                sxms2SxyxMap.put(sxmsName, sxms2SxyxMap.get(sxmsName) + "\n" + sxyxName);
                // sxms2SxyxMap.put(sxmsName, sxms2SxyxMap.get(sxmsName)+","+sxyxName);
            }
        }


        for (JSONObject oneObj : dfmeaRes) {
            String sxmsName = oneObj.getString("sxmsName");
            if (sxms2SxyxMap.containsKey(sxmsName)) {
                oneObj.put("sxyxName", sxms2SxyxMap.get(sxmsName));
            }
            if (sxms2SjgnMap.containsKey(sxmsName)) {
                oneObj.put("gnfxsj", sxms2SjgnMap.get(sxmsName));
            }
            if (sxms2SjbjMap.containsKey(sxmsName)) {
                oneObj.put("jgfxsj", sxms2SjbjMap.get(sxmsName));
            }
        }

        return dfmeaRes;
    }

    public void exportAll(String singleId, HttpServletRequest request, HttpServletResponse response) {
        List<JSONObject> list = getDfmeaExport(singleId);

        JSONObject jsonObject = getSingleDetail(singleId);
        // 部件、接口名称
        String structName = jsonObject.getString("structName");
        if (!jsonObject.containsKey("structName")) {
            structName = "";
        } else {
            structName = "【" + structName + "】";
        }
        // 项目流水号
        String singleNumber = jsonObject.getString("singleNumber");
        if (!jsonObject.containsKey("singleNumber")) {
            singleNumber = "";
        } else {
            singleNumber = "【" + singleNumber + "】";
        }
        // 机型
        String jixing = jsonObject.getString("jixing");
        if (!jsonObject.containsKey("jixing")) {
            jixing = "";
        } else {
            jixing = "【" + jixing + "】";
        }
        // 流程名称
        String flowName = "DFMEA";


        int i = 0;
        for (JSONObject one : list) {
            one.put("index", i + 1);
            i++;
        }

        String title = flowName + singleNumber + structName + jixing + "全部内容导出";
        String[] fieldNames = {"序号", "1.上一较高级别", "2.关注要素", "3.下一较低级别"
                , "1.上一较高级别", "2.关注要素", "3.下一较低级别"
                ,"1.对于上一较高级别要素和/或最终用户的失效影响(FE)","失效影响的严重度(S)","2.关注要素的失效模式(FM)","3.下一较低级别要素或特性的失效起因(FC)"
                ,"对失效起因的当前预防控制(PC)","失效起因的频度(O)","对失效起因的当前探测控制(DC)","对失效模式的当前探测控制(DC)","失效起因或失效模式的探测度(D)","DFMEA措施优先级","筛选器代码(可选)"
                ,"DFMEA预防措施","DFMEA探测措施","负责人姓名","目标完成日期","状态","采取基于证据的措施","完成日期"
                ,"严重度(S)","频度(O)","探测度(D)","DFMEA措施优先级","筛选器代码(可选)","备注"};
        String[] fieldCodes = {"index", "jgfxsj", "jgfxgzys", "jgfxxj"
                , "gnfxsj", "gnfxgzys", "gnfxxj"
                ,"sxyxName","yzd","sxmsAllName","sxyyAllName"
                ,"xxyfkz","fsd","tckzfc","tckzfm","tcd","csyxj","sxqdm_empty"
                ,"gjyfcs","gjtccs","fzrxm_empty","mbwcrq_empty","zt_empty","sqjyzjdcs__empty","wcrq_empty"
                ,"riskLevelFinalyzd","riskLevelFinalfsd","riskLevelFinaltcd","riskLevelFinal","sxqdm_empty","bz_empty"
        };
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(list, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(title, wbObj, response);
    }

    public List<JSONObject> getQuotaExport(String belongId, String filterParams) {
        Map<String, Object> param = new HashMap<>();
        param.put("belongSingleId", belongId);
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    param.put(name, value);
                }
            }
        }
        List<JSONObject> ckddCnList = drbfmSingleDao.getQuotaExport(param);
        List<JSONObject> ckddCnCollectList = drbfmSingleDao.getQuotaCollectList(param);
        ckddCnList.addAll(ckddCnCollectList);
        for (JSONObject oneData : ckddCnList) {
            oneData.put("CREATE_TIME_", DateFormatUtil.format(oneData.getDate("CREATE_TIME_"), "yyyy-MM-dd HH:mm"));
            if (oneData.getDate("stopTime") != null) {
                oneData.put("stopTime", DateFormatUtil.format(oneData.getDate("stopTime"), "yyyy-MM-dd HH:mm"));
            }
        }
        queryStandardNamesForQuota(ckddCnList, true);
        return ckddCnList;
    }

    public void exportQuota(String singleId, HttpServletRequest request, HttpServletResponse response) {
        List<JSONObject> list = getQuotaExport(singleId, null);

        JSONObject jsonObject = getSingleDetail(singleId);

        // 部件、接口名称
        String structName = jsonObject.getString("structName");
        if (!jsonObject.containsKey("structName")) {
            structName = "";
        } else {
            structName = "【" + structName + "】";
        }
        // 项目流水号
        String singleNumber = jsonObject.getString("singleNumber");
        if (!jsonObject.containsKey("singleNumber")) {
            singleNumber = "";
        } else {
            singleNumber = "【" + singleNumber + "】";
        }
        // 机型
        String jixing = jsonObject.getString("jixing");
        if (!jsonObject.containsKey("jixing")) {
            jixing = "";
        } else {
            jixing = "【" + jixing + "】";
        }
        // 流程名称
        String flowName = "部件验证项目";
        // 当前时间
        // String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        // nowDate = "【" +nowDate +"】";

        int i = 0;
        for (JSONObject one : list) {
            one.put("index", i + 1);
            i++;
        }

        String title = flowName + singleNumber + structName + jixing + "指标分解";
        String[] fieldNames =
                {"序号", "关联功能", "关联的要求", "指标名称", "设计标准值", "设计标准", "测试标准", "评价标准", "指标状态", "代替的旧指标", "创建时间", "作废时间"};
        String[] fieldCodes = {"index", "functionDesc", "requestDesc", "quotaName", "sjStandardValue",
                "sjStandardNames", "testStandardNames", "evaluateStandardNames", "validStatus", "replaceQuotaName",
                "CREATE_TIME_", "stopTime"};
        // String[] fieldNames = {"序号","指标名称", "设计标准值", "关联功能", "关联的要求",
        // "设计标准", "测试标准", "评价标准", "指标状态",
        // "代替的旧指标", "创建时间", "作废时间"};
        // String[] fieldCodes = {"index","quotaName", "sjStandardValue", "functionDesc", "requestDesc",
        // "sjStandardNames", "testStandardNames", "evaluateStandardNames", "validStatus",
        // "replaceQuotaName", "CREATE_TIME_", "stopTime"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(list, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(title, wbObj, response);
    }

    // 附件上传
    public void saveDemandFiles(HttpServletRequest request) {
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
            String relDemandId = toGetParamVal(parameters.get("relDemandId"));
            String belongSingleId = toGetParamVal(parameters.get("belongSingleId"));
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String filePathBase = SysPropertiesUtil.getGlobalProperty("drbfm");
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase");
                return;
            }
            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + relDemandId;
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
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("relDemandId", relDemandId);
            fileInfo.put("belongSingleId", belongSingleId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("creatorName", ContextUtil.getCurrentUser().getFullname());
            fileInfo.put("CREATE_TIME_", new Date());
            drbfmTestTaskDao.insertDemand(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getTotalStructDetail(String id) {
        JSONObject detailObj = drbfmSingleDao.queryTotalStructDetailById(id);
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.getDate("CREATE_TIME_") != null) {
            detailObj.put("CREATE_TIME_", DateUtil.formatDate(detailObj.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
        }
        return detailObj;
    }

    public void createSingleInterfaceCollectFlow(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        formData.put("interfaceFeedBackUserName", ContextUtil.getCurrentUser().getFullname());
        drbfmSingleDao.createSingleInterfaceCollectFlow(formData);
    }

    public JsonPageResult<?> getInterfaceCollectList(HttpServletRequest request, HttpServletResponse response,
                                                     boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        // rdmZhglUtil.addOrder(request, params, "drbfm_single_baseInfo.CREATE_TIME_", "desc");
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
        String structIds = RequestUtil.getString(request, "structIds", "");
        if (StringUtils.isNotBlank(structIds)) {
            params.put("structIds", Arrays.asList(structIds));
        }
        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }

        // TODO 增加角色过滤的条件（需要自己办理的目前已包含在下面的条件中）
        List<Map<String, Object>> singleList = drbfmSingleDao.getInterfaceCollectList(params);
        for (Map<String, Object> oneData : singleList) {
            if (oneData.get("CREATE_TIME_") != null) {
                oneData.put("CREATE_TIME_", DateUtil.formatDate((Date)oneData.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(singleList);
        // 过滤
        List<Map<String, Object>> singleListFilter = filterListById(singleList);
        // 返回
        result.setData(singleListFilter);
        // int countJsmmDataList = drbfmSingleDao.countInterfaceCollectList(params);
        int countJsmmDataList = singleListFilter.size();
        result.setTotal(countJsmmDataList);
        return result;
    }

    public List<Map<String, Object>> filterListById(List<Map<String, Object>> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (list == null || list.isEmpty()) {
            return result;
        }
        // 管理员查看所有的包括草稿数据
        if ("admin".equalsIgnoreCase(ContextUtil.getCurrentUser().getUserNo())) {
            return list;
        }
        // 当前用户id
        String currentUserId = ContextUtil.getCurrentUserId();
        // 依次过滤
        for (Map<String, Object> oneProject : list) {
            // 任务处理人
            if (oneProject.get("currentProcessUserId") != null
                    && oneProject.get("currentProcessUserId").toString().contains(currentUserId)) {
                result.add(oneProject);
                continue;
            }
            // 流程提出人（接口需求提出人）
            if (oneProject.get("CREATE_BY_").toString().contains(currentUserId)) {
                result.add(oneProject);
                continue;
            }
            // 屏蔽草稿
            if (oneProject.get("instStatus").toString().contains("DRAFTED")) {
                continue;
            }
            // 流程接收人（部件负责人）
            if (oneProject.get("analyseUserId").toString().contains(currentUserId)) {
                result.add(oneProject);
                continue;
            }
            // 产品主管
            if (oneProject.get("cpzgId").toString().contains(currentUserId)) {
                result.add(oneProject);
                continue;
            }
        }
        return result;
    }

    // 功能分解
    public List<JSONObject> getFunctionInterfaceList(String belongCollectFlowId, String filterParams) {
        List<JSONObject> ckddCnList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("belongCollectFlowId", belongCollectFlowId);
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    param.put(name, value);
                }
            }
        }
        ckddCnList = drbfmSingleDao.getFunctionInterfaceList(param);
        return ckddCnList;
    }

    // 要求描述
    public List<JSONObject> getRequestInterfaceList(String belongCollectFlowId, String filterParams) {
        Map<String, Object> param = new HashMap<>();
        param.put("belongCollectFlowId", belongCollectFlowId);
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    param.put(name, value);
                }
            }
        }
        List<JSONObject> ckddCnList = drbfmSingleDao.getRequestInterfaceList(param);
        return ckddCnList;
    }

    // 指标分解
    public List<JSONObject> getQuotaInterfaceList(String belongCollectFlowId, String filterParams) {
        Map<String, Object> param = new HashMap<>();
        param.put("belongCollectFlowId", belongCollectFlowId);
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    param.put(name, value);
                }
            }
        }
        List<JSONObject> ckddCnList = drbfmSingleDao.getQuotaInterfaceList(param);
        for (JSONObject oneData : ckddCnList) {
            oneData.put("CREATE_TIME_", DateFormatUtil.format(oneData.getDate("CREATE_TIME_"), "yyyy-MM-dd HH:mm"));
            if (oneData.getDate("stopTime") != null) {
                oneData.put("stopTime", DateFormatUtil.format(oneData.getDate("stopTime"), "yyyy-MM-dd HH:mm"));
            }
        }
        queryStandardNamesForQuota(ckddCnList, true);
        return ckddCnList;
    }

    public String querySingleIdInterface(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        String id = request.getParameter("id");
        List<JSONObject> sinleList = drbfmSingleDao.querySingleIdInterface(id);
        if (sinleList.size() == 0) {
            return "";
        }
        String singleId = sinleList.get(0).getString("id");
        return singleId;
    }

    public String queryInstIdIdInterface(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        String belongCollectFlowId = request.getParameter("belongCollectFlowId");
        List<JSONObject> sinleList = drbfmSingleDao.queryInstIdIdInterface(belongCollectFlowId);
        if (sinleList.size() == 0) {
            return "";
        }
        String instId = sinleList.get(0).getString("INST_ID_");
        return instId;
    }

    public JsonResult deleteInterfaceCollect(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        Map<String, Object> params = new HashMap<>();
        params.put("ids", Arrays.asList(ids));
        drbfmSingleDao.deleteInterfaceCollect(params);
        drbfmSingleDao.deleteInterfaceFunction(params);
        drbfmSingleDao.deleteInterfaceRequest(params);
        drbfmSingleDao.deleteInterfaceQuota(params);
        return result;
    }

    public String queryStatusBySingleId(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        String ceinfoobj = "";
        String id = RequestUtil.getString(request, "id");
        if (StringUtils.isNotBlank(id)) {
            List<JSONObject> defs = drbfmSingleDao.queryStatusBySingleId(id);
            if (defs != null && defs.size() > 0) {
                ceinfoobj = defs.get(0).getString("NAME_");
            }
        }
        return ceinfoobj;
    }

    public JsonResult checkSingleInterface(String singleId) {
        JsonResult result = new JsonResult(false);
        List<JSONObject> bpmInst = drbfmSingleDao.checkSingleInterface(singleId);
        for (JSONObject one : bpmInst) {
            String status = one.getString("STATUS_");
            // 检查流程是否为成功结束（SUCCESS_END）、作废（DISCARD_END）或草稿（DRAFTED），非三类状态为已运行（RUNNING）的，其中ABORT_END状态未做判断
            if (status.equalsIgnoreCase("RUNNING")) {
                result.setSuccess(false);
                return result;
            }
        }
        result.setSuccess(true);
        return result;
    }

    public List<JSONObject> yanzhongduPage(HttpServletRequest request) {
        List<JSONObject> list = drbfmSingleDao.getYanzhongduPage();
        return list;
    }

    public List<JSONObject> fashengduPage(HttpServletRequest request) {
        List<JSONObject> list = drbfmSingleDao.getFashengduPage();
        return list;
    }

    public List<JSONObject> tanceduPage(HttpServletRequest request) {
        List<JSONObject> list = drbfmSingleDao.getTanceduPage();
        return list;
    }

    public JSONObject getRiskLevelSOD(String requestId) {
        JSONObject SODJsonObj = drbfmSingleDao.querySODByRequestId(requestId);
        if (SODJsonObj == null) {
            return new JSONObject();
        }
        return SODJsonObj;
    }
    public JSONObject getRiskLevelFinalSOD(String requestId) {
        JSONObject SODJsonObj = drbfmSingleDao.querySODRiskLevelFinal(requestId);
        if (SODJsonObj == null) {
            return new JSONObject();
        }
        return SODJsonObj;
    }

    public JsonPageResult<?> getSelectSingleList(HttpServletRequest request, HttpServletResponse response,
                                                 boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "drbfm_single_baseInfo.CREATE_TIME_", "desc");
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
        String structIds = RequestUtil.getString(request, "structIds", "");
        if (StringUtils.isNotBlank(structIds)) {
            params.put("structIds", Arrays.asList(structIds));
        }
        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }

        // TODO 增加角色过滤的条件（需要自己办理的目前已包含在下面的条件中）
        List<Map<String, Object>> singleList = drbfmSingleDao.querySingleList(params);
        for (Map<String, Object> oneData : singleList) {
            if (oneData.get("CREATE_TIME_") != null) {
                oneData.put("CREATE_TIME_", DateUtil.formatDate((Date)oneData.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(singleList);
        result.setData(singleList);
        int countJsmmDataList = drbfmSingleDao.countSingleList(params);
        result.setTotal(countJsmmDataList);
        return result;
    }

    public JsonResult copySingleProcess(String[] ids, String singleId) {
        JsonResult result = new JsonResult(true, "快速引入操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        // 功能分解处理
        List<JSONObject> functionList = drbfmSingleDao.getFunctionListByIds(param);
        if (functionList.size() > 0) {
            for (JSONObject functionLine : functionList) {
                // 1.主键重新生成
                functionLine.put("id", IdUtil.getId());
                // 2.belongSingleId重置
                functionLine.put("belongSingleId", singleId);
                // 3.清空关联部门需求id
                functionLine.put("relDeptDemandId", null);
                // 4.创建时间创建人重置，更新时间清空
                functionLine.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                functionLine.put("CREATE_TIME_", new Date());
                functionLine.put("UPDATE_BY_", null);
                functionLine.put("UPDATE_TIME_", null);
            }
            Map<String, Object> paramFunctionList = new HashMap<>();
            paramFunctionList.put("list", functionList);
            drbfmSingleDao.insertFunctionArray(paramFunctionList);
        }
        // 需求分解处理
        HashMap<String, String> reqMap = new HashMap<>();
        List<JSONObject> requestList = drbfmSingleDao.getRequestListByIds(param);
        if (requestList.size() > 0) {
            for (JSONObject requestLine : requestList) {
                // 1.主键重新生成
                String oldId = requestLine.getString("id");
                String newId = IdUtil.getId();
                reqMap.put(oldId, newId);
                requestLine.put("id", newId);
                // 2.belongSingleId重置
                requestLine.put("belongSingleId", singleId);
                // 3.创建时间创建人重置，更新时间清空
                requestLine.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                requestLine.put("CREATE_TIME_", new Date());
                requestLine.put("UPDATE_BY_", null);
                requestLine.put("UPDATE_TIME_", null);
            }
            Map<String, Object> paraRequestList = new HashMap<>();
            paraRequestList.put("list", requestList);
            drbfmSingleDao.insertRequestArray(paraRequestList);
        }
        // 指标分解处理
        List<JSONObject> quotaList = drbfmSingleDao.getQuotaListByIds(param);
        if (quotaList.size() > 0) {
            for (JSONObject quotaLine : quotaList) {
                // 1.主键重新生成
                quotaLine.put("id", IdUtil.getId());
                // 2.belongSingleId重置
                quotaLine.put("belongSingleId", singleId);
                // 3.创建时间创建人重置，更新时间清空
                quotaLine.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                quotaLine.put("CREATE_TIME_", new Date());
                quotaLine.put("UPDATE_BY_", null);
                quotaLine.put("UPDATE_TIME_", null);
            }
            Map<String, Object> paraQuotaList = new HashMap<>();
            paraQuotaList.put("list", quotaList);
            drbfmSingleDao.insertQuotaArray(paraQuotaList);
        }

        // todo  引入失效模式列表

        // todo reqMap要在上面构造


        for (String oldSingleId : businessIds) {
            JSONObject sxmsParam = new JSONObject();
            sxmsParam.put("belongSingleId", oldSingleId);
            List<JSONObject> sxmsList = drbfmSingleDao.querySxmsListBySingleId(sxmsParam);
            if (sxmsList.size() > 0) {
                for (JSONObject oneSxms : sxmsList) {
                    // 1.主键重新生成
                    String oldId = oneSxms.getString("id");
                    String newId = IdUtil.getId();
                    // 这个也需要从old->new
                    oneSxms.put("id", newId);
                    oneSxms.put("oldId", oldId);
                    // 2.belongSingleId重置
                    oneSxms.put("partId", singleId);
                    // 3.创建时间创建人重置，更新时间清空
                    oneSxms.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneSxms.put("CREATE_TIME_", new Date());
                    oneSxms.put("UPDATE_BY_", null);
                    oneSxms.put("UPDATE_TIME_", null);
                    // 4. yqId这个字段要去字典中查
                    if (StringUtils.isNotBlank(oneSxms.getString("yqId"))) {
                        String yqId = reqMap.get(oneSxms.getString("yqId"));
                        if (StringUtils.isNotBlank(yqId)) {
                            oneSxms.put("yqId", yqId);
                        } else {
                            oneSxms.put("yqId", null);
                        }
                    } else {
                        oneSxms.put("yqId", null);
                    }
                }
                Map<String, Object> paraSxmsList = new HashMap<>();
                paraSxmsList.put("list", sxmsList);
                drbfmSingleDao.insertSxmsArray(paraSxmsList);
            }
        }





        // 根据ids查询
        return result;
    }

    /**
     * 导出一个或多个单一项目的信息
     *
     * @param response
     * @param request
     */
    public void exportSingle(HttpServletResponse response, HttpServletRequest request) {
        String filterParams = request.getParameter("filter");
        if (StringUtils.isBlank(filterParams)) {
            return;
        }
        Set<String> singleIds = new HashSet<>();
        JSONArray jsonArray = JSONArray.parseArray(filterParams);
        for (int i = 0; i < jsonArray.size(); i++) {
            String singleId = jsonArray.getString(i);
            singleIds.add(singleId);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("belongSingleIds", singleIds);
        List<JSONObject> quotaData = toQueryQuotaExportData(param);
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "部件风险分析列表";
        String excelName = nowDate + title;
        // 生成Excel数据
        HSSFWorkbook wbObj = exportQuotaExcelWB(quotaData);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    // 拼接要导出的数据
    private List<JSONObject> toQueryQuotaExportData(Map<String, Object> param) {
        // 查询指标和关联的要求的信息
        List<JSONObject> quotaList = drbfmSingleDao.getQuotaWithEvaluateList(param);
        // 关联的功能id
        Set<String> relFunctionIds = new HashSet<>();
        // 关联的要求中接口类对端部件id
        Set<String> interfaceRequestStructIds = new HashSet<>();
        // 零部件项目id
        Set<String> singleIds = new HashSet<>();
        // 指标id
        Set<String> quotaIds = new HashSet<>();
        for (JSONObject oneQuota : quotaList) {
            if (StringUtils.isNotBlank(oneQuota.getString("relFunctionId"))) {
                relFunctionIds.add(oneQuota.getString("relFunctionId"));
            }
            if (StringUtils.isNotBlank(oneQuota.getString("interfaceRequestStructId"))) {
                interfaceRequestStructIds.add(oneQuota.getString("interfaceRequestStructId"));
            }
            if (StringUtils.isNotBlank(oneQuota.getString("belongSingleId"))) {
                singleIds.add(oneQuota.getString("belongSingleId"));
            }
            quotaIds.add(oneQuota.getString("id"));
        }
        // 查找功能相关的信息
        Map<String, JSONObject> functionId2Obj = new HashMap<>();
        if (!relFunctionIds.isEmpty()) {
            Map<String, Object> paramTemp = new HashMap<>();
            paramTemp.put("relFunctionIds", relFunctionIds);
            List<JSONObject> functionObjs = drbfmSingleDao.queryFunctionForQuotaExport(paramTemp);
            if (functionObjs != null && !functionObjs.isEmpty()) {
                for (JSONObject oneData : functionObjs) {
                    functionId2Obj.put(oneData.getString("id"), oneData);
                }
            }
        }

        // 查找接口类部件名称
        Map<String, String> interfaceId2Name = new HashMap<>();
        if (!interfaceRequestStructIds.isEmpty()) {
            JSONObject paramTemp = new JSONObject();
            paramTemp.put("ids", interfaceRequestStructIds);
            List<JSONObject> structObjs = drbfmTotalDao.queryStructByParam(paramTemp);
            if (structObjs != null && !structObjs.isEmpty()) {
                for (JSONObject oneData : structObjs) {
                    interfaceId2Name.put(oneData.getString("id"), oneData.getString("structName"));
                }
            }
        }

        // 查找零部件基本信息和下一步工作
        Map<String, JSONObject> singleId2Obj = new HashMap<>();
        if (!singleIds.isEmpty()) {
            Map<String, Object> paramTemp = new HashMap<>();
            paramTemp.put("singleIds", singleIds);
            List<JSONObject> singleObjs = drbfmSingleDao.querySingleBaseForQuotaExport(paramTemp);
            if (singleObjs != null && !singleObjs.isEmpty()) {
                for (JSONObject oneData : singleObjs) {
                    String singleId = oneData.getString("id");
                    if (!singleId2Obj.containsKey(singleId)) {
                        singleId2Obj.put(singleId, new JSONObject());
                    }
                    singleId2Obj.get(singleId).put("structName", oneData.getString("structName"));
                    singleId2Obj.get(singleId).put("structType", oneData.getString("structType"));
                    singleId2Obj.get(singleId).put("jixing", oneData.getString("jixing"));
                    singleId2Obj.get(singleId).put("totalId", oneData.getString("totalId"));
                    if (StringUtils.isBlank(oneData.getString("nextWorkContent"))) {
                        continue;
                    }
                    if (!singleId2Obj.get(singleId).containsKey("nextWorkContent")) {
                        singleId2Obj.get(singleId).put("nextWorkContent", "");
                    }
                    String nextWorkContent = singleId2Obj.get(singleId).getString("nextWorkContent")
                            + oneData.getString("nextWorkContent") + "\n";
                    singleId2Obj.get(singleId).put("nextWorkContent", nextWorkContent);
                }
            }
        }

        // 查找指标关联的验证相关信息
        Map<String, List<JSONObject>> quotaId2TestObjs = new HashMap<>();
        if (!quotaIds.isEmpty()) {
            Map<String, Object> paramTemp = new HashMap<>();
            paramTemp.put("quotaIds", quotaIds);
            List<JSONObject> testObjs = drbfmTestTaskDao.queryTestInfoForQuotaExport(paramTemp);
            if (testObjs != null && !testObjs.isEmpty()) {
                for (JSONObject oneData : testObjs) {
                    String relQuotaId = oneData.getString("relQuotaId");
                    if (!quotaId2TestObjs.containsKey(relQuotaId)) {
                        quotaId2TestObjs.put(relQuotaId, new ArrayList<>());
                    }
                    quotaId2TestObjs.get(relQuotaId).add(oneData);
                }
            }
        }

        // 补充全部的信息
        for (JSONObject oneQuota : quotaList) {
            // 功能相关
            if (StringUtils.isNotBlank(oneQuota.getString("relFunctionId"))
                    && functionId2Obj.containsKey(oneQuota.getString("relFunctionId"))) {
                JSONObject functionObj = functionId2Obj.get(oneQuota.getString("relFunctionId"));
                oneQuota.put("functionDesc", functionObj.getString("functionDesc"));
                oneQuota.put("relDeptDemandId", functionObj.getString("relDeptDemandId"));
                oneQuota.put("demandDesc", functionObj.getString("demandDesc"));
            }
            // 对端接口
            if (StringUtils.isNotBlank(oneQuota.getString("interfaceRequestStructId"))
                    && interfaceId2Name.containsKey(oneQuota.getString("interfaceRequestStructId"))) {
                oneQuota.put("interfaceRequestStructName",
                        interfaceId2Name.get(oneQuota.getString("interfaceRequestStructId")));
            }
            if (StringUtils.isNotBlank(oneQuota.getString("belongSingleId"))
                    && singleId2Obj.containsKey(oneQuota.getString("belongSingleId"))) {
                JSONObject singleObj = singleId2Obj.get(oneQuota.getString("belongSingleId"));
                oneQuota.put("structName", singleObj.getString("structName"));
                oneQuota.put("structType", singleObj.getString("structType"));
                oneQuota.put("jixing", singleObj.getString("jixing"));
                oneQuota.put("totalId", singleObj.getString("totalId"));
                oneQuota.put("nextWorkContent", singleObj.getString("nextWorkContent"));
            }
            if (quotaId2TestObjs.containsKey(oneQuota.getString("id"))) {
                List<JSONObject> testObjList = quotaId2TestObjs.get(oneQuota.getString("id"));
                String abilityNames = "";
                String testContents = "";
                String quotaTestValues = "";
                String testResults = "";
                String nextWorkContentConcats = "";
                for (int index = 0; index < testObjList.size(); index++) {
                    JSONObject testObj = testObjList.get(index);
                    if (StringUtils.isNotBlank(testObj.getString("abilityName"))) {
                        abilityNames += (index + 1) + "、" + testObj.getString("abilityName") + "\n";
                    }
                    if (StringUtils.isNotBlank(testObj.getString("testContent"))) {
                        testContents += (index + 1) + "、" + testObj.getString("testContent") + "\n";
                    }
                    if (StringUtils.isNotBlank(testObj.getString("quotaTestValue"))) {
                        quotaTestValues += (index + 1) + "、" + testObj.getString("quotaTestValue") + "\n";
                    }
                    if (StringUtils.isNotBlank(testObj.getString("testResult"))) {
                        testResults += (index + 1) + "、" + testObj.getString("testResult") + "\n";
                    }
                    if (StringUtils.isNotBlank(testObj.getString("nextWorkContentConcat"))) {
                        nextWorkContentConcats += (index + 1) + "、" + testObj.getString("nextWorkContentConcat") + "\n";
                    }
                }
                oneQuota.put("abilityNames", abilityNames);
                oneQuota.put("testContents", testContents);
                oneQuota.put("quotaTestValues", quotaTestValues);
                oneQuota.put("testResults", testResults);
                oneQuota.put("nextWorkContentConcats", nextWorkContentConcats);
            }
        }
        // 补充标准名称
        queryStandardNamesForQuota(quotaList, false);
        // 按照excel要合并的列的顺序排序
        return quotaList;
    }

    // 导出excel
    private HSSFWorkbook exportQuotaExcelWB(List<JSONObject> quotaDataList) {
        List<String> fieldNames = Arrays.asList("机型", "部件名称", "零部件类型", "部门需求", "接口名称", "要求维度", "功能描述", "要求描述",
                "指标名称", "设计标准值", "较上一代产品变化点\n或相似产品变化点", "变化点带来的\n关联影响", "存在风险", "与竞品对比", "风险评级", "设计标准", "测试标准", "评价标准",
                "指标状态", "代替的旧指标", "验证方式", "验证内容", "指标实测值", "评判结果", "下一步工作", "综合验证结果", "验证结果说明", "不符合原因分析", "不符合改善对策",
                "验证改进后\n风险评级", "CTQ关键质量特性", "下一步工作");
        List<String> fieldCodes = Arrays.asList("jixing", "structName", "structType", "demandDesc",
                "interfaceRequestStructName", "relDimensionNames", "functionDesc", "relRequestDesc", "quotaName",
                "sjStandardValue", "requestChanges", "relEffect", "riskDesc", "compareToJP", "riskLevel", "sjStandardNames",
                "testStandardNames", "evaluateStandardNames", "validStatus", "replaceQuotaName", "abilityNames",
                "testContents", "quotaTestValues", "testResults", "nextWorkContentConcats", "overallResult", "resultDesc",
                "badReasonDesc", "badImproveDesc", "riskLevelFinal", "keyCTQ", "nextWorkContent");
        HSSFWorkbook wbObj = new HSSFWorkbook();
        HSSFSheet sheet = wbObj.createSheet();
        // 表头
        HSSFRow titleRow = sheet.createRow(0);
        titleRow.setHeight((short)600);
        HSSFCellStyle firstRowStyle = CommonExcelUtils.createFirstRowStyle(wbObj);
        firstRowStyle.setWrapText(true);
        HSSFCell cell = null;
        for (int i = 0; i < fieldNames.size(); i++) {
            cell = titleRow.createCell(i);
            cell.setCellStyle(firstRowStyle);
            cell.setCellValue(fieldNames.get(i));
        }
        // 设置每一列的自适应宽度
        for (int i = 0; i < fieldCodes.size(); i++) {
            String fieldCode = fieldCodes.get(i);
            String fieldName = fieldNames.get(i);
            if ("jixing".equalsIgnoreCase(fieldCode)) {
                sheet.setColumnWidth(i, 20 * 256);
            } else if ("structName".equalsIgnoreCase(fieldCode)) {
                sheet.setColumnWidth(i, 21 * 256);
            } else if ("demandDesc".equalsIgnoreCase(fieldCode)) {
                sheet.setColumnWidth(i, 26 * 256);
            } else if ("functionDesc".equalsIgnoreCase(fieldCode)) {
                sheet.setColumnWidth(i, 26 * 256);
            } else if ("relRequestDesc".equalsIgnoreCase(fieldCode)) {
                sheet.setColumnWidth(i, 30 * 256);
            } else if ("quotaName".equalsIgnoreCase(fieldCode)) {
                sheet.setColumnWidth(i, 24 * 256);
            } else if ("requestChanges".equalsIgnoreCase(fieldCode)) {
                sheet.setColumnWidth(i, 27 * 256);
            } else if ("relEffect".equalsIgnoreCase(fieldCode)) {
                sheet.setColumnWidth(i, 25 * 256);
            } else if ("riskDesc".equalsIgnoreCase(fieldCode)) {
                sheet.setColumnWidth(i, 23 * 256);
            } else if ("compareToJP".equalsIgnoreCase(fieldCode)) {
                sheet.setColumnWidth(i, 25 * 256);
            } else if ("sjStandardNames".equalsIgnoreCase(fieldCode) || "testStandardNames".equalsIgnoreCase(fieldCode)
                    || "evaluateStandardNames".equalsIgnoreCase(fieldCode)) {
                sheet.setColumnWidth(i, 25 * 256);
            } else if ("testContents".equalsIgnoreCase(fieldCode)) {
                sheet.setColumnWidth(i, 25 * 256);
            } else if ("nextWorkContentConcats".equalsIgnoreCase(fieldCode)) {
                sheet.setColumnWidth(i, 30 * 256);
            } else if ("nextWorkContent".equalsIgnoreCase(fieldCode)) {
                sheet.setColumnWidth(i, 30 * 256);
            } else {
                sheet.setColumnWidth(i, fieldName.getBytes().length * 256);
            }
        }
        if (quotaDataList == null || quotaDataList.isEmpty()) {
            return wbObj;
        }
        HSSFCellStyle cellStyle = wbObj.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 内容赋值
        for (int i = 0; i < quotaDataList.size(); i++) {
            JSONObject oneQuota = quotaDataList.get(i);
            HSSFRow row = sheet.createRow(i + 1);
            for (int j = 0; j < fieldCodes.size(); j++) {
                cell = row.createCell(j);
                cell.setCellStyle(cellStyle);
                String value = oneQuota.getString(fieldCodes.get(j));
                cell.setCellValue(value);
            }
        }
        // (根据需要合并单元格)
        if (quotaDataList.size() <= 1) {
            return wbObj;
        }
        // 要合并的列的位置
        int jixingColumnIndex = fieldCodes.indexOf("jixing");
        int structNameColumnIndex = fieldCodes.indexOf("structName");
        int structTypeColumnIndex = fieldCodes.indexOf("structType");
        int nextWorkContentColumnIndex = fieldCodes.indexOf("nextWorkContent");
        // 索引判断内容的变化
        int startTotalIndex = 0;
        int startSingleIndex = 0;
        for (int index = 1; index < quotaDataList.size(); index++) {
            String startTotal = quotaDataList.get(startTotalIndex).getString("totalId");
            String nowTotal = quotaDataList.get(index).getString("totalId");
            if (!startTotal.equalsIgnoreCase(nowTotal)) {
                // total的合并
                if ((index - 1) > startTotalIndex) {
                    sheet.addMergedRegionUnsafe(
                            new CellRangeAddress(startTotalIndex + 1, index - 1 + 1, jixingColumnIndex, jixingColumnIndex));
                }
                // total索引位置移动
                startTotalIndex = index;
            }
            String startSingle = quotaDataList.get(startSingleIndex).getString("belongSingleId");
            String nowSingle = quotaDataList.get(index).getString("belongSingleId");
            if (!startSingle.equalsIgnoreCase(nowSingle)) {
                // single的合并
                if ((index - 1) > startSingleIndex) {
                    sheet.addMergedRegionUnsafe(new CellRangeAddress(startSingleIndex + 1, index - 1 + 1,
                            structNameColumnIndex, structNameColumnIndex));
                    sheet.addMergedRegionUnsafe(new CellRangeAddress(startSingleIndex + 1, index - 1 + 1,
                            structTypeColumnIndex, structTypeColumnIndex));
                    sheet.addMergedRegionUnsafe(new CellRangeAddress(startSingleIndex + 1, index - 1 + 1,
                            nextWorkContentColumnIndex, nextWorkContentColumnIndex));
                }
                // single索引位置移动
                startSingleIndex = index;
            }
        }
        if (startTotalIndex != quotaDataList.size() - 1) {
            sheet.addMergedRegionUnsafe(new CellRangeAddress(startTotalIndex + 1, quotaDataList.size() - 1 + 1,
                    jixingColumnIndex, jixingColumnIndex));
        }
        if (startSingleIndex != quotaDataList.size() - 1) {
            sheet.addMergedRegionUnsafe(new CellRangeAddress(startSingleIndex + 1, quotaDataList.size() - 1 + 1,
                    structNameColumnIndex, structNameColumnIndex));
            sheet.addMergedRegionUnsafe(new CellRangeAddress(startSingleIndex + 1, quotaDataList.size() - 1 + 1,
                    structTypeColumnIndex, structTypeColumnIndex));
            sheet.addMergedRegionUnsafe(new CellRangeAddress(startSingleIndex + 1, quotaDataList.size() - 1 + 1,
                    nextWorkContentColumnIndex, nextWorkContentColumnIndex));
        }
        return wbObj;
    }

    public List<JSONObject> querySxmsList(JSONObject params) {
        List<JSONObject> demandList = drbfmSingleDao.querySxmsList(params);
        return demandList;
    }

    public List<JSONObject> queryRiskList(JSONObject params) {
        List<JSONObject> demandList = drbfmSingleDao.queryDimensionList(params);
        return demandList;
    }

//    public List<JSONObject> querySingleSxmsList(JSONObject params) {
//        // 需要展示向上向下关联是否完成 ，需要有flag
//        List<JSONObject> demandList = drbfmSingleDao.querySingleSxmsList(params);
//        // 要拼接向上关联的信息：部件名 功能和要求
//        List<JSONObject> sxms2SjList = drbfmSingleDao.querySxms2SjList(params);
//        // 名称字典
//        Map<String, String> sxms2SjbjMap = new HashMap<>();
//        Map<String, String> sxms2SjgnMap = new HashMap<>();
//        for (JSONObject oneObj : sxms2SjList) {
//            String sxmsName = oneObj.getString("sxmsName");
//            // 拼接了部件名称叫 sxyxAllName
//            // 要求名
//            String yqName = oneObj.getString("requestDesc");
//            String gnName = oneObj.getString("functionDesc");
//            String structName = oneObj.getString("structName");
//            if (!sxms2SjbjMap.containsKey(sxmsName)) {
//                sxms2SjbjMap.put(sxmsName, structName);
//            } else {
//                sxms2SjbjMap.put(sxmsName, sxms2SjbjMap.get(sxmsName) + "</br>" + structName);
//            }
//            if (!sxms2SjgnMap.containsKey(sxmsName)) {
//                sxms2SjgnMap.put(sxmsName, "功能:" + gnName + "</br>" + "要求:" + yqName);
//            } else {
//                sxms2SjgnMap.put(sxmsName,
//                    sxms2SjgnMap.get(sxmsName) + "</br>" + "功能:" + gnName + "</br>" + "要求:" + yqName);
//            }
//        }
//        for (JSONObject oneObj : demandList) {
//            String sxmsName = oneObj.getString("sxmsName");
//            oneObj.put("gnfxsj", sxms2SjgnMap.get(sxmsName));
//            oneObj.put("jgfxsj", sxms2SjbjMap.get(sxmsName));
//        }
//
//        return demandList;
//    }

    public void querySingleSxmsList(JSONObject params,JSONObject result) {
        // 需要展示向上向下关联是否完成 ，需要有flag
        List<JSONObject> demandList = drbfmSingleDao.querySingleSxmsList(params);
        if (demandList.size() == 0) {
            result.put("success", true);
            return;
        }
        for (JSONObject oneObj : demandList) {
            String gnfxgzys = oneObj.getString("gnfxgzys").replace("特性要求","</br>特性要求");
            String gnfxxj = oneObj.getString("gnfxxj").replace("特性要求","</br>特性要求");
            oneObj.put("gnfxgzys", gnfxgzys);
            oneObj.put("gnfxxj", gnfxxj);
        }

        // 要拼接向上关联的信息：部件名 功能和要求
        List<JSONObject> sxms2SjList = drbfmSingleDao.querySxms2SjList(params);
        // 名称字典
        Map<String, String> sxms2SjbjMap = new HashMap<>();
        Map<String, String> sxms2SjgnMap = new HashMap<>();
        for (JSONObject oneObj : sxms2SjList) {
            String sxmsName = oneObj.getString("sxmsName");
            // 拼接了部件名称叫 sxyxAllName
            // 要求名
            String yqName = oneObj.getString("requestDesc");
            String gnName = oneObj.getString("functionDesc");
            String structName = oneObj.getString("structName");
            if (!sxms2SjbjMap.containsKey(sxmsName)) {
                sxms2SjbjMap.put(sxmsName, structName);
            } else {
                sxms2SjbjMap.put(sxmsName, sxms2SjbjMap.get(sxmsName) + "</br></br>" + structName);
            }
            if (!sxms2SjgnMap.containsKey(sxmsName)) {
                sxms2SjgnMap.put(sxmsName, "功能:" + gnName + "</br>" + "特性要求:" + yqName);
            } else {
                sxms2SjgnMap.put(sxmsName,
                        sxms2SjgnMap.get(sxmsName) + "</br></br>" + "功能:" + gnName + "</br>" + "特性要求:" + yqName);
            }
        }
//        for (JSONObject oneObj : demandList) {
//            String sxmsName = oneObj.getString("sxmsName");
//            oneObj.put("gnfxsj", sxms2SjgnMap.get(sxmsName));
//            oneObj.put("jgfxsj", sxms2SjbjMap.get(sxmsName));
//        }
        List<JSONObject> mergeNum = new ArrayList<>();
        int startIdx = 0;
        // int endIdx = 0;
        // 遍历整个需求列表，1.拼接字符串 2.找到数组索引位置
        int rowNumber = 1;
        for (int idx = 0; idx < demandList.size(); idx++) {
            JSONObject startObj = demandList.get(startIdx);
            String startSxmsName = startObj.getString("sxmsName");
            JSONObject curObj = demandList.get(idx);
            String sxmsName = curObj.getString("sxmsName");
            if (sxms2SjgnMap.containsKey(sxmsName)) {
                curObj.put("gnfxsj", sxms2SjgnMap.get(sxmsName));
            }
            if (sxms2SjbjMap.containsKey(sxmsName)) {
                curObj.put("jgfxsj", sxms2SjbjMap.get(sxmsName));
            }
            if (sxmsName.equalsIgnoreCase(startSxmsName)) {
                curObj.put("rowNumber", rowNumber);
            } else {
                rowNumber += 1;
                curObj.put("rowNumber", rowNumber);
                if (idx - 1 > startIdx) {
                    JSONObject oneMergeNum = new JSONObject();
                    oneMergeNum.put("rowIndex", startIdx);
                    oneMergeNum.put("rowSpan", idx - startIdx);
                    mergeNum.add(oneMergeNum);
                }
                startIdx = idx;
            }

        }
        // 处理尾巴
        if (startIdx != demandList.size() - 1) {

            JSONObject oneMergeNum = new JSONObject();
            oneMergeNum.put("rowIndex", startIdx);
            oneMergeNum.put("rowSpan", demandList.size() - startIdx);
            mergeNum.add(oneMergeNum);
        }
        result.put("data", demandList);
        result.put("mergeNum", mergeNum);
        return ;
    }


    public List<JSONObject> querySingleChangeList(JSONObject params) {
        List<JSONObject> demandList = drbfmSingleDao.querySingleChangeList(params);

        for (int index = 0; index < demandList.size(); index++){
            String changeDetail = demandList.get(index).getString("changeDetail");
            if (changeDetail.equalsIgnoreCase("无"))
                demandList.remove(index);
        }

        return demandList;
    }


    public List<JSONObject> querySingleExpList(JSONObject params) {
        List<JSONObject> demandList = drbfmSingleDao.querySingleExpList(params);
        for (JSONObject gjll : demandList) {

            if (gjll.getDate("qhTime") != null) {
                gjll.put("qhTime", DateUtil.formatDate(gjll.getDate("qhTime"), "yyyy-MM-dd"));
            }
        }

        return demandList;
    }

    public void queryRiskAnalysisList(JSONObject params, JSONObject result) {
        // 这个要返回合并单元格的数组
        // todo 2 按失效模式排序
        List<JSONObject> mergeNum = new ArrayList<>();
        List<JSONObject> mergeYqNum = new ArrayList<>();

        List<JSONObject> demandListSoruce = drbfmSingleDao.queryRiskAnalysisList(params);
        if (demandListSoruce.size() == 0) {
            result.put("success", true);
            return;
        }
        List<JSONObject> demandList = addIsRelationChangePointList(params.getString("partId"), demandListSoruce);

        List<JSONObject> sxms2SxyxList = drbfmSingleDao.querySxms2SxyxList(params);

        // 名称字典
        Map<String, String> sxms2SxyxMap = new HashMap<>();
        for (JSONObject oneObj : sxms2SxyxList) {
            String sxmsName = oneObj.getString("sxmsName");
            // 拼接了部件名称叫 sxyxAllName
            String sxyxName = oneObj.getString("sxyxAllName");
            // String sxyxName = oneObj.getString("sxyxName");
            if (!sxms2SxyxMap.containsKey(sxmsName)) {
                sxms2SxyxMap.put(sxmsName, sxyxName);
            } else {
                sxms2SxyxMap.put(sxmsName, sxms2SxyxMap.get(sxmsName) + "</br>" + sxyxName);
            }
        }

        int startIdx = 0;
        int startYqIdx = 0;
        // int endIdx = 0;
        // 遍历整个需求列表，1.拼接字符串 2.找到数组索引位置
        for (int idx = 0; idx < demandList.size(); idx++) {
            JSONObject startObj = demandList.get(startIdx);
            JSONObject startYqObj = demandList.get(startYqIdx);
            String startSxmsName = startObj.getString("sxmsName");
            String startyqName = startYqObj.getString("requestDesc");
            JSONObject curObj = demandList.get(idx);
            String sxmsName = curObj.getString("sxmsName");
            String yqName = curObj.getString("requestDesc");
            if (sxms2SxyxMap.containsKey(sxmsName)) {
                curObj.put("sxyxName", sxms2SxyxMap.get(sxmsName));
            }
            if (sxmsName.equalsIgnoreCase(startSxmsName)) {
                ;
            } else {
                if (idx - 1 > startIdx) {
                    JSONObject oneMergeNum = new JSONObject();
                    oneMergeNum.put("rowIndex", startIdx);
                    oneMergeNum.put("rowSpan", idx - startIdx);
                    mergeNum.add(oneMergeNum);
                }
                startIdx = idx;
            }
            // 这里有空指针
            if (yqName.equalsIgnoreCase(startyqName)) {
                continue;
            } else {
                if (idx - 1 > startYqIdx) {
                    JSONObject oneMergeYqNum = new JSONObject();
                    oneMergeYqNum.put("rowIndex", startYqIdx);
                    oneMergeYqNum.put("rowSpan", idx - startYqIdx);
                    mergeYqNum.add(oneMergeYqNum);
                }
                startYqIdx = idx;
            }
        }
        // 处理尾巴
        if (startIdx != demandList.size() - 1) {
            JSONObject oneMergeNum = new JSONObject();
            oneMergeNum.put("rowIndex", startIdx);
            oneMergeNum.put("rowSpan", demandList.size() - startIdx);
            mergeNum.add(oneMergeNum);
        }
        if (startYqIdx != demandList.size() - 1) {
            JSONObject oneMergeYqNum = new JSONObject();
            oneMergeYqNum.put("rowIndex", startYqIdx);
            oneMergeYqNum.put("rowSpan", demandList.size() - startYqIdx);
            mergeYqNum.add(oneMergeYqNum);
        }

        result.put("data", demandList);
        result.put("mergeNum", mergeNum);
        result.put("mergeYqNum", mergeYqNum);
        // return demandList;
        return;
    }

    public List<JSONObject> getSxmsNetList(JSONObject params) {
        List<JSONObject> demandList = drbfmSingleDao.querySxmsNetList(params);
        return demandList;
    }


    public JsonPageResult<?> getModelSxmsList(JSONObject params) {
        JsonPageResult result = new JsonPageResult(true);
        List<JSONObject> demandList = drbfmSingleDao.queryModelSxmsList(params);
        result.setData(demandList);
        int countList = drbfmSingleDao.countModelSxmsList(params);
        result.setTotal(countList);
        return result;
    }

    public JsonPageResult<?> getSelectSxmsList(JSONObject params) {
        JsonPageResult result = new JsonPageResult(true);
        List<JSONObject> demandList = drbfmSingleDao.querySelectSxmsList(params);
        result.setData(demandList);
        int countList = drbfmSingleDao.countSelectSxmsList(params);
        result.setTotal(countList);
        return result;
    }

    public JsonResult createSxmsRel(JSONObject params, String relType) {
        JsonResult result = new JsonResult(true, "操作成功！");
        // 检查是否有这个关联，如果没有就添加

        //todo 查所有未关联到的，并且添加
        List<JSONObject> demandList = drbfmSingleDao.querySxmsRelList(params);
        Set<String> baseIdList = new HashSet<>();
        Set<String> relIdList = new HashSet<>();
        if (demandList.size() > 0) {
            for (JSONObject oneObj : demandList) {
                baseIdList.add(oneObj.get("baseId").toString());
                relIdList.add(oneObj.get("relId").toString());
            }
//            result.setMessage("已存在该失效模式（" + demandList.get(0).getString("sxmsName") + "）关联，请重新选择！");
//            return result;
        }
        // 创建关联
        params.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        params.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        if ("up".equalsIgnoreCase(relType)) {
            JSONArray baseIds = params.getJSONArray("baseIds");
            for (int i = 0; i < baseIds.size(); i++) {
                String baseId = baseIds.get(i).toString();
                if (baseIdList.contains(baseId)) {
                    continue;
                }
                params.put("baseId", baseId);
                params.put("id", IdUtil.getId());
                drbfmSingleDao.insertSxmsRel(params);
            }
        } else {
            JSONArray relIds = params.getJSONArray("relIds");
            for (int i = 0; i < relIds.size(); i++) {
                String relId = relIds.get(i).toString();
                if (relIdList.contains(relId)) {
                    continue;
                }
                params.put("relId", relId);
                params.put("id", IdUtil.getId());
                drbfmSingleDao.insertSxmsRel(params);
            }

        }

        return result;
    }

    public JsonResult deleteSxmsRel(JSONObject params) {
        JsonResult result = new JsonResult(true, "操作成功！");
        drbfmSingleDao.deleteSxmsRel(params);
        return result;
    }

    public JsonResult deleteSxyy(JSONObject params) {
        JsonResult result = new JsonResult(true, "操作成功！");
        drbfmSingleDao.deleteSxyy(params);
        // 删除失效原因的全部关联
        drbfmSingleDao.deleteSxyyRel(params);
        // 删除 失效原因上的分析数据
        drbfmSingleDao.deleteRiskAnalysis(params);

        return result;
    }

    public void insertRiskAnalysis(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        drbfmSingleDao.createRiskAnalysis(formData);

    }

    public void insertRiskAnalysisSxms(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        drbfmSingleDao.createRiskAnalysisSxms(formData);

    }

    public void updateRiskAnalysis(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        drbfmSingleDao.updateRiskAnalysis(formData);

    }

    public void updateRiskAnalysisSxms(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        // sxmsfxId 用这个当id
        drbfmSingleDao.updateRiskAnalysisSxms(formData);

    }

    public JSONObject getRiskAnalysisDetail(String sxmsId, String sxyyId,String partId) {
        // todo 查两次 拼一下
        JSONObject params = new JSONObject();
        params.put("sxmsId", sxmsId);
        params.put("sxyyId", sxyyId);

        JSONObject detailObj = drbfmSingleDao.queryRiskAnalysisDetail(params);
        if (detailObj == null) {
            JSONObject resObj = new JSONObject();
            if (StringUtils.isNotBlank(partId)) {
                params.put("partId", partId);
                JSONObject firstPc = drbfmSingleDao.queryFirstRiskAnalysisPc(params);
                if (firstPc == null) {
                    return resObj;
                }
                String pc = firstPc.getString("xxyfkz");
                if (StringUtils.isNotBlank(pc)) {
                    resObj.put("xxyfkz", pc);
                }
                String fc = firstPc.getString("tckzfc");
                if (StringUtils.isNotBlank(fc)) {
                    resObj.put("tckzfc", fc);
                }
                String fsd = firstPc.getString("fsd");
                if (StringUtils.isNotBlank(fsd)) {
                    resObj.put("fsd", fsd);
                }
                String tcdfc = firstPc.getString("tcdfc");
                if (StringUtils.isNotBlank(tcdfc)) {
                    resObj.put("tcdfc", tcdfc);
                }
            }
            return resObj;
        } else {
            if (StringUtils.isBlank(detailObj.getString("xxyfkz"))||StringUtils.isBlank(detailObj.getString("tckzfc"))
                    ||StringUtils.isBlank(detailObj.getString("tcdfc"))||StringUtils.isBlank(detailObj.getString("fsd"))) {
                if (StringUtils.isNotBlank(partId)) {
                    params.put("partId", partId);
                    JSONObject firstPc = drbfmSingleDao.queryFirstRiskAnalysisPc(params);
                    if (firstPc == null) {
                        return detailObj;
                    }
                    String pc = firstPc.getString("xxyfkz");
                    if (StringUtils.isNotBlank(pc)&&StringUtils.isBlank(detailObj.getString("xxyfkz"))) {
                        detailObj.put("xxyfkz", pc);
                    }
                    String fc = firstPc.getString("tckzfc");
                    if (StringUtils.isNotBlank(fc)&&StringUtils.isBlank(detailObj.getString("tckzfc"))) {
                        detailObj.put("tckzfc", fc);
                    }
                    String fsd = firstPc.getString("fsd");
                    if (StringUtils.isNotBlank(fsd)&&StringUtils.isBlank(detailObj.getString("fsd"))) {
                        detailObj.put("fsd", fsd);
                    }

                    String tcdfc = firstPc.getString("tcdfc");
                    if (StringUtils.isNotBlank(tcdfc)&&StringUtils.isBlank(detailObj.getString("tcdfc"))) {
                        detailObj.put("tcdfc", tcdfc);
                    }

                }
                return detailObj;
            }

        }
        return detailObj;

    }

    // 检查功能失效网是否关联完成，不允许有未关联的
    public JsonResult checkFunctionNetStatus(JSONObject params, String startOrEnd) {
        // todo 检查部件是否是开头和结尾
        JsonResult result = new JsonResult(true, "成功");
        // 需要展示向上向下关联是否完成 ，需要有flag
        List<JSONObject> demandList = drbfmSingleDao.querySingleSxmsList(params);
        for (JSONObject oneObj : demandList) {
            String upStatus = oneObj.getString("upRender");
            String downStatus = oneObj.getString("downRender");
            if ("start".equalsIgnoreCase(startOrEnd)) {
                if ("no".equalsIgnoreCase(downStatus)) {
                    result.setMessage(oneObj.getString("sxmsName") + "失效模式未完成关联，请检查后提交！");
                    result.setSuccess(false);
                    return result;
                }
            } else if ("end".equalsIgnoreCase(startOrEnd)) {
                if ("no".equalsIgnoreCase(upStatus)) {
                    result.setMessage(oneObj.getString("sxmsName") + "失效模式未完成关联，请检查后提交！");
                    result.setSuccess(false);
                    return result;
                }
            } else {
                if ("no".equalsIgnoreCase(upStatus) || "no".equalsIgnoreCase(downStatus)) {
                    result.setMessage(oneObj.getString("sxmsName") + "失效模式未完成关联，请检查后提交！");
                    result.setSuccess(false);
                    return result;
                }
            }
        }
        return result;
    }

    public JsonResult createExpRel(JSONObject params) {
        JsonResult result = new JsonResult(true, "操作成功！");

        List<JSONObject> demandList = drbfmSingleDao.querySingleExpList(params);
        Set<String> relIdList = new HashSet<>();
        if (demandList.size() > 0) {
            for (JSONObject oneObj : demandList) {
                relIdList.add(oneObj.get("gjId").toString());
            }
        }
        // 创建关联
        params.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        params.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        JSONArray relExpIds = params.getJSONArray("relExpIds");
        for (int i = 0; i < relExpIds.size(); i++) {
            String relExpId = relExpIds.get(i).toString();
            if (relIdList.contains(relExpId)) {
                continue;
            }
            params.put("relExpId", relExpId);
            params.put("id", IdUtil.getId());
            drbfmSingleDao.insertExpRel(params);
        }


        return result;
    }

    public JsonResult deleteExpRel(JSONObject params) {
        JsonResult result = new JsonResult(true, "操作成功！");
        drbfmSingleDao.deleteExpRel(params);
        return result;
    }


    // 经验教训模块附件上传
    public void saveExpFiles(HttpServletRequest request) {
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
            String belongSingleId = toGetParamVal(parameters.get("belongSingleId"));
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String filePathBase = SysPropertiesUtil.getGlobalProperty("drbfm");
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase");
                return;
            }
            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + belongSingleId;
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
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("belongSingleId", belongSingleId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            drbfmTestTaskDao.insertDemand(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveExpUploadFiles", e);
        }
    }

    // 文件列表查询
    public List<JSONObject> queryExpFileList(JSONObject params) {
        List<JSONObject> demandList = drbfmTestTaskDao.queryExpFileList(params);
        for (JSONObject demand : demandList) {
            if (demand.get("CREATE_TIME_") != null) {
                demand.put("CREATE_TIME_", DateUtil.formatDate((Date)demand.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return demandList;
    }

    // 文件删除
    public void deleteFile(String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileId = postBodyObj.getString("id");
        if (StringUtils.isBlank(fileId)) {
            return;
        }
        String fileName = postBodyObj.getString("fileName");
        String formId = postBodyObj.getString("formId");
        String fileBasePath = SysPropertiesUtil.getGlobalProperty("drbfm");
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, formId, fileBasePath);
        // 清除文件后,将表单中的文件信息置空
        JSONObject param = new JSONObject();
        param.put("id", fileId);
        drbfmTestTaskDao.deleteDemand(param);
    }

    public List<JSONObject> addIsRelationChangePointList(String belongId,List<JSONObject> txRequestList) {
        Map<String, Object> param = new HashMap<>();
        param.put("belongSingleId", belongId);
        List<JSONObject> relationChangePoint = drbfmSingleDao.getRealtionChangePointList(param);
        HashSet<String> relTxIds = new HashSet<>();
        HashSet<String> relSxIds = new HashSet<>();
        for (int i = 0; i < relationChangePoint.size(); i++) {
            relTxIds.add(relationChangePoint.get(i).getString("yqId"));
            relSxIds.add(relationChangePoint.get(i).getString("relSxmsId"));
        }
        relTxIds.remove(null);
        relSxIds.remove(null);
        if (relationChangePoint.size() != 0) {
            for (JSONObject oneObject:txRequestList) {
                if (relTxIds.contains(oneObject.get("id"))||relTxIds.contains(oneObject.get("yqId"))) {
                    oneObject.put("isRelChange", "yes");
                } else {
                    oneObject.put("isRelChange", "no");
                }
                if (relSxIds.contains(oneObject.get("sxmsId"))) {
                    oneObject.put("isRelSxms", "yes");
                } else {
                    oneObject.put("isRelSxms", "no");
                }
            }
        }
        return txRequestList;
    }

    public JsonPageResult<?> getRequestAndSxmsList(JSONObject params) {
        JsonPageResult result = new JsonPageResult(true);
        List<JSONObject> demandList = drbfmSingleDao.getRequestAndSxmsList(params);
        result.setData(demandList);
        int countList = drbfmSingleDao.countRequestAndSxmsList(params);
        result.setTotal(countList);
        return result;
    }
}
