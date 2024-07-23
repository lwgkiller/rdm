package com.redxun.serviceEngineering.core.service;

import java.io.File;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.util.DateFormatUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
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

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.SparepartsConsultationDao;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.manager.SysSeqIdManager;
import com.redxun.sys.org.manager.OsUserManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;

@Service
public class SparepartsConsultationService {
    private static Logger logger = LoggerFactory.getLogger(SparepartsConsultationService.class);
    @Autowired
    private SparepartsConsultationDao sparepartsConsultationDao;
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

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        CommonFuns.getSearchParam(params, request, doPage);
        List<Map<String, Object>> businessList = sparepartsConsultationDao.dataListQuery(params);
        for (Map<String, Object> business : businessList) {
            if (business.get("CREATE_TIME_") != null) {
                business.put("CREATE_TIME_", DateUtil.formatDate((Date) business.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(businessList);
        int businessListCount = sparepartsConsultationDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public JsonResult deleteBusiness(String[] ids, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        List<JSONObject> files = getBusinessFileList(businessIds);
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "sparepartsConsultation").getValue();
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("mainId"), filePathBase);
        }
        for (String oneBusinessId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneBusinessId, filePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        sparepartsConsultationDao.deleteBusinessFile(param);
        sparepartsConsultationDao.deleteBusiness(param);
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }

    //..
    public List<JSONObject> getBusinessFileList(List<String> businessIdList) {
        List<JSONObject> businessFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIdList);
        businessFileList = sparepartsConsultationDao.queryFileList(param);
        return businessFileList;
    }

    //..
    public JSONObject getDetail(String businessId) {
        JSONObject jsonObject = sparepartsConsultationDao.queryDetailById(businessId);
        if (jsonObject == null) {
            jsonObject = new JSONObject();
            jsonObject.put("applyUserId", ContextUtil.getCurrentUser().getUserId());
            jsonObject.put("applyUser", ContextUtil.getCurrentUser().getFullname());
            jsonObject.put("applyDepId", ContextUtil.getCurrentUser().getMainGroupId());
            jsonObject.put("applyDep", ContextUtil.getCurrentUser().getMainGroupName());
        }
        return jsonObject;
    }

    //..applyUser,applyDep自动生成
    public void createBusiness(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("applyUserId", ContextUtil.getCurrentUserId());
        formData.put("applyUser", ContextUtil.getCurrentUser().getFullname());
        formData.put("applyDepId", ContextUtil.getCurrentUser().getMainGroupId());
        formData.put("applyDep", ContextUtil.getCurrentUser().getMainGroupName());
        if (formData.containsKey("coordinatorId") &&
                StringUtil.isEmpty(formData.getString("coordinatorPhoneNo"))) {
            IUser coordinator = osUserManager.get(formData.getString("coordinatorId"));
            if (coordinator != null) {
                formData.put("coordinatorPhoneNo", StringUtil.isEmpty(coordinator.getMobile()) ? "无号码" : coordinator.getMobile());
            }
        }
        //@lwgkiller:此处是因为草稿状态无节点，提交后首节点会跳过，因此默认将首节点（编制中）的编号进行初始化写入
        formData.put("businessStatus", "A-editing");
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        sparepartsConsultationDao.insertBusiness(formData);
    }

    //..
    public void updateBusiness(JSONObject formData) {
        if (formData.containsKey("coordinatorId") &&
                StringUtil.isEmpty(formData.getString("coordinatorPhoneNo"))) {
            IUser coordinator = osUserManager.get(formData.getString("coordinatorId"));
            if (coordinator != null) {
                formData.put("coordinatorPhoneNo", StringUtil.isEmpty(coordinator.getMobile()) ? "无号码" : coordinator.getMobile());
            }
        }
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        sparepartsConsultationDao.updateBusiness(formData);
    }

    //..
    public void deleteOneBusinessFile(String fileId, String fileName, String businessId) {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "sparepartsConsultation").getValue();
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, businessId, filePathBase);
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        sparepartsConsultationDao.deleteBusinessFile(param);
    }

    //..
    public List<JSONObject> getFileList(List<String> businessIdList) {
        List<JSONObject> fileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIdList);
        fileList = sparepartsConsultationDao.queryFileList(param);
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
                "serviceEngineeringUploadPosition", "sparepartsConsultation").getValue();
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
            sparepartsConsultationDao.addFileInfos(fileInfo);
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
        JsonPageResult result = dataListQuery(request, response, false);
        List<Map<String, Object>> listData = result.getData();
        for (Map<String, Object> map : listData) {
            String businessStatus = map.get("businessStatus") != null ? map.get("businessStatus").toString() : "";
            switch (businessStatus) {
                case "A-editing":
                    map.put("businessStatus", "提问者填写");
                    break;
                case "B-confirming":
                    map.put("businessStatus", "技术支持人员审核");
                    break;
                case "C-confirmingProduct":
                    map.put("businessStatus", "产品所审核");
                    break;
                case "D-confirming":
                    map.put("businessStatus", "技术支持审核GSS");
                    break;
                case "E-confirmingGss":
                    map.put("businessStatus", "GSS补充");
                    break;
                case "Z-close":
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
        String title = "备件咨询";
        String excelName = nowDate + title;
        String[] fieldNames = {"申请人", "联系方式", "提出部门", "整车编号", "销售型号", "设计型号", "问题名称",
                "问题描述", "问题类型", "是否协同", "协同人", "协同人电话", "是否补充GSS", "机型负责人",
                "技术支持回复", "产品所回复", "GSS机型负责人回复", "业务状态", "流程状态", "当前处理人"};
        String[] fieldCodes = {"applyUser", "contactInformation", "applyDep", "pin", "salesModel", "designModel", "problemSummary",
                "problemDescription", "problemType", "isCoordinate", "coordinator", "coordinatorPhoneNo", "isGss", "gsser",
                "serviceEngineeringReply", "productInstituteReply", "gsserReply", "businessStatus", "status", "currentProcessUser"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
