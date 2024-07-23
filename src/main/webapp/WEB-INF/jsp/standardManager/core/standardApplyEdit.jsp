
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>编辑申请</title>
	<%@include file="/commons/edit.jsp"%>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/standardManager/standardApplyEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto">
		<form id="formStandardApply" method="post">
			<input id="applyId" name="applyId" class="mini-hidden"/>
			<input id="applyCategoryId" name="applyCategoryId" class="mini-hidden"/>
			<input id="applyCategoryName" name="applyCategoryName" class="mini-hidden"/>
			<input id="systemCategoryId" name="systemCategoryId" class="mini-hidden"/>
			<input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
			<table class="table-detail" cellspacing="1" cellpadding="0">
				<tr>
					<td style="width: 10%"><spring:message code="page.standardApplyEdit.name" />：</td>
					<td style="width: 40%;min-width:170px">
						<input id="standardSelectId" style="width:98%;" class="mini-buttonedit" name="standardId"
							   textname="standardName" allowInput="false" onbuttonclick="selectStandard()"/>
					</td>
					<td style="width: 12%"><spring:message code="page.standardApplyEdit.name1" />：</td>
					<td style="min-width:160px">
						<input style="width:98%;" class="mini-textbox" id="standardNumber" name="standardNumber" readonly/>
					</td>
				</tr>
				<tr>
					<td style="width: 10%"><spring:message code="page.standardApplyEdit.name2" />：</td>
					<td style="min-width:170px">
						<input style="width:98%;" class="mini-textbox" id="categoryName" name="categoryName" readonly/>
					</td>
					<td style="width: 12%"><spring:message code="page.standardApplyEdit.name3" />：</td>
					<td style="min-width:160px">
						<input style="width:98%;" class="mini-textbox" id="systemName" name="systemName" readonly/>
					</td>
				</tr>
				<tr>
					<td><spring:message code="page.standardApplyEdit.name4" />：<span style="color: #ff0000">*</span></td>
					<td colspan="3">
						<textarea id="applyReason" name="applyReason" class="mini-textarea rxc" plugins="mini-textarea" style="width:99.1%;height:200px;line-height:25px;"
								  label="<spring:message code="page.standardApplyEdit.name5" />" datatype="varchar" length="500" vtype="length:500" minlen="0" allowinput="true"
								  emptytext="<spring:message code="page.standardApplyEdit.name6" />..." mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<div id="selectStandardWindow" title="<spring:message code="page.standardApplyEdit.name7" />" class="mini-window" style="width:850px;height:600px;"
		 showModal="true" showFooter="true" allowResize="true">
		<div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
			 borderStyle="border-left:0;border-top:0;border-right:0;">
			<span style="font-size: 14px;color: #777"><spring:message code="page.standardApplyEdit.name8" />: </span>
			<input id="standardSystemCategory" class="mini-combobox" style="width:150px;margin-right: 15px"
				   textField="systemCategoryName" valueField="systemCategoryId"
				   required="false" allowInput="false" />
			<span style="font-size: 14px;color: #777"><spring:message code="page.standardApplyEdit.name1" />: </span>
			<input class="mini-textbox" width="130" id="filterNumberId" style="margin-right: 15px"/>
			<a class="mini-button" iconCls="icon-search" plain="true" onclick="searchStandard()"><spring:message code="page.standardApplyEdit.name9" /></a>
		</div>
		<div class="mini-fit">
			<div id="standardListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
				 idField="id" multiSelect="false" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
				 allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/standardManager/core/standard/queryList.do"
				 onrowdblclick="onRowDblClick()"
			>
				<div property="columns">
					<div type="checkcolumn" width="30"></div>
					<div field="systemName" sortField="systemName" width="90" headerAlign="center" align="center"
						 allowSort="true"><spring:message code="page.standardApplyEdit.name10" />
					</div>
					<div field="categoryName" sortField="categoryName" width="60" headerAlign="center" align="center"
						 allowSort="true"><spring:message code="page.standardApplyEdit.name2" />
					</div>
					<div field="standardNumber" sortField="standardNumber" width="140" headerAlign="center" align="center"
						 align="center" allowSort="true"><spring:message code="page.standardApplyEdit.name12" />
					</div>
					<div field="standardName" sortField="standardName" width="180" headerAlign="center" align="center"
						 allowSort="true"><spring:message code="page.standardApplyEdit.name13" />
					</div>
					<div field="belongDepName" sortField="belongDepName" width="80" headerAlign="center" align="center"
						 allowSort="true"><spring:message code="page.standardApplyEdit.name14" />
					</div>
					<div field="standardStatus" sortField="standardStatus" width="60" headerAlign="center" align="center"
						 allowSort="true" renderer="statusRenderer"><spring:message code="page.standardApplyEdit.name15" />
					</div>
				</div>
			</div>
		</div>
		<div property="footer" style="padding:5px;height: 35px">
			<table style="width:100%;height: 100%">
				<tr>
					<td style="width:120px;text-align:center;">
						<input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.standardApplyEdit.name16" />" onclick="okWindow()"/>
						<input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.standardApplyEdit.name17" />" onclick="hideWindow()"/>
					</td>
				</tr>
			</table>
		</div>
	</div>
</div>


<script type="text/javascript">
    mini.parse();
    var nodeVarsStr='${nodeVars}';
    var action="${action}";
    var jsUseCtxPath="${ctxPath}";
    var standardApplyJsonObj=${standardApplyJsonObj};

    var formStandardApply = new mini.Form("#formStandardApply");
    var standardListGrid=mini.get("standardListGrid");
    var selectStandardWindow=mini.get("selectStandardWindow");

    formStandardApply.setData(standardApplyJsonObj);

    function statusRenderer(e) {
        var record = e.record;
        var status = record.standardStatus;

        var arr = [{'key': 'draft', 'value': '草稿', 'css': 'orange'},
            {'key': 'enable', 'value': '有效', 'css': 'green'},
            {'key': 'disable', 'value': '已废止', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }

</script>
</body>
</html>