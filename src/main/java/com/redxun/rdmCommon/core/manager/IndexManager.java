package com.redxun.rdmCommon.core.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.redxun.core.util.Tree;
import com.redxun.digitization.core.service.RdmI18nUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.redxun.core.constants.MBoolean;
import com.redxun.core.util.BeanUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.ITenant;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.menu.UIMenu;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.core.entity.Subsystem;
import com.redxun.sys.core.entity.SysMenu;
import com.redxun.sys.core.manager.SubsystemManager;
import com.redxun.sys.core.manager.SysMenuManager;
import com.redxun.sys.org.entity.OsUser;

import groovy.util.logging.Slf4j;

@Service
@Slf4j
public class IndexManager {
    private static Logger logger = LoggerFactory.getLogger(IndexManager.class);
    @Resource
    private SubsystemManager subsystemManager;
    @Resource
    private SysMenuManager sysMenuManager;

    /**
     * 获取根菜单。
     *
     * @return
     * @throws Exception
     */
    public List<UIMenu> getRootMenu(String webAppName,boolean netType) throws Exception {
        // String tenantId=ContextUtil.getCurrentTenantId();
        ITenant curTenant = ContextUtil.getTenant();
        IUser curUser = ContextUtil.getCurrentUser();
        String mgrDomain = WebAppUtil.getOrgMgrDomain();
        List<Subsystem> syses = null;
        // 获得平台级的登录用户
        if (mgrDomain.equals(curUser.getTenant().getDomain()) && curUser.isSuperAdmin()) {
            syses = subsystemManager.getPlatformValidSystem();
        } else if (curUser.isSaaSAdmin()) {// 授权租户管理员使用
            syses = subsystemManager.getInstTypeValidSystem(curTenant.getInstType());
        } else {// 根据用户所属的组来进行授权
            Set<String> groupIds = ((OsUser)curUser).getGroupIds();
            syses = subsystemManager.getGrantSubsByGroupIds(groupIds,netType);
            // syses=subsystemManager.getGrantSubsByUserId(curUser.getUserId(),curTenant.getTenantId(),curTenant.getInstType());
            // 对于标准管理平台，只放开标准管理板块
            if (syses != null && !syses.isEmpty() && StringUtils.isNotBlank(webAppName)
                && RdmConst.WEBAPPNAME_SIM.equalsIgnoreCase(webAppName)) {
                Iterator<Subsystem> iterator = syses.iterator();
                while (iterator.hasNext()) {
                    Subsystem subsystem = iterator.next();
                    if (!subsystem.getKey().equalsIgnoreCase(RdmConst.STANDARD_SUBSYS)) {
                        iterator.remove();
                    }
                }
            }

        }
        return getBySystem(syses);
    }

    /**
     * 将子系统转换为菜单。
     *
     * @param sysList
     * @return
     */
    private List<UIMenu> getBySystem(List<Subsystem> sysList) {
        List<UIMenu> rootList = new ArrayList<UIMenu>();
        if (sysList == null || sysList.isEmpty()) {
            return rootList;
        }
        for (Subsystem sys : sysList) {
            UIMenu menu = new UIMenu(sys.getSysId(), sys.getKey(), sys.getName(), sys.getHomeUrl(), "", sys.getSn());
            menu.setIconCls(sys.getIconCls());
            menu.setType(sys.getType());
            menu.setWindowOpen(sys.getWindowOpen());
            rootList.add(menu);
        }
        return rootList;
    }

    /**
     * 根据系统获取菜单。
     *
     * @param sysId
     * @return
     * @throws Exception
     */
    public List<UIMenu> getBySysId(String sysId, String menuName, boolean list2Tree ,boolean netType) throws Exception {
        String tenantId = ContextUtil.getCurrentTenantId();
        IUser curUser = ContextUtil.getCurrentUser();
        String mgrDomain = WebAppUtil.getOrgMgrDomain();
        List<SysMenu> menums = null;
        if (mgrDomain.equals(curUser.getTenant().getDomain()) && curUser.isSuperAdmin()) {// Saas管理员
            menums = sysMenuManager.getBySysIdIsBtnMenu(sysId, MBoolean.NO.name(), menuName);
        } else if (curUser.isSaaSAdmin()) {// 租户管理员
            // 根据租户找到菜单,
            menums = sysMenuManager.getUrlMenuByTenantMgr(sysId, tenantId, MBoolean.NO.name(), menuName);
        } else {
            // menums=sysMenuManager.getGrantMenusBySysIdUserId(sysId, curUser.getUserId(),tenantId,MBoolean.NO.name());
            Set<String> groupIds = ((OsUser)curUser).getGroupIds();
            menums = sysMenuManager.getGrantMenusBySysIdGroupIds(sysId, groupIds, menuName, netType);
        }
        List<UIMenu> rtnMemu = getByMemus(menums, sysId);
        if (list2Tree) {
            List<UIMenu> memus = BeanUtil.listToTree(rtnMemu);
            return memus;
        } else {
            return rtnMemu;
        }
    }

    /**
     * 构建菜单结构。
     *
     * @param menus
     * @return
     */
    private List<UIMenu> getByMemus(List<SysMenu> menus, String sysId) {
        List<UIMenu> rtnList = new ArrayList<UIMenu>();
        for (SysMenu menu : menus) {
            String url = menu.getUrl();
            if (StringUtils.isNotBlank(url)) {
                if (url.contains("{userId}")) {
                    url = url.replaceAll("\\{userId}", ContextUtil.getCurrentUserId());
                }
                if (url.contains("{userName}")) {
                    url = url.replaceAll("\\{userName}", ContextUtil.getCurrentUser().getFullname());
                }
            }
            UIMenu uiMenu = new UIMenu(menu.getMenuId(), menu.getKey(), menu.getName(), url, "", menu.getSn());
            if (StringUtil.isNotEmpty(menu.getIconCls())) {
                uiMenu.setIconCls(menu.getIconCls().trim());
            }
            uiMenu.setParentId(menu.getParentId());
            uiMenu.setIsBtnMenu(menu.getIsBtnMenu());
            uiMenu.setShowType(menu.getShowType());
            uiMenu.setSysId(menu.getSysId());
            if (StringUtils.isBlank(menu.getSysId()) && StringUtils.isNotBlank(sysId)) {
                uiMenu.setSysId(sysId);
            }
            uiMenu.setChilds(menu.getChilds());
            rtnList.add(uiMenu);
        }
        return rtnList;
    }

    public void callBackMenu(UIMenu menu, String language) {
        String showType = menu.getShowType();
        if ("TAB_NAV".equals(showType) || "FUN".equals(showType) || "FUNS".equals(showType)
                || "FUNS_BLOCK".equals(showType)) {
            menu.setChildren(null);
        }
        // 国际化
        RdmI18nUtil.oneMenuI18n(language, menu);
        if (BeanUtil.isEmpty(menu.getChildren())) {
            return;
        }
        for (Tree subMenu : menu.getChildren()) {
            callBackMenu((UIMenu)subMenu, language);
        }
    }
}
