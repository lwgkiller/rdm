package com.redxun.rdmCommon.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.manager.ImportUserManager;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Controller
@RequestMapping("/user/core/import/")
public class ImportUserController {
    @Resource
    ImportUserManager importUserManager;
    /**
     * 模板下载
     * */
    @RequestMapping("importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return importUserManager.importTemplateDownload();
    }
    /**
     * 导入人员信息
     * */
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        importUserManager.importUser(result, request);
        return result;
    }
}
