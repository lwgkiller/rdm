
package com.redxun.world.core.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.world.core.dao.WorldResearchDao;
import com.redxun.world.core.service.WorldResearchService;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.redxun.core.manager.MybatisBaseManager;
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.org.api.model.IUser;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.MybatisListController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;

/**
 * 全球协同研发
 * @author zz
 */
@Controller
@RequestMapping("/world/core/research/")
public class WorldResearchController extends MybatisListController {
    @Resource
    WorldResearchService worldResearchService;
    @Resource
    WorldResearchDao worldResearchDao;
    @Resource
    private RdmZhglFileManager rdmZhglFileManager;
    @Resource
    CommonInfoManager commonInfoManager;
    @Override
    public MybatisBaseManager getBaseManager() {
        return null;
    }
    /**
     * 列表
     * */
    @RequestMapping("listPage")
    public ModelAndView getListPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String jspPath = "world/core/worldResearchList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        String menuType = request.getParameter("menuType");
        String menuFlag = request.getParameter("menuFlag");
        mv.addObject("currentUser", currentUser);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("menuType",menuType);
        mv.addObject("menuFlag",menuFlag);
        JSONObject resultJson = commonInfoManager.hasPermission("QQYFSPY");
        mv.addObject("isFGSH",resultJson.getBoolean("QQYFSPY")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }
   /**
    * 获取编辑页面
    * */
    @RequestMapping("editPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/productEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String mainId = request.getParameter("mainId");
        String action = request.getParameter("action");
        return mv;
    }
    /**
     * 获取list
     * */
    @RequestMapping("getListData")
    @ResponseBody
    public JsonPageResult<?> getListData(HttpServletRequest request, HttpServletResponse response) {
        return worldResearchService.query(request);
    }
    @RequestMapping("fileWindow")
    public ModelAndView workPlanFileWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/productPicList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = RequestUtil.getString(request, "id");
        String mainId = RequestUtil.getString(request, "mainId");
        String editable = RequestUtil.getString(request, "editable");
        String coverContent = RequestUtil.getString(request, "coverContent");
        mv.addObject("coverContent", coverContent);
        mv.addObject("id", id);
        mv.addObject("mainId", mainId);
        mv.addObject("editable", editable);
        return mv;
    }
    /**
     * 模板列表
     * */
    @RequestMapping("templateListPage")
    public ModelAndView getTemplateListPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String jspPath = "world/core/worldTemplateList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        String menuType = request.getParameter("menuType");
        String menuFlag = request.getParameter("menuFlag");
        mv.addObject("menuFlag",menuFlag);
        mv.addObject("currentUser", currentUser);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("menuType",menuType);
        JSONObject resultJson = commonInfoManager.hasPermission("QQYFSPY");
        mv.addObject("permission",resultJson.getBoolean("QQYFSPY")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }
    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "world/core/worldFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        String menuType = request.getParameter("menuType");
        String fileType = request.getParameter("fileType");
        String menuFlag = request.getParameter("menuFlag");
        mv.addObject("menuFlag",menuFlag);
        mv.addObject("menuType",menuType);
        mv.addObject("fileType",fileType);
        return mv;
    }
    @RequestMapping("openTemplateWindow")
    public ModelAndView openTemplateWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "world/core/worldTemplateUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        String menuType = request.getParameter("menuType");
        String fileType = request.getParameter("fileType");
        String menuFlag = request.getParameter("menuFlag");
        mv.addObject("menuFlag",menuFlag);
        mv.addObject("menuType",menuType);
        mv.addObject("fileType",fileType);
        return mv;
    }
    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>(16);
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            worldResearchService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return modelMap;
    }
    @RequestMapping("deleteFiles")
    public void deleteFiles(HttpServletRequest request, HttpServletResponse response) {
        String menuType = RequestUtil.getString(request, "menuType");
        String id = RequestUtil.getString(request, "id");
        String fileName = RequestUtil.getString(request, "fileName");
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        worldResearchService.deleteFileOnDisk(menuType, id, suffix);
        Map<String, Object> fileParams = new HashMap<>(16);
        List<String> fileIds = new ArrayList<>();
        fileIds.add(id);
        fileParams.put("ids", fileIds);
        worldResearchDao.batchDelete(fileParams);
    }
    @RequestMapping("downOrPdfPreview")
    public ResponseEntity<byte[]> downOrPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String relativeFilePath = RequestUtil.getString(request, "relativeFilePath");
        String fileBasePath = SysPropertiesUtil.getGlobalProperty("worldResearch");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, relativeFilePath, fileBasePath);
    }
    @RequestMapping("officePreview")
    public void officePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String fileBasePath = SysPropertiesUtil.getGlobalProperty("worldResearch");
        String relativeFilePath = RequestUtil.getString(request, "relativeFilePath");
        if (StringUtils.isBlank(fileName)) {
            logger.error("文档预览失败，文件名为空！");
            return;
        }
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("文档预览失败，找不到存储路径");
            return;
        }
        if (StringUtils.isBlank(relativeFilePath)) {
            relativeFilePath = "";
        }
        String originalFilePath = fileBasePath + File.separator  + relativeFilePath + File.separator + fileId + "." + suffix;
        String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
        String convertPdfPath = fileBasePath + File.separator + relativeFilePath + File.separator + convertPdfDir + File.separator+ fileId + ".pdf";
        OfficeDocPreview.previewOfficeDoc(originalFilePath, convertPdfPath, response);
    }

    @RequestMapping("imagePreview")
    @ResponseBody
    public void imagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String fileBasePath = SysPropertiesUtil.getGlobalProperty("worldResearch");
        String relativeFilePath = RequestUtil.getString(request, "relativeFilePath");
        if (StringUtils.isBlank(fileName)) {
            logger.error("图片预览失败，文件名为空！");
            return;
        }
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("图片预览失败，找不到存储路径");
            return;
        }
        String originalFilePath = fileBasePath + File.separator  + relativeFilePath + File.separator + fileId + "." + suffix;
        OfficeDocPreview.imagePreview(originalFilePath, response);
    }
    @RequestMapping("submit")
    @ResponseBody
    public JSONObject submit(HttpServletRequest request, HttpServletResponse response) {
        return  worldResearchService.submit(request);
    }
    @RequestMapping("batchAudit")
    @ResponseBody
    public JSONObject batchAudit(HttpServletRequest request, HttpServletResponse response) {
        return  worldResearchService.batchAudit(request);
    }
    @RequestMapping(value = "audit", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getObj(HttpServletRequest request, @RequestBody String postData,
                             HttpServletResponse response) throws Exception {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }
        return worldResearchService.audit(postDataJson);
    }
}
