package com.redxun.rdmZhgl.core.wsdl;

import javax.xml.rpc.holders.StringHolder;

import com.redxun.core.util.StringUtil;
import com.redxun.rdmZhgl.core.util.RdmZhglConst;
import com.redxun.rdmZhgl.core.wsdl.costcenter.XgwjjSapZWJGDZC_GET_KOSUPLIERLocator;
import com.redxun.rdmZhgl.core.wsdl.costcenter.XgwjjSapZWJGDZC_GET_KOSUPLIERPortType;
import com.redxun.rdmZhgl.core.wsdl.materialadd.ITEMS;
import com.redxun.rdmZhgl.core.wsdl.materialadd.ITEMS_OUT;
import com.redxun.rdmZhgl.core.wsdl.materialadd.XgwjSapRdmZWJ_CRESET_MBBLocator;
import com.redxun.rdmZhgl.core.wsdl.materialadd.XgwjSapRdmZWJ_CRESET_MBBPortType;
import com.redxun.rdmZhgl.core.wsdl.materialadd.holders.ArrayOfITEMSHolder;
import com.redxun.rdmZhgl.core.wsdl.materialadd.holders.ArrayOfITEMS_OUTHolder;
import com.redxun.rdmZhgl.core.wsdl.materialdel.RESB_OUT;
import com.redxun.rdmZhgl.core.wsdl.materialdel.RES_IN;
import com.redxun.rdmZhgl.core.wsdl.materialdel.XgwjSapRdmZWJ_DEL_MATEELocator;
import com.redxun.rdmZhgl.core.wsdl.materialdel.XgwjSapRdmZWJ_DEL_MATEEPortType;
import com.redxun.rdmZhgl.core.wsdl.materialdel.holders.ArrayOfRESB_OUTHolder;
import com.redxun.rdmZhgl.core.wsdl.materialdel.holders.ArrayOfRES_INHolder;
import com.redxun.rdmZhgl.core.wsdl.materialstatus.XgwjSapZWJ_GET_RESSTATELocator;
import com.redxun.rdmZhgl.core.wsdl.materialstatus.XgwjSapZWJ_GET_RESSTATEPortType;
import com.redxun.rdmZhgl.core.wsdl.materialstatus.holders.RES_INHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @author zhangzhen
 */
public class WsdlUtils {
    private static Logger logger = LoggerFactory.getLogger(WsdlUtils.class);

    /**
     * 获取全部成本中心
     */
    public static JSONArray getCostCenter() {
        JSONArray resultArray = new JSONArray();
        try {
            com.redxun.rdmZhgl.core.wsdl.costcenter.OUT_DATA[] out_data = null;
            XgwjjSapZWJGDZC_GET_KOSUPLIERLocator locator = new XgwjjSapZWJGDZC_GET_KOSUPLIERLocator();
            XgwjjSapZWJGDZC_GET_KOSUPLIERPortType service = locator.getXgwjjSapZWJGDZC_GET_KOSUPLIERHttpPort();
            com.redxun.rdmZhgl.core.wsdl.costcenter.holders.ArrayOfOUT_DATAHolder arrayOfOUT_dataHolder =
                new com.redxun.rdmZhgl.core.wsdl.costcenter.holders.ArrayOfOUT_DATAHolder();
            StringHolder stringHolder = new StringHolder();
            StringHolder stringHolder2 = new StringHolder();
            service.ZWJGDZC_GET_KOSTL(arrayOfOUT_dataHolder, stringHolder, stringHolder2);
            out_data = arrayOfOUT_dataHolder.value;
            JSONObject jsonObject = new JSONObject();
            String value = "";
            String text = "";
            for (com.redxun.rdmZhgl.core.wsdl.costcenter.OUT_DATA temp : out_data) {
                value = temp.getKOSTL();
                text = temp.getKTEXT();
                if (text.contains("作废") || text.contains("停用") || text.contains("冻结")) {
                    continue;
                }
                jsonObject = new JSONObject();
                jsonObject.put("value", value);
                jsonObject.put("text", text);
                resultArray.add(jsonObject);
            }
        } catch (Exception e) {
            logger.error("Exception in getCostCenter", e);
        }
        return resultArray;
    }

    /**
     * 创建预留单
     */
    public static JSONObject createMaterial(JSONObject jsonObject) {
        JSONObject resJson = new JSONObject();
        try {
            XgwjSapRdmZWJ_CRESET_MBBLocator locator = new XgwjSapRdmZWJ_CRESET_MBBLocator();
            XgwjSapRdmZWJ_CRESET_MBBPortType service = locator.getXgwjSapRdmZWJ_CRESET_MBBHttpPort();
            // 收货库存地点
            String UMLGO = jsonObject.getString("UMLGO");
            if (StringUtil.isNotEmpty(UMLGO)) {
                UMLGO = UMLGO.toUpperCase();
            }
            // 收货方
            String WEMPF = jsonObject.getString("WEMPF");
            // 需求日期
            String BDTER = jsonObject.getString("BDTER");
            // 成本中心
            String KOSTL = jsonObject.getString("KOSTL");
            // 订单编号
            String AUFNR = jsonObject.getString("AUFNR");
            // 移动类型
            String MOVE_TYPE = jsonObject.getString("MOVE_TYPE");
            // 总账科目编号
            String SAKNR = jsonObject.getString("SAKNR");
            // 物料详情数组
            JSONArray itemArray = jsonObject.getJSONArray("itemArray");
            ArrayOfITEMSHolder arrayOfITEMS = new ArrayOfITEMSHolder();
            ITEMS[] itemsArray = new ITEMS[itemArray.size()];
            for (int i = 0; i < itemArray.size(); i++) {
                JSONObject obj = itemArray.getJSONObject(i);
                ITEMS items = new ITEMS();
                // 物料编号
                items.setMATNR(obj.getString("MATNR").trim());
                // 物料描述
                items.setMAKTX(obj.getString("MAKTX"));
                // 需求量
                items.setBDMNG(obj.getString("BDMNG"));
                // 存储地点
                String LGORT = obj.getString("LGORT");
                if (StringUtil.isNotEmpty(LGORT)) {
                    LGORT = LGORT.toUpperCase().trim();
                }
                items.setLGORT(LGORT);
                // 基本计量单位
                items.setMEINS(obj.getString("MEINS"));
                itemsArray[i] = items;
            }
            arrayOfITEMS.value = itemsArray;
            ArrayOfITEMS_OUTHolder arrayOfITEMS_OUT = new ArrayOfITEMS_OUTHolder();
            StringHolder RETURNCODE = new StringHolder();
            StringHolder RETURNMSG = new StringHolder();
            StringHolder RSNUM = new StringHolder();
            service.ZWJ_SET_MB21(AUFNR, BDTER, KOSTL, MOVE_TYPE, UMLGO, WEMPF, SAKNR, arrayOfITEMS, arrayOfITEMS_OUT,
                RETURNCODE, RETURNMSG, RSNUM);
            String errorCode = RETURNCODE.value;
            JSONArray itemList = new JSONArray();
            // 如果成功则写入SAP行号、预留单号；如果失败则往推送信息表中插入失败信息
            if (RdmZhglConst.SAP_OK.equals(errorCode)) {
                JSONObject itemObj;
                for (ITEMS_OUT items_out : arrayOfITEMS_OUT.value) {
                    itemObj = new JSONObject();
                    itemObj.put("RSPOS", items_out.getRSPOS());
                    itemObj.put("MATNR", items_out.getMATNR());
                    itemObj.put("RSNUM", items_out.getRSNUM());
                    itemList.add(itemObj);
                }
            }
            resJson.put("errorCode", RETURNCODE.value);
            resJson.put("errorMsg", RETURNMSG.value);
            resJson.put("materialCode", RSNUM.value);
            resJson.put("itemList", itemList);
        } catch (Exception e) {
            logger.error("Exception in createMaterial", e);
            resJson.put("errorCode", "NG");
            resJson.put("errorMsg", "创建物料预留单号异常");
            return resJson;
        }
        return resJson;
    }

    /**
     * 删除物料信息
     */
    public static JSONObject delMaterialItems(JSONArray delObjList) {
        JSONObject resJson = new JSONObject();
        try {
            XgwjSapRdmZWJ_DEL_MATEELocator locator = new XgwjSapRdmZWJ_DEL_MATEELocator();
            XgwjSapRdmZWJ_DEL_MATEEPortType service = locator.getXgwjSapRdmZWJ_DEL_MATEEHttpPort();
            RES_IN[] resIns = new RES_IN[delObjList.size()];
            ArrayOfRES_INHolder arrayOfRES_IN = new ArrayOfRES_INHolder();
            ArrayOfRESB_OUTHolder arrayOfRESB_OUT = new ArrayOfRESB_OUTHolder();
            StringHolder RETURNCODE = new StringHolder();
            StringHolder RETURNMSG = new StringHolder();
            for (int i = 0; i < delObjList.size(); i++) {
                JSONObject delObj = delObjList.getJSONObject(i);
                RES_IN res_in = new RES_IN();
                res_in.setRSNUM(delObj.getString("RSNUM"));
                res_in.setRSPOS(delObj.getString("RSPOS"));
                resIns[i] = res_in;
            }
            arrayOfRES_IN.value = resIns;
            service.ZWJ_SET_RESDEL(arrayOfRESB_OUT, arrayOfRES_IN, RETURNCODE, RETURNMSG);
            String errorCode = RETURNCODE.value;
            JSONArray itemList = new JSONArray();
            // 获取每条信息的删除情况
            if ("OK".equals(errorCode)) {
                JSONObject itemObj;
                for (RESB_OUT items_out : arrayOfRESB_OUT.value) {
                    itemObj = new JSONObject();
                    itemObj.put("RSNUM", items_out.getRSNUM());
                    itemObj.put("RSPOS", items_out.getRSPOS());
                    itemObj.put("RETURNCODE", items_out.getRETURNCODE());
                    itemObj.put("RETURNMSG", items_out.getRETURNMSG());
                    itemList.add(itemObj);
                }
            }
            resJson.put("errorCode", RETURNCODE.value);
            resJson.put("errorMsg", RETURNMSG.value);
            resJson.put("itemList", itemList);
        } catch (Exception e) {
            logger.error("Exception in delMaterialItems", e);
            resJson.put("errorCode", "NG");
            resJson.put("errorMsg", "创建物料预留单号异常");
            return resJson;
        }
        return resJson;
    }

    /**
     * 查询预留物料状态
     */
    public static JSONObject getMaterialItemStatus(JSONArray itemList) {
        JSONObject resJson = new JSONObject();
        try {
            XgwjSapZWJ_GET_RESSTATELocator locator = new XgwjSapZWJ_GET_RESSTATELocator();
            XgwjSapZWJ_GET_RESSTATEPortType service = locator.getXgwjSapZWJ_GET_RESSTATEHttpPort();
            com.redxun.rdmZhgl.core.wsdl.materialstatus.RES_IN[] resIns =
                new com.redxun.rdmZhgl.core.wsdl.materialstatus.RES_IN[itemList.size()];
            com.redxun.rdmZhgl.core.wsdl.materialstatus.holders.ArrayOfRES_INHolder arrayOfRES_IN =
                new com.redxun.rdmZhgl.core.wsdl.materialstatus.holders.ArrayOfRES_INHolder();
            com.redxun.rdmZhgl.core.wsdl.materialstatus.holders.ArrayOfRESB_OUTHolder arrayOfRESB_OUT =
                new com.redxun.rdmZhgl.core.wsdl.materialstatus.holders.ArrayOfRESB_OUTHolder();
            StringHolder RETURNCODE = new StringHolder();
            StringHolder RETURNMSG = new StringHolder();
            for (int i = 0; i < itemList.size(); i++) {
                JSONObject itemObj = itemList.getJSONObject(i);
                com.redxun.rdmZhgl.core.wsdl.materialstatus.RES_IN res_in =
                    new com.redxun.rdmZhgl.core.wsdl.materialstatus.RES_IN();
                res_in.setRSNUM(itemObj.getString("RSNUM"));
                res_in.setRSPOS(itemObj.getString("RSPOS"));
                resIns[i] = res_in;
            }
            arrayOfRES_IN.value = resIns;
            service.ZWJ_GET_RESSTATE(arrayOfRESB_OUT, arrayOfRES_IN, RETURNCODE, RETURNMSG);
            String errorCode = RETURNCODE.value;
            JSONArray resultList = new JSONArray();
            // 获取每条信息的删除情况
            if ("OK".equals(errorCode)) {
                JSONObject itemObj;
                for (com.redxun.rdmZhgl.core.wsdl.materialstatus.RESB_OUT items_out : arrayOfRESB_OUT.value) {
                    itemObj = new JSONObject();
                    itemObj.put("RSNUM", items_out.getRSNUM());
                    itemObj.put("RSPOS", items_out.getRSPOS());
                    itemObj.put("XLOEK", items_out.getXLOEK());
                    itemObj.put("XWAOK", items_out.getXWAOK());
                    itemObj.put("WQSL", items_out.getWQSL());
                    itemObj.put("KZEAR", items_out.getKZEAR());
                    itemObj.put("RETURNCODE", items_out.getRETURNCODE());
                    itemObj.put("RETURNMSG", items_out.getRETURNMSG());
                    resultList.add(itemObj);
                }
            }
            resJson.put("errorCode", RETURNCODE.value);
            resJson.put("errorMsg", RETURNMSG.value);
            resJson.put("resultList", resultList);
        } catch (Exception e) {
            logger.error("Exception in getMaterialItemStatus", e);
            resJson.put("errorCode", "NG");
            resJson.put("errorMsg", "创建物料预留单号异常");
            return resJson;
        }
        return resJson;
    }

}
