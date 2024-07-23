package com.redxun.standardManager.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.manager.StandardConfigManager;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/standardManager/core/standardConfig/")
public class StandardConfigController extends GenericController {
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private StandardConfigManager standardConfigManager;
    @Resource
    private OsGroupManager osGroupManager;

    @RequestMapping("templateList")
    public ModelAndView templateList(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = getPathView(request);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        IUser currentUser = ContextUtil.getCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        return mv;
    }

    @RequestMapping("templateUploadWindow")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "standardManager/core/standardConfigTemplateUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping("templateSee")
    public ModelAndView templateSee(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = getPathView(request);
        String id = RequestUtil.getString(request, "id");
        Map<String, Object> oneTemplate = null;
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> params = new HashMap<>();
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            params.put("id", id);
            List<Map<String, Object>> templateInfos = standardConfigManager.queryStandardTemplateList(params);
            if (templateInfos != null && !templateInfos.isEmpty()) {
                oneTemplate = templateInfos.get(0);
                String userDepFullName = "";
                // 查找创建人的部门
                OsGroup mainDep = osGroupManager.getMainDeps(oneTemplate.get("CREATE_BY_").toString(),
                        ContextUtil.getCurrentTenantId());
                if (mainDep != null) {
                    // 获取部门的全路径
                    String fullName = osGroupManager.getGroupFullPathNames(mainDep.getGroupId());
                    if (fullName.contains("-")) {
                        String[] fullNames = fullName.split("[-]");
                        for (int i = 1; i < fullNames.length; i++) {
                            if (StringUtil.isEmpty(userDepFullName)) {
                                userDepFullName = fullNames[i];
                            } else {
                                userDepFullName += "-" + fullNames[i];
                            }
                        }
                    } else {
                        userDepFullName = fullName;
                    }
                }
                oneTemplate.put("creatorDepFullName", userDepFullName);
            }
        }
        JSONObject templateJOSNObject = XcmgProjectUtil.convertMap2JsonObject(oneTemplate);
        mv.addObject("templateObj", templateJOSNObject);
        return mv;
    }

    @RequestMapping("templateQuery")
    @ResponseBody
    public List<Map<String, Object>> templateQuery(HttpServletRequest request, HttpServletResponse response) {
        String tenantId = ContextUtil.getCurrentTenantId();
        String docName = RequestUtil.getString(request, "docName");
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", tenantId);
        if (StringUtils.isNotBlank(docName)) {
            params.put("templateName", docName);
        }
        List<Map<String, Object>> fileInfos = standardConfigManager.queryStandardTemplateList(params);
        return fileInfos;
    }

    @RequestMapping(value = "templateUpload")
    @ResponseBody
    public Map<String, Object> templateUpload(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            standardConfigManager.saveStandardTemplate(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    // 删除某个文件（从文件表和磁盘上都删除）
    @RequestMapping("templateDelete")
    public void xcmgDocMgrDelete(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        String relativeFilePath = RequestUtil.getString(request, "relativeFilePath");
        String fileName = RequestUtil.getString(request, "fileName");
        standardConfigManager.deleteStandardTemplate(id, relativeFilePath, fileName);
    }

    // 下载文件
    @RequestMapping("templateDownload")
    public ResponseEntity<byte[]> templateDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            String relativeFilePath = RequestUtil.getString(request, "relativeFilePath");
            String fileName = RequestUtil.getString(request, "fileName");
            String id = RequestUtil.getString(request, "id");
            if (StringUtils.isBlank(fileName)) {
                logger.error("fileName is blank");
                return null;
            }
            if (StringUtils.isBlank(id)) {
                logger.error("id is blank");
                return null;
            }
            String standardTemplatePathBase = WebAppUtil.getProperty("standardTemplatePathBase");
            if (StringUtils.isBlank(standardTemplatePathBase)) {
                logger.error("can't find standardTemplatePathBase");
                return null;
            }
            String fullFilePath = standardTemplatePathBase + File.separator + fileName;
            // 创建文件实例
            File file = new File(fullFilePath);
            // 修改文件名的编码格式
            String downloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");

            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 downloadFileName
            headers.setContentDispositionFormData("attachment", downloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            // 更新下载次数加1
            standardConfigManager.updateTemplateDownloadNum(id);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in templateDownload", e);
            return null;
        }
    }

    @RequestMapping("categoryMg")
    public ModelAndView msgSee(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = getPathView(request);
        IUser currentUser = ContextUtil.getCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        return mv;
    }

    @RequestMapping("categoryQuery")
    @ResponseBody
    public List<Map<String, Object>> categoryQuery(HttpServletRequest request, HttpServletResponse response) {
        return standardConfigManager.categoryQuery();
    }

    @RequestMapping("categorySave")
    @ResponseBody
    public JSONObject categorySave(HttpServletRequest request, @RequestBody String changeGridDataStr,
        HttpServletResponse response) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(changeGridDataStr)) {
            logger.warn("requestBody is blank");
            result.put("message", "保存失败，数据为空！");
            return result;
        }
        standardConfigManager.saveStandardCategory(result, changeGridDataStr);
        if (result.get("message") == null) {
            result.put("message", "保存成功！");
        }
        return result;
    }

    @RequestMapping("belongDepMg")
    public ModelAndView belongDepMg(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = getPathView(request);
        IUser currentUser = ContextUtil.getCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        return mv;
    }

    @RequestMapping("belongDepSave")
    @ResponseBody
    public JSONObject belongDepSave(HttpServletRequest request, @RequestBody String changeGridDataStr,
        HttpServletResponse response) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(changeGridDataStr)) {
            logger.warn("requestBody is blank");
            result.put("message", "保存失败，数据为空！");
            return result;
        }
        standardConfigManager.saveBelongDep(result, changeGridDataStr);
        if (result.get("message") == null) {
            result.put("message", "保存成功！");
        }
        return result;
    }

    @RequestMapping("belongDepQuery")
    @ResponseBody
    public List<Map<String, Object>> belongDepQuery(HttpServletRequest request, HttpServletResponse response) {
        return standardConfigManager.belongDepQuery();
    }
}
