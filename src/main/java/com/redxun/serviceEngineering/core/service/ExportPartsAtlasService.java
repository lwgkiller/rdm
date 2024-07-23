package com.redxun.serviceEngineering.core.service;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.productDataManagement.core.dao.ProductSpectrumDao;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.entity.BpmInst;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.materielextend.core.util.CommonUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.controller.RdmApiController;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmCommon.core.util.RdmCommonUtil;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.PatentInterpretationService;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.ExportPartsAtlasDao;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualDemandDao;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.sys.org.entity.OsRelType;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Service
public class ExportPartsAtlasService {
    private static Logger logger = LoggerFactory.getLogger(PatentInterpretationService.class);
    private static int EXCEL_ROW_OFFSET = 1;
    @Autowired
    private ExportPartsAtlasDao exportPartsAtlasDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private CommonInfoDao commonInfoDao;
    @Autowired
    private MaintenanceManualDemandService maintenanceManualDemandService;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private UserService userService;
    @Autowired
    private MaintenanceManualDemandDao maintenanceManualDemandDao;
    @Autowired
    private ProductSpectrumDao productSpectrumDao;

    // 需求通知单列表查询
    public JsonPageResult<?> demandListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        getListParams(params, request, "finishStatus,CREATE_TIME_", "asc", true);
        List<JSONObject> demandList = exportPartsAtlasDao.queryDemandList(params);
        int demandListTotal = exportPartsAtlasDao.queryDemandListTotal(params);
        for (JSONObject oneDemand : demandList) {
            if (oneDemand.get("CREATE_TIME_") != null) {
                oneDemand.put("CREATE_TIME_", DateUtil.formatDate(oneDemand.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }

        }

        result.setData(demandList);
        result.setTotal(demandListTotal);
        return result;
    }

    private void getListParams(Map<String, Object> params, HttpServletRequest request, String defaultSortField,
                               String defaultSortOrder, boolean doPage) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", defaultSortField);
            params.put("sortOrder", defaultSortOrder);
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    if ("applyDateBegin".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("applyDateEnd".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    if ("expectTime".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.parseDate(value), "yyyy-MM-dd");
                    }
                    if ("taskStatus".equalsIgnoreCase(name)) {
                        params.put(name, Arrays.asList(value.split(",", -1)));
                    } else if (name.equalsIgnoreCase("isFWP")) {
                        String[] values = value.split(",");
                        for (String field : values) {
                            params.put(field, "true");
                        }
                    } else {
                        params.put(name, value);
                    }
                }
            }
        }

        // 增加分页条件
        if (doPage) {
            params.put("startIndex", 0);
            params.put("pageSize", 20);
            String pageIndex = request.getParameter("pageIndex");
            String pageSize = request.getParameter("pageSize");
            if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
                params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
                params.put("pageSize", Integer.parseInt(pageSize));
            }
        }
    }

    //todo 入口 A 创建需求通知单及对应的需求明细
    public void createPartsAtlasDemandApply(JSONObject result, String postBody, boolean isNewCrm) throws Exception {
        logger.info("CRM推送出口产品零件图册需求通知单，" + postBody);
        JSONObject postBodyObj = new JSONObject();
        if (isNewCrm) {
            //A-3 适配新CRM接口的报文数据
            postBodyObj = this.doTransToOldPostBody(postBody);
        } else {
            postBodyObj = JSONObject.parseObject(postBody);
        }
        if (postBodyObj == null || postBodyObj.isEmpty()) {
            result.put("success", false);
            result.put("message", "传输的消息内容为空！");
            return;
        }
        JSONObject demandResultObj = new JSONObject();
        // 需求通知单号
        String demandNo = postBodyObj.getString("SPECORDERNO");
        if (StringUtils.isBlank(demandNo)) {
            logger.error("CRM创建出口产品零件图册需求通知单失败，需求单号为空！");
            result.put("success", false);
            result.put("message", "特殊订单号为空！");
            return;
        }
        // 销售方向
        String saleDirection = CommonFuns.nullToString(postBodyObj.getString("DIRECTION"));
        String filterNx = SysPropertiesUtil.getGlobalProperty("filterNx");
        // 如果需要过滤掉内销场景，则根据开关过滤
        if ("内销".equalsIgnoreCase(saleDirection) && "yes".equalsIgnoreCase(filterNx)) {
            logger.error("CRM创建出口产品零件图册需求通知单失败，内销场景的过滤掉，需求单号“" + demandNo + "”！");
            result.put("success", false);
            result.put("message", "内销场景的需求单暂不接收！");
            return;
        }
        // 需求通知单号是否唯一
        JSONObject param = new JSONObject();
        param.put("demandNo", demandNo);
        List<JSONObject> existDemands = exportPartsAtlasDao.queryDemandList(param);
        if (existDemands != null && !existDemands.isEmpty()) {
            logger.error("CRM创建出口产品零件图册需求通知单发生变更，需求单号“" + demandNo + "”！");
            //B 需求单更新
            StringBuilder changeDesc = this.demandChangeProcess(existDemands.get(0), postBodyObj, result);
            // @lwgkiller:从零件图册模块更新异动2023-04-17
            if (result.getBooleanValue("success") && changeDesc != null && changeDesc.length() > 0) {
                maintenanceManualDemandService.updateFromExportPartsAtlasService(changeDesc, demandNo);
            }
            return;
        }
        demandResultObj.put("demandNo", demandNo);
        // 出口国家为空则使用客户公司字段赋值
        String exportCountryName = postBodyObj.getString("REQCOUNTRY");
        if (StringUtils.isBlank(exportCountryName)) {
            exportCountryName = postBodyObj.getString("CLIENTCOMPANY");
        }
        demandResultObj.put("exportCountryName", exportCountryName);
        // 创建人
        String creatorName = postBodyObj.getString("DUTYMAN");
        /*if (StringUtils.isBlank(creatorName)) {
            logger.error("CRM创建出口产品零件图册需求通知单失败，创建人为空！");
            result.put("success", false);
            result.put("message", "责任人为空！");
            return;
        }*/
        demandResultObj.put("creatorName", creatorName);
        JSONArray detailsArr = postBodyObj.getJSONArray("EQUIPMENTDATA");
        if (detailsArr == null || detailsArr.isEmpty()) {
            logger.error("CRM创建出口产品零件图册需求通知单失败，需求明细为空！");
            result.put("success", false);
            result.put("message", "申请设备清单为空！");
            return;
        }
        demandResultObj.put("id", IdUtil.getId());
        demandResultObj.put("CREATE_BY_", "1");
        demandResultObj.put("CREATE_TIME_", new Date());
        demandResultObj.put("saleDirection", saleDirection);
        demandResultObj.put("sourceDeptName", postBodyObj.getString("RESERVE2"));


        Map<String, JSONObject> detailNo2Obj = new HashMap<>();
        //A-1 解析CRM接口传输的设备清单信息
        this.parseEquipmentData(detailNo2Obj, detailsArr, demandResultObj.getString("id"), result, true, saleDirection);
        if (!result.getBooleanValue("success")) {
            return;
        }


        Map<String, List<JSONObject>> detailNo2ConfigList = new HashMap<>();
        JSONArray configsArr = postBodyObj.getJSONArray("CONFIGUREDATA");
        if (configsArr != null) {
            //A-2 解析CRM接口传输的配置清单信息
            this.parseConfigureData(detailNo2ConfigList, configsArr, demandResultObj.getString("id"), demandNo,
                    detailNo2Obj, result, true);
            if (!result.getBooleanValue("success")) {
                return;
            }
        }
        // @lwgkiller:在此切入操保手册需求申请单的创建 todo
        String flag =
                sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringCallbackUrls", "CRMToMaintDemand").getValue();
        if (flag.equalsIgnoreCase("yes")) {
            JSONObject postDataMaint = new JSONObject();
            postDataMaint.put("demandResultObj", demandResultObj);
            postDataMaint.put("detailNo2Obj", detailNo2Obj);
            postDataMaint.put("detailNo2ConfigList", detailNo2ConfigList);
            maintenanceManualDemandService.createFormExportPartsAtlas(postDataMaint, RdmApiController.SAVE_DRAFT);
        }

        // 存储需求通知单基本信息
        exportPartsAtlasDao.insertDemand(demandResultObj);
        // 存储需求明细信息
        List<JSONObject> tempParam = new ArrayList<>();
        for (Map.Entry<String, JSONObject> entry : detailNo2Obj.entrySet()) {
            tempParam.add(entry.getValue());
            if (tempParam.size() % 10 == 0) {
                exportPartsAtlasDao.batchInsertDemandDetails(tempParam);
                tempParam.clear();
            }
        }
        if (!tempParam.isEmpty()) {
            exportPartsAtlasDao.batchInsertDemandDetails(tempParam);
            tempParam.clear();
        }
        // 存储配置明细信息
        for (Map.Entry entry : detailNo2ConfigList.entrySet()) {
            List<JSONObject> oneDetailConfigList = (List<JSONObject>) entry.getValue();
            for (JSONObject oneConfig : oneDetailConfigList) {
                tempParam.add(oneConfig);
                if (tempParam.size() % 10 == 0) {
                    exportPartsAtlasDao.batchInsertDetailConfigs(tempParam);
                    tempParam.clear();
                }
            }
        }
        if (!tempParam.isEmpty()) {
            exportPartsAtlasDao.batchInsertDetailConfigs(tempParam);
            tempParam.clear();
        }

    }

    //todo A-1 B-1 解析CRM接口传输的设备清单信息
    private void parseEquipmentData(Map<String, JSONObject> detailNo2Obj, JSONArray detailsArr, String demandId,
                                    JSONObject result, boolean generateId, String saleDirection) {
        for (int detailIndex = 0; detailIndex < detailsArr.size(); detailIndex++) {
            String detailNo = detailsArr.getJSONObject(detailIndex).getString("LISTNO");
            if (StringUtils.isBlank(detailNo)) {
                logger.error("CRM创建出口产品零件图册需求通知单失败，需求明细编号为空！");
                result.put("success", false);
                result.put("message", "清单号为空！");
                return;
            }
            String matCode = detailsArr.getJSONObject(detailIndex).getString("COMPNO");
            if (StringUtils.isBlank(matCode)) {
                logger.error("CRM创建出口产品零件图册需求通知单失败，物料编码为空！");
                result.put("success", false);
                result.put("message", "物料号为空！");
                return;
            }
            String designType = detailsArr.getJSONObject(detailIndex).getString("DESIGNMODEL");
            if (StringUtils.isBlank(designType)) {
                logger.error("CRM创建出口产品零件图册需求通知单失败，设计型号为空！");
                result.put("success", false);
                result.put("message", "设计型号为空！");
                return;
            }
            String saleType = detailsArr.getJSONObject(detailIndex).getString("SALEMODEL");
            if (StringUtils.isBlank(saleType)) {
                logger.error("CRM创建出口产品零件图册需求通知单失败，销售型号为空！");
                result.put("success", false);
                result.put("message", "销售型号为空！");
                return;
            }
            String needNumber = detailsArr.getJSONObject(detailIndex).getString("PLANNUM");
            if (StringUtils.isBlank(needNumber)) {
                logger.error("CRM创建出口产品零件图册需求通知单失败，数量为空！");
                result.put("success", false);
                result.put("message", "数量为空！");
                return;
            }
            // 如果不是内销场景，随机文件语言是必填要求
            String languages = detailsArr.getJSONObject(detailIndex).getString("LGGID");
            if (StringUtils.isBlank(languages)) {
                if (!"内销".equalsIgnoreCase(saleDirection)) {
                    logger.error("CRM创建出口产品零件图册需求通知单失败，随机文件语言为空！");
                    result.put("success", false);
                    result.put("message", "随机语言为空！");
                    return;
                } else {
                    languages = "中文";
                }
            }
            String needTime = detailsArr.getJSONObject(detailIndex).getString("DLVDATE");
            if (StringUtils.isBlank(needTime)) {
                logger.error("CRM创建出口产品零件图册需求通知单失败，交货时间为空！");
                result.put("success", false);
                result.put("message", "交货日期为空！");
                return;
            }
            needTime = needTime.replaceAll("/", "-");

            JSONObject detailsResultObj = new JSONObject();
            if (generateId) {
                detailsResultObj.put("id", IdUtil.getId());
            }
            detailsResultObj.put("demandId", demandId);
            detailsResultObj.put("detailNo", detailNo);
            detailsResultObj.put("matCode", matCode);
            detailsResultObj.put("designType", designType);
            detailsResultObj.put("saleType", saleType);
            detailsResultObj.put("needNumber", needNumber);
            detailsResultObj.put("languages", languages);
            detailsResultObj.put("needTime", needTime);
            // 是否需要EC声明
            if (detailsArr.getJSONObject(detailIndex).containsKey("field_l1i12__c")) {
                String field_l1i12__c = detailsArr.getJSONObject(detailIndex).getString("field_l1i12__c");
                detailsResultObj.put("field_l1i12__c", field_l1i12__c);
            } else {
                detailsResultObj.put("field_l1i12__c", "");
            }

            // EC声明语言种类
            if (detailsArr.getJSONObject(detailIndex).containsKey("field_8M8qH__c")) {
                String field_8M8qH__c = detailsArr.getJSONObject(detailIndex).getString("field_8M8qH__c");
                detailsResultObj.put("field_8M8qH__c", field_8M8qH__c);
            } else {
                detailsResultObj.put("field_8M8qH__c", "");
            }

            // EC声明配置识别码
            if (detailsArr.getJSONObject(detailIndex).containsKey("field_7p01H__c")) {
                String field_7p01H__c = detailsArr.getJSONObject(detailIndex).getString("field_7p01H__c");
                detailsResultObj.put("field_7p01H__c", field_7p01H__c);
            } else {
                detailsResultObj.put("field_7p01H__c", "");
            }

            detailsResultObj.put("CREATE_BY_", "1");
            detailsResultObj.put("CREATE_TIME_", new Date());
            detailNo2Obj.put(detailNo, detailsResultObj);
        }
    }

    //todo A-2 B-2 解析CRM接口传输的配置清单信息
    private void parseConfigureData(Map<String, List<JSONObject>> detailNo2ConfigList, JSONArray configsArr,
                                    String demandId, String demandNo, Map<String, JSONObject> detailNo2Obj,
                                    JSONObject result, boolean generateId) {
        for (int configIndex = 0; configIndex < configsArr.size(); configIndex++) {
            JSONObject oneConfig = configsArr.getJSONObject(configIndex);
            String detailNo = oneConfig.getString("LISTNO");
            if (StringUtils.isBlank(detailNo)) {
                logger.error("CRM创建出口产品零件图册需求通知单，配置数组中的需求明细编号为空！需求单号" + demandNo);
                result.put("success", false);
                result.put("message", "配置清单中设备清单号LISTNO为空！");
                return;
            }
            if (!detailNo2Obj.containsKey(detailNo)) {
                logger.error("CRM创建出口产品零件图册需求通知单，配置数组中的需求明细编号不存在！需求单号" + demandNo);
                result.put("success", false);
                result.put("message", "配置清单中设备清单号LISTNO不存在！");
                return;
            }
            String configNo = oneConfig.getString("RNO");
            if (StringUtils.isBlank(configNo)) {
                logger.error("CRM创建出口产品零件图册需求通知单，配置数组中的配置编号为空！需求单号" + demandNo);
                result.put("success", false);
                result.put("message", "配置清单中序号RNO为空！");
                return;
            }
            String configStr = oneConfig.getString("CONFIGURE");
            /*            if (StringUtils.isBlank(configStr)) {
                logger.error("CRM创建出口产品零件图册需求通知单，配置数组中的配置说明为空！需求单号" + demandNo);
                result.put("success", false);
                result.put("message", "配置清单中配置说明CONFIGURE为空！");
                return;
            }*/
            JSONObject configResultObj = new JSONObject();
            if (generateId) {
                configResultObj.put("id", IdUtil.getId());
            }
            configResultObj.put("demandId", demandId);
            configResultObj.put("demandDetailId", detailNo2Obj.get(detailNo).getString("id"));
            configResultObj.put("dispatchId", "");
            configResultObj.put("dispatchDetailId", "");
            configResultObj.put("configDesc", configStr);
            configResultObj.put("CREATE_BY_", "1");
            configResultObj.put("CREATE_TIME_", new Date());
            configResultObj.put("configNo", configNo);
            if (!detailNo2ConfigList.containsKey(detailNo)) {
                detailNo2ConfigList.put(detailNo, new ArrayList<>());
            }
            detailNo2ConfigList.get(detailNo).add(configResultObj);
        }
    }

    //todo A-3 适配新CRM接口的报文数据
    public JSONObject doTransToOldPostBody(String postBody) throws Exception {
        List<JSONObject> productSpectrums = productSpectrumDao.dataListQuery(null);
        //..缓存物料2设计型号的映射，便于检索设计型号
        Map<String, String> materialCode2DesignModel = new HashedMap();
        for (JSONObject productSpectrum : productSpectrums) {
            materialCode2DesignModel.put(productSpectrum.getString("materialCode"), productSpectrum.getString("designModel"));
        }
        StringBuilder stringBuilder = new StringBuilder();
        JSONObject newCrmBody = JSONObject.parseObject(postBody);
        JSONObject oldCrmBody = new JSONObject();
        //..
        oldCrmBody.put("SPECORDERNO", newCrmBody.getString("SPECORDERNO"));
        JSONArray details = newCrmBody.getJSONArray("Details");
        String REQCOUNTRY = "";
        Set<String> REQCOUNTRYSet = new HashSet<>();
        JSONArray EQUIPMENTDATA = new JSONArray();
        JSONArray CONFIGUREDATA = new JSONArray();
        for (int i = 0; i < details.size(); i++) {
            JSONObject detail = details.getJSONObject(i);
            REQCOUNTRYSet.add(detail.getString("REQCOUNTRY"));
            String DESIGNMODEL = "未匹配到";
            if (materialCode2DesignModel.containsKey(detail.getString("COMPNO"))
                    && StringUtil.isNotEmpty(materialCode2DesignModel.get(detail.getString("COMPNO")))) {
                //RDM产品型谱中匹配到了当前物料的设计型号
                DESIGNMODEL = materialCode2DesignModel.get(detail.getString("COMPNO"));
            }
            EQUIPMENTDATA.add(new JSONObject()
                    .fluentPut("SPECORDERNO", detail.getString("SPECORDERNO"))
                    .fluentPut("LISTNO", detail.getString("LISTNO"))
                    .fluentPut("COMPNO", detail.getString("COMPNO"))
                    .fluentPut("DESIGNMODEL", DESIGNMODEL)
                    .fluentPut("SALEMODEL", detail.getString("SALEMODEL"))
                    .fluentPut("PLANNUM", detail.getString("PLANNUM"))
                    .fluentPut("LGGID", detail.getString("LGGID"))
                    .fluentPut("DLVDATE", detail.getString("DLVDATE"))
                    .fluentPut("JUDGEDLVDATE", detail.getString("JUDGEDLVDATE"))
                    .fluentPut("RESERVE1", detail.getString("RESERVE1"))
                    .fluentPut("RESERVE2", detail.getString("RESERVE2"))
                    .fluentPut("field_l1i12__c", detail.getString("field_l1i12__c"))
                    .fluentPut("field_8M8qH__c", detail.getString("field_8M8qH__c"))
                    .fluentPut("field_7p01H__c", detail.getString("field_7p01H__c"))

            );
            CONFIGUREDATA.add(new JSONObject()
                    .fluentPut("SPECORDERNO", detail.getString("SPECORDERNO"))
                    .fluentPut("LISTNO", detail.getString("LISTNO"))
                    .fluentPut("RNO", "1")
                    .fluentPut("CONFIGURE", detail.getString("memo"))
            );
        }
        for (String s : REQCOUNTRYSet) {
            stringBuilder.append(s).append(",");
        }
        if (stringBuilder.length() > 1) {
            REQCOUNTRY = stringBuilder.substring(0, stringBuilder.length() - 1);
        }
        if (StringUtil.isEmpty(REQCOUNTRY)) {
            REQCOUNTRY = newCrmBody.getString("CLIENTCOMPANY");
        }
        oldCrmBody.put("REQCOUNTRY", REQCOUNTRY);
        oldCrmBody.put("RCREATEDATE", newCrmBody.getString("RCREATEDATE"));
        oldCrmBody.put("AREANO", newCrmBody.getString("AREANO"));
        oldCrmBody.put("DUTYMAN", newCrmBody.getString("DUTYMAN"));
        oldCrmBody.put("DIRECTION", newCrmBody.getString("DIRECTION"));
        oldCrmBody.put("TYPE", newCrmBody.getString("TYPE"));
        oldCrmBody.put("RESERVE2", newCrmBody.getString("RESERVE2"));
        oldCrmBody.put("EQUIPMENTDATA", EQUIPMENTDATA);
        oldCrmBody.put("CONFIGUREDATA", CONFIGUREDATA);
        return oldCrmBody;
    }

    //todo B 需求单更新
    private StringBuilder demandChangeProcess(JSONObject demandApply, JSONObject postBodyObj, JSONObject result) {
        // 记录变更内容
        StringBuilder demandChangeSb = new StringBuilder();
        // 基本信息的更新
        boolean baseInfoChangeFlag = false;
        // 判断出口国家
        String exportCountryName = postBodyObj.getString("REQCOUNTRY");
        if (StringUtils.isBlank(exportCountryName)) {
            exportCountryName = postBodyObj.getString("CLIENTCOMPANY");
        }
        String oldName = demandApply.getString("exportCountryName");
        if (!exportCountryName.equalsIgnoreCase(oldName)) {
            baseInfoChangeFlag = true;
            demandApply.put("exportCountryName", exportCountryName);
            demandChangeSb.append("出口国家变更为“" + exportCountryName + "”，原信息为“" + oldName + "”<br>");
        }
        // 判断销售方向
        String saleDirection = CommonFuns.nullToString(postBodyObj.getString("DIRECTION"));
        String oldSaleDirection = CommonFuns.nullToString(demandApply.getString("saleDirection"));
        if (!saleDirection.equalsIgnoreCase(oldSaleDirection)) {
            baseInfoChangeFlag = true;
            demandApply.put("saleDirection", saleDirection);
            demandChangeSb.append("销售方向变更为“" + saleDirection + "”，原信息为“" + oldSaleDirection + "”<br>");
        }
        if (baseInfoChangeFlag) {
            exportPartsAtlasDao.updateDemand(demandApply);
        }

        // 查询这个需求通知单当前数据库中所有的需求明细和配置信息，并进行组装
        Map<String, JSONObject> detailNo2Obj = new HashMap<>();
        List<JSONObject> demandDetails = this.getDemandDetailList(demandApply.getString("id"));
        JSONObject param = new JSONObject();
        Set<String> demandDetailIds = new HashSet<>();
        for (JSONObject oneDetail : demandDetails) {
            demandDetailIds.add(oneDetail.getString("id"));
            oneDetail.put("configNo2Obj", new JSONObject());
            detailNo2Obj.put(oneDetail.getString("detailNo"), oneDetail);
        }
        param.put("demandDetailIds", demandDetailIds);
        List<JSONObject> configList = exportPartsAtlasDao.queryDetailConfigs(param);
        for (JSONObject oneConfig : configList) {
            String detailNo = oneConfig.getString("detailNo");
            JSONObject oneDetail = detailNo2Obj.get(detailNo);
            oneDetail.getJSONObject("configNo2Obj").put(oneConfig.getString("configNo"), oneConfig);
        }

        // 解析本次传输的需求明细及配置信息，并进行组装
        Map<String, JSONObject> detailNo2ObjCrm = new HashMap<>();
        //B-1 解析CRM接口传输的设备清单信息
        this.parseEquipmentData(detailNo2ObjCrm, postBodyObj.getJSONArray("EQUIPMENTDATA"), demandApply.getString("id"),
                result, false, saleDirection);
        if (!result.getBooleanValue("success")) {
            return demandChangeSb;
        }
        Map<String, List<JSONObject>> detailNo2ConfigListCrm = new HashMap<>();
        JSONArray configsArr = postBodyObj.getJSONArray("CONFIGUREDATA");
        if (configsArr != null) {
            //B-2 解析CRM接口传输的配置清单信息
            this.parseConfigureData(detailNo2ConfigListCrm, configsArr, demandApply.getString("id"),
                    demandApply.getString("demandNo"), detailNo2ObjCrm, result, false);
            if (!result.getBooleanValue("success")) {
                return demandChangeSb;
            }
            for (Map.Entry entry : detailNo2ObjCrm.entrySet()) {
                JSONObject oneDetailObj = (JSONObject) entry.getValue();
                oneDetailObj.put("configNo2Obj", new JSONObject());
                if (detailNo2ConfigListCrm.containsKey(entry.getKey())) {
                    for (JSONObject oneConfig : detailNo2ConfigListCrm.get(entry.getKey())) {
                        oneDetailObj.getJSONObject("configNo2Obj").put(oneConfig.getString("configNo"), oneConfig);
                    }
                }
            }
        }
        //B-3 比对数据库中已有的和本次传递的区别，记录变化内容。需求清单和配置清单都只能增减，不能更新
        this.compareDemandDetailChanges(detailNo2Obj, detailNo2ObjCrm, demandApply.getString("demandNo"), result,
                demandChangeSb);
        return demandChangeSb;
    }

    //todo B-3 比对数据库中已有的和本次传递的区别，记录变化内容。需求清单和配置清单都只能增减，不能更新
    private void compareDemandDetailChanges(Map<String, JSONObject> detailNo2ObjExist,
                                            Map<String, JSONObject> detailNo2ObjCrm, String demandNo,
                                            JSONObject result, StringBuilder demandChangeSb) {
        // 新增的需求明细
        List<String> addDemandDetailNoList = new ArrayList<>(detailNo2ObjCrm.keySet());
        addDemandDetailNoList.removeAll(new ArrayList<>(detailNo2ObjExist.keySet()));
        // 删掉的需求明细
        List<String> delDemandDetailNoList = new ArrayList<>(detailNo2ObjExist.keySet());
        delDemandDetailNoList.removeAll(new ArrayList<>(detailNo2ObjCrm.keySet()));
        // 不变的需求明细（下面的配置明细有可能变化）
        List<String> sameDemandDetailNoList = new ArrayList<>(detailNo2ObjCrm.keySet());
        sameDemandDetailNoList.retainAll(new ArrayList<>(detailNo2ObjExist.keySet()));

        // 处理新增
        for (String addDemandDetailNo : addDemandDetailNoList) {
            //B-3-1 处理需求明细的新增
            this.procAddDemandDetail(detailNo2ObjCrm.get(addDemandDetailNo), demandChangeSb);
        }
        // 处理不变
        for (String sameDemandDetailNo : sameDemandDetailNoList) {
            //B-3-2 比较相同的需求明细下的配置明细变化
            this.procSameDemandDetail(detailNo2ObjExist.get(sameDemandDetailNo), detailNo2ObjCrm.get(sameDemandDetailNo),
                    demandChangeSb);
        }
        // 处理删除
        for (String delDemandDetailNo : delDemandDetailNoList) {
            //B-3-3 处理需求明细的删除（需求明细、配置明细和关联的制作任务）
            this.procDelDemandDetail(detailNo2ObjExist.get(delDemandDetailNo), demandChangeSb);
        }
        // 组装变更内容并写入表中
        if (demandChangeSb.length() != 0) {
            JSONObject changeNoticeObj = new JSONObject();
            changeNoticeObj.put("id", IdUtil.getId());
            changeNoticeObj.put("changeType", "需求通知单");
            changeNoticeObj.put("changeTitle", "通知单编号" + demandNo);
            changeNoticeObj.put("changeDesc", demandChangeSb.toString());
            changeNoticeObj.put("changeConfirm", "未确认");
            changeNoticeObj.put("CREATE_BY_", "1");
            changeNoticeObj.put("CREATE_TIME_", new Date());
            exportPartsAtlasDao.insertChangeNotice(changeNoticeObj);
        }
    }

    //todo B-3-1 处理需求明细的新增
    private void procAddDemandDetail(JSONObject newDemandDetail, StringBuilder demandChangeSb) {
        String addDemandDetailId = IdUtil.getId();
        newDemandDetail.put("id", addDemandDetailId);
        exportPartsAtlasDao.insertDemandDetail(newDemandDetail);
        // 如果下面有配置信息，则全部新增
        if (!newDemandDetail.getJSONObject("configNo2Obj").isEmpty()) {
            JSONObject configs = newDemandDetail.getJSONObject("configNo2Obj");
            Set<String> configNos = configs.keySet();
            for (String oneConfigNo : configNos) {
                JSONObject oneConfig = configs.getJSONObject(oneConfigNo);
                oneConfig.put("id", IdUtil.getId());
                oneConfig.put("demandDetailId", addDemandDetailId);
                exportPartsAtlasDao.insertDetailConfig(oneConfig);
            }
        }
        demandChangeSb.append("新增需求明细“" + newDemandDetail.getString("detailNo") + "”及配置信息<br>");
    }

    //todo B-3-2 比较相同的需求明细下的配置明细变化
    private void procSameDemandDetail(JSONObject oldDemandDetail, JSONObject newDemandDetail,
                                      StringBuilder demandChangeSb) {
        String detailNo = oldDemandDetail.getString("detailNo");
        String detailId = oldDemandDetail.getString("id");
        JSONObject oldConfigs = oldDemandDetail.getJSONObject("configNo2Obj");
        JSONObject newConfigs = newDemandDetail.getJSONObject("configNo2Obj");
        Set<String> oldConfigNos = oldConfigs == null ? new HashSet<>() : oldConfigs.keySet();
        Set<String> newConfigNos = newConfigs == null ? new HashSet<>() : newConfigs.keySet();

        // 新增的配置明细
        List<String> addConfigNoList = new ArrayList<>(newConfigNos);
        addConfigNoList.removeAll(new ArrayList<>(oldConfigNos));
        // 删掉的配置明细
        List<String> delConfigNoList = new ArrayList<>(oldConfigNos);
        delConfigNoList.removeAll(new ArrayList<>(newConfigNos));
        // 处理新增
        for (String addConfigNo : addConfigNoList) {
            JSONObject newConfig = newConfigs.getJSONObject(addConfigNo);
            newConfig.put("id", IdUtil.getId());
            newConfig.put("demandDetailId", detailId);
            exportPartsAtlasDao.insertDetailConfig(newConfig);
            demandChangeSb.append("新增配置信息“" + newConfig.getString("configNo") + "”，对应需求明细“" + detailNo + "”<br>");
        }
        // 处理删除
        for (String delConfigNo : delConfigNoList) {
            JSONObject delConfig = oldConfigs.getJSONObject(delConfigNo);
            exportPartsAtlasDao.delConfigDetail(delConfig);
            demandChangeSb.append("删除配置信息“" + delConfig.getString("configNo") + "”，对应需求明细“" + detailNo + "”<br>");
        }
    }

    //todo B-3-3 处理需求明细的删除（需求明细、配置明细和关联的制作任务）
    private void procDelDemandDetail(JSONObject oldDemandDetail, StringBuilder demandChangeSb) {
        // 1、需求明细删除
        exportPartsAtlasDao.delDemandDetail(oldDemandDetail);
        // 2、下面所有的配置明细删除
        JSONObject configs = oldDemandDetail.getJSONObject("configNo2Obj");
        if (configs != null && !configs.isEmpty()) {
            for (String key : configs.keySet()) {
                JSONObject oneConfig = configs.getJSONObject(key);
                exportPartsAtlasDao.delConfigDetail(oneConfig);
            }
        }
        // 3、处理关联的整机制作任务(不是作废状态的)
        Map<String, JSONObject> machineCode2ObjExistRunning = new HashMap<>();
        JSONObject param = new JSONObject();
        param.put("filterZf", "yes");
        param.put("demandDetailId", oldDemandDetail.getString("id"));
        queryAllMachineTaskInfos(machineCode2ObjExistRunning, param);
        List<JSONObject> statusList = new ArrayList<>();
        //B-3-3-1 处理一批整机制作任务的解绑和作废，如与成品库有关联则仅“解绑”，无关联则“解绑并作废”
        this.processMachineTaskZf(machineCode2ObjExistRunning);
        demandChangeSb.append("删除需求明细“" + oldDemandDetail.getString("detailNo") + "”及配置信息<br>");
    }

    //todo B-3-3-1 C-2-1 处理一批整机制作任务的解绑和作废，如与成品库有关联则仅“解绑”，无关联则“解绑并作废”
    private void processMachineTaskZf(Map<String, JSONObject> machineCode2ObjZf) {
        if (machineCode2ObjZf == null || machineCode2ObjZf.isEmpty()) {
            return;
        }
        // 解绑并作废的任务
        Set<String> zfTaskIds = new HashSet<>();
        StringBuilder taskZfChangeSb = new StringBuilder();
        List<JSONObject> statusList = new ArrayList<>();
        // 仅仅解绑的任务
        Set<String> unBindTaskIds = new HashSet<>();
        StringBuilder taskUnBindChangeSb = new StringBuilder();
        for (Map.Entry<String, JSONObject> entry : machineCode2ObjZf.entrySet()) {
            JSONObject oneTask = entry.getValue();
            String fwpId = oneTask.getString("fwpId");
            if (StringUtils.isBlank(fwpId)) {
                // 与成品库无关，则“解绑并作废”
                zfTaskIds.add(oneTask.getString("id"));
                // 记录任务的状态变化
                JSONObject oneStatus = new JSONObject();
                oneStatus.put("id", IdUtil.getId());
                oneStatus.put("busKeyId", oneTask.getString("id"));
                oneStatus.put("statusDesc", RdmConst.PARTS_ATLAS_STATUS_ZF);
                oneStatus.put("scene", "task");
                oneStatus.put("creatorName", oneTask.getString("relDemandUserName"));
                oneStatus.put("optionDesc", "");
                oneStatus.put("CREATE_BY_", "1");
                oneStatus.put("CREATE_TIME_", new Date());
                statusList.add(oneStatus);
                // 变更内容组装
                taskZfChangeSb.append("制作任务作废，整机PIN码：" + entry.getKey())
                        .append("，作废时状态：" + toGetTaskStatusNameByKey(oneTask.getString("taskStatus")))
                        .append("，需求通知单号：" + oneTask.getString("demandNo"))
                        .append("，需求清单号：" + oneTask.getString("detailNo") + "<br>");
            } else {
                unBindTaskIds.add(oneTask.getString("id"));
                // 变更内容组装
                taskUnBindChangeSb.append("制作任务解绑，整机PIN码：" + entry.getKey())
                        .append("，需求通知单号：" + oneTask.getString("demandNo"))
                        .append("，需求清单号：" + oneTask.getString("detailNo") + "<br>");
            }
        }
        // 更新制作任务的信息
        if (!zfTaskIds.isEmpty()) {
            JSONObject param = new JSONObject();
            param.put("idList", zfTaskIds);
            param.put("demandId", "");
            param.put("demandDetailId", "");
            param.put("dispatchId", "");
            param.put("dispatchDetailId", "");
            param.put("changeStatus", RdmConst.PARTS_ATLAS_STATUS_ZF);
            param.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            param.put("UPDATE_TIME_", new Date());
            exportPartsAtlasDao.updateTask2Zf(param);
            // 写入变更提醒内容
            JSONObject changeNoticeObj = new JSONObject();
            changeNoticeObj.put("id", IdUtil.getId());
            changeNoticeObj.put("changeType", "整机信息");
            changeNoticeObj.put("changeTitle", "图册制作任务作废");
            changeNoticeObj.put("changeDesc", taskZfChangeSb.toString());
            changeNoticeObj.put("changeConfirm", "未确认");
            changeNoticeObj.put("CREATE_BY_", "1");
            changeNoticeObj.put("CREATE_TIME_", new Date());
            exportPartsAtlasDao.insertChangeNotice(changeNoticeObj);
        }

        if (!unBindTaskIds.isEmpty()) {
            JSONObject param = new JSONObject();
            param.put("idList", unBindTaskIds);
            param.put("demandId", "");
            param.put("demandDetailId", "");
            param.put("dispatchId", "");
            param.put("dispatchDetailId", "");
            param.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            param.put("UPDATE_TIME_", new Date());
            exportPartsAtlasDao.updateTask2UnBind(param);
            // 写入变更提醒内容
            JSONObject changeNoticeObj = new JSONObject();
            changeNoticeObj.put("id", IdUtil.getId());
            changeNoticeObj.put("changeType", "整机信息");
            changeNoticeObj.put("changeTitle", "图册制作任务解绑");
            changeNoticeObj.put("changeDesc", taskUnBindChangeSb.toString());
            changeNoticeObj.put("changeConfirm", "未确认");
            changeNoticeObj.put("CREATE_BY_", "1");
            changeNoticeObj.put("CREATE_TIME_", new Date());
            exportPartsAtlasDao.insertChangeNotice(changeNoticeObj);
        }

        if (!statusList.isEmpty()) {
            //B-3-3-1-1 记录任务的状态变化（解绑并作废）的数据
            this.recordTaskStatusHis(statusList);
        }
    }

    //todo B-3-3-1-1 C-4-1-1 记录任务的状态变化（解绑并作废）的数据
    public void recordTaskStatusHis(List<JSONObject> statusList) {
        if (statusList != null && !statusList.isEmpty()) {
            List<JSONObject> tempStatusList = new ArrayList<>();
            for (JSONObject oneData : statusList) {
                tempStatusList.add(oneData);
                if (tempStatusList.size() % 10 == 0) {
                    exportPartsAtlasDao.batchInsertTaskStatusHis(tempStatusList);
                    tempStatusList.clear();
                }
            }
            if (!tempStatusList.isEmpty()) {
                exportPartsAtlasDao.batchInsertTaskStatusHis(tempStatusList);
                tempStatusList.clear();
            }
        }
    }

    //todo 入口C 接收MES推送的需求明细中的整机信息（查找有没有运行中的任务，对于删除场景需要判断是否与成品库有关；对于新增场景如果系统已存在则更新关联）
    public void createPartsAtlasDemandMachine(JSONObject result, String postBody) {
        logger.info("MES推送出口产品零件图册需求明细中整机信息，" + postBody);
        JSONArray postBodyObjArr = JSONArray.parseArray(postBody);
        if (postBodyObjArr == null || postBodyObjArr.isEmpty()) {
            result.put("success", false);
            result.put("message", "传输的消息内容为空！");
            return;
        }
        // 需要作废的
        Map<String, JSONObject> machineCode2ObjZf = new HashMap<>();
        // 需要新增的（含更新）
        Map<String, JSONObject> machineCode2ObjXZ = new HashMap<>();
        Set<String> addDemandNos = new HashSet<>();
        //C-1 解析MES传递的字符串
        this.parseMesDemandMachine(postBodyObjArr, result, addDemandNos, machineCode2ObjZf, machineCode2ObjXZ);
        if (!result.getBooleanValue("success")) {
            return;
        }
        //C-2 处理作废的数据（作废先于新增处理，因为换车场景就是先作废然后新增）
        this.processMesDemandMachineZF(machineCode2ObjZf);
        //C-3 处理MES传递信息中要新增或更新的整机任务
        this.processMesDemandMachineXZ(machineCode2ObjXZ, addDemandNos, result);
        if (!result.getBooleanValue("success")) {
            return;
        }
        //C-4 判断本次录入的图册制作任务，当前时间与关联明细的“交货时间”相比间隔是否≤1天（有可能是负值），如果是则自动发起异常反馈不需要审核；
        if (!machineCode2ObjXZ.isEmpty()) {
            this.relDemandDetailAbnormal(new ArrayList<>(machineCode2ObjXZ.values()));
        }
    }

    //todo C-1 解析MES传递的字符串
    private void parseMesDemandMachine(JSONArray postBodyObjArr, JSONObject result, Set<String> addDemandNos,
                                       Map<String, JSONObject> machineCode2ObjZf, Map<String, JSONObject> machineCode2ObjXZ) {
        if (postBodyObjArr == null || postBodyObjArr.isEmpty()) {
            return;
        }
        for (int index = 0; index < postBodyObjArr.size(); index++) {
            JSONObject oneObj = postBodyObjArr.getJSONObject(index);
            // 判断整机编号是否为空
            String machineCode = oneObj.getString("machineCode");
            if (StringUtils.isBlank(machineCode)) {
                logger.error("MES推送出口产品整机信息失败，整机PIN码为空！");
                result.put("success", false);
                result.put("message", "整机id为空！");
                return;
            }
            // 判断新增还是作废(Y是作废，N是新增)
            String voidMark = oneObj.getString("voidMark");
            if (StringUtils.isBlank(voidMark)) {
                voidMark = "N";
                oneObj.put("voidMark", voidMark);
            }
            // 删除场景
            if ("Y".equalsIgnoreCase(voidMark)) {
                machineCode2ObjZf.put(machineCode, oneObj);
                continue;
            }
            // 新增场景，检查字段完整性
            String demandNo = oneObj.getString("demandNo");
            if (StringUtils.isBlank(demandNo)) {
                logger.error("MES推送出口产品整机信息失败，需求通知单号为空！");
                result.put("success", false);
                result.put("message", "特殊订单号为空！");
                return;
            }
            String detailNo = oneObj.getString("demandDetailId");
            if (StringUtils.isBlank(detailNo)) {
                logger.error("MES推送出口产品整机信息失败，需求清单号为空！");
                result.put("success", false);
                result.put("message", "清单号为空！");
                return;
            }
            String inTime = oneObj.getString("inTime");
            if (StringUtils.isBlank(inTime)) {
                logger.error("MES推送出口产品整机信息失败，入库时间为空！");
                result.put("success", false);
                result.put("message", "入库时间为空！");
                return;
            }
            String operatorName = oneObj.getString("operatorName");
            if (StringUtils.isBlank(operatorName)) {
                logger.error("MES推送出口产品整机信息失败，整机录入人为空！");
                result.put("success", false);
                result.put("message", "整机录入人为空！");
                return;
            }
            String exchcodeMark = oneObj.getString("exchcodeMark");
            if (StringUtils.isBlank(exchcodeMark)) {
                exchcodeMark = "N";
                oneObj.put("exchcodeMark", exchcodeMark);
            }
            machineCode2ObjXZ.put(machineCode, oneObj);
            addDemandNos.add(demandNo);
        }
    }

    //todo C-2 处理作废的数据（作废先于新增处理，因为换车场景就是先作废然后新增）
    private void processMesDemandMachineZF(Map<String, JSONObject> machineCode2ObjZf) {
        if (machineCode2ObjZf == null || machineCode2ObjZf.isEmpty()) {
            return;
        }
        Map<String, JSONObject> machineCode2ObjExistRunning = new HashMap<>();
        JSONObject param = new JSONObject();
        param.put("filterZf", "yes");
        param.put("machineCodeList", new ArrayList<>(machineCode2ObjZf.keySet()));
        this.queryAllMachineTaskInfos(machineCode2ObjExistRunning, param);
        //C-2-1 处理一批整机制作任务的解绑和作废，如与成品库有关联则仅“解绑”，无关联则“解绑并作废”
        this.processMachineTaskZf(machineCode2ObjExistRunning);
    }

    //todo C-3 处理MES传递信息中要新增或更新的整机任务
    private void processMesDemandMachineXZ(Map<String, JSONObject> machineCode2ObjXZ, Set<String> addDemandNos,
                                           JSONObject result) {
        if (machineCode2ObjXZ == null || machineCode2ObjXZ.isEmpty()) {
            return;
        }
        // 根据demandNos查找数据库中的需求明细
        Map<String, Map<String, JSONObject>> demandNo2DetailNo2Obj = new HashMap<>();
        this.queryDemandDetailsByDemandNos(addDemandNos, demandNo2DetailNo2Obj);
        // 检查要关联的需求单和需求明细是否存在
        for (Map.Entry<String, JSONObject> entry : machineCode2ObjXZ.entrySet()) {
            JSONObject oneData = entry.getValue();
            String demandNo = oneData.getString("demandNo");
            String detailNo = oneData.getString("demandDetailId");
            Map<String, JSONObject> detailNo2Obj = demandNo2DetailNo2Obj.get(demandNo);
            if (detailNo2Obj == null || detailNo2Obj.get(detailNo) == null) {
                logger.error("MES推送出口产品整机信息失败，需求清单号" + detailNo + "在需求通知单" + demandNo + "中不存在！");
                result.put("success", false);
                result.put("message", "此清单号在对应特殊订单中不存在！");
                return;
            }
        }
        // 根据machineCode查找这些新增的任务中有没有运行中的
        Map<String, JSONObject> machineCode2ObjExistRunning = new HashMap<>();
        JSONObject param = new JSONObject();
        param.put("filterZf", "yes");
        param.put("machineCodeList", new ArrayList<>(machineCode2ObjXZ.keySet()));
        this.queryAllMachineTaskInfos(machineCode2ObjExistRunning, param);
        // 逐个任务处理，没有运行中的则纯新增，有运行中的则更新
        Map<String, JSONObject> machineCode2ObjUpdate = new HashMap<>();
        Map<String, JSONObject> machineCode2ObjInsert = new HashMap<>();
        for (Map.Entry<String, JSONObject> entry : machineCode2ObjXZ.entrySet()) {
            JSONObject oneTaskObj = entry.getValue();
            String machineCode = entry.getKey();
            // 找到要挂接的需求明细
            String demandNo = oneTaskObj.getString("demandNo");
            String detailNo = oneTaskObj.getString("demandDetailId");
            JSONObject demandDetailObj = demandNo2DetailNo2Obj.get(demandNo).get(detailNo);
            if (machineCode2ObjExistRunning.containsKey(machineCode)) {
                // 更新
                oneTaskObj.put("id", machineCode2ObjExistRunning.get(machineCode).getString("id"));
                oneTaskObj.put("demandId", demandDetailObj.getString("demandId"));
                oneTaskObj.put("demandDetailId", demandDetailObj.getString("id"));
                oneTaskObj.put("needTime", demandDetailObj.getDate("needTime"));
                oneTaskObj.put("relDemandUserName", oneTaskObj.getString("operatorName"));
                oneTaskObj.put("relDemandTime", new Date());
                oneTaskObj.put("exchangeScene", oneTaskObj.getString("exchcodeMark").equalsIgnoreCase("Y") ? "是" : "否");
                machineCode2ObjUpdate.put(machineCode, oneTaskObj);
            } else {
                // 新增
                oneTaskObj.put("id", IdUtil.getId());
                oneTaskObj.put("CREATE_BY_", "1");
                oneTaskObj.put("CREATE_TIME_", new Date());
                oneTaskObj.put("demandId", demandDetailObj.getString("demandId"));
                oneTaskObj.put("demandDetailId", demandDetailObj.getString("id"));
                oneTaskObj.put("needTime", demandDetailObj.getDate("needTime"));
                oneTaskObj.put("relDemandUserName", oneTaskObj.getString("operatorName"));
                oneTaskObj.put("relDemandTime", new Date());
                oneTaskObj.put("exchangeScene", oneTaskObj.getString("exchcodeMark").equalsIgnoreCase("Y") ? "是" : "否");
                oneTaskObj.put("taskStatus", RdmConst.PARTS_ATLAS_STATUS_WLQ);
                oneTaskObj.put("fwpId", null);
                oneTaskObj.put("dispatchTime", null);
                machineCode2ObjInsert.put(machineCode, oneTaskObj);
            }
        }
        //C-3-1 MES推送的整机新增场景，如果系统当前有运行中的，则更新关联的需求信息
        this.mesMachineTaskUpdate(machineCode2ObjUpdate);
        //C-3-2 MES推送的整机新增场景，真正的新增插入一条
        this.mesMachineTaskInsert(machineCode2ObjInsert);
    }

    //todo C-3-1 MES推送的整机新增场景，如果系统当前有运行中的，则更新关联的需求信息
    private void mesMachineTaskUpdate(Map<String, JSONObject> machineCode2ObjThisUpdate) {
        if (machineCode2ObjThisUpdate == null || machineCode2ObjThisUpdate.isEmpty()) {
            return;
        }
        List<JSONObject> tempTaskList = new ArrayList<>();
        for (Map.Entry<String, JSONObject> entry : machineCode2ObjThisUpdate.entrySet()) {
            JSONObject oneTask = entry.getValue();
            tempTaskList.add(oneTask);
            if (tempTaskList.size() % 10 == 0) {
                exportPartsAtlasDao.batchUpdateMachineTaskWhichDispatchFWPCreate(tempTaskList);
                tempTaskList.clear();
            }
        }
        if (!tempTaskList.isEmpty()) {
            exportPartsAtlasDao.batchUpdateMachineTaskWhichDispatchFWPCreate(tempTaskList);
            tempTaskList.clear();
        }
    }

    //todo C-3-2 MES推送的整机新增场景，真正的新增插入一条
    private void mesMachineTaskInsert(Map<String, JSONObject> machineCode2ObjThisInsert) {
        // 新增
        List<JSONObject> tempTaskList = new ArrayList<>();
        List<JSONObject> statusList = new ArrayList<>();
        for (Map.Entry<String, JSONObject> entry : machineCode2ObjThisInsert.entrySet()) {
            JSONObject oneTask = entry.getValue();
            tempTaskList.add(oneTask);
            JSONObject oneStatus = new JSONObject();
            oneStatus.put("id", IdUtil.getId());
            oneStatus.put("busKeyId", oneTask.getString("id"));
            oneStatus.put("statusDesc", RdmConst.PARTS_ATLAS_STATUS_WLQ);
            oneStatus.put("scene", "task");
            oneStatus.put("creatorName", oneTask.getString("relDemandUserName"));
            oneStatus.put("optionDesc", "");
            oneStatus.put("CREATE_BY_", "1");
            oneStatus.put("CREATE_TIME_", new Date());
            statusList.add(oneStatus);
            if (tempTaskList.size() % 10 == 0) {
                exportPartsAtlasDao.batchInsertMachineTasks(tempTaskList);
                tempTaskList.clear();
            }
        }
        if (!tempTaskList.isEmpty()) {
            exportPartsAtlasDao.batchInsertMachineTasks(tempTaskList);
            tempTaskList.clear();
        }
        // 记录任务状态历史
        recordTaskStatusHis(statusList);
    }

    //todo C-4 判断本次录入的图册制作任务，当前时间与关联明细的“交货时间”相比间隔是否≤1天（有可能是负值），如果是则自动发起异常反馈不需要审核；
    private void relDemandDetailAbnormal(List<JSONObject> taskObjs) {
        // 异常反馈通知人员
        String noticeUserIdStr = "";
        List<Map<String, String>> roleUsers = commonInfoManager
                .queryUserByGroupNameAndRelType(RdmConst.EXPORT_PARTSATLAS_ABNORMAL_NOTICE, "GROUP-USER-BELONG");
        if (roleUsers != null && !roleUsers.isEmpty()) {
            for (Map<String, String> oneUser : roleUsers) {
                noticeUserIdStr += oneUser.get("USER_ID_") + ",";
            }
            if (StringUtils.isNotBlank(noticeUserIdStr)) {
                noticeUserIdStr = noticeUserIdStr.substring(0, noticeUserIdStr.length() - 1);
            }
        }
        for (JSONObject oneTask : taskObjs) {
            // 判断是否符合
            Date needTime = oneTask.getDate("needTime");
            if (needTime == null) {
                continue;
            }
            long needTimeMillSeconds = needTime.getTime();
            if (needTimeMillSeconds - System.currentTimeMillis() > 24 * 60 * 60 * 1000) {
                continue;
            }
            JSONObject abnormalObj = new JSONObject();
            abnormalObj.put("id", IdUtil.getId());
            abnormalObj.put("abnormalNum",
                    "YCFK-" + DateFormatUtil.getNowUTCDateStr("yyyyMMddHHmmssSSS") + CommonUtil.genereate3Random());
            abnormalObj.put("machinetaskId", oneTask.getString("id"));
            abnormalObj.put("reasonDesc", "图册制作任务接收时间距交货时间<=1天");
            String expectTime = DateFormatUtil.format(DateUtil.addDay(new Date(), 5), "yyyy-MM-dd");
            abnormalObj.put("expectTime", expectTime);
            abnormalObj.put("abnormalStatus", RdmConst.PARTS_ATLAS_ABNORMAL_FB);
            abnormalObj.put("creatorName", "管理员");
            abnormalObj.put("CREATE_BY_", "1");
            abnormalObj.put("CREATE_TIME_", new Date());
            exportPartsAtlasDao.insertTaskAbnormal(abnormalObj);
            //C-4-1 记录异常反馈状态变化
            this.recordOneTaskStatusHis(generateStatusHis(abnormalObj.getString("id"), RdmConst.PARTS_ATLAS_ABNORMAL_FB,
                    "abnormal", "", oneTask.getString("relDemandUserName"), "1"));
            // 异常反馈发布，通知到相应人员（角色“零件图册异常反馈通知人员”）
            /*            JSONObject noticeObj = new JSONObject();
            noticeObj.put("content",
                    "零件图册制作异常反馈已发布，请前往RDM平台“后市场技术”-“零件图册”-“异常反馈列表”查看！" + "\n整机PIN码：" + oneTask.getString("machineCode")
                            + "\n预计完成时间：" + abnormalObj.getString("expectTime") + "\n原因描述："
                            + abnormalObj.getString("reasonDesc") + "\n反馈人：" + abnormalObj.getString("creatorName"));
            sendDDNoticeManager.sendNoticeForCommon(noticeUserIdStr, noticeObj);*/
        }
    }

    //todo C-4-1 记录异常反馈状态变化
    public void recordOneTaskStatusHis(JSONObject statusHis) {
        if (statusHis != null) {
            //C-4-1-1 记录任务的状态变化（解绑并作废）的数据
            this.recordTaskStatusHis(Arrays.asList(statusHis));
        }
    }

    // 创建发运通知单及对应的发运明细
    public void createPartsAtlasDispatchApply(JSONObject result, String postBody) {
        logger.info("CRM推送出口产品零件图册发运通知单，" + postBody);
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        if (postBodyObj == null || postBodyObj.isEmpty()) {
            result.put("success", false);
            result.put("message", "传输的消息内容为空！");
            return;
        }
        JSONObject dispatchResultObj = new JSONObject();
        // 发运通知单号
        String dispatchNo = postBodyObj.getString("SHIPORDER");
        if (StringUtils.isBlank(dispatchNo)) {
            logger.error("CRM创建出口产品零件图册发运通知单失败，发运单号为空！");
            result.put("success", false);
            result.put("message", "发运单号为空！");
            return;
        }
        dispatchResultObj.put("dispatchNo", dispatchNo);
        // 出口国家
        String exportCountryName = postBodyObj.getString("REQCOUNTRY");
        if (StringUtils.isBlank(exportCountryName)) {
            logger.error("CRM创建出口产品零件图册发运通知单失败，出口国家为空！");
            result.put("success", false);
            result.put("message", "需求国家为空！");
            return;
        }
        dispatchResultObj.put("exportCountryName", exportCountryName);
        // 创建人
        String creatorName = postBodyObj.getString("DUTYMAN");
        if (StringUtils.isBlank(creatorName)) {
            logger.error("CRM创建出口产品零件图册发运通知单失败，创建人为空！");
            result.put("success", false);
            result.put("message", "责任人为空！");
            return;
        }
        dispatchResultObj.put("creatorName", creatorName);
        // 区域
        String region = postBodyObj.getString("REGION");
        if (StringUtils.isBlank(region)) {
            logger.error("CRM创建出口产品零件图册发运通知单失败，区域为空！");
            result.put("success", false);
            result.put("message", "区域为空！");
            return;
        }
        dispatchResultObj.put("region", region);
        // 业务员
        String salesMan = postBodyObj.getString("SALESMAN");
        if (StringUtils.isBlank(salesMan)) {
            logger.error("CRM创建出口产品零件图册发运通知单失败，业务员为空！");
            result.put("success", false);
            result.put("message", "业务员为空！");
            return;
        }
        dispatchResultObj.put("salesMan", salesMan);
        // 发运要求
        String dispatchDesc = postBodyObj.getString("REQQUEST");
        if (StringUtils.isBlank(dispatchDesc)) {
            logger.error("CRM创建出口产品零件图册发运通知单失败，发运要求为空！");
            result.put("success", false);
            result.put("message", "发运要求为空！");
            return;
        }
        dispatchResultObj.put("dispatchDesc", dispatchDesc);
        // 发运明细
        JSONArray detailsArr = postBodyObj.getJSONArray("workData");
        if (detailsArr == null || detailsArr.isEmpty()) {
            logger.error("CRM创建出口产品零件图册发运通知单失败，发运明细为空！");
            result.put("success", false);
            result.put("message", "申请设备清单为空！");
            return;
        }
        // 发运通知单号是否唯一
        JSONObject param = new JSONObject();
        param.put("dispatchNo", dispatchNo);
        List<JSONObject> existDispatchs = exportPartsAtlasDao.queryDispatchList(param);
        if (existDispatchs != null && !existDispatchs.isEmpty()) {
            logger.error("CRM创建出口产品零件图册发运通知单发生变更，发运单号“" + dispatchNo + "”！");
            dispatchChangeProcess(existDispatchs.get(0), postBodyObj, dispatchResultObj, result);
            return;
        }
        dispatchResultObj.put("id", IdUtil.getId());
        dispatchResultObj.put("dispatchStatus", "commit");
        dispatchResultObj.put("CREATE_BY_", "1");
        dispatchResultObj.put("CREATE_TIME_", new Date());
        // 解析发运明细
        Map<String, JSONObject> detailNo2ConfigList = new HashMap<>();
        JSONArray configsArr = postBodyObj.getJSONArray("workData");
        if (configsArr != null) {
            parseDispatchData(detailNo2ConfigList, configsArr, dispatchResultObj.getString("id"), result, true);
            if (!result.getBooleanValue("success")) {
                return;
            }
        }
        // 存储发运通知单基本信息
        exportPartsAtlasDao.insertDispatchApi(dispatchResultObj);
        // 存储发运明细信息
        List<JSONObject> tempParam = new ArrayList<>();
        for (Map.Entry<String, JSONObject> entry : detailNo2ConfigList.entrySet()) {
            tempParam.add(entry.getValue());
            if (tempParam.size() % 10 == 0) {
                exportPartsAtlasDao.batchInsertDispatchDetails(tempParam);
                tempParam.clear();
            }
        }
        if (!tempParam.isEmpty()) {
            exportPartsAtlasDao.batchInsertDispatchDetails(tempParam);
            tempParam.clear();
        }
    }


    // 解析CRM接口传输的发运明细信息
    private void parseDispatchData(Map<String, JSONObject> detailNo2Obj, JSONArray detailsArr, String dispatchId,
                                   JSONObject result, boolean generateId) {
        for (int detailIndex = 0; detailIndex < detailsArr.size(); detailIndex++) {
            // 发运编号（需求编号复用）
            String detailNo = detailsArr.getJSONObject(detailIndex).getString("LISTNO");
            if (StringUtils.isBlank(detailNo)) {
                logger.error("CRM创建出口产品零件图册发运通知单失败，发运明细编号为空！");
                result.put("success", false);
                result.put("message", "清单号为空！");
                return;
            }
            // 根据需求编号，查询需求单的全部行信息，包括需求通知单号，物料代码，设计型号，销售型号，数量？随机文件语言
            JSONObject param = new JSONObject();
            param.put("detailNo", detailNo);
            List<JSONObject> dataList = exportPartsAtlasDao.queryDemandListByDetailNo(param);
            if (dataList.size() != 1) {
                logger.error("CRM创建出口产品零件图册发运通知单失败，系统无此需求明细信息！");
                result.put("success", false);
                result.put("message", "系统无此需求明细信息！");
                return;
            }
            // 数量
            String needNumber = detailsArr.getJSONObject(detailIndex).getString("PLANNUM");
            if (StringUtils.isBlank(needNumber)) {
                logger.error("CRM创建出口产品零件图册发运通知单失败，数量为空！");
                result.put("success", false);
                result.put("message", "数量为空！");
                return;
            }
            if (Integer.parseInt(needNumber) > Integer.parseInt(dataList.get(0).getString("needNumber"))) {
                logger.error("CRM创建出口产品零件图册发运通知单失败，数量超过需求最大值！");
                result.put("success", false);
                result.put("message", "数量超过需求最大值！");
                return;
            }
            //整车编号
            String vehicleNumber = detailsArr.getJSONObject(detailIndex).getString("VEHICLENUMBER");
            if (StringUtils.isBlank(vehicleNumber)) {
                logger.error("CRM创建出口产品零件图册发运通知单失败，整车编号为空！");
                result.put("success", false);
                result.put("message", "整车编号为空！");
                return;
            }
            //车辆配置
            String cardisPose = detailsArr.getJSONObject(detailIndex).getString("CARDISPOSE");
            if (StringUtils.isBlank(cardisPose)) {
                logger.error("CRM创建出口产品零件图册发运通知单失败，车辆配置为空！");
                result.put("success", false);
                result.put("message", "车辆配置为空！");
                return;
            }
            //发车开始时间
            String departurestartTime = detailsArr.getJSONObject(detailIndex).getString("DEPARTURESTARTTIME");
            if (StringUtils.isBlank(departurestartTime)) {
                logger.error("CRM创建出口产品零件图册发运通知单失败，发车开始时间为空！");
                result.put("success", false);
                result.put("message", "发车开始时间为空！");
                return;
            }
            //发车结束时间
            String departureendTime = detailsArr.getJSONObject(detailIndex).getString("DEPARTUREENDTIME");
            if (StringUtils.isBlank(departureendTime)) {
                logger.error("CRM创建出口产品零件图册发运通知单失败，发车结束时间为空！");
                result.put("success", false);
                result.put("message", "发车结束时间为空！");
                return;
            }
            //目的地
            String destination = detailsArr.getJSONObject(detailIndex).getString("DESTINATION");
            if (StringUtils.isBlank(destination)) {
                logger.error("CRM创建出口产品零件图册发运通知单失败，目的地为空！");
                result.put("success", false);
                result.put("message", "目的地为空！");
                return;
            }
            //实际发车国家
            String actualdepartureCountry = detailsArr.getJSONObject(detailIndex).getString("ACTUALDEPARTURECOUNTRY");
            if (StringUtils.isBlank(actualdepartureCountry)) {
                logger.error("CRM创建出口产品零件图册发运通知单失败，实际发车国家为空！");
                result.put("success", false);
                result.put("message", "实际发车国家为空！");
                return;
            }
            JSONObject detailsResultObj = new JSONObject();
            if (generateId) {
                detailsResultObj.put("id", IdUtil.getId());
            }
            detailsResultObj.put("dispatchId", dispatchId);
            detailsResultObj.put("detailNo", detailNo);
            detailsResultObj.put("demandNo", dataList.get(0).getString("demandNo"));
            detailsResultObj.put("matCode", dataList.get(0).getString("matCode"));
            detailsResultObj.put("designType", dataList.get(0).getString("designType"));
            detailsResultObj.put("saleType", dataList.get(0).getString("saleType"));
            detailsResultObj.put("departurestartTime", departurestartTime);
            detailsResultObj.put("cardisPose", cardisPose);
            detailsResultObj.put("vehicleNumber", vehicleNumber);
            detailsResultObj.put("departureendTime", departureendTime);
            detailsResultObj.put("destination", destination);
            detailsResultObj.put("actualdepartureCountry", actualdepartureCountry);
            // 传入数量信息
            detailsResultObj.put("needNumber", needNumber);
            detailsResultObj.put("languages", dataList.get(0).getString("languages"));
            detailsResultObj.put("CREATE_BY_", "1");

            detailsResultObj.put("CREATE_TIME_", new Date());
            detailNo2Obj.put(detailNo, detailsResultObj);
        }
    }

    /**
     * CRM中传递的发运通知单信息变更（识别并存储变更内容，更改发运通知单）
     *
     * @param dispatchApply
     * @param result
     */
    private void dispatchChangeProcess(JSONObject dispatchApply, JSONObject postBodyObj, JSONObject dispatchResultObj,
                                       JSONObject result) {
        // 查询这个发运通知单当前数据库中所有的需求明细和配置信息，并进行组装
        Map<String, JSONObject> detailNo2Obj = new HashMap<>();
        List<JSONObject> dispatchDetails = getDispatchDetailList(dispatchApply.getString("id"));
        if (dispatchDetails == null || dispatchDetails.isEmpty()) {
            logger.error("发运通知单“" + dispatchApply.getString("dispatchNo") + "”原有发运明细为空！");
            result.put("success", false);
            result.put("message", "原有发运明细为空，不支持明细新增！");
            return;
        }
        // 先更新基础表
        JSONObject dispatchBasic = new JSONObject();
        dispatchBasic.put("id", dispatchApply.getString("id"));
        dispatchBasic.putAll(dispatchResultObj);
        dispatchBasic.put("UPDATE_BY_", "1");
        dispatchBasic.put("UPDATE_TIME_", new Date());
        dispatchBasic.put("dispatchStatus", "commit");
        exportPartsAtlasDao.updateDispatchApi(dispatchBasic);
        // 做记录
        StringBuilder dispatchChangeSb = new StringBuilder();
        boolean isDetailChange = false;
        // 责任人变化
        if (!dispatchApply.getString("creatorName").equalsIgnoreCase(dispatchResultObj.getString("creatorName"))) {
            dispatchChangeSb.append("发运通知单" + dispatchResultObj.getString("dispatchNo"))
                    .append("：“责任人”变更为" + dispatchResultObj.getString("creatorName"))
                    .append("，原“责任人”" + dispatchApply.getString("creatorName") + "<br>");
            isDetailChange = true;
        }
        // 出口国家变化
        if (!dispatchApply.getString("exportCountryName")
                .equalsIgnoreCase(dispatchResultObj.getString("exportCountryName"))) {
            dispatchChangeSb.append("发运通知单" + dispatchResultObj.getString("dispatchNo"))
                    .append("：“出口国家”变更为" + dispatchResultObj.getString("exportCountryName"))
                    .append("，原“出口国家”" + dispatchApply.getString("exportCountryName") + "<br>");
            isDetailChange = true;
        }
        // 区域变化
        if (!dispatchApply.getString("region").equalsIgnoreCase(dispatchResultObj.getString("region"))) {
            dispatchChangeSb.append("发运通知单" + dispatchResultObj.getString("dispatchNo"))
                    .append("：“区域”变更为" + dispatchResultObj.getString("region"))
                    .append("，原“区域”" + dispatchApply.getString("region") + "<br>");
            isDetailChange = true;
        }
        // 业务员变化
        if (!dispatchApply.getString("salesMan").equalsIgnoreCase(dispatchResultObj.getString("salesMan"))) {
            dispatchChangeSb.append("发运通知单" + dispatchResultObj.getString("dispatchNo"))
                    .append("：“业务员”变更为" + dispatchResultObj.getString("salesMan"))
                    .append("，原“业务员”" + dispatchApply.getString("salesMan") + "<br>");
            isDetailChange = true;
        }
        // 发运配置明细
        if (!dispatchApply.getString("dispatchDesc").equalsIgnoreCase(dispatchResultObj.getString("dispatchDesc"))) {
            dispatchChangeSb.append("发运通知单" + dispatchResultObj.getString("dispatchNo"))
                    .append("：“发运配置明细”变更为" + dispatchResultObj.getString("dispatchDesc"))
                    .append("，原“发运配置明细”" + dispatchApply.getString("dispatchDesc") + "<br>");
            isDetailChange = true;
        }
        if (isDetailChange) {
            JSONObject changeNoticeObj = new JSONObject();
            changeNoticeObj.put("id", IdUtil.getId());
            changeNoticeObj.put("changeType", "发运通知单");
            changeNoticeObj.put("changeTitle", "发运通知单" + dispatchBasic.getString("dispatchNo"));
            changeNoticeObj.put("changeDesc", dispatchChangeSb.toString());
            changeNoticeObj.put("changeConfirm", "未确认");
            changeNoticeObj.put("CREATE_BY_", "1");
            changeNoticeObj.put("CREATE_TIME_", new Date());
            exportPartsAtlasDao.insertChangeNotice(changeNoticeObj);
        }

        // 更新明细（删增）
        // 解析发运明细（组合）
        Map<String, JSONObject> detailNo2ConfigList = new HashMap<>();
        JSONArray configsArr = postBodyObj.getJSONArray("workData");
        if (configsArr != null) {
            parseDispatchData(detailNo2ConfigList, configsArr, dispatchResultObj.getString("id"), result, false);
            if (!result.getBooleanValue("success")) {
                return;
            }
        }
        JSONObject param = new JSONObject();
        param.clear();
        Set<String> dispatchDetailIds = new HashSet<>();
        for (JSONObject oneDetail : dispatchDetails) {
            dispatchDetailIds.add(oneDetail.getString("id"));
            oneDetail.put("configNo2Obj", new JSONObject());
            detailNo2Obj.put(oneDetail.getString("detailNo"), oneDetail);
        }
        param.put("dispatchDetailIds", dispatchDetailIds);
        List<JSONObject> configList = exportPartsAtlasDao.queryDetailConfigs(param);
        for (JSONObject oneConfig : configList) {
            String detailNo = oneConfig.getString("detailNo");
            JSONObject oneDetail = detailNo2Obj.get(detailNo);
            oneDetail.getJSONObject("configNo2Obj").put(oneConfig.getString("configNo"), oneConfig);
        }

        // 解析本次传输的发运明细及配置信息，并进行组装
        Map<String, JSONObject> detailNo2ObjCrm = new HashMap<>();
        parseDispatchData(detailNo2ObjCrm, postBodyObj.getJSONArray("workData"), dispatchApply.getString("id"), result,
                false);
        if (!result.getBooleanValue("success")) {
            return;
        }
        // 比对本次传输的和已有的之间的区别
        compareDispatchDetailChanges(detailNo2Obj, detailNo2ObjCrm, dispatchApply.getString("dispatchNo"), result);
    }


    // 比对数据库中已有的和本次传递的区别，记录变化内容，更新发运清单或者配置清单（需求清单只能更新，配置清单只能增减）
    private void compareDispatchDetailChanges(Map<String, JSONObject> detailNo2ObjExist,
                                              Map<String, JSONObject> detailNo2ObjCrm, String dispatchNo, JSONObject result) {
        StringBuilder dispatchChangeSb = new StringBuilder();
        for (Map.Entry<String, JSONObject> entry : detailNo2ObjCrm.entrySet()) {
            String detailNo = entry.getKey();
            if (!detailNo2ObjExist.containsKey(detailNo)) {
                logger.error("发运通知单“" + dispatchNo + "”中不包含需求编号为“" + detailNo + "”的数据！");
                result.put("success", false);
                result.put("message", "RDM中不含需求编号为“" + detailNo + "”的数据，无法更新！");
                return;
            }
            JSONObject detailObjCrm = entry.getValue();
            JSONObject detailObjExist = detailNo2ObjExist.get(detailNo);
            boolean isDetailChange = false;
            // 当前仅比较数量、随机文件语言、交货日期和配置清单的变化
            if (!detailObjCrm.getString("needNumber").equalsIgnoreCase(detailObjExist.getString("needNumber"))) {
                dispatchChangeSb.append("设备清单号" + detailNo).append("：“数量”变更为" + detailObjCrm.getString("needNumber"))
                        .append("，原“数量”" + detailObjExist.getString("needNumber") + "<br>");
                detailObjExist.put("needNumber", detailObjCrm.getString("needNumber"));
                isDetailChange = true;
            }
            if (judgeLanguageChange(detailObjCrm.getString("languages"), detailObjExist.getString("languages"))) {
                dispatchChangeSb.append("设备清单号" + detailNo).append("：“随机语言”变更为" + detailObjCrm.getString("languages"))
                        .append("，原“随机语言”" + detailObjExist.getString("languages") + "<br>");
                detailObjExist.put("languages", detailObjCrm.getString("languages"));
                isDetailChange = true;
            }
            // 更新需求明细
            if (isDetailChange) {
                detailObjExist.put("UPDATE_BY_", "1");
                detailObjExist.put("UPDATE_TIME_", new Date());
                exportPartsAtlasDao.updateDispatchDetailApi(detailObjExist);
            }
        }
        // 组装变更内容并写入表中
        if (dispatchChangeSb.length() != 0) {
            JSONObject changeNoticeObj = new JSONObject();
            changeNoticeObj.put("id", IdUtil.getId());
            changeNoticeObj.put("changeType", "发运通知单");
            changeNoticeObj.put("changeTitle", "发运通知单" + dispatchNo);
            changeNoticeObj.put("changeDesc", dispatchChangeSb.toString());
            changeNoticeObj.put("changeConfirm", "未确认");
            changeNoticeObj.put("CREATE_BY_", "1");
            changeNoticeObj.put("CREATE_TIME_", new Date());
            exportPartsAtlasDao.insertChangeNotice(changeNoticeObj);
        }
    }

    // 判断随机文件语言是否变化（因为是逗号分隔的，字符串不同有可能变了也可能没变）
    private boolean judgeLanguageChange(String newLanguages, String oldLanguages) {
        if (oldLanguages.equalsIgnoreCase(newLanguages)) {
            return false;
        }
        List<String> newLanguageList = Arrays.asList(newLanguages.split(",", -1));
        List<String> oldLanguageList = Arrays.asList(oldLanguages.split(",", -1));
        if (newLanguageList.size() != oldLanguageList.size()) {
            return true;
        }
        for (String newLanguage : newLanguageList) {
            if (!oldLanguageList.contains(newLanguage)) {
                return true;
            }
        }
        return false;
    }


    // 查询数据库中所有的整机制作任务(根据需要过滤掉“作废”状态的)
    private void queryAllMachineTaskInfos(Map<String, JSONObject> machineCode2ObjExist, JSONObject param) {
        List<JSONObject> machineTasks = exportPartsAtlasDao.queryAllMachineTasks(param);
        if (machineTasks != null && !machineTasks.isEmpty()) {
            for (JSONObject oneMachine : machineTasks) {
                String machineCode = oneMachine.getString("machineCode");
                machineCode2ObjExist.put(machineCode, oneMachine);
            }
        }
    }

    // 根据参数查询需求明细信息
    private void queryDemandDetailsByDemandNos(Set<String> demandNos,
                                               Map<String, Map<String, JSONObject>> demandNo2DetailNo2Obj) {
        JSONObject param = new JSONObject();
        param.put("demandNos", demandNos);
        List<JSONObject> demandDetailInfos = exportPartsAtlasDao.queryDemandDetailInfos(param);
        if (demandDetailInfos != null && !demandDetailInfos.isEmpty()) {
            for (JSONObject oneDetail : demandDetailInfos) {
                String demandNo = oneDetail.getString("demandNo");
                String detailNo = oneDetail.getString("detailNo");
                if (!demandNo2DetailNo2Obj.containsKey(demandNo)) {
                    demandNo2DetailNo2Obj.put(demandNo, new HashMap<>());
                }
                demandNo2DetailNo2Obj.get(demandNo).put(detailNo, oneDetail);
            }
        }
    }


    // 接收MES推送的发运明细中的整机信息
    public void createPartsAtlasDispatchMachine(JSONObject result, String postBody) {
        logger.info("MES推送出口产品零件图册发运明细中整机信息，" + postBody);
        JSONArray postBodyObjArr = JSONArray.parseArray(postBody);
        if (postBodyObjArr == null || postBodyObjArr.isEmpty()) {
            result.put("success", false);
            result.put("message", "传输的消息内容为空！");
            return;
        }
        JSONObject dispatchResultObj = new JSONObject();
        // 传入参数
        // machineCode,relDispatchUserName,changeApply,detailNo(需求)-->id*2
        // dispatchId,dispatchDetailId,relDispatchTime
        // queryMachineTasksByMachineCode
        for (int index = 0; index < postBodyObjArr.size(); index++) {
            JSONObject oneObj = postBodyObjArr.getJSONObject(index);
            // 判断整机编号是否为空
            String machineCode = oneObj.getString("machineCode");
            if (StringUtils.isBlank(machineCode)) {
                logger.error("推送出口产品零件图册发运明细中整机信息失败，整机PIN码为空！");
                result.put("success", false);
                result.put("message", "整机id为空！");
                return;
            }
            dispatchResultObj.put("machineCode", machineCode);
            // 判断整机编号是否为空
            String relDispatchUserName = oneObj.getString("relDispatchUserName");
            if (StringUtils.isBlank(relDispatchUserName)) {
                logger.error("推送出口产品零件图册发运明细中整机信息失败，关联发运明细操作人为空！");
                result.put("success", false);
                result.put("message", "操作人为空！");
                return;
            }
            dispatchResultObj.put("relDispatchUserName", relDispatchUserName);
            // 改制订单信息是否为空
            String changeApply = oneObj.getString("changeApply");
            if (StringUtils.isBlank(changeApply)) {
                logger.error("推送出口产品零件图册发运明细中整机信息失败，改制订单信息为空！");
                result.put("success", false);
                result.put("message", "改制订单信息为空！");
                return;
            }
            dispatchResultObj.put("changeApply", changeApply);
            // 需求编号是否为空
            String detailNo = oneObj.getString("detailNo");
            if (StringUtils.isBlank(detailNo)) {
                logger.error("推送出口产品零件图册发运明细中整机信息失败，需求编号为空！");
                result.put("success", false);
                result.put("message", "需求编号为空！");
                return;
            }
            // 由 “需求编号” 查询“发运订单id”和“发运订单明细id”
            JSONObject param = new JSONObject();
            param.put("detailNo", detailNo);
            List<JSONObject> dataList = exportPartsAtlasDao.queryDispatchDetailsByDetailNo(param);
            if (dataList.size() == 1) {
                String dispatchId = dataList.get(0).getString("dispatchId");
                String dispatchDetailId = dataList.get(0).getString("id");
                String departTime = dataList.get(0).getString("departTime");

                // 检验departTime
                String regEx = "^(\\d{8})$"; // 8位数字
                Pattern pattern = Pattern.compile(regEx);
                Matcher matcher = pattern.matcher(departTime);
                boolean rs = matcher.matches();

                // 若是八位数字，则按照日期格式截取
                // 不是八位数字，则赋值当前操作时间，用于排查错误
                // 均转为"YYYY-MM-dd"格式，与后续TMS送入时间格式保持一致
                if (rs) {
                    String YYYY = departTime.substring(0, 4);
                    String MM = departTime.substring(4, 6);
                    String dd = departTime.substring(6, 8);
                    departTime = YYYY + "-" + MM + "-" + dd;
                } else {
                    Date date = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
                    departTime = dateFormat.format(date);
                }

                dispatchResultObj.put("dispatchId", dispatchId);
                dispatchResultObj.put("dispatchDetailId", dispatchDetailId);
                dispatchResultObj.put("departTime", departTime);
            } else {
                logger.error("推送出口产品零件图册发运明细中整机信息失败，系统无此发运明细信息！");
                result.put("success", false);
                result.put("message", "系统无此发运明细信息！");
                return;
            }

            // 根据machineCode是否存在来判断新增和修改
            param.clear();
            param.put("machineCode", machineCode);
            param.put("filterZf", "yes");
            List<JSONObject> listMachineTask = exportPartsAtlasDao.queryAllMachineTasks(param);

            // 拟合参数
            dispatchResultObj.put("relDispatchTime", new Date());

            // 数据操作
            if (listMachineTask.size() == 1) {
                String id = listMachineTask.get(0).getString("id");
                dispatchResultObj.put("id", id);
                exportPartsAtlasDao.updateTaskStatusApi(dispatchResultObj);
                result.put("success", true);
                result.put("message", "操作成功！");
            } else {
                // 错误
                logger.error("推送出口产品零件图册发运明细中整机信息失败，整机编号无对应的需求任务实例！");
                result.put("success", false);
                result.put("message", "整机编号无对应的需求任务实例！");
                return;
            }

        }

    }

    // TMS推送出口产品零件图册发运明细中整机发运信息
    public void batchUpdatePartsAtlasDispatchTime(JSONObject result, String postBody) {
        logger.info("TMS推送出口产品零件图册发运明细中整机发运信息，" + postBody);
        JSONArray postBodyObjArr = JSONArray.parseArray(postBody);
        if (postBodyObjArr == null || postBodyObjArr.isEmpty()) {
            result.put("success", false);
            result.put("message", "传输的消息内容为空！");
            return;
        }

        List<JSONObject> updateList = new ArrayList<>();

        for (int index = 0; index < postBodyObjArr.size(); index++) {
            JSONObject dispatchResultObj = new JSONObject();
            JSONObject oneObj = postBodyObjArr.getJSONObject(index);
            // 判断整机编号是否为空
            String machineCode = oneObj.getString("machineCode");
            if (StringUtils.isBlank(machineCode)) {
                logger.error("推送出口产品零件图册发运明细中整机信息失败，整机PIN码为空！");
                result.put("success", false);
                result.put("message", "整机id为空！");
                return;
            }
            // 判断整机编号是否存在
            JSONObject carObj = exportPartsAtlasDao.getCarIsExist(machineCode);
            if (carObj == null) {
                logger.error("RDM 整机编号不存在！");
                result.put("success", false);
                result.put("message", "RDM 整机编号不存在！");
                return;
            }
            dispatchResultObj.put("machineCode", machineCode);
            // 发运时间
            String departTime = oneObj.getString("departTime");
            if (StringUtils.isBlank(departTime)) {
                logger.error("推送出口产品零件图册发运明细中整机信息失败，发运时间为空！");
                result.put("success", false);
                result.put("message", "发运时间为空！");
                return;
            }
            dispatchResultObj.put("departTime", departTime);
            updateList.add(dispatchResultObj);
        }

        exportPartsAtlasDao.batchAtlasDispatchTime(updateList);
        result.put("success", true);
        result.put("message", "操作成功！");
        return;

    }

    public JSONObject getDemandInfo(String demandId) {
        JSONObject jsonObject = exportPartsAtlasDao.getDemandInfoById(demandId);
        if (jsonObject == null) {
            return new JSONObject();
        }
        String createTime = jsonObject.getString("CREATE_TIME_");
        if (StringUtils.isNotBlank(createTime)) {
            jsonObject.put("CREATE_TIME_", DateFormatUtil.format(jsonObject.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
        }
        return jsonObject;
    }

    public JSONObject getDemandInfoByNo(String demandNo) {
        JSONObject jsonObject = exportPartsAtlasDao.getDemandInfoByNo(demandNo);
        if (jsonObject == null) {
            return new JSONObject();
        }
        String createTime = jsonObject.getString("CREATE_TIME_");
        if (StringUtils.isNotBlank(createTime)) {
            jsonObject.put("CREATE_TIME_", DateFormatUtil.format(jsonObject.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
        }
        return jsonObject;
    }

    //查询需求清单
    public List<JSONObject> getDemandDetailList(String demandId) {
        List<JSONObject> itemList = exportPartsAtlasDao.getDemandDetailList(demandId);
        return itemList;
    }

    public JsonPageResult<?> getAllDemandDetailList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject queryParam = new JSONObject();
        // 分页
        int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
        int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
        queryParam.put("startIndex", "0" + pageIndex * pageSize);
        queryParam.put("pageSize", "0" + pageSize);
        // 排序
        queryParam.put("sortField", "demandNo,detailNo");
        queryParam.put("sortOrder", "asc");
        // 过滤条件
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    queryParam.put(name, value);
                }
            }
        }
        List<JSONObject> itemList = exportPartsAtlasDao.getAllDemandDetailList(queryParam);
        result.setData(itemList);
        int countNum = exportPartsAtlasDao.countAllDemandDetailList(queryParam);
        result.setTotal(countNum);
        return result;
    }

    // 查询某条清单中的整机
    public List<JSONObject> getMachineList(HttpServletRequest request) {
        String demandId = RequestUtil.getString(request, "demandId", "");
        String demandDetailId = RequestUtil.getString(request, "demandDetailId", "");
        String dispatchId = RequestUtil.getString(request, "dispatchId", "");
        String dispatchDetailId = RequestUtil.getString(request, "dispatchDetailId", "");
        if (StringUtils.isBlank(demandDetailId) && StringUtils.isBlank(dispatchDetailId)) {
            return Collections.emptyList();
        }
        JSONObject param = new JSONObject();
        if (StringUtils.isNotBlank(demandId)) {
            param.put("demandId", demandId);
        }
        if (StringUtils.isNotBlank(demandId)) {
            param.put("demandDetailId", demandDetailId);
        }
        if (StringUtils.isNotBlank(dispatchId)) {
            param.put("dispatchId", dispatchId);
        }
        if (StringUtils.isNotBlank(dispatchDetailId)) {
            param.put("dispatchDetailId", dispatchDetailId);
        }
        List<JSONObject> itemList = exportPartsAtlasDao.getMachineList(param);
        for (JSONObject oneItem : itemList) {
            if (oneItem.getDate("relDemandTime") != null) {
                oneItem.put("relDemandTime", DateFormatUtil.format(oneItem.getDate("relDemandTime"), "yyyy-MM-dd"));
            }
            if (oneItem.getDate("relDispatchTime") != null) {
                oneItem.put("relDispatchTime", DateFormatUtil.format(oneItem.getDate("relDispatchTime"), "yyyy-MM-dd"));
            }
        }
        return itemList;
    }

    // 查询某条清单(需求、发运)中的配置信息
    public List<JSONObject> getDetailConfigList(HttpServletRequest request) {
        String demandId = RequestUtil.getString(request, "demandId", "");
        String demandDetailId = RequestUtil.getString(request, "demandDetailId", "");
        String dispatchId = RequestUtil.getString(request, "dispatchId", "");
        String dispatchDetailId = RequestUtil.getString(request, "dispatchDetailId", "");
        if (StringUtils.isBlank(demandDetailId) && StringUtils.isBlank(dispatchDetailId)) {
            return Collections.emptyList();
        }
        JSONObject param = new JSONObject();
        if (StringUtils.isNotBlank(demandId)) {
            param.put("demandId", demandId);
        }
        if (StringUtils.isNotBlank(demandId)) {
            param.put("demandDetailId", demandDetailId);
        }
        if (StringUtils.isNotBlank(dispatchId)) {
            param.put("dispatchId", dispatchId);
        }
        if (StringUtils.isNotBlank(dispatchDetailId)) {
            param.put("dispatchDetailId", dispatchDetailId);
        }
        List<JSONObject> itemList = exportPartsAtlasDao.queryDetailConfigs(param);
        return itemList;
    }

    public JsonPageResult<?> taskListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        String scene = RequestUtil.getString(request, "scene", "");
        if (StringUtils.isBlank(scene)) {
            result.setSuccess(false);
            result.setMessage("查询场景为空！");
            return result;
        }
        JSONObject params = new JSONObject();
        String export = RequestUtil.getString(request, "export", "");
        if (StringUtils.isBlank(export)) {
            getListParams(params, request, "taskStatus,service_engineering_exportpartsatlas_machinetask.CREATE_TIME_",
                    "desc", true);
        } else {
            getListParams(params, request, "taskStatus,service_engineering_exportpartsatlas_machinetask.CREATE_TIME_",
                    "desc", false);
        }

        // 个人任务页面
        if (scene.equalsIgnoreCase("self")) {
            // 判断是否是零件图册档案室人员
            boolean exportPartsAtlasArchive = commonInfoManager.judgeUserIsPointRole(RdmConst.EXPORT_PARTSATLAS_ARCHIVE,
                    ContextUtil.getCurrentUserId());
            if (exportPartsAtlasArchive) {
                if (StringUtils.isBlank(params.getString("taskStatus"))) {
                    params.put("taskStatus",
                            Arrays.asList(RdmConst.PARTS_ATLAS_STATUS_ZZWCYZC, RdmConst.PARTS_ATLAS_STATUS_YJS));
                }
            } else {
                params.put("taskUserId", ContextUtil.getCurrentUserId());
                if (StringUtils.isBlank(params.getString("taskStatus"))) {
                    params.put("taskStatus",
                            Arrays.asList(RdmConst.PARTS_ATLAS_STATUS_YLQ, RdmConst.PARTS_ATLAS_STATUS_JXZZSQ,
                                    RdmConst.PARTS_ATLAS_STATUS_JXZZ, RdmConst.PARTS_ATLAS_STATUS_SLZZ,
                                    RdmConst.PARTS_ATLAS_STATUS_GZ));
                }
            }
        }
        List<JSONObject> taskList = exportPartsAtlasDao.queryAtlasTaskList(params);
        List<String> machinetaskIds = new ArrayList<>();
        for (JSONObject oneTask : taskList) {
            if (oneTask.get("CREATE_TIME_") != null) {
                oneTask.put("CREATE_TIME_", DateUtil.formatDate(oneTask.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (oneTask.get("relDemandTime") != null) {
                oneTask.put("relDemandTime", DateUtil.formatDate(oneTask.getDate("relDemandTime"), "yyyy-MM-dd"));
            }
            if (oneTask.get("relDispatchTime") != null) {
                oneTask.put("relDispatchTime", DateUtil.formatDate(oneTask.getDate("relDispatchTime"), "yyyy-MM-dd"));
            }
            if (oneTask.get("reDispatchTime") != null) {
                oneTask.put("reDispatchTime", DateUtil.formatDate(oneTask.getDate("reDispatchTime"), "yyyy-MM-dd"));
            }
            machinetaskIds.add(oneTask.getString("id"));
        }
        result.setData(taskList);
        int taskListTotal = exportPartsAtlasDao.countAtlasTaskList(params);
        result.setTotal(taskListTotal);
        if (machinetaskIds.isEmpty()) {
            return result;
        }
        // 查询制作任务关联的最新“发布”状态的异常反馈信息及是否存在运行中的异常反馈
        params.clear();
        params.put("machinetaskIds", machinetaskIds);
        params.put("sortField",
                "service_engineering_exportpartsatlas_taskabnormal.machinetaskId,service_engineering_exportpartsatlas_taskabnormal.CREATE_TIME_");
        params.put("sortOrder", "desc");
        List<JSONObject> abnormalList = exportPartsAtlasDao.queryAbnormalList(params);
        Map<String, JSONObject> machinetaskId2AbnormalInfo = new HashMap<>();
        for (JSONObject oneAbnormal : abnormalList) {
            String machinetaskId = oneAbnormal.getString("machinetaskId");
            String abnormalStatus = oneAbnormal.getString("abnormalStatus");
            if (!machinetaskId2AbnormalInfo.containsKey(machinetaskId)) {
                machinetaskId2AbnormalInfo.put(machinetaskId, new JSONObject());
            }
            JSONObject machineAbnormalInfo = machinetaskId2AbnormalInfo.get(machinetaskId);
            if (RdmConst.PARTS_ATLAS_ABNORMAL_BZ.equalsIgnoreCase(abnormalStatus)
                    || RdmConst.PARTS_ATLAS_ABNORMAL_SH.equalsIgnoreCase(abnormalStatus)) {
                machineAbnormalInfo.put("hasExistRunningAbnormal", "true");
            }
            if (RdmConst.PARTS_ATLAS_ABNORMAL_FB.equalsIgnoreCase(abnormalStatus)
                    && StringUtils.isBlank(machineAbnormalInfo.getString("expectTime"))) {
                machineAbnormalInfo.put("expectTime", oneAbnormal.getString("expectTime"));
                machineAbnormalInfo.put("reasonDesc", oneAbnormal.getString("reasonDesc"));
            }
        }
        for (JSONObject oneTask : taskList) {
            String machinetaskId = oneTask.getString("id");
            if (machinetaskId2AbnormalInfo.containsKey(machinetaskId)) {
                oneTask.put("hasExistRunningAbnormal",
                        machinetaskId2AbnormalInfo.get(machinetaskId).getString("hasExistRunningAbnormal"));
                oneTask.put("expectTime", machinetaskId2AbnormalInfo.get(machinetaskId).getString("expectTime"));
                oneTask.put("reasonDesc", machinetaskId2AbnormalInfo.get(machinetaskId).getString("reasonDesc"));
            }
        }
        return result;
    }

    public void taskExport(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = taskListQuery(request, response);

        List<Map<String, Object>> listData = result.getData();
        List<JSONObject> taskList = new ArrayList<>();
        int index = 0;
        for (Map<String, Object> data : listData) {
            JSONObject jsonObject = new JSONObject(data);
            jsonObject.put("index", ++index);
            String taskStatus = jsonObject.getString("taskStatus");
            if (taskStatus != null) {
                switch (taskStatus) {
                    case "00WLQ":
                        taskStatus = "未领取";
                        break;
                    case "01YLQ":
                        taskStatus = "已领取";
                        break;
                    case "02JXZZSQ":
                        taskStatus = "机型制作申请中";
                        break;
                    case "03JXZZ":
                        taskStatus = "机型制作中";
                        break;
                    case "04SLZZ":
                        taskStatus = "实例制作中";
                        break;
                    case "05GZ":
                        taskStatus = "改制中";
                        break;
                    case "06ZZWCYZC":
                        taskStatus = "制作完成已转出";
                        break;
                    case "07YJS":
                        taskStatus = "档案室已接收";
                        break;
                    case "08ZF":
                        taskStatus = "作废";
                        break;
                }
            }
            jsonObject.put("taskStatus", taskStatus);
            taskList.add(jsonObject);
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "零件图册制作任务管理导出表";
        String excelName = nowDate + title;
        String export = RequestUtil.getString(request, "export", "");
        if (export.equals("self")) {
            String[] fieldNames = {"序号", "任务状态", "制作风险", "整机PIN码", "需求通知单号", "整机物料编码", "设计型号", "销售型号", "随机文件语言",
                    "交货时间(需求清单)", "入库时间", "整机关联需求清单时间(RDM接收)", "发运通知单号", "发车时间(发运清单)", "整机关联发运清单时间(RDM接收)", "发车时间",
                    "异常反馈预计完成时间(已发布)", "异常反馈原因(已发布)", "换车号场景", "补发时间"};
            String[] fieldCodes = {"index", "taskStatus", "xx", "machineCode", "demandNo", "matCode", "designType",
                    "saleType", "languages", "needTime", "inTime", "relDemandTime", "dispatchNo", "departTime",
                    "relDispatchTime", "dispatchTime", "expectTime", "reasonDesc", "exchangeScene", "reDispatchTime"};

            HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(taskList, fieldNames, fieldCodes, title);
            // 输出
            ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
        } else {
            String[] fieldNames = {"序号", "图册制作人", "任务状态", "制作风险", "整机PIN码", "需求通知单号", "整机物料编码", "设计型号", "销售型号",
                    "随机文件语言", "交货时间(需求清单)", "入库时间", "整机关联需求清单时间(RDM接收)", "发运通知单号", "发车时间(发运清单)", "整机关联发运清单时间(RDM接收)",
                    "发车时间", "异常反馈预计完成时间(已发布)", "异常反馈原因(已发布)", "换车号场景", "补发时间"};
            String[] fieldCodes =
                    {"index", "taskUserName", "taskStatus", "xx", "machineCode", "demandNo", "matCode", "designType",
                            "saleType", "languages", "needTime", "inTime", "relDemandTime", "dispatchNo", "departTime",
                            "relDispatchTime", "dispatchTime", "expectTime", "reasonDesc", "exchangeScene", "reDispatchTime"};

            HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(taskList, fieldNames, fieldCodes, title);
            // 输出
            ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
        }

    }

    // 查询状态变化历史记录表
    public List<JSONObject> statusHisQuery(String busKeyId, String scene) {
        JSONObject param = new JSONObject();
        param.put("busKeyId", busKeyId);
        param.put("scene", scene);
        List<JSONObject> statusHisList = exportPartsAtlasDao.queryStatusHisList(param);
        for (JSONObject oneStatus : statusHisList) {
            if (oneStatus.get("CREATE_TIME_") != null) {
                oneStatus.put("CREATE_TIME_", DateUtil.formatDate(oneStatus.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return statusHisList;
    }

    // 任务领取或者释放
    public JsonResult taskReceiceOrRelease(HttpServletRequest request) {
        JsonResult result = new JsonResult();
        String scene = RequestUtil.getString(request, "scene", "");
        String ids = RequestUtil.getString(request, "ids", "");
        if (StringUtils.isBlank(scene) || StringUtils.isBlank(ids)) {
            result.setMessage("操作失败，参数为空！");
            return result;
        }
        List<String> idList = Arrays.asList(ids.split(",", -1));
        JSONObject param = new JSONObject();
        param.put("idList", idList);
        if (RdmConst.PARTS_ATLAS_TASK_RECEIVE.equalsIgnoreCase(scene)) {
            param.put("taskStatus", Arrays.asList(RdmConst.PARTS_ATLAS_STATUS_WLQ));
        } else {
            param.put("taskStatus", Arrays.asList(RdmConst.PARTS_ATLAS_STATUS_YLQ, RdmConst.PARTS_ATLAS_STATUS_SLZZ));
        }
        List<JSONObject> okTask = exportPartsAtlasDao.queryAtlasTaskList(param);
        if (okTask == null || okTask.isEmpty()) {
            result.setMessage("操作失败，不存在可以领取或释放的任务！");
            return result;
        }
        List<String> finalIdList = new ArrayList<>();
        for (JSONObject oneData : okTask) {
            finalIdList.add(oneData.getString("id"));
        }
        param.put("idList", finalIdList);
        if (RdmConst.PARTS_ATLAS_TASK_RECEIVE.equalsIgnoreCase(scene)) {
            param.put("changeStatus", RdmConst.PARTS_ATLAS_STATUS_YLQ);
            param.put("taskUserId", ContextUtil.getCurrentUserId());
            param.put("taskUserName", ContextUtil.getCurrentUser().getFullname());
        } else {
            param.put("changeStatus", RdmConst.PARTS_ATLAS_STATUS_WLQ);
            param.put("taskUserId", "");
            param.put("taskUserName", "");
        }
        exportPartsAtlasDao.updateTaskStatus(param);
        result.setSuccess(true);
        result.setMessage("操作成功！");

        // 记录任务状态历史
        List<JSONObject> statusList = new ArrayList<>();
        for (JSONObject oneTask : okTask) {
            JSONObject oneStatus = new JSONObject();
            oneStatus.put("id", IdUtil.getId());
            oneStatus.put("busKeyId", oneTask.getString("id"));
            oneStatus.put("statusDesc", RdmConst.PARTS_ATLAS_TASK_RECEIVE.equalsIgnoreCase(scene)
                    ? RdmConst.PARTS_ATLAS_STATUS_YLQ : RdmConst.PARTS_ATLAS_STATUS_WLQ);
            oneStatus.put("scene", "task");
            oneStatus.put("creatorName", ContextUtil.getCurrentUser().getFullname());
            oneStatus.put("optionDesc", "");
            oneStatus.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            oneStatus.put("CREATE_TIME_", new Date());
            statusList.add(oneStatus);
        }
        recordTaskStatusHis(statusList);
        return result;
    }

    // 相同物料编码“已领取”任务的推进("已领取"--->"实例制作")
    public JsonResult taskSlzz(HttpServletRequest request) {
        JsonResult result = new JsonResult();
        String ids = RequestUtil.getString(request, "ids", "");
        if (StringUtils.isBlank(ids)) {
            result.setMessage("操作失败，参数为空！");
            return result;
        }
        List<String> taskIdList = Arrays.asList(ids.split(",", -1));
        // 获取当前该任务的状态
        JSONObject param = new JSONObject();
        param.put("idList", taskIdList);
        List<JSONObject> oneTasks = exportPartsAtlasDao.queryAtlasTaskList(param);
        if (oneTasks == null || oneTasks.isEmpty()) {
            result.setMessage("操作失败，任务不存在！");
            return result;
        }
        String matCode = oneTasks.get(0).getString("matCode");
        // 1、根据整机物料编码查询该任务对应的机型制作流程（作废的除外）
        List<String> status = Arrays.asList(BpmInst.STATUS_DRAFTED, BpmInst.STATUS_RUNNING, BpmInst.STATUS_SUCCESS_END);
        param.clear();
        param.put("matCode", matCode);
        param.put("status", status);


        List<JSONObject> modelMadeList = exportPartsAtlasDao.queryModelMadeList(param);
        // 2、不存在则前端弹框提示启动流程
        if (modelMadeList == null || modelMadeList.isEmpty()) {
            result.setExtPros("startModelMade");
            result.setMessage("启动机型制作流程");
            return result;
        }
        JSONObject oneModelMadeFlow = modelMadeList.get(0);
        // 3、存在提前创建的草稿流程，则认为状态无变化
        if (BpmInst.STATUS_DRAFTED.equalsIgnoreCase(oneModelMadeFlow.getString("status"))) {
            result.setMessage("存在草稿状态的机型制作流程，请跟进处理！");
            return result;
        }
        // 4、存在成功结束的流程，则状态变为“实例制作中”
        if (StringUtils.isBlank(oneModelMadeFlow.getString("status"))
                || BpmInst.STATUS_SUCCESS_END.equalsIgnoreCase(oneModelMadeFlow.getString("status"))) {
            param.clear();
            param.put("idList", taskIdList);
            param.put("originalStatus", RdmConst.PARTS_ATLAS_STATUS_YLQ);
            param.put("changeStatus", RdmConst.PARTS_ATLAS_STATUS_SLZZ);
            exportPartsAtlasDao.updateTaskStatus(param);
            result.setSuccess(true);
            result.setMessage("操作成功！");
            // 记录状态历史
            for (String id : taskIdList) {
                recordOneTaskStatusHis(generateStatusHis(id, RdmConst.PARTS_ATLAS_STATUS_SLZZ, "task", "", "", ""));
            }
            return result;
        }
        // 5、存在运行中的流程，则要根据所在节点进一步判断
        if (BpmInst.STATUS_RUNNING.equalsIgnoreCase(oneModelMadeFlow.getString("status"))) {
            String currentProcessTask = oneModelMadeFlow.getString("currentProcessTask");
            if (RdmConst.PARTS_ATLAS_MODEL_MADE_BZ.equalsIgnoreCase(currentProcessTask)) {
                // 流程在编制节点，则状态不变
                result.setMessage("存在编制状态的机型制作流程，请跟进处理！");
            } else if (RdmConst.PARTS_ATLAS_MODEL_MADE_SQ.equalsIgnoreCase(currentProcessTask)) {
                // 流程在申请节点，则状态变为“机型制作申请中”
                param.clear();
                param.put("idList", taskIdList);
                param.put("originalStatus", RdmConst.PARTS_ATLAS_STATUS_YLQ);
                param.put("changeStatus", RdmConst.PARTS_ATLAS_STATUS_JXZZSQ);
                exportPartsAtlasDao.updateTaskStatus(param);
                result.setSuccess(true);
                result.setMessage("操作成功！");
                // 记录状态历史
                for (String id : taskIdList) {
                    recordOneTaskStatusHis(
                            generateStatusHis(id, RdmConst.PARTS_ATLAS_STATUS_JXZZSQ, "task", "", "", ""));
                }
            } else if (RdmConst.PARTS_ATLAS_MODEL_MADE_ZZ.equalsIgnoreCase(currentProcessTask)) {
                // 流程在制作节点，则状态变为“机型制作中”
                param.clear();
                param.put("idList", taskIdList);
                param.put("originalStatus", RdmConst.PARTS_ATLAS_STATUS_YLQ);
                param.put("changeStatus", RdmConst.PARTS_ATLAS_STATUS_JXZZ);
                exportPartsAtlasDao.updateTaskStatus(param);
                result.setSuccess(true);
                result.setMessage("操作成功！");
                // 记录状态历史
                for (String id : taskIdList) {
                    recordOneTaskStatusHis(generateStatusHis(id, RdmConst.PARTS_ATLAS_STATUS_JXZZ, "task", "", "", ""));
                }
            }
        } else {
            result.setMessage("操作失败，状态未知！");
        }
        return result;
    }

    /**
     * “实例制作”、"改制中"----->"制作完成转出"
     *
     * @param request
     * @return
     */
    public JsonResult taskZzwc(HttpServletRequest request) {
        JsonResult result = new JsonResult();
        String ids = RequestUtil.getString(request, "ids", "");
        if (StringUtils.isBlank(ids)) {
            result.setMessage("操作失败，参数为空！");
            return result;
        }
        // 获取当前该任务的状态
        JSONObject param = new JSONObject();
        param.put("idList", Arrays.asList(ids.split(",", -1)));
        List<JSONObject> oneTasks = exportPartsAtlasDao.queryAtlasTaskList(param);
        if (oneTasks == null || oneTasks.isEmpty()) {
            result.setMessage("操作失败，任务不存在！");
            return result;
        }
        for (JSONObject oneTask : oneTasks) {
            String taskCurStatus = oneTask.getString("taskStatus");
            // 根据当前不同状态判断下一步的状态
            switch (taskCurStatus) {
                case RdmConst.PARTS_ATLAS_STATUS_SLZZ:
                case RdmConst.PARTS_ATLAS_STATUS_GZ:
                    param.put("originalStatus", taskCurStatus);
                    param.put("changeStatus", RdmConst.PARTS_ATLAS_STATUS_ZZWCYZC);
                    param.put("atlasFilePath", RequestUtil.getString(request, "atlasFilePath", ""));
                    param.put("fileDesc", RequestUtil.getString(request, "fileDesc", ""));
                    exportPartsAtlasDao.updateTaskStatus(param);
                    result.setSuccess(true);
                    result.setMessage("操作成功！");
                    // 记录状态历史
                    recordOneTaskStatusHis(generateStatusHis(oneTask.getString("id"),
                            RdmConst.PARTS_ATLAS_STATUS_ZZWCYZC, "task", "", "", ""));
                    break;
                default:
                    break;
            }
        }

        List<List<JSONObject>> groupData = new ArrayList<>();
        int sourceSize = oneTasks.size();
        int size = (oneTasks.size() / 2) + 1;
        for (int i = 0; i < size; i++) {
            List<JSONObject> subset = new ArrayList<>();
            for (int j = i * 2; j < (i + 1) * 2; j++) {
                if (j < sourceSize) {
                    subset.add(oneTasks.get(j));
                }
            }
            groupData.add(subset);
        }
        String deptRespUserIds = "";
        JSONObject msgObj = new JSONObject();
        List<Map<String, String>> depRespMans =
                commonInfoManager.queryUserByGroupNameAndRelType("零件图册钉钉通知人", OsRelType.REL_CAT_GROUP_USER_BELONG);
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                deptRespUserIds += depRespMan.get("USER_ID_") + ",";
            }
        }
        if (StringUtils.isNotBlank(deptRespUserIds)) {
            deptRespUserIds = deptRespUserIds.substring(0, deptRespUserIds.length() - 1);
        }
        for (List<JSONObject> oneDataList : groupData) {
            if (oneDataList.size() > 0) {
                String msg = "零件图册制作需求单号：";
                for (JSONObject oneData : oneDataList) {
                    msg += oneData.getString("demandNo") + "、";
                }
                msg += "制作完成已转出，请及时接收！";
                msgObj.put("content", msg);
                sendDDNoticeManager.sendNoticeForCommon(deptRespUserIds, msgObj);
                msgObj.clear();
            }
        }
        return result;
    }

    public JsonResult taskBack(HttpServletRequest request) {
        JsonResult result = new JsonResult();
        String id = RequestUtil.getString(request, "id", "");
        if (StringUtils.isBlank(id)) {
            result.setMessage("操作失败，参数为空！");
            return result;
        }
        result.setMessage("操作成功！");
        return result;
    }

    // 归档确认接收
    public JsonResult taskGdqr(HttpServletRequest request) {
        JsonResult result = new JsonResult();
        String ids = RequestUtil.getString(request, "ids", "");
        if (StringUtils.isBlank(ids)) {
            result.setMessage("操作失败，参数为空！");
            return result;
        }
        List<String> idList = Arrays.asList(ids.split(",", -1));
        JSONObject param = new JSONObject();
        param.put("idList", idList);
        param.put("taskStatus", Arrays.asList(RdmConst.PARTS_ATLAS_STATUS_ZZWCYZC));
        List<JSONObject> okTask = exportPartsAtlasDao.queryAtlasTaskList(param);
        if (okTask == null || okTask.isEmpty()) {
            result.setMessage("操作失败，任务已被归档确认，无法重复操作！");
            return result;
        }
        param.put("originalStatus", RdmConst.PARTS_ATLAS_STATUS_ZZWCYZC);
        param.put("changeStatus", RdmConst.PARTS_ATLAS_STATUS_YJS);
        exportPartsAtlasDao.updateTaskStatus(param);
        result.setSuccess(true);
        result.setMessage("操作成功！");

        // 记录任务状态历史
        List<JSONObject> statusList = new ArrayList<>();
        for (JSONObject oneTask : okTask) {
            JSONObject oneStatus =
                    generateStatusHis(oneTask.getString("id"), RdmConst.PARTS_ATLAS_STATUS_YJS, "task", "", "", "");
            statusList.add(oneStatus);
        }
        recordTaskStatusHis(statusList);
        return result;
    }

    // 跳过机型制作流程（“已领取”----->“实例制作中”）
    public JsonResult jumpModelMade2Slzz(HttpServletRequest request) {
        JsonResult result = new JsonResult();
        String ids = RequestUtil.getString(request, "ids", "");
        if (StringUtils.isBlank(ids)) {
            result.setMessage("操作失败，参数为空！");
            return result;
        }
        List<String> idList = Arrays.asList(ids.split(",", -1));
        JSONObject param = new JSONObject();
        param.put("idList", idList);
        param.put("taskStatus", Arrays.asList(RdmConst.PARTS_ATLAS_STATUS_YLQ));
        List<JSONObject> okTask = exportPartsAtlasDao.queryAtlasTaskList(param);
        if (okTask == null || okTask.isEmpty()) {
            result.setMessage("操作失败，任务状态已发生变化，请刷新后重试！");
            return result;
        }
        param.put("originalStatus", RdmConst.PARTS_ATLAS_STATUS_YLQ);
        param.put("changeStatus", RdmConst.PARTS_ATLAS_STATUS_SLZZ);
        exportPartsAtlasDao.updateTaskStatus(param);
        result.setSuccess(true);
        result.setMessage("操作成功！");

        // 记录任务状态历史
        List<JSONObject> statusList = new ArrayList<>();
        for (JSONObject oneTask : okTask) {
            JSONObject oneStatus =
                    generateStatusHis(oneTask.getString("id"), RdmConst.PARTS_ATLAS_STATUS_SLZZ, "task", "", "", "");
            statusList.add(oneStatus);
        }
        recordTaskStatusHis(statusList);
        return result;
    }

    private JSONObject generateStatusHis(String busKeyId, String statusDesc, String scene, String optionDesc,
                                         String creatorName, String creatorId) {
        JSONObject oneStatusHis = new JSONObject();
        oneStatusHis.put("id", IdUtil.getId());
        oneStatusHis.put("busKeyId", busKeyId);
        oneStatusHis.put("statusDesc", statusDesc);
        oneStatusHis.put("scene", scene);
        oneStatusHis.put("creatorName",
                StringUtils.isNotBlank(creatorName) ? creatorName : ContextUtil.getCurrentUser().getFullname());
        oneStatusHis.put("optionDesc", optionDesc);
        oneStatusHis.put("CREATE_BY_", StringUtils.isNotBlank(creatorId) ? creatorId : ContextUtil.getCurrentUserId());
        oneStatusHis.put("CREATE_TIME_", new Date());

        return oneStatusHis;
    }

    // 机型制作数据查询
    public JsonPageResult<?> modelMadeListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        rdmZhglUtil.addOrder(request, params, "service_engineering_exportpartsatlas_modelmade.CREATE_TIME_", "desc");
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    if ("status".equalsIgnoreCase(name)) {
                        if ("blank".equalsIgnoreCase(value)) {
                            params.put("statusBlank", "yes");
                        } else {
                            params.put(name, Arrays.asList(value));
                            params.put("statusBlank", "no");
                        }
                    } else {
                        params.put(name, value);
                    }
                }
            }
        }
        // 增加分页条件
        rdmZhglUtil.addPage(request, params);
        // 增加角色过滤的条件
        RdmCommonUtil.addListAllQueryRoleExceptDraft(params, ContextUtil.getCurrentUserId(),
                ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> modelMadeList = exportPartsAtlasDao.queryModelMadeList(params);
        for (JSONObject oneData : modelMadeList) {
            if (oneData.get("CREATE_TIME_") != null) {
                oneData.put("CREATE_TIME_", DateUtil.formatDate(oneData.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }

        }
        // 添加当前任务处理人信息
        xcmgProjectManager.setTaskCurrentUserJSON(modelMadeList);
        result.setData(modelMadeList);
        int modelMadeListTotal = exportPartsAtlasDao.countQueryModelMadeList(params);
        result.setTotal(modelMadeListTotal);
        return result;
    }

    // 机型制作表单详情查询
    public JSONObject getModelMadeDetail(String id) {
        return exportPartsAtlasDao.getModelMadeDetail(id);
    }

    // 机型制作数据查询
    public JsonPageResult<?> modelMadeWaitListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            JSONObject params = new JSONObject();
            rdmZhglUtil.addOrder(request, params, "CREATE_TIME_",
                    "desc");
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
            rdmZhglUtil.addPage(request, params);
            List<JSONObject> modelMadeWaitList = exportPartsAtlasDao.queryModelMadeWaitList(params);
            for (JSONObject oneData : modelMadeWaitList) {
                if (oneData.get("needTime") != null) {
                    oneData.put("needTime", DateUtil.formatDate(oneData.getDate("needTime"), "yyyy-MM-dd"));
                }
            }
            result.setData(modelMadeWaitList);
            int modelMadeWaitListTotal = exportPartsAtlasDao.countQueryModelMadeWaitList(params);
            result.setTotal(modelMadeWaitListTotal);
        } catch (Exception e) {
            logger.error("Exception in modelMadeWaitListQuery", e);
            result.setSuccess(false);
            result.setMessage("系统异常！");
        }
        return result;
    }

    public void exportModelMadeWaitList(HttpServletRequest request, HttpServletResponse response) {
        JSONObject params = new JSONObject();
        rdmZhglUtil.addOrder(request, params, "service_engineering_exportpartsatlas_demanddetails.CREATE_TIME_",
                "desc");
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
        List<JSONObject> modelMadeWaitList = exportPartsAtlasDao.queryModelMadeWaitList(params);
        int index = 0;
        for (JSONObject oneData : modelMadeWaitList) {
            if (oneData.get("needTime") != null) {
                oneData.put("needTime", DateUtil.formatDate(oneData.getDate("needTime"), "yyyy-MM-dd"));
            }
            oneData.put("index", ++index);
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "徐工挖机零件图册待制作机型统计表";
        String excelName = nowDate + title;
        String[] fieldNames = {"序号", "需求通知单编号", "设计物料编码", "设计型号", "销售型号", "随机文件语言", "交货时间"};
        String[] fieldCodes = {"index", "demandNo", "matCode", "designType", "saleType", "languages", "needTime"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(modelMadeWaitList, fieldNames, fieldCodes, title);

        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    private void demandProcess(String applyId, JSONArray demandArr, String gridType) {
        if (demandArr == null || demandArr.isEmpty()) {
            return;
        }
        JSONObject param = new JSONObject();
        param.put("ids", new JSONArray());
        for (int index = 0; index < demandArr.size(); index++) {
            JSONObject oneObject = demandArr.getJSONObject(index);
            String id = oneObject.getString("id");
            String state = oneObject.getString("_state");
            //由于有默认项进来 除removed状态外，id为空的时候是要新增的
            if ("added".equals(state) || (StringUtils.isBlank(id) && (!"removed".equals(state)))) {
                // 新增
                oneObject.put("id", IdUtil.getId());
                oneObject.put("applyId", applyId);
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                if ("changeRel".equalsIgnoreCase(gridType)) {
                    exportPartsAtlasDao.insertChangeRel(oneObject);
                } else if ("wgjtczl".equalsIgnoreCase(gridType)) {
                    exportPartsAtlasDao.insertWgjtczl(oneObject);
                } else if ("wxzyj".equalsIgnoreCase(gridType)) {
                    exportPartsAtlasDao.insertWxzyj(oneObject);
                } else if ("gyhjmx".equalsIgnoreCase(gridType)) {
                    exportPartsAtlasDao.insertGyhjmx(oneObject);
                }
            } else if ("modified".equals(state)) {
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", new Date());
                if ("changeRel".equalsIgnoreCase(gridType)) {
                    exportPartsAtlasDao.updateChangeRel(oneObject);
                } else if ("wgjtczl".equalsIgnoreCase(gridType)) {
                    exportPartsAtlasDao.updateWgjtczl(oneObject);
                } else if ("wxzyj".equalsIgnoreCase(gridType)) {
                    exportPartsAtlasDao.updateWxzyj(oneObject);
                } else if ("gyhjmx".equalsIgnoreCase(gridType)) {
                    exportPartsAtlasDao.updateGyhjmx(oneObject);
                }
            } else if ("removed".equals(state)) {
                // 删除
                param.getJSONArray("ids").add(oneObject.getString("id"));
            }
        }
        if (!param.getJSONArray("ids").isEmpty()) {
            if ("changeRel".equalsIgnoreCase(gridType)) {
                exportPartsAtlasDao.deleteChangeRel(param);
            } else if ("wgjtczl".equalsIgnoreCase(gridType)) {
                exportPartsAtlasDao.deleteWgjtczl(param);
            } else if ("wxzyj".equalsIgnoreCase(gridType)) {
                exportPartsAtlasDao.deleteWxzyj(param);
            } else if ("gyhjmx".equalsIgnoreCase(gridType)) {
                exportPartsAtlasDao.deleteGyhjmx(param);
            }
        }
    }

    public void createModelMade(JSONObject formDataJson) {
        formDataJson.put("id", IdUtil.getId());
        formDataJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formDataJson.put("CREATE_TIME_", new Date());
        exportPartsAtlasDao.insertModelMade(formDataJson);

        // 这里加四个子表

        demandProcess(formDataJson.getString("id"), formDataJson.getJSONArray("changeRelGrid"), "changeRel");
        demandProcess(formDataJson.getString("id"), formDataJson.getJSONArray("wgjtczlGrid"), "wgjtczl");
        demandProcess(formDataJson.getString("id"), formDataJson.getJSONArray("wxzyjGrid"), "wxzyj");
        demandProcess(formDataJson.getString("id"), formDataJson.getJSONArray("gyhjmxGrid"), "gyhjmx");

    }

    public void updateModelMade(JSONObject formDataJson) {
        formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formDataJson.put("UPDATE_TIME_", new Date());
        exportPartsAtlasDao.updateModelMade(formDataJson);

        // 这里加四个子表
        demandProcess(formDataJson.getString("id"), formDataJson.getJSONArray("changeRelGrid"), "changeRel");
        demandProcess(formDataJson.getString("id"), formDataJson.getJSONArray("wgjtczlGrid"), "wgjtczl");
        demandProcess(formDataJson.getString("id"), formDataJson.getJSONArray("wxzyjGrid"), "wxzyj");
        demandProcess(formDataJson.getString("id"), formDataJson.getJSONArray("gyhjmxGrid"), "gyhjmx");
    }

    // 根据机型制作中的设计物料编码，更新所有对应制作任务的状态，并记录历史
    public void updateTaskStatusByModelMade(String matCode, String changeStatus) {
        // 根据matCode找到所有当前状态不是changeStatus、未领取、作废的制作任务
        JSONObject params = new JSONObject();
        params.put("verifyMatCode", matCode);
        params.put("notStatus",
                Arrays.asList(changeStatus, RdmConst.PARTS_ATLAS_STATUS_WLQ, RdmConst.PARTS_ATLAS_STATUS_ZF));
        List<JSONObject> taskList = exportPartsAtlasDao.queryAtlasTaskList(params);
        if (taskList == null || taskList.isEmpty()) {
            return;
        }
        Set<String> taskIds = new HashSet<>();
        List<JSONObject> statusList = new ArrayList<>();
        for (JSONObject oneTask : taskList) {
            taskIds.add(oneTask.getString("id"));
            JSONObject oneStatus = new JSONObject();
            oneStatus.put("id", IdUtil.getId());
            oneStatus.put("busKeyId", oneTask.getString("id"));
            oneStatus.put("statusDesc", changeStatus);
            oneStatus.put("scene", "task");
            oneStatus.put("creatorName", ContextUtil.getCurrentUser().getFullname());
            oneStatus.put("optionDesc", "");
            oneStatus.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            oneStatus.put("CREATE_TIME_", new Date());
            statusList.add(oneStatus);
        }
        params.clear();
        params.put("changeStatus", changeStatus);
        params.put("idList", taskIds);
        exportPartsAtlasDao.updateTaskStatus(params);

        // 记录状态历史
        recordTaskStatusHis(statusList);
    }

    public JsonResult deleteModelMade(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        JSONObject param = new JSONObject();
        param.put("ids", Arrays.asList(ids));
        exportPartsAtlasDao.deleteModelMade(param);
        return result;
    }

    // ..导入机型制作数据
    public void importModelMade(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile fileObj = multipartRequest.getFile("importFile");
            if (fileObj == null) {
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            String fileName = ((CommonsMultipartFile) fileObj).getFileItem().getName();
            ((CommonsMultipartFile) fileObj).getFileItem().getName().endsWith(ExcelReaderUtil.EXCEL03_EXTENSION);
            Workbook wb = null;
            if (fileName.endsWith(ExcelReaderUtil.EXCEL03_EXTENSION)) {
                wb = new HSSFWorkbook(fileObj.getInputStream());
            } else if (fileName.endsWith(ExcelReaderUtil.EXCEL07_EXTENSION)) {
                wb = new XSSFWorkbook(fileObj.getInputStream());
            }
            Sheet sheet = wb.getSheet("模板");
            if (sheet == null) {
                logger.error("找不到导入模板");
                result.put("message", "数据导入失败，找不到导入模板导入页！");
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
                titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
            }

            if (rowNum < 3) {
                logger.info("数据行为空");
                result.put("message", "数据导入完成，数据行为空！");
                return;
            }
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> itemList = new ArrayList<>();
            String id = IdUtil.getId();
            for (int i = 2; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = this.generateModelMadeDataFromRow(row, itemList, titleList, id);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            // 查重
            List<JSONObject> listAlready = exportPartsAtlasDao.queryModelMadeList(null);
            Set<String> set = new HashSet<>();
            for (JSONObject jsonObject : listAlready) {
                set.add(jsonObject.getString("modelMadeNum"));
            }
            for (Map<String, Object> map : itemList) {
                if (set.contains(map.get("modelMadeNum").toString())) {
                    result.put("message", "已经存在重复的机型制作编号：" + map.get("modelMadeNum").toString());
                    return;
                }
            }
            // 以上查重
            for (int i = 0; i < itemList.size(); i++) {
                Map<String, Object> map = itemList.get(i);
                String json = JSON.toJSONString(map);
                JSONObject jsonObject = JSON.parseObject(json);
                exportPartsAtlasDao.insertModelMade(jsonObject);
                exportPartsAtlasDao.updateModelMadeNum(jsonObject);
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importModelMade", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    // ..
    private JSONObject generateModelMadeDataFromRow(Row row, List<Map<String, Object>> itemList, List<String> titleList,
                                                    String mainId) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = ExcelUtil.getCellFormatValue(cell);
            }
            switch (title) {
                case "机型制作编号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "机型制作编号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("modelMadeNum", cellValue);
                    break;
                case "设计物料编码":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "设计物料编码为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("matCode", cellValue);
                    break;
                case "设计型号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "设计型号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("designType", cellValue);
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("id", IdUtil.getId());
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("creator", ContextUtil.getCurrentUser().getFullname());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    // ..
    public ResponseEntity<byte[]> importModelMadeTemplateDownload() {
        try {
            String fileName = "出口产品零件图册机型制作列表导入模板.xlsx";
            // 创建文件实例
            File file = new File(
                    MaterielService.class.getClassLoader().getResource("templates/serviceEngineering/" + fileName).toURI());
            String finalDownloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");

            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 finalDownloadFileName
            headers.setContentDispositionFormData("attachment", finalDownloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in importModelMadeTemplateDownload", e);
            return null;
        }
    }

    // 发运通知单列表查询
    public JsonPageResult<?> dispatchListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        getListParams(params, request, "finishStatus,CREATE_TIME_", "asc", true);
        RdmCommonUtil.addListAllQueryRoleExceptDraft(params, ContextUtil.getCurrentUserId(),
                ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> dispatchList = exportPartsAtlasDao.queryDispatchList(params);
        int dispatchListTotal = exportPartsAtlasDao.queryDispatchListTotal(params);
        for (JSONObject oneDispatch : dispatchList) {
            if (oneDispatch.get("CREATE_TIME_") != null) {
                oneDispatch.put("CREATE_TIME_", DateUtil.formatDate(oneDispatch.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }

        }

        result.setData(dispatchList);
        result.setTotal(dispatchListTotal);
        return result;
    }

    /**
     * 查询发运清单
     *
     * @param dispatchId
     * @return
     */
    public List<JSONObject> getDispatchDetailList(String dispatchId) {
        List<JSONObject> itemList = exportPartsAtlasDao.getDispatchDetailList(dispatchId);
        return itemList;
    }

    public JSONObject getDispatchInfo(String demandId) {
        JSONObject jsonObject = exportPartsAtlasDao.getDispatchInfoById(demandId);
        if (jsonObject == null) {
            return new JSONObject();
        }
        String createTime = jsonObject.getString("CREATE_TIME_");
        if (StringUtils.isNotBlank(createTime)) {
            jsonObject.put("CREATE_TIME_", DateFormatUtil.format(jsonObject.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
        }
        return jsonObject;
    }

    // 保存或者提交发运通知单
    public void saveOrCommitDispatch(JSONObject formDataJson, String scene, JsonResult result) {
        String id = formDataJson.getString("id");
        if ("save".equalsIgnoreCase(scene)) {
            if (StringUtils.isBlank(id)) {
                formDataJson.put("id", IdUtil.getId());
                formDataJson.put("dispatchStatus", "draft");
                formDataJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                formDataJson.put("CREATE_TIME_", new Date());
                exportPartsAtlasDao.insertDispatch(formDataJson);
            } else {
                formDataJson.put("dispatchStatus", "draft");
                formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                formDataJson.put("UPDATE_TIME_", new Date());
                exportPartsAtlasDao.updateDispatch(formDataJson);
            }
        } else if ("commit".equalsIgnoreCase(scene) && StringUtils.isNotBlank(id)) {
            formDataJson.put("dispatchStatus", "commit");
            formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            formDataJson.put("UPDATE_TIME_", new Date());
            exportPartsAtlasDao.updateDispatch(formDataJson);
        }
    }

    public JSONObject getDispatchDetail(String dispatchDetailId) {
        JSONObject jsonObject = exportPartsAtlasDao.getDispatchDetailById(dispatchDetailId);
        if (jsonObject == null) {
            return new JSONObject();
        }
        return jsonObject;
    }

    public void saveDispatchDetail(JSONObject formDataJson, String scene, JsonResult result) {
        String id = formDataJson.getString("id");
        // 新增或者更新发运明细信息
        if (!"relMachine".equalsIgnoreCase(scene)) {
            if (StringUtils.isBlank(id)) {
                formDataJson.put("id", IdUtil.getId());
                formDataJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                formDataJson.put("CREATE_TIME_", new Date());
                exportPartsAtlasDao.insertDispatchDetail(formDataJson);
            } else {
                formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                formDataJson.put("UPDATE_TIME_", new Date());
                exportPartsAtlasDao.updateDispatchDetail(formDataJson);
            }
            JSONArray detailConfigChangeData = formDataJson.getJSONArray("detailConfigChangeData");
            if (detailConfigChangeData != null && !detailConfigChangeData.isEmpty()) {
                for (int index = 0; index < detailConfigChangeData.size(); index++) {
                    JSONObject oneObject = detailConfigChangeData.getJSONObject(index);
                    String state = oneObject.getString("_state");
                    String configId = oneObject.getString("id");
                    if ("added".equals(state) || StringUtils.isBlank(configId)) {
                        // 新增
                        oneObject.put("id", IdUtil.getId());
                        oneObject.put("dispatchId", formDataJson.getString("dispatchId"));
                        oneObject.put("dispatchDetailId", formDataJson.getString("id"));
                        oneObject.put("demandId", "");
                        oneObject.put("demandDetailId", "");
                        oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        oneObject.put("CREATE_TIME_", new Date());
                        exportPartsAtlasDao.batchInsertDetailConfigs(Arrays.asList(oneObject));
                    } else if ("modified".equals(state)) {
                        oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        oneObject.put("UPDATE_TIME_", new Date());
                        exportPartsAtlasDao.updateDispatchDetailConfig(oneObject);
                    } else if ("removed".equals(state)) {
                        // 删除
                        exportPartsAtlasDao.delConfigDetail(oneObject);
                    }
                }
            }
        } else {
            // 关联或者解绑整机
            JSONArray detailMachineChangeData = formDataJson.getJSONArray("detailMachineChangeData");
            if (detailMachineChangeData != null && !detailMachineChangeData.isEmpty()) {
                for (int index = 0; index < detailMachineChangeData.size(); index++) {
                    JSONObject oneObject = detailMachineChangeData.getJSONObject(index);
                    String state = oneObject.getString("_state");
                    if ("added".equals(state)) {
                        // 新关联
                        oneObject.put("dispatchId", formDataJson.getString("dispatchId"));
                        oneObject.put("dispatchDetailId", formDataJson.getString("id"));
                        oneObject.put("relDispatchUserName", ContextUtil.getCurrentUser().getFullname());
                        oneObject.put("relDispatchTime", new Date());
                        exportPartsAtlasDao.updateMachineTaskByDispatch(oneObject);
                        // 判断是否要将该任务的状态改为“改制中”
                        judgeChangeTask2GZ(oneObject, formDataJson.getString("departTime"));
                    } else if ("modified".equals(state)) {
                        exportPartsAtlasDao.updateMachineTaskByDispatch(oneObject);
                        // 判断是否要将该任务的状态改为“改制中”
                        judgeChangeTask2GZ(oneObject, formDataJson.getString("departTime"));
                    } else if ("removed".equals(state)) {
                        // 解除关联
                        oneObject.put("dispatchId", "");
                        oneObject.put("dispatchDetailId", "");
                        oneObject.put("relDispatchUserName", "");
                        oneObject.put("relDispatchTime", null);
                        exportPartsAtlasDao.updateMachineTaskByDispatch(oneObject);
                    }
                }
            }
        }
    }

    // 判断是否要将该任务的状态改为“改制中”，如果改变状态，同步记录状态历史
    private void judgeChangeTask2GZ(JSONObject oneObject, String departTime) {
        // 无改制订单
        String changeApply = oneObject.getString("changeApply");
        if (StringUtils.isBlank(changeApply)) {
            return;
        }
        // 状态不是已转出、已接收
        String taskStatus = oneObject.getString("taskStatus");
        if (!RdmConst.PARTS_ATLAS_STATUS_ZZWCYZC.equalsIgnoreCase(taskStatus)
                && !RdmConst.PARTS_ATLAS_STATUS_YJS.equalsIgnoreCase(taskStatus)) {
            return;
        }
        try {
            Date departTimeDate = DateFormatUtil.parse(departTime, "yyyy-MM-dd");
            long diffTime = departTimeDate.getTime() - System.currentTimeMillis();
            if (diffTime < 72 * 60 * 60 * 1000) {
                return;
            }
            // 更新状态
            JSONObject param = new JSONObject();
            param.put("idList", Arrays.asList(oneObject.getString("id")));
            param.put("changeStatus", RdmConst.PARTS_ATLAS_STATUS_GZ);
            exportPartsAtlasDao.updateTaskStatus(param);
            // 记录历史状态
            recordOneTaskStatusHis(
                    generateStatusHis(oneObject.getString("id"), RdmConst.PARTS_ATLAS_STATUS_GZ, "task", "", "", ""));
        } catch (ParseException e) {
            logger.error("Exception in parse departTime:" + departTime, e);
            return;
        }
    }

    // 删除发运明细及配置
    public void delDispatchDetail(List<String> ids) {
        JSONObject param = new JSONObject();
        param.put("dispatchDetailIds", ids);
        exportPartsAtlasDao.delConfigDetail(param);
        param.clear();
        param.put("ids", ids);
        exportPartsAtlasDao.delDispatchDetail(param);
    }

    // 异常反馈列表查询
    public JsonPageResult<?> abnormalListQuery(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        boolean doPage = Boolean.parseBoolean(RequestUtil.getString(request, "doPage", "true"));
        JSONObject params = new JSONObject();
        getListParams(params, request, "service_engineering_exportpartsatlas_taskabnormal.CREATE_TIME_", "desc",
                doPage);
        List<JSONObject> abnormalList = exportPartsAtlasDao.queryAbnormalList(params);
        for (JSONObject oneAbnormal : abnormalList) {
            if (oneAbnormal.get("CREATE_TIME_") != null) {
                oneAbnormal.put("CREATE_TIME_", DateUtil.formatDate(oneAbnormal.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }

        }
        result.setData(abnormalList);
        if (doPage) {
            int abnormalListTotal = exportPartsAtlasDao.countAbnormalList(params);
            result.setTotal(abnormalListTotal);
        }
        return result;
    }

    // 前台创建异常反馈
    public void saveAbnormal(JsonResult result, JSONObject abnormalFormObj) {
        String machinetaskId = abnormalFormObj.getString("machinetaskId");
        String machineCode = abnormalFormObj.getString("machineCode");
        if (StringUtils.isBlank(machinetaskId) || StringUtils.isBlank(machineCode)) {
            result.setSuccess(false);
            result.setMessage("操作失败，参数为空！");
            return;
        }
        List<String> machinetaskIdList = Arrays.asList(machinetaskId.split(",", -1));
        List<String> machineCodeList = Arrays.asList(machineCode.split(",", -1));
        if (machinetaskIdList.size() != machineCodeList.size()) {
            result.setSuccess(false);
            result.setMessage("操作失败，任务数与整机PIN码数不一致！");
            return;
        }
        // 服务工程所负责人
        String deptRespUserIds = "";
        List<Map<String, String>> depRespMans = commonInfoManager.queryDeptRespUser(RdmConst.FWGCS_NAME);
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                deptRespUserIds += depRespMan.get("PARTY2_") + ",";
            }
        }
        if (StringUtils.isNotBlank(deptRespUserIds)) {
            deptRespUserIds = deptRespUserIds.substring(0, deptRespUserIds.length() - 1);
        }
        for (int index = 0; index < machinetaskIdList.size(); index++) {
            JSONObject abnormalObj = new JSONObject();
            abnormalObj.put("id", IdUtil.getId());
            abnormalObj.put("abnormalNum",
                    "YCFK-" + DateFormatUtil.getNowUTCDateStr("yyyyMMddHHmmssSSS") + CommonUtil.genereate3Random());
            abnormalObj.put("machinetaskId", machinetaskIdList.get(index));
            abnormalObj.put("reasonDesc", abnormalFormObj.getString("reasonDesc"));
            abnormalObj.put("expectTime", abnormalFormObj.getString("expectTime"));
            abnormalObj.put("abnormalStatus", RdmConst.PARTS_ATLAS_ABNORMAL_SH);
            abnormalObj.put("creatorName", ContextUtil.getCurrentUser().getFullname());
            abnormalObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            abnormalObj.put("CREATE_TIME_", new Date());
            exportPartsAtlasDao.insertTaskAbnormal(abnormalObj);

            // 记录异常反馈状态变化
            recordOneTaskStatusHis(generateStatusHis(abnormalObj.getString("id"), RdmConst.PARTS_ATLAS_ABNORMAL_SH,
                    "abnormal", "", "", ""));
            // 给审核人员(服务所所长)发送钉钉通知
            JSONObject noticeObj = new JSONObject();
            noticeObj.put("content",
                    "零件图册制作异常反馈待审核，请前往RDM平台“后市场技术”-“零件图册”-“异常反馈列表”处理！" + "\n整机PIN码：" + machineCodeList.get(index)
                            + "\n预计完成时间：" + abnormalObj.getString("expectTime") + "\n原因描述："
                            + abnormalObj.getString("reasonDesc") + "\n反馈人：" + abnormalObj.getString("creatorName"));
            sendDDNoticeManager.sendNoticeForCommon(deptRespUserIds, noticeObj);
        }
    }

    public JSONObject taskDetailInfo(String id) {
        JSONObject param = new JSONObject();
        param.put("idList", Arrays.asList(id));
        List<JSONObject> taskList = exportPartsAtlasDao.queryAtlasTaskList(param);
        if (taskList == null || taskList.isEmpty()) {
            return new JSONObject();
        }
        JSONObject oneTask = taskList.get(0);
        if (oneTask.get("CREATE_TIME_") != null) {
            oneTask.put("CREATE_TIME_", DateUtil.formatDate(oneTask.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
        }
        if (oneTask.get("relDemandTime") != null) {
            oneTask.put("relDemandTime", DateUtil.formatDate(oneTask.getDate("relDemandTime"), "yyyy-MM-dd"));
        }
        if (oneTask.get("relDispatchTime") != null) {
            oneTask.put("relDispatchTime", DateUtil.formatDate(oneTask.getDate("relDispatchTime"), "yyyy-MM-dd"));
        }

        param.clear();
        param.put("machinetaskIds", Arrays.asList(id));
        param.put("sortField",
                "service_engineering_exportpartsatlas_taskabnormal.machinetaskId,service_engineering_exportpartsatlas_taskabnormal.CREATE_TIME_");
        param.put("sortOrder", "desc");
        List<JSONObject> abnormalList = exportPartsAtlasDao.queryAbnormalList(param);
        Map<String, JSONObject> machinetaskId2AbnormalInfo = new HashMap<>();
        for (JSONObject oneAbnormal : abnormalList) {
            String machinetaskId = oneAbnormal.getString("machinetaskId");
            String abnormalStatus = oneAbnormal.getString("abnormalStatus");
            if (!machinetaskId2AbnormalInfo.containsKey(machinetaskId)) {
                machinetaskId2AbnormalInfo.put(machinetaskId, new JSONObject());
            }
            JSONObject machineAbnormalInfo = machinetaskId2AbnormalInfo.get(machinetaskId);
            if (RdmConst.PARTS_ATLAS_ABNORMAL_BZ.equalsIgnoreCase(abnormalStatus)
                    || RdmConst.PARTS_ATLAS_ABNORMAL_SH.equalsIgnoreCase(abnormalStatus)) {
                machineAbnormalInfo.put("hasExistRunningAbnormal", "true");
            }
            if (RdmConst.PARTS_ATLAS_ABNORMAL_FB.equalsIgnoreCase(abnormalStatus)
                    && StringUtils.isBlank(machineAbnormalInfo.getString("expectTime"))) {
                machineAbnormalInfo.put("expectTime", oneAbnormal.getString("expectTime"));
                machineAbnormalInfo.put("reasonDesc", oneAbnormal.getString("reasonDesc"));
            }
        }
        if (machinetaskId2AbnormalInfo.containsKey(id)) {
            oneTask.put("hasExistRunningAbnormal",
                    machinetaskId2AbnormalInfo.get(id).getString("hasExistRunningAbnormal"));
            oneTask.put("expectTime", machinetaskId2AbnormalInfo.get(id).getString("expectTime"));
            oneTask.put("reasonDesc", machinetaskId2AbnormalInfo.get(id).getString("reasonDesc"));
        }

        return oneTask;
    }

    // 删除异常反馈及对应的状态记录
    public void delAbnormal(List<String> idList) {
        JSONObject param = new JSONObject();
        param.put("ids", idList);
        exportPartsAtlasDao.delTaskAbnormal(param);
        param.clear();
        param.put("busKeyIds", idList);
        exportPartsAtlasDao.delStatusHis(param);
    }

    public void abnormalNext(JSONObject abnormalObj, String currentStatus) {
        String nextStatus = RdmConst.PARTS_ATLAS_ABNORMAL_SH;
        if (RdmConst.PARTS_ATLAS_ABNORMAL_SH.equalsIgnoreCase(currentStatus)) {
            nextStatus = RdmConst.PARTS_ATLAS_ABNORMAL_FB;
        }
        abnormalObj.put("abnormalStatus", nextStatus);
        abnormalObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        abnormalObj.put("UPDATE_TIME_", new Date());
        exportPartsAtlasDao.updateTaskAbnormal(abnormalObj);

        // 暂时不考虑多数据批量操作场景
        // 记录异常反馈状态变化
        recordOneTaskStatusHis(generateStatusHis(abnormalObj.getString("id"), nextStatus, "abnormal", "", "", ""));
        String noticeUserIdStr = "";
        JSONObject noticeObj = new JSONObject();
        if (RdmConst.PARTS_ATLAS_ABNORMAL_SH.equalsIgnoreCase(currentStatus)) {
            // 异常反馈发布，通知到相应人员（角色“零件图册异常反馈通知人员”）
            /*List<Map<String, String>> roleUsers = commonInfoManager
                    .queryUserByGroupNameAndRelType(RdmConst.EXPORT_PARTSATLAS_ABNORMAL_NOTICE, "GROUP-USER-BELONG");
            if (roleUsers != null && !roleUsers.isEmpty()) {
                for (Map<String, String> oneUser : roleUsers) {
                    noticeUserIdStr += oneUser.get("USER_ID_") + ",";
                }
                if (StringUtils.isNotBlank(noticeUserIdStr)) {
                    noticeUserIdStr = noticeUserIdStr.substring(0, noticeUserIdStr.length() - 1);
                }
                noticeObj.put("content",
                        "零件图册制作异常反馈已发布，请前往RDM平台“后市场技术”-“零件图册”-“异常反馈列表”查看！" + "\n整机PIN码："
                                + abnormalObj.getString("machineCode") + "\n预计完成时间：" + abnormalObj.getString("expectTime")
                                + "\n原因描述：" + abnormalObj.getString("reasonDesc") + "\n反馈人："
                                + abnormalObj.getString("creatorName"));
            }*/
        } else {
            // 给审核人员（服务工程所负责人）发送钉钉通知
            List<Map<String, String>> depRespMans = commonInfoManager.queryDeptRespUser(RdmConst.FWGCS_NAME);
            if (depRespMans != null && !depRespMans.isEmpty()) {
                for (Map<String, String> depRespMan : depRespMans) {
                    noticeUserIdStr += depRespMan.get("PARTY2_") + ",";
                }
            }
            if (StringUtils.isNotBlank(noticeUserIdStr)) {
                noticeUserIdStr = noticeUserIdStr.substring(0, noticeUserIdStr.length() - 1);
            }
            noticeObj.put("content",
                    "零件图册制作异常反馈待审核，请前往RDM平台“后市场技术”-“零件图册”-“异常反馈列表”处理！" + "\n整机PIN码：" + abnormalObj.getString("machineCode")
                            + "\n预计完成时间：" + abnormalObj.getString("expectTime") + "\n原因描述："
                            + abnormalObj.getString("reasonDesc") + "\n反馈人：" + abnormalObj.getString("creatorName"));
            sendDDNoticeManager.sendNoticeForCommon(noticeUserIdStr, noticeObj);
        }

    }

    // 异常反馈驳回
    public void abnormalPre(JSONObject abnormalObj, String optionDesc) {
        String nextStatus = RdmConst.PARTS_ATLAS_ABNORMAL_BZ;
        abnormalObj.put("abnormalStatus", nextStatus);
        abnormalObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        abnormalObj.put("UPDATE_TIME_", new Date());
        exportPartsAtlasDao.updateTaskAbnormal(abnormalObj);

        // 暂时不考虑多数据批量操作场景
        // 记录异常反馈状态变化
        recordOneTaskStatusHis(
                generateStatusHis(abnormalObj.getString("id"), nextStatus, "abnormal", optionDesc, "", ""));
        // 给编制人员发送钉钉通知
        String noticeUserIdStr = abnormalObj.getString("CREATE_BY_");
        JSONObject noticeObj = new JSONObject();
        noticeObj.put("content",
                "零件图册制作异常反馈被驳回【驳回原因：" + optionDesc + "】，请前往RDM平台“后市场技术”-“零件图册”-“异常反馈列表”处理！" + "\n整机PIN码："
                        + abnormalObj.getString("machineCode") + "\n预计完成时间：" + abnormalObj.getString("expectTime") + "\n原因描述："
                        + abnormalObj.getString("reasonDesc") + "\n反馈人：" + abnormalObj.getString("creatorName"));

        sendDDNoticeManager.sendNoticeForCommon(noticeUserIdStr, noticeObj);
    }

    // 查询制作任务关联的相关流程（机型制作、变更、作废），并按类型、时间排序后拼装
    public void relFlowQuery(String taskId, List<JSONObject> dataList) {
        // 机型制作流程
        JSONObject params = new JSONObject();
        params.put("idList", Arrays.asList(taskId));
        List<JSONObject> taskList = exportPartsAtlasDao.queryAtlasTaskList(params);
        if (taskList == null || taskList.size() != 1) {
            return;
        }
        JSONObject oneTask = taskList.get(0);
        String matCode = oneTask.getString("matCode");
        if (StringUtils.isBlank(matCode)) {
            return;
        }
        params.clear();
        params.put("matCode", matCode);
        params.put("sortField", "service_engineering_exportpartsatlas_modelmade.CREATE_TIME_");
        params.put("sortOrder", "desc");
        List<JSONObject> modelMadeList = exportPartsAtlasDao.queryModelMadeList(params);
        // 添加当前任务处理人信息
        xcmgProjectManager.setTaskCurrentUserJSON(modelMadeList);
        for (JSONObject oneModelMade : modelMadeList) {
            oneModelMade.put("flowType", "机型制作");
            oneModelMade.put("flowNum", oneModelMade.getString("modelMadeNum"));
            oneModelMade.put("flowStatus", oneModelMade.getString("status"));
            oneModelMade.put("jumpDetailUrl",
                    "/serviceEngineering/core/exportPartsAtlas/modelMadeEditPage.do?action=detail&id="
                            + oneModelMade.getString("id") + "&status=" + oneModelMade.getString("status"));
        }
        dataList.addAll(modelMadeList);

        for (JSONObject oneData : dataList) {
            if (oneData.get("CREATE_TIME_") != null) {
                oneData.put("CREATE_TIME_", DateUtil.formatDate(oneData.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
    }

    // 查询变更提醒列表
    public JsonPageResult<?> changeNoticeListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        getListParams(params, request, "CONVERT(changeConfirm using GBK),CREATE_TIME_", "desc", true);
        List<JSONObject> changeNoticeList = exportPartsAtlasDao.queryChangeNoticeList(params);
        int changeNoticeTotal = exportPartsAtlasDao.queryChangeNoticeTotal(params);
        for (JSONObject oneChange : changeNoticeList) {
            if (oneChange.get("CREATE_TIME_") != null) {
                oneChange.put("CREATE_TIME_", DateUtil.formatDate(oneChange.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (oneChange.get("UPDATE_TIME_") != null) {
                oneChange.put("UPDATE_TIME_", DateUtil.formatDate(oneChange.getDate("UPDATE_TIME_"), "yyyy-MM-dd"));
            }
        }

        result.setData(changeNoticeList);
        result.setTotal(changeNoticeTotal);
        return result;
    }

    // 变更提醒确认
    public void changeConfirm(String changeId, JsonResult result) {
        JSONObject param = new JSONObject();
        param.put("id", changeId);
        param.put("changeConfirm", "已确认");
        param.put("confirmUserName", ContextUtil.getCurrentUser().getFullname());
        param.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        param.put("UPDATE_TIME_", new Date());
        exportPartsAtlasDao.updateChangeNoticeConfirm(param);
    }

    private String toGetTaskStatusNameByKey(String statusKey) {
        String result = "";
        if (StringUtils.isNotBlank(statusKey)) {
            switch (statusKey) {
                case RdmConst.PARTS_ATLAS_STATUS_WLQ:
                    result = "未领取";
                    break;
                case RdmConst.PARTS_ATLAS_STATUS_YLQ:
                    result = "已领取";
                    break;
                case RdmConst.PARTS_ATLAS_STATUS_JXZZSQ:
                    result = "机型制作申请中";
                    break;
                case RdmConst.PARTS_ATLAS_STATUS_JXZZ:
                    result = "机型制作中";
                    break;
                case RdmConst.PARTS_ATLAS_STATUS_SLZZ:
                    result = "实例制作中";
                    break;
                case RdmConst.PARTS_ATLAS_STATUS_GZ:
                    result = "改制中";
                    break;
                case RdmConst.PARTS_ATLAS_STATUS_ZZWCYZC:
                    result = "制作完成已转出";
                    break;
                case RdmConst.PARTS_ATLAS_STATUS_YJS:
                    result = "档案室已接收";
                    break;
                case RdmConst.PARTS_ATLAS_STATUS_ZF:
                    result = "作废";
                    break;
            }
        }
        return result;
    }

    // 从oa接补发时间
    public void reDispatchApply(JSONObject result, String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        JSONObject data = new JSONObject();
        if (postBodyObj == null || postBodyObj.isEmpty()) {
            data.put("isok", 0);
            data.put("errorMsg", "传输的消息内容为空");
            result.put("Data", data);
            result.put("Message", "调用失败");
            return;
        }
        String reDispatchTime = postBodyObj.getString("xgssFinishTime");
        if (StringUtils.isBlank(reDispatchTime)) {
            data.put("isok", 0);
            data.put("errorMsg", "传输的X-GSS制作完成时间为空");
            result.put("Data", data);
            result.put("Message", "调用失败");
            return;
        }
        JSONArray postBodyObjArr = JSONArray.parseArray(postBodyObj.getString("reDispatchTableDetails"));
        if (postBodyObjArr == null || postBodyObjArr.isEmpty()) {
            data.put("isok", 0);
            data.put("errorMsg", "传输的消息子表内容为空");
            result.put("Data", data);
            result.put("Message", "调用失败");
            return;
        }
        JSONObject param = new JSONObject();
        List<String> machineCodes = new ArrayList<>();
        // 传入参数
        for (int index = 0; index < postBodyObjArr.size(); index++) {
            JSONObject oneObj = postBodyObjArr.getJSONObject(index);
            String machineCode = oneObj.getString("machinePin");
            if (!StringUtil.isEmpty(machineCode)) {
                machineCodes.add(machineCode);
            }
        }
        if (machineCodes.size() == 0) {
            data.put("isok", 0);
            data.put("errorMsg", "传输的消息内容整机编号为空");
            result.put("Data", data);
            result.put("Message", "调用失败");
            return;
        }
        param.put("machineCodes", machineCodes);
        String manualType = postBodyObj.getString("randomFileType");
        // 1代表操保手册：reOperationDispatchTime 2代表零件图册：reDispatchTime
        if ("2".equalsIgnoreCase(manualType)) {
            param.put("reDispatchTime", reDispatchTime);
        } else if ("1".equalsIgnoreCase(manualType)) {
            param.put("reOperationDispatchTime", reDispatchTime);
        } else {
            data.put("isok", 0);
            data.put("errorMsg", "文件类型错误");
            result.put("Data", data);
            result.put("Message", "调用失败");
            return;
        }
        param.put("UPDATE_BY_", "1");
        param.put("UPDATE_TIME_", new Date());
        try {
            exportPartsAtlasDao.updateReDispatchTimeApi(param);
            result.put("Message", "调用成功");
            data.put("isok", 1);
            data.put("errorMsg", "");
            result.put("Data", data);
        } catch (Exception e) {
            logger.error("oa对接补发时间更新错误,错误：", e, "\\n消息体：", postBody);
        }

    }

    // ..
    public void exportList(HttpServletRequest request, HttpServletResponse response) {
        List<Map<String, Object>> listDataMap = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        // 初始化查询参数
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                }
                params.put(name, value);
            }
        }

        List<JSONObject> listData = exportPartsAtlasDao.exportDemandMessage(params);

        for (JSONObject business : listData) {
            if (business.get("CREATE_TIME_") != null) {
                business.put("CREATE_TIME_", DateUtil.formatDate((Date) business.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }

        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "零件图册需求通知单";
        String excelName = nowDate + title;
        String[] fieldNames =
                {"需求通知单编号", "出口国家", "编制人", "编制日期", "配置描述", "物料编码", "设计型号", "销售型号", "数量", "随机文件语言", "交货日期"};
        String[] fieldCodes = {"demandNo", "exportCountryName", "creatorName", "CREATE_TIME_", "configDescs", "matCode",
                "designType", "saleType", "needNumber", "languages", "needDate"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    /**
     * @lwgkiller:以下成品库功能相关
     */
    // ..
    public JsonPageResult<?> dispatchFPWListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        getListParams(params, request, "dispatchTimeFPW", "asc", true);
        List<JSONObject> businessList = exportPartsAtlasDao.queryDispatchFPWList(params);
        int businessListTotal = exportPartsAtlasDao.queryDispatchFPWListTotal(params);
        for (JSONObject oneDispatch : businessList) {
            if (oneDispatch.get("CREATE_TIME_") != null) {
                oneDispatch.put("CREATE_TIME_", DateUtil.formatDate(oneDispatch.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            String res = "";
            JSONObject statusParams = new JSONObject();
            String demandNo = oneDispatch.getString("demandNo");
            if (StringUtils.isNotBlank(demandNo)) {
                statusParams.put("demandNo", demandNo);
                statusParams.put("materialCode", oneDispatch.getString("materialCode"));
                statusParams.put("designModel", oneDispatch.getString("designModel"));
                statusParams.put("salesModel", oneDispatch.getString("salesModel"));
                res = checkManaulStatus(statusParams);
                oneDispatch.put("manualStatus", res);
            }
        }


        result.setData(businessList);
        result.setTotal(businessListTotal);
        return result;
    }

    // ..
    public JsonResult deleteDispatchFPW(String[] ids) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        JSONObject params = new JSONObject();
        for (String businessId : businessIds) {
            JSONObject dispatchFWPDetail = exportPartsAtlasDao.getDispatchFWPDetailById(businessId);
            params.clear();
            params.put("machineCode", dispatchFWPDetail.getString("pin"));
            List<JSONObject> taskList = exportPartsAtlasDao.queryAtlasTaskList(params);
            if (taskList.size() > 0) {// 所有状态的全面解绑逆向凭证
                for (JSONObject task : taskList) {
                    task.put("fwpId", "");
                    task.put("dispatchTime", "");
                    exportPartsAtlasDao.updateMachineTaskByDispatchFWP(task);
                }
            }
        }
        params.clear();
        params.put("businessIds", businessIds);
        exportPartsAtlasDao.deleteDispatchFPW(params);
        return result;
    }

    // ..
    public ResponseEntity<byte[]> importDispatchFPWTemplateDownload() {
        try {
            String fileName = "成品库发运产品明细导入模板.xlsx";
            // 创建文件实例
            File file = new File(
                    MaterielService.class.getClassLoader().getResource("templates/serviceEngineering/" + fileName).toURI());
            String finalDownloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 finalDownloadFileName
            headers.setContentDispositionFormData("attachment", finalDownloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in importDispatchFPWTemplateDownload", e);
            return null;
        }
    }

    // ..
    public void importDispatchFPW(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile fileObj = multipartRequest.getFile("importFile");
            if (fileObj == null) {
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            String fileName = ((CommonsMultipartFile) fileObj).getFileItem().getName();
            ((CommonsMultipartFile) fileObj).getFileItem().getName().endsWith(ExcelReaderUtil.EXCEL03_EXTENSION);
            Workbook wb = null;
            if (fileName.endsWith(ExcelReaderUtil.EXCEL03_EXTENSION)) {
                wb = new HSSFWorkbook(fileObj.getInputStream());
            } else if (fileName.endsWith(ExcelReaderUtil.EXCEL07_EXTENSION)) {
                wb = new XSSFWorkbook(fileObj.getInputStream());
            }
            Sheet sheet = wb.getSheet("模板");
            if (sheet == null) {
                logger.error("找不到导入模板");
                result.put("message", "数据导入失败，找不到导入模板导入页！");
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
                titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
            }

            if (rowNum < 3) {
                logger.info("数据行为空");
                result.put("message", "数据导入完成，数据行为空！");
                return;
            }
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> itemList = new ArrayList<>();
            String id = IdUtil.getId();
            for (int i = 2; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                // 验证终止符begin
                Cell cell = row.getCell(0);
                String cellValue = "";
                if (cell != null) {
                    cellValue = ExcelUtil.getCellFormatValue(cell);
                }
                if (cellValue.equalsIgnoreCase("终止")) {
                    break;
                }
                // 验证终止符end
                JSONObject rowParse = this.generateDispatchFPWFromRow(row, itemList, titleList, id);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            // 机型人员映射配置读取
            String[] allMatInstanceUserss =
                    sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringMat-Instance", "allMatInstanceUsers")
                            .getValue().split(",");
            Map<String, List<String>> usernoToMaterialList = new HashedMap();
            for (String matInstanceUser : allMatInstanceUserss) {
                List<String> materialList = Arrays.asList(sysDicManager
                        .getBySysTreeKeyAndDicKey("serviceEngineeringMat-Instance", matInstanceUser).getValue().split(","));
                usernoToMaterialList.put(matInstanceUser, materialList);
            }
            Set<String> toBeSendMessageUsernos = new HashSet<>();
            // 查重
            List<JSONObject> dispatchFPWList = exportPartsAtlasDao.queryDispatchFPWList(null);
            Set<String> dispatchFPWSet = new HashSet<>();
            for (JSONObject dispatchFPW : dispatchFPWList) {
                dispatchFPWSet.add(dispatchFPW.getString("pin"));
            }
            for (int i = 0; i < itemList.size(); i++) {
                JSONObject jsonObject = new JSONObject(itemList.get(i));
                if (dispatchFPWSet.contains(jsonObject.getString("pin"))) {
                    result.put("message",
                            "数据导入失败，第" + (i + 3) + "行数据错误：已经存在相同的pin码 " + jsonObject.getString("pin") + "");
                    return;
                }
            }
            // 逆向业务
            JSONObject params = new JSONObject();
            for (int i = 0; i < itemList.size(); i++) {
                JSONObject fwp = new JSONObject(itemList.get(i));
                exportPartsAtlasDao.insertDispatchFPW(fwp);
                // @lwgkiller:处理任务的新增或更新 todo
                params.put("machineCode", fwp.getString("pin"));
                List<JSONObject> taskListAllWithOneMachineCode = exportPartsAtlasDao.queryAtlasTaskList(params);
                List<JSONObject> taskListRuningWithOneMachineCode = new ArrayList<>();
                for (JSONObject task : taskListAllWithOneMachineCode) {
                    if (!task.getString("taskStatus").equalsIgnoreCase(RdmConst.PARTS_ATLAS_STATUS_ZF)) {
                        taskListRuningWithOneMachineCode.add(task);
                    }
                }
                if (!taskListAllWithOneMachineCode.isEmpty()) {// 所有状态的全面关联逆向凭证
                    for (JSONObject task : taskListAllWithOneMachineCode) {
                        task.put("fwpId", fwp.getString("id"));
                        task.put("dispatchTime", fwp.getString("dispatchTimeFPW"));
                        exportPartsAtlasDao.updateMachineTaskByDispatchFWP(task);
                    }
                }
                if (taskListRuningWithOneMachineCode.isEmpty()) {// 如果没有运行中的，新增
                    JSONObject task = new JSONObject();
                    task.put("id", IdUtil.getId());
                    task.put("taskStatus", RdmConst.PARTS_ATLAS_STATUS_WLQ);
                    task.put("fwpId", fwp.getString("id"));
                    task.put("machineCode", fwp.getString("pin"));
                    task.put("dispatchTime", fwp.getString("dispatchTimeFPW"));
                    task.put("CREATE_BY_", "1");
                    task.put("CREATE_TIME_", new Date());
                    exportPartsAtlasDao.insertMachineTaskByDispatchFWP(task);
                }
                // 对应机型人员发通知
                for (Map.Entry<String, List<String>> entry : usernoToMaterialList.entrySet()) {
                    List<String> materialList = entry.getValue();
                    if (materialList.contains(fwp.getString("materialCode"))) {
                        System.out.println(entry.getKey());
                        toBeSendMessageUsernos.add(entry.getKey());
                    }
                }
            }
            // 发钉钉
            JSONObject noticeObj = new JSONObject();
            noticeObj.put("content", "您负责的机型在成品库有新的实绩发运数据，请前往RDM平台“后市场技术”-“零件图册”-“零件图册任务管理”查看！");
            for (String toBeSendMessageUserno : toBeSendMessageUsernos) {
                IUser user = userService.getByUsername(toBeSendMessageUserno);
                sendDDNoticeManager.sendNoticeForCommon(user.getUserId(), noticeObj);
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importExcel", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    // ..
    private JSONObject generateDispatchFPWFromRow(Row row, List<Map<String, Object>> itemList, List<String> titleList,
                                                  String mainId) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = ExcelUtil.getCellFormatValue(cell);
            }
            switch (title) {
                case "出口国家":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "出口国家为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("exportingCountry", cellValue);
                    break;
                case "整机物料编码":
                    if (!StringUtils.isBlank(cellValue) && cellValue.length() != 9) {
                        oneRowCheck.put("message", "整机物料编码必须是9位");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialCode", cellValue);
                    break;
                case "设计型号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "设计型号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("designModel", cellValue);
                    break;
                case "销售型号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "销售型号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("salesModel", cellValue);
                    break;
                case "整机PIN码":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "整机PIN码为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("pin", cellValue);
                    break;
                case "需求通知单号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "需求通知单号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("demandNo", cellValue);
                    break;
                case "发运通知单号":
                    oneRowMap.put("dispatchNo", cellValue);
                    break;
                case "清单号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "清单号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("detailNo", cellValue);
                    break;
                case "语言":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "清单号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("languages", cellValue);
                    break;
                case "发车日期":
                    if (StringUtils.isBlank(cellValue) || cellValue.length() < 10 || cellValue.split("-").length != 3) {
                        oneRowCheck.put("message", "发车日期为空或格式不正确");
                        return oneRowCheck;
                    }
                    oneRowMap.put("dispatchTimeFPW", cellValue.substring(0, 10));
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("id", IdUtil.getId());
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    // ..
    public JSONObject getDispatchFWPDetailById(String businessId) {
        JSONObject jsonObject = exportPartsAtlasDao.getDispatchFWPDetailById(businessId);
        if (jsonObject == null) {
            return new JSONObject();
        }
        return jsonObject;
    }

    // @mh 根据五个筛选条件匹配发运/需求单，如果找到需求单,找子表，将子表中的文件名称字段拼接
    // 2023年4月8日09:05:38设计型号匹配去掉，状态筛选条件去掉
    public String checkManaulStatus(JSONObject params) {
        String res = "";
        // 要获取主表Id
        List<JSONObject> demandList = exportPartsAtlasDao.queryManualStatusList(params);
        if (demandList.size() == 0) {
            res = "未接收到出口需求通知";
            return res;
        }
        String mainId = demandList.get(0).getString("id");
        String businessStatus = demandList.get(0).getString("businessStatus");
        // 不是结束和打印的，要展示当前状态
        if (!("E-printing".equalsIgnoreCase(businessStatus) || "F-close".equalsIgnoreCase(businessStatus))) {
            switch (businessStatus) {
                case "A-editing":
                    res = "编辑中";
                    break;
                case "B-confirming":
                    res = "确认信息";
                    break;
                case "C-confirmingProduct":
                    res = "产品主管确认";
                    break;
                case "D1-adding":
                    res = "新增中";
                    break;
                case "D3-translating":
                    res = "翻译中";
                    break;
                default:
                    res = businessStatus;
                    break;
            }
            return res;
        }

        JSONObject subParams = new JSONObject();
        subParams.put("businessId", mainId);
        // 无需筛选REF_ID_
        List<JSONObject> demandSubList = maintenanceManualDemandDao.getManualMatchList(subParams);
        if (demandSubList.size() == 0) {
            res = "历史需求";
            return res;
        }
        StringBuffer resBuffer = new StringBuffer();
        // 拼接文件名 @mh 2023年4月6日09:45:28 正常只会有一条数据 -by lwgkiller
        for (JSONObject object : demandSubList) {
            if (!object.containsKey("REF_ID_") || (!object.getString("REF_ID_").equalsIgnoreCase("1")
                    && !object.getString("REF_ID_").equalsIgnoreCase("2"))) {
                resBuffer.append(object.getString("manualDescription")).append(",").append("</br>");
            }
        }
        // 把最后一个逗号和换行符删掉
        if (resBuffer.length() > 6) {
            resBuffer.delete(resBuffer.length() - 6, resBuffer.length());
        }
        res = resBuffer.toString();
        return res;
    }

    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        // 这个导出不分页
        JSONObject params = new JSONObject();
        getListParams(params, request, "dispatchTimeFPW", "asc", false);
        List<JSONObject> businessList = exportPartsAtlasDao.queryDispatchFPWList(params);
        for (JSONObject oneDispatch : businessList) {
            if (oneDispatch.get("CREATE_TIME_") != null) {
                oneDispatch.put("CREATE_TIME_", DateUtil.formatDate(oneDispatch.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            String taskStatus = oneDispatch.getString("taskStatus");
            String s = "";
            // 任务状态render
            if (taskStatus != null) {
                switch (taskStatus) {
                    case "00WLQ":
                        s = "未领取";
                        break;
                    case "01YLQ":
                        s = "已领取";
                        break;
                    case "02JXZZSQ":
                        s = "机型制作申请中";
                        break;
                    case "03JXZZ":
                        s = "机型制作中";
                        break;
                    case "04SLZZ":
                        s = "实例制作中";
                        break;
                    case "05GZ":
                        s = "改制中";
                        break;
                    case "06ZZWCYZC":
                        s = "制作完成已转出";
                        break;
                    case "07YJS":
                        s = "档案室已接收";
                        break;
                    case "08ZF":
                        s = "作废";
                        break;
                    default:
                        s = "其他";
                        break;
                }
            }
            oneDispatch.put("taskStatus", s);

            // 加一列操保手册状态
            JSONObject manualStatusParam = new JSONObject();
            manualStatusParam.put("demandNo", oneDispatch.getString("demandNo"));
            manualStatusParam.put("exportingCountry", oneDispatch.getString("exportingCountry"));
            manualStatusParam.put("materialCode", oneDispatch.getString("materialCode"));
            manualStatusParam.put("designModel", oneDispatch.getString("designModel"));
            manualStatusParam.put("salesModel", oneDispatch.getString("salesModel"));
            String manualStatus = checkManaulStatus(manualStatusParam);
            oneDispatch.put("manualStatus", manualStatus);
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "成品库发运数据导出";
        String excelName = nowDate + title;
        String[] fieldNames = {"任务状态", "操保手册状态", "操保手册状态描述", "出口国家", "整机物料编码", "设计型号", "销售型号", "整机PIN码", "需求通知单号",
                "发运通知单号", "明细号", "语言", "发车日期", "创建日期"};
        String[] fieldCodes = {"taskStatus", "manualStatus", "statusDesc", "exportingCountry", "materialCode",
                "designModel", "salesModel", "pin", "demandNo", "dispatchNo", "detailNo", "languages", "dispatchTimeFPW",
                "CREATE_TIME_"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(businessList, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    public void saveStatusDesc(JsonResult result, JSONArray demandArr) {
        if (demandArr == null || demandArr.isEmpty()) {
            return;
        }
        for (int index = 0; index < demandArr.size(); index++) {
            JSONObject oneObject = demandArr.getJSONObject(index);
            String state = oneObject.getString("_state");
            if ("modified".equalsIgnoreCase(state)) {
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_",
                        XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                exportPartsAtlasDao.updateStatusDesc(oneObject);
            }
        }
        result.setSuccess(true);
        result.setMessage("保存成功！");
    }

    /**
     * 机型制做_变动关系梳理
     *
     * @param
     * @return
     */
    public List<JSONObject> queryChangeRelList(JSONObject params) {
        List<JSONObject> itemList = exportPartsAtlasDao.queryChangeRelList(params);
        return itemList;
    }

    /**
     * 机型制做_维修专用件
     *
     * @param
     * @return
     */
    public List<JSONObject> queryWgjtczlList(JSONObject params) {
        List<JSONObject> itemList = exportPartsAtlasDao.queryWgjtczlList(params);
        return itemList;
    }

    /**
     * 机型制做_外购件资料
     *
     * @param
     * @return
     */
    public List<JSONObject> queryWxzyjList(JSONObject params) {
        List<JSONObject> itemList = exportPartsAtlasDao.queryWxzyjList(params);
        if (itemList.size() == 0) {
            JSONObject obj1 = new JSONObject();
            obj1.put("zzjName", "动臂");
            obj1.put("wxjName", "前叉组件");
            itemList.add(obj1);
            JSONObject obj2 = new JSONObject();
            obj2.put("zzjName", "动臂");
            obj2.put("wxjName", "后支撑组件");
            itemList.add(obj2);
            JSONObject obj3 = new JSONObject();
            obj3.put("zzjName", "动臂");
            obj3.put("wxjName", "动臂总成");
            itemList.add(obj3);
            JSONObject obj4 = new JSONObject();
            obj4.put("zzjName", "斗杆");
            obj4.put("wxjName", "马拉头");
            itemList.add(obj4);
            JSONObject obj5 = new JSONObject();
            obj5.put("zzjName", "斗杆");
            obj5.put("wxjName", "斗杆总成");
            itemList.add(obj5);
            JSONObject obj6 = new JSONObject();
            obj6.put("zzjName", "连杆");
            obj6.put("wxjName", "连杆总成");
            itemList.add(obj6);
            JSONObject obj7 = new JSONObject();
            obj7.put("zzjName", "偏转体");
            obj7.put("wxjName", "偏转体总成");
            itemList.add(obj7);
        }
        return itemList;
    }

    /**
     * 机型制做_维修专用件
     *
     * @param
     * @return
     */
    public List<JSONObject> queryGyhjmxList(JSONObject params) {
        List<JSONObject> itemList = exportPartsAtlasDao.queryGyhjmxList(params);
        return itemList;
    }

    //..
    public ResponseEntity<byte[]> importItemTDownload(String importType) {
        try {
            String fileName = "";
            switch (importType) {
                case "bdgxsl":
                    fileName = "变动关系梳理导入模板.xlsx";
                    break;
                case "wgjtczl":
                    fileName = "外购件图册资料导入模板.xlsx";
                    break;
                case "wxzyjsl":
                    fileName = "维修专用件梳理导入模板.xlsx";
                    break;
                case "gyhjmx":
                    fileName = "工艺合件明细导入模板.xlsx";
                    break;
                default:
                    break;
            }
            // 创建文件实例
            File file = new File(CustomsDeclarationRawdataService.class.getClassLoader().
                    getResource("templates/serviceEngineering/" + fileName).toURI());
            String finalDownloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 finalDownloadFileName
            headers.setContentDispositionFormData("attachment", finalDownloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in importItemTDownload", e);
            return null;
        }
    }

    //..
    public void importItem(JSONObject result, HttpServletRequest request) {
        try {
            String importType = RequestUtil.getString(request, "importType");
            String businessId = RequestUtil.getString(request, "businessId");
            if (StringUtil.isEmpty(importType)) {
                result.put("message", "数据导入失败，未指定导入类型！");
                return;
            }
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile fileObj = multipartRequest.getFile("importFile");
            if (fileObj == null) {
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            String fileName = ((CommonsMultipartFile) fileObj).getFileItem().getName();
            ((CommonsMultipartFile) fileObj).getFileItem().getName().endsWith(ExcelReaderUtil.EXCEL03_EXTENSION);
            Workbook wb = null;
            if (fileName.endsWith(ExcelReaderUtil.EXCEL03_EXTENSION)) {
                wb = new HSSFWorkbook(fileObj.getInputStream());
            } else if (fileName.endsWith(ExcelReaderUtil.EXCEL07_EXTENSION)) {
                wb = new XSSFWorkbook(fileObj.getInputStream());
            }
            Sheet sheet = wb.getSheet("主表");
            if (sheet == null) {
                logger.error("找不到导入模板");
                result.put("message", "数据导入失败，找不到导入模板导入页！");
                return;
            }
            int rowNum = sheet.getPhysicalNumberOfRows();
            //if (rowNum < 2) {
            if (rowNum < EXCEL_ROW_OFFSET) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }

            // 解析标题部分
//            Row titleRow = sheet.getRow(1);
            Row titleRow = sheet.getRow(EXCEL_ROW_OFFSET - 1);
            if (titleRow == null) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }
            List<String> titleList = new ArrayList<>();
            for (int i = 0; i < titleRow.getLastCellNum(); i++) {
                titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
            }

//            if (rowNum < 3) {
            if (rowNum < EXCEL_ROW_OFFSET + 1) {
                logger.info("数据行为空");
                result.put("message", "数据导入完成，数据行为空！");
                return;
            }
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> itemList = new ArrayList<>();
//            for (int i = 2; i < rowNum; i++) {
            JSONObject rowParse = new JSONObject();
            for (int i = EXCEL_ROW_OFFSET; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                switch (importType) {
                    case "bdgxsl":
                        rowParse = generateDataFromRowBdgxsl(row, itemList, titleList);
                        break;
                    case "wgjtczl":
                        rowParse = generateDataFromRowWgjtczl(row, itemList, titleList);
                        break;
                    case "wxzyjsl":
                        rowParse = generateDataFromRowWxzyjsl(row, itemList, titleList);
                        break;
                    case "gyhjmx":
                        rowParse = generateDataFromRowGyhjmx(row, itemList, titleList);
                        break;
                    default:
                        break;
                }
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            switch (importType) {
                case "bdgxsl":
                    this.importItemProcessBdgxsl(result, itemList, businessId);
                    break;
                case "wgjtczl":
                    this.importItemProcessWgjtczl(result, itemList, businessId);
                    break;
                case "wxzyjsl":
                    this.importItemProcessWxzyjsl(result, itemList, businessId);
                    break;
                case "gyhjmx":
                    this.importItemProcessGyhjmx(result, itemList, businessId);
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            logger.error("Exception in importItem", e);
            result.put("message", "数据导入失败，系统异常！:" + (e.getCause() == null ? e.getMessage() : e.getCause().getMessage()));
            System.out.println("");
        }
    }

    //..
    private JSONObject generateDataFromRowBdgxsl(Row row, List<Map<String, Object>> itemList, List<String> titleList) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>();
        DataFormatter dataFormatter = new DataFormatter();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = StringUtils.trim(dataFormatter.formatCellValue(cell));

            }
            switch (title) {
                case "参考原型物料编码":
                    if (!StringUtil.isEmpty(cellValue)) {
                        oneRowMap.put("sourcelCode", cellValue.split("\\.")[0]);
                    } else {
                        oneRowMap.put("sourcelCode", cellValue);
                    }
                    break;
                case "零部件物料描述":
                    oneRowMap.put("partDesc", cellValue);
                    break;
                case "变动后物料编码":
                    if (!StringUtil.isEmpty(cellValue)) {
                        oneRowMap.put("newCode", cellValue.split("\\.")[0]);
                    } else {
                        oneRowMap.put("newCode", cellValue);
                    }
                    break;
                case "主要变动点说明":
                    oneRowMap.put("mainChangeDesc", cellValue);
                    break;
                case "部件负责人":
                    if (!StringUtil.isEmpty(cellValue)) {
                        List<JSONObject> list = commonInfoManager.getUserIdByUserName(cellValue);
                        if (list != null && list.isEmpty()) {
                            oneRowCheck.put("message", "用户：" + cellValue + "在系统中找不到对应的账号！");
                            return oneRowCheck;
                        } else if (list != null && list.size() > 1) {
                            oneRowCheck.put("message", "用户：" + cellValue + "在系统中找到多个账号！");
                            return oneRowCheck;
                        } else if (list != null && list.size() == 1) {
                            oneRowMap.put("partPersonId", list.get(0).getString("USER_ID_"));
                            oneRowMap.put("partPerson", list.get(0).getString("FULLNAME_"));
                        } else {
                            oneRowCheck.put("message", "根据用户名查找用户信息出错！");
                            return oneRowCheck;
                        }
                    } else {
                        oneRowMap.put("partPersonId", cellValue);
                        oneRowMap.put("partPerson", cellValue);
                    }
                    break;
                case "备注":
                    oneRowMap.put("remark", cellValue);
                    break;
                default:
                    break;
            }
        }
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    //..
    private JSONObject generateDataFromRowWgjtczl(Row row, List<Map<String, Object>> itemList, List<String> titleList) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = ExcelUtil.getCellFormatValue(cell);
            }
            switch (title) {
                case "所属部件":
                    if (StringUtil.isEmpty(cellValue)) {
                        oneRowCheck.put("message", "所属部件为空");
                        return oneRowCheck;
                    }
                    String[] arr = new String[]{"液压", "动力", "覆盖件", "底盘", "电气", "转台", "传动", "其他"};
                    if (!Arrays.asList(arr).contains(cellValue)) {
                        oneRowCheck.put("message", "所属部件必须为：“液压”，“动力”，“覆盖件”，“底盘”，“电气”，“转台”，“传动”，“其他”");
                        return oneRowCheck;
                    }
                    oneRowMap.put("belongParts", cellValue);
                    break;
                case "物料名称":
                    if (StringUtil.isEmpty(cellValue)) {
                        oneRowCheck.put("message", "物料名称为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialName", cellValue);
                    break;
                case "外购件编码":
                    oneRowMap.put("wgjCode", cellValue);
                    break;
                case "物料描述":
                    oneRowMap.put("materialDesc", cellValue);
                    break;
                case "外购件选型负责人":
                    if (!StringUtil.isEmpty(cellValue)) {
                        List<JSONObject> list = commonInfoManager.getUserIdByUserName(cellValue);
                        if (list != null && list.isEmpty()) {
                            oneRowCheck.put("message", "用户：" + cellValue + "在系统中找不到对应的账号！");
                            return oneRowCheck;
                        } else if (list != null && list.size() > 1) {
                            oneRowCheck.put("message", "用户：" + cellValue + "在系统中找到多个账号！");
                            return oneRowCheck;
                        } else if (list != null && list.size() == 1) {
                            oneRowMap.put("xxyfPersonId", list.get(0).getString("USER_ID_"));
                            oneRowMap.put("xxyfPersonName", list.get(0).getString("FULLNAME_"));
                        } else {
                            oneRowCheck.put("message", "根据用户名查找用户信息出错！");
                            return oneRowCheck;
                        }
                    } else {
                        oneRowMap.put("xxyfPersonId", cellValue);
                        oneRowMap.put("xxyfPersonName", cellValue);
                    }
                    break;
                case "备注":
                    oneRowMap.put("remark", cellValue);
                    break;
                default:
                    break;
            }
        }
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    //..
    private JSONObject generateDataFromRowWxzyjsl(Row row, List<Map<String, Object>> itemList, List<String> titleList) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = ExcelUtil.getCellFormatValue(cell);
            }
            switch (title) {
                case "自制件":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "自制件为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("zzjName", cellValue);
                    break;
                case "自制件物料号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "自制件物料号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("zzjCode", cellValue.split("\\.")[0]);
                    break;
                case "物料描述及尺寸信息":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料描述及尺寸信息为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("zzjDesc", cellValue);
                    break;
                case "维修专用件":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "维修专用件为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("wxjName", cellValue);
                    break;
                case "维修专用件物料号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "维修专用件物料号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("wxjCode", cellValue.split("\\.")[0]);
                    break;
                case "物料描述":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料描述为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("wxjDesc", cellValue);
                    break;
                case "总成BOM":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "总成BOM为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("zcBom", cellValue);
                    break;
                case "备注":
                    oneRowMap.put("remark", cellValue);
                    break;
                default:
                    break;
            }
        }
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    //..
    private JSONObject generateDataFromRowGyhjmx(Row row, List<Map<String, Object>> itemList, List<String> titleList) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = ExcelUtil.getCellFormatValue(cell);
            }
            switch (title) {
                case "父项物料号":
                    if (StringUtil.isEmpty(cellValue)) {
                        oneRowCheck.put("message", "父项物料号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("fxCode", cellValue.split("\\.")[0]);
                    break;
                case "父项物料描述":
                    oneRowMap.put("fxDesc", cellValue);
                    break;
                case "工艺合件物料号":
                    if (!StringUtil.isEmpty(cellValue)) {
                        oneRowMap.put("gyhjCode", cellValue.split("\\.")[0]);
                    } else {
                        oneRowMap.put("gyhjCode", cellValue);
                    }
                    break;
                case "工艺合件描述":
                    oneRowMap.put("gyhjDesc", cellValue);
                    break;
                case "子项物料号":
                    if (!StringUtil.isEmpty(cellValue)) {
                        oneRowMap.put("zxCode", cellValue.split("\\.")[0]);
                    } else {
                        oneRowMap.put("zxCode", cellValue);
                    }
                    break;
                case "子项物料描述":
                    oneRowMap.put("zxDesc", cellValue);
                    break;
                default:
                    break;
            }
        }
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    //..
    private void importItemProcessBdgxsl(JSONObject result, List<Map<String, Object>> itemList, String businessId) {
        List<JSONObject> tempAddList = new ArrayList<>();
        for (Map<String, Object> map : itemList) {
            JSONObject itemJson = new JSONObject(map);
            itemJson.put("id", IdUtil.getId());
            itemJson.put("applyId", businessId);
            itemJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            itemJson.put("CREATE_TIME_", new Date());
            tempAddList.add(itemJson);
            if (tempAddList.size() % 50 == 0) {
                exportPartsAtlasDao.batchInsertChangeRel(tempAddList);
                tempAddList.clear();
            }
        }
        if (!tempAddList.isEmpty()) {
            exportPartsAtlasDao.batchInsertChangeRel(tempAddList);
            tempAddList.clear();
        }
        result.put("message", "数据导入成功！");
    }

    //..
    private void importItemProcessWgjtczl(JSONObject result, List<Map<String, Object>> itemList, String businessId) {
        List<JSONObject> tempAddList = new ArrayList<>();
        for (Map<String, Object> map : itemList) {
            JSONObject itemJson = new JSONObject(map);
            itemJson.put("id", IdUtil.getId());
            itemJson.put("applyId", businessId);
            itemJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            itemJson.put("CREATE_TIME_", new Date());
            tempAddList.add(itemJson);
            if (tempAddList.size() % 50 == 0) {
                exportPartsAtlasDao.batchInsertWgjtczl(tempAddList);
                tempAddList.clear();
            }
        }
        if (!tempAddList.isEmpty()) {
            exportPartsAtlasDao.batchInsertWgjtczl(tempAddList);
            tempAddList.clear();
        }
        result.put("message", "数据导入成功！");
    }

    //..
    private void importItemProcessWxzyjsl(JSONObject result, List<Map<String, Object>> itemList, String businessId) {
        List<JSONObject> tempAddList = new ArrayList<>();
        for (Map<String, Object> map : itemList) {
            JSONObject itemJson = new JSONObject(map);
            itemJson.put("id", IdUtil.getId());
            itemJson.put("applyId", businessId);
            itemJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            itemJson.put("CREATE_TIME_", new Date());
            tempAddList.add(itemJson);
            if (tempAddList.size() % 50 == 0) {
                exportPartsAtlasDao.batchInsertWxzyj(tempAddList);
                tempAddList.clear();
            }
        }
        if (!tempAddList.isEmpty()) {
            exportPartsAtlasDao.batchInsertWxzyj(tempAddList);
            tempAddList.clear();
        }
        result.put("message", "数据导入成功！");
    }

    //..
    private void importItemProcessGyhjmx(JSONObject result, List<Map<String, Object>> itemList, String businessId) {
        List<JSONObject> tempAddList = new ArrayList<>();
        for (Map<String, Object> map : itemList) {
            JSONObject itemJson = new JSONObject(map);
            itemJson.put("id", IdUtil.getId());
            itemJson.put("applyId", businessId);
            itemJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            itemJson.put("CREATE_TIME_", new Date());
            tempAddList.add(itemJson);
            if (tempAddList.size() % 50 == 0) {
                exportPartsAtlasDao.batchInsertGyhjmx(tempAddList);
                tempAddList.clear();
            }
        }
        if (!tempAddList.isEmpty()) {
            exportPartsAtlasDao.batchInsertGyhjmx(tempAddList);
            tempAddList.clear();
        }
        result.put("message", "数据导入成功！");
    }

    //..
    public void exportItem(HttpServletRequest request, HttpServletResponse response) {
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, false);
        String type = RequestUtil.getString(request, "type");
        List<JSONObject> listData = new ArrayList<>();
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "";
        String excelName = "";
        String[] fieldNames = null;
        String[] fieldCodes = null;
        switch (type) {
            case "bdgxsl":
                listData = exportPartsAtlasDao.queryChangeRelList(params);
                title = "变动关系梳理导出";
                excelName = nowDate + title;
                fieldNames = new String[]{"参考原型物料编码", "零部件物料描述", "变动后物料编码", "主要变动点说明", "部件负责人", "备注"};
                fieldCodes = new String[]{"sourcelCode", "partDesc", "newCode", "mainChangeDesc", "partPerson", "remark"};
                break;
            case "wgjtczl":
                listData = exportPartsAtlasDao.queryWgjtczlList(params);
                title = "外购件图册资料导出";
                excelName = nowDate + title;
                fieldNames = new String[]{"所属部件", "物料名称", "外购件编码", "物料描述", "外购件选型研发负责人", "备注"};
                fieldCodes = new String[]{"belongParts", "materialName", "wgjCode", "materialDesc", "xxyfPersonName", "remark"};
                break;
            case "wxzyjsl":
                listData = exportPartsAtlasDao.queryWxzyjList(params);
                title = "维修专用件梳理导出";
                excelName = nowDate + title;
                fieldNames = new String[]{"自制件", "自制件物料号", "物料描述及尺寸信息", "维修专用件", "维修专用件物料号",
                        "物料描述", "总成BOM", "备注"};
                fieldCodes = new String[]{"zzjName", "zzjCode", "zzjDesc", "wxjName", "wxjCode",
                        "wxjDesc", "zcBom", "remark"};
                break;
            case "gyhjmx":
                listData = exportPartsAtlasDao.queryGyhjmxList(params);
                title = "工艺合件明细导出";
                excelName = nowDate + title;
                fieldNames = new String[]{"父项物料号", "父项物料描述", "工艺合件物料号", "工艺合件描述", "子项物料号", "子项物料描述"};
                fieldCodes = new String[]{"fxCode", "fxDesc", "gyhjCode", "gyhjDesc", "zxCode", "zxDesc"};
                break;
            default:
                break;
        }
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        //输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    //..
    public JsonPageResult<?> dataListQueryForExport(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        rdmZhglUtil.addOrder(request, params, "service_engineering_exportpartsatlas_modelmade.CREATE_TIME_", "desc");
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    if ("status".equalsIgnoreCase(name)) {
                        if ("blank".equalsIgnoreCase(value)) {
                            params.put("statusBlank", "yes");
                        } else {
                            params.put(name, Arrays.asList(value));
                            params.put("statusBlank", "no");
                        }
                    } else {
                        params.put(name, value);
                    }
                }
            }
        }
        List<JSONObject> modelMadeList = exportPartsAtlasDao.queryModelMadeList(params);
        for (JSONObject oneData : modelMadeList) {
            if (oneData.get("CREATE_TIME_") != null) {
                oneData.put("CREATE_TIME_", DateUtil.formatDate(oneData.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }

        }
        // 添加当前任务处理人信息
        xcmgProjectManager.setTaskCurrentUserJSON(modelMadeList);
        result.setData(modelMadeList);
        int modelMadeListTotal = exportPartsAtlasDao.countQueryModelMadeList(params);
        result.setTotal(modelMadeListTotal);
        return result;
    }

    //..
    public void exportBusiness(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = dataListQueryForExport(request, response);
        List<Map<String, Object>> listData = result.getData();
        for (Map<String, Object> map : listData) {
            String status = map.get("status") != null ? map.get("status").toString() : "";
            switch (status) {
                case "RUNNING":
                    map.put("status", "运行中");
                    break;
                case "DRAFTED":
                    map.put("status", "草稿");
                    break;
                case "SUCCESS_END":
                    map.put("status", "成功结束");
                    break;
                case "DISCARD_END":
                    map.put("status", "作废");
                    break;
                case "ABORT_END":
                    map.put("status", "异常中止结束");
                    break;
            }
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "零件图册机型制作列表";
        String excelName = nowDate + title;
        String[] fieldNames = {"流程状态", "机型制作编号", "设计物料编码", "设计型号", "产品主管",
                "机型制作人", "编制人", "创建时间", "当前处理人", "当前流程任务"};
        String[] fieldCodes = {"status", "modelMadeNum", "matCode", "designType", "modelOwnerName",
                "jxzzrName", "creator", "CREATE_TIME_", "currentProcessUser", "currentProcessTask"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
