package com.redxun.portrait.core.manager;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.redxun.core.util.StringUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.portrait.core.dao.PortraitDocumentDao;
import com.redxun.portrait.core.dao.PortraitIndexDao;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.sys.org.dao.OsUserDao;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * @author zhangzhen
 */
@Service
public class PortraitIndexManager {
    private static final Logger logger = LoggerFactory.getLogger(PortraitIndexManager.class);
    @Resource
    PortraitIndexDao portraitIndexDao;
    @Autowired
    OsUserDao osUserDao;
    @Resource
    CommonInfoDao commonInfoDao;
    @Resource
    CommonInfoManager commonInfoManager;
    @Resource
    PortraitDocumentDao portraitDocumentDao;
    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            List<String> idList = new ArrayList<>();
            //用户权限
            JSONObject resultJson = commonInfoManager.hasPermission("YGHXZGLY");
            if("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
                Map<String,String> deptMap=commonInfoManager.queryDeptUnderJSZX();
                idList.addAll(deptMap.keySet());
            }else{
                if(resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)||resultJson.getBoolean("HX-GLY")){
                    //分管领导看所有数据
                    Map<String,String> deptMap=commonInfoManager.queryDeptUnderJSZX();
                    idList.addAll(deptMap.keySet());
                }else if(resultJson.getBoolean("isLeader")||resultJson.getBoolean("YGHXZGLY")){
                    //部门领导看自己部门的
                    idList.add(ContextUtil.getCurrentUser().getMainGroupId());
                }else{
                    //普通员工看自己的
                    idList.add(ContextUtil.getCurrentUser().getMainGroupId());
                }
            }
            params.put("ids", idList);
            list = portraitIndexDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            params.put("ids", idList);
            totalList = portraitIndexDao.query(params);
            CommonFuns.convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
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
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            String id = IdUtil.getId();
            objBody.put("id", id);
            portraitIndexDao.addObject(objBody);
        } catch (Exception e) {
            logger.error("Exception in add 添加指标异常！", e);
            return ResultUtil.result(false, "添加指标异常！", "");
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
                objBody.put(mapKey, mapValue);
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            portraitIndexDao.updateObject(objBody);
        } catch (Exception e) {
            logger.error("Exception in update 更新指标异常", e);
            return ResultUtil.result(false, "更新指标异常！", "");
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
            portraitIndexDao.batchDelete(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除指标", e);
            return ResultUtil.result(false, "删除指标异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }
    public JSONObject getObjectById(String id){
        return portraitIndexDao.getObjectById(id);
    }

    public JSONArray getIndexDic(HttpServletRequest request, HttpServletResponse response){
        JSONObject paramJson = new JSONObject();
        paramJson.put("deptId",ContextUtil.getCurrentUser().getMainGroupId());
        return portraitIndexDao.getIndexDic(paramJson);
    }

    /**
     * 模板下载
     *
     * @return
     */
    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "三高一可指标导入模板.xls";
            // 创建文件实例
            File file = new File(
                    MaterielService.class.getClassLoader().getResource("templates/portrait/" + fileName).toURI());
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

    public void importIndex(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            MultipartFile fileObj = multipartRequest.getFile("indexImportFile");
            if (fileObj == null) {
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            HSSFWorkbook wb = new HSSFWorkbook(fileObj.getInputStream());
            HSSFSheet sheet = wb.getSheet("三高一可指标");
            if (sheet == null) {
                logger.error("找不到三高一可指标导入页");
                result.put("message", "数据导入失败，找不到三高一可指标导入页！");
                return;
            }
            int rowNum = sheet.getPhysicalNumberOfRows();
            if (rowNum < 1) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }

            // 解析标题部分
            HSSFRow titleRow = sheet.getRow(0);
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
            Map<String, Object> rate2Id = new HashMap<>(16);
            params = new HashMap<>(16);
            params.put("key", "indexRate");
            CommonFuns.getCategoryName2Id(rate2Id, commonInfoDao.getDicValues(params));
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> dataInsert = new ArrayList<>();
            for (int i = 1; i < rowNum; i++) {
                HSSFRow row = sheet.getRow(i);
                JSONObject rowParse =
                        generateDataFromRow(row, dataInsert, titleList,userCertNo2UserId,rate2Id);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            // 分批写入数据库(20条一次)
            List<Map<String, Object>> tempInsert = new ArrayList<>();
            for (int i = 0; i < dataInsert.size(); i++) {
                tempInsert.add(dataInsert.get(i));
                if (tempInsert.size() % 20 == 0) {
                    portraitIndexDao.batchInsertIndex(tempInsert);
                    tempInsert.clear();
                }
            }
            if (!tempInsert.isEmpty()) {
                portraitIndexDao.batchInsertIndex(tempInsert);
                tempInsert.clear();
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importIndex", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }
    private JSONObject generateDataFromRow(HSSFRow row, List<Map<String, Object>> dataInsert, List<String> titleList,
                                           Map<String, String> userNo2UserId,Map<String, Object> rate2Id) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>(16);
        DataFormatter dataFormatter = new DataFormatter();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            HSSFCell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = StringUtils.trim(dataFormatter.formatCellValue(cell));
            }
            switch (title) {
                case "所属年度":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "所属年度为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("year", cellValue);
                    break;
                case "适用部门":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "适用部门为空");
                        return oneRowCheck;
                    }
                    List<JSONObject> list = commonInfoDao.getDeptInfoByDeptName(cellValue);
                    if(list!=null && list.isEmpty()){
                        oneRowCheck.put("message", "找不到对应部门");
                        return oneRowCheck;
                    }else if(list!=null && list.size()>1){
                        oneRowCheck.put("message", "找到多个部门");
                        return oneRowCheck;
                    }else if(list!=null && list.size()==1 ){
                        oneRowMap.put("deptId", list.get(0).getString("GROUP_ID_"));
                    }else{
                        oneRowCheck.put("message", "查找部门有误");
                        return oneRowCheck;
                    }
                    break;
                case "指标名称":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "指标名称为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("indexName", cellValue);
                    break;
                case "指标值":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "指标值为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("indexValue", cellValue);
                    break;
                case "分值":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "分值为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("indexScore", cellValue);
                    break;
                case "得分标准":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "得分标准为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("scoreRule", cellValue);
                    break;
                case "频次":
                    if (StringUtils.isNotBlank(cellValue) && !rate2Id.containsKey(cellValue)) {
                        oneRowCheck.put("message", "频次不存在");
                        return oneRowCheck;
                    }
                    oneRowMap.put("indexRate", rate2Id.get(cellValue));
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        /**
         * 根据通报编号和人员id判断是否已经存在
         * */
        String deptId = CommonFuns.nullToString(oneRowMap.get("deptId"));
        String indexName = CommonFuns.nullToString(oneRowMap.get("indexName"));
        JSONObject paramJson = new JSONObject();
        paramJson.put("deptId",deptId);
        paramJson.put("indexName",indexName);
        List<JSONObject> resultList = portraitIndexDao.getIndexInfo(paramJson);
        if(resultList!=null && resultList.size()>0){
            oneRowCheck.put("message", "相同部门相同指标已存在，不能重复导入！");
            return oneRowCheck;
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

}
