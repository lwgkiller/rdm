<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
<title>挖掘机械研究院所长月度绩效列表查看</title>
	<%@include file="/commons/list.jsp"%>
</head>
<body>
	 <div class="mini-toolbar">
	 	<div class="searchBox">
			<form id="searchForm" class="search-form" style="margin-bottom: 25px">
				<ul >
					<li style="margin-right: 15px">
						<span class="text" style="width:auto">考核期间: </span>
						<input id="yearMonth" name="yearMonth" class="mini-monthpicker" onfocus="this.blur()" style="width:98%;" />
					</li>
					<li style="margin-right: 15px"><span class="text" style="width:auto">被考核人: </span>
						<input id="bkhUserId" name="bkhUserId" textname="bkhUserName" class="mini-user rxc"
							   plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="" length="50" maxlength="50"
							   mainfield="no"  single="true" />
					</li>
					<li style="margin-right: 15px"><span class="text" style="width:auto">流程状态: </span>
						<input id="instStatus" name="instStatus" class="mini-combobox" style="width:150px;"
							   textField="value" valueField="key" emptyText="请选择..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
							   data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '运行中'},{'key' : 'SUCCESS_END','value' : '成功结束'},{'key' : 'DISCARD_END','value' : '作废'}]"
						/>
					</li>
					<li style="margin-left: 10px">
						<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
						<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
						<div style="display: inline-block" class="separator"></div>
						<a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportKpiLeaderReport()">导出</a>

					</li>
				</ul>
			</form>
		</div>
     </div>
	<div class="mini-fit" style="height: 100%;">
		<div id="kpiLeaderFlowListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" idField="id"
			url="${ctxPath}/kpiLeader/core/queryListReport.do"
			multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
			<div property="columns">
				<div type="checkcolumn" width="20"></div>
				<div name="action" cellCls="actionIcons" width="70" headerAlign="center" align="center" renderer="onActionRenderer">操作</div>
				<div field="yearMonth" width="80" headerAlign="center" align="center" allowSort="false" >考核期间</div>
				<div field="type" width="80" headerAlign="center" align="center" allowSort="false" renderer="onTypeRenderer">考核类型</div>
				<div field="post" width="100" headerAlign="center" align="center" allowSort="false" >岗位</div>
				<div field="bkhUserName" width="70" headerAlign="center" align="center" allowSort="false" >被考核人</div>
				<div field="scoreTotal" width="70" headerAlign="center" align="center" allowSort="false" >考核总分</div>
				<div field="CREATE_TIME_" dateFormat="yyyy-MM-dd" align="center" headerAlign="center" >创建时间</div>
				<div field="taskName" align="center" headerAlign="center"  allowSort="false">当前任务</div>
				<div field="allTaskUserNames"  width="80" align="center" headerAlign="center" allowSort="false">当前处理人</div>
				<div field="instStatus" sortField="instStatus" align="center" headerAlign="center"   width="60"  allowSort="true" renderer="onStatusRenderer">流程状态</div>
			</div>
		</div>
	</div>

	 <!--导出Excel相关HTML-->
	 <form id="excelForm" action="${ctxPath}/kpiLeader/core/exportKpiLeaderReport.do" method="post" target="excelIFrame">
		 <input type="hidden" name="filter" id="filter"/>
		 <input type="hidden" name="month" id="month"/>
	 </form>
	 <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

	<script type="text/javascript">
		mini.parse();
		var jsUseCtxPath="${ctxPath}";
        var currentUserNo="${currentUserNo}";
        var currentUserId="${currentUserId}";
		var kpiLeaderFlowListGrid=mini.get("kpiLeaderFlowListGrid");
		var yearMonth="${yearMonth}";

		$(function () {
			mini.get("yearMonth").setText(yearMonth);
			mini.get("yearMonth").setValue(yearMonth);
			searchFrm();
		});

		//行功能按钮
		function onActionRenderer(e) {
			var record = e.record;
			var id = record.id;
			var instId=record.instId;
			var instStatus='';
			if(record.instStatus) {
				instStatus=record.instStatus;
			}
			if(instStatus=='NO_CREAT') {
				return "";
			}
			var s = '<span title="查看" onclick="detailKpiLeaderMonthRow(\'' + id +'\')">查看</span>';
			s+='<span title="编辑" onclick="editKpiLeaderMonthRow(\'' +id+ '\')">编辑</span>';
			return s;
		}


		function onStatusRenderer(e) {
            var record = e.record;
            var instStatus = record.instStatus;
            var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
                {'key' : 'RUNNING','value' : '运行中','css' : 'green'},
                {'key' : 'SUCCESS_END','value' : '成功结束','css' : 'blue'},
                {'key' : 'DISCARD_END','value' : '作废','css' : 'red'},
                {'key' : 'NO_CREAT','value' : '未创建','css' : 'red'},
                {'key' : 'ABORT_END','value' : '异常中止结束','css' : 'red'},
                {'key' : 'PENDING','value' : '挂起','css' : 'gray'}
            ];
            return $.formatItemValue(arr,instStatus);
        }

		//明细（直接跳转到详情的业务controller）
		function detailKpiLeaderMonthRow(id) {
			var action = "detail";
			var url = jsUseCtxPath + "/kpiLeader/core/editMonthPageReport.do?action=" + action + "&id=" + id;
			var winObj = window.open(url);
			var loop = setInterval(function () {
				if(!winObj) {
					clearInterval(loop);
				} else if (winObj.closed) {
					clearInterval(loop);
					if (kpiLeaderFlowListGrid) {
						kpiLeaderFlowListGrid.reload();
					}
				}
			}, 1000);
		}

		//编辑
		//编辑行数据流程（后台根据配置的表单进行跳转）
		function editKpiLeaderMonthRow(id) {
			var action = "edit";
			var url = jsUseCtxPath + "/kpiLeader/core/editMonthPageReport.do?action=" + action + "&id=" + id;
			var winObj = window.open(url);
			var loop = setInterval(function () {
				if(!winObj) {
					clearInterval(loop);
				} else if (winObj.closed) {
					clearInterval(loop);
					if (kpiLeaderFlowListGrid) {
						kpiLeaderFlowListGrid.reload();
					}
				}
			}, 1000);
		}
		function exportKpiLeaderReport() {
			var yearMonth = mini.get('yearMonth').getText();
			if (yearMonth == null || yearMonth == '') {
				mini.alert("请选择要导出的月份！");
				return;
			}

			var parent = $(".search-form");
			var inputAry = $("input", parent);
			var params = [];
			inputAry.each(function (i) {
				var el = $(this);
				var obj = {};
				obj.name = el.attr("name");
				if (!obj.name) return true;
				obj.value = el.val();
				params.push(obj);
			});

			$("#filter").val(mini.encode(params));
			$("#month").val(yearMonth);
			var excelForm = $("#excelForm");
			excelForm.submit();
		}

	</script>
	 <redxun:gridScript gridId="kpiLeaderFlowListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>
