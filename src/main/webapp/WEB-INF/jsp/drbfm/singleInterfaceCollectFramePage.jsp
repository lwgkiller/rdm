<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib  prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib  prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html >
	<head>
		<title>部件验证项目接口需求收集</title>
		<%@include file="/commons/edit.jsp"%>
		<script src="${ctxPath}/scripts/drbfm/singleInterfaceCollectFramePage.js?version=${static_res_version}" type="text/javascript"></script>
		<script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
		<style>
			.table-detail > tbody > tr > td{
				border: 1px solid #ccc;
			}
		</style>
	</head>
	<body>
		<div id="detailToolBar" class="topToolBar" style="display: none">
			<div>
				<a id="processInfo" class="mini-button" style="display: none" onclick="processCollectInfo()">流程信息</a>
				<a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
			</div>
		</div>
		<div class="mini-fit" style="height: 100%;">
			<div class="form-container" style="margin: 0;padding-top: 0">
				<form id="formProject" method="post">
					<input id="id" name="id" class="mini-hidden"/>
					<input id="instId" name="instId" class="mini-hidden"/>
					<input id="cpzgId" name="cpzgId" class="mini-hidden"/>
					<input id="cpzgName" name="cpzgName" class="mini-hidden"/>
					<input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
					<input id="interfaceFeedBackUserName" name="interfaceFeedBackUserName" class="mini-hidden"/>
					<input id="structId" name="structId" class="mini-hidden"/>
					<input id="structNumber" name="structNumber" class="mini-hidden"/>
					<input id="structName" name="structName" class="mini-hidden"/>
					<input id="analyseUserId" name="analyseUserId" class="mini-hidden"/>
					<input id="analyseUserName" name="analyseUserName" class="mini-hidden"/>
					<input id="belongTotalId" name="belongTotalId" class="mini-hidden"/>
					<input id="belongSingleId" name="belongSingleId" class="mini-hidden"/>
				</form>
				<div id="systemTab" class="mini-tabs" style="width:100%;height:100%;" >
					<div title="功能&要求描述" name="functionAndRequest" refreshOnClick="true"></div>
					<div title="指标分解" name="quotaDecompose" refreshOnClick="true"></div>
				</div>
			</div>
		</div>
		<script>
			mini.parse();
			var jsUseCtxPath="${ctxPath}";
			var stageName ="${stageName}";
			var action="${action}";
			var status="${status}";
			var currentUserNo = "${currentUserNo}";
			var currentUserId = "${currentUserId}";
			var collectType = "${collectType}";
			var systemTab=mini.get("systemTab");
			var id ="${id}";
			var instId ="${instId}";
			var structId = "${structId}";
			var belongSingleId = "${belongSingleId}";
			var formProject = new mini.Form("formProject");
		</script>
	</body>
</html>