<%-- 
    Document   : [MailFolder]明细页
    Created on : 2015-3-28, 17:42:57
    Author     : csx
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="rx" uri="http://www.redxun.cn/detailFun"%>
<%@taglib prefix="rxc" uri="http://www.redxun.cn/commonFun"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>[MailFolder]明细</title>
<%@include file="/commons/dynamic.jspf"%>
<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
<link href="${ctxPath}/styles/icons.css" rel="stylesheet" type="text/css" />
<script src="${ctxPath}/scripts/boot.js" type="text/javascript"></script>
<script src="${ctxPath}/scripts/share.js" type="text/javascript"></script>
<link href="${ctxPath}/styles/form.css" rel="stylesheet" type="text/css" />
</head>
<body>
	<rx:toolbar toolbarId="toolbar1" />
	<div id="form1" class="form-outer">
		<div style="padding: 5px;">
			<table style="width: 100%" class="table-detail" cellpadding="0"
				cellspacing="1">
				<caption>[MailFolder]基本信息</caption>
				<tr>
					<th>配置ID：</th>
					<td>${mailFolder.configId}</td>
				</tr>
				<tr>
					<th>主键：</th>
					<td>${mailFolder.userId}</td>
				</tr>
				<tr>
					<th>文件夹名称：</th>
					<td>${mailFolder.name}</td>
				</tr>
				<tr>
					<th>父目录：</th>
					<td>${mailFolder.parentId}</td>
				</tr>
				<tr>
					<th>目录层：</th>
					<td>${mailFolder.depth}</td>
				</tr>
				<tr>
					<th>目录路径：</th>
					<td>${mailFolder.path}</td>
				</tr>
				<tr>
					<th>文件夹类型 RECEIVE-FOLDER=收信箱 SENDER-FOLDEr=发信箱
						DRAFT-FOLDER=草稿箱 DEL-FOLDER=删除箱 OTHER-FOLDER=其他：</th>
					<td>${mailFolder.type}</td>
				</tr>
			</table>
		</div>
		<div style="padding: 5px">
			<table class="table-detail" cellpadding="0" cellspacing="1">
				<caption>更新信息</caption>
				<tr>
					<th>创建人</th>
					<td><rxc:userLabel userId="${mailFolder.createBy}" /></td>
					<th>创建时间</th>
					<td><fmt:formatDate value="${mailFolder.createTime}"
							pattern="yyyy-MM-dd HH:mm" /></td>
				</tr>
				<tr>
					<th>更新人</th>
					<td><rxc:userLabel userId="${mailFolder.updateBy}" /></td>
					<th>更新时间</th>
					<td><fmt:formatDate value="${mailFolder.updateTime}"
							pattern="yyyy-MM-dd HH:mm" /></td>
				</tr>
			</table>
		</div>
	</div>
	<rx:detailScript baseUrl="inst/mail/mailFolder" formId="form1" />
</body>
</html>