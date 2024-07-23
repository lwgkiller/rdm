package com.redxun.environment.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.environment.core.dao.ClhbDao;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import groovy.util.logging.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class ClhbService {
    private Logger logger = LogManager.getLogger(ClhbService.class);
    @Autowired
    private ClhbDao clhbDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private BpmInstManager bpmInstManager;

    public JsonPageResult<?> queryWj(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
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
                params.put(name, value);

            }
        }
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        params.put("currentUserId", ContextUtil.getCurrentUserId());
        // 增加分页条件
        List<Map<String, Object>> wjList = clhbDao.queryWj(params);
        for (Map<String, Object> wj : wjList) {
            if (wj.get("CREATE_TIME_") != null) {
                wj.put("CREATE_TIME_", DateUtil.formatDate((Date) wj.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        xcmgProjectManager.setTaskCurrentUser(wjList);
        result.setData(wjList);
        int countQbgzDataList = clhbDao.countWjList(params);
        result.setTotal(countQbgzDataList);
        return result;
    }

    public void insertWj(JSONObject formData) {
        formData.put("wjId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        clhbDao.insertWj(formData);
    }

    public void updateWj(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        clhbDao.updateWj(formData);
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    public void saveWjUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("wjFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find wjFilePathBase");
            return;
        }
        try {
            String belongId = toGetParamVal(parameters.get("wjId"));
            String fileId = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileType = toGetParamVal(parameters.get("fileType"));
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
            fileInfo.put("fileType", fileType);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            clhbDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getWjDetail(String standardId) {
        JSONObject detailObj = clhbDao.queryWjById(standardId);
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.get("CREATE_TIME_") != null) {
            detailObj.put("CREATE_TIME_", DateUtil.formatDate((Date) detailObj.get("CREATE_TIME_"), "yyyy-MM-dd"));
        }
        return detailObj;
    }

    public JSONObject getOldWjDetail(String standardId) {
        JSONObject detailObj = clhbDao.queryOldWjById(standardId);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    public List<JSONObject> getWjFileList(String wjId, String fileType) {
        List<JSONObject> wjFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("wjId", wjId);
        param.put("fileType", fileType);
        wjFileList = clhbDao.queryWjFileList(param);
        return wjFileList;
    }

    public List<JSONObject> getBoxList(String textType) {
        List<JSONObject> getBoxList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("textType", textType);
        getBoxList = clhbDao.queryBox(param);
        return getBoxList;
    }

    public void deleteOneWjFile(String fileId, String fileName, String wjId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, wjId, WebAppUtil.getProperty("wjFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("fileId", fileId);
        clhbDao.deleteWjFile(param);
    }

    public JsonResult deleteWj(String[] wjIdsArr, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        for (String wjId : wjIdsArr) {
            // 删除基本信息
            String fileType = null;
            List<JSONObject> files = getWjFileList(wjId, fileType);
            String wjFilePathBase = WebAppUtil.getProperty("wjFilePathBase");
            for (JSONObject oneFile : files) {
                rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"),
                        oneFile.getString("wjId"), wjFilePathBase);
            }
            rdmZhglFileManager.deleteDirFromDisk(wjId, wjFilePathBase);
            param.put("wjId", wjId);
            clhbDao.deleteWjFile(param);
            clhbDao.deleteWj(param);
        }
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }

    public String toGetClhbNumber() {
        String num = null;
        String nowYearStart = DateFormatUtil.getNowByString("yyyyMM");
        Map<String, Object> param = new HashMap<>();
        param.put("applyTimeStart", nowYearStart);
        JSONObject maxClhb = clhbDao.queryMaxClhbNum(param);
        int existNumber = 0;
        if (maxClhb != null) {
            existNumber = Integer.parseInt(maxClhb.getString("num").substring(15));
        }
        int thisNumber = existNumber + 1;
        String newDate = DateFormatUtil.getNowByString("yyyyMM");
        if (thisNumber < 10) {
            num = "WJHB-" + newDate + "-G3-" + "0" + thisNumber;
        } else if (thisNumber < 100 && thisNumber >= 10) {
            num = "WJHB-" + newDate + "-G3-" + thisNumber;
        }
        return num;
    }

    public ResponseEntity<byte[]> importClhbDownload() {
        try {
            String fileName = "国三阶段环保信息标签打刻模板.doc";
            // 创建文件实例
            File file = new File(
                    MaterielService.class.getClassLoader().getResource("templates/environment/" + fileName).toURI());
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

    public void exportClhbList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = queryWj(request, response, false);
        List<Map<String, Object>> listData = result.getData();
        //流程状态处理
        for (Map<String, Object> one:listData){
            if (!one.containsKey("status")){
                one.put("status","");
            }else if(one.get("status").toString().equalsIgnoreCase("DRAFTED")){
                one.put("status","草稿");
            }else if(one.get("status").toString().equalsIgnoreCase("RUNNING")){
                one.put("status","审批中");
            }else if(one.get("status").toString().equalsIgnoreCase("SUCCESS_END")){
                one.put("status","批准");
            }else if(one.get("status").toString().equalsIgnoreCase("DISCARD_END")){
                one.put("status","作废");
            }
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "国三车辆环保信息列表";
        String excelName = nowDate + title;
        String[] fieldNames = {"环保编号", "内部编号", "车辆类别", "国家标准", "整机设计型号","环保信息状态", "排放标准", "机械型号", "机械名称", "质量名称",
                "质量(kg)", "额定功率(kW)","外形尺寸", "整机物料号", "机械的标识方法和位置（整机铭牌）", "机械环保代码在机体上的打刻位置", "环保信息标签的位置",
                "额定工作参数", "商标", "机械分类-大类", "机械分类-小类", "制造商名称", "生产厂地址", "燃料类型", "燃料规格", "隔热措施有无",
                "隔热措施描述", "后处理具体安装位置", "进气系统特征", "机械进气系统阻力（kPa）", "中冷器形式", "排气系统背压（kPa）",
                "发动机型号", "发动机商标","发动机品牌", "发动机总成物料号", "发动机信息公开号", "整机环保信息公开号","流程状态","发布人","申请部门","发布时间"};
        String[] fieldCodes = {"num", "wjNo", "carType", "nationStandard", "designXh","noteStatus", "wjEmission", "wjModel", "wjName", "zlName",
                "wjWeight", "ratedPower","wjSize", "zjwlh", "bsLocation", "dmLocation", "bqLocation", "parameter",
                "wjBrand", "classA", "classB", "producter", "adress",
                "fuelType", "fuelSpecies", "grMeasures", "grMeasuresMs", "hclLocation", "jqSystem", "resistance", "coldType", "pqPressure",
                "fdjxh", "fdjsb","fdjzzs", "fdjzcwlh", "fdjgkbh", "zjgkh","status","applyName","deptName","CREATE_TIME_"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    public JSONObject getApiList(JSONObject result, String searchValue) {

        // 以searchValue为查询条件 返回查询到的列表
        String resUrlBase = "http://wjrdm.xcmg.com/rdm/environment/core/Wj/edit.do?action=detail&wjId=";
        List<JSONObject> resList = clhbDao.queryApiList(searchValue);
        for (JSONObject oneRes : resList) {
            oneRes.put("url", resUrlBase + oneRes.getString("wjId"));
        }
        if (resList.size() > 0) {
            result.put("result", resList);
        } else {
            result.put("success", false);
            result.put("message", "无查询结果，请检查搜索条件！");
        }
        return result;
    }



    public void zipFile(File Allfile,ZipOutputStream zipOut, String uIdStr) throws IOException {
            String[] ids = uIdStr.split(",");
            FileInputStream fileInputStream = null;
            BufferedInputStream bufferedInputStream = null;
            byte[] bufferArea = new byte[1024 * 10];
            String wjFilePathBase = WebAppUtil.getProperty("wjFilePathBase");
            for (String id : ids) {
                //将当前文件作为一个zip实体写入压缩流，fileName代表压缩文件中的文件名称
                zipOut.putNextEntry(new ZipEntry(id + "/"));
                String filePath = wjFilePathBase + File.separator + id;
                File fileToZip = new File(filePath);
                File[] listFiles = fileToZip.listFiles();
                for (File file : listFiles) {
                    if(file.isFile()){
                        fileInputStream = new FileInputStream(file);
                        bufferedInputStream = new BufferedInputStream(fileInputStream, 1024 * 10);
                    }
                }

            }
            int length = 0;
            while ((length = bufferedInputStream.read(bufferArea, 0, 1024 * 10)) != -1) {
                zipOut.write(bufferArea, 0, length);
            }
            //关闭流
            fileInputStream.close();
            bufferedInputStream.close();
        }

    public JsonResult statusChange(String wjId,String noteStatus) {
        JsonResult result = new JsonResult(true, "操作成功！");
        JSONObject idJson = new JSONObject();
        idJson.put("wjId", wjId);
        if("有效".equals(noteStatus)){
            idJson.put("noteStatus", "作废");
        }else if ("作废".equals(noteStatus)) {
            idJson.put("noteStatus", "有效");
        }else {
            idJson.put("noteStatus", "有效");
        }
        // 基本信息
        clhbDao.statusChange(idJson);
        return result;
    }
}
