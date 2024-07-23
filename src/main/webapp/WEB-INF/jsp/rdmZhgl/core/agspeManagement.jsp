<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>高品奖编辑页面</title>
	<%@include file="/commons/edit.jsp"%>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/rdmZhgl/agspeManagement.js?version=${static_res_version}"
			type="text/javascript"></script>
	<script src="${ctxPath}/scripts/standardManager/standardUtil.js?version=${static_res_version}"
			type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar">
	<div>
		<a id="saveStandard" class="mini-button" onclick="saveAgspe('${awardId}')">保存修改</a>
		<a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
	</div>
</div>

<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto">
		<form id="formStandard" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<table class="table-detail" cellspacing="1" cellpadding="0">
				<tr>
					<td style="width: 14%">获奖类别：<span style="color: #ff0000">*</span></td>
						<%--<span style="color:red">*</span></td>--%>
					<td style="width: 36%;min-width:170px">
						<input id="awardType" name="awardType" class="mini-combobox" style="width:98%;"
							   textField="key" valueField="value" emptyText="请选择..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
							   data="[ {'key' : '国家级','value' : 'gjj'},{'key' : '省部级、市级','value' : 'sbj'},{'key' : '其他(不计入画像)','value' : 'other'}]"
						/>
					</td>

					<td style="width: 14%">获奖时间：<span style="color: #ff0000">*</span></td>
					<td style="width: 36%;">
						<input id="prizeTime" name="prizeTime"  class="mini-datepicker" format="yyyy-MM-dd" showOkButton="true" showClearButton="false" style="width:98%"/>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">预计下次高企复审时间：</td>
					<td style="width: 36%;">
						<input id="predictRecheckTime" name="predictRecheckTime"  class="mini-datepicker" format="yyyy-MM-dd" showOkButton="true" showClearButton="false" style="width:98%"/>
					</td>
					<td style="width: 14%">失效时间：</td>
					<td style="width: 36%;">
						<input id="expiryTime" name="expiryTime"  class="mini-datepicker" format="yyyy-MM-dd" showOkButton="true" showClearButton="false" style="width:98%"/>
					</td>

				</tr>
				<tr>

					<td style="width: 14%">下次评审时状态：</td>
					<td style="width: 36%;min-width:170px">
						<input name="nextReviewState" class="mini-combobox" style="width:98%;"
							   textField="key" valueField="value" emptyText="请选择..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
							   data="[ {'key' : '有效','value' : '1'},{'key' : '无效','value' : '0'}]"
						/>
					</td>
					<td style="width: 14%">有效期：</td>
					<td style="width: 36%;min-width:170px">
						<input id="validity" name="validity"  class="mini-textbox" style="width:98%;" />
					</td>

				</tr>
				<tr>
					<td style="width: 14%">荣誉：</td>
					<td colspan="3">
						<input id="honor" name="honor"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">表彰单位：</td>
					<td colspan="3">
						<input id="commendUnit" name="commendUnit"  class="mini-textbox" style="width:98%;" />
					</td>

				</tr>
				<tr>
					<td style="width: 14%">证书编号：<span style="color: #ff0000">*</span></td>
					<td colspan="3">
						<input id="certificateNumber" name="certificateNumber"  class="mini-textbox" style="width:98%;" />
					</td>

				</tr>
				<tr>
					<td style="width: 14%">获奖人：</td>
					<td colspan="3">
						<%--<input id="prizewinner" name="prizewinner"  class="mini-textbox" style="width:98%;" />--%>
						<textarea id="prizewinner" name="prizewinner" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;"
								  label="" datatype="varchar" allowinput="true"
								  emptytext="" mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
					</td>
				</tr>
				<tr>

					<td style="width: 25%">画像积分人员：</td>
					<td colspan="3">
						<input id="portrayalPointPersonId" name="portrayalPointPersonId" textname="portrayalPointPersonName"
							   property="editor" class="mini-user rxc" plugins="mini-user"
							   style="width:98%;height:34px;" allowinput="false" mainfield="no" single="false"/>
					</td>
				</tr>

				<tr>
					<td style="width: 14%">获奖单位：</td>
					<td colspan="3">
						<textarea id="winningUnit" name="winningUnit" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;"
								  label="获奖单位" datatype="varchar" allowinput="true"
								  emptytext="请输入获奖单位..." mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>

					</td>
				</tr>


				<tr>
					<td style="width: 14%">附件：</td>
					<td colspan="3">
						<input class="mini-textbox" style="width:70%;float: left" id="fileName" name="fileName"  readonly />
						<input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()"   />
						<a id="uploadFileBtn" class="mini-button"  style="float: left;margin-left: 10px" onclick="uploadFile()">选择文件</a>
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
    var cpaObj=${cpaObj};

    var formStandard = new mini.Form("#formStandard");
    var currentUserId="${currentUser.userId}";
    var currentUserName="${currentUser.fullname}";

    var cpaGrid = mini.get("formStandard");


    function setData(data) {
        systemCategoryId='JS';
        isPointManager=data.isPointManager;
    }

</script>
</body>
</html>
