package com.redxun.rdmCommon.core.manager;

import java.io.File;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.dao.ImportUserDao;
import com.redxun.rdmCommon.core.util.PasswordUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.sys.org.dao.OsUserDao;
import com.redxun.sys.org.entity.OsInstUsers;
import com.redxun.sys.org.entity.OsRelInst;

@Service
public class ImportUserManager {
    @Autowired
    OsUserDao osUserDao;
    @Autowired
    ImportUserDao importUserDao;
    @Autowired
    CommonInfoDao commonInfoDao;

    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "XE-RDM系统用户模板.xls";
            // 创建文件实例
            File file =
                new File(ImportUserManager.class.getClassLoader().getResource("templates/common/" + fileName).toURI());
            String finalDownloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");

            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 finalDownloadFileName
            headers.setContentDispositionFormData("attachment", finalDownloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers,
                org.springframework.http.HttpStatus.OK);
        } catch (Exception e) {
            return null;
        }
    }

    public void importUser(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            MultipartFile fileObj = multipartRequest.getFile("importFile");
            if (fileObj == null) {
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            HSSFWorkbook wb = new HSSFWorkbook(fileObj.getInputStream());
            HSSFSheet sheet = wb.getSheet("用户信息");
            if (sheet == null) {
                result.put("message", "数据导入失败，找不到用户信息工作簿！");
                return;
            }
            int rowNum = sheet.getPhysicalNumberOfRows();
            if (rowNum < 1) {
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }
            // 解析标题部分
            HSSFRow titleRow = sheet.getRow(0);
            if (titleRow == null) {
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }
            List<String> titleList = new ArrayList<>();
            for (int i = 0; i < titleRow.getLastCellNum(); i++) {
                titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
            }
            if (rowNum < 2) {
                result.put("message", "数据导入完成，数据行为空！");
                return;
            }

            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> insertList = new ArrayList<>();
            for (int i = 1; i < rowNum; i++) {
                HSSFRow row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRow(row, insertList, titleList);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            for (int i = 0; i < insertList.size(); i++) {
                JSONObject resultJson = saveOtherInfo(insertList.get(i));
                if (!resultJson.getBoolean("success")) {
                    result.put("message", resultJson.getString("message"));
                    return;
                }
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    /**
     * 处理每一行数据
     */
    private JSONObject generateDataFromRow(HSSFRow row, List<Map<String, Object>> insertList, List<String> titleList) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> itemRowMap = new HashMap<>(16);
        String id = IdUtil.getId();
        DataFormatter dataFormatter = new DataFormatter();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            HSSFCell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = StringUtils.trim(dataFormatter.formatCellValue(cell));
            }
            switch (title) {
                case "姓名":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "姓名为空或不存在");
                        return oneRowCheck;
                    }
                    itemRowMap.put("userName", cellValue);
                    break;
                case "OA账号名":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "OA账号名为空");
                        return oneRowCheck;
                    }
                    JSONObject osUser = importUserDao.getUserByNo(cellValue);
                    if (osUser != null) {
                        oneRowCheck.put("message", "OA账号名已存在");
                        return oneRowCheck;
                    }
                    itemRowMap.put("userNo", cellValue);
                    break;
                case "所在部门":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "所在部门为空或不存在");
                        return oneRowCheck;
                    }
                    itemRowMap.put("deptName", cellValue);
                    JSONObject paramJson = new JSONObject();
                    paramJson.put("deptName", cellValue);
                    paramJson.put("DimId", "1");
                    JSONObject groupJson = importUserDao.getGroupId(paramJson);
                    if (groupJson == null) {
                        oneRowCheck.put("message", "系统中不存在此部门");
                        return oneRowCheck;
                    } else {
                        itemRowMap.put("groupId", groupJson.getString("groupId"));
                    }
                    break;
                case "手机号码":
                    if (!StringUtils.isBlank(cellValue)) {
                        itemRowMap.put("telephone", cellValue);
                    }
                    break;
                case "身份证号码":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "身份证号码为空或不存在");
                        return oneRowCheck;
                    }
                    itemRowMap.put("CERT_NO_", cellValue);
                    break;
                case "PDM账号":
                    itemRowMap.put("pdmUserNo", cellValue);
                    break;
                case "岗位":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "岗位为空或不存在");
                        return oneRowCheck;
                    }
                    paramJson = new JSONObject();
                    paramJson.put("groupType", "GW");
                    paramJson.put("groupName", cellValue);
                    List<JSONObject> list = commonInfoDao.getGroupInfo(paramJson);
                    if (list != null) {
                        if (list.isEmpty()) {
                            oneRowCheck.put("message", "系统中该岗位不存在，请先维护！");
                            return oneRowCheck;
                        } else if (list.size() == 1) {
                            itemRowMap.put("postId", list.get(0).getString("GROUP_ID_"));
                            itemRowMap.put("postDimId", list.get(0).getString("DIM_ID_"));
                        } else {
                            oneRowCheck.put("message", "获取岗位信息有误！");
                            return oneRowCheck;
                        }
                    } else {
                        oneRowCheck.put("message", "获取岗位信息有误！");
                        return oneRowCheck;
                    }
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”非法");
                    return oneRowCheck;
            }
        }
        JSONObject paramJson = new JSONObject();
        paramJson.put("groupType", "ZJ");
        paramJson.put("groupName", "师一级");
        List<JSONObject> zjList = commonInfoDao.getGroupInfo(paramJson);
        if (zjList != null) {
            if (zjList.isEmpty()) {
                oneRowCheck.put("message", "系统中该职级不存在，请先维护！");
                return oneRowCheck;
            } else if (zjList.size() == 1) {
                itemRowMap.put("dutyId", zjList.get(0).getString("GROUP_ID_"));
                itemRowMap.put("dutyDimId", zjList.get(0).getString("DIM_ID_"));
            } else {
                oneRowCheck.put("message", "获取职级信息有误！");
                return oneRowCheck;
            }
        } else {
            oneRowCheck.put("message", "获取职级信息有误！");
            return oneRowCheck;
        }

        itemRowMap.put("id", id);
        itemRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        itemRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        itemRowMap.put("CREATE_TIME_", new Date());
        itemRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        itemRowMap.put("UPDATE_TIME_", new Date());
        insertList.add(itemRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    public JSONObject saveOtherInfo(Map<String, Object> objBody) {
        String json = JSON.toJSONString(objBody);// map转String
        JSONObject jsonObject = JSON.parseObject(json);// String转json
        JSONObject resultJson = new JSONObject();
        // 4.保存用户信息
        resultJson = saveUser(jsonObject);
        if (!resultJson.getBoolean("success")) {
            return ResultUtil.result(false, resultJson.getString("message"), "");
        }
        String userId = resultJson.getString("id");
        objBody.put("userId", userId);
        // 7.添加 操作os_inst_users表
        resultJson = saveInstUser(jsonObject, userId);
        if (!resultJson.getBoolean("success")) {
            return ResultUtil.result(false, resultJson.getString("message"), "");
        }
        // 8.添加 部门关系 操作os_rel_inst表
        resultJson = saveDeptOsRelInst(jsonObject, userId);
        if (!resultJson.getBoolean("success")) {
            return ResultUtil.result(false, resultJson.getString("message"), "");
        }
        // 9.添加人员岗位信息
        if(StringUtils.isNotBlank(jsonObject.getString("postId"))) {
            resultJson = saveWorkOsRelInst(jsonObject, userId);
            if (!resultJson.getBoolean("success")) {
                return ResultUtil.result(false, resultJson.getString("message"), "");
            }
        }
        // 10.添加人员职级信息 职级不为空 则添加
        if (jsonObject.getString("dutyId") != null && !"".equals(jsonObject.getString("dutyId"))) {
            resultJson = saveDutyOsRelInst(jsonObject, userId);
            if (!resultJson.getBoolean("success")) {
                return ResultUtil.result(false, resultJson.getString("message"), "");
            }
        }

        return resultJson;
    }

    public JSONObject saveUser(JSONObject jsonObject) {
        JSONObject resultJson = new JSONObject();
        JSONObject paramJson = new JSONObject();
        String id = IdUtil.getId();
        try {
            String userNo = jsonObject.getString("userNo");
            String userName = jsonObject.getString("userName");
            String pwd = jsonObject.getString("pwd");
            if (StringUtils.isBlank(pwd)) {
                pwd = PasswordUtil.encodePwd(jsonObject.getString("userNo"));
            }
            paramJson.put("id", id);
            paramJson.put("userNo", userNo);
            paramJson.put("userName", userName);
            paramJson.put("pwd", pwd);
            paramJson.put("mobile", jsonObject.getString("telephone"));
            paramJson.put("email", userNo);
            paramJson.put("CERT_NO_", jsonObject.getString("CERT_NO_"));
            paramJson.put("pdmUserNo", jsonObject.getString("pdmUserNo"));
            paramJson.put("szrUserId", jsonObject.getString("szrUserId"));
            paramJson.put("szrUserName", jsonObject.getString("szrUserName"));
            importUserDao.addUserInfo(paramJson);
        } catch (Exception e) {
            resultJson.put("success", false);
            resultJson.put("message", "保存用户信息异常！");
            return resultJson;
        }
        resultJson.put("success", true);
        resultJson.put("message", "保存用户成功！");
        resultJson.put("id", id);
        return resultJson;
    }

    public JSONObject saveInstUser(JSONObject jsonObject, String userId) {
        String id = IdUtil.getId();
        JSONObject resultJson = new JSONObject();
        try {
            OsInstUsers osInstUsers = new OsInstUsers();
            osInstUsers.setId(id);
            osInstUsers.setUserId(userId);
            osInstUsers.setApproveUser("1");
            osInstUsers.setIsAdmin(0);
            osInstUsers.setDomain("redxun.cn");
            osInstUsers.setStatus("IN_JOB");
            osInstUsers.setTenantId("1");
            osInstUsers.setCreateType("CREATE");
            osInstUsers.setApplyStatus("ENABLED");
            importUserDao.addInstUser(osInstUsers);
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.put("success", false);
            resultJson.put("message", "保存组织机构关系信息异常！");
            return resultJson;
        }
        resultJson.put("success", true);
        resultJson.put("message", "保存组织机构关系信息成功！");
        return resultJson;
    }

    public JSONObject saveDeptOsRelInst(JSONObject jsonObject, String userId) {
        String id = IdUtil.getId();
        JSONObject resultJson = new JSONObject();
        try {
            OsRelInst osRelInst = new OsRelInst();
            osRelInst.setInstId(id);
            osRelInst.setRelTypeId("1");
            osRelInst.setRelTypeKey("GROUP-USER-BELONG");
            osRelInst.setParty1(jsonObject.getString("groupId"));
            osRelInst.setParty2(userId);
            osRelInst.setDim1("1");
            osRelInst.setIsMain("YES");
            osRelInst.setStatus("ENABLED");
            osRelInst.setRelType("GROUP-USER");
            osRelInst.setCreateBy("1");
            osRelInst.setCreateTime(new Date());
            osRelInst.setTenantId("1");
            importUserDao.addOsRelInst(osRelInst);
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.put("success", false);
            resultJson.put("message", "保存部门人员信息异常！");
            return resultJson;
        }
        resultJson.put("success", true);
        resultJson.put("message", "保存部门人员信息成功！");
        return resultJson;
    }

    public JSONObject saveWorkOsRelInst(JSONObject jsonObject, String userId) {
        JSONObject resultJson = new JSONObject();
        try {
            OsRelInst osRelInst = new OsRelInst();
            osRelInst.setInstId(IdUtil.getId());
            osRelInst.setRelTypeId("1");
            osRelInst.setRelTypeKey("GROUP-USER-BELONG");
            osRelInst.setParty1(jsonObject.getString("postId"));
            osRelInst.setParty2(userId);
            osRelInst.setDim1(jsonObject.getString("postDimId"));
            osRelInst.setIsMain("NO");
            osRelInst.setStatus("ENABLED");
            osRelInst.setRelType("GROUP-USER");
            osRelInst.setCreateBy("1");
            osRelInst.setCreateTime(new Date());
            osRelInst.setTenantId("1");
            importUserDao.addOsRelInst(osRelInst);
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.put("success", false);
            resultJson.put("message", "保存岗位人员信息异常！");
            return resultJson;
        }
        resultJson.put("success", true);
        resultJson.put("message", "保存岗位人员信息成功！");
        return resultJson;
    }

    public JSONObject saveDutyOsRelInst(JSONObject jsonObject, String userId) {
        JSONObject resultJson = new JSONObject();
        try {
            OsRelInst osRelInst = new OsRelInst();
            osRelInst.setInstId(IdUtil.getId());
            osRelInst.setRelTypeId("1");
            osRelInst.setRelTypeKey("GROUP-USER-BELONG");
            osRelInst.setParty1(jsonObject.getString("dutyId"));
            osRelInst.setParty2(userId);
            osRelInst.setDim1(jsonObject.getString("dutyDimId"));
            osRelInst.setIsMain("NO");
            osRelInst.setStatus("ENABLED");
            osRelInst.setRelType("GROUP-USER");
            osRelInst.setCreateBy("1");
            osRelInst.setCreateTime(new Date());
            osRelInst.setTenantId("1");
            importUserDao.addOsRelInst(osRelInst);
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.put("success", false);
            resultJson.put("message", "保存职级人员信息异常！");
            return resultJson;
        }
        resultJson.put("success", true);
        resultJson.put("message", "保存职级人员信息成功！");
        return resultJson;
    }
}
