
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
<title>挖掘机械研究院月度预算后台汇总管理</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/xcmgbudget/budgetMonthFlowSum.js?version=${static_res_version}" type="text/javascript"></script>
	<style>
		.mini-grid-rows-view
		{
			background: white !important;
		}
		.mini-grid-cell-inner {
			line-height: 40px !important;
			padding: 0;
		}
		.mini-grid-cell-inner
		{
			font-size:14px !important;
		}
	</style>
</head>
<body>
<div class="mini-toolbar" >
	<div class="searchBox">
		<form id="searchForm" class="search-form" style="margin-bottom: 25px">
			<ul >
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">申报月份: </span>
					<input id="yearMonth" name="yearMonth" class="mini-monthpicker" onfocus="searchBudgetMonth()" style="width:98%;" />
				</li>

				<li style="margin-left: 10px">
					<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchBudgetMonth()">查询</a>
					<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="resetBtn()">清空查询</a>
					<div style="display: inline-block" class="separator"></div>
					<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="submitToEPM()">月度预算提报</a>
				</li>
			</ul>
		</form>
		<span style="color: red">（仅审批完成的预算申请金额纳入月度统计）</span>
	</div>
</div>
	<div class="mini-fit" style="height: 100%;">
		<div id="budgetMonthListSumGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" allowAlternating="false" idField="id"
			 url="${ctxPath}/xcmgBudget/core/budgetMonth/querySumList.do"
			 allowCellWrap="true" autoload="false" showPager="false" multiSelect="false" showColumnsMenu="true" allowCellEdit="false" allowCellSelect="false">
			<div property="columns">
				<div field="action" cellCls="actionIcons" width="50px" headerAlign="center" align="center" renderer="onActionRenderer">金额明细</div>
				<div type="indexcolumn" headerAlign="center" align="center" width="50">序号</div>
				<div field="subjectCode" width="120px" headerAlign="center" align="center" allowSort="false">总账科目</div>
				<div field="subjectName" width="220px" headerAlign="center" align="center" allowSort="false" >科目描述</div>
				<div field="subjectContent" width="300px" headerAlign="center" align="center" allowSort="false" >核算内容</div>
				<div field="zjMonthExpect"  width="120px" align="center" headerAlign="center" allowSort="false">资金预算</div>
				<div field="zjDetail"  width="320px" align="center" headerAlign="center" allowSort="false" visible="false">资金说明</div>
				<div field="fyMonthExpect"  width="120px" align="center" headerAlign="center" allowSort="false" >费用预算</div>
				<div field="fyDetail"  width="320px" align="center" headerAlign="center" allowSort="false" visible="false">费用说明</div>
				<div field="applyNames"  width="200px" align="center" headerAlign="center" allowSort="false" visible="false">申报人</div>
			</div>
		</div>
	</div>

	<script type="text/javascript">
		mini.parse();
		var jsUseCtxPath="${ctxPath}";
        var deptId = "${deptId}";
        var deptName = "${deptName}";
        var bigTypeId = "${bigTypeId}";
        var bigTypeName = "${bigTypeName}";
        <%--var editBudget = ${editBudget};--%>
		var budgetType = "${budgetType}";
		var budgetId = "${budgetId}";
        <%--var isYsglry=${isYsglry};--%>
        var currentUserDeptId="${currentUserDeptId}";
        var currentUserDeptName="${currentUserDeptName}";
        var currentUserNo = "${currentUserNo}";
		var yearMonth="${yearMonth}";
		var epm_url="${epm_url}";

        var budgetMonthListSumGrid=mini.get("budgetMonthListSumGrid");

		budgetMonthListSumGrid.frozenColumns(0, 8);

		//行功能按钮
		function onActionRenderer(e) {
			var record = e.record;
			if(record.type=='totalSum') {
			    return "<span style='font-weight: bold;color: black;'>合计</span>";
			} else{
			    if(!(record.zjMonthExpect == 0 && record.fyMonthExpect ==0)) {
                    return '<span  style="color:#2ca9f6;cursor: pointer" title="查看" onclick="editBudgetMonth(' + JSON.stringify(record).replace(/"/g, '&quot;')+ ')">查看</span>';
                } else {
                    return '<span  style="color: silver" title="查看">查看</span>';
				}
			}
			return '';
		}

		// renderer="onHiddenRenderer"
		//隐藏多余序号
		// function onHiddenRenderer(e) {
		// 	var record = e.record;
		// 	if(record.type=='totalSum') {
		// 		return "";
		// 	}
		// }

		budgetMonthListSumGrid.on("drawcell",function(e){
            var record=e.record;
            if(record.type&&record.type=='totalSum') {
                e.cellStyle="font-weight: bold;";
			}
            // if(record.type&&record.type=='groupSum') {
            //     e.cellStyle="font-weight: bold;";
            //     if(e.field=='subjectName') {
            //         e.cellStyle+="background-color: yellow;";
            //     }
            // }
		});
	</script>
<%--	<redxun:gridScript gridId="budgetMonthListSumGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />--%>
</body>
</html>
