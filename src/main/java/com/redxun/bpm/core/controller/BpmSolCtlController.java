package com.redxun.bpm.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.redxun.core.json.JsonResult;
import com.redxun.core.manager.BaseManager;
import com.redxun.core.query.QueryFilter;
import com.redxun.saweb.controller.BaseListController;
import com.redxun.saweb.util.QueryFilterBuilder;
import com.redxun.sys.log.LogEnt;
import com.redxun.bpm.core.entity.BpmSolCtl;
import com.redxun.bpm.core.manager.BpmSolCtlManager;

/**
 * [BpmSolCtl]管理
 * @author csx
 */
@Controller
@RequestMapping("/bpm/core/bpmSolCtl/")
public class BpmSolCtlController extends BaseListController{
    @Resource
    BpmSolCtlManager bpmSolCtlManager;
   
	@Override
	protected QueryFilter getQueryFilter(HttpServletRequest request) {
		return QueryFilterBuilder.createQueryFilter(request);
	}
   
    @RequestMapping("del")
    @ResponseBody
    @LogEnt(action = "del", module = "流程", submodule = "资源访问控制权限")
    public JsonResult del(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String uId=request.getParameter("ids");
        if(StringUtils.isNotEmpty(uId)){
            String[] ids=uId.split(",");
            for(String id:ids){
                bpmSolCtlManager.delete(id);
            }
        }
        return new JsonResult(true,"成功删除！");
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
        String pkId=request.getParameter("pkId");
        BpmSolCtl bpmSolCtl=null;
        if(StringUtils.isNotEmpty(pkId)){
           bpmSolCtl=bpmSolCtlManager.get(pkId);
        }else{
        	bpmSolCtl=new BpmSolCtl();
        }
        return getPathView(request).addObject("bpmSolCtl",bpmSolCtl);
    }
    
    
    @RequestMapping("edit")
    public ModelAndView edit(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	String pkId=request.getParameter("pkId");
    	//复制添加
    	String forCopy=request.getParameter("forCopy");
    	BpmSolCtl bpmSolCtl=null;
    	if(StringUtils.isNotEmpty(pkId)){
    		bpmSolCtl=bpmSolCtlManager.get(pkId);
    		if("true".equals(forCopy)){
    			bpmSolCtl.setRightId(null);
    		}
    	}else{
    		bpmSolCtl=new BpmSolCtl();
    	}
    	return getPathView(request).addObject("bpmSolCtl",bpmSolCtl);
    }

	@SuppressWarnings("rawtypes")
	@Override
	public BaseManager getBaseManager() {
		return bpmSolCtlManager;
	}

}
