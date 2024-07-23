package com.redxun.rdmCommon.core.util;

import java.util.Arrays;
import java.util.List;

public interface RdmConst {
    String CXY = "产学研";
    String CXY_GZ = "产学研（国重）类";

    String WEBAPPNAME_SIM = "sim";
    String STANDARD_SUBSYS = "standardManager";

    String[] NOT_SHOW_SYSTEM_NAMES = {"首页", "组织架构", "系统配置", "门户首页", "开发配置"};
    // 项目消息钉钉端
    String RECTYPE_DINGDING = "dingding";
    // 系统消息
    String MESSAGE_SYSTEM = "system";
    // 组消息
    String MESSAGE_GROUP = "group";
    // 仿真申请书交付物
    String PROJECT_DELIVERY_FZSQS = "仿真申请书";
    // 产品开发需求交付物
    String PROJECT_DELIVERY_CPJY = "产品开发建议书";
    String PROJECT_DELIVERY_SCFX = "产品竞争力分析报告";

    String I18N_ZH_CN = "zh_CN";

    String I18N_EN_US = "en_US";

    String GROUP_USER_BELONG = "GROUP-USER-BELONG";

    /********************** 角色 **************************************/
    String NOT_BZS_ADD_APPLY = "非标准所修订任务创建人";
    // 所有数据查看权限
    String AllDATA_QUERY_NAME = "所有数据查看权限";
    String ALLDATA_QUERY_KEY = "allDataQuery";
    String JSZX_ALL_PROJECT_QUERY = "技术中心所有项目查看人员";
    String JSZXXMGLRY = "技术中心项目管理人员";

    String GYJSBXMGLRY = "工艺技术部项目管理人员";

    String YYJSBXMGLRY = "应用技术部项目管理人员";

    String GY_FGLD = "工艺分管领导";
    String EXPORT_PARTSATLAS_ARCHIVE = "出口产品零件图册档案室人员";
    String EXPORT_PARTSATLAS_MACHINE_OPERATOR = "出口产品需求制造部扩充人员";
    String EXPORT_PARTSATLAS_ABNORMAL_NOTICE = "零件图册异常反馈通知人员";
    String STANDARD_DOCHECK_NOTICE_JSZX = "技术中心标准自查异常提醒人员";
    String STANDARD_DOCHECK_NOTICE_OTHER = "其它部门标准自查异常提醒人员";
    String JSZX_ZR = "技术中心主任";
    String JSZX_DZBSJ = "技术支部书记";
    String SQWJ_AllDATA = "售前文件所有数据查看";
    String JSZX_RZZY = "技术中心人资专员";
    String JSZX_SZ = "技术中心所长";
    String JSZX_LD = "技术中心领导";
    String FGLD = "分管领导";
    String ZLGLY = "专利管理员";
    String ZLGCS = "专利工程师";
    String HYGLZY = "会议管理专员";
    String WLSQZY = "物料申请专员";
    String CCBGZY = "出差报告专员";
    String HTGLZY = "合同管理专员";
    String BMHYGLZY = "部门会议管理专员";
    String NPIFILEOP = "npi文件操作人员";
    String GNCPYBSH = "售前文件-国内产品样本审核";
    String HWCPYBSH = "售前文件-海外产品样本审核";
    String FWSQWJFZR = "售前文件负责人";

    /*********************************** 部门 *******************************/
    String JSZX_NAME = "挖掘机械研究院";

    String ZLBZB_NAME = "质量保证部";

    String HWXSGLB_NAME = "海外销售管理部";

    String HWSCJSZC_NAME = "海外市场与技术支持部";

    String HWFWZC_NAME = "海外服务支持部";

    String BZJSS_NAME = "标准技术所";

    String FWGCS_NAME = "服务工程技术研究所";

    String ZNKZS_NAME = "智能控制研究所";

    String CSYJS_NAME = "测试研究所";

    String BJGS_NAME = "备件公司";

    String GJHCPS_NAME = "国际化产品研究一所";

    String GNYXGS_NAME = "挖机营销公司";

    String HWYXGS_NAME = "挖机海外营销公司";

    String CGGLB_NAME = "采购管理部";

    String GJHCPS_NAME2 = "国际化产品研究二所";

    String GCZX_NAME = "工程中心";

    String JSGLB_NAME = "技术管理部";

    String GFFZB_NAME = "供方发展部";

    String ZZGLB_NAME = "制造管理部";

    String XXHGLB_NAME = "数字化部";

    String GYJSB_NAME = "工艺技术部";

    String YYJSYJS_NAME = "产品应用技术研究所";

    String YYJSB_NAME = "应用技术部";

    String LBJYJS_NAME = "高端属具产品研究所";

    String XWYJS_NAME = "小挖研究所";
    /************************************ 零件图册制作任务状态 *****************/
    // 未领取
    String PARTS_ATLAS_STATUS_WLQ = "00WLQ";
    // 已领取
    String PARTS_ATLAS_STATUS_YLQ = "01YLQ";
    // 机型制作申请中
    String PARTS_ATLAS_STATUS_JXZZSQ = "02JXZZSQ";
    // 机型制作中
    String PARTS_ATLAS_STATUS_JXZZ = "03JXZZ";
    // 实例制作中
    String PARTS_ATLAS_STATUS_SLZZ = "04SLZZ";
    // 改制中
    String PARTS_ATLAS_STATUS_GZ = "05GZ";
    // 制作完成已转出
    String PARTS_ATLAS_STATUS_ZZWCYZC = "06ZZWCYZC";
    // 档案室已接收
    String PARTS_ATLAS_STATUS_YJS = "07YJS";
    // 作废
    String PARTS_ATLAS_STATUS_ZF = "08ZF";

    // 任务领取
    String PARTS_ATLAS_TASK_RECEIVE = "receive";

    // 机型制作流程节点
    String PARTS_ATLAS_MODEL_MADE_BZ = "服务工程所编制";
    String PARTS_ATLAS_MODEL_MADE_SQ = "产品主管申请";
    String PARTS_ATLAS_MODEL_MADE_ZZ = "服务工程所制作";

    // 异常反馈状态
    String PARTS_ATLAS_ABNORMAL_BZ = "编制";
    String PARTS_ATLAS_ABNORMAL_SH = "审核";
    String PARTS_ATLAS_ABNORMAL_FB = "发布";
    String PARTS_ATLAS = "零件图册";

    /******************* 标准自查状态 **********************/
    String STANDARD_DOCHECK_WC = "自查完成";
    String STANDARD_DOCHECK_STAGE_ZGPLANWRITE = "zgPlanWrite";
    String STANDARD_DOCHECK_STAGE_ZGPLANDEPTCHECK = "zgPlanDeptCheck";
    String STANDARD_DOCHECK_STAGE_ZGPLANWRITEAGAIN = "zgPlanWriteAgain";
    String STANDARD_DOCHECK_STAGE_ZGPLANDEPTCHECKAGAIN = "zgPlanDeptCheckAgain";
    String STANDARD_DOCHECK_STAGE_ZGRESULTWRITE = "zgResultWrite";
    String STANDARD_DOCHECK_STAGE_ZGRESULTDEPTCHECK = "zgResultDeptCheck";
    String STANDARD_DOCHECK_STAGE_ZGRESULTWRITEAGAIN = "zgResultWriteAgain";
    String STANDARD_DOCHECK_STAGE_ZGRESULTDEPTCHECKAGAIN = "zgResultDeptCheckAgain";

    List<String> STANDARD_DOCHECK_STAGE_FILTER_ARR =
        Arrays.asList(STANDARD_DOCHECK_STAGE_ZGPLANWRITE, STANDARD_DOCHECK_STAGE_ZGPLANDEPTCHECK,
            STANDARD_DOCHECK_STAGE_ZGPLANWRITEAGAIN, STANDARD_DOCHECK_STAGE_ZGPLANDEPTCHECKAGAIN,
            STANDARD_DOCHECK_STAGE_ZGRESULTWRITE, STANDARD_DOCHECK_STAGE_ZGRESULTDEPTCHECK,
            STANDARD_DOCHECK_STAGE_ZGRESULTWRITEAGAIN, STANDARD_DOCHECK_STAGE_ZGRESULTDEPTCHECKAGAIN);

    /************************ 系统模块的key *************************/
    String SUBSYS_SOFT_KEY = "KZRJGL";
}
