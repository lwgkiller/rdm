<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>文件上传</title>
    <%@include file="/commons/edit.jsp" %>
    <script src="${ctxPath}/scripts/multiFileUpload/h5uploadFile.js" type="text/javascript"></script>
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
        <a class="mini-button file-select" iconCls="icon-search">选择文件</a>
        <a class="mini-button file-upload" iconCls="icon-upload" name="multiupload">开始上传</a>
        <a class="mini-button file-clear" iconCls="icon-remove" name="removeAll">清空列表</a>
    </div>
    <div id="multiuploadGrid" class="mini-datagrid" allowSortColumn="false" multiSelect="false" idField="id"
         allowCellEdit="true" allowCellSelect="true" showPager="false" showToolbar="false" allowCellWrap="false">
        <div property="columns">
            <div type="indexColumn"></div>
            <div field="fileName" width="110" headerAlign="center" align="center">文件名
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <%--<div field="fileDesc" width="90" headerAlign="center">备注说明--%>
            <%--<textarea property="editor" id="fileDesc" name="fileDesc" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;height:200px;line-height:25px;"--%>
            <%--label="备注说明" datatype="varchar" length="500" vtype="length:500" minlen="0" allowinput="true"--%>
            <%--emptytext="请输入备注说明..." mwidth="80" wunit="%" hunit="px"></textarea>--%>
            <%--</div>--%>
            <div field="fileSize" width="50" headerAlign="center" align="center">文件大小
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div displayField="name" name="fileType" field="fileType" width="60" headerAlign="center" align="center">
                文件类型
                <input property="editor" class="mini-combobox" style="width:90%;"
                       textField="name" valueField="key" emptyText="请选择..."
                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=standardValueTableNames"/>
            </div>
            <div name="fileLanguage" field="fileLanguage" width="50" headerAlign="center" align="center">语言
                <input id="fileLanguage" property="editor" class="mini-combobox" style="width:90%;"
                       textField="text" valueField="id" emptyText="请选择..."
                       data="[{id:'英文',text:'英文'},{id:'中文',text:'中文'}]"
                       allowInput="false" showNullItem="false" nullItemText="请选择..." required="true"/>
            </div>
            <div name="fileVersionType" field="fileVersionType" width="50" headerAlign="center" align="center">版本类型
                <input id="fileVersionType" property="editor" class="mini-combobox" style="width:90%;"
                       textField="text" valueField="id" emptyText="请选择..."
                       data="[{id:'完整版',text:'完整版'},{id:'常规版',text:'常规版'},{id:'测试版',text:'测试版'},{id:'测试版修订',text:'测试版修订'}]"
                       allowInput="false" showNullItem="false" nullItemText="请选择..." required="true"/>
            </div>
            <div field="complete" width="50" headerAlign="center" align="center" renderer="onProgressRenderer">上传进度
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
    var masterId = "${masterId}";
    var multiuploadGrid = mini.get("#multiuploadGrid");
    var productType = "${productType}";
    var versionType = "${versionType}";
    var applicationNumber = "${applicationNumber}";
    var salesModel = "${salesModel}";
    var jxbzzsh = "${jxbzzsh}";
    var step = "${step}";

    $(function () {
            if (masterId != "1001") {
                multiuploadGrid.hideColumn("fileType");
            }else {
                multiuploadGrid.hideColumn("fileLanguage");
                multiuploadGrid.hideColumn("fileVersionType");
            }
            if (jxbzzsh != "yes") {
                multiuploadGrid.hideColumn("fileLanguage");
                multiuploadGrid.hideColumn("fileVersionType");
            }
        }
    );

    initUpload();

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


    function bytesToSize(bytes) {
        if (bytes === 0) return '0 B';
        var k = 1024,
            sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
            i = Math.floor(Math.log(bytes) / Math.log(k));

        return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i];
    }

    function initUpload() {
        $('#fileUploadDiv').Huploadify({
            formData: {
                masterId: masterId,
                productType: productType,
                versionType: versionType,
                applicationNumber: applicationNumber,
                salesModel: salesModel,
                step: step
            },
            sendFileAttr: ["fileName", "fileSize", "fileType","fileLanguage","fileVersionType"],
            uploadFileList: multiuploadGrid,
            url: jsUseCtxPath + '/serviceEngineering/core/jxcshc/jxcshcUpload.do',
            onUploadStart: function (file) {
                if (productType && versionType && applicationNumber && salesModel) {
                    if (this.uploadFileList.data.length > 1) {
                        return {success: false, message: "一次只能选择一个文件!"};
                    }
                }
                var row = this.uploadFileList.findRow(function (row) {
                    if (row.id == file.id) return true;
                });
                if (jxbzzsh == "yes"&&masterId != "1001") {
                    if (!row.fileLanguage) {
                        return {success: false, message: "“" + row.fileName + "”没有选择语言，请点击单元格处选择！"};
                    }
                    if (!row.fileVersionType) {
                        return {success: false, message: "“" + row.fileName + "”没有选择版本类型，请点击单元格处选择！"};
                    }
                }
                if (masterId == "1001") {
                    if (!row.fileType) {
                        return {success: false, message: "“" + row.fileName + "”没有选择文件类别，请点击单元格处选择！"};
                    }
                    if (row.fileName) {
                        var fileNameSuffix = row.fileName.split('.').pop();
                        if (fileNameSuffix != 'xlsx') {
                            return {success: false, message: "“" + row.fileName + "”不是xlsx文件！"}
                        }
                    }
                    var gridData = this.uploadFileList.data;
                    var fileTypeList = [];
                    var flag = false;
                    for (var i = 0; i < gridData.length; i++) {
                        fileTypeList += gridData[i].fileType + ",";
                    }
                    for (var i = 0; i < gridData.length; i++) {
                        if (fileTypeList.replace(gridData[i].fileType + ",").indexOf(gridData[i].fileType + ",") > -1) {
                            flag = true;
                        }
                    }
                    if (flag) {
                        return {success: false, message: "上传模板类型重复,请重新选择！"}
                    }
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
