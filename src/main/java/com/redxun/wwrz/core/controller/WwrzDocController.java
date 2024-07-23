
package com.redxun.wwrz.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.wwrz.core.dao.WwrzDocDao;
import com.redxun.wwrz.core.service.WwrzDocService;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.service.YfjbProductCostService;
import com.redxun.saweb.context.ContextUtil;

import java.util.List;

/**
 * 委外认证 认证资料维护
 * @author zz
 */
@Controller
@RequestMapping("/wwrz/core/doc/")
public class WwrzDocController {
    @Resource
    WwrzDocService wwrzDocService;
    @Resource
    WwrzDocDao wwrzDocDao;
    @Resource
    CommonInfoManager commonInfoManager;
    @RequestMapping("listPage")
    public ModelAndView getProcessPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String jspPath = "wwrz/core/wwrzDocList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        return mv;
    }
    @RequestMapping(value = "dealData", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject dealData(HttpServletRequest request, @RequestBody String changeGridDataStr,
                               HttpServletResponse response) {
        return wwrzDocService.saveOrUpdateItem(changeGridDataStr);
    }
    @RequestMapping("listData")
    @ResponseBody
    public JsonPageResult<?> listData(HttpServletRequest request, HttpServletResponse response) {
        return wwrzDocService.query(request);
    }
    @RequestMapping("getDicDoc")
    @ResponseBody
    public List<JSONObject> getDicDoc(HttpServletRequest request, HttpServletResponse response) {
        return wwrzDocService.getDocListByType(request);
    }
    @RequestMapping(value = "documentList", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getDocumentList(HttpServletRequest request, @RequestBody String changeGridDataStr,
                               HttpServletResponse response) {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(changeGridDataStr)) {
            postDataJson = JSONObject.parseObject(changeGridDataStr);
        }else{
            return ResultUtil.result(false,"请求参数不能为空",null);

        }
        String docType = postDataJson.getString("docType");
        List<JSONObject> list = wwrzDocDao.getDocListByType(docType);
        return ResultUtil.result(true,"成功",list);
    }
    @RequestMapping(value = "docListByApplyId", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getDocListByApplyId(HttpServletRequest request, @RequestBody String changeGridDataStr,
                                      HttpServletResponse response) {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(changeGridDataStr)) {
            postDataJson = JSONObject.parseObject(changeGridDataStr);
        }else{
            return ResultUtil.result(false,"请求参数不能为空",null);

        }
        String applyId = postDataJson.getString("applyId");
        List<JSONObject> list = wwrzDocDao.getDocListByApplyId(applyId);
        return ResultUtil.result(true,"成功",list);
    }
    /**
     * 模板下载
     * */
    @RequestMapping("/importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return wwrzDocService.importTemplateDownload();
    }
    /**
     * 批量导入
     * */
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        wwrzDocService.importDocument(result, request);
        return result;
    }
}
