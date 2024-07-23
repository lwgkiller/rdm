package com.redxun.world.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aspose.cells.*;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.world.core.dao.OverseaSalesCustomizationClientDao;
import com.redxun.world.core.dao.OverseaSalesCustomizationDao;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OverseaSalesCustomizationClientService {
    private static Logger logger = LoggerFactory.getLogger(OverseaSalesCustomizationClientService.class);
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private OverseaSalesCustomizationClientDao overseaSalesCustomizationClientDao;
    @Autowired
    private OverseaSalesCustomizationDao overseaSalesCustomizationDao;

    //..
    public JSONArray productGroupQuery(HttpServletRequest request, HttpServletResponse response) {
        JSONArray jsonArray = new JSONArray();
        JSONObject title = new JSONObject().fluentPut("id", "title").fluentPut("text", "Product Group");
        jsonArray.add(title);
        List<SysDic> sysDicList = sysDicManager.getByTreeKey("overseaSalesCustomizationProductGroup");
        for (SysDic sysDic : sysDicList) {
            JSONObject jsonObject = new JSONObject().fluentPut("id", sysDic.getKey()).
                    fluentPut("text", sysDic.getValue()).fluentPut("pid", "title");
            jsonArray.fluentAdd(jsonObject);
        }
        return jsonArray;
    }

    //..
    public JSONArray modelQueryByGroup(HttpServletRequest request, HttpServletResponse response) {
        String productGroup = RequestUtil.getString(request, "productGroup");
        JSONArray jsonArray = new JSONArray();
        JSONObject params = new JSONObject();
        params.put("productGroup", productGroup);
        if (!ContextUtil.getCurrentUser().getUserNo().equalsIgnoreCase("admin")) {
            params.put("groupId", ContextUtil.getCurrentUser().getMainGroupId());
        }
        params.put("sortField", "orderNo");
        params.put("sortOrder", "asc");
        List<JSONObject> modelList = overseaSalesCustomizationDao.modelListQuery(params);
        for (JSONObject model : modelList) {
            JSONObject jsonObject = new JSONObject()
                    .fluentPut("id", model.getString("id"))
                    .fluentPut("salsesModel", model.getString("salsesModel"))
                    .fluentPut("fileName", model.getString("fileName"));
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    //..
    public List<JSONObject> clientTreeQuery(HttpServletRequest request, HttpServletResponse response) {
        String modelId = RequestUtil.getString(request, "modelId");
        String clientGroupId = ContextUtil.getCurrentUser().getMainGroupId();
        JSONObject params = new JSONObject();
        params.put("modelId", modelId);
        params.put("clientGroupId", clientGroupId);
        List<JSONObject> businessList = overseaSalesCustomizationClientDao.clientTreeQuery(params);
        List<String> businessIds = new ArrayList<>();
        Map<String, JSONObject> idToNode = new LinkedHashMap<>();
        Set<String> parentNodeIds = new HashSet<>();
        Set<String> hasFileNodeIds = new HashSet<>();
        JSONObject basic = new JSONObject();
        //初始化节点库
        for (JSONObject node : businessList) {
            if (node.containsKey("nodeType") && node.getString("nodeType").
                    equalsIgnoreCase(OverseaSalesCustomizationService.overseaSalesCustomizationNodeType.BASIC)) {
                basic = node;//记录基础包节点
            }
            idToNode.put(node.getString("id"), node);
            businessIds.add(node.getString("id"));
        }
        //将有文件的nodeid缓存起来
        params.clear();
        params.put("businessIds", businessIds);
        List<JSONObject> fileListInfos = overseaSalesCustomizationDao.getFileListInfos(params);
        for (JSONObject fileListInfo : fileListInfos) {
            hasFileNodeIds.add(fileListInfo.getString("businessId"));
        }
        //初始化所有父节点id
        idToNode.forEach((key, value) -> {
            if (value.containsKey("PARENT_ID_") && StringUtil.isNotEmpty(value.getString("PARENT_ID_"))) {
                parentNodeIds.add(value.getString("PARENT_ID_"));
            }
            if (hasFileNodeIds.contains(key)) {
                value.put("isHasFile", "true");
            } else {
                value.put("isHasFile", "false");
            }
        });
        //清洗节点库
        for (JSONObject node : businessList) {
            if (node.containsKey("nodeType") && node.getString("nodeType").
                    equalsIgnoreCase(OverseaSalesCustomizationService.overseaSalesCustomizationNodeType.BASIC_XUJIEDIAN)) {
                //清除基础虚节点
                idToNode.remove(node.getString("id"));
            } else if (node.containsKey("nodeType") && node.getString("nodeType").
                    equalsIgnoreCase(OverseaSalesCustomizationService.overseaSalesCustomizationNodeType.BASIC_JIEDIAN)) {
                //基础节点父节点转为basic
                node.put("PARENT_ID_", basic.getString("id"));
            } else if (node.containsKey("nodeType") && node.getString("nodeType").
                    equalsIgnoreCase(OverseaSalesCustomizationService.overseaSalesCustomizationNodeType.CUSTOM_XUJIEDIAN)) {
                //清除没有子节点的选配虚节点
                if (!parentNodeIds.contains(node.getString("id"))) {
                    idToNode.remove(node.getString("id"));
                }
            }
        }
        //将节点库转换为list返回
        return idToNode.values().stream().collect(Collectors.toList());
    }

    //..
    public JsonResult checkAndIniInst(HttpServletRequest request) throws Exception {
        JsonResult jsonResult = new JsonResult(true, "操作成功！");
        String modelId = RequestUtil.getString(request, "modelId");
        String clientGroupId = RequestUtil.getString(request, "clientGroupId");
        JSONObject params = new JSONObject().fluentPut("modelId", modelId).fluentPut("clientGroupId", clientGroupId);
        List<JSONObject> instList = overseaSalesCustomizationClientDao.clientInstQuery(params);
        if (instList.size() > 1) {
            jsonResult.setSuccess(false);
            jsonResult.setMessage("当前型号用户实例超过两个，请联系管理员处理！");
        } else if (instList.isEmpty()) {
            JSONObject inst = new JSONObject();
            inst.put("id", IdUtil.getId());
            inst.put("modelId", modelId);
            inst.put("clientGroupId", clientGroupId);
            JSONArray jsonArray = new JSONArray();//初始化specialNeedsDesc
            jsonArray.add(new JSONObject().fluentPut("currencySymbol", ""));//初始化specialNeedsDesc
            inst.put("specialNeedsDesc", jsonArray.toJSONString());//初始化specialNeedsDesc
            inst.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            inst.put("CREATE_TIME_", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            overseaSalesCustomizationClientDao.insertClientInst(inst);
            jsonResult.setData(inst);
        } else if (instList.size() == 1) {
            jsonResult.setData(instList.get(0));
        }
        return jsonResult;
    }

    //..
    public JsonResult saveClientInst(JSONObject jsonObject) throws Exception {
        JsonResult result = new JsonResult(true, "数据保存成功！");
        String instId = jsonObject.getString("instId");
        //..
        JSONArray specialNeedsDesc = new JSONArray();
        if (jsonObject.containsKey("specialNeedsDesc")
                && StringUtil.isNotEmpty(jsonObject.getString("specialNeedsDesc"))) {
            JSONArray specialNeedsDescItems = jsonObject.getJSONArray("specialNeedsDesc");
            for (Object itemDataObject : specialNeedsDescItems) {
                JSONObject itemDataJson = (JSONObject) itemDataObject;
                if (itemDataJson.containsKey("_state")) {//处理新增和修改
                    if (itemDataJson.getString("_state").equalsIgnoreCase("added")) {
                        itemDataJson.remove("_state");
                        itemDataJson.remove("_id");
                        itemDataJson.remove("_uid");
                        specialNeedsDesc.add(itemDataJson);
                    } else if (itemDataJson.getString("_state").equalsIgnoreCase("modified")) {
                        itemDataJson.remove("_state");
                        itemDataJson.remove("_id");
                        itemDataJson.remove("_uid");
                        specialNeedsDesc.add(itemDataJson);
                    }
                } else {//处理没变化的
                    itemDataJson.remove("_state");
                    itemDataJson.remove("_id");
                    itemDataJson.remove("_uid");
                    specialNeedsDesc.add(itemDataJson);
                }
            }
        }
        //..
        JSONArray configinsts = jsonObject.getJSONArray("configinsts");
        overseaSalesCustomizationClientDao.updateClientInst(new JSONObject()
                .fluentPut("id", instId).fluentPut("specialNeedsDesc", specialNeedsDesc.toString()));
        for (int i = 0; i < configinsts.size(); i++) {
            JSONObject oneObject = configinsts.getJSONObject(i);
            if (!oneObject.containsKey("configinstId") || StringUtil.isEmpty(oneObject.getString("configinstId"))) {
                overseaSalesCustomizationClientDao.insertClientConfigInst(new JSONObject()
                        .fluentPut("id", IdUtil.getId())
                        .fluentPut("instId", instId)
                        .fluentPut("configId", oneObject.getString("id"))
                        .fluentPut("salesPrice", oneObject.getString("salesPrice"))
                        .fluentPut("CREATE_BY_", ContextUtil.getCurrentUserId())
                        .fluentPut("CREATE_TIME_", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss")));
            } else {
                overseaSalesCustomizationClientDao.updateClientConfigInst(new JSONObject()
                        .fluentPut("id", oneObject.getString("configinstId"))
                        .fluentPut("salesPrice", oneObject.getString("salesPrice"))
                        .fluentPut("UPDATE_BY_", ContextUtil.getCurrentUserId())
                        .fluentPut("UPDATE_TIME_", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss")));
            }
        }
        return result;
    }

    //..
    public JsonResult doCreateReport(JSONObject jsonObject) throws Exception {
        JsonResult result = new JsonResult(true, "生成成功！");
        //获取模板的workbook
        Workbook workbook = this.getWorkbook();
        //获取模板的worksheet
        Worksheet worksheet = this.getWorksheet(workbook);
        //需要动态调整位置的关键位置坐标初始化
        Map<String, Point> keyPositionsMap = this.getKeyPositionsMap(worksheet);
        //写值处理
        this.reWriteWorksheet(worksheet, keyPositionsMap, jsonObject);
        worksheet.autoFitRows(6, 6 + jsonObject.getJSONArray("nodesToReport").size());
        //存到业务文件区域
        this.saveWorksheet(workbook, jsonObject);
        return result;
    }

    //获取模板的workbook
    private Workbook getWorkbook() throws Exception {
        String fileName = "overseaSalesCustomization.xlsx";
        URL url = OverseaSalesCustomizationClientService.class.getClassLoader().
                getResource("templates/world/" + fileName);
        String path = url.getPath();
        Workbook workbook = new Workbook(path);
        return workbook;
    }

    //获取模板的worksheet
    private Worksheet getWorksheet(Workbook workbook) throws Exception {
        WorksheetCollection worksheets = workbook.getWorksheets();
        Worksheet worksheet = worksheets.get("template");
        return worksheet;
    }

    //需要动态调整位置的关键位置坐标初始化
    private Map<String, Point> getKeyPositionsMap(Worksheet worksheet) {
        Cells cells = worksheet.getCells();
        Map<String, Point> keyPositionsMap = new HashedMap();
        keyPositionsMap.put("P", new Point());
        keyPositionsMap.put("salsesModel", new Point());
        keyPositionsMap.put("engine", new Point());
        keyPositionsMap.put("ratedPower", new Point());
        keyPositionsMap.put("bucketCapacity", new Point());
        keyPositionsMap.put("operatingMass", new Point());
        keyPositionsMap.put("list", new Point());
        keyPositionsMap.put("totalPrice", new Point());
        for (Object cell : cells) {
            Cell cellForXY = (Cell) cell;
            switch (cellForXY.getStringValue()) {
                case "{{P}}":
                    keyPositionsMap.get("P").setLocation(cellForXY.getRow(), cellForXY.getColumn());
                    break;
                case "{{salsesModel}}":
                    keyPositionsMap.get("salsesModel").setLocation(cellForXY.getRow(), cellForXY.getColumn());
                    break;
                case "{{engine}}":
                    keyPositionsMap.get("engine").setLocation(cellForXY.getRow(), cellForXY.getColumn());
                    break;
                case "{{ratedPower}}":
                    keyPositionsMap.get("ratedPower").setLocation(cellForXY.getRow(), cellForXY.getColumn());
                    break;
                case "{{bucketCapacity}}":
                    keyPositionsMap.get("bucketCapacity").setLocation(cellForXY.getRow(), cellForXY.getColumn());
                    break;
                case "{{operatingMass}}":
                    keyPositionsMap.get("operatingMass").setLocation(cellForXY.getRow(), cellForXY.getColumn());
                    break;
                case "{{list}}":
                    keyPositionsMap.get("list").setLocation(cellForXY.getRow(), cellForXY.getColumn());
                    break;
                case "{{totalPrice}}":
                    keyPositionsMap.get("totalPrice").setLocation(cellForXY.getRow(), cellForXY.getColumn());
                    break;
                default:
            }
        }
        return keyPositionsMap;
    }

    //写值处理
    private void reWriteWorksheet(Worksheet worksheet, Map<String, Point> keyPositionsMap,
                                  JSONObject jsonObject) throws Exception {
        Cells cells = worksheet.getCells();
        Iterator iterator = cells.iterator();
        while (iterator.hasNext()) {
            Cell cell = (Cell) iterator.next();
            switch (cell.getStringValue()) {
                case "{{P}}":
                    this.reWritePicture(cell, worksheet, jsonObject);
                    break;
                case "{{salsesModel}}":
                    cell.putValue(jsonObject.getJSONObject("model").getString("salsesModel"));
                    break;
                case "{{engine}}":
                    cell.putValue("Engine: " + jsonObject.getJSONObject("model").getString("engine"));
                    break;
                case "{{ratedPower}}":
                    cell.putValue("Rated power: " + jsonObject.getJSONObject("model").getString("ratedPower"));
                    break;
                case "{{bucketCapacity}}":
                    cell.putValue("Bucket capacity: " + jsonObject.getJSONObject("model").getString("bucketCapacity"));
                    break;
                case "{{operatingMass}}":
                    cell.putValue("Operating mass: " + jsonObject.getJSONObject("model").getString("operatingMass"));
                    break;
                case "{{totalPrice}}":
                    cell.putValue("Total price: $" + jsonObject.getString("totalPrice"));
                    break;
                case "{{list}}":
                    JSONArray nodesToReport = jsonObject.getJSONArray("nodesToReport");
                    for (int i = 0; i < nodesToReport.size(); i++) {
                        JSONObject node = nodesToReport.getJSONObject(i);
                        //cells.merge(keyPositionsMap.get("list").x + i, keyPositionsMap.get("list").y, 1, 2);
                        Cell dCell = cells.get(keyPositionsMap.get("list").x + i, keyPositionsMap.get("list").y);
                        Style style = dCell.getStyle();
                        style.setTextWrapped(true);
                        dCell.setStyle(style);
                        if (node.getString("nodeType").equalsIgnoreCase("basic") ||
                                node.getString("nodeType").equalsIgnoreCase("customXuJieDian") ||
                                node.getString("nodeType").equalsIgnoreCase("specialNeedsDescTitle")) {
                            dCell.putValue("●" + node.getString("nodeName"));
                        } else if (node.getString("nodeType").equalsIgnoreCase("specialNeedsDesc")) {
                            dCell.putValue(node.getString("nodeName"));
                        } else {
                            dCell.putValue("..." + node.getString("nodeName"));
                        }
                        jsonObject.put("lastCellRowNum", keyPositionsMap.get("list").x + i + 1);
                    }
                    break;
                default:
            }
        }
    }

    //写图片
    private void reWritePicture(Cell cell, Worksheet worksheet, JSONObject jsonObject) throws Exception {
        JSONObject params = new JSONObject();
        String fileBasePath = "";
        String fileFullPath = "";
        int pictureIndex = 0;
        Picture picture = null;
        cell.putValue("");//清空图片标识
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("worldUploadPosition",
                "overseaSalesCustomizationProductPic").getValue();
        String suffix = CommonFuns.toGetFileSuffix(jsonObject.getJSONObject("model").getString("fileName"));
        fileFullPath = filePathBase + File.separator + jsonObject.getJSONObject("model").getString("id") + "." + suffix;
        File file = new File(fileFullPath);
        if (file.exists()) {
            pictureIndex = worksheet.getPictures().add(cell.getRow(), cell.getColumn(), fileFullPath);
            picture = worksheet.getPictures().get(pictureIndex);
            picture.setWidth(200);
            picture.setHeight(150);
        }
    }

    //存到业务文件区域
    private void saveWorksheet(Workbook workbook, JSONObject jsonObject) throws Exception {
        String fileBasePath = sysDicManager.getBySysTreeKeyAndDicKey("worldUploadPosition",
                "overseaSalesCustomizationClientReport").getValue();
        String fileDirPath = fileBasePath + File.separator + jsonObject.getString("instId");
        File dirFile = new File(fileDirPath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        String fileFullPath = fileBasePath + File.separator + jsonObject.getString("instId")
                + File.separator + jsonObject.getJSONObject("model").getString("salsesModel");
        if (jsonObject.getString("type").equalsIgnoreCase("excel")) {
            fileFullPath = fileFullPath + ".xlsx";
        } else if (jsonObject.getString("type").equalsIgnoreCase("pdf")) {
            fileFullPath = fileFullPath + ".pdf";
        }

        File file = new File(fileFullPath);
        if (file.exists()) {
            file.delete();
        }

        if (jsonObject.getString("type").equalsIgnoreCase("excel")) {
            workbook.save(fileFullPath);
        } else if (jsonObject.getString("type").equalsIgnoreCase("pdf")) {
            WorksheetCollection worksheets = workbook.getWorksheets();
            Worksheet worksheet = worksheets.get("template");
            PageSetup pageSetup = worksheet.getPageSetup();
            pageSetup.setPrintArea("A1:B" + jsonObject.getString("lastCellRowNum"));
            PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
            pdfSaveOptions.setOnePagePerSheet(false);
            workbook.save(fileFullPath, pdfSaveOptions);
        }
    }
}
