<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>战略规划编辑页面</title>
	<%@include file="/commons/edit.jsp"%>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/xcmgProjectManager/xgzlghEdit.js?version=${static_res_version}"
			type="text/javascript"></script>
	<script src="${ctxPath}/scripts/standardManager/standardUtil.js?version=${static_res_version}"
			type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar">
	<div>
		<a id="saveStandard" class="mini-button" onclick="saveZlgh('${zlghId}','${type}')">保存修改</a>
		<a id="notice" class="mini-button btn-red" onclick="notice()">通知</a>
		<a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
	</div>
</div>

<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto">
		<form id="formZlgh" method="post">
			<input id="zlghId" name="zlghId" class="mini-hidden"/>
			<table class="table-detail" cellspacing="1" cellpadding="0">
				<tr>
					<td style="width: 14%">战略规划名称：<span style="color: #ff0000">*</span></td>
					<td style="width: 36%;min-width:170px">
						<input id="zlghName" name="zlghName"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">主要规划内容：<span style="color: #ff0000">*</span></td>
					<td colspan="3">
						<textarea id="ghnr" name="ghnr" class="mini-textarea rxc"
								  plugins="mini-textarea"
								  style="width:99.1%;height:120px;line-height:25px;"
								  label="" datatype="varchar" allowinput="true"
								  emptytext="请输入内容..." mwidth="80" wunit="%" mheight="300"
								  hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">规划主管部门：<span style="color: #ff0000">*</span></td>
					<td colspan="3">
						<input id="ghzgbmId" name="ghzgbmId" textname="ghzgbmName" class="mini-dep rxc" plugins="mini-dep"
							   style="width:98%;height:34px"
							   allowinput="false" textname="ghzgbmName" length="500" maxlength="500" minlen="0" single="false" initlogindep="false"/>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">规划负责人：<span style="color: #ff0000">*</span></td>
					<td colspan="3">
						<input id="ghfzrId" name="ghfzrId" textname="ghfzrName"
							   property="editor" class="mini-user rxc" plugins="mini-user"
							   style="width:98%;height:34px;" allowinput="false" mainfield="no" single="false"/>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">规划年份：<span style="color: #ff0000">*</span></td>
					<td style="width: 36%;">
						<input id="ghnf" name="ghnf" class="mini-combobox rxc" plugins="mini-combobox"
							   style="width:100%;height:34px"  label="年度："
							   length="50"
							   only_read="false" required="true"  allowinput="false" mwidth="100"
							   wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
							   textField="text" valueField="value" emptyText="请选择..."
							   url="${ctxPath}/sys/core/commonInfo/getNearYearList.do"
							   nullitemtext="请选择..." emptytext="请选择..."/>
					</td>
					<td style="width: 14%">规划完成时间：<span style="color: #ff0000">*</span></td>
					<td style="width: 36%;">
						<input id="ghwcTime" name="ghwcTime"  class="mini-datepicker" format="yyyy-MM-dd" showOkButton="false" showClearButton="false" style="width:98%"/>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">规划版本：<span style="color: #ff0000">*</span></td>
					<td style="width: 30%;">
						<input id="ghbb" name="ghbb"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">规划有效周期开始：<span style="color: #ff0000">*</span></td>
					<td style="width: 36%;">
						<input id="ghyxqs" name="ghyxqs"  class="mini-datepicker" format="yyyy-MM-dd" showOkButton="false" showClearButton="false" style="width:98%"/>
					</td>
					<td style="width: 14%">规划有效周期结束：<span style="color: #ff0000">*</span></td>
					<td style="width: 36%;">
						<input id="ghyxqe" name="ghyxqe"  class="mini-datepicker" format="yyyy-MM-dd" showOkButton="false" showClearButton="false" style="width:98%"/>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">协办部门：</td>
					<td colspan="3">
						<input id="xbbmId" name="xbbmId" textname="xbbmName" class="mini-dep rxc" plugins="mini-dep"
							   style="width:98%;height:34px"
							   allowinput="false" textname="ghzgbmName" length="500" maxlength="500" minlen="0" single="false" initlogindep="false"/>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">协办部门负责人：</td>
					<td colspan="3">
						<input id="xbbmfzrId" name="xbbmfzrId" textname="xbbmfzrName"
							   property="editor" class="mini-user rxc" plugins="mini-user"
							   style="width:98%;height:34px;" allowinput="false" mainfield="no" single="false"/>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">备注：</td>
					<td colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;"
								  label="备注" datatype="varchar" allowinput="true"
								  emptytext="请输入..." mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>

					</td>
				</tr>
			</table>
		</form>
	</div>

</div>

<script type="text/javascript">
    mini.parse();
    var action="${action}";
    var type="${type}";
    var jsUseCtxPath="${ctxPath}";
    var zlghObj=${zlghObj};
    var zlghId="${zlghId}";
    var formZlgh = new mini.Form("#formZlgh");
    var currentUserId="${currentUser.userId}";
    var currentUserName="${currentUser.fullname}";




</script>
</body>
</html>
