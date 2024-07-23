package com.redxun.xcmgProjectManager.core.manager;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.dao.BpmInstDao;
import com.redxun.bpm.core.entity.BpmInst;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectDeliveryApprovalDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectFileDao;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.xcmgProjectManager.report.dao.XcmgProjectReportDao;

import groovy.util.logging.Slf4j;

/**
 * @author zz
 */
@Service
@Slf4j
public class XcmgProjectFileManager {
    private static Logger logger = LoggerFactory.getLogger(XcmgProjectFileManager.class);
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private XcmgProjectFileDao xcmgProjectFileDao;
    @Autowired
    private XcmgProjectReportDao xcmgProjectReportDao;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private XcmgProjectAbolishManager xcmgProjectAbolishManager;
    @Autowired
    private XcmgProjectDeliveryApprovalDao xcmgProjectDeliveryApprovalDao;
    @Autowired
    private BpmInstDao bpmInstDao;
    @Resource
    private XcmgProjectFileUploadManager fileUploadManager;

    public void add(Map<String, Object> params) {
        String preFix = "SP-";
        String id = preFix + XcmgProjectUtil.getNowLocalDateStr("yyyyMMddHHmmssSSS") + (int)(Math.random() * 100);
        params.put("id", id);
        params.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        params.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        xcmgProjectFileDao.add(params);
    }

    public void update(Map<String, Object> params) {
        params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        params.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        xcmgProjectFileDao.update(params);
    }

    /**
     * 删除表单及关联的所有信息
     */
    public JsonResult delete(String[] applyIdArr, String[] instIdArr) {
        if (applyIdArr.length != instIdArr.length) {
            return new JsonResult(false, "表单和流程个数不相同！");
        }
        for (int i = 0; i < applyIdArr.length; i++) {
            String applyId = applyIdArr[i];
            xcmgProjectFileDao.delete(applyId);
            // 删除实例
            bpmInstManager.deleteCascade(instIdArr[i], "");
        }
        return new JsonResult(true, "成功删除!");
    }

    /**
     * 查询变更列表
     */
    public JsonPageResult<?> queryList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>();
            // 传入条件(不包括分页)
            params = XcmgProjectUtil.getSearchParam(params, request);
            // 如果是挖掘机械研究院项目管理人员，则只查询挖掘机械研究院下的部门
            boolean isJSZXProjManagUsers =
                commonInfoManager.judgeUserIsPointRole(RdmConst.JSZXXMGLRY, ContextUtil.getCurrentUserId());
            if (isJSZXProjManagUsers) {
                Map<String, String> deptId2Name = commonInfoManager.queryDeptUnderJSZX();
                //2024-3-29新增技术中心项目管理员也可以看到工艺的项目
                boolean isGYProjManagUsers =
                        commonInfoManager.judgeUserIsPointRole(RdmConst.GYJSBXMGLRY, ContextUtil.getCurrentUserId());
                if (isGYProjManagUsers) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("deptName", RdmConst.GYJSB_NAME);
                    List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                    if (deptList != null && !deptList.isEmpty()) {
                        deptId2Name.put(deptList.get(0).getString("GROUP_ID_"), deptList.get(0).getString("NAME_"));
                    }
                }
                params.put("deptIds", deptId2Name.keySet());
                //2024-4-29新增技术中心项目管理员也可以看到应用技术部的项目
                boolean isYYProjManagUsers =
                        commonInfoManager.judgeUserIsPointRole(RdmConst.YYJSBXMGLRY, ContextUtil.getCurrentUserId());
                if (isYYProjManagUsers) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("deptName", RdmConst.YYJSB_NAME);
                    List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                    if (deptList != null && !deptList.isEmpty()) {
                        deptId2Name.put(deptList.get(0).getString("GROUP_ID_"), deptList.get(0).getString("NAME_"));
                    }
                }
                params.put("deptIds", deptId2Name.keySet());
            } else {
                boolean isGYFGLD =
                    commonInfoManager.judgeUserIsPointRole(RdmConst.GY_FGLD, ContextUtil.getCurrentUserId());
                if (isGYFGLD) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("deptName", RdmConst.GYJSB_NAME);
                    List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                    if (deptList != null && !deptList.isEmpty()) {
                        params.put("deptIds", Arrays.asList(deptList.get(0).getString("GROUP_ID_")));
                    }
                }
            }
            List<Map<String, Object>> applyList = xcmgProjectFileDao.queryList(params);
            // 增加不同角色和部门的人看到的数据不一样的过滤
            List<Map<String, Object>> finalAllApplyList = null;
            finalAllApplyList = xcmgProjectAbolishManager.filterApplyListByDepRole(applyList);
            // 根据分页进行subList截取
            int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
            int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
            int startSubListIndex = pageIndex * pageSize;
            int endSubListIndex = startSubListIndex + pageSize;
            List<Map<String, Object>> finalSubApplyList = new ArrayList<Map<String, Object>>();
            if (startSubListIndex < finalAllApplyList.size()) {
                finalSubApplyList = finalAllApplyList.subList(startSubListIndex,
                    endSubListIndex < finalAllApplyList.size() ? endSubListIndex : finalAllApplyList.size());
            }

            if (finalSubApplyList != null && !finalSubApplyList.isEmpty()) {
                for (Map<String, Object> oneApply : finalSubApplyList) {
                    if (oneApply.get("CREATE_TIME_") != null) {
                        oneApply.put("CREATE_TIME_",
                            DateUtil.formatDate((Date)oneApply.get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneApply.get("applyTime") != null) {
                        oneApply.put("applyTime",
                            DateUtil.formatDate((Date)oneApply.get("applyTime"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneApply.get("UPDATE_TIME_") != null) {
                        oneApply.put("UPDATE_TIME_",
                            DateUtil.formatDate((Date)oneApply.get("UPDATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                }
            }
            // 返回结果
            result.setData(finalSubApplyList);
            result.setTotal(finalAllApplyList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询变更列表", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }

    public List<Map<String, Object>> getFileList(Map<String, Object> params) {
        List list = xcmgProjectFileDao.getFileList(params);
        return list;
    }

    public JsonResult delFile(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "成功解绑!");
        try {
            String id = RequestUtil.getString(request, "id");
            String fileId = RequestUtil.getString(request, "fileId");
            if (StringUtils.isBlank(id) || StringUtils.isBlank(fileId)) {
                return result;
            }
            Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            Map<String, Object> resultMap = xcmgProjectFileDao.getObject(params);
            if (resultMap == null) {
                return result;
            }
            String fileIds = XcmgProjectUtil.nullToString(resultMap.get("fileIds"));
            if (StringUtils.isBlank(fileIds)) {
                return result;
            }
            String[] fileArray = fileIds.split(",");
            String newFileIds = "";
            for (String file : fileArray) {
                if (!fileId.equals(file)) {
                    newFileIds += file + ",";
                }
            }
            if (newFileIds.length() > 1 && newFileIds.indexOf(",") > -1) {
                newFileIds = newFileIds.substring(0, newFileIds.length() - 1);
            }
            resultMap.put("fileIds", newFileIds);
            xcmgProjectFileDao.update(resultMap);
        } catch (Exception e) {
            logger.error("Exception in delFile", e);
            return new JsonResult(false, "解绑失败!");
        }
        return result;
    }

    public JsonResult addFile(HttpServletRequest request, HttpServletResponse response) {
        try {
            String id = RequestUtil.getString(request, "id");
            String fileIds = RequestUtil.getString(request, "fileIds");
            Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            Map<String, Object> resultMap = xcmgProjectFileDao.getObject(params);
            String orgFileIds = XcmgProjectUtil.nullToString(resultMap.get("fileIds"));
            if (!"".equals(orgFileIds) && !orgFileIds.endsWith(",")) {
                orgFileIds += ",";
            }
            String newFileIds = orgFileIds + fileIds;
            resultMap.put("fileIds", newFileIds);
            xcmgProjectFileDao.update(resultMap);
        } catch (Exception e) {
            logger.error("Exception in delFile", e);
            return new JsonResult(false, "添加失败!");
        }
        return new JsonResult(true, "添加成功!");
    }

    public List<Map<String, Object>> getStageFileList(HttpServletRequest request, HttpServletResponse response) {
        String projectId = RequestUtil.getString(request, "projectId");
        String stageId = RequestUtil.getString(request, "stageId");
        String recordId = RequestUtil.getString(request, "recordId");
        BpmInst bpmInst = bpmInstDao.getByBusKey(recordId);
        String solId = bpmInst.getSolId();

        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("projectId", projectId);
        params.put("stageId", stageId);
        params.put("isFolder", "0");
        List<Map<String, Object>> queryResult = xcmgProjectDeliveryApprovalDao.queryDeliveryApproval(params);
        List<Map<String, Object>> resultList = new ArrayList();
        if (queryResult == null || queryResult.isEmpty()) {
            return resultList;
        }
        for (Map<String, Object> oneResult : queryResult) {
            if (oneResult.get("isPDMFile") != null && "1".equalsIgnoreCase(oneResult.get("isPDMFile").toString())) {
                continue;
            }
            String approvalSolutionId = XcmgProjectUtil.nullToString(oneResult.get("approvalSolutionId"));
            // 审批类型与当前审批单的类型一致，且目前未提交的
            if (StringUtils.isNotBlank(approvalSolutionId) && approvalSolutionId.equalsIgnoreCase(solId)) {
                if (oneResult.get("instStatus") == null) {
                    resultList.add(oneResult);
                }
            }
        }
        return resultList;
    }

    public List<Map<String, Object>> getApprovalFileList(HttpServletRequest request, HttpServletResponse response) {
        String recordId = RequestUtil.getString(request, "recordId");
        if (StringUtil.isEmpty(recordId)) {
            return new ArrayList<>();
        }
        // 获取已经有的交付物
        Map<String, Object> params = new HashMap<>(16);
        params.put("id", recordId);
        Map<String, Object> resultMap = xcmgProjectFileDao.getObject(params);
        String fileIds = "";
        if (resultMap != null) {
            fileIds = XcmgProjectUtil.nullToString(resultMap.get("fileIds"));
        }
        String[] idArr = fileIds.split(",", -1);
        Map<String, Object> fileParams = new HashMap<>(16);
        fileParams.put("fileIds", Arrays.asList(idArr));
        List list = xcmgProjectFileDao.getFileList(fileParams);
        return list;
    }

    public JsonResult delOrgFiles(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 要删除的文件ids
            String ids = RequestUtil.getString(request, "ids");
            String applyId = RequestUtil.getString(request, "applyId");
            // 获取申请单原始的所有的文件
            Map<String, Object> params = new HashMap<>();
            params.put("id", applyId);
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            Map<String, Object> resultMap = xcmgProjectFileDao.getObject(params);
            String fileIds = XcmgProjectUtil.nullToString(resultMap.get("fileIds"));
            if (fileIds.endsWith(",")) {
                fileIds = fileIds.substring(0, fileIds.length() - 1);
            }
            List<String> allFileIds = Arrays.asList(fileIds.split(",", -1));
            String[] deleteIdArray = ids.split(",", -1);
            List<String> deleteIdList = Arrays.asList(deleteIdArray);

            // 遍历所有的文件，在删除列表中则删除文件，否则拼接到新的fileIds中去
            StringBuilder newFileIdsSb = new StringBuilder();
            for (String oneFileId : allFileIds) {
                if (deleteIdList.contains(oneFileId)) {
                    JSONObject fileJson = xcmgProjectFileDao.getFileObj(oneFileId);
                    if (fileJson != null) {
                        String projectId = fileJson.getString("projectId");
                        String relativeFilePath = fileJson.getString("relativeFilePath");
                        String fileName = fileJson.getString("fileName");
                        fileUploadManager.deleteProjectOneFile(projectId, oneFileId, relativeFilePath, fileName, false,
                            ConstantUtil.DOWNLOAD);
                        fileUploadManager.deleteProjectOneFile(projectId, oneFileId, relativeFilePath, fileName, false,
                            ConstantUtil.PREVIEW);
                    }
                } else {
                    newFileIdsSb.append(oneFileId).append(",");
                }
            }

            // 更新申请单
            String newFileIds = "";
            if (newFileIdsSb.length()>0) {
                newFileIds = newFileIdsSb.substring(0, newFileIdsSb.length() - 1);
            }
            resultMap.put("fileIds", newFileIds);
            xcmgProjectFileDao.update(resultMap);
        } catch (Exception e) {
            logger.error("Exception in delFile", e);
            return new JsonResult(false, "删除失败!");
        }
        return new JsonResult(true, "成功删除!");
    }

}
