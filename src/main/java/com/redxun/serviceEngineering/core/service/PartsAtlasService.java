package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.PartsAtlasDao;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.org.manager.OsUserManager;
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

import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service
public class PartsAtlasService {
    private static Logger logger = LoggerFactory.getLogger(PartsAtlasService.class);
    @Autowired
    private PartsAtlasDao partsAtlasDao;
   


    //..根据action：storage，shipment和yearMonthBegin和yearMonthEnd获取指定区间内指定类型的数据列表
    public List<JSONObject> getListByActionAndDaterange(Map<String, Object> params) {
        List<JSONObject> businessList = partsAtlasDao.getListByActionAndDaterange(params);
        return businessList;
    }

    //..获取机型零件图册制作总数
    public Integer getPartsAtlasModelTotal() {
        return partsAtlasDao.getPartsAtlasModelTotal();
    }

    //..实例零件图册制作总数
    public Integer getPartsAtlasInstanceTotal() {
        return partsAtlasDao.getPartsAtlasInstanceTotal();
    }

    
}
