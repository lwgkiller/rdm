package com.redxun.rdmCommon.core.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.standardManager.core.util.StandardManagerUtil;
import com.redxun.sys.core.entity.SysMenu;
import com.redxun.sys.core.manager.SysMenuManager;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.entity.BpmInst;
import com.redxun.bpm.core.entity.BpmSolution;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmSolutionManager;
import com.redxun.bpm.form.api.FormHandlerFactory;
import com.redxun.bpm.form.api.IFormHandler;
import com.redxun.bpm.form.entity.FormModel;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.digitization.core.service.RdmI18nUtil;
import com.redxun.rdmCommon.core.dao.RdmHomeDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.IndexManager;
import com.redxun.rdmCommon.core.manager.RdmHomeManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.menu.UIMenu;
import com.redxun.saweb.util.RequestUtil;

/**
 * @author zhangzhen
 */
@Controller
@RequestMapping("/rdmHome/core")
public class RdmHomeController {
    private Logger logger = LogManager.getLogger(RdmHomeController.class);
    @Resource
    private RdmHomeManager rdmHomeManager;
    @Resource
    private IndexManager indexManager;
    @Resource
    private RdmHomeDao rdmHomeDao;
    @Resource
    BpmInstManager bpmInstManager;
    @Resource
    BpmSolutionManager bpmSolutionManager;
    @Resource
    FormHandlerFactory formHandlerFactory;
    @Resource
    private CommonInfoManager commonInfoManager;
    @Autowired
    SysMenuManager sysMenuManager;

    //todo:mark一下：这里指定不同的主页
    @RequestMapping("/rdmHomePage")
    public ModelAndView rdmHomePage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmCommon/rdmHome.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        boolean netType = StandardManagerUtil.judgeGLNetwork(request);
        List<UIMenu> subSysList = indexManager.getRootMenu(null, netType);
        JSONArray subSysArray = new JSONArray();
        for (UIMenu uiMenu : subSysList) {
            if (!"首页".equalsIgnoreCase(uiMenu.getName())) {
                subSysArray.add(uiMenu);
            }
        }
        UIMenu todoMenu = new UIMenu();
        todoMenu.setName("待办");
        if (subSysArray.size() < 3) {
            subSysArray.add(todoMenu);
        } else {
            subSysArray.add(2, todoMenu);
        }
        // 国际化
        RdmI18nUtil.rootMenuI18n(request, subSysArray);
        mv.addObject("subSysList", subSysArray);
        return mv;
    }

    //..
    @RequestMapping("/rdmHome2024Page")
    public ModelAndView rdmHome2024Page(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmCommon/rdmHome2024.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        boolean netType = StandardManagerUtil.judgeGLNetwork(request);
        List<UIMenu> subSysList = indexManager.getRootMenu(null, netType);
        JSONArray subSysArray = new JSONArray();
        for (UIMenu uiMenu : subSysList) {
            if (!"首页".equalsIgnoreCase(uiMenu.getName())) {
                subSysArray.add(uiMenu);
            }
        }
        UIMenu todoMenu = new UIMenu();
        todoMenu.setName("待办");
        if (subSysArray.size() < 3) {
            subSysArray.add(todoMenu);
        } else {
            subSysArray.add(2, todoMenu);
        }
        // 国际化
        RdmI18nUtil.rootMenuI18n(request, subSysArray);
        mv.addObject("subSysList", subSysArray);
        return mv;
    }

    @ResponseBody
    @RequestMapping("/getMenusBySysId")
    public List<UIMenu> getMenusByMenuId(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String sysId = request.getParameter("sysId");
        boolean netType = StandardManagerUtil.judgeGLNetwork(request);
        List list = indexManager.getBySysId(sysId, null, true, netType);
        String language = RdmI18nUtil.toGetLanguage(request);
        for (Object obj : list) {
            UIMenu subMenu = (UIMenu) obj;
            indexManager.callBackMenu(subMenu, language);
        }
        return list;
    }

    @RequestMapping("/menuWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmCommon/rdmMenuWindow.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping("/menuNo")
    public ModelAndView menuNo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmCommon/rdmMenuNo.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping("/workStatusDetailPage")
    public ModelAndView workStatusDetailPage(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String leaderUserId = RequestUtil.getString(request, "leaderUserId", "");
        String leaderUserName = RequestUtil.getString(request, "leaderUserName", "");
        String jspPath = "rdmCommon/rdmHomeWorkStatusDetail.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("leaderUserId", leaderUserId);
        mv.addObject("leaderUserName", leaderUserName);
        mv.addObject("todayStr", DateFormatUtil.getNowByString("yyyy-MM-dd"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    @RequestMapping("/rdmHomeTabPage")
    public ModelAndView rdmHomeTabPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String name = RequestUtil.getString(request, "name", "");
        String jspPath = "rdmCommon/rdmMenuNo.jsp";
        if (StringUtils.isNotBlank(name)) {
            switch (name) {
                case "todo":
                    jspPath = "rdmCommon/rdmHomeTodo.jsp";
                    break;
                case "message":
                    jspPath = "rdmCommon/rdmHomeMessage.jsp";
                    break;
                case "notice":
                    jspPath = "rdmCommon/rdmHomeNotice.jsp";
                    break;
                case "workStatus":
                    jspPath = "rdmCommon/rdmHomeWorkStatus.jsp";
                    break;
                case "bbsNotice":
                    jspPath = "rdmCommon/rdmHomeBbsNotice.jsp";
                    break;
                case "hasDone":
                    jspPath = "rdmCommon/rdmHomeHasDone.jsp";
                    break;
                case "projectDelay":
                    jspPath = "rdmCommon/rdmHomeProjectDelay.jsp";
                    break;
                case "delivery":
                    jspPath = "rdmCommon/rdmHomeDelivery.jsp";
                    break;
            }
        }

        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject resultJson = commonInfoManager.hasPermission("JSZXXMGLRY");
        mv.addObject("permission",
                resultJson.getBoolean("JSZXXMGLRY") || resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)
                        || "admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        return mv;
    }

    @ResponseBody
    @RequestMapping("/rdmHomeTabContent")
    public JsonPageResult<?> rdmHomeTabContent(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String name = RequestUtil.getString(request, "name", "");
        switch (name) {
            case "todo":
                return rdmHomeManager.queryAndFilterCurrentUserTodos(request, true);
            case "message":
                return rdmHomeManager.queryUserMessage(request, "", true);
            case "notice":
                return rdmHomeManager.queryUserNotice(request, "", true);
            case "workStatus":
                return rdmHomeManager.queryWorkStatusNow(request);
            case "bbsNotice":
                return rdmHomeManager.queryBbsNotice(request, "", true);
            case "delivery":
                return rdmHomeManager.queryDelivery(request, true);
        }
        return new JsonPageResult(true);
    }

    @ResponseBody
    @RequestMapping("/saveWorkStatus")
    public JsonResult saveWorkStatus(HttpServletRequest request, HttpServletResponse response,
                                     @RequestBody String requestBody) {
        JsonResult result = new JsonResult(true, "保存成功！");
        if (StringUtils.isBlank(requestBody)) {
            return result;
        }
        rdmHomeManager.saveOrUpdateWorkStatus(result, requestBody);
        return result;
    }

    @ResponseBody
    @RequestMapping("/queryWorkStatusOneUser")
    public List<JSONObject> queryWorkStatusOneUser(HttpServletRequest request, HttpServletResponse response) {
        String userId = RequestUtil.getString(request, "userId", "");
        String dayStr = RequestUtil.getString(request, "dayStr", "");
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(dayStr)) {
            return null;
        }
        return rdmHomeManager.queryWorkStatusOneUser(userId, dayStr);
    }

    @ResponseBody
    @RequestMapping("/sendWorkStatusDDNotice")
    public JsonResult sendWorkStatusDDNotice(HttpServletRequest request, HttpServletResponse response,
                                             @RequestBody String requestBody) {
        JsonResult result = new JsonResult(true, "发送成功！");
        String leaderUserId = RequestUtil.getString(request, "leaderUserId", "");
        if (StringUtils.isBlank(leaderUserId) || StringUtils.isBlank(requestBody)) {
            result.setSuccess(false);
            result.setMessage("发送失败");
            return result;
        }
        JSONArray changeGridDataJson = JSONObject.parseArray(requestBody);
        List<JSONObject> noticeList = changeGridDataJson.toJavaList(JSONObject.class);
        Collections.sort(noticeList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                return o1.getString("startTime").compareTo(o2.getString("startTime"));
            }
        });
        rdmHomeManager.sendOneLeaderScheduleNotice(leaderUserId, noticeList, result);
        return result;
    }

    @ResponseBody
    @RequestMapping("/readScheduleNotice")
    public JsonResult readScheduleNotice(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "操作成功！");
        JSONObject param = new JSONObject();
        param.put("leaderUserId", ContextUtil.getCurrentUserId());
        param.put("dayStr", DateFormatUtil.getNowByString("yyyy-MM-dd"));
        rdmHomeDao.updateIfNotice(param);
        return result;
    }

    @ResponseBody
    @RequestMapping("/rdmHomeTabNum")
    public JSONObject rdmHomeTabNum(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject result = new JSONObject();
        String tabNames = RequestUtil.getString(request, "names", "");
        result.put("todo", 0);
        result.put("message", 0);
        result.put("notice", 0);
        result.put("bbsNoticeNum", 0);
        if (StringUtils.isNotBlank(tabNames)) {
            String[] arr = tabNames.split(",", -1);
            for (int index = 0; index < arr.length; index++) {
                if (StringUtils.isNotBlank(arr[index])) {
                    switch (arr[index]) {
                        case "todo":
                            JsonPageResult tasks = rdmHomeManager.queryAndFilterCurrentUserTodos(request, false);
                            if (tasks != null && tasks.getData() != null) {
                                result.put("todo", tasks.getData().size());
                            }
                            break;
                        case "message":
                            JsonPageResult messages = rdmHomeManager.queryUserMessage(request, "0", false);
                            if (messages != null && messages.getData() != null) {
                                result.put("message", messages.getData().size());
                            }
                            break;
                        case "notice":
                            JsonPageResult notices = rdmHomeManager.queryUserNotice(request, "0", false);
                            if (notices != null && notices.getData() != null) {
                                result.put("notice", notices.getData().size());
                            }
                            break;
                        case "bbsNotice":
                            JsonPageResult bbs = rdmHomeManager.queryBbsNotice(request, "0", false);
                            if (bbs != null && bbs.getData() != null) {
                                result.put("bbsNoticeNum", bbs.getData().size());
                            }
                            break;
                    }
                }
            }
        }
        return result;
    }

    @RequestMapping("/bpmInstDetail")
    public ModelAndView bpmInstDetail(HttpServletRequest request) throws Exception {
        // 从流程草稿中启动流程
        String instId = request.getParameter("instId");

        ModelAndView mv = new ModelAndView("bpm/core/bpmInstDetail.jsp");
        BpmSolution bpmSolution = null;
        BpmInst bpmInst = bpmInstManager.get(instId);
        bpmSolution = bpmSolutionManager.get(bpmInst.getSolId());
        String solId = bpmSolution.getSolId();

        // ////////////////////////开始--用于控制开始节点的相关配置////////////////////////////////////////
        // 取得流程的全局配置
        // mv.addObject("bpmSolution", bpmSolution);
        mv.addObject("instId", instId);

        // 展示当前表单
        IFormHandler formHandler = formHandlerFactory.getByType(IFormHandler.FORM_TYPE_PC);
        List<FormModel> formModels = formHandler.getStartForm(solId, instId, null);
        if (formModels != null && !formModels.isEmpty() && StringUtils.isNotBlank(formModels.get(0).getContent())) {
            formModels.get(0).setContent(formModels.get(0).getContent() + "&action=detail");
        }
        mv.addObject("formModels", formModels);
        return mv;
    }

    @ResponseBody
    @RequestMapping("/menuSearch")
    public List<JSONObject> menuSearch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<JSONObject> result = new ArrayList<>();
        String menuName = RequestUtil.getString(request, "key", "");
        if (StringUtils.isBlank(menuName)) {
            return result;
        }
        boolean netType = StandardManagerUtil.judgeGLNetwork(request);
        List<UIMenu> list = indexManager.getBySysId(null, menuName, false, netType);
        for (UIMenu menu : list) {
            List<SysMenu> listChild = sysMenuManager.getByParentId(menu.getId());
            boolean isShow = true;
            for (SysMenu sysMenu : listChild) {
                if (sysMenu.getIsBtnMenu().equalsIgnoreCase("NO")) {
                    isShow = false;
                    break;
                }
            }
            if (isShow) {
                JSONObject menuObj = new JSONObject();
                menuObj.put("menuId", menu.getMenuId() + "_" + menu.getSysId());
                menuObj.put("menuName", menu.getName());
                result.add(menuObj);
            }
            //@lwgkiller:以下是张振写的，以前CHILDS_属性没修正，现在修正了，造成有按钮菜单的getChilds！=0，也不显示因此修改为以上代码
//            if (menu.getChilds() == 0) {
//                JSONObject menuObj = new JSONObject();
//                menuObj.put("menuId", menu.getMenuId() + "_" + menu.getSysId());
//                menuObj.put("menuName", menu.getName());
//                result.add(menuObj);
//            }
        }
        return result;
    }

    //..添加自己最近的记录，重复的不添加，已添加的保留X条
    @ResponseBody
    @RequestMapping("/addMyMenuRecent")
    public void addMyMenuRecent(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<JSONObject> result = new ArrayList<>();
        String menuIdSysId = RequestUtil.getString(request, "menuIdSysId", "");
        if (!StringUtils.isBlank(menuIdSysId)) {
            commonInfoManager.addMyMenuRecent(menuIdSysId);
        }
    }

    //..找自己的访问记录
    @ResponseBody
    @RequestMapping("/searchMyMenuRecent")
    public List<JSONObject> searchMyMenuRecent(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject params = new JSONObject();
        params.put("clickUserId", ContextUtil.getCurrentUserId());
        List<JSONObject> myMenuRecentList = commonInfoManager.getMyMenuRecent(params);
        for (JSONObject jsonObject : myMenuRecentList) {
            jsonObject.put("menuId", jsonObject.getString("menuId") + "_" + jsonObject.getString("sysId"));
        }
        return myMenuRecentList;
    }
}
