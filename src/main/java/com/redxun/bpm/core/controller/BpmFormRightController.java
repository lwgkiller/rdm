
package com.redxun.bpm.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.bpm.form.entity.BpmFormView;
import com.redxun.bpm.form.manager.BpmFormViewManager;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.entity.BpmFormRight;
import com.redxun.bpm.core.manager.BpmFormRightManager;
import com.redxun.core.json.JsonResult;
import com.redxun.core.manager.BaseManager;
import com.redxun.core.query.QueryFilter;
import com.redxun.core.util.StringUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.TenantListController;
import com.redxun.saweb.util.QueryFilterBuilder;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.log.LogEnt;

import java.util.List;

/**
 * 表单权限控制器
 * @author ray
 */
@Controller
@RequestMapping("/bpm/core/bpmFormRight/")
public class BpmFormRightController extends TenantListController{
    @Resource
    BpmFormRightManager bpmFormRightManager;
    @Resource
    BpmFormViewManager bpmFormViewManager;

	@Override
	protected QueryFilter getQueryFilter(HttpServletRequest request) {
		return QueryFilterBuilder.createQueryFilter(request);
	}
   
    @RequestMapping("del")
    @ResponseBody
    @LogEnt(action = "del", module = "bpm", submodule = "表单权限")
    public JsonResult del(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String uId=RequestUtil.getString(request, "ids");
        if(StringUtils.isNotEmpty(uId)){
            String[] ids=uId.split(",");
            for(String id:ids){
                bpmFormRightManager.delete(id);
            }
        }
        return new JsonResult(true,"成功删除!");
    }
    
    @RequestMapping("clearRight")
    @ResponseBody
    @LogEnt(action = "clearRight", module = "bpm", submodule = "表单权限")
    public JsonResult clearRight(BpmFormRight bpmFormRight) throws Exception{
    	bpmFormRightManager.delBySolForm(bpmFormRight);
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
        String pkId=RequestUtil.getString(request, "pkId");
        BpmFormRight bpmFormRight=null;
        if(StringUtils.isNotEmpty(pkId)){
           bpmFormRight=bpmFormRightManager.get(pkId);
        }else{
        	bpmFormRight=new BpmFormRight();
        }
        return getPathView(request).addObject("bpmFormRight",bpmFormRight);
    }
    
    
    @RequestMapping("edit")
    public ModelAndView edit(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	String pkId=RequestUtil.getString(request, "pkId");
    	BpmFormRight bpmFormRight=null;
    	if(StringUtils.isNotEmpty(pkId)){
    		bpmFormRight=bpmFormRightManager.get(pkId);
    	}else{
    		bpmFormRight=new BpmFormRight();
    	}
    	return getPathView(request).addObject("bpmFormRight",bpmFormRight);
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
    public String getJson(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	String uId=RequestUtil.getString(request, "ids");    	
        BpmFormRight bpmFormRight = bpmFormRightManager.getBpmFormRight(uId);
        String json = JSONObject.toJSONString(bpmFormRight);
        return json;
    }

    /**
     * 根据流程定义，解决方案Id,表单bo定义Id进行相应的参数初始化
     * @param request
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping("doInitFormRights")
    @ResponseBody
	public JsonResult doInitFormRights(HttpServletRequest request){
        String solId=request.getParameter("solId");
        String actDefId=request.getParameter("actDefId");
        String boDefId=request.getParameter("boDefId");

        List<BpmFormView> bpmFormViews=bpmFormViewManager.getByBoDefIdMainVersion(boDefId);
        if(bpmFormViews==null || bpmFormViews.size()==0){
            return new JsonResult(false);
        }
        BpmFormView bpmFormView=bpmFormViews.get(0);
        String formAlias=bpmFormView.getKey();
        List<String> nodeIds=bpmFormRightManager.doInitSolFormRights(solId,actDefId,boDefId,formAlias);

        JSONObject formObject=new JSONObject();
        formObject.put("formName",bpmFormView.getName());
        formObject.put("isAll","true");
        formObject.put("type",bpmFormView.getType());
        formObject.put("formAlias",bpmFormView.getKey());
        formObject.put("nodeIds",nodeIds);
        return new JsonResult(true,"success",formObject);
    }

	
	@RequestMapping("getRightJson")
    @ResponseBody
	public JSONObject getRightJson(HttpServletRequest request,HttpServletResponse response) throws Exception{

		String actDefId=RequestUtil.getString(request, "actDefId");
		String solId=RequestUtil.getString(request, "solId");
		String nodeId=RequestUtil.getString(request, "nodeId");
		String formAlias=RequestUtil.getString(request, "formAlias");
		String tenantId=ContextUtil.getCurrentTenantId();
		
		if(StringUtil.isEmpty(solId)) {
			nodeId=BpmFormRight.NODE_FORM;
		}
		//获取节点配置
		JSONObject rtn= bpmFormRightManager.obtainRight(tenantId, solId, actDefId, nodeId, formAlias);
		return rtn;
    }

	@Override
	public BaseManager getBaseManager() {
		return bpmFormRightManager;
	}
}
