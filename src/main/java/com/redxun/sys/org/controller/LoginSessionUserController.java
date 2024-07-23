
package com.redxun.sys.org.controller;

import com.redxun.core.cache.CacheUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.manager.MybatisBaseManager;
import com.redxun.core.query.QueryFilter;
import com.redxun.core.util.BeanUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.saweb.controller.MybatisListController;
import com.redxun.saweb.util.QueryFilterBuilder;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.log.LogEnt;
import com.redxun.sys.org.entity.LoginSessionUser;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.sys.org.manager.LoginSessionUserManager;
import com.redxun.sys.org.manager.OsGroupManager;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * LOGIN_SESSION_USER控制器
 * @author szw
 */
@Controller
@RequestMapping("/sys/org/loginSessionUser/")
public class LoginSessionUserController extends MybatisListController {
    protected Logger LOG= LogManager.getLogger(LoginSessionUserController.class);
    @Resource
    LoginSessionUserManager loginSessionUserManager;
    @Resource
    OsGroupManager osGroupManager;

	@Override
	protected QueryFilter getQueryFilter(HttpServletRequest request) {
		QueryFilter queryFilter = QueryFilterBuilder.createQueryFilter(request);
		queryFilter.addFieldParam("SESSION_TYPE_", LoginSessionUser.LOGIN_TYPE);
		return queryFilter;
	}

    /**
     * 终止登陆用户
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("removeLogUser")
    @ResponseBody
    @LogEnt(action = "removeLogUser", module = "流程", submodule = "任务待办")
    public JsonResult removeLogUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HashMap mymap =(HashMap) CacheUtil.getCache(LoginSessionUser.USERNO_MAP_SESSION);

        String pkIds = request.getParameter("pkIds");
        String[] ids = pkIds.split("[,]");
        for (String id:ids) {
            LoginSessionUser user =loginSessionUserManager.get(id);
            if(user!=null){
                HttpSession oldSession =(HttpSession)mymap.get(user.getSessionId());
                //已经存在登陆，旧session失效
                if(oldSession!=null){
                    try {
                        oldSession.invalidate();
                    }catch (Exception e){
                        LOG.error("---LoginSessionUserController.removeLogUser is error: "+e.getMessage());
                    }
                }
            }
        }
        return new JsonResult(true, "成功终止用户");
    }

    /**
     * 对话框的用户搜索
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("getLoginCount")
    @ResponseBody
    public JsonPageResult getLoginCount(HttpServletRequest request, HttpServletResponse response) throws Exception{
        List<LoginSessionUser> list = new ArrayList<>();
        Integer ints=loginSessionUserManager.getLoginCount();
        LoginSessionUser user =new LoginSessionUser();
        user.setControlNum(LoginSessionUser.OULOGIN_NUM);
        user.setOccupyNum(ints);
        user.setRemainderNum(LoginSessionUser.OULOGIN_NUM-ints);

        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        String result = numberFormat.format((float)ints/(float) LoginSessionUser.OULOGIN_NUM*100);
        user.setOccupancyRate(result+"%");
        list.add(user);
        return new JsonPageResult(list, list.size());
    }

    /**
     * 对话框的用户搜索
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("getLoginList")
    @ResponseBody
    public JsonPageResult<LoginSessionUser> getLoginList(HttpServletRequest request, HttpServletResponse response) throws Exception{

        String fullname=request.getParameter("fullname");
        String userNo=request.getParameter("userNo");

        QueryFilter queryFilter= QueryFilterBuilder.createQueryFilter(request);

        if(StringUtils.isNotEmpty(fullname)){
            queryFilter.addParam("fullname", "%" + fullname + "%");
        }
        if(StringUtils.isNotEmpty(userNo)){
            queryFilter.addParam("userNo", "%" +userNo + "%");
        }
        List<LoginSessionUser> list=loginSessionUserManager.getLoginList(queryFilter);
        if(list!=null){
            for (LoginSessionUser user:list) {
                //获得主部门
                OsGroup mainDep=osGroupManager.getMainDeps(user.getUserId(),"1");
                if(mainDep!=null){
                    //获取部门的全路径
                    String fullName = osGroupManager.getGroupFullPathNames(mainDep.getGroupId());
                    if(fullName.contains("-")){
                        String [] fullNames =fullName.split("[-]");
                        user.setOrgGroup(fullNames[0]);
                        String sb="";
                        for(int i=1;i<fullNames.length;i++){
                            if(StringUtil.isEmpty(sb)){
                                sb=fullNames[i];
                            }else {
                                sb+="-"+fullNames[i];
                            }
                        }
                        user.setUserOrgGroup(sb);
                    }else {
                        user.setOrgGroup(fullName);
                        user.setUserOrgGroup(fullName);
                    }
                }
            }
        }
        return new JsonPageResult<LoginSessionUser>(list, queryFilter.getPage().getTotalItems());
    }


    @RequestMapping("del")
    @ResponseBody
    @LogEnt(action = "del", module = "sys", submodule = "LOGIN_SESSION_USER")
    public JsonResult del(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String uId= RequestUtil.getString(request, "ids");
        if(StringUtils.isNotEmpty(uId)){
            String[] ids=uId.split(",");
            for(String id:ids){
                loginSessionUserManager.delete(id);
            }
        }
        return new JsonResult(true,"成功删除!");
    }
    
    /**
     * 查看明细
     * @param request
     * @param response
     * @return
     * @throws Exception 
     */
    @RequestMapping("get")
    public ModelAndView get(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String pkId= RequestUtil.getString(request, "pkId");
        LoginSessionUser loginSessionUser=null;
        if(StringUtils.isNotEmpty(pkId)){
           loginSessionUser=loginSessionUserManager.get(pkId);
        }else{
        	loginSessionUser=new LoginSessionUser();
        }
        return getPathView(request).addObject("loginSessionUser",loginSessionUser);
    }
    
    
    @RequestMapping("edit")
    public ModelAndView edit(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	String pkId= RequestUtil.getString(request, "pkId");
    	LoginSessionUser loginSessionUser=null;
    	if(StringUtils.isNotEmpty(pkId)){
    		loginSessionUser=loginSessionUserManager.get(pkId);
    	}else{
    		loginSessionUser=new LoginSessionUser();
    	}
    	return getPathView(request).addObject("loginSessionUser",loginSessionUser);
    }
    
    /**
     * 有子表的情况下编辑明细的json
     * @param request
     * @param response
     * @return
     * @throws Exception 
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("getJson")
    @ResponseBody
    public LoginSessionUser getJson(HttpServletRequest request, HttpServletResponse response) throws Exception{
    	String uId= RequestUtil.getString(request, "ids");
        LoginSessionUser loginSessionUser = loginSessionUserManager.getLoginSessionUser(uId);
        return loginSessionUser;
    }
    
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    @LogEnt(action = "save", module = "sys", submodule = "LOGIN_SESSION_USER")
    public JsonResult save(HttpServletRequest request, @RequestBody LoginSessionUser loginSessionUser, BindingResult result) throws Exception  {

        if (result.hasFieldErrors()) {
            return new JsonResult(false, getErrorMsg(result));
        }
        String msg = null;
        /*if (StringUtils.isEmpty(loginSessionUser.getId())) {
            loginSessionUserManager.create(loginSessionUser);
            msg = getMessage("loginSessionUser.created", new Object[]{loginSessionUser.getIdentifyLabel()}, "[LOGIN_SESSION_USER]成功创建!");
        } else {
        	String id=loginSessionUser.getId();
        	LoginSessionUser oldEnt=loginSessionUserManager.get(id);
        	BeanUtil.copyNotNullProperties(oldEnt, loginSessionUser);
            loginSessionUserManager.update(oldEnt);
       
            msg = getMessage("loginSessionUser.updated", new Object[]{loginSessionUser.getIdentifyLabel()}, "[LOGIN_SESSION_USER]成功更新!");
        }*/
        //saveOpMessage(request, msg);
        return new JsonResult(true, msg);
    }

	@SuppressWarnings("rawtypes")
	@Override
	public MybatisBaseManager getBaseManager() {
		return loginSessionUserManager;
	}
}
