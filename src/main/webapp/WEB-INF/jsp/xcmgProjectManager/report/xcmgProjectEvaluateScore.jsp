
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
<title>项目预估分统计报表</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/xcmgProjectManager/xcmgProjectEvaluateScore.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
	 <div class="mini-toolbar" >
	 	<div class="searchBox">
			<form id="searchForm" class="search-form" style="margin-bottom: 25px">
				<ul style="margin-left: 15px">
					<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.xcmgProjectEvaluateScore.name" />:</span>
						<input id="mainDepId" name="mainDepId" class="mini-dep rxc" plugins="mini-dep"
							   data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
							   style="width:160px;height:34px"
							   allowinput="false" label="<spring:message code="page.xcmgProjectEvaluateScore.name1" />" textname="bm_name" length="500" maxlength="500" minlen="0" single="false" required="false" initlogindep="false"
							   level="" refconfig="" grouplevel="" groupid="" mwidth="160" wunit="px" mheight="34" hunit="px"/>
					</li>
					<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.xcmgProjectEvaluateScore.name2" />: </span><input class="mini-textbox" name="projectName"></li>
					<li style="margin-right: 15px">
						<span class="text" style="width:auto"><spring:message code="page.xcmgProjectEvaluateScore.name3" /> </span>:<input id="projectStartTime"  name="projectStartTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
					</li>
					<li style="margin-right: 15px">
						<span class="text-to" style="width:auto"><spring:message code="page.xcmgProjectEvaluateScore.name4" />: </span><input  id="projectEndTime" name="projectEndTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
					</li>

					<li style="margin-right: 15px">
						<span class="text" style="width:auto"><spring:message code="page.xcmgProjectEvaluateScore.name5" /> </span>:<input id="evaluateScoreStartTime"  name="evaluateScoreStartTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
					</li>
					<li style="margin-right: 15px">
						<span class="text-to" style="width:auto"><spring:message code="page.xcmgProjectEvaluateScore.name4" />: </span><input  id="evaluateScoreEndTime" name="evaluateScoreEndTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
					</li>
					<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.xcmgProjectEvaluateScore.name6" />: </span>
						<input id="projectCategory" name="categoryId" class="mini-combobox" style="width:150px;"
							   textField="categoryName" valueField="categoryId" emptyText="<spring:message code="page.xcmgProjectEvaluateScore.name7" />..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.xcmgProjectEvaluateScore.name7" />..."/>
					</li>
					<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.xcmgProjectEvaluateScore.name8" />: </span>
						<input id="projectLevel" name="levelId" class="mini-combobox" style="width:150px;"
							   textField="levelName" valueField="levelId" emptyText="<spring:message code="page.xcmgProjectEvaluateScore.name7" />..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.xcmgProjectEvaluateScore.name7" />..."/>
					</li>
					<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.xcmgProjectEvaluateScore.name9" />: </span>
						<input id="instStatus" name="status" class="mini-combobox" style="width:150px;"
							   textField="value" valueField="key" emptyText="<spring:message code="page.xcmgProjectEvaluateScore.name7" />..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.xcmgProjectEvaluateScore.name7" />..."
							   data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '运行中'},{'key' : 'SUCCESS_END','value' : '成功结束'},{'key' : 'DISCARD_END','value' : '作废'},{'key' : 'ABORT_END','value' : '异常中止结束'},{'key' : 'PENDING','value' : '挂起'}]"
						/>
					</li>
					<li style="margin-left: 10px">
						<a class="mini-button"  style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.xcmgProjectEvaluateScore.name10" /></a>
						<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()"><spring:message code="page.xcmgProjectEvaluateScore.name11" /></a>
                        <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportEvaluateScore()"><spring:message code="page.xcmgProjectEvaluateScore.name12" /></a>
					</li>
				</ul>
			</form>
			<span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
			</span>
		</div>
     </div>
	<div class="mini-fit" style="height: 100%;">
		<div id="evaluateScoreListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
			url="${ctxPath}/xcmgProjectManager/report/xcmgProject/evaluateScoreList.do" idField="projectId"
			multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
			<div property="columns">
                <div type="indexcolumn"></div>
				<div field="mainDepName" name="mainDepName" width="120" headerAlign="center" align="center" ><spring:message code="page.xcmgProjectEvaluateScore.name" /></div>
				<div field="categoryName" name="categoryName" width="160" headerAlign="center" align="center" ><spring:message code="page.xcmgProjectEvaluateScore.name13" /></div>
				<div field="levelName" name="levelName" width="70" headerAlign="center" align="center" ><spring:message code="page.xcmgProjectEvaluateScore.name14" /></div>
				<div field="standardScore" name="standardScore" width="80" headerAlign="center" align="center" ><spring:message code="page.xcmgProjectEvaluateScore.name15" /></div>
				<div field="projectName"  width="240" headerAlign="center" align="center"  renderer="jumpToDetail"><spring:message code="page.xcmgProjectEvaluateScore.name16" /></div>
				<div field="number"   width="120" headerAlign="center" align="center" ><spring:message code="page.xcmgProjectEvaluateScore.name17" /></div>
				<div field="respMan"   width="120" headerAlign="center" align="center" ><spring:message code="page.xcmgProjectEvaluateScore.name18" /></div>
				<div field="STATUS"  width="80" headerAlign="center" align="center" renderer="onStatusRenderer"><spring:message code="page.xcmgProjectEvaluateScore.name9" /></div>
				<div field="evaluateScore"  align="center" width="80" headerAlign="center" allowSort="false" ><spring:message code="page.xcmgProjectEvaluateScore.name19" /></div>
			</div>
		</div>
	</div>

     <!--导出Excel相关HTML-->
     <form id="excelForm" action="${ctxPath}/xcmgProjectManager/report/xcmgProject/exportEvaluateScoreList.do" method="post" target="excelIFrame">
         <input type="hidden" name="filter" id="filter"/>
     </form>
     <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

	<script type="text/javascript">
		mini.parse();
		var jsUseCtxPath="${ctxPath}";
		var clickDeptId="${clickDeptId}";
        var clickDeptName="${clickDeptName}";
        var projectStartTime="${projectStartTime}";
        var projectEndTime="${projectEndTime}";


        var evaluateScoreListGrid=mini.get("evaluateScoreListGrid");
        evaluateScoreListGrid.on("load", function () {
            evaluateScoreListGrid.mergeColumns(["mainDepName"]);
        });
		//行功能按钮
        function jumpToDetail(e) {
            var record = e.record;
            var projectId = record.projectId;
            var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" onclick="detailProjectRow(\'' + projectId +'\',\''+record.status+ '\')">'+record.projectName+'</a>';
            return s;
        }

        function onStatusRenderer(e) {
            var record = e.record;
            var status = record.STATUS;

            var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
                {'key' : 'RUNNING','value' : '运行中','css' : 'green'},
                {'key' : 'SUCCESS_END','value' : '成功结束','css' : 'blue'},
                {'key' : 'DISCARD_END','value' : '作废','css' : 'red'},
                {'key' : 'ABORT_END','value' : '异常中止结束','css' : 'red'},
                {'key' : 'PENDING','value' : '挂起','css' : 'gray'}
            ];

            return $.formatItemValue(arr,status);
        }

	</script>
	<redxun:gridScript gridId="evaluateScoreListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>