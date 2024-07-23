package com.redxun.serviceEngineering.core.service;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.JxxqxfDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Service
public class JxxqxfService {

    private static Logger logger = LoggerFactory.getLogger(JxxqxfService.class);
    @Autowired
    private JxxqxfDao jxxqxfDao;

    @Autowired
    private CommonInfoDao commonInfoDao;

    // ..
    public JsonPageResult<?> jxxqxfListQuery(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
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
        String passBacks = (String)params.get("passBacks");
        if (StringUtils.isNotEmpty(passBacks)) {
            String[] strs = passBacks.split(",");
            params.put("passBack", strs);
        }
        List<Map<String, Object>> list = jxxqxfDao.jxxqxfListQuery(params);
        Map<String, Object> map = new HashMap<>();
        // 组数回传率
        String zshcl = jxxqxfDao.queryZshcl() + "%";
        int total = jxxqxfDao.jxxqxfCountQuery(map);
        int notHcwcCount = jxxqxfDao.queryNotHcwcCount();
        // 机型回传率
        String jxhcl = "0";
        if (total != 0) {
            double res = (1.0 - (double)notHcwcCount / total) * 100;
            jxhcl = new DecimalFormat("#.0").format(res) + "%";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Map<String, Object> jxxqxf : list) {
            if (jxxqxf.get("CREATE_TIME_") != null) {
                jxxqxf.put("CREATE_TIME_", sdf.format(jxxqxf.get("CREATE_TIME_")));
                jxxqxf.put("jxhcl", jxhcl);
                jxxqxf.put("zshcl", zshcl);
            }
        }
        result.setData(list);
        int count = jxxqxfDao.jxxqxfCountQuery(params);
        result.setTotal(count);
        return result;
    }

    // ..
    public JsonResult deleteJxxqxf(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("ids", businessIds);
        jxxqxfDao.deleteJxxqxf(param);
        return result;
    }

    public JSONObject getJxxqxfDetail(String jxxqxfId) {
        JSONObject detailObj = jxxqxfDao.queryJxxqxfById(jxxqxfId);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    public void createJxxqxf(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        jxxqxfDao.insertJxxqxf(formData);
    }

    public void updateJxxqxf(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        jxxqxfDao.updateJxxqxf(formData);
    }

    public ResponseEntity<byte[]> downloadTemplate() {
        try {
            String fileName = "机型需求下发导入模板.xlsx";
            // 创建文件实例
            File file = new File(
                MaterielService.class.getClassLoader().getResource("templates/serviceEngineering/" + fileName).toURI());
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

    public void importJxxqxfExcel(JSONObject result, HttpServletRequest request) {
        try {
            String issueDepartmentId = RequestUtil.getString(request, "issueDepartmentId");
            String issueDepartment = RequestUtil.getString(request, "issueDepartment");
            String versionType = RequestUtil.getString(request, "versionType");
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
            if (rowNum < 1) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }

            // 解析标题部分
            Row titleRow = sheet.getRow(0);
            if (titleRow == null) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }
            List<String> titleList = new ArrayList<>();
            for (int i = 0; i < titleRow.getLastCellNum(); i++) {
                titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
            }

            if (rowNum < 2) {
                logger.info("数据行为空");
                result.put("message", "数据导入完成，数据行为空！");
                return;
            }
            Map<String, Object> priorityList = new HashMap<>(16);
            Map params = new HashMap<>(16);
            params.put("key", "ZYD");
            CommonFuns.getCategoryName2Id(priorityList, commonInfoDao.getDicValues(params));
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> itemList = new ArrayList<>();
            for (int i = 1; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRow(row, itemList, titleList, issueDepartmentId, issueDepartment,
                    versionType, priorityList);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            for (int i = 0; i < itemList.size(); i++) {
                jxxqxfDao.insertJxxqxf(itemList.get(i));
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in import", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    private JSONObject generateDataFromRow(Row row, List<Map<String, Object>> itemList, List<String> titleList,
        String issueDepartmentId, String issueDepartment, String versionType, Map<String, Object> priorityList) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>(16);
        DataFormatter dataFormatter = new DataFormatter();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            String cellValue = dataFormatter.formatCellValue(cell);
            switch (title) {
                case "产品所":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "产品所为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("productDepartment", cellValue);
                    JSONObject paramJson = new JSONObject();
                    paramJson.put("deptName", cellValue);
                    paramJson.put("DimId", "1");
                    JSONObject groupJson = jxxqxfDao.getGroupById(paramJson);
                    if (groupJson == null) {
                        oneRowCheck.put("message", "系统中不存在此部门");
                        return oneRowCheck;
                    } else {
                        oneRowMap.put("productDepartmentId", groupJson.getString("groupId"));
                    }
                    break;
                case "产品类型":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "产品类型为空");
                        return oneRowCheck;
                    }
                    String productType = "";
                    if (cellValue.equals("轮挖")) {
                        productType = "lunWa";
                    } else if (cellValue.equals("履挖")) {
                        productType = "lvWa";
                    } else if (cellValue.equals("电挖")) {
                        productType = "dianWa";
                    }else if (cellValue.equals("特挖")) {
                        productType = "teWa";
                    }
                    else {
                        oneRowCheck.put("message", "产品类型不存在");
                        return oneRowCheck;
                    }
                    oneRowMap.put("productType", productType);
                    break;
                case "物料编码":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料编码为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialCode", cellValue);
                    break;
                case "销售型号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "销售型号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("salesModel", cellValue);
                    break;
                case "设计型号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "设计型号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("designModel", cellValue);
                    break;
                case "第四位PIN码":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "第四位PIN码为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("pinCode", cellValue);
                    break;
                case "是否回传":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "是否回传为空或回传类型不存在");
                        return oneRowCheck;
                    }
                    String passBack = "";
                    if (cellValue.equals("回传完成")) {
                        passBack = "hcwc";
                    } else if (cellValue.equals("未回传")) {
                        passBack = "whc";
                    } else if (cellValue.equals("回传中")) {
                        passBack = "hcz";
                    } else {
                        oneRowCheck.put("message", "回传类型不存在");
                        return oneRowCheck;
                    }
                    oneRowMap.put("passBack", passBack);
                    break;
                case "回传组数":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "回传组数为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("passBackNum", cellValue);
                    break;
                case "备注":
                    oneRowMap.put("remark", cellValue);
                    break;
                case "优先级(☆越多越优先)":
                    if (StringUtils.isBlank(cellValue) || !priorityList.containsKey(cellValue)) {
                        oneRowCheck.put("message", "优先级为空或优先级不存在");
                        return oneRowCheck;
                    }
                    oneRowMap.put("priority", priorityList.get(cellValue));
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("id", IdUtil.getId());
        oneRowMap.put("issueDepartmentId", issueDepartmentId);
        oneRowMap.put("issueDepartment", issueDepartment);
        oneRowMap.put("versionType", versionType);
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    public void exportJxxqxf(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> params = new HashMap<>();
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
        String passBacks = (String)params.get("passBacks");
        if (StringUtils.isNotEmpty(passBacks)) {
            String[] strs = passBacks.split(",");
            params.put("passBack", strs);
        }
        List<Map<String, Object>> list = jxxqxfDao.jxxqxfListQuery(params);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Map<String, Object> jxxqxf : list) {
            String versionType = (String)jxxqxf.get("versionType");
            String productType = (String)jxxqxf.get("productType");
            String priority = (String)jxxqxf.get("priority");
            String passBack = (String)jxxqxf.get("passBack");
            if (versionType != null) {
                if (versionType.equals("cgb")) {
                    versionType = "常规版";
                } else {
                    versionType = "测试版";
                }
                jxxqxf.put("versionType", versionType);
            }
            if (productType != null) {
                if (productType.equals("lunWa")) {
                    productType = "轮挖";
                } else if (productType.equals("teWa")) {
                    productType = "特挖";
                } else if (productType.equals("dianWa")) {
                    productType = "电挖";
                } else {
                    productType = "履挖";
                }
                jxxqxf.put("productType", productType);
            }
            if (priority != null) {
                if (priority.equals("1")) {
                    priority = "☆";
                } else if (priority.equals("2")) {
                    priority = "☆☆";
                } else {
                    priority = "☆☆☆";
                }
                jxxqxf.put("priority", priority);
            }
            if (passBack != null) {
                if (passBack.equals("whc")) {
                    passBack = "未回传";
                } else if (passBack.equals("hcz")) {
                    passBack = "回传中";
                } else {
                    passBack = "回传完成";
                }
                jxxqxf.put("passBack", passBack);
            }
            if (jxxqxf.get("CREATE_TIME_") != null) {
                jxxqxf.put("CREATE_TIME_", sdf.format(jxxqxf.get("CREATE_TIME_")));
            }
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "需求下发列表";
        String excelName = nowDate + title;
        String[] fieldNames =
            {"下发部门", "产品所", "版本类型", "产品类型", "物料编码", "销售型号", "设计型号", "第四位PIN码", "优先级", "是否回传", "回传组数", "创建时间", "创建者"};
        String[] fieldCodes = {"issueDepartment", "productDepartment", "versionType", "productType", "materialCode",
            "salesModel", "designModel", "pinCode", "priority", "passBack", "passBackNum", "CREATE_TIME_", "creator"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(list, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
