package com.redxun.materielextend.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.materielextend.core.service.MaterielApplyService;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.materielextend.core.util.MaterielConstant;
import com.redxun.materielextend.core.util.ResultData;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.sys.org.manager.OsUserManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2020 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2020/6/2 17:40
 */
@Controller
@RequestMapping("/materielExtend/apply")
public class MaterielExtendApplyController {
    private static final Logger logger = LoggerFactory.getLogger(MaterielExtendApplyController.class);
    @Autowired
    private MaterielService materielService;
    @Autowired
    private MaterielApplyService materielApplyService;
    @Autowired
    private OsUserManager osUserManager;

    @RequestMapping("/listPage")
    public ModelAndView applyListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "materielExtend/materielExtendApplyList.jsp";
        String opRoleName = materielApplyService.queryMaterielOpRoleByUserId(ContextUtil.getCurrentUserId());
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("opRoleName", opRoleName);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("/queryPage")
    public ModelAndView applyQueryPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "materielExtend/materielExtendApplyQueryList.jsp";
        String opRoleName = materielApplyService.queryMaterielOpRoleByUserId(ContextUtil.getCurrentUserId());
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("opRoleName", opRoleName);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("/getApplyList")
    @ResponseBody
    public JsonPageResult<?> getApplyList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return materielApplyService.getApplyList(request, response, true);
    }

    @RequestMapping("/queryCodeList")
    @ResponseBody
    public JsonPageResult<?> queryCodeList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return materielApplyService.queryCodeList(request,true);
    }

    @RequestMapping("/applyDel")
    @ResponseBody
    public ResultData applyDel(HttpServletRequest request, HttpServletResponse response) {
        String ids = RequestUtil.getString(request, "ids");
        List<String> applyNos = new ArrayList<>();
        if (StringUtils.isNotBlank(ids)) {
            applyNos.addAll(Arrays.asList(ids.split(",", -1)));
        }
        return materielApplyService.applyDel(applyNos);
    }

    @RequestMapping("/editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "materielExtend/materielExtendApplyEdit.jsp";
        String applyNo = RequestUtil.getString(request, "applyNo", "");
        String action = RequestUtil.getString(request, "action", "");
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject applyObj = new JSONObject();
        if (StringUtils.isNotBlank(applyNo)) {
            // 查询申请单的详情
            applyObj = materielApplyService.queryMaterielApplyById(applyNo);
        } else {
            OsUser currentUser = osUserManager.get(ContextUtil.getCurrentUserId());
            applyObj.put("applyUserName", ContextUtil.getCurrentUser().getFullname());
            applyObj.put("applyUserMobile", currentUser.getMobile());
            applyObj.put("applyUserDepName", ContextUtil.getCurrentUser().getMainGroupName());
        }
        List<JSONObject> respProperties = new ArrayList<>();
        // 对于需要编辑数据的action场景，查询当前登录用户的角色
        if (!MaterielConstant.MATERIEL_ACTION_VIEW.equalsIgnoreCase(action)) {
            String opRoleName = materielApplyService.queryMaterielOpRoleByUserId(ContextUtil.getCurrentUserId());
            mv.addObject("opRoleName", opRoleName);
            // 查询当前角色负责的字段和字段必填性
            Map<String, Object> params = new HashMap<>();
            params.put("respRoleKey", opRoleName);
            respProperties = materielService.queryMaterielProperties(params);
        }
        mv.addObject("respProperties", respProperties);
        mv.addObject("action", action);
        mv.addObject("applyObj", applyObj);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserId", ContextUtil.getCurrentUser().getUserId());
        return mv;
    }

    @RequestMapping("/applyDetail")
    @ResponseBody
    public JSONObject queryApplyDetail(HttpServletRequest request, HttpServletResponse response) {
        String applyNo = RequestUtil.getString(request, "applyNo", "");
        if (StringUtils.isBlank(applyNo)) {
            return new JSONObject();
        } else {
            return materielApplyService.queryMaterielApplyById(applyNo);
        }
    }

    /**
     * 申请单的创建、更新和提交
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/applySaveOrCommit")
    @ResponseBody
    public ResultData applySaveOrCommit(HttpServletRequest request, @RequestBody String requestBody,
        HttpServletResponse response) {
        if (StringUtils.isBlank(requestBody)) {
            logger.error("applySaveOrCommit failed, postBody is blank!");
            ResultData resultData = new ResultData();
            resultData.setSuccess(false);
            resultData.setMessage("操作失败，发送内容为空！");
            return resultData;
        }
        return materielApplyService.applySaveOrCommit(requestBody);
    }

    /**
     * 主动触发更新到SAP
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/sync2SAP")
    @ResponseBody
    public ResultData sync2SAP(HttpServletRequest request, HttpServletResponse response) {
        String applyNo = RequestUtil.getString(request, "applyNo", "");
        ResultData resultData = new ResultData();
        resultData.setMessage("操作成功!");
        materielApplyService.sync2SAP(resultData, applyNo);
        return resultData;
    }

    @RequestMapping("/materielList")
    @ResponseBody
    public List<JSONObject> queryMaterielList(HttpServletRequest request, HttpServletResponse response) {
        String applyNo = RequestUtil.getString(request, "applyNo", "");
        Map<String, Object> param = new HashMap<>();
        List<String> applyNos = new ArrayList<>();
        applyNos.add(applyNo);
        param.put("applyNos", applyNos);
        List<JSONObject> materielList = materielService.queryMaterielsByApplyNo(param);
        return materielList;
    }

    @RequestMapping("/materielEditPage")
    public ModelAndView materielEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "materielExtend/materielExtendMaterielEdit.jsp";
        String action = RequestUtil.getString(request, "action", "");
        String materielId = RequestUtil.getString(request, "materielId", "");
        String applyNo = RequestUtil.getString(request, "applyNo", "");
        String sqUserId = RequestUtil.getString(request, "sqUserId", "");

        JSONObject materielObj = new JSONObject();
        materielObj.put("applyNo", applyNo);
        materielObj.put("markError", "no");

        // 查询物料的详细信息
        if (StringUtils.isNotBlank(materielId)) {
            materielObj = materielService.queryMaterielById(materielId);
        }
        ModelAndView mv = new ModelAndView(jspPath);
        List<JSONObject> respProperties = new ArrayList<>();
        // 对于需要编辑数据的action场景，查询当前登录用户的角色
        if (!MaterielConstant.MATERIEL_ACTION_VIEW.equalsIgnoreCase(action)) {
            String opRoleName = materielApplyService.queryMaterielOpRoleByUserId(ContextUtil.getCurrentUserId());
            mv.addObject("opRoleName", opRoleName);
            // 查询当前角色负责的字段和字段必填性
            Map<String, Object> params = new HashMap<>();
            params.put("respRoleKey", opRoleName);
            respProperties = materielService.queryMaterielProperties(params);
        }
        mv.addObject("respProperties", respProperties);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserId", ContextUtil.getCurrentUser().getUserId());
        mv.addObject("sqUserId", sqUserId);
        mv.addObject("action", action);
        mv.addObject("materielObj", materielObj);

        return mv;
    }

    @RequestMapping("/materielDic")
    @ResponseBody
    public Map<String, Map<String, JSONObject>> queryMaterielDic(HttpServletRequest request,
        HttpServletResponse response) {
        return materielService.queryMaterielDic();
    }

    @RequestMapping("/materielEditSave")
    @ResponseBody
    public ResultData materielEditSave(HttpServletRequest request, @RequestBody String postBody,
        HttpServletResponse response) {
        return materielService.materielEditSave(postBody);
    }

    @RequestMapping("/materielEditDel")
    @ResponseBody
    public ResultData materielEditDel(HttpServletRequest request, HttpServletResponse response) {
        String ids = RequestUtil.getString(request, "ids");
        List<String> idList = new ArrayList<>();
        if (StringUtils.isNotBlank(ids)) {
            idList.addAll(Arrays.asList(ids.split(",", -1)));
        }
        return materielService.deleteMaterielByIds(idList);
    }

    // 物料扩充导入模板下载
    @RequestMapping("/importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return materielService.importTemplateDownload();
    }

    /**
     * 物料的批量导入和更新
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/importMateriel")
    @ResponseBody
    public JSONObject importMateriel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        result.put("result", true);
        result.put("message", new JSONArray());
        materielService.importMateriel(result, request);
        return result;
    }

    /**
     * 导出所有或者问题物料
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/exportMateriels")
    public void exportMateriels(HttpServletRequest request, HttpServletResponse response) {
        materielService.exportMateriels(request, response);
    }

    @RequestMapping("/materielPropertyDescPage")
    public ModelAndView materielPropertyDescPage(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new ModelAndView("materielExtend/materielPropertyDesc.jsp");
        return mv;
    }

    @RequestMapping("/materielPropertyDesc")
    @ResponseBody
    public List<JSONObject> materielPropertyDesc(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> params = new HashMap<>();
        List<JSONObject> materielProps = materielService.queryMaterielProperties(params);
        Iterator<JSONObject> iterator = materielProps.iterator();
        while (iterator.hasNext()) {
            JSONObject oneObj = iterator.next();
            String respRoleKey = oneObj.getString("respRoleKey");
            if (respRoleKey.equalsIgnoreCase(MaterielConstant.APPLY_OP_COMMON)) {
                iterator.remove();
            } else {
                switch (respRoleKey) {
                    case MaterielConstant.APPLY_OP_SQRKC:
                        oneObj.put("operaterName", "申请人");
                        break;
                    case MaterielConstant.APPLY_OP_GYKC:
                        oneObj.put("operaterName", "工艺");
                        break;
                    case MaterielConstant.APPLY_OP_CGKC:
                        oneObj.put("operaterName", "采购");
                        break;
                    case MaterielConstant.APPLY_OP_WLKC:
                        oneObj.put("operaterName", "物流");
                        break;
                    case MaterielConstant.APPLY_OP_ZZKC:
                        oneObj.put("operaterName", "制造");
                        break;
                    case MaterielConstant.APPLY_OP_CWKC:
                        oneObj.put("operaterName", "财务");
                        break;
                    case MaterielConstant.APPLY_OP_GFKC:
                        oneObj.put("operaterName", "供方");
                        break;
                }
            }
        }
        JSONObject markError = new JSONObject();
        markError.put("operaterName", "所有人");
        markError.put("propertyName", "是否为问题物料");
        markError.put("propertyDesc",
            "代表该物料是否数据存在问题。默认等于“否”，如果标记为“是”系统将不再对该物料信息正确性进行任何检查，最终该物料将不会被同步更新到SAP系统中。除“申请人”和“工艺”角色外，其他角色不允许将物料问题状态由“是”改为“否”");
        JSONObject markErrorReason = new JSONObject();
        markErrorReason.put("operaterName", "所有人");
        markErrorReason.put("propertyName", "问题原因");
        markErrorReason.put("propertyDesc", "当“是否为问题物料”为“是”时，该字段必填");

        materielProps.add(0, markError);
        materielProps.add(1, markErrorReason);
        return materielProps;
    }

    @PostMapping("/exportMaterielsByApply")
    public void exportMaterielsByApply(HttpServletResponse response, HttpServletRequest request) {
        materielApplyService.exportMaterielsByApply(response, request);
    }

    /**
     * 导出选中审批单的物料信息
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/exportApplyMaterials")
    public void exportApplyMaterials(HttpServletRequest request, HttpServletResponse response) {
        materielApplyService.exportApplyMaterials(request, response);
    }

    /**
     * 按照多个申请单进行物料的批量导入和更新
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/importApplyMateriel")
    @ResponseBody
    public JSONObject importApplyMateriel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        result.put("result", true);
        result.put("message", new JSONArray());
        materielApplyService.importMateriel(result, request);
        return result;
    }

    @RequestMapping("/failConfirm")
    @ResponseBody
    public JSONObject failConfirm(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        materielApplyService.applyFailConfirm(result, request);
        return result;
    }

    @RequestMapping("/checkSuccessConfirm")
    @ResponseBody
    public JSONObject checkSuccessConfirm(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        materielApplyService.checkSuccessConfirm(result, request);
        return result;
    }
}
