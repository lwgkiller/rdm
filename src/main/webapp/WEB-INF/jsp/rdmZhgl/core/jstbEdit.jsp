<%--
  Created by IntelliJ IDEA.
  User: matianyu
  Date: 2021/2/23
  Time: 14:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>发布技术通报</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/jstbEdit.js?version=${static_res_version}"type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
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
<div id="changeToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="saveChange" class="mini-button" onclick="saveChange()">保存变更</a>
        <a class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formJstb" method="post">
            <input id="jstbId" name="jstbId" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: left">发放部门（挖掘机械研究院直接选择“挖掘机械研究院”）：<span style="color: red">*</span></td>
                    <td id="recTd" colspan="3">
                        <input id="depSelectId" name="relatedDeptIds" class="mini-dep rxc" plugins="mini-dep"
                               style="width:98%;height:34px"
                               allowinput="false" textname="relatedDeptNames" single="false" initlogindep="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">技术通报编号(保存后自动生成)：</td>
                    <td style="width: 33%;min-width:170px">
                        <input id="numInfo" name="numInfo"  class="mini-textbox" readonly style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;text-align: left">技术通报标题：<span style="color: #ff0000">*</span></td>
                    <td>
                        <input id="jstbTitle" name="jstbTitle" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: left">主题词或通报概述：（200字以内）</td>
                    <td>
                    <textarea id="jstbContent" name="jstbContent" class="mini-textarea rxc" plugins="mini-textarea"
                              style="width:98%;height:250px;line-height:25px;" label="消息内容" datatype="varchar"
                              length="200" vtype="length:200" minlen="0" allowinput="true" emptytext="请输入概述或关键字..."
                              mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%;height: 300px">附件列表：</td>
                    <td colspan="3">
                        <div style="margin-top: 25px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="fileupload()">添加附件</a>
                            <span style="color: red">注：添加附件前，请先进行草稿的保存</span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 100%"
                             allowResize="false"
                             idField="id"  autoload="true"
                             url="${ctxPath}/rdmZhgl/Jstb/getJstbFileList.do?jstbId=${jstbId}"
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
    var jstbId = "${jstbId}";
    var formJstb = new mini.Form("#formJstb");
    var fileListGrid=mini.get("fileListGrid");
    var jstbListGrid = mini.get("jstbListGrid");
    var nodeVarsStr='${nodeVars}';
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml=returnJstbPreviewSpan(record.fileName,record.fileId,record.jstbId,coverContent);
        //增加删除按钮
        if(action=='edit' || (action=='task' && isBianzhi == 'yes')||action=='change') {
            var deleteUrl="/rdmZhgl/Jstb/delJstbFile.do"
            cellHtml+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.jstbId+'\',\''+deleteUrl+'\')">删除</span>';
        }
        return cellHtml;
    }

</script>
</body>
</html>