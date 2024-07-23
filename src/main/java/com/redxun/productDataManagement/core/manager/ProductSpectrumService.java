package com.redxun.productDataManagement.core.manager;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.BpmSolution;
import com.redxun.bpm.core.entity.ProcessMessage;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmSolutionManager;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.*;
import com.redxun.core.util.DateUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.productDataManagement.core.dao.ProductSpectrumDao;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 产品型谱
 *
 * @author mh
 * @date 2022年10月13日10:02:28
 */

@Service
public class ProductSpectrumService {
    private static Logger logger = LoggerFactory.getLogger(ProductSpectrumService.class);
    @Autowired
    private ProductSpectrumDao productSpectrumDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private UserService userService;
    @Resource
    private BpmSolutionManager bpmSolutionManager;
    @Autowired
    private BpmInstManager bpmInstManager;

    // ..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = null;
        int businessListCount;
        String region = request.getParameter("menuType");
        if (StringUtils.isNotEmpty(region)) {
            params.put("region", region);
        }
        businessList = productSpectrumDao.dataListQuery(params);
        businessListCount = productSpectrumDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    public JsonPageResult<?> queryApplyList(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "ps.CREATE_TIME_", "desc");
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                if (name.equalsIgnoreCase("tagIds") || name.equalsIgnoreCase("tagNamess")
                    || name.equalsIgnoreCase("selectModel")) {
                    String valueStr = jsonArray.getJSONObject(i).getString("value");
                    if (StringUtils.isNotBlank(valueStr)) {
                        String[] valueList = valueStr.split(",");
                        List<String> value = Arrays.asList(valueList);
                        params.put(name, value);
                    }
                } else {
                    String value = jsonArray.getJSONObject(i).getString("value");
                    if (StringUtils.isNotBlank(value)) {
                        params.put(name, value);
                    }
                }
                // String value = jsonArray.getJSONObject(i).getString("value");
                //
                // if (StringUtils.isNotBlank(value)) {
                // params.put(name, value);
                // }
            }
        }
        // getListParams(params, request);

        // 增加角色过滤的条件
        // 2023年3月22日09:15:27 黄建林 由于需要看全部草稿状态，这个筛选条件暂时关闭
        // addRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());

        List<JSONObject> applyList = productSpectrumDao.dataListQuery(params);
        for (JSONObject oneApply : applyList) {
            if (oneApply.get("CREATE_TIME_") != null) {
                oneApply.put("CREATE_TIME_", DateUtil.formatDate((Date)oneApply.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (oneApply.get("applyTime") != null) {
                oneApply.put("applyTime", DateUtil.formatDate((Date)oneApply.get("applyTime"), "yyyy-MM-dd HH:mm:ss"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(applyList, ContextUtil.getCurrentUserId());

        // 根据分页进行subList截取
        List<JSONObject> finalSubList = new ArrayList<JSONObject>();
        if (doPage) {
            int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
            int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
            int startSubListIndex = pageIndex * pageSize;
            int endSubListIndex = startSubListIndex + pageSize;
            if (startSubListIndex < applyList.size()) {
                finalSubList = applyList.subList(startSubListIndex,
                    endSubListIndex < applyList.size() ? endSubListIndex : applyList.size());
            }
        } else {
            finalSubList = applyList;
        }
        result.setData(finalSubList);
        result.setTotal(applyList.size());
        return result;
    }

    private void addRoleParam(Map<String, Object> params, String userId, String userNo) {
        if (userNo.equalsIgnoreCase("admin")) {
            return;
        }
        params.put("currentUserId", userId);
        params.put("currentUserName", ContextUtil.getCurrentUser().getFullname());
        params.put("roleName", "other");
    }

    public JsonPageResult<?> tagListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        params.put("type", "CPXPBQ");
        List<JSONObject> businessList = null;
        int businessListCount;
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

        businessList = productSpectrumDao.tagListQuery(params);
        businessListCount = businessList.size();
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    // ..

    // ..

    // ..
    private void getListParams(Map<String, Object> params, HttpServletRequest request) {
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
                // 这里对tagIds和tagNames做一下类型转换 改成list类型，用于筛选的时候遍历
                if (name.equalsIgnoreCase("tagIds") || name.equalsIgnoreCase("tagNamess")) {
                    String valueStr = jsonArray.getJSONObject(i).getString("value");
                    if (StringUtils.isNotBlank(valueStr)) {
                        String[] valueList = valueStr.split(",");
                        List<String> value = Arrays.asList(valueList);
                        params.put(name, value);
                    }
                } else {
                    String value = jsonArray.getJSONObject(i).getString("value");
                    if (StringUtils.isNotBlank(value)) {
                        params.put(name, value);
                    }
                }

            }
        }

        // 增加分页条件
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
    }

    // ..

    // ..
    public JSONObject queryDataById(String id) {
        JSONObject jsonObject = productSpectrumDao.queryDataById(id);
        return jsonObject;
    }

    // ..
    public void deleteBusiness(JSONObject result, String id) {
        try {
            productSpectrumDao.deleteBusiness(id);
            result.put("message", "删除成功！");
        } catch (Exception e) {
            logger.error("Exception in delete", e);
            result.put("message", "系统异常！");
        }
    }

    // ..
    private void monthStatusProcess(String applyId, JSONArray demandArr) {
        if (demandArr == null || demandArr.isEmpty()) {
            return;
        }
        JSONObject param = new JSONObject();
        param.put("ids", new JSONArray());
        for (int index = 0; index < demandArr.size(); index++) {
            JSONObject oneObject = demandArr.getJSONObject(index);
            String id = oneObject.getString("id");
            String state = oneObject.getString("_state");
            if ("added".equals(state) || StringUtils.isBlank(state) || StringUtils.isBlank(id)) {
                // 新增 复制时，会把主表id清空，并把子表的各id也清空，故都会走这里新增
                oneObject.put("id", IdUtil.getId());
                oneObject.put("applyId", applyId);
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                productSpectrumDao.insertMonthStatus(oneObject);
            } else if ("modified".equals(state)) {
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", new Date());
                productSpectrumDao.updateMonthStatus(oneObject);
            } else if ("removed".equals(state)) {
                // 删除
                param.getJSONArray("ids").add(oneObject.getString("id"));
            }
        }
        if (!param.getJSONArray("ids").isEmpty()) {
            productSpectrumDao.deleteMonthStatus(param);
        }
    }

    private void mainSettingProcess(String applyId, JSONArray demandArr) {
        if (demandArr == null || demandArr.isEmpty()) {
            return;
        }
        JSONObject param = new JSONObject();
        param.put("ids", new JSONArray());
        for (int index = 0; index < demandArr.size(); index++) {
            JSONObject oneObject = demandArr.getJSONObject(index);
            String id = oneObject.getString("id");
            String state = oneObject.getString("_state");
            if ("added".equals(state) || StringUtils.isBlank(state) || StringUtils.isBlank(id)) {
                oneObject.put("id", IdUtil.getId());
                oneObject.put("applyId", applyId);
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                productSpectrumDao.insertMainSetting(oneObject);
            } else if ("modified".equals(state)) {
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", new Date());
                productSpectrumDao.updateMainSetting(oneObject);
            } else if ("removed".equals(state)) {
                // 删除
                param.getJSONArray("ids").add(oneObject.getString("id"));
            }
        }
        if (!param.getJSONArray("ids").isEmpty()) {
            productSpectrumDao.deleteMainSetting(param);
        }
    }

    // ..

    private void mainParamProcess(String applyId, JSONArray demandArr) {
        if (demandArr == null || demandArr.isEmpty()) {
            return;
        }
        JSONObject param = new JSONObject();
        param.put("ids", new JSONArray());
        for (int index = 0; index < demandArr.size(); index++) {
            JSONObject oneObject = demandArr.getJSONObject(index);
            String id = oneObject.getString("id");
            String state = oneObject.getString("_state");
            if ("added".equals(state) || StringUtils.isBlank(state) || StringUtils.isBlank(id)) {
                oneObject.put("id", IdUtil.getId());
                oneObject.put("applyId", applyId);
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                productSpectrumDao.insertMainParam(oneObject);
            } else if ("modified".equals(state)) {
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", new Date());
                productSpectrumDao.updateMainParam(oneObject);
            } else if ("removed".equals(state)) {
                // 删除
                param.getJSONArray("ids").add(oneObject.getString("id"));
            }
        }
        if (!param.getJSONArray("ids").isEmpty()) {
            productSpectrumDao.deleteMainParam(param);
        }
    }

    private void manualChangeProcess(String applyId, JSONArray demandArr) {
        if (demandArr == null || demandArr.isEmpty()) {
            return;
        }
        JSONObject param = new JSONObject();
        param.put("ids", new JSONArray());
        for (int index = 0; index < demandArr.size(); index++) {
            JSONObject oneObject = demandArr.getJSONObject(index);
            String id = oneObject.getString("id");
            String state = oneObject.getString("_state");
            if ("added".equals(state) || StringUtils.isBlank(state) || StringUtils.isBlank(id)) {
                oneObject.put("id", IdUtil.getId());
                oneObject.put("applyId", applyId);
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                productSpectrumDao.insertManualChange(oneObject);
            } else if ("modified".equals(state)) {
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", new Date());
                productSpectrumDao.updateManualChange(oneObject);
            } else if ("removed".equals(state)) {
                // 删除
                param.getJSONArray("ids").add(oneObject.getString("id"));
            }
        }
        if (!param.getJSONArray("ids").isEmpty()) {
            productSpectrumDao.deleteManualChange(param);
        }
    }

    private void workDeviceProcess(String applyId, JSONArray demandArr) {
        if (demandArr == null || demandArr.isEmpty()) {
            return;
        }
        JSONObject param = new JSONObject();
        param.put("ids", new JSONArray());
        for (int index = 0; index < demandArr.size(); index++) {
            JSONObject oneObject = demandArr.getJSONObject(index);
            String id = oneObject.getString("id");
            String state = oneObject.getString("_state");
            if ("added".equals(state) || StringUtils.isBlank(state) || StringUtils.isBlank(id)) {
                oneObject.put("id", IdUtil.getId());
                oneObject.put("applyId", applyId);
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                productSpectrumDao.insertWorkDevice(oneObject);
            } else if ("modified".equals(state)) {
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", new Date());
                productSpectrumDao.updateWorkDevice(oneObject);
            } else if ("removed".equals(state)) {
                // 删除
                param.getJSONArray("ids").add(oneObject.getString("id"));
            }
        }
        if (!param.getJSONArray("ids").isEmpty()) {
            productSpectrumDao.deleteWorkDevice(param);
        }
    }

    // ..

    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        // 这个导出不分页

        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = null;
        String region = request.getParameter("menuType");
        if (StringUtils.isNotEmpty(region)) {
            params.put("region", region);
        }

        businessList = productSpectrumDao.dataListExport(params);

        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "产品型谱";
        String excelName = nowDate + title;
        String[] fieldNames = {"物料号", "设计型号", "产品所", "产品主管", "销售型号", "研发状态", "产品说明", "产品标签", "制造状态", "销售状态", "规划内外销售",
            "规划销售区域或国外", "可售状态"};
        String[] fieldCodes = {"materialCode", "designModel", "departName", "productManagerName", "saleModel",
            "rdStatus", "productNotes", "tagNames", "manuStatus", "saleStatus", "abroad", "region", "saleStatus"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(businessList, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);

    }

    public void exportMainSetting(HttpServletRequest request, HttpServletResponse response) {
        // 这个导出不分页

        List<JSONObject> businessList = null;
        String applyId = RequestUtil.getString(request, "applyId", "");
        JSONObject params = new JSONObject();
        params.put("applyId", applyId);
        businessList = productSpectrumDao.queryMainSettingList(params);

        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "产品型谱主要设置";
        String excelName = nowDate + title;
        String[] fieldNames = {"一级类别", "类别", "物料编码", "型号", "供应商名称", "是否选配", "备注"};
        String[] fieldCodes = {"settingTypeClass", "settingType", "settingMaterialCode", "settingModel", "supplyName",
            "sfxp", "settingRemark"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(businessList, fieldNames, fieldCodes, title);
        // 输出
        wbObj.setSheetName(0, "模板");
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);

    }

    public void exportMainParam(HttpServletRequest request, HttpServletResponse response) {
        // 这个导出不分页

        List<JSONObject> businessList = null;
        String applyId = RequestUtil.getString(request, "applyId", "");
        JSONObject params = new JSONObject();
        params.put("applyId", applyId);
        businessList = productSpectrumDao.queryMainParamList(params);

        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "产品型谱主要参数";
        String excelName = nowDate + title;
        String[] fieldNames = {"一级类别", "参数类型", "参数单位", "参数值", "备注"};
        String[] fieldCodes = {"paramTypeClass", "paramType", "paramUnit", "paramValue", "remark"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(businessList, fieldNames, fieldCodes, title);
        // 输出
        wbObj.setSheetName(0, "模板");
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);

    }

    public List<JSONObject> queryMonthStatusList(JSONObject params) {
        List<JSONObject> demandList = productSpectrumDao.queryMonthStatusList(params);
        return demandList;
    }

    public List<JSONObject> queryMainSettingList(JSONObject params) {
        List<JSONObject> demandList = productSpectrumDao.queryMainSettingList(params);
        return demandList;
    }

    public List<JSONObject> queryMainParamList(JSONObject params) {
        List<JSONObject> demandList = productSpectrumDao.queryMainParamList(params);
        return demandList;
    }

    public List<JSONObject> queryManualChangeList(JSONObject params) {
        List<JSONObject> demandList = productSpectrumDao.queryManualChangeList(params);
        return demandList;
    }

    public List<JSONObject> queryWorkDeviceList(JSONObject params) {
        List<JSONObject> demandList = productSpectrumDao.queryWorkDeviceList(params);
        return demandList;
    }

    public List<JSONObject> tagListGrid(JSONObject params) {
        List<JSONObject> resList = new ArrayList<>();
        List<JSONObject> demandList = productSpectrumDao.tagListQuery(params);
        // @mh 用id代替子表
        // 重新组织标签，把相同类别的放在一行，标签名称逗号分割
        if (demandList.size() > 0) {
            // 当前类别
            String currentType = demandList.get(0).get("key_").toString();
            String currentText = "";
            String currentId = "";
            // 遍历列表
            for (JSONObject demand : demandList) {
                // 与当前类别相同
                if (currentType.equalsIgnoreCase(demand.get("key_").toString())) {
                    currentText += demand.get("text").toString() + "，";
                    currentId += demand.get("id").toString() + ",";
                } else {
                    JSONObject res = new JSONObject();
                    res.put("tagType", currentType);
                    res.put("tagName", currentText.substring(0, currentText.length() - 1));
                    res.put("tagId", currentId.substring(0, currentId.length() - 1));
                    resList.add(res);
                    currentType = demand.get("key_").toString();
                    currentText = demand.get("text").toString() + "，";
                    currentId = demand.get("id").toString() + ",";
                }
            }
            JSONObject lastRes = new JSONObject();
            lastRes.put("tagType", currentType);
            lastRes.put("tagName", currentText.substring(0, currentText.length() - 1));
            lastRes.put("tagId", currentId.substring(0, currentId.length() - 1));
            resList.add(lastRes);
        }
        return resList;
    }

    // pdm接口
    public List<JSONObject> getPdmInfoList(JSONObject params) {
        String productModel = params.getString("productModel");
        try {
            String pdmStructdocInfoUrl = SysPropertiesUtil.getGlobalProperty("pdmStructdocInfo");
            if (StringUtils.isBlank(pdmStructdocInfoUrl)) {
                logger.error("未找到请求结构化信息的url配置");
                return null;
            }
            pdmStructdocInfoUrl += productModel;
            // @mh 2023年1月27日 增加身份验证
            String user = "CN_WJ_RDM";
            String pwd = "IMktH72CTgFt";
            Map<String, String> reqHeaders = new HashMap<>();
            reqHeaders.put("Authorization",
                "Basic " + Base64.getUrlEncoder().encodeToString((user + ":" + pwd).getBytes()));
            reqHeaders.put("Accept", "application/json");
            HttpClientUtil.HttpRtnModel httpRtnModel =
                HttpClientUtil.getFromUrlHreader(pdmStructdocInfoUrl, reqHeaders);
            if (httpRtnModel.getStatusCode() == 200) {
                JSONObject dataFromTis = JSONObject.parseObject(httpRtnModel.getContent()).getJSONObject("msg");
                List<JSONObject> resList = JSONObject.parseArray(dataFromTis.getString("resultData"), JSONObject.class);
                for (JSONObject res : resList) {
                    if (res.get("docCreateTime") != null) {
                        res.put("docCreateTime", DateUtil.formatDate(res.getDate("docCreateTime"), "yyyy-MM-dd"));
                    }
                    // 这里加上请求crm
                    // 以JSTZ为开始的要请求CRM获取整改进度
                    // String jstzNO = res.get("number").toString();
                    // if (jstzNO.startsWith("JSTZ")) {
                    // String zgProcessStr = getZgProcessStrFromCrm(jstzNO);
                    // res.put("zgProgress", zgProcessStr);
                    // }
                }
                return resList;
            } else {
                logger.info(productModel + "型号产品pdm接口返回值不为200");
                return null;
            }
        } catch (Exception e) {
            logger.error(productModel + "型号产品pdm接口调用错误", e);
            return null;
        }
    }

    public void getZgProcessFromCrm(JsonResult jsonResult, JSONObject params) {
        String jstzNo = params.getString("jstzNo");
        try {
            // 构建请求体
            String spectrumToCrmUrl =
                sysDicManager.getBySysTreeKeyAndDicKey("cpxpApiUrls", "zgProcessCrmUrl").getValue();
            if (StringUtils.isBlank(spectrumToCrmUrl)) {
                jsonResult.setSuccess(false);
                jsonResult.setMessage("crm接口调用失败，回调url为空，请联系管理员！");
                return;
            }
            // 参数1.时间戳用于校验 2.技术通知单号
            JSONObject requestBody = new JSONObject();
            requestBody.put("timeStamp", System.currentTimeMillis() / 1000);
            requestBody.put("jstzNo", jstzNo);
            Map<String, String> reqHeaders = new HashMap<>();
            reqHeaders.put("Content-Type", "application/json;charset=utf-8");
            String rtnContent = HttpClientUtil.postJson(spectrumToCrmUrl, requestBody.toJSONString(), reqHeaders);
            logger.info("技术通知单号："+jstzNo+"response from crm,return message:" + rtnContent);
            if (StringUtils.isBlank(rtnContent)) {
                jsonResult.setSuccess(false);
                jsonResult.setMessage("CRM系统返回值为空！");
                return;
            }
            JSONObject returnObj = JSONObject.parseObject(rtnContent);
            if ("0".equalsIgnoreCase(returnObj.getJSONObject("Data").getString("isok"))) {
                jsonResult.setSuccess(false);
                // 2023年3月21日16:38:35 查询失败不用显示信息，直接置空
                jsonResult.setMessage("");
            }
            if ("1".equalsIgnoreCase(returnObj.getJSONObject("Data").getString("isok"))) {
                jsonResult.setSuccess(true);
                int allCnt = returnObj.getJSONObject("Data").getIntValue("planSelectNum")
                    + returnObj.getJSONObject("Data").getIntValue("planApprovalNum")
                    + returnObj.getJSONObject("Data").getIntValue("planExecutingNum")
                    + returnObj.getJSONObject("Data").getIntValue("planCompleteNum");
                String resDisplay = returnObj.getJSONObject("Data").getString("planCompleteNum") + "/" + allCnt;
                jsonResult.setData(resDisplay);
            }
            return;

        } catch (Exception e) {
            logger.error(jstzNo + "crm接口调用错误", e);
            jsonResult.setSuccess(false);
            jsonResult.setMessage("查询失败," + jstzNo + "crm接口调用错误");
            return;
        }
    }

    // 从无流程改到有流程
    // 创建入口在产品型号申请流程结束后，cpxhsq中要初始化表单数据和子表的默认数据
    public void createSpectrum(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("creatorDeptId", ContextUtil.getCurrentUser().getMainGroupId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        // 基本信息
        productSpectrumDao.insertBusiness(formData);
        // 子表处理
        monthStatusProcess(formData.getString("id"), formData.getJSONArray("monthGrid"));
        mainSettingProcess(formData.getString("id"), formData.getJSONArray("mainSettingGrid"));
        mainParamProcess(formData.getString("id"), formData.getJSONArray("mainParamGrid"));
        manualChangeProcess(formData.getString("id"), formData.getJSONArray("manualChangeGrid"));
        workDeviceProcess(formData.getString("id"), formData.getJSONArray("deviceGrid"));

    }

    // 这个要有子表
    public void updateSpectrum(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        // 基本信息
        productSpectrumDao.updateBusiness(formData);

        // 子表处理
        monthStatusProcess(formData.getString("id"), formData.getJSONArray("monthGrid"));
        mainSettingProcess(formData.getString("id"), formData.getJSONArray("mainSettingGrid"));
        mainParamProcess(formData.getString("id"), formData.getJSONArray("mainParamGrid"));
        manualChangeProcess(formData.getString("id"), formData.getJSONArray("manualChangeGrid"));
        workDeviceProcess(formData.getString("id"), formData.getJSONArray("deviceGrid"));
    }

    public JsonResult delApplys(String[] applyIdArr) {
        List<String> applyIdList = Arrays.asList(applyIdArr);
        JsonResult result = new JsonResult(true, "操作成功！");
        if (applyIdArr.length == 0) {
            return result;
        }
        JSONObject param = new JSONObject();
        param.put("applyIds", applyIdList);

        // 删除主表
        param.put("ids", applyIdList);
        productSpectrumDao.deleteSpectrum(param);

        // 删四个子表
        JSONObject params = new JSONObject();
        params.put("applyIds", applyIdList);
        productSpectrumDao.deleteMainParam(params);
        productSpectrumDao.deleteMainSetting(params);
        productSpectrumDao.deleteManualChange(params);
        productSpectrumDao.deleteMonthStatus(params);
        productSpectrumDao.deleteWorkDevice(params);
        return result;
    }

    // @mh 2023年5月17日10:42:59 这个是台账转流程时专用的，后续可以注释掉或删除，也可以留做其他地方参考
    public void createProcessDraft(HttpServletRequest request, HttpServletResponse response) {
        Integer num = RequestUtil.getInt(request, "num", 1);
        IUser user = userService.getByUserId(ContextUtil.getCurrentUserId());
        ContextUtil.setCurUser(user);
        // 查找solution
        BpmSolution bpmSol = bpmSolutionManager.getByKey("CPXPFB", "1");
        String solId = "";
        if (bpmSol != null) {
            solId = bpmSol.getSolId();
        }
        for (int i = 0; i < num; i++) {
            ProcessMessage handleMessage = new ProcessMessage();
            try {
                ProcessHandleHelper.setProcessMessage(handleMessage);
                ProcessStartCmd startCmd = new ProcessStartCmd();
                startCmd.setSolId(solId);
                JSONObject formData = new JSONObject();
                JSONArray varsArr = new JSONArray();
                formData.put("vars", varsArr);
                formData.put("bos", new JSONArray());
                formData.put("test", "test");
                startCmd.setJsonData(formData.toJSONString());
                // 启动流程【草稿】
                bpmInstManager.doSaveDraft(startCmd);
            } catch (Exception ex) {
                // 把具体的错误放置在内部处理，以显示正确的错误信息提示，在此不作任何的错误处理
                logger.error(ExceptionUtil.getExceptionMessage(ex));

            }
        }
    }

    // 查找materialCode相同或者designModel相同的，并且id不等于参数id的，是否已存在
    public boolean checkDesignModelExist(JSONObject params) {
        String designModel = params.getString("designModel");
        String materialCode = params.getString("materialCode");
        if (StringUtils.isBlank(designModel) && StringUtils.isBlank(materialCode)) {
            // 不能都为空，否则认为有重复的
            return true;
        }
        boolean exist = false;
        try {
            if (StringUtils.isNotBlank(designModel) && StringUtils.isBlank(materialCode)) {
                params.put("flag", "designModel");
            }
            if (StringUtils.isNotBlank(materialCode) && StringUtils.isBlank(designModel)) {
                params.put("flag", "materialCode");
            }
            if (StringUtils.isNotBlank(designModel) && StringUtils.isNotBlank(materialCode)) {
                params.put("flag", "designModel_materialCode");
            }
            List<JSONObject> res = productSpectrumDao.checkDesignModelExist(params);
            if (res.size() > 0) {
                exist = true;
            }
        } catch (Exception e) {
            logger.error("Exception in checkDesignModelExist", e);
            // 异常情况直接不让他创建了返回true
            return true;
        }

        return exist;
    }

    // 批量导入主要参数表
    public void batchImportMainParam(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            MultipartFile fileObj = multipartRequest.getFile("importFile");
            if (fileObj == null) {
                result.put("success", false);
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            Map<String, String[]> parameters = request.getParameterMap();
            if (parameters == null || parameters.isEmpty()) {
                result.put("success", false);
                logger.warn("没有找到上传的参数");
                return;
            }
            String fileName = ((CommonsMultipartFile)fileObj).getFileItem().getName();
            // todo 获取主表id
            // String applyId = "";
            String applyId = parameters.get("applyId")[0];
            String paramType = parameters.get("paramType")[0];
            if (StringUtils.isBlank(paramType)) {
                result.put("success", false);
                result.put("message", "数据导入失败，导入模板类型为空！");
                return;
            }
            Workbook wb = null;
            if (fileName.endsWith(ExcelReaderUtil.EXCEL03_EXTENSION)) {
                wb = new HSSFWorkbook(fileObj.getInputStream());
            } else if (fileName.endsWith(ExcelReaderUtil.EXCEL07_EXTENSION)) {
                wb = new XSSFWorkbook(fileObj.getInputStream());
            }
            importMainParam(wb, applyId, paramType, result);

        } catch (Exception e) {
            logger.error("Exception in batchImportMainParam", e);
            result.put("success", false);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    private void importMainParam(Workbook wb, String applyId, String paramType, JSONObject result) {

        // 获取页签
        // Sheet sheet = wb.getSheet("Sheet0");
        Sheet sheet = wb.getSheet("模板");
        if (sheet == null) {
            logger.error("找不到导入页");
            result.put("message", "数据导入失败，找不到导入页！");
            return;
        }
        int rowNum = sheet.getPhysicalNumberOfRows();
        if (rowNum < 2) {
            logger.error("找不到标题行");
            result.put("message", "数据导入失败，找不到标题行！");
            return;
        }

        // 解析标题部分
        Row titleRow = sheet.getRow(1);
        if (titleRow == null) {
            logger.error("找不到标题行");
            result.put("message", "数据导入失败，找不到标题行！");
            return;
        }
        List<String> titleList = new ArrayList<>();
        for (int i = 0; i < titleRow.getLastCellNum(); i++) {
            Cell titleName = titleRow.getCell(i);
            if (titleName != null && StringUtils.isNotBlank(titleName.getStringCellValue())) {
                titleList.add(StringUtils.trim(titleName.getStringCellValue()));
            }
        }

        if (rowNum < 3) {
            logger.info("数据行为空");
            result.put("message", "数据导入失败，数据行为空！");
            return;
        }

        List<JSONObject> itemList = new ArrayList<>();
        // 读取每一行的数据
        StringBuilder resMessage = new StringBuilder();
        Boolean dataErrorFlag = false;
        for (int i = 2; i < rowNum; i++) {
            Row row = sheet.getRow(i);
            JSONObject rowParse = generateDataFromMainParam(row, itemList, titleList, applyId);
            if (!rowParse.getBoolean("result")) {
                // 这里改拼接字符串
                dataErrorFlag = true;
                resMessage.append("数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message")).append("</br>");
            }
        }

        if (dataErrorFlag) {
            // 把最后一个换行符删掉
            resMessage.delete(resMessage.length() - 5, resMessage.length());
            result.put("success", false);
            result.put("message", resMessage.toString());
            return;
        }
        // 校验1:行数 2：前两行是否和配置项一样

        // 校验1:行数 2：导入数据的类目是否在字典中

        // 获取默认参数表
        JSONObject mainParams = new JSONObject();
        if ("轮挖".equalsIgnoreCase(paramType)) {
            mainParams.put("type", "mainParamLW");
        } else if ("中大挖".equalsIgnoreCase(paramType)) {
            mainParams.put("type", "mainParamZDW");
        } else if ("微小挖".equalsIgnoreCase(paramType)) {
            mainParams.put("type", "mainParamWXW");
        } else {
            result.put("success", false);
            result.put("message", "模板类型错误，请联系管理员!");
            return;
        }
        List<JSONObject> mainParamList = productSpectrumDao.paramListQuery(mainParams);
        Map<String, List<String>> mainParamMap = new HashMap<>();
        for (JSONObject oneSetting : mainParamList) {
            // 遍历主要参数表,如果一级类别在map中不存在，新建，已存在，添加到列表
            if (mainParamMap.containsKey(oneSetting.getString("paramTypeClass"))) {
                mainParamMap.get(oneSetting.getString("paramTypeClass")).add(oneSetting.getString("paramType"));
            } else {
                List<String> listObj = new ArrayList<>();
                listObj.add(oneSetting.getString("paramType"));
                mainParamMap.put(oneSetting.getString("paramTypeClass"), listObj);
            }
        }

        for (JSONObject oneObj : itemList) {
            if (mainParamMap.containsKey(oneObj.getString("paramTypeClass"))) {
                if (!(mainParamMap.get(oneObj.getString("paramTypeClass")).contains(oneObj.getString("paramType")))) {
                    result.put("success", false);
                    result.put("message",
                        "请确认导入类型为  " + paramType + "， 请确认\"" + oneObj.getString("paramType") + "\"字段后重新导入！");
                    return;
                }

            } else {
                result.put("success", false);
                result.put("message",
                    "请确认导入类型为  " + paramType + "， 请确认\"" + oneObj.getString("paramTypeClass") + "\"字段后重新导入！");
                return;
            }
        }

        if (itemList.size() != mainParamList.size()) {
            result.put("success", false);
            result.put("message", "导入行数与默认配置表行数不符,请检查后重试！");
            return;
        }

        // 成功的话要先清数据
        JSONObject clearParam = new JSONObject();
        clearParam.put("applyId", applyId);
        productSpectrumDao.deleteMainParamWhenImport(clearParam);

        // 分批写入数据库
        List<JSONObject> tempInsert = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            tempInsert.add(itemList.get(i));
            if (tempInsert.size() % 20 == 0) {
                productSpectrumDao.batchInsertMainParam(tempInsert);
                tempInsert.clear();
            }
        }
        if (!tempInsert.isEmpty()) {
            // devAccountingImportDao.createXljcb(tempInsert);
            productSpectrumDao.batchInsertMainParam(tempInsert);
            tempInsert.clear();
        }
        result.put("message", "数据导入完成!");

    }

    private JSONObject generateDataFromMainSetting(Row row, List<JSONObject> itemList, List<String> titleList,
        String applyId) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        JSONObject oneRowMap = new JSONObject();
        boolean dataErrorFlag = false;
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            // 都以文本方式读取，直接设置成str
            String cellValue = null;
            if (cell != null) {
                cell.setCellType(CellType.STRING);
                cellValue = ExcelUtil.getCellFormatValue(cell);
            }
            switch (title) {
                case "序号":
                    break;
                case "一级类别":
                    // 这里要判空
                    if (StringUtils.isBlank(cellValue)) {
                        dataErrorFlag = true;
                        oneRowCheck.put("message", "一级类别不能为空，请检查");
                        break;
                    }
                    oneRowMap.put("settingTypeClass", cellValue);
                    break;
                case "类别":
                    // 这里要判空
                    if (StringUtils.isBlank(cellValue)) {
                        dataErrorFlag = true;
                        oneRowCheck.put("message", "类别不能为空，请检查");
                        break;
                    }
                    oneRowMap.put("settingType", cellValue);
                    break;
                case "物料编码":
                    // 这里要判空
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("settingMaterialCode", "");
                        break;
                    }
                    oneRowMap.put("settingMaterialCode", cellValue);
                    break;
                case "型号":
                    // 这里要判空
                    if (StringUtils.isBlank(cellValue)) {
                        dataErrorFlag = true;
                        oneRowCheck.put("message", "型号不能为空，请检查");
                        break;
                    }
                    oneRowMap.put("settingModel", cellValue);
                    break;
                case "供应商名称":
                    // 这里要判空
                    if (StringUtils.isBlank(cellValue)) {
                        dataErrorFlag = true;
                        oneRowCheck.put("message", "供应商名称不能为空，请检查");
                        break;
                    }
                    oneRowMap.put("supplyName", cellValue);
                    break;
                case "是否选配":
                    // 这里要判空
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("sfxp", "");
                        break;
                    } else if (("是".equalsIgnoreCase(cellValue) || ("否".equalsIgnoreCase(cellValue)))) {
                        oneRowMap.put("sfxp", cellValue);
                        break;
                    }
                    dataErrorFlag = true;
                    oneRowCheck.put("message", "是否选配值错误,请检查");
                    break;

                case "备注":
                    // 这里要判空
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("settingRemark", "");
                        break;
                    }
                    oneRowMap.put("settingRemark", cellValue);
                    break;

                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("id", IdUtil.getId());
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("applyId", applyId);
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        itemList.add(oneRowMap);
        if (dataErrorFlag) {
            oneRowCheck.put("result", false);
        } else {
            oneRowCheck.put("result", true);
        }
        return oneRowCheck;
    }

    private JSONObject generateDataFromMainParam(Row row, List<JSONObject> itemList, List<String> titleList,
        String applyId) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        JSONObject oneRowMap = new JSONObject();
        boolean dataErrorFlag = false;
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            // 都以文本方式读取，直接设置成str
            String cellValue = null;
            if (cell != null) {
                cell.setCellType(CellType.STRING);
                cellValue = ExcelUtil.getCellFormatValue(cell);
            }
            switch (title) {
                case "序号":
                    break;
                case "一级类别":
                    // 这里要判空
                    if (StringUtils.isBlank(cellValue)) {
                        dataErrorFlag = true;
                        oneRowCheck.put("message", "一级类别不能为空，请检查");
                        break;
                    }
                    oneRowMap.put("paramTypeClass", cellValue);
                    break;
                case "参数类型":
                    // 这里要判空
                    if (StringUtils.isBlank(cellValue)) {
                        dataErrorFlag = true;
                        oneRowCheck.put("message", "参数类型不能为空，请检查");
                        break;
                    }
                    oneRowMap.put("paramType", cellValue);
                    break;
                case "参数单位":
                    // 这里要判空
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("paramUnit", "");
                        break;
                    }
                    oneRowMap.put("paramUnit", cellValue);
                    break;
                case "参数值":
                    // 这里要判空
                    if (StringUtils.isBlank(cellValue)) {
                        dataErrorFlag = true;
                        oneRowCheck.put("message", "参数值不能为空，请检查");
                        break;
                    }
                    oneRowMap.put("paramValue", cellValue);
                    break;
                case "备注":
                    // 这里要判空
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("remark", "");
                        break;
                    }
                    oneRowMap.put("remark", cellValue);
                    break;

                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("id", IdUtil.getId());
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("applyId", applyId);
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        itemList.add(oneRowMap);
        if (dataErrorFlag) {
            oneRowCheck.put("result", false);
        } else {
            oneRowCheck.put("result", true);
        }
        return oneRowCheck;
    }

    // 批量导入销量及成本
    public void batchImportMainSetting(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            MultipartFile fileObj = multipartRequest.getFile("importFile");
            if (fileObj == null) {
                result.put("success", false);
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            Map<String, String[]> parameters = request.getParameterMap();
            if (parameters == null || parameters.isEmpty()) {
                result.put("success", false);
                logger.warn("没有找到上传的参数");
                return;
            }
            String fileName = ((CommonsMultipartFile)fileObj).getFileItem().getName();
            // todo 获取主表id
            // String applyId = "";
            String applyId = parameters.get("applyId")[0];
            Workbook wb = null;
            if (fileName.endsWith(ExcelReaderUtil.EXCEL03_EXTENSION)) {
                wb = new HSSFWorkbook(fileObj.getInputStream());
            } else if (fileName.endsWith(ExcelReaderUtil.EXCEL07_EXTENSION)) {
                wb = new XSSFWorkbook(fileObj.getInputStream());
            }
            importMainSetting(wb, applyId, result);

        } catch (Exception e) {
            logger.error("Exception in batchImportXljcb", e);
            result.put("success", false);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    private void importMainSetting(Workbook wb, String applyId, JSONObject result) {

        // 获取页签
        // Sheet sheet = wb.getSheet("Sheet0");
        Sheet sheet = wb.getSheet("模板");
        if (sheet == null) {
            logger.error("找不到导入页");
            result.put("message", "数据导入失败，找不到导入页！");
            return;
        }
        int rowNum = sheet.getPhysicalNumberOfRows();
        if (rowNum < 2) {
            logger.error("找不到标题行");
            result.put("message", "数据导入失败，找不到标题行！");
            return;
        }

        // 解析标题部分
        Row titleRow = sheet.getRow(1);
        if (titleRow == null) {
            logger.error("找不到标题行");
            result.put("message", "数据导入失败，找不到标题行！");
            return;
        }
        List<String> titleList = new ArrayList<>();
        for (int i = 0; i < titleRow.getLastCellNum(); i++) {
            Cell titleName = titleRow.getCell(i);
            if (titleName != null && StringUtils.isNotBlank(titleName.getStringCellValue())) {
                titleList.add(StringUtils.trim(titleName.getStringCellValue()));
            }
        }

        if (rowNum < 3) {
            logger.info("数据行为空");
            result.put("message", "数据导入失败，数据行为空！");
            return;
        }

        // 获取默认配置表
        JSONObject settingsParams = new JSONObject();
        settingsParams.put("type", "mainSettingInit");
        List<JSONObject> settingsList = productSpectrumDao.settingListQuery(settingsParams);

        Map<String, List<String>> settingMap = new HashMap<>();
        for (JSONObject oneSetting : settingsList) {
            // 遍历主要参数表,如果一级类别在map中不存在，新建，已存在，添加到列表
            if (settingMap.containsKey(oneSetting.getString("settingTypeClass"))) {
                settingMap.get(oneSetting.getString("settingTypeClass")).add(oneSetting.getString("settingType"));
            } else {
                List<String> listObj = new ArrayList<>();
                listObj.add(oneSetting.getString("settingType"));
                settingMap.put(oneSetting.getString("settingTypeClass"), listObj);
            }
        }

        List<JSONObject> itemList = new ArrayList<>();
        // 读取每一行的数据
        StringBuilder resMessage = new StringBuilder();
        Boolean dataErrorFlag = false;
        for (int i = 2; i < rowNum; i++) {
            Row row = sheet.getRow(i);
            JSONObject rowParse = generateDataFromMainSetting(row, itemList, titleList, applyId);
            if (!rowParse.getBoolean("result")) {
                // 这里改拼接字符串
                dataErrorFlag = true;
                resMessage.append("数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message")).append("</br>");
            }
        }

        if (dataErrorFlag) {
            // 把最后一个换行符删掉
            resMessage.delete(resMessage.length() - 5, resMessage.length());
            result.put("success", false);
            result.put("message", resMessage.toString());
            return;
        }

        // 校验1:行数 2：导入数据的类目是否在字典中

        for (JSONObject oneObj : itemList) {
            if (settingMap.containsKey(oneObj.getString("settingTypeClass"))) {
                if (!(settingMap.get(oneObj.getString("settingTypeClass")).contains(oneObj.getString("settingType")))) {
                    result.put("success", false);
                    result.put("message", "导入模板配置项类目不可改动，请确认\"" + oneObj.getString("settingType") + "\"字段后重新导入！");
                    return;
                }

            } else {
                result.put("success", false);
                result.put("message", "导入一级类别不可改动，请确认\"" + oneObj.getString("settingTypeClass") + "\"字段后重新导入！");
                return;
            }
        }

        if (itemList.size() != settingsList.size()) {
            result.put("success", false);
            result.put("message", "导入行数与默认配置表行数不符,请检查后重试！");
            return;
        }

        // 成功的话要先清数据
        JSONObject clearParam = new JSONObject();
        clearParam.put("applyId", applyId);
        productSpectrumDao.deleteMainSettingWhenImport(clearParam);
        // 分批写入数据库
        List<JSONObject> tempInsert = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            tempInsert.add(itemList.get(i));
            if (tempInsert.size() % 20 == 0) {
                productSpectrumDao.batchInsertMainSetting(tempInsert);
                tempInsert.clear();
            }
        }
        if (!tempInsert.isEmpty()) {
            productSpectrumDao.batchInsertMainSetting(tempInsert);
            tempInsert.clear();
        }
        result.put("message", "数据导入完成!");

    }

    /**
     * 支撑API获取产品型谱数据
     * 
     * @param paramJson
     * @param resultJson
     */
    public void queryDesignSpectrumForApi(JSONObject paramJson, JSONObject resultJson) {
        // 对外暴露查询产品型谱的服务
        Map<String, Object> params = new HashMap<>();
        String sortFiled = paramJson.getString("sortField");
        String sortOrder = paramJson.getString("sortOrder");

        // 排序字段为空,默认根据设计型号升序查询
        if (StringUtils.isEmpty(sortFiled)) {
            params.put("sortField", "designModel");
            params.put("sortOrder", "asc");
        } else {
            // 不为空根据指定的字段方式排序
            params.put("sortField", sortFiled);
            params.put("sortOrder", StringUtils.isBlank(sortOrder) ? "asc" : sortOrder);
        }
        // 如果分页(默认分页)，则设置默认分页参数
        if (!paramJson.containsKey("doPage") || paramJson.getBooleanValue("doPage")) {
            params.put("startIndex", 0);
            params.put("pageSize", 20);
            // 获取分页参数
            String pageIndex = paramJson.getString("pageIndex");
            String pageSize = paramJson.getString("pageSize");
            if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
                params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
                params.put("pageSize", Integer.parseInt(pageSize));
            }
        }
        // filter处理
        JSONArray jsonArray = paramJson.getJSONArray("filter");
        for (int i = 0; i < jsonArray.size(); i++) {
            String name = jsonArray.getJSONObject(i).getString("name");
            String value = jsonArray.getJSONObject(i).getString("value");
            if (StringUtils.isNotBlank(value)) {
                params.put(name, value);
            }
        }
        // 根据params查询
        List<JSONObject> result = productSpectrumDao.queryDesignSpectrumForApi(params);
        resultJson.put("data", result);
        if (!paramJson.containsKey("doPage") || paramJson.getBooleanValue("doPage")) {
            resultJson.put("total", productSpectrumDao.countDesignSpectrumForApi(params));
        }
    }
}
