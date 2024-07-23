<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>属具型号申请</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow1" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<%----%>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="formBusiness" method="post" style="height: 95%;width: 98%">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="businessNo" name="businessNo" class="mini-hidden"/>
            <input id="businessStatus" name="businessStatus" class="mini-hidden"/>
            <input id="applyTime" name="applyTime" class="mini-hidden"/>
            <input id="INST_ID_" name="INST_ID_" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    属具设计型号申请
                </caption>
                <tr>
                    <td style="text-align: center;width: 20%">申请人：</td>
                    <td style="min-width:170px">
                        <input id="applyUserId" name="applyUserId" textname="applyUserName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="申请人" length="50"
                               mainfield="no" single="true" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">申请部门：</td>
                    <td style="min-width:170px">
                        <input id="applyUserDeptName" name="applyUserDeptName" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">属具类型：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="attachedtoolsType" name="attachedtoolsType"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByTreeKeyTopOne.do?dicKey=attachedtoolsType"
                               valueField="key" textField="value" onvaluechanged="attachedtoolsTypeChanged"/>
                    </td>
                    <td style="text-align: center;width: 15%">属具子类型：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="attachedtoolsType2" name="attachedtoolsType2"
                               class="mini-combobox" style="width:98%"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">设计型号：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="designModel" name="designModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%">适配挖机吨位：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="suitableTonnage" name="suitableTonnage" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">产品主管：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="responsibleId" name="responsibleId" textname="responsibleName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="产品主管" length="50"
                               mainfield="no" single="true"/>
                    </td>
                    <td style="text-align: center;width: 20%">物料大类：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="wlNumber" name="wlNumber" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">规划内外销售：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="abroad" name="abroad" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key"
                               emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="请选择..."
                               multiSelect="true"
                               data="[{'key' : '国内','value' : '国内'},{'key' : '出口','value' : '出口'}]"/>
                    </td>
                    <td style="text-align: center;width: 20%">规划销售区域或国家：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="region" name="region" class="mini-combobox" style="width:98%"
                               multiSelect="true"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=CPXPGHXSQYGJ"
                               valueField="name" textField="name"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">产品设计文件夹存放位置：<span style="color:red">*</span></td>
                    <td colspan="3">
                        <input id="location" name="location" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<%----%>
<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var businessId = "${businessId}";
    var nodeVarsStr = '${nodeVars}';
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var formBusiness = new mini.Form("#formBusiness");
    var codeName = "";
    //..
    $(function () {
        var url = jsUseCtxPath + "/productDataManagement/core/attachedtoolsSpectrumItemModelApply/getDataById.do";
        $.post(
            url,
            {businessId: businessId},
            function (json) {
                formBusiness.setData(json);
                if (action == 'detail') {
                    formBusiness.setEnabled(false);
                    $("#detailToolBar").show();
                    if (status != 'DRAFTED') {
                        $("#processInfo").show();
                    }
                } else if (action == 'task') {
                    taskActionProcess();
                }
            });
    });
    //..获取任务相关的环境变量，处理表单可见性
    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();
        //获取环境变量
        if (!nodeVarsStr) {
            nodeVarsStr = "[]";
        }
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'codeName') {
                codeName = nodeVars[i].DEF_VAL_;
            }
        }
        if (codeName != 'A') {//如果不是编辑节点，首先吧表单和文件的上传权限都封死
            formBusiness.setEnabled(false);
        }
    }
    //..流程引擎调用此方法进行表单数据的获取
    function getData() {
        var formData = _GetFormJsonMini("formBusiness");
        return formData;
    }
    //..保存草稿
    function saveBusiness(e) {
        window.parent.saveDraft(e);
    }
    //..流程中暂存信息（如编制阶段）
    function saveBusinessInProcess() {
        var formData = getData();
        $.ajax({
            url: jsUseCtxPath + '/productDataManagement/core/attachedtoolsSpectrumItemModelApply/saveBusiness.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        message = "数据保存成功";
                    } else {
                        message = "数据保存失败" + data.message;
                    }
                    mini.alert(message, "提示信息", function () {
                        window.location.reload();
                    });
                }
            }
        });
    }
    //..启动流程
    function startBusinessProcess(e) {
        var formValid = validBusiness();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }
    //..下一步审批
    function businessApprove() {
        //编制阶段和流程期间需要上传文件的节点，下一步需要校验表单必填字段
        if (codeName == "A") {
            var formValid = validBusiness();//各种类型的复杂情况都在里面了，除了关联产品的数量放在了okWindow里
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        window.parent.approve();
    }
    //..检验表单是否必填
    function validBusiness() {
        var attachedtoolsType = $.trim(mini.get("attachedtoolsType").getValue());
        if (!attachedtoolsType) {
            return {"result": false, "message": "属具类型不能为空"};
        }
        var attachedtoolsType2 = $.trim(mini.get("attachedtoolsType2").getValue());
        if (!attachedtoolsType2) {
            return {"result": false, "message": "属具子类型不能为空"};
        }
        var designModel = $.trim(mini.get("designModel").getValue());
        if (!designModel) {
            return {"result": false, "message": "设计型号不能为空"};
        }
        //这里要验证罗马字符
        if (hasRomanCharacters(designModel)) {
            return {"result": false, "message": "设计型号中不能包含罗马字符及空格"};
        }
        var suitableTonnage = $.trim(mini.get("suitableTonnage").getValue());
        if (!suitableTonnage) {
            return {"result": false, "message": "适配挖机吨位不能为空"};
        }
        var responsibleId = $.trim(mini.get("responsibleId").getValue());
        if (!responsibleId) {
            return {"result": false, "message": "产品主管不能为空"};
        }
        var wlNumber = $.trim(mini.get("wlNumber").getValue());
        if (!wlNumber) {
            return {"result": false, "message": "物料大类不能为空"};
        }
        var abroad = $.trim(mini.get("abroad").getValue());
        if (!abroad) {
            return {"result": false, "message": "规划内外销售区域不能为空"};
        }
        var region = $.trim(mini.get("region").getValue());
        if (!region) {
            return {"result": false, "message": "规划销售区域不能为空"};
        }
        var location = $.trim(mini.get("location").getValue());
        if (!location) {
            return {"result": false, "message": "产品设计文件夹存放位置不能为空"};
        }
        //验证产品型号申请中是否有除自身之外的running或success_end的数据，如果没有再检查型谱表中是否有这个产品设计型号
        var result = {"result": true};
        $.ajax({
            url: jsUseCtxPath + '/productDataManagement/core/attachedtoolsSpectrumItemModelApply/' +
            'checkDesignModelValid.do?designModel=' + designModel + "&id=" + businessId,
            async: false,
            success: function (returnData) {
                if (!returnData.success) {
                    result = {"result": false, "message": returnData.message};
                }
            }
        });
        return result;
    }
    //..
    function hasRomanCharacters(str) {
        //这里加了两个空格 一个中文一个英文=
        var romanRegex = /[ⅠⅡⅢⅣⅤⅥⅦⅧⅨⅩⅪⅫ  ]/i;
        return romanRegex.test(str);
    }
    //..
    function attachedtoolsTypeChanged() {
        var parent = mini.get('attachedtoolsType').getSelected();
        mini.get('attachedtoolsType2').setValue("");
        if (parent) {
            var url = '${ctxPath}/sys/core/sysDic/getByParentId.do?parentId=' + parent.dicId;
            mini.get('attachedtoolsType2').setUrl(url);
            mini.get('attachedtoolsType2').select(0);
        }
    }
</script>
</body>
</html>
