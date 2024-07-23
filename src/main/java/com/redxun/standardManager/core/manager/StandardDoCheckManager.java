package com.redxun.standardManager.core.manager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.util.DateFormatUtil;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
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
import com.redxun.bpm.core.entity.BpmSolution;
import com.redxun.bpm.core.entity.ProcessMessage;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmSolutionManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExceptionUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.dao.StandardDoCheckDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;

import groovy.util.logging.Slf4j;

@Service
@Slf4j
public class StandardDoCheckManager {
    private static Logger logger = LoggerFactory.getLogger(StandardDoCheckManager.class);
    @Autowired
    private StandardDoCheckDao standardDoCheckDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private UserService userService;
    @Resource
    private BpmSolutionManager bpmSolutionManager;
    @Autowired
    BpmInstManager bpmInstManager;

    public void saveInProcess(JsonResult result, String data) {
        JSONObject object = JSONObject.parseObject(data);
        if (object == null || object.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        if (StringUtils.isBlank(object.getString("id"))) {
            createStandardDoCheck(object);
            result.setData(object.getString("id"));
        } else {
            updateStandardDoCheck(object);
            result.setData(object.getString("id"));
        }
    }

    public void createStandardDoCheck(JSONObject formData) {
        // 保存基本信息
        String baseInfoId = IdUtil.getId();
        formData.put("id", baseInfoId);
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        standardDoCheckDao.insertDoCheckBaseInfo(formData);
        // 新增自查情况明细
        JSONArray checkGrid = formData.getJSONArray("SUB_checkGrid");
        if (checkGrid != null && !checkGrid.isEmpty()) {
            for (int index = 0; index < checkGrid.size(); index++) {
                JSONObject oneData = checkGrid.getJSONObject(index);
                oneData.put("id", IdUtil.getId());
                oneData.put("baseInfoId", baseInfoId);
                oneData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneData.put("CREATE_TIME_", new Date());
                standardDoCheckDao.insertDoCheckDetail(oneData);
            }
        }
    }

    public void updateStandardDoCheck(JSONObject formData) {
        // 更新执行性判定（存在为空的，则为“”）
        String checkResult = "";
        boolean existNo = false;
        boolean existBlank = false;
        JSONArray checkGrid = formData.getJSONArray("SUB_checkGrid");
        if (checkGrid != null && !checkGrid.isEmpty()) {
            for (int index = 0; index < checkGrid.size(); index++) {
                JSONObject oneData = checkGrid.getJSONObject(index);
                if (StringUtils.isBlank(oneData.getString("judge"))) {
                    existBlank = true;
                    break;
                }
                if ("否".equalsIgnoreCase(oneData.getString("judge"))) {
                    existNo = true;
                }
            }
            if (!existBlank) {
                if (existNo) {
                    checkResult = "未完全按标准执行";
                } else {
                    checkResult = "完全按标准执行";
                }
            }
        }

        // 更新基本信息
        formData.put("checkResult", checkResult);
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        standardDoCheckDao.updateDoCheckBaseInfo(formData);
        String baseInfoId = formData.getString("id");
        // 新增、更新、删除自查情况明细
        Set<String> delDetailIds = new HashSet<>();
        JSONArray changeCheckGrid = JSONObject.parseArray(formData.getString("changeCheckGrid"));
        if (changeCheckGrid != null && !changeCheckGrid.isEmpty()) {
            for (int i = 0; i < changeCheckGrid.size(); i++) {
                JSONObject oneObject = changeCheckGrid.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("baseInfoId", baseInfoId);
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    standardDoCheckDao.insertDoCheckDetail(oneObject);
                } else if ("modified".equals(state)) {
                    // 修改
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    standardDoCheckDao.updateDoCheckDetail(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    delDetailIds.add(id);
                }
            }
        }
        if (!delDetailIds.isEmpty()) {
            JSONObject param = new JSONObject();
            param.put("ids", delDetailIds);
            standardDoCheckDao.delDoCheckDetail(param);
        }

    }

    public JsonPageResult<?> queryDoCheckList(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "standard_doCheckBaseInfo.CREATE_TIME_", "desc");
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
        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        // admin能看所有的，其他能看非草稿的（本处列表不做其他限制，在单条的明细查看处限制）
        if (!ContextUtil.getCurrentUser().getUserNo().equalsIgnoreCase("admin")) {
            params.put("currentUserId", ContextUtil.getCurrentUserId());
            params.put("roleName", "other");
        }
        List<JSONObject> doCheckList = standardDoCheckDao.queryDoCheckList(params);
        for (JSONObject oneData : doCheckList) {
            oneData.put("createYear", DateUtil.formatDate((Date)oneData.get("CREATE_TIME_"), "yyyy"));
        }
        // 向业务数据中写入任务相关的信息
        rdmZhglUtil.setTaskInfo2Data(doCheckList, ContextUtil.getCurrentUserId());
        result.setData(doCheckList);
        int count = standardDoCheckDao.countDoCheckList(params);
        result.setTotal(count);
        return result;
    }

    public JsonResult delApplys(String[] applyIdArr) {
        List<String> applyIdList = Arrays.asList(applyIdArr);
        JsonResult result = new JsonResult(true, "操作成功！");
        if (applyIdArr.length == 0) {
            return result;
        }
        JSONObject param = new JSONObject();
        param.put("baseInfoIds", applyIdList);
        // 删除自查情况子表
        standardDoCheckDao.delDoCheckDetail(param);
        // 查询附件信息
        List<JSONObject> files = standardDoCheckDao.queryCheckDetailFileList(param);
        if (files.size() > 0) {
            String doCheckFilePathBase = WebAppUtil.getProperty("standardDoCheckFilePathBase");
            // 删除数据库中的附件信息
            standardDoCheckDao.delDoCheckFile(param);
            // 删除磁盘中的附件
            for (JSONObject oneFile : files) {
                rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                    oneFile.getString("baseInfoId"), doCheckFilePathBase);
            }
            for (String applyId : applyIdList) {
                rdmZhglFileManager.deleteDirFromDisk(applyId, doCheckFilePathBase);
            }
        }
        // 删除主表
        param.put("ids", applyIdList);
        standardDoCheckDao.delDoCheckBaseInfo(param);
        return result;
    }

    public JSONObject queryApplyJson(String id) {
        JSONObject params = new JSONObject();
        params.put("id", id);
        JSONObject obj = standardDoCheckDao.queryApplyJson(params);
        if (obj == null) {
            return new JSONObject();
        }
        return obj;
    }

    public List<JSONObject> checkDetailList(JSONObject params) {
        List<JSONObject> checkDetailList = standardDoCheckDao.queryCheckDetailList(params);
        return checkDetailList;
    }

    public List<JSONObject> checkDetailFileList(JSONObject params) {
        List<JSONObject> files = standardDoCheckDao.queryCheckDetailFileList(params);
        for (JSONObject oneFile : files) {
            if (oneFile.get("CREATE_TIME_") != null) {
                oneFile.put("CREATE_TIME_", DateUtil.formatDate((Date)oneFile.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return files;
    }

    public void saveUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到文件上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        try {
            String id = IdUtil.getId();
            String baseInfoId = toGetParamVal(parameters.get("baseInfoId"));
            String detailId = toGetParamVal(parameters.get("detailId"));
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String filePathBase = WebAppUtil.getProperty("standardDoCheckFilePathBase");
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase");
                return;
            }
            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + baseInfoId;
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
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("baseInfoId", baseInfoId);
            fileInfo.put("detailId", detailId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            standardDoCheckDao.insertCheckFile(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    public JsonResult startDoCheckFlow(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(true);
        String standardIds = RequestUtil.getString(request, "standardIds");
        if (StringUtils.isBlank(standardIds)) {
            result.setMessage("没有要启动的标准自查流程！");
            return result;
        }
        List<String> standardIdList = Arrays.asList(standardIds.split(",", -1));
        JSONObject param = new JSONObject();
        param.put("ids", standardIdList);
        List<JSONObject> standardInfos = standardDoCheckDao.queryStandardFirstWriterInfo(param);
        for (JSONObject standardObj : standardInfos) {
            // 加上处理的消息提示
            ProcessMessage handleMessage = new ProcessMessage();
            try {
                IUser user = userService.getByUserId(ContextUtil.getCurrentUserId());
                ContextUtil.setCurUser(user);
                // 查找solution
                BpmSolution bpmSol = bpmSolutionManager.getByKey("BZZXXZC", "1");
                String solId = "";
                if (bpmSol != null) {
                    solId = bpmSol.getSolId();
                }
                ProcessHandleHelper.setProcessMessage(handleMessage);
                ProcessStartCmd startCmd = new ProcessStartCmd();
                startCmd.setSolId(solId);

                standardObj.put("djrUserId", ContextUtil.getCurrentUserId());
                standardObj.put("djrUserName", ContextUtil.getCurrentUser().getFullname());
                startCmd.setJsonData(standardObj.toJSONString());
                // 启动流程
                bpmInstManager.doStartProcess(startCmd);
            } catch (Exception ex) {
                // 把具体的错误放置在内部处理，以显示正确的错误信息提示，在此不作任何的错误处理
                logger.error(ExceptionUtil.getExceptionMessage(ex));
                if (handleMessage.getErrorMsges().size() == 0) {
                    handleMessage.addErrorMsg(ex.getMessage());
                }
            } finally {
                // 在处理过程中，是否有错误的消息抛出
                if (handleMessage.getErrorMsges().size() > 0) {
                    result.setSuccess(false);
                    result.setMessage("启动流程失败!");
                    result.setData(handleMessage.getErrors());
                } else {
                    result.setSuccess(true);
                    result.setMessage("流程创建成功，请在相应页面进行流程确认并提交流程！");
                }
                ProcessHandleHelper.clearProcessCmd();
                ProcessHandleHelper.clearProcessMessage();
            }
        }
        return result;
    }

    public void exportStandardDoCheckList(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> queryParam = new HashMap<String, Object>();
        rdmZhglUtil.addOrder(request, queryParam, "standard_doCheckBaseInfo.CREATE_TIME_", "desc");
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    queryParam.put(name, value);
                }
            }
        }
        // admin能看所有的，其他能看非草稿的（本处列表不做其他限制，在单条的明细查看处限制）
        if (!ContextUtil.getCurrentUser().getUserNo().equalsIgnoreCase("admin")) {
            queryParam.put("currentUserId", ContextUtil.getCurrentUserId());
            queryParam.put("roleName", "other");
        }
        List<JSONObject> doCheckList = standardDoCheckDao.exportDoCheckList(queryParam);
        for (JSONObject oneData : doCheckList) {

            if (oneData.get("planFinishTime") != null && StringUtils.isNotBlank(oneData.get("planFinishTime").toString())) {

                oneData.put("planFinishTime", oneData.get("planFinishTime").toString().substring(0,10));
            }
            if (oneData.get("status") != null && StringUtils.isNotBlank(oneData.get("status").toString())) {
                String status = oneData.get("status").toString();
                switch (status) {
                    case "SUCCESS_END":
                        status = "成功结束";
                        break;
                    case "DRAFTED":
                        status = "草稿";
                        break;
                    case "RUNNING":
                        status = "审批中";
                        break;
                    case "DISCARD_END":
                        status = "流程作废";
                        break;
                }
                oneData.put("status", status);
            }

        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "徐工挖机执行性标准自查导出表";
        String excelName = nowDate + title;
        String[] fieldNames = { "标准编号", "标准名称", "执行性判定","标准自查人","标准对接人","流程状态",
                                 "文件类别", "文件编号及名称","文件存储位置","应用简要说明","符合性判定","责任人",
                                "责任部门","问题分类","原因分析及纠正措施","计划完成时间","问题关闭责任人",
                                "问题关闭结果及说明"};
        String[] fieldCodes = {"standardNumber", "standardName", "checkResult", "firstWriterName","djrUserName","status",
                                "fileType", "fileName","filePath","useDesc","judge","respUserName",
                                "deptName","detailTypes","modifyMethod","planFinishTime","closeRespUserName",
                                "closeDesc"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(doCheckList, fieldNames, fieldCodes, title);

        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);

    }
}
