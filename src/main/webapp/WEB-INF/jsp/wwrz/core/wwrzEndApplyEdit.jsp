<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>编辑申请</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/wwrz/wwrzEndApplyEdit.js?version=${static_res_version}"
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
            <input id="mainId" name="mainId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption>
                    项目废止审批单
                </caption>
                <tr>
                    <td style="text-align: center;width: 20%">申请人：</td>
                    <td style="min-width:170px">
                        <input id="CREATE_BY_" name="CREATE_BY_" textname="userName"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                               label="编制人" length="50" mainfield="no" single="true" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">申请部门：</td>
                    <td style="min-width:170px">
                        <input id="deptId" name="deptId" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:98%;height:34px" allowinput="false" label="部门" textname="deptName" length="500"
                               maxlength="500" minlen="0" single="true" required="false" initlogindep="false" showclose="false"
                               mwidth="160" wunit="px" mheight="34" hunit="px" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">销售型号：</td>
                    <td colspan="1">
                        <input id="productModel" readonly  name="productModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%">认证项目：</td>
                    <td colspan="1">
                        <input id="itemNames"  readonly name="itemNames" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">项目状态：</td>
                    <td colspan="1">
                        <input id="projectStatus" required  name="projectStatus" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%">预计费用：</td>
                    <td colspan="1">
                        <input id="money"   name="money" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center">废止原因：</td>
                    <td colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:99%;height:100px;line-height:25px;" required
                                  label="废止原因" datatype="varchar" length="500" vtype="length:500" minlen="0"
                                  allowinput="true"
                                  mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
            </table>
        </form>
        <div class="mini-fit" style="height:  100%;">
            <div id="planDiv">
                <div style="text-align: center;margin-top: 10px"><span style="font-size: x-large;">认证申请</span></div>
                <div id="applyListGrid" class="mini-datagrid" style="height: auto;min-height: 300px" allowResize="true"
                     url="${ctxPath}/wwrz/core/test/listApplyData.do" idField="id" showPager="false" allowCellWrap="true"
                     multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]"  allowAlternating="true"
                     pagerButtons="#pagerButtons">
                    <div property="columns">
                        <div type="indexcolumn" width="45" headerAlign="center" align="center" allowSort="true">序号</div>
                        <div field="id" name="id" name="action" cellCls="actionIcons" width="80px" headerAlign="center"
                             align="center" renderer="onActionRenderer" cellStyle="padding:0;">
                            操作
                        </div>
                        <div field="productModel" width="80" headerAlign="center" align="center" allowSort="false">型号</div>
                        <div field="productType" width="80" headerAlign="center" align="center" allowSort="true" renderer="onProductType">产品类型</div>
                        <div field="cabForm" width="80" headerAlign="center" align="center" allowSort="true" renderer="onCabForm">司机室形式</div>
                        <div field="chargerName" width="80" headerAlign="center" align="center" allowSort="false">产品主管</div>
                        <div field="itemNames" width="80" headerAlign="center" align="center" allowSort="true">认证项目</div>
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
    var applyListGrid = mini.get("applyListGrid");
    var productTypeList = getDics("CPLX");
    var cabFormList = getDics("SJSXS");
</script>
</body>
</html>
