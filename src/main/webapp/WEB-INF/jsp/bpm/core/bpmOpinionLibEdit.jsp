<%-- 
    Document   : [BpmOpinionLib]编辑页
    Created on : 2015-3-21, 0:11:48
    Author     : csx
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html >
<head>
<title>常用审批意见编辑</title>
<%@include file="/commons/edit.jsp"%>

</head>
<body>
<rx:toolbar toolbarId="toolbar1" pkId="${bpmOpinionLib.opId}" />
<div class="mini-fit">
	<div class="form-container">
		<form id="form1" method="post">
				<input id="pkId" name="opId" class="mini-hidden"
					value="${bpmOpinionLib.opId}" />
				<table class="table-detail column-two table-align" cellspacing="1" cellpadding="0">
					<caption>常用审批意见基本信息</caption>
					<tr>
						<td>常用审批意见</td>
						<td><textarea name="opText" class="mini-textarea"
								vtype="maxLength:512" style="width: 100%;min-height: 100px;">${bpmOpinionLib.opText}</textarea></td>
					</tr>
				</table>
		</form>
	</div>
</div>
	<rx:formScript formId="form1" baseUrl="bpm/core/bpmOpinionLib"
		entityName="com.redxun.bpm.core.entity.BpmOpinionLib" />
		
		
	<script type="text/javascript">
		addBody();
	</script>
</body>
</html>