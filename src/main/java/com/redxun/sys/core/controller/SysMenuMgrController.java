package com.redxun.sys.core.controller;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.redxun.core.constants.MBoolean;
import com.redxun.core.json.JSONUtil;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.BaseController;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.core.entity.Subsystem;
import com.redxun.sys.core.entity.SysMenu;
import com.redxun.sys.core.manager.SubsystemManager;
import com.redxun.sys.core.manager.SysMenuManager;
import com.redxun.sys.log.LogEnt;
import com.redxun.sys.org.entity.OsRelInst;
import com.redxun.sys.org.manager.OsRelInstManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * 用于管理系统中的资源菜单
 * 
 * @author csx
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn） 本源代码受软件著作法保护，请在授权允许范围内使用
 */
@Controller
@RequestMapping("/sys/core/sysMenuMgr")
public class SysMenuMgrController extends BaseController {
    @Resource
    SysMenuManager sysMenuManager;
    @Resource
    SubsystemManager subsystemManager;
    @Resource
    OsRelInstManager osRelInstManager;

    @RequestMapping("listSys")
    @ResponseBody
    public List<Subsystem> listSys(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String instType = request.getParameter("instType");
        if (StringUtils.isEmpty(instType)) {
            instType = "PLATFORM";
        }
        List list = subsystemManager.getInstTypeValidSystem(instType);
        return list;
    }

    @RequestMapping("listAllSyses")
    @ResponseBody
    public List<Subsystem> listAllSyses(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List list = subsystemManager.getAll();
        return list;
    }

    @RequestMapping("listAllSubSys")
    @ResponseBody
    public List<Subsystem> listAllSubSys(HttpServletRequest request, HttpServletResponse response) {
        String type = RequestUtil.getString(request, "type");
        String mgrDomain = WebAppUtil.getOrgMgrDomain();
        IUser user = ContextUtil.getCurrentUser();
        List<Subsystem> list = null;
        Collection<Subsystem> grantSyses = null;

        if ((mgrDomain.equals(user.getTenant().getDomain()) && user.isSuperAdmin()) || "gnjs".equalsIgnoreCase(type) || "bbs".equalsIgnoreCase(type)) {
            grantSyses = subsystemManager.getByValidSystem();
        } else {
            // 先取得该用户授权下的用户组，再根据用户组获得其授权访问的菜单
            // 应用系统进行菜单合并
            grantSyses = new HashSet<Subsystem>();
            List<OsRelInst> groupInsts =
                osRelInstManager.getGrantGroupsByUserId(user.getUserId(), user.getTenant().getTenantId());
            for (OsRelInst inst : groupInsts) {
                String groupId = inst.getParty1();
                List<Subsystem> groupSyses = subsystemManager.getGrantSubsByGroupId(groupId);
                grantSyses.addAll(groupSyses);
            }
        }
        if (grantSyses != null) {
            list = new ArrayList<Subsystem>();
            for (Subsystem subsystem : grantSyses) {
                list.add(subsystem);
            }
        }

        // 功能建设
        if (StringUtils.isNotBlank(type) && (type.equals("gnjs")||type.equals("bbs"))) {
            List<Subsystem> gnjsList = new ArrayList<>();
            String parentId = IdUtil.getId();
            List<String> notShowNames = Arrays.asList(RdmConst.NOT_SHOW_SYSTEM_NAMES);
            for (Subsystem subsystem : list) {
                if (!notShowNames.contains(subsystem.getName())) {
                    subsystem.setParentId(parentId);
                    gnjsList.add(subsystem);
                }
            }
            if (!gnjsList.isEmpty()&& type.equals("gnjs")) {
                Subsystem parentSystem = new Subsystem(parentId);
                parentSystem.setName("所有模块");
                gnjsList.add(0, parentSystem);
            }
            return gnjsList;
        }
        return list;
    }

    /**
     * 按树展示菜单
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("treeBySysId")
    @ResponseBody
    public List<SysMenu> treeBySysId(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String sysId = request.getParameter("sysId");

        List<SysMenu> menus = null;
        if (StringUtils.isNotEmpty(sysId)) {
            menus = sysMenuManager.getBySysId(sysId);
        } else {
            menus = sysMenuManager.getAll();
        }
        return menus;
    }

    /**
     * 保存菜单
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("saveMenu")
    @ResponseBody
    @LogEnt(action = "saveMenu", module = "系统", submodule = "菜单管理")
    public JsonResult saveMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String data = request.getParameter("data");

        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setExcludes(new String[] {"children"});
        JSONObject obj = JSONObject.fromObject(data, jsonConfig);
        // String key = obj.getString("key");
        SysMenu sysMenu = (SysMenu)JSONObject.toBean(obj, SysMenu.class);

        String key = sysMenu.getKey();

        if (StringUtil.isEmpty(key)) {
            return new JsonResult(false, "菜单key必填！");
        }
        boolean isKeyExist = sysMenuManager.isKeyExist(key, sysMenu.getMenuId());
        if (isKeyExist) {
            return new JsonResult(false, key + "系统中已存在!");
        }

        if (StringUtil.isNotEmpty(sysMenu.getBoListId())) {
            boolean rtn = sysMenuManager.isBoListExist(sysMenu);
            if (rtn) {
                return new JsonResult(false, "BO列表已添加！");
            }
        }

        Subsystem subsys = subsystemManager.get(sysMenu.getSysId());
        sysMenu.setSysId(subsys.getSysId());

        if (StringUtils.isEmpty(sysMenu.getMenuId())) {
            if (sysMenu.getSn() == null) {
                sysMenu.setSn(0);
            }
            sysMenu.setMenuId(idGenerator.getSID());

            String parentId = JSONUtil.getString(obj, "parentId");
            SysMenu parentMenu = sysMenuManager.get(parentId);
            if (parentMenu != null) {
                sysMenu.setPath(parentMenu.getPath() + sysMenu.getMenuId() + ".");
                int depth = parentMenu.getDepth() == null ? 0 : parentMenu.getDepth();
                sysMenu.setDepth(depth + 1);
                sysMenu.setParentId(parentMenu.getMenuId());
            } else {
                sysMenu.setPath(sysMenu.getParentId() + "." + sysMenu.getMenuId() + ".");
                sysMenu.setDepth(1);
                sysMenu.setParentId(subsys.getSysId());
            }

            sysMenuManager.create(sysMenu);
        } else {
            sysMenuManager.update(sysMenu);
        }
        return new JsonResult(true, "成功保存！", sysMenu);
    }

    @RequestMapping("batchSaveMenu")
    @ResponseBody
    @LogEnt(action = "batchSaveMenu", module = "系统", submodule = "菜单管理")
    public JsonResult batchSaveMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String sysId = request.getParameter("sysId");
        Subsystem subsystem = subsystemManager.get(sysId);
        String gridData = request.getParameter("gridData");
        JsonResult result = new JsonResult(true, "成功保存！");
        genSysMenu(gridData, null, subsystem, result);
        return result;
    }

    /**
     * 产生菜单的上下级内容
     * 
     * @param menuJson
     * @param parentMenu
     * @param subsystem
     */
    private void genSysMenu(String menuJson, SysMenu parentMenu, Subsystem subsystem, JsonResult jsonResult) {
        JSONArray jsonArray = JSONArray.fromObject(menuJson);
        if (parentMenu != null) {
            parentMenu.setChilds(jsonArray.size());
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            String menuId = (String)jsonObj.get("menuId");
            SysMenu sysMenu = null;
            // 是否为创建
            boolean isCreated = false;
            if (menuId == null) {
                sysMenu = new SysMenu();
                sysMenu.setMenuId(idGenerator.getSID());
                sysMenu.setSysId(subsystem.getSysId());
                isCreated = true;
            } else {
                sysMenu = sysMenuManager.get(menuId.toString());
            }
            String id = isCreated ? "" : menuId;
            String checkKey = isCreated ? sysMenu.getKey() : jsonObj.getString("key");
            boolean isMenuExist = sysMenuManager.isKeyExist(checkKey, id);
            if (isMenuExist) {
                jsonResult.setSuccess(false);
                jsonResult.setMessage(sysMenu.getKey() + "系统中已存在KEY");
                break;
            }

            String name = jsonObj.getString("name");
            String key = jsonObj.getString("key");
            String img = JSONUtil.getString(jsonObj, "img");
            String isMgr = JSONUtil.getString(jsonObj, "isMgr");
            String iconCls = JSONUtil.getString(jsonObj, "iconCls");
            if (StringUtils.isEmpty(isMgr)) {
                isMgr = MBoolean.NO.toString();
            }
            int sn = JSONUtil.getInt(jsonObj, "sn");
            String url = JSONUtil.getString(jsonObj, "url");

            sysMenu.setName(name);
            sysMenu.setKey(key);
            sysMenu.setImg(img);
            sysMenu.setUrl(url);
            sysMenu.setSn(sn);
            sysMenu.setIconCls(iconCls);

            if (parentMenu == null) {
                sysMenu.setParentId(subsystem.getSysId());
                sysMenu.setPath(subsystem.getSysId() + "." + sysMenu.getMenuId() + ".");
                sysMenu.setDepth(1);
            } else {
                sysMenu.setParentId(parentMenu.getMenuId());
                sysMenu.setPath(parentMenu.getPath() + sysMenu.getMenuId() + ".");
                sysMenu.setDepth(parentMenu.getDepth() + 1);
            }

            String children = JSONUtil.getString(jsonObj, "children");
            if (StringUtils.isNotEmpty(children)) {
                genSysMenu(children, sysMenu, subsystem, jsonResult);
            }
            if (isCreated) {
                sysMenuManager.create(sysMenu);
            } else {
                sysMenuManager.update(sysMenu);
            }
        }

    }

    /**
     * 删除菜单
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("del")
    @ResponseBody
    @LogEnt(action = "del", module = "系统", submodule = "菜单管理")
    public JsonResult del(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String menuId = request.getParameter("menuId");
        sysMenuManager.deleteCascade(menuId);
        return new JsonResult(true, "成功删除菜单");
    }
}
