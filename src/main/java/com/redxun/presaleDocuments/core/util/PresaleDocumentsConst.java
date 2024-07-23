package com.redxun.presaleDocuments.core.util;

public interface PresaleDocumentsConst {
    //文件状态-字典
    static final class PresaleDocumentStatus {
        public static final String BUSINESS_STATUS_BIANJIZHONG = "编辑中";//编辑中
        public static final String BUSINESS_STATUS_YIFABU = "已发布";//已发布
        public static final String BUSINESS_STATUS_LISHIBANBEN = "历史版本";//历史版本
    }

    //文件类型-字典
    static final class PresaleDocumentType {
        public static final String BUSINESS_TYPE_JISHUGUIGESHU = "技术规格书";
        public static final String BUSINESS_TYPE_CHANPINBIAOXUANPEIBIAO = "产品标选配表";
        public static final String BUSINESS_TYPE_CHANPINLIANGDIANJIJINGZHENGLIFENXI = "产品亮点及竞争力分析";
        public static final String BUSINESS_TYPE_CHANPINJIBENJIEGOUGONGNENGYUYUANLIJIESHAO = "产品基本结构功能与原理介绍";
        public static final String BUSINESS_TYPE_CHANPINDAOGOUSHOUCE = "产品导购手册";
        public static final String BUSINESS_TYPE_CHANPINQUANSHENGMINGZHOUQICHENGBENQINGDAN = "产品全生命周期成本清单";
        public static final String BUSINESS_TYPE_DUOGONGNENGJIJUXITONGYALILIULIANGFANWEIBIAOZHUNZHIBIAO = "多功能机具系统压力流量范围标准值表";
        public static final String BUSINESS_TYPE_JISHUWENJIANFUJIAN = "技术文件资料附件";
    }

    //销售区域-引用服务工程模块字典
    static final class serviceEngineeringDecorationManualSalesArea {
        public static final String BUSINESS_SALESAREA_BEIMEI = "北美";
        public static final String BUSINESS_SALESAREA_OUZHOU = "欧洲";
        public static final String BUSINESS_SALESAREA_AODALIYA = "澳大利亚";
        public static final String BUSINESS_SALESAREA_NEIXIAO = "内销";
        public static final String BUSINESS_SALESAREA_QITA = "其他";

    }
}
