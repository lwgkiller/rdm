package com.redxun.rdmZhgl.core.service;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.GzxmProjectInfoDao;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
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
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * @author zhangzhen
 */
@Service
public class GzxmProjectInfoService {
    private static final Logger logger = LoggerFactory.getLogger(GzxmProjectInfoService.class);
    @Resource
    GzxmProjectInfoDao gzxmProjectInfoDao;
    @Resource
    CommonInfoManager commonInfoManager;
    @Resource
    CommonInfoDao commonInfoDao;

    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            JSONObject resultJson = commonInfoManager.hasPermission("GZXM-GLY");
            JSONObject managerJson = commonInfoManager.hasPermission("GZXM-GLZ");
            if(resultJson.getBoolean("GZXM-GLY")||resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)||managerJson.getBoolean("GZXM-GLZ")||"admin".equals(ContextUtil.getCurrentUser().getUserNo())){
            }else{
                params.put("userId", ContextUtil.getCurrentUserId());
            }
            list = gzxmProjectInfoDao.query(params);
            params = new HashMap<>(16);
            if(resultJson.getBoolean("GZXM-GLY")||resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)||managerJson.getBoolean("GZXM-GLZ")||"admin".equals(ContextUtil.getCurrentUser().getUserNo())){
            }else{
                params.put("userId", ContextUtil.getCurrentUserId());
            }
            params = CommonFuns.getSearchParam(params, request, false);
            totalList = gzxmProjectInfoDao.query(params);
            convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
        }
        return result;
    }
    public JSONObject add(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            if (parameters == null || parameters.isEmpty()) {
                logger.error("表单内容为空！");
                return ResultUtil.result(false, "操作失败，表单内容为空！", "");
            }
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                if (mapKey.equals("startDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                if (mapKey.equals("endDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            String id = IdUtil.getId();
            objBody.put("id", id);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("deptId", ContextUtil.getCurrentUser().getMainGroupId());
            resultJson.put("id",id);
            gzxmProjectInfoDao.addObject(objBody);
        } catch (Exception e) {
            logger.error("Exception in add 添加异常！", e);
            return ResultUtil.result(false, "添加异常！", "");
        }
        return ResultUtil.result(true, "新增成功", resultJson);
    }
    public JSONObject update(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                if (mapKey.equals("startDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                if (mapKey.equals("endDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            gzxmProjectInfoDao.updateObject(objBody);
            resultJson.put("id",objBody.get("id"));
        } catch (Exception e) {
            logger.error("Exception in update 更新异常", e);
            return ResultUtil.result(false, "更新异常！", "");
        }
        return ResultUtil.result(true, "更新成功", resultJson);
    }
    public JSONObject saveOrUpdateItem(String changeGridDataStr) {
        JSONObject resultJson = new JSONObject();
        try {
            JSONArray changeGridDataJson = JSONObject.parseArray(changeGridDataStr);
            for(int i=0;i<changeGridDataJson.size();i++) {
                JSONObject oneObject=changeGridDataJson.getJSONObject(i);
                String state=oneObject.getString("_state");
                String id=oneObject.getString("id");
                if("added".equals(state)|| StringUtils.isBlank(id)) {
                    //新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    //根据负责人id 获取对应部门
                    gzxmProjectInfoDao.addItem(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    gzxmProjectInfoDao.updateItem(oneObject);
                }else if ("removed".equals(state)) {
                    // 删除
                    gzxmProjectInfoDao.delItem(oneObject.getString("id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("保存信息异常",e);
            return ResultUtil.result(false, "处理信息异常！", "");
        }
        return ResultUtil.result(true, "保存成功！", "");
    }
    public JSONObject remove(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String ids = request.getParameter("ids");
            String[] idArr = ids.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            params.put("ids", idList);
            gzxmProjectInfoDao.batchDelete(params);
            gzxmProjectInfoDao.batchDeleteItems(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除国重项目", e);
            return ResultUtil.result(false, "删除国重项目异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }
    public JSONObject getObjectById(String id){
        JSONObject jsonObject =  gzxmProjectInfoDao.getObjectById(id);
        return jsonObject;
    }
    public List<JSONObject> getItemList(HttpServletRequest request) {
        List<JSONObject>  resultArray = new ArrayList<>();
        try {
            String mainId = request.getParameter("mainId");
            JSONObject paramJson = new JSONObject();
            paramJson.put("mainId",mainId);
            resultArray = gzxmProjectInfoDao.getItemList(paramJson);
            StringBuffer finishProcessName = new StringBuffer();
            for(int i=0;i<resultArray.size();i++){
                if(i==0){
                    String currentStageText = resultArray.get(i).getString("currentStageText");
                    String processDetail = resultArray.get(i).getString("processDetail");
                    finishProcessName.append("当前进度：<br>").append(CommonFuns.nullToString(currentStageText)).append("<br>");
                    finishProcessName.append("详述：<br>").append(CommonFuns.nullToString(processDetail));
                }
                resultArray.get(i).put("finishProcessName",finishProcessName);
            }
            List<String> timeNames = new ArrayList<>();
            timeNames.add("planStartDate");
            timeNames.add("planEndDate");
            timeNames.add("actStartDate");
            timeNames.add("actEndDate");
            CommonFuns.convertJsonListDate(resultArray,timeNames,"yyyy-MM-dd");
            Collections.sort(resultArray, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject o1, JSONObject o2) {
                    String str1 = CommonFuns.nullToString(o1.getString("indexSort"));
                    String str2 = CommonFuns.nullToString(o2.getString("indexSort"));
                    if(str1.isEmpty()||str2.isEmpty()){
                        return 1;
                    }
                    String[] str1Split = str1.split("\\.");
                    String[] str2Split = str2.split("\\.");
                    int minSplitLength = str1Split.length > str2Split.length
                            ? str2Split.length : str1Split.length;
                    // 用长度小的数组长度作为遍历边界，防止数组越界
                    for (int i = 0; i < minSplitLength; i++) {
                        // 将字符串转换成数字解决 一位数字(eg: 2) 和 两位数字(eg: 10) 的大小比较问题
                        Integer strToInt1 = Integer.valueOf(str1Split[i]);
                        Integer strToInt2 = Integer.valueOf(str2Split[i]);
                        int compareResult = strToInt1.compareTo(strToInt2);
                        if (compareResult == 0) {
                            continue;
                        } else if (compareResult > 0) {
                            return 1;
                        } else if (compareResult < 0) {
                            return -1;
                        }
                    }
                    // 若程序进行到这里，说明在循环里没有得出比较结果。
                    // 此时应该是数组长度长的字符串（1.10.1）排在后面，数组长度短的字符串（1.10）排在前面
                    if (minSplitLength == str1Split.length) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });
        } catch (Exception e) {
            logger.error("Exception in getItemList", e);
        }
        return resultArray;
    }
    public List<JSONObject> getGzxmList(HttpServletRequest request) {
        List<JSONObject>  resultArray = new ArrayList<>();
        try {
            JSONObject paramJson = new JSONObject();
            resultArray = gzxmProjectInfoDao.getGzxmList(paramJson);
            CommonFuns.convertDateJSON(resultArray);
        } catch (Exception e) {
            logger.error("Exception in getGzxmList", e);
        }
        return resultArray;
    }
    public static void convertDate(List<Map<String, Object>> list) {
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> oneApply : list) {
                for (String key : oneApply.keySet()) {
                    if (key.endsWith("_TIME_") || key.endsWith("_time") || key.endsWith("_date")) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd  HH:mm:ss"));
                        }
                    }
                    if ("startDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                    if ("endDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                }
            }
        }
    }

    /**
     * 模板下载
     *
     * @return
     */
    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "国重项目任务导入模板.xlsx";
            // 创建文件实例
            File file = new File(
                    MaterielService.class.getClassLoader().getResource("templates/zhgl/" + fileName).toURI());
            String finalDownloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");

            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 finalDownloadFileName
            headers.setContentDispositionFormData("attachment", finalDownloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in importTemplateDownload", e);
            return null;
        }
    }

    public void importGtzz(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            MultipartFile fileObj = multipartRequest.getFile("importFile");
            if (fileObj == null) {
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            String fileName = ((CommonsMultipartFile) fileObj).getFileItem().getName();
            ((CommonsMultipartFile) fileObj).getFileItem().getName().endsWith(ExcelReaderUtil.EXCEL03_EXTENSION);
            Workbook wb = null;
            if(fileName.endsWith(ExcelReaderUtil.EXCEL03_EXTENSION)){
                wb = new HSSFWorkbook(fileObj.getInputStream());
            }else if(fileName.endsWith(ExcelReaderUtil.EXCEL07_EXTENSION)){
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
            for (int i = 2; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRow(row,itemList, titleList);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            //保存主表数据
            String projectName = sheet.getRow(0).getCell(0).getStringCellValue();
            List<JSONObject> projectList = gzxmProjectInfoDao.getProjectInfoByName(projectName);
            String mainId = "";
            if(projectList!=null){
                if(projectList.size()==0){
                    result.put("message", "未找到项目信息！");
                    return;
                }else if(projectList.size()>1){
                    result.put("message", "匹配到多条项目！");
                    return;
                }else{
                    mainId = projectList.get(0).getString("id");
                }
            }
            if(StringUtil.isEmpty(mainId)){
                result.put("message", "未找到项目信息！");
                return;
            }
            for (int i = 0; i < itemList.size(); i++) {
                itemList.get(i).put("mainId",mainId);
                gzxmProjectInfoDao.addItem(itemList.get(i));
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importProduct", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    private JSONObject generateDataFromRow(Row row, List<Map<String, Object>> itemList, List<String> titleList) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>(16);
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ","");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = ExcelUtil.getCellFormatValue(cell);
            }
            switch (title) {
                case "编号":
                    oneRowMap.put("indexSort", cellValue);
                    break;
                case "重要度":
                    if (!StringUtils.isBlank(cellValue)) {
                        if("重要".equals(cellValue)){
                            oneRowMap.put("important", "0");
                        }else if("一般".equals(cellValue)){
                            oneRowMap.put("important", "1");
                        }
                    }
                    break;
                case "任务名称":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "项目名称为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("taskName", cellValue);
                    break;
                case "任务目标":
                    oneRowMap.put("taskTarget", cellValue);
                    break;
                case "输出物":
                    oneRowMap.put("outputFile", cellValue);
                    break;
                case "责任单位":
                    oneRowMap.put("resDeptIds", cellValue);
                    break;
                case "责任人":
                    oneRowMap.put("resUserIds", cellValue);
                    break;
                case "计划开始时间":
                    oneRowMap.put("planStartDate", cellValue);
                    break;
                case "计划结束时间":
                    oneRowMap.put("planEndDate", cellValue);
                    break;
                case "实际开始时间":
                    oneRowMap.put("actStartDate", cellValue);
                    break;
                case "实际结束时间":
                    oneRowMap.put("actEndDate", cellValue);
                    break;
                case "延期原因与补救措施":
                    oneRowMap.put("reason", cellValue);
                    break;
                case "备注":
                    oneRowMap.put("remark", cellValue);
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("id",IdUtil.getId());
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }
    public List<JSONObject> getFileListByMainId(HttpServletRequest request) {
        String taskId = RequestUtil.getString(request, "taskId");
        String fileType = RequestUtil.getString(request, "fileType");
        JSONObject paramJson = new JSONObject();
        paramJson.put("taskId",taskId);
        paramJson.put("fileType",fileType);
        List<JSONObject> fileInfos = gzxmProjectInfoDao.getFileListByMainId(paramJson);
        CommonFuns.convertDateJSON(fileInfos, "yyyy-MM-dd");
        return fileInfos;
    }
    /**
     * 新增保存文件到磁盘和数据库
     *
     * @param request
     */
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
            String taskId = CommonFuns.toGetParamVal(parameters.get("taskId"));
            String id = IdUtil.getId();
            String fileName = CommonFuns.toGetParamVal(parameters.get("fileName"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String filePathBase = SysPropertiesUtil.getGlobalProperty("gzxmFileUrl");
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find gzxmFileUrl");
                return;
            }
            // 向下载目录中写入文件
            String filePath = filePathBase + (StringUtils.isNotBlank(taskId) ? (File.separator + taskId) : "");
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);

            // 写入数据库
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("id", id);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", CommonFuns.toGetParamVal(parameters.get("fileSize")));
            fileInfo.put("taskId", taskId);
            fileInfo.put("fileDesc", CommonFuns.toGetParamVal(parameters.get("fileDesc")));
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            fileInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("UPDATE_TIME_", new Date());
            fileInfo.put("fileType", CommonFuns.toGetParamVal(parameters.get("fileType")));
            gzxmProjectInfoDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
        return;
    }
    public void deleteFileOnDisk(String taskId, String fileId, String suffix,String fileUrl) {
        try {
            String fullFileName = fileId + "." + suffix;
            StringBuilder fileBasePath = new StringBuilder(SysPropertiesUtil.getGlobalProperty(fileUrl));
            String fullPath =
                    fileBasePath.append(File.separator).append(taskId).append(File.separator).append(fullFileName).toString();
            File file = new File(fullPath);
            if (file.exists()) {
                file.delete();
            }
            // 删除目录
            String filePathDir = SysPropertiesUtil.getGlobalProperty(fileUrl) + File.separator + taskId;
            File pathFile = new File(filePathDir);
            pathFile.delete();
        }catch (Exception e){
            logger.error("Exception in deleteDirFromDisk", e);
        }
    }
    public void exportProjectTask(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> params = new HashMap<>();
        // 传入条件(不包括分页)
        params = CommonFuns.getSearchParam(params, request, false);
        String projectId = CommonFuns.nullToString(params.get("mainId"));
        JSONObject projectObj = gzxmProjectInfoDao.getObjectById(projectId);
        String projectName = projectObj.getString("projectName");
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + projectName;
        List<JSONObject> list = getItemList(request);
        Map<String, Object> zydMap = commonInfoManager.genMap("ZDXMZYJD");
        for(Map<String,Object> map:list){
            if(map.get("important")!=null){
                map.put("importantText",zydMap.get(map.get("important")));
            }
        }
        String[] fieldNames = {"编号", "重要度", "任务名称", "任务目标", "输出物", "责任部门", "责任人",
                "计划开始时间", "计划结束时间", "实际开始时间", "实际结束时间", "延期原因与补救措施", "备注"};
        String[] fieldCodes = {"indexSort", "importantText", "taskName", "taskTarget", "outputFile", "resDeptIds", "resUserIds",
                "planStartDate", "planEndDate", "actStartDate", "actEndDate", "reason", "remark"};
        HSSFWorkbook wbObj = ExcelUtils.exportMonthWorkExcel(list, fieldNames, fieldCodes, projectName);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

}
