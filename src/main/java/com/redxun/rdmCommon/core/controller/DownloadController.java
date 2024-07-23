package com.redxun.rdmCommon.core.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.manager.DownLoadManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.materielextend.core.service.MaterielApplyService;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.materielextend.core.util.MaterielConstant;
import com.redxun.materielextend.core.util.ResultData;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.sys.org.manager.OsUserManager;

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
 * @since 2020/6/2 17:40
 */
@Controller
@RequestMapping("/rdmCommon/download")
public class DownloadController {
    private static final Logger logger = LoggerFactory.getLogger(DownloadController.class);
    @Autowired
    private DownLoadManager downLoadManager;

    // 浏览器下载
    @RequestMapping("downloadExplorer")
    public ResponseEntity<byte[]> downloadExplorer(HttpServletRequest request, HttpServletResponse response) {
        return downLoadManager.downloadExplorer();
    }

}
