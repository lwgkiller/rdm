package com.redxun.materielextend.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.materielextend.core.dao.MaterielApplyDao;
import com.redxun.materielextend.core.dao.MaterielDao;
import com.redxun.materielextend.core.util.CommonUtil;
import com.redxun.materielextend.core.util.MaterielConstant;
import com.redxun.materielextend.core.util.ResultData;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2020 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2020/6/6 15:07
 */
@Service
public class MaterielService {
    private static final Logger logger = LoggerFactory.getLogger(MaterielService.class);

    @Autowired
    private MaterielDao materielDao;
    @Autowired
    private MaterielApplyService materielApplyService;
    @Autowired
    private MaterielService materielService;
    @Resource
    private MaterielApplyDao materielApplyDao;

    public void exportMateriels(HttpServletRequest request, HttpServletResponse response) {
        String applyNo = RequestUtil.getString(request, "applyNo", "");
        String scene = RequestUtil.getString(request, "scene", "");
        String action = RequestUtil.getString(request, "action", "");
        String sqUserId = RequestUtil.getString(request, "sqUserId", "");
        String sceneName = "全部物料";
        if ("right".equalsIgnoreCase(scene)) {
            sceneName = "正确物料";
        } else if ("fail".equalsIgnoreCase(scene)) {
            sceneName = "问题物料";
        }
        try {
            // 通过模板导出物料
            String templateFileName = "物料扩充导出模板.xls";
            File file = new File(MaterielService.class.getClassLoader()
                    .getResource("templates/materielExtend/" + templateFileName).toURI());
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
            if (wb == null) {
                return;
            }
            List<String> applyNos = new ArrayList<>();
            applyNos.add(applyNo);
            boolean exportJg = true;
            if (MaterielConstant.MATERIEL_ACTION_VIEW.equalsIgnoreCase(action)
                    && !ContextUtil.getCurrentUserId().equals(sqUserId)) {
                exportJg = false;
            }
            generateWbByApplyNo(applyNos, scene, wb, !MaterielConstant.MATERIEL_ACTION_VIEW.equalsIgnoreCase(action),
                    exportJg);
            String downLoadFileName = applyNo + sceneName;
            ExcelUtils.writeWorkBook2Stream(downLoadFileName, wb, response);
        } catch (Exception e) {
            logger.error("Exception in exportMateriels", e);
            return;
        }
    }

    /**
     * 查询物料的字典信息，同时将prePropertyEffect影响的属性也抽取出来
     *
     * @return
     */
    public Map<String, Map<String, JSONObject>> toGetMaterielDic() {
        // 先根据查询结果分组
        Map<String, Map<String, JSONObject>> materielDic = queryMaterielDic();
        if (materielDic.isEmpty()) {
            return materielDic;
        }
        // 抽取前置属性影响后续的属性信息
        Map<String, JSONObject> preDefaultData = materielDic.get("preDefault");
        if (preDefaultData == null || preDefaultData.isEmpty()) {
            return materielDic;
        }
        // 组装
        Map<String, JSONObject> prePropertyEffect = new HashMap<String, JSONObject>();
        for (JSONObject oneProperty : preDefaultData.values()) {
            JSONArray prePropertyArr = oneProperty.getJSONArray("value");
            if (prePropertyArr == null || prePropertyArr.isEmpty()) {
                continue;
            }
            for (int index = 0; index < prePropertyArr.size(); index++) {
                JSONObject preProperty = prePropertyArr.getJSONObject(index);
                String enumPrePropertyKey = preProperty.getString("enumPrePropertyKey");
                if (!prePropertyEffect.containsKey(enumPrePropertyKey)) {
                    prePropertyEffect.put(enumPrePropertyKey, new JSONObject());
                }
                JSONObject oneEnumPrePropertyObj = prePropertyEffect.get(enumPrePropertyKey);
                String enumPrePropertyOp = preProperty.getString("enumPrePropertyOp");
                if (!oneEnumPrePropertyObj.containsKey(enumPrePropertyOp)) {
                    oneEnumPrePropertyObj.put(enumPrePropertyOp, new JSONObject());
                }
                JSONObject oneOpObj = oneEnumPrePropertyObj.getJSONObject(enumPrePropertyOp);
                String enumPrePropertyValue = preProperty.getString("enumPrePropertyValue");
                if (!oneOpObj.containsKey(enumPrePropertyValue)) {
                    oneOpObj.put(enumPrePropertyValue, new JSONArray());
                }

                JSONObject obj = new JSONObject();
                obj.put("propertyKey", preProperty.getString("propertyKey"));
                obj.put("propertyName", preProperty.getString("propertyName"));
                obj.put("propertyValue", preProperty.getString("propertyValue"));
                oneOpObj.getJSONArray(enumPrePropertyValue).add(obj);
            }
        }
        materielDic.put("prePropertyEffect", prePropertyEffect);
        return materielDic;
    }

    /**
     * 查询物料的属性信息，组装成各个角色负责的属性
     *
     * @param role2Name2Key
     * @param role2Key2Obj
     */
    public void toGetMaterielProperty(Map<String, Map<String, String>> role2Name2Key, JSONObject role2Key2Obj) {
        Map<String, Object> params = new HashMap<>();
        List<JSONObject> respProperties = queryMaterielProperties(params);
        if (respProperties == null || respProperties.isEmpty()) {
            return;
        }
        for (int index = 0; index < respProperties.size(); index++) {
            JSONObject propertyObj = respProperties.get(index);
            String respRoleKey = propertyObj.getString("respRoleKey");
            String propertyName = propertyObj.getString("propertyName");
            String propertyKey = propertyObj.getString("propertyKey");
            // Map
            if (!role2Name2Key.containsKey(respRoleKey)) {
                role2Name2Key.put(respRoleKey, new HashMap<>());
            }
            role2Name2Key.get(respRoleKey).put(propertyName, propertyKey);

            // JSONObject
            if (!role2Key2Obj.containsKey(respRoleKey)) {
                role2Key2Obj.put(respRoleKey, new JSONObject());
            }
            JSONObject oneRespRoleKeyObj = role2Key2Obj.getJSONObject(respRoleKey);
            JSONObject oneObj = new JSONObject();
            oneObj.put("propertyKey", propertyKey);
            oneObj.put("propertyName", propertyName);
            oneObj.put("requiredPrePropertyKey", propertyObj.getOrDefault("requiredPrePropertyKey", ""));
            oneObj.put("requiredPrePropertyName", propertyObj.getOrDefault("requiredPrePropertyName", ""));
            oneObj.put("requiredPrePropertyValue", propertyObj.getOrDefault("requiredPrePropertyValue", ""));
            oneObj.put("required", propertyObj.getString("required"));
            oneObj.put("propertyDesc", propertyObj.getOrDefault("propertyDesc", ""));
            oneRespRoleKeyObj.put(propertyKey, oneObj);
        }
    }

    /**
     * 物料导入模板下载
     *
     * @return
     */
    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "物料扩充导入模板.xls";
            // 创建文件实例
            File file = new File(
                    MaterielService.class.getClassLoader().getResource("templates/materielExtend/" + fileName).toURI());
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

    // 批量导入物料信息
    public void importMateriel(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            String applyNo = RequestUtil.getString(request, "applyNo", "");
            if (StringUtils.isBlank(applyNo)) {
                logger.error("申请单号为空！");
                result.put("result", false);
                result.getJSONArray("message").add("数据导入失败，申请单号为空！");
                return;
            }
            MultipartFile fileObj = multipartRequest.getFile("materielImportFile");
            if (fileObj == null) {
                logger.error("文件为空！");
                result.put("result", false);
                result.getJSONArray("message").add("数据导入失败，内容为空！");
                return;
            }
            Workbook wb = null;
            String fileName = fileObj.getOriginalFilename();
            if (fileName.endsWith(".xls")) {
                wb = new HSSFWorkbook(fileObj.getInputStream());
            } else {
                wb = new XSSFWorkbook(fileObj.getInputStream());
            }
            Sheet sheet = wb.getSheet("物料数据");
            if (sheet == null) {
                sheet = wb.getSheetAt(0);
                if (sheet == null) {
                    logger.error("找不到物料数据导入页");
                    result.put("result", false);
                    result.getJSONArray("message").add("数据导入失败，找不到物料数据导入页！");
                    return;
                }
            }
            int rowNum = sheet.getPhysicalNumberOfRows();
            if (rowNum < 1) {
                logger.error("找不到标题行");
                result.put("result", false);
                result.getJSONArray("message").add("数据导入失败，找不到标题行！");
                return;
            }

            // 解析标题部分
            Row titleRow = sheet.getRow(0);
            if (titleRow == null) {
                logger.error("找不到标题行");
                result.put("result", false);
                result.getJSONArray("message").add("数据导入失败，找不到标题行！");
                return;
            }
            List<String> titleList = new ArrayList<>();
            for (int i = 0; i < titleRow.getLastCellNum(); i++) {
                String titleVal = StringUtils.trim(titleRow.getCell(i).getStringCellValue());
                if ("备注(材料）".equalsIgnoreCase(titleVal)) {
                    titleVal = "备注（材质）";
                }
                titleList.add(titleVal);
            }

            if (rowNum < 2) {
                logger.info("数据行为空");
                result.put("result", false);
                result.getJSONArray("message").add("数据导入失败，有效数据为空!");
                return;
            }

            // 解析验证数据部分,根据角色确定要处理哪些属性，以及默认值的赋值和必填的校验
            List<JSONObject> dataInsert = new ArrayList<>();
            List<JSONObject> dataUpdate = new ArrayList<>();
            // 存储角色对应的字段Name和key
            Map<String, Map<String, String>> role2Name2Key = new HashMap<String, Map<String, String>>();
            JSONObject role2Key2Obj = new JSONObject();
            toGetMaterielProperty(role2Name2Key, role2Key2Obj);
            String opRoleName = materielApplyService.queryMaterielOpRoleByUserId(ContextUtil.getCurrentUserId());
            Map<String, String> name2Key = role2Name2Key.get(opRoleName);
            if (name2Key == null || name2Key.isEmpty()) {
                result.put("result", false);
                result.getJSONArray("message").add("数据导入失败，本角色没有可处理的属性！");
                return;
            }
            // 查询该申请单当前已经入库的物料信息(用来判断一个申请单中不允许有不同的数据但是相同的物料号的情况)
            Map<String, String> materielNo2Id = new HashMap<>();
            Map<String, Object> param = new HashMap<>();
            List<String> applyNos = new ArrayList<>();
            applyNos.add(applyNo);
            param.put("applyNos", applyNos);
            List<JSONObject> materielList = materielService.queryMaterielsByApplyNo(param);
            for (int index = 0; index < materielList.size(); index++) {
                JSONObject oneMateriel = materielList.get(index);
                materielNo2Id.put(oneMateriel.getString("wlhm"), oneMateriel.getString("id"));
            }
            Map<String, Object> params = new HashMap<>();
            List<JSONObject> codeList = materielApplyDao.queryCodeList(params);
            Map<String, Map<String, JSONObject>> materielDic = toGetMaterielDic();
            for (int i = 1; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                // 判断是否是个空行（所有cell的值都为空），不处理
                boolean isBlankRow = CommonUtil.judgeIsBlankRow(row);
                if (!isBlankRow) {
                    parseAndCheckDataFromRow(i, row, dataInsert, dataUpdate, titleList, result, materielDic,
                            role2Name2Key, role2Key2Obj, opRoleName, materielNo2Id, codeList);
                }
            }
            // 如果数据有错误，则不进行新增或者更新
            if (!result.getBooleanValue("result")) {
                logger.error("数据中存在错误，本次操作不执行！");
                return;
            }

            // 批量新增
            List<JSONObject> tempAddList = new ArrayList<>();
            for (int i = 0; i < dataInsert.size(); i++) {
                JSONObject oneAddMaterielObj = dataInsert.get(i);
                convert2Upper(oneAddMaterielObj);
                oneAddMaterielObj.put("applyNo", applyNo);
                tempAddList.add(oneAddMaterielObj);
                logger.info("表单中提交excel上传（add）" + "单号：" + applyNo + "更新人员:" + ContextUtil.getCurrentUserId() +"采购类型："+oneAddMaterielObj.get("cglx") +"采购存储地点：" + oneAddMaterielObj.get("cgccdd"));
                if (tempAddList.size() % 20 == 0) {
                    materielDao.batchAddMateriels(tempAddList);
                    tempAddList.clear();
                }
            }
            if (!tempAddList.isEmpty()) {
                materielDao.batchAddMateriels(tempAddList);
                tempAddList.clear();
            }
            // 批量更新
            List<Map<String, String>> updateList = CommonUtil.convertJSONObject2Map(dataUpdate);
            List<Map<String, String>> tempUpdateList = new ArrayList<>();
            for (int i = 0; i < updateList.size(); i++) {
                Map<String, String> oneUpdateMaterielObj = updateList.get(i);
                convert2UpperMap(oneUpdateMaterielObj);
                // oneUpdateMaterielObj.put("applyNo", applyNo);
                tempUpdateList.add(oneUpdateMaterielObj);
                logger.info("表单中提交excel上传（update）" + "单号：" + applyNo + "更新人员:" + ContextUtil.getCurrentUserId() +"采购类型："+oneUpdateMaterielObj.get("cglx") +"采购存储地点：" + oneUpdateMaterielObj.get("cgccdd"));
                if (tempUpdateList.size() % 20 == 0) {
                    materielDao.updateMaterielBatch(tempUpdateList);
                    tempUpdateList.clear();
                }
            }
            if (!tempUpdateList.isEmpty()) {
                materielDao.updateMaterielBatch(tempUpdateList);
                tempUpdateList.clear();
            }
            result.put("result", true);
            if (result.getJSONArray("message").isEmpty()) {
                result.getJSONArray("message").add("数据导入完成！");
            }
        } catch (Exception e) {
            logger.error("Exception in importMateriel", e);
            result.put("result", false);
            result.getJSONArray("message").add("数据导入失败，系统异常！");
        }
    }

    /**
     * 解析并验证每一行数据
     *
     * @param rowIndex
     * @param row
     * @param dataInsert
     * @param dataUpdate
     * @param titleList
     * @param result
     * @param materielDic
     * @param role2Name2Key
     * @param role2Key2Obj
     */
    private void parseAndCheckDataFromRow(int rowIndex, Row row, List<JSONObject> dataInsert,
                                          List<JSONObject> dataUpdate, List<String> titleList, JSONObject result,
                                          Map<String, Map<String, JSONObject>> materielDic, Map<String, Map<String, String>> role2Name2Key,
                                          JSONObject role2Key2Obj, String opRoleName, Map<String, String> materielNo2Id, List<JSONObject> codeList) {
        if (MaterielConstant.APPLY_OP_SQRKC.equalsIgnoreCase(opRoleName)) {
            sqrkcImportMateriels(rowIndex, row, dataInsert, dataUpdate, titleList, result, materielDic, role2Name2Key,
                    role2Key2Obj, materielNo2Id, codeList);
        } else {
            noSqrkcImportMateriels(rowIndex, row, dataUpdate, titleList, result, materielDic, role2Name2Key,
                    role2Key2Obj, opRoleName, materielNo2Id);
        }
    }

    // 申请人扩充物料的导入
    private void sqrkcImportMateriels(int rowIndex, Row row, List<JSONObject> dataInsert, List<JSONObject> dataUpdate,
                                      List<String> titleList, JSONObject result, Map<String, Map<String, JSONObject>> materielDic,
                                      Map<String, Map<String, String>> role2Name2Key, JSONObject role2Key2Obj, Map<String, String> materielNo2Id,
                                      List<JSONObject> codeList) {
        JSONObject materielObj = new JSONObject();
        int materielIdIndex = titleList.indexOf("物料主键");
        String materielId = CommonUtil.toGetCellStringByType(row, materielIdIndex);
        if (StringUtils.isNotBlank(materielId)) {
            materielObj.put("id", materielId);
        }
        // 问题物料信息处理
        int markErrorIndex = titleList.indexOf("是否为问题物料");
        String markErrorValue = CommonUtil.toGetCellStringByType(row, markErrorIndex);
        ;
        boolean judgeErrorResult = judgeErrorMateriel(markErrorValue, result, titleList, rowIndex, row, materielObj);
        if (!judgeErrorResult) {
            return;
        }

        // 读取所有的值，如果为default则取默认值
        Map<String, JSONObject> defaultKey2Obj = materielDic.get("default");
        for (Map.Entry<String, Map<String, String>> entry : role2Name2Key.entrySet()) {
            for (Map.Entry<String, String> innerEntry : entry.getValue().entrySet()) {
                String propertyName = innerEntry.getKey();
                String propertyKey = innerEntry.getValue();
                if (defaultKey2Obj.containsKey(propertyKey)) {
                    materielObj.put(propertyKey, defaultKey2Obj.get(propertyKey).getJSONArray("value").getString(0));
                } else {
                    int cellIndex = titleList.indexOf(propertyName);
                    String cellVal = CommonUtil.toGetCellStringByType(row, cellIndex);
                    if ("采购类型".equalsIgnoreCase(propertyName)) {
                        cellVal = cellVal.toUpperCase();
                    }
                    // 如果是价格字段，值为空则丢弃
                    if ("价格".equals(propertyName)) {
                        if (StringUtils.isNotBlank(cellVal) && !CommonUtil.judgeIsNumber(cellVal)) {
                            result.put("result", false);
                            result.getJSONArray("message").add("第" + (rowIndex + 1) + "行数据导入失败，价格不是数字！");
                            return;
                        }
                        if (StringUtils.isNotBlank(cellVal)) {
                            DecimalFormat df = new DecimalFormat("0.00");
                            cellVal = df.format(Double.parseDouble(cellVal));
                        }
                    }
                    materielObj.put(propertyKey, cellVal);
                }
            }
        }

        // 对属性中影响到其他的属性进行赋值
        if (materielDic.containsKey("prePropertyEffect")) {
            Map<String, JSONObject> prePropertyEffectObj = materielDic.get("prePropertyEffect");
            for (String key : materielObj.keySet()) {
                if (prePropertyEffectObj.containsKey(key)) {
                    JSONObject thisPrePropertyEffect = prePropertyEffectObj.get(key);
                    String cellVal = materielObj.getString(key);
                    toAssignEffectProperty(thisPrePropertyEffect, cellVal, materielObj);
                }
            }
        }
        // 对值不为空的select属性进行检查
        if (materielDic.containsKey("select")) {
            Map<String, JSONObject> selectObjMap = materielDic.get("select");
            for (Map.Entry<String, JSONObject> selectObjEntry : selectObjMap.entrySet()) {
                String selectCellVal = materielObj.getString(selectObjEntry.getKey());
                if (StringUtils.isNotBlank(selectCellVal)) {
                    JSONArray values = selectObjEntry.getValue().getJSONArray("value");
                    if (!values.contains(selectCellVal)) {
                        result.put("result", false);
                        result.getJSONArray("message").add("第" + (rowIndex + 1) + "行数据导入失败，“"
                                + selectObjEntry.getValue().getString("propertyName") + "”的值非法！");
                        return;
                    }
                }
            }
        }
        // 对属性进行必填性检查(标注为问题的不检查)
        if (!"是".equals(markErrorValue)) {
            JSONObject oneRoleProperty = role2Key2Obj.getJSONObject(MaterielConstant.APPLY_OP_SQRKC);
            boolean judgeResult =
                    judgeRequiredByOwnProperty(rowIndex, row, oneRoleProperty, materielObj, result, titleList);
            if (!judgeResult) {
                return;
            }
        }

        // 判断当前是否已经有相同物料号码的数据（包括数据库和本excel中的）
        String id = materielObj.getString("id");
        String wlhm = materielObj.getString("wlhm");
        if (materielNo2Id.containsKey(wlhm)) {
            String existId = materielNo2Id.get(wlhm);
            if (StringUtils.isNotBlank(existId) && !existId.equalsIgnoreCase(id)) {
                result.put("result", false);
                result.getJSONArray("message").add("第" + (rowIndex + 1) + "行数据导入失败，物料号码“" + wlhm + "”在申请单或本excel中已存在！");
                return;
            } else if (StringUtils.isBlank(existId)) {
                result.put("result", false);
                result.getJSONArray("message").add("第" + (rowIndex + 1) + "行数据导入失败，物料号码“" + wlhm + "”在本excel中已存在！");
                return;
            }
        } else {
            materielNo2Id.put(wlhm, id);
        }
        // 判断关重件是否填写规范
        String codeName = materielObj.getString("codeName");
        boolean codeExsit = false;
        String codeId = null;
        for (JSONObject codeJson : codeList) {
            if (codeName.equals(codeJson.getString("codeName"))) {
                codeExsit = true;
                codeId = codeJson.getString("codeId");
                break;
            }
        }
        if (codeExsit) {
            materielObj.put("codeId", codeId);
        } else {
            result.put("result", false);
            result.getJSONArray("message").add("第" + (rowIndex + 1) + "行数据导入失败，关重件名称“" + codeName + "”不存在,请修改后重新导入！"
                    + "<br>请参考页面新增窗口中选择的关重件名称，不是关重件请填写“非关重件”，是关重件但当前没有可供选择的请填写“需扩充”！");
            return;
        }
        //
        // codeSet.add(codeJson.getString("codeName"));
        // }
        // if (!codeSet.contains(codeName)) {
        // result.put("result", false);
        // result.getJSONArray("message").add("第" + (rowIndex + 1) + "行数据导入失败，关重件名称“" + codeName + "”不存在,请修改后重新导入！");
        // return;
        // } else {
        // for (JSONObject codeJson : codeList) {
        //
        // }
        // }
        materielObj.put("wlzt", MaterielConstant.WLZT);
        if (materielObj.containsKey("id")) {
            dataUpdate.add(materielObj);
        } else {
            materielObj.put("id", IdUtil.getId());
            // 新增物料不允许设置为问题物料
            materielObj.put("markError", MaterielConstant.MARK_ERROR_NO);
            materielObj.put("markErrorReason", "");
            materielObj.put("markErrorUserId", "");
            materielObj.put("markErrorTime", "");
            dataInsert.add(materielObj);
        }
    }

    // 非申请人扩充物料的导入
    private void noSqrkcImportMateriels(int rowIndex, Row row, List<JSONObject> dataUpdate, List<String> titleList,
                                        JSONObject result, Map<String, Map<String, JSONObject>> materielDic,
                                        Map<String, Map<String, String>> role2Name2Key, JSONObject role2Key2Obj, String opRoleName,
                                        Map<String, String> materielNo2Id) {
        int materielIdIndex = titleList.indexOf("物料主键");
        String materielId = CommonUtil.toGetCellStringByType(row, materielIdIndex);
        if (StringUtils.isBlank(materielId)) {
            result.put("result", false);
            result.getJSONArray("message").add("第" + (rowIndex + 1) + "行数据导入失败，“物料主键”为空！");
            return;
        }
        JSONObject materielObj = new JSONObject();
        materielObj.put("id", materielId);

        // 问题物料信息处理
        int markErrorIndex = titleList.indexOf("是否为问题物料");
        String markErrorValue = CommonUtil.toGetCellStringByType(row, markErrorIndex);
        boolean judgeErrorResult = judgeErrorMateriel(markErrorValue, result, titleList, rowIndex, row, materielObj);
        if (!judgeErrorResult) {
            return;
        }
        // 构造物料信息体，选择性的读取字段，同时赋值本角色属性影响的属性
        Map<String, String> name2Key = role2Name2Key.get(opRoleName);
        for (Map.Entry<String, String> entry : name2Key.entrySet()) {
            int cellIndex = titleList.indexOf(entry.getKey());
            String cellVal = CommonUtil.toGetCellStringByType(row, cellIndex);
            if ("采购类型".equalsIgnoreCase(entry.getKey())) {
                cellVal = cellVal.toUpperCase();
            }
            // 本属性影响的属性赋值
            if (materielDic.containsKey("prePropertyEffect")
                    && materielDic.get("prePropertyEffect").containsKey(entry.getValue())) {
                JSONObject prePropertyEffect = materielDic.get("prePropertyEffect").get(entry.getValue());
                toAssignEffectProperty(prePropertyEffect, cellVal, materielObj);
            }
            // 属性为default不处理
            if (materielDic.containsKey("default") && materielDic.get("default").containsKey(entry.getValue())) {
                continue;
            }
            // 属性为preDefault不处理(前置条件时候已经处理完)
            if (materielDic.containsKey("preDefault") && materielDic.get("preDefault").containsKey(entry.getValue())) {
                continue;
            }
            // 如果是价格字段，值为空则丢弃
            if ("价格".equals(entry.getKey())) {
                if (StringUtils.isBlank(cellVal)) {
                    continue;
                } else {
                    if (!CommonUtil.judgeIsNumber(cellVal)) {
                        result.put("result", false);
                        result.getJSONArray("message").add("第" + (rowIndex + 1) + "行数据导入失败，价格不是数字！");
                        return;
                    }
                    DecimalFormat df = new DecimalFormat("0.00");
                    cellVal = df.format(Double.parseDouble(cellVal));
                }
            }
            // 集采类型转换
            if ("集采类型".equals(entry.getKey())) {
                if (cellVal.equalsIgnoreCase("非集采")) {
                    cellVal = "fjc";
                } else if (cellVal.equalsIgnoreCase("保税集采")) {
                    cellVal = "bsjc";
                } else if (cellVal.equalsIgnoreCase("供应集采")) {
                    cellVal = "gyjc";
                }
            }
            materielObj.put(entry.getValue(), cellVal);
        }
        // 财务角色额外读取重量，采购角色额外读取供应商（如果不为空）
        if (opRoleName.equals(MaterielConstant.APPLY_OP_CWKC)) {
            int cellIndex = titleList.indexOf("重量");
            String cellVal = CommonUtil.toGetCellStringByType(row, cellIndex);
            if (StringUtils.isNotBlank(cellVal)) {
                materielObj.put("zl", cellVal);
            }
        }
        if (opRoleName.equals(MaterielConstant.APPLY_OP_CGKC)) {
            int cellIndex = titleList.indexOf("供应商");
            String cellVal = CommonUtil.toGetCellStringByType(row, cellIndex);
            if (StringUtils.isNotBlank(cellVal)) {
                materielObj.put("gys", cellVal);
            }
        }

        // 对select进行检查(如果是问题物料，只检查不为空的select)
        if (materielDic.containsKey("select")) {
            Map<String, JSONObject> selectObjMap = materielDic.get("select");
            Collection<String> opRoleKeys = name2Key.values();
            for (Map.Entry<String, JSONObject> selectObjEntry : selectObjMap.entrySet()) {
                if (opRoleKeys.contains(selectObjEntry.getKey())) {
                    JSONArray values = selectObjEntry.getValue().getJSONArray("value");
                    String propertyName = selectObjEntry.getValue().getString("propertyName");
                    String cellValue = materielObj.getString(selectObjEntry.getKey());

                    if (!"是".equals(markErrorValue) || StringUtils.isNotBlank(cellValue)) {
                        if (!values.contains(cellValue)) {
                            result.put("result", false);
                            result.getJSONArray("message")
                                    .add("第" + (rowIndex + 1) + "行数据导入失败，" + propertyName + "值非法！");
                            return;
                        }
                    }
                }
            }
        }

        // 对于非工艺角色，不允许将问题物料状态由“是”改为“否”
        if (!MaterielConstant.APPLY_OP_GYKC.equals(opRoleName) && !"是".equals(markErrorValue)) {
            JSONObject materielDBObj = materielDao.queryMaterielById(materielId);
            if (MaterielConstant.MARK_ERROR_YES.equals(materielDBObj.getString("markError"))) {
                result.put("result", false);
                result.getJSONArray("message").add("第" + (rowIndex + 1) + "行数据导入失败，本角色所在环节不允许将问题物料状态由“是”改为“否”！");
                return;
            }
        }
        // 对属性进行必填性检查（非问题物料）
        if (!"是".equals(markErrorValue)) {
            JSONObject oneRoleProperty = role2Key2Obj.getJSONObject(opRoleName);
            boolean judgeResult =
                    judgeRequiredByOwnProperty(rowIndex, row, oneRoleProperty, materielObj, result, titleList);
            if (!judgeResult) {
                return;
            }
        }
        // 判断当前是否已经有相同物料号码的数据（包括数据库和本excel中的）
        // String id = materielObj.getString("id");
        // String wlhm = materielObj.getString("wlhm");
        // if (materielNo2Id.containsKey(wlhm)) {
        // String existId = materielNo2Id.get(wlhm);
        // if (StringUtils.isNotBlank(existId) && !existId.equalsIgnoreCase(id)) {
        // result.put("result", false);
        // result.getJSONArray("message").add("第" + (rowIndex + 1) + "行数据导入失败，物料号码“" + wlhm + "”在申请单或本excel中已存在！");
        // return;
        // } else if (StringUtils.isBlank(existId)) {
        // result.put("result", false);
        // result.getJSONArray("message").add("第" + (rowIndex + 1) + "行数据导入失败，物料号码“" + wlhm + "”在本excel中已存在！");
        // return;
        // }
        // } else {
        // materielNo2Id.put(wlhm, id);
        // }

        dataUpdate.add(materielObj);
    }

    public boolean judgeErrorMateriel(String markErrorValue, JSONObject result, List<String> titleList, int rowIndex,
                                      Row row, JSONObject materielObj) {
        int markErrorReasonIndex = titleList.indexOf("问题原因");
        String markErrorReason = CommonUtil.toGetCellStringByType(row, markErrorReasonIndex);
        if ("是".equals(markErrorValue)) {
            if (StringUtils.isBlank(markErrorReason)) {
                result.put("result", false);
                result.getJSONArray("message").add("第" + (rowIndex + 1) + "行数据导入失败，“是否为问题物料”值为“是”时，“问题原因”必填！");
                return false;
            } else {
                materielObj.put("markError", MaterielConstant.MARK_ERROR_YES);
                materielObj.put("markErrorReason", markErrorReason);
                materielObj.put("markErrorUserId", ContextUtil.getCurrentUserId());
                materielObj.put("markErrorTime", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            }
        } else if ("否".equals(markErrorValue) || "".equals(markErrorValue)) {
            materielObj.put("markError", MaterielConstant.MARK_ERROR_NO);
            materielObj.put("markErrorReason", markErrorReason);
            materielObj.put("markErrorUserId", "");
            materielObj.put("markErrorTime", "");
        } else {
            result.put("result", false);
            result.getJSONArray("message").add("第" + (rowIndex + 1) + "行数据导入失败，“是否为问题物料”请选择“是”或“否”！");
            return false;
        }
        return true;
    }

    public boolean judgeRequiredByOwnProperty(int rowIndex, Row row, JSONObject oneRoleProperty, JSONObject materielObj,
                                              JSONObject result, List<String> titleList) {
        // 对属性进行必填性检查（仅限于对象中读入的属性，对于非申请人场景，default和predefault都不读取）
        for (String key : materielObj.keySet()) {
            if (oneRoleProperty.containsKey(key)) {
                JSONObject oneRoleOneProperty = oneRoleProperty.getJSONObject(key);
                if (MaterielConstant.REQUIRED_YES.equalsIgnoreCase(oneRoleOneProperty.getString("required"))) {
                    String value = materielObj.getString(key);
                    String requiredPrePropertyName = oneRoleOneProperty.getString("requiredPrePropertyName");
                    String requiredPrePropertyKey = oneRoleOneProperty.getString("requiredPrePropertyKey");

                    if (StringUtils.isBlank(requiredPrePropertyName)) {
                        if (StringUtils.isBlank(value)) {
                            result.put("result", false);
                            result.getJSONArray("message").add("第" + (rowIndex + 1) + "行数据导入失败，“"
                                    + oneRoleOneProperty.getString("propertyName") + "”的值不能为空！");
                            return false;
                        }
                    } else {
                        // 带前置条件的必填检查，如果前置条件不在本次更新字段中，则需要从数据库中读取，防止恶意篡改
                        String requiredPrePropertyValue = oneRoleOneProperty.getString("requiredPrePropertyValue");
                        String currentPreCellValue = "";
                        if (materielObj.containsKey(requiredPrePropertyKey)) {
                            currentPreCellValue = materielObj.getString(requiredPrePropertyKey);
                        } else if (StringUtils.isNotBlank(materielObj.getString("id"))) {
                            JSONObject dbObj = materielDao.queryMaterielById(materielObj.getString("id"));
                            if (dbObj != null) {
                                currentPreCellValue = dbObj.getString(requiredPrePropertyKey);
                            }
                        }
                        if (currentPreCellValue.equals(requiredPrePropertyValue) && StringUtils.isBlank(value)) {
                            result.put("result", false);
                            result.getJSONArray("message")
                                    .add("第" + (rowIndex + 1) + "行数据导入失败，当“" + requiredPrePropertyName + "”为“"
                                            + requiredPrePropertyValue + "”时，“" + oneRoleOneProperty.getString("propertyName")
                                            + "”不能为空！");
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    // 将该属性影响到的属性进行赋值
    public void toAssignEffectProperty(JSONObject prePropertyEffect, String cellValue, JSONObject materielObj) {
        for (String opVal : prePropertyEffect.keySet()) {
            JSONObject opValObj = prePropertyEffect.getJSONObject(opVal);
            if (MaterielConstant.PRE_OP_YES.equalsIgnoreCase(opVal)) {
                for (String preVal : opValObj.keySet()) {
                    if (cellValue.equals(preVal)) {
                        JSONArray preValArr = opValObj.getJSONArray(preVal);
                        for (int index = 0; index < preValArr.size(); index++) {
                            materielObj.put(preValArr.getJSONObject(index).getString("propertyKey"),
                                    preValArr.getJSONObject(index).getString("propertyValue"));
                        }
                    }
                }
            } else if (MaterielConstant.PRE_OP_NO.equalsIgnoreCase(opVal)) {
                for (String preVal : opValObj.keySet()) {
                    if (!cellValue.equals(preVal)) {
                        JSONArray preValArr = opValObj.getJSONArray(preVal);
                        for (int index = 0; index < preValArr.size(); index++) {
                            materielObj.put(preValArr.getJSONObject(index).getString("propertyKey"),
                                    preValArr.getJSONObject(index).getString("propertyValue"));
                        }
                    }
                }
            }
        }
    }

    /**
     * 通过id批量删除物料
     *
     * @param materielIds
     */
    public ResultData deleteMaterielByIds(List<String> materielIds) {
        ResultData resultData = new ResultData();
        if (materielIds == null || materielIds.isEmpty()) {
            return resultData;
        }
        try {
            materielDao.batchDeleteMaterielsByIds(materielIds);
        } catch (Exception e) {
            logger.error("Exception in deleteMaterielByIds", e);
            resultData.setSuccess(false);
            resultData.setMessage("系统异常");
        }
        return resultData;
    }

    //.查询物料字段属性
    public List<JSONObject> queryMaterielProperties(Map<String, Object> params) {
        List<JSONObject> materielProperties = materielDao.queryMaterielProperties(params);
        if (materielProperties == null) {
            materielProperties = new ArrayList<>();
        }
        return materielProperties;
    }

    /**
     * 通过id查询物料详情
     *
     * @param materielId
     * @return
     */
    public JSONObject queryMaterielById(String materielId) {
        if (StringUtils.isBlank(materielId)) {
            return new JSONObject();
        }
        JSONObject materielObj = materielDao.queryMaterielById(materielId);
        return materielObj;
    }

    //..通过applyNo查找申请单关联的物料明细
    public List<JSONObject> queryMaterielsByApplyNo(Map<String, Object> param) {
        if (param.get("applyNos") == null) {
            return new ArrayList<>();
        }
        List<JSONObject> materiels = materielDao.queryMaterielsByApplyNo(param);
        if (materiels == null) {
            materiels = new ArrayList<>();
        }
        return materiels;
    }

    /**
     * 查询物料字典并分类
     * <p>
     * "default":{ "jgdw":{ "propertyKey":"jgdw", "propertyName":"价格单位", "value":["1"] } }, "select":{ "cglx":{
     * "propertyKey":"cglx", "propertyName":"采购类型", "value":["E","F"] } } "preDefault":{ "pldx":{ "propertyKey":"pldx",
     * "propertyName":"批量大小", "value":[ { "propertyKey":"pldx", "propertyName":"批量大小", "propertyValue":"EX",
     * "enumPrePropertyKey":"cglx", "enumPrePropertyName":"采购类型", "enumPrePropertyOp":"yes", "enumPrePropertyValue":"E"
     * }, { "propertyKey":"pldx", "propertyName":"批量大小", "propertyValue":"WB", "enumPrePropertyKey":"cglx",
     * "enumPrePropertyName":"采购类型", "enumPrePropertyOp":"yes", "enumPrePropertyValue":"F" } ] } } }
     *
     * @return
     */
    public Map<String, Map<String, JSONObject>> queryMaterielDic() {
        Map<String, Map<String, JSONObject>> result = new HashMap<>();
        JSONArray dicArray = materielDao.queryMaterielDic();
        if (dicArray == null || dicArray.isEmpty()) {
            return result;
        }
        for (int index = 0; index < dicArray.size(); index++) {
            JSONObject oneObject = dicArray.getJSONObject(index);
            String type = oneObject.getString("type");
            if (!result.containsKey(type)) {
                result.put(type, new HashMap<>());
            }
            Map<String, JSONObject> oneTypeKey2Obj = result.get(type);
            String propertyKey = oneObject.getString("propertyKey");
            if (!oneTypeKey2Obj.containsKey(propertyKey)) {
                oneTypeKey2Obj.put(propertyKey, new JSONObject());
            }
            JSONObject propertyObj = oneTypeKey2Obj.get(propertyKey);
            propertyObj.put("propertyName", oneObject.getString("propertyName"));
            propertyObj.put("propertyKey", propertyKey);
            if (!propertyObj.containsKey("value")) {
                propertyObj.put("value", new JSONArray());
            }
            JSONArray valArr = propertyObj.getJSONArray("value");
            String enumValue = oneObject.getString("enumValue");
            switch (type) {
                case "select":
                    valArr.add(enumValue);
                    break;
                case "default":
                    valArr.add(enumValue);
                    break;
                case "preDefault":
                    String enumPrePropertyName = oneObject.getString("enumPrePropertyName");
                    String enumPrePropertyKey = oneObject.getString("enumPrePropertyKey");
                    String enumPrePropertyOp = oneObject.getString("enumPrePropertyOp");
                    String enumPrePropertyValue = oneObject.getString("enumPrePropertyValue");
                    JSONObject oneValue = new JSONObject();
                    oneValue.put("enumPrePropertyName", enumPrePropertyName);
                    oneValue.put("enumPrePropertyKey", enumPrePropertyKey);
                    oneValue.put("enumPrePropertyOp", enumPrePropertyOp);
                    oneValue.put("enumPrePropertyValue", enumPrePropertyValue);
                    oneValue.put("propertyName", propertyObj.getString("propertyName"));
                    oneValue.put("propertyKey", propertyObj.getString("propertyKey"));
                    oneValue.put("propertyValue", enumValue);
                    valArr.add(oneValue);
                    break;
                default:
            }
        }
        return result;
    }

    //..保存或更新单条物料
    public ResultData materielEditSave(String postBodyStr) {
        ResultData resultData = new ResultData();
        if (StringUtils.isBlank(postBodyStr)) {
            logger.error("请求体为空！");
            resultData.setSuccess(false);
            resultData.setMessage("表单为空");
            return resultData;
        }
        JSONObject postBodyObj = JSONObject.parseObject(postBodyStr);
        if (postBodyObj == null || postBodyObj.isEmpty()) {
            logger.error("请求体为空！");
            resultData.setSuccess(false);
            resultData.setMessage("表单为空");
            return resultData;
        }
        // 处理新增或者更新
        try {
            convert2Upper(postBodyObj);
            postBodyObj.put("wlzt", MaterielConstant.WLZT);
            String jg = postBodyObj.getString("jg");
            if (StringUtils.isNotBlank(jg)) {
                DecimalFormat df = new DecimalFormat("0.00");
                postBodyObj.put("jg", df.format(postBodyObj.getDouble("jg")));
            }
            if (StringUtils.isBlank(postBodyObj.getString("id"))) {
                batchAddMateriels(Arrays.asList(postBodyObj), resultData);
            } else {
                batchUpdateMateriels(Arrays.asList(postBodyObj), resultData);
            }
        } catch (Exception e) {
            logger.error("Exception in materielEditSave", e);
            resultData.setSuccess(false);
            resultData.setMessage("系统异常");
        }
        return resultData;
    }

    /**
     * 批量新增
     *
     * @param materiels
     * @param resultData
     */
    private void batchAddMateriels(List<JSONObject> materiels, ResultData resultData) {
        List<JSONObject> tempList = new ArrayList<>();
        for (JSONObject oneMateriel : materiels) {
            oneMateriel.put("id", IdUtil.getId());
            tempList.add(oneMateriel);
            logger.info("表单新增操作(add)"+"单号："+oneMateriel.getString("applyNo")+"更新人员:"+ContextUtil.getCurrentUserId()+"采购类型："+oneMateriel.get("cglx") +"采购存储地点"+oneMateriel.getString("cgccdd"));
            if (tempList.size() == 20) {
                materielDao.batchAddMateriels(tempList);
                tempList.clear();
            }
        }
        if (!tempList.isEmpty()) {
            materielDao.batchAddMateriels(tempList);
            tempList.clear();
        }
        resultData.setMessage("新增成功");
    }

    private void batchUpdateMateriels(List<JSONObject> materiels, ResultData resultData) {
        if (materiels == null || materiels.isEmpty()) {
            resultData.setMessage("更新成功");
            return;
        }
        for (JSONObject oneMateriel : materiels) {
            materielDao.updateOneMateriel(oneMateriel);
            logger.info("表单保存操作"+"单号："+oneMateriel.getString("applyNo")+"更新人员:"+ContextUtil.getCurrentUserId()+ContextUtil.getCurrentUserId() +"采购类型："+oneMateriel.get("cglx")+"采购存储地点"+oneMateriel.getString("cgccdd"));
        }
        resultData.setMessage("更新成功");
    }

    public void generateWbByApplyNo(List<String> applyNos, String scene, Workbook wb, boolean exportId,
                                    boolean exportJg) {
        Map<String, Object> param = new HashMap<>();
        param.put("applyNos", applyNos);
        param.put("scene", scene);
        List<JSONObject> materielList = materielService.queryMaterielsByApplyNo(param);
        if (materielList == null || materielList.isEmpty()) {
            return;
        }
        Sheet sheet = wb.getSheet("物料数据");

        // 解析汉字与key的对应关系
        Map<String, Object> params = new HashMap<>();
        List<JSONObject> properties = queryMaterielProperties(params);
        if (properties == null || properties.isEmpty()) {
            return;
        }
        Map<String, String> name2Key = new HashMap<>();
        for (int index = 0; index < properties.size(); index++) {
            JSONObject oneProp = properties.get(index);
            name2Key.put(oneProp.getString("propertyName"), oneProp.getString("propertyKey"));
        }
        if (!"fail".equalsIgnoreCase(scene)) {
            name2Key.put("是否为问题物料", "markError");
        }
        if (exportId) {
            name2Key.put("物料主键", "id");
        }
        if (!exportJg) {
            name2Key.remove("价格");
        }
        name2Key.put("问题原因", "markErrorReason");
        name2Key.put("物料状态", "wlzt");
        name2Key.put("审批单号", "applyNo");
        name2Key.put("申请人", "sqUserName");
        name2Key.put("申请部门", "sqDeptName");

        // 取标题行数据
        Row titleRow = sheet.getRow(0);
        if (titleRow == null) {
            logger.error("找不到标题行");
            return;
        }
        List<String> titleList = new ArrayList<>();
        for (int i = 0; i < titleRow.getLastCellNum(); i++) {
            titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
        }
        // 写入数据
        for (int rowIndex = 0; rowIndex < materielList.size(); rowIndex++) {
            JSONObject oneMateriel = materielList.get(rowIndex);
            // 集采类型文字替换
            if (oneMateriel.containsKey("jclx")) {
                if (oneMateriel.getString("jclx").equalsIgnoreCase("fjc")) {
                    oneMateriel.put("jclx", "非集采");
                } else if (oneMateriel.getString("jclx").equalsIgnoreCase("bsjc")) {
                    oneMateriel.put("jclx", "保税集采");
                } else if (oneMateriel.getString("jclx").equalsIgnoreCase("gyjc")) {
                    oneMateriel.put("jclx", "供应集采");
                }
            }
            Row row = sheet.createRow(rowIndex + 1);
            for (int colIndex = 0; colIndex < titleList.size(); colIndex++) {
                String oneTitleName = titleList.get(colIndex);
                String oneTitleKey = name2Key.get(oneTitleName);
                String value = "";
                if (StringUtils.isNotBlank(oneTitleKey)) {
                    value = oneMateriel.getString(oneTitleKey);
                }
                Cell cell = row.createCell(colIndex);
                if ("是否为问题物料".equalsIgnoreCase(oneTitleName)) {
                    if (MaterielConstant.MARK_ERROR_YES.equalsIgnoreCase(value)) {
                        value = "是";
                    } else if (MaterielConstant.MARK_ERROR_NO.equalsIgnoreCase(value)) {
                        value = "否";
                    }
                }
                cell.setCellValue(value);
            }
        }
    }

    public void convert2Upper(JSONObject oneObj) {
        String cgz = oneObj.getString("cgz");
        if (StringUtils.isNotBlank(cgz)) {
            oneObj.put("cgz", cgz.toUpperCase());
        }
        String mrplx = oneObj.getString("mrplx");
        if (StringUtils.isNotBlank(mrplx)) {
            oneObj.put("mrplx", mrplx.toUpperCase());
        }
        String mrpkzz = oneObj.getString("mrpkzz");
        if (StringUtils.isNotBlank(mrpkzz)) {
            oneObj.put("mrpkzz", mrpkzz.toUpperCase());
        }
        String kyxjc = oneObj.getString("kyxjc");
        if (StringUtils.isNotBlank(kyxjc)) {
            oneObj.put("kyxjc", kyxjc.toUpperCase());
        }
        String kcdd1 = oneObj.getString("kcdd1");
        if (StringUtils.isNotBlank(kcdd1)) {
            oneObj.put("kcdd1", kcdd1.toUpperCase());
        }
        String kcdd2 = oneObj.getString("kcdd2");
        if (StringUtils.isNotBlank(kcdd2)) {
            oneObj.put("kcdd2", kcdd2.toUpperCase());
        }
        String kcdd3 = oneObj.getString("kcdd3");
        if (StringUtils.isNotBlank(kcdd3)) {
            oneObj.put("kcdd3", kcdd3.toUpperCase());
        }
        String pldx = oneObj.getString("pldx");
        if (StringUtils.isNotBlank(pldx)) {
            oneObj.put("pldx", pldx.toUpperCase());
        }
        String cglx = oneObj.getString("cglx");
        if (StringUtils.isNotBlank(cglx)) {
            oneObj.put("cglx", cglx.toUpperCase());
        }
        String scccdd = oneObj.getString("scccdd");
        if (StringUtils.isNotBlank(scccdd)) {
            oneObj.put("scccdd", scccdd.toUpperCase());
        }
        String xlhcswj = oneObj.getString("xlhcswj");
        if (StringUtils.isNotBlank(xlhcswj)) {
            oneObj.put("xlhcswj", xlhcswj.toUpperCase());
        }
        String wlzt = oneObj.getString("wlzt");
        if (StringUtils.isNotBlank(wlzt)) {
            oneObj.put("wlzt", wlzt.toUpperCase());
        }
        String cgccdd = oneObj.getString("cgccdd");
        if (StringUtils.isNotBlank(cgccdd)) {
            oneObj.put("cgccdd", cgccdd.toUpperCase());
        }
    }

    public void convert2UpperMap(Map<String, String> oneObj) {
        String cgz = oneObj.get("cgz");
        if (StringUtils.isNotBlank(cgz)) {
            oneObj.put("cgz", cgz.toUpperCase());
        }
        String mrplx = oneObj.get("mrplx");
        if (StringUtils.isNotBlank(mrplx)) {
            oneObj.put("mrplx", mrplx.toUpperCase());
        }
        String mrpkzz = oneObj.get("mrpkzz");
        if (StringUtils.isNotBlank(mrpkzz)) {
            oneObj.put("mrpkzz", mrpkzz.toUpperCase());
        }
        String kyxjc = oneObj.get("kyxjc");
        if (StringUtils.isNotBlank(kyxjc)) {
            oneObj.put("kyxjc", kyxjc.toUpperCase());
        }
        String kcdd1 = oneObj.get("kcdd1");
        if (StringUtils.isNotBlank(kcdd1)) {
            oneObj.put("kcdd1", kcdd1.toUpperCase());
        }
        String kcdd2 = oneObj.get("kcdd2");
        if (StringUtils.isNotBlank(kcdd2)) {
            oneObj.put("kcdd2", kcdd2.toUpperCase());
        }
        String kcdd3 = oneObj.get("kcdd3");
        if (StringUtils.isNotBlank(kcdd3)) {
            oneObj.put("kcdd3", kcdd3.toUpperCase());
        }
        String pldx = oneObj.get("pldx");
        if (StringUtils.isNotBlank(pldx)) {
            oneObj.put("pldx", pldx.toUpperCase());
        }
        String cglx = oneObj.get("cglx");
        if (StringUtils.isNotBlank(cglx)) {
            oneObj.put("cglx", cglx.toUpperCase());
        }
        String scccdd = oneObj.get("scccdd");
        if (StringUtils.isNotBlank(scccdd)) {
            oneObj.put("scccdd", scccdd.toUpperCase());
        }
        String xlhcswj = oneObj.get("xlhcswj");
        if (StringUtils.isNotBlank(xlhcswj)) {
            oneObj.put("xlhcswj", xlhcswj.toUpperCase());
        }
        String wlzt = oneObj.get("wlzt");
        if (StringUtils.isNotBlank(wlzt)) {
            oneObj.put("wlzt", wlzt.toUpperCase());
        }
        String cgccdd = oneObj.get("cgccdd");
        if (StringUtils.isNotBlank(cgccdd)) {
            oneObj.put("cgccdd", cgccdd.toUpperCase());
        }
    }

}
