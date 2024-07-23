<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>消息查看</title>
    <%@include file="/commons/edit.jsp" %>
</head>
<body>
<div id="content" style="margin-top: 20px;"></div>
<script type="text/javascript">
    var jsUseCtxPath="${ctxPath}";
    var messageObj=${applyObj};
    $("#content").html(messageObj.content);
</script>
</body>
</html>
