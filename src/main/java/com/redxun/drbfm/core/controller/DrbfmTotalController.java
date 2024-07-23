package com.redxun.drbfm.core.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.util.IdUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.entity.BpmInst;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.drbfm.core.dao.DrbfmSingleDao;
import com.redxun.drbfm.core.dao.DrbfmTotalDao;
import com.redxun.drbfm.core.service.DrbfmTotalService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;

@Controller
@RequestMapping("/drbfm/total/")
public class DrbfmTotalController {
    private Logger logger = LogManager.getLogger(DrbfmTotalController.class);
    @Autowired
    private DrbfmTotalService drbfmTotalService;
    @Autowired
    private DrbfmTotalDao drbfmTotalDao;
    @Autowired
    private DrbfmSingleDao drbfmSingleDao;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    @RequestMapping("totalListPage")
    public ModelAndView totalListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/drbfmTotalList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("getTotalList")
    @ResponseBody
    public JsonPageResult<?> getTotalList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return drbfmTotalService.getTotalList(request, response, true);
    }

    @RequestMapping("deleteTotal")
    @ResponseBody
    public JsonResult deleteTotal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            JsonResult result = new JsonResult(true, "操作成功！");
            String uIdStr = RequestUtil.getString(request, "ids");
            if (StringUtils.isBlank(uIdStr)) {
                return result;
            }
            String[] totalIds = uIdStr.split(",");
            return drbfmTotalService.deleteTotal(totalIds);
        } catch (Exception e) {
            logger.error("Exception in deleteTotal", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("saveTotal")
    @ResponseBody
    public JsonResult saveTotal(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        String gridData = request.getParameter("gridData");
        String formData = request.getParameter("formData");
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            result.setSuccess(false);
            result.setMessage("表单为空，保存失败！");
            return result;
        }
        JSONObject formJSON = JSONObject.parseObject(formData);
        drbfmTotalService.saveTotal(result, formJSON, gridData);
        return result;
    }

    // 提交保存
    @RequestMapping("commitAndCreateSingle")
    @ResponseBody
    public JsonResult commitAndCreateSingle(HttpServletRequest request, HttpServletResponse response,
        @RequestBody JSONObject postObj) {
        JsonResult result = new JsonResult(true, "操作成功");
        if (!postObj.containsKey("formData")) {
            logger.warn("formData is blank");
            result.setSuccess(false);
            result.setMessage("表单为空，保存失败！");
            return result;
        }
        // 先保存
        JSONObject formJSON = postObj.getJSONObject("formData");
        String gridDataStr = "";
        if (postObj.containsKey("gridData")) {
            gridDataStr = postObj.getJSONArray("gridData").toJSONString();
        }
        drbfmTotalService.saveTotal(result, formJSON, gridDataStr);

        // @mh 2023年12月12日14:06:33 复制流程数据
        JSONObject param = new JSONObject();
        param.put("belongTotalId", result.getData().toString());
        param.put("scene", "createSingle");
        List<JSONObject> structList = drbfmTotalDao.queryStructByParam(param);
        if (structList == null || structList.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("未找到需创建的部件，操作失败！");
            return result;
        }
        // structId 新id到被复制的id映射
        HashMap<String, String> structIdMap = new HashMap<>();
        for (JSONObject oneObj : structList) {
            structIdMap.put(oneObj.getString("id"), oneObj.getString("oldId"));
        }

        // 针对需要风险分析的部件进行部件分析流程的创建(过滤掉已有的)

        drbfmTotalService.createSingleFlow(result, result.getData().toString(), formJSON.getString("jixing"));

        drbfmTotalService.copyFlowData(result, result.getData().toString(), structIdMap, structList);

        return result;
    }



    @RequestMapping("copyTotal")
    @ResponseBody
    public JsonResult copyTotalTemp(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        String gridData = request.getParameter("gridData");
        String formData = request.getParameter("formData");
        String action = request.getParameter("action");
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            result.setSuccess(false);
            result.setMessage("表单为空，保存失败！");
            return result;
        }
        JSONObject formJSON = JSONObject.parseObject(formData);

        // 还是用原copyTotalAndSaveDraft
        String newFormId = IdUtil.getId();
        // structId 新id到被复制的id映射
        HashMap<String, String> structIdMap = new HashMap<>();
        drbfmTotalService.copyTotal(result, formJSON, gridData, newFormId, structIdMap);

        return result;
    }

    // 总项目的结构分解页面
    @RequestMapping("totalDecomposePage")
    public ModelAndView totalDecomposePage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/drbfmTotalDecompose.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = RequestUtil.getString(request, "id");
        String action = RequestUtil.getString(request, "action");
        String flag = RequestUtil.getString(request, "flag");
        mv.addObject("id", id);
        mv.addObject("action", action);
        mv.addObject("commitFlag", flag);
        boolean isBaseFemaAdmin =
                commonInfoManager.judgeUserIsPointRole("基础FEMA管理员", ContextUtil.getCurrentUserId());
        mv.addObject("isBaseFemaAdmin", isBaseFemaAdmin);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        return mv;
    }

    @RequestMapping("queryTotalDetail")
    @ResponseBody
    public JSONObject queryTotalDetail(HttpServletRequest request, HttpServletResponse response) {
        String totalId = RequestUtil.getString(request, "id", "");
        if (StringUtils.isBlank(totalId)) {
            return new JSONObject();
        }
        return drbfmTotalService.getTotalDetail(totalId);
    }

    @RequestMapping("projectList")
    @ResponseBody
    public List<Map<String, Object>> queryProjectList(HttpServletRequest request) {
        Map<String, Object> params = new HashMap<>();
        // TODO 是否增加限制条件
        params.put("sortField", "CONVERT(project_baseinfo.projectName using GBK)");
        params.put("status", Arrays.asList(BpmInst.STATUS_RUNNING));
        List<Map<String, Object>> projectList = xcmgProjectOtherDao.queryProjectList(params);
        return projectList;
    }

    @RequestMapping("delStruct")
    @ResponseBody
    public JsonResult delStruct(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        JSONObject param = new JSONObject();
        param.put("pathId", id);
        // 查找这个节点及所有子节点
        List<JSONObject> queryResults = drbfmTotalDao.queryStructByParam(param);
        if (queryResults != null && !queryResults.isEmpty()) {
            JSONObject delObj = null;
            Set<String> structIds = new HashSet<>();
            for (JSONObject oneData : queryResults) {
                String thisDataId = oneData.getString("id");
                structIds.add(thisDataId);
                if (thisDataId.equals(id)) {
                    delObj = oneData;
                }
            }
            // 判断这个结构项及其子项是否已经创建了部件分析项目，如果已创建则不允许删除
            Map<String, Object> querySingleParam = new HashMap<>();
            querySingleParam.put("structIds", structIds);
            List<Map<String, Object>> singleList = drbfmSingleDao.querySingleList(querySingleParam);
            if (singleList != null && !singleList.isEmpty()) {
                return new JsonResult(false, "操作失败，此节点或子节点已创建部件分析项目，请删除部件分析项目后重试！");
            }
            drbfmTotalService.delAndUpdateParentChilds(delObj);
            return new JsonResult(true, "成功删除“" + delObj.getString("structName") + "”及子节点");
        } else {
            return new JsonResult(false, "操作失败，此节点不存在！");
        }
    }

    @RequestMapping("listStruct")
    @ResponseBody
    public List<JSONObject> listStruct(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String belongTotalId = RequestUtil.getString(request, "belongTotalId", "");
        if (StringUtils.isBlank(belongTotalId)) {
            return new ArrayList<>();
        }
        JSONObject param = new JSONObject();
        param.put("belongTotalId", belongTotalId);
        List<JSONObject> queryResults = drbfmTotalDao.queryStructByParam(param);
        if (queryResults == null || queryResults.isEmpty()) {
            queryResults = new ArrayList<>();
        } else {
            // 拼接每个节点关联的部件分析项目的信息
            Set<String> structIds = new HashSet<>();
            for (JSONObject oneData : queryResults) {
                structIds.add(oneData.getString("id"));
            }
            param.put("structIds", structIds);
            List<JSONObject> singleList = drbfmSingleDao.querySingleByStruct(param);
            Set<String> hasSingleStructIds = new HashSet<>();
            for (JSONObject oneSingle : singleList) {
                hasSingleStructIds.add(oneSingle.getString("structId"));
            }
            for (JSONObject oneData : queryResults) {
                oneData.put("hasSingle", hasSingleStructIds.contains(oneData.getString("id")));
            }
        }
        return queryResults;
    }

    // 是否风险分析
    @RequestMapping("needAnalysisCxx")
    public ModelAndView fxfxCxd(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/SOD/chuangxinxing.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping("showChuangxinxing")
    @ResponseBody
    public List<JSONObject> showCxd(HttpServletRequest request, HttpServletResponse response) {
        return drbfmTotalService.chuangxinxingPage(request);
    }

    @RequestMapping("needAnalysisYzx")
    public ModelAndView needAnalysisYzx(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/SOD/yanzhongxing.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping("showYanzhongxing")
    @ResponseBody
    public List<JSONObject> yanzhongxing(HttpServletRequest request, HttpServletResponse response) {
        return drbfmTotalService.yanzhongxingPage(request);
    }

    @RequestMapping("saveNeedAnalysis")
    @ResponseBody
    public JsonResult saveNeedAnalysis(HttpServletRequest request, HttpServletResponse response,
        @RequestBody String postBody) {
        JsonResult result = new JsonResult(true, "保存成功");
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String id = postBodyObj.getString("id");
        String cxx = postBodyObj.getString("cxx");
        String yzx = postBodyObj.getString("yzx");
        String csfxpg = postBodyObj.getString("csfxpg");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("chuangxinxingLevel", cxx);
        jsonObject.put("yanzhongxingLevel", yzx);
        jsonObject.put("csfxpg", csfxpg);
        if (csfxpg.contains("低")) {
            jsonObject.put("judgeNeedAnalyse", "否");
        } else {
            jsonObject.put("judgeNeedAnalyse", "是");
        }

        drbfmTotalDao.updateNeedAnalysis(jsonObject);
        return result;
    }

}
