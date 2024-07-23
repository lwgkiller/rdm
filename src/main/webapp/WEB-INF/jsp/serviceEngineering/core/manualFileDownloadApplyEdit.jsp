<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title>操保手册下载申请表单</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/serviceEngineering/manualFileDownloadApplyEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()"><spring:message
                code="page.manualFileDownloadApplyEdit.name"/></a>
        <a id="closeWindow1" class="mini-button" onclick="CloseWindow()"><spring:message code="page.manualFileDownloadApplyEdit.name1"/></a>
    </div>
</div>

<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="manualFileDownloadForm" method="post" style="height: 95%;width: 98%">
            <input class="mini-hidden" id="id" name="id"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.manualFileDownloadApplyEdit.name2"/>：</td>
                    <td style="min-width:170px">
                        <input id="applyNumber" name="applyNumber" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.manualFileDownloadApplyEdit.name3"/>：</td>
                    <td style="min-width:170px">
                        <input id="CREATE_BY_" name="CREATE_BY_" class="mini-textbox" style="display: none"/>
                        <input id="creatorName" name="creatorName" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.manualFileDownloadApplyEdit.name4"/>：</td>
                    <td style="min-width:170px">
                        <input name="creatorDeptId" class="mini-textbox" style="display: none"/>
                        <input id="creatorDeptName" name="creatorDeptName" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.manualFileDownloadApplyEdit.name5"/>：<spring:message
                            code="page.manualFileDownloadApplyEdit.name6"/></td>
                    <td colspan="3">
						<textarea id="useDesc" name="useDesc" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:100px;line-height:25px;"
                                  label="<spring:message code="page.manualFileDownloadApplyEdit.name7" />" datatype="varchar" allowinput="true"
                                  emptytext="<spring:message code="page.manualFileDownloadApplyEdit.name8" />..." mwidth="80" wunit="%"
                                  mheight="20000" hunit="px"></textarea>
                    </td>
                </tr>
            </table>

            <p style="font-size: 16px;font-weight: bold;margin-top: 20px"><spring:message code="page.manualFileDownloadApplyEdit.name9"/><span
                    style="font-size: 14px;color:red"><spring:message code="page.manualFileDownloadApplyEdit.name10"/></span></p>
            <div class="mini-toolbar" style="margin-bottom: 5px" id="demandListToolBar">
                <a class="mini-button" id="selfAddBtn" plain="true" onclick="addManualFile"><spring:message
                        code="page.manualFileDownloadApplyEdit.name11"/></a>
                <a class="mini-button btn-red" id="delDemand" plain="true" onclick="delManualFile"><spring:message
                        code="page.manualFileDownloadApplyEdit.name12"/></a>
            </div>
            <div id="demandGrid" class="mini-datagrid" allowResize="false" style="height:340px" autoload="true"
                 idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false"
                 multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
                 url="${ctxPath}/serviceEngineering/core/manualFileDownloadApply/demandList.do?applyId=${applyId}"
            >
                <div property="columns">
                    <div type="checkcolumn" width="50"></div>
                    <div type="indexcolumn" headerAlign="center" width="50"><spring:message code="page.manualFileDownloadApplyEdit.name13"/></div>
                    <div field="salesModel" width="120" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                            code="page.manualFileDownloadApplyEdit.name14"/></div>
                    <div field="designModel" width="120" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                            code="page.manualFileDownloadApplyEdit.name15"/></div>
                    <div field="materialCode" width="120" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                            code="page.manualFileDownloadApplyEdit.name16"/></div>
                    <div field="manualDescription" width="500" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                            code="page.manualFileDownloadApplyEdit.name17"/></div>
                    <div field="cpzgName" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                            code="page.manualFileDownloadApplyEdit.name18"/></div>
                    <div field="manualLanguage" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                            code="page.manualFileDownloadApplyEdit.name19"/></div>
                    <div field="manualCode" width="140" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                            code="page.manualFileDownloadApplyEdit.name20"/></div>
                    <div field="manualVersion" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                            code="page.manualFileDownloadApplyEdit.name21"/></div>
                    <div field="isCE" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                            code="page.manualFileDownloadApplyEdit.name22"/></div>
                    <div field="keyUser" displayField="keyUser" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                        <spring:message code="page.manualFileDownloadApplyEdit.name23"/></div>
                    <div field="publishTime" width="110" headerAlign="center" align="center" dateFormat="yyyy-MM-dd" renderer="render"
                         allowSort="true"><spring:message code="page.manualFileDownloadApplyEdit.name24"/></div>
                    <div field="manualStatus" width="100" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                            code="page.manualFileDownloadApplyEdit.name25"/></div>
                    <div cellCls="actionIcons" width="110" headerAlign="center" align="center" renderer="manualFileRenderer" cellStyle="padding:0;">
                        <spring:message code="page.manualFileDownloadApplyEdit.name26"/></div>
                </div>
            </div>

            <p style="font-size: 16px;font-weight: bold;margin-top: 10px"><spring:message code="page.manualFileDownloadApplyEdit.name27"/></p>
            <div class="mini-toolbar" style="margin-bottom: 5px" id="fileListToolBar">
                <a id="cxFileUploadBtn" class="mini-button" style="margin-bottom: 5px" onclick="uploadFile()"><spring:message
                        code="page.manualFileDownloadApplyEdit.name28"/></a>
            </div>
            <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height:200px"
                 allowResize="false" allowCellWrap="true" idField="id"
                 url="${ctxPath}/serviceEngineering/core/manualFileDownloadApply/fileList.do?applyId=${applyId}"
                 autoload="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                <div property="columns">
                    <div field="fileName" name="name" width="160" headerAlign='center' align='center'><spring:message
                            code="page.manualFileDownloadApplyEdit.name17"/></div>
                    <div field="fileSize" headerAlign='center' align='center' width="60"><spring:message
                            code="page.manualFileDownloadApplyEdit.name29"/></div>
                    <div field="fileDesc" width="80" headerAlign="center" align="center"><spring:message
                            code="page.manualFileDownloadApplyEdit.name7"/></div>
                    <div field="creator" width="80" headerAlign='center' align="center"><spring:message
                            code="page.manualFileDownloadApplyEdit.name30"/></div>
                    <div field="CREATE_TIME_" width="100" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign='center' align="center"><spring:message
                            code="page.manualFileDownloadApplyEdit.name31"/></div>
                    <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer"><spring:message
                            code="page.manualFileDownloadApplyEdit.name26"/></div>
                </div>
            </div>
        </form>
    </div>
</div>

<div id="selectManualFileWindow" title="<spring:message code="page.manualFileDownloadApplyEdit.name32" />" class="mini-window"
     style="width:1450px;height:700px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px" borderStyle="border-left:0;border-top:0;border-right:0;">
        <li style="margin-right: 15px">
            <span class="text" style="width:auto"><spring:message code="page.manualFileDownloadApplyEdit.name14"/>：</span>
            <input class="mini-textbox" id="salesModel" name="salesModel" style="width: 120px"/>
            <span class="text" style="width:auto"><spring:message code="page.manualFileDownloadApplyEdit.name15"/>：</span>
            <input class="mini-textbox" id="designModel" name="designModel" style="width: 120px"/>
            <span class="text" style="width:auto"><spring:message code="page.manualFileDownloadApplyEdit.name33"/>：</span>
            <input class="mini-textbox" id="materialCode" name="materialCode" style="width: 120px"/>
            <span class="text" style="width:auto"><spring:message code="page.manualFileDownloadApplyEdit.name17"/>：</span>
            <input class="mini-textbox" id="manualDescription" name="manualDescription" style="width: 120px"/>
            <span class="text" style="width:auto"><spring:message code="page.manualFileDownloadApplyEdit.name19"/>：</span>
            <input class="mini-textbox" id="manualLanguage" name="manualLanguage" style="width: 120px"/>
            <span class="text" style="width:auto"><spring:message code="page.manualFileDownloadApplyEdit.name20"/>：</span>
            <input class="mini-textbox" id="manualCode" name="manualCode" style="width: 120px"/>
            <span class="text" style="width:auto"><spring:message code="page.manualFileDownloadApplyEdit.name34"/>：</span>
            <input class="mini-textbox" id="isCE" name="isCE" style="width: 120px"/>
        </li>

        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchManualFile()"><spring:message
                code="page.manualFileDownloadApplyEdit.name35"/></a>
        <span style="color: red;font-size: 15px"><spring:message code="page.manualFileDownloadApplyEdit.name36"/></span>
    </div>
    <div class="mini-fit">
        <div id="manualFileListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" multiSelect="true"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/serviceEngineering/core/maintenanceManualfile/dataListQuery.do">
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="salesModel" width="120" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.manualFileDownloadApplyEdit.name14"/></div>
                <div field="designModel" width="120" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.manualFileDownloadApplyEdit.name15"/></div>
                <div field="materialCode" width="120" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.manualFileDownloadApplyEdit.name16"/></div>
                <div field="manualDescription" width="400" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.manualFileDownloadApplyEdit.name17"/></div>
                <div field="cpzgName" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.manualFileDownloadApplyEdit.name18"/></div>
                <div field="manualLanguage" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.manualFileDownloadApplyEdit.name19"/></div>
                <div field="manualCode" width="140" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.manualFileDownloadApplyEdit.name20"/></div>
                <div field="manualVersion" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.manualFileDownloadApplyEdit.name21"/></div>
                <div field="isCE" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.manualFileDownloadApplyEdit.name22"/></div>
                <div field="publishTime" width="110" headerAlign="center" align="center" dateFormat="yyyy-MM-dd" renderer="render" allowSort="true">
                    <spring:message code="page.manualFileDownloadApplyEdit.name24"/></div>
                <div field="remark" width="300" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.maintenanceManualfileList.name33"/></div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.manualFileDownloadApplyEdit.name37" />"
                           onclick="selectFileOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.manualFileDownloadApplyEdit.name38" />"
                           onclick="selectFileHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var nodeVarsStr = '${nodeVars}';
    var jsUseCtxPath = "${ctxPath}";
    var manualFileDownloadForm = new mini.Form("#manualFileDownloadForm");
    var fileListGrid = mini.get("fileListGrid");
    var demandGrid = mini.get("demandGrid");
    var manualFileListGrid = mini.get("manualFileListGrid");

    var action = "${action}";
    var status = "${status}";
    var applyId = "${applyId}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;

    var selectManualFileWindow = mini.get("selectManualFileWindow");

    function operationRenderer(e) {
        var record = e.record;
        //预览、下载和删除
        var cellHtml = returnPreviewSpan(record.fileName, record.id, record.applyId, coverContent);
        var downloadUrl = '/serviceEngineering/core/manualFileDownloadApply/fileDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + manualFileDownloadApplyEdit_name + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.applyId + '\',\'' + downloadUrl + '\')">' + manualFileDownloadApplyEdit_name + '</span>';
        if (action == 'edit' || (action == 'task' && stageName == 'start')) {
            var deleteUrl = "/serviceEngineering/core/manualFileDownloadApply/deleteFiles.do";
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + manualFileDownloadApplyEdit_name1 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.applyId + '\',\'' + deleteUrl + '\')">' + manualFileDownloadApplyEdit_name1 + '</span>';
        }
        return cellHtml;
    }

    function manualFileRenderer(e) {
        var record = e.record;
        //需都可以预览，只有申请人在流程结束后，通过detail查看才能下载
        var cellHtml = '<span  title=' + manualFileDownloadApplyEdit_name2 + ' style="color:#409EFF;cursor: pointer;" onclick="previewManualFile(\'' + record.manualFileId + '\',\'' + coverContent + '\')">' + manualFileDownloadApplyEdit_name2 + '</span>';
        var creatorId = mini.get("CREATE_BY_").getValue();
        if (action == 'detail' && status == 'SUCCESS_END' && currentUserId == creatorId) {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + manualFileDownloadApplyEdit_name + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downloadManualFile(\'' + record.manualFileId + '\',\'' + record.manualDescription + '\')">' + manualFileDownloadApplyEdit_name + '</span>';
        }

        return cellHtml;
    }

    function previewManualFile(id, coverContent) {
        var previewUrl = jsUseCtxPath + "/serviceEngineering/core/maintenanceManualfile/Preview.do?id=" + id;
        window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverContent) + "&file=" + encodeURIComponent(previewUrl));
    }

    function downloadManualFile(id, description) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/maintenanceManualfile/Download.do");
        var idAttr = $("<input>");
        idAttr.attr("type", "hidden");
        idAttr.attr("name", "id");
        idAttr.attr("value", id);
        var descriptionAttr = $("<input>");
        descriptionAttr.attr("type", "hidden");
        descriptionAttr.attr("name", "description");
        descriptionAttr.attr("value", description);
        $("body").append(form);
        form.append(idAttr);
        form.append(descriptionAttr);
        form.submit();
        form.remove();
    }

    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title=' + manualFileDownloadApplyEdit_name2 + ' style="color: silver" >' + manualFileDownloadApplyEdit_name2 + '</span>';
        } else {
            var url = '/serviceEngineering/core/manualFileDownloadApply/preview.do?fileType=' + fileType;
            s = '<span  title=' + manualFileDownloadApplyEdit_name2 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf6(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\',\'' + fileType + '\')">' + manualFileDownloadApplyEdit_name2 + '</span>';
        }
        return s;
    }

    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
</script>
</body>
</html>
