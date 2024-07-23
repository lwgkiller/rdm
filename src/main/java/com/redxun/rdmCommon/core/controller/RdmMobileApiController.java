package com.redxun.rdmCommon.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.environment.core.dao.GsClhbDao;
import com.redxun.environment.core.service.ClhbService;
import com.redxun.environment.core.service.GsClhbService;
import com.redxun.materielextend.core.service.MaterielApiService;
import com.redxun.productDataManagement.core.manager.ProductSpectrumService;
import com.redxun.rdmCommon.core.manager.PdmApiManager;
import com.redxun.rdmCommon.core.manager.RdmMobileApiService;
import com.redxun.rdmZhgl.core.service.MaterialApplyService;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.service.ExportPartsAtlasService;
import com.redxun.serviceEngineering.core.service.SeGeneralKanBanNewService;
import com.redxun.serviceEngineering.core.service.WgjzlsjService;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.world.core.service.CkddService;
import com.redxun.wwrz.core.service.CeinfoService;
import com.redxun.xcmgFinance.core.manager.OAFinanceManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectScoreDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectAPIManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgTdm.core.service.TdmIIRequestService;
import com.redxun.zlgjNPI.core.manager.ZlgjWTService;
import com.redxun.zlgjNPI.core.manager.ZlgjjysbService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 移动端用API
 * <p>
 * Copyright: Copyright (C) 2020 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liwenguang
 * @since 2024/7/10 17:40
 */
@Controller
@RequestMapping("/rdmMobile/api/")
public class RdmMobileApiController {
    private static final Logger logger = LoggerFactory.getLogger(RdmMobileApiController.class);
    @Autowired
    private RdmMobileApiService rdmMobileApiService;

    //..获取根菜单
    @RequestMapping(value = "getRootMenu", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getRootMenu(HttpServletRequest request, @RequestBody String postData,
                                  HttpServletResponse response) {
        postData = new String(postData.getBytes(), Charset.forName("utf-8"));
        try {
            return rdmMobileApiService.getRootMenu(postData);
        } catch (Exception e) {
            return ResultUtil.result(false, "操作失败，系统异常！错误信息：" + e.getMessage(), null);
        }
    }

    //..根据某个根菜单的ID，获取下面的菜单树
    @RequestMapping(value = "getBySysId", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getBySysId(HttpServletRequest request, @RequestBody String postData,
                                 HttpServletResponse response) {
        postData = new String(postData.getBytes(), Charset.forName("utf-8"));
        try {
            return rdmMobileApiService.getBySysId(postData);
        } catch (Exception e) {
            return ResultUtil.result(false, "操作失败，系统异常！错误信息：" + e.getMessage(), null);
        }
    }
}
