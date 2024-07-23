package com.redxun.zlgjNPI.core.manager;

import java.io.File;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.zlgjNPI.core.dao.ZlgjDao;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2021 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2021/2/23 10:43
 */
@Service
public class ZlgjService {
    private Logger logger = LoggerFactory.getLogger(ZlgjService.class);
    @Autowired
    private ZlgjDao zlgjDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private LoginRecordManager loginRecordManager;

    public JsonPageResult<?> getGjllFileList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult();
        String docName = RequestUtil.getString(request, "docName", "");
        Map<String, Object> param = new HashMap<>();
        if (StringUtils.isNotBlank(docName)) {
            param.put("docName", docName);
        }
        rdmZhglUtil.addPage(request, param);
        rdmZhglUtil.addOrder(request, param, "CREATE_TIME_", "asc");
        List<JSONObject> gjllFileList = zlgjDao.queryGjllFileList(param);
        if (gjllFileList != null && !gjllFileList.isEmpty()) {
            for (JSONObject oneFile : gjllFileList) {
                if (oneFile.getDate("CREATE_TIME_") != null) {
                    oneFile.put("CREATE_TIME_",
                        DateUtil.formatDate(oneFile.getDate("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                }
            }
        }
        result.setData(gjllFileList);
        int count = zlgjDao.countGjllFileList(param);
        result.setTotal(count);
        return result;
    }

    public void saveGjllUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        String filePathBase = WebAppUtil.getProperty("zlgjllFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find zlgjllFilePathBase");
            return;
        }
        try {
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase;
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
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            zlgjDao.insertGjllFile(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public void deleteOneGjllFile(String fileId, String fileName) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, "", WebAppUtil.getProperty("zlgjllFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        zlgjDao.deleteGjllFile(param);
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }


    // 判断问题处理人的选择是否合适
    public void judgeWtProcessor(JSONObject result, String zrrId, String ssbmId, String ssbmName) {
        // 找到问题处理人的主部门
        Map<String, Object> params = new HashMap<>();
        params.put("userId", zrrId);
        params.put("typeKeyList", Arrays.asList(RdmConst.GROUP_USER_BELONG));
        List<Map<String, Object>> currentUserDeps = xcmgProjectOtherDao.queryUserDeps(params);
        if (currentUserDeps == null || currentUserDeps.isEmpty()) {
            result.put("result", false);
            return;
        }
        String userDeptId = "";
        String userDeptName = "";
        for (Map<String, Object> oneData : currentUserDeps) {
            if (oneData.get("PARTY1_") != null && oneData.get("NAME_") != null
                && "YES".equalsIgnoreCase(oneData.get("IS_MAIN_").toString())) {
                userDeptId = oneData.get("PARTY1_").toString();
                userDeptName = oneData.get("NAME_").toString();
                break;
            }
        }
        if (StringUtils.isBlank(userDeptId) || StringUtils.isBlank(userDeptName)) {
            result.put("result", false);
            return;
        }
        // 判断所属部门是否是挖掘机械研究院下的
        JSONObject isJSZX = loginRecordManager.judgeIsJSZX(ssbmId, ssbmName);
        boolean ssbmJszx = isJSZX.getBoolean("isJSZX");
        if (!ssbmJszx) {
            // 责任部门不是挖掘机械研究院下的，则要求处理人部门与责任部门一致
            if (!userDeptId.equalsIgnoreCase(ssbmId)) {
                result.put("result", false);
            }
        } else {
            // 否则，判断处理人部门是否也是挖掘机械研究院下的，如果不是则失败
            JSONObject isUserDeptJSZX = loginRecordManager.judgeIsJSZX(userDeptId, userDeptName);
            boolean userDeptJszx = isUserDeptJSZX.getBoolean("isJSZX");
            if (!userDeptJszx) {
                result.put("result", false);
            }
        }
    }
}
