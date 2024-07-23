
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
<title>挖掘机械研究院各所月度预算报表</title>
	<%@include file="/commons/list.jsp"%>
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
				</li>
			</ul>
		</form>
		<span style="color: red">（仅审批完成的预算申请金额纳入月度统计，固定资产不做统计）</span>
	</div>
</div>
	<div class="mini-fit" style="height: 100%;">
		<div id="budgetMonthListSumGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" allowAlternating="false" idField="id"
			 url="${ctxPath}/xcmgBudget/core/budgetMonth/querySumDeptList.do"
			 allowCellWrap="true" autoload="false" showPager="false" multiSelect="false" showColumnsMenu="true" allowCellEdit="false" allowCellSelect="false">
			<div property="columns">
				<div type="indexcolumn" headerAlign="center" align="center" width="50">序号</div>
				<div field="yearMonth" width="120px" headerAlign="center" align="center" allowSort="false">年月</div>
				<div field="deptName" width="220px" headerAlign="center" align="center" allowSort="false" >部门名称</div>
				<div field="zjMonthExpect"  width="120px" align="center" headerAlign="center" allowSort="false">资金预算</div>
				<div field="fyMonthExpect"  width="120px" align="center" headerAlign="center" allowSort="false" >费用预算</div>
				<div field="zjDetail"  width="320px" align="center" headerAlign="center" allowSort="false" visible="false">资金说明</div>
				<div field="fyDetail"  width="320px" align="center" headerAlign="center" allowSort="false" visible="false">费用说明</div>
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
		var budgetType = "${budgetType}";
		var budgetId = "${budgetId}";
        var currentUserDeptId="${currentUserDeptId}";
        var currentUserDeptName="${currentUserDeptName}";
        var currentUserNo = "${currentUserNo}";
		var yearMonth="${yearMonth}";
        var budgetMonthListSumGrid=mini.get("budgetMonthListSumGrid");
		budgetMonthListSumGrid.frozenColumns(0, 8);

		$(function () {
			mini.get("yearMonth").setText(yearMonth);
			mini.get("yearMonth").setValue(yearMonth);
			searchBudgetMonth();
		});

		function searchBudgetMonth() {
			budgetMonthListSumGrid.setData([]);
			var yearMonth = mini.get("yearMonth").getText();
			if(!yearMonth) {
				return;
			}
			$.ajax({
				url: jsUseCtxPath+"/xcmgBudget/core/budgetMonth/querySumDeptList.do?yearMonth="+yearMonth,
				success: function (result) {
					if (!result.success) {
						top._ShowTips({
							msg: result.message
						});
					} else {
						budgetMonthListSumGrid.setData(result.data);
					}
				}
			});
		}

		budgetMonthListSumGrid.on("drawcell",function(e){
            var record=e.record;
            if(record.type&&record.type=='totalSum') {
                e.cellStyle="font-weight: bold;";
			}
		});

		function resetBtn() {
			mini.get("yearMonth").setText();
			budgetMonthListSumGrid.setData([]);
		}
	</script>
</body>
</html>
