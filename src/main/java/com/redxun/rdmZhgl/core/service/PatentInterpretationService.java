package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.dao.PatentInterpretationDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.manager.SysSeqIdManager;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Service
public class PatentInterpretationService {
    private static Logger logger = LoggerFactory.getLogger(PatentInterpretationService.class);
    @Autowired
    private PatentInterpretationDao patentInterpretationDao;
    @Autowired
    private PatentInterpretationTechnologyBranchService patentInterpretationTechnologyBranchService;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    SysSeqIdManager sysSeqIdManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = patentInterpretationDao.dataListQuery(params);
        for (JSONObject business : businessList) {
            if (business.get("CREATE_TIME_") != null) {
                business.put("CREATE_TIME_", DateUtil.formatDate((Date) business.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        // 查询当前处理人--会签不用这个
        //xcmgProjectManager.setTaskCurrentUser(businessList);
        rdmZhglUtil.setTaskInfo2Data(businessList, ContextUtil.getCurrentUserId());
        int businessListCount = patentInterpretationDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    private void getListParams(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "businessStatus");
            params.put("sortOrder", "asc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
//                    if ("communicateStartTime".equalsIgnoreCase(name)) {
//                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
//                    }
//                    if ("communicateEndTime".equalsIgnoreCase(name)) {
//                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
//                    }
                    params.put(name, value);
                }
            }
        }

        // 增加分页条件
        params.put("startIndex", 0);
        params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
    }

    //..
    public JsonResult deleteBusiness(String[] ids, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        List<JSONObject> files = getBusinessFileList(businessIds);
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "zhglUploadPosition", "patentInterpretation").getValue();
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("mainId"), filePathBase);
        }
        for (String oneBusinessId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneBusinessId, filePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        patentInterpretationDao.deleteBusinessFile(param);
        patentInterpretationDao.deleteBusiness(param);
        patentInterpretationDao.deleteBusinessItem(param);
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }

    //..
    public List<JSONObject> getBusinessFileList(List<String> businessIdList) {
        List<JSONObject> businessFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIdList);
        businessFileList = patentInterpretationDao.queryFileList(param);
        return businessFileList;
    }

    //..
    public void importExcel(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile fileObj = multipartRequest.getFile("importFile");
            if (fileObj == null) {
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            String fileName = ((CommonsMultipartFile) fileObj).getFileItem().getName();
            ((CommonsMultipartFile) fileObj).getFileItem().getName().endsWith(ExcelReaderUtil.EXCEL03_EXTENSION);
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
            String id = IdUtil.getId();
            for (int i = 2; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRow(row, itemList, titleList, id);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            //查重2022-06-29
            List<JSONObject> listAlready = patentInterpretationDao.dataListQuery(new JSONObject());
            Set<String> set = new HashSet<>();
            for (JSONObject jsonObject : listAlready) {
                set.add(jsonObject.getString("patentPublicationNo"));
            }
            for (Map<String, Object> map : itemList) {
                if (set.contains(map.get("patentPublicationNo").toString())) {
                    result.put("message", "已经存在重复的专利公开号：" + map.get("patentPublicationNo").toString());
                    return;
                }
            }
            //以上查重
            for (int i = 0; i < itemList.size(); i++) {
                Map<String, Object> map = itemList.get(i);
                String json = JSON.toJSONString(map);
                JSONObject jsonObject = JSON.parseObject(json);
                jsonObject.put("businessStatus", "A-editing");
                patentInterpretationDao.insertBusiness(jsonObject);
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importExcel", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    //..
    private JSONObject generateDataFromRow(Row row, List<Map<String, Object>> itemList, List<String> titleList, String mainId) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = ExcelUtil.getCellFormatValue(cell);
            }
            switch (title) {
                case "专业类别":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "专业类别为空");
                        return oneRowCheck;
                    }
                    if (StringUtils.isNotBlank(cellValue)) {
                        String sysDicCheckResult = sysDicCheck(cellValue, "professionalCategory");
                        if (!sysDicCheckResult.equalsIgnoreCase("ok")) {
                            oneRowCheck.put("message", "专业类别别必须为字典要求的值:" + sysDicCheckResult);
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("professionalCategory", cellValue);
                    break;
                case "专利公开号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "专利公开号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("patentPublicationNo", cellValue);
                    break;
                case "专利名称":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "专利名称为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("patentName", cellValue);
                    break;
                case "申请日":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "申请日为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("applicationDate", cellValue.substring(0, 10));
                    break;
                case "公开日":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "公开日为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("openDate", cellValue.substring(0, 10));
                    break;
                case "IPC分类号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "IPC分类号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("IPCNo", cellValue);
                    break;
                case "IPC主分类号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "IPC主分类号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("IPCMainNo", cellValue);
                    break;
                case "专利申请人":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "专利申请人为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("patentApplicant", cellValue);
                    break;
                case "解读完成日期":
                    oneRowMap.put("interpretationCompletionDate", cellValue);
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("id", IdUtil.getId());
        oneRowMap.put("applyUserId", ContextUtil.getCurrentUserId());
        oneRowMap.put("applyUser", ContextUtil.getCurrentUser().getFullname());
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    //..
    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "专利解读导入模板.xlsx";
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

    //..
    public JSONObject getDetail(String businessId) {
        JSONObject jsonObject = patentInterpretationDao.queryDetailById(businessId);
        if (jsonObject == null) {
            return new JSONObject();
        }
        return jsonObject;
    }

    //..applyUser自动生成
    public void createBusiness(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("applyUserId", ContextUtil.getCurrentUserId());
        formData.put("applyUser", ContextUtil.getCurrentUser().getFullname());
        //@lwgkiller:此处是因为(草稿状态和空状态)无节点，提交后首节点会跳过，因此默认将首节点（编制中）的编号进行初始化写入
        formData.put("businessStatus", "A-editing");
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        patentInterpretationDao.insertBusiness(formData);
    }

    //..
    public void updateBusiness(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        patentInterpretationDao.updateBusiness(formData);
    }

    //..
    public void deleteOneBusinessFile(String fileId, String fileName, String businessId) {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "zhglUploadPosition", "patentInterpretation").getValue();
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, businessId, filePathBase);
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        patentInterpretationDao.deleteBusinessFile(param);
    }

    //..
    public List<JSONObject> getFileList(List<String> businessIdList) {
        List<JSONObject> fileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIdList);
        fileList = patentInterpretationDao.queryFileList(param);
        return fileList;
    }

    //..
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
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "zhglUploadPosition", "patentInterpretation").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find FilePathBase");
            return;
        }
        try {
            String businessId = toGetParamVal(parameters.get("businessId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + businessId;
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
            fileInfo.put("mainId", businessId);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            patentInterpretationDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    //..
    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    //..
    public List<JSONObject> getItemList(String businessId) {
        List<JSONObject> itemList = patentInterpretationDao.queryItemList(businessId);
        return itemList;
    }

    //..
    public JsonResult deleteItem(String id) {
        JsonResult result = new JsonResult(true, "删除成功！");
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        patentInterpretationDao.deleteBusinessItem(param);
        return result;
    }

    //..
    public JSONObject getItemDetail(String businessId) {
        JSONObject jsonObject = patentInterpretationDao.queryItemDetailById(businessId);
        if (jsonObject == null) {
            return new JSONObject();
        }
        return jsonObject;
    }

    //..
    public void saveItem(JsonResult result, String itemDataStr) {
        JSONObject formDataJson = JSONObject.parseObject(itemDataStr);
        if (StringUtils.isBlank(formDataJson.getString("id"))) {
            formDataJson.put("id", IdUtil.getId());
            formDataJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            formDataJson.put("CREATE_TIME_", new Date());
            patentInterpretationDao.insertItem(formDataJson);
        } else {
            formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            formDataJson.put("UPDATE_TIME_", new Date());
            patentInterpretationDao.updateItem(formDataJson);
        }
        result.setData(formDataJson.getString("id"));
    }

    //..检查字典值
    private String sysDicCheck(String cellValue, String treeKey) {
        List<SysDic> sysDicList = sysDicManager.getByTreeKey(treeKey);
        boolean isok = false;
        StringBuilder stringBuilder = new StringBuilder();
        for (SysDic sysDic : sysDicList) {
            if (cellValue.equalsIgnoreCase(sysDic.getName())) {
                isok = true;
            }
            stringBuilder.append(sysDic.getName()).append(",");
        }
        if (isok == false) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            return stringBuilder.toString();
        } else {
            return "ok";
        }
    }

    //..查重2022-06-29
    public JsonResult isAlreadyHave(String patentPublicationNo, String id) {
        JsonResult result = new JsonResult(true, "专利公开号重复校验通过！");
        List<JSONObject> listAlready = patentInterpretationDao.dataListQuery(new JSONObject());
        List<JSONObject> listAlreadySame = new ArrayList<>();
        for (JSONObject jsonObject : listAlready) {
            if (jsonObject.getString("patentPublicationNo").equalsIgnoreCase(patentPublicationNo)) {
                listAlreadySame.add(jsonObject);
            }
        }
        //如果是新增且库中已经存在相同的patentPublicationNo，就重复了
        if (listAlreadySame.size() > 0 && id.equalsIgnoreCase("")) {
            result.setSuccess(false);
            result.setMessage("已经存在重复的专利公开号：" + patentPublicationNo);
        }
        //如果库中已经存在相同的patentPublicationNo，走到这肯定是修改了
        for (JSONObject jsonObject : listAlreadySame) {
            //那就看看一旦有一个和待修改记录patentPublicationNo相同的记录和待修改的记录不是同一个记录，那就证明重复了
            if (!jsonObject.getString("id").equalsIgnoreCase(id)) {
                result.setSuccess(false);
                result.setMessage("已经存在重复的专利公开号：" + patentPublicationNo);
                break;
            }
        }
        return result;
    }

    //..校验一下技术分支和专业类别是否匹配2022-09-17
    public JsonResult isBranchOk(String technologyBranchId, String id) {
        JsonResult result = new JsonResult(true, "技术分支和专业类别校验通过！");
        JSONObject detail = patentInterpretationDao.queryDetailById(id);
        JSONObject technologyBranchRoot =
                patentInterpretationTechnologyBranchService.queryRootDataById(technologyBranchId);
        if (!technologyBranchRoot.getString("description").contains(detail.getString("professionalCategory"))) {
            result.setSuccess(false);
            result.setMessage("技术分支和专业类别不匹配，请选择和专业类别同类型下的技术分支");
        }
        return result;
    }
}
