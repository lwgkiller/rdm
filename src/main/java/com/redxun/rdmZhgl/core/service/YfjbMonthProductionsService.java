package com.redxun.rdmZhgl.core.service;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.redxun.rdmCommon.core.manager.CommonFuns;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.ExcelUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.YfjbMonthProductionsDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * @author zhangzhen
 */
@Service
public class YfjbMonthProductionsService {
    private static final Logger logger = LoggerFactory.getLogger(YfjbMonthProductionsService.class);
    @Resource
    CommonInfoManager commonInfoManager;
    @Resource
    private YfjbMonthProductionsDao yfjbMonthProductionsDao;

    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            JSONObject yfjbAdmin = commonInfoManager.hasPermission("YFJB-GLY");
            JSONObject YFJB_FGZR = commonInfoManager.hasPermission("YFJB-FGZR");
            JSONObject JBZY = commonInfoManager.hasPermission("JBZY");
            if (yfjbAdmin.getBoolean("YFJB-GLY") || yfjbAdmin.getBoolean(RdmConst.ALLDATA_QUERY_KEY) ||YFJB_FGZR.getBoolean("YFJB-FGZR")
                || "admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
            } else if(JBZY.getBoolean("JBZY")) {
                params.put("deptId", ContextUtil.getCurrentUser().getMainGroupId());
            }else{
                result.setData(list);
                result.setTotal(0);
                return result;
            }
            list = yfjbMonthProductionsDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            if (yfjbAdmin.getBoolean("YFJB-GLY") || yfjbAdmin.getBoolean(RdmConst.ALLDATA_QUERY_KEY) ||YFJB_FGZR.getBoolean("YFJB-FGZR")
                    || "admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
            } else if(JBZY.getBoolean("JBZY")) {
                params.put("deptId", ContextUtil.getCurrentUser().getMainGroupId());
            }
            totalList = yfjbMonthProductionsDao.query(params);
            CommonFuns.convertDate(list);
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
                objBody.put(mapKey, mapValue);
            }
            // 判断是否已经存在
            JSONObject obj = yfjbMonthProductionsDao.getObjByInfo(objBody);
            if (obj != null) {
                return ResultUtil.result(false, "本年度已存在此型号的产量数据！", "");
            }
            String id = IdUtil.getId();
            objBody.put("id", id);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("deptId", ContextUtil.getCurrentUser().getMainGroupId());
            resultJson.put("id", id);
            yfjbMonthProductionsDao.addObject(objBody);
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
            String id = request.getParameter("id");
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                objBody.put(mapKey, mapValue);
            }
            JSONObject obj = yfjbMonthProductionsDao.getObjByInfo(objBody);
            if (obj != null && !id.equals(obj.getString("id"))) {
                return ResultUtil.result(false, "本年度已存在此型号的产量数据！", "");
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            yfjbMonthProductionsDao.updateObject(objBody);
        } catch (Exception e) {
            logger.error("Exception in update 更新异常", e);
            return ResultUtil.result(false, "更新异常！", "");
        }
        return ResultUtil.result(true, "更新成功", resultJson);
    }

    public JSONObject remove(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String ids = request.getParameter("ids");
            String[] idArr = ids.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            params.put("ids", idList);
            yfjbMonthProductionsDao.batchDelete(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除信息异常", e);
            return ResultUtil.result(false, "删除信息异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }

    public JSONObject getObjectById(String id) {
        JSONObject jsonObject = yfjbMonthProductionsDao.getObjectById(id);
        return jsonObject;
    }

    /**
     * 模板下载
     *
     * @return
     */
    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "月度产量导入模板.xlsx";
            // 创建文件实例
            File file =
                new File(MaterielService.class.getClassLoader().getResource("templates/zhgl/" + fileName).toURI());
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

    public void importProduct(JSONObject result, HttpServletRequest request) {
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
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> itemList = new ArrayList<>();
            String reportYear = request.getParameter("reportYear");
            for (int i = 1; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRow(row, itemList, titleList, reportYear);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            for (int i = 0; i < itemList.size(); i++) {
                yfjbMonthProductionsDao.addObject(itemList.get(i));
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importProduct", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    private JSONObject generateDataFromRow(Row row, List<Map<String, Object>> itemList, List<String> titleList,
        String productYear) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>(16);
        Map<String, Object> itemRowMap = new HashMap<>(16);
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = ExcelUtil.getCellFormatValue(cell);
            }
            switch (title) {
                case "整机型号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "整机型号为空");
                        return oneRowCheck;
                    }
                    // 判断是否已经存在
                    Map<String, Object> objBody = new HashMap<>(16);
                    objBody.put("productYear", productYear);
                    objBody.put("productModel", cellValue);
                    JSONObject obj = yfjbMonthProductionsDao.getObjByInfo(objBody);
                    if (obj != null) {
                        oneRowCheck.put("message", cellValue + "整机型号本年度已存在");
                        return oneRowCheck;
                    }
                    oneRowMap.put("productModel", cellValue);
                    break;
                case "一月计划产量":
                    oneRowMap.put("january", cellValue);
                    break;
                case "二月计划产量":
                    oneRowMap.put("february", cellValue);
                    break;
                case "三月计划产量":
                    oneRowMap.put("march", cellValue);
                    break;
                case "四月计划产量":
                    oneRowMap.put("april", cellValue);
                    break;
                case "五月计划产量":
                    oneRowMap.put("may", cellValue);
                    break;
                case "六月计划产量":
                    oneRowMap.put("june", cellValue);
                    break;
                case "七月计划产量":
                    oneRowMap.put("july", cellValue);
                    break;
                case "八月计划产量":
                    oneRowMap.put("august", cellValue);
                    break;
                case "九月计划产量":
                    oneRowMap.put("september", cellValue);
                    break;
                case "十月计划产量":
                    oneRowMap.put("october", cellValue);
                    break;
                case "十一月计划产量":
                    oneRowMap.put("november", cellValue);
                    break;
                case "十二月计划产量":
                    oneRowMap.put("december", cellValue);
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("id", IdUtil.getId());
        oneRowMap.put("productYear", productYear);
        oneRowMap.put("deptId", ContextUtil.getCurrentUser().getMainGroupId());
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
