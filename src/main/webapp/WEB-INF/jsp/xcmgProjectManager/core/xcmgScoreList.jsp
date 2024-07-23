
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>积分列表</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/xcmgProjectManager/xcmgScoreList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
	<div class="searchBox">
		<form id="searchForm" class="search-form" style="margin-bottom: 25px">
			<ul >
				<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.xcmgScoreList.name" />: </span><input class="mini-textbox" name="userName" /></li>
				<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.xcmgScoreList.name1" />: </span>
					<input name="userDepId" class="mini-dep rxc" plugins="mini-dep"
						   data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
						   style="width:160px;height:34px"
						   allowinput="false" label="<spring:message code="page.xcmgScoreList.name2" />" textname="bm_name" length="500" maxlength="500" minlen="0" single="false" required="false" initlogindep="false"
						   level="" refconfig="" grouplevel="" groupid="" mwidth="160" wunit="px" mheight="34" hunit="px" />
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto"><spring:message code="page.xcmgScoreList.name3" />: </span>
					<input class="mini-textbox" name="projectName" />
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto"><spring:message code="page.xcmgScoreList.name4" /> </span>:<input id="startTime"  name="startTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
				</li>
				<li style="margin-right: 15px">
					<span class="text-to" style="width:auto"><spring:message code="page.xcmgScoreList.name5" />: </span><input  id="endTime" name="endTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
				</li>

				<li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em><spring:message code="page.xcmgScoreList.name6" /></em>
						<i class="unfoldIcon"></i>
					</span>
				</li>
				<li style="margin-left: 10px">
					<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()"><spring:message code="page.xcmgScoreList.name7" /></a>
					<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()"><spring:message code="page.xcmgScoreList.name8" /></a>
					<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="exportBtn()"><spring:message code="page.xcmgScoreList.name9" /></a>
				</li>
			</ul>
			<div id="moreBox">
				<ul>
					<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.xcmgScoreList.name10" />: </span>
						<input id="projectLevel" name="levelId" class="mini-combobox" style="width:150px;"
							   textField="levelName" valueField="levelId" emptyText="<spring:message code="page.xcmgScoreList.name11" />..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.xcmgScoreList.name11" />..."/>
					</li>
					<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.xcmgScoreList.name12" />: </span>
						<input id="instStatus" name="status" class="mini-combobox" style="width:150px;"
							   textField="value" valueField="key" emptyText="<spring:message code="page.xcmgScoreList.name11" />..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.xcmgScoreList.name11" />..."
							   data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '运行中'},{'key' : 'SUCCESS_END','value' : '成功结束'},{'key' : 'DISCARD_END','value' : '作废'},{'key' : 'ABORT_END','value' : '异常中止结束'},{'key' : 'PENDING','value' : '挂起'}]"
						/>
					</li>
					<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.xcmgScoreList.name13" />: </span>
						<input id="memberRole" name="roleId" class="mini-combobox" style="width:150px;"
							   textField="roleName" valueField="roleId" emptyText="<spring:message code="page.xcmgScoreList.name11" />..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.xcmgScoreList.name11" />..."/>
					</li>
					<li style="margin-right: 15px">
						<span class="text" style="width:auto"><spring:message code="page.xcmgScoreList.name14" />: </span>
						<input class="mini-textbox" name="projectNumber" />
					</li>
				</ul>
			</div>
		</form>
		<!--导出Excel相关HTML-->
		<form id="excelForm" action="${ctxPath}/xcmgProjectManager/core/xcmgScore/exportProjectScoreExcel.do" method="post" target="excelIFrame">
			<input type="hidden" name="pageIndex" id="pageIndex"/>
			<input type="hidden" name="pageSize" id="pageSize"/>
			<input type="hidden" name="filter" id="filter"/>
		</form>
		<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
	</div>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="scoreListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
		 url="${ctxPath}/xcmgProjectManager/core/xcmgScore/getScoreList.do" idField="id"
		 multiSelect="false" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
		<div property="columns">
			<div field="depName"  sortField="depName"  width="80" headerAlign="center" align="center" allowSort="false"><spring:message code="page.xcmgScoreList.name1" /></div>
			<div field="userName"  sortField="userName"  width="80" headerAlign="center" align="center" allowSort="false"><spring:message code="page.xcmgScoreList.name" /></div>
			<div field="number"  width="140" headerAlign="center" align="center" allowSort="false"><spring:message code="page.xcmgScoreList.name14" /></div>
			<div field="projectName"  width="140" headerAlign="center" align="center" allowSort="false" renderer="jumpToDetail"><spring:message code="page.xcmgScoreList.name3" /></div>
			<div field="categoryName"  sortField="categoryName"  width="80" headerAlign="center" align="center" allowSort="false"><spring:message code="page.xcmgScoreList.name15" /></div>
			<div field="levelName"  sortField="levelName"  width="80" headerAlign="center" align="center" allowSort="false"><spring:message code="page.xcmgScoreList.name10" /></div>
			<div field="projectDepName"  sortField="projectDepName"  width="80" headerAlign="center" align="center" allowSort="false"><spring:message code="page.xcmgScoreList.name16" /></div>
			<div field="projectStatus" sortField="projectStatus"  headerAlign="center" align="center" width="60"  allowSort="false" renderer="onStatusRenderer"><spring:message code="page.xcmgScoreList.name12" /></div>
			<div field="roleName" sortField="roleName" align="center" width="120" headerAlign="center" allowSort="false"><spring:message code="page.xcmgScoreList.name13" /></div>
			<div field="userProjectScore" sortField="userProjectScore" align="center" width="120" headerAlign="center" allowSort="false" renderer="onUserProjectScoreRenderer"><spring:message code="page.xcmgScoreList.name17" /></div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var projectLevel=${projectLevel};
    var memberRole=${memberRole};
    var scoreListGrid=mini.get("scoreListGrid");
    var isGY="${isGY}";

    mini.get("#projectLevel").load(projectLevel);
    mini.get("#memberRole").load(memberRole);

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.projectStatus;
        var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
            {'key' : 'RUNNING','value' : '运行中','css' : 'green'},
            {'key' : 'SUCCESS_END','value' : '成功结束','css' : 'blue'},
            {'key' : 'DISCARD_END','value' : '作废','css' : 'red'},
            {'key' : 'ABORT_END','value' : '异常中止结束','css' : 'red'},
            {'key' : 'PENDING','value' : '挂起','css' : 'gray'}
        ];

        return $.formatItemValue(arr,status);
    }

    function onUserProjectScoreRenderer(e) {
        var record = e.record;
        var projectId = record.projectId;
        var userId=record.userId;
        var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" onclick="showStageScores(\'' + projectId +'\',\''+userId+ '\')">'+record.userProjectScore+'</a>';
        return s;
    }
    
    function jumpToDetail(e) {
        var record = e.record;
        var projectId = record.projectId;
        var projectDepName= record.projectDepName;
        var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" onclick="detailProjectRow(\'' + projectId +'\',\''+record.projectStatus+'\',\''+projectDepName+ '\')">'+record.projectName+'</a>';
        return s;
    }
</script>
<redxun:gridScript gridId="scoreListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>