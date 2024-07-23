package com.redxun.zlgjNPI.core.manager;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.query.Page;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.entity.BpmInst;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.*;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmCommonUtil;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.sys.org.manager.OsUserManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.zlgjNPI.core.dao.ZlgjWTDao;

import sun.misc.BASE64Encoder;

@Service
public class ZlgjWTService {
    private Logger logger = LoggerFactory.getLogger(ZlgjWTService.class);
    @Autowired
    private ZlgjWTDao zlgjWTDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private OsUserManager osUserManager;
    @Autowired
    private CommonInfoDao commonInfoDao;

    private DecimalFormat df = new DecimalFormat("0.00");

    public JsonResult deleteZlgj(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> wtIds = Arrays.asList(ids);
        List<JSONObject> files = getZlgjFileList(wtIds);
        String zlgjFilePathBase = WebAppUtil.getProperty("zlgjFilePathBase");
        String zlgjFilePathBase_view = WebAppUtil.getProperty("zlgjFilePathBase_preview");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                    oneFile.getString("wtId"), zlgjFilePathBase);
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                    oneFile.getString("wtId"), zlgjFilePathBase_view);
        }
        for (String oneZlgjId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneZlgjId, zlgjFilePathBase);
            rdmZhglFileManager.deleteDirFromDisk(oneZlgjId, zlgjFilePathBase_view);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("wtIds", wtIds);
        zlgjWTDao.deleteZlgjFile(param);
        zlgjWTDao.deleteZlgj(param);
        zlgjWTDao.deleteWtyy(param);
        zlgjWTDao.deleteLscs(param);
        zlgjWTDao.deleteYyyz(param);
        zlgjWTDao.deleteFayz(param);
        zlgjWTDao.deleteJjfa(param);
        zlgjWTDao.deleteRisk(param);
        zlgjWTDao.deleteRiskUser(param);
        return result;
    }

    public void createZlgj(JSONObject formData) {
        formData.put("wtId", IdUtil.getId());
        if (StringUtils.isBlank(formData.getString("CREATE_BY_"))) {
            formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        }
        formData.put("CREATE_TIME_", new Date());
        if (StringUtils.isBlank(formData.getString("yzTime"))) {
            formData.put("yzTime", null);
        }
        if (StringUtils.isBlank(formData.getString("yjTime"))) {
            formData.put("yjTime", null);
        }
        zlgjWTDao.insertZlgj(formData);
    }

    public void updateZlgj(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        if (StringUtils.isBlank(formData.getString("yzTime"))) {
            formData.put("yzTime", null);
        }
        if (StringUtils.isBlank(formData.getString("yjTime"))) {
            formData.put("yjTime", null);
        }
        // 问题原因数据
        if (StringUtils.isNotBlank(formData.getString("changeReasonData"))) {
            JSONArray reasonDataJson = JSONObject.parseArray(formData.getString("changeReasonData"));
            for (int i = 0; i < reasonDataJson.size(); i++) {
                JSONObject oneObject = reasonDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String yyId = oneObject.getString("yyId");
                if ("added".equals(state) || StringUtils.isBlank(yyId)) {
                    // 新增
                    oneObject.put("yyId", IdUtil.getId());
                    oneObject.put("wtId", formData.getString("wtId"));
                    oneObject.put("reason", oneObject.getString("reason"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    zlgjWTDao.addWtyy(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    zlgjWTDao.updatWtyy(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    zlgjWTDao.delWtyy(oneObject.getString("yyId"));
                }
            }
        }
        // 临时措施
        if (StringUtils.isNotBlank(formData.getString("changeLscsData"))) {
            JSONArray linshiCSDataJson = JSONObject.parseArray(formData.getString("changeLscsData"));
            for (int i = 0; i < linshiCSDataJson.size(); i++) {
                JSONObject oneObject = linshiCSDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String csId = oneObject.getString("csId");
                if ("added".equals(state) || StringUtils.isBlank(csId)) {
                    // 新增
                    oneObject.put("csId", IdUtil.getId());
                    oneObject.put("wtId", formData.getString("wtId"));
                    oneObject.put("yyId", oneObject.getString("yyId"));
                    oneObject.put("dcfa", oneObject.getString("dcfa"));
                    oneObject.put("ddpc", oneObject.getString("ddpc"));
                    oneObject.put("zrr", oneObject.getString("zrr"));
                    oneObject.put("zrrName", oneObject.getString("zrrName"));
                    oneObject.put("tzdh", oneObject.getString("tzdh"));
                    if (StringUtils.isBlank(oneObject.getString("wcTime"))) {
                        oneObject.put("wcTime", null);
                    }
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    zlgjWTDao.addLscs(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    zlgjWTDao.updatLscs(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    zlgjWTDao.delLscs(oneObject.getString("csId"));
                    // todo:补全删除文件，以前没有处理
                    JSONObject params = new JSONObject();
                    params.put("faId", oneObject.getString("csId"));
                    List<JSONObject> fileList = zlgjWTDao.getFileListByTypeIdh(params);
                    for (JSONObject file : fileList) {
                        String fileName = file.getString("fileName");
                        String suffix = CommonFuns.toGetFileSuffix(fileName);
                        this.deleteFileOnDisk(formData.getString("wtId"), file.getString("id"), suffix);
                        params.clear();
                        params.put("id", file.getString("id"));
                        zlgjWTDao.deleteZlgjFile(params);
                    }
                }
            }
        }
        // 原因验证
        if (StringUtils.isNotBlank(formData.getString("changeYyyzData"))) {
            JSONArray reasonYZDataJson = JSONObject.parseArray(formData.getString("changeYyyzData"));
            for (int i = 0; i < reasonYZDataJson.size(); i++) {
                JSONObject oneObject = reasonYZDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String yzyyId = oneObject.getString("yzyyId");
                if ("added".equals(state) || StringUtils.isBlank(yzyyId)) {
                    // 新增
                    oneObject.put("yzyyId", IdUtil.getId());
                    oneObject.put("wtId", formData.getString("wtId"));
                    oneObject.put("yyId", oneObject.getString("yyId"));
                    oneObject.put("jcjg", oneObject.getString("jcjg"));
                    oneObject.put("jielun", oneObject.getString("jielun"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    zlgjWTDao.addYyyz(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    zlgjWTDao.updatYyyz(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    zlgjWTDao.delYyyz(oneObject.getString("yzyyId"));
                    // todo:补全删除文件，以前没有处理
                    JSONObject params = new JSONObject();
                    params.put("faId", oneObject.getString("yzyyId"));
                    List<JSONObject> fileList = zlgjWTDao.getFileListByTypeIdh(params);
                    for (JSONObject file : fileList) {
                        String fileName = file.getString("fileName");
                        String suffix = CommonFuns.toGetFileSuffix(fileName);
                        this.deleteFileOnDisk(formData.getString("wtId"), file.getString("id"), suffix);
                        params.clear();
                        params.put("id", file.getString("id"));
                        zlgjWTDao.deleteZlgjFile(params);
                    }
                }
            }
        }
        // 方案验证
        if (StringUtils.isNotBlank(formData.getString("changeFayzData"))) {
            JSONArray fayzDataJson = JSONObject.parseArray(formData.getString("changeFayzData"));
            for (int i = 0; i < fayzDataJson.size(); i++) {
                JSONObject oneObject = fayzDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String faId = oneObject.getString("faId");
                if ("added".equals(state) || StringUtils.isBlank(faId)) {
                    // 新增
                    oneObject.put("faId", IdUtil.getId());
                    oneObject.put("wtId", formData.getString("wtId"));
                    oneObject.put("yyId", oneObject.getString("yyId"));
                    oneObject.put("yzjg", oneObject.getString("yzjg"));
                    oneObject.put("gjfa", oneObject.getString("gjfa"));
                    oneObject.put("yzzrr", oneObject.getString("yzzrr"));
                    oneObject.put("yzzrrName", oneObject.getString("yzzrrName"));
                    if (StringUtils.isBlank(oneObject.getString("sjTime"))) {
                        oneObject.put("sjTime", null);
                    }
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    zlgjWTDao.addFayz(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    zlgjWTDao.updatFayz(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    zlgjWTDao.delFayz(oneObject.getString("faId"));
                    // todo:补全删除文件，以前没有处理
                    JSONObject params = new JSONObject();
                    params.put("faId", oneObject.getString("faId"));
                    List<JSONObject> fileList = zlgjWTDao.getFileListByTypeIdh(params);
                    for (JSONObject file : fileList) {
                        String fileName = file.getString("fileName");
                        String suffix = CommonFuns.toGetFileSuffix(fileName);
                        this.deleteFileOnDisk(formData.getString("wtId"), file.getString("id"), suffix);
                        params.clear();
                        params.put("id", file.getString("id"));
                        zlgjWTDao.deleteZlgjFile(params);
                    }
                }
            }
        }
        // 永久解决方案
        if (StringUtils.isNotBlank(formData.getString("changeFajjData"))) {
            JSONArray jjfaDataJson = JSONObject.parseArray(formData.getString("changeFajjData"));
            for (int i = 0; i < jjfaDataJson.size(); i++) {
                JSONObject oneObject = jjfaDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String faId = oneObject.getString("faId");
                if ("added".equals(state) || StringUtils.isBlank(faId)) {
                    // 新增
                    oneObject.put("faId", IdUtil.getId());
                    oneObject.put("wtId", formData.getString("wtId"));
                    oneObject.put("yyId", oneObject.getString("yyId"));
                    oneObject.put("cqcs", oneObject.getString("cqcs"));
                    oneObject.put("tzdh", oneObject.getString("tzdh"));
                    oneObject.put("zrr", oneObject.getString("zrr"));
                    oneObject.put("zrrName", oneObject.getString("zrrName"));
                    if (StringUtils.isBlank(oneObject.getString("wcTime"))) {
                        oneObject.put("wcTime", null);
                    }
                    oneObject.put("yjqhch", oneObject.getString("yjqhch"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    zlgjWTDao.addJjfa(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    zlgjWTDao.updatJjfa(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    zlgjWTDao.delJjfa(oneObject.getString("faId"));
                    // todo:补全删除文件，以前没有处理
                    JSONObject params = new JSONObject();
                    params.put("faId", oneObject.getString("faId"));
                    List<JSONObject> fileList = zlgjWTDao.getFileListByTypeIdh(params);
                    for (JSONObject file : fileList) {
                        String fileName = file.getString("fileName");
                        String suffix = CommonFuns.toGetFileSuffix(fileName);
                        this.deleteFileOnDisk(formData.getString("wtId"), file.getString("id"), suffix);
                        params.clear();
                        params.put("id", file.getString("id"));
                        zlgjWTDao.deleteZlgjFile(params);
                    }
                }
            }
        }
        // 风险排查人员
        if (StringUtils.isNotBlank(formData.getString("changeRiskUserData"))) {
            JSONArray jjfaDataJson = JSONObject.parseArray(formData.getString("changeRiskUserData"));
            for (int i = 0; i < jjfaDataJson.size(); i++) {
                JSONObject oneObject = jjfaDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String ryId = oneObject.getString("ryId");
                if ("added".equals(state) || StringUtils.isBlank(ryId)) {
                    // 新增
                    oneObject.put("ryId", IdUtil.getId());
                    oneObject.put("wtId", formData.getString("wtId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    zlgjWTDao.addRiskUser(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    zlgjWTDao.updateRiskUser(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    zlgjWTDao.delRiskUser(oneObject.getString("ryId"));
                }
            }
        }
        // 风险排查及整改
        if (StringUtils.isNotBlank(formData.getString("changeRiskData"))) {
            JSONArray jjfaDataJson = JSONObject.parseArray(formData.getString("changeRiskData"));
            for (int i = 0; i < jjfaDataJson.size(); i++) {
                JSONObject oneObject = jjfaDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String riskId = oneObject.getString("riskId");
                if ("added".equals(state) || StringUtils.isBlank(riskId)) {
                    // 新增
                    oneObject.put("riskId", IdUtil.getId());
                    oneObject.put("wtId", formData.getString("wtId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    zlgjWTDao.addRisk(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    zlgjWTDao.updateRisk(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    zlgjWTDao.delRisk(oneObject.getString("riskId"));
                    // todo:补全删除文件，以前没有处理
                    JSONObject params = new JSONObject();
                    params.put("faId", oneObject.getString("riskId"));
                    List<JSONObject> fileList = zlgjWTDao.getFileListByTypeIdh(params);
                    for (JSONObject file : fileList) {
                        String fileName = file.getString("fileName");
                        String suffix = CommonFuns.toGetFileSuffix(fileName);
                        this.deleteFileOnDisk(formData.getString("wtId"), file.getString("id"), suffix);
                        params.clear();
                        params.put("id", file.getString("id"));
                        zlgjWTDao.deleteZlgjFile(params);
                    }
                }
            }
        }
        zlgjWTDao.updateZlgj(formData);
    }

    public void saveUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到文件上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        try {
            String mainId = toGetParamVal(parameters.get("mainId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fjlx = toGetParamVal(parameters.get("fjlx"));
            String faId = toGetParamVal(parameters.get("faId"));
            if (StringUtils.isNotBlank(fjlx) && "ghtp".equals(fjlx)) {
                /*Map<String, Object> params = new HashMap<>();
                params.put("faId", mainId);
                List<JSONObject> wtIdL = zlgjWTDao.queryWtId(params);
                JSONObject objs = wtIdL.get(0);
                String obj=objs.getString("wtId");*/
                Iterator<MultipartFile> it = fileMap.values().iterator();
                MultipartFile mf = it.next();
                String filePathBase = StringUtils.isBlank(mainId) ? WebAppUtil.getProperty("zlgjFilePathBase")
                        : WebAppUtil.getProperty("zlgjFilePathBase");
                String filePathBase_view =
                        StringUtils.isBlank(mainId) ? WebAppUtil.getProperty("zlgjFilePathBase_preview")
                                : WebAppUtil.getProperty("zlgjFilePathBase_preview");
                if (StringUtils.isBlank(filePathBase)) {
                    logger.error("can't find filePathBase or filePathBase_preview");
                    return;
                }
                // 向下载目录中写入文件
                String filePath = filePathBase + (StringUtils.isNotBlank(mainId) ? (File.separator + mainId) : "");
                File pathFile = new File(filePath);
                if (!pathFile.exists()) {
                    pathFile.mkdirs();
                }
                String suffix = CommonFuns.toGetFileSuffix(fileName);
                String fileFullPath = filePath + File.separator + id + "." + suffix;
                File file = new File(fileFullPath);
                FileCopyUtils.copy(mf.getBytes(), file);
                // 预览目录
                String filePath_view =
                        filePathBase_view + (StringUtils.isNotBlank(mainId) ? (File.separator + mainId) : "");
                File pathFile_view = new File(filePath_view);
                if (!pathFile_view.exists()) {
                    pathFile_view.mkdirs();
                }
                String fileFullPath_view = filePath_view + File.separator + id + "." + suffix;
                File file_view = new File(fileFullPath_view);
                FileCopyUtils.copy(mf.getBytes(), file_view);
                // 写入数据库
                Map<String, Object> fileInfo = new HashMap<>();
                fileInfo.put("id", id);
                fileInfo.put("fileName", fileName);
                fileInfo.put("fileSize", toGetParamVal(parameters.get("fileSize")));
                fileInfo.put("wtId", mainId);
                fileInfo.put("faId", faId);
                fileInfo.put("fjlx", toGetParamVal(parameters.get("fjlx")));
                fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                zlgjWTDao.addFileInfos(fileInfo);
            } else {
                Iterator<MultipartFile> it = fileMap.values().iterator();
                MultipartFile mf = it.next();
                String filePathBase = StringUtils.isBlank(mainId) ? WebAppUtil.getProperty("zlgjFilePathBase")
                        : WebAppUtil.getProperty("zlgjFilePathBase");
                String filePathBase_view =
                        StringUtils.isBlank(mainId) ? WebAppUtil.getProperty("zlgjFilePathBase_preview")
                                : WebAppUtil.getProperty("zlgjFilePathBase_preview");
                if (StringUtils.isBlank(filePathBase)) {
                    logger.error("can't find filePathBase or filePathBase_preview");
                    return;
                }
                // 向下载目录中写入文件
                String filePath = filePathBase + (StringUtils.isNotBlank(mainId) ? (File.separator + mainId) : "");
                File pathFile = new File(filePath);
                if (!pathFile.exists()) {
                    pathFile.mkdirs();
                }
                String suffix = CommonFuns.toGetFileSuffix(fileName);
                String fileFullPath = filePath + File.separator + id + "." + suffix;
                File file = new File(fileFullPath);
                FileCopyUtils.copy(mf.getBytes(), file);
                // 预览目录
                String filePath_view =
                        filePathBase_view + (StringUtils.isNotBlank(mainId) ? (File.separator + mainId) : "");
                File pathFile_view = new File(filePath_view);
                if (!pathFile_view.exists()) {
                    pathFile_view.mkdirs();
                }
                String fileFullPath_view = filePath_view + File.separator + id + "." + suffix;
                File file_view = new File(fileFullPath_view);
                FileCopyUtils.copy(mf.getBytes(), file_view);
                // 写入数据库
                Map<String, Object> fileInfo = new HashMap<>();
                fileInfo.put("id", id);
                fileInfo.put("fileName", fileName);
                fileInfo.put("fileSize", toGetParamVal(parameters.get("fileSize")));
                fileInfo.put("wtId", mainId);
                if (StringUtils.isNotBlank(fjlx) && "lszmcl".equals(fjlx)) {
                    fileInfo.put("faId", faId);
                }
                if (StringUtils.isNotBlank(fjlx) && "yyzmcl".equals(fjlx)) {
                    fileInfo.put("faId", faId);
                }
                if (StringUtils.isNotBlank(fjlx) && "fazmcl".equals(fjlx)) {
                    fileInfo.put("faId", faId);
                }
                if (StringUtils.isNotBlank(fjlx) && "risk".equals(fjlx)) {
                    fileInfo.put("faId", faId);
                }
                fileInfo.put("fjlx", toGetParamVal(parameters.get("fjlx")));
                fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                zlgjWTDao.addFileInfos(fileInfo);
            }

        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
        return;
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    public List<JSONObject> getFileListByTypeId(HttpServletRequest request) {
        String mainId = RequestUtil.getString(request, "mainId");
        String fileName = RequestUtil.getString(request, "fileName");
        String fjlx = RequestUtil.getString(request, "fjlx");
        String faId = RequestUtil.getString(request, "faId");
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(mainId)) {
            params.put("mainId", mainId);
        }
        if (StringUtils.isNotBlank(fjlx)) {
            params.put("fjlx", fjlx);
        }
        if (StringUtils.isNotBlank(fileName)) {
            params.put("fileName", fileName);
        }
        if (StringUtils.isNotBlank(faId)) {
            params.put("faId", faId);
        }
        List<JSONObject> fileInfos = new ArrayList<>();
        if ("gzxg".equalsIgnoreCase(fjlx)) {
            fileInfos = zlgjWTDao.getFileListByTypeId(params);
        }
        if ("gztp".equalsIgnoreCase(fjlx)) {
            fileInfos = zlgjWTDao.getFileListByTypeId(params);
        }
        if ("gjfj".equalsIgnoreCase(fjlx)) {
            fileInfos = zlgjWTDao.getFileListByTypeId(params);
        }
        if ("ghtp".equalsIgnoreCase(fjlx)) {
            fileInfos = zlgjWTDao.getFileListByTypeIdh(params);
        }
        if ("lszmcl".equalsIgnoreCase(fjlx)) {
            fileInfos = zlgjWTDao.getFileListByTypeIdh(params);
        }
        if ("yyzmcl".equalsIgnoreCase(fjlx)) {
            fileInfos = zlgjWTDao.getFileListByTypeIdh(params);
        }
        if ("fazmcl".equalsIgnoreCase(fjlx)) {
            fileInfos = zlgjWTDao.getFileListByTypeIdh(params);
        }
        if ("risk".equalsIgnoreCase(fjlx)) {
            fileInfos = zlgjWTDao.getFileListByTypeIdh(params);
        }

        return fileInfos;
    }

    public JsonPageResult<?> getZlgjList(HttpServletRequest request, HttpServletResponse response, boolean doPage,
                                         Map<String, Object> params, boolean queryTaskUser, String defaultSortField) {
        JsonPageResult result = new JsonPageResult(true);
        String wtlxtype = RequestUtil.getString(request, "wtlxtype", "");
        String czxpj = RequestUtil.getString(request, "czxpj", "");
        String report = RequestUtil.getString(request, "report", "");
        params.put("wtlxtype", wtlxtype);
        params.put("czxpj", czxpj);
        rdmZhglUtil.addOrder(request, params, defaultSortField, "desc");
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    if ("rdTimeStart".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("rdTimeEnd".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    if ("yzcd".equalsIgnoreCase(name)) {
                        params.put(name, Arrays.asList(value.split(",", -1)));
                    } else {
                        params.put(name, value);
                    }
                }
            }
        }
        String currentUserId = ContextUtil.getCurrentUserId();
        if (StringUtils.isNotBlank(report) && "jump".equalsIgnoreCase(report)) {
            params.put("report", report);
        }
        // 增加角色过滤的条件
        RdmCommonUtil.addListAllQueryRoleExceptDraft(params, currentUserId, ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> zlgjList = zlgjWTDao.queryZlgjList(params);
        for (JSONObject zlgj : zlgjList) {
            if (zlgj.get("yzTime") != null) {
                zlgj.put("yzTime", DateUtil.formatDate((Date) zlgj.get("yzTime"), "yyyy-MM-dd"));
            }
            if (zlgj.get("yjTime") != null) {
                zlgj.put("yjTime", DateUtil.formatDate((Date) zlgj.get("yjTime"), "yyyy-MM-dd"));
            }
            String status = zlgj.getString("status");
            String zrbmshTime = zlgj.getString("zrbmshTime");
            if (StringUtils.isNotBlank(zrbmshTime) || BpmInst.STATUS_DISCARD.equalsIgnoreCase(status)) {
                zlgj.put("jiean", "已完成");
            } else if (StringUtils.isBlank(zrbmshTime) && BpmInst.STATUS_RUNNING.equalsIgnoreCase(status)) {
                zlgj.put("jiean", "进行中");
            }
        }
        // 查询当前处理人
        if (queryTaskUser) {
            xcmgProjectManager.setTaskCurrentUserJSON(zlgjList);
        }
        // 过滤处理人条件
        if (params.get("currentProcessUserId") != null
                && StringUtils.isNotBlank(params.get("currentProcessUserId").toString())) {
            Iterator<JSONObject> iterator = zlgjList.iterator();
            while (iterator.hasNext()) {
                JSONObject oneData = iterator.next();
                String actualProcessUserId = oneData.getString("currentProcessUserId");
                if (StringUtils.isBlank(actualProcessUserId)
                        || !actualProcessUserId.contains(params.get("currentProcessUserId").toString())) {
                    iterator.remove();
                }
            }
        }
        // 将需要当前处理人处理的排在最前面,分2块
        List<JSONObject> needCurrentProcess = new ArrayList<>();
        List<JSONObject> otherData = new ArrayList<>();
        List<String> zrrIds = new ArrayList<>();//责任人Id
        List<String> currentProcessUserIds = new ArrayList<>();//流程处理人Id

        for (JSONObject oneData : zlgjList) {
            String zrrId = oneData.getString("zrrId");
            if (StringUtils.isNotBlank(zrrId)) {
                zrrIds.add(zrrId);
            }
            String processUserId = oneData.getString("currentProcessUserId");
            if (StringUtils.isNotBlank(processUserId)) {
                String[] processUserIdStr = processUserId.split(",");
                for (String oneId : processUserIdStr) {
                    currentProcessUserIds.add(oneId);
                }
            }
        }
        Map<String, String> zrrId2DeptName = new HashMap<>();
        Map<String, String> processUserId2DeptName = new HashMap<>();
        if (!zrrIds.isEmpty() && zrrIds != null) {
            //所有的责任人部门信息
            Map<String, Object> zrrIdparams = new HashMap<>();
            zrrIdparams.put("userIds", zrrIds);
            List<JSONObject> zrrDeptInfos = commonInfoDao.getDeptInfoByUserIds(zrrIdparams);
            for (JSONObject zrrDeptInfo : zrrDeptInfos) {
                zrrId2DeptName.put(zrrDeptInfo.getString("userId"), zrrDeptInfo.getString("deptName"));
            }
        }
        if (!currentProcessUserIds.isEmpty() && currentProcessUserIds != null) {
            //所有的流程处理人部门信息
            Map<String, Object> processUserIdparams = new HashMap<>();
            processUserIdparams.put("userIds", currentProcessUserIds);
            List<JSONObject> processUserDeptInfos = commonInfoDao.getDeptInfoByUserIds(processUserIdparams);
            for (JSONObject processUserDeptInfo : processUserDeptInfos) {
                processUserId2DeptName.put(processUserDeptInfo.getString("userId"), processUserDeptInfo.getString("deptName"));
            }
        }

        for (JSONObject oneData : zlgjList) {
            //责任人部门
            oneData.put("zrrDeptName", zrrId2DeptName.get(oneData.getString("zrrId")));
            //流程处理人部门
            StringBuilder stringBuilder = new StringBuilder();
            String processUserId = oneData.getString("currentProcessUserId");
            if (StringUtils.isNotBlank(processUserId)) {
                String[] processUserIdStr = processUserId.split(",");
                for (String oneId : processUserIdStr) {
                    String currentProcessUserDeptName = processUserId2DeptName.get(oneId);
                    if (stringBuilder.indexOf(currentProcessUserDeptName) == -1) {
                        stringBuilder.append(currentProcessUserDeptName).append(",");
                    }
                    if (stringBuilder.length() > 0) {
                        oneData.put("currentProcessUserDeptName", stringBuilder.substring(0, stringBuilder.length() - 1));
                    }
                }
            }
            if (StringUtils.isNotBlank(processUserId) && processUserId.contains(currentUserId)) {
                needCurrentProcess.add(oneData);
            } else {
                otherData.add(oneData);
            }
        }
        // 需要当前处理人的，按照taskCreateTime正序
        if (needCurrentProcess.size() != 0) {
            Collections.sort(needCurrentProcess, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject o1, JSONObject o2) {
                    String o1TaskCreateTime = o1.getString("taskCreateTime");
                    String o2TaskCreateTime = o2.getString("taskCreateTime");
                    return o1TaskCreateTime.compareTo(o2TaskCreateTime);
                }
            });
        }
        // 不需要当前处理人的，按CREATE_TIME倒序
        if (otherData.size() != 0) {
            Collections.sort(otherData, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject o1, JSONObject o2) {
                    String o1CreateTime = o1.getString("CREATE_TIME");
                    String o2CreateTime = o2.getString("CREATE_TIME");
                    return o2CreateTime.compareTo(o1CreateTime);
                }
            });
        }
        List<JSONObject> finalList = new ArrayList<>();
        finalList.addAll(needCurrentProcess);
        finalList.addAll(otherData);
        // 人工分页
        if (doPage) {
            String pageIndex = request.getParameter("pageIndex");
            String pageSize = request.getParameter("pageSize");
            if (StringUtils.isBlank(pageIndex)) {
                pageIndex = "0";
            }
            if (StringUtils.isBlank(pageSize)) {
                pageSize = "20";
            }
            int startSubListIndex = Integer.parseInt(pageIndex) * Integer.parseInt(pageSize);
            int endSubListIndex = startSubListIndex + Integer.parseInt(pageSize);
            List<JSONObject> finalSubApplyList = new ArrayList<JSONObject>();
            if (startSubListIndex < finalList.size()) {
                finalSubApplyList = finalList.subList(startSubListIndex,
                        endSubListIndex < finalList.size() ? endSubListIndex : finalList.size());
            }
            result.setData(finalSubApplyList);
        } else {
            result.setData(finalList);
        }
        result.setTotal(finalList.size());
        return result;
    }

    public JSONObject getZlgjDetail(String wtId) {
        JSONObject detailObj = zlgjWTDao.queryZlgjById(wtId);
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.get("yzTime") != null) {
            detailObj.put("yzTime", DateUtil.formatDate((Date) detailObj.get("yzTime"), "yyyy-MM-dd"));
        }
        if (detailObj.get("yjTime") != null) {
            detailObj.put("yjTime", DateUtil.formatDate((Date) detailObj.get("yjTime"), "yyyy-MM-dd"));
        }
        return detailObj;
    }

    public void deleteFileOnDisk(String mainId, String fileId, String suffix) {
        String fullFileName = fileId + "." + suffix;
        StringBuilder fileBasePath = new StringBuilder(WebAppUtil.getProperty("zlgjFilePathBase"));
        String fullPath =
                fileBasePath.append(File.separator).append(mainId).append(File.separator).append(fullFileName).toString();
        File file = new File(fullPath);
        if (file.exists()) {
            file.delete();
        }
        // 预览目录删除
        StringBuilder fileBasePath_view = new StringBuilder(WebAppUtil.getProperty("zlgjFilePathBase_preview"));
        String fullPath_view = fileBasePath_view.append(File.separator).append(mainId).append(File.separator)
                .append(fullFileName).toString();
        File file_view = new File(fullPath_view);
        if (file_view.exists()) {
            file_view.delete();
        }
        // 删除预览目录中pdf文件
        String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
        String convertPdfPath = WebAppUtil.getProperty("zlgjFilePathBase_preview") + File.separator + mainId
                + File.separator + convertPdfDir + File.separator + fileId + ".pdf";
        File pdffile = new File(convertPdfPath);
        pdffile.delete();
    }

    public List<JSONObject> getReasonList(String wtId) {
        JSONObject paramJson = new JSONObject();
        paramJson.put("wtId", wtId);
        List<JSONObject> reasonList = zlgjWTDao.getReasonList(paramJson);
        return reasonList;
    }

    public List<JSONObject> getLscsList(String wtId, String rounds) {
        JSONObject paramJson = new JSONObject();
        paramJson.put("wtId", wtId);
        paramJson.put("rounds", rounds);
        List<JSONObject> lscsList = zlgjWTDao.getLscsList(paramJson);
        return lscsList;
    }

    public List<JSONObject> getyzList(String wtId, String rounds) {
        JSONObject paramJson = new JSONObject();
        paramJson.put("wtId", wtId);
        paramJson.put("rounds", rounds);
        List<JSONObject> yzList = zlgjWTDao.getyzList(paramJson);
        return yzList;
    }

    public List<JSONObject> getfayzList(String wtId, String rounds) {
        JSONObject paramJson = new JSONObject();
        paramJson.put("wtId", wtId);
        paramJson.put("rounds", rounds);
        List<JSONObject> yzList = zlgjWTDao.getfayzList(paramJson);
        return yzList;
    }

    public List<JSONObject> getfajjList(String wtId, String rounds) {
        JSONObject paramJson = new JSONObject();
        paramJson.put("wtId", wtId);
        paramJson.put("rounds", rounds);
        List<JSONObject> yzList = zlgjWTDao.getfajjList(paramJson);
        return yzList;
    }

    public List<JSONObject> getRiskUserList(String wtId) {
        JSONObject paramJson = new JSONObject();
        paramJson.put("wtId", wtId);
        List<JSONObject> yzList = zlgjWTDao.getRiskUserList(paramJson);
        return yzList;
    }

    public List<JSONObject> getRiskList(String wtId) {
        JSONObject paramJson = new JSONObject();
        paramJson.put("wtId", wtId);
        List<JSONObject> yzList = zlgjWTDao.getRiskList(paramJson);
        for (JSONObject wj : yzList) {
            if (wj.get("CREATE_TIME_") != null) {
                wj.put("CREATE_TIME_", DateUtil.formatDate((Date) wj.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return yzList;
    }

    public List<JSONObject> getZlgjFileList(List<String> zlgjIdList) {
        List<JSONObject> zlgjFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("zlgjIds", zlgjIdList);
        zlgjFileList = zlgjWTDao.queryZlgjFileList(param);
        return zlgjFileList;
    }

    public List<JSONObject> reasonList(String[] ids) {
        List<String> yyIds = Arrays.asList(ids);
        JSONObject paramJson = new JSONObject();
        paramJson.put("yyIds", yyIds);
        List<JSONObject> result = zlgjWTDao.reasonList(paramJson);
        return result;
    }

    private List<Map<String, Object>> filterList(List<Map<String, Object>> JstbList) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (JstbList == null || JstbList.isEmpty()) {
            return result;
        }
        // 管理员查看所有的包括草稿数据
        if ("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
            return JstbList;
        }
        // 分管领导的查看权限等同于项目管理人员
        boolean showAll = false;
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUser().getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        for (Map<String, Object> oneRole : currentUserRoles) {
            if (oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-BELONG")) {
                if (RdmConst.AllDATA_QUERY_NAME.equalsIgnoreCase(oneRole.get("NAME_").toString())) {
                    showAll = true;
                    break;
                }
            }
        }
        String currentUserId = ContextUtil.getCurrentUserId();
        for (Map<String, Object> oneProject : JstbList) {
            if (showAll) {
                if (oneProject.get("status") != null && !"DRAFTED".equals(oneProject.get("status").toString())) {
                    result.add(oneProject);
                } else {
                    if (oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneProject);
                    }
                }
            } else {
                // 自己是当前流程处理人
                if (oneProject.get("status") != null && !"DRAFTED".equals(oneProject.get("status").toString())) {
                    if (oneProject.get("currentProcessUserId") != null
                            && oneProject.get("currentProcessUserId").toString().contains(currentUserId)) {
                        result.add(oneProject);
                    }
                }
                // 自己的查看所有
                if (oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                    result.add(oneProject);
                }
            }

        }
        return result;
    }

    public void exportWtsbExcel(HttpServletResponse response, HttpServletRequest request) {
        Map<String, Object> params = new HashMap<>();
        JsonPageResult queryDataResult = getZlgjList(request, response, false, params, true, "zwt.smallJiXing");
        String dataType = "";
        if (params.get("wtlxtype") != null && StringUtils.isNotBlank(params.get("wtlxtype").toString())) {
            dataType = params.get("wtlxtype").toString();
        }
        String titleName = toGetWtNameByCode(dataType);
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + titleName;
        if (!"LBJSY".equalsIgnoreCase(dataType)) {
            String[] fieldNames = {"问题类型", "问题响应要求", "问题严重度", "问题编号", "机型类别", "机型", "整机编号", "工作小时", "施工工况", "故障系统",
                    "故障零件", "问题描述", "现场处置方法", "故障率", "零部件供应商", "问题可能原因", "临时措施", "永久方案", "责任部门", "第一责任人", "问题处理人", "问题处理人部门",
                    "第一责任人问题接收时间", "第一责任人持续时长(天)", "问题处理人问题接收时间", "问题处理人持续时长(天)", "结案时间", "结案状态", "是否改进", "不改进理由", "流程处理人", "流程处理人部门",
                    "流程任务", "流程状态", "提报人", "提报部门", "提报时间", "排放标准"};
            String[] fieldCodes = {"wtlx", "jjcd", "yzcd", "zlgjNumber", "jiXingName", "smallJiXing", "zjbh", "gzxs",
                    "sggk", "gzxt", "gzlj", "wtms", "xcczfa", "gzl", "lbjgys", "reason", "dcfa", "cqcs", "ssbmName",
                    "zrcpzgName", "zrrName", "zrrDeptName", "cpzgStartTime", "cpzgDuration", "zrrStartTime", "zrrDuration", "zrbmshTime",
                    "jiean", "ifgj", "noReason", "currentProcessUser", "currentProcessUserDeptName", "currentProcessTask", "status", "applyUserName",
                    "applyUserDeptName", "CREATE_TIME", "pfbz"};
            // 再处理数据，补充缺失的数据
            List<JSONObject> queryData = queryDataResult.getData();
            processWtsbExportData(queryData);
            HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(queryData, fieldNames, fieldCodes, titleName);
            // 输出
            ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
        } else {
            String[] fieldNames = {"问题类型", "问题响应要求", "问题严重度", "问题编号", "零部件型号", "零部件类别", "零部件名称", "配套主机型号", "物料编码",
                    "零部件供应商", "试验类型", "试验负责人", "测试结论", "不合格项说明", "改进建议", "问题可能原因", "临时措施", "永久方案", "责任部门", "第一责任人", "问题处理人", "问题处理人部门",
                    "第一责任人问题接收时间", "第一责任人持续时长(天)", "问题处理人问题接收时间", "问题处理人持续时长(天)", "结案时间", "结案状态", "是否改进", "不改进理由", "流程处理人", "流程处理人部门",
                    "流程任务", "流程状态", "提报人", "提报部门", "提报时间"};
            String[] fieldCodes = {"wtlx", "jjcd", "yzcd", "zlgjNumber", "componentModel", "componentCategory",
                    "componentName", "machineModel", "materialCode", "lbjgys", "testType", "testLeader", "testConclusion",
                    "nonconformingDescription", "improvementSuggestions", "reason", "dcfa", "cqcs", "ssbmName",
                    "zrcpzgName", "zrrName", "zrrDeptName", "cpzgStartTime", "cpzgDuration", "zrrStartTime", "zrrDuration", "zrbmshTime",
                    "jiean", "ifgj", "noReason", "currentProcessUser", "currentProcessUserDeptName", "currentProcessTask", "status", "applyUserName",
                    "applyUserDeptName", "CREATE_TIME"};
            // 再处理数据，补充缺失的数据
            List<JSONObject> queryData = queryDataResult.getData();
            processWtsbExportData(queryData);
            HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(queryData, fieldNames, fieldCodes, titleName);
            // 输出
            ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
        }
    }

    private String toGetWtNameByCode(String code) {
        String wtName = "";
        switch (code) {
            case ZlgjConstant.CNWT:
                wtName = "厂内问题";
                break;
            case ZlgjConstant.SCWT:
                wtName = "市场问题";
                break;
            case ZlgjConstant.HWWT:
                wtName = "海外问题";
                break;
            case ZlgjConstant.XPSZ:
                wtName = "新品试制";
                break;
            case ZlgjConstant.XPZDSY:
                wtName = "新品整机试验";
                break;
            case ZlgjConstant.XPLS:
                wtName = "新品路试";
                break;
            case ZlgjConstant.WXBLX:
                wtName = "维修便利性";
                break;
            case ZlgjConstant.LBJSY:
                wtName = "新品零部件试验";
                break;

        }
        return wtName;
    }

    private void processWtsbExportData(List<JSONObject> queryData) {
        if (queryData == null || queryData.isEmpty()) {
            return;
        }
        SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd");
        List<String> wtsbIdList = new ArrayList<>();
        for (JSONObject oneData : queryData) {
            if (StringUtils.isNotBlank(oneData.getString("jiXing"))) {
                String jiXingName = toGetJixingNameByCode(oneData.getString("jiXing"));
                oneData.put("jiXingName", jiXingName);
            }
            if (oneData.getDate("zrbmshTime") == null) {
                oneData.put("hasZrbmshTime", "否");
            } else {
                oneData.put("hasZrbmshTime", "是");
                oneData.put("zrbmshTime", fullSdf.format(oneData.getDate("zrbmshTime")));
            }
            String wtName = toGetWtNameByCode(oneData.getString("wtlx"));
            oneData.put("wtlx", wtName);
            wtsbIdList.add(oneData.getString("wtId"));

            String ifgj = oneData.getString("ifgj");
            if ("yes".equalsIgnoreCase(ifgj)) {
                oneData.put("ifgj", "是");
            } else if ("no".equalsIgnoreCase(ifgj)) {
                oneData.put("ifgj", "否");
            } else {
                oneData.put("ifgj", "");
            }

            String statusName = XcmgProjectUtil.convertProjectStatusCode2Name(oneData.get("status"));
            oneData.put("status", statusName);
        }
        // 查询原因、措施、解决方案
        Map<String, Object> params = new HashMap<>();
        params.put("wtIds", wtsbIdList);
        Map<String, List<JSONObject>> wtId2ReasonList = new HashMap<>();
        Map<String, List<JSONObject>> wtId2LscsList = new HashMap<>();
        Map<String, List<JSONObject>> wtId2CqcsList = new HashMap<>();
        List<JSONObject> reasonList = zlgjWTDao.queryReasonByWtId(params);
        if (reasonList != null && !reasonList.isEmpty()) {
            for (JSONObject oneData : reasonList) {
                String wtId = oneData.getString("wtId");
                if (!wtId2ReasonList.containsKey(wtId)) {
                    wtId2ReasonList.put(wtId, new ArrayList<>());
                }
                wtId2ReasonList.get(wtId).add(oneData);
            }
        }
        List<JSONObject> lscsList = zlgjWTDao.queryLscsByWtId(params);
        if (lscsList != null && !lscsList.isEmpty()) {
            for (JSONObject oneData : lscsList) {
                String wtId = oneData.getString("wtId");
                if (!wtId2LscsList.containsKey(wtId)) {
                    wtId2LscsList.put(wtId, new ArrayList<>());
                }
                wtId2LscsList.get(wtId).add(oneData);
            }
        }
        List<JSONObject> cqcsList = zlgjWTDao.queryCqcsByWtId(params);
        if (cqcsList != null && !cqcsList.isEmpty()) {
            for (JSONObject oneData : cqcsList) {
                String wtId = oneData.getString("wtId");
                if (!wtId2CqcsList.containsKey(wtId)) {
                    wtId2CqcsList.put(wtId, new ArrayList<>());
                }
                wtId2CqcsList.get(wtId).add(oneData);
            }
        }
        // 查询第一次问题接收时间和结案持续时长
        params.put("nodeNames", Arrays.asList(ZlgjConstant.CPZGSH_NODE, ZlgjConstant.ZRBMFX_NODE));
        List<JSONObject> nodeCreateTimeList = zlgjWTDao.queryNodeFirstCreateTime(params);
        Map<String, Map<String, String>> wtId2NodeName2Time = new HashMap<>();
        if (nodeCreateTimeList != null && !nodeCreateTimeList.isEmpty()) {
            for (JSONObject oneObj : nodeCreateTimeList) {
                String wtId = oneObj.getString("wtId");
                if (!wtId2NodeName2Time.containsKey(wtId)) {
                    wtId2NodeName2Time.put(wtId, new HashMap<>());
                }
                String createTime = fullSdf.format(oneObj.getDate("createTime"));
                wtId2NodeName2Time.get(wtId).put(oneObj.getString("nodeName"), createTime);
            }
        }
        // 合并到列表数据中
        for (JSONObject oneData : queryData) {
            String wtId = oneData.getString("wtId");
            // 问题原因
            if (wtId2ReasonList.containsKey(wtId)) {
                List<JSONObject> oneWtReasonList = wtId2ReasonList.get(wtId);
                if (oneWtReasonList != null && !oneWtReasonList.isEmpty()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (JSONObject oneObj : oneWtReasonList) {
                        stringBuilder.append(oneObj.getString("reason")).append("；");
                    }
                    oneData.put("reason", stringBuilder.toString());
                }
            }
            // 临时措施
            if (wtId2LscsList.containsKey(wtId)) {
                List<JSONObject> oneWtLscsList = wtId2LscsList.get(wtId);
                if (oneWtLscsList != null && !oneWtLscsList.isEmpty()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (JSONObject oneObj : oneWtLscsList) {
                        stringBuilder.append(oneObj.getString("dcfa")).append("；");
                    }
                    oneData.put("dcfa", stringBuilder.toString());
                }
            }
            // 永久方案
            if (wtId2CqcsList.containsKey(wtId)) {
                List<JSONObject> oneWtCqcsList = wtId2CqcsList.get(wtId);
                if (oneWtCqcsList != null && !oneWtCqcsList.isEmpty()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    int i = 1;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    for (JSONObject oneObj : oneWtCqcsList) {
                        String wcTime = "";
                        if (oneObj.getDate("wcTime") != null) {
                            wcTime = sdf.format(oneObj.getDate("wcTime"));
                        }
                        String temp = Integer.toString(i) + "、长期措施：" + oneObj.getString("cqcs") + "；完成时间：" + wcTime
                                + "；通知单号：" + oneObj.getString("tzdh") + "；预计切换车号：" + oneObj.getString("yjqhch");
                        stringBuilder.append(temp).append("。\n");
                        i++;
                    }
                    oneData.put("cqcs", stringBuilder.toString());
                }
            }
            String zrbmshTime = oneData.getString("zrbmshTime");
            // 产品主管问题接收时间
            if (wtId2NodeName2Time.containsKey(wtId)
                    && wtId2NodeName2Time.get(wtId).containsKey(ZlgjConstant.CPZGSH_NODE)) {
                String cpzgStartTime = wtId2NodeName2Time.get(wtId).get(ZlgjConstant.CPZGSH_NODE);
                oneData.put("cpzgStartTime", cpzgStartTime);
                String diffDays = computeDateDiff(zrbmshTime, cpzgStartTime);
                oneData.put("cpzgDuration", diffDays);
            }
            // 责任人问题接收时间
            if (wtId2NodeName2Time.containsKey(wtId)
                    && wtId2NodeName2Time.get(wtId).containsKey(ZlgjConstant.ZRBMFX_NODE)) {
                String zrrStartTime = wtId2NodeName2Time.get(wtId).get(ZlgjConstant.ZRBMFX_NODE);
                oneData.put("zrrStartTime", zrrStartTime);
                String diffDays = computeDateDiff(zrbmshTime, zrrStartTime);
                oneData.put("zrrDuration", diffDays);
            }
        }
    }

    // 计算开始和结束之间的天数，如果endTime为空，则使用当前时间
    private String computeDateDiff(String endTime, String startTime) {
        SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar endTimeCalendar = Calendar.getInstance();
        Calendar startTimeCalendar = Calendar.getInstance();
        try {
            if (StringUtils.isBlank(endTime)) {
                endTimeCalendar.setTime(new Date());
            } else {
                endTimeCalendar.setTime(fullSdf.parse(endTime));
            }
            long endTimeVal = endTimeCalendar.getTimeInMillis();
            startTimeCalendar.setTime(fullSdf.parse(startTime));
            long startTimeVal = startTimeCalendar.getTimeInMillis();
            return df.format((endTimeVal - startTimeVal) / 1000 / 3600 / 24.0);
        } catch (Exception e) {
            logger.error("Exception in computeDateDiff", e);
        }
        return "0.00";
    }

    public static String toGetJixingNameByCode(String jiXingCode) {
        if (StringUtils.isBlank(jiXingCode)) {
            return "";
        }
        String jiXingName = "";
        switch (jiXingCode) {
            case "WEIWA":
                jiXingName = "微挖";
                break;
            case "LUNWA":
                jiXingName = "轮挖";
                break;
            case "XIAOWA":
                jiXingName = "小挖";
                break;
            case "ZHONGWA":
                jiXingName = "中挖";
                break;
            case "DAWA":
                jiXingName = "大挖";
                break;
            case "TEWA":
                jiXingName = "特挖";
                break;
            case "SHUJU":
                jiXingName = "属具";
                break;
            case "XINNENGYUAN":
                jiXingName = "新能源";
                break;
            case "HAIWAI":
                jiXingName = "海外";
                break;
            default:
        }
        return jiXingName;
    }

    public static String toGetJixingCodeByName(String jiXingName) {
        if (StringUtils.isBlank(jiXingName)) {
            return "";
        }
        String jiXingCode = "";
        switch (jiXingName) {
            case "微挖":
                jiXingCode = "WEIWA";
                break;
            case "轮挖":
                jiXingCode = "LUNWA";
                break;
            case "小挖":
                jiXingCode = "XIAOWA";
                break;
            case "中挖":
                jiXingCode = "ZHONGWA";
                break;
            case "大挖":
                jiXingCode = "DAWA";
                break;
            case "特挖":
                jiXingCode = "TEWA";
                break;
            case "属具":
                jiXingCode = "SHUJU";
                break;
            case "新能源":
                jiXingCode = "XINNENGYUAN";
                break;
            case "海外":
                jiXingCode = "HAIWAI";
                break;
            default:
        }
        return jiXingCode;
    }

    // 已废弃，请调用数据库字典故障系统（GZXT）
    public static String toGetGzxtCodeByName(String gzxtName) {
        if (StringUtils.isBlank(gzxtName)) {
            return "";
        }
        String gzxtCode = "";
        switch (gzxtName) {
            case "底盘总成":
                gzxtCode = "底盘总成";
                break;
            case "电气系统":
                gzxtCode = "电气系统";
                break;
            case "发动机系统":
                gzxtCode = "发动机系统";
                break;
            case "工作装置":
                gzxtCode = "工作装置";
                break;
            case "回转平台总成":
                gzxtCode = "回转平台总成";
                break;
            case "机棚总成":
                gzxtCode = "机棚总成";
                break;
            case "驾驶室总成":
                gzxtCode = "驾驶室总成";
                break;
            case "空调系统":
                gzxtCode = "空调系统";
                break;
            case "驱动系统":
                gzxtCode = "驱动系统";
                break;
            case "润滑系统":
                gzxtCode = "润滑系统";
                break;
            case "上车液压系统":
                gzxtCode = "上车液压系统";
                break;
            case "下车液压系统":
                gzxtCode = "下车液压系统";
                break;
            case "先导液压系统":
                gzxtCode = "先导液压系统";
                break;
            case "制动系统":
                gzxtCode = "制动系统";
                break;
            case "转向系统":
                gzxtCode = "转向系统";
                break;
            case "传动机构":
                gzxtCode = "传动机构";
                break;
            case "配重":
                gzxtCode = "配重";
                break;
            case "整机性能":
                gzxtCode = "整机性能";
                break;
            case "操控性评价":
                gzxtCode = "操控性评价";
                break;
            case "启动系统":
                gzxtCode = "启动系统";
                break;
            case "覆盖件总成":
                gzxtCode = "覆盖件总成";
                break;
            case "属具":
                gzxtCode = "属具";
                break;
            case "管理":
                gzxtCode = "管理";
                break;
            case "备件":
                gzxtCode = "备件";
                break;
            case "BOM":
                gzxtCode = "BOM";
                break;
            case "图纸":
                gzxtCode = "图纸";
                break;
            case "工序":
                gzxtCode = "工序";
                break;
            case "零部件":
                gzxtCode = "零部件";
                break;
            default:
        }
        return gzxtCode;
    }

    // 质量改进问题回调外部系统，并更新回调状态
    public JsonResult zlgjWtCallBack(String wtId) {
        JsonResult result = new JsonResult(true, "操作成功！");
        Map<String, Object> param = new HashMap<>();
        param.put("busKeyId", wtId);
        List<JSONObject> outSystemInfoList = zlgjWTDao.queryOutSystemByBusKeyId(param);
        if (outSystemInfoList == null || outSystemInfoList.isEmpty()) {
            return result;
        }
        if (outSystemInfoList.size() > 1) {
            result.setSuccess(false);
            result.setMessage("外部系统信息存在多条，请联系管理员！");
            return result;
        }
        JSONObject outSystemInfo = outSystemInfoList.get(0);
        if ("yes".equalsIgnoreCase(outSystemInfo.getString("callBackResult"))) {
            result.setSuccess(false);
            result.setMessage("外部系统回调已成功，禁止重复操作！");
            return result;
        }
        if ("tdm".equalsIgnoreCase(outSystemInfo.getString("outSystem"))) {
            tdmCallBack(outSystemInfo, result);
        }
        if ("tdmII".equalsIgnoreCase(outSystemInfo.getString("outSystem"))) {
            tdmIICallBack(outSystemInfo, result);
        }
        // 更新回调状态
        if (result.getSuccess()) {
            JSONObject updateParam = new JSONObject();
            updateParam.put("id", outSystemInfo.getString("id"));
            updateParam.put("callBackResult", "yes");
            zlgjWTDao.updateOutSystemCallResult(updateParam);
        }
        return result;
    }

    // tdm系统的回调
    private void tdmCallBack(JSONObject outSystemInfo, JsonResult result) {
        String outSystemJson = outSystemInfo.getString("outSystemJson");
        if (StringUtils.isBlank(outSystemJson)) {
            result.setSuccess(false);
            result.setMessage("回调需要的参数为空，请联系管理员！");
            return;
        }
        String callBackUrl = SysPropertiesUtil.getGlobalProperty("tdmCallBackUrl");
        if (StringUtils.isBlank(callBackUrl)) {
            result.setSuccess(false);
            result.setMessage("回调url为空，请联系管理员！");
            return;
        }
        try {
            String rdmKeyId = outSystemInfo.getString("busKeyId");
            // 问题原因
            List<JSONObject> reasonList = getReasonList(rdmKeyId);
            if (reasonList == null || reasonList.isEmpty()) {
                return;
            }
            // 临时措施
            List<JSONObject> lscsList = getLscsList(rdmKeyId, "");
            Map<String, List<JSONObject>> reasonId2Lscs = new HashMap<>();
            for (JSONObject oneData : lscsList) {
                String yyId = oneData.getString("yyId");
                if (!reasonId2Lscs.containsKey(yyId)) {
                    reasonId2Lscs.put(yyId, new ArrayList<>());
                }
                reasonId2Lscs.get(yyId).add(oneData);
            }
            // 原因验证
            List<JSONObject> yyyzList = getyzList(rdmKeyId, "");
            Map<String, List<JSONObject>> reasonId2Yyyz = new HashMap<>();
            for (JSONObject oneData : yyyzList) {
                String yyId = oneData.getString("yyId");
                if (!reasonId2Yyyz.containsKey(yyId)) {
                    reasonId2Yyyz.put(yyId, new ArrayList<>());
                }
                reasonId2Yyyz.get(yyId).add(oneData);
            }
            // 方案验证
            List<JSONObject> fayzList = getfayzList(rdmKeyId, "");
            Map<String, List<JSONObject>> reasonId2Fayz = new HashMap<>();
            for (JSONObject oneData : fayzList) {
                String yyId = oneData.getString("yyId");
                if (!reasonId2Fayz.containsKey(yyId)) {
                    reasonId2Fayz.put(yyId, new ArrayList<>());
                }
                reasonId2Fayz.get(yyId).add(oneData);
            }
            // 永久解决方案
            List<JSONObject> jjfaList = getfajjList(rdmKeyId, "");
            Map<String, List<JSONObject>> reasonId2Jjfa = new HashMap<>();
            for (JSONObject oneData : jjfaList) {
                String yyId = oneData.getString("yyId");
                if (!reasonId2Jjfa.containsKey(yyId)) {
                    reasonId2Jjfa.put(yyId, new ArrayList<>());
                }
                reasonId2Jjfa.get(yyId).add(oneData);
            }
            // 永久解决方案附件(目录)
            Map<String, Object> params = new HashMap<>();
            params.put("mainId", rdmKeyId);
            params.put("fjlx", "ghtp");
            List<JSONObject> fileInfos = zlgjWTDao.getFileListByTypeIdh(params);
            Map<String, List<JSONObject>> jjfaId2Files = new HashMap<>();
            for (JSONObject oneData : fileInfos) {
                String faId = oneData.getString("faId");
                if (!jjfaId2Files.containsKey(faId)) {
                    jjfaId2Files.put(faId, new ArrayList<>());
                }
                jjfaId2Files.get(faId).add(oneData);
            }

            // 构建请求体
            JSONObject requestBody = new JSONObject();
            JSONObject outSystemJsonObj = JSONObject.parseObject(outSystemJson);
            String className = outSystemJsonObj.getString("T_CLASSNAME");
            String tdmKeyId = outSystemJsonObj.getString("T_FAULTID");
            requestBody.put("rdmKeyId", rdmKeyId);
            requestBody.put("tdmKeyId", tdmKeyId);
            requestBody.put("className", className);
            requestBody.put("data", new JSONArray());
            for (JSONObject oneReason : reasonList) {
                // 处理单个原因的数据
                String yyId = oneReason.getString("yyId");
                JSONObject oneReasonResult = new JSONObject();
                oneReasonResult.put("reason", oneReason.getString("reason"));
                requestBody.getJSONArray("data").add(oneReasonResult);
                oneReasonResult.put("lscs", new JSONArray());
                oneReasonResult.put("yyyz", new JSONArray());
                oneReasonResult.put("fayz", new JSONArray());
                oneReasonResult.put("jjfa", new JSONArray());

                // 临时措施
                List<JSONObject> oneReasonLscsList = reasonId2Lscs.get(yyId);
                if (oneReasonLscsList != null) {
                    for (JSONObject oneData : oneReasonLscsList) {
                        JSONObject oneLscsResult = new JSONObject();
                        oneLscsResult.put("dcfa", oneData.getString("dcfa"));
                        oneLscsResult.put("ddpc", oneData.getString("ddpc"));
                        oneLscsResult.put("wcTime", oneData.getString("wcTime") + " 00:00:00");
                        oneLscsResult.put("tzdh", oneData.getString("tzdh"));
                        oneReasonResult.getJSONArray("lscs").add(oneLscsResult);
                    }
                }
                // 原因验证
                List<JSONObject> oneReasonYyyzList = reasonId2Yyyz.get(yyId);
                if (oneReasonYyyzList != null) {
                    for (JSONObject oneData : oneReasonYyyzList) {
                        JSONObject oneYyyzResult = new JSONObject();
                        oneYyyzResult.put("jcjg", oneData.getString("jcjg"));
                        oneYyyzResult.put("jielun", oneData.getString("jielun"));
                        oneReasonResult.getJSONArray("yyyz").add(oneYyyzResult);
                    }
                }
                // 方案验证
                List<JSONObject> oneReasonFayzList = reasonId2Fayz.get(yyId);
                if (oneReasonFayzList != null) {
                    for (JSONObject oneData : oneReasonFayzList) {
                        JSONObject oneFayzResult = new JSONObject();
                        oneFayzResult.put("gjfa", oneData.getString("gjfa"));
                        oneFayzResult.put("sjTime", oneData.getString("sjTime") + " 00:00:00");
                        oneFayzResult.put("yzjg", oneData.getString("yzjg"));
                        oneReasonResult.getJSONArray("fayz").add(oneFayzResult);
                    }
                }
                // 永久解决方案
                List<JSONObject> oneReasonJjfaList = reasonId2Jjfa.get(yyId);
                if (oneReasonJjfaList != null) {
                    for (JSONObject oneData : oneReasonJjfaList) {
                        JSONObject oneJjfaResult = new JSONObject();
                        oneJjfaResult.put("cqcs", oneData.getString("cqcs"));
                        oneJjfaResult.put("yjqhch", oneData.getString("yjqhch"));
                        oneJjfaResult.put("wcTime", oneData.getString("wcTime") + " 00:00:00");
                        oneJjfaResult.put("tzdh", oneData.getString("tzdh"));
                        oneJjfaResult.put("files", new JSONArray());
                        // 附件
                        String faId = oneData.getString("faId");
                        List<JSONObject> oneJjfaFileList = jjfaId2Files.get(faId);
                        if (oneJjfaFileList != null) {
                            for (JSONObject oneFile : oneJjfaFileList) {
                                JSONObject oneFileResult = new JSONObject();
                                oneFileResult.put("fileName", oneFile.getString("fileName"));
                                oneFileResult.put("fileSize", oneFile.getString("fileSize"));
                                oneFileResult.put("creator", oneFile.getString("creator"));
                                oneFileResult.put("createTime", oneFile.getString("CREATE_TIME_") + " 00:00:00");
                                oneFileResult.put("fileContent", getZlgjFileByteStr(oneFile.getString("fileName"),
                                        oneFile.getString("id"), oneFile.getString("wtId")));
                                oneJjfaResult.getJSONArray("files").add(oneFileResult);
                            }
                        }
                        oneReasonResult.getJSONArray("jjfa").add(oneJjfaResult);
                    }
                }
            }

            Map<String, String> reqHeaders = new HashMap<>();
            reqHeaders.put("Content-Type", "application/json;charset=utf-8");
            String rtnContent = HttpClientUtil.postJson(callBackUrl, requestBody.toJSONString(), reqHeaders);
            logger.info("response from tdm,return message:" + rtnContent);
            if (StringUtils.isBlank(rtnContent)) {
                result.setSuccess(false);
                result.setMessage("TDM系统返回值为空！");
                return;
            }
            JSONObject returnObj = JSONObject.parseObject(rtnContent);
            if (!"0".equalsIgnoreCase(returnObj.getString("CODE"))) {
                result.setSuccess(false);
                result.setMessage("TDM系统回调失败，原因：" + returnObj.getString("MSG"));
            }
        } catch (Exception e) {
            logger.error("Exception in tdmCallBack", e);
            result.setSuccess(false);
            result.setMessage("回调TDM系统异常！");
        }
    }

    // tdmII系统的回调
    private void tdmIICallBack(JSONObject outSystemInfo, JsonResult result) {
        String outSystemJson = outSystemInfo.getString("outSystemJson");
        if (StringUtils.isBlank(outSystemJson)) {
            result.setSuccess(false);
            result.setMessage("回调需要的参数为空，请联系管理员！");
            return;
        }
        String callBackUrl = sysDicManager.getBySysTreeKeyAndDicKey("TDMUrl", "tdmIICallBackUrl").getValue();
        if (StringUtils.isBlank(callBackUrl)) {
            result.setSuccess(false);
            result.setMessage("回调url为空，请联系管理员！");
            return;
        }
        try {
            String rdmKeyId = outSystemInfo.getString("busKeyId");
            // 问题原因
            List<JSONObject> reasonList = getReasonList(rdmKeyId);
            if (reasonList == null || reasonList.isEmpty()) {
                return;
            }
            // 永久解决方案
            List<JSONObject> jjfaList = getfajjList(rdmKeyId, "");
            Map<String, List<JSONObject>> reasonId2Jjfa = new HashMap<>();
            for (JSONObject oneData : jjfaList) {
                String yyId = oneData.getString("yyId");
                if (!reasonId2Jjfa.containsKey(yyId)) {
                    reasonId2Jjfa.put(yyId, new ArrayList<>());
                }
                reasonId2Jjfa.get(yyId).add(oneData);
            }
            // 构建请求体
            JSONObject requestBody = new JSONObject();
            JSONObject outSystemJsonObj = JSONObject.parseObject(outSystemJson);
            String tdmIIKeyId = outSystemJsonObj.getString("outSystemId");
            requestBody.put("rdmKeyId", rdmKeyId);
            requestBody.put("tdmIIKeyId", tdmIIKeyId);
            requestBody.put("data", new JSONArray());
            for (JSONObject oneReason : reasonList) {
                // 处理单个原因的数据
                String yyId = oneReason.getString("yyId");
                JSONObject oneReasonResult = new JSONObject();
                oneReasonResult.put("reason", oneReason.getString("reason"));
                requestBody.getJSONArray("data").add(oneReasonResult);
                oneReasonResult.put("jjfa", new JSONArray());
                // 永久解决方案
                List<JSONObject> oneReasonJjfaList = reasonId2Jjfa.get(yyId);
                if (oneReasonJjfaList != null) {
                    for (JSONObject oneData : oneReasonJjfaList) {
                        JSONObject oneJjfaResult = new JSONObject();
                        oneJjfaResult.put("cqcs", oneData.getString("cqcs"));
                        oneJjfaResult.put("yjqhch", oneData.getString("yjqhch"));
                        oneJjfaResult.put("wcTime", oneData.getString("wcTime") + " 00:00:00");
                        oneJjfaResult.put("tzdh", oneData.getString("tzdh"));
                        oneJjfaResult.put("files", new JSONArray());
                        oneReasonResult.getJSONArray("jjfa").add(oneJjfaResult);
                    }
                }
            }

            Map<String, String> reqHeaders = new HashMap<>();
            reqHeaders.put("Content-Type", "application/json;charset=utf-8");
            String rtnContent = HttpClientUtil.postJson(callBackUrl, requestBody.toJSONString(), reqHeaders);
            logger.info("response from tdmII,return message:" + rtnContent);
            if (StringUtils.isBlank(rtnContent)) {
                result.setSuccess(false);
                result.setMessage("TDMII系统返回值为空！");
                return;
            }
            JSONObject returnObj = JSONObject.parseObject(rtnContent);
            if (!returnObj.getBoolean("success")) {
                result.setSuccess(false);
                result.setMessage("TDMII系统回调失败，原因：" + returnObj.getString("message"));
            }
        } catch (Exception e) {
            logger.error("Exception in tdmIICallBack", e);
            result.setSuccess(false);
            result.setMessage("回调TDMII系统异常！");
        }
    }

    // 读取某个质量改进图片为byte字符串
    private String getZlgjFileByteStr(String fileName, String fileId, String wtId) {
        try {
            String fileBasePath = WebAppUtil.getProperty("zlgjFilePathBase_preview");
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储根路径");
                return "";
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String relativeFilePath = File.separator + wtId;
            String realFileName = fileId + "." + suffix;
            String fullFilePath = fileBasePath + relativeFilePath + File.separator + realFileName;
            // 创建文件实例
            File file = new File(fullFilePath);
            BASE64Encoder base64Encoder = new BASE64Encoder();
            return base64Encoder.encodeBuffer(FileUtils.readFileToByteArray(file));
        } catch (Exception e) {
            logger.error("Exception in getZlgjFileByteStr", e);
            return "";
        }
    }

    // 结构化文档接口
    public JSONObject getApiList(JSONObject result, String searchValue) {

        // 以searchValue为查询条件 返回查询到的列表
        String resUrlBase = "http://wjrdm.xcmg.com/rdm/xjsdr/core/zlgj/editPage.do?action=detail&wtId=";
        List<JSONObject> resList = zlgjWTDao.queryApiList(searchValue);
        for (JSONObject oneRes : resList) {
            oneRes.put("url", resUrlBase + oneRes.getString("wtid"));

        }
        if (resList.size() > 0) {
            result.put("result", resList);
        } else {
            result.put("message", "无查询结果，请检查搜索条件！");
        }
        return result;
    }

    public JsonResult checkFile(String wtId, String checkType, String rounds) {
        JsonResult result = new JsonResult(true, "成功");
        JSONObject params = new JSONObject();
        String detailInfo = "";
        List<JSONObject> fileInfos = new ArrayList<>();
        if ("reason".equals(checkType)) {
            List<JSONObject> lscsList = getLscsList(wtId, rounds);
            List<JSONObject> yyyzList = getyzList(wtId, rounds);
            List<JSONObject> fayzList = getfayzList(wtId, rounds);
            List<JSONObject> jjfaList = getfajjList(wtId, rounds);
            boolean lcfile = true;
            boolean yyfile = true;
            boolean fafile = true;
            boolean jjfile = true;
            if (lscsList != null && !lscsList.isEmpty()) {
                for (JSONObject lscs : lscsList) {
                    String detailId = lscs.getString("csId");
                    params.put("faId", detailId);
                    fileInfos = zlgjWTDao.getFileListByTypeIdh(params);
                    if (fileInfos == null || fileInfos.isEmpty()) {
                        detailInfo +=
                                "临时措施-原因“" + lscs.getString("reason") + "”;轮次“" + lscs.getString("rounds") + "”;<br>";
                        lcfile = false;
                    }
                }
            }
            if (yyyzList != null && !yyyzList.isEmpty()) {
                for (JSONObject yyyz : yyyzList) {
                    String detailId = yyyz.getString("yzyyId");
                    params.put("faId", detailId);
                    fileInfos = zlgjWTDao.getFileListByTypeIdh(params);
                    if (fileInfos == null || fileInfos.isEmpty()) {
                        detailInfo +=
                                "原因验证-原因“" + yyyz.getString("reason") + "”;轮次“" + yyyz.getString("rounds") + "”;<br>";
                        yyfile = false;
                    }
                }
            }
            if (fayzList != null && !fayzList.isEmpty()) {
                for (JSONObject fayz : fayzList) {
                    String detailId = fayz.getString("faId");
                    params.put("faId", detailId);
                    fileInfos = zlgjWTDao.getFileListByTypeIdh(params);
                    if (fileInfos == null || fileInfos.isEmpty()) {
                        detailInfo +=
                                "方案验证-原因“" + fayz.getString("reason") + "”;轮次“" + fayz.getString("rounds") + "”;<br>";
                        fafile = false;
                    }
                }
            }
            if (jjfaList != null && !jjfaList.isEmpty()) {
                for (JSONObject jjfa : jjfaList) {
                    String detailId = jjfa.getString("faId");
                    params.put("faId", detailId);
                    fileInfos = zlgjWTDao.getFileListByTypeIdh(params);
                    if (fileInfos == null || fileInfos.isEmpty()) {
                        detailInfo +=
                                "永久解决方案-原因“" + jjfa.getString("reason") + "”;轮次“" + jjfa.getString("rounds") + "”;<br>";
                        jjfile = false;
                    }
                }
            }

            if (!fafile || !yyfile || !lcfile || !jjfile) {
                result.setMessage("下列明细未上传附件:<br>" + detailInfo + "请上传附件后再提交!");
                result.setSuccess(false);
                return result;
            }
            return result;
        } else if ("risk".equals(checkType)) {
            JSONObject paramJson = new JSONObject();
            paramJson.put("wtId", wtId);
            paramJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            paramJson.put("riskLevel", "yes");
            List<JSONObject> riskList = zlgjWTDao.getRiskList(paramJson);
            boolean riskfile = true;
            if (riskList != null && !riskList.isEmpty()) {
                for (JSONObject jjfa : riskList) {
                    String detailId = jjfa.getString("riskId");
                    params.put("faId", detailId);
                    fileInfos = zlgjWTDao.getFileListByTypeIdh(params);
                    if (fileInfos == null || fileInfos.isEmpty()) {
                        detailInfo += "风险排查及整改-排查内容“" + jjfa.getString("riskContent") + "”;<br>";
                        riskfile = false;
                    }
                }
            }
            if (!riskfile) {
                result.setMessage("下列明细未上传附件:<br>" + detailInfo + "请上传附件后再提交!");
                result.setSuccess(false);
                return result;
            }
        } else if ("pic".equals(checkType)) {
            Map<String, Object> param = new HashMap<>();
            param.put("mainId", wtId);
            param.put("fjlx", "gztp");
            List<JSONObject> picList = zlgjWTDao.getFileListByTypeId(param);
            if (picList.isEmpty() || picList == null) {
                result.setMessage("请上传问题图片附件!");
                result.setSuccess(false);
                return result;
            }
        }
        // } else if ("gzxg".equals(checkType)) {
        // Map<String, Object> param = new HashMap<>();
        // param.put("mainId", wtId);
        // param.put("fjlx", "gzxg");
        // List<JSONObject> picList = zlgjWTDao.getFileListByTypeId(param);
        // if (picList.isEmpty() || picList == null) {
        // result.setMessage("请上传改进效果跟踪证明材料!");
        // result.setSuccess(false);
        // return result;
        // }
        // }
        return result;
    }

    public JsonResult checkNoFile(String wtId) {
        JsonResult result = new JsonResult(true, "成功");
        JSONObject params = new JSONObject();
        // 不改进
        params.put("mainId", wtId);
        params.put("fjlx", "gjfj");
        List<JSONObject> gjfjList = zlgjWTDao.getFileListByTypeId(params);
        boolean gjfile = gjfjList == null || gjfjList.isEmpty() ? false : true;
        if (!gjfile) {
            result.setMessage("不改进附件说明未提交:<br>" + "请上传附件后再提交!");
            return result;
        }
        return result;
    }

    public JsonPageResult<?> getZlgjListCq(HttpServletRequest request, HttpServletResponse response, boolean doPage,
                                           Map<String, Object> params, boolean queryTaskUser, String defaultSortField) {
        JsonPageResult result = new JsonPageResult(true);
        String wtlxtype = RequestUtil.getString(request, "wtlxtype", "");
        String chartType = RequestUtil.getString(request, "chartType", "");
        String czxpj = RequestUtil.getString(request, "czxpj", "");
        String zrType = RequestUtil.getString(request, "zrType", "");
        String deptName = RequestUtil.getString(request, "deptName", "");
        params.put("wtlxtype", wtlxtype);
        params.put("chartType", chartType);
        params.put("czxpj", czxpj);
        if ("问题处理人".equalsIgnoreCase(zrType)) {
            params.put("zrrType", "yes");
        } else if ("流程处理人".equalsIgnoreCase(zrType)) {
            params.put("processType", "yes");
        }else {
            params.put("ssbmType", "yes");
        }
        rdmZhglUtil.addOrder(request, params, defaultSortField, "desc");
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    if ("rdTimeStart".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("rdTimeEnd".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    if ("yzcd".equalsIgnoreCase(name)) {
                        params.put(name, Arrays.asList(value.split(",", -1)));
                    } else {
                        params.put(name, value);
                    }
                }
            }
        }
        // 增加角色过滤的条件
        RdmCommonUtil.addListAllQueryRoleExceptDraft(params, ContextUtil.getCurrentUserId(),
                ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> zlgjList = zlgjWTDao.queryZlgjListCq(params);
        for (JSONObject zlgj : zlgjList) {
            if (zlgj.get("yzTime") != null) {
                zlgj.put("yzTime", DateUtil.formatDate((Date) zlgj.get("yzTime"), "yyyy-MM-dd"));
            }
            if (zlgj.get("yjTime") != null) {
                zlgj.put("yjTime", DateUtil.formatDate((Date) zlgj.get("yjTime"), "yyyy-MM-dd"));
            }
            String status = zlgj.getString("status");
            String zrbmshTime = zlgj.getString("zrbmshTime");
            if (StringUtils.isNotBlank(zrbmshTime) || BpmInst.STATUS_DISCARD.equalsIgnoreCase(status)) {
                zlgj.put("jiean", "已完成");
            } else if (StringUtils.isBlank(zrbmshTime) && BpmInst.STATUS_RUNNING.equalsIgnoreCase(status)) {
                zlgj.put("jiean", "进行中");
            }
        }
        // 查询当前处理人
        if (queryTaskUser) {
            xcmgProjectManager.setTaskCurrentUserJSON(zlgjList);
        }
        List<String> zrrIds = new ArrayList<>();//责任人Id
        List<String> currentProcessUserIds = new ArrayList<>();//流程处理人Id

        for (JSONObject oneData : zlgjList) {
            String zrrId = oneData.getString("zrrId");
            if (StringUtils.isNotBlank(zrrId)) {
                zrrIds.add(zrrId);
            }
            String processUserId = oneData.getString("currentProcessUserId");
            if (StringUtils.isNotBlank(processUserId)) {
                String[] processUserIdStr = processUserId.split(",");
                for (String oneId : processUserIdStr) {
                    currentProcessUserIds.add(oneId);
                }
            }
        }
        Map<String, String> zrrId2DeptName = new HashMap<>();
        Map<String, String> processUserId2DeptName = new HashMap<>();
        if (!zrrIds.isEmpty() && zrrIds != null) {
            //所有的责任人部门信息
            Map<String, Object> zrrIdparams = new HashMap<>();
            zrrIdparams.put("userIds", zrrIds);
            List<JSONObject> zrrDeptInfos = commonInfoDao.getDeptInfoByUserIds(zrrIdparams);
            for (JSONObject zrrDeptInfo : zrrDeptInfos) {
                zrrId2DeptName.put(zrrDeptInfo.getString("userId"), zrrDeptInfo.getString("deptName"));
            }
        }
        if (!currentProcessUserIds.isEmpty() && currentProcessUserIds != null) {
            //所有的流程处理人部门信息
            Map<String, Object> processUserIdparams = new HashMap<>();
            processUserIdparams.put("userIds", currentProcessUserIds);
            List<JSONObject> processUserDeptInfos = commonInfoDao.getDeptInfoByUserIds(processUserIdparams);
            for (JSONObject processUserDeptInfo : processUserDeptInfos) {
                processUserId2DeptName.put(processUserDeptInfo.getString("userId"), processUserDeptInfo.getString("deptName"));
            }
        }

        for (JSONObject oneData : zlgjList) {
            //责任人部门
            oneData.put("zrrDeptName", zrrId2DeptName.get(oneData.getString("zrrId")));
            //流程处理人部门
            StringBuilder stringBuilder = new StringBuilder();
            String processUserId = oneData.getString("currentProcessUserId");
            if (StringUtils.isNotBlank(processUserId)) {
                String[] processUserIdStr = processUserId.split(",");
                for (String oneId : processUserIdStr) {
                    String currentProcessUserDeptName = processUserId2DeptName.get(oneId);
                    if (stringBuilder.indexOf(currentProcessUserDeptName) == -1) {
                        stringBuilder.append(currentProcessUserDeptName).append(",");
                    }
                    if (stringBuilder.length() > 0) {
                        oneData.put("currentProcessUserDeptName", stringBuilder.substring(0, stringBuilder.length() - 1));
                    }
                }
            }
        }
        if(StringUtils.isNotBlank(deptName)){
            if ("问题处理人".equalsIgnoreCase(zrType)) {
                zlgjList=zlgjList.stream().filter(j->deptName.equalsIgnoreCase(j.getString("zrrDeptName"))).collect(Collectors.toList());
            } else if ("流程处理人".equalsIgnoreCase(zrType)) {
                zlgjList=zlgjList.stream().filter(j->StringUtils.isNotBlank(j.getString("currentProcessUserDeptName"))&&j.getString("currentProcessUserDeptName").contains(deptName)).collect(Collectors.toList());
            }
        }
        // 总数设置并分页
        result.setTotal(zlgjList.size());
        List<JSONObject> finalSubProjectList = new ArrayList<JSONObject>();
        if (doPage) {
            // 根据分页进行subList截取
            int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
            int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
            int startSubListIndex = pageIndex * pageSize;
            int endSubListIndex = startSubListIndex + pageSize;
            if (startSubListIndex < zlgjList.size()) {
                finalSubProjectList = zlgjList.subList(startSubListIndex,
                        endSubListIndex < zlgjList.size() ? endSubListIndex : zlgjList.size());
            }
        } else {
            finalSubProjectList = zlgjList;
        }
        result.setData(finalSubProjectList);
        return result;
    }

    public void exportWtsbCqExcel(HttpServletResponse response, HttpServletRequest request) {
        Map<String, Object> params = new HashMap<>();
        JsonPageResult queryDataResult = getZlgjListCq(request, response, false, params, true, "zwt.smallJiXing");
        String dataType = "";
        if (params.get("wtlxtype") != null && StringUtils.isNotBlank(params.get("wtlxtype").toString())) {
            dataType = params.get("wtlxtype").toString();
        }
        String titleName = toGetWtNameByCode(dataType);
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + titleName + "质量问题列表";
        String[] fieldNames = {"问题类型", "问题响应要求", "问题严重度", "问题编号", "机型类别", "机型", "整机编号", "工作小时", "施工工况", "故障系统",
                "故障零件", "问题描述", "现场处置方法", "故障率", "零部件供应商", "问题可能原因", "临时措施", "永久方案", "责任部门", "第一责任人", "问题处理人", "问题处理人部门",
                "第一责任人问题接收时间", "第一责任人持续时长(天)", "问题处理人问题接收时间", "问题处理人持续时长(天)", "结案时间", "结案状态", "是否改进", "不改进理由", "流程处理人", "流程处理人部门",
                "流程任务", "流程状态", "提报人", "提报部门", "提报时间", "排放标准"};
        String[] fieldCodes = {"wtlx", "jjcd", "yzcd", "zlgjNumber", "jiXingName", "smallJiXing", "zjbh", "gzxs",
                "sggk", "gzxt", "gzlj", "wtms", "xcczfa", "gzl", "lbjgys", "reason", "dcfa", "cqcs", "ssbmName",
                "zrcpzgName", "zrrName", "zrrDeptName", "cpzgStartTime", "cpzgDuration", "zrrStartTime", "zrrDuration", "zrbmshTime",
                "jiean", "ifgj", "noReason", "currentProcessUser", "currentProcessUserDeptName", "currentProcessTask", "status", "applyUserName",
                "applyUserDeptName", "CREATE_TIME", "pfbz"};
        // 再处理数据，补充缺失的数据
        List<JSONObject> queryData = queryDataResult.getData();
        processWtsbExportData(queryData);
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(queryData, fieldNames, fieldCodes, titleName);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    public void judgeRes(JSONObject result, String resId, String leaderId) {
        JSONObject resDeps = commonInfoManager.queryDeptByUserId(resId);
        if (resDeps == null || resDeps.isEmpty()) {
            result.put("result", false);
            return;
        }
        String resDeptId = resDeps.getString("deptId");
        String resDeptName = resDeps.getString("deptName");
        ;
        if (StringUtils.isBlank(resDeptId) || StringUtils.isBlank(resDeptName)) {
            result.put("result", false);
            return;
        }
        JSONObject leaderDeps = commonInfoManager.queryDeptByUserId(leaderId);
        if (leaderDeps == null || leaderDeps.isEmpty()) {
            result.put("result", false);
            return;
        }
        String leaderDeptId = leaderDeps.getString("deptId");
        String leaderDeptName = leaderDeps.getString("deptName");
        ;
        if (StringUtils.isBlank(leaderDeptId) || StringUtils.isBlank(leaderDeptName)) {
            result.put("result", false);
            return;
        }
        if (!leaderDeptId.equals(resDeptId)) {
            result.put("result", false);
            return;
        }
    }

    /**
     * 向PDM创建符合条件的PR
     *
     * @param wtId
     * @param result
     */
    public void createPr2Pdm(String wtId, JsonResult result) throws Exception {
        String pdmCreatePrUrl = SysPropertiesUtil.getGlobalProperty("pdmCreatePrUrl");
        if (StringUtils.isBlank(pdmCreatePrUrl)) {
            logger.error("未找到向PDM创建PR的url配置");
            result.setSuccess(false);
            result.setMessage("操作失败，未找到向PDM创建PR的url配置");
            return;
        }
        String user = "CN_WJ_RDM";
        String pwd = "IMktH72CTgFt";
        Map<String, String> reqHeaders = new HashMap<>();
        reqHeaders.put("Authorization",
                "Basic " + Base64.getUrlEncoder().encodeToString((user + ":" + pwd).getBytes()));
        JSONObject requestBody = new JSONObject();
        // 拼接请求体内容
        JSONObject zlgjObj = getZlgjDetail(wtId);
        // 不允许重复提交
        if (StringUtils.isNotBlank(zlgjObj.getString("pdmPrNo"))) {
            logger.error("已经创建PR，单号为" + zlgjObj.getString("pdmPrNo"));
            result.setSuccess(false);
            result.setMessage("操作失败，" + "已经创建PR，单号为" + zlgjObj.getString("pdmPrNo"));
            return;
        }
        // 判断只有挖机研究院下面的部门和工艺技术部的允许提报
        boolean wjyjy = commonInfoManager.judgeIsTechDept(zlgjObj.getString("ssbmId"));
        boolean gyjsb = RdmConst.GYJSB_NAME.equalsIgnoreCase(zlgjObj.getString("ssbmName"));
        if (!wjyjy && !gyjsb) {
            result.setSuccess(false);
            result.setMessage("操作失败，当前仅支持“责任部门”是挖机研究院各所或工艺技术部的问题单推送至PDM");
            return;
        }
        // 问题处理人要有对应的Windchill账号
        OsUser zrrUserObj = osUserManager.get(zlgjObj.getString("zrrId"));
        if (zrrUserObj == null || StringUtils.isBlank(zrrUserObj.getPdmUserNo())) {
            result.setSuccess(false);
            result.setMessage("操作失败，当前问题处理人“" + zlgjObj.getString("zrrName") + "”没有在RDM中配置PDM关联账号");
            return;
        }
        String issuecategory = wjyjy ? ZlgjConstant.ISSUE_CATEGORY_E : ZlgjConstant.ISSUE_CATEGORY_P;
        String severitylevel =
                (zlgjObj.getString("yzcd").equalsIgnoreCase("A") || zlgjObj.getString("yzcd").equalsIgnoreCase("B"))
                        ? ZlgjConstant.SEVERITYLEVEL_SERIOUS : ZlgjConstant.SEVERITYLEVEL_COMMON;
        requestBody.put("issue_no", zlgjObj.getString("zlgjNumber"));
        requestBody.put("source_system", "XE-RDM");
        requestBody.put("name", "挖机RDM问题报告" + "（" + zlgjObj.getString("zlgjNumber") + "）");
        requestBody.put("issuecategory", issuecategory);
        requestBody.put("description", zlgjObj.getString("wtms"));
        requestBody.put("occurrencetime", DateUtil.formatDate(zlgjObj.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
        requestBody.put("severitylevel", severitylevel);
        requestBody.put("reviewers", zrrUserObj.getPdmUserNo());
        requestBody.put("product_no", zlgjObj.getString("zjbh"));
        requestBody.put("issue_part_no", StringUtil.nullToString(zlgjObj.getString("issuePartNo")));
        requestBody.put("issue_part_name",
                StringUtil.nullToString(zlgjObj.getString("gzxt")) + "，"
                        + StringUtil.nullToString(zlgjObj.getString("gzbw")) + "，"
                        + StringUtil.nullToString(zlgjObj.getString("gzlj")));
        requestBody.put("issue_department", zlgjObj.getString("applyUserDeptName"));
        requestBody.put("issue_submitter", zlgjObj.getString("applyUserName"));
        requestBody.put("productName", ZlgjConstant.PRODUCTNAME_WJ);
        // 查询问题附件
        Map<String, String> fileId2Name = new HashMap<>();
        String filePathBase = WebAppUtil.getProperty("zlgjFilePathBase");
        if (StringUtils.isNotBlank(filePathBase)) {
            List<JSONObject> files = getZlgjFileList(Arrays.asList(wtId));
            if (files != null && !files.isEmpty()) {
                for (JSONObject oneFile : files) {
                    fileId2Name.put(oneFile.getString("id"), oneFile.getString("fileName"));
                }
            }
        }

        // 调用接口
        HttpPostFormData post = new HttpPostFormData(pdmCreatePrUrl);
        post.addTextParams("jsonStr", requestBody.toJSONString());
        for (Map.Entry<String, String> entry : fileId2Name.entrySet()) {
            String fileRealName = entry.getValue();
            String suffix = CommonFuns.toGetFileSuffix(fileRealName);
            String fullPath = filePathBase + File.separator + wtId + File.separator + entry.getKey() + "." + suffix;
            JSONObject fileObj = new JSONObject();
            fileObj.put("fileName", fileRealName);
            fileObj.put("file", new File(fullPath));
            post.addFileparams(entry.getKey(), fileObj);
        }
        logger.info("问题单ID“" + wtId + "”调用pdmCreatePR，传递内容:" + requestBody.toJSONString());
        String returnMsg = post.send(reqHeaders);
        // 解析返回内容
        logger.info("问题单ID“" + wtId + "”调用pdmCreatePR，返回内容:" + returnMsg);
        if (StringUtils.isBlank(returnMsg)) {
            logger.error("问题单ID“" + wtId + "”创建PR接口返回内容为空");
            result.setSuccess(false);
            result.setMessage("操作失败，接口返回内容为空");
            return;
        }
        JSONObject rtnContentObj = JSONObject.parseObject(returnMsg);
        String prNo = rtnContentObj.getString("data");
        String resultCode = rtnContentObj.getString("result");
        String message = rtnContentObj.getString("message");
        result.setSuccess("S".equalsIgnoreCase(resultCode) ? true : false);
        result.setMessage(message);
        result.setData(prNo);
        // 如果成功则将PRNO写入数据库
        JSONObject updateParam = new JSONObject();
        updateParam.put("wtId", wtId);
        updateParam.put("pdmPrNo", prNo);
        zlgjWTDao.updatePdmPrNo(updateParam);
    }

    /**
     * 从PDM查询PR状态
     *
     * @param wtId
     * @param result
     * @throws Exception
     */
    public void queryPrFromPdm(String wtId, JsonResult result) throws Exception {
        String pdmQueryPrUrl = SysPropertiesUtil.getGlobalProperty("pdmQueryPrUrl");
        if (StringUtils.isBlank(pdmQueryPrUrl)) {
            logger.error("未找到从PDM查询PR的url配置");
            return;
        }
        String user = "CN_WJ_RDM";
        String pwd = "IMktH72CTgFt";
        Map<String, String> reqHeaders = new HashMap<>();
        reqHeaders.put("Authorization",
                "Basic " + Base64.getUrlEncoder().encodeToString((user + ":" + pwd).getBytes()));
        reqHeaders.put("Accept", "application/json");
        // 查询之前已创建的PRNO
        JSONObject zlgjObj = getZlgjDetail(wtId);
        String prNo = zlgjObj.getString("pdmPrNo");
        if (StringUtils.isBlank(prNo)) {
            result.setSuccess(false);
            result.setMessage("查询失败，" + "尚未创建PR");
            return;
        }
        pdmQueryPrUrl += "?PRNO=" + prNo;
        HttpClientUtil.HttpRtnModel rtnModel = HttpClientUtil.getFromUrlHreader(pdmQueryPrUrl, reqHeaders);
        if (StringUtils.isBlank(rtnModel.getContent())) {
            result.setSuccess(false);
            result.setMessage("查询失败，接口返回内容为空");
            return;
        }
        JSONObject rtnContentObj = JSONObject.parseObject(rtnModel.getContent());
        String prStatus = rtnContentObj.getString("data");
        String resultCode = rtnContentObj.getString("result");
        String message = rtnContentObj.getString("message");
        String decr_no = rtnContentObj.getString("decr_no");
        String decr_state = rtnContentObj.getString("decr_state");
        String decn_no = rtnContentObj.getString("decn_no");
        String decn_state = rtnContentObj.getString("decn_state");
        result.setSuccess("S".equalsIgnoreCase(resultCode) ? true : false);
        result.setMessage(message);
        result.setData("PR单号：" + prNo + "，PR状态：" + prStatus + "，DECR单号：" + decr_no + "，DECR状态：" + decr_state
                + "，DECN单号：" + decn_no + "，DECN状态：" + decn_state);
    }
}
