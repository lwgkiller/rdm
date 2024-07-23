<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>文件上传</title>
    <%@include file="/commons/edit.jsp" %>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/multiFileUpload/multiuploadFile.js" type="text/javascript"></script>
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
        <span>当前支持文件类型：pdf、doc、docx、xls、xlsx</span>
    </div>
    <div colspan="4" style="height:20px;"><span style="color: red">零件图册：文件名格式“整机编码-语言缩写小写.文件后缀”，保养件、易损件清单：文件名格式“对应编号-**件清单.文件后缀”</span></div>
    <div id="multiuploadGrid" class="mini-datagrid"  allowResize="false" idField="id"
         allowCellEdit="false" allowCellSelect="false" allowSortColumn="false"
         multiSelect="false" showColumnsMenu="false" allowAlternating="true" showPager="false" allowCellWrap="false">
        <div property="columns">
            <div type="indexColumn"></div>
            <div field="fileName" width="110" headerAlign="center" align="center">文件名
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="fileType" headerAlign="center" align="center" width="110">文件类型
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="fileSize" width="40" headerAlign="center" align="center">文件大小
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

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var multiuploadGrid = mini.get("#multiuploadGrid");

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
        $('#fileUploadDiv').Huploadify({
            sendFileAttr:["fileName","fileType","fileSize"],
            uploadFileList:multiuploadGrid,
            url:jsUseCtxPath+'/serviceEngineering/core/patsAtlasFileCollection/fileUpload.do',
            onUploadStart:function (file) {
                var row = this.uploadFileList.findRow(function(row){
                    if(row.id == file.id) return true;
                });
                return {success:true,message:""};
            },
            addFileToGrid: function (files) {
                for(var i=0;i<files.length;i++) {
                    var file=files[i];
                    var fileName = file.name;
                    var fileTypeSign = fileName.lastIndexOf(".");
                    var fileNameSuffix = fileName.substring(fileTypeSign+1,fileName.length);
                    if (!["pdf", "doc", "xlsx", "xls", "docx"].includes(fileNameSuffix)) {
                        mini.alert("请上传pdf、doc、docx、xls、xlsx文件！");
                        return;
                    }
                    var filleNameNoSuffix = fileName.substring(0,fileTypeSign);
                    //文件名校验，名称中包含：保养件清单、易损件清单，识别对应的文件类型，否则默认为：零件图册；除零件图册涉及多语种，其它两种类型文件默认为中文
                    if(filleNameNoSuffix){
                        var tempFileType = "";
                        var languageType = "";
                        var vinCode = "";
                        if (/(-易损件清单)$/.test(filleNameNoSuffix)) {
                            tempFileType = "易损件清单";
                            languageType = "zh-cn";
                        }else if (/(-保养件清单)$/.test(filleNameNoSuffix)) {
                            tempFileType = "保养件清单";
                            languageType = "zh-cn";
                        } else if (!filleNameNoSuffix.includes("易损件清单")&& !filleNameNoSuffix.includes("保养件清单") && /(-([a-zA-Z]{2})|-([a-zA-Z]{2})-([a-zA-Z]{2}))$/.test(filleNameNoSuffix)){
                            tempFileType = "零件图册";
                            var index = filleNameNoSuffix.indexOf('-');
                            languageType = filleNameNoSuffix.slice(index);
                        }
                        vinCode = filleNameNoSuffix.split('-').shift();

                        if(vinCode!='' && languageType!='' && tempFileType!=''){
                            var row = {id:file.id,fileName:file.name,fileType:tempFileType,status:file.status,fileSize:bytesToSize(file.size),complete:0};
                            this.uploadFileList.addRow(row);
                        }else {
                            mini.alert('零件图册：文件名命名格式“整机-语言代码.文件后缀”！'+'易损件、保养件清单：文件名命名格式“物料号等信息-文件类型.文件后缀”');
                            return;
                        }
                    }else{
                        mini.alert('文件名异常！');
                        return;
                    }


                }
            }


        });
    });

</script>
</body>
</html>
