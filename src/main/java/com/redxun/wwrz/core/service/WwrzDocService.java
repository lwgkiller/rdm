package com.redxun.wwrz.core.service;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.wwrz.core.dao.WwrzDocDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author zhangzhen
 */
@Service
public class WwrzDocService {
    private static final Logger logger = LoggerFactory.getLogger(WwrzDocService.class);
    @Resource
    WwrzDocDao wwrzDocDao;
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
            list = wwrzDocDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            totalList = wwrzDocDao.query(params);
            convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
        }
        return result;
    }
    public JSONObject saveOrUpdateItem(String changeGridDataStr) {
        JSONObject resultJson = new JSONObject();
        StringBuffer resultStr = new StringBuffer();
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
                    oneObject.put("deptId",ContextUtil.getCurrentUser().getMainGroupId());
                    //根据年份和机型，判断是否已经存在
                    JSONObject objJson = wwrzDocDao.getObjByInfo(oneObject);
                    if(objJson!=null){
                        resultStr.append(oneObject.getString("docName")).append(",");
                        continue;
                    }
                    wwrzDocDao.addItem(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    JSONObject objJson = wwrzDocDao.getObjByInfo(oneObject);
                    if(objJson!=null&&!objJson.getString("id").equals(oneObject.getString("id"))){
                        resultStr.append(oneObject.getString("docName")).append(",");
                        continue;
                    }
                    wwrzDocDao.updateItem(oneObject);
                }else if ("removed".equals(state)) {
                    // 删除
                    wwrzDocDao.delItem(oneObject.getString("id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("保存信息异常",e);
            return ResultUtil.result(false,"保存信息异常！",null);
        }
        if(StringUtil.isNotEmpty(resultStr.toString())){
            return ResultUtil.result(false,resultStr.toString()+"文件已存在！",resultStr.toString());
        }
        return ResultUtil.result(true,"保存成功！",null);
    }
    public List<JSONObject> getDocListByType(HttpServletRequest request){
        List<JSONObject> list = new ArrayList<>();
        try {
            String docType = request.getParameter("docType");
            list = wwrzDocDao.getDocListByType(docType);
        }catch (Exception e){
            logger.error("保存信息异常",e);
        }
        return list;
    }
    /**
     * 模板下载
     *
     * @return
     */
    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "认证资料导入模板.xlsx";
            // 创建文件实例
            File file = new File(
                    MaterielService.class.getClassLoader().getResource("templates/wwrz/" + fileName).toURI());
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
    public void importDocument(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            MultipartFile fileObj = multipartRequest.getFile("documentImportFile");
            String fileName = ((CommonsMultipartFile)fileObj).getFileItem().getName();
            ((CommonsMultipartFile)fileObj).getFileItem().getName().endsWith(ExcelReaderUtil.EXCEL03_EXTENSION);
            if (fileObj == null) {
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            Workbook wb = null;
            if (fileName.endsWith(ExcelReaderUtil.EXCEL03_EXTENSION)) {
                wb = new HSSFWorkbook(fileObj.getInputStream());
            } else if (fileName.endsWith(ExcelReaderUtil.EXCEL07_EXTENSION)) {
                wb = new XSSFWorkbook(fileObj.getInputStream());
            }
            Sheet sheet = wb.getSheet("认证资料明细");
            if (sheet == null) {
                logger.error("找不到认证资料明细导入页");
                result.put("message", "数据导入失败，找不到认证资料明细导入页！");
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
            Map<String, String> userCertNo2UserId = new HashMap<>(16);
            commonInfoManager.getUserCert2Id(userCertNo2UserId);
            Map<String, Object> params = new HashMap<>(16);
            Map<String, Object> type2Id = new HashMap<>(16);
            params = new HashMap<>(16);
            params.put("key", "RZZLLX");
            CommonFuns.getCategoryName2Id(type2Id, commonInfoDao.getDicValues(params));
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> dataInsert = new ArrayList<>();
            for (int i = 1; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse =
                        generateDataFromRow(row, dataInsert, titleList,userCertNo2UserId,type2Id);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            // 分批写入数据库(20条一次)
            List<Map<String, Object>> tempInsert = new ArrayList<>();
            for (int i = 0; i < dataInsert.size(); i++) {
                JSONObject obj = wwrzDocDao.getObjByMapInfo(dataInsert.get(i));
                if(obj==null){
                    wwrzDocDao.addItem(dataInsert.get(i));
                }
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importDocument", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }
    private JSONObject generateDataFromRow(Row row, List<Map<String, Object>> dataInsert, List<String> titleList,
                                           Map<String, String> userNo2UserId,Map<String, Object> type2Id) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>(16);
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = ExcelUtil.getCellFormatValue(cell);
            }
            switch (title) {
                case "分类":
                    if (StringUtils.isNotBlank(cellValue) && !type2Id.containsKey(cellValue)) {
                        oneRowCheck.put("message", "分类不存在,请先维护！");
                        return oneRowCheck;
                    }
                    oneRowMap.put("docType", cellValue);
                    break;
                case "资料":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "资料为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("docName", cellValue);
                    break;
                case "备注":
                    oneRowMap.put("remark", cellValue);
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        oneRowMap.put("id", IdUtil.getId());
        dataInsert.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }
    public static void convertDate(List<Map<String, Object>> list) {
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> oneApply : list) {
                for (String key : oneApply.keySet()) {
                    if (key.endsWith("_TIME_") || key.endsWith("_time")) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd  HH:mm:ss"));
                        }
                    }
                }
            }
        }
    }

}
