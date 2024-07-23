package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.DecorationManualTopicDao;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.manager.SysSeqIdManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
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

import static com.redxun.rdmCommon.core.util.RdmCommonUtil.toGetParamVal;

@Service
public class DecorationManualTopicService {
    private static Logger logger = LoggerFactory.getLogger(DecorationManualTopicService.class);
    public static final String MANUAL_STATUS_EDITING = "编辑中";
    public static final String MANUAL_STATUS_READY = "可转出";
    public static final String MANUAL_STATUS_REVISION = "历史版本";
    @Autowired
    private DecorationManualTopicDao decorationManualTopicDao;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private SysSeqIdManager sysSeqIdManager;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = null;
        int businessListCount = 0;
        businessList = decorationManualTopicDao.dataListQuery(params);
        businessListCount = decorationManualTopicDao.countDataListQuery(params);
        for (JSONObject business : businessList) {
            List<String> businessids = new ArrayList<>();
            businessids.add(business.getString("id"));
            if (this.getFileList(businessids).size() > 0) {
                business.put("isExistFile", "true");
            } else {
                business.put("isExistFile", "false");
            }
        }
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
            params.put("sortField", "businessNo");
            params.put("sortOrder", "asc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    //数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
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
//        params.put("startIndex", 0);
//        params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
    }

    //..
    public JSONObject queryDataById(String id) {
        JSONObject jsonObject = decorationManualTopicDao.queryDataById(id);
        return jsonObject;
    }

    //..
    public void deleteBusiness(String id) {
        try {
            List<String> businessIds = new ArrayList<>();
            businessIds.add(id);
            JSONObject param = new JSONObject();
            param.put("businessIds", businessIds);
            List<JSONObject> files = getFileList(businessIds);
            decorationManualTopicDao.deleteBusinessFile(param);
            decorationManualTopicDao.deleteBusiness(id);
            String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                    "serviceEngineeringUploadPosition", "decorationManualTopic").getValue();
            for (JSONObject oneFile : files) {
                rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"),
                        oneFile.getString("fileName"), oneFile.getString("mainId"), filePathBase);
            }
            for (String oneBusinessId : businessIds) {
                rdmZhglFileManager.deleteDirFromDisk(id, filePathBase);
            }
            deleteFileFromDisk(id);
        } catch (Exception e) {
            logger.error("Exception in delete", e);
            throw e;
        }
    }

    //..
    private void deleteFileFromDisk(String id) {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "decorationManualTopic").getValue();
        // 处理下载目录的删除
        String fileFullPath = filePathBase + File.separator + id + ".pdf";
        File file = new File(fileFullPath);
        file.delete();
    }

    //..
    public void doReleaseBusiness(String id) {
        try {
            //找编号相同的手册，将它们的状态都变为历史版本；将本条状态变为可转出
            JSONObject jsonObject = decorationManualTopicDao.queryDataById(id);
            JSONObject param = new JSONObject();
            param.put("businessNo", jsonObject.getString("businessNo"));
            List<JSONObject> jsonObjects = decorationManualTopicDao.dataListQuery(param);
            for (JSONObject decorationManualTopic : jsonObjects) {
                if (decorationManualTopic.getString("manualStatus").equalsIgnoreCase(this.MANUAL_STATUS_READY)) {
                    decorationManualTopic.put("manualStatus", this.MANUAL_STATUS_REVISION);
                    decorationManualTopic.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    decorationManualTopic.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    decorationManualTopicDao.updateBusiness(decorationManualTopic);
                }
            }
            jsonObject.put("manualStatus", this.MANUAL_STATUS_READY);
            decorationManualTopicDao.updateBusiness(jsonObject);
        } catch (Exception e) {
            throw e;
        }
    }

    //..trycatch抛出试验
    public String addBusiness(JSONObject formData) {
        try {
            formData.put("id", IdUtil.getId());
            formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            formData.put("CREATE_TIME_", new Date());
            if (StringUtil.isEmpty(formData.getString("businessNo"))) {
                formData.put("businessNo", sysSeqIdManager.genSequenceNo("decorationManualTopic", "1"));
            }
            decorationManualTopicDao.insertBusiness(formData);
            return formData.getString("id");
        } catch (Exception e) {
            throw e;
            //return "";事实证明，只要在这里不抛出，而是做了返回，事务控制就会失效，
            //好多这么直接返回JsonResult或者直接给输入参数赋值的场景,我也用过，绝对是一种错误写法！要注意!
        }
    }

    //..throws抛出试验,直接抛出也是可以进行事务控制的
    public void saveBusiness(JSONObject formData) throws Exception {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        //基本信息
        decorationManualTopicDao.updateBusiness(formData);
    }

    //..
    public void exportList(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> params = RequestUtil.getParameterValueMap(request, true);
        JsonPageResult result = dataListQuery(request, response);
        List<JSONObject> listData = result.getData();
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "装修手册Topic";
        String excelName = nowDate + title;
        String[] fieldNames = {"编号", "章节", "系统", "Topic编码", "Topic名称", "Topic类别",
                "关联物料编码", "产品系列", "销售区域", "销售型号", "设计型号", "备注",
                "版本", "版本状态", "创建人", "创建时间"};
        String[] fieldCodes = {"businessNo", "chapter", "system", "topicCode", "topicName", "topicType",
                "materialCode", "productSerie", "salesArea", "salesModel", "designModel", "remark",
                "manualVersion", "manualStatus", "creatorName", "createTime"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    //..
    public void saveUploadFiles(HttpServletRequest request) throws Exception {
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
                "serviceEngineeringUploadPosition", "decorationManualTopic").getValue();
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

            // 写入数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("mainId", businessId);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            decorationManualTopicDao.addFileInfos(fileInfo);

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
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
            throw e;
        }
    }

    //..
    public List<JSONObject> getFileList(List<String> businessIdList) {
        List<JSONObject> fileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIdList);
        fileList = decorationManualTopicDao.queryFileList(param);
        return fileList;
    }

    //..throws抛出试验
    public void deleteOneBusinessFile(String fileId, String fileName, String businessId) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        decorationManualTopicDao.deleteBusinessFile(param);
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "decorationManualTopic").getValue();
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, businessId, filePathBase);
    }
}
