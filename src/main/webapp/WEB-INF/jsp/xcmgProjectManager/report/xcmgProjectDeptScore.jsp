
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>部门积分列表</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/xcmgProjectManager/xcmgProjectDeptScore.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
	<div class="searchBox">
		<form id="searchForm" class="search-form" style="margin-bottom: 25px">
			<ul >
				<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.xcmgProjectDeptScore.name" />: </span>
					<input id="userDepId" name="userDepId" class="mini-dep rxc" plugins="mini-dep"
						   data-options="{'single':'false','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
						   style="width:300px;height:34px"
						   allowinput="false" label="<spring:message code="page.xcmgProjectDeptScore.name1" />" textname="bm_name" length="500" maxlength="500" minlen="0" single="false" required="false" initlogindep="false"
						   level="" refconfig="" grouplevel="" groupid="" mwidth="160" wunit="px" mheight="34" hunit="px" />
				</li>
				<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.xcmgProjectDeptScore.name2" />: </span>
					<input id="projectCategory" name="categoryId" class="mini-combobox" style="width:150px;"
						   textField="categoryName" valueField="categoryId" emptyText="<spring:message code="page.xcmgProjectDeptScore.name3" />..."
						   required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.xcmgProjectDeptScore.name3" />..."/>
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto"><spring:message code="page.xcmgProjectDeptScore.name4" /> </span>:
					<input id="startTime"  name="startTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
				</li>
				<li style="margin-right: 15px">
					<span class="text-to" style="width:auto"><spring:message code="page.xcmgProjectDeptScore.name5" />: </span>
					<input id="endTime" name="endTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
				</li>
				<li style="margin-left: 10px">
					<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()"><spring:message code="page.xcmgProjectDeptScore.name6" /></a>
					<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()"><spring:message code="page.xcmgProjectDeptScore.name7" /></a>
					<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="exportBtn()"><spring:message code="page.xcmgProjectDeptScore.name8" /></a>
				</li>
			</ul>
		</form>
		<!--导出Excel相关HTML-->
		<form id="excelForm" action="${ctxPath}/xcmgProjectManager/report/xcmgProject/exportDeptScoreExcel.do" method="post" target="excelIFrame">
			<input type="hidden" name="pageIndex" id="pageIndex"/>
			<input type="hidden" name="pageSize" id="pageSize"/>
			<input type="hidden" name="filter" id="filter"/>
		</form>
		<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
	</div>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="scoreListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
		 url="${ctxPath}/xcmgProjectManager/report/xcmgProject/getDeptScoreList.do" idField="id"
		 multiSelect="false" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
		<div property="columns">
			<div field="deptName"   width="80" headerAlign="center" align="center" allowSort="false"><spring:message code="page.xcmgProjectDeptScore.name9" /></div>
			<div field="userNum"   width="80" headerAlign="center" align="center" allowSort="false"><spring:message code="page.xcmgProjectDeptScore.name10" /></div>
			<div field="totalScore"  width="140" headerAlign="center" align="center" allowSort="true" renderer="onDepScoreRenderer"><spring:message code="page.xcmgProjectDeptScore.name11" /></div>
			<div field="avgScore"    width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.xcmgProjectDeptScore.name12" /></div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var clickDeptId="${clickDeptId}";
    var clickDeptName="${clickDeptName}";
    var jfStartTime="${jfStartTime}";
    var jfEndTime="${jfEndTime}";
    function onDepScoreRenderer(e) {
        var record = e.record;
        var deptId = record.deptId;
        var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" onclick="showUserScores(\'' + deptId + '\')">'+record.totalScore+'</a>';
        return s;
	}
</script>
<redxun:gridScript gridId="scoreListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>