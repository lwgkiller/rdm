<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>消息查看</title>
    <%@include file="/commons/edit.jsp" %>
    <script src="${ctxPath}/scripts/kindeditor/kindeditor-all-min.js" type="text/javascript"></script>
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
<div class="mini-fit"  style="width: 100%; height: 100%" showfooter="false" bodystyle="padding:0"
     showheader="false">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
    <table class="table-detail" cellspacing="1" cellpadding="0">
        <tr>
            <td style="width: 25%">消息类型：<span style="color: #ff0000">*</span></td>
            <td style="width: 25%;min-width:200px">
                <input id="messageTypeInput" style="width:98%;height: 100%" readonly/>
            </td>
            <td style="width: 25%">发信人：</td>
            <td>
                <input id="sendUserInput" style="width:98%;height: 100%" readonly/>
            </td>
        </tr>
        <tr>
            <td>消息标题：</td>
            <td colspan="3">
                <input id="titleInput" style="width:98%;height: 100%" readonly/>
            </td>
        </tr>
        <tr>
            <td>消息内容：（500字以内）</td>
            <td colspan="3">
                <input id="contentInput" name="content" class="mini-hidden"/>
                <div name="editor" id="editor" rows="10" cols="95" style="height: 335px;width: 98%">
                </div>
            </td>
        </tr>
    </table>
    </div>

</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var messageObj=${msgDetailObj};
    var editor = KindEditor.create('#editor');
    function setContent(_text) {
        editor.html(_text);
    }
    function getContent() {
        return editor.html();
    }
    function setDisabled() {
        editor.readonly();
    }
    function setEnabled() {
        editor.readonly(false);
    }
    if(messageObj) {
        var messageType=messageObj.messageType;
        if(messageType=='system') {
            $("#messageTypeInput").val('平台消息');
        } else {
            $("#messageTypeInput").val('组消息');
        }
        $("#sendUserInput").val(messageObj.sendUserName);
        $("#titleInput").val(messageObj.title);
        setContent(messageObj.content);
        setDisabled();
    }
</script>
</body>
</html>
