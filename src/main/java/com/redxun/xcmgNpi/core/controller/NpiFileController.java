package com.redxun.xcmgNpi.core.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgNpi.core.manager.NpiFileManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/xcmgNpi/core/npiFile/")
public class NpiFileController {
    private static final Logger logger = LoggerFactory.getLogger(NpiFileController.class);

    @Autowired
    private NpiFileManager npiFileManager;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    @RequestMapping("listPage")
    public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "xcmgNpi/core/npiFileListPage.jsp";
        boolean isOperator = commonInfoManager.judgeUserIsPointRole(RdmConst.NPIFILEOP, ContextUtil.getCurrentUserId());
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("isOperator", isOperator);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    // 列表查询
    @RequestMapping("queryList")
    @ResponseBody
    public JsonPageResult<?> queryNpiFileList(HttpServletRequest request, HttpServletResponse response) {
        return npiFileManager.queryNpiFileList(request, true);
    }

    // 根据体系类别查询体系
    @RequestMapping("treeQuery")
    @ResponseBody
    public List<JSONObject> treeQuery(HttpServletRequest request, HttpServletResponse response) {
        JSONObject param = new JSONObject();
        List<JSONObject> systemInfos = npiFileManager.systemQuery(param);
        return systemInfos;
    }

    @RequestMapping("getDicInfos")
    @ResponseBody
    public List<JSONObject> getDicInfos(HttpServletRequest request, HttpServletResponse response) {
        List<SysDic> stageInfos = sysDicManager.getByTreeKey("ZLSSJD");
        List<JSONObject> result = new ArrayList<>();
        if (stageInfos != null && !stageInfos.isEmpty()) {
            for (SysDic sysDic : stageInfos) {
                JSONObject object = new JSONObject();
                object.put("dicId", sysDic.getDicId());
                object.put("name", sysDic.getName() + "【" + sysDic.getKey() + "】");
                result.add(object);
            }
        }
        return result;
    }

    @RequestMapping("pdfPreviewOrDownload")
    public ResponseEntity<byte[]> pdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String applyId = RequestUtil.getString(request, "formId");
        String fileBasePath = SysPropertiesUtil.getGlobalProperty("npiFileDir");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, applyId, fileBasePath);
    }

    // 新增和编辑页面
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "xcmgNpi/core/npiFileEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String recordId = RequestUtil.getString(request, "recordId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("action", action);
        mv.addObject("recordId", recordId);
        return mv;
    }

    @RequestMapping("queryNpiFileById")
    @ResponseBody
    public JSONObject queryNpiFileById(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        if (StringUtils.isBlank(id)) {
            return new JSONObject();
        }
        return npiFileManager.queryNpiFileById(id);
    }

    // 保存（包括新增、编辑）
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "保存成功！");
        npiFileManager.saveNpiFile(result, request);
        return result;
    }

    // 删除
    @RequestMapping("delete")
    @ResponseBody
    public JSONObject deleteStandard(HttpServletRequest request, @RequestBody String requestBody,
        HttpServletResponse response) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(requestBody)) {
            logger.error("requestBody is blank");
            result.put("message", "删除失败，消息体为空！");
            return result;
        }
        JSONObject requestBodyObj = JSONObject.parseObject(requestBody);
        if (StringUtils.isBlank(requestBodyObj.getString("ids"))) {
            logger.error("ids is blank");
            result.put("message", "删除失败，主键为空！");
            return result;
        }
        String npiFileIds = requestBodyObj.getString("ids");
        npiFileManager.deleteNpiFile(result, npiFileIds);
        return result;
    }

    // 导出
    @RequestMapping("exportExcel")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        npiFileManager.exportNpiFileExcel(request, response);
    }

}
