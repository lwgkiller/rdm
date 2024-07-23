package com.redxun.newProductAssembly.core.service;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.BpmInst;
import com.redxun.bpm.core.entity.BpmSolution;
import com.redxun.bpm.core.entity.ProcessMessage;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmSolutionManager;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.*;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.newProductAssembly.core.dao.NewproductAssemblyKanbanDao;
import com.redxun.org.api.model.IGroup;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.GroupService;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.entity.SysTree;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.manager.SysSeqIdManager;
import com.redxun.sys.core.manager.SysTreeManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.zlgjNPI.core.dao.ZlgjWTDao;
import com.redxun.zlgjNPI.core.manager.ZlgjConstant;

@Service
public class NewproductAssemblyKanbanService {
    private static Logger logger = LoggerFactory.getLogger(NewproductAssemblyKanbanService.class);
    @Autowired
    private NewproductAssemblyKanbanDao newproductAssemblyKanbanDao;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private SysSeqIdManager sysSeqIdManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private SysTreeManager sysTreeManager;
    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private BpmSolutionManager bpmSolutionManager;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private ZlgjWTDao zlgjWTDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;

    // ..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = newproductAssemblyKanbanDao.dataListQuery(params);
        int businessListCount = newproductAssemblyKanbanDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    // ..
    private void getListParams(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "CREATE_TIME_");
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
        // 增加分页条件
        params.put("startIndex", 0);
        params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
    }

    // ..
    public JSONObject queryDataById(String businessId) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(businessId)) {
            return result;
        }
        JSONObject jsonObject = newproductAssemblyKanbanDao.queryDataById(businessId);
        if (jsonObject == null) {
            return result;
        }
        return jsonObject;
    }

    // ..
    public void saveBusiness(JsonResult result, String dataStr) {
        JSONObject dataObj = JSONObject.parseObject(dataStr);
        if (dataObj == null || dataObj.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        try {
            IUser user = userService.getByUserId(dataObj.getString("projectLeaderId"));
            if (user != null) {
                IGroup group = groupService.getMainByUserId(user.getUserId());
                if (group != null) {
                    dataObj.put("productDepId", group.getIdentityId());
                    dataObj.put("productDep", group.getIdentityName());
                }
            }
            if (StringUtils.isBlank(dataObj.getString("id"))) {
                dataObj.put("id", IdUtil.getId());
                dataObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                dataObj.put("CREATE_TIME_", new Date());
                newproductAssemblyKanbanDao.insertBusiness(dataObj);
                result.setData(dataObj.getString("id"));
            } else {
                dataObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                dataObj.put("UPDATE_TIME_", new Date());
                newproductAssemblyKanbanDao.updateBusiness(dataObj);
                result.setData(dataObj.getString("id"));
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getCause().toString());
            return;
        }

    }

    // ..
    public JsonResult deleteBusiness(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        List<String> exceptionIds = new ArrayList<>();
        for (String businessId : businessIds) {// 找出每个主信息的明细
            List<JSONObject> exceptions = this.getExceptionList(businessId);
            for (JSONObject exception : exceptions) {
                exceptionIds.add(exception.getString("id"));
            }
        }
        newproductAssemblyKanbanDao.deleteExceptions(param);// 批删明细
        if (exceptionIds.size() > 0) {
            param.put("exceptionIds", exceptionIds);// 将所有主信息明细的id汇总
            List<JSONObject> exceptionFiles = getExceptionFileList(exceptionIds);// 获取明细文件实体
            String filePathBase =
                sysDicManager.getBySysTreeKeyAndDicKey("newproductAssemblyUploadPosition", "exceptionFile").getValue();
            newproductAssemblyKanbanDao.deleteExceptionFile(param);// 批删明细文件
            for (JSONObject oneFile : exceptionFiles) {// 删文件实体
                rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                    oneFile.getString("mainId"), filePathBase);
            }
            for (String exceptionId : exceptionIds) {// 删文件目录
                rdmZhglFileManager.deleteDirFromDisk(exceptionId, filePathBase);
            }
        }
        newproductAssemblyKanbanDao.deleteBusiness(param);// 批删主信息
        return result;
    }

    // ..
    public List<JSONObject> getExceptionList(String businessId) {
        JSONObject jsonObject = newproductAssemblyKanbanDao.queryDataById(businessId);
        JSONObject params = new JSONObject();
        params.put("smallJiXing", jsonObject.getString("designModel"));// 产品型号->机型
        params.put("jjsl", jsonObject.getString("testQuantity"));// 试制台数->交机数量
        params.put("jiXing", jsonObject.getString("productCategory"));// 产品种类->机型类别
        params.put("wtlx", ZlgjConstant.XPSZ);// "XPSZ"->问题类型
        List<JSONObject> listZlgj = zlgjWTDao.queryZlgjList(params);// 通过上述条件基本能定位到同一个试制档案下面的异常信息生成的质量改进信息
        xcmgProjectManager.setTaskCurrentUserJSON(listZlgj);// 设置当前处理人,这一句必须加，需要带出来当前处理人！！！
        Map<String, JSONObject> jsonMapZlgj = new HashedMap();
        for (JSONObject zlgj : listZlgj) {
            jsonMapZlgj.put(zlgj.getString("wtId"), zlgj);
        }
        // 回归本业务
        List<JSONObject> itemList = newproductAssemblyKanbanDao.queryExceptionList(businessId);
        for (JSONObject exception : itemList) {
            JSONObject zlgj = jsonMapZlgj.get(exception.getString("REF_ID_"));
            if (zlgj != null) {
                if (zlgj.getString("status").equalsIgnoreCase("SUCCESS_END")) {
                    exception.put("currentProcessTaskZlgj", "审批完成");
                    exception.put("currentProcessUserZlgj", "审批完成");
                } else if (zlgj.getString("status").equalsIgnoreCase("DISCARD_END")) {
                    exception.put("currentProcessTaskZlgj", "作废");
                    exception.put("currentProcessUserZlgj", "作废");
                } else {
                    exception.put("currentProcessTaskZlgj", zlgj.getString("currentProcessTask"));
                    exception.put("currentProcessUserZlgj", zlgj.getString("currentProcessUser"));
                }
            } else {
                exception.put("currentProcessTaskZlgj", "未开始");
                exception.put("currentProcessUserZlgj", "未开始");
            }
        }
        return itemList;
    }

    // ..
    public List<JSONObject> getExceptionFileList(List<String> exceptionIds) {
        List<JSONObject> exceptionFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("exceptionIds", exceptionIds);
        exceptionFileList = newproductAssemblyKanbanDao.queryExceptionFileList(param);
        return exceptionFileList;
    }

    // ..
    public JsonResult deleteExceptions(String[] ids) {
        JsonResult result = new JsonResult(true, "删除成功！");
        List<String> exceptionIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("exceptionIds", exceptionIds);
        newproductAssemblyKanbanDao.deleteExceptions(param);
        List<JSONObject> exceptionFiles = getExceptionFileList(exceptionIds);// 获取明细文件实体
        String filePathBase =
            sysDicManager.getBySysTreeKeyAndDicKey("newproductAssemblyUploadPosition", "exceptionFile").getValue();
        newproductAssemblyKanbanDao.deleteExceptionFile(param);// 批删明细文件
        for (JSONObject oneFile : exceptionFiles) {// 删文件实体
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                oneFile.getString("mainId"), filePathBase);
        }
        for (String exceptionId : exceptionIds) {// 删文件目录
            rdmZhglFileManager.deleteDirFromDisk(exceptionId, filePathBase);
        }
        return result;
    }

    // ..
    public JsonResult doSynException(String mainId, String exceptionId) throws Exception {
        JsonResult result = new JsonResult(true, "同步成功！");
        JSONObject mainDetail = this.queryDataById(mainId);
        JSONObject exceptionDetail = this.getExceptionDetail(exceptionId);
        // 数据源头信息必填项正确性校验
        if (false == this.synValidMainDetail(result, mainDetail)
            || false == this.synValidExceptionDetail(result, exceptionDetail)) {
            return result;
        }
        // 查找质量改进的solution
        String solId = this.synGetZlgjSolId(result);
        if (StringUtil.isEmpty(solId)) {
            return result;
        }
        // 拼接质量改进表单数据
        JSONObject zlgjDetail = this.synTransToZlgjDetail(mainDetail, exceptionDetail);
        // 启动流程
        result = this.startProcess(solId, zlgjDetail);
        String wtId = "";
        if (result.getSuccess()) {
            wtId = result.getData().toString();
            exceptionDetail.put("REF_ID_", wtId);
            exceptionDetail.put("isSyn", "是");
            exceptionDetail.put("feedbackTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
            newproductAssemblyKanbanDao.updateException(exceptionDetail);
            result.setMessage("同步成功！");
        } else {
            return result;
        }

        // 同步文件信息
        result = this.synFile(exceptionDetail, wtId);
        if (result.getSuccess()) {
            result.setMessage("同步成功！");
        } else {
            return result;
        }
        return result;
    }

    // ..建档信息必填项正确性校验
    private boolean synValidMainDetail(JsonResult result, JSONObject mainDetail) {
        if (StringUtil.isEmpty(mainDetail.getString("designModel"))) {
            result.setSuccess(false);
            result.setMessage("同步失败,建档信息中设计型号为空！");
            return false;
        }
        if (StringUtil.isEmpty(mainDetail.getString("testQuantity"))) {
            result.setSuccess(false);
            result.setMessage("同步失败,建档信息中试制台数为空！");
            return false;
        }
        if (StringUtil.isEmpty(mainDetail.getString("pin"))) {
            result.setSuccess(false);
            result.setMessage("同步失败,建档信息中整机编号为空！");
            return false;
        }
        if (StringUtil.isEmpty(mainDetail.getString("productCategory"))) {
            result.setSuccess(false);
            result.setMessage("同步失败,建档信息中产品种类为空！");
            return false;
        }
        return true;
    }

    // ..异常信息必填项正确性校验
    private boolean synValidExceptionDetail(JsonResult result, JSONObject exceptionDetail) {
        if (StringUtil.isEmpty(exceptionDetail.getString("repDepLeaderId"))
            || StringUtil.isEmpty(exceptionDetail.getString("repDepLeader"))) {
            result.setSuccess(false);
            result.setMessage("同步失败,异常信息中第一责任人为空！");
            return false;
        }
        if (StringUtil.isEmpty(exceptionDetail.getString("repDepId"))
            || StringUtil.isEmpty(exceptionDetail.getString("repDep"))) {
            result.setSuccess(false);
            result.setMessage("同步失败,异常信息中责任部门为空！");
            return false;
        }
        if (StringUtil.isEmpty(exceptionDetail.getString("problemLevel"))) {
            result.setSuccess(false);
            result.setMessage("同步失败,异常信息中问题紧急程度为空！");
            return false;
        }
        if (StringUtil.isEmpty(exceptionDetail.getString("exceptionDescription"))) {
            result.setSuccess(false);
            result.setMessage("同步失败,异常信息中异常描述为空！");
            return false;
        }
        if (StringUtil.isEmpty(exceptionDetail.getString("workingHours"))) {
            result.setSuccess(false);
            result.setMessage("同步失败,异常信息中工作小时为空！");
            return false;
        }
        if (StringUtil.isEmpty(exceptionDetail.getString("feedbackPersonId"))) {
            result.setSuccess(false);
            result.setMessage("同步失败,异常信息中反馈人为空！");
            return false;
        }
        if (StringUtil.isEmpty(exceptionDetail.getString("severity"))) {
            result.setSuccess(false);
            result.setMessage("同步失败,异常信息中问题严重度为空！");
            return false;
        }
        if (StringUtil.isEmpty(exceptionDetail.getString("isImprove"))) {
            result.setSuccess(false);
            result.setMessage("同步失败,异常信息中是否需要改进为空！");
            return false;
        }
        return true;
    }

    // ..查找质量改进的solutionid
    private String synGetZlgjSolId(JsonResult result) {
        BpmSolution bpmSol = bpmSolutionManager.getByKey("ZLGJ", "1");
        String solId = "";
        if (bpmSol != null) {
            solId = bpmSol.getSolId();
        } else {
            result.setSuccess(false);
            result.setMessage("同步失败,质量改进的流程方案获取出现异常！");
        }
        return solId;
    }

    // ..拼接质量改进表单数据
    private JSONObject synTransToZlgjDetail(JSONObject mainDetail, JSONObject exceptionDetail) {
        JSONObject zlgjDetail = new JSONObject();
        zlgjDetail.put("smallJiXing", mainDetail.getString("designModel"));// 产品型号->机型
        zlgjDetail.put("jjsl", mainDetail.getString("testQuantity"));// 试制台数->交机数量
        zlgjDetail.put("zjbh", exceptionDetail.getString("pin"));// 整机编号->整机编号
        Properties properties = new Properties();
        properties.put("大挖", "DAWA");
        properties.put("中挖", "ZHONGWA");
        properties.put("小挖", "XIAOWA");
        properties.put("轮挖", "LUNWA");
        properties.put("微挖", "WEIWA");
        properties.put("特挖", "TEWA");
        properties.put("新能源", "XINNENGYUAN");
        properties.put("属具", "SHUJU");
        properties.put("海外", "HAIWAI");
        String productCategory = mainDetail.getString("productCategory");
        zlgjDetail.put("jiXing",
            properties.containsKey(productCategory) ? properties.getProperty(productCategory) : productCategory);// 产品种类->机型类别
        zlgjDetail.put("wtlx", ZlgjConstant.XPSZ);// "XPSZ"->问题类型
        zlgjDetail.put("zrcpzgId", exceptionDetail.getString("repDepLeaderId"));// 第一责任人->第一责任人
        zlgjDetail.put("zrcpzgName", exceptionDetail.getString("repDepLeader"));// 第一责任人->第一责任人
        zlgjDetail.put("ssbmId", exceptionDetail.getString("repDepId"));// 责任部门->责任部门
        zlgjDetail.put("ssbmName", exceptionDetail.getString("repDep"));// 责任部门->责任部门
        zlgjDetail.put("ssbmName", exceptionDetail.getString("repDep"));// 责任部门->责任部门
        zlgjDetail.put("jjcd", exceptionDetail.getString("problemLevel"));// 问题紧急程度->问题响应要求
        zlgjDetail.put("gzlj", exceptionDetail.getString("exceptionPart"));// 异常部件->故障零件
        zlgjDetail.put("wtms", exceptionDetail.getString("exceptionDescription"));// 异常描述->问题详细描述
        zlgjDetail.put("gzxs", exceptionDetail.getString("workingHours"));// 工作小时->工作小时
        zlgjDetail.put("sggk", exceptionDetail.getString("workingCondition"));// 施工工况->施工工况
        zlgjDetail.put("lbjgys", exceptionDetail.getString("supplier"));// 零部件供应商->零部件供应商
        zlgjDetail.put("gzsl", "1");// "1"->故障数量
        zlgjDetail.put("gzl", exceptionDetail.getString("failureRate"));// 故障率->故障率
        zlgjDetail.put("gzbw", exceptionDetail.getString("failurePosition"));// 故障部位->故障部位
        zlgjDetail.put("CREATE_BY_", exceptionDetail.getString("feedbackPersonId"));// 反馈人->创建人
        zlgjDetail.put("wtpcjc", exceptionDetail.getString("testMethod"));// 问题排查过程及检测方法->问题排查过程及检测方法
        zlgjDetail.put("xcczfa", exceptionDetail.getString("disposalMethod"));// 现场处置方法->现场处置方法
        zlgjDetail.put("gjyq", exceptionDetail.getString("improvementRequirements"));// 改进要求->改进要求
        zlgjDetail.put("yzcd", exceptionDetail.getString("severity"));// 问题严重度->问题严重度
        zlgjDetail.put("ifgj", exceptionDetail.getString("isImprove").equalsIgnoreCase("是") ? "yes" : "no");// 是否需要改进->是否需要改进
        return zlgjDetail;
    }

    // ..启动流程
    private JsonResult startProcess(String solId, JSONObject zlgjDetail) throws Exception {
        ProcessMessage handleMessage = new ProcessMessage();
        BpmInst bpmInst = null;
        JsonResult result = new JsonResult();
        IUser currentUser = ContextUtil.getCurrentUser();// 暂存
        try {
            IUser user = userService.getByUserId(zlgjDetail.getString("CREATE_BY_"));
            ContextUtil.setCurUser(user);
            ProcessHandleHelper.setProcessMessage(handleMessage);
            ProcessStartCmd startCmd = new ProcessStartCmd();
            startCmd.setSolId(solId);
            startCmd.setJsonData(zlgjDetail.toJSONString());
            // 启动流程
            bpmInstManager.doStartProcess(startCmd);
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

    // ..同步文件
    private JsonResult synFile(JSONObject exceptionDetail, String wtId) {
        JsonResult result = new JsonResult();
        try {
            List<JSONObject> fileListOri = new ArrayList<>();
            List<String> businessIdList = new ArrayList<>();
            businessIdList.add(exceptionDetail.getString("id"));
            JSONObject param = new JSONObject();
            param.put("businessIds", businessIdList);
            fileListOri = newproductAssemblyKanbanDao.queryFileList(param);
            // 源文件基础目录
            String filePathBaseOri =
                sysDicManager.getBySysTreeKeyAndDicKey("newproductAssemblyUploadPosition", "exceptionFile").getValue();
            // 目标文件下载基础目录
            String filePathBaseTarget = WebAppUtil.getProperty("zlgjFilePathBase");
            // 目标文件预览基础目录
            String filePathBase_viewTarget = WebAppUtil.getProperty("zlgjFilePathBase_preview");
            for (JSONObject fileOri : fileListOri) {
                // 写入目标文件数据库
                JSONObject fileTarget = new JSONObject();
                fileTarget.put("id", IdUtil.getId());
                fileTarget.put("fileName", fileOri.getString("fileName"));
                fileTarget.put("fileSize", fileOri.getString("fileSize"));
                fileTarget.put("wtId", wtId);
                fileTarget.put("fjlx", "gztp");
                fileTarget.put("CREATE_BY_", exceptionDetail.getString("feedbackPersonId"));
                zlgjWTDao.addFileInfos(fileTarget);
                // 扩展名，共用
                String suffix = CommonFuns.toGetFileSuffix(fileOri.getString("fileName"));
                // 源文件全路径
                String fileFullPathOri = filePathBaseOri + File.separator + exceptionDetail.getString("id")
                    + File.separator + fileOri.getString("id") + "." + suffix;
                // 向目标文件下载目录中写入文件
                String filePathTarget = filePathBaseTarget + File.separator + wtId;
                File pathFileTarget = new File(filePathTarget);
                if (!pathFileTarget.exists()) {
                    pathFileTarget.mkdirs();
                }
                String fileFullPathTarget = filePathTarget + File.separator + fileTarget.getString("id") + "." + suffix;
                FileUtil.copyFile(fileFullPathOri, fileFullPathTarget);
                // 向目标文件预览目录中写入文件
                String filePath_viewTarget = filePathBase_viewTarget + File.separator + wtId;
                File pathFile_viewTarget = new File(filePath_viewTarget);
                if (!pathFile_viewTarget.exists()) {
                    pathFile_viewTarget.mkdirs();
                }
                String fileFullPath_viewTarget =
                    filePath_viewTarget + File.separator + fileTarget.getString("id") + "." + suffix;
                FileUtil.copyFile(fileFullPathOri, fileFullPath_viewTarget);
            }
            result.setSuccess(true);
            return result;
        } catch (Exception ex) {
            result.setSuccess(false);
            result.setMessage("文件同步失败!" + ex.getMessage());
            return result;
        }
    }

    // ..质量改进回调，处理异常信息更新
    public void doSynZlgjToUpdateException(JSONObject zlgjJson) {
        JSONObject exceptionJson = newproductAssemblyKanbanDao.queryExceptionDetailByRefId(zlgjJson.getString("wtId"));
        if (exceptionJson != null) {
            if (StringUtil.isNotEmpty(zlgjJson.getString("zrrId"))) {
                exceptionJson.put("repUserId", zlgjJson.getString("zrrId"));// 问题处理人<-问题处理人
            }
            if (StringUtil.isNotEmpty(zlgjJson.getString("zrrName"))) {
                exceptionJson.put("repUser", zlgjJson.getString("zrrName"));// 问题处理人<-问题处理人
            }
            if (StringUtil.isNotEmpty(zlgjJson.getString("yzcd"))) {
                exceptionJson.put("severity", zlgjJson.getString("yzcd"));// 问题严重度<-问题严重度
            }
            if (StringUtil.isNotEmpty(zlgjJson.getString("ifgj"))) {
                exceptionJson.put("isImprove", zlgjJson.getString("ifgj").equalsIgnoreCase("yes") ? "是" : "否");// 是否需要改进<-是否需要改进
            }
            if (StringUtil.isNotEmpty(zlgjJson.getString("noReason"))) {
                exceptionJson.put("noImproveReason", zlgjJson.getString("noReason"));// 不改进理由<-不改进理由
            }
            // 处理临时措施的拼接
            List<JSONObject> lscsList = zlgjWTDao.getLscsList(zlgjJson);
            if (lscsList.size() > 0) {
                StringBuilder temporaryMeasuresBuilder = new StringBuilder();
                StringBuilder temporaryTimeBuilder = new StringBuilder();
                for (JSONObject lscs : lscsList) {
                    temporaryMeasuresBuilder.append(lscs.getString("dcfa")).append(";");
                    if (StringUtils.isNotBlank(lscs.getString("wcTime"))) {
                        temporaryTimeBuilder.append(lscs.getString("wcTime").substring(0, 10)).append(";");
                    }
                }
                exceptionJson.put("temporaryMeasures",
                    temporaryMeasuresBuilder.substring(0, temporaryMeasuresBuilder.length() - 1));// 临时处理措施<-临时措施.对策方案
                if (temporaryTimeBuilder.length() > 0) {
                    exceptionJson.put("temporaryTime", temporaryTimeBuilder.// 临时处理时间<-临时措施.完成时间
                        substring(0, temporaryTimeBuilder.length() - 1));
                } else {
                    exceptionJson.put("temporaryTime", "");
                }
            }
            // 处理永久方案的额拼接
            List<JSONObject> fajjList = zlgjWTDao.getfajjList(zlgjJson);
            if (fajjList.size() > 0) {
                StringBuilder permanentMeasuresBuilder = new StringBuilder();
                StringBuilder permanentTimeBuilder = new StringBuilder();
                for (JSONObject fajj : fajjList) {
                    permanentMeasuresBuilder.append(fajj.getString("cqcs")).append(";");
                    if (StringUtils.isNotBlank(fajj.getString("wcTime"))) {
                        permanentTimeBuilder.append(fajj.getString("wcTime").substring(0, 10)).append(";");
                    }
                }
                exceptionJson.put("permanentMeasures",
                    permanentMeasuresBuilder.substring(0, permanentMeasuresBuilder.length() - 1));// 最终解决方案<-永久解决方案.长期措施
                if (permanentTimeBuilder.length() > 0) {
                    exceptionJson.put("permanentTime", permanentTimeBuilder.// 最终解决方案时间<-永久解决方案.实际完成时间
                        substring(0, permanentTimeBuilder.length() - 1));
                } else {
                    exceptionJson.put("permanentTime", "");
                }
            }
            newproductAssemblyKanbanDao.updateException(exceptionJson);
        }
    }

    // ..质量改进回调，处理异常信息的闭环
    public void doSynZlgjToClearException(JSONObject zlgjJson) {
        JSONObject exceptionJson = newproductAssemblyKanbanDao.queryExceptionDetailByRefId(zlgjJson.getString("wtId"));
        if (exceptionJson != null) {
            exceptionJson.put("isClear", "是");
            newproductAssemblyKanbanDao.updateException(exceptionJson);
        }
    }

    // ..
    public JSONObject getExceptionDetail(String businessId) {
        JSONObject jsonObject = newproductAssemblyKanbanDao.queryExceptionDetailById(businessId);
        if (jsonObject == null) {
            return new JSONObject();
        }
        return jsonObject;
    }

    // ..
    public void saveException(JsonResult result, String itemDataStr) {
        JSONObject formDataJson = JSONObject.parseObject(itemDataStr);
        if (formDataJson == null || formDataJson.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        try {
            IUser user = userService.getByUserId(formDataJson.getString("repDepLeaderId"));
            if (user != null) {
                IGroup group = groupService.getMainByUserId(user.getUserId());
                if (group != null) {
                    formDataJson.put("repDepId", group.getIdentityId());
                    formDataJson.put("repDep", group.getIdentityName());
                }
            }
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                formDataJson.put("id", IdUtil.getId());
                formDataJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                formDataJson.put("CREATE_TIME_", new Date());
                List<JSONObject> alreadyHaves =
                    newproductAssemblyKanbanDao.queryExceptionList(formDataJson.getString("mainId"));
                boolean isAlreadyHave = false;
                for (JSONObject jsonObject : alreadyHaves) {
                    if (jsonObject.getString("indexLocal").equalsIgnoreCase(formDataJson.getString("indexLocal"))) {
                        isAlreadyHave = true;
                        break;
                    }
                }
                if (isAlreadyHave == false) {
                    newproductAssemblyKanbanDao.insertException(formDataJson);
                    result.setData(formDataJson.getString("id"));
                } else {
                    result.setSuccess(false);
                    result.setMessage("已经存在本地异常号相同的记录！");
                }
            } else {
                formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                formDataJson.put("UPDATE_TIME_", new Date());
                newproductAssemblyKanbanDao.updateException(formDataJson);
                result.setData(formDataJson.getString("id"));
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getCause().toString());
            return;
        }
    }

    // ..
    public List<JSONObject> getFileList(List<String> businessIdList) {
        List<JSONObject> fileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIdList);
        fileList = newproductAssemblyKanbanDao.queryFileList(param);
        return fileList;
    }

    // ..
    public void importItem(JSONObject result, HttpServletRequest request) {
        int count = 0;
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            MultipartFile fileObj = multipartRequest.getFile("importFile");
            if (fileObj == null) {
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            String fileName = ((CommonsMultipartFile)fileObj).getFileItem().getName();
            ((CommonsMultipartFile)fileObj).getFileItem().getName().endsWith(ExcelReaderUtil.EXCEL03_EXTENSION);
            Workbook wb = null;
            if (fileName.endsWith(ExcelReaderUtil.EXCEL03_EXTENSION)) {
                wb = new HSSFWorkbook(fileObj.getInputStream());
            } else if (fileName.endsWith(ExcelReaderUtil.EXCEL07_EXTENSION)) {
                wb = new XSSFWorkbook(fileObj.getInputStream());
            }
            Sheet sheet = wb.getSheet("模板");
            if (sheet == null) {
                logger.error("找不到导入模板");
                result.put("message", "数据导入失败，找不到导入模板导入页！");
                return;
            }
            int rowNum = sheet.getPhysicalNumberOfRows();
            if (rowNum < 2) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }

            // 解析标题部分
            Row titleRow = sheet.getRow(1);
            if (titleRow == null) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }
            List<String> titleList = new ArrayList<>();
            for (int i = 0; i < titleRow.getLastCellNum(); i++) {
                titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
            }

            if (rowNum < 3) {
                logger.info("数据行为空");
                result.put("message", "数据导入完成，数据行为空！");
                return;
            }
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> itemList = new ArrayList<>();
            String id = IdUtil.getId();
            for (int i = 2; i < rowNum; i++) {
                count = i;
                Row row = sheet.getRow(i);
                JSONObject rowParse =
                    generateItemDataFromRow(row, itemList, titleList, id, RequestUtil.getString(request, "pin"));
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            List<JSONObject> alreadyHaves =
                newproductAssemblyKanbanDao.queryExceptionList(RequestUtil.getString(request, "mainId"));
            for (int i = 0; i < itemList.size(); i++) {
                // 默认字段
                itemList.get(i).put("mainId", RequestUtil.getString(request, "mainId"));
                // 整机编号直接导入了，因为主信息不止一个了，导入时判断必须在主信息设定的编号范围内
                // itemList.get(i).put("pin", RequestUtil.getString(request, "pin"));
                itemList.get(i).put("problemLevel",
                    sysDicManager.getBySysTreeKeyAndDicKey("newProductAssembly-problemLevel", "一般").getValue());
                itemList.get(i).put("workingHours", "0");
                itemList.get(i).put("severity",
                    sysDicManager.getBySysTreeKeyAndDicKey("newProductAssembly-severity", "C").getValue());
                itemList.get(i).put("isImprove", "是");
                itemList.get(i).put("isSyn", "否");
                itemList.get(i).put("isClear", "否");
                boolean isAlreadyHave = false;// 是否已经存在相同异常号且未同步的记录
                for (JSONObject jsonObject : alreadyHaves) {
                    if (jsonObject.getString("isSyn").equalsIgnoreCase("否")) {
                        if (jsonObject.getString("indexLocal")
                            .equalsIgnoreCase(itemList.get(i).get("indexLocal").toString())) {
                            itemList.get(i).put("id", jsonObject.getString("id"));
                            newproductAssemblyKanbanDao.updateException(itemList.get(i));
                            isAlreadyHave = true;
                            break;
                        }
                    }
                }
                if (isAlreadyHave == false) {
                    newproductAssemblyKanbanDao.insertException(itemList.get(i));
                }
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importExcel", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    // ..
    private JSONObject generateItemDataFromRow(Row row, List<Map<String, Object>> itemList, List<String> titleList,
        String mainId, String pin) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = ExcelUtil.getCellFormatValue(cell);
            }
            switch (title) {
                case "整机编号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "整机编号为空");
                        return oneRowCheck;
                    }
                    if (cellValue.length() != 17) {
                        oneRowCheck.put("message", "整机编号必须是17位");
                        return oneRowCheck;
                    }
                    if (!pin.contains(cellValue)) {
                        oneRowCheck.put("message", "整机编号必须在档案范围内");
                        return oneRowCheck;
                    }
                    oneRowMap.put("pin", cellValue);
                    break;
                case "本机异常号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "本机异常号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("indexLocal", cellValue);
                    break;
                case "异常类型":// 必输,字典约束
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "异常类型为空");
                        return oneRowCheck;
                    }
                    if (StringUtils.isNotBlank(cellValue)) {
                        String sysDicCheckResult = sysDicCheck(cellValue, "exceptionType");
                        if (!sysDicCheckResult.equalsIgnoreCase("ok")) {
                            oneRowCheck.put("message", "异常类型必须为字典要求的值:" + sysDicCheckResult);
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("exceptionType", cellValue);
                    break;
                case "部件分类":// 必输,字典约束
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "部件分类为空");
                        return oneRowCheck;
                    }
                    if (StringUtils.isNotBlank(cellValue)) {
                        String sysDicCheckResult = sysDicCheck(cellValue, "partsCategory");
                        if (!sysDicCheckResult.equalsIgnoreCase("ok")) {
                            oneRowCheck.put("message", "部件分类必须为字典要求的值:" + sysDicCheckResult);
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("partsCategory", cellValue);
                    break;
                case "异常部件":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "异常部件为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("exceptionPart", cellValue);
                    break;
                case "异常描述":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "异常描述为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("exceptionDescription", cellValue);
                    break;
                case "异常节点":// 必输,字典约束
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "异常节点为空");
                        return oneRowCheck;
                    }
                    if (StringUtils.isNotBlank(cellValue)) {
                        String sysDicCheckResult = sysDicCheck(cellValue, "exceptionNode");
                        if (!sysDicCheckResult.equalsIgnoreCase("ok")) {
                            oneRowCheck.put("message", "异常节点必须为字典要求的值:" + sysDicCheckResult);
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("assemblyNode", cellValue);
                    break;
                case "施工工况":
                    oneRowMap.put("workingCondition", cellValue);
                    break;
                case "零部件供应商":
                    oneRowMap.put("supplier", cellValue);
                    break;
                case "故障率":
                    oneRowMap.put("failureRate", cellValue);
                    break;
                case "故障部位":
                    oneRowMap.put("failurePosition", cellValue);
                    break;
                case "第一责任人":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "第一责任人为空");
                        return oneRowCheck;
                    }
                    if (StringUtils.isNotBlank(cellValue)) {
                        List<JSONObject> jsonObjects = newproductAssemblyKanbanDao.getUserByFullName(cellValue);
                        if (jsonObjects.size() == 0) {
                            oneRowCheck.put("message", "用户不存在，建议先不导入姓名，后期手工维护");
                            return oneRowCheck;
                        } else if (jsonObjects.size() > 1) {
                            oneRowCheck.put("message", "系统中存在同名用户，建议先不导入姓名，后期手工维护");
                            return oneRowCheck;
                        }
                        oneRowMap.put("repDepLeaderId", jsonObjects.get(0).getString("USER_ID_"));
                        oneRowMap.put("repDepLeader", cellValue);
                        // 直接把责任部门也给自动生成了
                        IUser user = userService.getByUserId(jsonObjects.get(0).getString("USER_ID_"));
                        if (user != null) {
                            IGroup group = groupService.getMainByUserId(user.getUserId());
                            if (group != null) {
                                oneRowMap.put("repDepId", group.getIdentityId());
                                oneRowMap.put("repDep", group.getIdentityName());
                            }
                        }
                    }
                    break;
                case "反馈人":// 不必输,但必须有用户且不重名，一次性将部门信息也查出来了
                    if (StringUtils.isNotBlank(cellValue)) {
                        List<JSONObject> jsonObjects = newproductAssemblyKanbanDao.getUserByFullName(cellValue);
                        if (jsonObjects.size() == 0) {
                            oneRowCheck.put("message", "用户不存在，建议先不导入姓名，后期手工维护");
                            return oneRowCheck;
                        } else if (jsonObjects.size() > 1) {
                            oneRowCheck.put("message", "系统中存在同名用户，建议先不导入姓名，后期手工维护");
                            return oneRowCheck;
                        }
                        oneRowMap.put("feedbackPersonId", jsonObjects.get(0).getString("USER_ID_"));
                        oneRowMap.put("feedbackPerson", cellValue);
                    }
                    break;
                case "问题排查过程及检测方法":
                    oneRowMap.put("testMethod", cellValue);
                    break;
                case "现场处置方法":
                    oneRowMap.put("disposalMethod", cellValue);
                    break;
                case "改进要求":
                    oneRowMap.put("improvementRequirements", cellValue);
                    break;
                case "备注":
                    oneRowMap.put("remark", cellValue);
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("id", IdUtil.getId());
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    // ..
    public ResponseEntity<byte[]> importItemTemplateDownload() {
        try {
            String fileName = "新品装配异常信息导入模板.xlsx";
            // 创建文件实例
            File file = new File(
                MaterielService.class.getClassLoader().getResource("templates/newproductAssembly/" + fileName).toURI());
            String finalDownloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 finalDownloadFileName
            headers.setContentDispositionFormData("attachment", finalDownloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in importItemTemplateDownload", e);
            return null;
        }
    }

    // ..
    public void deleteOneExceptionFile(String fileId, String fileName, String businessId) {
        String filePathBase =
            sysDicManager.getBySysTreeKeyAndDicKey("newproductAssemblyUploadPosition", "exceptionFile").getValue();
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, businessId, filePathBase);
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        newproductAssemblyKanbanDao.deleteExceptionFile(param);
    }

    // ..
    public void saveUploadFiles(HttpServletRequest request) {
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
        String filePathBase =
            sysDicManager.getBySysTreeKeyAndDicKey("newproductAssemblyUploadPosition", "exceptionFile").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find FilePathBase");
            return;
        }
        try {
            String mainId = toGetParamVal(parameters.get("mainId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + mainId;
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
            fileInfo.put("mainId", mainId);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            newproductAssemblyKanbanDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    // ..
    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    // 检查字典值
    private String sysDicCheck(String cellValue, String treeKey) {
        List<SysDic> sysDicList = sysDicManager.getByTreeKey(treeKey);
        boolean isok = false;
        StringBuilder stringBuilder = new StringBuilder();
        for (SysDic sysDic : sysDicList) {
            if (cellValue.equalsIgnoreCase(sysDic.getName())) {
                isok = true;
            }
            stringBuilder.append(sysDic.getName()).append(",");
        }
        if (isok == false) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            return stringBuilder.toString();
        } else {
            return "ok";
        }
    }

    // ..以下异常汇总相关
    // ..
    public JsonPageResult<?> exceptionSummaryListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParamsExceptionSummary(params, request);
        List<JSONObject> businessList = newproductAssemblyKanbanDao.exceptionSummaryListQuery(params);
        int businessListCount = newproductAssemblyKanbanDao.countExceptionSummaryListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    // ..
    private void getListParamsExceptionSummary(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "orderReleaseTime");
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
        // 增加分页条件
        params.put("startIndex", 0);
        params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
    }

    // ..
    public void exportExceptionSummaryList(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> params = new HashMap<>();
        getListParamsExceptionSummary(params, request);
        params.remove("startIndex");
        params.remove("pageSize");
        List<JSONObject> listData = newproductAssemblyKanbanDao.exceptionSummaryListQuery(params);
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "新品装配异常信息汇总";
        String excelName = nowDate + title;
        String[] fieldNames = {"产品型号", "试验台数", "说明", "产品种类", "产品部门", "项目负责人", "计划类型", "订单下达时间", "整机编号", "异常类型", "部件分类",
            "异常部件", "异常描述", "异常节点", "责任部门", "反馈人", "反馈时间", "现场处置方法", "改进要求", "临时处理措施", "临时处理时间", "永久解决方案", "永久解决方案时间",
            "是否闭环", "备注"};
        String[] fieldCodes = {"designModel", "testQuantity", "theExplain", "productCategory", "productDep",
            "projectLeader", "planCategory", "orderReleaseTime", "pin", "exceptionType", "partsCategory",
            "exceptionPart", "exceptionDescription", "assemblyNode", "repDep", "feedbackPerson", "feedbackTime",
            "disposalMethod", "improvementRequirements", "temporaryMeasures", "temporaryTime", "permanentMeasures",
            "permanentTime", "isClear", "remark"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    // ..以下年度计划相关
    // ..
    public JsonPageResult<?> annualPlanDepListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParamsAnnualPlanSummary(params, request);
        List<JSONObject> businessList = newproductAssemblyKanbanDao.annualPlanDepListQuery(params);
        int businessListCount = newproductAssemblyKanbanDao.countAnnualPlanDepListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    // ..
    private void getListParamsAnnualPlanSummary(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "signYear");
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
        // 增加分页条件
        params.put("startIndex", 0);
        params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
    }

    // ..
    public JsonResult deleteAnnualPlanDep(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        newproductAssemblyKanbanDao.deleteAnnualPlanDep(param);// 批删主信息
        return result;
    }

    // ..
    public void saveAnnualPlanDep(JsonResult result, String dataStr) {
        JSONArray businessObjs = JSONObject.parseArray(dataStr);
        if (businessObjs == null || businessObjs.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("内容为空，操作失败！");
            return;
        }
        for (Object object : businessObjs) {
            JSONObject businessObj = (JSONObject)object;
            if (StringUtils.isBlank(businessObj.getString("id"))) {
                businessObj.put("id", IdUtil.getId());
                businessObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                businessObj.put("CREATE_TIME_", new Date());
                newproductAssemblyKanbanDao.insertAnnualPlanDep(businessObj);
            } else {
                businessObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                businessObj.put("UPDATE_TIME_", new Date());
                newproductAssemblyKanbanDao.updateAnnualPlanDep(businessObj);
            }
        }
    }

    // ..
    public JsonPageResult<?> annualPlanPrdListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParamsAnnualPlanSummary(params, request);
        List<JSONObject> businessList = newproductAssemblyKanbanDao.annualPlanPrdListQuery(params);
        int businessListCount = newproductAssemblyKanbanDao.countAnnualPlanPrdListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    // ..
    public JsonResult deleteAnnualPlanPrd(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        newproductAssemblyKanbanDao.deleteAnnualPlanPrd(param);// 批删主信息
        return result;
    }

    // ..
    public void saveAnnualPlanPrd(JsonResult result, String dataStr) {
        JSONArray businessObjs = JSONObject.parseArray(dataStr);
        if (businessObjs == null || businessObjs.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("内容为空，操作失败！");
            return;
        }
        for (Object object : businessObjs) {
            JSONObject businessObj = (JSONObject)object;
            if (StringUtils.isBlank(businessObj.getString("id"))) {
                businessObj.put("id", IdUtil.getId());
                businessObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                businessObj.put("CREATE_TIME_", new Date());
                newproductAssemblyKanbanDao.insertAnnualPlanPrd(businessObj);
            } else {
                businessObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                businessObj.put("UPDATE_TIME_", new Date());
                newproductAssemblyKanbanDao.updateAnnualPlanPrd(businessObj);
            }
        }
    }

    // ..以下看板相关
    // ..试验执行及进度
    public JSONObject getCompletionData(HttpServletRequest request, HttpServletResponse response, String postDataStr) {
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        postDataJson.put("orderReleaseTimeBegin", postDataJson.getString("signYear") + "-01-01");
        postDataJson.put("orderReleaseTimeEnd", postDataJson.getString("signYear") + "-12-31");
        // postDataJson.put("realYear", postDataJson.getString("signYear"));
        List<JSONObject> businessListPlan = newproductAssemblyKanbanDao.dataListQuery(postDataJson);
        // dataset.dimensions
        List<String> dimensions = new ArrayList<>();
        if (postDataJson.getString("action").equalsIgnoreCase("monthly")) {
            dimensions.add("月份");
            dimensions.add("装配上线");
            dimensions.add("转序完成");
        } else if (postDataJson.getString("action").equalsIgnoreCase("prd")) {
            dimensions.add("种类");
            // dimensions.add("计划完成");
            dimensions.add("实际完成");
        } else if (postDataJson.getString("action").equalsIgnoreCase("dep")) {
            dimensions.add("部门");
            // dimensions.add("计划完成");
            dimensions.add("实际完成");
        } else if (postDataJson.getString("action").equalsIgnoreCase("prdThreeBar")) {
            dimensions.add("种类");
            dimensions.add("计划完成");
            dimensions.add("当前完成");
            dimensions.add("当年完成");
        } else if (postDataJson.getString("action").equalsIgnoreCase("prdThreeBar")) {
            dimensions.add("种类");
            dimensions.add("计划完成");
            dimensions.add("当前完成");
            dimensions.add("当年完成");
        } else if (postDataJson.getString("action").equalsIgnoreCase("depThreeBar")) {
            dimensions.add("部门");
            dimensions.add("计划完成");
            dimensions.add("当前完成");
            dimensions.add("当年完成");
        }
        // dataset.source
        List<JSONObject> sources = new ArrayList<>();
        JSONObject Statistics = new JSONObject(new LinkedHashMap());
        if (postDataJson.getString("action").equalsIgnoreCase("monthly")) {
            // 初始化各月份
            for (int i = 0; i < 12; i++) {
                JSONObject source = new JSONObject();
                String month = i < 9 ? "0" + String.valueOf(i + 1) : String.valueOf(i + 1);
                source.put("月份", month);
                source.put("装配上线", 0);
                source.put("转序完成", 0);
                Statistics.put(month, source);
            }
        } else if (postDataJson.getString("action").equalsIgnoreCase("prd")) {
            SysTree sysTree = sysTreeManager.getByKey("productCategory");
            List<SysDic> sysDics = new ArrayList<SysDic>();
            sysDics = sysDicManager.getByTreeId(sysTree.getTreeId());
            // 初始化各种类
            for (SysDic sysDic : sysDics) {
                // JSONObject params = new JSONObject();
                // params.put("productCategory", sysDic.getValue());
                // List<JSONObject> temp = newproductAssemblyKanbanDao.annualPlanPrdListQuery(params);
                // Integer plancount = temp.size() == 0 ? 0 : temp.get(0).getInteger("testQuantity");
                JSONObject source = new JSONObject();
                source.put("种类", sysDic.getValue());
                // source.put("计划完成", plancount);
                source.put("实际完成", 0);
                Statistics.put(sysDic.getValue(), source);
            }
        } else if (postDataJson.getString("action").equalsIgnoreCase("dep")) {
            SysTree sysTree = sysTreeManager.getByKey("newProductAssembly-applyDep");
            List<SysDic> sysDics = new ArrayList<SysDic>();
            sysDics = sysDicManager.getByTreeId(sysTree.getTreeId());
            // 初始化各部门
            for (SysDic sysDic : sysDics) {
                // JSONObject params = new JSONObject();
                // params.put("productDep", sysDic.getValue());
                // List<JSONObject> temp = newproductAssemblyKanbanDao.annualPlanDepListQuery(params);
                /// Integer plancount = temp.size() == 0 ? 0 : temp.get(0).getInteger("testQuantity");
                JSONObject source = new JSONObject();
                source.put("部门", sysDic.getValue());
                // source.put("计划完成", plancount);
                source.put("实际完成", 0);
                Statistics.put(sysDic.getValue(), source);
            }
        } else if (postDataJson.getString("action").equalsIgnoreCase("prdThreeBar")) {
            SysTree sysTree = sysTreeManager.getByKey("productCategory");
            List<SysDic> sysDics = new ArrayList<SysDic>();
            sysDics = sysDicManager.getByTreeId(sysTree.getTreeId());
            // 初始化各种类ThreeBar
            for (SysDic sysDic : sysDics) {
                JSONObject params = new JSONObject();
                params.put("realYear", postDataJson.getString("signYear"));
                params.put("productCategory", sysDic.getValue());
                List<JSONObject> temp = newproductAssemblyKanbanDao.annualPlanPrdListQuery(params);
                Integer plancount = temp.size() == 0 ? 0 : temp.get(0).getInteger("testQuantity");
                JSONObject source = new JSONObject();
                source.put("种类", sysDic.getValue());
                source.put("计划完成", plancount);
                source.put("当前完成", 0);
                source.put("当年完成", 0);
                Statistics.put(sysDic.getValue(), source);
            }
        } else if (postDataJson.getString("action").equalsIgnoreCase("depThreeBar")) {
            SysTree sysTree = sysTreeManager.getByKey("newProductAssembly-applyDep");
            List<SysDic> sysDics = new ArrayList<SysDic>();
            sysDics = sysDicManager.getByTreeId(sysTree.getTreeId());
            // 初始化各种类ThreeBar
            for (SysDic sysDic : sysDics) {
                JSONObject params = new JSONObject();
                params.put("realYear", postDataJson.getString("signYear"));
                params.put("productDep", sysDic.getValue());
                List<JSONObject> temp = newproductAssemblyKanbanDao.annualPlanDepListQuery(params);
                Integer plancount = temp.size() == 0 ? 0 : temp.get(0).getInteger("testQuantity");
                JSONObject source = new JSONObject();
                source.put("部门", sysDic.getValue());
                source.put("计划完成", plancount);
                source.put("当前完成", 0);
                source.put("当年完成", 0);
                Statistics.put(sysDic.getValue(), source);
            }
        }
        // --dataset数据填充
        if (postDataJson.getString("action").equalsIgnoreCase("monthly")) {
            // 处理各月数据
            for (JSONObject business : businessListPlan) {
                if (StringUtil.isNotEmpty(business.getString("orderReleaseTime"))
                    && business.getString("orderReleaseTime").substring(0, 4)
                        .equalsIgnoreCase(postDataJson.getString("signYear"))) {// 必须在postDataJson.getString("signYear")范围内
                    String month = business.getString("orderReleaseTime").substring(5, 7);
                    JSONObject source = Statistics.getJSONObject(month);
                    // todo:改为通过testQuantity数量计数
                    if (source != null) {
                        // source.put("装配上线", source.getIntValue("装配上线") + 1);
                        source.put("装配上线", source.getIntValue("装配上线") + business.getIntValue("testQuantity"));
                    }
                    if (StringUtil.isNotEmpty(business.getString("prototypeSequenceTime"))
                        && business.getString("prototypeSequenceTime").substring(0, 4)
                            .equalsIgnoreCase(postDataJson.getString("signYear"))) {// 必须在postDataJson.getString("signYear")范围内
                        // source.put("转序完成", source.getIntValue("转序完成") + 1);
                        source.put("转序完成", source.getIntValue("转序完成") + business.getIntValue("testQuantity"));
                    }
                    Statistics.put(month, source);
                }
            }
        } else if (postDataJson.getString("action").equalsIgnoreCase("prd")) {
            // 处理各种类数据
            for (JSONObject business : businessListPlan) {
                String productCategory = business.getString("productCategory");
                JSONObject source = Statistics.getJSONObject(productCategory);
                if (source != null && StringUtil.isNotEmpty(business.getString("prototypeSequenceTime"))) {
                    // source.put("实际完成", source.getIntValue("实际完成") + 1);
                    source.put("实际完成", source.getIntValue("实际完成") + business.getIntValue("testQuantity"));
                }
                Statistics.put(productCategory, source);
            }
        } else if (postDataJson.getString("action").equalsIgnoreCase("dep")) {
            // 处理各部门数据
            for (JSONObject business : businessListPlan) {
                String productDep = business.getString("productDep");
                JSONObject source = Statistics.getJSONObject(productDep);
                if (source != null && StringUtil.isNotEmpty(business.getString("prototypeSequenceTime"))) {
                    // source.put("实际完成", source.getIntValue("实际完成") + 1);
                    source.put("实际完成", source.getIntValue("实际完成") + business.getIntValue("testQuantity"));
                }
                Statistics.put(productDep, source);
            }
        } else if (postDataJson.getString("action").equalsIgnoreCase("prdThreeBar")) {
            // 处理各种类数据ThreeBar
            JSONObject params = new JSONObject();
            params.put("realYear", postDataJson.getString("signYear"));
            List<JSONObject> businessListPlan2 = newproductAssemblyKanbanDao.dataListQuery(params);// 当前完成
            for (JSONObject business2 : businessListPlan2) {
                String productCategory = business2.getString("productCategory");
                JSONObject source = Statistics.getJSONObject(productCategory);
                if (source != null && StringUtil.isNotEmpty(business2.getString("prototypeSequenceTime"))) {
                    source.put("当前完成", source.getIntValue("当前完成") + business2.getIntValue("testQuantity"));
                }
                Statistics.put(productCategory, source);
            }

            params.clear();
            params.put("prototypeSequenceTimeBegin", postDataJson.getString("signYear") + "-01");
            params.put("prototypeSequenceTimeEnd", postDataJson.getString("signYear") + "-12");
            List<JSONObject> businessListPlan3 = newproductAssemblyKanbanDao.dataListQuery(params);// 当年完成
            for (JSONObject business3 : businessListPlan3) {
                String productCategory = business3.getString("productCategory");
                JSONObject source = Statistics.getJSONObject(productCategory);
                if (source != null) {
                    source.put("当年完成", source.getIntValue("当年完成") + business3.getIntValue("testQuantity"));
                }
                Statistics.put(productCategory, source);
            }
        } else if (postDataJson.getString("action").equalsIgnoreCase("depThreeBar")) {
            // 处理各种类数据ThreeBar
            JSONObject params = new JSONObject();
            params.put("realYear", postDataJson.getString("signYear"));
            List<JSONObject> businessListPlan2 = newproductAssemblyKanbanDao.dataListQuery(params);// 当前完成
            for (JSONObject business2 : businessListPlan2) {
                String productDep = business2.getString("productDep");
                JSONObject source = Statistics.getJSONObject(productDep);
                if (source != null && StringUtil.isNotEmpty(business2.getString("prototypeSequenceTime"))) {
                    source.put("当前完成", source.getIntValue("当前完成") + business2.getIntValue("testQuantity"));
                }
                Statistics.put(productDep, source);
            }

            params.clear();
            params.put("prototypeSequenceTimeBegin", postDataJson.getString("signYear") + "-01");
            params.put("prototypeSequenceTimeEnd", postDataJson.getString("signYear") + "-12");
            List<JSONObject> businessListPlan3 = newproductAssemblyKanbanDao.dataListQuery(params);// 当年完成
            for (JSONObject business3 : businessListPlan3) {
                String productDep = business3.getString("productDep");
                JSONObject source = Statistics.getJSONObject(productDep);
                if (source != null) {
                    source.put("当年完成", source.getIntValue("当年完成") + business3.getIntValue("testQuantity"));
                }
                Statistics.put(productDep, source);
            }
        }
        // 构造前端数据结构
        for (Map.Entry entry : Statistics.entrySet()) {
            sources.add((JSONObject)entry.getValue());
        }
        JSONObject resultJson = new JSONObject();
        resultJson.put("dimensions", dimensions);
        resultJson.put("source", sources);
        return resultJson;
    }

    // ..试验执行及进度-饼图用
    public List<JSONObject> getCompletionDataPie(HttpServletRequest request, HttpServletResponse response,
        String postDataStr) {
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        postDataJson.put("orderReleaseTimeBegin", postDataJson.getString("signYear") + "-01");
        postDataJson.put("orderReleaseTimeEnd", postDataJson.getString("signYear") + "-12");
        // postDataJson.put("realYear", postDataJson.getString("signYear"));
        List<JSONObject> businessListPlan = newproductAssemblyKanbanDao.dataListQuery(postDataJson);
        Integer niandu = 0, xinzeng = 0;
        JSONObject jsonObjectNiandu = new JSONObject();
        JSONObject jsonObjectXinzeng = new JSONObject();
        Integer yiwancheng = 0, weiwancheng = 0;
        JSONObject jsonObjectYiwancheng = new JSONObject();
        JSONObject jsonObjectWeiwancheng = new JSONObject();
        List<JSONObject> resultList = new ArrayList<>();
        if (postDataJson.getString("action").equalsIgnoreCase("1")) {
            for (JSONObject business : businessListPlan) {
                if (business.getString("planCategory").equals("年度计划")
                    && StringUtil.isNotEmpty(business.getString("prototypeSequenceTime"))) {
                    // niandu++;
                    niandu += business.getIntValue("testQuantity");
                } else if (business.getString("planCategory").equals("新增计划")
                    && StringUtil.isNotEmpty(business.getString("prototypeSequenceTime"))) {
                    // xinzeng++;
                    xinzeng += business.getIntValue("testQuantity");
                }
            }
            jsonObjectNiandu.put("value", niandu);
            jsonObjectNiandu.put("name", "年度计划完成量");
            jsonObjectXinzeng.put("value", xinzeng);
            jsonObjectXinzeng.put("name", "新增计划完成量");
            resultList.add(jsonObjectNiandu);
            resultList.add(jsonObjectXinzeng);
        } else if (postDataJson.getString("action").equalsIgnoreCase("2")) {
            postDataJson.put("signYear", postDataJson.getString("signYear"));
            List<JSONObject> list = newproductAssemblyKanbanDao.annualPlanDepListQuery(postDataJson);
            Integer total = 0;
            for (JSONObject jsonObject : list) {
                total += jsonObject.getInteger("testQuantity");
            }
            for (JSONObject business : businessListPlan) {
                if (business.getString("planCategory").equals("年度计划")
                    && StringUtil.isNotEmpty(business.getString("prototypeSequenceTime"))) {
                    yiwancheng++;
                }
            }
            weiwancheng = total - yiwancheng;
            jsonObjectYiwancheng.put("value", yiwancheng);
            jsonObjectYiwancheng.put("name", "年度计划已完成量");
            jsonObjectWeiwancheng.put("value", weiwancheng);
            jsonObjectWeiwancheng.put("name", "年度计划完未成量");
            resultList.add(jsonObjectYiwancheng);
            resultList.add(jsonObjectWeiwancheng);
        }
        return resultList;
    }

    // ..新品试制异常统计
    public JsonPageResult<?> exceptionStatisticListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParamsExceptionStatistic(params, request);
        List<JSONObject> businessList = newproductAssemblyKanbanDao.dataListQuery(params);
        // int businessListCount = newproductAssemblyKanbanDao.countDataListQuery(params);
        List<JSONObject> businessListTrue = new ArrayList<>();
        JSONObject businessListTrueJsons = new JSONObject(true);
        for (JSONObject business : businessList) {
            Double exceptionCount = 0d, closedLoopCount = 0d;
            List<JSONObject> exceptionList = newproductAssemblyKanbanDao.queryExceptionList(business.getString("id"));
            for (JSONObject exception : exceptionList) {
                exceptionCount++;
                if (exception.getString("isClear").equals("是")) {
                    closedLoopCount++;
                }
            }
            BigDecimal bigDecimal = exceptionCount == 0 ? new BigDecimal(0d)
                : new BigDecimal(closedLoopCount / exceptionCount).setScale(2, BigDecimal.ROUND_HALF_UP);
            JSONObject businessTrue = new JSONObject();
            businessTrue.put("id", business.getString("id"));
            businessTrue.put("designModel", business.getString("designModel"));
            businessTrue.put("productDep", business.getString("productDep"));
            businessTrue.put("projectLeader", business.getString("projectLeader"));
            businessTrue.put("exceptionQuantity", exceptionCount);
            businessTrue.put("closedLoopQuantity", closedLoopCount);
            businessTrue.put("closedLoopRate", bigDecimal.toString());
            if (businessListTrueJsons.containsKey(businessTrue.getString("designModel"))) {
                JSONObject temp = businessListTrueJsons.getJSONObject(businessTrue.getString("designModel"));
                temp.put("exceptionQuantity",
                    temp.getDouble("exceptionQuantity") + businessTrue.getDouble("exceptionQuantity"));
                temp.put("closedLoopQuantity",
                    temp.getDouble("closedLoopQuantity") + businessTrue.getDouble("closedLoopQuantity"));
                BigDecimal closedLoopRate = temp.getDouble("exceptionQuantity") == 0 ? new BigDecimal(0d)
                    : new BigDecimal(temp.getDouble("closedLoopQuantity") / temp.getDouble("exceptionQuantity"))
                        .setScale(2, BigDecimal.ROUND_HALF_UP);
                temp.put("closedLoopRate", closedLoopRate.toString());
                businessListTrueJsons.put(businessTrue.getString("designModel"), temp);
            } else {
                businessListTrueJsons.put(businessTrue.getString("designModel"), businessTrue);
            }
        }
        for (Map.Entry entry : businessListTrueJsons.entrySet()) {
            // 把异常为0的过滤掉
            if (((JSONObject)entry.getValue()).getIntValue("exceptionQuantity") != 0) {
                businessListTrue.add((JSONObject)entry.getValue());
            }
        }
        result.setData(businessListTrue);
        // result.setTotal(businessListCount);
        return result;
    }

    // ..p
    private void getListParamsExceptionStatistic(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "orderReleaseTime");
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
        // 增加分页条件
        // params.put("startIndex", 0);
        // params.put("pageSize", 20);
        // String pageIndex = request.getParameter("pageIndex");
        // String pageSize = request.getParameter("pageSize");
        // if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
        // params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
        // params.put("pageSize", Integer.parseInt(pageSize));
        // }
    }

    // ..异常闭环及进度
    public JSONObject getExceptionData(HttpServletRequest request, HttpServletResponse response, String postDataStr) {
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        postDataJson.put("orderReleaseTimeBegin", postDataJson.getString("signYear") + "-01-01");
        postDataJson.put("orderReleaseTimeEnd", postDataJson.getString("signYear").length() == 4
            ? postDataJson.getString("signYear") + "-12-31" : postDataJson.getString("signYear") + "-31");
        List<JSONObject> businessListPlan = newproductAssemblyKanbanDao.exceptionSummaryListQuery(postDataJson);
        // dataset.dimensions
        List<String> dimensions = new ArrayList<>();
        if (postDataJson.getString("action").equalsIgnoreCase("partsCategory")) {
            dimensions.add("部件");
            dimensions.add("异常数量");
            dimensions.add("闭环解决");
        } else if (postDataJson.getString("action").equalsIgnoreCase("exceptionType")) {
            dimensions.add("异常类型");
            dimensions.add("异常数量");
            dimensions.add("闭环解决");
        } else if (postDataJson.getString("action").equalsIgnoreCase("dep")) {
            dimensions.add("责任部门");
            dimensions.add("异常数量");
            dimensions.add("闭环解决");
        }
        // dataset.source
        List<JSONObject> sources = new ArrayList<>();
        JSONObject Statistics = new JSONObject(new LinkedHashMap());
        //// ----------------------
        if (postDataJson.getString("action").equalsIgnoreCase("partsCategory")) {
            SysTree sysTree = sysTreeManager.getByKey("partsCategory");
            List<SysDic> sysDics = new ArrayList<SysDic>();
            sysDics = sysDicManager.getByTreeId(sysTree.getTreeId());
            // 初始化各部件
            for (SysDic sysDic : sysDics) {
                JSONObject source = new JSONObject();
                source.put("部件", sysDic.getValue());
                source.put("异常数量", 0);
                source.put("闭环解决", 0);
                Statistics.put(sysDic.getValue(), source);
            }
        } else if (postDataJson.getString("action").equalsIgnoreCase("exceptionType")) {
            SysTree sysTree = sysTreeManager.getByKey("exceptionType");
            List<SysDic> sysDics = new ArrayList<SysDic>();
            sysDics = sysDicManager.getByTreeId(sysTree.getTreeId());
            // 初始化各异常类型
            for (SysDic sysDic : sysDics) {
                JSONObject source = new JSONObject();
                source.put("异常类型", sysDic.getValue());
                source.put("异常数量", 0);
                source.put("闭环解决", 0);
                Statistics.put(sysDic.getValue(), source);
            }
        } else if (postDataJson.getString("action").equalsIgnoreCase("dep")) {
            SysTree sysTree = sysTreeManager.getByKey("newProductAssembly-applyDep2");
            List<SysDic> sysDics = new ArrayList<SysDic>();
            sysDics = sysDicManager.getByTreeId(sysTree.getTreeId());
            // 初始化各所部
            for (SysDic sysDic : sysDics) {
                JSONObject source = new JSONObject();
                source.put("责任部门", sysDic.getValue());
                source.put("异常数量", 0);
                source.put("闭环解决", 0);
                Statistics.put(sysDic.getValue(), source);
            }
        }
        //// ----------------------
        if (postDataJson.getString("action").equalsIgnoreCase("partsCategory")) {
            // 处理各部件数据
            for (JSONObject business : businessListPlan) {
                String partsCategory = business.getString("partsCategory");
                JSONObject source = Statistics.getJSONObject(partsCategory);
                if (source != null) {
                    source.put("异常数量", source.getIntValue("异常数量") + 1);
                    if (business.getString("isClear").equals("是")) {
                        source.put("闭环解决", source.getIntValue("闭环解决") + 1);
                    }
                }
                Statistics.put(partsCategory, source);
            }
        } else if (postDataJson.getString("action").equalsIgnoreCase("exceptionType")) {
            // 处理各异常类型数据
            for (JSONObject business : businessListPlan) {
                String exceptionType = business.getString("exceptionType");
                JSONObject source = Statistics.getJSONObject(exceptionType);
                if (source != null) {
                    source.put("异常数量", source.getIntValue("异常数量") + 1);
                    if (business.getString("isClear").equals("是")) {
                        source.put("闭环解决", source.getIntValue("闭环解决") + 1);
                    }
                }
                Statistics.put(exceptionType, source);
            }
        } else if (postDataJson.getString("action").equalsIgnoreCase("dep")) {
            // 处理各所部数据
            for (JSONObject business : businessListPlan) {
                String repDep = business.getString("repDep");
                JSONObject source = Statistics.getJSONObject(repDep);
                if (source != null) {
                    source.put("异常数量", source.getIntValue("异常数量") + 1);
                    if (business.getString("isClear").equals("是")) {
                        source.put("闭环解决", source.getIntValue("闭环解决") + 1);
                    }
                }
                Statistics.put(repDep, source);
            }
        }

        for (Map.Entry entry : Statistics.entrySet()) {
            sources.add((JSONObject)entry.getValue());
        }
        JSONObject resultJson = new JSONObject();
        resultJson.put("dimensions", dimensions);
        resultJson.put("source", sources);
        return resultJson;
    }
}
