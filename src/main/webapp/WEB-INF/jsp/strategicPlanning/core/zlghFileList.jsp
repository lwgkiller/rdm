<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2021/4/2
  Time: 14:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>文件附表</title>
    <%@include file="/commons/edit.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/multiFileUpload/h5uploadFile.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/xcmgProjectManager/zlghFileUpload.js?version=${static_res_version}" type="text/javascript"></script>
    <link href="${ctxPath}/styles/css/multiupload.css" rel="stylesheet" type="text/css"/>

</head>
<body>
    <div class="mini-toolbar" id="fileButtons">
        <a id="uploadFile" class="mini-button"  style="margin-bottom: 5px" onclick="uploadCompleteFile">上传文件</a>
    </div>
    <div class="mini-fit" style="height: 100%;">
        <div id="targetFileInfoGrid" class="mini-datagrid" style="height: 100%;" allowResize="false"
             idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
             multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false" allowCellWrap="false" showVGridLines="true">
            <div property="columns">
                <div field="id"  headerAlign="left" visible="false">id</div>
                <div type="indexcolumn" headerAlign="center">序号</div>
                <div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
                <div field="fileSize" align="center"  headerAlign="center"  width="60" >文件大小</div>
                <div field="CREATE_BY_NAME" align="center"  headerAlign="center" width="80" >创建人</div>
                <div field="CREATE_TIME_" align="center" dateFormat="yyyy-MM-dd" headerAlign="center"  width="120">创建时间</div>
                <div renderer="targetFileInfoRenderer" align="center" headerAlign="center" width="100" >操作</div>
            </div>
        </div>
    </div>
    <script type="text/javascript">
        mini.parse();
        var jsUseCtxPath="${ctxPath}";
        var targetFileInfoGrid=mini.get("targetFileInfoGrid");
        var standardId = "${meetingId}";
        var coverContent="${coverContent}";
        var projectAction="${projectAction}";
        var canOperateFile="${canOperateFile}";
        var currentTime="${currentTime}";
        var currentUserId = "${currentUserId}";
        var isZLGHZY =${isZLGHZY};
        var isFzr =${isFzr};
        var isZgsz =${isZgsz};
        // var coverContent=currentUserName+"<br/>"+"<br/>徐州徐工挖掘机械有限公司";
        var url=jsUseCtxPath+"/strategicPlanning/core/xgzlgh/getFileList.do?standardId="+standardId ;

        $(function () {
            queryProjectFiles();
            if(canOperateFile=="false") {
                mini.get("uploadFile").setEnabled(false);
            }
        });
        function queryProjectFiles() {
            targetFileInfoGrid.setUrl(url);
            targetFileInfoGrid.load();
        }
        function uploadCompleteFile() {
            mini.open({
                title: "文件上传",
                url: jsUseCtxPath + "/strategicPlanning/core/xgzlgh/openUploadWindow.do",
                width: 650,
                height: 350,
                showModal:false,
                allowResize: true,
                onload: function () {
                    var iframe = this.getIFrameEl();
                    var projectParams={};
                    projectParams.standardId = standardId;
                    var data = { projectParams: projectParams };  //传递上传参数
                    iframe.contentWindow.SetData(data);
                },
                ondestroy: function (action) {
                    queryProjectFiles();
                }
            });
        }

    </script>
</body>
</html>
