package com.redxun.serviceEngineering.core.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.controller.PatentInterpretationController;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.service.ExportPartsAtlasService;

@Controller
@RequestMapping("/serviceEngineering/core/exportPartsAtlas/")
public class ExportPartsAtlasController {
    private static final Logger logger = LoggerFactory.getLogger(PatentInterpretationController.class);
    @Autowired
    private ExportPartsAtlasService exportPartsAtlasService;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    // 需求通知单列表
    @RequestMapping("demandListPage")
    public ModelAndView demandListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/exportPartsAtlasDemandList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    // 需求通知单列表查询
    @RequestMapping("demandListQuery")
    @ResponseBody
    public JsonPageResult<?> demandListQuery(HttpServletRequest request, HttpServletResponse response) {
        return exportPartsAtlasService.demandListQuery(request, response);
    }

    // 需求通知单表单
    @RequestMapping("demandEditPage")
    public ModelAndView demandEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/exportPartsAtlasDemandEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String demandId = RequestUtil.getString(request, "demandId");
        String demandNo = RequestUtil.getString(request, "demandNo", "");
        if (StringUtils.isNotBlank(demandId)) {
            mv.addObject("demandId", demandId);
        } else {
            if (StringUtils.isNotBlank(demandNo)) {
                JSONObject jsonObject = exportPartsAtlasService.getDemandInfoByNo(demandNo);
                if (jsonObject != null && jsonObject.containsKey("id")) {
                    mv.addObject("demandId", jsonObject.getString("id"));
                }
            }
        }
        return mv;
    }

    // 需求通知单信息
    @RequestMapping("getDemandInfo")
    @ResponseBody
    public JSONObject getDemandInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject();
        String demandId = RequestUtil.getString(request, "demandId");
        if (StringUtils.isNotBlank(demandId)) {
            jsonObject = exportPartsAtlasService.getDemandInfo(demandId);
        }
        return jsonObject;
    }

    // 查询需求清单
    @RequestMapping("getDemandDetailList")
    @ResponseBody
    public List<JSONObject> getDemandDetailList(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String demandId = RequestUtil.getString(request, "demandId", "");
        if (StringUtils.isBlank(demandId)) {
            return Collections.emptyList();
        }
        return exportPartsAtlasService.getDemandDetailList(demandId);
    }

    @RequestMapping("getAllDemandDetailList")
    @ResponseBody
    public JsonPageResult<?> getAllDemandDetailList(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        return exportPartsAtlasService.getAllDemandDetailList(request);
    }

    // 查询某条清单(需求、发运)中的整机
    @RequestMapping("getMachineList")
    @ResponseBody
    public List<JSONObject> getMachineList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return exportPartsAtlasService.getMachineList(request);
    }

    // 查询某条清单(需求、发运)中的配置信息
    @RequestMapping("getDetailConfigList")
    @ResponseBody
    public List<JSONObject> getDetailConfigList(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        return exportPartsAtlasService.getDetailConfigList(request);
    }

    // 所有制作任务管理页面
    @RequestMapping("taskAllPage")
    public ModelAndView taskAllPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/exportPartsAtlasTaskAllList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        // 判断是否是服务工程所
        boolean fwgcsUser = false;
        String deptName = ContextUtil.getCurrentUser().getMainGroupName();
        if (RdmConst.FWGCS_NAME.equalsIgnoreCase(deptName)) {
            fwgcsUser = true;
        }
        mv.addObject("fwgcsUser", fwgcsUser);
        return mv;
    }

    // 制作任务查询
    @RequestMapping("taskListQuery")
    @ResponseBody
    public JsonPageResult<?> taskListQuery(HttpServletRequest request, HttpServletResponse response) {
        return exportPartsAtlasService.taskListQuery(request, response);
    }

    @RequestMapping("taskExport")
    public void taskExport(HttpServletRequest request, HttpServletResponse response) {
        exportPartsAtlasService.taskExport(request, response);
    }

    // 制作任务详情
    @RequestMapping("taskDetailInfo")
    @ResponseBody
    public JSONObject taskDetailInfo(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id", "");
        if (StringUtils.isBlank(id)) {
            return new JSONObject();
        }
        return exportPartsAtlasService.taskDetailInfo(id);
    }

    // 个人制作任务管理页面
    @RequestMapping("taskSelfPage")
    public ModelAndView taskSelfPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/exportPartsAtlasTaskSelfList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        // 判断是否是零件图册档案室人员
        boolean exportPartsAtlasArchive =
                commonInfoManager.judgeUserIsPointRole(RdmConst.EXPORT_PARTSATLAS_ARCHIVE, ContextUtil.getCurrentUserId());
        mv.addObject("archiveUser", exportPartsAtlasArchive);
        // 判断是否是服务工程所
        boolean fwgcsUser = false;
        String deptName = ContextUtil.getCurrentUser().getMainGroupName();
        if (RdmConst.FWGCS_NAME.equalsIgnoreCase(deptName)) {
            fwgcsUser = true;
        }
        mv.addObject("fwgcsUser", fwgcsUser);
        return mv;
    }

    // 状态历史记录(图册任务状态历史或者异常反馈流程审批历史)
    @RequestMapping("statusHisQuery")
    @ResponseBody
    public List<JSONObject> statusHisQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String busKeyId = RequestUtil.getString(request, "busKeyId", "");
        if (StringUtils.isBlank(busKeyId)) {
            return Collections.emptyList();
        }
        String scene = RequestUtil.getString(request, "scene", "");
        if (StringUtils.isBlank(scene)) {
            return Collections.emptyList();
        }
        return exportPartsAtlasService.statusHisQuery(busKeyId, scene);
    }

    // 制作任务领取或释放
    @RequestMapping("taskReceiceOrRelease")
    @ResponseBody
    public JsonResult taskReceiceOrRelease(HttpServletRequest request, HttpServletResponse response) {
        return exportPartsAtlasService.taskReceiceOrRelease(request);
    }

    // 制作任务下一步
    @RequestMapping("taskNext")
    @ResponseBody
    public JsonResult taskNext(HttpServletRequest request, HttpServletResponse response) {
        String scene = RequestUtil.getString(request, "scene", "");
        if ("gdqr".equalsIgnoreCase(scene)) {
            return exportPartsAtlasService.taskGdqr(request);
        } else if ("zzwc".equalsIgnoreCase(scene)) {
            return exportPartsAtlasService.taskZzwc(request);
        } else if ("slzz".equalsIgnoreCase(scene)) {
            return exportPartsAtlasService.taskSlzz(request);
        } else if ("jumpModelMade2Slzz".equalsIgnoreCase(scene)) {
            return exportPartsAtlasService.jumpModelMade2Slzz(request);
        } else {
            JsonResult result = new JsonResult();
            result.setMessage("操作失败！");
            return result;
        }
    }

    // 制作任务上一步
    @RequestMapping("taskBack")
    @ResponseBody
    public JsonResult taskBack(HttpServletRequest request, HttpServletResponse response) {
        return exportPartsAtlasService.taskBack(request);
    }

    @RequestMapping("modelMadeListPage")
    public ModelAndView modelMadeListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/exportPartsAtlasModelMadeList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());

        // 判断是否是服务工程所
        boolean fwgcsUser = false;
        String deptName = ContextUtil.getCurrentUser().getMainGroupName();
        if (RdmConst.FWGCS_NAME.equalsIgnoreCase(deptName)) {
            fwgcsUser = true;
        }
        mv.addObject("fwgcsUser", fwgcsUser);

        List<JSONObject> nodeSetListWithName =
                commonBpmManager.getNodeSetListWithName("LJTCJXZZ", "userTask");
        mv.addObject("nodeSetListWithName", nodeSetListWithName);
        return mv;
    }

    @RequestMapping("modelMadeWaitListPage")
    public ModelAndView modelMadeWaitListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/exportPartsAtlasModelWaitMadeList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        // 判断是否是服务工程所
        boolean fwgcsUser = false;
        String deptName = ContextUtil.getCurrentUser().getMainGroupName();
        if (RdmConst.FWGCS_NAME.equalsIgnoreCase(deptName)) {
            fwgcsUser = true;
        }
        mv.addObject("fwgcsUser", fwgcsUser);
        return mv;
    }

    // 机型制作列表查询
    @RequestMapping("modelMadeListQuery")
    @ResponseBody
    public JsonPageResult<?> modelMadeListQuery(HttpServletRequest request, HttpServletResponse response) {
        return exportPartsAtlasService.modelMadeListQuery(request, response);
    }

    // 机型待制作列表查询
    @RequestMapping("modelMadeWaitListQuery")
    @ResponseBody
    public JsonPageResult<?> modelMadeWaitListQuery(HttpServletRequest request, HttpServletResponse response) {
        return exportPartsAtlasService.modelMadeWaitListQuery(request, response);
    }

    // 机型待制作列表查询
    @RequestMapping("exportModelMadeWaitList")
    @ResponseBody
    public void exportModelMadeWaitList(HttpServletRequest request, HttpServletResponse response) {
        exportPartsAtlasService.exportModelMadeWaitList(request, response);
    }

    // exportModelMadeWaitList
    // 机型制作流程表单页面
    @RequestMapping("modelMadeEditPage")
    public ModelAndView modelMadeEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/exportPartsAtlasModelMadeEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String id = RequestUtil.getString(request, "id", "");
        String nodeId = RequestUtil.getString(request, "nodeId", "");
        String action = RequestUtil.getString(request, "action", "");
        String status = RequestUtil.getString(request, "status", "");
        String matCode = RequestUtil.getString(request, "matCode", "");
        String designType = RequestUtil.getString(request, "designType", "");
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        mv.addObject("id", id).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "LJTCJXZZ", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        JSONObject objDetail = new JSONObject();
        if (StringUtils.isBlank(id)) {
            objDetail.put("matCode", matCode);
            objDetail.put("designType", designType);
            objDetail.put("creator", ContextUtil.getCurrentUser().getFullname());
        } else {
            objDetail = exportPartsAtlasService.getModelMadeDetail(id);
        }
        mv.addObject("objDetail", objDetail);
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    // 机型制作流程表单详情
    @RequestMapping("getModelMadeDetail")
    @ResponseBody
    public JSONObject getModelMadeDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject modelMadeObj = new JSONObject();
        String id = RequestUtil.getString(request, "id");
        if (StringUtils.isNotBlank(id)) {
            modelMadeObj = exportPartsAtlasService.getModelMadeDetail(id);
        }
        return modelMadeObj;
    }

    // 删除机型制作流程
    @RequestMapping("deleteModelMade")
    @ResponseBody
    public JsonResult deleteModelMade(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isNotBlank(instIdStr)) {
                String[] instIds = instIdStr.split(",");
                for (int index = 0; index < instIds.length; index++) {
                    bpmInstManager.deleteCascade(instIds[index], "");
                }
            }
            String[] ids = uIdStr.split(",");
            return exportPartsAtlasService.deleteModelMade(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteModelMade", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //导入机型制作数据
    @RequestMapping("importModelMade")
    @ResponseBody
    public JSONObject importModelMade(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        exportPartsAtlasService.importModelMade(result, request);
        return result;
    }

    //..
    @RequestMapping("importModelMadeTemplateDownload")
    public ResponseEntity<byte[]> importModelMadeTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return exportPartsAtlasService.importModelMadeTemplateDownload();
    }

    // 发运通知单列表
    @RequestMapping("dispatchListPage")
    public ModelAndView dispatchListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/exportPartsAtlasDispatchList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());

        boolean dispatchRelMachineUser = commonInfoManager.judgeUserIsPointRole(RdmConst.EXPORT_PARTSATLAS_MACHINE_OPERATOR, ContextUtil.getCurrentUserId());
        mv.addObject("dispatchRelMachineUser", dispatchRelMachineUser);
        return mv;
    }

    // 发运通知单列表查询
    @RequestMapping("dispatchListQuery")
    @ResponseBody
    public JsonPageResult<?> dispatchListQuery(HttpServletRequest request, HttpServletResponse response) {
        return exportPartsAtlasService.dispatchListQuery(request, response);
    }

    // 发运通知单表单
    @RequestMapping("dispatchEditPage")
    public ModelAndView dispatchEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/exportPartsAtlasDispatchEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String dispatchId = RequestUtil.getString(request, "dispatchId", "");
        mv.addObject("dispatchId", dispatchId);
        String action = RequestUtil.getString(request, "action", "detail");
        mv.addObject("action", action);
        return mv;
    }

    // 发运通知单信息
    @RequestMapping("getDispatchInfo")
    @ResponseBody
    public JSONObject getDispatchInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject();
        String dispatchId = RequestUtil.getString(request, "dispatchId");
        if (StringUtils.isNotBlank(dispatchId)) {
            jsonObject = exportPartsAtlasService.getDispatchInfo(dispatchId);
        } else {
            jsonObject.put("creatorName", ContextUtil.getCurrentUser().getFullname());
            jsonObject.put("CREATE_TIME_", DateFormatUtil.format(new Date(), "yyyy-MM-dd"));
        }
        return jsonObject;
    }

    // 暂存或提交发运通知单信息
    @RequestMapping("saveOrCommitDispatch")
    @ResponseBody
    public JsonResult saveOrCommitDispatch(HttpServletRequest request, @RequestBody String postStr,
                                           HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(postStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("请求参数为空");
            return result;
        }
        try {
            String scene = RequestUtil.getString(request, "scene", "");
            if (StringUtils.isBlank(scene)) {
                result.setSuccess(false);
                result.setMessage("类型为空");
                return result;
            }
            JSONObject formDataJson = JSONObject.parseObject(postStr);
            exportPartsAtlasService.saveOrCommitDispatch(formDataJson, scene, result);
            result.setData(formDataJson.getString("id"));
        } catch (Exception e) {
            logger.error("Exception in saveOrCommitDispatch");
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }

    // 查询发运清单
    @RequestMapping("getDispatchDetailList")
    @ResponseBody
    public List<JSONObject> getDispatchDetailList(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String dispatchId = RequestUtil.getString(request, "dispatchId", "");
        if (StringUtils.isBlank(dispatchId)) {
            return Collections.emptyList();
        }
        return exportPartsAtlasService.getDispatchDetailList(dispatchId);
    }

    @RequestMapping("getDispatchDetail")
    @ResponseBody
    public JSONObject getDispatchDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject();
        String dispatchDetailId = RequestUtil.getString(request, "dispatchDetailId");
        if (StringUtils.isNotBlank(dispatchDetailId)) {
            jsonObject = exportPartsAtlasService.getDispatchDetail(dispatchDetailId);
        }
        return jsonObject;
    }

    @RequestMapping("saveDispatchDetail")
    @ResponseBody
    public JsonResult saveDispatchDetail(HttpServletRequest request, @RequestBody String postStr,
                                         HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        try {
            String scene = RequestUtil.getString(request, "scene", "");
            JSONObject formDataJson = JSONObject.parseObject(postStr);
            exportPartsAtlasService.saveDispatchDetail(formDataJson, scene, result);
            result.setData(formDataJson.getString("id"));
        } catch (Exception e) {
            logger.error("Exception in saveDispatchDetail");
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }

    @RequestMapping("delDispatchDetail")
    @ResponseBody
    public JsonResult delDispatchDetail(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            exportPartsAtlasService.delDispatchDetail(Arrays.asList(ids));
        } catch (Exception e) {
            logger.error("Exception in delDispatchDetail");
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }

    // 异常反馈列表
    @RequestMapping("abnormalListPage")
    public ModelAndView abnormalListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/exportPartsAtlasAbnormalList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        // 判断是否是服务工程所
        boolean fwgcsUser = false;
        String deptName = ContextUtil.getCurrentUser().getMainGroupName();
        if (RdmConst.FWGCS_NAME.equalsIgnoreCase(deptName)) {
            fwgcsUser = true;
        }
        mv.addObject("fwgcsUser", fwgcsUser);
        // 判断是否服务工程所所长
        boolean fwgcsLeader =
                commonInfoManager.judgeUserIsDeptRespor(ContextUtil.getCurrentUserId(), RdmConst.FWGCS_NAME);
        mv.addObject("fwgcsLeader", fwgcsLeader);
        return mv;
    }

    // 异常反馈数据查询
    @RequestMapping("abnormalListQuery")
    @ResponseBody
    public JsonPageResult<?> abnormalListQuery(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        return exportPartsAtlasService.abnormalListQuery(request);
    }

    // 发起异常反馈
    @RequestMapping("saveAbnormal")
    @ResponseBody
    public JsonResult saveAbnormal(HttpServletRequest request, @RequestBody String abnormalDataStr,
                                   HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "提交成功");
        JSONObject abnormalObj = JSONObject.parseObject(abnormalDataStr);
        exportPartsAtlasService.saveAbnormal(result, abnormalObj);
        return result;
    }

    @RequestMapping("delAbnormal")
    @ResponseBody
    public JsonResult delAbnormal(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "操作成功");
        String uIdStr = RequestUtil.getString(request, "ids", "");
        if (StringUtils.isNotEmpty(uIdStr)) {
            String[] ids = uIdStr.split(",");
            exportPartsAtlasService.delAbnormal(Arrays.asList(ids));
        }
        return result;
    }

    // 异常反馈下一步（提交、审核）
    @RequestMapping("abnormalNext")
    @ResponseBody
    public JsonResult abnormalNext(HttpServletRequest request, @RequestBody String abnormalDataStr,
                                   HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "操作成功");
        String abnormalObjStr = RequestUtil.getString(request, "abnormalObj", "");
        String currentStatus = RequestUtil.getString(request, "currentStatus", "");
        if (StringUtils.isNotEmpty(abnormalObjStr) && StringUtils.isNotBlank(currentStatus)) {
            JSONObject abnormalObj = JSONObject.parseObject(abnormalObjStr);
            exportPartsAtlasService.abnormalNext(abnormalObj, currentStatus);
        }
        return result;
    }

    // 异常反馈上一步（驳回）
    @RequestMapping("abnormalPre")
    @ResponseBody
    public JsonResult abnormalPre(HttpServletRequest request, @RequestBody String abnormalDataStr,
                                  HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "操作成功");
        String abnormalObjStr = RequestUtil.getString(request, "abnormalObj", "");
        String optionDesc = RequestUtil.getString(request, "optionDesc", "");
        if (StringUtils.isNotEmpty(abnormalObjStr)) {
            JSONObject abnormalObj = JSONObject.parseObject(abnormalObjStr);
            exportPartsAtlasService.abnormalPre(abnormalObj, optionDesc);
        }
        return result;
    }

    /**
     * 查询制作任务关联的所有流程（可能多种类型，每种类型多个）
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("relFlowQuery")
    @ResponseBody
    public List<JSONObject> relFlowQuery(HttpServletRequest request, HttpServletResponse response) {
        List<JSONObject> result = new ArrayList<>();
        String taskId = RequestUtil.getString(request, "taskId", "");
        if (StringUtils.isNotEmpty(taskId)) {
            exportPartsAtlasService.relFlowQuery(taskId, result);
        }
        return result;
    }

    // 异常反馈列表
    @RequestMapping("changeNoticeListPage")
    public ModelAndView changeNoticeListPage(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String jspPath = "serviceEngineering/core/exportPartsAtlasChangeNoticeList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        // 判断是否是服务工程所
        boolean fwgcsUser = false;
        String deptName = ContextUtil.getCurrentUser().getMainGroupName();
        if (RdmConst.FWGCS_NAME.equalsIgnoreCase(deptName)) {
            fwgcsUser = true;
        }
        mv.addObject("fwgcsUser", fwgcsUser);
        return mv;
    }

    @RequestMapping("changeNoticeListQuery")
    @ResponseBody
    public JsonPageResult<?> changeNoticeListQuery(HttpServletRequest request, HttpServletResponse response) {
        return exportPartsAtlasService.changeNoticeListQuery(request, response);
    }

    @RequestMapping("changeConfirm")
    @ResponseBody
    public JsonResult changeConfirm(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "操作成功！");
        String changeId = RequestUtil.getString(request, "id", "");
        if (StringUtils.isNotEmpty(changeId)) {
            exportPartsAtlasService.changeConfirm(changeId, result);
        }
        return result;
    }

    @RequestMapping("exportList")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        exportPartsAtlasService.exportList(request, response);
    }

    /**
     * @lwgkiller:以下成品库功能相关
     */
    //..成品库发运列表查询
    @RequestMapping("dispatchFPWListPage")
    public ModelAndView dispatchFPWListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/exportPartsAtlasDispatchFPWList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        String whatIsTheRole = "browse";
        List<String> usernoList = Arrays.asList(sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringGroups", "dispatchFPWImporter").
                getValue().split(","));
        if (ContextUtil.getCurrentUserId().equalsIgnoreCase("1")) {
            whatIsTheRole = "sa";
        } else if (usernoList.contains(ContextUtil.getCurrentUser().getUserNo())) {
            whatIsTheRole = "sa";
        }
        boolean manualAdmin = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "操保手册管理员");
        mv.addObject("manualAdmin", manualAdmin);
        mv.addObject("whatIsTheRole", whatIsTheRole);
        return mv;
    }

    //..成品库发运列表查询
    @RequestMapping("dispatchFPWListQuery")
    @ResponseBody
    public JsonPageResult<?> dispatchFPWListQuery(HttpServletRequest request, HttpServletResponse response) {
        return exportPartsAtlasService.dispatchFPWListQuery(request, response);
    }

    //..
    @RequestMapping("deleteDispatchFPW")
    @ResponseBody
    public JsonResult deleteDispatchFPW(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return exportPartsAtlasService.deleteDispatchFPW(ids);
        } catch (Exception e) {
            logger.error("Exception in delete", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("importDispatchFPWTemplateDownload")
    public ResponseEntity<byte[]> importDispatchFPWTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return exportPartsAtlasService.importDispatchFPWTemplateDownload();
    }

    //..
    @RequestMapping("importDispatchFPW")
    @ResponseBody
    public JSONObject importDispatchFPW(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        exportPartsAtlasService.importDispatchFPW(result, request);
        return result;
    }

    //..
    @RequestMapping("getDispatchFWPDetail")
    @ResponseBody
    public JSONObject getDispatchFWPDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject();
        String businessId = RequestUtil.getString(request, "id");
        if (StringUtils.isNotBlank(businessId)) {
            jsonObject = exportPartsAtlasService.getDispatchFWPDetailById(businessId);
        }
        return jsonObject;
    }

    @RequestMapping("checkManaulStatus")
    @ResponseBody
    public String checkManaulStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String res = "";
        String demandNo = RequestUtil.getString(request, "demandNo");
        if (StringUtils.isNotBlank(demandNo)) {
            JSONObject params = new JSONObject();
            params.put("demandNo", RequestUtil.getString(request, "demandNo"));
            params.put("exportingCountry", RequestUtil.getString(request, "exportingCountry"));
            params.put("materialCode", RequestUtil.getString(request, "materialCode"));
            params.put("designModel", RequestUtil.getString(request, "designModel"));
            params.put("salesModel", RequestUtil.getString(request, "salesModel"));
            res = exportPartsAtlasService.checkManaulStatus(params);
        }
        return res;
    }

    // 导出
    @RequestMapping("exportData")
    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        exportPartsAtlasService.exportData(request, response);
    }

    //保存列表中的操保手册状态描述
    @RequestMapping("saveStatusDesc")
    @ResponseBody
    public JsonResult saveStatusDesc(HttpServletRequest request, @RequestBody String postStr,
                                     HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "提交成功");
        JSONArray jsonArray = JSONObject.parseArray(postStr);
        exportPartsAtlasService.saveStatusDesc(result, jsonArray);
        return result;
    }


    @RequestMapping("changeRelList")
    @ResponseBody
    public List<JSONObject> changeRelList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String applyId = RequestUtil.getString(request, "applyId", "");
        if (StringUtils.isBlank(applyId)) {
            logger.error("applyId is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("applyId", applyId);
        List<JSONObject> demandList = exportPartsAtlasService.queryChangeRelList(params);
        return demandList;
    }


    @RequestMapping("wgjtczlList")
    @ResponseBody
    public List<JSONObject> wgjtczlList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String applyId = RequestUtil.getString(request, "applyId", "");
        if (StringUtils.isBlank(applyId)) {
            logger.error("applyId is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("applyId", applyId);
        List<JSONObject> demandList = exportPartsAtlasService.queryWgjtczlList(params);
        return demandList;
    }


    @RequestMapping("wxzyjList")
    @ResponseBody
    public List<JSONObject> wxzyjList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String applyId = RequestUtil.getString(request, "applyId", "");
        if (StringUtils.isBlank(applyId)) {
            logger.error("applyId is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("applyId", applyId);
        List<JSONObject> demandList = exportPartsAtlasService.queryWxzyjList(params);
        return demandList;
    }

    @RequestMapping("gyhjmxList")
    @ResponseBody
    public List<JSONObject> gyhjmxList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String applyId = RequestUtil.getString(request, "applyId", "");
        if (StringUtils.isBlank(applyId)) {
            logger.error("applyId is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("applyId", applyId);
        List<JSONObject> demandList = exportPartsAtlasService.queryGyhjmxList(params);
        return demandList;
    }


    @RequestMapping("saveBusiness")
    @ResponseBody
    public JsonResult saveBusiness(HttpServletRequest request, @RequestBody String formDataStr,
                                   HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(formDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(formDataStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                exportPartsAtlasService.createModelMade(formDataJson);
            } else {
                exportPartsAtlasService.updateModelMade(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save exportPartsAtlasModelMade");
            result.setSuccess(false);
            result.setMessage("Exception in save exportPartsAtlasModelMade");
            return result;
        }
        return result;
    }

    //..
    @RequestMapping("importItemTDownload")
    public ResponseEntity<byte[]> importItemTDownload(HttpServletRequest request, HttpServletResponse response) {
        String importType = RequestUtil.getString(request, "importType");
        if (StringUtil.isNotEmpty(importType)) {
            return exportPartsAtlasService.importItemTDownload(importType);
        } else {
            return null;
        }
    }

    //..
    @RequestMapping("importItem")
    @ResponseBody
    public JSONObject importItem(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        exportPartsAtlasService.importItem(result, request);
        return result;
    }

    //..
    @RequestMapping("exportItem")
    public void exportItem(HttpServletRequest request, HttpServletResponse response) {
        exportPartsAtlasService.exportItem(request, response);
    }

    //..
    @RequestMapping("exportBusiness")
    public void exportBusiness(HttpServletRequest request, HttpServletResponse response) {
        exportPartsAtlasService.exportBusiness(request, response);
    }
}
