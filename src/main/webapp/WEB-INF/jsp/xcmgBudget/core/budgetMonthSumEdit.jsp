<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>挖掘机械研究院月度预算填写</title>
    <%@include file="/commons/edit.jsp" %>
    <script src="${ctxPath}/scripts/xcmgbudget/budgetMonthSumEdit.js?version=${static_res_version}" type="text/javascript"></script>
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
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto;">
        <form id="formProject" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="yearMonth" name="yearMonth" class="mini-hidden"/>
            <input id="subjectMonthId" name="subjectMonthId" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
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
            </table>
        </form>

        <div class="mini-fit" style="height: 100%;">
            <div id="budgetSubjectListSumGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" allowAlternating="false" idField="id"
                 url="${ctxPath}/xcmgBudget/core/budgetMonth/queryBudgetSubjectList.do"
                 allowCellWrap="true" autoload="false" showPager="false" multiSelect="false" showColumnsMenu="true" allowCellEdit="false" allowCellSelect="false">
                <div property="columns">
                    <div field="index" headerAlign="center" align="center" width="100" renderer="onActionRenderer">序号</div>
                    <div field="glcwddh"  width="120px" align="center" headerAlign="center" allowSort="false">关联财务订单号</div>
                    <div field="zjMonthExpect"  width="120px" align="center" headerAlign="center" allowSort="false">资金预算</div>
                    <div field="zjDetail"  width="320px" align="center" headerAlign="center" allowSort="false" >资金说明</div>
                    <div field="fyMonthExpect"  width="120px" align="center" headerAlign="center" allowSort="false" >费用预算</div>
                    <div field="fyDetail"  width="320px" align="center" headerAlign="center" allowSort="false" >费用说明</div>
                    <div field="FULLNAME_"  width="100px" align="center" headerAlign="center" allowSort="false" >申报人</div>
                    <div field="changeNum"  width="100px" align="center" headerAlign="center" allowSort="false" renderer="onChangeNumRenderer">流程页面</div>

                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var formProject = new mini.Form("#formProject");
    var budgetSubjectListSumGrid = mini.get("budgetSubjectListSumGrid");

    function setData(applyObj){
        if(applyObj && applyObj.recordKey) {
            formProject.setData(applyObj.recordKey);
        }
        searchBudgetMonth();
    }

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        if(record.type=='totalSum') {
            return "<span style='font-weight: bold;color: black;'>合计</span>";
        }else {
            return record._id;
        }
    }

    //金额修改跳转
    function onChangeNumRenderer(e){
        var record = e.record;
        var budgetId = record.budgetId;
        var status = 'SUCCESS_END';
        if (budgetId !=null && budgetId !='' && record.type!="totalSum"){
            // updateBudgetMonthRow(record.budgetId,'SUCCESS_END');
            return  '<span title="修改" href="#" onclick="updateBudgetMonthRow(\'' + budgetId +'\',\''+status+ '\')"><a href="#" style="color:#44cef6;text-decoration:underline;">修改</a></span>';
        }

    }

    budgetSubjectListSumGrid.on("drawcell",function(e){
        var record=e.record;
        if(record.type&&record.type=='totalSum') {
            e.cellStyle="font-weight: bold;";
        }
    });

</script>
</body>
</html>
