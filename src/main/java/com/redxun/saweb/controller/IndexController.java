package com.redxun.saweb.controller;

import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.util.RdmCommonUtil;
import com.redxun.sys.core.entity.SysDic;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.entity.BpmTask;
import com.redxun.bpm.core.manager.BpmTaskManager;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.query.QueryFilter;
import com.redxun.core.util.*;
import com.redxun.digitization.core.service.RdmI18nUtil;
import com.redxun.embedsoft.core.dao.*;
import com.redxun.org.api.model.IGroup;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.GroupService;
import com.redxun.rdmCommon.core.dao.RdmHomeDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.IndexManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.YdgztbDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.menu.UIMenu;
import com.redxun.saweb.util.QueryFilterBuilder;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.manager.StandardMessageManager;
import com.redxun.standardManager.core.util.StandardManagerUtil;
import com.redxun.sys.core.entity.Subsystem;
import com.redxun.sys.core.entity.SysFile;
import com.redxun.sys.core.manager.SubsystemManager;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.manager.SysFileManager;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.sys.org.manager.OsUserManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectAbolishManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectMsgManager;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * @author csx
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn） 本源代码受软件著作法保护，请在授权允许范围内使用
 */
@Controller
@RequestMapping("/")
public class IndexController extends BaseController {
    @Resource
    SubsystemManager subsystemManager;
    @Resource
    OsUserManager osUserManager;
    @Resource
    SysFileManager sysFileManager;
    @Resource
    GroupService groupService;
    @Resource
    StandardMessageManager standardMessageManager;
    @Autowired
    private CxMessageDao cxMessageDao;
    @Autowired
    private DmgzDao dmgzDao;
    @Autowired
    private TxxyAddDao txxyAddDao;
    @Autowired
    private KzxtgnkfDao kzxtgnkfDao;
    @Resource
    BpmTaskManager bpmTaskManager;
    @Resource
    private XcmgProjectMsgManager xcmgProjectMsgManager;
    @Resource
    private XcmgProjectAbolishManager xcmgProjectAbolishManager;

    @Resource
    private RdmHomeDao rdmHomeDao;

    @Autowired
    SysDicManager sysDicManager;

    @Autowired
    private IndexManager indexManager;

    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private YdgztbDao ydgztbDao;
    @Autowired
    private StorageAreaManagementDao storageAreaManagementDao;

    @RequestMapping("subsys/{subname}/home")
    public ModelAndView subhome(@PathVariable String subname, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Subsystem subsystem = subsystemManager.getByKey(subname);
        String menuId = request.getParameter("menuId");
        if (subsystem == null) {
            return new ModelAndView("/index/subhomes/noexist.jsp");
        }
        String homeUrl = request.getContextPath() + "/oa/info/insPortalDef/home.do";
        if (StringUtils.isNotEmpty(subsystem.getHomeUrl())) {
            if (!subsystem.getHomeUrl().startsWith("http")) {
                homeUrl = request.getContextPath() + subsystem.getHomeUrl();
            } else {
                homeUrl = subsystem.getHomeUrl();
            }
        }
        return new ModelAndView("/index/subhomes/home.jsp").addObject("menuId", menuId)
                .addObject("subsystem", subsystem).addObject("homeUrl", homeUrl);
    }

    //..todo:mark一下：主要关注这个映射，是整个前端显示架构的索引,新增了IndexManager这个类进行配合，注意!
    @RequestMapping("index")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 加载系统的可访问子系统
        IUser curUser = ContextUtil.getCurrentUser();
        String recId = ContextUtil.getCurrentUserId();
        boolean netType = StandardManagerUtil.judgeGLNetwork(request);
        int newMsgCount = 0;
        IGroup curDep = groupService.getMainByUserId(curUser.getUserId());
        String theme = CookieUtil.getValueByName("index", request);
        if (StringUtil.isEmpty(theme)) {
            theme = "index4";
        }
        ModelAndView pageMv = new ModelAndView("index/" + theme + ".jsp");
        OsUser osUser = osUserManager.get(recId);
        String photoId = null;
        String webappName = WebAppUtil.getProperty("webappName", "rdm");
        String appName = "";
        if ("sim".equalsIgnoreCase(webappName)) {
            appName = "标准化管理平台";
        } else {
            appName = RdmI18nUtil.toGetValueByKey(request, "page.index.appName");
        }
        String fullPath = "";
        if (StringUtils.isNotBlank(photoId)) {
            SysFile sysFile = sysFileManager.get(photoId);
            String fileId = sysFile == null ? "" : sysFile.getFileId();
            fullPath = request.getContextPath() + "/sys/core/file/download/" + fileId + ".do";
        } else {
            fullPath = request.getContextPath() + "/styles/images/index/index_tap_06.png";
        }
        List<UIMenu> rootList = indexManager.getRootMenu(webappName, netType);
        Map<String, Integer> key2Index = new HashMap<>();
        for (int index = 0; index < rootList.size(); index++) {
            UIMenu uiMenu = rootList.get(index);
            key2Index.put(uiMenu.getKey(), index + 1);
        }
        // 查询自己有权限看到的平台消息,未读、应该弹出、没有过期的，用户登录后弹出消息
        List<Map<String, Object>> noReadMsgs = new ArrayList<>();
        String userId = ContextUtil.getCurrentUserId();
        List<Map<String, Object>> recMsgs = xcmgProjectMsgManager.queryRecMsg(userId, "computer", webappName);
        if (recMsgs != null && !recMsgs.isEmpty()) {
            for (Map<String, Object> oneMap : recMsgs) {
                if ("未读".equalsIgnoreCase(oneMap.get("status").toString())
                        && "yes".equalsIgnoreCase(oneMap.get("canPopup").toString())) {
                    if (oneMap.get("expireTime") != null) {
                        Date expireTimeDate = (Date) oneMap.get("expireTime");
                        long currentTime =
                                DateFormatUtil.parseDateTime(XcmgProjectUtil.getNowLocalDateStr("")).getTime();
                        if (expireTimeDate.getTime() - currentTime > 0) {
                            noReadMsgs.add(oneMap);
                        }
                    } else {
                        noReadMsgs.add(oneMap);
                    }
                }
            }
        }
        JSONArray noReadMsgsArray = XcmgProjectUtil.convertListMap2JsonArrObject(noReadMsgs);

        // zz 查询是否有标准管理的宣贯消息
        List<Map<String, Object>> standardMsgList = new ArrayList<>();
        standardMsgList = standardMessageManager.getStandardMsgByUserId(ContextUtil.getCurrentUserId());
        boolean isGLNet = StandardManagerUtil.judgeGLNetwork(request);
        long currentTime = DateFormatUtil.parseDateTime(XcmgProjectUtil.getNowLocalDateStr("")).getTime();
        // 对于标准宣贯，超过7天时间太久的不再弹出
        long sevenDayMillSecs = 7 * 24 * 60 * 60 * 1000;
        Iterator<Map<String, Object>> it = standardMsgList.iterator();
        while (it.hasNext()) {
            Map<String, Object> oneObj = it.next();
            if (oneObj.get("CREATE_TIME_") != null) {
                Date createDate = (Date) oneObj.get("CREATE_TIME_");
                if (currentTime - createDate.getTime() > sevenDayMillSecs) {
                    it.remove();
                    continue;
                }
            }
            if (isGLNet) {
                if (oneObj.get("systemCategoryId") != null
                        && "JS".equalsIgnoreCase(oneObj.get("systemCategoryId").toString())) {
                    it.remove();
                }
            }
        }
        JSONArray standardMsgArray = XcmgProjectUtil.convertListMap2JsonArrObject(standardMsgList);

        // 程序发布
        List<JSONObject> cxMsgList = cxMessageDao.getCxMsgByUserId(ContextUtil.getCurrentUserId());
        JSONArray cxMsgArray = new JSONArray();
        for (JSONObject cxMsg : cxMsgList) {
            cxMsgArray.add(cxMsg);
        }

        // 故障代码新增
        List<JSONObject> gzmdMsgList = dmgzDao.getDmgzMsgByUserId(ContextUtil.getCurrentUserId());
        JSONArray gzdmMsgArray = new JSONArray();
        for (JSONObject gzdmMsg : gzmdMsgList) {
            gzdmMsgArray.add(gzdmMsg);
        }

        // 通信协议新增
        List<JSONObject> txxyMsgList = txxyAddDao.getTxxyMsgByUserId(ContextUtil.getCurrentUserId());
        JSONArray txxyMsgArray = new JSONArray();
        for (JSONObject txxyMsg : txxyMsgList) {
            txxyMsgArray.add(txxyMsg);
        }

        // 控制系统功能开发新增
        List<JSONObject> kzxtMsgList = kzxtgnkfDao.getKzxtMsgByUserId(ContextUtil.getCurrentUserId());
        JSONArray kzxtMsgArray = new JSONArray();
        for (JSONObject kzxtMsg : kzxtMsgList) {
            kzxtMsgArray.add(kzxtMsg);
        }

        // 控制系统功能开发新增
        List<JSONObject> kzccMsgList =
                storageAreaManagementDao.getStorageAreaMsgByUserId(ContextUtil.getCurrentUserId());
        JSONArray kzccMsgArray = new JSONArray();
        for (JSONObject kzccMsg : kzccMsgList) {
            kzccMsgArray.add(kzccMsg);
        }

        // 领导日程提醒弹框
        JSONObject leaderScheduleNotice = new JSONObject();
        Map<String, Object> params = new HashMap<>();
        params.put("userIds", Arrays.asList(userId));
        params.put("dayStr", DateFormatUtil.getNowByString("yyyy-MM-dd"));
        params.put("ifNotice", "no");
        List<JSONObject> scheduleList = rdmHomeDao.queryWorkStatus(params);
        if (scheduleList != null && !scheduleList.isEmpty()) {
            leaderScheduleNotice.put("title", DateFormatUtil.getNowByString("yyyy-MM-dd") + "日程提醒");
            StringBuilder stringBuilder = new StringBuilder();
            for (int index = 0; index < scheduleList.size(); index++) {
                JSONObject oneSchedule = scheduleList.get(index);
                stringBuilder.append("<br>" + (index + 1) + "、开始时间：" + oneSchedule.getString("startTime"))
                        .append("，结束时间：" + oneSchedule.getString("endTime"));
                stringBuilder.append("，地点：" + oneSchedule.getString("place"))
                        .append("，事项：" + oneSchedule.getString("scheduleDesc"));
                stringBuilder.append("，创建人：" + oneSchedule.getString("creatorName"));
            }
            leaderScheduleNotice.put("content", stringBuilder.toString());
        }

        // 综合管理部门优秀案例选择提醒
        JSONObject ydgztbRemind = new JSONObject();
        String yearMonth = DateFormatUtil.format(new Date(), "yyyy-MM");
        String dateStr = yearMonth.substring(5, 7);
        // 每个月26号之后
        if (dateStr.compareTo("26") >= 0) {
            List<Map<String, String>> deptResps = commonInfoManager.queryJszxAllSuoZhang();
            Set<String> deptRespUserIds = new HashSet<>();
            if (deptResps != null && !deptResps.isEmpty()) {
                for (Map<String, String> depRespMan : deptResps) {
                    deptRespUserIds.add(depRespMan.get("USER_ID_"));
                }
            }
            // 技术中心的所长
            if (deptRespUserIds.contains(curUser.getUserId())) {
                JSONObject paramJson = new JSONObject();
                paramJson.put("yearMonth", yearMonth);
                List<JSONObject> cancelUserList = ydgztbDao.queryRemindCancel(paramJson);
                Set<String> cancelUserIds = new HashSet<>();
                if (cancelUserList != null && !cancelUserList.isEmpty()) {
                    for (JSONObject cancelUser : cancelUserList) {
                        cancelUserIds.add(cancelUser.getString("deptRespUserId"));
                    }
                }
                // 本月没有人为取消
                if (!cancelUserIds.contains(curUser.getUserId())) {
                    // 判断所在的部门是否尚未选择优秀案例
                    Set<String> undoDepartIds = new HashSet<>();
                    List<JSONObject> undoDeptList = ydgztbDao.queryUndoDepartList(paramJson);
                    if (undoDeptList != null && !undoDeptList.isEmpty()) {
                        for (JSONObject undoDept : undoDeptList) {
                            undoDepartIds.add(undoDept.getString("departId"));
                        }
                    }
                    if (undoDepartIds.contains(curUser.getMainGroupId())) {
                        ydgztbRemind.put("content", "请点击“前往评选”按钮进行部门月度工作提报优秀案例评选，点击“不再提醒”按钮后本月不再提醒！");
                    }
                }
            }

        }
        //todo@lwgkiller：增加嵌入式系统屏蔽账户过滤
        boolean QRSXTXXPBYH_allMessage_hide = false;
        SysDic sysDic = sysDicManager.getBySysTreeKeyAndDicKey("QRSXTXXPBYH", "allMessage");
        if (sysDic != null) {
            String users = sysDic.getValue();
            if (StringUtil.isNotEmpty(users)) {
                List<String> userNoList = Arrays.asList(users.split(","));
                for (String userNo : userNoList) {
                    if (ContextUtil.getCurrentUser().getUserNo().equalsIgnoreCase(userNo)) {
                        QRSXTXXPBYH_allMessage_hide = true;
                        break;
                    }
                }
            }
        }
        if (!QRSXTXXPBYH_allMessage_hide) {
            pageMv.addObject("ydgztbRemind", ydgztbRemind);
            pageMv.addObject("leaderScheduleNotice", leaderScheduleNotice);
            pageMv.addObject("appName", appName).addObject("curUser", curUser).addObject("curDep", curDep)
                    .addObject("newMsgCount", newMsgCount).addObject("fullPath", fullPath).addObject("menus", rootList)
                    .addObject("menuSize", rootList.size()).addObject("cxMsgArray", cxMsgArray)
                    .addObject("noReadMsgs", noReadMsgsArray).addObject("standardMsgList", standardMsgArray)
                    .addObject("userId", curUser.getUserId()).addObject("webappName", webappName)
                    .addObject("gzdmMsgArray", gzdmMsgArray).addObject("txxyMsgArray", txxyMsgArray)
                    .addObject("kzxtMsgArray", kzxtMsgArray).addObject("kzccMsgArray", kzccMsgArray);
        } else {
            pageMv.addObject("ydgztbRemind", new JSONObject());
            pageMv.addObject("leaderScheduleNotice", new JSONObject());
            pageMv.addObject("appName", appName).addObject("curUser", curUser).addObject("curDep", curDep)
                    .addObject("newMsgCount", newMsgCount).addObject("fullPath", fullPath).addObject("menus", rootList)
                    .addObject("menuSize", rootList.size()).addObject("cxMsgArray", new JSONArray())
                    .addObject("noReadMsgs", new JSONArray()).addObject("standardMsgList", new JSONArray())
                    .addObject("userId", curUser.getUserId()).addObject("webappName", webappName)
                    .addObject("gzdmMsgArray", new JSONArray()).addObject("txxyMsgArray", new JSONArray())
                    .addObject("kzxtMsgArray", new JSONArray()).addObject("kzccMsgArray", new JSONArray());
        }

        // 查找当前用户是否是弱密码
        String pwdStatus = osUser.getPwdStatus();
        pageMv.addObject("weakPwd", "weak".equalsIgnoreCase(pwdStatus) ? "yes" : "no");
        String developOrProduce = SysPropertiesUtil.getGlobalProperty("developOrProduce");
        pageMv.addObject("developOrProduce", developOrProduce);
        int actSysIndex = 1;
        String actSysKey = RequestUtil.getString(request, "actSysKey", "");
        if (StringUtils.isNotBlank(actSysKey)) {
            String realKey = "";
            try {
                realKey = EncryptUtil.decrypt(actSysKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (key2Index.containsKey(realKey)) {
                actSysIndex = key2Index.get(realKey);
            }
        }
        pageMv.addObject("actSysIndex", actSysIndex);
        String clickMenuId = RequestUtil.getString(request, "clickMenuId", "");
        pageMv.addObject("clickMenuId", clickMenuId);
        return pageMv;
    }

    @RequestMapping("weakPwdReset")
    public ModelAndView weakPwdReset(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView pageMv = new ModelAndView("index/weakPwdReset.jsp");
        pageMv.addObject("userId", RequestUtil.getString(request, "userId", ContextUtil.getCurrentUserId()));
        return pageMv;
    }

    /**
     * 一次性获取菜单。
     *
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("getMenusByMenuId")
    public List<UIMenu> getMenusByMenuId(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String menuId = request.getParameter("menuId");
        String menuName = request.getParameter("menuName");
        // 网络类型1：技术网 2：管理网
        boolean netType = StandardManagerUtil.judgeGLNetwork(request);
        if (StringUtil.isEmpty(menuId)) {
            List<UIMenu> rootList = indexManager.getRootMenu(null, netType);
            if (rootList != null && !rootList.isEmpty()) {
                menuId = rootList.get(0).getMenuId();
                menuName = rootList.get(0).getName();
            }
        }
        List list = indexManager.getBySysId(menuId, null, true, netType);
        // 国际化
        String language = RdmI18nUtil.toGetLanguage(request);
        for (Object obj : list) {
            UIMenu subMenu = (UIMenu) obj;
            subMenu.setSysName(menuName);
            indexManager.callBackMenu(subMenu, language);
        }
        if ("标准管理".equals(menuName)) {
            QueryFilter filter = QueryFilterBuilder.createQueryFilter(request);
            Page pageParam = (Page) filter.getPage();
            pageParam.setPageSize(2000);
            pageParam.setPageIndex(0);
            // 待办
            List<BpmTask> bpmTasks = bpmTaskManager.getByUserId(filter);
            String standardTaskKeys = WebAppUtil.getProperty("standardTaskKey");
            String[] taskKeys = standardTaskKeys.split(",");
            List<BpmTask> newTasks = new ArrayList<>();
            String solKey;
            JSONObject jsonObject = new JSONObject();
            for (BpmTask bpmTask : bpmTasks) {
                solKey = bpmTask.getSolKey();
                if (solKey == null || ConstantUtil.NULL.equals(solKey) || "".equals(solKey)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("solId", bpmTask.getSolId());
                    jsonObject = xcmgProjectAbolishManager.getBpmSolution(param);
                    solKey = jsonObject.getString("KEY_");
                }
                if (XcmgProjectUtil.filterTask(solKey, taskKeys)) {
                    newTasks.add(bpmTask);
                }
            }
            setMsgNumForStandardMenu(newTasks.size(), 0, list);
        }
        return list;
    }

    // 判断是否在主页面进来时弹出消息
    private boolean popAtFirst(Map<String, Object> oneMap) throws ParseException {
        if (oneMap.get("expireTime") == null) {
            return true;
        }
        Date expireTimeDate = (Date) oneMap.get("expireTime");
        long currentTime = DateFormatUtil.parseDateTime(XcmgProjectUtil.getNowLocalDateStr("")).getTime();
        if (expireTimeDate.getTime() - currentTime > 0) {
            return true;
        }
        return false;
    }

    // 为项目管理模块设置消息提醒
    private void setMsgNumForProjectMenu(int todoTaskNum, int noReadMsgNum, List<UIMenu> menus) {
        UIMenu firstMenu = null;
        for (UIMenu menu : menus) {
            if ("消息中心".equals(menu.getName())) {
                firstMenu = menu;
            }
        }
        if (firstMenu != null) {
            firstMenu.setMsgNum(todoTaskNum + noReadMsgNum);
            for (Tree uiMenu : firstMenu.getChildren()) {
                UIMenu secondMenu = (UIMenu) uiMenu;
                if ("项目待办管理".equals(secondMenu.getName())) {
                    secondMenu.setMsgNum(todoTaskNum);
                }
                if ("项目消息管理".equals(secondMenu.getName())) {
                    secondMenu.setMsgNum(noReadMsgNum);
                }
            }
        }
    }

    // 为标准管理模块设置消息提醒（不设置未读消息）
    private void setMsgNumForStandardMenu(int todoTaskNum, int noReadMsgNum, List<UIMenu> menus) {
        UIMenu firstMenu = null;
        for (UIMenu menu : menus) {
            if ("消息中心".equals(menu.getName())) {
                firstMenu = menu;
            }
        }
        if (firstMenu != null) {
            firstMenu.setMsgNum(todoTaskNum + noReadMsgNum);
            for (Tree uiMenu : firstMenu.getChildren()) {
                UIMenu secondMenu = (UIMenu) uiMenu;
                if ("标准待办管理".equals(secondMenu.getName())) {
                    secondMenu.setMsgNum(todoTaskNum);
                }
            }
        }
    }

    // @TDM
    private void setMsgNumForTdmMenu(int todoTaskNum, int noReadMsgNum, List<UIMenu> menus) {
        UIMenu firstMenu = null;
        for (UIMenu menu : menus) {
            if ("TDM工作台".equals(menu.getName())) {
                firstMenu = menu;
            }
        }
        if (firstMenu != null) {
            firstMenu.setMsgNum(todoTaskNum + noReadMsgNum);
            // for (Tree uiMenu : firstMenu.getChildren()) {
            // UIMenu secondMenu = (UIMenu)uiMenu;
            // if ("XXX".equals(secondMenu.getName())) {
            // secondMenu.setMsgNum(todoTaskNum);
            // }
            // }
        }
    }

    /**
     * 一次性获取菜单。
     *
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("getMenus")
    public List<UIMenu> getMemus(HttpServletRequest request) throws Exception {
        boolean netType = StandardManagerUtil.judgeGLNetwork(request);
        List<UIMenu> rootList = indexManager.getRootMenu(null, netType);
        for (UIMenu root : rootList) {
            List list = indexManager.getBySysId(root.getMenuId(), null, true, netType);
            for (Object obj : list) {
                UIMenu subMenu = (UIMenu) obj;
                indexManager.callBackMenu(subMenu, RdmI18nUtil.toGetLanguage(request));
            }
            root.setChildren(list);
        }
        return rootList;
    }

    /**
     * 适用于点击首页菜单、近期搜索菜单或者子系统名称之后的跳转
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("redirectOut")
    @ResponseBody
    public JsonResult redirectOut(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url = RequestUtil.getString(request, "url");
        if (StringUtils.isBlank(url) || !url.contains("?")) {
            return null;
        }
        String language = RdmI18nUtil.toGetLanguage(request);
        if ("zh".equalsIgnoreCase(language)) {
            language = RdmConst.I18N_ZH_CN;
        } else if ("en".equalsIgnoreCase(language)) {
            language = RdmConst.I18N_EN_US;
        }
        String menuId = RequestUtil.getString(request, "clickMenuId", "");
        response.sendRedirect(url + "&acc=" + ContextUtil.getCurrentUser().getUserNo() + "&language=" + language
                + "&clickMenuId=" + menuId);
        return null;
    }

    /**
     * 适用于点击菜单或者链接之后的跳转，不适合直接查询数据（因为转发会丢失查询条件）
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("pageJumpRedirect")
    @ResponseBody
    public JsonResult pageJumpRedirect(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String targetUrl = RequestUtil.getString(request, "targetUrl");
        if (StringUtils.isBlank(targetUrl)) {
            return null;
        }
        String ipPort = SysPropertiesUtil.getGlobalProperty("rdmAddress");
        String targetSubSysKey = RequestUtil.getString(request, "targetSubSysKey");
        if (StringUtils.isNotBlank(targetSubSysKey)) {
            Subsystem targetSubsystem = subsystemManager.getByKey(targetSubSysKey);
            if (targetSubsystem != null && StringUtils.isNotBlank(targetSubsystem.getIp_port())) {
                ipPort = targetSubsystem.getIp_port();
            }
        }
        if (StringUtils.isBlank(ipPort)) {
            return null;
        }

        String language = RdmI18nUtil.toGetLanguage(request);
        if ("zh".equalsIgnoreCase(language)) {
            language = RdmConst.I18N_ZH_CN;
        } else if ("en".equalsIgnoreCase(language)) {
            language = RdmConst.I18N_EN_US;
        }
        String newUrl = ipPort + "/pageJumpLogin.do?";
        newUrl += "acc=" + ContextUtil.getCurrentUser().getUserNo() + "&language=" + language;
        newUrl += "&targetUrl=" + URLEncoder.encode(targetUrl, "utf-8");
        response.sendRedirect(newUrl);
        return null;
    }
}
