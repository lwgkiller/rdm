
package com.redxun.rdmZhgl.core.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmZhgl.core.dao.TxlDao;
import com.redxun.rdmZhgl.core.service.TxlManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;

/**
 * 通讯录管理
 *
 * @author x
 */
@Controller
@RequestMapping("/zhgl/core/txl/")
public class TxlController {
    private Logger logger = LoggerFactory.getLogger(TxlController.class);

    @Autowired
    private TxlManager txlManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private TxlDao txlDao;

    @RequestMapping("txlListPage")
    public ModelAndView txlListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/txlList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 是否是通讯录管理人员，可以进行操作
        boolean isMgrUser = false;
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUser().getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        for (Map<String, Object> oneRole : currentUserRoles) {
            if (oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-LEADER")
                || oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-BELONG")) {
                if ("通讯录管理人员".equalsIgnoreCase(oneRole.get("NAME_").toString())) {
                    isMgrUser = true;
                    break;
                }
            }
        }
        mv.addObject("isMgrUser", isMgrUser);
        return mv;
    }

    @RequestMapping("txlEditPage")
    public ModelAndView txlEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/txlEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = RequestUtil.getString(request, "id", "");
        JSONObject txlObj = new JSONObject();
        List<JSONObject> deptNameLevelOneList = Collections.emptyList();
        List<JSONObject> deptNameLevelTwoList = Collections.emptyList();
        if (StringUtils.isNotBlank(id)) {
            txlObj = txlDao.queryTxlById(id);
            String companyName = txlObj.getString("companyName");
            if (StringUtils.isNotBlank(companyName)) {
                Map<String, Object> params = new HashMap<>();
                params.put("companyName", companyName);
                deptNameLevelOneList = txlDao.queryDeptNameLevelOne(params);
                if (deptNameLevelOneList == null) {
                    deptNameLevelOneList = Collections.emptyList();
                }

                String deptNameLevelOne = txlObj.getString("deptNameLevelOne");
                if (StringUtils.isNotBlank(deptNameLevelOne)) {
                    params.clear();
                    params.put("companyName", companyName);
                    params.put("deptNameLevelOne", deptNameLevelOne);
                    deptNameLevelTwoList = txlDao.queryDeptNameLevelTwo(params);
                    if (deptNameLevelTwoList == null) {
                        deptNameLevelTwoList = Collections.emptyList();
                    }
                }
            }
        }
        mv.addObject("deptNameLevelOneList", deptNameLevelOneList);
        mv.addObject("deptNameLevelTwoList", deptNameLevelTwoList);
        mv.addObject("txlObj", txlObj);
        mv.addObject("id", id);
        return mv;
    }

    @RequestMapping("txlList")
    @ResponseBody
    public JsonPageResult<?> txlList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return txlManager.queryTxlList(request);
    }

    @RequestMapping("saveTxl")
    @ResponseBody
    public JsonResult saveTxl(HttpServletRequest request, @RequestBody String postDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功！");
        if (StringUtils.isBlank(postDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("信息为空，保存失败！");
            return result;
        }
        txlManager.saveTxl(result, postDataStr);
        return result;
    }

    @RequestMapping("queryCompany")
    @ResponseBody
    public List<JSONObject> queryCompany(HttpServletRequest request, HttpServletResponse response) {
        return txlDao.queryCompany();
    }

    @RequestMapping("queryDeptNameLevelOne")
    @ResponseBody
    public List<JSONObject> queryDeptNameLevelOne(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> params = new HashMap<>();
        String companyName = RequestUtil.getString(request, "companyName", "");
        if (StringUtils.isBlank(companyName)) {
            return Collections.EMPTY_LIST;
        }
        params.put("companyName", companyName);
        return txlDao.queryDeptNameLevelOne(params);
    }

    @RequestMapping("queryDeptNameLevelTwo")
    @ResponseBody
    public List<JSONObject> queryDeptNameLevelTwo(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> params = new HashMap<>();
        String companyName = RequestUtil.getString(request, "companyName", "");
        String deptNameLevelOne = RequestUtil.getString(request, "deptNameLevelOne", "");
        if (StringUtils.isBlank(companyName) || StringUtils.isBlank(deptNameLevelOne)) {
            return Collections.EMPTY_LIST;
        }
        params.put("companyName", companyName);
        params.put("deptNameLevelOne", deptNameLevelOne);
        return txlDao.queryDeptNameLevelTwo(params);
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
        String txlIds = requestBodyObj.getString("ids");
        txlManager.deleteTxl(result, txlIds);
        return result;
    }
}
