<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>编辑申请</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/wwrz/wwrzPreviewApplyEdit.js?version=${static_res_version}"
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
            <input id="fileId" name="fileId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption>
                    证书报告预览审批单(审批通过后两个月内有效)
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
                    <td style="text-align: center;width: 20%">证书报告名称：</td>
                    <td colspan="1">
                        <input id="fileName" readonly  name="fileName" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%">技术审批所长：</td>
                    <td colspan="1">
                        <input id="techLeaderUserId" name="techLeaderUserId" textname="techLeaderName"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                               label="编制人" length="50" mainfield="no" single="true" enabled="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center">申请原因：</td>
                    <td colspan="3">
						<textarea id="applyReason" name="applyReason" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:99%;height:100px;line-height:25px;" required
                                  label="申请原因" datatype="varchar" length="500" vtype="length:500" minlen="0"
                                  allowinput="true"
                                  mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
            </table>
        </form>
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
</script>
</body>
</html>
