package com.redxun.rdmCommon.core.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.HttpClientUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.menu.UIMenu;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.manager.SysInstManager;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.sys.org.manager.OsUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RdmMobileApiService {
    private static final Logger logger = LoggerFactory.getLogger(RdmMobileApiService.class);
    @Autowired
    private IndexManager indexManager;
    @Autowired
    private OsUserManager osUserManager;
    @Autowired
    private OsGroupManager osGroupManager;
    @Autowired
    private SysInstManager sysInstManager;

    //..初始化用户线程变量
    private void iniCurrentUserInst(JSONObject postDataJson) throws Exception {
        if (!postDataJson.containsKey("username") || StringUtil.isEmpty(postDataJson.getString("username"))) {
            throw new RuntimeException("没有传递账户信息！");
        }
        OsUser mobileUser = osUserManager.getByUserName(postDataJson.getString("username"));
        if (mobileUser == null) {
            throw new RuntimeException("系统无当前账户信息！");
        }
        List<OsGroup> osGroups = osGroupManager.getByUserId(mobileUser.getUserId());
        for (OsGroup osGroup : osGroups) {
            mobileUser.getGroupIds().add(osGroup.getGroupId());
        }
        mobileUser.setTenant(sysInstManager.get("1"));
        ContextUtil.setCurUser(mobileUser);
    }

    //..获取根菜单
    public JSONObject getRootMenu(String postData) throws Exception {
        this.iniCurrentUserInst(JSON.parseObject(postData));
        List<UIMenu> rootList = indexManager.getRootMenu("rdm", true);
        return ResultUtil.result(true, "操作成功", rootList);
    }

    //..根据某个根菜单的ID，获取下面的菜单树
    public JSONObject getBySysId(String postData) throws Exception {
        JSONObject postDataJson = JSON.parseObject(postData);
        this.iniCurrentUserInst(postDataJson);
        List<UIMenu> rootList = indexManager.getBySysId(postDataJson.getString("sysId"), null, true, true);
        return ResultUtil.result(true, "操作成功", rootList);
    }
}
