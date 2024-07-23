package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.BpmInst;
import com.redxun.bpm.core.entity.BpmSolution;
import com.redxun.bpm.core.entity.ProcessMessage;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmSolutionManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExceptionUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.controller.RdmApiController;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.BucketConfigurationDao;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Service
public class BucketConfigurationService {
    private static Logger logger = LoggerFactory.getLogger(BucketConfigurationService.class);
    @Autowired
    private BucketConfigurationDao bucketConfigurationDao;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private BpmSolutionManager bpmSolutionManager;
    @Autowired
    private UserService userService;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        CommonFuns.getSearchParam(params, request, true);
        List<JSONObject> businessList = bucketConfigurationDao.dataListQuery(params);
        //查询当前处理人
        xcmgProjectManager.setTaskCurrentUserJSON(businessList);
        int businessListCount = bucketConfigurationDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public List<JSONObject> getFileListInfos(JSONObject params) {
        List<JSONObject> fileList = new ArrayList<>();
        fileList = bucketConfigurationDao.getFileListInfos(params);
        return fileList;
    }

    //..
    public JsonResult saveFiles(HttpServletRequest request) throws Exception {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return new JsonResult(false, "没有找到上传的参数");
        }
        //多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return new JsonResult(false, "没有找到上传的文件");
        }
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition",
                "bucketConfiguration").getValue();
        if (StringUtil.isEmpty(filePathBase)) {
            logger.error("没有找到上传的路径");
            return new JsonResult(false, "没有找到上传的路径");
        }
        try {
            String businessId = toGetParamVal(parameters.get("businessId"));
            String businessType = toGetParamVal(parameters.get("businessType"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + businessId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);
            // 写入数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("businessId", businessId);
            fileInfo.put("businessType", businessType);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            bucketConfigurationDao.insertFileInfos(fileInfo);
            return new JsonResult(true, "上传成功");
        } catch (Exception e) {
            logger.error("Exception in saveFiles", e);
            throw e;
        }
    }

    //..
    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    //..
    public void deleteFile(String fileId, String fileName, String businessId, String businessType) {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "bucketConfiguration").getValue();
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, businessId, filePathBase);
        JSONObject params = new JSONObject();
        params.put("id", fileId);
        bucketConfigurationDao.deleteFileInfos(params);
    }

    //..
    public void createBusiness(JSONObject formData) {
        try {
            formData.put("id", IdUtil.getId());
            formData.put("businessStatus", "A");
            if (!formData.containsKey("isAuto") || StringUtil.isEmpty(formData.getString("isAuto"))) {
                formData.put("isAuto", "false");
            }
            formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            formData.put("CREATE_TIME_", new Date());
            bucketConfigurationDao.insertBusiness(formData);
        } catch (Exception e) {
            throw e;
        }
    }

    //..
    public void updateBusiness(JSONObject formData) {
        try {
            formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            formData.put("UPDATE_TIME_", new Date());
            bucketConfigurationDao.updateBusiness(formData);
        } catch (Exception e) {
            throw e;
        }
    }

    //..
    public JsonResult deleteBusiness(String[] ids, String[] instIds) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        JSONObject param = new JSONObject();
        param.put("businessIds", businessIds);
        List<JSONObject> files = bucketConfigurationDao.getFileListInfos(param);
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "bucketConfiguration").getValue();
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("businessId"), filePathBase);
        }
        for (String oneBusinessId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneBusinessId, filePathBase);
        }
        bucketConfigurationDao.deleteFileInfos(param);
        bucketConfigurationDao.deleteBusiness(param);
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }

    //..
    public JSONObject getDetail(String businessId) {
        JSONObject jsonObject = bucketConfigurationDao.queryDetailById(businessId);
        if (jsonObject == null) {
            JSONObject newjson = new JSONObject();
            newjson.put("repUserId", ContextUtil.getCurrentUserId());
            newjson.put("repUser", ContextUtil.getCurrentUser().getFullname());
            newjson.put("applyTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
            newjson.put("isAuto", "false");
            return newjson;
        }
        return jsonObject;
    }

    //..
    public void createAuto(JSONObject result, JSONObject businessData, String whatTypeToStart) throws Exception {
        logger.error("向RDM创建质量改进建议流程开始");
        //查找铲斗选配的solutionid
        String solId = this.synGetSolId(result);
        if (!result.getBoolean("success")) {
            return;
        }
        //启动流程
        JsonResult processStartResult = this.synStartProcess(solId, businessData, whatTypeToStart);
        if (!processStartResult.getSuccess()) {
            result.put("success", false);
            result.put("message", processStartResult.getMessage() + ":" + processStartResult.getData().toString());
            return;
        }
    }

    //..
    private String synGetSolId(JSONObject result) {
        BpmSolution bpmSol = bpmSolutionManager.getByKey("bucketConfiguration", "1");
        String solId = "";
        if (bpmSol != null) {
            solId = bpmSol.getSolId();
        } else {
            result.put("success", false);
            result.put("message", "创建失败,铲斗选配的流程方案获取出现异常！！");
        }
        return solId;
    }

    //..
    private JsonResult synStartProcess(String solId, JSONObject postData, String whatTypeToStart) throws Exception {
        ProcessMessage handleMessage = new ProcessMessage();
        BpmInst bpmInst = null;
        JsonResult result = new JsonResult();
        IUser currentUser = ContextUtil.getCurrentUser();//暂存
        try {
            IUser user = userService.getByUserId(postData.getString("repUserId"));
            ContextUtil.setCurUser(user);
            ProcessHandleHelper.setProcessMessage(handleMessage);
            ProcessStartCmd startCmd = new ProcessStartCmd();
            startCmd.setSolId(solId);
            startCmd.setJsonData(postData.toJSONString());
            //启动流程
            if (whatTypeToStart.equalsIgnoreCase(RdmApiController.SAVE_DRAFT)) {
                bpmInstManager.doSaveDraft(startCmd);
            } else if (whatTypeToStart.equalsIgnoreCase(RdmApiController.START_PROCESS)) {
                bpmInstManager.doStartProcess(startCmd);
            } else {
                throw new Exception("没有收到具体的启动指令");
            }
            result.setData(startCmd.getBusinessKey());
        } catch (Exception ex) {
            //把具体的错误放置在内部处理，以显示正确的错误信息提示，在此不作任何的错误处理
            logger.error(ExceptionUtil.getExceptionMessage(ex));
            if (handleMessage.getErrorMsges().size() == 0) {
                handleMessage.addErrorMsg(ex.getMessage());
            }
        } finally {
            //在处理过程中，是否有错误的消息抛出
            if (handleMessage.getErrorMsges().size() > 0) {
                result.setSuccess(false);
                result.setMessage("启动流程失败!");
                result.setData(handleMessage.getErrors());
            } else {
                result.setSuccess(true);
            }
            ProcessHandleHelper.clearProcessCmd();
            ProcessHandleHelper.clearProcessMessage();
            ContextUtil.setCurUser(currentUser);//恢复当前用户
        }
        return result;
    }
}
