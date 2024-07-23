<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>试制试验新增申请文件上传</title>
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
    <input class="file-input" style="display:none;" type="file" name="fileselect[]" multiple accept=""/>
    <div class="mini-toolbar">
        <a class="mini-button file-select" iconCls="icon-search">浏览...</a>
        <a class="mini-button file-upload" iconCls="icon-upload" name="multiupload">开始上传</a>
        <a class="mini-button file-clear" iconCls="icon-remove" name="removeAll">清空列表</a>
    </div>
    <div id="multiuploadGrid" class="mini-datagrid" allowSortColumn="false" multiSelect="false" idField="id"
         allowCellEdit="true" allowCellSelect="true" showPager="false" showToolbar="false" allowCellWrap="false">
        <div property="columns">
            <div type="indexColumn"></div>
            <div field="fileType" displayField="fileTypeName" width="110" headerAlign="center" align="center">附件类型<span style="color: #ff0000">*</span>
                <input property="editor" class="mini-combobox" style="width:90%;"
                       textField="value" valueField="key" emptyText="请选择..."
                       allowInput="false" data="[{'key': 'scheme', 'value': '试验方案'},{'key': 'report', 'value': '试验报告'}]" />
            </div>
            <div field="fileName" width="110" headerAlign="center" align="center">文件名
                <input property="editor" class="mini-textbox" readonly/>
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
    var belongSingleId = "${belongSingleId}";


    function SetData(params) {
        projectParams = params.projectParams;
        initUpload();
    }

    function onProgressRenderer(e) {
        var record = e.record;
        var value = e.value;
        var uid = record._uid;
        var s = '<div class="progressbar">'
            + '<div class="progressbar-percent" style="width:' + value + '%;"></div>'
            + '<div class="progressbar-label">' + value + '%</div>'
            + '</div>';
        return s;
    }

    function initUpload() {
        $('#fileUploadDiv').Huploadify({
            formData: projectParams,
            sendFileAttr: ["fileType", "fileName", "fileSize", "fileDesc"],
            uploadFileList: multiuploadGrid,
            url: jsUseCtxPath + '/drbfm/testTask/upload.do?applyId=' + applyId + "&belongSingleId" + belongSingleId,

            onUploadStart: function (file) {
                var row = this.uploadFileList.findRow(function (row) {
                    if (row.id == file.id) return true;
                });
                if(!row.fileType) {
                    return {success:false,message:"“"+row.fileName+"”没有选择附件类型，请点击单元格处选择！"};
                }
                return {success: true, message: ""};
            },
            addFileToGrid: function (files) {
                var fileTypeName="";
                if(fileType) {
                    fileTypeName=fileType=='scheme'?'试验方案':'试验报告';
                }
                for (var i = 0; i < files.length; i++) {
                    var file = files[i];
                    var row = {
                        id: file.id,
                        fileType:fileType,
                        fileTypeName:fileTypeName,
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
    multiuploadGrid.on("cellbeginedit", function (e) {
        if (e.field == "fileType") {
            if(fileType) {
                e.editor.setEnabled(false);
            } else {
                e.editor.setEnabled(true);
            }
        }
    });

</script>
</body>
</html>
