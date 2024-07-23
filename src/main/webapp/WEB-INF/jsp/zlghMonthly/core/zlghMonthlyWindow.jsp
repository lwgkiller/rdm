<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>文件上传</title>
    <%@include file="/commons/edit.jsp" %>
    <script src="${ctxPath}/scripts/multiFileUpload/h5uploadFile.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/zlghMonthly/monthlyWindow.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/standardManager/standardUtil.js" type="text/javascript"></script>
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
        <p style="display: inline-block;color: #f6253e;font-size:15px;vertical-align: middle;">
            （<image src="${ctxPath}/styles/images/warn.png" style="margin-right:5px;vertical-align: middle;height: 15px"/>
            仅限于上传pdf文件）
        </p>
    </div>
    <div id="monthlyFile" class="mini-datagrid"  allowSortColumn="false" multiSelect="false" idField="id"
         allowCellEdit="true" allowCellSelect="true" showPager="false" showToolbar="false" allowCellWrap="false">
        <div property="columns">
            <div type="indexColumn"></div>
            <div field="ykTime" width="60" headerAlign="center" align="center" dateFormat="yyyy-MM">月刊时间<span style="color: #ff0000">*</span>
                <input id="ykTime" class="mini-monthpicker" property="editor" />
            </div>
            <div field="fileName" width="110" headerAlign="center" align="center">文件名
                <input id="fileName" property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="description" width="100" headerAlign="center">文件说明
                <textarea property="editor" id="description" name="description" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;height:200px;line-height:25px;"
                          label="文件说明" datatype="varchar" length="500" vtype="length:500" minlen="0" allowinput="true"
                          emptytext="请输入文件说明..." mwidth="80" wunit="%" hunit="px"></textarea>
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
    var monthlyFile = mini.get("#monthlyFile");
    var type = "${type}";
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


</script>
</body>
</html>
