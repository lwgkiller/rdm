<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>文件上传</title>
    <%@include file="/commons/edit.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/multiFileUpload/h5uploadFile.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rjzzFileUpload.js?version=${static_res_version}" type="text/javascript"></script>
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
<div class="mini-panel" id="fileUploadDiv" style="width: 100%; height: 100%" showfooter="true" bodystyle="padding:0" borderStyle="border:0"
     showheader="false">
    <input class="file-input" style="display:none;" type="file" name="fileselect[]" multiple accept="" />
    <div class="mini-toolbar">
        <a class="mini-button file-select" iconCls="icon-search" >浏览...</a>
        <a class="mini-button file-upload" iconCls="icon-upload" name="multiupload">开始上传</a>
        <a class="mini-button file-clear" iconCls="icon-remove" name="removeAll">清空列表</a>
    </div>
    <div id="multiupload1" class="mini-datagrid"  allowSortColumn="false" multiSelect="false" idField="id"
         allowCellEdit="true" allowCellSelect="true" showPager="false" showToolbar="false" allowCellWrap="false">
        <div property="columns">
            <div type="indexColumn"></div>
            <div field="fileName" width="110" headerAlign="center" align="center">文件名
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="fileSize" width="40" headerAlign="center" align="center">文件大小
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="fjlx" width="100" displayField="value" headerAlign="center">附件类型
                <input property="editor" class="mini-combobox" style="width:90%;"
                       textField="value" valueField="key" emptyText="请选择..."
                       allowInput="false" showNullItem="false" nullItemText="请选择..."
                       data="[{'key' : 'RJSYSC','value' : '软件使用手册'},{'key' : 'RJYCX','value' : '软件源程序'},{'key' : 'ZS','value' : '证书'}]"/>
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
    var grid = mini.get("multiupload1");
    var rjzzId="${rjzzId}";


</script>
</body>
</html>
