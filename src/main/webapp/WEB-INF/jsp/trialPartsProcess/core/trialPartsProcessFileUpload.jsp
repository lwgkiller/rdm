<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>试制零部件进度文件上传</title>
    <%@include file="/commons/edit.jsp" %>
    <script src="${ctxPath}/scripts/multiFileUpload/h5uploadFile.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js" type="text/javascript"></script>
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
<div id="fileUploadDiv" class="mini-panel" style="width: 100%; height: 100%" showfooter="false" bodystyle="padding:0"
     borderStyle="border:0"
     showheader="false">
    <input class="file-input" style="display:none;" type="file" name="fileselect[]" multiple accept="image/*,.doc,.docx"/>
    <div class="mini-toolbar">
        <a class="mini-button file-select" iconCls="icon-search">浏览...</a>
        <a class="mini-button file-upload" iconCls="icon-upload" name="multiupload">开始上传</a>
        <a class="mini-button file-clear" iconCls="icon-remove" name="removeAll">清空列表</a>
    </div>
    <div id="multiuploadGrid" class="mini-datagrid" allowSortColumn="false" multiSelect="false" idField="id"
         allowCellEdit="true" allowCellSelect="true" showPager="false" showToolbar="false" allowCellWrap="false">
        <div property="columns">
            <div type="indexColumn"></div>
            <div field="fileName" width="110" headerAlign="center">文件名
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="fileType" width="110" headerAlign="center">文件类型<span style="color: red">*</span>
                <input property="editor" class="mini-combobox" data="ALLFileType"
                       textField="value" valueField="key"
                       required="true" allowInput="false"
                       multiSelect="false"
                >
            </div>
            <div field="fileSize" width="50" headerAlign="center" align="center">文件大小
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div name="fileDesc" field="fileDesc" width="75" headerAlign="center" align="center"
                 allowSort="true">备注说明
                <input property="editor" class="mini-textbox">
            </div>
            <div field="complete" width="70" headerAlign="center" align="center" renderer="onProgressRenderer">上传进度
            </div>
            <div field="status" width="60" headerAlign="center" align="center">上传状态
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="action" width="30" headerAlign="center" align="center">操作
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var multiuploadGrid = mini.get("#multiuploadGrid");
    var applyId = "${applyId}";
    var fileType = "${fileType}";

    var ALLFileType = [{key:"实物质量评价报告",value:"实物质量评价报告"},{key:"初物检查表",value:"初物检查表"},{key:"评审单",value:"评审单"},{key:"故障数据",value:"故障数据"},{key:"其它",value:"其它"}];

    function SetData(params) {
        projectParams = params.projectParams;
        initUpload();
    }

    function onProgressRenderer(e) {
        var value = e.value;
        var s = '<div class="progressbar">'
            + '<div class="progressbar-percent" style="width:' + value + '%;"></div>'
            + '<div class="progressbar-label">' + value + '%</div>'
            + '</div>';
        return s;
    }

    function initUpload() {
        $('#fileUploadDiv').Huploadify({
            formData: projectParams,
            sendFileAttr: ["fileName", "fileType","fileSize", "fileDesc"],
            uploadFileList: multiuploadGrid,
            url: jsUseCtxPath + '/trialPartsProcess/core/trialPartsProcess/upload.do?applyId=' + applyId ,

            onUploadStart: function (file) {
                var row = this.uploadFileList.findRow(function (row) {
                    if (row.id == file.id) return true;
                });
                if(!row.fileType) {
                    return {success:false,message:"“"+row.fileName+"”没有选择文件类型，请点击单元格处选择！"};
                }

                return {success: true, message: ""};
            },
            addFileToGrid: function (files) {
                for (var i = 0; i < files.length; i++) {
                    var file = files[i];
                    var row = {
                        id: file.id,
                        fileName: file.name,
                        status: file.status,
                        fileSize: bytesToSize(file.size),
                        complete: 0
                    };
                    this.uploadFileList.addRow(row);
                }
            }
        });
    }

</script>
</body>
</html>
