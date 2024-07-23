<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>查看</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
</head>

<body>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;">
		<div id="listGrid" class="mini-datagrid" allowResize="true" style="height:  500px;" sortField="UPDATE_TIME_"
			 sortOrder="desc"
			 url="${ctxPath}/rdmZhgl/core/ndkfjh/plan/listReportData.do?yearMonth=${yearMonth}&&barType=${barType}&&deptName=${deptName}&&userName=${userName}" idField="id" showPager="false" allowCellWrap="false"
			 multiSelect="true" showColumnsMenu="false" sizeList="[15,20,50,100,200]" pageSize="15" allowAlternating="true" autoload="true"
			 pagerButtons="#pagerButtons">
			<div property="columns">
				<div field="planCode" name="planCode" width="80px" headerAlign="center" align="center" allowSort="false">编号</div>
				<div field="productName" width="100px" headerAlign="center" align="center" allowSort="false">项目/产品名称</div>
				<div field="target" name="target" width="200px" headerAlign="center" align="center" allowSort="false">年度目标</div>
				<div field="planSource" width="100px" headerAlign="center"  renderer="onPalnSorce" align="center">计划来源</div>
				<div field="startEndDate" width="200px" headerAlign="center" align="center" allowSort="false">项目开始结束时间</div>
				<div field="process" name="process" width="240px" headerAlign="center" align="center">当前进度
					<div property="columns" align="center">
						<div field="currentStage" width="100px" headerAlign="center" align="center" allowSort="false">当前阶段</div>
						<div field="stageFinishDate" width="160px" headerAlign="center" align="center" allowSort="false">当前阶段要求完成时间</div>
						<div field="finishRate" width="80px" headerAlign="center" align="center" allowSort="false">完成率</div>
						<div field="isDelay" width="80px" headerAlign="center" align="center" renderer="onDelay" allowSort="false">是否延期</div>
						<div field="delayDays" width="80px" headerAlign="center" align="center" allowSort="false">延期天数</div>
						<div field="remark" width="150px" headerAlign="center" align="center" allowSort="false">备注</div>
					</div>
				</div>
				<div field="chargerManName" width="100px" headerAlign="center" align="center" allowSort="false">负责人</div>
				<div field="chargerDeptName" width="100px" headerAlign="center" align="center" allowSort="false">部门</div>
				<div field="managerName" width="100px" headerAlign="center" align="center" renderer="process" allowSort="false">管控人</div>
				<div field="responsorName" width="100px" headerAlign="center" align="center" renderer="process" allowSort="false">责任所长</div>
			</div>
		</div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    let jsUseCtxPath = "${ctxPath}";
	var yesOrNo = getDics("YESORNO");
	var sourceList = getDics("ndkfjh_source");
	var listGrid = mini.get("listGrid");
	$(function () {
		var column = listGrid.getColumn("process");
		var currentDate = new Date();
		/**
		 * 如果是5号之前则展示上个月，5号之后展示本月
		 * */
		var year = currentDate.getFullYear();
		var day = currentDate.getDate();
		var month = currentDate.getMonth();
		var showDate='';
		if(day<5){
			showDate = '('+month+'月5日）';
		}else{
			showDate = '('+(month+1)+'月5日）';
		}
		listGrid.updateColumn(column,{header:'当前进度'+showDate});
	})
	function onPalnSorce(e) {
		var record = e.record;
		var planSource = record.planSource;
		var resultText = '';
		for (var i = 0; i < sourceList.length; i++) {
			if (sourceList[i].key_ == planSource) {
				resultText = sourceList[i].text;
				break
			}
		}
		return resultText;
	}
	function onDelay(e) {
		var record = e.record;
		var isDelay = record.isDelay;
		var resultText = '';
		for (var i = 0; i < yesOrNo.length; i++) {
			if (yesOrNo[i].key_ == isDelay) {
				resultText = yesOrNo[i].text;
				break
			}
		}
		return resultText;
	}
</script>
</body>
</html>
