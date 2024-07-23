<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div>
    <a id="addFile" class="mini-button" onclick="fileupload()">添加模板</a>
    <span style="color: red">注：相同类型模板只能保存一个,更换前请先删除原模板</span>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 100%;"
         allowResize="false"
         idField="id" autoload="true"
         url="${ctxPath}/serviceEngineering/core/jxbzzbsh/queryJxbzzbshFileList.do?jxbzzbshId=1001"
         multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
        <div property="columns">
            <div type="indexcolumn" align="center" width="20">序号</div>
            <div field="fileName" width="100" headerAlign="center" align="center">模板名称</div>
            <div field="fileSize" width="50" headerAlign="center" align="center">模板大小</div>
            <div field="fileType" width="60" headerAlign="center" align="center" renderer="fileTypeRenderer">模板类型</div>
            <div field="action" width="40" headerAlign='center' align="center" renderer="operationRenderer">操作
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var fileListGrid = mini.get("#fileListGrid");
    var isJSGLBUser =${isJSGLBUser};
    var currentTime="${currentTime}";
    var currentUserName="${currentUserName}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";

    // if (!isJSGLBUser) {
    //     mini.get("addFile").setEnabled(false);
    // }

    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        var deleteUrl="/serviceEngineering/core/jxcshc/delUploadFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.masterId + '\',\'' + deleteUrl + '\')">删除</span>';
        return cellHtml;
    }

    function fileupload() {
        mini.open({
            title: "添加模板",
            url: jsUseCtxPath + "/serviceEngineering/core/jxbzzbsh/openJxbzzbshUploadWindow.do?masterId=1001",
            width: 850,
            height: 550,
            showModal:true,
            allowResize: true,
            ondestroy: function () {
                if(fileListGrid) {
                    fileListGrid.load();
                }
            }
        });
    }

    function fileTypeRenderer(e) {
        var record = e.record;
        var fileType = record.fileType;
        var arr = [
            {'key': 'dianwa_cgb', 'value': '电挖（常规版）'},
            {'key': 'dianwa_csb', 'value': '电挖（测试版）'},
            {'key': 'dianwa_wzb', 'value': '电挖（完整版）'},
            {'key': 'tewa_cgb', 'value': '特挖（常规版）'},
            {'key': 'tewa_csb', 'value': '特挖（测试版）'},
            {'key': 'tewa_wzb', 'value': '特挖（完整版）'},
            {'key': 'lunwa_cgb', 'value': '轮挖（常规版）'},
            {'key': 'lunwa_csb', 'value': '轮挖（测试版）'},
            {'key': 'lunwa_wzb', 'value': '轮挖（完整版）'},
            {'key': 'lvwa_cgb', 'value': '履挖（常规版）'},
            {'key': 'lvwa_csb', 'value': '履挖（测试版）'},
            {'key': 'lvwa_wzb', 'value': '履挖（完整版）'}
        ];
        return $.formatItemValue(arr,fileType);
    }
</script>
</body>
</html>
