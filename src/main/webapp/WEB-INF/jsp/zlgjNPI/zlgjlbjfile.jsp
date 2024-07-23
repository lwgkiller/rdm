<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>测试报告</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-fit" style="height: 100%;">
    <div id="fileListGridReport" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false" idField="id"
         url="${ctxPath}/componentTest/core/kanban/getTestReportFileList.do?mainKanbanId=${businessId}"
         multiSelect="false" showPager="false" showColumnsMenu="false" autoload="true"
         allowAlternating="true"  allowCellEdit="true" allowCellSelect="true">
        <div property="columns">
            <div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
            <div field="fileDesc" align="center" headerAlign="center" width="100">报告编号
            </div>
            <div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
            <div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
            <div field="action" width="80" headerAlign='center' align="center" renderer="operationRendererReport">操作</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var fileListGridReport = mini.get("fileListGridReport");
    var businessId = "${businessId}";
    var currentUserName = "${currentUserName}";
    var currentUserId = "${currentUserId}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";


    function operationRendererReport(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnPreviewSpanReport(record.fileName, record.id, record.mainKanbanId, coverContent);
        //和pdf预览用的同一个url
        var downLoadUrl = '/componentTest/core/kanban/pdfPreviewAndAllDownloadReport.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainKanbanId + '\',\'' + downLoadUrl + '\')">下载</span>';
        return cellHtml;
    }
    //..
    function returnPreviewSpanReport(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/componentTest/core/kanban/pdfPreviewAndAllDownloadReport.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/componentTest/core/kanban/officePreviewReport.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/componentTest/core/kanban/imagePreviewReport.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }


</script>
<redxun:gridScript gridId="productPicGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
