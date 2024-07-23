<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>农机鉴定用户编辑</title>
	<%@include file="/commons/edit.jsp"%>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/rdmZhgl/userPublicEdit.js?version=${static_res_version}"
			type="text/javascript"></script>
	<script src="${ctxPath}/scripts/standardManager/standardUtil.js?version=${static_res_version}"
			type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar">
	<div>
		<a id="saveStandard" class="mini-button" onclick="saveStandard('${njjdId}')">保存修改</a>
		<a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
	</div>
</div>

<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto">
		<form id="formStandard" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<table class="table-detail" cellspacing="1" cellpadding="0">
				<tr>
					<td style="width: 14%">用户姓名：</td>
					<td style="width: 36%;min-width:170px">
						<input id="userName" name="userName"  class="mini-textbox" style="width:98%;" />
					</td>

					<td style="width: 14%">用户详细地址：</td>
					<td style="width: 36%;min-width:140px">
						<input id="address" name="address"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">联系电话：</td>
					<td style="width: 36%;min-width:170px">
						<input id="phone" name="phone"  class="mini-textbox" style="width:98%;" />
					</td>

					<td style="width: 14%">购买日期：</td>
					<td style="width: 36%;">
						<input id="buyTime" name="buyTime"  class="mini-datepicker" format="yyyy-MM-dd" showTime="false" showOkButton="false" showClearButton="true" style="width:98%"/>
					</td>
				</tr>

				<tr>
					<td style="width: 14%">全文附件：</td>
					<td colspan="3">
						<input class="mini-textbox" style="width:70%;float: left" id="fileName" name="fileName"  readonly />
						<%--<input id="fileName" name="fileName" class="mini-hidden"/>--%>
						<input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()"   />
						<a id="uploadFileBtn" class="mini-button"  style="float: left;margin-left: 10px" onclick="uploadFile()">选择文件</a>
						<%--<a id="addFile" class="mini-button" onclick="addUserFile()">添加附件</a>--%>
						<a id="clearFileBtn" class="mini-button"  style="float: left;margin-left: 10px" onclick="clearUploadFile">清除</a>
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

    var formStandard = new mini.Form("#formStandard");
    var currentUserId="${currentUser.userId}";
    var currentUserName="${currentUser.fullname}";

    var grid = mini.get("formStandard");
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
