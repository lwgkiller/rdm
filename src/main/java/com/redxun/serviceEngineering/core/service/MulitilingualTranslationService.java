package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.MulitilingualTranslationDao;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import jdk.nashorn.internal.ir.annotations.Immutable;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Service
public class MulitilingualTranslationService {

    private static Logger logger = LoggerFactory.getLogger(PartsAtlasService.class);

    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    @Autowired
    private MulitilingualTranslationDao mulitilingualTranslationDao;

    @Resource
    CommonInfoManager commonInfoManager;

    @Resource
    CommonInfoDao commonInfoDao;

    /**
     * 查询零部件词汇列表
     *
     * @param request
     * @param response
     * @param doPage
     * @return
     */
    public JsonPageResult<?> getLjtcList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "chineseName", "desc");
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
        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        List<Map<String, Object>> lctcList = mulitilingualTranslationDao.queryLjtcList(params);
        result.setData(lctcList);
        int countLbjfyList = mulitilingualTranslationDao.countLjtcList(params);
        result.setTotal(countLbjfyList);
        return result;
    }

    // 根据id查询标准详情
    public Map<String, Object> queryLjtcById(String chineseId) {
        Map<String, Object> param = new HashMap<>();
        param.put("chineseId", chineseId);
        Map<String, Object> oneInfo = mulitilingualTranslationDao.queryLjtcById(param);
        return oneInfo;
    }

    public void addLjtc(JSONObject formData) {
        formData.put("chineseId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        mulitilingualTranslationDao.addLjtc(formData);
    }

    public void updateLjtc(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        mulitilingualTranslationDao.updateLjtc(formData);
    }

    public void exportLjtcList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = getLjtcList(request, response, true);
        List<Map<String, Object>> listData = result.getData();
        String languageList = RequestUtil.getString(request, "languageList");
        if (languageList == null) {
            languageList = "\"\"";
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "零件图册零部件词汇列表";
        String excelName = nowDate + title;
        if (!listData.isEmpty() && !languageList.equals("\"\"")) {
            Map<String, Object> dataMap = listData.get(0);
            Set<String> strings = dataMap.keySet();
            List<String> fieldNames = new ArrayList<>();
            List<String> fieldCodes = new ArrayList<>();
            fieldNames.add("物料编码");
            fieldCodes.add("materialCode");
            fieldNames.add("原始中文");
            fieldCodes.add("originChinese");
            fieldNames.add("中文");
            fieldCodes.add("chineseName");
            if (languageList.contains("english")) {
                fieldNames.add("英文");
                fieldCodes.add("englishName");
            }
            if (languageList.contains("russian")) {
                fieldNames.add("俄文");
                fieldCodes.add("russianName");
            }
            if (languageList.contains("portuguese")) {
                fieldNames.add("葡文");
                fieldCodes.add("portugueseName");
            }
            if (languageList.contains("germany")) {
                fieldNames.add("德文");
                fieldCodes.add("germanyName");
            }
            if (languageList.contains("spanish")) {
                fieldNames.add("西文");
                fieldCodes.add("spanishName");
            }
            if (languageList.contains("french")) {
                fieldNames.add("法文");
                fieldCodes.add("frenchName");
            }
            if (languageList.contains("italian")) {
                fieldNames.add("意文");
                fieldCodes.add("italianName");
            }
            if (languageList.contains("polish")) {
                fieldNames.add("波兰语");
                fieldCodes.add("polishName");
            }
            if (languageList.contains("turkish")) {
                fieldNames.add("土耳其语");
                fieldCodes.add("turkishName");
            }
            if (languageList.contains("swedish")) {
                fieldNames.add("瑞典语");
                fieldCodes.add("swedishName");
            }
            if (languageList.contains("danish")) {
                fieldNames.add("丹麦文");
                fieldCodes.add("danishName");
            }
            if (languageList.contains("dutch")) {
                fieldNames.add("荷兰语");
                fieldCodes.add("dutchName");
            }
            if (languageList.contains("slovenia")) {
                fieldNames.add("斯洛文尼亚语");
                fieldCodes.add("sloveniaName");
            }
            if (languageList.contains("romania")) {
                fieldNames.add("罗马尼亚语");
                fieldCodes.add("romaniaName");
            }
            if (languageList.contains("chineseT")) {
                fieldNames.add("繁体字");
                fieldCodes.add("chineseTName");
            }
            if (languageList.contains("thai")) {
                fieldNames.add("泰语");
                fieldCodes.add("thaiName");
            }
            if (languageList.contains("hungarian")) {
                fieldNames.add("匈牙利语");
                fieldCodes.add("hungarianName");
            }
            if (languageList.contains("norwegian")) {
                fieldNames.add("挪威语");
                fieldCodes.add("norwegianName");
            }
            if (languageList.contains("korean")) {
                fieldNames.add("韩语");
                fieldCodes.add("koreanName");
            }
            if (languageList.contains("indone")) {
                fieldNames.add("印尼语");
                fieldCodes.add("indoneName");
            }
            if (languageList.contains("arabic")) {
                fieldNames.add("阿拉伯语");
                fieldCodes.add("arabicName");
            }
            if (languageList.contains("japanese")) {
                fieldNames.add("日语");
                fieldCodes.add("japaneseName");
            }
            HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames.toArray(new String[fieldNames.size()]),
                fieldCodes.toArray(new String[fieldCodes.size()]), title);
            // 输出
            ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);

        } else if (!listData.isEmpty() && languageList.equals("\"\"")) {
            String[] fieldNames = {"中文", "英文", "葡文", "德文", "西文", "法文", "意文", "俄文", "波兰语", "土耳其语", "瑞典语", "丹麦文", "荷兰语",
                "斯洛文尼亚语", "罗马尼亚语", "繁体字", "泰语", "匈牙利语", "挪威语", "韩语","印尼语","阿拉伯语","日语"};
            String[] fieldCodes = {"chineseName", "englishName", "portugueseName", "germanyName", "spanishName",
                "frenchName", "italianName", "russianName", "polishName", "turkishName", "swedishName", "danishName",
                "dutchName", "sloveniaName", "romaniaName", "chineseTName", "thaiName", "hungarianName",
                "norwegianName", "koreanName","indoneName","arabicName","japaneseName"};
            HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
            // 输出
            ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
        }
    }

    public void exportLjtcListAll(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = getLjtcList(request, response, false);
        List<Map<String, Object>> listData = result.getData();
        String languageList = RequestUtil.getString(request, "languageListAll");
        if (languageList == null) {
            languageList = "\"\"";
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "零件图册零部件词汇列表";
        String excelName = nowDate + title;
        if (!listData.isEmpty() && !languageList.equals("\"\"")) {
            Map<String, Object> dataMap = listData.get(0);
            // Set<String> strings = dataMap.keySet();
            List<String> fieldNames = new ArrayList<>();
            List<String> fieldCodes = new ArrayList<>();
            fieldNames.add("物料编码");
            fieldCodes.add("materialCode");
            fieldNames.add("原始中文");
            fieldCodes.add("originChinese");
            fieldNames.add("中文");
            fieldCodes.add("chineseName");
            if (languageList.contains("english")) {
                fieldNames.add("英文");
                fieldCodes.add("englishName");
            }
            if (languageList.contains("russian")) {
                fieldNames.add("俄文");
                fieldCodes.add("russianName");
            }
            if (languageList.contains("portuguese")) {
                fieldNames.add("葡文");
                fieldCodes.add("portugueseName");
            }
            if (languageList.contains("germany")) {
                fieldNames.add("德文");
                fieldCodes.add("germanyName");
            }
            if (languageList.contains("spanish")) {
                fieldNames.add("西文");
                fieldCodes.add("spanishName");
            }
            if (languageList.contains("french")) {
                fieldNames.add("法文");
                fieldCodes.add("frenchName");
            }
            if (languageList.contains("italian")) {
                fieldNames.add("意文");
                fieldCodes.add("italianName");
            }
            if (languageList.contains("polish")) {
                fieldNames.add("波兰语");
                fieldCodes.add("polishName");
            }
            if (languageList.contains("turkish")) {
                fieldNames.add("土耳其语");
                fieldCodes.add("turkishName");
            }
            if (languageList.contains("swedish")) {
                fieldNames.add("瑞典语");
                fieldCodes.add("swedishName");
            }
            if (languageList.contains("danish")) {
                fieldNames.add("丹麦文");
                fieldCodes.add("danishName");
            }
            if (languageList.contains("dutch")) {
                fieldNames.add("荷兰语");
                fieldCodes.add("dutchName");
            }
            if (languageList.contains("slovenia")) {
                fieldNames.add("斯洛文尼亚语");
                fieldCodes.add("sloveniaName");
            }
            if (languageList.contains("romania")) {
                fieldNames.add("罗马尼亚语");
                fieldCodes.add("romaniaName");
            }
            if (languageList.contains("chineseT")) {
                fieldNames.add("繁体字");
                fieldCodes.add("chineseTName");
            }
            if (languageList.contains("thai")) {
                fieldNames.add("泰语");
                fieldCodes.add("thaiName");
            }
            if (languageList.contains("hungarian")) {
                fieldNames.add("匈牙利语");
                fieldCodes.add("hungarianName");
            }
            if (languageList.contains("norwegian")) {
                fieldNames.add("挪威语");
                fieldCodes.add("norwegianName");
            }
            if (languageList.contains("korean")) {
                fieldNames.add("韩语");
                fieldCodes.add("koreanName");
            }
            if (languageList.contains("indone")) {
                fieldNames.add("印尼语");
                fieldCodes.add("indoneName");
            }
            if (languageList.contains("arabic")) {
                fieldNames.add("阿拉伯语");
                fieldCodes.add("arabicName");
            }
            if (languageList.contains("japanese")) {
                fieldNames.add("日语");
                fieldCodes.add("japaneseName");
            }
            HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames.toArray(new String[fieldNames.size()]),
                fieldCodes.toArray(new String[fieldCodes.size()]), title);
            // 输出
            ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);

        } else if (!listData.isEmpty() && languageList.equals("\"\"")) {
            String[] fieldNames = {"中文", "英文", "葡文", "德文", "西文", "法文", "意文", "俄文", "波兰语", "土耳其语", "瑞典语", "丹麦文", "荷兰语",
                "斯洛文尼亚语", "罗马尼亚语", "繁体字", "泰语", "匈牙利语", "挪威语", "韩语","印尼语","阿拉伯语","日语"};
            String[] fieldCodes = {"chineseName", "englishName", "portugueseName", "germanyName", "spanishName",
                "frenchName", "italianName", "russianName", "polishName", "turkishName", "swedishName", "danishName",
                "dutchName", "sloveniaName", "romaniaName", "chineseTName", "thaiName", "hungarianName",
                "norwegianName", "koreanName","indoneName","arabicName","japaneseName"};
            HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
            // 输出
            ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
        }
    }

    public boolean getLjtcExist(String materialCode, String chineseId) {
        int count = mulitilingualTranslationDao.getLjtcExist(materialCode, chineseId);
        if (count == 0) {
            return false;
        }
        return true;
    }

    // 仪表
    public JsonPageResult<?> getYbList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "originChinese", "desc");
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    params.put(name, value);

                    //@mh 2023年8月14日09:32:50 选择单语种的时候要把所选语言为空的过滤掉
                    if ("languageId".equalsIgnoreCase(name) && !value.contains(",")) {
                        params.put("filterBlank", value+"Name");
                    }
                }

            }
        }
        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }

        List<Map<String, Object>> lbjfyList = mulitilingualTranslationDao.queryYbList(params);
        result.setData(lbjfyList);
        int countLbjfyList = mulitilingualTranslationDao.countYbList(params);
        result.setTotal(countLbjfyList);
        return result;
    }

    // 根据id查询标准详情
    public Map<String, Object> queryYbById(String chineseId) {
        Map<String, Object> param = new HashMap<>();
        param.put("chineseId", chineseId);
        Map<String, Object> oneInfo = mulitilingualTranslationDao.queryYbById(param);
        return oneInfo;
    }

    public void addYb(JSONObject formData) {
        formData.put("chineseId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        mulitilingualTranslationDao.addYb(formData);
    }

    public void updateYb(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        mulitilingualTranslationDao.updateYb(formData);
    }

    // ..
    public void exportYbList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = getYbList(request, response, true);
        List<Map<String, Object>> listData = result.getData();
        String languageList = RequestUtil.getString(request, "languageList");
        if (languageList == null) {
            languageList = "\"\"";
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "仪表零部件词汇列表";
        String excelName = nowDate + title;
        if (!listData.isEmpty() && !languageList.equals("\"\"")) {
            Map<String, Object> dataMap = listData.get(0);
            Set<String> strings = dataMap.keySet();
            List<String> fieldNames = new ArrayList<>();
            List<String> fieldCodes = new ArrayList<>();
            fieldNames.add("中文");
            fieldCodes.add("originChinese");
            if (languageList.contains("english")) {
                fieldNames.add("英文");
                fieldCodes.add("englishName");
            }
            if (languageList.contains("russian")) {
                fieldNames.add("俄文");
                fieldCodes.add("russianName");
            }
            if (languageList.contains("portuguese")) {
                fieldNames.add("葡文");
                fieldCodes.add("portugueseName");
            }
            if (languageList.contains("germany")) {
                fieldNames.add("德文");
                fieldCodes.add("germanyName");
            }
            if (languageList.contains("spanish")) {
                fieldNames.add("西文");
                fieldCodes.add("spanishName");
            }
            if (languageList.contains("french")) {
                fieldNames.add("法文");
                fieldCodes.add("frenchName");
            }
            if (languageList.contains("italian")) {
                fieldNames.add("意文");
                fieldCodes.add("italianName");
            }
            if (languageList.contains("polish")) {
                fieldNames.add("波兰语");
                fieldCodes.add("polishName");
            }
            if (languageList.contains("turkish")) {
                fieldNames.add("土耳其语");
                fieldCodes.add("turkishName");
            }
            if (languageList.contains("swedish")) {
                fieldNames.add("瑞典语");
                fieldCodes.add("swedishName");
            }
            if (languageList.contains("danish")) {
                fieldNames.add("丹麦文");
                fieldCodes.add("danishName");
            }
            if (languageList.contains("dutch")) {
                fieldNames.add("荷兰语");
                fieldCodes.add("dutchName");
            }
            if (languageList.contains("slovenia")) {
                fieldNames.add("斯洛文尼亚语");
                fieldCodes.add("sloveniaName");
            }
            if (languageList.contains("romania")) {
                fieldNames.add("罗马尼亚语");
                fieldCodes.add("romaniaName");
            }
            if (languageList.contains("chineseT")) {
                fieldNames.add("繁体字");
                fieldCodes.add("chineseTName");
            }
            if (languageList.contains("thai")) {
                fieldNames.add("泰语");
                fieldCodes.add("thaiName");
            }
            if (languageList.contains("hungarian")) {
                fieldNames.add("匈牙利语");
                fieldCodes.add("hungarianName");
            }
            if (languageList.contains("norwegian")) {
                fieldNames.add("挪威语");
                fieldCodes.add("norwegianName");
            }
            if (languageList.contains("korean")) {
                fieldNames.add("韩语");
                fieldCodes.add("koreanName");
            }
            if (languageList.contains("indone")) {
                fieldNames.add("印尼语");
                fieldCodes.add("indoneName");
            }
            if (languageList.contains("arabic")) {
                fieldNames.add("阿拉伯语");
                fieldCodes.add("arabicName");
            }
            if (languageList.contains("japanese")) {
                fieldNames.add("日语");
                fieldCodes.add("japaneseName");
            }

            HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames.toArray(new String[fieldNames.size()]),
                fieldCodes.toArray(new String[fieldCodes.size()]), title);
            // 输出
            ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);

        } else if (!listData.isEmpty() && languageList.equals("\"\"")) {
            String[] fieldNames = {"中文", "英文", "葡文", "德文", "西文", "法文", "意文", "俄文", "波兰语", "土耳其语", "瑞典语", "丹麦文", "荷兰语",
                "斯洛文尼亚语", "罗马尼亚语", "繁体字", "泰语", "匈牙利语", "挪威语", "韩语","印尼语","阿拉伯语","日语"};
            String[] fieldCodes = {"originChinese", "englishName", "portugueseName", "germanyName", "spanishName",
                "frenchName", "italianName", "russianName", "polishName", "turkishName", "swedishName", "danishName",
                "dutchName", "sloveniaName", "romaniaName", "chineseTName", "thaiName", "hungarianName",
                "norwegianName", "koreanName","indoneName","arabicName","japaneseName"};
            HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
            // 输出
            ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
        }
    }

    // ..
    public void exportYbAllList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = getYbList(request, response, false);
        List<Map<String, Object>> listData = result.getData();
        String languageList = RequestUtil.getString(request, "languageListAll");
        if (languageList == null) {
            languageList = "\"\"";
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "仪表零部件全部词汇列表";
        String excelName = nowDate + title;
        if (!listData.isEmpty() && !languageList.equals("\"\"")) {
            Map<String, Object> dataMap = listData.get(0);
            Set<String> strings = dataMap.keySet();
            List<String> fieldNames = new ArrayList<>();
            List<String> fieldCodes = new ArrayList<>();
            fieldNames.add("中文");
            fieldCodes.add("originChinese");
            if (languageList.contains("english")) {
                fieldNames.add("英文");
                fieldCodes.add("englishName");
            }
            if (languageList.contains("russian")) {
                fieldNames.add("俄文");
                fieldCodes.add("russianName");
            }
            if (languageList.contains("portuguese")) {
                fieldNames.add("葡文");
                fieldCodes.add("portugueseName");
            }
            if (languageList.contains("germany")) {
                fieldNames.add("德文");
                fieldCodes.add("germanyName");
            }
            if (languageList.contains("spanish")) {
                fieldNames.add("西文");
                fieldCodes.add("spanishName");
            }
            if (languageList.contains("french")) {
                fieldNames.add("法文");
                fieldCodes.add("frenchName");
            }
            if (languageList.contains("italian")) {
                fieldNames.add("意文");
                fieldCodes.add("italianName");
            }
            if (languageList.contains("polish")) {
                fieldNames.add("波兰语");
                fieldCodes.add("polishName");
            }
            if (languageList.contains("turkish")) {
                fieldNames.add("土耳其语");
                fieldCodes.add("turkishName");
            }
            if (languageList.contains("swedish")) {
                fieldNames.add("瑞典语");
                fieldCodes.add("swedishName");
            }
            if (languageList.contains("danish")) {
                fieldNames.add("丹麦文");
                fieldCodes.add("danishName");
            }
            if (languageList.contains("dutch")) {
                fieldNames.add("荷兰语");
                fieldCodes.add("dutchName");
            }
            if (languageList.contains("slovenia")) {
                fieldNames.add("斯洛文尼亚语");
                fieldCodes.add("sloveniaName");
            }
            if (languageList.contains("romania")) {
                fieldNames.add("罗马尼亚语");
                fieldCodes.add("romaniaName");
            }
            if (languageList.contains("chineseT")) {
                fieldNames.add("繁体字");
                fieldCodes.add("chineseTName");
            }
            if (languageList.contains("thai")) {
                fieldNames.add("泰语");
                fieldCodes.add("thaiName");
            }
            if (languageList.contains("hungarian")) {
                fieldNames.add("匈牙利语");
                fieldCodes.add("hungarianName");
            }
            if (languageList.contains("norwegian")) {
                fieldNames.add("挪威语");
                fieldCodes.add("norwegianName");
            }
            if (languageList.contains("korean")) {
                fieldNames.add("韩语");
                fieldCodes.add("koreanName");
            }
            if (languageList.contains("indone")) {
                fieldNames.add("印尼语");
                fieldCodes.add("indoneName");
            }
            if (languageList.contains("arabic")) {
                fieldNames.add("阿拉伯语");
                fieldCodes.add("arabicName");
            }
            if (languageList.contains("japanese")) {
                fieldNames.add("日语");
                fieldCodes.add("japaneseName");
            }
            HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames.toArray(new String[fieldNames.size()]),
                fieldCodes.toArray(new String[fieldCodes.size()]), title);
            // 输出
            ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);

        } else if (!listData.isEmpty() && languageList.equals("\"\"")) {
            String[] fieldNames = {"中文", "英文", "葡文", "德文", "西文", "法文", "意文", "俄文", "波兰语", "土耳其语", "瑞典语", "丹麦文", "荷兰语",
                "斯洛文尼亚语", "罗马尼亚语", "繁体字", "泰语", "匈牙利语", "挪威语", "韩语","印尼语","阿拉伯语","日语"};
            String[] fieldCodes = {"originChinese", "englishName", "portugueseName", "germanyName", "spanishName",
                "frenchName", "italianName", "russianName", "polishName", "turkishName", "swedishName", "danishName",
                "dutchName", "sloveniaName", "romaniaName", "chineseTName", "thaiName", "hungarianName",
                "norwegianName", "koreanName","indoneName","arabicName","japaneseName"};
            HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
            // 输出
            ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
        }
    }

    // ..
    public void importMaterialYb(JSONObject result, HttpServletRequest request) {
        Map<String, Object> params = new HashMap<>();
        List<Map<String, Object>> ybList = mulitilingualTranslationDao.queryYbList(params);
        Set<String> originChineses = new HashSet<>();
        for (Map<String, Object> oneYb : ybList) {
            originChineses.add(oneYb.get("originChinese").toString().trim());
        }
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            MultipartFile fileObj = multipartRequest.getFile("importFile");
            if (fileObj == null) {
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            String fileName = ((CommonsMultipartFile)fileObj).getFileItem().getName();
            ((CommonsMultipartFile)fileObj).getFileItem().getName().endsWith(ExcelReaderUtil.EXCEL03_EXTENSION);
            Workbook wb = null;
            if (fileName.endsWith(ExcelReaderUtil.EXCEL03_EXTENSION)) {
                wb = new HSSFWorkbook(fileObj.getInputStream());
            } else if (fileName.endsWith(ExcelReaderUtil.EXCEL07_EXTENSION)) {
                wb = new XSSFWorkbook(fileObj.getInputStream());
            }
            Sheet sheet = wb.getSheet("零部件词汇");
            if (sheet == null) {
                logger.error("找不到导入模板");
                result.put("message", "数据导入失败，找不到导入模板导入页！");
                return;
            }
            int rowNum = sheet.getPhysicalNumberOfRows();
            if (rowNum < 1) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }

            // 解析标题部分
            Row titleRow = sheet.getRow(0);
            if (titleRow == null) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }
            List<String> titleList = new ArrayList<>();
            for (int i = 0; i < titleRow.getLastCellNum(); i++) {
                titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
            }

            if (rowNum < 2) {
                logger.info("数据行为空");
                result.put("message", "数据导入完成，数据行为空！");
                return;
            }
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> itemList = new ArrayList<>();
            for (int i = 1; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRow(row, itemList, titleList);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            for (int i = 0; i < itemList.size(); i++) {
                Map<String, Object> message = itemList.get(i);
                JSONObject oneCellInfo = new JSONObject(message);
                if (originChineses.contains(message.get("originChinese").toString().trim())) {
                    mulitilingualTranslationDao.updateYb(oneCellInfo);
                } else {
                    String chineseId = IdUtil.getId();
                    oneCellInfo.put("chineseId", chineseId);
                    mulitilingualTranslationDao.addYb(oneCellInfo);
                }
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importProduct", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    public void importMaterialljtc(JSONObject result, HttpServletRequest request) {
        Map<String, Object> params = new HashMap<>();
        List<Map<String, Object>> ljtcList = mulitilingualTranslationDao.queryLjtcList(params);
        Set<String> materialCodes = new HashSet<>();
        for (Map<String, Object> oneLjtc : ljtcList) {
            materialCodes.add(oneLjtc.get("materialCode").toString().trim());
        }
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            MultipartFile fileObj = multipartRequest.getFile("importFile");
            if (fileObj == null) {
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            String fileName = ((CommonsMultipartFile)fileObj).getFileItem().getName();
            ((CommonsMultipartFile)fileObj).getFileItem().getName().endsWith(ExcelReaderUtil.EXCEL03_EXTENSION);
            Workbook wb = null;
            if (fileName.endsWith(ExcelReaderUtil.EXCEL03_EXTENSION)) {
                wb = new HSSFWorkbook(fileObj.getInputStream());
            } else if (fileName.endsWith(ExcelReaderUtil.EXCEL07_EXTENSION)) {
                wb = new XSSFWorkbook(fileObj.getInputStream());
            }
            Sheet sheet = wb.getSheet("零部件词汇");
            if (sheet == null) {
                logger.error("找不到导入模板");
                result.put("message", "数据导入失败，找不到导入模板导入页！");
                return;
            }
            int rowNum = sheet.getPhysicalNumberOfRows();
            if (rowNum < 1) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }

            // 解析标题部分
            Row titleRow = sheet.getRow(0);
            if (titleRow == null) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }
            List<String> titleList = new ArrayList<>();
            for (int i = 0; i < titleRow.getLastCellNum(); i++) {
                titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
            }

            if (rowNum < 2) {
                logger.info("数据行为空");
                result.put("message", "数据导入完成，数据行为空！");
                return;
            }
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> itemList = new ArrayList<>();
            for (int i = 1; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRowLjtc(row, itemList, titleList);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            for (int i = 0; i < itemList.size(); i++) {
                Map<String, Object> message = itemList.get(i);
                JSONObject oneCellInfo = new JSONObject(message);
                if (materialCodes.contains(message.get("materialCode").toString().trim())) {
                    mulitilingualTranslationDao.updateLjtc(oneCellInfo);
                } else {
                    String chineseId = IdUtil.getId();
                    oneCellInfo.put("chineseId", chineseId);
                    mulitilingualTranslationDao.addLjtc(oneCellInfo);
                }
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importProduct", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    /**
     * 仪表模板下载
     *
     * @return
     */
    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "仪表零部件词汇导入模板.xls";
            // 创建文件实例
            File file = new File(
                MaterielService.class.getClassLoader().getResource("templates/serviceEngineering/" + fileName).toURI());
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

    /**
     * 零件图册模板下载
     *
     * @return
     */
    public ResponseEntity<byte[]> importTemplateDownloadLjtc() {
        try {
            String fileName = "零部件词汇导入模板.xls";
            // 创建文件实例
            File file = new File(
                MaterielService.class.getClassLoader().getResource("templates/serviceEngineering/" + fileName).toURI());
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

    // ..
    private JSONObject generateDataFromRow(Row row, List<Map<String, Object>> itemList, List<String> titleList) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>(16);
        Map<String, Object> itemRowMap = new HashMap<>(16);

        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = ExcelUtil.getCellFormatValue(cell);
            }
            if (StringUtils.isBlank(cellValue)) {
                cellValue = "";
            }
            switch (title) {
                case "原始中文":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "原始中文为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("originChinese", cellValue);
                    break;
                case "英文":
                    oneRowMap.put("englishName", cellValue);
                    break;
                case "葡文":
                    oneRowMap.put("portugueseName", cellValue);
                    break;
                case "德文":
                    oneRowMap.put("germanyName", cellValue);
                    break;
                case "西文":
                    oneRowMap.put("spanishName", cellValue);
                    break;
                case "法文":
                    oneRowMap.put("frenchName", cellValue);
                    break;
                case "意文":
                    oneRowMap.put("italianName", cellValue);
                    break;
                case "俄文":
                    oneRowMap.put("russianName", cellValue);
                    break;
                case "波兰语":
                    oneRowMap.put("polishName", cellValue);
                    break;
                case "土耳其语":
                    oneRowMap.put("turkishName", cellValue);
                    break;
                case "瑞典语":
                    oneRowMap.put("swedishName", cellValue);
                    break;
                case "丹麦文":
                    oneRowMap.put("danishName", cellValue);
                    break;
                case "荷兰语":
                    oneRowMap.put("dutchName", cellValue);
                    break;
                case "斯洛文尼亚语":
                    oneRowMap.put("sloveniaName", cellValue);
                    break;
                case "罗马尼亚语":
                    oneRowMap.put("romaniaName", cellValue);
                    break;
                case "繁体字":
                    oneRowMap.put("chineseTName", cellValue);
                    break;
                case "泰语":
                    oneRowMap.put("thaiName", cellValue);
                    break;
                case "匈牙利语":
                    oneRowMap.put("hungarianName", cellValue);
                    break;
                case "挪威语":
                    oneRowMap.put("norwegianName", cellValue);
                    break;
                case "韩语":
                    oneRowMap.put("koreanName", cellValue);
                    break;
                case "印尼语":
                    oneRowMap.put("indoneName", cellValue);
                    break;
                case "阿拉伯语":
                    oneRowMap.put("arabicName", cellValue);
                    break;
                case "日语":
                    oneRowMap.put("japaneseName", cellValue);
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    // ..
    private JSONObject generateDataFromRowLjtc(Row row, List<Map<String, Object>> itemList, List<String> titleList) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>(16);

        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = ExcelUtil.getCellFormatValue(cell);
            }
            if (StringUtils.isBlank(cellValue)) {
                cellValue = "";
            }
            switch (title) {
                case "物料编码":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料编码为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialCode", cellValue);
                    break;
                case "原始中文":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "原始中文为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("originChinese", cellValue);
                    break;
                case "中文":
                    // if (StringUtils.isBlank(cellValue)) {
                    // oneRowCheck.put("message", "中文为空");
                    // return oneRowCheck;
                    // }
                    oneRowMap.put("chineseName", cellValue);
                    break;
                case "英文":
                    oneRowMap.put("englishName", cellValue);
                    break;
                case "葡文":
                    oneRowMap.put("portugueseName", cellValue);
                    break;
                case "德文":
                    oneRowMap.put("germanyName", cellValue);
                    break;
                case "西文":
                    oneRowMap.put("spanishName", cellValue);
                    break;
                case "法文":
                    oneRowMap.put("frenchName", cellValue);
                    break;
                case "意文":
                    oneRowMap.put("italianName", cellValue);
                    break;
                case "俄文":
                    oneRowMap.put("russianName", cellValue);
                    break;
                case "波兰语":
                    oneRowMap.put("polishName", cellValue);
                    break;
                case "土耳其语":
                    oneRowMap.put("turkishName", cellValue);
                    break;
                case "瑞典语":
                    oneRowMap.put("swedishName", cellValue);
                    break;
                case "丹麦文":
                    oneRowMap.put("danishName", cellValue);
                    break;
                case "荷兰语":
                    oneRowMap.put("dutchName", cellValue);
                    break;
                case "斯洛文尼亚语":
                    oneRowMap.put("sloveniaName", cellValue);
                    break;
                case "罗马尼亚语":
                    oneRowMap.put("romaniaName", cellValue);
                    break;
                case "繁体字":
                    oneRowMap.put("chineseTName", cellValue);
                    break;
                case "泰语":
                    oneRowMap.put("thaiName", cellValue);
                    break;
                case "匈牙利语":
                    oneRowMap.put("hungarianName", cellValue);
                    break;
                case "挪威语":
                    oneRowMap.put("norwegianName", cellValue);
                    break;
                case "韩语":
                    oneRowMap.put("koreanName", cellValue);
                    break;
                case "印尼语":
                    oneRowMap.put("indoneName", cellValue);
                    break;
                case "阿拉伯语":
                    oneRowMap.put("arabicName", cellValue);
                    break;
                case "日语":
                    oneRowMap.put("japaneseName", cellValue);
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    public boolean getYbExist(String originChinese, String chineseId) {
        int count = mulitilingualTranslationDao.getYbExist(originChinese, chineseId);
        if (count == 0) {
            return false;
        }
        return true;
    }

    // @mh 2023年8月11日11:05:43 推荐英文及小语种
    public JSONObject getRecommend(String chineseName, String multilingualSign) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(chineseName)) {
            return result;
        }
        // 这里要构造一个字典，从sign-语种名称 如无映射，跳过小语种的推荐翻译

        Map<String, String> signMap = new HashMap<>();

        signMap.put("ru", "russianName");
        signMap.put("pt", "portugueseName");
        signMap.put("de", "germanyName");
        signMap.put("es", "spanishName");
        signMap.put("fr", "frenchName");
        signMap.put("it", "italianName");
        signMap.put("da", "danishName");
        signMap.put("nl", "dutchName");
        signMap.put("pl", "polishName");
        signMap.put("tr", "turkishName");
        signMap.put("sl", "sloveniaName");
        signMap.put("hu", "hungarianName");
        signMap.put("no", "norwegianName");
        signMap.put("zh-TW", "chineseTName");
        signMap.put("sv", "swedishName");
        signMap.put("ko", "koreanName");
        signMap.put("id", "indoneName");
        signMap.put("ar", "arabicName");
        signMap.put("ja", "japaneseName");
        String multiName = signMap.get(multilingualSign);

        JSONObject param = new JSONObject();
        param.put("multiName", multiName);
        param.put("chineseName", chineseName);
        JSONObject res = mulitilingualTranslationDao.getRecommend(param);
        if (res == null) {
            return result;
        }
        res.put("success", true);
        return res;
    }

}
