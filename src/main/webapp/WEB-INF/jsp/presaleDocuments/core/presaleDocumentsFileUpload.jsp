<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>文件上传</title>
    <%@include file="/commons/edit.jsp" %>
    <script src="${ctxPath}/scripts/presaleDocuments/presaleDocumentsConst.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/multiFileUpload/h5uploadFile.js?version=${static_res_version}" type="text/javascript"></script>
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
<div style="display: none">
    <input id="comboboxEditor" name="systemType" class="mini-combobox" style="width:98%"
           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=PresaleDocumentJiShuWenJianZiLiaoFuJianZiFenLei"
           valueField="key" textField="value"/>
    <input id="textareaEditor" property="editor" id="fileDesc" name="fileDesc" class="mini-textarea rxc" plugins="mini-textarea"
           style="width:98%;height:200px;line-height:25px;"
           label="备注说明" datatype="varchar" length="500" vtype="length:500" minlen="0" allowinput="true"
           emptytext="请输入备注说明..." mwidth="80" wunit="%" hunit="px"></input>
</div>
<div class="mini-panel" id="fileUploadDiv" style="width: 100%; height: 100%" showfooter="true" bodystyle="padding:0" borderStyle="border:0"
     showheader="false">
    <input class="file-input" style="display:none;" type="file" name="fileselect[]" multiple accept=""/>
    <div class="mini-toolbar">
        <a class="mini-button file-select" iconCls="icon-search">选择文件</a>
        <a class="mini-button file-upload" iconCls="icon-upload" name="multiupload">开始上传</a>
        <a class="mini-button file-clear" iconCls="icon-remove" name="removeAll">清空列表</a>
        <span id="errMsg" style="color: red"></span>
    </div>
    <div id="multiuploadGrid" class="mini-datagrid" allowSortColumn="false" multiSelect="false" idField="id"
         allowCellEdit="true" allowCellSelect="true" showPager="false" showToolbar="false" allowCellWrap="false"
         oncellbeginedit="cellbeginedit">
        <div property="columns">
            <div type="indexColumn"></div>
            <div field="fileName" width="110">文件名
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="fileDesc" width="90" headerAlign="center">备注说明</div>
            <div field="fileSize" width="50" headerAlign="center" align="center">文件大小
                <input property="editor" class="mini-textbox" readonly/>
            </div>

            <div field="complete" width="80" headerAlign="center" align="center" renderer="onProgressRenderer">上传进度
            </div>
            <div field="status" width="60" headerAlign="center" align="center">上传状态
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="action" width="30" headerAlign="center" align="center">操作
            </div>
        </div>
    </div>
</div>

</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var grid = mini.get("multiuploadGrid");
    var businessId = "${businessId}";
    var businessType_ = "${businessType}";
    var urlCut = "${urlCut}";
    var fileType = "${fileType}";
    var isSingle = "${isSingle}";
    //..
    $(function () {
        initUpload();
    });
    //..
    function initUpload() {
        $('#fileUploadDiv').Huploadify({
            formData: {
                businessId: businessId,
                businessType: businessType_
            },
            sendFileAttr: ["fileName", "fileSize", "fileDesc"],
            uploadFileList: grid,
            url: jsUseCtxPath + urlCut,
            onUploadStart: function (file) {
                if (this.uploadFileList.data.length > 1 && isSingle == "true") {
                    document.getElementById('errMsg').textContent = "只能上传一个文件！";
                    return;
                }
                if (businessType_ == PresaleDocumentType.BUSINESS_TYPE_JISHUWENJIANFUJIAN) {
                    for (var i = 0, l = this.uploadFileList.data.length; i < l; i++) {
                        if (!this.uploadFileList.data[i].fileDesc) {
                            alert("必须在描述中选择具体类型！");
                            return;
                        }
                    }
                }
                var row = this.uploadFileList.findRow(function (row) {
                    if (row.id == file.id) return true;
                });
                if (!fileType || fileType == uploadFormat.UPLOAD_FORMAT_OTHER) {
                    //不限制
                } else if (fileType == uploadFormat.UPLOAD_FORMAT_PIC) {
                    //只支持图片
                    var suffix = getFileType(row.fileName);
                    if (!suffix || suffix != uploadFormat.UPLOAD_FORMAT_PIC) {
                        return {success: false, message: "“" + row.fileName + "”不是图片类型，请重新选择！"};
                    }
                } else if (fileType == uploadFormat.UPLOAD_FORMAT_OFFICE) {
                    //只支office
                    var suffix = getFileType(row.fileName);
                    if (!suffix || suffix != uploadFormat.UPLOAD_FORMAT_OFFICE) {
                        return {success: false, message: "“" + row.fileName + "”不是OFFICE类型，请重新选择！"};
                    }
                } else if (fileType == uploadFormat.UPLOAD_FORMAT_PDF) {
                    //只支office
                    var suffix = getFileType(row.fileName);
                    if (!suffix || suffix != uploadFormat.UPLOAD_FORMAT_PDF) {
                        return {success: false, message: "“" + row.fileName + "”不是PDF类型，请重新选择！"};
                    }
                }
                return {success: true, message: ""};
            },
            addFileToGrid: function (files) {
                for (var i = 0; i < files.length; i++) {
                    var file = files[i];
                    var row = {id: file.id, fileName: file.name, status: file.status, fileSize: bytesToSize(file.size), complete: 0};
                    this.uploadFileList.addRow(row);
                }
            }
        });
    }
    //..
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
    //..
    function cellbeginedit(e) {
        var record = e.record, field = e.field, value = e.value;
        if (field == "fileDesc") {
            if (businessType_ == PresaleDocumentType.BUSINESS_TYPE_JISHUWENJIANFUJIAN) {
                e.editor = mini.get("comboboxEditor");
                e.column.editor = e.editor;
            } else {
                e.editor = mini.get("textareaEditor");
                e.column.editor = e.editor;
            }
        }
    }
</script>
</body>
</html>
