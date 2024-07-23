<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>委外认证附件</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar" id="fileButtons">
    <a id="uploadFile" class="mini-button" style="margin-bottom: 5px" onclick="uploadFile">上传附件</a>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="fileGrid" class="mini-datagrid" style="height: 99%" allowResize="false"
         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
         multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false" allowCellWrap="true"
         showVGridLines="true">
        <div property="columns">
            <div field="id" headerAlign="left" visible="false">id</div>
            <div type="indexcolumn" headerAlign="center">序号</div>
            <div field="fileName" align="left" width="150" headerAlign="center">附件名称</div>
            <div field="fileDesc" align="left" width="80" headerAlign="center">描述</div>
            <div field="userName" align="center" width="50" headerAlign="center">上传人</div>
            <div field="CREATE_TIME_" align="center" width="80" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign="center">
                上传时间
            </div>
            <div renderer="fileInfoRenderer" align="center" width="80" headerAlign="center">操作</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var fileGrid = mini.get("fileGrid");
    var currentUserId = "${currentUser.userId}";
    var detailId = "${detailId}";
    var fileType = "${fileType}";
    var editable = ${editable};
    var url = jsUseCtxPath + "/wwrz/core/file/files.do";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    $(function () {
        queryFiles();
        if (!editable) {
            mini.get('uploadFile').setEnabled(false);
        }
    });

    function queryFiles() {
        fileGrid.setUrl(url + "?detailId=" + detailId + "&fileType=" + fileType);
        fileGrid.load();
    }

    function uploadFile() {
        mini.open({
            title: "附件列表",
            url: jsUseCtxPath + "/wwrz/core/file/fileUploadWindow.do?detailId=" + detailId + "&fileType=" + fileType,
            width: 1000,
            height: 500,
            showModal: false,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                queryFiles();
            }
        });
    }

    function fileInfoRenderer(e) {
        var record = e.record;
        var s = '';
        s += returnPreviewSpan(record.fileName, record.id, record.mainId, coverContent, "wwrzFileUrl");
        s += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downFile(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">下载</span>';
        if(!editable) {
            s+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:silver;">删除</span>';
        } else {
            s+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
        }
        return s;
    }

    function getFileType(fileName) {
        var suffix = "";
        var suffixIndex = fileName.lastIndexOf('.');
        if (suffixIndex != -1) {
            suffix = fileName.substring(suffixIndex + 1).toLowerCase();
        }
        var pdfArray = ['pdf'];
        if (pdfArray.indexOf(suffix) != -1) {
            return 'pdf';
        }
        var officeArray = ['doc', 'docx', 'ppt', 'txt', 'xlsx', 'xls', 'pptx'];
        if (officeArray.indexOf(suffix) != -1) {
            return 'office';
        }
        var picArray = ['jpg', 'jpeg', 'jif', 'bmp', 'png', 'tif', 'gif'];
        if (picArray.indexOf(suffix) != -1) {
            return 'pic';
        }
        return 'other';
    }

    //下载文档
    function downFile(record) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/sys/core/commonInfo/fileDownload.do?action=download");
        var inputFileName = $("<input>");
        inputFileName.attr("type", "hidden");
        inputFileName.attr("name", "fileName");
        inputFileName.attr("value", record.fileName);
        var detailId = $("<input>");
        detailId.attr("type", "hidden");
        detailId.attr("name", "formId");
        detailId.attr("value", record.mainId);
        var fileId = $("<input>");
        fileId.attr("type", "hidden");
        fileId.attr("name", "fileId");
        fileId.attr("value", record.id);
        var fileUrl = $("<input>");
        fileUrl.attr("type", "hidden");
        fileUrl.attr("name", "fileUrl");
        fileUrl.attr("value", "wwrzFileUrl");
        $("body").append(form);
        form.append(inputFileName);
        form.append(detailId);
        form.append(fileId);
        form.append(fileUrl);
        form.submit();
        form.remove();
    }

    function deleteFile(record) {
        mini.confirm("确定删除？", "确定？",
            function (action) {
                if (action == "ok") {
                    var url = jsUseCtxPath + "/wwrz/core/file/delFile.do";
                    var data = {
                        mainId: record.mainId,
                        id: record.id,
                        fileName: record.fileName
                    };
                    $.post(
                        url,
                        data,
                        function (json) {
                            queryFiles();
                        });
                }
            }
        );
    }


</script>
<redxun:gridScript gridId="fileGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
