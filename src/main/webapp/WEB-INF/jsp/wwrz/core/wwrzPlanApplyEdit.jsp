<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>编辑申请</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/wwrz/wwrzPlanApplyEdit.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" style="display: none" class="mini-button" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 80%;">
        <form id="applyForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="taskId_" name="taskId_" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="planIds" name="planIds" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption>
                    认证计划审批单
                </caption>
                <tr>
                    <td style="text-align: center;width: 20%">部门：</td>
                    <td style="min-width:170px" colspan="3">
                        <input id="deptId" name="deptId" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:98%;height:34px" allowinput="true" label="部门" textname="deptName" length="500"
                               maxlength="500" minlen="0" single="true" required="false" initlogindep="false" showclose="false"
                               mwidth="160" wunit="px" mheight="34" hunit="px" enabled="true"/>
                    </td>
                </tr>
            </table>
        </form>
        <div class="mini-fit" style="height:  100%;">
            <div id="planDiv">
                <div style="text-align: center;margin-top: 10px"><span id="applyName" style="font-size: x-large;">测试计划</span></div>
                <div id="planListGrid" class="mini-datagrid" style="height: auto;min-height: 300px" allowResize="true"
                     url="${ctxPath}/wwrz/core/testPlan/planList.do" idField="id" showPager="false" allowCellWrap="true"
                     multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]"  allowAlternating="true"
                     pagerButtons="#pagerButtons">
                    <div property="columns">
                        <div type="indexcolumn" width="45" headerAlign="center" align="center" allowSort="true">序号</div>
                        <div field="deptName" width="120" headerAlign="center" align="center" allowSort="false">部门</div>
                        <div field="productModel" width="80" headerAlign="center" align="center" allowSort="false">型号</div>
                        <div field="chargerName" width="80" headerAlign="center" align="center" allowSort="false">产品主管</div>
                        <div field="certType" width="80" headerAlign="center" align="center" allowSort="true" renderer="onCertType">认证类别</div>
                        <div field="testType" width="80" headerAlign="center" align="center" allowSort="true" renderer="onTestType">全新/补测</div>
                        <div field="yearMonth" width="80" headerAlign="center" align="center" allowSort="true">测试月份</div>
                        <div field="remark" width="120" headerAlign="center" align="center" allowSort="true">备注</div>
                    </div>
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
    var currentUserName = "${currentUserName}";
    var currentUserId = "${currentUserId}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var planListGrid = mini.get("planListGrid");
    var certTypeList = getDics("RZLB");
    var testTypeList = getDics("CSLB");
    function onCertType(e) {
        var record = e.record;
        var certType = record.certType;
        var resultText = '';
        for (var i = 0; i < certTypeList.length; i++) {
            if (certTypeList[i].key_ == certType) {
                resultText = certTypeList[i].text;
                break
            }
        }
        return resultText;
    }
    function onTestType(e) {
        var record = e.record;
        var testType = record.testType;
        var resultText = '';
        for (var i = 0; i < testTypeList.length; i++) {
            if (testTypeList[i].key_ == testType) {
                resultText = testTypeList[i].text;
                break
            }
        }
        return resultText;
    }
</script>
</body>
</html>
