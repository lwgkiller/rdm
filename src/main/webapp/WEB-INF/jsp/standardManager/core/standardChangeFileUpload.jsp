<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>文件上传</title>
    <%@include file="/commons/edit.jsp" %>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}" type="text/javascript"></script>
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
<div id="fileUploadDiv" class="mini-panel" style="width: 100%; height: 100%" showfooter="false" bodystyle="padding:0" borderStyle="border:0"
     showheader="false">
    <input class="file-input" style="display:none;" type="file" name="fileselect[]" multiple accept="" />
    <div class="mini-toolbar">
        <a class="mini-button file-select" iconCls="icon-search" >浏览...</a>
        <a class="mini-button file-upload" iconCls="icon-upload" name="multiupload">开始上传</a>
        <a class="mini-button file-clear" iconCls="icon-remove" name="removeAll">清空列表</a>
    </div>
    <div id="multiuploadGrid" class="mini-datagrid"  allowSortColumn="false" multiSelect="false" idField="id"
         allowCellEdit="true" allowCellSelect="true" showPager="false" showToolbar="false" allowCellWrap="false">
        <div property="columns">
            <div type="indexColumn"></div>
            <div field="fileName" width="110" >文件名
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="fileSize" width="50" headerAlign="center" align="center">文件大小
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="fileDeliveryId" width="100" displayField="deliveryTypeName" headerAlign="center">
                文件类型<span style="color: #ff0000">*</span>
                <input id="deliverySelect" property="editor" class="mini-combobox" style="width:90%;"
                       textField="text" valueField="id" emptyText="请选择..."
                       data="[{id:'GBJH',text:'国标计划下达文件'},{id:'JSFW',text:'技术服务合同'},
                               {id:'BZCA',text:'标准草案'},{id:'BZZQ',text:'标准征求意见稿'},
                               {id:'YJHZ',text:'意见汇总及处理'},{id:'BZHS',text:'标准会审稿'},
                               {id:'BZBP',text:'标准报批稿'},{id:'FBG',text:'发布稿'}]"
                       allowInput="false" showNullItem="false" nullItemText="请选择..." required="true"/>
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

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var multiuploadGrid = mini.get("#multiuploadGrid");
    var standardId = "${standardId}";
    $(function () {
        initUpload();
    });

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

    function initUpload(){
        $('#fileUploadDiv').Huploadify({
            formData:{standardId:standardId},
            sendFileAttr:["fileName","fileSize","fileDeliveryId"],
            uploadFileList:multiuploadGrid,
            url:jsUseCtxPath+'/standardManager/core/NationStandardChangeController/upload.do',
            onUploadStart:function (file) {
                var row = this.uploadFileList.findRow(function(row){
                    if(row.id == file.id) return true;
                });
                if(!row.fileDeliveryId) {
                    return {success:false,message:"“"+row.fileName+"”没有选择附件类型，请点击单元格处选择！"};
                }
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

    function bytesToSize(bytes) {
        if (bytes === 0) return '0 B';
        var k = 1024,
            sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
            i = Math.floor(Math.log(bytes) / Math.log(k));

        return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i];
    }
</script>
</body>
</html>
