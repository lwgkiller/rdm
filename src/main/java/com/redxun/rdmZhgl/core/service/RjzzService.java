package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.util.RdmCommonUtil;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.RjzzDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Service
public class RjzzService {
    private Logger logger = LoggerFactory.getLogger(RjzzService.class);
    @Autowired
    private RjzzDao rjzzDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    public JsonPageResult<?> getRjzzList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "rjzzNum", "desc");
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    if ("CREATE_TIME_StartTime".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("CREATE_TIME_EndTime".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    if ("djTimeStartTime".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("djTimeEndTime".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    params.put(name, value);
                }
            }
        }
        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        // 增加角色过滤的条件
        RdmCommonUtil.addListAllQueryRoleExceptDraft(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<Map<String, Object>> rjzzList = rjzzDao.queryRjzzList(params);
        for (Map<String, Object> jsmm : rjzzList) {
            if (jsmm.get("wcTime") != null) {
                jsmm.put("wcTime", DateUtil.formatDate((Date) jsmm.get("wcTime"), "yyyy-MM-dd"));
            }
            if (jsmm.get("djTime") != null) {
                jsmm.put("djTime", DateUtil.formatDate((Date) jsmm.get("djTime"), "yyyy-MM-dd"));
            }
            if (jsmm.get("CREATE_TIME_") != null) {
                jsmm.put("CREATE_TIME_", DateUtil.formatDate((Date) jsmm.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(rjzzList);
        //过滤条件
//        rjzzList =filterList(rjzzList);
        result.setData(rjzzList);
        int countRjzzDataList = rjzzDao.countRjzzList(params);
        result.setTotal(countRjzzDataList);
        return result;
    }

    public JSONObject getRjzzDetail(String rjzzId) {
        JSONObject detailObj = rjzzDao.queryRjzzById(rjzzId);
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.get("wcTime") != null) {
            detailObj.put("wcTime", DateUtil.formatDate((Date) detailObj.get("wcTime"), "yyyy-MM-dd"));
        }
        if (detailObj.get("djTime") != null) {
            detailObj.put("djTime", DateUtil.formatDate((Date) detailObj.get("djTime"), "yyyy-MM-dd"));
        }
        return detailObj;
    }

    public void createRjzz(JSONObject formData) {
        formData.put("rjzzId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        if (StringUtils.isBlank(formData.getString("wcTime"))) {
            formData.put("wcTime", null);
        }
        if (StringUtils.isBlank(formData.getString("djTime"))) {
            formData.put("djTime", null);
        }
        formData.put("CREATE_TIME_", new Date());
        rjzzDao.insertRjzz(formData);
    }

    public void updateRjzz(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        if (StringUtils.isBlank(formData.getString("wcTime"))) {
            formData.put("wcTime", null);
        }
        if (StringUtils.isBlank(formData.getString("djTime"))) {
            formData.put("djTime", null);
        }
        formData.put("UPDATE_TIME_", new Date());
        rjzzDao.updateRjzz(formData);
    }

    public String toGetRjzzNum_Obsoleted() {
        String nowYearStart = DateFormatUtil.getNowByString("yyyy") + "-01-01";
        String nowYearStartUTC = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(nowYearStart), -8));
        Map<String, Object> param = new HashMap<>();
        param.put("applyTimeStart", nowYearStartUTC);
        JSONObject maxRjzz = rjzzDao.queryMaxRjzzNum_Obsoleted(param);
        int existNumber = 0;
        if (maxRjzz != null) {
            existNumber = Integer.parseInt(maxRjzz.getString("rjzzNum").split("-", -1)[2]);
        }
        int thisNumber = existNumber + 1;
        if (thisNumber < 10) {
            return "RZ-" + DateFormatUtil.getNowByString("yyyy") + "-0" + thisNumber;
        } else {
            return "RZ-" + DateFormatUtil.getNowByString("yyyy") + "-" + thisNumber;
        }
    }

    public String toGetRjzzNum() {
        String nowYear = DateFormatUtil.getNowByString("yyyy");
        Map<String, Object> param = new HashMap<>();
        param.put("rjzzNum", "-" + nowYear + "-");
        JSONObject maxRjzz = rjzzDao.queryMaxRjzzNum(param);
        int existNumber = 0;
        if (maxRjzz != null) {
            existNumber = Integer.parseInt(maxRjzz.getString("rjzzNum").split("-", -1)[2]);
        }
        int thisNumber = existNumber + 1;
        if (thisNumber < 10) {
            return "RZ-" + DateFormatUtil.getNowByString("yyyy") + "-0" + thisNumber;
        } else {
            return "RZ-" + DateFormatUtil.getNowByString("yyyy") + "-" + thisNumber;
        }
    }

    public List<JSONObject> getRjzzFileList(List<String> rjzzIdList) {
        List<JSONObject> rjzzFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("rjzzIds", rjzzIdList);
        rjzzFileList = rjzzDao.queryRjzzFileList(param);
        return rjzzFileList;
    }

    public void saveUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        String filePathBase = WebAppUtil.getProperty("rjzzFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find rjzzFilePathBase");
            return;
        }
        try {
            String rjzzId = toGetParamVal(parameters.get("rjzzId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fjlx = toGetParamVal(parameters.get("fjlx"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + rjzzId;
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
            fileInfo.put("rjzzId", rjzzId);
            fileInfo.put("fjlx", fjlx);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            rjzzDao.addRjzzFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public void deleteOneRjzzFile(String fileId, String fileName, String rjzzId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, rjzzId, WebAppUtil.getProperty("rjzzFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        rjzzDao.deleteRjzzFile(param);
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    private List<Map<String, Object>> filterList(List<Map<String, Object>> rjzzList) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (rjzzList == null || rjzzList.isEmpty()) {
            return result;
        }
        // 管理员查看所有的包括草稿数据
//        if ("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
//            return rjzzList;
//        }
        // 分管领导和专利工程师的查看权限等同于项目管理人员
        boolean showAll = false;
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUser().getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        for (Map<String, Object> oneRole : currentUserRoles) {
            if (oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-LEADER")
                    || oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-BELONG")) {
                if (RdmConst.AllDATA_QUERY_NAME.equalsIgnoreCase(oneRole.get("NAME_").toString())
                        || RdmConst.ZLGCS.equalsIgnoreCase(oneRole.get("NAME_").toString())) {
                    showAll = true;
                    break;
                }
            }
        }
        String currentUserId = ContextUtil.getCurrentUserId();
        for (Map<String, Object> oneProject : rjzzList) {
            if (showAll) {
                //分管领导查看非草稿状态的所有数据
                if (oneProject.get("status") != null && !"DRAFTED".equals(oneProject.get("status").toString())) {
                    result.add(oneProject);
                } else if (oneProject.get("status") == null) {
                    result.add(oneProject);
                } else {
                    if (oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneProject);
                    }
                }
            } else {
                // 自己是当前流程处理人
                if (oneProject.get("status") != null && !"DRAFTED".equals(oneProject.get("status").toString())) {
                    if (oneProject.get("currentProcessUserId") != null && oneProject.get("currentProcessUserId").toString().contains(currentUserId)) {
                        result.add(oneProject);
                    } else {
                        if (oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                            result.add(oneProject);
                        }
                    }
                } else {
                    //自己的查看所有
                    if (oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneProject);
                    }
                }
            }

        }
        return result;
    }

    public JsonResult deleteRjzz(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> rjzzIds = Arrays.asList(ids);
        List<JSONObject> files = getRjzzFileList(rjzzIds);
        String rjzzFilePathBase = WebAppUtil.getProperty("rjzzFilePathBase");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                    oneFile.getString("rjzzId"), rjzzFilePathBase);
        }
        for (String oneJsmmId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneJsmmId, rjzzFilePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("rjzzIds", rjzzIds);
        rjzzDao.deleteRjzzFile(param);
        rjzzDao.deleteRjzz(param);
        return result;
    }

    public void exportRjzzList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = getRjzzList(request, response, false);
        List<Map<String, Object>> listData = result.getData();
        for (Map<String, Object> oneMap : listData) {
            oneMap.put("statusName", convertStatusName(oneMap.get("status")));
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "软件著作列表";
        String excelName = nowDate + title;
        String[] fieldNames = {"软件著作编号", "软件名全称", "软件名简称", "软件版本号", "开发完成日期", "申请人", "申请人部门", "申请时间", "认定状态"};
        String[] fieldCodes = {"rjzzNum", "rjmqc", "rjmjc", "rjbbh", "wcTime", "applyUserName", "applyUserDeptName", "CREATE_TIME_", "statusName"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    public void exportRjzzDetail(HttpServletRequest request, HttpServletResponse response) {
//        JsonPageResult result = getRjzzList(request, response, false);
//        List<JSONObject> listData = result.getData();
//        List<JSONObject> detailData = new  ArrayList<>();
//        for (JSONObject oneMap : listData) {
//            String rjzzId = oneMap.getString("rjzzId");
//            JSONObject detailObj = rjzzDao.queryRjzzById(rjzzId);
//            if (detailObj == null) {
//                continue;
//            }
//            if(detailObj.get("wcTime")!=null){
//                detailObj.put("wcTime",DateUtil.formatDate((Date)detailObj.get("wcTime"), "yyyy-MM-dd"));
//            }
//            if(detailObj.get("djTime")!=null){
//                detailObj.put("djTime",DateUtil.formatDate((Date)detailObj.get("djTime"), "yyyy-MM-dd"));
//            }
//            detailData.add(oneMap);
//        }
//        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
//        String title = "软件著作明细表";
//        String excelName = nowDate + title;
//        String[] fieldNames = {"软件著作编号", "软件名全称", "软件名简称","软件版本号","开发完成日期", "申请人", "申请人部门", "申请时间", "认定状态"};
//        String[] fieldCodes = {"rjzzNum", "rjmqc", "rjmjc","rjbbh","wcTime", "applyUserName", "applyUserDeptName","CREATE_TIME_", "statusName"};
//        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(detailData, fieldNames, fieldCodes, title);
//        // 输出
//        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
        JsonPageResult result = getRjzzList(request, response, false);
        List<Map<String, Object>> listData = result.getData();
        for (Map<String, Object> oneMap : listData) {
            oneMap.put("statusName", convertStatusName(oneMap.get("status")));
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "软件著作列表";
        String excelName = nowDate + title;
        String[] fieldNames = {"软件著作编号", "软件名全称", "软件名简称", "软件版本号", "软件分类号", "软件作品说明", "发表状态", "首次发表时间/地点"
                , "著作权人", "联系人/联系方式", "软件功能", "技术特点", "硬件环境", "软件环境", "编程语言及版本", "软件代码行数", "应用产品", "软件开发者姓名"
                , "开发完成日期", "申请人", "申请人部门", "申请时间", "认定状态"};
        String[] fieldCodes = {"rjzzNum", "rjmqc", "rjmjc", "rjbbh", "rjflh", "zpsm", "fbzt", "ddsj", "zzqr", "lxfs", "rjgn",
                "jstd", "yjhj", "rjhj", "bjyy", "dmhs", "yycp", "rjkfNames", "wcTime", "applyUserName", "applyUserDeptName", "CREATE_TIME_", "statusName"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    private String convertStatusName(Object statusKey) {
        if (statusKey == null) {
            return "";
        }
        String statusKeyStr = statusKey.toString();
        switch (statusKeyStr) {
            case "DRAFTED":
                return "草稿";
            case "RUNNING":
                return "认定中";
            case "SUCCESS_END":
                return "通过认定";
            case "DISCARD_END":
                return "未通过认定";
        }
        return "";
    }
}
