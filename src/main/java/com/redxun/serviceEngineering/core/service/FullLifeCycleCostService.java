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
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.FullLifeCycleCostDao;
import com.redxun.serviceEngineering.core.dao.FullLifeCycleCostFileDao;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualDemandDao;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualfileDao;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.manager.SysSeqIdManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.xcmgbudget.core.dao.BudgetMonthUserDao;
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
public class FullLifeCycleCostService {
    private static Logger logger = LoggerFactory.getLogger(FullLifeCycleCostService.class);
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
    BpmSolutionManager bpmSolutionManager;
    @Autowired
    private BudgetMonthUserDao budgetMonthUserDao;

    @Autowired
    private FullLifeCycleCostDao fullLifeCycleCostDao;
    @Autowired
    private FullLifeCycleCostFileDao fullLifeCycleCostFileDao;

    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        String businessId = RequestUtil.getString(request, "businessId");//提交时检有没有查重复业务的id参数侵入
        if (StringUtil.isNotEmpty(businessId)) {
            JSONObject detail = this.getDetail(businessId);
            params.put("demandListNo", detail.getString("demandListNo"));
            params.put("salesModel", detail.getString("salesModel"));
            params.put("designModel", detail.getString("designModel"));
            params.put("materialCode", detail.getString("materialCode"));
            params.put("salesArea", detail.getString("salesArea"));
            params.put("demandTime", detail.getString("demandTime"));
            params.put("xiaohanNoNoNo", "xiaohanNoNoNo");
        }
        List<Map<String, Object>> businessList = fullLifeCycleCostDao.dataListQuery(params);
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
        //数据过滤
        List<Map<String, Object>> finalAllProjectList = filterPlanListByDepRole(businessList);
        int businessListCount = finalAllProjectList.size();
        result.setData(finalAllProjectList);
        result.setTotal(businessListCount);
        return result;
    }
//
    //..
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
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
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
//    //..
    public JsonResult deleteBusiness(String[] ids, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        List<JSONObject> files = getBusinessFileList(businessIds);
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "qsmzq").getValue();
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("mainId"), filePathBase);
        }
        for (String oneBusinessId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneBusinessId, filePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        fullLifeCycleCostDao.deleteBusinessFile(param);
//        maintenanceManualDemandDao.deleteManualMatch(param);
        fullLifeCycleCostDao.deleteBusiness(param);
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }
//
    //..
    public List<JSONObject> getBusinessFileList(List<String> businessIdList) {
        List<JSONObject> businessFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIdList);
        businessFileList = fullLifeCycleCostDao.queryFileList(param);
        return businessFileList;
    }

    //..
    public JSONObject getDetail(String businessId) {
        JSONObject jsonObject = fullLifeCycleCostDao.queryDetailById(businessId);
        if (jsonObject == null) {
            return new JSONObject();
        }
        return jsonObject;
    }

    //..
    public void createBusiness(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("busunessNo", sysSeqIdManager.genSequenceNo("QSMZQ", ContextUtil.getCurrentTenantId()));
        formData.put("applyUserId", ContextUtil.getCurrentUserId());
        formData.put("applyUser", ContextUtil.getCurrentUser().getFullname());
        formData.put("applyDepId", ContextUtil.getCurrentUser().getMainGroupId());
        formData.put("applyDep", ContextUtil.getCurrentUser().getMainGroupName());
//        formData.put("businessStatus", "A-editing");
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        //基本信息
        fullLifeCycleCostDao.insertBusiness(formData);
    }

    //..
    public void updateBusiness(JSONObject formData) {
        //获取匹配归档信息
        JSONObject params = new JSONObject();
        params.put("businessId", formData.getString("id"));
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        //基本信息
        fullLifeCycleCostDao.updateBusiness(formData);
    }

    //..
    public void deleteOneBusinessFile(String fileId, String fileName, String businessId) {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "qsmzq").getValue();
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, businessId, filePathBase);
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        fullLifeCycleCostDao.deleteBusinessFile(param);
    }

    //..
    public List<JSONObject> getFileList(List<String> businessIdList) {
        List<JSONObject> fileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIdList);
        fileList = fullLifeCycleCostDao.queryFileList(param);
        return fileList;
    }
//
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
                "serviceEngineeringUploadPosition", "qsmzq").getValue();
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
            fullLifeCycleCostDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }
//
    //..
    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    /**
     * 列表分权限过滤。
     *
     * @param list
     * @return
     */
    public List<Map<String, Object>> filterPlanListByDepRole(List<Map<String, Object>> list) {
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
        // 是否是全生命周期管理人员
        boolean isQsManager = false;
        JSONObject userRole = queryUserRoles(currentUserId);
        if (userRole.get("qsManager") != null && "yes".equalsIgnoreCase(userRole.getString("qsManager"))) {
            isQsManager = true;
        }
        for (Map<String, Object> oneProject : list) {
            // 创建人
            if (oneProject.get("CREATE_BY_") != null && oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                result.add(oneProject);
                continue;
            }
            // 如果是草稿状态，后面角色跳过，都不可见
            if (oneProject.get("status") == null || "DRAFTED".equalsIgnoreCase(oneProject.get("status").toString())) {
                continue;
            }
//            // 全生命周期管理人员
//            if (isQsManager) {
//                result.add(oneProject);
//                continue;
//            }
//            // 任务处理人
//            if (oneProject.get("currentProcessUserId") != null && oneProject.get("currentProcessUserId").toString().contains(currentUserId)) {
//                result.add(oneProject);
//                continue;
//            }
            //权限调整，流程信息展示给所有人员（除草稿）
            if (oneProject.size()>0){
                result.add(oneProject);
                continue;
            }
        }
        return result;
    }

    /**
     * 查询用户的角色（普通账户(返回部门id）、部门负责人（返回部门id）、分管领导(返回分管部门ids)、业务角色）
     *
     * @param userId
     * @return
     */
    public JSONObject queryUserRoles(String userId) {
        JSONObject result = new JSONObject();
        // 先查询存在的业务角色
        List<JSONObject> roleKeys = budgetMonthUserDao.queryRelInstRoles(userId);
        if (roleKeys != null && !roleKeys.isEmpty()) {
            // 全生命周期管理人员
            Boolean qsManager = false;

            for (JSONObject temp : roleKeys) {
                String roleKey = temp.getString("roleKey");
                if ("QSMZQ".equalsIgnoreCase(roleKey)) {
                    qsManager = true;
                }

            }
            if (qsManager) {
                result.put("qsManager", "yes");
            }
        }
        return result;
    }
}
