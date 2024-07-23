package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.environment.core.service.OilService;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.rdmZhgl.core.dao.RyydDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import groovy.util.logging.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

@Service
@Slf4j
public class RyydService {
    private Logger logger = LogManager.getLogger(RyydService.class);
    @Autowired
    private RyydDao ryydDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    public JsonPageResult<?> queryRyyd(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "CREATE_TIME_");
            params.put("sortOrder", "asc");
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
        params.put("currentUserId",ContextUtil.getCurrentUserId());
        //addRyydRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> RyydList = ryydDao.queryRyyd(params);
        for (JSONObject Ryyd : RyydList) {
            if (Ryyd.getDate("CREATE_TIME_") != null) {
                Ryyd.put("CREATE_TIME_", DateUtil.formatDate(Ryyd.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }

        }
        result.setData(RyydList);
        int countRyydDataList = ryydDao.countRyydList(params);
        result.setTotal(countRyydDataList);
        return result;
    }
    public JSONObject getRyydById(String zId) {
        JSONObject detailObj = ryydDao.queryRyydById(zId);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    public List<JSONObject> getRyydDetailList(HttpServletRequest request) {
        String belongId = RequestUtil.getString(request, "belongId", "");
        String gridType = RequestUtil.getString(request, "gridType", "");
        Map<String, Object> param = new HashMap<>();
        param.put("belongId", belongId);
        param.put("gridType", gridType);
        List<JSONObject> ryydDetailList = ryydDao.queryRyydDetail(param);
        for (Map<String, Object> qbgz : ryydDetailList) {
            if (qbgz.get("CREATE_TIME_") != null) {
                qbgz.put("CREATE_TIME_", DateUtil.formatDate((Date)qbgz.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return ryydDetailList;
    }

    public void createRyyd(JSONObject formData) {
        formData.put("zId", IdUtil.getId());
        formData.put("title",formData.getString("month").substring(0,7)+"技术中心人员异动表");
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        ryydDao.createRyyd(formData);
    }
    public void updateRyyd(JSONObject formData) {
        formData.put("title",formData.getString("month").substring(0,7)+"技术中心人员异动表");
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        if (StringUtils.isNotBlank(formData.getString("ryzj"))) {
            JSONArray reasonDataJson = JSONObject.parseArray(formData.getString("ryzj"));
            for (int i = 0; i < reasonDataJson.size(); i++) {
                JSONObject oneObject = reasonDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String rId = oneObject.getString("rId");
                if ("added".equals(state) || StringUtils.isBlank(rId)) {
                    //新增
                    oneObject.put("rId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("zId"));
                    oneObject.put("gridType", "ryzj");
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    ryydDao.createRyydDetail(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    ryydDao.updateRyydDetail(oneObject);
                }
            }
        }
        if (StringUtils.isNotBlank(formData.getString("ryjs"))) {
            JSONArray reasonDataJson = JSONObject.parseArray(formData.getString("ryjs"));
            for (int i = 0; i < reasonDataJson.size(); i++) {
                JSONObject oneObject = reasonDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String rId = oneObject.getString("rId");
                if ("added".equals(state) || StringUtils.isBlank(rId)) {
                    //新增
                    oneObject.put("rId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("zId"));
                    oneObject.put("gridType", "ryjs");
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    ryydDao.createRyydDetail(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    ryydDao.updateRyydDetail(oneObject);
                }
            }
        }
        if (StringUtils.isNotBlank(formData.getString("rylz"))) {
            JSONArray reasonDataJson = JSONObject.parseArray(formData.getString("rylz"));
            for (int i = 0; i < reasonDataJson.size(); i++) {
                JSONObject oneObject = reasonDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String rId = oneObject.getString("rId");
                if ("added".equals(state) || StringUtils.isBlank(rId)) {
                    //新增
                    oneObject.put("rId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("zId"));
                    oneObject.put("gridType", "rylz");
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    ryydDao.createRyydDetail(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    ryydDao.updateRyydDetail(oneObject);
                }
            }
        }
        ryydDao.updateRyyd(formData);
    }

    public JsonResult deleteRyydDetail(String[] RyydIdsArr) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        for (String rId : RyydIdsArr) {
            // 删除基本信息
            param.clear();
            param.put("rId", rId);
            ryydDao.deleteRyydDetail(param);
        }
        return result;
    }

    public JsonResult deleteRyyd(String zId) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        param.put("belongId",zId);
        ryydDao.deleteRyydDetail(param);
        param.clear();
        param.put("zId",zId);
        ryydDao.deleteRyyd(param);
        return result;
    }
    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "月度绩效导入模板.xlsx";
            // 创建文件实例
            File file = new File(
                    OilService.class.getClassLoader().getResource("templates/Ryyd/" + fileName).toURI());
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
    public void importRyyd(JSONObject result, HttpServletRequest request) {
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
            List<JSONObject> itemList = new ArrayList<>();
            for (int i = 1; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRow(row,itemList, titleList);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            //保存主表数据
            for (int i = 1; i < itemList.size()-2; i++) {
                ryydDao.createRyyd(itemList.get(i));
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importProduct", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    private JSONObject generateDataFromRow(Row row, List<JSONObject> itemList, List<String> titleList) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        JSONObject oneRowMap = new JSONObject();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ","");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = ExcelUtil.getCellFormatValue(cell);
            }
            switch (title) {
                case "序号":
                    break;
                case "部门":
                    oneRowMap.put("depId_name", cellValue);
                    break;
                case "姓名":
                    oneRowMap.put("personId_name", cellValue);
                    break;
                case "岗位":
                    oneRowMap.put("post", cellValue);
                    break;
                case "身份证号码":
                    oneRowMap.put("identity", cellValue);
                    break;
                case "项目考核系数":
                    oneRowMap.put("projectNum", cellValue);
                    break;
                case "部门考核系数":
                    oneRowMap.put("depNum", cellValue);
                    break;
                case "备注":
                    oneRowMap.put("note", cellValue);
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

}
