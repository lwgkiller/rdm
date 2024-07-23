package com.redxun.rdmZhgl.core.service;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.YfjbBaseInfoDao;
import com.redxun.rdmZhgl.core.dao.YfjbEditApplyDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.util.CommonExcelUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

import groovy.util.logging.Slf4j;

/**
 * @author zz
 */
@Service
@Slf4j
public class YfjbEditApplyService {
    private static Logger logger = LoggerFactory.getLogger(YfjbEditApplyService.class);
    @Resource
    private BpmInstManager bpmInstManager;
    @Resource
    private YfjbEditApplyDao yfjbEditApplyDao;
    @Resource
    private XcmgProjectManager xcmgProjectManager;
    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    CommonInfoManager commonInfoManager;
    @Resource
    YfjbBaseInfoDao yfjbBaseInfoDao;
    public void add(Map<String, Object> params) {
        String preFix = "SP-";
        String id = preFix + XcmgProjectUtil.getNowLocalDateStr("yyyyMMddHHmmssSSS") + (int)(Math.random() * 100);
        params.put("id", id);
        params.put("deptId", ContextUtil.getCurrentUser().getMainGroupId());
        params.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        params.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        params.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        yfjbEditApplyDao.add(params);

    }

    public void update(Map<String, Object> params) {
        params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        params.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        yfjbEditApplyDao.update(params);
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
            yfjbEditApplyDao.delete(applyId);
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
            List<Map<String, Object>> applyList = yfjbEditApplyDao.queryList(params);
            // 增加不同角色和部门的人看到的数据不一样的过滤
            List<Map<String, Object>> finalAllApplyList = null;
            finalAllApplyList = filterApplyListByDepRole(applyList);
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
            logger.error("Exception in queryList", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }
    public void exportBaseInfoExcel(HttpServletRequest request, HttpServletResponse response) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "研发降本项目申请修改信息列表";
        Map<String, Object> params = new HashMap<>(16);
        params = CommonFuns.getSearchParam(params, request, false);
        List<Map<String,Object>> list = yfjbEditApplyDao.getApplyProjectInfoList(params);
        convertDate(list);
        Map<String, Object> sfMap = commonInfoManager.genMap("YESORNO");
        Map<String, Object> statusMap = commonInfoManager.genMap("YFJB-XMZT");
        Map<String, Object> costTypeMap = commonInfoManager.genMap("YFJB-JBFS");
        Map<String, Object> processMap = commonInfoManager.genMap("YFJB-JDZT");
        Map<String, Object> majorMap = commonInfoManager.genMap("YFJB-SSZY");
        Map<String, Object> processTypeMap = commonInfoManager.genMap("YFJB-JDLB");
        for(Map<String,Object> map:list){
            if(map.get("infoStatus")!=null){
                map.put("infoStatusText",statusMap.get(map.get("infoStatus")));
            }
            if(map.get("isNewProcess")!=null){
                map.put("isNewProcessText",sfMap.get(map.get("isNewProcess")));
            }
            if(map.get("costType")!=null){
                map.put("costTypeText",costTypeMap.get(map.get("costType")));
            }
            if(map.get("isReplace")!=null){
                map.put("isReplaceText",sfMap.get(map.get("isReplace")));
            }
            if(map.get("processStatus")!=null){
                map.put("processStatusText",processMap.get(map.get("processStatus")));
            }
            if(map.get("major")!=null){
                map.put("majorText",majorMap.get(map.get("major")));
            }
            if(map.get("type")!=null){
                map.put("typeText",processTypeMap.get(map.get("type")));
            }
        }
        String title = "研发降本项目申请修改信息列表";
        String[] fieldNames = {"申请单号","申请原因","申请日期","申请人","销售型号", "设计型号", "项目状态",  "是否填写最新进度", "原物料编码", "原物料名称", "原供应商", "降本方式",
                "降本措施", "替代物料编码", "替代物料名称", "新供应商", "差额", "单台用量", "代替比例(%)", "单台降本", "已实现单台降本", "风险评估", "生产是否切换",
                "计划试制时间","实际试制时间","计划下发切换通知单时间","实际下发切换通知单时间","计划切换时间","实际切换时间","所属部门","所属专业"};
        String[] fieldCodes = {"id","reason","CREATE_TIME_","applyUserName","saleModel", "designModel", "infoStatusText",  "isNewProcessText", "orgItemCode", "orgItemName", "orgSupplier", "costTypeText",
                "costMeasure", "newItemCode", "newItemName", "newSupplier", "differentPrice", "perSum", "replaceRate", "perCost", "achieveCost", "risk", "isReplaceText",
                "jhsz_date","sjsz_date","jhxfqh_date","sjxfqh_date","jhqh_date","sjqh_date","deptName","majorText"};
        HSSFWorkbook wbObj = CommonExcelUtils.exportStyleExcel(list, fieldNames, fieldCodes, title);
        // 输出
        CommonExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }


    /**
     * 根据登录人部门、角色对列表进行过滤
     */
    public List<Map<String, Object>> filterApplyListByDepRole(List<Map<String, Object>> applyList) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (applyList == null || applyList.isEmpty()) {
            return result;
        }
        // 刷新任务的当前执行人
        xcmgProjectManager.setTaskCurrentUser(applyList);
        // 管理员查看所有的包括草稿数据
        if (ConstantUtil.ADMIN.equals(ContextUtil.getCurrentUser().getUserNo())) {
            return applyList;
        }
        JSONObject roleObj = commonInfoManager.hasPermission("YFJB-GLY");
        if (roleObj.getBoolean("YFJB-GLY")) {
            return applyList;
        }
        // 分管领导的查看权限等同于项目管理人员
        boolean showAll = false;
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
        boolean isDepProjectManager =
            XcmgProjectUtil.judgeIsDepProjectManager(currentUserMainDepName, currentUserRoles);
        // 过滤
        for (Map<String, Object> oneApply : applyList) {
            // 自己是当前流程处理人
            if (oneApply.get("currentProcessUserId") != null
                && oneApply.get("currentProcessUserId").toString().contains(currentUserId)) {
                oneApply.put("processTask", true);
                result.add(oneApply);
            } else if (showAll) {
                // 分管领导和项目管理人员查看所有非草稿的数据或者草稿但是创建人CREATE_BY_是自己的
                if (oneApply.get("instStatus") != null && !"DRAFTED".equals(oneApply.get("instStatus").toString())) {
                    result.add(oneApply);
                } else {
                    if (oneApply.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneApply);
                    }
                }
            } else if (isDepRespMan || isDepProjectManager) {
                // 部门负责人对于非草稿的且申请人部门是当前部门，或者草稿但是创建人CREATE_BY_是自己的
                if (oneApply.get("instStatus") != null && !"DRAFTED".equals(oneApply.get("instStatus").toString())) {
                    if (oneApply.get("applyUserDepId").toString().equals(currentUserMainDepId)) {
                        result.add(oneApply);
                    }
                } else {
                    if (oneApply.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneApply);
                    }
                }

            } else {
                // 其他人对于创建人CREATE_BY_是自己的
                if (oneApply.get("CREATE_BY_").toString().equals(currentUserId)) {
                    result.add(oneApply);
                }
            }
        }
        return result;
    }

    public static void convertDate(List<Map<String, Object>> list) {
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> oneApply : list) {
                for (String key : oneApply.keySet()) {
                    if (key.endsWith("_TIME_") || key.endsWith("_time")) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd  HH:mm:ss"));
                        }
                    }
                    if ("noticeDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                }

            }
        }
    }
}
