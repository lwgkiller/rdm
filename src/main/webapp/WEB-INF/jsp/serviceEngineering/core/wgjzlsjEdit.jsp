<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>外购件资料收集编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <style type="text/css">
        fieldset {
            border: solid 1px #aaaaaab3;
            min-width: 920px;
        }
    </style>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="wgjzlsjProcessInfo()">流程信息</a>
        <a id="editDiskPath" class="mini-button" style="display: none;" onclick="submitWgjzlsj()">提交</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formWgjzlsj" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="taskId" name="taskId" class="mini-hidden"/>
            <input id="step" name="step" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold;">外购件资料收集及制作</caption>
                <tr>
                    <td style="width: 14%">单据编号(自动生成)：</td>
                    <td colspan="3">
                        <input id="businessNo" name="businessNo" class="mini-textbox" enabled="false" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 17%">资料类型：</td>
                    <td style="width: 33%;min-width:170px">
                        <input id="dataType" name="dataType" class="mini-checkboxlist" multiSelect="false" style="width:98%"
                               textField="value" valueField="key" emptyText="请选择..."
                               data="[{key:'维修手册类',value:'维修手册类'},{key:'零件图册类',value:'零件图册类'},{key:'使用说明',value: '使用说明'}]"
                        />
                    </td>
                    <td style="width: 14%">物料编码：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="materialCode" name="materialCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">物料名称：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="materialName" name="materialName" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 14%">物料描述：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="materialDescription" name="materialDescription" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">物料所属部门(自动生成)：</td>
                    <td style="width: 36%;min-width:140px">
                        <input id="materialDepartmentId" name="materialDepartmentId" class="mini-dep rxc" plugins="mini-dep"
                               style="width:98%;height:34px" enabled="false"
                               allowinput="false" textname="materialDepartment" length="200" maxlength="200" minlen="0" single="true"
                               initlogindep="false"/>
                    </td>
                    <td style="width: 14%">反馈来源：</td>
                    <td style="width: 36%;">
                        <input id="office" name="office" class="mini-checkboxlist" multiSelect="false" style="width:98%"
                               textField="value" valueField="key" emptyText="请选择..."
                               data="[{key:'实例图册',value:'实例图册'},{key:'机型图册',value:'机型图册'},{key:'各类通知单号',value:'各类通知单号'}]"
                               onvalueChanged="disableOrderNo"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">通知单号：</td>
                    <td style="width: 36%;">
                        <input id="orderNo" name="orderNo" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 14%">设计机型：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="designModel" name="designModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">反馈日期：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="submitDate" name="submitDate" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               timeFormat="HH:mm:ss" showTime="true" showOkButton="true" showClearButton="false" style="width:98%;"/>
                    </td>
                    <td style="width: 14%">服务工程责任人：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="fwgcPrincipalId" name="fwgcPrincipalId" textname="fwgcPrincipal" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="服务工程责任人" length="50"
                               mainfield="no" single="true" onvalueChanged="fwgcPrincipalIdChange"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">产品所责任人：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="cpsPrincipalId" name="cpsPrincipalId" textname="cpsPrincipal" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="产品所责任人" length="50"
                               mainfield="no" single="true" onvalueChanged="cpsPrincipalIdChange"/>
                    </td>
                    <td style="width: 14%">预计归档完成时间：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="filingTimeExp " name="filingTimeExp" class="mini-datepicker" allowInput="false" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">供应商：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="supplier" name="supplier" class="mini-textbox" style="width:98%;"/>

                    </td>
                    <td style="width: 14%">供应商联系人：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="supplierContact" name="supplierContact" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">供应商联系方式：</td>
                    <td colspan="3">
                        <input id="supplieContactWay" name="supplieContactWay" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">是否已归档：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="filing" name="filing" class="mini-combobox" style="width:98%"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{key:'yes',value:'是'},{key:'no',value:'否'}]"
                        />
                    </td>
                    <td style="width: 14%">归档完成时间：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="filingTime" name="filingTime" class="mini-datepicker" allowInput="false" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">外购件资料网盘路径：</td>
                    <td colspan="3">
                        <input id="networkDiskPath" name="networkDiskPath" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">是否已制作：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="thirdMake" name="thirdMake" class="mini-combobox" style="width:98%"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{key:'yes',value:'是'},{key:'no',value:'否'}]"
                        />
                    </td>
                    <td style="width: 14%">预计制作完成时间：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="yjwcsj" name="yjwcsj" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">计划制作完成时间：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="makeTimePlan" name="makeTimePlan" class="mini-datepicker" allowInput="false" style="width:98%;"/>
                    </td>
                    <td style="width: 14%">制作完成时间：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="makeTime" name="makeTime" class="mini-datepicker" allowInput="false" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">外购图册版本号/手册Topic号：</td>
                    <td colspan="3">
                        <input id="VTNo" name="VTNo" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">备注：</td>
                    <td colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000"
                                  minlen="0" allowinput="true"
                                  emptytext="请输入备注" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<%--JS--%>
<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var taskStatus = "${taskStatus}";
    var jsUseCtxPath = "${ctxPath}";
    var formWgjzlsj = new mini.Form("#formWgjzlsj");
    var wgjzlsjId = "${wgjzlsjId}";
    var nodeVarsStr = '${nodeVars}';
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var isFwgc = "${isFwgc}";
    var mainGroupName = "${mainGroupName}";
    var mainGroupId = "${mainGroupId}";
    var currentUserId = "${currentUserId}";
    var nodeName = "";
    var isfwgcPrincipalIdChange = false;
    var iscpsPrincipalIdChange = false;
    //..
    $(function () {
        if (wgjzlsjId) {
            var url = jsUseCtxPath + "/serviceEngineering/core/wgjzlsj/getWgjzlsjDetail.do";
            $.ajaxSettings.async = false;
            $.post(
                url,
                {wgjzlsjId: wgjzlsjId},
                function (json) {
                    formWgjzlsj.setData(json);
                    if (json.office == '各类通知单号') {
                        mini.get("orderNo").setEnabled(true);
                    } else {
                        mini.get("orderNo").setEnabled(false);
                    }
                });
            $.ajaxSettings.async = true;
        }
        //明细入口
        if (action == 'detail') {
            formWgjzlsj.setEnabled(false);
            $("#detailToolBar").show();
            //非草稿放开流程信息查看按钮
            if (taskStatus != 'DRAFTED') {
                $("#processInfo").show();
            }
        } else if (action == 'task') {
            taskActionProcess();
        } else if (action == 'editDiskPath') {
            $("#detailToolBar").show();
            $("#editDiskPath").show();
            formWgjzlsj.setEnabled(false);
            mini.get("networkDiskPath").setEnabled(true);
        }
        if (action == 'edit') {//首次编辑
            formWgjzlsj.setEnabled(false);
            mini.get("dataType").setEnabled(true);
            mini.get("materialCode").setEnabled(true);
            mini.get("materialName").setEnabled(true);
            mini.get("materialDescription").setEnabled(true);
            mini.get("office").setEnabled(true);
            mini.get("orderNo").setEnabled(true);
            mini.get("designModel").setEnabled(true);
            mini.get("fwgcPrincipalId").setEnabled(true);
            mini.get("cpsPrincipalId").setEnabled(true);
            mini.get("remark").setEnabled(true);
        }
    });
    //..
    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();
        //获取环境变量
        if (!nodeVarsStr) {
            nodeVarsStr = "[]";
        }
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'nodeName') {
                nodeName = nodeVars[i].DEF_VAL_;
            }
        }
        formWgjzlsj.setEnabled(false);
        mini.get("remark").setEnabled(true);
        if (nodeName == 'A') {//编制
            mini.get("dataType").setEnabled(true);
            mini.get("materialCode").setEnabled(true);
            mini.get("materialName").setEnabled(true);
            mini.get("materialDescription").setEnabled(true);
            mini.get("office").setEnabled(true);
            mini.get("orderNo").setEnabled(true);
            mini.get("designModel").setEnabled(true);
            mini.get("fwgcPrincipalId").setEnabled(true);
            mini.get("cpsPrincipalId").setEnabled(true);
        } else if (nodeName == 'B') {//资料确认
            mini.get("filing").setEnabled(true);
            mini.get("filingTime").setEnabled(true);
            mini.get("networkDiskPath").setEnabled(true);
            mini.get("fwgcPrincipalId").setEnabled(true);
        } else if (nodeName == 'C') {//所内收集
            mini.get("filing").setEnabled(true);
            mini.get("filingTime").setEnabled(true);
            mini.get("networkDiskPath").setEnabled(true);
            mini.get("supplier").setEnabled(true);
            mini.get("supplierContact").setEnabled(true);
            mini.get("supplieContactWay").setEnabled(true);
            mini.get("cpsPrincipalId").setEnabled(true);
        } else if (nodeName == 'E') {//制作任务调整
            mini.get("makeTimePlan").setEnabled(true);
        } else if (nodeName == 'G') {//制作更新
            mini.get("thirdMake").setEnabled(true);
            mini.get("VTNo").setEnabled(true);
        }
    }
    //..
    function getData() {
        var formData = _GetFormJsonMini("formWgjzlsj");
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        //此处用于向后台产生流程实例时替换标题中的参数时使用
        formData.bos = [];
        formData.vars = [{key: 'materialCode', val: formData.materialCode}];
        return formData;
    }
    //..
    function disableOrderNo() {
        var office = mini.get("office").getValue();
        if (office == '各类通知单号') {
            mini.get("orderNo").setEnabled(true);
        } else {
            mini.get("orderNo").setValue("");
            mini.get("orderNo").setEnabled(false);
        }
    }
    //..
    function fwgcPrincipalIdChange() {
        isfwgcPrincipalIdChange = true;
    }
    //..
    function cpsPrincipalIdChange() {
        iscpsPrincipalIdChange = true;
    }
    //..保存草稿
    function saveWgjzlsj(e) {
        var caoGaoValid = validCaoGao();
        if (!caoGaoValid.result) {
            mini.alert(caoGaoValid.message);
            return;
        }
        window.parent.saveDraft(e);
    }
    //..保存草稿必选类型，因为生成编号过程只写在create方法中出现一次
    function validCaoGao() {
        var dataType = mini.get("dataType").getValue();
        if (!dataType) {
            return {"result": false, "message": "请选择资料类型"};
        }
        return {"result": true};
    }
    //..点击提交时触发保存网盘路径用
    function submitWgjzlsj(e) {
        var postData = _GetFormJsonMini("formWgjzlsj");
        if (!postData.networkDiskPath) {
            mini.alert('请输入外购件网盘路径');
            return;
        }
        $.ajax({
            url: jsUseCtxPath + "/serviceEngineering/core/wgjzlsj/saveWgjzlsj.do",
            type: 'POST',
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            CloseWindow()
                        }
                    });
                }
            }
        });
    }
    //..启动流程
    function startWgjzlsjProcess(e) {
        var bianZhiValid = validBianZhi();
        if (!bianZhiValid.result) {
            mini.alert(bianZhiValid.message);
            return;
        }
        window.parent.startProcess(e);
    }
    //..流程中暂存信息（如编制阶段）
    function saveWgjzlsjInProcess() {
        var formData = _GetFormJsonMini("formWgjzlsj");
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/wgjzlsj/saveWgjzlsj.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        message = "数据保存成功";
                        isfwgcPrincipalIdChange = false;
                        iscpsPrincipalIdChange = false;
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
    //..流程中的审批或者下一步
    function wgjzlsjApprove() {
        var valid = true;
        if (nodeName == 'A') {//编制
            valid = validBianZhi();
            if (!valid.result) {
                mini.alert(valid.message);
                return;
            }
            mini.get("step").setValue("A");
        } else if (nodeName == 'B') {//资料确认
            valid = validB()
            if (!valid.result) {
                mini.alert(valid.message);
                return;
            }
            mini.get("step").setValue("B");
        } else if (nodeName == 'C') {//所内收集
            valid = validC()
            if (!valid.result) {
                mini.alert(valid.message);
                return;
            }
            mini.get("step").setValue("C");
        } else if (nodeName == 'D') {//资料再确认
            mini.get("step").setValue("D");
        } else if (nodeName == 'E') {//制作任务调整
            valid = validE()
            if (!valid.result) {
                mini.alert(valid.message);
                return;
            }
            mini.get("step").setValue("E");
        } else if (nodeName == 'F') {//所内审核
            mini.get("step").setValue("F");
        } else if (nodeName == 'G') {//制作更新
            valid = validG()
            if (!valid.result) {
                mini.alert(valid.message);
                return;
            }
            mini.get("step").setValue("G");
        }

        //检查通过
        window.parent.approve();
    }
    //..检验表单是否必填(编制)
    function validBianZhi() {
        var dataType = mini.get("dataType").getValue();
        if (!dataType) {
            return {"result": false, "message": "请选择资料类型"};
        }
        var materialCode = $.trim(mini.get("materialCode").getValue())
        if (!materialCode) {
            return {"result": false, "message": "请输入物料编码"};
        }
        var materialName = $.trim(mini.get("materialName").getValue())
        if (!materialName) {
            return {"result": false, "message": "请输入物料名称"};
        }
        var materialDescription = $.trim(mini.get("materialDescription").getValue());
        if (!materialDescription) {
            return {"result": false, "message": "请输入物料描述"};
        }
        var office = $.trim(mini.get("office").getValue())
        if (!office) {
            return {"result": false, "message": "请选择反馈来源"};
        }
        var orderNo = $.trim(mini.get("orderNo").getValue())
        if (office == '各类通知单号' && !orderNo) {
            return {"result": false, "message": "请输入通知单号"};
        }
        var designModel = $.trim(mini.get("designModel").getValue())
        if (!designModel) {
            return {"result": false, "message": "请输入设计机型"};
        }
        var fwgcPrincipalId = $.trim(mini.get("fwgcPrincipalId").getValue())
        if (!fwgcPrincipalId) {
            return {"result": false, "message": "请选择服务工程负责人"};
        }
        var cpsPrincipalId = mini.get("cpsPrincipalId").getValue();
        if (!cpsPrincipalId) {
            return {"result": false, "message": "请选择产品所责任人"};
        }
        var businessNo = mini.get("businessNo").getValue();
        if (!businessNo) {
            return {"result": false, "message": "请先保存草稿生成编号"};
        }
        return {"result": true};
    }
    //..检验表单是否必填(资料确认)
    function validB() {
        var fwgcPrincipalId = $.trim(mini.get("fwgcPrincipalId").getValue())//转办场景
        if (!fwgcPrincipalId) {
            return {"result": false, "message": "请选择服务工程负责人"};
        }
        var filing = mini.get("filing").getValue();
        if (!filing) {
            return {"result": false, "message": "请选择是否已归档"};
        } else {
            if (filing == 'yes') {
                var filingTime = mini.get("filingTime").getValue();
                if (!filingTime) {
                    return {"result": false, "message": "请选择归档完成时间"}
                }
                var networkDiskPath = $.trim(mini.get("networkDiskPath").getValue());
                if (!networkDiskPath) {
                    return {"result": false, "message": "请输入外购件网盘路径"}
                }
            }
        }
        return {"result": true};
    }
    //..检验表单是否必填(所内收集)
    function validC() {
        var cpsPrincipalId = mini.get("cpsPrincipalId").getValue();//转办场景
        if (!cpsPrincipalId) {
            return {"result": false, "message": "请选择产品所责任人"};
        }
        var filing = mini.get("filing").getValue();
        if (!filing) {
            return {"result": false, "message": "请选择是否已归档"};
        } else {
            if (filing == 'yes') {
                var filingTime = mini.get("filingTime").getValue();
                if (!filingTime) {
                    return {"result": false, "message": "请选择归档完成时间"}
                }
                var networkDiskPath = $.trim(mini.get("networkDiskPath").getValue());
                if (!networkDiskPath) {
                    return {"result": false, "message": "请输入外购件网盘路径"}
                }
            } else if (filing == 'no') {
                return {"result": false, "message": "未归档不允许下一步，确认不提供可以选择驳回，驳回原因为“确认不提供”"}
            }
        }
        var supplier = $.trim(mini.get("supplier").getValue());
        if (!supplier) {
            return {"result": false, "message": "请输入供应商"};
        }
        var supplierContact = $.trim(mini.get("supplierContact").getValue());
        if (!supplierContact) {
            return {"result": false, "message": "请输入供应商联系人"};
        }
        var supplieContactWay = $.trim(mini.get("supplieContactWay").getValue());
        if (!supplieContactWay) {
            return {"result": false, "message": "请输入供应商联系方式"};
        }
        var isReptition = validReptition();
        if (!isReptition) {
            return {"result": false, "message": "物料已存在"};
        }
        return {"result": true};
    }
    //..检验表单是否必填(制作任务调整)
    function validE() {
        var makeTimePlan = mini.get("makeTimePlan").getValue();
        if (!makeTimePlan) {
            return {"result": false, "message": "请选择计划制作完成时间"}
        }
        return {"result": true};
    }
    //..检验表单是否必填(制作更新)
    function validG() {
        var thirdMake = mini.get("thirdMake").getValue();
        if (!thirdMake) {
            return {"result": false, "message": "请选择是否已制作"};
        }
        var VTNo = mini.get("VTNo").getValue();
        if (!VTNo) {
            return {"result": false, "message": "请输入外购图册版本号/手册Topic号"};
        }
        return {"result": true};
    }
    //..校验是否重复
    function validReptition() {
        var valid = true;
        var postData = {};
        postData.id = mini.get("id").getValue();
        postData.materialCode = $.trim(mini.get("materialCode").getValue());
        postData.supplier = $.trim(mini.get("supplier").getValue());
        postData.dataType = mini.get("dataType").getValue();
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/wgjzlsj/validReptition.do',
            type: 'post',
            async: false,
            data: mini.encode(postData),
            contentType: 'application/json',
            success: function (data) {
                if (!data.success) {
                    valid = false;
                }
            }
        });
        return valid;
    }
    //..
    function wgjzlsjProcessInfo() {
        var instId = $("#instId").val();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: "流程图实例",
            width: 800,
            height: 600
        });
    }
    //..
    function doTransferDiy() {
        if (nodeName == 'B') {//资料确认
            if (currentUserId == mini.get("fwgcPrincipalId").getValue()) {
                mini.alert("转办之前请指定“服务工程责任人”为转办目标用户");
                return;
            } else if (isfwgcPrincipalIdChange == true) {
                mini.alert("请先点击暂存信息保存新的“服务工程责任人”");
                return;
            } else {
                mini.confirm("转办之前请指定“服务工程责任人”为转办目标用户,确认指定完成了吗？", "提示", function (action) {
                    if (action != 'ok') {
                        return;
                    } else {
                        var url = __rootPath + '/bpm/core/bpmTask/transfer.do?taskIds=' + mini.get("taskId").getValue();
                        _OpenWindow({
                            title: '任务转办',
                            url: url,
                            height: 350,
                            width: 1000,
                            ondestroy: function (action) {
                                if (action == "ok") {
                                    CloseWindow('ok');
                                }
                            }
                        });
                    }
                })
            }
        } else if (nodeName == 'C') {//所内收集
            if (currentUserId == mini.get("cpsPrincipalId").getValue()) {
                mini.alert("转办之前请指定“产品所责任人”为转办目标用户");
                return;
            } else if (iscpsPrincipalIdChange == true) {
                mini.alert("请先点击暂存信息保存新的“产品所责任人”");
                return;
            } else {
                mini.confirm("转办之前请指定“服务工程责任人”为转办目标用户,确认指定完成了吗？", "提示", function (action) {
                    if (action != 'ok') {
                        return;
                    } else {
                        var url = __rootPath + '/bpm/core/bpmTask/transfer.do?taskIds=' + mini.get("taskId").getValue();
                        _OpenWindow({
                            title: '任务转办',
                            url: url,
                            height: 350,
                            width: 1000,
                            ondestroy: function (action) {
                                if (action == "ok") {
                                    CloseWindow('ok');
                                }
                            }
                        });
                    }
                })
            }
        }
    }
</script>
</body>
</html>
