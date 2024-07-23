
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>编辑</title>
	<%@include file="/commons/edit.jsp"%>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectConfig.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}" type="text/javascript"></script>
	<link href="${ctxPath}/scripts/mini/miniui/themes/icons.css" rel="stylesheet" type="text/css" />
</head>

<body>
<div class="topToolBar">
	<div>
		<a class="mini-button" onclick="saveDelivery()"><spring:message code="page.configDeliveryEdit.name" /></a>
		<a class="mini-button" onclick="CloseWindow()"><spring:message code="page.configDeliveryEdit.name1" /></a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto">
		<form id="formDelivery" method="post">
			<input id="deliveryId" name="deliveryId" class="mini-hidden"/>
			<table class="table-detail" cellspacing="1" cellpadding="0">
				<tr>
					<td style="width: 10%"><spring:message code="page.configDeliveryEdit.name2" />：</td>
					<td style="width: 40%">
						<input name="categoryName" class="mini-textbox" style="width:98%;" readonly/>
					</td>
					<td style="width: 10%"><spring:message code="page.configDeliveryEdit.name3" />：<span style="color: #ff0000">*</span></td>
					<td style="width: 40%">
						<input id="deliveryName" name="deliveryName"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 10%"><spring:message code="page.configDeliveryEdit.name4" />：<span style="color: #ff0000">*</span></td>
					<td style="width: 40%">
						<input id="stageInput" name="stageId" class="mini-combobox" style="width:98%;"
							   textField="stageNameAndPercent" valueField="stageId" emptyText="<spring:message code="page.configDeliveryEdit.name5" />..."
							   allowInput="false" showNullItem="false" onvaluechanged="stageChanged()"/>
					</td>
					<td style="width: 10%"><spring:message code="page.configDeliveryEdit.name6" />：</td>
					<td style="width: 40%">
						<input id="stageNoInput" name="stageNo"  class="mini-textbox" style="width:98%;" readonly/>
					</td>
				</tr>
				<tr>
					<td style="width: 10%"><spring:message code="page.configDeliveryEdit.name7" />：</td>
					<td style="width: 40%">
						<div id="delivery2Levels" name="levelIds" class="mini-checkboxlist" repeatItems="3" repeatLayout="table"
							 textField="levelName" valueField="levelId">
						</div>
					</td>
					<td style="width: 10%"><span style="font-weight:bold;color: red"><spring:message code="page.configDeliveryEdit.name8" /></span><spring:message code="page.configDeliveryEdit.name9" />：</td>
					<td style="width: 40%">
						<div id="delivery2Sources" name="excludeSourceIds" class="mini-checkboxlist" repeatItems="3" repeatLayout="table"
							 textField="sourceName" valueField="sourceId">
						</div>
					</td>
				</tr>
				<tr>
					<td style="width: 10%"><spring:message code="page.configDeliveryEdit.name10" />：</td>
					<td style="width: 40%" colspan="3">
						<input id="fileType"  name="fileType" class="mini-combobox" style="width:98%;" required="false"
								textField="text" valueField="id" emptyText="<spring:message code="page.configDeliveryEdit.name5" />..." url="${ctxPath}/xcmgProjectManager/core/config/bpmSolutionList.do"
								allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.configDeliveryEdit.name5" />..." />

					</td>
				</tr>
                <tr>
                    <td style="width: 10%"><span style="font-weight:bold;color: red"><spring:message code="page.configDeliveryEdit.name11" /></span><spring:message code="page.configDeliveryEdit.name12" />：</td>
                    <td>
                        <input id="fromPdm" name="fromPdm" class="mini-radiobuttonlist" style="width:100%;"  repeatItems="2" repeatLayout="table"
                               repeatDirection="horizontal" textfield="value" valuefield="key"
                               data="[{'key' : 'yes','value' : '是'},{'key' : 'no','value' : '否'}]"/>
                    </td>
                </tr>
			</table>
		</form>
	</div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var deliveryId="${deliveryId}";
    var deliveryObj=${deliveryObj};
    var stageInfos=${stageInfos};
    var sourceInfos=${sourceInfos};
    var levelInfos=${levelInfos};

    var formDelivery = new mini.Form("#formDelivery");
	mini.get("#stageInput").load(stageInfos);
    mini.get("#delivery2Levels").load(levelInfos);
    mini.get("#delivery2Sources").load(sourceInfos);
    formDelivery.setData(deliveryObj);


</script>
</body>
</html>