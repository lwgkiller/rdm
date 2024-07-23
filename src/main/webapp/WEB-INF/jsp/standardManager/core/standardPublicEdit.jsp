<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>标准编辑</title>
	<%@include file="/commons/edit.jsp"%>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/standardManager/standardPublicEdit.js?version=${static_res_version}"
			type="text/javascript"></script>
	<script src="${ctxPath}/scripts/standardManager/standardUtil.js?version=${static_res_version}"
			type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar">
	<div>
		<a id="saveStandard" class="mini-button" onclick="saveStandard()"><spring:message code="page.standardPublicEdit.name" /></a>
		<a id="publishNotice" class="mini-button" onclick="publishNotice()"><spring:message code="page.standardPublicEdit.name1" /></a>
		<a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()"><spring:message code="page.standardPublicEdit.name2" /></a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto">
		<form id="formStandard" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<table class="table-detail" cellspacing="1" cellpadding="0">
				<tr>
					<td style="width: 14%"><spring:message code="page.standardPublicEdit.name3" />：</td>
					<td style="width: 36%;min-width:170px">
						<input id="companyName" name="companyName"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 14%"><spring:message code="page.standardPublicEdit.name4" />：</td>
					<td style="width: 36%;min-width:140px">
						<input id="standardName" name="standardName"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%"><spring:message code="page.standardPublicEdit.name5" />：</td>
					<td style="width: 36%;min-width:170px">
						<input id="standardNumber" name="standardNumber"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 14%"><spring:message code="page.standardPublicEdit.name6" />：</td>
					<td style="width: 36%;min-width:170px">
						<input id="standardStatus" class="mini-combobox" style="width:98%;" name="standardStatus"
							   textField="value" valueField="key" required="false" allowInput="false"
							   data="[ {'key' : 'enable','value' : '有效'},{'key' : 'disable','value' : '废止'}]"
						/>
					</td>
				</tr>

				<tr>
					<td style="width: 14%"><spring:message code="page.standardPublicEdit.name7" />：</td>
					<td style="width: 36%;">
						<input id="publishTimeId" name="publishTime"  class="mini-datepicker" format="yyyy-MM-dd HH:mm:ss" timeFormat="HH:mm:ss" showTime="true" showOkButton="true" showClearButton="false" style="width:98%"/>
					</td>
				</tr>
				<tr>
					<td style="width: 14%"><spring:message code="page.standardPublicEdit.name8" />：</td>
					<td colspan="3">
						<input class="mini-textbox" style="width:70%;float: left" id="fileName" name="fileName" readonly />
						<input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()" accept="application/pdf" />
						<a id="uploadFileBtn" class="mini-button"  style="float: left;margin-left: 10px" onclick="uploadFile"><spring:message code="page.standardPublicEdit.name9" /></a>
						<a id="clearFileBtn" class="mini-button"  style="float: left;margin-left: 10px" onclick="clearUploadFile"><spring:message code="page.standardPublicEdit.name10" /></a>
					</td>
				</tr>
			</table>
		</form>
	</div>

</div>

<script type="text/javascript">
    mini.parse();
    var action="${action}";
    var jsUseCtxPath="${ctxPath}";
    var standardObj=${standardObj};
    var currentUserId="${currentUser.userId}";
    var currentUserName="${currentUser.fullname}";

    var formStandard = new mini.Form("#formStandard");
	//当前体系类别
	var systemCategoryId="";
	//当前是否为大管理员
	var isPointManager="";

    function setData(data) {
        systemCategoryId='JS';
        isPointManager=data.isPointManager;
    }

    function statusRenderer(e) {
        var record = e.record;
        var status = record.standardStatus;

        var arr = [{'key': 'draft', 'value': '草稿', 'css': 'orange'},
            {'key': 'enable', 'value': '有效', 'css': 'green'},
            {'key': 'disable', 'value': '废止', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }
</script>
</body>
</html>
