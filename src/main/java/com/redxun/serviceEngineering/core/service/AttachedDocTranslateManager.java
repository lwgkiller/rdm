package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.dao.*;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.manager.SysSeqIdManager;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.sys.org.manager.OsUserManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.atp.Switch;
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
import java.lang.invoke.SwitchPoint;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AttachedDocTranslateManager {
    private static Logger logger = LoggerFactory.getLogger(AttachedDocTranslateManager.class);
    @Autowired
    private AttachedDocTranslateDao attachedDocTranslateDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    SysSeqIdManager sysSeqIdManager;
    @Autowired
    OsUserManager osUserManager;
    @Autowired
    ExternalTranslationDao externalTranslationDao;
    @Autowired
    XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    RdmZhglUtil rdmZhglUtil;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        //..以下权限处理
        String currentUserId = ContextUtil.getCurrentUserId();
        String currentUserNo = ContextUtil.getCurrentUser().getUserNo();
        String currentUserMainDepId = ContextUtil.getCurrentUser().getMainGroupId();
        if (!currentUserNo.equalsIgnoreCase("admin")) {
            params.put("currentUserId", currentUserId);
            Map<String, Object> queryUserParam = new HashMap<>();
            queryUserParam.put("userId", currentUserId);
            queryUserParam.put("groupName", RdmConst.FGLD);
            List<Map<String, Object>> queryRoleResult = xcmgProjectOtherDao.queryUserRoles(queryUserParam);
            if (queryRoleResult != null && !queryRoleResult.isEmpty()) {//分管领导
                params.put("roleName", "fgld");
            } else if (rdmZhglUtil.judgeIsPointRoleUser(currentUserId, RdmConst.AllDATA_QUERY_NAME)) {
                params.put("roleName", "fgld");
            } else if (ContextUtil.getCurrentUser().getMainGroupName().equalsIgnoreCase(RdmConst.FWGCS_NAME)) {
                params.put("roleName", "fgld");
            } else {
                params.put("roleName", "ptyg");
                params.put("currentUserMainDepId", currentUserMainDepId);
            }
        }
        List<Map<String, Object>> businessList = attachedDocTranslateDao.dataListQuery(params);
        for (Map<String, Object> business : businessList) {
            if (business.get("CREATE_TIME_") != null) {
                business.put("CREATE_TIME_", DateUtil.formatDate((Date) business.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(businessList);
        int businessListCount = attachedDocTranslateDao.countDataListQuery(params);
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
            params.put("sortField", "id");
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
//        params.put("startIndex", 0);
//        params.put("pageSize", 20);
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
                "serviceEngineeringUploadPosition", "attachedDocTranslate").getValue();
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("mainId"), filePathBase);
        }
        //外发相关删除
        JSONObject params = new JSONObject();
        String fileBasePath = WebAppUtil.getProperty("externalTranslationFilePathBase");
        for (String businessId : businessIds) {
            JSONObject params2 = new JSONObject();
            params2.put("applyId", businessId);
            List<JSONObject> demandList = externalTranslationDao.queryDemandList(params2);
            for (JSONObject oneObject : demandList) {
                rdmZhglFileManager.deleteOneFileFromDisk(oneObject.getString("outFileId"),
                        oneObject.getString("outFileName"), oneObject.getString("applyId"), fileBasePath);
                rdmZhglFileManager.deleteOneFileFromDisk(oneObject.getString("returnFileId"),
                        oneObject.getString("returnFileName"), oneObject.getString("applyId"), fileBasePath);
            }
            //删除需求子表
            List<String> applyIds = new ArrayList<>();
            applyIds.add(businessId);
            params2.put("applyIds", applyIds);
            externalTranslationDao.deleteDemand(params2);
        }
        //删除文件夹
        for (String oneBusinessId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneBusinessId, filePathBase);
        }
        //删除主业务信息
        params.clear();
        params.put("businessIds", businessIds);
        attachedDocTranslateDao.deleteBusinessFile(params);
        attachedDocTranslateDao.deleteBusiness(params);
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }

    //
    public List<JSONObject> getBusinessFileList(List<String> businessIdList) {
        List<JSONObject> businessFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIdList);
        businessFileList = attachedDocTranslateDao.queryFileList(param);
        return businessFileList;
    }

    //..
    public JSONObject getDetail(String businessId) {
        JSONObject jsonObject = attachedDocTranslateDao.queryDetailById(businessId);
        if (jsonObject == null) {
            return new JSONObject();
        }
        return jsonObject;
    }

    //..transApplyId，apply，applyDep自动生成
    public void createBusiness(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("transApplyId", sysSeqIdManager.genSequenceNo("attachedDocTranslate", ContextUtil.getCurrentTenantId()));
        formData.put("applyId", ContextUtil.getCurrentUserId());
        formData.put("creatorName", ContextUtil.getCurrentUser().getFullname());
        formData.put("applyDepId", ContextUtil.getCurrentUser().getMainGroupId());
        formData.put("applyDep", ContextUtil.getCurrentUser().getMainGroupName());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        attachedDocTranslateDao.insertBusiness(formData);
    }

    //..
    public void updateBusiness(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        attachedDocTranslateDao.updateBusiness(formData);
    }

    //..
    public void deleteOneBusinessFile(String fileId, String fileName, String businessId) {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "attachedDocTranslate").getValue();
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, businessId, filePathBase);
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        attachedDocTranslateDao.deleteBusinessFile(param);
    }

    //..
    public List<JSONObject> getFileList(List<String> businessIdList) {
        List<JSONObject> fileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIdList);
        fileList = attachedDocTranslateDao.queryFileList(param);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Map<String, Object> oneObj : fileList) {
            if (oneObj.get("CREATE_TIME_") != null) {
                oneObj.put("CREATE_TIME_", simpleDateFormat.format(oneObj.get("CREATE_TIME_")));
            }
        }
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
                "serviceEngineeringUploadPosition", "attachedDocTranslate").getValue();
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
            attachedDocTranslateDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    //..
    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = dataListQuery(request, response);
        List<Map<String, Object>> listData = result.getData();
        List<JSONObject> attachList = new ArrayList<>();

        for (Map<String, Object> data : listData) {
            String status = data.get("status").toString();
            switch (status) {
                case "DRAFTED":
                    data.put("status", "草稿");
                    break;
                case "RUNNING":
                    data.put("status", "审批中");
                    break;
                case "SUCCESS_END":
                    data.put("status", "批准");
                    break;
                case "DISCARD_END":
                    data.put("status", "作废");
                    break;
            }
            JSONObject jsonObject = new JSONObject(data);
            attachList.add(jsonObject);
        }

        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "随机文件翻译申请清单";
        String excelName = nowDate + title;
        String[] fieldNames = {"翻译申请单号", "手册类型", "销售型号", "设计型号", "物料编码", "手册编码",
                "手册版本", "是否CE版本手册", "申请时间", "需求时间", "流程结束时间", "源手册语言", "翻译语言", "总字符数", "自翻译率", "外发翻译率",
                "申请人姓名", "申请部门", "发运需求申请单编号", "发运需求申请单对应单号", "翻译人员", "备注", "运行状态"};
        String[] fieldCodes = {"transApplyId", "manualType", "saleType", "designType", "materialCode", "manualCode",
                "manualVersion", "mannulCE", "applyTime", "needTime", "endTime", "sourceManualLan", "targetManualLan", "totalCodeNum", "selfTranslatePercent", "outTranslatePercent",
                "creatorName", "applyDep", "transportApplyId", "transportApplyNo", "translator", "remark", "status"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(attachList, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);

    }

    //..
    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    //..
    public JsonPageResult<?> dataListQuerySdltm(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<Map<String, Object>> businessList = attachedDocTranslateDao.dataListQuerySdltm(params);
        for (Map<String, Object> business : businessList) {
            if (business.get("CREATE_TIME_") != null) {
                business.put("CREATE_TIME_", DateUtil.formatDate((Date) business.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(businessList);
        int businessListCount = attachedDocTranslateDao.countDataListQuerySdltm(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }
}

