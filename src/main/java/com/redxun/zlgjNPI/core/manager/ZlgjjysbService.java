package com.redxun.zlgjNPI.core.manager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;
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
import com.redxun.core.util.*;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.controller.RdmApiController;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.zlgjNPI.core.dao.ZlgjWTDao;
import com.redxun.zlgjNPI.core.dao.ZlgjjysbDao;

import sun.misc.BASE64Decoder;

/**
 * 质量改进改进建议申报
 */
@Service
public class ZlgjjysbService {
    private static final Logger logger = LoggerFactory.getLogger(ZlgjjysbService.class);
    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private ZlgjjysbDao zlgjjysbDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private BpmSolutionManager bpmSolutionManager;
    @Autowired
    private UserService userService;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private ZlgjWTDao zlgjWTDao;// 重用外部系统中间表的操作
    @Autowired
    private SysDicManager sysDicManager;

    public JsonPageResult<?> zlgjjysbListQuery(HttpServletRequest request, HttpServletResponse response,
        boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    if ("endTimeStart".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("endTimeEnd".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    params.put(name, value);
                }
            }
        }
        // 增加分页条件
        if (doPage) {
            params.put("startIndex", 0);
            params.put("pageSize", 20);
            String pageIndex = request.getParameter("pageIndex");
            String pageSize = request.getParameter("pageSize");
            if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
                params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
                params.put("pageSize", Integer.parseInt(pageSize));
            }
        }
        // 增加角色过滤的条件（需要自己办理的目前已包含在下面的条件中）
        addZlgjjysbRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> zlgjjysbList = zlgjjysbDao.zlgjjysbListQuery(params);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        for (JSONObject zlgjjysb : zlgjjysbList) {
            if (zlgjjysb.get("declareTime") != null) {
                zlgjjysb.put("declareTime", format.format(zlgjjysb.get("declareTime")));
            }
            if (zlgjjysb.get("endTime") != null) {
                zlgjjysb.put("endTime", format.format(zlgjjysb.get("endTime")));
            }
            if (zlgjjysb.get("CREATE_TIME_") != null) {
                zlgjjysb.put("CREATE_TIME_", sdf.format(zlgjjysb.get("CREATE_TIME_")));
            }
        }
        // 向业务数据中写入任务相关的信息
        rdmZhglUtil.setTaskInfo2Data(zlgjjysbList, ContextUtil.getCurrentUserId());
        result.setData(zlgjjysbList);

        int count = zlgjjysbDao.countZlgjjysbQuery(params);
        // 追加外部系统信息
        this.setOutSystemMessage(zlgjjysbList);
        result.setTotal(count);
        return result;
    }

    // 1、admin所有，相当于不加条件；
    // 2、分管领导、服务工程人员、其他人看所有提交的，自己的；
    private void addZlgjjysbRoleParam(Map<String, Object> params, String userId, String userNo) {
        params.put("currentUserId", userId);
        if (userNo.equalsIgnoreCase("admin")) {
            return;
        }
        boolean isFgld = rdmZhglUtil.judgeUserIsFgld(userId);
        if (isFgld) {
            params.put("roleName", "fgld");
            return;
        }
        params.put("roleName", "ptyg");
    }

    public JSONObject getZlgjjysbDetail(String zlgjjysbId) {
        JSONObject zlgjjysbDetail = zlgjjysbDao.getZlgjjysbDetail(zlgjjysbId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date declareTime = zlgjjysbDetail.getDate("declareTime");
        if (declareTime != null) {
            zlgjjysbDetail.put("declareTime", sdf.format(declareTime));
        }
        Date sswcTime = zlgjjysbDetail.getDate("sswcTime");
        if (sswcTime != null) {
            zlgjjysbDetail.put("sswcTime", sdf.format(sswcTime));
        }
        return zlgjjysbDetail;
    }

    public void zlgjjysbUpload(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("zlgjjysbPathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find zlgjjysbPathBase");
            return;
        }
        try {
            String zlgjjysbId = toGetParamVal(parameters.get("zlgjjysbId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + zlgjjysbId;
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
            fileInfo.put("zlgjjysbId", zlgjjysbId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("creator", ContextUtil.getCurrentUser().getFullname());
            fileInfo.put("CREATE_TIME_", new Date());
            zlgjjysbDao.addFiles(fileInfo);
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

    public List<JSONObject> queryZlgjjysbFileList(JSONObject params) {
        List<JSONObject> fileList = zlgjjysbDao.queryZlgjjysbFileList(params);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (fileList.size() > 0) {
            for (JSONObject file : fileList) {
                if (file.get("CREATE_TIME_") != null) {
                    file.put("CREATE_TIME_", sdf.format(file.get("CREATE_TIME_")));
                }
            }
        }
        return fileList;
    }

    public void delZlgjjysbFileById(String id, String fileName, String zlgjjysbId) {
        String filePath = WebAppUtil.getProperty("zlgjjysbPathBase");
        rdmZhglFileManager.deleteOneFileFromDisk(id, fileName, zlgjjysbId, filePath);
        zlgjjysbDao.delZlgjjysbFileById(id);
    }

    public JsonResult deleteZlgjjysb(String[] ids) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        JSONObject param = new JSONObject();
        param.put("zlgjjysbIds", businessIds);
        List<JSONObject> files = zlgjjysbDao.queryZlgjjysbFileList(param);
        String filePath = WebAppUtil.getProperty("zlgjjysbPathBase");
        if (files.size() > 0) {
            for (JSONObject oneFile : files) {
                rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                    oneFile.getString("zlgjjysbId"), filePath);
            }
            for (String id : ids) {
                rdmZhglFileManager.deleteDirFromDisk(id, filePath);
            }
            zlgjjysbDao.delFileByZlgjjysbId(param);
        }
        zlgjjysbDao.deleteZlgjjysb(param);
        zlgjjysbDao.deleteDcry(param);
        param.clear();
        param.put("busKeyIds", businessIds);
        zlgjWTDao.deleteOutSystemByBusKeyId(param);
        return result;
    }

    public void createZlgjjysb(JSONObject formDataJson) {
        formDataJson.put("id", IdUtil.getId());
        formDataJson.put("CREATE_TIME_", new Date());
        zlgjjysbDao.createZlgjjysb(formDataJson);
    }

    public void updateZlgjjysb(JSONObject formDataJson) {
        formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formDataJson.put("UPDATE_TIME_", new Date());
        if (StringUtils.isBlank(formDataJson.getString("sswcTime"))) {
            formDataJson.put("sswcTime", null);
        }
        zlgjjysbDao.updateZlgjjysb(formDataJson);
    }

    public List<JSONObject> queryDcry(String zlgjjysbId) {
        return zlgjjysbDao.queryDcry(zlgjjysbId);
    }

    public JsonResult zlgjjysbAppeal(String projectName, String declareTime, String creator) {
        JsonResult result = new JsonResult(true, "申诉成功，消息已发送。");
        try {
            Map<String, Object> roleParams = new HashMap<>();
            roleParams.put("groupName", "质量改进建议专员");
            roleParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            List<Map<String, String>> userInfos = xcmgProjectOtherDao.queryUserByGroupName(roleParams);
            if (userInfos.size() > 0) {
                for (Map<String, String> userMap : userInfos) {
                    JSONObject noticeObj = new JSONObject();
                    StringBuilder stringBuilder = new StringBuilder("【质量改进建议申报通知】：");
                    stringBuilder.append("项目名称：").append(projectName).append("，申报人：").append(creator).append("，申报日期：")
                        .append(declareTime).append("，已申诉，请抓紧处理");
                    noticeObj.put("content", stringBuilder.toString());
                    sendDDNoticeManager.sendNoticeForCommon(userMap.get("USER_ID_"), noticeObj);
                } ;
            }
        } catch (Exception e) {
            logger.error("Exception in zlgjjysbAppeal");
            result.setSuccess(false);
            result.setMessage("申诉失败");
            return result;
        }
        return result;
    }

    public void deleteDcryById(String id) {
        zlgjjysbDao.deleteDcryById(id);
    }

    public void exportZlgjjysb(HttpServletRequest request, HttpServletResponse response) {
        JSONObject params = new JSONObject();
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
        List<JSONObject> zlgjjysbList = zlgjjysbDao.zlgjjysbListQuery(params);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (JSONObject zlgjjysb : zlgjjysbList) {
            if (zlgjjysb.get("declareTime") != null) {
                zlgjjysb.put("declareTime", simpleDateFormat.format(zlgjjysb.get("declareTime")));
            }
            if (zlgjjysb.get("endTime") != null) {
                zlgjjysb.put("endTime", simpleDateFormat.format(zlgjjysb.get("endTime")));
            }
            List<JSONObject> dcrylist = zlgjjysbDao.queryDcry(zlgjjysb.getString("id"));
            String dcbmjysh = "";
            if (dcrylist.size() > 0) {
                for (int i = 0; i < dcrylist.size(); i++) {
                    JSONObject dcry = dcrylist.get(i);
                    String dcryName = dcry.getString("dcryName");
                    if (StringUtils.isNotBlank(dcryName)) {
                        String implementationStatus = dcry.getString("implementationStatus");
                        if (implementationStatus.equals("ljss")) {
                            implementationStatus = "立即实施";
                        } else {
                            implementationStatus = "下一代机型实施";
                        }
                        String auditSuggest = dcry.getString("auditSuggest");
                        dcbmjysh += dcryName + "-" + implementationStatus + "-" + auditSuggest;
                        if (i != dcrylist.size() - 1) {
                            dcbmjysh += "，";
                        }
                    }
                }
            }
            zlgjjysb.put("dcbmjysh", dcbmjysh);
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "质量改进建议申报";
        String excelName = nowDate + title;
        String[] fieldNames = {"项目名称", "申报人", "电话", "申报单位", "申报日期", "问题大类", "问题小类", "建议对策部门", "涉及机型", "问题描述",
            "对策部门审核意见", "等级评定", "流程结束时间"};
        String[] fieldCodes = {"projectName", "creator", "phoneNumber", "applicationUnit", "declareTime", "wtdl",
            "wtxl", "suggestDepartment", "involvingModel", "problemDescription", "dcbmjysh", "rating", "endTime"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(zlgjjysbList, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    // 追加外部系统信息
    private void setOutSystemMessage(List<JSONObject> zlgjjysbList) {
        for (JSONObject zlgjjysb : zlgjjysbList) {
            JSONObject params = new JSONObject();
            params.put("busKeyId", zlgjjysb.getString("id"));
            List<JSONObject> list = zlgjWTDao.queryOutSystemByBusKeyId(params);
            if (!list.isEmpty()) {
                zlgjjysb.put("callBackResult", list.get(0).getString("callBackResult"));
            }
        }
    }

    // ..从外部系统创建质量改进建议信息
    public void createZlgjjyFormOutSystem(JSONObject result, String postData, String whatTypeToStart) throws Exception {
        try {
            JSONObject outSysData = JSONObject.parseObject(postData).getJSONObject("data");
            logger.error(
                outSysData.getString("outSystem") + "向RDM创建质量改进建议流程开始，Id：" + outSysData.getString("outSystemId"));
            // 1.外部传过来的表单正确性校验
            this.synValidZlgjjyFormOutSystem(result, outSysData);
            if (!result.getBoolean("success")) {
                return;
            }
            // 2.查找质量改进建议的solutionid
            String solId = this.synGetZlgjjySolId(result);
            if (!result.getBoolean("success")) {
                return;
            }
            // 3.拼接质量改进建议的表单数据
            JSONObject zlgjjyJson = this.synTransOutSysDataToZlgjjy(outSysData);
            // 4.启动流程
            JsonResult processStartResult = this.synStartProcess(solId, zlgjjyJson, whatTypeToStart);
            if (!processStartResult.getSuccess()) {
                result.put("success", false);
                result.put("message", processStartResult.getMessage() + ":" + processStartResult.getData().toString());
                return;
            }
            String businessId = processStartResult.getData().toString();
            // 5.将对应信息写入zlgj_wtsb_outsystem
            this.synWriteOutsystemTable(outSysData, businessId);
            // 6.同步文件信息,无法控制事务，放到最后
            this.synFile(outSysData, businessId);
        } catch (Exception e) {
            logger.error("创建失败：" + e.getMessage(), e);
            throw e;
        }
    }

    // ..1.外部传过来的表单正确性校验
    private void synValidZlgjjyFormOutSystem(JSONObject result, JSONObject outSysData) {
        String outSystemId = outSysData.getString("outSystemId");
        if (StringUtil.isEmpty(outSystemId)) {
            result.put("success", false);
            result.put("message", "同步失败,'业务主键'为空！");
            return;
        }
        String projectName = outSysData.getString("projectName");
        if (StringUtil.isEmpty(projectName)) {
            result.put("success", false);
            result.put("message", "同步失败,'项目名称'为空！");
            return;
        }
        String wtdl = outSysData.getString("wtdl");
        if (!Arrays.asList("部品检验", "结构检验", "装配", "焊接", "涂装", "产品建议").contains(wtdl)) {
            result.put("success", false);
            result.put("message", "同步失败,'问题大类'为空或其值RDM中无对应！");
            return;
        }
        JSONArray wtxlArr = outSysData.getJSONArray("wtxl");
        if (wtxlArr == null || wtxlArr.isEmpty()) {
            result.put("success", false);
            result.put("message", "同步失败,'问题小类'为空！");
            return;
        }
        for (int i = 0; i < wtxlArr.size(); i++) {
            JSONObject wtxl = wtxlArr.getJSONObject(i);
            if (!Arrays.asList("图纸错误", "设计不合理", "工艺不合理", "其他").contains(wtxl.getString("wtxlName"))) {
                result.put("success", false);
                result.put("message", "同步失败,'问题小类'的值在RDM中无对应！");
                return;
            }
        }
        String jxlb = outSysData.getString("jxlb");
        if (!Arrays.asList("大挖", "中挖", "小挖", "微挖", "轮挖", "特挖").contains(jxlb)) {
            result.put("success", false);
            result.put("message", "同步失败,'机型类别'为空或其值RDM中无对应！");
            return;
        }
        String involvingModel = outSysData.getString("involvingModel");
        if (StringUtil.isEmpty(involvingModel)) {
            result.put("success", false);
            result.put("message", "同步失败,'涉及机型'为空！");
            return;
        }
        String problemDescription = outSysData.getString("problemDescription");
        if (StringUtil.isEmpty(problemDescription)) {
            result.put("success", false);
            result.put("message", "同步失败,'问题简述'为空！");
            return;
        }
        String reasonAnalysis = outSysData.getString("reasonAnalysis");
        if (StringUtil.isEmpty(reasonAnalysis)) {
            result.put("success", false);
            result.put("message", "同步失败,'原因分析'为空！");
            return;
        }
        String dcjyfa = outSysData.getString("dcjyfa");
        if (StringUtil.isEmpty(dcjyfa)) {
            result.put("success", false);
            result.put("message", "同步失败,'对策建议方案'为空！");
            return;
        }
    }

    // ..2.查找质量改进建议的solutionid
    private String synGetZlgjjySolId(JSONObject result) {
        BpmSolution bpmSol = bpmSolutionManager.getByKey("ZLGJJYSB", "1");
        String solId = "";
        if (bpmSol != null) {
            solId = bpmSol.getSolId();
        } else {
            result.put("success", false);
            result.put("message", "同步失败,质量改进建议的流程方案获取出现异常！！");
        }
        return solId;
    }

    // ..3.拼接质量改进建议的表单数据
    private JSONObject synTransOutSysDataToZlgjjy(JSONObject outSysData) {
        JSONObject zlgjjyJson = new JSONObject();
        zlgjjyJson.put("projectName", outSysData.getString("projectName"));// 项目名称
        zlgjjyJson.put("phoneNumber", outSysData.getString("feedbackCompany") + ","
            + outSysData.getString("feedbackMan") + "," + outSysData.getString("feedbackManPhoneNumber"));// 电话号码
        zlgjjyJson.put("wtdl", outSysData.getString("wtdl"));// 问题大类
        JSONArray wtxlArr = outSysData.getJSONArray("wtxl");
        StringBuilder wtxl = new StringBuilder();
        Set<String> wtxlSet = new HashSet<>();
        for (int i = 0; i < wtxlArr.size(); i++) {
            JSONObject wtxlJson = wtxlArr.getJSONObject(i);
            wtxlSet.add(wtxlJson.getString("wtxlName"));
        }
        for (String wtxlString : wtxlSet) {
            wtxl.append(wtxlString).append(",");
        }
        zlgjjyJson.put("wtxl", wtxl.substring(0, wtxl.length() - 1));// 问题小类
        zlgjjyJson.put("jxlb", outSysData.getString("jxlb"));// 机型类别
        zlgjjyJson.put("involvingModel", outSysData.getString("involvingModel"));// 涉及机型
        zlgjjyJson.put("problemDescription", outSysData.getString("problemDescription"));// 问题简述
        zlgjjyJson.put("reasonAnalysis", outSysData.getString("reasonAnalysis"));// 原因分析
        zlgjjyJson.put("dcjyfa", outSysData.getString("dcjyfa"));// 对策建议方案
        String crmToZlgjjyAgent = sysDicManager.getBySysTreeKeyAndDicKey("zlgjGroups", "crmToZlgjjyAgent").getValue();
        IUser user = userService.getByUsername(crmToZlgjjyAgent);
        zlgjjyJson.put("CREATE_BY_", user.getUserId());// 创建人
        zlgjjyJson.put("creator", user.getFullname());// 创建人
        return zlgjjyJson;
    }

    // ..4.启动流程
    private JsonResult synStartProcess(String solId, JSONObject zlgjjyJson, String whatTypeToStart) throws Exception {
        ProcessMessage handleMessage = new ProcessMessage();
        BpmInst bpmInst = null;
        JsonResult result = new JsonResult();
        IUser currentUser = ContextUtil.getCurrentUser();// 暂存
        try {
            IUser user = userService.getByUserId(zlgjjyJson.getString("CREATE_BY_"));
            ContextUtil.setCurUser(user);
            ProcessHandleHelper.setProcessMessage(handleMessage);
            ProcessStartCmd startCmd = new ProcessStartCmd();
            startCmd.setSolId(solId);
            startCmd.setJsonData(zlgjjyJson.toJSONString());
            // 启动流程
            if (whatTypeToStart.equalsIgnoreCase(RdmApiController.SAVE_DRAFT)) {
                bpmInstManager.doSaveDraft(startCmd);
            } else if (whatTypeToStart.equalsIgnoreCase(RdmApiController.START_PROCESS)) {
                bpmInstManager.doStartProcess(startCmd);
            } else {
                throw new Exception("没有收到具体的启动指令");
            }
            result.setData(startCmd.getBusinessKey());
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
            }
            ProcessHandleHelper.clearProcessCmd();
            ProcessHandleHelper.clearProcessMessage();
            ContextUtil.setCurUser(currentUser);// 恢复当前用户
        }
        return result;
    }

    // ..5.将对应信息写入zlgj_wtsb_outsystem
    private void synWriteOutsystemTable(JSONObject outSysData, String businessId) {
        JSONObject param = new JSONObject();
        param.put("id", IdUtil.getId());
        param.put("busKeyId", businessId);
        param.put("outSystem", outSysData.getString("outSystem"));
        JSONObject outSystemJson = new JSONObject();
        outSystemJson.put("outSystemId", outSysData.getString("outSystemId"));
        param.put("outSystemJson", outSystemJson.toJSONString());
        param.put("CREATE_BY_", "1");
        param.put("CREATE_TIME_", new Date());
        zlgjWTDao.insertZlgjOutSystem(param);
    }

    // ..6.同步文件信息,无法控制事务，放到最后
    private void synFile(JSONObject outSysData, String businessId) throws IOException {
        String crmToZlgjjyAgent = sysDicManager.getBySysTreeKeyAndDicKey("zlgjGroups", "crmToZlgjjyAgent").getValue();
        IUser user = userService.getByUsername(crmToZlgjjyAgent);
        String filePathBase = WebAppUtil.getProperty("zlgjjysbPathBase");
        JSONArray fileList = outSysData.getJSONArray("fileList");
        if (fileList != null && !fileList.isEmpty()) {
            for (int fileIndex = 0; fileIndex < fileList.size(); fileIndex++) {
                JSONObject oneFile = fileList.getJSONObject(fileIndex);
                String id = IdUtil.getId();
                // 文件目录
                String filePath = filePathBase + File.separator + businessId;
                File pathFile = new File(filePath);
                if (!pathFile.exists()) {
                    pathFile.mkdirs();
                }
                // 文件扩展名
                String suffix = CommonFuns.toGetFileSuffix(oneFile.getString("fileName"));
                // 文件物理全名
                String fileFullPath = filePath + File.separator + id + "." + suffix;
                File file = new File(fileFullPath);
                // --图片格式编码--
                BASE64Decoder decoder = new BASE64Decoder();
                byte[] fileContent = decoder.decodeBuffer(oneFile.getString("fileContent"));
                // 写入数据库
                JSONObject fileInfo = new JSONObject();
                fileInfo.put("id", id);
                fileInfo.put("fileName", oneFile.getString("fileName"));
                fileInfo.put("fileSize", FileUtil.getSize(fileContent.length));
                fileInfo.put("zlgjjysbId", businessId);
                fileInfo.put("CREATE_BY_", user.getUserId());
                fileInfo.put("creator", user.getFullname());
                fileInfo.put("CREATE_TIME_", new Date());
                zlgjjysbDao.addFiles(fileInfo);
                // 写入物理盘
                FileCopyUtils.copy(fileContent, file);
            }
        }
    }

    // ..质量改进建议回调外部系统，并更新回调状态
    public JsonResult doCallBackOutSystemFromzlgjjysb(String businessId, int okOrNot, String zlshshyjMock,
        String rating, String gjssqryj) throws Exception {
        try {
            JsonResult result = new JsonResult(true, "回调成功！");
            Map<String, Object> param = new HashMap<>();
            param.put("busKeyId", businessId);
            List<JSONObject> outSystemInfoList = zlgjWTDao.queryOutSystemByBusKeyId(param);
            if (outSystemInfoList == null || outSystemInfoList.isEmpty()) {
                return result;// 找不到中间表信息，就算成功回调了,其实什么也不做
            }
            if (outSystemInfoList.size() > 1) {
                result.setSuccess(false);
                result.setMessage("回调失败，外部系统信息存在多条，请联系管理员！");
                return result;
            }
            JSONObject outSystemInfo = outSystemInfoList.get(0);
            if ("yes".equalsIgnoreCase(outSystemInfo.getString("callBackResult"))// 和当前外部系统key匹配且已经发送过的
                && "crm-srv-zlgj".equalsIgnoreCase(outSystemInfo.getString("outSystem"))) {
                result.setSuccess(false);
                result.setMessage("回调失败，外部系统回调已成功，禁止重复操作！");
                return result;
            }
            if ("crm-srv-zlgj".equalsIgnoreCase(outSystemInfo.getString("outSystem"))) {
                this.synCallBackCrm(outSystemInfo, result, okOrNot, zlshshyjMock, rating, gjssqryj);
            } else {
                return result;// 中间表信息的key不匹配，就算成功回调了，其实什么也不做
            }
            // 一路坎坷走下来了，也就是回调成功了，更新回调状态
            if (result.getSuccess()) {
                JSONObject updateParam = new JSONObject();
                updateParam.put("id", outSystemInfo.getString("id"));
                updateParam.put("callBackResult", "yes");
                zlgjWTDao.updateOutSystemCallResult(updateParam);
            }
            return result;
        } catch (Exception e) {
            logger.error("回调失败：" + e.getMessage(), e);
            throw e;
        }
    }

    // ..质量改进建议回调CRM
    private void synCallBackCrm(JSONObject outSystemInfo, JsonResult jsonResult, int okOrNot, String zlshshyjMock,
        String rating, String gjssqryj) throws Exception {
        String outSystemJson = outSystemInfo.getString("outSystemJson");
        if (StringUtils.isBlank(outSystemJson)) {
            jsonResult.setSuccess(false);
            jsonResult.setMessage("回调失败，回调需要的参数为空，请联系管理员！");
            return;
        }
        String zlgjjyToCrmUrl = sysDicManager.getBySysTreeKeyAndDicKey("zlgjCallbackUrls", "zlgjjyToCrmUrl").getValue();
        if (StringUtils.isBlank(zlgjjyToCrmUrl)) {
            jsonResult.setSuccess(false);
            jsonResult.setMessage("回调失败，回调url为空，请联系管理员！");
            return;
        }
        try {
            // 构建请求体
            JSONObject requestBody = new JSONObject();
            JSONObject outSystemJsonObj = JSONObject.parseObject(outSystemJson);
            String outSystemId = outSystemJsonObj.getString("outSystemId");
            requestBody.put("qualityId", outSystemId);
            requestBody.put("timeStamp", System.currentTimeMillis() / 1000);
            requestBody.put("techResult", zlshshyjMock);
            requestBody.put("qualityOptions", gjssqryj);
            requestBody.put("level", rating);
            requestBody.put("approvalStatus", okOrNot);
            Map<String, String> reqHeaders = new HashMap<>();
            reqHeaders.put("Content-Type", "application/json;charset=utf-8");
            String rtnContent = HttpClientUtil.postJson(zlgjjyToCrmUrl, requestBody.toJSONString(), reqHeaders);
            logger.info("response from crm,return message:" + rtnContent);
            if (StringUtils.isBlank(rtnContent)) {
                jsonResult.setSuccess(false);
                jsonResult.setMessage("回调失败，CRM系统返回值为空！");
                return;
            }
            JSONObject returnObj = JSONObject.parseObject(rtnContent);
            if ("0".equalsIgnoreCase(returnObj.getJSONObject("Data").getString("isok"))) {
                jsonResult.setSuccess(false);
                jsonResult.setMessage("回调失败，原因：" + returnObj.getJSONObject("Data").getString("errormsg"));
            }
        } catch (Exception e) {
            logger.error("Exception in synCallBackCrm", e);
            throw e;
        }
    }

    public boolean judgeZlbzb(String userId) {
        JSONObject param = new JSONObject();
        param.put("userId", userId);
        List<JSONObject> messages = zlgjjysbDao.judgeZlbzb(param);
        boolean flag = false;
        for (JSONObject one : messages) {
            if (one.getString("groupName").equalsIgnoreCase("质量保证部")) {
                flag = true;
            }
        }
        return flag;
    }
}
