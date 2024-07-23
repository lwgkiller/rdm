package com.redxun.standardManager.core.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.json.JsonPageResult;
import com.redxun.standardManager.core.util.StandardManagerUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.StringUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.standardManager.core.manager.StandardFieldManagerService;
import com.redxun.standardManager.core.manager.StandardSystemManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/standardManager/core/standardField/")
public class StandardFieldController extends GenericController {
    @Autowired
    private StandardFieldManagerService standardFieldManagerService;
    @Autowired
    private StandardSystemManager standardSystemManager;

    // 专业领域列表界面
    @RequestMapping("listPage")
    public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new ModelAndView("/standardManager/core/fieldList.jsp");
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    // 新增或者编辑专业领域
    @RequestMapping("fieldEditPage")
    public ModelAndView getStorageEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "standardManager/core/fieldEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = RequestUtil.getString(request, "id");
        JSONObject fieldObj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            JSONObject paramJSON = new JSONObject();
            paramJSON.put("id", id);
            List<JSONObject> fieldObjs = standardFieldManagerService.queryFieldObject(paramJSON);
            if (fieldObjs != null && !fieldObjs.isEmpty()) {
                fieldObj = fieldObjs.get(0);
            }
        }
        mv.addObject("fieldObj", fieldObj);
        List<Map<String, Object>> systemCategoryInfos = standardSystemManager.systemCategoryQuery();
        if (StandardManagerUtil.judgeGLNetwork(request)) {
            Iterator<Map<String, Object>> it = systemCategoryInfos.iterator();
            while (it.hasNext()) {
                Map<String, Object> oneObj = it.next();
                if (oneObj.get("systemCategoryId") != null
                    && "JS".equalsIgnoreCase(oneObj.get("systemCategoryId").toString())) {
                    it.remove();
                }
            }
        }
        JSONArray systemCategoryArr = XcmgProjectUtil.convertListMap2JsonArrObject(systemCategoryInfos);
        mv.addObject("systemCategoryArr", systemCategoryArr);
        return mv;
    }

    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("fieldId");
        JSONObject resultJSON = null;
        if (StringUtil.isEmpty(id)) {
            resultJSON = standardFieldManagerService.addField(request);
        } else {
            resultJSON = standardFieldManagerService.updateField(request);
        }
        return resultJSON;
    }

    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = standardFieldManagerService.deleteField(request);
        return resultJSON;
    }

    @RequestMapping("fieldList")
    @ResponseBody
    public List<JSONObject> fieldList(HttpServletRequest request, HttpServletResponse response) {
        JSONObject paramJSON = new JSONObject();
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    paramJSON.put(name, value);
                }
            }
        }
        return standardFieldManagerService.queryFieldObject(paramJSON);
    }

    /**
     * 查询领域并统计出各个领域的标准的个数
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("fieldStandardCountList")
    @ResponseBody
    public List<JSONObject> fieldStandardCountList(HttpServletRequest request, HttpServletResponse response) {
        JSONObject paramJSON = new JSONObject();
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    paramJSON.put(name, value);
                }
            }
        }
        return standardFieldManagerService.fieldStandardCountList(paramJSON);
    }

    @RequestMapping("fieldUserList")
    @ResponseBody
    public JsonPageResult<?> fieldUserList(HttpServletRequest request, HttpServletResponse response) {
        return standardFieldManagerService.fieldUserList(request);
    }

    @RequestMapping("fieldUserSave")
    @ResponseBody
    public JSONObject fieldUserSave(HttpServletRequest request, @RequestBody String gridDataStr,
        HttpServletResponse response) {
        JSONObject result = new JSONObject();
        String fieldId = RequestUtil.getString(request, "fieldId", "");
        standardFieldManagerService.fieldUserSave(result, gridDataStr, fieldId);
        if (result.get("message") == null) {
            result.put("message", "数据保存成功！");
        }
        return result;
    }

    @RequestMapping("fieldUserListPage")
    public ModelAndView fieldUserListPage(HttpServletRequest request, HttpServletResponse response) {
        String fieldId = RequestUtil.getString(request, "fieldId", "");
        String jspPath = "standardManager/core/fieldUserList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("fieldId", fieldId);
        return mv;
    }
}
