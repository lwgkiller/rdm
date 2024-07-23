<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>质量改进问题附件上传</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" id="fileButtons">
    <a id="uploadFile" class="mini-button" style="margin-bottom: 5px" onclick="uploadZlgjPic">上传附件</a>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="productPicGrid" class="mini-datagrid" style="height: 99%" allowResize="false"
         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
         multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false" allowCellWrap="true" showVGridLines="true">
        <div property="columns">
            <div field="id" headerAlign="left" visible="false">id</div>
            <div type="indexcolumn" headerAlign="center">序号</div>
            <div field="fileName" align="center" headerAlign="center">附件名</div>
            <div field="fileSize" align="center" headerAlign="center">附件大小</div>
            <div field="creator" align="center" headerAlign="center">上传人</div>
            <div field="CREATE_TIME_" align="center" dateFormat="yyyy-MM-dd" headerAlign="center">上传时间</div>
            <div renderer="fileInfoRenderer" align="center" headerAlign="center">操作</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var productPicGrid = mini.get("productPicGrid");
    var mainId = "${meetingId}";
    var action = "${action}";
    var coverContent = "${coverContent}";
    var canOperateFile = "${canOperateFile}";
    var fjlx = "${fjlx}";
    var faId = "${faId}";
    var url = jsUseCtxPath + "/xjsdr/core/zlgj/files.do?mainId=" + mainId + "&fjlx=" + fjlx + "&faId=" + faId;
    var currentUserId = "${currentUserId}";
    $(function () {
        queryProjectFiles();
        if (canOperateFile == 'false') {
            mini.get('uploadFile').setEnabled(false);
        }
    });

    function queryProjectFiles() {
        productPicGrid.setUrl(url);
        productPicGrid.load();
    }

    function uploadZlgjPic() {
        mini.open({
            title: "附件上传",
            url: jsUseCtxPath + "/xjsdr/core/zlgj/openUploadWindow.do",
            width: 650,
            height: 350,
            showModal: false,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var projectParams = {};
                projectParams.mainId = mainId;
                projectParams.fjlx = fjlx;
                projectParams.faId = faId;
                var data = {projectParams: projectParams};  //传递上传参数
                iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {
                queryProjectFiles();
            }
        });
    }

    function fileInfoRenderer(e) {
        var record = e.record;
        var mainId = record.wtId;
        var s = returnZlgjPreviewSpan(record.fileName, record.id, mainId, coverContent);
        s += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downFile(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">下载</span>';
        if (canOperateFile == 'false') {
            s += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:silver;">删除</span>';
        } else {
            if (record.CREATE_BY_==currentUserId||action=='change') {
                s += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                    'onclick="deleteFile(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
            }
        }
        return s;
    }

    function returnZlgjPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/xjsdr/core/zlgj/zlgjPdfPreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/xjsdr/core/zlgj/zlgjOfficePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/xjsdr/core/zlgj/zlgjImagePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }

    function deleteFile(record) {
        mini.confirm("确定删除？", "确定？",
            function (action) {
                if (action == "ok") {
                    var url = jsUseCtxPath + "/xjsdr/core/zlgj/deleteFiles.do";
                    var data = {
                        mainId: record.wtId,
                        id: record.id,
                        fileName: record.fileName
                    };
                    $.post(
                        url,
                        data,
                        function (json) {
                            queryProjectFiles();
                        });
                }
            }
        );
    }
    function deleteFilez(record) {
        mini.confirm("确定删除？", "确定？",
            function (action) {
                if (action == "ok") {
                    var url = jsUseCtxPath + "/xjsdr/core/zlgj/deleteFiles.do";
                    var data = {
                        mainId: record.faId,
                        id: record.id,
                        fileName: record.fileName
                    };
                    $.post(
                        url,
                        data,
                        function (json) {
                            queryProjectFiles();
                        });
                }
            }
        );
    }
    //下载文档
    function downFile(record) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/xjsdr/core/zlgj/fileDownload.do?action=download");
        var inputFileName = $("<input>");
        inputFileName.attr("type", "hidden");
        inputFileName.attr("name", "fileName");
        inputFileName.attr("value", record.fileName);
        var mainId = $("<input>");
        mainId.attr("type", "hidden");
        mainId.attr("name", "mainId");
        mainId.attr("value", record.wtId);
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
<redxun:gridScript gridId="productPicGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
