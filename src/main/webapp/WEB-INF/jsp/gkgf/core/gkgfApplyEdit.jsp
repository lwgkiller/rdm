<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>编辑申请</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/gkgf/gkgfApplyEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" onclick="processInfo()"><spring:message code="page.gkgfApplyEdit.name" /></a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.gkgfApplyEdit.name1" /></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 80%;">
        <form id="applyForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="taskId_" name="taskId_" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="applyType" name="applyType" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption>
                    <spring:message code="page.gkgfApplyEdit.name2" />
                </caption>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.gkgfApplyEdit.name3" />：</td>
                    <td style="min-width:170px">
                        <input id="CREATE_BY_" name="CREATE_BY_" textname="userName"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                               label="<spring:message code="page.gkgfApplyEdit.name3" />" length="50" mainfield="no" single="true" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.gkgfApplyEdit.name4" />：</td>
                    <td style="min-width:170px">
                        <input id="deptId" name="deptId" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:98%;height:34px" allowinput="false" label="<spring:message code="page.gkgfApplyEdit.name4" />" textname="deptName" length="500"
                               maxlength="500" minlen="0" single="true" required="false" initlogindep="false" showclose="false"
                               mwidth="160" wunit="px" mheight="34" hunit="px" enabled="false"/>
                    </td>
                <tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.gkgfApplyEdit.name5" /><span style="color: #ff0000">*</span>：</td>
                    <td colspan="1">
                        <input id="model" required name="model" class="mini-textbox" style="width:96%;"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.gkgfApplyEdit.name6" />：</td>
                    <td colspan="1">
                        <input id="remark"  name="remark" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
            </table>
        </form>
        <div class="mini-toolbar">
            <ul class="toolBtnBox">
                <li style="float: left">
                    <a id="addItem" class="mini-button"  onclick="addItem()"><spring:message code="page.gkgfApplyEdit.name7" /></a>
                    <a id ="removeItem" class="mini-button btn-red" plain="true" onclick="removeItem()"><spring:message code="page.gkgfApplyEdit.name8" /></a>
                    <a id="openImport" class="mini-button " style="margin-left: 10px;" plain="true" onclick="openImportDialog()"><spring:message code="page.gkgfApplyEdit.name9" /></a>
                </li>
                <span class="separator"></span>
                <li>
                    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">（
                        <image src="${ctxPath}/styles/images/warn.png"
                               style="margin-right:5px;vertical-align: middle;height: 15px"/><spring:message code="page.gkgfApplyEdit.name10" />）
                    </p>
                </li>
            </ul>
        </div>
        <div class="mini-fit" style="height: 100%;margin-top: 10px">
            <div id="itemGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
                 url="${ctxPath}/gkgf/core/apply/items.do"
                 idField="id" allowAlternating="true" showPager="false" multiSelect="true" allowCellEdit="true"
                 allowCellSelect="true" allowCellWrap="true"
                 editNextOnEnterKey="true" editNextRowCell="true">
                <div property="columns">
                    <div type="checkcolumn" width="10"></div>
                    <div type="indexcolumn" headerAlign="center" align="center" width="20"><spring:message code="page.gkgfApplyEdit.name11" /></div>
                    <div field="workType" displayfield="workType" width="50" headerAlign="center" align="center"><spring:message code="page.gkgfApplyEdit.name12" /><span
                            style="color: #ff0000">*</span>
                        <input property="editor" class="mini-textbox"/>
                    </div>
                    <div field="tool" displayfield="tool" width="50" headerAlign="center" align="center"><spring:message code="page.gkgfApplyEdit.name13" /><span
                            style="color: #ff0000">*</span>
                        <input property="editor" class="mini-textbox"/>
                    </div>
                    <div field="workItem" displayfield="workItem" width="50" headerAlign="center" align="center"><spring:message code="page.gkgfApplyEdit.name14" /><span
                            style="color: #ff0000">*</span>
                        <input property="editor" class="mini-textbox"/>
                    </div>
                    <div field="carModel" displayfield="carModel" width="50" headerAlign="center" align="center"><spring:message code="page.gkgfApplyEdit.name15" />
                        <input property="editor" class="mini-textbox"/>
                    </div>
                    <div field="selfVideoUrl" displayfield="selfVideoUrl" width="100" headerAlign="center" align="center"><spring:message code="page.gkgfApplyEdit.name16" />
                        <input property="editor" class="mini-textarea"/>
                    </div>
                    <div  renderer="picAttach"  align="center" width="50" headerAlign="center">
                        <spring:message code="page.gkgfApplyEdit.name17" />
                    </div>
                    <div field="bgVideoUrl" displayfield="bgVideoUrl" width="100" headerAlign="center" align="center"><spring:message code="page.gkgfApplyEdit.name18" />
                        <input property="editor" class="mini-textarea"/>
                    </div>
                    <div  renderer="videoAttach"  align="center" width="50" headerAlign="center">
                        <spring:message code="page.gkgfApplyEdit.name19" />
                    </div>
                    <div  renderer="reportAttach"  align="center" width="50" headerAlign="center">
                        <spring:message code="page.gkgfApplyEdit.name20" />
                    </div>
                </div>
            </div>
        </div>
    </div>

</div>
<div id="importWindow" title="<spring:message code="page.gkgfApplyEdit.name21" />" class="mini-window" style="width:750px;height:280px;"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importData" class="mini-button" onclick="importData()"><spring:message code="page.gkgfApplyEdit.name22" /></a>
        <a id="closeImportantWindow" class="mini-button btn-red" onclick="closeWindow()"><spring:message code="page.gkgfApplyEdit.name1" /></a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%" align="center"><spring:message code="page.gkgfApplyEdit.name23" />：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downTemplate()"><spring:message code="page.gkgfApplyEdit.name24" />.xls</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%" align="center"><spring:message code="page.gkgfApplyEdit.name25" />：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="importFileName" name="importFileName"
                               readonly/>
                        <input id="inputImportFile" style="display:none;" type="file" onchange="getImportFile()"
                               accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadImportFile"><spring:message code="page.gkgfApplyEdit.name26" /></a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clearImportFile"><spring:message code="page.gkgfApplyEdit.name27" /></a>
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
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var itemGrid = mini.get("itemGrid");
    var importWindow = mini.get("importWindow");
</script>
</body>
</html>
