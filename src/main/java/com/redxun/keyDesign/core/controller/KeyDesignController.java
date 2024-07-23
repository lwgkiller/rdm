package com.redxun.keyDesign.core.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.keyDesign.core.service.KeyDesignService;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/key/core/keyDesign/")
public class KeyDesignController extends GenericController {
    @Autowired
    private KeyDesignService keyDesignService;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    // 标准体系管理框架页面
    @RequestMapping("management")
    public ModelAndView management(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "keyDesign/core/keyDesign.jsp";
        String codeId = RequestUtil.getString(request, "codeId");
        ModelAndView mv = new ModelAndView(jspPath);
        boolean isGlr = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "关键零部件管理人");
        mv.addObject("isGlr", isGlr);
        mv.addObject("codeId",codeId);
        return mv;
    }

    // 编辑节点
    @RequestMapping("treeEdit")
    public ModelAndView treeEdit(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "keyDesign/core/keyTreeEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    // 根据体系类别查询体系
    @RequestMapping("treeQuery")
    @ResponseBody
    public JSONArray treeQuery(HttpServletRequest request, HttpServletResponse response) {
        String codeId = RequestUtil.getString(request, "codeId");
        List<Map<String, Object>> systemInfos = keyDesignService.systemQuery(codeId);
        JSONArray systemArray = XcmgProjectUtil.convertListMap2JsonArrObject(systemInfos);
        return systemArray;
    }

    // 保存体系树
    @RequestMapping("treeSave")
    @ResponseBody
    public JSONObject treeSave(HttpServletRequest request, @RequestBody String changedDataStr,
        HttpServletResponse response) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(changedDataStr)) {
            logger.warn("requestBody is blank");
            result.put("message", "数据为空");
            return result;
        }
        keyDesignService.treeSave(result, changedDataStr);
        return result;
    }

    // 查询标准体系下标准的个数
    @RequestMapping("queryBySystemIds")
    @ResponseBody
    public JSONObject queryStandardBySystemIds(HttpServletRequest request, @RequestBody String belong,
        HttpServletResponse response) {
        JSONObject result = new JSONObject();
        JSONObject belongbj = JSON.parseObject(belong);
        keyDesignService.queryBySystemIds(result, belongbj);
        return result;
    }


    @RequestMapping("queryCategory")
    @ResponseBody
    public List<Map<String, Object>> querySystemCategory(HttpServletRequest request, HttpServletResponse response) {
        List<Map<String, Object>> systemCategorys = keyDesignService.systemCategoryQuery();
        /*if (StandardManagerUtil.judgeGLNetwork(request)) {
            Iterator<Map<String, Object>> it = systemCategorys.iterator();
            while (it.hasNext()) {
                Map<String, Object> oneObj = it.next();
                if (oneObj.get("systemCategoryId") != null
                    && "JS".equalsIgnoreCase(oneObj.get("systemCategoryId").toString())) {
                    it.remove();
                }
            }
        }*/
        return systemCategorys;
    }
}
