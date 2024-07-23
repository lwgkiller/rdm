package com.redxun.wwrz.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.util.CommonExcelUtils;
import com.redxun.wwrz.core.dao.WwrzDocListDao;
import com.redxun.wwrz.core.dao.WwrzFilesDao;
import com.redxun.wwrz.core.dao.WwrzTestApplyDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import groovy.util.logging.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zz
 */
@Service
@Slf4j
public class WwrzTestApplyService {
    private static Logger logger = LoggerFactory.getLogger(WwrzTestApplyService.class);
    @Resource
    private BpmInstManager bpmInstManager;
    @Resource
    private WwrzTestApplyDao wwrzTestApplyDao;
    @Resource
    private XcmgProjectManager xcmgProjectManager;
    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Resource
    private WwrzDocListDao wwrzDocListDao;
    @Resource
    private WwrzFilesService wwrzFilesService;
    @Resource
    private WwrzFilesDao wwrzFilesDao;
    @Resource
    private CommonInfoManager commonInfoManager;
    @Resource
    private CommonInfoDao commonInfoDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    public void add(Map<String, Object> params) {
        String preFix = "SP-";
        String id = preFix + XcmgProjectUtil.getNowLocalDateStr("yyyyMMddHHmmssSSS") + (int)(Math.random() * 100);
        params.put("id", id);
        params.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        params.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        params.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        //根据产品主管，查到所属部门
        String productLeader = CommonFuns.nullToString(params.get("productLeader"));
        if(StringUtil.isNotEmpty(productLeader)){
            String deptId = commonInfoDao.getDeptIdByUserId(productLeader);
            params.put("deptId",deptId);
        }
        wwrzTestApplyDao.add(params);
//        if (StringUtils.isNotBlank(CommonFuns.nullToString(params.get("SUB_problem")))) {
//            JSONArray productDataJson = JSONObject.parseArray(params.get("SUB_problem").toString());
//            for (int i = 0; i < productDataJson.size(); i++) {
//                JSONObject oneObject = productDataJson.getJSONObject(i);
//                oneObject.put("id", IdUtil.getId());
//                oneObject.put("mainId", id);
//                oneObject.put("problem", oneObject.getString("problem"));
//                oneObject.put("charger", oneObject.getString("charger"));
//                oneObject.put("plan", oneObject.getString("plan"));
//                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
//                oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
//                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
//                oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
//                wwrzTestApplyDao.addProblem(oneObject);
//            }
//        }
//        if (StringUtils.isNotBlank(CommonFuns.nullToString(params.get("SUB_document")))) {
//            JSONArray documentDataJson = JSONObject.parseArray(params.get("SUB_document").toString());
//            for (int i = 0; i < documentDataJson.size(); i++) {
//                JSONObject oneObject = documentDataJson.getJSONObject(i);
//                oneObject.put("id", IdUtil.getId());
//                oneObject.put("mainId", id);
//                oneObject.put("docType", oneObject.getString("docType"));
//                oneObject.put("docName", oneObject.getString("docName"));
//                oneObject.put("charger", oneObject.getString("charger"));
//                oneObject.put("used", oneObject.getString("used"));
//                oneObject.put("passed", oneObject.getString("passed"));
//                oneObject.put("problem", oneObject.getString("problem"));
//                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
//                oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
//                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
//                oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
//                wwrzDocListDao.addDoc(oneObject);
//            }
//        }
    }

    public void update(Map<String, Object> params) {
        params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        params.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        //根据产品主管，查到所属部门
        String productLeader = CommonFuns.nullToString(params.get("productLeader"));
        if(StringUtil.isNotEmpty(productLeader)){
            String deptId = commonInfoDao.getDeptIdByUserId(productLeader);
            params.put("deptId",deptId);
        }
        wwrzTestApplyDao.update(params);
//        if (StringUtils.isNotBlank(CommonFuns.nullToString(params.get("SUB_problem")))) {
//            JSONArray productDataJson = JSONObject.parseArray(params.get("SUB_problem").toString());
//            for (int i = 0; i < productDataJson.size(); i++) {
//                JSONObject oneObject = productDataJson.getJSONObject(i);
//                String state = oneObject.getString("_state");
//                String productId = oneObject.getString("id");
//                if ("added".equals(state) || StringUtils.isBlank(productId)) {
//                    // 新增
//                    oneObject.put("id", IdUtil.getId());
//                    oneObject.put("mainId", params.get("id"));
//                    oneObject.put("problem", oneObject.getString("problem"));
//                    oneObject.put("charger", oneObject.getString("charger"));
//                    oneObject.put("plan", oneObject.getString("plan"));
//                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
//                    oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
//                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
//                    oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
//                    wwrzTestApplyDao.addProblem(oneObject);
//                } else if ("modified".equals(state)) {
//                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
//                    oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
//                    wwrzTestApplyDao.updateProblem(oneObject);
//                } else if ("removed".equals(state)) {
//                    // 删除
//                    wwrzTestApplyDao.delProblemById(oneObject.getString("id"));
//                    delProblemFiles(oneObject.getString("id"));
//                }
//            }
//        }
//        if (StringUtils.isNotBlank(CommonFuns.nullToString(params.get("SUB_document")))) {
//            JSONArray documentDataJson = JSONObject.parseArray(params.get("SUB_document").toString());
//            for (int i = 0; i < documentDataJson.size(); i++) {
//                JSONObject oneObject = documentDataJson.getJSONObject(i);
//                String state = oneObject.getString("_state");
//                String productId = oneObject.getString("id");
//                if ("added".equals(state) || StringUtils.isBlank(productId)) {
//                    // 新增
//                    oneObject.put("id", IdUtil.getId());
//                    oneObject.put("mainId", params.get("id"));
//                    oneObject.put("docType", oneObject.getString("docType"));
//                    oneObject.put("docName", oneObject.getString("docName"));
//                    oneObject.put("charger", oneObject.getString("charger"));
//                    oneObject.put("used", oneObject.getString("used"));
//                    oneObject.put("passed", oneObject.getString("passed"));
//                    oneObject.put("problem", oneObject.getString("problem"));
//                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
//                    oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
//                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
//                    oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
//                    wwrzDocListDao.addDoc(oneObject);
//                } else if ("modified".equals(state)) {
//                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
//                    oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
//                    wwrzDocListDao.updateDoc(oneObject);
//                } else if ("removed".equals(state)) {
//                    // 删除
//                    wwrzDocListDao.delDocById(oneObject.getString("id"));
//                    delDocumentFiles(oneObject.getString("id"));
//                }            }
//        }
    }
    public void saveOrUpdateProblem(String changeGridDataStr) {
        JSONObject resultJson = new JSONObject();
        try {
            JSONArray changeGridDataJson = JSONObject.parseArray(changeGridDataStr);
            for (int i = 0; i < changeGridDataJson.size(); i++) {
                JSONObject oneObject = changeGridDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String problemId = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(problemId)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("mainId", oneObject.getString("mainId"));
                    oneObject.put("problem", oneObject.getString("problem"));
                    oneObject.put("charger", oneObject.getString("charger"));
                    oneObject.put("plan", oneObject.getString("plan"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    wwrzTestApplyDao.addProblem(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    wwrzTestApplyDao.updateProblem(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    wwrzTestApplyDao.delProblemById(oneObject.getString("id"));
                    delProblemFiles(oneObject.getString("id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("保存信息异常", e);
            return;
        }
    }
    public void saveOrUpdateDocument(String changeGridDataStr) {
        JSONObject resultJson = new JSONObject();
        try {
            JSONArray changeGridDataJson = JSONObject.parseArray(changeGridDataStr);
            for (int i = 0; i < changeGridDataJson.size(); i++) {
                JSONObject oneObject = changeGridDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String documentId = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(documentId)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("mainId", oneObject.getString("mainId"));
                    oneObject.put("docType", oneObject.getString("docType"));
                    oneObject.put("docName", oneObject.getString("docName"));
                    oneObject.put("charger", oneObject.getString("charger"));
                    oneObject.put("used", oneObject.getString("used"));
                    oneObject.put("passed", oneObject.getString("passed"));
                    oneObject.put("problem", oneObject.getString("problem"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    wwrzDocListDao.addDoc(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    wwrzDocListDao.updateDoc(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    wwrzDocListDao.delDocById(oneObject.getString("id"));
                    delDocumentFiles(oneObject.getString("id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("保存信息异常", e);
            return;
        }
    }

    public void delDocumentFiles(String documentId) {
        try {
            List<JSONObject> list = wwrzFilesDao.getFileListByMainId(documentId);
            for (JSONObject file : list) {
                wwrzFilesService.deleteOneFile(file.getString("id"), file.getString("fileName"),
                    file.getString("mainId"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delProblemFiles(String problemId) {
        try {
            List<JSONObject> list = wwrzFilesDao.getFileListByMainId(problemId);
            for (JSONObject file : list) {
                wwrzFilesService.deleteOneFile(file.getString("id"), file.getString("fileName"),
                    file.getString("mainId"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除表单及关联的所有信息
     */
    public JsonResult delete(String[] applyIdArr, String[] instIdArr) {
        if (applyIdArr.length != instIdArr.length) {
            return new JsonResult(false, "表单和流程个数不相同！");
        }
        Map<String, Object> param = new HashMap<>();
        for (int i = 0; i < applyIdArr.length; i++) {
            String applyId = applyIdArr[i];
            param.put("applyId", applyId);
            wwrzTestApplyDao.delete(applyId);
            //删除子表信息及附件信息
            dealDetailInfo(applyId);
            // 删除实例
            bpmInstManager.deleteCascade(instIdArr[i], "");
        }
        return new JsonResult(true, "成功删除!");
    }

    public void dealDetailInfo(String applyId) {
        try {
            List<JSONObject> problemList = wwrzTestApplyDao.getProblemList(applyId);
            for (JSONObject problem : problemList) {
                delProblemFiles(problem.getString("id"));
                wwrzTestApplyDao.delProblemById(problem.getString("id"));
            }
            List<JSONObject> documentList = wwrzDocListDao.getDocumentList(applyId);
            for (JSONObject document : documentList) {
                delDocumentFiles(document.getString("id"));
                wwrzDocListDao.delDocById(document.getString("id"));
            }
            List<JSONObject> reportList = wwrzFilesDao.getFileListByMainId(applyId);
            for (JSONObject file : reportList) {
                wwrzFilesService.deleteOneFile(file.getString("id"), file.getString("fileName"),
                        file.getString("mainId"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询变更列表
     */
    public JsonPageResult<?> queryList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>();
            // 传入条件(不包括分页)
            params = XcmgProjectUtil.getSearchParam(params, request);
            List<JSONObject> applyList = wwrzTestApplyDao.dataListQuery(params);
            // 增加不同角色和部门的人看到的数据不一样的过滤
            rdmZhglUtil.setTaskInfo2Data(applyList, ContextUtil.getCurrentUserId());
            applyList = filterApplyListByDepRole(applyList);
            // 根据分页进行subList截取
            List<JSONObject> finalSubList = new ArrayList<JSONObject>(16);
            // 根据分页进行subList截取
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
            CommonFuns.convertDateJSON(finalSubList);
            result.setData(finalSubList);
            result.setTotal(applyList.size());
        } catch (Exception e) {
            logger.error("Exception in queryList", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }

    /**
     * 根据登录人部门、角色对列表进行过滤
     */
    public List<JSONObject> filterApplyListByDepRole(List<JSONObject>  applyList) {
        List<JSONObject> result = new ArrayList<>();
        if (applyList == null || applyList.isEmpty()) {
            return result;
        }
        // 刷新任务的当前执行人
        boolean showAll = false;
        // 管理员查看所有的包括草稿数据
        if (ConstantUtil.ADMIN.equals(ContextUtil.getCurrentUser().getUserNo())) {
            showAll = true;
        }
        // 分管领导的查看权限等同于项目管理人员
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUser().getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        for (Map<String, Object> oneRole : currentUserRoles) {
            if (oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-LEADER")
                    || oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-BELONG")) {
                if (RdmConst.AllDATA_QUERY_NAME.equalsIgnoreCase(oneRole.get("NAME_").toString())) {
                    showAll = true;
                    break;
                }
                if (RdmConst.JSZX_ZR.equalsIgnoreCase(oneRole.get("NAME_").toString())) {
                    showAll = true;
                    break;
                }
                if ("WWRZ-DATA".equals(oneRole.get("KEY_").toString())) {
                    showAll = true;
                    break;
                }
            }
        }
        // 确定当前登录人是否是部门负责人
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        if (!ConstantUtil.SUCCESS.equals(currentUserDepInfo.getString("result"))) {
            return result;
        }
        boolean isDepRespMan = currentUserDepInfo.getBoolean("isDepRespMan");
        String currentUserMainDepId = currentUserDepInfo.getString("currentUserMainDepId");
        String currentUserId = ContextUtil.getCurrentUserId();
        String currentUserMainDepName = currentUserDepInfo.getString("currentUserMainDepName");
        //标准技术所 查看所有数据
        if (RdmConst.BZJSS_NAME.equalsIgnoreCase(currentUserMainDepName)) {
            showAll = true;
        }
        // 过滤
        for (JSONObject oneApply : applyList) {
            // 自己是当前流程处理人
            if (StringUtils.isNotBlank(oneApply.getString("myTaskId"))) {
                oneApply.put("processTask", true);
                result.add(oneApply);
            } else if (showAll) {
                // 分管领导和项目管理人员查看所有非草稿的数据或者草稿但是创建人CREATE_BY_是自己的
                if (oneApply.get("status") != null && !"DRAFTED".equals(oneApply.get("status").toString())) {
                    result.add(oneApply);
                } else {
                    if (oneApply.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneApply);
                    }
                }
            } else if (isDepRespMan ) {
                // 部门负责人对于非草稿的且申请人部门是当前部门，或者草稿但是创建人CREATE_BY_是自己的
                if (oneApply.get("status") != null && !"DRAFTED".equals(oneApply.get("status").toString())) {
                    if (oneApply.get("deptId").toString().equals(currentUserMainDepId)) {
                        result.add(oneApply);
                    }
                } else {
                    if (oneApply.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneApply);
                    }
                }

            } else {
                // 其他人对于创建人CREATE_BY_是自己的
                if (oneApply.get("CREATE_BY_").toString().equals(currentUserId)||oneApply.get("productLeader").toString().equals(currentUserId)) {
                    result.add(oneApply);
                }
            }
        }
        return result;
    }
    public JsonPageResult<?> getApplyList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            list = wwrzTestApplyDao.getApplyList(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            totalList = wwrzTestApplyDao.getApplyList(params);
            CommonFuns.convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            result.setSuccess(false);
            result.setMessage("查询异常");
        }
        return result;
    }
    public JsonPageResult<?> getAllProblemList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            params.put("userId",ContextUtil.getCurrentUserId());
            list = wwrzTestApplyDao.getAllProblemList(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            params.put("userId",ContextUtil.getCurrentUserId());
            totalList = wwrzTestApplyDao.getAllProblemList(params);
            CommonFuns.convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            result.setSuccess(false);
            result.setMessage("查询异常");
        }
        return result;
    }
    public void exportProblemListExcel(HttpServletRequest request, HttpServletResponse response) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "认证问题列表";
        Map<String, Object> params = new HashMap<>(16);
        params =CommonFuns.getSearchParam(params, request, false);
        List<Map<String,Object>> list = wwrzTestApplyDao.getAllProblemList(params);
        Map<String, Object> productTypeMap = commonInfoManager.genMap("CPLX");
        Map<String, Object> cabFormMap = commonInfoManager.genMap("SJSXS");
        for(Map<String,Object> map:list){
            if(map.get("productType")!=null){
                map.put("productTypeText",productTypeMap.get(map.get("productType")));
            }
            if(map.get("cabForm")!=null){
                map.put("cabFormText",cabFormMap.get(map.get("cabForm")));
            }
        }
        CommonFuns.convertDate(list);
        String title = "认证问题列表";
        String[] fieldNames = {"流程编号","销售型号", "产品类型", "司机室形式","认证项目","认证问题","整改方案","方案编制着","整改完成时间"};
        String[] fieldCodes = {"mainId","productModel", "productTypeText", "cabFormText", "itemNames","problem","plan","chargerName","finish_date"};
        HSSFWorkbook wbObj = CommonExcelUtils.exportStyleExcel(list, fieldNames, fieldCodes, title);
        // 输出
        CommonExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    public void exportBaseInfoExcel(HttpServletRequest request, HttpServletResponse response) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "认证过程审批单";
        JsonPageResult result  = queryList(request, response,false);
        List<Map<String, Object> > list = result.getData();
        Map<String, Object> productTypeMap = commonInfoManager.genMap("CPLX");
        Map<String, Object> cabFormMap = commonInfoManager.genMap("SJSXS");
        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("DRAFTED","草稿");
        statusMap.put("RUNNING","运行中");
        statusMap.put("SUCCESS_END","成功结束");
        statusMap.put("DISCARD_END","作废");
        statusMap.put("ABORT_END","异常中止结束");
        statusMap.put("PENDING","挂起");
        for(Map<String,Object> map:list){
            if(map.get("cabForm")!=null){
                map.put("cabFormText",cabFormMap.get(map.get("cabForm")));
            }
            if(map.get("productType")!=null){
                map.put("productTypeText",productTypeMap.get(map.get("productType")));
            }
            if(map.get("status")!=null){
                map.put("statusText",statusMap.get(map.get("status")));
            }
        }
        String title = "认证过程审批单";
        String[] fieldNames = {"流程编号","申请人", "销售型号", "申请流程状态","当前处理人","当前任务","认证计划","产品主管","认证项目","产品类型","司机室形式","申请时间"};
        String[] fieldCodes = {"id","userName", "productModel", "statusText", "allTaskUserNames","taskName","planCode","productLeaderName","itemNames","productTypeText","cabFormText","CREATE_TIME_"};
        HSSFWorkbook wbObj = CommonExcelUtils.exportStyleExcel(list, fieldNames, fieldCodes, title);
        // 输出
        CommonExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    public JsonResult checkNote(String id) {
        JsonResult result = new JsonResult(true, "成功");
            JSONObject paramJson = new JSONObject();
            paramJson.put("id", id);
            int count = wwrzTestApplyDao.checkNote(paramJson);
            if (count==0) {
                result.setMessage("请点击“创建台账”创建CE自声明台账后再提交!");
                result.setSuccess(false);
            }
        return result;
    }
}
