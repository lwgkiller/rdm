package com.redxun.serviceEngineering.core.service;

import static com.redxun.rdmCommon.core.util.RdmCommonUtil.toGetParamVal;

import java.io.File;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONArray;
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
import com.redxun.core.query.Page;
import com.redxun.core.util.*;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.dao.AttachedDocTranslateDao;
import com.redxun.serviceEngineering.core.dao.YlshDao;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;

/**
 * 语料库审核
 *
 * @mh 2022年6月011日08:49:56
 */

@Service
public class YlshManager {
    private static final Logger logger = LoggerFactory.getLogger(YlshManager.class);
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private YlshDao ylshDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private AttachedDocTranslateManager attachedDocTranslateManager;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private AttachedDocTranslateDao attachedDocTranslateDao;
    @Autowired
    private BpmSolutionManager bpmSolutionManager;
    @Autowired
    private UserService userService;
    @Autowired
    private BpmInstManager bpmInstManager;

    public JsonPageResult<?> queryApplyList(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "ylsh_info.CREATE_TIME_", "desc");
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

        // 增加角色过滤的条件
        addRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());

        //List<JSONObject> applyList = ylshDao.queryApplyList(params);
        List<Map<String, Object>> applyList = ylshDao.queryApplyList(params);
        for (Object oneApply : applyList) {
            if (((JSONObject) oneApply).get("CREATE_TIME_") != null) {
                ((JSONObject) oneApply).put("CREATE_TIME_",
                        DateUtil.formatDate((Date) ((JSONObject) oneApply).get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        //rdmZhglUtil.setTaskInfo2Data(applyList, ContextUtil.getCurrentUserId());
        xcmgProjectManager.setTaskCurrentUser(applyList);
        // 根据分页进行subList截取
        List<Map<String, Object>> finalSubList = new ArrayList<Map<String, Object>>();
        if (doPage) {
            int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
            int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
            int startSubListIndex = pageIndex * pageSize;
            int endSubListIndex = startSubListIndex + pageSize;
            if (startSubListIndex < applyList.size()) {
                finalSubList = applyList.subList(startSubListIndex,
                        endSubListIndex < applyList.size() ? endSubListIndex : applyList.size());
            }
        } else {
            finalSubList = applyList;
        }
        result.setData(finalSubList);
        result.setTotal(applyList.size());
        return result;
    }

    public void saveInProcess(JsonResult result, String data) {
        JSONObject object = JSONObject.parseObject(data);
        if (object == null || object.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        if (StringUtils.isBlank(object.getString("id"))) {
            createYlsh(object);
            result.setData(object.getString("id"));
        } else {
            updateYlsh(object);
            result.setData(object.getString("id"));
        }
    }

    public JSONObject queryApplyDetail(String id) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(id)) {
            return result;
        }
        JSONObject params = new JSONObject();
        params.put("id", id);
        JSONObject obj = ylshDao.queryApplyDetail(params);
        if (obj == null) {
            return result;
        }
        if (obj.get("endTime") != null) {
            obj.put("endTime", DateUtil.formatDate((Date) obj.get("endTime"), "yyyy-MM-dd"));
        }
        return obj;
    }

    private void addRoleParam(Map<String, Object> params, String userId, String userNo) {
        if (userNo.equalsIgnoreCase("admin")) {
            return;
        }
        params.put("currentUserId", userId);
        params.put("roleName", "other");
    }

    public void createYlsh(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        // 基本信息
        ylshDao.insertYlsh(formData);

    }

    public void updateYlsh(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        // 基本信息
        ylshDao.updateYlsh(formData);

        // 子表信息
        demandProcess(formData.getString("id"), formData.getJSONArray("changeDemandGrid"));

    }

    private void demandProcess(String applyId, JSONArray demandArr) {
        if (demandArr == null || demandArr.isEmpty()) {
            return;
        }
        String fileBasePath = WebAppUtil.getProperty("ylshFilePathBase");
        for (int index = 0; index < demandArr.size(); index++) {
            JSONObject oneObject = demandArr.getJSONObject(index);
            String id = oneObject.getString("id");
            String state = oneObject.getString("_state");
            // 只会用到remove
            if ("removed".equals(state)) {
                rdmZhglFileManager.deleteOneFileFromDisk(oneObject.getString("aimFileId"),
                        oneObject.getString("aimFileName"), applyId, fileBasePath);
                if (StringUtils.isNotBlank(oneObject.getString("resFileId"))) {
                    rdmZhglFileManager.deleteOneFileFromDisk(oneObject.getString("resFileId"),
                            oneObject.getString("resFileName"), applyId, fileBasePath);
                }
                //删除外发语料文件的关联
                JSONObject params = new JSONObject();
                params.put("id", applyId);
                JSONObject ylsh = ylshDao.queryApplyDetail(params);
                String oriFileId = ylsh.getString("oriFileId");
                if (!StringUtil.isEmpty(oriFileId)) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("REF_ID_", "0");
                    jsonObject.put("id", oriFileId);
                    attachedDocTranslateDao.updateFileREFInfos(jsonObject);
                }
                clearFile(applyId);
            }
        }
    }

    public JsonResult delApplys(String[] applyIdArr) {
        List<String> applyIdList = Arrays.asList(applyIdArr);
        JsonResult result = new JsonResult(true, "操作成功！");
        if (applyIdArr.length == 0) {
            return result;
        }
        JSONObject param = new JSONObject();
        //删除主表用
        param.put("ids", applyIdList);

        for (String applyId : applyIdList) {
            JSONObject params = new JSONObject();
            params.put("id", applyId);
            String fileBasePath = WebAppUtil.getProperty("ylshFilePathBase");
            List<JSONObject> demandList = queryDemandList(params);
            for (JSONObject oneApply : demandList) {
                // 删除待审核文件
                if (StringUtils.isNotBlank(oneApply.getString("aimFileId"))
                        && StringUtils.isNotBlank(oneApply.getString("aimFileName"))) {
                    rdmZhglFileManager.deleteOneFileFromDisk(oneApply.getString("aimFileId"),
                            oneApply.getString("aimFileName"), applyId, fileBasePath);
                }
                // 删除已审核文件【应该用不到】
                if (StringUtils.isNotBlank(oneApply.getString("resFileId"))
                        && StringUtils.isNotBlank(oneApply.getString("resFileName"))) {
                    rdmZhglFileManager.deleteOneFileFromDisk(oneApply.getString("resFileId"),
                            oneApply.getString("resFileName"), applyId, fileBasePath);
                }
            }
            //删除文件夹
            rdmZhglFileManager.deleteDirFromDisk(applyId, fileBasePath);
            //删除外发语料文件的关联
            JSONObject ylsh = ylshDao.queryApplyDetail(params);
            String oriFileId = ylsh.getString("oriFileId");
            if (!StringUtil.isEmpty(oriFileId)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("REF_ID_", "0");
                jsonObject.put("id", oriFileId);
                attachedDocTranslateDao.updateFileREFInfos(jsonObject);
            }
        }
        ylshDao.deleteYlsh(param);
        return result;
    }

    public void clearFile(String applyId) {
        JSONObject params = new JSONObject();
        params.put("id", applyId);
        params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        params.put("UPDATE_TIME_", new Date());
        ylshDao.clearDemand(params);

    }

    // 附件上传
    public void saveUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到文件上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        try {
            String id = IdUtil.getId();
            String applyId = toGetParamVal(parameters.get("applyId"));
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String aimDesc = toGetParamVal(parameters.get("fileDesc"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String filePathBase = WebAppUtil.getProperty("ylshFilePathBase");
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase");
                return;
            }
            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + applyId;
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
            String aimFileType = "";
            if ("xls".equalsIgnoreCase(suffix) || "xlsx".equalsIgnoreCase(suffix)) {
                aimFileType = "翻译对照表";
            } else if ("sdltm".equalsIgnoreCase(suffix)) {
                aimFileType = "语料库";
            } else {
                aimFileType = "其他";
            }

            fileInfo.put("aimFileId", id);
            fileInfo.put("aimFileType", aimFileType);
            fileInfo.put("aimFileName", fileName);
            fileInfo.put("aimFileNum", fileSize);
            fileInfo.put("aimDesc", aimDesc);
            fileInfo.put("id", applyId);
            fileInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("UPDATE_TIME_", new Date());
            ylshDao.updateDemand(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public void saveReturnFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到文件上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        try {
            // id 要获取表单中的id
            String id = IdUtil.getId();
            String applyId = toGetParamVal(parameters.get("applyId"));
            String fileName = toGetParamVal(parameters.get("fileName"));
            String resDesc = toGetParamVal(parameters.get("fileDesc"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String filePathBase = WebAppUtil.getProperty("ylshFilePathBase");
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase");
                return;
            }
            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + applyId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);
            // 更新数据库
            JSONObject fileInfo = new JSONObject();

            fileInfo.put("id", applyId);
            fileInfo.put("resFileName", fileName);
            fileInfo.put("resFileId", id);
            fileInfo.put("resFileTime", new Date());
            fileInfo.put("resDesc", resDesc);
            fileInfo.put("UPDATE_TIME_", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));

            ylshDao.updateDemand(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public List<JSONObject> queryDemandList(JSONObject params) {
        List<JSONObject> demandList = ylshDao.queryDemandList(params);
        return demandList;
    }

    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        // 这个导出不分页
        JsonPageResult result = queryApplyList(request, false);
        List<Map<String, Object>> listData = result.getData();
        List<JSONObject> attachList = new ArrayList<>();

        for (Map<String, Object> data : listData) {
            JSONObject jsonObject = new JSONObject(data);
            attachList.add(jsonObject);
        }

        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "语料库审核申请清单";
        String excelName = nowDate + title;
        String[] fieldNames =
                {"翻译申请单号", "文件名", "手册类型", "销售型号", "手册编码", "源手册语言", "翻译语言", "词条数", "提交人", "校对人", "当前处理人", "备注"};
        String[] fieldCodes = {"transApplyId", "aimFileName", "manualType", "saleType", "manualCode", "sourceManualLan",
                "targetManualLan", "wordsNum", "creatorName", "checkName", "allTaskUserNames", "remark"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(attachList, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);

    }

    //..处理sdltm文件拷贝
    public void getSdltmFromTranslationToYlsh(JsonResult result, String translationId, String applyId, String fileId) {
        List<JSONObject> fileListOri = new ArrayList<>();
        List<String> translationIdList = new ArrayList<>();
        translationIdList.add(translationId);
        fileListOri = attachedDocTranslateManager.getFileList(translationIdList);
        //源文件基础目录
        String filePathBaseOri = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "attachedDocTranslate").getValue();
        //目标文件基础目录
        String filePathBaseTarget = WebAppUtil.getProperty("ylshFilePathBase");
        for (JSONObject fileOri : fileListOri) {
            if (fileOri.getString("id").equalsIgnoreCase(fileId)) {
                JSONObject params = new JSONObject();
                params.put("id", applyId);
                List<JSONObject> demandList = this.queryDemandList(params);
                if (!demandList.isEmpty()) {
                    //先清空现有的
                    if (StringUtils.isNotBlank(demandList.get(0).getString("aimFileId"))) {
                        rdmZhglFileManager.deleteOneFileFromDisk(demandList.get(0).getString("aimFileId"),
                                demandList.get(0).getString("aimFileName"), applyId, filePathBaseTarget);
                    }
                    if (StringUtils.isNotBlank(demandList.get(0).getString("resFileId"))) {
                        rdmZhglFileManager.deleteOneFileFromDisk(demandList.get(0).getString("resFileId"),
                                demandList.get(0).getString("resFileName"), applyId, filePathBaseTarget);
                    }
                }
                clearFile(applyId);
                //写入目标文件数据库
                JSONObject fileTarget = new JSONObject();
                fileTarget.put("id", applyId);
                fileTarget.put("aimFileId", IdUtil.getId());
                fileTarget.put("aimFileType", "语料库");
                fileTarget.put("aimFileName", fileOri.getString("fileName"));
                fileTarget.put("aimFileNum", fileOri.getString("fileSize"));
                fileTarget.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                fileTarget.put("UPDATE_TIME_", new Date());
                fileTarget.put("oriFileId", fileId);
                ylshDao.updateDemand(fileTarget);
                //扩展名，共用
                String suffix = CommonFuns.toGetFileSuffix(fileOri.getString("fileName"));
                //源文件全路径
                String fileFullPathOri = filePathBaseOri + File.separator + translationId +
                        File.separator + fileOri.getString("id") + "." + suffix;
                //向目标文件下载目录中写入文件
                String filePathTarget = filePathBaseTarget + File.separator + applyId;
                File pathFileTarget = new File(filePathTarget);
                if (!pathFileTarget.exists()) {
                    pathFileTarget.mkdirs();
                }
                String fileFullPathTarget = filePathTarget + File.separator + fileTarget.getString("aimFileId") + "." + suffix;
                FileUtil.copyFile(fileFullPathOri, fileFullPathTarget);
            }
        }
    }

    //..
    public JsonResult copyBusiness(String businessId) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        //查找相关solution
        String solId = this.synGetYlshSolId(result);
        if (StringUtil.isEmpty(solId)) {
            return result;
        }
        JSONObject params = new JSONObject();
        params.put("id", businessId);
        JSONObject ylshObj = ylshDao.queryApplyDetail(params);
        ylshObj.put("id", "");
        //
        ylshObj.put("endTime", null);
        ylshObj.put("version", "");
        ylshObj.put("oriFileId", "");
        ylshObj.put("aimFileId", "");
        ylshObj.put("aimFileNum", "");
        ylshObj.put("aimFileName", "");
        ylshObj.put("aimFileType", "");
        ylshObj.put("aimDesc", "");
        ylshObj.put("resFileId", "");
        ylshObj.put("resFileName", "");
        ylshObj.put("resFileTime", "");
        ylshObj.put("resDesc", "");
        ylshObj.put("creatorName", "");
        ylshObj.put("INST_ID_", "");
        ylshObj.put("UPDATE_BY_", "");
        ylshObj.put("UPDATE_TIME_", "");
        //启动流程
        result = this.startProcess(solId, ylshObj);
        return result;
    }

    //..启动流程
    private JsonResult startProcess(String solId, JSONObject collectDetail) {
        ProcessMessage handleMessage = new ProcessMessage();
        BpmInst bpmInst = null;
        JsonResult result = new JsonResult();
        IUser currentUser = ContextUtil.getCurrentUser();//暂存当前用户
        try {
            IUser user = userService.getByUserId(collectDetail.getString("CREATE_BY_"));//取表单当前用户
            if (user != null) {//表单当前用户不空的话，用表单当前用户代替当前用户
                ContextUtil.setCurUser(user);
            }
            ProcessHandleHelper.setProcessMessage(handleMessage);
            ProcessStartCmd startCmd = new ProcessStartCmd();
            startCmd.setSolId(solId);
            startCmd.setJsonData(collectDetail.toJSONString());
            //启动流程
            //bpmInstManager.doStartProcess(startCmd);
            //创建草稿
            bpmInstManager.doStartProcess(startCmd, false);
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
                result.setMessage("启动流程失败!" + handleMessage.getErrors());
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

    //..查找相关solution
    private String synGetYlshSolId(JsonResult result) {
        BpmSolution bpmSol = bpmSolutionManager.getByKey("FYYLKSXGD", "1");
        String solId = "";
        if (bpmSol != null) {
            solId = bpmSol.getSolId();
        } else {
            result.setSuccess(false);
            result.setMessage("创建失败,翻译语料库审校归档流程方案获取出现异常！");
        }
        return solId;
    }
}
