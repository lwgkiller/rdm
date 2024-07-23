package com.redxun.materielextend.core.util;

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
 * @since 2020/6/8 17:58
 */
public interface MaterielConstant {
    // 申请单暂存
    String APPLY_SAVE_ACTION_TEMP = "saveTemp";
    // 申请单提交
    String APPLY_SAVE_ACTION_COMMIT = "saveCommit";
    // 申请人操作
    String APPLY_OP_SQRKC = "SQRKC";
    // 制造操作
    String APPLY_OP_ZZKC = "ZZKC";
    // 物流操作
    String APPLY_OP_WLKC = "WLKC";
    // 财务操作
    String APPLY_OP_CWKC = "CWKC";
    // 采购操作
    String APPLY_OP_CGKC = "CGKC";
    // 供方操作
    String APPLY_OP_GFKC = "GFKC";
    // 工艺操作
    String APPLY_OP_GYKC = "GYKC";
    // 公共操作
    String APPLY_OP_COMMON = "COMMON";

    // 物料的编辑或者新增操作
    String MATERIEL_ACTION_EDIT = "edit";
    // 物料的查看操作
    String MATERIEL_ACTION_VIEW = "view";

    // 申请单状态，草稿
    String APPLYSTATUS_DRAFT = "draft";
    // 申请单状态，运行中
    String APPLYSTATUS_RUNNING = "running";
    // 申请单状态，成功结束
    String APPLYSTATUS_SUCCESSEND = "successEnd";
    // 申请单状态，异常结束
    String APPLYSTATUS_FAILEND = "failEnd";
    // 申请单状态，待更新至SAP
    String APPLYSTATUS_NEEDSYNC2SAP = "needSync2Sap";

    // 扩充节点状态，未进行
    String KCSTATUS_NO = "00";
    // 扩充节点状态，进行中
    String KCSTATUS_DOING = "01";
    // 扩充节点状态，已完成
    String KCSTATUS_DONE = "02";

    // 物料最终扩充结果，成功
    String EXTENDRESULT_SUCCESS = "success";
    // 物料最终扩充结果，失败
    String EXTENDRESULT_FAIL = "fail";

    // 物料标记错误，是
    String MARK_ERROR_YES = "yes";
    // 物料标记错误，否
    String MARK_ERROR_NO = "no";

    // 字段必填，yes
    String REQUIRED_YES = "yes";
    // 字段必填，no
    String REQUIRED_NO = "no";

    // 前置属性等于
    String PRE_OP_YES = "yes";
    // 前置属性不等于
    String PRE_OP_NO = "no";

    // 物料状态
    String WLZT = "D";
}
