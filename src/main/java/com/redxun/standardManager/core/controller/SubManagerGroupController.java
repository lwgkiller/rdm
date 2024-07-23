package com.redxun.standardManager.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.StringUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.manager.StandardSystemManager;
import com.redxun.standardManager.core.manager.SubManagerGroupService;
import com.redxun.standardManager.core.util.StandardConstant;
import com.redxun.standardManager.core.util.StandardManagerUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zz 标准子管理员用户组
 */
@Controller
@RequestMapping("/standardManager/core/subManagerGroup/")
public class SubManagerGroupController {
    private static final Logger logger = LoggerFactory.getLogger(SubManagerGroupController.class);
    @Resource
    private SubManagerGroupService subManagerGroupService;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private StandardSystemManager standardSystemManager;

    /**
     * 获取列表页面
     */
    @RequestMapping("groupListPage")
    public ModelAndView getStorageListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "standardManager/core/subManagerGroupList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        String webappName= WebAppUtil.getProperty("webappName","rdm");
        String systemCategoryValue=StandardConstant.SYSTEMCATEGORY_GL;
        if ("rdm".equalsIgnoreCase(webappName)&& !StandardManagerUtil.judgeGLNetwork(request)) {
            systemCategoryValue = StandardConstant.SYSTEMCATEGORY_JS;
        }
        mv.addObject("systemCategoryValue",systemCategoryValue);
        return mv;
    }

    @RequestMapping("groupEditPage")
    public ModelAndView getStorageEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "standardManager/core/subManagerGroupEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        JSONObject applyObj = new JSONObject();
        JSONArray systemArray =new JSONArray();
        // 1.查看、或者修改 根据id查询 预约单信息
        if (StandardConstant.FORM_EDIT.equals(action) || StandardConstant.FORM_VIEW.equals(action)) {
            JSONObject paramJSON = new JSONObject();
            paramJSON.put("id", id);
            applyObj = subManagerGroupService.getObject(paramJSON);
            List<Map<String, Object>> systemInfos =
                standardSystemManager.systemQuery(applyObj.getString("systemCategoryId"));
            systemArray = XcmgProjectUtil.convertListMap2JsonArrObject(systemInfos);
        }
        mv.addObject("systemArray", systemArray);
        mv.addObject("action", action);
        mv.addObject("applyObj", applyObj);

        // 查询当前用户角色信息中包含的“xx标准管理人员”
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        JSONArray systemCategoryIdArr = new JSONArray();
        for (int i = 0; i < userRolesJsonArray.size(); i++) {
            JSONObject object = userRolesJsonArray.getJSONObject(i);
            String relTypeKey = object.getString("REL_TYPE_KEY_");
            if (relTypeKey.equalsIgnoreCase("GROUP-USER-LEADER") || relTypeKey.equalsIgnoreCase("GROUP-USER-BELONG")) {
                String relTypeName = object.getString("NAME_");
                if (relTypeName.contains("管理标准管理人员")) {
                    systemCategoryIdArr.add(StandardConstant.SYSTEMCATEGORY_GL);
                }
                if (relTypeName.contains("技术标准管理人员")) {
                    systemCategoryIdArr.add(StandardConstant.SYSTEMCATEGORY_JS);
                }
                if (relTypeName.contains("内控标准管理人员")) {
                    systemCategoryIdArr.add(StandardConstant.SYSTEMCATEGORY_NK);
                }
            }
        }
        mv.addObject("systemCategoryIdSet", systemCategoryIdArr);
        return mv;
    }

    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if (StringUtil.isEmpty(id)) {
            resultJSON = subManagerGroupService.add(request);
        } else {
            resultJSON = subManagerGroupService.update(request);
        }
        return resultJSON;
    }

    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = subManagerGroupService.remove(request);
        return resultJSON;
    }

    @RequestMapping("groupList")
    @ResponseBody
    public JsonPageResult<?> groupList(HttpServletRequest request, HttpServletResponse response) {
        return subManagerGroupService.query(request);
    }
}
