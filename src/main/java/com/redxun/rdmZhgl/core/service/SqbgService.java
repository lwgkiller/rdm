package com.redxun.rdmZhgl.core.service;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.entity.BpmInst;
import com.redxun.bpm.core.entity.BpmSolution;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmSolutionManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.dao.SaleFileApplyDao;
import com.redxun.rdmZhgl.core.dao.SqbgDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;

import groovy.util.logging.Slf4j;

@Service
@Slf4j
public class SqbgService {
    private Logger logger = LogManager.getLogger(SqbgService.class);
    @Autowired
    private SqbgDao sqbgDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private SaleFileApplyDao saleFileApplyDao;
    @Resource
    private BpmInstManager bpmInstManager;
    @Resource
    private BpmSolutionManager bpmSolutionManager;
    @Autowired
    UserService userService;

    public JsonPageResult<?> querySqbg(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        String applyType = request.getParameter("applyType");
        params.put("applyType", applyType);
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "salefile_change.CREATE_TIME_");
            params.put("sortOrder", "desc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    params.put(name, value);
                }
            }
        }
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        params.put("currentUserId", ContextUtil.getCurrentUserId());
        // addSqbgRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> sqbgList = sqbgDao.querySqbg(params);
        for (JSONObject sqbg : sqbgList) {
            if (sqbg.get("CREATE_TIME_") != null) {
                sqbg.put("CREATE_TIME_", DateUtil.formatDate((Date)sqbg.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(sqbgList, ContextUtil.getCurrentUserId());
        result.setData(sqbgList);
        int countSqbgDataList = sqbgDao.countSqbgList(params);
        result.setTotal(countSqbgDataList);
        return result;
    }

    public void createSqbg(JSONObject formData) {
        formData.put("sqbgId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        sqbgDao.createSqbg(formData);
        if (StringUtils.isNotBlank(formData.getString("detail"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("detail"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String detailId = oneObject.getString("detailId");
                if ("added".equals(state) || StringUtils.isBlank(detailId)) {
                    oneObject.put("detailId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("sqbgId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    sqbgDao.createDetail(oneObject);
                }
            }
        }
    }

    public void updateSqbg(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        sqbgDao.updateSqbg(formData);
        if (StringUtils.isNotBlank(formData.getString("detail"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("detail"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String detailId = oneObject.getString("detailId");
                if ("added".equals(state) || StringUtils.isBlank(detailId)) {
                    oneObject.put("detailId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("sqbgId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    sqbgDao.createDetail(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    sqbgDao.updateDetail(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("detailId", oneObject.getString("detailId"));
                    sqbgDao.deleteDetail(param);
                }
            }
        }
    }

    public void saveSqbgUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        String filePathBase = WebAppUtil.getProperty("sqbgFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find sqbgFilePathBase");
            return;
        }
        try {
            String belongId = toGetParamVal(parameters.get("sqbgId"));
            String fileId = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + belongId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + fileId + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);

            // 写入数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("fileId", fileId);
            fileInfo.put("fileName", fileName);
            fileInfo.put("belongId", belongId);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            sqbgDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getSqbgDetail(String sqbgId) {
        JSONObject detailObj = sqbgDao.querySqbgById(sqbgId);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    public List<JSONObject> getSqbgFileList(String belongId) {
        List<JSONObject> sqbgFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("sqbgId", belongId);
        sqbgFileList = sqbgDao.querySqbgFileList(param);
        return sqbgFileList;
    }

    public List<JSONObject> getDetailList(String belongId) {
        List<JSONObject> sqbgCnList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("sqbgId", belongId);
        sqbgCnList = sqbgDao.queryDetailList(param);
        return sqbgCnList;
    }

    public void deleteOneSqbgFile(String fileId, String fileName, String sqbgId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, sqbgId, WebAppUtil.getProperty("sqbgFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("fileId", fileId);
        sqbgDao.deleteSqbgFile(param);
    }

    public JsonResult deleteSqbg(String sqbgId) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<JSONObject> files = getSqbgFileList(sqbgId);
        String wjFilePathBase = WebAppUtil.getProperty("sqbgFilePathBase");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"),
                oneFile.getString("belongId"), wjFilePathBase);
        }
        rdmZhglFileManager.deleteDirFromDisk(sqbgId, wjFilePathBase);
        Map<String, Object> param = new HashMap<>();
        param.put("sqbgId", sqbgId);
        sqbgDao.deleteSqbgFile(param);
        sqbgDao.deleteSqbg(param);
        sqbgDao.deleteDetail(param);
        return result;
    }

    /**
     * 售前变更流程结束之后，生成一条新的售前文件审批流程
     * 
     * @param request
     * @return
     */
    public JSONObject startDeliveryApproval(HttpServletRequest request) {
        JSONObject result = new JSONObject();
        result.put("success", true);
        IUser realUser = ContextUtil.getCurrentUser();
        try {
            // 生成流程
            String sqbgId = RequestUtil.getString(request, "sqbgId");
            if (StringUtils.isBlank(sqbgId)) {
                result.put("success", false);
                result.put("message", "要变更的流程id为空，请联系管理员处理");
                return result;
            }
            String solId = "";
            BpmSolution bpmSol = bpmSolutionManager.getByKey("SQWJSP", "1");
            if (bpmSol != null) {
                solId = bpmSol.getSolId();
            }
            if (StringUtils.isBlank(solId)) {
                result.put("success", false);
                result.put("message", "未找到售前文件审批流程，请联系管理员处理");
                return result;
            }
            // 根据要售前变更的id，查找内容，拼接cmd
            ProcessStartCmd cmd = new ProcessStartCmd();
            Map<String, Object> param = new HashMap<>();
            param.put("sqbgId", sqbgId);
            JSONObject detailObj = sqbgDao.querySqbgById(sqbgId);
            JSONObject saleFileApplyObj = new JSONObject();
            saleFileApplyObj.put("applyType", detailObj.getString("applyType"));
            saleFileApplyObj.put("CREATE_BY_", detailObj.getString("CREATE_BY_"));
            saleFileApplyObj.put("editorUserId", detailObj.getString("CREATE_BY_"));
            saleFileApplyObj.put("editorUserDeptId", detailObj.getString("editorUserDeptId"));
            saleFileApplyObj.put("fileType", detailObj.getString("fileType"));
            saleFileApplyObj.put("version", detailObj.getString("newVersion"));
            saleFileApplyObj.put("designModel", detailObj.getString("designModel"));
            saleFileApplyObj.put("saleModel", detailObj.getString("saleModel"));
            saleFileApplyObj.put("director", detailObj.getString("director"));
            saleFileApplyObj.put("region", detailObj.getString("region"));
            saleFileApplyObj.put("language", detailObj.getString("language"));
            String jsonData = saleFileApplyObj.toJSONString();
            // 设置变更流程的创建者为审批流程的创建者
            IUser user = userService.getByUserId(detailObj.getString("CREATE_BY_"));
            ContextUtil.setCurUser(user);
            cmd.setSolId(solId);
            cmd.setJsonData(jsonData);
            BpmInst bpmInstNew = bpmInstManager.doStartProcess(cmd);
            // 将变更流程中的文件复制到审批流程中
            List<JSONObject> saleFileList = sqbgDao.querySqbgFileList(param);
            String newapplyId = bpmInstNew.getBusKey();
            for (JSONObject saleFile : saleFileList) {
                copySaveFile(saleFile, newapplyId, detailObj.getString("CREATE_BY_"));
            }
            // 作废原来的审批文件的流程状态
            String id = detailObj.getString("saleId");
            Map<String, Object> params = saleFileApplyDao.getObjectById(id);
            param.clear();
            param.put("instId", params.get("instId").toString());
            sqbgDao.updateStatus(param);
        } catch (Exception e) {
            logger.error("Exception in startDeliveryApproval", e);
            result.put("success", false);
            result.put("message", "流程启动失败，请联系管理员处理");
            return result;
        }
        result.put("message", "售前文件审批流程启动成功");

        // 设置为原来的用户
        ContextUtil.setCurUser(realUser);
        return result;
    }

    // 将变更流程中的文件拷贝到审批流程中
    private void copySaveFile(JSONObject saleFile, String newapplyId, String createBy) {
        String bfileName = saleFile.getString("fileName");
        String bfileId = saleFile.getString("fileId");
        String fileSize = saleFile.getString("fileSize");
        String belongId = saleFile.getString("belongId");
        String bfileBasePath = WebAppUtil.getProperty("sqbgFilePathBase");
        String bsuffix = CommonFuns.toGetFileSuffix(bfileName);
        String relativeFilePath = "";
        if (StringUtils.isNotBlank(belongId)) {
            relativeFilePath = File.separator + belongId;
        }
        String brealFileName = bfileId + "." + bsuffix;
        String bfullFilePath = bfileBasePath + relativeFilePath + File.separator + brealFileName;
        File bfile = new File(bfullFilePath);
        if (!bfile.exists()) {
            logger.error("文件" + bfullFilePath + "不存在");
            return;
        }

        // 向下载目录中写入文件
        String filePath = SysPropertiesUtil.getGlobalProperty("saleFileUrl") + File.separator + newapplyId;
        File pathFile = new File(filePath);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String suffix = CommonFuns.toGetFileSuffix(bfileName);
        String id = IdUtil.getId();
        String fileFullPath = filePath + File.separator + id + "." + suffix;
        File file = new File(fileFullPath);
        try {
            FileCopyUtils.copy(FileUtils.readFileToByteArray(bfile), file);
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("fileName", bfileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("applyId", newapplyId);
            fileInfo.put("CREATE_BY_", createBy);
            fileInfo.put("CREATE_TIME_", new Date());
            fileInfo.put("fileModel", "sq");
            saleFileApplyDao.addSaleFileInfos(fileInfo);
        } catch (IOException e) {
            logger.error("文件加载失败", e.getMessage());
        }
    }
}
