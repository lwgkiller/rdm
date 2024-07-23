package com.redxun.standardManager.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.dao.StandardConfigDao;
import com.redxun.standardManager.core.dao.StandardDao;
import com.redxun.standardManager.core.manager.StandardManager;
import com.redxun.standardManager.core.manager.StandardSystemManager;
import com.redxun.standardManager.core.manager.SubManagerUserService;
import com.redxun.standardManager.core.util.StandardConstant;
import com.redxun.standardManager.core.util.StandardManagerUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/standardManager/core/standard/")
public class StandardController extends GenericController {
    @Autowired
    private StandardDao standardDao;
    @Autowired
    private StandardManager standardManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private StandardSystemManager standardSystemManager;
    @Autowired
    private SubManagerUserService subManagerUserService;
    @Autowired
    private StandardConfigDao standardConfigDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    // 标准管理框架页面
    @RequestMapping("management")
    public ModelAndView management(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = getPathView(request);
        boolean isGlNetwork = StandardManagerUtil.judgeGLNetwork(request);
        List<Map<String, Object>> systemCategorys = standardSystemManager.systemCategoryQuery();
/*        if (StandardManagerUtil.judgeGLNetwork(request)) {
            Iterator<Map<String, Object>> it = systemCategorys.iterator();
            while (it.hasNext()) {
                Map<String, Object> oneObj = it.next();
                if (oneObj.get("systemCategoryId") != null
                    && "JS".equalsIgnoreCase(oneObj.get("systemCategoryId").toString())) {
                    it.remove();
                }
            }
        }*/
        JSONArray systemCategorysJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(systemCategorys);
        mv.addObject("systemCategorysJsonArray", systemCategorysJsonArray);
        String webappName = WebAppUtil.getProperty("webappName", "rdm");
        // 从工作台传递的要查询的体系类别
        String systemCategory = RequestUtil.getString(request, "systemCategory", "");
        String activeTabIndex = "0";
        if (StringUtils.isBlank(systemCategory)) {
            if ("rdm".equalsIgnoreCase(webappName) && !isGlNetwork) {
                activeTabIndex = toQuerySystemIndex(systemCategorys, StandardConstant.SYSTEMCATEGORY_JS);
            }
            if ("sim".equalsIgnoreCase(webappName)) {
                activeTabIndex = toQuerySystemIndex(systemCategorys, StandardConstant.SYSTEMCATEGORY_GL);
            }
        } else {
            activeTabIndex = toQuerySystemIndex(systemCategorys, systemCategory);
        }
        mv.addObject("activeTabIndex", activeTabIndex);
        // 获取可能从工作台页面或者总览页面传递的参数
        String fieldId = RequestUtil.getString(request, "fieldId", "");
        String standardNumber = RequestUtil.getString(request, "standardNumber", "");
        String standardName = RequestUtil.getString(request, "standardName", "");
        String standardCategory = RequestUtil.getString(request, "standardCategory", "");
        String standardCategoryName = RequestUtil.getString(request, "standardCategoryName", "");
        if (StringUtils.isBlank(standardCategory) && StringUtils.isNotBlank(standardCategoryName)) {
            // 根据类别名字查找id
            Map<String, Object> param = new HashMap<>();
            param.put("standardCategoryName", standardCategoryName);
            JSONArray standardCategoryArr = standardConfigDao.queryStandardCategoryByName(param);
            if (standardCategoryArr != null && !standardCategoryArr.isEmpty()) {
                standardCategory = standardCategoryArr.getJSONObject(0).getString("id");
            }
        }
        String publishTimeFrom = RequestUtil.getString(request, "publishTimeFrom", "");
        String publishTimeTo = RequestUtil.getString(request, "publishTimeTo", "");
        String standardStatus = RequestUtil.getString(request, "standardStatus", "");
        mv.addObject("publishTimeFrom", publishTimeFrom);
        mv.addObject("publishTimeTo", publishTimeTo);
        mv.addObject("standardNumber", standardNumber);
        mv.addObject("standardName", standardName);
        mv.addObject("standardCategory", standardCategory);
        mv.addObject("standardStatus", standardStatus);
        if (StringUtils.isNotBlank(fieldId)) {
            mv.addObject("fieldId", fieldId);
        }
        return mv;
    }

    // 查询指定类别在数组中的索引
    private String toQuerySystemIndex(List<Map<String, Object>> systemCategorys, String targetSystemCategoryId) {
        for (int i = 0; i < systemCategorys.size(); i++) {
            Map<String, Object> onesystemCategory = systemCategorys.get(i);
            String systemCategoryId = onesystemCategory.get("systemCategoryId").toString();
            if (targetSystemCategoryId.equalsIgnoreCase(systemCategoryId)) {
                return "" + i;
            }
        }
        return "0";
    }

    // 标准管理tab页面
    @RequestMapping("tabPage")
    public ModelAndView tabPage(HttpServletRequest request, HttpServletResponse response) {
        boolean isGlNetwork = StandardManagerUtil.judgeGLNetwork(request);
        ModelAndView mv = getPathView(request);
        IUser currentUser = ContextUtil.getCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        // 职级信息
        List<Map<String, Object>> currentUserZJ = xcmgProjectOtherDao.queryUserZJ(params);
        JSONArray userZJJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserZJ);
        mv.addObject("currentUserZJ", userZJJsonArray);
        mv.addObject("tabName", RequestUtil.getString(request, "tabName"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        //马天宇:type是某个业务的id
        //String type = RequestUtil.getString(request, "type", "");
        //mv.addObject("type", type);
        //@lwgkiller改:将type改为业务标志，然后再传一个businessId代表业务id。
        //如果后期绑定业务暴增，还要进行改造，通过post传json过来，内包含回调地址，省去绑定页面的分支判断。
        String type = RequestUtil.getString(request, "type", "");
        String businessId = RequestUtil.getString(request, "businessId", "");
        mv.addObject("type", type).addObject("businessId", businessId);
        //@lwgkiller改-end
        // 获取可能从工作台页面传递的参数
        String standardNumber = RequestUtil.getString(request, "standardNumber", "");
        String standardName = RequestUtil.getString(request, "standardName", "");
        String standardCategory = RequestUtil.getString(request, "standardCategory", "");
        String publishTimeFrom = RequestUtil.getString(request, "publishTimeFrom", "");
        String publishTimeTo = RequestUtil.getString(request, "publishTimeTo", "");
        String standardStatus = RequestUtil.getString(request, "standardStatus", "");
        String fieldId = RequestUtil.getString(request, "fieldId", "");

        // 获取当前用户在标准子管理组中，标准类别与标准体系的数据
        JSONObject subManagerObj = subManagerUserService.querySubManagerSystemIds(ContextUtil.getCurrentUserId());
        mv.addObject("subManagerObj", subManagerObj);
        mv.addObject("standardNumber", standardNumber);
        mv.addObject("standardName", standardName);
        mv.addObject("standardCategory", standardCategory);
        mv.addObject("publishTimeFrom", publishTimeFrom);
        mv.addObject("publishTimeTo", publishTimeTo);
        mv.addObject("standardStatus", standardStatus);
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("fieldId", fieldId);
        mv.addObject("isGlNetwork", isGlNetwork);

        //判断是否是海外员工
        boolean isHw = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "海外员工");
        mv.addObject("isHw", isHw);
        return mv;
    }

    @RequestMapping("getStandardSelectInfos")
    @ResponseBody
    public Map<String, Object> getStandardSelectInfos(HttpServletRequest request, HttpServletResponse response) {
        return standardManager.getStandardSelectInfos(RequestUtil.getString(request, "systemCategoryId", ""));
    }

    // 标准新增和编辑页面
    @RequestMapping("edit")
    public ModelAndView editStandard(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = getPathView(request);
        String standardId = RequestUtil.getString(request, "standardId");
        String type = RequestUtil.getString(request, "type");
        String action = RequestUtil.getString(request, "action");
        JSONObject standardObj = new JSONObject();
        if (StringUtils.isNotBlank(standardId)) {
            Map<String, Object> standardInfo = standardManager.queryStandardById(standardId);
            standardObj = XcmgProjectUtil.convertMap2JsonObject(standardInfo);
        }
        mv.addObject("standardObj", standardObj);
        mv.addObject("action", action).addObject("type", type);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("systemCategoryId",
                RequestUtil.getString(request, "systemCategoryId", StandardConstant.SYSTEMCATEGORY_JS));
        String standardTaskId = RequestUtil.getString(request, "standardTaskId", "");
        mv.addObject("standardTaskId", standardTaskId);
        return mv;
    }

    /**
     * 根据Id查询单个标准详情
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("queryStandardById")
    public Map<String, Object> queryStandardById(HttpServletRequest request, HttpServletResponse response) {
        String standardId = RequestUtil.getString(request, "standardId");
        if (StringUtils.isBlank(standardId)) {
            logger.error("standardId is blank");
            return null;
        }
        return standardManager.queryStandardById(standardId);
    }

    // 标准列表查询
    @RequestMapping("queryList")
    @ResponseBody
    public JsonPageResult<?> queryStandardList(HttpServletRequest request, HttpServletResponse response) {
        return standardManager.queryStandardList(request);
    }

    // 标准批量导入
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        standardManager.importStandard(result, request);
        return result;
    }

    // 标准保存（包括新增、编辑）
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        standardManager.saveStandard(result, request);
        return result;
    }


    // 动态管理流程中废止并启用
    @RequestMapping("stopAndNew")
    @ResponseBody
    public JSONObject stopAndNew(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        standardManager.stopAndNew(result, request);
        return result;
    }

    // 标准删除
    @RequestMapping("delete")
    @ResponseBody
    public JSONObject deleteStandard(HttpServletRequest request, @RequestBody String requestBody,
                                     HttpServletResponse response) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(requestBody)) {
            logger.error("requestBody is blank");
            result.put("message", "删除失败，消息体为空！");
            return result;
        }
        JSONObject requestBodyObj = JSONObject.parseObject(requestBody);
        if (StringUtils.isBlank(requestBodyObj.getString("ids"))) {
            logger.error("ids is blank");
            result.put("message", "删除失败，主键为空！");
            return result;
        }
        String standardIds = requestBodyObj.getString("ids");
        standardManager.deleteStandard(result, standardIds);
        return result;
    }

    // 废止老标准，生成新标准
    @RequestMapping("stopOldPublishNew")
    @ResponseBody
    public JSONObject stopOldPublishNew(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        String standardId = RequestUtil.getString(request, "standardId");
        if (StringUtils.isBlank(standardId)) {
            logger.error("standardId is blank");
            result.put("message", "标准ID为空，操作失败！");
            return result;
        }
        standardManager.stopOldPublishNew(standardId, result);
        return result;
    }

    // 标准导出
    @RequestMapping("exportExcel")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        standardManager.exportExcel(request, response);
    }

    // 标准预览
    @RequestMapping("preview")
    public void preview(HttpServletRequest request, HttpServletResponse response) {
        standardManager.preview(request, response);
    }

    // 标准下载
    @RequestMapping("download")
    public ResponseEntity<byte[]> download(HttpServletRequest request, HttpServletResponse response) {
        String standardId = RequestUtil.getString(request, "standardId");
        String standardName = RequestUtil.getString(request, "standardName");
        String standardNumber = RequestUtil.getString(request, "standardNumber");
        return standardManager.download(request, standardId, standardName, standardNumber);
    }

    // 标准导入模板下载
    @RequestMapping("importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return standardManager.importTemplateDownload();
    }

    // 标准预览或者下载记录
    @RequestMapping("record")
    @ResponseBody
    public JSONObject record(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        String standardId = RequestUtil.getString(request, "standardId");
        String action = RequestUtil.getString(request, "action");
        if (StringUtils.isBlank(standardId)) {
            logger.error("standardId is blank");
            result.put("message", "主键为空！");
            return result;
        }
        if (StringUtils.isBlank(action)) {
            logger.error("action is blank");
            result.put("message", "操作类型为空！");
            return result;
        }
        standardManager.recordStandardOperate(result, standardId, action);
        return result;
    }

    // 标准借用页面
    @RequestMapping("borrowPage")
    public ModelAndView borrowPage(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = getPathView(request);
        String standardFromId = RequestUtil.getString(request, "standardFromId", "");

        List<Map<String, Object>> systemCategory = standardSystemManager.systemCategoryQuery();
        JSONArray systemCategoryArray = XcmgProjectUtil.convertListMap2JsonArrObject(systemCategory);
        List<Map<String, Object>> borrowStandardList = standardManager.queryStandardBorrowList(standardFromId);
        JSONArray borrowStandardArray = XcmgProjectUtil.convertListMap2JsonArrObject(borrowStandardList);

        mv.addObject("standardFromId", standardFromId);
        mv.addObject("systemCategoryArray", systemCategoryArray);
        mv.addObject("borrowStandardArray", borrowStandardArray);
        return mv;
    }

    @RequestMapping("borrowList")
    @ResponseBody
    public List<Map<String, Object>> borrowList(HttpServletRequest request, HttpServletResponse response) {
        String standardFromId = RequestUtil.getString(request, "standardFromId", "");
        if (StringUtils.isBlank(standardFromId)) {
            logger.error("standardFromId is blank!");
            return Collections.emptyList();
        }
        List<Map<String, Object>> borrowStandardList = standardManager.queryStandardBorrowList(standardFromId);
        return borrowStandardList;
    }

    @RequestMapping("addBorrow")
    @ResponseBody
    public JSONObject addBorrow(HttpServletRequest request, HttpServletResponse response,
                                @RequestBody String requestBody) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(requestBody)) {
            logger.error("requestBody is blank!");
            result.put("message", "操作失败，请求参数为空！");
            return result;
        }
        JSONObject postBodyObj = JSONObject.parseObject(requestBody);
        standardManager.addBorrow(postBodyObj, result);
        return result;
    }

    @RequestMapping("callBackBorrow")
    @ResponseBody
    public JSONObject callBackBorrow(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        String standardToId = RequestUtil.getString(request, "standardToId", "");
        if (StringUtils.isBlank(standardToId)) {
            logger.error("standardToId is blank!");
            result.put("message", "操作失败，请求参数为空！");
            return result;
        }
        standardManager.callBackBorrow(standardToId, result);
        return result;
    }

    @RequestMapping("getSolutions")
    @ResponseBody
    public List<Map<String, String>> getSolutions(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        String standardTaskKeys = WebAppUtil.getProperty("standardTaskKey");
        String[] taskKeys = standardTaskKeys.split(",");
        params.put("keys", Arrays.asList(taskKeys));
        return xcmgProjectOtherDao.getSolutions(params);
    }

    // 标准公开管理页面
    @RequestMapping("publicListPage")
    public ModelAndView publicListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "standardManager/core/standardPublicList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());

        String standardNumber = RequestUtil.getString(request, "standardNumber", "");
        String standardName = RequestUtil.getString(request, "standardName", "");
        String companyName = RequestUtil.getString(request, "companyName", "");
        mv.addObject("standardNumber", standardNumber);
        mv.addObject("standardName", standardName);
        mv.addObject("companyName", companyName);
        return mv;
    }

    // 标准新增和编辑页面
    @RequestMapping("publicEditPage")
    public ModelAndView publicEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "standardManager/core/standardPublicEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String standardId = RequestUtil.getString(request, "standardId");
        String action = RequestUtil.getString(request, "action");
        JSONObject standardObj = new JSONObject();
        if (StringUtils.isNotBlank(standardId)) {
            Map<String, Object> standardInfo = standardManager.queryPublicStandardById(standardId);
            standardObj = XcmgProjectUtil.convertMap2JsonObject(standardInfo);
        }
        mv.addObject("standardObj", standardObj);
        mv.addObject("action", action);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    /**
     * 根据Id查询单个标准详情
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("queryPublicStandardById")
    public Map<String, Object> queryPublicStandardById(HttpServletRequest request, HttpServletResponse response) {
        String standardId = RequestUtil.getString(request, "standardId");
        if (StringUtils.isBlank(standardId)) {
            logger.error("standardId is blank");
            return null;
        }
        return standardManager.queryPublicStandardById(standardId);
    }

    // 标准列表查询
    @RequestMapping("queryPublicList")
    @ResponseBody
    public JsonPageResult<?> queryPublicList(HttpServletRequest request, HttpServletResponse response) {
        return standardManager.queryPublicStandardList(request);
    }

    // 标准保存（包括新增、编辑）
    @RequestMapping("savePublic")
    @ResponseBody
    public JSONObject savePublic(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        standardManager.savePublicStandard(result, request);
        return result;
    }

    // 标准删除
    @RequestMapping("deletePublic")
    @ResponseBody
    public JSONObject deletePublic(HttpServletRequest request, @RequestBody String requestBody,
                                   HttpServletResponse response) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(requestBody)) {
            logger.error("requestBody is blank");
            result.put("message", "删除失败，消息体为空！");
            return result;
        }
        JSONObject requestBodyObj = JSONObject.parseObject(requestBody);
        if (StringUtils.isBlank(requestBodyObj.getString("ids"))) {
            logger.error("ids is blank");
            result.put("message", "删除失败，主键为空！");
            return result;
        }
        String standardIds = requestBodyObj.getString("ids");
        standardManager.deletePublicStandard(result, standardIds);
        return result;
    }

    // 标准导出
    @RequestMapping("exportPublicExcel")
    public void exportPublicExcel(HttpServletRequest request, HttpServletResponse response) {
        standardManager.exportPublicExcel(request, response);
    }

    // 标准预览
    @RequestMapping("publicPreview")
    public void publicPreview(HttpServletRequest request, HttpServletResponse response) {
        standardManager.publicPreview(request, response);
    }

    // 标准下载
    @RequestMapping("publicDownload")
    public ResponseEntity<byte[]> publicDownload(HttpServletRequest request, HttpServletResponse response) {
        String standardId = RequestUtil.getString(request, "standardId");
        String standardName = RequestUtil.getString(request, "standardName");
        String standardNumber = RequestUtil.getString(request, "standardNumber");
        return standardManager.publicDownload(request, standardId, standardName, standardNumber);
    }


    @RequestMapping("saveCollect")
    @ResponseBody
    public JsonResult saveCollect(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "收藏成功");
        try {
            JSONObject formDataJson = new JSONObject();
            String userId = RequestUtil.getString(request, "userId");
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            formDataJson.put("belongId", userId);
            List<JSONObject> jsbzLists = standardDao.queryCollect(formDataJson);
            for (String standardId : ids) {
                boolean link = true;
                for (JSONObject jsbzList : jsbzLists) {
                    if (jsbzList.getString("standardId").equals(standardId)) {
                        link = false;
                        break;
                    }
                }
                if (link) {
                    formDataJson.put("standardId", standardId);
                    standardManager.createCollect(formDataJson);
                }

            }

        } catch (Exception e) {
            logger.error("Exception in save Cnsx");
            result.setSuccess(false);
            result.setMessage("Exception in save Cnsx");
            return result;
        }
        return result;
    }

    @RequestMapping("deleteCollect")
    @ResponseBody
    public JsonResult deleteCollect(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return standardManager.deleteCollect(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteCollect", e);
            return new JsonResult(false, e.getMessage());
        }
    }
    @RequestMapping("autoGenerateStandardNum")
    @ResponseBody
    public JsonResult autoGenerateStandardNum(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "操作成功");
        String systemId = RequestUtil.getString(request, "systemId", "");
        if (systemId.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("体系编号为空！");
            return result;
        }
        return standardManager.autoGenerateStandardNum(result,systemId);
    }
}
