<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>文件上传</title>
    <%@include file="/commons/edit.jsp" %>
    <script src="${ctxPath}/scripts/h5Upload/jquery.Huploadify.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/common/UUID.js?version=${static_res_version}" type="text/javascript"></script>
    <link href="${ctxPath}/scripts/h5Upload/Huploadify.css" rel="stylesheet" type="text/css"/>
    <link href="${ctxPath}/styles/css/multiupload.css" rel="stylesheet" type="text/css"/>
    <style>
        html, body {
            overflow: hidden;
            height: 100%;
            width: 100%;
            padding: 0;
            margin: 0;
        }

    </style>
</head>
<body>
<div id="fileUploadDiv" class="mini-panel" style="width: 100%; height: 100%" showfooter="false" bodystyle="padding:0" borderStyle="border:0"
     showheader="false">
    <input class="file-input" style="display:none;" type="file" name="fileselect[]" multiple accept="" />
    <div class="mini-toolbar">
        <a class="mini-button file-select" iconCls="icon-search" >浏览...</a>
        <a class="mini-button file-upload" iconCls="icon-upload" name="multiupload">开始上传</a>
        <a class="mini-button file-clear" iconCls="icon-remove" name="removeAll">清空列表</a>
    </div>
    <div id="multiuploadGrid" class="mini-datagrid"  allowResize="false" idField="id"
         allowSortColumn="false" allowCellEdit="true" allowCellSelect="true"
         multiSelect="false" showColumnsMenu="false" allowAlternating="true" showPager="false" allowCellWrap="true">
        <div property="columns">
            <div type="indexColumn" width="8.6%"></div>
            <div field="fileName" width="30%" headerAlign="center">文件名
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="fileSize" width="12%" headerAlign="center" align="center">文件大小
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="complete" width="27%" headerAlign="center" align="center" renderer="onProgressRenderer">上传进度
            </div>
            <div field="status" width="10%" headerAlign="center" align="center">上传状态
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="fileDesc" width="12%" headerAlign="center" align="center">说明
                <input property="editor" class="mini-textbox" />
            </div>
            <div field="action" width="12.4%" headerAlign="center" align="center">操作
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var multiuploadGrid = mini.get("#multiuploadGrid");
    var currentUserId="${currentUser.userId}";
    var currentUserName="${currentUser.fullname}";
    var uploadUrl = "${uploadUrl}";
    var outFormData ={};

    function SetData(params) {
        outFormData=params;
    }

    function onProgressRenderer(e) {
        var record = e.record;
        var value = e.value;
        var uid = record._uid;
        var s='<div class="progressbar">'
            + '<div class="progressbar-percent" style="width:' + value + '%;"></div>'
            + '<div class="progressbar-label">' + value + '%</div>'
            + '</div>';
        return s;
    }

    $(function(){
            var up = $('#fileUploadDiv').Huploadify({
                url:uploadUrl,
                formData:outFormData,
                sendFileAttr:["fileDesc"],
                uploadFileList:multiuploadGrid,
                addFileToGrid: function (files) {
                    for(var i=0;i<files.length;i++) {
                        var file=files[i];
                        var row = {id:file.id,fileName:file.name,status:file.status,fileSize:bytesToSize(file.size),complete:0};
                        this.uploadFileList.addRow(row);
                    }
                }
        });

    });

</script>
</body>
</html>
