<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>挖掘机械研究院月度预算填写</title>
    <%@include file="/commons/edit.jsp" %>
    <script src="${ctxPath}/scripts/xcmgbudget/budgetMonthEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/xcmgbudget/budgetMonthUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <link href="${ctxPath}/scripts/mini/miniui/themes/icons.css" rel="stylesheet" type="text/css"/>
    <style type="text/css">
        .table-detail > tbody > tr > td {
            border: 1px solid #ccc;
            font-size: 15px;
        }
    </style>
</head>
<body>
<div id="detailToolBar" class="topToolBar">
    <div>
        <a id="saveBudgetMonth" class="mini-button" onclick="saveBudgetMonth()">保存</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto;">
        <form id="formProject" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="budgetId" name="budgetId" class="mini-hidden"/>
            <input id="budgetType" name="budgetType" class="mini-hidden"/>
            <input id="bigTypeId" name="bigTypeId" class="mini-hidden"/>
            <input id="bigTypeName" name="bigTypeName" class="mini-hidden"/>
            <input id="subjectMonthId" name="subjectMonthId" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 10%;text-align: center">申报部门：</td>
                    <td style="width: 25%;">
                        <input id="deptId" name="deptId" class="mini-hidden"/>
                        <input id="deptName" name="deptName" class="mini-textbox" style="width:98%;" readonly/>
                    </td>
                    <td style="width: 10%;text-align: center">申报月份：</td>
                    <td style="width: 25%">
                        <input id="yearMonth" name="yearMonth" class="mini-textbox" style="width:98%;" readonly/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%;text-align: center">总账科目：</td>
                    <td style="width: 25%">
                        <input id="subjectId" name="subjectId" class="mini-hidden"/>
                        <input id="subjectCode" name="subjectCode" class="mini-textbox" style="width:98%;" readonly/>
                    </td>
                    <td style="width: 10%;text-align: center">科目描述：</td>
                    <td style="width: 25%">
                        <input id="subjectName" name="subjectName" class="mini-textbox" style="width:98%;" readonly/>
                    </td>
                </tr>
                <tr id="projectTr">
                    <td style="width: 10%;text-align: center">关联财务订单号：</td>
                    <td style="width: 25%" colspan="1">
                        <input id="glcwddh" name="glcwddh" class="mini-combobox" data=""
                               textField="text" valueField="id" emptyText="请选择..." required="true"
                               onclick="valuechanged" style="width:99%"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%;text-align: center">资金预算(没有则写0)：
<%--                        <image src="${ctxPath}/styles/images/warn.png" style="margin-right:5px;--%>
<%--                        vertical-align: middle;height: 15px" title="不含归口费用，仅需填写申报数值"/>--%>
                    </td>
                    <td style="width: 25%">
                        <input id="zjMonthExpect" name="zjMonthExpect" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 10%;text-align: center">费用预算(没有则写0)：
<%--                        <image src="${ctxPath}/styles/images/warn.png" style="margin-right:5px;--%>
<%--                        vertical-align: middle;height: 15px" title="不含归口费用，仅需填写申报数值"/></td>--%>
                    <td style="width: 25%">
                        <input id="fyMonthExpect" name="fyMonthExpect" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%;text-align: center">资金说明：</td>
                    <td style="width: 25%;height: 100px">
                        <input id="zjDetail" name="zjDetail" class="mini-textarea" style="width:98%;height: 98%"/>
                    </td>
                    <td style="width: 10%;text-align: center">费用说明：</td>
                    <td style="width: 25%;height: 100px">
                        <input id="fyDetail" name="fyDetail" class="mini-textarea" style="width:98%;height: 98%"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var formProject = new mini.Form("#formProject");
    var operate = "";


    function setData(applyObj){
        if(applyObj && applyObj.recordKey) {
            operate = applyObj.recordKey.operate;
            cwddh = applyObj.recordKey.cwddh;
            gbcwddh = applyObj.recordKey.gbcwddh;
            if (operate=="copy"){
                applyObj.recordKey.zjMonthExpect = 0;
                applyObj.recordKey.fyMonthExpect = 0;
                applyObj.recordKey.zjDetail = '';
                applyObj.recordKey.fyDetail = '';
            }
            formProject.setData(applyObj.recordKey);
            var flag = applyObj.recordKey.budgetType;
            if (flag == 'xml'){
                $("#projectTr").attr("style","display:''")
            }
            if (flag == 'fxml'){
                $("#projectTr").attr("style","display:none;")
            }
        }
    }
</script>
</body>
</html>
