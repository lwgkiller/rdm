<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
<title>标准发布日期统计报表</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/standardManager/standardGroupByPublish.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
	 <div class="mini-toolbar" >
	 	<div class="searchBox">
			<form id="searchForm" class="search-form" style="margin-bottom: 25px">
				<ul style="margin-left: 15px">
					<li style="margin-right: 15px">
						<span class="text" style="width:auto"><spring:message code="page.standardGroupByPublish.name1" />：</span>
						<input id="systemCategoryId" name="systemCategoryId" class="mini-combobox" style="width:150px;font-size: 15px;"
							   textfield="systemCategoryName" valuefield="systemCategoryId"
							   required="false" allowInput="false" showNullItem="false" onvaluechanged="searchFrm()"
							   />
					</li>
					<li style="margin-right: 15px">
						<span class="text" style="width:auto"><spring:message code="page.standardGroupByPublish.name2" />：</span>
						<input id="publishTimeFrom" name="publishTimeFrom" class="mini-combobox" style="width:105px;"
							   textField="key" valueField="value"
							   required="false" allowInput="false" showNullItem="false"/>
					</li>
					<li style="margin-right: 15px">
						<span class="text-to" style="width:auto"><spring:message code="page.standardGroupByPublish.name3" /></span>
						<input id="publishTimeTo" name="publishTimeTo" class="mini-combobox" style="width:105px;"
							   textField="key" valueField="value"
							   required="false" allowInput="false" showNullItem="false"/>
					</li>
					<li style="margin-right: 15px">
						<span class="text" style="width:auto"><spring:message code="page.standardGroupByPublish.name4" />：</span>
						<input id="status" name="standardStatus" class="mini-combobox" style="width:150px;"
							   textField="value" valueField="key" emptyText="<spring:message code="page.standardGroupByPublish.name11" />..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.standardGroupByPublish.name11" />..."  onvaluechanged="searchFrm()"
							   data="[ {'key' : 'enable','value' : '有效'},{'key' : 'disable','value' : '已废止'},{'key' : 'draft','value' : '草稿'}]"
						/>
					</li>

					<li class="liBtn">
						<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
							<em><spring:message code="page.standardGroupByPublish.name5" /></em>
							<i class="unfoldIcon"></i>
						</span>
					</li>

					<li style="margin-left: 10px">
						<a class="mini-button"  style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.standardGroupByPublish.name6" /></a>
						<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearGroupByPublishSearch()"><spring:message code="page.standardGroupByPublish.name7" /></a>
						<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="exportPublishReport()"><spring:message code="page.standardGroupByPublish.name8" /></a>
					</li>
				</ul>
				<div id="moreBox">
					<ul>
						<li style="margin-left: 15px;margin-right: 15px">
							<span class="text" style="width:auto"><spring:message code="page.standardGroupByPublish.name9" />：</span>
							<input id="selectedSystemIds" name="selectedSystemIds" class="mini-textbox" style="display: none" />
							<input id="systemTreeSelect" style="width:98%;" class="mini-buttonedit" textname="systemName" allowInput="false" onbuttonclick="selectSystem()"/>
						</li>

						<li style="margin-right: 15px">
							<span class="text" style="width:auto"><spring:message code="page.standardGroupByPublish.name10" />：</span>
							<input id="category" name="standardCategoryId" class="mini-combobox" style="width:150px;"
								   textField="categoryName" valueField="id" emptyText="<spring:message code="page.standardGroupByPublish.name11" />..."
								   required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.standardGroupByPublish.name11" />..."/>
						</li>

						<li style="margin-right: 15px">
							<span class="text" style="width:auto"><spring:message code="page.standardGroupByPublish.name12" />：</span>
							<input id="belongDep" name="belongDepId" class="mini-combobox" style="width:150px;"
								   textField="belongDepName" valueField="id" emptyText="<spring:message code="page.standardGroupByPublish.name11" />..."
								   required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.standardGroupByPublish.name11" />..."/>
						</li>
					</ul>
				</div>
			</form>
		</div>
     </div>
	<div class="mini-fit" style="height: 100%;">
		<div id="standardPublishGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
			url="${ctxPath}/standardManager/report/standard/groupByPublishQuery.do" idField="id"
			multiSelect="false" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
			<div property="columns">
				<div type="indexColumn" headerAlign="center" ><spring:message code="page.standardGroupByPublish.name13" /></div>
				<div field="publishY" name="publishY" width="80" headerAlign="center" align="center" ><spring:message code="page.standardGroupByPublish.name2" /></div>
				<div field="systemName" width="120" headerAlign="center" align="center" ><spring:message code="page.standardGroupByPublish.name9" /></div>
				<div field="systemNumber" width="110" headerAlign="center" align="center" ><spring:message code="page.standardGroupByPublish.name14" /></div>
				<div field="standardNumber"  width="120" headerAlign="center" align="center" ><spring:message code="page.standardGroupByPublish.name15" /></div>
				<div field="categoryName" width="90" headerAlign="center" align="center" ><spring:message code="page.standardGroupByPublish.name10" /></div>
				<div field="standardName"   width="160" headerAlign="center" align="left" ><spring:message code="page.standardGroupByPublish.name16" /></div>
				<div field="standardStatus"   width="70" headerAlign="center" align="center" renderer="statusRenderer"><spring:message code="page.standardGroupByPublish.name4" /></div>
				<div field="belongDepName"  width="80" headerAlign="center" align="center"   width="60"  ><spring:message code="page.standardGroupByPublish.name12" /></div>
			</div>
		</div>
	</div>


	 <div id="selectSystemWindow" title="<spring:message code="page.standardGroupByPublish.name17" />" class="mini-window" style="width:750px;height:450px;"
		  showModal="true" showFooter="true" allowResize="true">
		 <div class="mini-toolbar" style="text-align:center;line-height:30px;"
			  borderStyle="border-left:0;border-top:0;border-right:0;">
			 <span style="font-size: 14px;color: #777"><spring:message code="page.standardGroupByPublish.name18" />: </span>
			 <input class="mini-textbox" width="130" id="filterNameId" onenter="filterSystemTree()"/>
			 <a class="mini-button" iconCls="icon-search" plain="true" onclick="filterSystemTree()"></a>
		 </div>
		 <div class="mini-fit">
			 <ul id="selectSystemTree" class="mini-tree" style="width:100%;height:100%;"
				 showTreeIcon="true" textField="systemName" idField="id" expandOnLoad="0" parentField="parentId" resultAsTree="false">
			 </ul>
		 </div>
		 <div property="footer" style="padding:5px;height: 40px">
			 <table style="width:100%;height: 100%">
				 <tr>
					 <td style="width:120px;text-align:center;">
						 <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.standardGroupByPublish.name19" />" onclick="okWindow()"/>
						 <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.standardGroupByPublish.name20" />" onclick="hideWindow()"/>
					 </td>
				 </tr>
			 </table>
		 </div>
	 </div>

	 <!--导出Excel相关HTML-->
	 <form id="excelForm" action="${ctxPath}/standardManager/report/standard/exportGroupByPublish.do" method="post" target="excelIFrame">
		 <input type="hidden" name="filter" id="filter"/>
	 </form>
	 <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

	<script type="text/javascript">
		mini.parse();
		var jsUseCtxPath="${ctxPath}";
        var grid=mini.get("standardPublishGrid");
        var selectSystemTree=mini.get("selectSystemTree");
        var selectSystemWindow=mini.get("selectSystemWindow");
		var systemCategoryValue="${systemCategoryValue}";
        //必须为要合并的列增加name属性
        grid.on("load", function () {
            grid.mergeColumns(["publishY"]);
        });

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