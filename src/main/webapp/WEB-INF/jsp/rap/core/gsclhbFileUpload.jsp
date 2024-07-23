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
        <a class="mini-button file-select" iconCls="icon-search" >选择文件</a>
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
            <div field="fileSize" width="30" headerAlign="center" align="center">文件大小
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="fileType" width="110" headerAlign="center" align="center">文件类型
                <input property="editor" id="fileType" name="fileType" class="mini-combobox" style="width:98%" textField="text"
                       valueField="id" emptyText="请选择..." data="dataList"
                       required="true" allowInput="false" showNullItem="true" nullItemText="请选择..."/>
            </div>
            <div field="complete" width="50" headerAlign="center" align="center" renderer="onProgressRenderer">上传进度
            </div>
            <div field="status" width="40" headerAlign="center" align="center">上传状态
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="action" width="20" headerAlign="center" align="center">操作
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var multiuploadGrid = mini.get("#multiuploadGrid");
    var cxId = "${cxId}";
    var upNode = "${upNode}";


    if ("cp" == upNode) {
        var dataList = [{id:'外形照片（正面）',text:'外形照片（正面）'},{id:'外形照片（正侧面45°）',text:'外形照片（正侧面45°）'}
                ,{id:'多种运行模式的相关描述',text:'多种运行模式的相关描述'}, {id:'环保信息标签示意图',text:'环保信息标签示意图'}
                ,{id:'车载终端和精准定位系统防拆除技术措施',text:'车载终端和精准定位系统防拆除技术措施'}
                ,{id:'机械环保代码在机体上的打刻示意图',text:'机械环保代码在机体上的打刻示意图'}
            ,{id:'商标示意图',text:'商标示意图'}];
    }else if ("dq" == upNode) {
        var dataList =
            [{id:'远程车载终端安装位置示意图',text:'远程车载终端安装位置示意图'}];
    } else if ("dl" == upNode) {
        var dataList =
            [{id:'排放控制件位置示意图',text:'排放控制件位置示意图'}
            ,{id:'排放质保零部件清单',text:'排放质保零部件清单'}
            ,{id:'失效策略合规声明',text:'失效策略合规声明'}
            ,{id:'燃油箱安装位置示意图',text:'燃油箱安装位置示意图'}];
    } else if ("hb" == upNode) {
        var dataList =
            [{id:'整机环保信息公开表',text:'整机环保信息公开表'},{id:'发动机环保信息公开表',text:'发动机环保信息公开表'}
            ,{id:'环保信息标签打刻内容',text:'环保信息标签打刻内容'},{id:'单机信息上传内容',text:'单机信息上传内容'}];
    }



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
            formData:{cxId:cxId},
            sendFileAttr:["fileName","fileSize","fileType"],
            uploadFileList:multiuploadGrid,
            url:jsUseCtxPath+'/environment/core/Cx/upload.do?upNode='+upNode,
            onUploadStart:function (file) {
                var row = this.uploadFileList.findRow(function(row){
                    if(row.id == file.id) return true;
                });
                if(!row.fileType) {
                    return {success:false,message:"“"+row.fileName+"”没有选择文件类别，请点击单元格处选择！"};
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
