package com.redxun.productDataManagement.core.manager;

import static com.redxun.rdmCommon.core.util.RdmCommonUtil.toGetParamVal;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.BpmSolution;
import com.redxun.bpm.core.entity.ProcessMessage;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmSolutionManager;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.ExceptionUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.productDataManagement.core.dao.ZsjbbDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import org.apache.camel.util.FileUtil;
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
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;

import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;


/**
 * 主数据属性治理工作提报
 *
 * @zwt 2024.3.1
 */
@Service
public class ZsjbbManager {
    private static final Logger logger = LoggerFactory.getLogger(ZsjbbManager.class);

    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private ZsjbbDao zsjbbDao;
    @Autowired
    private SysDicManager sysDicManager;
    @Resource
    private CommonInfoManager commonInfoManager;
    @Resource
    private BpmSolutionManager bpmSolutionManager;
    @Resource
    private UserService userService;
    @Resource
    private BpmInstManager bpmInstManager;
    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    public JsonPageResult<?> queryApplyList(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "zsjbb_base.CREATE_TIME_",
            "desc");
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
        List<JSONObject> applyList = zsjbbDao.queryApplyList(params);
        for (JSONObject oneApply : applyList) {
            if (oneApply.get("CREATE_TIME_") != null) {
                oneApply.put("CREATE_TIME_", DateUtil.formatDate((Date)oneApply.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(applyList, ContextUtil.getCurrentUserId());

        // 根据分页进行subList截取
        List<JSONObject> finalSubList = new ArrayList<JSONObject>();
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

    public JSONObject queryApplyDetail(String id) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(id)) {
            return result;
        }
        JSONObject params = new JSONObject();
        params.put("id", id);
        JSONObject obj = zsjbbDao.queryApplyDetail(params);
        if (obj == null) {
            return result;
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

    public void createZsjbb(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("departId", ContextUtil.getCurrentUser().getMainGroupId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("creatorName", ContextUtil.getCurrentUser().getFullname());

        formData.put("CREATE_TIME_", new Date());
        // 基本信息
        zsjbbDao.insertZsjbb(formData);

    }

    public void updateZsjbb(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        // 基本信息
        zsjbbDao.updateZsjbb(formData);

    }

    public JsonResult delApplys(String[] applyIdArr) {
        List<String> applyIdList = Arrays.asList(applyIdArr);
        JsonResult result = new JsonResult(true, "操作成功！");
        if (applyIdArr.length == 0) {
            return result;
        }
        JSONObject param = new JSONObject();
        param.put("applyIds", applyIdList);

        // 删除附件 限制单选，applyIds其实只有一个,直接删一个就行了
        String applyId = applyIdList.get(0);
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "pdmUploadPosition", "zsjbb").getValue();
        String filePath = filePathBase + File.separator + applyId;
        File fileDir = new File(filePath);
        FileUtil.removeDir(fileDir);
        // 删除主表

        param.put("ids", applyIdList);
        zsjbbDao.deleteZsjbb(param);
        return result;
    }

    // 附件上传
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
            String applyId = toGetParamVal(parameters.get("applyId"));
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                    "pdmUploadPosition", "zsjbb").getValue();
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
            fileInfo.put("id", id);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("applyId", applyId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            zsjbbDao.insertFile(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public List<JSONObject> queryFileList(JSONObject params) {
        List<JSONObject> fileList = zsjbbDao.queryFileList(params);
        return fileList;
    }
//
//    public JsonResult chooseYxal(String[] applyIdArr) {
//        List<String> applyIdList = Arrays.asList(applyIdArr);
//        JsonResult result = new JsonResult(true, "操作成功！");
//        if (applyIdArr.length == 0) {
//            return result;
//        }
//        // 这里查询已最大数量和已有数量
//        String deptName = ContextUtil.getCurrentUser().getMainGroupName();
//        JSONObject params = new JSONObject();
//        params.put("type","YDGZTBGBSYXALS");
//        params.put("deptName", deptName);
//        int totalNumber = zsjbbDao.queryDepartTotalNumber(params);
//        String yearMonth = DateFormatUtil.format(new Date(), "yyyy-MM");
//        params.put("yearMonth", yearMonth);
//        int choseNumber = zsjbbDao.queryDepartChoseNumber(params);
//        // 超过最大评选数量要提示返回
//        if (choseNumber + applyIdList.size() > totalNumber) {
//            //这里result给false 前台不显示了
//            result.setMessage("最多只能评定"+totalNumber+"个优秀案例,请确认后重新评定！");
//            return result;
//        }
//        JSONObject param = new JSONObject();
//        param.put("applyIds", applyIdList);
//
//        param.put("ids", applyIdList);
//        param.put("isYxal", "yes");
//        param.put("yxalTime", new Date());
//
//        zsjbbDao.chooseYxal(param);
//        return result;
//    }

//    public JsonResult cancelYxal(String[] applyIdArr) {
//        List<String> applyIdList = Arrays.asList(applyIdArr);
//        JsonResult result = new JsonResult(true, "操作成功！");
//        if (applyIdArr.length == 0) {
//            return result;
//        }
//        JSONObject param = new JSONObject();
//        param.put("applyIds", applyIdList);
//
//        param.put("ids", applyIdList);
//        param.put("isYxal", "no");
//        //用空字符串 还能看出来修改记录
//        param.put("yxalTime", "");
//        zsjbbDao.cancelYxal(param);
//        return result;
//    }


    public void exportExecl(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = queryApplyList(request, false);
        List<Map<String, Object>> listData = result.getData();
        for (Map<String, Object> oneData : listData) {
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
//            if (oneData.get("isYxal") != null && StringUtils.isNotBlank(oneData.get("isYxal").toString())) {
//                String isYxal = oneData.get("isYxal").toString();
//                switch (isYxal) {
//                    case "yes":
//                        isYxal = "是";
//                        break;
//
//                    case "no":
//                        isYxal = "否";
//                        break;
//                }
//                oneData.put("isYxal", isYxal);
//            }
//            if (oneData.get("yxalTime") != null && StringUtils.isNotBlank(oneData.get("yxalTime").toString())) {
//                String yxalTime = oneData.get("yxalTime").toString();
//                if (yxalTime.length() > 10) {
//                    oneData.put("yxalTime", yxalTime.substring(0,10));
//                }
//            }

            }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "主数据属性治理月度工作";
        String excelName = nowDate + title;
        String[] fieldNames = {"申请人", "申请部门", "年月",
                "当前任务", "当前处理人", "状态"};
        String[] fieldCodes = {"creatorName", "departName", "yearMonth",
                "taskName", "allTaskUserNames", "status"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }


}
