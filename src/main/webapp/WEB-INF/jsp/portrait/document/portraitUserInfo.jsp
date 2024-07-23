<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>用户详情信息</title>
	<%@include file="/commons/edit.jsp" %>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
</head>
<body>
<div id="ToolBar" class="topToolBar" style="display: block">
	<div>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin:0 auto; width: 100%;">
		<form id="userInfoForm" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
				<caption>
					基本信息
				</caption>
				<tbody>
				<tr class="firstRow displayTr">
					<td align="center"></td>
					<td align="left"></td>
					<td align="center"></td>
					<td align="left"></td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						姓名：
					</td>
					<td align="left">
						<input name="userName" class="mini-textbox rxc" style="width: 100%"  readonly />
					</td>
					<td align="center" style="white-space: nowrap;">
						所在部门：
					</td>
					<td align="left">
						<input name="deptName" class="mini-textbox rxc" style="width: 100%"  readonly />
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1"  style="white-space: nowrap;">
						学历：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input name="education" class="mini-textbox rxc" style="width: 100%"  readonly />
					</td>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						专业：
					</td>
					<td align="left">
						<input name="major" class="mini-textbox rxc" style="width: 100%"  readonly />
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						岗位：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input name="post" class="mini-textbox rxc" style="width: 100%"  readonly />
					</td>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						职级：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input name="duty" class="mini-textbox rxc" style="width: 100%"  readonly />
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						入职时间：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input name="hireDate" class="mini-textbox rxc" style="width: 100%"  readonly />
					</td>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						技术专家：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input name="expert" class="mini-textbox rxc" style="width: 100%"  readonly />
					</td>
				</tr>
				</tbody>
			</table>
		</form>
	</div>
</div>
<script type="text/javascript">
	mini.parse();
	var jsUseCtxPath = "${ctxPath}";
	var applyObj = ${applyObj};
	var userInfoForm = new mini.Form("#userInfoForm");
	var score = 0;
	$(function () {
		userInfoForm.setData(applyObj);
	})
</script>
</body>
</html>
