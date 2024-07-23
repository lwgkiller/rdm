<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>图片</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>

</head>
<body>
<div class="mini-toolbar" id="fileButtons">
    <a id="uploadFile" class="mini-button" style="margin-bottom: 5px" onclick="uploadPic">上传附件</a>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="fileListGrid" class="mini-datagrid" style="height: 99%" allowResize="false"
         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
         multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="true" allowCellWrap="true"
         showVGridLines="true">


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
    var fileListGrid = mini.get("fileListGrid");
    var fileType = "${fileType}";
    var canEdit = "${canEdit}";
    var baseInfoId = "${applyId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var url = jsUseCtxPath + "/world/core/fitnessImprove/getFileList.do?baseInfoId=" + baseInfoId + "&fileType=" + fileType;
    $(function () {
        queryFiles();
        debugger;
        if (canEdit == 'false') {
            mini.get('uploadFile').setEnabled(false);
        }
    });

    function queryFiles() {
        // 在url里加type
        fileListGrid.setUrl(url);
        fileListGrid.load();
    }

    function uploadPic() {
        // debugger;
        // mini.alert("baseInfoId:" + baseInfoId + "  fileType" + fileType);

        mini.open({
            title: "附件上传",
            url: jsUseCtxPath + "/world/core/fitnessImprove/openUploadWindow.do?fileType=" + fileType + "&baseInfoId=" + baseInfoId,
            width: 650,
            height: 350,
            showModal: false,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var projectParams = {};
                // projectParams.wtId=wtId;
                // projectParams.faId=faId;
                projectParams.fileType = fileType;
                projectParams.baseInfoId = baseInfoId;
                var data = {projectParams: projectParams};  //传递上传参数
                iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {
                queryFiles();
            }
        });
    }


    function fileInfoRenderer(e) {
        var record = e.record;
        //预览、下载和删除
        var cellHtml = returnPreviewSpan(record.fileName, record.id, record.baseInfoId, coverContent);
        var downloadUrl = '/world/core/fitnessImprove/fileDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.baseInfoId + '\',\'' + downloadUrl + '\')">下载</span>';
        // todo 这里删除要做权限
        if (record&&canEdit=="true") {
            var deleteUrl = "/world/core/fitnessImprove/deleteFiles.do";
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.baseInfoId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }


    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        debugger;
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else {
            var url = '/world/core/fitnessImprove/preview.do?fileType=' + fileType;
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf6(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\',\'' + fileType + '\')">预览</span>';
        }
        return s;
    }

    // function previewPdf(fileName, fileId, formId, coverConent, url, fileType) {
    //     if (!fileName) {
    //         fileName = '';
    //     }
    //     if (!fileId) {
    //         fileId = '';
    //     }
    //     if (!formId) {
    //         formId = '';
    //     }
    //     var previewUrl = jsUseCtxPath + url + "&fileName=" + encodeURIComponent(fileName) + "&fileId=" + fileId + "&formId=" + formId;
    //     if (fileType != 'pic') {
    //         window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverConent) + "&file=" + encodeURIComponent(previewUrl));
    //     } else {
    //         window.open(previewUrl);
    //     }
    // }

    // function deleteFile(record) {
    //     mini.alert(record.id);
    //     mini.confirm("确定删除？", "确定？",
    //         function (action) {
    //             if (action == "ok") {
    //                 // var url = jsUseCtxPath + "/zlgjNPI/core/gyswt/deleteFiles.do";
    //                 var url = jsUseCtxPath + "/world/core/fitnessImprove/deleteFiles.do";
    //                 var data = {
    //                     baseInfoId: record.baseInfoId,
    //                     id: record.id,
    //                     fileName: record.fileName
    //                 };
    //                 $.ajax({
    //                     url: url,
    //                     method: 'post',
    //                     contentType: 'application/json',
    //                     data: mini.encode(data),
    //                     success: function (json) {
    //                         queryFiles();
    //                     }
    //                 });
    //
    //                 // $.post(
    //                 // 		url,
    //                 // 		data,
    //                 // 		function (json) {
    //                 //             queryFiles();
    //                 // 		});
    //             }
    //         }
    //     );
    // }

    //下载文档
    // function downFile(record) {
    //     debugger;
    //     var form = $("<form>");
    //     form.attr("style", "display:none");
    //     form.attr("target", "");
    //     form.attr("method", "post");
    //     // form.attr("action", jsUseCtxPath + "/zlgjNPI/core/gyswt/fileDownload.do?action=download");
    //     form.attr("action", jsUseCtxPath + "/world/core/fitnessImprove/fileDownload.do?action=download");
    //     var inputFileName = $("<input>");
    //     inputFileName.attr("type", "hidden");
    //     inputFileName.attr("name", "fileName");
    //     inputFileName.attr("value", record.fileName);
    //     var mainId = $("<input>");
    //     mainId.attr("type", "hidden");
    //     mainId.attr("name", "mainId");
    //     mainId.attr("value", record.wtId);
    //     var fileId = $("<input>");
    //     fileId.attr("type", "hidden");
    //     fileId.attr("name", "fileId");
    //     fileId.attr("value", record.id);
    //     $("body").append(form);
    //     form.append(inputFileName);
    //     form.append(mainId);
    //     form.append(fileId);
    //     form.submit();
    //     form.remove();
    // }
</script>
<redxun:gridScript gridId="fileListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
