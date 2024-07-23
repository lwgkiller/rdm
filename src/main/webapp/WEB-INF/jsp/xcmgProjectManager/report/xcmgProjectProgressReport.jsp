
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
<title>项目进度统计报表</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/xcmgProjectManager/xcmgProjectProgressReport.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
	 <div class="mini-toolbar" >
	 	<div class="searchBox">
			<form id="searchForm" class="search-form" style="margin-bottom: 25px">
				<ul style="margin-left: 15px">
					<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.xcmgProjectProgressReport.name" />:</span>
						<input id="mainDepId" name="mainDepId" class="mini-dep rxc" plugins="mini-dep"
							   data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
							   style="width:160px;height:34px"
							   allowinput="false" label="<spring:message code="page.xcmgProjectProgressReport.name1" />" textname="bm_name" length="500" maxlength="500" minlen="0" single="false" required="false" initlogindep="false"
							   level="" refconfig="" grouplevel="" groupid="" mwidth="160" wunit="px" mheight="34" hunit="px"/>
					</li>
					<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.xcmgProjectProgressReport.name2" />: </span><input class="mini-textbox" name="projectName"></li>
					<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.xcmgProjectProgressReport.name3" />: </span>
						<input id="projectCategory" name="categoryId" class="mini-combobox" style="width:150px;"
							   textField="categoryName" valueField="categoryId" emptyText="<spring:message code="page.xcmgProjectProgressReport.name4" />..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.xcmgProjectProgressReport.name4" />..."/>
					</li>
					<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.xcmgProjectProgressReport.name5" />: </span>
						<input id="projectLevel" name="levelId" class="mini-combobox" style="width:150px;"
							   textField="levelName" valueField="levelId" emptyText="<spring:message code="page.xcmgProjectProgressReport.name4" />..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.xcmgProjectProgressReport.name4" />..."/>
					</li>
					<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.xcmgProjectProgressReport.name6" />: </span>
						<input id="instStatus" name="status" class="mini-combobox" style="width:150px;"
							   textField="value" valueField="key" emptyText="<spring:message code="page.xcmgProjectProgressReport.name4" />..." value="RUNNING"
							   required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.xcmgProjectProgressReport.name4" />..."
							   data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '运行中'},{'key' : 'SUCCESS_END','value' : '成功结束'},{'key' : 'DISCARD_END','value' : '作废'},{'key' : 'ABORT_END','value' : '异常中止结束'},{'key' : 'PENDING','value' : '挂起'}]"
						/>
					</li>
					<li style="margin-right: 15px">
						<span class="text" style="width:auto"><spring:message code="page.xcmgProjectProgressReport.name7" /> </span>:<input id="projectStartTime"  name="projectStartTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
					</li>
					<li style="margin-right: 15px">
						<span class="text-to" style="width:auto"><spring:message code="page.xcmgProjectProgressReport.name8" />: </span><input  id="projectEndTime" name="projectEndTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
					</li>

					<li style="margin-right: 15px">
						<span class="text-to" style="width:auto"><spring:message code="page.xcmgProjectProgressReport.name9" />: </span><input  id="queryEndTime" name="queryEndTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
					</li>
					<li style="margin-left: 10px">
						<a class="mini-button"  style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.xcmgProjectProgressReport.name10" /></a>
						<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()"><spring:message code="page.xcmgProjectProgressReport.name11" /></a>
                        <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportProjectProgress()"><spring:message code="page.xcmgProjectProgressReport.name12" /></a>
					</li>
				</ul>
			</form>
			<span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
			</span>
		</div>
     </div>
	<div class="mini-fit" style="height: 100%;">
		<div id="progressListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
			url="${ctxPath}/xcmgProjectManager/report/xcmgProject/progressReportList.do" idField="projectId"
			multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
			<div property="columns">
                <div type="indexcolumn"></div>
				<div field="mainDepName" name="mainDepName" width="120" headerAlign="center" align="center" ><spring:message code="page.xcmgProjectProgressReport.name" /></div>
				<div field="categoryName" name="categoryName" width="120" headerAlign="center" align="center" ><spring:message code="page.xcmgProjectProgressReport.name13" /></div>
				<div field="levelName" name="levelName" width="100" headerAlign="center" align="center" ><spring:message code="page.xcmgProjectProgressReport.name14" /></div>
				<div field="standardScore" name="standardScore" width="100" headerAlign="center" align="center" ><spring:message code="page.xcmgProjectProgressReport.name15" /></div>
				<div field="projectName"  width="170" headerAlign="center" align="left"  renderer="jumpToDetail"><spring:message code="page.xcmgProjectProgressReport.name16" /></div>
				<div field="number"   width="130" headerAlign="center" align="center" ><spring:message code="page.xcmgProjectProgressReport.name17" /></div>
				<div field="respMan"   width="110" headerAlign="center" align="center" ><spring:message code="page.xcmgProjectProgressReport.name18" /></div>
				<div field="status"  width="60"  headerAlign="center" align="center" renderer="onStatusRenderer"><spring:message code="page.xcmgProjectProgressReport.name6" /></div>
				<div field="currentStageName"  width="120" headerAlign="center" align="center" ><spring:message code="page.xcmgProjectProgressReport.name19" /></div>
				<div field="allTaskUserNames"   width="100" align="center" headerAlign="center" ><spring:message code="page.xcmgProjectProgressReport.name20" /></div>
				<div width="90" align="center" headerAlign="center" renderer="onRiskRenderer"><spring:message code="page.xcmgProjectProgressReport.name21" /></div>
				<div width="120" headerAlign="center" align="center" renderer="onProgressRenderer"><spring:message code="page.xcmgProjectProgressReport.name22" /></div>
				<div field="stageScore"  align="center" width="90" headerAlign="center" allowSort="true"><spring:message code="page.xcmgProjectProgressReport.name23" /></div>
			</div>
		</div>
	</div>

     <!--导出Excel相关HTML-->
     <form id="excelForm" action="${ctxPath}/xcmgProjectManager/report/xcmgProject/exportProjectProgressExcel.do" method="post" target="excelIFrame">
         <input type="hidden" name="filter" id="filter"/>
     </form>
     <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

	<script type="text/javascript">
		mini.parse();
		var jsUseCtxPath="${ctxPath}";
        var clickDeptId="${clickDeptId}";
        var clickDeptName="${clickDeptName}";
        var progressListGrid=mini.get("progressListGrid");

        progressListGrid.on("load", function () {
            progressListGrid.mergeColumns(["mainDepName"]);
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
            var status = record.status;

            var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
                {'key' : 'RUNNING','value' : '运行中','css' : 'green'},
                {'key' : 'SUCCESS_END','value' : '成功结束','css' : 'blue'},
                {'key' : 'DISCARD_END','value' : '作废','css' : 'red'},
                {'key' : 'ABORT_END','value' : '异常中止结束','css' : 'red'},
                {'key' : 'PENDING','value' : '挂起','css' : 'gray'}
            ];

            return $.formatItemValue(arr,status);
        }
        function onProgressRenderer(e) {
            var record = e.record;
            var projectId = record.projectId;
            if(!record.progressNum) {
                record.progressNum=0;
            }
            var s ='<div class="mini-progressbar" id="p1" style="border-width: 0px;width:120px"><div class="mini-progressbar-border"><div class="mini-progressbar-bar" style="width: '+record.progressNum+'%;"></div><div class="mini-progressbar-text" id="p1$text">'+record.progressNum+'%</div></div></div>';
            return s;
        }

        function onRiskRenderer(e) {
            var record = e.record;
            var hasRisk = record.hasRisk;

			var color='#32CD32';
			var title='项目未延误';
			if(hasRisk==1) {
                color='#EEEE00';
                title='项目延误时间未超过5天';
			} else if(hasRisk==2) {
                color='#fb0808';
                title='项目延误时间超过5天';
			}else if(hasRisk==3) {
                color='#cccccc';
                title='项目未启动或已停止';
            }else if (hasRisk == 4) {
				color = '#9B00FCFF'
				title='项目没有计划时间且延误超过30天';
			}

            var s = '<span title= "'+title+'" style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + color + '"></span>';
            return s;
        }

	</script>
	<redxun:gridScript gridId="progressListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>