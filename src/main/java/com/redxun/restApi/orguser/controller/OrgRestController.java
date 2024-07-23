package com.redxun.restApi.orguser.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.redxun.core.constants.MBoolean;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.BeanUtil;
import com.redxun.restApi.orguser.entity.Group;
import com.redxun.restApi.orguser.entity.TenantEntity;
import com.redxun.saweb.util.IdUtil;
import com.redxun.sys.core.entity.SysInst;
import com.redxun.sys.core.manager.SysInstManager;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.sys.org.manager.OsInstUsersManager;
import com.redxun.sys.org.manager.OsRelInstManager;
import com.redxun.sys.org.manager.OsUserManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 模块：jsaas
 * 包名：com.redxun.restApi.orguser.controller
 * 功能描述：实现组织架构的同步处理
 *
 * @author：csx
 * @date:2019/7/12
 */
@RequestMapping("/sys/org/rest/")
@RestController
public class OrgRestController {
    private Logger logger=LoggerFactory.getLogger(OrgRestController.class);
    @Resource
    SysInstManager sysInstManager;
    @Resource
    OsGroupManager osGroupManager;
    @Resource
    OsUserManager osUserManager;
    @Resource
    OsRelInstManager osRelInstManager;
    @Resource
    OsInstUsersManager osInstUsersManager;

     /**
     * 同步机构
     * @param tenantEntity
     * @return
     */
    @RequestMapping("syncSysInst")
    public JsonResult syncSysInst(@RequestBody TenantEntity tenantEntity){
        if(StringUtils.isEmpty(tenantEntity.getDomain())){
            return new JsonResult<>(false,"domain不能为空！");
        }
        if(StringUtils.isEmpty(tenantEntity.getNameCn())){
            return new JsonResult(false,"机构中文名不能为空！");
        }

        SysInst sysInst=sysInstManager.getByDomain(tenantEntity.getDomain());

        if(sysInst!=null && !sysInst.getInstId().equals(tenantEntity.getInstId())){
           return new JsonResult(false,"机构域名"+tenantEntity.getDomain()+"已经存在！");
        }

        if(StringUtils.isEmpty(tenantEntity.getInstId())
                || sysInstManager.get(tenantEntity.getInstId())==null){
            sysInst=new SysInst();
            sysInst.setInstId(IdUtil.getId());
            BeanUtil.copyProperties(sysInst,tenantEntity);
            sysInst.setStatus(MBoolean.ENABLED.name());
            sysInstManager.create(sysInst);
        }else{
            sysInst=sysInstManager.get(tenantEntity.getInstId());
            try {
                BeanUtil.copyNotNullProperties(sysInst, tenantEntity);
                sysInstManager.update(sysInst);
            }catch(Exception e){
                logger.error(e.getMessage());
            }
        }
        return new JsonResult(true,"成功同步机构！",sysInst);
    }

    /**
     * 创建所有部门
     * {
     * 	tenantId:'1',
     * 	dimId:'1',
     * 	groups:[
     *                {
     * 			groupId:'001',
     * 			name:'A01',
     * 			key:'A01'
     * 			parentId:'0'
     *        },
     *        {
     * 			groupId:'002',
     * 			name:'',
     * 			key:''
     * 			parentId:'001'
     *        }
     * 	]
     * }
     *
     * @return
     */
    @RequestMapping("syncGroups")
    public JsonResult syncGroups(@RequestBody JSONObject groupsObj){
        String tenantId=groupsObj.getString("tenantId");
        String dimId= groupsObj.getString("dimId");
        JSONArray jsonArr=groupsObj.getJSONArray("groups");

        List<OsGroup> groups=JSONArray.parseArray(jsonArr.toJSONString(),OsGroup.class);

        Map<String,OsGroup> pathMaps=new HashMap<>();

        for(OsGroup g:groups){

            if(StringUtils.isNotEmpty(g.getGroupId())){
                OsGroup nGroup=osGroupManager.get(g.getGroupId());
                if(nGroup!=null) {
                    try {
                        BeanUtil.copyNotNullProperties(nGroup, g);
                        osGroupManager.update(nGroup);
                        pathMaps.put(nGroup.getGroupId(), nGroup);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    continue;
                }
            }

            OsGroup nGroup=new OsGroup();
            try {
                BeanUtil.copyNotNullProperties(nGroup, g);
            }catch (Exception e){
                logger.error(e.getMessage());
            }
            nGroup.setSn(1);
            nGroup.setStatus(MBoolean.ENABLED.name());
            nGroup.setDimId(dimId);
            nGroup.setTenantId(tenantId);
            if(StringUtils.isEmpty(nGroup.getGroupId())) {
                nGroup.setGroupId(IdUtil.getId());
            }
            //若为根目录，则更新其paretnId及path
            if(StringUtils.isEmpty(g.getParentId())){
                nGroup.setParentId("0");
                nGroup.setPath("0."+nGroup.getGroupId()+".");
            }
            nGroup.setChilds(0);
            osGroupManager.create(nGroup);
            pathMaps.put(nGroup.getGroupId(),nGroup);
        }

        //更新其路径
        Iterator<OsGroup> it=pathMaps.values().iterator();
        while(it.hasNext()){
            OsGroup group=it.next();
            handleGroupPath(group,pathMaps);
        }

        return new JsonResult(true,"成功创建部门");
    }

    /**
     * 处理组的路径
     * @param osGroup
     * @param pathMap
     */
    private void handleGroupPath(OsGroup osGroup,Map<String,OsGroup> pathMap){
        if(StringUtils.isNotEmpty(osGroup.getPath())){
            return;
        }
        if(StringUtils.isEmpty(osGroup.getParentId()) || "0".equals(osGroup.getParentId())){
            osGroup.setPath("0."+osGroup.getGroupId()+".");
            osGroup.setParentId("0");
            osGroupManager.update(osGroup);
        }else{
            OsGroup parentGroup=pathMap.get(osGroup.getParentId());
            //找到父组
            if(parentGroup==null){
                parentGroup=osGroupManager.get(osGroup.getParentId());
                if(parentGroup!=null) {
                    pathMap.put(parentGroup.getGroupId(), parentGroup);
                }
            }

            if(parentGroup!=null){
                handleGroupPath(parentGroup,pathMap);
                parentGroup.setChilds(1);
                osGroup.setPath(parentGroup.getPath()+ osGroup.getGroupId()+".");
                osGroupManager.update(osGroup);
                osGroupManager.update(parentGroup);
            }
        }
    }

    /**
     *  同步用户
     *
     * {
     * 	tenantId:'',
     * 	users:[
     * 		{
     * 			userId:'',
     * 			fullname:'',
     * 			userNo:'',
     * 			pwd:'',
     * 			userType:'',
     * 			sex:'',
     * 			mobile:'',
     * 			email:'',
     * 			address:'',
     * 			status:'',
     * 			depId:''//部门Id
     * 		}
     * 	]
     * }
     *
     * @param usersObj
     * @return
     */
    @RequestMapping("syncUsers")
    public JsonResult syncUsers(@RequestBody JSONObject usersObj){
        String tenantId=usersObj.getString("tenantId");
        SysInst sysInst=sysInstManager.get(tenantId);
        String domain=sysInst!=null?sysInst.getDomain():"";
        List<OsUser> osUsers=JSONArray.parseArray(usersObj.getJSONArray("users").toJSONString(),OsUser.class);
        for(OsUser osUser:osUsers){
            //若推过来的带有userId
            if(StringUtils.isNotEmpty(osUser.getUserId())){
                OsUser tmp=osUserManager.get(osUser.getUserId());
                if(tmp!=null){
                    try {
                        BeanUtil.copyNotNullProperties(tmp, osUser);
                    }catch(Exception ex){
                        logger.debug(ex.getMessage());
                    }
                }else{
                    osUser.setTenantId(tenantId);
                    osUser.setDomain(domain);
                    osUser.setDefaultDomain(domain);
                    osUserManager.create(osUser);
                }
            }else{
                osUser.setUserId(IdUtil.getId());
                osUser.setTenantId(tenantId);
                osUserManager.create(osUser);
            }
            //加至机构Id
            osInstUsersManager.createFormOsUser(osUser,domain);
            //加入至部门
            osRelInstManager.addBelongRelInst(osUser.getDepId(),osUser.getUserId(),"YES");
        }
        return new JsonResult(true,"成功同步用户!");
    }

    /**
     * 同步组内用户关系
     * @param resManObj
     * @return
     */
    @RequestMapping("syncRepMans")
    public JsonResult syncRepMans(@RequestBody JSONObject resManObj){
        String tenantId=resManObj.getString("tenantId");
        JSONArray repMans=resManObj.getJSONArray("groupMans");

        for(int i=0;i<repMans.size();i++) {
            JSONObject rowObj=repMans.getJSONObject(i);
            String groupId=rowObj.getString("groupId");
            String userId=rowObj.getString("userId");
            osRelInstManager.addGroupUserLeader(groupId, userId,tenantId);
        }

        return new JsonResult(true,"成功同步组负责人");
    }

}

