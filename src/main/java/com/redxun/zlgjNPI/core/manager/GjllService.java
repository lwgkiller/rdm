package com.redxun.zlgjNPI.core.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.zlgjNPI.core.dao.GjllDao;
import groovy.util.logging.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Service
@Slf4j
public class GjllService {
    private Logger logger = LogManager.getLogger(GjllService.class);
    @Autowired
    private GjllDao gjllDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;


    public JsonPageResult<?> queryGjll(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
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
            params.put("sortOrder", "desc");
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
        // 增加分页条件
        List<JSONObject> gjllList = gjllDao.queryGjll(params);
        for (JSONObject gjll : gjllList) {
            if (gjll.getDate("CREATE_TIME_") != null) {
                gjll.put("CREATE_TIME_", DateUtil.formatDate(gjll.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (gjll.getDate("qhTime") != null) {
                gjll.put("qhTime", DateUtil.formatDate(gjll.getDate("qhTime"), "yyyy-MM-dd"));
            }
        }
        result.setData(gjllList);
        int countQbgzDataList = gjllDao.countGjllList(params);
        result.setTotal(countQbgzDataList);
        return result;
    }
    
    public void insertGjll(JSONObject formData) {
        formData.put("gjId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        if(StringUtils.isBlank(formData.getString("qhTime"))) {
            formData.put("qhTime", null);
        }
        gjllDao.autoCreateLl(formData);
    }

    public void updateGjll(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        if(StringUtils.isBlank(formData.getString("qhTime"))) {
            formData.put("qhTime", null);
        }
        gjllDao.updateGjll(formData);
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }
    public void saveGjllUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("gjllFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find gjllFilePathBase");
            return;
        }
        try {
            String belongId = toGetParamVal(parameters.get("gjId"));
            String fileId = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + belongId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + fileId + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);

            // 写入数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("fileId", fileId);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("belongId", belongId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            gjllDao.autoCreateLlFile(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getGjllDetail(String standardId) {
        JSONObject detailObj = gjllDao.queryGjllById(standardId);
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.getDate("qhTime") != null) {
            detailObj.put("qhTime", DateUtil.formatDate(detailObj.getDate("qhTime"), "yyyy-MM-dd"));
        }
        return detailObj;
    }

    public List<JSONObject> getGjllFileList(List<String> gjIdList) {
        List<JSONObject> gjllFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("gjIds", gjIdList);
        gjllFileList = gjllDao.queryGjllFileList(param);
        return gjllFileList;
    }

    public void deleteOneGjllFile(String fileId, String fileName, String gjId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, gjId, WebAppUtil.getProperty("gjllFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("fileId", fileId);
        gjllDao.deleteGjllFile(param);
    }

    public JsonResult deleteGjll(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> gjIds = Arrays.asList(ids);
        List<JSONObject> files = getGjllFileList(gjIds);
        String gjllFilePathBase = WebAppUtil.getProperty("gjllFilePathBase");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"),
                    oneFile.getString("gjId"), gjllFilePathBase);
        }
        for (String onegjll : ids) {
            rdmZhglFileManager.deleteDirFromDisk(onegjll, gjllFilePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("gjIds", gjIds);
        gjllDao.deleteGjllFile(param);
        gjllDao.deleteGjll(param);
        return result;
    }
    public void exportGjllList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = queryGjll(request, response, false);
        List<Map<String, Object>> listData = result.getData();
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "质量改进履历";
        String excelName = nowDate + title;
        String[] fieldNames = {"机型类别", "机型", "故障零件", "问题详细描述", "长期措施",
                "通知单号", "预计切换车号", "零部件供应商","实际完成时间", "责任人","所属部门"};
        String[] fieldCodes = {"jiXing", "smallJiXing", "gzlj", "wtms", "cqcs","tzdh",
                "yjqhch", "lbjgys", "qhTime","zrrName", "ssbmName"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
