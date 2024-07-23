
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>个人积分列表</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/xcmgProjectManager/xcmgProjectPersonScore.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
	<div class="searchBox">
		<form id="searchForm" class="search-form" style="margin-bottom: 25px">
			<ul >
				<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.xcmgProjectPersonScore.name" />: </span><input class="mini-textbox" name="userName" /></li>
				<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.xcmgProjectPersonScore.name1" />: </span>
					<input name="userDepId" class="mini-dep rxc" plugins="mini-dep"
						   data-options="{'single':'false','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
						   style="width:300px;height:34px"
						   allowinput="false" label="<spring:message code="page.xcmgProjectPersonScore.name2" />" textname="bm_name" length="500" maxlength="500" minlen="0" single="false" required="false" initlogindep="false"
						   level="" refconfig="" grouplevel="" groupid="" mwidth="160" wunit="px" mheight="34" hunit="px" />
				</li>
				<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.xcmgProjectPersonScore.name3" />: </span>
					<input id="projectCategory" name="categoryId" class="mini-combobox" style="width:150px;"
						   textField="categoryName" valueField="categoryId" emptyText="<spring:message code="page.xcmgProjectPersonScore.name4" />..."
						   required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.xcmgProjectPersonScore.name4" />..."/>
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto"><spring:message code="page.xcmgProjectPersonScore.name5" /> </span>:<input id="startTime"  name="startTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
				</li>
				<li style="margin-right: 15px">
					<span class="text-to" style="width:auto"><spring:message code="page.xcmgProjectPersonScore.name6" />: </span><input  id="endTime" name="endTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto"><spring:message code="page.xcmgProjectPersonScore.name7" /> </span>:<input id="projectStartTime"  name="projectStartTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
				</li>
				<li style="margin-right: 15px">
					<span class="text-to" style="width:auto"><spring:message code="page.xcmgProjectPersonScore.name6" />: </span><input  id="projectEndTime" name="projectEndTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
				</li>
				<li style="margin-left: 10px">
					<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()"><spring:message code="page.xcmgProjectPersonScore.name8" /></a>
					<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()"><spring:message code="page.xcmgProjectPersonScore.name9" /></a>
					<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="exportBtn()"><spring:message code="page.xcmgProjectPersonScore.name10" /></a>
				</li>
			</ul>
		</form>
		<!--导出Excel相关HTML-->
		<form id="excelForm" action="${ctxPath}/xcmgProjectManager/report/xcmgProject/exportPersonScoreExcel.do" method="post" target="excelIFrame">
			<input type="hidden" name="pageIndex" id="pageIndex"/>
			<input type="hidden" name="pageSize" id="pageSize"/>
			<input type="hidden" name="filter" id="filter"/>
		</form>
		<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
	</div>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="scoreListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
		 url="${ctxPath}/xcmgProjectManager/report/xcmgProject/getPersonScoreList.do" idField="id"
		 multiSelect="false" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
		<div property="columns">
			<div field="name"   width="80" headerAlign="center" align="center" allowSort="false"><spring:message code="page.xcmgProjectPersonScore.name" /></div>
			<div field="deptName"   width="80" headerAlign="center" align="center" allowSort="false"><spring:message code="page.xcmgProjectPersonScore.name1" /></div>
			<div field="workName"  width="140" headerAlign="center" align="center" allowSort="false"><spring:message code="page.xcmgProjectPersonScore.name11" /></div>
			<div field="dutyName"    width="80" headerAlign="center" align="center" allowSort="false"><spring:message code="page.xcmgProjectPersonScore.name12" /></div>
			<div field="stageScore"  align="center" width="120" headerAlign="center" allowSort="true" renderer="onUserScoreRenderer"><spring:message code="page.xcmgProjectPersonScore.name13" /></div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    function onUserScoreRenderer(e) {
        var record = e.record;
        var userId = record.userId;
        var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" onclick="showProjectScores(\'' + userId + '\')">'+record.stageScore+'</a>';
        return s;
    }
</script>
<redxun:gridScript gridId="scoreListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>