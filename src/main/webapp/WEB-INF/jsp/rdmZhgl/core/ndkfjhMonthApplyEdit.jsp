<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>编辑申请</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/ndkfjhMonthApplyEdit.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;">
        <form id="applyForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="taskId_" name="taskId_" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="mainId" name="mainId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption>
                    年度开发计划月度审批表单
                </caption>
                <tr>
                    <td style="text-align: center;width: 20%">申请人：</td>
                    <td style="min-width:170px">
                        <input id="applyUserId" name="applyUserId" textname="applyUserName"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;"
                               allowinput="false"
                               label="申请人" length="50" mainfield="no" single="true" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">部门：</td>
                    <td style="min-width:170px">
                        <input id="deptId" name="deptId" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:98%;height:34px" allowinput="false" label="部门" textname="deptName"
                               length="500"
                               maxlength="500" minlen="0" single="true" required="false" initlogindep="false"
                               showclose="false"
                               mwidth="160" wunit="px" mheight="34" hunit="px" enabled="false"/>
                    </td>
                <tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        申请年月<span style="color: red">*</span>：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="yearMonth" allowinput="false" class="mini-monthpicker" required="true"
                               style="width:100%;height:34px" name="yearMonth"/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        备注说明：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="remark" name="remark" class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
            </table>
        </form>
        <div id="listGrid" class="mini-datagrid" allowResize="true" style="height:  500px;" sortField="UPDATE_TIME_"
             sortOrder="desc"
             url="${ctxPath}/rdmZhgl/core/ndkfjh/planDetail/listProcess.do?yearMonth=${applyObj.yearMonth}" idField="id"
             showPager="false" allowCellWrap="false"
             multiSelect="true" showColumnsMenu="false" sizeList="[15,20,50,100,200]" pageSize="15"
             allowAlternating="true" autoload="true"
             pagerButtons="#pagerButtons">
            <div property="columns">
                <div type="indexcolumn" align="center" headerAlign="center" width="40px">序号</div>
                <div field="productName" width="100px" headerAlign="center" align="center" renderer="showDetail"
                     allowSort="false">计划名称
                </div>
                <div field="delayDays" width="80px" headerAlign="center" align="center" allowSort="false">延期天数</div>
                <div field="remark" width="150px" headerAlign="center" align="center" allowSort="false">延期原因</div>
                <div field="chargerManName" width="100px" headerAlign="center" align="center" allowSort="false">负责人
                </div>
                <div field="responsorName" width="100px" headerAlign="center" align="center" allowSort="false">责任所长
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    let nodeVarsStr = '${nodeVars}';
    var action = "${action}";
    let jsUseCtxPath = "${ctxPath}";
    let ApplyObj =${applyObj};
    let status = "${status}";
    let applyForm = new mini.Form("#applyForm");
    var yesOrNo = getDics("YESORNO");
    var sourceList = getDics("ndkfjh_source");

    function onPalnSorce(e) {
        var record = e.record;
        var planSource = record.planSource;
        var resultText = '';
        for (var i = 0; i < sourceList.length; i++) {
            if (sourceList[i].key_ == planSource) {
                resultText = sourceList[i].text;
                break
            }
        }
        return resultText;
    }

    function onDelay(e) {
        var record = e.record;
        var isDelay = record.isDelay;
        var resultText = '';
        for (var i = 0; i < yesOrNo.length; i++) {
            if (yesOrNo[i].key_ == isDelay) {
                resultText = yesOrNo[i].text;
                break
            }
        }
        return resultText;
    }

    function showDetail(e) {
        var s = '';
        var record = e.record;
        var detailId = record.detailId;
        var productName = record.productName;
        s = '<span   style="cursor: pointer;color: #0a7ac6" onclick="openDetail(\'' + detailId + '\')">' + productName + '</span>';
        return s;
    }

    function openDetail(detailId) {
		var url = jsUseCtxPath + "/rdmZhgl/core/ndkfjh/planDetail/planViewPage.do?detailId=" + detailId ;
		window.open(url);
    }
</script>
</body>
</html>
