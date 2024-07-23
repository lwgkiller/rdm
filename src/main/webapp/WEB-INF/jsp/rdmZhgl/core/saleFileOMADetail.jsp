<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>文件明细</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 80%;">
        <form id="applyForm" method="post">
            <input id="applyType" name="applyType" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption>
                    <spring:message code="page.saleFileDetail.name2" />
                </caption>

                <tr>
                    <td align="center" style="white-space: nowrap;">
                        <spring:message code="page.saleFileDetail.name3" />
                    </td>
                    <td align="center"  rowspan="1">
                        <input id="fileType" name="fileType" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="<spring:message code="page.saleFileDetail.name3" />："  onvaluechanged="onChangeFileType"
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="<spring:message code="page.saleFileDetail.name4" />..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=saleFileOMA_WJFL"
                               nullitemtext="<spring:message code="page.saleFileDetail.name4" />..." emptytext="<spring:message code="page.saleFileDetail.name4" />..."/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.saleFileDetail.name5" /></td>
                    <td colspan="1">
                        <input id="language" name="language" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="<spring:message code="page.saleFileDetail.name3" />："  onvaluechanged="onChangeFileType"
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="<spring:message code="page.saleFileDetail.name4" />..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=AMOYZFL"
                               nullitemtext="<spring:message code="page.saleFileDetail.name4" />..." emptytext="<spring:message code="page.saleFileDetail.name4" />..."/>
                    </td>
                </tr>

                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.saleFileDetail.name6" /></td>
                    <td colspan="1">
                        <input id="designModel"  name="designModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.saleFileDetail.name7" /></td>
                    <td colspan="1">
                        <input id="saleModel"  name="saleModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
				<tr>
					<td style="text-align: center;height: 300px"><spring:message code="page.saleFileDetail.name8" />：</td>
					<td colspan="3">
						<div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
							 idField="id"
                             url="${ctxPath}/rdmZhgl/core/saleFileOMA/saleFileOMAList.do?fileModel=sq
                             &designModel=${designModel}&saleModel=${saleModel}&fileType=${fileType}&language=${language}"
                             autoload="true"
							 multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
							<div property="columns">
								<div type="indexcolumn" align="center" width="20"><spring:message code="page.saleFileDetail.name9" /></div>
								<div field="fileName" align="center" headerAlign="center" width="150"><spring:message code="page.saleFileDetail.name10" /></div>
								<div field="fileSize" align="center" headerAlign="center" width="60"><spring:message code="page.saleFileDetail.name11" /></div>
								<div field="fileDesc" align="center" headerAlign="center" width="100"><spring:message code="page.saleFileDetail.name12" /></div>
                                <div field="version" align="center" headerAlign="center" width="50"><spring:message code="page.saleFileDetail.name13" /></div>
                                <div field="applyId" align="center" headerAlign="center" width="170">申请编号</div>
                                <div field="userName" align="center" headerAlign="center" width="80">创建人</div>
                                <div field="status" align="center" headerAlign="center" width="100" renderer="onStatusRenderer"><spring:message code="page.saleFileDetail.name14" /></div>
                                <div field="CREATE_TIME_"  dateFormat="yyyy-MM-dd" headerAlign='center' align="center"><spring:message code="page.saleFileDetail.name15" /></div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRendererSq"><spring:message code="page.saleFileDetail.name16" /></div>
							</div>
						</div>
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
    let designModel ="${designModel}";
    let saleModel ="${saleModel}";
    let fileType ="${fileType}";
    let language ="${language}";
    let status = "${status}";
    let applyForm = new mini.Form("#applyForm");
	var fileListGrid = mini.get("fileListGrid");
    var currentUserName = "${currentUserName}";
    var currentUserId = "${currentUserId}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;

    $(function () {
        mini.get("designModel").setValue(designModel);
        mini.get("saleModel").setValue(saleModel);
        mini.get("saleModel").setValue(saleModel);
        mini.get("fileType").setValue(fileType);
        mini.get("language").setValue(language);
        if (action == 'detail') {
            applyForm.setEnabled(false);
        }
    });
    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;
        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
            {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
            {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
        ];

        return $.formatItemValue(arr, status);
    }

    function operationRendererSq(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnSalePreviewSpan(record.fileName, record.id, record.applyId, coverContent, record.fileSize);
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + saleFileDetail_name + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downFile(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + saleFileDetail_name + '</span>';
        return cellHtml;
    }
    function returnSalePreviewSpan(fileName, fileId, formId, coverContent,fileSize) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title=' + saleFileDetail_name1 + ' style="color: silver" >' + saleFileDetail_name1 + '</span>';
        } else if (fileType == 'pdf') {
            var url = '/rdmZhgl/core/saleFileOMA/pdfPreview.do';
            s = '<span  title=' + saleFileDetail_name1 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + saleFileDetail_name1 + '</span>';
        } else if (fileType == 'office') {
            var url = '/rdmZhgl/core/saleFileOMA/officePreview.do';
            s = '<span  title=' + saleFileDetail_name1 + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url+ '\',\'' + fileSize + '\')">' + saleFileDetail_name1 + '</span>';
        } else if (fileType == 'pic') {
            var url = '/rdmZhgl/core/saleFileOMA/imagePreview.do';
            s = '<span  title=' + saleFileDetail_name1 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + saleFileDetail_name1 + '</span>';
        }
        return s;
    }
    function downFile(record) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/rdmZhgl/core/saleFile/fileDownload.do?action=download");
        var inputFileName = $("<input>");
        inputFileName.attr("type", "hidden");
        inputFileName.attr("name", "fileName");
        inputFileName.attr("value", record.fileName);
        var mainId = $("<input>");
        mainId.attr("type", "hidden");
        mainId.attr("name", "applyId");
        mainId.attr("value", record.applyId);
        var fileId = $("<input>");
        fileId.attr("type", "hidden");
        fileId.attr("name", "fileId");
        fileId.attr("value", record.id);
        $("body").append(form);
        form.append(inputFileName);
        form.append(mainId);
        form.append(fileId);
        form.submit();
        form.remove();
    }
</script>
</body>
</html>
