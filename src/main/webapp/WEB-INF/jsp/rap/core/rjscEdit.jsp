
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>新增</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rap/rjscEdit.js?version=${static_res_version}"type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
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
<div id="detailToolBar" class="topToolBar" >
    <div>
        <a id="save" name="save" class="mini-button" onclick="save()">保存</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formRjsc" method="post">
            <input id="rjId" name="rjId" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 10%;font-size:14pt">软件名称：</td>
                    <td style="width: 33%;min-width:170px;font-size:14pt">
                        <input id="rjName" name="rjName"  class="mini-textbox"  style="width:98%;" />
                    </td>
                    <td style="font-size:14pt;width: 20%;text-align: left">版本号：</td>
                    <td style="width: 33%;min-width:170px;font-size:14pt">
                        <input id="rjNo" name="rjNo"  class="mini-textbox"  style="width:98%;" />
                    </td>
                    <td style="font-size:14pt;width: 20%;text-align: left">有效期：</td>
                    <td style="width: 33%;min-width:170px;font-size:14pt">
                        <input id="term" name="term"  class="mini-textbox"  style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%;height: 300px">附件列表：</td>
                    <td colspan="5">
                        <div style="margin-top: 25px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="fileupload()">添加附件</a>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 100%"
                             allowResize="false"
                             idField="id"  autoload="true"
                             url="${ctxPath}/environment/core/Rjsc/getFileList.do?rjId=${rjId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20">序号</div>
                                <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                                <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer">操作
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var action="${action}";
    var jsUseCtxPath = "${ctxPath}";
    var rjscListGrid = mini.get("rjscListGrid");
    var rjId = "${rjId}";
    var formRjsc = new mini.Form("#formRjsc");
    var fileListGrid=mini.get("fileListGrid");
    var currentUserName="${currentUserName}";
    var currentUserId = "${currentUserId}";
    var currentTime="${currentTime}";
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        
        if(action=='edit') {
            var deleteUrl="/environment/core/Rjsc/deleterjscFile.do"
            cellHtml+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.rjId+'\',\''+deleteUrl+'\')">删除</span>';
        }
        return cellHtml;
    }

</script>
</body>
</html>