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
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualDemandDao;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualfileDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.manager.SysSeqIdManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.mybatis.spring.MyBatisSystemException;
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
public class MaintenanceManualDemandService {
    private static Logger logger = LoggerFactory.getLogger(MaintenanceManualDemandService.class);
    //原始单据异动标记
    public static final String DEMAND_LIST_STATUS_CHONGFUCHUANGJIAN = "重复创建";//重复创建
    public static final String DEMAND_LIST_STATUS_XINZENGMINGXI = "新增明细";//新增明细
    public static final String DEMAND_LIST_STATUS_SHANCHUMINGXI = "删除明细";//删除明细
    public static final String DEMAND_LIST_STATUS_PEIZHIBIANGENG = "配置变更";//配置变更
    @Autowired
    private MaintenanceManualDemandDao maintenanceManualDemandDao;
    @Autowired
    private MaintenanceManualfileDao maintenanceManualfileDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private SysSeqIdManager sysSeqIdManager;
    @Autowired
    private BpmSolutionManager bpmSolutionManager;
    @Autowired
    private UserService userService;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        String collectorId = RequestUtil.getString(request, "collectorId");//收藏人id侵入
        if (StringUtils.isNotEmpty(collectorId)) {
            params.put("collectorId", collectorId);
        }
        String businessId = RequestUtil.getString(request, "businessId");//提交时检有没有查重复业务的id参数侵入
        if (StringUtil.isNotEmpty(businessId)) {
            JSONObject detail = this.getDetail(businessId);
            params.put("demandListNo", detail.getString("demandListNo"));
            params.put("salesModel", detail.getString("salesModel"));
            params.put("designModel", detail.getString("designModel"));
            params.put("materialCode", detail.getString("materialCode"));
            params.put("manualLanguage", detail.getString("manualLanguage"));
            params.put("isCE", detail.getString("isCE"));
            params.put("salesArea", detail.getString("salesArea"));
            params.put("quantity", detail.getString("quantity"));
            params.put("publishTime", detail.getString("publishTime"));
            params.put("xiaohanNoNoNo", "xiaohanNoNoNo");
        }
        List<Map<String, Object>> businessList = maintenanceManualDemandDao.dataListQuery(params);
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
        int businessListCount = maintenanceManualDemandDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    private void getListParams(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "businessStatus");
            params.put("sortOrder", "asc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
//                    if ("communicateStartTime".equalsIgnoreCase(name)) {
//                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
//                    }
//                    if ("communicateEndTime".equalsIgnoreCase(name)) {
//                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
//                    }
                    params.put(name, value);
                }
            }
        }

        // 增加分页条件
        params.put("startIndex", 0);
        params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
    }

    //..
    public JsonResult deleteBusiness(String[] ids, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        List<JSONObject> files = getBusinessFileList(businessIds);
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "maintenanceManualDemand").getValue();
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("mainId"), filePathBase);
        }
        for (String oneBusinessId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneBusinessId, filePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        maintenanceManualDemandDao.deleteBusinessFile(param);
        maintenanceManualDemandDao.deleteManualMatch(param);
        maintenanceManualDemandDao.deleteBusiness(param);
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }

    //..
    public JsonResult amHandle(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        for (String businessId : businessIds) {
            JSONObject detail = maintenanceManualDemandDao.queryDetailById(businessId);
            if (detail != null) {
                detail.put("amHandle", "已处理" + DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_FULL));
                maintenanceManualDemandDao.updateBusiness(detail);
            }
        }
        return result;
    }

    //..
    public List<JSONObject> getBusinessFileList(List<String> businessIdList) {
        List<JSONObject> businessFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIdList);
        businessFileList = maintenanceManualDemandDao.queryFileList(param);
        return businessFileList;
    }

    //..
    public JSONObject getDetail(String businessId) {
        JSONObject jsonObject = maintenanceManualDemandDao.queryDetailById(businessId);
        if (jsonObject == null) {
            return new JSONObject();
        }
        return jsonObject;
    }

    //..
    public void createBusiness(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("busunessNo", sysSeqIdManager.genSequenceNo("maintenanceManualDemand", ContextUtil.getCurrentTenantId()));
        formData.put("applyUserId", ContextUtil.getCurrentUserId());
        formData.put("applyUser", ContextUtil.getCurrentUser().getFullname());
        formData.put("applyDepId", ContextUtil.getCurrentUser().getMainGroupId());
        formData.put("applyDep", ContextUtil.getCurrentUser().getMainGroupName());
        //@lwgkiller:此处是因为草稿状态无节点，提交后首节点会跳过，因此默认将首节点（编制中）的编号进行初始化写入
        formData.put("businessStatus", "A-editing");
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
//        if (formData.getString("designModel").equalsIgnoreCase("XE135C.01")) {
//            int i = 1 / 0;
//        }
        //基本信息
        formData.put("manualLanguage", formData.getString("manualLanguage")
                .replaceAll("汉语", "中文")
                .replaceAll("西班牙语", "西语"));
        maintenanceManualDemandDao.insertBusiness(formData);
        //明细信息
        this.manualMatchProcess(formData.getString("id"), formData.getJSONArray("changeManualMatchListGrid"));
    }

    //..
    public void updateBusiness(JSONObject formData) {
        //获取匹配归档信息
        JSONObject params = new JSONObject();
        params.put("businessId", formData.getString("id"));
        List<JSONObject> manualMatchList = maintenanceManualDemandDao.getManualMatchList(params);
        if (manualMatchList != null && manualMatchList.size() == 1) {//只有唯一一本的时候进行匹配
            formData.put("manualfileId", manualMatchList.get(0).getString("manualFileId"));
            formData.put("manualCode", manualMatchList.get(0).getString("manualCode"));
            formData.put("manualVersion", manualMatchList.get(0).getString("manualVersion"));
        } else {
            formData.put("manualfileId", "未匹配到");
            formData.put("manualCode", "未匹配到");
            formData.put("manualVersion", "未匹配到");
        }
//        params.put("salesModel", formData.getString("salesModel"));
//        params.put("designModel", formData.getString("designModel"));
//        params.put("materialCode", formData.getString("materialCode"));
//        params.put("manualLanguage", formData.getString("manualLanguage"));
//        params.put("isCE", formData.getString("isCE"));
//        params.put("manualStatus", "可打印");
//        List<JSONObject> jsonObjects = maintenanceManualfileDao.dataListQuery(params);
//        //匹配到了，理论上只能匹配到一本，因此默认取第一个的manualCode
//        if (jsonObjects.size() > 0) {
//            formData.put("manualfileId", jsonObjects.get(0).getString("id"));
//            formData.put("manualCode", jsonObjects.get(0).getString("manualCode"));
//            formData.put("manualVersion", jsonObjects.get(0).getString("manualVersion"));
//        } else {
//            formData.put("manualfileId", "未匹配到");
//            formData.put("manualCode", "未匹配到");
//            formData.put("manualVersion", "未匹配到");
//        }
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        //基本信息
        maintenanceManualDemandDao.updateBusiness(formData);
        //明细信息
        this.manualMatchProcess(formData.getString("id"), formData.getJSONArray("changeManualMatchListGrid"));
    }

    //..
    private void manualMatchProcess(String businessId, JSONArray subArr) {
        if (subArr == null || subArr.isEmpty()) {
            return;
        }
        JSONObject param = new JSONObject();
        param.put("ids", new JSONArray());
        for (int index = 0; index < subArr.size(); index++) {
            JSONObject oneObject = subArr.getJSONObject(index);
            String id = oneObject.getString("id");
            String state = oneObject.getString("_state");
            if ("added".equals(state) || StringUtils.isBlank(id)) {
                //新增
                oneObject.put("id", IdUtil.getId());
                oneObject.put("businessId", businessId);
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                maintenanceManualDemandDao.insertManualMatch(oneObject);
            } else if ("removed".equals(state)) {
                //删除 只有MCT最终回传才会带回参考标记，那时候就不能删除了，理论上这个判断没用，但是保留着
                if (!oneObject.containsKey("REF_ID_")
                        || !oneObject.getString("REF_ID_").equalsIgnoreCase("1")) {
                    param.getJSONArray("ids").add(oneObject.getString("id"));
                }
            }
        }
        if (!param.getJSONArray("ids").isEmpty()) {
            maintenanceManualDemandDao.deleteManualMatch(param);
        }
    }

    //..
    public void deleteOneBusinessFile(String fileId, String fileName, String businessId) {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "maintenanceManualDemand").getValue();
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, businessId, filePathBase);
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        maintenanceManualDemandDao.deleteBusinessFile(param);
    }

    //..
    public List<JSONObject> getFileList(List<String> businessIdList) {
        List<JSONObject> fileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIdList);
        fileList = maintenanceManualDemandDao.queryFileList(param);
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
                "serviceEngineeringUploadPosition", "maintenanceManualDemand").getValue();
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
            maintenanceManualDemandDao.addFileInfos(fileInfo);
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
                case "A-editing":
                    map.put("businessStatus", "编辑中");
                    break;
                case "B-confirming":
                    map.put("businessStatus", "确认信息");
                    break;
                case "C-confirmingProduct":
                    map.put("businessStatus", "产品主管确认");
                    break;
                case "D1-adding":
                    map.put("businessStatus", "新增中");
                    break;
                case "D3-translating":
                    map.put("businessStatus", "翻译中");
                    break;
                case "E-printing":
                    map.put("businessStatus", "打印");
                    break;
                case "F-close":
                    map.put("businessStatus", "结束");
                    break;
            }

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
        String title = "操保手册发运/需求申请";
        String excelName = nowDate + title;
        String[] fieldNames = {"流程状态", "单据编号", "需求单号", "原始需求异动", "异动处理状态", "销售型号", "设计型号", "物料编码", "语言", "是否CE版", "销售区域", "数量", "配置描述",
                "手册编号", "申请时间", "需求时间", "申请人", "申请部门", "审批状态", "当前处理人", "服务工程处理人", "流程结束时间", "备注"};
        String[] fieldCodes = {"status", "busunessNo", "demandListNo", "demandListStatus", "amHandle", "salesModel", "designModel", "materialCode", "manualLanguage", "isCE", "salesArea", "quantity", "configurationDescription",
                "manualCode", "applyTime", "publishTime", "applyUser", "applyDep", "businessStatus", "currentProcessUser", "Bconfirming", "UPDATE_TIME_", "remark"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    //..不分页
    public JsonPageResult<?> dataListQueryForExport(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        int businessListCount = maintenanceManualDemandDao.countDataListQuery(params);
        params.put("pageSize", businessListCount);
        List<Map<String, Object>> businessList = maintenanceManualDemandDao.dataListQuery(params);
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
    public void createCollect(JSONObject formData) {
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        maintenanceManualDemandDao.createCollect(formData);
    }

    //..
    public JsonResult deleteCollect(String[] IdsArr, String userId) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        param.put("collectorId", userId);
        for (String id : IdsArr) {
            param.put("businessId", id);
            maintenanceManualDemandDao.deleteCollect(param);
        }
        return result;
    }

    //..
    public JsonResult copyBusiness(String businessId) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        //查找相关solution
        String solId = this.synGetMaintenanceManualDemandSolId(result);
        if (StringUtil.isEmpty(solId)) {
            return result;
        }
        JSONObject jsonObject = maintenanceManualDemandDao.queryDetailById(businessId);
        jsonObject.put("id", "");
        jsonObject.put("INST_ID_", "");
        jsonObject.put("busunessNo", "");
        jsonObject.put("businessStatus", "");
        jsonObject.put("instructions", "");
        jsonObject.put("BconfirmingId", "");
        jsonObject.put("Bconfirming", "");
        jsonObject.put("manualCode", "");
        jsonObject.put("manualfileId", "");
        jsonObject.put("manualVersion", "");
        jsonObject.put("CREATE_BY_", "");
        //启动流程
        result = this.startProcess(solId, jsonObject);
        return result;
    }

    //..启动流程
    private JsonResult startProcess(String solId, JSONObject collectDetail) {
        ProcessMessage handleMessage = new ProcessMessage();
        BpmInst bpmInst = null;
        JsonResult result = new JsonResult();
        IUser currentUser = ContextUtil.getCurrentUser();//暂存当前用户
        try {
            IUser user = userService.getByUserId(collectDetail.getString("CREATE_BY_"));//取表单当前用户
            if (user != null) {//表单当前用户不空的话，用表单当前用户代替当前用户
                ContextUtil.setCurUser(user);
            }
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
                result.setMessage("启动流程失败!" + handleMessage.getErrors());
                result.setData(handleMessage.getErrors());
            } else {
                result.setSuccess(true);
            }
            ProcessHandleHelper.clearProcessCmd();
            ProcessHandleHelper.clearProcessMessage();
            ContextUtil.setCurUser(currentUser);//恢复当前用户
        }
        return result;
    }

    //..查找相关solution
    private String synGetMaintenanceManualDemandSolId(JsonResult result) {
        BpmSolution bpmSol = bpmSolutionManager.getByKey("maintenanceManualDemand", "1");
        String solId = "";
        if (bpmSol != null) {
            solId = bpmSol.getSolId();
        } else {
            result.setSuccess(false);
            result.setMessage("创建失败,操保手册需求申请流程方案获取出现异常！");
        }
        return solId;
    }

    //..获取匹配明细
    public List<JSONObject> getManualMatchList(JSONObject params) {
        List<JSONObject> manualMatchList = maintenanceManualDemandDao.getManualMatchList(params);
        return manualMatchList;
    }

    //..从外部系统创建2022-12-23
    public void createFormExportPartsAtlas(JSONObject postData, String whatTypeToStart) {
        try {
            //1.拼接操保手册需求申请表单数据
            Map<String, JSONObject> maintDemandToBeCreateMap = this.synTransOutSysDataToMaintDemand(postData);
            System.out.println("");
            //2.查找相关solution,启动流程
            if (maintDemandToBeCreateMap != null && !maintDemandToBeCreateMap.isEmpty()) {
                JsonResult jsonResult = new JsonResult(true);
                String solId = this.synGetMaintenanceManualDemandSolId(jsonResult);
                if (!jsonResult.getSuccess()) {
                    logger.error("createFormExportPartsAtlas未找到流程方案");
                    return;
                }
                for (Map.Entry<String, JSONObject> entry : maintDemandToBeCreateMap.entrySet()) {
                    jsonResult = this.startProcess(solId, entry.getValue());
                    if (!jsonResult.getSuccess()) {
                        logger.error("createFormExportPartsAtl操保手册需求单启动错误，原始需求单号：" + entry.getValue().getString("demandListNo") +
                                "设计型号:" + entry.getValue().getString("designModel") + "销售型号:" + entry.getValue().getString("salesModel") +
                                "物料号:" + entry.getValue().getString("materialCode") + "语言:" + entry.getValue().getString("manualLanguage"));
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("通过零件图册创建操保手册异常", e);
        }
    }

    //..从零件图册模块更新异动2023-04-17
    public void updateFromExportPartsAtlasService(StringBuilder changeDesc, String demandNo) {
        try {
            JSONObject params = new JSONObject();
            params.put("demandListNo", demandNo);
            List<Map<String, Object>> maintenanceManualDemandList = maintenanceManualDemandDao.dataListQueryExactByDemandListNo(params);
            if (maintenanceManualDemandList != null && !maintenanceManualDemandList.isEmpty()) {
                for (Map<String, Object> maintDemandToBeUpdate : maintenanceManualDemandList) {
                    String s = maintDemandToBeUpdate.containsKey("demandListStatus") ?
                            maintDemandToBeUpdate.get("demandListStatus").toString() : "";
                    maintDemandToBeUpdate.put("demandListStatus", s + changeDesc.toString() + "(" +
                            DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_FULL) + ");<br>");
                    maintDemandToBeUpdate.put("amHandle", "未处理");
                    maintenanceManualDemandDao.updateBusiness(new JSONObject(maintDemandToBeUpdate));
                    //发钉钉
                    params.clear();
                    params.put("roleKey", "maintenanceManualAdmin");
                    List<JSONObject> userList = maintenanceManualfileDao.getUsersByRolekey(params);
                    StringBuilder userids = new StringBuilder();
                    for (JSONObject user : userList) {
                        userids.append(user.getString("USER_ID_")).append(",");
                    }
                    String busunessNo = maintDemandToBeUpdate.get("busunessNo").toString();
                    String message = "需求单号为:‘" + demandNo + "’编号为:‘" + busunessNo + "’的操保手册需求申请产生异动！";
                    JSONObject noticeObj = new JSONObject();
                    noticeObj.put("content", message);
                    sendDDNoticeManager.sendNoticeForCommon(userids.substring(0, userids.length() - 1), noticeObj);
                }
            }
        } catch (Exception e) {
            logger.error("通过主单的需求号更新操保手册异动异常", e);
        }
    }

    //..拼接操保手册需求申请表单数据2022-12-23
    private Map<String, JSONObject> synTransOutSysDataToMaintDemand(JSONObject outSysData) {
        JSONObject demandResultObj = outSysData.getJSONObject("demandResultObj");
        Map<String, JSONObject> detailNo2Obj = (Map<String, JSONObject>) outSysData.get("detailNo2Obj");
        Map<String, List<JSONObject>> detailNo2ConfigList =
                (Map<String, List<JSONObject>>) outSysData.get("detailNo2ConfigList");
        //声明一个<designType-saleType-matCode-languages,待创建的单据业务体>的Map
        Map<String, JSONObject> toBeCreate = new HashedMap();
        for (Map.Entry<String, JSONObject> entry : detailNo2Obj.entrySet()) {
            String key = "";
            JSONObject detail = entry.getValue();
            if (detail.containsKey("field_l1i12__c") && StringUtil.isNotEmpty(detail.getString("field_l1i12__c")) &&
                    detail.getString("field_l1i12__c").equalsIgnoreCase("true")) {
                //需要EC自声明
                key = detail.getString("designType") + "-" +
                        detail.getString("saleType") + "-" +
                        detail.getString("matCode") + "-" +
                        detail.getString("languages") + "-" +
                        detail.getString("field_8M8qH__c") + "-" +
                        detail.getString("field_7p01H__c");
            } else {
                //不需要EC自声明
                key = detail.getString("designType") + "-" +
                        detail.getString("saleType") + "-" +
                        detail.getString("matCode") + "-" +
                        detail.getString("languages");
            }
            if (toBeCreate.containsKey(key))//已经存在操保需求单业务意义上相同的
            {
                JSONObject maintDemand = toBeCreate.get(key);
                if (maintDemand.getString("publishTime").compareTo(detail.getString("needTime").substring(0, 10)) > 0) {
                    maintDemand.put("publishTime", detail.getString("needTime").substring(0, 10));
                }
                maintDemand.put("quantity", maintDemand.getInteger("quantity") + detail.getInteger("needNumber"));
                maintDemand.put("remark", maintDemand.getString("remark") + detail.getString("detailNo") + ";");
                if (detail.containsKey("field_l1i12__c") && StringUtil.isNotEmpty(detail.getString("field_l1i12__c")) &&
                        detail.getString("field_l1i12__c").equalsIgnoreCase("true")) {
                    maintDemand.put("isSelfDeclaration", "是");
                    maintDemand.put("CEOnlyNum", detail.getString("field_7p01H__c"));
                    maintDemand.put("CELanguage", detail.getString("field_8M8qH__c"));
                } else if (detail.containsKey("field_l1i12__c") && StringUtil.isNotEmpty(detail.getString("field_l1i12__c")) &&
                        detail.getString("field_l1i12__c").equalsIgnoreCase("false")) {
                    maintDemand.put("isSelfDeclaration", "否");
                }
                List<JSONObject> ConfigList = detailNo2ConfigList.get(detail.getString("detailNo"));
                if (ConfigList != null && !ConfigList.isEmpty()) {
                    StringBuilder configSb = new StringBuilder();
                    configSb.append(detail.getString("detailNo")).append(":");
                    for (JSONObject config : ConfigList) {
                        configSb.append(config.getString("configDesc")).append("|");
                    }
                    configSb.append(";");
                    maintDemand.put("configurationDescription", maintDemand.getString("configurationDescription") + configSb.toString());
                }
            } else {//不存在操保需求单业务意义上相同的
                JSONObject maintDemand = new JSONObject();
                maintDemand.put("salesArea", demandResultObj.getString("exportCountryName"));
                maintDemand.put("demandListNo", demandResultObj.getString("demandNo"));
                maintDemand.put("applyTime", DateUtil.formatDate(demandResultObj.getDate("CREATE_TIME_"), DateUtil.DATE_FORMAT_YMD));
                maintDemand.put("salesModel", detail.getString("saleType"));
                maintDemand.put("designModel", detail.getString("designType"));
                maintDemand.put("materialCode", detail.getString("matCode"));
                maintDemand.put("manualLanguage", detail.getString("languages"));
                maintDemand.put("publishTime", detail.getString("needTime").substring(0, 10));
                maintDemand.put("quantity", detail.getInteger("needNumber"));
                maintDemand.put("remark", detail.getString("detailNo") + ";");
                if (detail.containsKey("field_l1i12__c") && StringUtil.isNotEmpty(detail.getString("field_l1i12__c")) &&
                        detail.getString("field_l1i12__c").equalsIgnoreCase("true")) {
                    maintDemand.put("isSelfDeclaration", "是");
                    maintDemand.put("CEOnlyNum", detail.getString("field_7p01H__c"));
                    maintDemand.put("CELanguage", detail.getString("field_8M8qH__c"));
                } else if (detail.containsKey("field_l1i12__c") && StringUtil.isNotEmpty(detail.getString("field_l1i12__c")) &&
                        detail.getString("field_l1i12__c").equalsIgnoreCase("false")) {
                    maintDemand.put("isSelfDeclaration", "否");
                }
                List<JSONObject> ConfigList = detailNo2ConfigList.get(detail.getString("detailNo"));
                if (ConfigList != null && !ConfigList.isEmpty()) {
                    StringBuilder configSb = new StringBuilder();
                    configSb.append(detail.getString("detailNo")).append(":");
                    for (JSONObject config : ConfigList) {
                        configSb.append(config.getString("configDesc")).append("|");
                    }
                    configSb.append(";");
                    maintDemand.put("configurationDescription", configSb.toString());
                }
                maintDemand.put("id", "");
                maintDemand.put("INST_ID_", "");
                maintDemand.put("busunessNo", "");
                maintDemand.put("businessStatus", "");
                maintDemand.put("instructions", "");
                maintDemand.put("BconfirmingId", "");
                maintDemand.put("Bconfirming", "");
                maintDemand.put("manualCode", "");
                maintDemand.put("manualfileId", "");
                maintDemand.put("manualVersion", "");
                maintDemand.put("isCE", "");
                maintDemand.put("CREATE_BY_", "1");//创建人
                toBeCreate.put(key, maintDemand);
            }
        }
        return toBeCreate;
    }

    //..检查待创建的需求申请单是否已经存在，存在的话放到待更新异动里面，从原有待创建map里删除2023-04-15
    private List<Map<String, Object>> filteChongFuChuangJian(Map<String, JSONObject> maintDemandToBeCreateMap) {
        List<Map<String, Object>> maintDemandToBeUpdateList = new ArrayList<>();
        Set<String> keysToBeRemoveFromMaintDemandToBeCreateMap = new HashSet<>();
        for (Map.Entry<String, JSONObject> entry : maintDemandToBeCreateMap.entrySet()) {
            JSONObject maintDemandToBeCreate = entry.getValue();
            JSONObject params = new JSONObject();
            params.put("designModel", maintDemandToBeCreate.getString("designModel"));
            params.put("salesModel", maintDemandToBeCreate.getString("salesModel"));
            params.put("materialCode", maintDemandToBeCreate.getString("materialCode"));
            params.put("manualLanguage", maintDemandToBeCreate.getString("manualLanguage"));
            List<Map<String, Object>> maintenanceManualDemandListAlready = maintenanceManualDemandDao.dataListQueryExact(params);
            if (maintenanceManualDemandListAlready != null && maintenanceManualDemandListAlready.size() != 0)//已经存在业务意义上相同的操保手册申请单
            {
                for (Map<String, Object> maintenanceManualDemand : maintenanceManualDemandListAlready) {
                    maintDemandToBeUpdateList.add(maintenanceManualDemand);
                }
                keysToBeRemoveFromMaintDemandToBeCreateMap.add(entry.getKey());
            }
        }
        for (String keyToBeRemoveFromMaintDemandToBeCreateMap : keysToBeRemoveFromMaintDemandToBeCreateMap) {
            maintDemandToBeCreateMap.remove(keyToBeRemoveFromMaintDemandToBeCreateMap);
        }
        return maintDemandToBeUpdateList;
    }

}
