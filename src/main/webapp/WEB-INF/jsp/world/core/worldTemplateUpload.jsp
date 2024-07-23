<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>文件上传</title>
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
<div id="fileUploadDiv" class="mini-panel" style="width: 100%; height: 100%" showfooter="false" bodystyle="padding:0" borderStyle="border:0"
     showheader="false">
    <input class="file-input" style="display:none;" type="file" name="fileselect[]" multiple accept="" />
    <div class="mini-toolbar">
        <a class="mini-button file-select" iconCls="icon-search" >浏览...</a>
        <a class="mini-button file-upload" iconCls="icon-upload" name="multiupload">开始上传</a>
        <a class="mini-button file-clear" iconCls="icon-remove" name="removeAll">清空列表</a>
    </div>
    <div id="multiuploadGrid" class="mini-datagrid"  allowSortColumn="false" multiSelect="false" idField="id" style="margin-top: 2px"
         allowCellEdit="true" allowCellSelect="true" showPager="false" showToolbar="false" allowCellWrap="false">
        <div property="columns">
            <div type="indexColumn"></div>
            <div field="fileName" width="110" headerAlign="center">模板名称
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="fileCategory" name="fileCategory"  width="90"  renderer="onBdlbType" headerAlign="center">类别
                <input property="editor" class="mini-combobox" textField="text" valueField="key_"
                       emptyText="请选择..." required
                       showNullItem="false" nullItemText="请选择..."
                       url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=BDLB"/>
            </div>
            <div field="fileDesc" width="110" headerAlign="center">描述
                <input property="editor" class="mini-textbox" />
            </div>
            <div field="fileSize" width="60" headerAlign="center" align="center">文件大小
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="complete" width="60" headerAlign="center" align="center" renderer="onProgressRenderer">上传进度
            </div>
            <div field="status" width="60" headerAlign="center" align="center">上传状态
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="action" width="60" headerAlign="center" align="center">操作
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var multiuploadGrid = mini.get("#multiuploadGrid");
    var BDLBList = getDics("BDLB");
    var menuType = "${menuType}";
    var fileType = "${fileType}";
    var menuFlag = "${menuFlag}";
    if(menuFlag=='hide'){
        multiuploadGrid.hideColumn("fileCategory");
    }
    function SetData(params) {
        projectParams=params.projectParams;
        initUpload();
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

    function onBdlbType(e) {
        var record = e.record;
        var value = record.fileCategory;
        var resultText = '';
        for (var i = 0; i < BDLBList.length; i++) {
            if (BDLBList[i].key_ == value) {
                resultText = BDLBList[i].text;
                break
            }
        }
        return resultText;
    }
    function initUpload(){
        $('#fileUploadDiv').Huploadify({
            formData:projectParams,
            sendFileAttr:["fileName","fileSize","fileCategory","fileDesc","menuType","fileType"],
            uploadFileList:multiuploadGrid,
            url:jsUseCtxPath+'/world/core/research/upload.do',
            onUploadStart:function (file) {
                var row = this.uploadFileList.findRow(function(row){
                    if(row.id == file.id) return true;
                });
                return {success:true,message:""};
            },
            addFileToGrid: function (files) {
                for(var i=0;i<files.length;i++) {
                    var file=files[i];
                    var row = {id:file.id,fileName:file.name,status:file.status,fileSize:bytesToSize(file.size),complete:0};
                    this.uploadFileList.addRow(row);
                }
            }
        });
    }
</script>
</body>
</html>
