<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>消息管理</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/xcmgProjectManager/messageManagement.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>

<div class="mini-toolbar">
	<div class="searchBox">
			<ul>
				<li style="float: right">
					<a id="editMsg" class="mini-button "  style="margin-right: 20px" iconCls="icon-emailEdit" onclick="editMsg()">发送消息</a>
					<a id="refresh" class="mini-button " iconCls="icon-reload" onclick="searchSend()">刷新</a>
				</li>
			</ul>
	</div>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="sendMsgListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
		 idField="id" allowAlternating="true" showPager="false" multiSelect="false"
		 url="${ctxPath}/xcmgProjectManager/core/message/querySend.do">
		<div property="columns">
			<div field="typeName" headerAlign='center' align='center' width="60" >消息类型</div>
			<div field="title" headerAlign='center' align='center' width="80">消息标题</div>
			<div field="content"  headerAlign='center' align="center">消息内容</div>
			<div field="CREATE_TIME_" width="60" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign='center' align="center">发送时间</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var sendMsgListGrid=mini.get("sendMsgListGrid");
</script>
</body>
</html>