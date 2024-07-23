<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>公共文档管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <ul class="toolBtnBox">
        <li style="float: left">
            <a id="uploadBtn" class="mini-button" iconCls="icon-upload" onclick="openDocMgrUploadWindow()" plain="true">上传文件</a>
            <p style="display: inline-block;color: #888;font-size:15px;vertical-align: middle">（
                <image src="${ctxPath}/styles/images/warn.png" style="margin-right:5px;vertical-align: middle;height: 15px"/>
                同名文件会被覆盖更新）
            </p>
        </li>
        <span class="separator"></span>
        <li>
            <span class="text" style="width:auto">文档名称: </span><input class="mini-textbox" id="docName"/>
            <a class="mini-button" iconCls="icon-search" onclick="searchDoc()" plain="true">查询</a>
        </li>
    </ul>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         idField="id" allowAlternating="true" showPager="false" multiSelect="false">
        <div property="columns">
            <div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>

            <div field="fileName" headerAlign='center' align='center' width="160">文件名称</div>

            <div field="fileDesc" align="center" headerAlign="center" width="100">说明</div>

            <div cellCls="actionIcons" width="60" headerAlign="center" align="center" renderer="actionOperate" cellStyle="padding:0;">操作</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var url = "${ctxPath}/componentTest/core/docmgr/dataListQuery.do?docName=";
    var isComponentTestAdmin = "${isComponentTestAdmin}";
    var currentUserName = "${currentUser.fullname}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var fileListGrid = mini.get("fileListGrid");
    //..
    $(function () {
        fileListGrid.setUrl(url + mini.get("#docName").getValue());
        fileListGrid.load();
    });
    //操作栏
    fileListGrid.on("update", function (e) {
        handGridButtons(e.sender.el);
    });

    function actionOperate(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnPreviewSpan(record.fileName, record.id, record.mainKanbanId, coverContent);
        //和pdf预览用的同一个url
        var downLoadUrl = '/componentTest/core/docmgr/pdfPreviewAndAllDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainKanbanId + '\',\'' + downLoadUrl + '\')">下载</span>';
        //增加删除按钮
        if (isComponentTestAdmin == 'true' || currentUserName == '管理员') {
            var deleteUrl = "/componentTest/core/docmgr/delFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainKanbanId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }

    //..
    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/componentTest/core/docmgr/pdfPreviewAndAllDownload.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/componentTest/core/docmgr/officePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/componentTest/core/docmgr/imagePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }

    //..
    function openDocMgrUploadWindow() {
        if (isComponentTestAdmin == 'false' && currentUserName != '管理员') {
            mini.alert('没有权限操作');
            return;
        }
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/componentTest/core/docmgr/uploadWindow.do",
            width: 750,
            height: 450,
            showModal: false,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                searchDoc();
            }
        });
    }

    //..
    function searchDoc() {
        fileListGrid.setUrl(url + mini.get("#docName").getValue());
        fileListGrid.load();
    }
</script>

</body>
</html>