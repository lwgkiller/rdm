package com.redxun.serviceEngineering.core.service;

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
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.DecorationManualCollectDao;
import com.redxun.serviceEngineering.core.dao.DecorationManualDemandDao;
import com.redxun.serviceEngineering.core.dao.DecorationManualFileDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.serviceEngineering.core.dao.SchematicDiagramDao;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Service
public class DecorationManualDemandService {
    private static Logger logger = LoggerFactory.getLogger(DecorationManualDemandService.class);
    public static final String ITEM_STATUS_EXE = "待确认";
    public static final String ITEM_STATUS_CLEAR = "已归档";
    @Autowired
    private DecorationManualDemandDao decorationManualDemandDao;
    @Autowired
    private DecorationManualFileDao decorationManualFileDao;
    @Autowired
    private DecorationManualCollectDao decorationManualCollectDao;
    @Autowired
    private SchematicDiagramDao schematicDiagramDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private BpmSolutionManager bpmSolutionManager;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        CommonFuns.getSearchParam(params, request, true);
        List<Map<String, Object>> businessList = decorationManualDemandDao.dataListQuery(params);
        for (Map<String, Object> business : businessList) {
            if (business.get("CREATE_TIME_") != null) {
                business.put("CREATE_TIME_", DateUtil.formatDate((Date) business.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (business.get("UPDATE_TIME_") != null) {
                business.put("UPDATE_TIME_", DateUtil.formatDate((Date) business.get("UPDATE_TIME_"), "yyyy-MM-dd"));
            }

        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(businessList);
        int businessListCount = decorationManualDemandDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public JsonResult deleteBusiness(String[] ids, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        List<JSONObject> files = getFileList(businessIds);
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "decorationManualDemand").getValue();
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("mainId"), filePathBase);
        }
        for (String oneBusinessId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneBusinessId, filePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        decorationManualDemandDao.deleteBusinessFile(param);
        decorationManualDemandDao.deleteBusiness(param);
        decorationManualDemandDao.deleteItem(param);
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }

    //..
    public JSONObject getDetail(String businessId) {
        JSONObject jsonObject = decorationManualDemandDao.queryDetailById(businessId);
        if (jsonObject == null) {
            return new JSONObject();
        }
        return jsonObject;
    }

    //..
    public JSONObject getItemDetail(String businessId) {
        JSONObject itemDetail = decorationManualDemandDao.queryItemDetailById(businessId);
        if (itemDetail == null) {
            return new JSONObject();
        }
        return itemDetail;
    }

    //..
    public List<JSONObject> getItemList(JSONObject params) {
        List<JSONObject> itemList = decorationManualDemandDao.queryItemList(params);
        return itemList;
    }

    //..
    public List<JSONObject> getItemListExp(JSONObject params) {
        List<JSONObject> itemList = decorationManualDemandDao.queryItemList(params);
        List<JSONObject> itemListExp = new ArrayList<>();
        for (JSONObject item : itemList) {
            if (item.containsKey("repUserId") && StringUtil.isNotEmpty(item.getString("repUserId"))) {
                boolean isDelay = DateUtil.judgeEndBigThanStart(item.getDate("accomplishTime"), new Date());
                if (isDelay && !item.getString("businessStatus").equalsIgnoreCase(DecorationManualDemandService.ITEM_STATUS_CLEAR)) {
                    itemListExp.add(item);
                }
            }
        }
        return itemListExp;
    }

    //..
    public void createBusiness(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("applyUserId", ContextUtil.getCurrentUserId());
        formData.put("applyUser", ContextUtil.getCurrentUser().getFullname());
        formData.put("applyDepId", ContextUtil.getCurrentUser().getMainGroupId());
        formData.put("applyDep", ContextUtil.getCurrentUser().getMainGroupName());
        formData.put("businessStatus", "A");
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        decorationManualDemandDao.insertBusiness(formData);
        if (formData.getJSONArray("itemChangeData") != null && !formData.getJSONArray("itemChangeData").isEmpty()) {
            this.saveOrUpdateItems(formData);
        }

        // 如果有明细的变化则处理
//        JSONObject params = new JSONObject();
//        params.put("salesModel", formData.getString("salesModel"));
//        params.put("designModel", formData.getString("designModel"));
//        params.put("materialCode", formData.getString("materialCode"));
//        params.put("manualType", "装修手册");
//        params.put("manualStatus", DecorationManualFileService.MANUAL_STATUS_READY);
//        if (formData.getJSONArray("itemChangeData") != null && !formData.getJSONArray("itemChangeData").isEmpty()) {
//            this.saveOrUpdateItems(formData.getJSONArray("itemChangeData"), formData.getString("id"), params);
//        }
//        //新增或修改时处理手册匹配状态
//        List<JSONObject> itemList = this.getItemList(formData.getString("id"));
//        for (JSONObject item : itemList) {
//            params.put("manualLanguage", item.getString("manualLanguage"));
//            List<JSONObject> manualFiles = decorationManualFileDao.dataListQuery(params);
//            if (manualFiles.size() > 0) {
//                item.put("manualCode", manualFiles.get(0).getString("manualCode"));
//                item.put("businessStatus", this.ITEM_STATUS_CLEAR);
//            } else {
//                item.put("manualCode", "");
//                item.put("businessStatus", "");
//            }
//            this.updateItem(item);
//        }
    }

    //..
    public void updateBusiness(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        decorationManualDemandDao.updateBusiness(formData);
        if (formData.getJSONArray("itemChangeData") != null && !formData.getJSONArray("itemChangeData").isEmpty()) {
            this.saveOrUpdateItems(formData);
        }

//        // 如果有明细的变化则处理
//        JSONObject params = new JSONObject();
//        params.put("salesModel", formData.getString("salesModel"));
//        params.put("designModel", formData.getString("designModel"));
//        params.put("materialCode", formData.getString("materialCode"));
//        params.put("manualType", "装修手册");
//        params.put("manualStatus", DecorationManualFileService.MANUAL_STATUS_READY);
//        if (formData.getJSONArray("itemChangeData") != null && !formData.getJSONArray("itemChangeData").isEmpty()) {
//            this.saveOrUpdateItems(formData.getJSONArray("itemChangeData"), formData.getString("id"), params);
//        }
//        decorationManualDemandDao.updateBusiness(formData);
//        //新增或修改时处理手册匹配状态
//        List<JSONObject> itemList = this.getItemList(formData.getString("id"));
//        for (JSONObject item : itemList) {
//            params.put("manualLanguage", item.getString("manualLanguage"));
//            List<JSONObject> manualFiles = decorationManualFileDao.dataListQuery(params);
//            if (manualFiles.size() > 0) {
//                item.put("manualCode", manualFiles.get(0).getString("manualCode"));
//                item.put("businessStatus", this.ITEM_STATUS_CLEAR);
//            } else {
//                item.put("manualCode", "");
//                item.put("businessStatus", "");
//            }
//            this.updateItem(item);
//        }
    }

    //..
    private void saveOrUpdateItems(JSONObject formData) {
        JSONArray changeGridDataJson = formData.getJSONArray("itemChangeData");
        String mainId = formData.getString("id");
        for (int i = 0; i < changeGridDataJson.size(); i++) {
            JSONObject oneObject = changeGridDataJson.getJSONObject(i);
            String state = oneObject.getString("_state");
            String id = oneObject.getString("id");
            oneObject.put("mainId", mainId);
            if ("added".equals(state) || StringUtils.isBlank(id)) {
                //新增
                oneObject.put("id", IdUtil.getId());
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", new Date());
                this.processItemStatus(formData, oneObject);
                decorationManualDemandDao.insertItem(oneObject);
            } else if ("modified".equals(state)) {
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", new Date());
                this.processItemStatus(formData, oneObject);
                decorationManualDemandDao.updateItem(oneObject);
            } else if ("removed".equals(state)) {
                JSONObject params2 = new JSONObject();
                params2.put("id", id);
                decorationManualDemandDao.deleteItem(params2);
            } else {//无状态的也要匹配
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", new Date());
                this.processItemStatus(formData, oneObject);
                decorationManualDemandDao.updateItem(oneObject);
            }
        }
    }

    //..
    private void processItemStatus(JSONObject formData, JSONObject itemData) {
        JSONObject params = new JSONObject();
        String manualType = itemData.getString("manualType");
        if (StringUtil.isNotEmpty(manualType)) {
            if (manualType.contains("原理图") && manualType.split("-").length > 1) {
                params.put("salesModel", formData.getString("salesModel"));
                params.put("designModel", formData.getString("designModel"));
                params.put("materialCodeProduct", formData.getString("materialCode"));
                params.put("diagramType", manualType.split("-")[1]);
                params.put("manualStatus", SchematicDiagramService.MANUAL_STATUS_READY);
                List<JSONObject> manualFiles = schematicDiagramDao.dataListQuery(params);
                if (manualFiles.size() > 0) {
                    itemData.put("manualCode", manualFiles.get(0).getString("manualCode"));
                    itemData.put("businessStatus", this.ITEM_STATUS_CLEAR);
                } else {
                    itemData.put("manualCode", "");
                    itemData.put("businessStatus", "");
                }
            } else {
                params.put("salesModel", formData.getString("salesModel"));
                params.put("designModel", formData.getString("designModel"));
                params.put("materialCode", formData.getString("materialCode"));
                params.put("manualType", manualType);
                params.put("manualStatus", DecorationManualFileService.MANUAL_STATUS_READY);
                params.put("manualLanguage", itemData.getString("manualLanguage"));
                List<JSONObject> manualFiles = decorationManualFileDao.dataListQuery(params);
                if (manualFiles.size() > 0) {
                    itemData.put("manualCode", manualFiles.get(0).getString("manualCode"));
                    itemData.put("businessStatus", this.ITEM_STATUS_CLEAR);
                } else {
                    itemData.put("manualCode", "");
                    itemData.put("businessStatus", "");
                }
            }
        }
    }

    //..取消
    public void saveOrUpdateItems_obsolete(JSONArray changeGridDataJson, String mainId, JSONObject params) {
        for (int i = 0; i < changeGridDataJson.size(); i++) {
            JSONObject oneObject = changeGridDataJson.getJSONObject(i);
            String state = oneObject.getString("_state");
            String id = oneObject.getString("id");
            oneObject.put("mainId", mainId);
            if ("added".equals(state) || StringUtils.isBlank(id)) {
                // 新增
                oneObject.put("id", IdUtil.getId());
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", new Date());
                decorationManualDemandDao.insertItem(oneObject);
            } else if ("modified".equals(state)) {
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", new Date());
                decorationManualDemandDao.updateItem(oneObject);
            } else if ("removed".equals(state)) {
                JSONObject params2 = new JSONObject();
                params2.put("id", id);
                decorationManualDemandDao.deleteItem(params2);
            }
        }
    }

    //..
    public void deleteOneBusinessFile(String fileId, String fileName, String businessId) {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "decorationManualDemand").getValue();
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, businessId, filePathBase);
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        decorationManualDemandDao.deleteBusinessFile(param);
    }

    //..
    public List<JSONObject> getFileList(List<String> businessIdList) {
        List<JSONObject> fileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIdList);
        fileList = decorationManualDemandDao.queryFileList(param);
        return fileList;
    }

    //..
    public void saveUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "decorationManualDemand").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find FilePathBase");
            return;
        }
        try {
            String businessId = toGetParamVal(parameters.get("businessId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + businessId;
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
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("mainId", businessId);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            decorationManualDemandDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    //..
    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    //..
    public void exportList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = dataListQueryForExport(request, response);
        List<Map<String, Object>> listData = result.getData();
        for (Map<String, Object> map : listData) {
            String businessStatus = map.get("businessStatus") != null ? map.get("businessStatus").toString() : "";
            switch (businessStatus) {
                case "A":
                    map.put("businessStatus", "编辑中");
                    break;
                case "B":
                    map.put("businessStatus", "申请部门领导审核");
                    break;
                case "C":
                    map.put("businessStatus", "产品所领导审核");
                    break;
                case "D":
                    map.put("businessStatus", "承办部门领导审核");
                    break;
                case "E":
                    map.put("businessStatus", "分管领导审批");
                    break;
                case "F":
                    map.put("businessStatus", "需求确认");
                    break;
                case "Z":
                    map.put("businessStatus", "结束");
                    break;
            }
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "装修手册需求申请";
        String excelName = nowDate + title;
        String[] fieldNames = {"单据编号", "销售型号", "设计型号", "物料编码", "销售区域", "销售国家", "配置描述",
                "申请时间", "需求时间", "申请人", "申请部门", "审批状态", "当前处理人", "流程结束时间", "备注"};
        String[] fieldCodes = {"busunessNo", "salesModel", "designModel", "materialCode", "salesArea", "salesCountry", "configurationDescription",
                "applyTime", "publishTime", "applyUser", "applyDep", "businessStatus", "currentProcessUser", "UPDATE_TIME_", "remark"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    //..
    public JsonPageResult<?> dataListQueryForExport(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        CommonFuns.getSearchParam(params, request, false);
        int businessListCount = decorationManualDemandDao.countDataListQuery(params);
        //params.put("pageSize", businessListCount);
        List<Map<String, Object>> businessList = decorationManualDemandDao.dataListQuery(params);
        for (Map<String, Object> business : businessList) {
            if (business.get("CREATE_TIME_") != null) {
                business.put("CREATE_TIME_", DateUtil.formatDate((Date) business.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (business.get("UPDATE_TIME_") != null) {
                business.put("UPDATE_TIME_", DateUtil.formatDate((Date) business.get("UPDATE_TIME_"), "yyyy-MM-dd"));
            }

        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(businessList);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public JsonResult createCollect(JSONObject params) throws Exception {
        JsonResult result = new JsonResult(true, "创建成功！");
        JSONObject demandDetail = this.getDetail(params.getString("demandId"));
        JSONObject itemDetail = this.getItemDetail(params.getString("demandItemId"));
        String instructions = params.getString("instructions");
        String collectType = params.getString("collectType");
        //数据源头信息必填项正确性校验
        if (false == this.synValidMainDetail(result, demandDetail) ||
                false == this.synValidItemDetail(result, itemDetail)) {
            return result;
        }
        //已创建的流程重复性检查
        if (false == this.synValidCollectUnique(result, params)) {
            return result;
        }
        //查找相关solution
        String solId = this.synGetDecorationManualCollectSolId(result);
        if (StringUtil.isEmpty(solId)) {
            return result;
        }
        //拼接装修手册资料收集表单数据
        JSONObject collectDetail = this.synTransToDecorationManualCollectDetail(demandDetail, itemDetail, instructions, collectType);
        //启动流程
        result = this.startProcess(solId, collectDetail);
        if (result.getSuccess()) {
            //反写的需求明细表单状态数据更新
            decorationManualDemandDao.updateItem(itemDetail);
            result.setMessage("创建成功！");
        } else {
            return result;
        }
        return result;
    }

    //..需求信息必填项正确性校验，冗余防护，表单到这一步肯定不会为空的
    private boolean synValidMainDetail(JsonResult result, JSONObject mainDetail) {
        if (StringUtil.isEmpty(mainDetail.getString("salesModel"))) {
            result.setSuccess(false);
            result.setMessage("创建失败,需求信息中销售型号为空！");
            return false;
        }
        if (StringUtil.isEmpty(mainDetail.getString("designModel"))) {
            result.setSuccess(false);
            result.setMessage("创建失败,需求信息中设计型号为空！");
            return false;
        }
        if (StringUtil.isEmpty(mainDetail.getString("materialCode"))) {
            result.setSuccess(false);
            result.setMessage("创建失败,需求信息中物料号为空！");
            return false;
        }
        if (StringUtil.isEmpty(mainDetail.getString("salesArea"))) {
            result.setSuccess(false);
            result.setMessage("创建失败,需求信息中销售区域为空！");
            return false;
        }
        if (StringUtil.isEmpty(mainDetail.getString("publishTime"))) {
            result.setSuccess(false);
            result.setMessage("创建失败,需求信息中需求时间为空！");
            return false;
        }
        return true;
    }

    //..异常信息必填项正确性校验，冗余防护，表单到这一步肯定不会为空的
    private boolean synValidItemDetail(JsonResult result, JSONObject itemDetail) {
        if (StringUtil.isEmpty(itemDetail.getString("manualLanguage"))) {
            result.setSuccess(false);
            result.setMessage("创建失败,语言为空！");
            return false;
        }
        return true;
    }

    //..已创建的流程重复性检查
    private boolean synValidCollectUnique(JsonResult result, JSONObject params) {
        if (decorationManualCollectDao.countDataListQuery(params) > 0) {
            result.setSuccess(false);
            result.setMessage("创建失败,此条目已经有存在相应的收集流程数据！");
            return false;
        }
        return true;
    }

    //..查找相关solution
    private String synGetDecorationManualCollectSolId(JsonResult result) {
        BpmSolution bpmSol = bpmSolutionManager.getByKey("decorationManualCollect", "1");
        String solId = "";
        if (bpmSol != null) {
            solId = bpmSol.getSolId();
        } else {
            result.setSuccess(false);
            result.setMessage("创建失败,装修手册资料收集流程方案获取出现异常！");
        }
        return solId;
    }

    //..拼接装修手册资料收集表单数据,同时反写需求明细表单状态数据
    private JSONObject synTransToDecorationManualCollectDetail(JSONObject demandDetail, JSONObject itemDetail, String... what) {
        JSONObject collectDetail = new JSONObject();
        collectDetail.put("instructions", what[0]);
        collectDetail.put("collectType", what[1]);
        collectDetail.put("salesModel", demandDetail.getString("salesModel"));
        collectDetail.put("designModel", demandDetail.getString("designModel"));
        collectDetail.put("materialCode", demandDetail.getString("materialCode"));
        collectDetail.put("salesArea", demandDetail.getString("salesArea"));
        collectDetail.put("manualLanguage", itemDetail.getString("manualLanguage"));
        collectDetail.put("publishTime", demandDetail.getString("publishTime"));
        collectDetail.put("demandId", demandDetail.getString("id"));
        collectDetail.put("demandItemId", itemDetail.getString("id"));
        collectDetail.put("demandInstId", demandDetail.getString("INST_ID_"));
        collectDetail.put("demandBusunessNo", demandDetail.getString("busunessNo"));
        //处理反写
        if (what[0].equalsIgnoreCase(DecorationManualCollectService.INSTRUCTIONS_TRANSLATION)) {
            if (StringUtil.isEmpty(itemDetail.getString("businessStatus"))) {
                itemDetail.put("businessStatus", what[0]);
            }
        } else {
            if (StringUtil.isEmpty(itemDetail.getString("businessStatus"))) {
                itemDetail.put("businessStatus", what[0] + "-" + what[1]);
            } else {
                itemDetail.put("businessStatus", itemDetail.getString("businessStatus") + "," + what[0] + "-" + what[1]);
            }
        }//处理反写
        return collectDetail;
    }

    //..启动流程
    private JsonResult startProcess(String solId, JSONObject collectDetail) throws Exception {
        ProcessMessage handleMessage = new ProcessMessage();
        BpmInst bpmInst = null;
        JsonResult result = new JsonResult();
        try {
            ContextUtil.setCurUser(ContextUtil.getCurrentUser());
            ProcessHandleHelper.setProcessMessage(handleMessage);
            ProcessStartCmd startCmd = new ProcessStartCmd();
            startCmd.setSolId(solId);
            startCmd.setJsonData(collectDetail.toJSONString());
            //启动流程
            //bpmInstManager.doStartProcess(startCmd);
            //创建草稿
            bpmInstManager.doStartProcess(startCmd, false);
            result.setData(startCmd.getBusinessKey());
        } catch (Exception ex) {
            //把具体的错误放置在内部处理，以显示正确的错误信息提示，在此不作任何的错误处理
            logger.error(ExceptionUtil.getExceptionMessage(ex));
            if (handleMessage.getErrorMsges().size() == 0) {
                handleMessage.addErrorMsg(ex.getMessage());
            }
        } finally {
            //在处理过程中，是否有错误的消息抛出
            if (handleMessage.getErrorMsges().size() > 0) {
                result.setSuccess(false);
                result.setMessage("启动流程失败!");
                result.setData(handleMessage.getErrors());
            } else {
                result.setSuccess(true);
            }
            ProcessHandleHelper.clearProcessCmd();
            ProcessHandleHelper.clearProcessMessage();
        }
        return result;
    }

    //..
    public JsonResult syncManual(String postBody) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        JSONObject postJson = JSONObject.parseObject(postBody);
        JSONArray itemArray = postJson.getJSONArray("rows");
        for (Object o : itemArray) {
            JSONObject itemJson = (JSONObject) o;
            this.processItemStatus(postJson, itemJson);
            itemJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            itemJson.put("UPDATE_TIME_", new Date());
            decorationManualDemandDao.updateItem(itemJson);
        }
        return result;
    }

    //..
    public void updateItem(JSONObject demandItemDetail) {
        decorationManualDemandDao.updateItem(demandItemDetail);
    }
}
