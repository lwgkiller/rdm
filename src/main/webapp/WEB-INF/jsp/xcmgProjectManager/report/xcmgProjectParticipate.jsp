
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>项目参与情况列表</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/xcmgProjectManager/xcmgProjectParticipate.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
	<div class="searchBox">
		<form id="searchForm" class="search-form" style="margin-bottom: 25px">
			<ul >
				<li style="margin-right: 15px" id="userName">
					<span class="text" style="width:auto"><spring:message code="page.xcmgProjectParticipate.name" />: </span><input class="mini-textbox" name="userName" />
				</li>
				<li style="margin-right: 15px" id="userDepId">
					<span class="text" style="width:auto"><spring:message code="page.xcmgProjectParticipate.name1" />: </span>
					<input name="userDepId" class="mini-dep rxc" plugins="mini-dep"
						   data-options="{'single':'false','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
						   style="width:300px;height:34px"
						   allowinput="false" label="<spring:message code="page.xcmgProjectParticipate.name1" />" textname="bm_name" length="500" maxlength="500" minlen="0" single="true" required="false" initlogindep="false"
						   level="" refconfig="" grouplevel="" groupid="" mwidth="160" wunit="px" mheight="34" hunit="px" />
				</li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.xcmgProjectParticipate.name2" />: </span>
                    <input class="mini-textbox" id="projectName" name="projectName" />
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.xcmgProjectParticipate.name3" />: </span>
                    <input id="projectCategory" name="categoryId" class="mini-combobox" style="width:150px;"
                           textField="categoryName" valueField="categoryId" emptyText="<spring:message code="page.xcmgProjectParticipate.name4" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.xcmgProjectParticipate.name4" />..."/>
                </li>
				<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.xcmgProjectParticipate.name5" />: </span>
					<input id="projectLevel" name="levelId" class="mini-combobox" style="width:150px;"
						   textField="levelName" valueField="levelId" emptyText="<spring:message code="page.xcmgProjectParticipate.name4" />..."
						   required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.xcmgProjectParticipate.name4" />..."/>
				</li>
				<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.xcmgProjectParticipate.name6" />: </span>
					<input id="projectRole" name="roleId" class="mini-combobox" style="width:150px;"
						   textField="roleName" valueField="roleId" emptyText="<spring:message code="page.xcmgProjectParticipate.name4" />..."
						   required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.xcmgProjectParticipate.name4" />..."/>
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto"><spring:message code="page.xcmgProjectParticipate.name7" />: </span>
					<input id="startTime"  name="startTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
				</li>
				<li style="margin-right: 15px">
					<span class="text-to" style="width:auto"><spring:message code="page.xcmgProjectParticipate.name8" /></span>
					<input  id="endTime" name="endTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
				</li>
				<li style="margin-left: 10px">
					<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()"><spring:message code="page.xcmgProjectParticipate.name9" /></a>
					<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()"><spring:message code="page.xcmgProjectParticipate.name10" /></a>
					<a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportProjectParticipate()"><spring:message code="page.xcmgProjectParticipate.name11" /></a>
					<p style="display: inline-block;color: #888;font-size:15px;vertical-align: middle">（<spring:message code="page.xcmgProjectParticipate.name12" />）</p>
				</li>
			</ul>
		</form>
	</div>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="participateListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
		 url="${ctxPath}/xcmgProjectManager/report/xcmgProject/participateList.do?userRoleStr=${userRoleStr}" idField="id"
		 multiSelect="false" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
		<div property="columns">
			<div field="userName"   width="60" headerAlign="center" align="center" allowSort="false"><spring:message code="page.xcmgProjectParticipate.name13" /></div>
			<div field="deptName"   width="80" headerAlign="center" align="center" allowSort="false"><spring:message code="page.xcmgProjectParticipate.name14" /></div>
			<div field="projectName"  width="160" headerAlign="center" align="center" allowSort="false" renderer="onProjectDetail"><spring:message code="page.xcmgProjectParticipate.name2" /></div>
			<div field="categoryName"  sortField="categoryName"  width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.xcmgProjectParticipate.name15" /></div>
			<div field="levelName"   sortField="levelName" width="50" headerAlign="center" align="center" allowSort="true"><spring:message code="page.xcmgProjectParticipate.name5" /></div>
			<div field="mainDepName"    width="50" headerAlign="center" align="center" allowSort="false"><spring:message code="page.xcmgProjectParticipate.name16" /></div>
			<div field="projectStatus"  sortField="projectStatus"  width="60" headerAlign="center" align="center" allowSort="true" renderer="onStatusRenderer"><spring:message code="page.xcmgProjectParticipate.name17" /></div>
			<div field="roleName"   sortField="roleName" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.xcmgProjectParticipate.name6" /></div>
			<div field="createTime" dateFormat="yyyy-MM-dd" align="center" width="80" headerAlign="center" allowSort="false"><spring:message code="page.xcmgProjectParticipate.name18" /></div>
			<div field="knotTime"  dateFormat="yyyy-MM-dd" align="center" width="80" headerAlign="center" allowSort="false"><spring:message code="page.xcmgProjectParticipate.name19" /></div>
		</div>
	</div>
</div>
<form id="excelForm" action="${ctxPath}/xcmgProjectManager/report/xcmgProject/exportParticipateExcel.do?userRoleStr=${userRoleStr}" method="post" target="excelIFrame">
	<input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var participateListGrid=mini.get("#participateListGrid");
    var userRoleStr="${userRoleStr}";
    var isGY="${isGY}";

    function onProjectDetail(e) {
        var record = e.record;
        var projectId = record.projectId;
        var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" onclick="detailProjectRow(\'' + projectId +'\',\''+record.projectStatus+ '\')">'+record.projectName+'</a>';
        return s;
    }
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
</script>
<redxun:gridScript gridId="participateListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>