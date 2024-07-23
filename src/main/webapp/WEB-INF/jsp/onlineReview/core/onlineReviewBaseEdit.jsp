<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>出口线上评审</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit" id="content">
    <div class="form-container" style="margin: 0 auto; width: 100%">
        <form id="formOnlineReviewBase" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <p style="font-size: 16px;font-weight: bold">订单信息</p>
            <hr/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="  width: 25%;text-align: center">订单需求单位生产通知单号：</td>
                    <td style="width: 25%">
                        <input id="noticeNo" name="noticeNo" class="mini-textbox" style="  width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 25%">订单需求单位名称：</td>
                    <td>
                        <input id="company" name="company" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="订单需求单位名称："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="text" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=company"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                </tr>
                <tr>
                    <td style="  width: 20%;text-align: center">订单需求单位业务员：</td>
                    <td style="width: 20%">
                        <input id="respName" name="respName" class="mini-textbox" style="  width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%">出口区域：
                    </td>
                    <td>
                        <input id="saleArea" name="saleArea" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="出口区域："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="text" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=reviewSaleArea"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                </tr>
                <tr>
                    <td style="  width: 20%;text-align: center">出口国家：</td>
                    <td style="width: 20%">
                        <input id="saleCountry" name="saleCountry" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="出口国家："
                               length="50" valueFromSelect="true"
                               only_read="false" required="true" allowinput="true" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="text" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=saleCountry"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td style="text-align: center;width: 20%">内部订单号：</td>
                    <td>
                        <input id="saleNum" name="saleNum" class="mini-textbox" style="  width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="  width: 20%;text-align: center">合同类型：</td>
                    <td style="width: 20%" colspan="3">
                        <input id="contractType" name="contractType" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="合同类型："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="true"
                               textField="text" valueField="text" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=contractType"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                </tr>
            </table>
        </form>
        <p style="font-size: 16px;font-weight: bold">机型汇总信息</p>
        <hr/>
        <div style="margin-top: 5px;margin-bottom: 2px">
            <a id="addModelDetail" style="margin-right: 5px;display: none" class="mini-button"
               onclick="addModelDetail()">添加</a>
            <a id="editModelDetail" style="margin-right: 5px;display: none" class="mini-button"
               onclick="editModelDetail()">编辑</a>
            <a id="lookModelDetail" style="margin-right: 5px;display: none" class="mini-button"
               onclick="lookModelDetail()">查看</a>
            <a id="removeModelDetail" style="margin-right: 5px;display: none" class="mini-button"
               onclick="removeModelDetail()">删除</a>
            <a id="uploadczq" style="margin-right: 5px;display: none" class="mini-button"
               onclick="uploadczq()">上传长周期件明细</a>
        </div>
        <div id="modelDetailListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
             allowCellWrap="true"
             idField="id" url="${ctxPath}/onlineReview/core/getModelDetailList.do?belongId=${id}"
             autoload="true"
             multiSelect="false" showPager="false" showColumnsMenu="false" allowAlternating="true">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div field="jsNum" width="50px" headerAlign="center" align="center">技术通知编号</div>
                <div field="saleModel" width="40px" headerAlign="center" align="center">销售型号</div>
                <div field="designModel" width="50px" headerAlign="center" align="center">设计型号</div>
                <div field="materielCode" headerAlign="center" align="center" width="50px">整机物料号</div>
                <div field="num" width="30px" headerAlign="center" align="center">数量</div>
                <div field="jszgName" width="30px" headerAlign="center" align="center">技术主管</div>
                <div field="gyzgName" width="30px" headerAlign="center" align="center">工艺主管</div>
                <div field="jhyName" width="40px" headerAlign="center" align="center">制造部计划员</div>
                <div field="needTime" dateFormat="yyyy-MM-dd" width="40px" headerAlign="center" align="center">
                    需求交货日期
                </div>
                <div field="planTime" dateFormat="yyyy-MM-dd" width="40px" headerAlign="center" align="center">
                    预计交货日期
                </div>
                <div field="timeRisk" width="40" align="center" headerAlign="center" renderer="onRiskStatusRenderer">是否满足交货期</div>
                <div field="gzOption" headerAlign='center' align='center' width="40">研发领导意见</div>
                <div field="gzReason" headerAlign='center' align='center' width="60">不同意原因</div>
                <div field="yzOption" headerAlign='center' align='center' width="40">工艺领导意见</div>
                <div field="yzReason" headerAlign='center' align='center' width="60">不同意原因</div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var status = "${status}";
    var action = "${action}";
    var nodeVarsStr = '${nodeVars}';
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var deptName = "${deptName}";
    var modelDetailListGrid = mini.get("modelDetailListGrid");
    var formOnlineReviewBase = new mini.Form("#formOnlineReviewBase");
    var id = "${id}";
    var stageName = "";
    var currentUserRoles=${currentUserRoles};
    var currentUserMainDepId = "${currentUserMainDepId}";

    $(function () {
        if (id) {
            var url = jsUseCtxPath + "/onlineReview/core/getOnlineReviewBase.do";
            $.post(
                url,
                {id: id},
                function (json) {
                    formOnlineReviewBase.setData(json);
                });
        }
        //明细入口

        if (action == 'task') {
            $("#lookModelDetail").show();
            taskActionProcess();
        } else if (action == "detail") {
            $("#lookModelDetail").show();
            formOnlineReviewBase.setEnabled(false);
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        } else if (action == "edit") {
            $("#addModelDetail").show();
            $("#removeModelDetail").show();
            $("#editModelDetail").show();
            $("#lookModelDetail").show();
        }else if (action == "uploadczq") {
            $("#uploadczq").show();
            formOnlineReviewBase.setEnabled(false);
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        }

    });


    function onRiskStatusRenderer(e) {
        var record = e.record;
        var timeRisk = record.timeRisk;

        var arr = [
            {'key': '满足', 'value': '满足', 'css': 'green'},
            {'key': '不满足', 'value': '不满足', 'css': 'red'}
        ];

        return $.formatItemValue(arr, timeRisk);
    }

    function valfirst() {
        var noticeNo = $.trim(mini.get("noticeNo").getValue());
        if (!noticeNo) {
            return {"result": false, "message": "请填写订单需求单位生产通知单号"};
        }
        var company = $.trim(mini.get("company").getValue());
        if (!company) {
            return {"result": false, "message": "请填写订单需求单位名称"};
        }
        var respName = $.trim(mini.get("respName").getValue());
        if (!respName) {
            return {"result": false, "message": "请填写业务员"};
        }
        var saleArea = $.trim(mini.get("saleArea").getValue());
        if (!saleArea) {
            return {"result": false, "message": "请填写出口区域"};
        }
        var saleCountry = $.trim(mini.get("saleCountry").getValue());
        if (!saleCountry) {
            return {"result": false, "message": "请填写出口国家"};
        }
        var saleNum = $.trim(mini.get("saleNum").getValue());
        if (!saleNum) {
            return {"result": false, "message": "请填写内部订单号"};
        }
        // var contractType = $.trim(mini.get("contractType").getValue());
        // if (!contractType) {
        //     return {"result": false, "message": "请填写合同类型"};
        // }
        // var detail = modelDetailListGrid.getData();
        // if (detail.length < 1) {
        //     return {"result": false, "message": "请添加机型"};
        // }
        return {"result": true};
    }

    function saveOnlineReviewBase() {
        var formValfirstId = valfirst();
        if (!formValfirstId.result) {
            mini.alert(formValfirstId.message);
            return;
        }
        var formData = _GetFormJsonMini("formOnlineReviewBase");
        $.ajax({
            url: jsUseCtxPath + '/onlineReview/core/saveOnlineReviewBase.do?',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (returnObj) {
                if (returnObj) {
                    var message = "";
                    if (returnObj.success) {
                        message = "数据保存成功";
                        mini.alert(message, "提示信息", function () {
                            window.location.href = jsUseCtxPath + "/onlineReview/core/editPage.do?action=edit&id=" + returnObj.data;
                        });
                    } else {
                        message = "数据保存失败，" + returnObj.message;
                        mini.alert(message, "提示信息");
                    }
                }
            }
        });
    }

    function addModelDetail() {
        var belongId = mini.get("id").getValue();
        if (!belongId) {
            mini.alert('请先点击‘保存’进行表单的保存！');
            return;
        }

        var url = jsUseCtxPath + "/onlineReview/core/modelEditPage.do?belongId=" + belongId + "&action="+action+"&stageName=" + stageName + "&status=" + status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (modelDetailListGrid) {
                    modelDetailListGrid.reload()
                }
            }
        }, 1000);
    }

    function editModelDetail() {
        var belongId = mini.get("id").getValue();
        if (!belongId) {
            mini.alert('请先点击‘保存’进行表单的保存！');
            return;
        }
        var row = modelDetailListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
        }
        if(row.timeRisk=='满足'&&stageName != 'oneydbk'){
            mini.alert("满足交货期的机型无法再进行编辑");
            return;
        }
        if(row.jszgId!=currentUserId&&currentUserId!='1'&&
            (stageName == 'onejsfa'||stageName == 'twojsfa'||stageName == 'onecpzgtime'||stageName == 'twocpzgtime')){
            mini.alert("请选择技术主管是自己的进行编辑");
            return;
        }
        if(row.deptId!=currentUserMainDepId&&currentUserId!='1'&&
            (stageName == 'threejsfa'||stageName == 'threecpzgtime')){
            mini.alert("请选择自己部门的机型信息进行编辑");
            return;
        }
        if(row.gyzgId!=currentUserId&&currentUserId!='1'&&
            (stageName == 'onefyfa'||stageName == 'twofyfa')){
            mini.alert("请选择工艺主管是自己的进行编辑");
            return;
        }
        if(row.jhyId!=currentUserId&&currentUserId!='1'&&
            (stageName == 'oneddxf'||stageName == 'twoddxf'||stageName == 'onesxrk'||stageName == 'twosxrk')){
            mini.alert("请选择制造部计划员是自己的进行编辑");
            return;
        }
        if (row) {
            var id = row.id;
            var url = jsUseCtxPath + "/onlineReview/core/modelEditPage.do?id=" + id + "&belongId=" + belongId + "&action="+action+"&stageName=" + stageName + "&status=" + status;
            var winObj = window.open(url);
            var loop = setInterval(function () {
                if (winObj.closed) {
                    clearInterval(loop);
                    if (modelDetailListGrid) {
                        modelDetailListGrid.reload()
                    }
                }
            }, 1000);
        }
    }

    function lookModelDetail() {
        var belongId = mini.get("id").getValue();
        var row = modelDetailListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
        }
        if (row) {
            var id = row.id;
            var url = jsUseCtxPath + "/onlineReview/core/modelEditPage.do?id=" + id + "&belongId=" + belongId + "&action=detail&stageName=" + stageName + "&status=" + status;
            var winObj = window.open(url);
            var loop = setInterval(function () {
                if (winObj.closed) {
                    clearInterval(loop);
                    if (modelDetailListGrid) {
                        modelDetailListGrid.reload()
                    }
                }
            }, 1000);
        }
    }

    function uploadczq() {
        var belongId = mini.get("id").getValue();
        var row = modelDetailListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
        }
        if(row.jszgId!=currentUserId&&currentUserId!='1'&& action == 'uploadczq'){
            mini.alert("请选择技术主管是自己的进行上传");
            return;
        }
        if (row) {
            var id = row.id;
            var url = jsUseCtxPath + "/onlineReview/core/modelEditPage.do?id=" + id + "&belongId=" + belongId + "&action="+action+"&stageName=" + stageName + "&status=" + status;
            var winObj = window.open(url);
            var loop = setInterval(function () {
                if (winObj.closed) {
                    clearInterval(loop);
                    if (modelDetailListGrid) {
                        modelDetailListGrid.reload()
                    }
                }
            }, 1000);
        }
    }

    function removeModelDetail() {
        var id = mini.get("id").getValue();
        if (!id) {
            mini.alert('请先点击‘保存’进行表单的保存！');
            return;
        }
        var row = modelDetailListGrid.getSelected();
        if (row) {
            var id = row.id;
            mini.confirm("确定删除选中记录？", "提示", function (action) {
                if (action != 'ok') {
                    return;
                } else {
                    _SubmitJson({
                        url: jsUseCtxPath + "/onlineReview/core/deleteModel.do",
                        method: 'POST',
                        showMsg: false,
                        data: {id: id},
                        success: function (data) {
                            if (data) {
                                mini.alert(data.message);
                                modelDetailListGrid.reload();
                            }
                        }
                    });
                }
            });
        } else {
            mini.alert("请选中一条记录");
        }
    }

    function getData() {
        var formData = _GetFormJsonMini("formOnlineReviewBase");
        return formData;
    }

    function saveOnlineReview(e) {
        var formValfirstId = valfirst();
        if (!formValfirstId.result) {
            mini.alert(formValfirstId.message);
            return;
        }
        window.parent.saveDraft(e);
    }


    function startOnlineReviewProcess(e) {
        var formValid = valfirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }


    function onlineReviewApprove() {
        //编制阶段的下一步需要校验表单必填字段
        $.ajaxSettings.async = false;
        var id = mini.get('id').getValue();
        var url = jsUseCtxPath + "/onlineReview/core/approveValid.do?id=" + id + "&stageName=" + stageName + '&action=' + action;
        var checkResult = {};
        $.get(
            url,
            function (json) {
                checkResult = json;
            });
        $.ajaxSettings.async = true;
        if (!checkResult.success) {
            mini.alert(checkResult.message);
            return;
        }
        //检查通过
        window.parent.approve();
    }



    function processInfo() {
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

    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();

        //获取环境变量
        if (!nodeVarsStr) {
            nodeVarsStr = "[]";
        }
        formOnlineReviewBase.setEnabled(false);
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'stageName') {
                stageName = nodeVars[i].DEF_VAL_;
            }
        }
        if (stageName == 'start') {
            formOnlineReviewBase.setEnabled(true);
            $("#addModelDetail").show();
            $("#removeModelDetail").show();
            $("#editModelDetail").show();
        } else {
            $("#editModelDetail").show();
        }
    }
</script>
</body>
</html>
