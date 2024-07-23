
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
<title>挖掘机械研究院月度预算申报管理</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/xcmgbudget/budgetMonthFlowCgTab.js?version=${static_res_version}" type="text/javascript"></script>
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
	<div id="bxStatus" class="mini-toolbar" style="display:none;">
		<li style="float: left;">
			<span class="text" style="width:auto;font-size: 17px">预算审批状态：</span>
			<span id="deptCurrentStatusName" style="width: 180px;text-align:left;font-weight: bold;"></span>
		</li>
	</div>
	<div class="mini-fit" style="height: 100%;">
		<div id="budgetMonthListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" allowAlternating="false" idField="budgetMonthId"
			 allowCellWrap="true" autoload="false" showPager="false" multiSelect="false" showColumnsMenu="true" allowCellEdit="false" allowCellSelect="false">
			<div property="columns">
				<div name="action" cellCls="actionIcons" width="130px" headerAlign="center" align="center" renderer="onActionRenderer">操作</div>
				<div type="indexcolumn" headerAlign="center" align="center" width="50">序号</div>
				<div field="subjectCode" width="120px" headerAlign="center" align="center" allowSort="false" renderer="subjectCodeRender">总账科目</div>
				<div field="subjectName" width="220px" headerAlign="center" align="center" allowSort="false" >科目描述</div>
				<div field="subjectContent" width="300px" headerAlign="center" align="center" allowSort="false" >核算内容</div>
				<div field="glcwddh" name="glcwddh" width="120px" align="center" headerAlign="center" allowSort="false">关联财务订单号</div>
				<div field="zjMonthExpect"  width="150px" align="center" headerAlign="center" allowSort="false">资金预算</div>
				<div field="zjDetail"  width="350px" align="center" headerAlign="center" allowSort="false" >资金说明</div>
				<div field="fyMonthExpect"  width="150px" align="center" headerAlign="center" allowSort="false" >费用预算</div>
				<div field="fyDetail"  width="350px" align="center" headerAlign="center" allowSort="false" >费用说明</div>
			</div>
		</div>
	</div>

	<div id="budgetMonthProcessWindow" title="审核数据编辑" class="mini-window" style="width:1170px;height:620px;"
		 showModal="true" showFooter="false" allowResize="true" showCloseButton="false" >
		<div class="mini-toolbar" >
			<div class="searchBox">
				<form id="searchBxGrid" class="search-form" style="margin-bottom: 25px">
					<ul >
						<li style="margin-right: 15px;margin-left: 15px"><span class="text" style="width:auto">金额类型: </span>
							<input id="moneyStatus" name="moneyStatus" class="mini-combobox" style="width:150px;"
								   textField="value" valueField="key" emptyText="请选择..."
								   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
								   data="[{'key':'zj','value':'付款'},{'key':'fy','value':'挂账'},{'key':'dh','value':'电汇'}]"
							/>
						</li>
						<li style="margin-left: 15px">
							<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchProcessData()">查询</a>
							<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="cleanProcessData()">清空查询</a>
							<div style="display: inline-block" class="separator"></div>
							<a class="mini-button" id="addProcessData" style="margin-right: 5px" plain="true" onclick="addProcessData()">新增</a>
							<a class="mini-button btn-red" id="rmProcessData" style="margin-right: 5px" plain="true" onclick="rmProcessData()">刪除</a>
							<a class="mini-button" id="saveProcessData" style="margin-right: 5px;" plain="true" onclick="saveProcessData()">保存</a>
							<div style="display: inline-block" class="separator"></div>
							<a class="mini-button btn-red" id="closeBxWindow" style="float: right" onclick="closeProcessData()">关闭</a>
						</li>
					</ul>
				</form>
			</div>
		</div>
		<div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 5px"
			 borderStyle="border-left:0;border-top:0;border-right:0;">
			<a style="color: #ff0000">（新增、修改、删除后需点击保存，税后金额超出预算需技术管理部操作）</a>
			<input class="mini-hidden" id="budgetIdFilter" name="budgetId"/>
			<input class="mini-hidden" id="bigTypeIdFilter" name="bigTypeId"/>
			<input class="mini-hidden" id="deptIdFilter" name="deptId"/>
			<input class="mini-hidden" id="yearMonthFilter" name="yearMonth"/>
			<input class="mini-hidden" id="subjectIdFilter" name="subjectId"/>
			<input class="mini-hidden" id="monthZjExpect" name="monthZjExpect"/>
			<input class="mini-hidden" id="monthFyExpect" name="monthFyExpect"/>
		</div>
		<div class="mini-fit" style="width: 100%; height: 85%;">
			<div id="processListGrid" class="mini-datagrid"
				 allowResize="false" showPager="false" allowCellEdit="true" allowCellSelect="true"
				 idField="id" showColumnsMenu="false" allowCellWrap="true" showCellTip="true" allowAlternating="true" multiSelect="true"
				 oncellbeginedit="OnCellBeginEditDetail">
				<div property="columns">
					<div field="id" headerAlign="left" visible="false">id</div>
					<div field="applyName" headerAlign="left" visible="false">申报人</div>
					<div type="checkcolumn" width="20"></div>
					<div field="zjOrFy" width="60"  headerAlign="center" align="center" renderer="onStatusRenderer">金额类型<span style="color: red">*</span>
						<input property="editor" class="mini-combobox" style="width:90%;"
							   textField="text" valueField="value" emptyText="请选择..."
							   allowInput="false" showNullItem="false" nullItemText="请选择..."
							   data="[{'text':'付款','value':'zj'},{'text':'挂账','value':'fy'},{'text':'电汇','value':'dh'}]"
						/>
					</div>
					<div field="moneyNumBeforeRate"  width="80" headerAlign="center" align="center" >税前金额<span style="color: red">*</span>
						<input property="editor" class="mini-textbox" style="width: 100%;"/>
					</div>
					<div field="rate"  width="80" headerAlign="center" align="center" >税率<span style="color: #ff0000">*</span><br/><a style="color: red;font-size:6px">例：(1.06，1.13)</a>
						<input property="editor" class="mini-textbox" style="width: 100%;"/>
					</div>
					<div field="moneyNum"  width="80" headerAlign="center" align="center" >税后金额<span style="color: red">*</span>
						<input property="editor" class="mini-textbox" style="width: 100%;" readonly/>
					</div>
					<div field="moneyDesc" width="170" headerAlign="center" align="left" >金额描述<span style="color: red">*</span>
						<input property="editor" class="mini-textarea" style="width: 100%;"/>
					</div>
					<div field="operateTime" width="100" headerAlign="center" align="center" >操作时间
						<input property="editor" class="mini-textarea" readonly style="width: 100%;"/>
					</div>
				</div>
			</div>
		</div>
	</div>

	<script type="text/javascript">
		mini.parse();
		var jsUseCtxPath="${ctxPath}";
		var yearMonth = "${yearMonth}";
        var deptId = "${deptId}";
        var deptName = "${deptName}";
        var bigTypeId = "${bigTypeId}";
        var bigTypeName = "${bigTypeName}";
        var editBudget = ${editBudget};
		var budgetType = "${budgetType}";
		var budgetId = "${budgetId}";
		var projectId = "${projectId}";
        var isYsglry=${isYsglry};
        var currentUserDeptId="${currentUserDeptId}";
        var currentUserDeptName="${currentUserDeptName}";
        var currentUserNo = "${currentUserNo}";
		var action="${action}";
		var epm_url="${epm_url}";
		var budgetMonthProcessWindow=mini.get("budgetMonthProcessWindow");

        var budgetMonthListGrid=mini.get("budgetMonthListGrid");
        var processListGrid = mini.get("processListGrid");

        budgetMonthListGrid.frozenColumns(0, 4);

		//行功能按钮
		function onActionRenderer(e) {
			var record = e.record;
			var s = '';
			if(record.type=='totalSum') {
				s +='<span style="font-weight: bold;color: black;">合计</span>';
			} else if((!record.type || record.type!='groupSum')){
			    if(editBudget && (!record.hasSon || record.hasSon!='yes')) {
					s += '<span style="color:#2ca9f6;cursor: pointer" title="编辑" onclick="editBudgetMonth(' + JSON.stringify(record).replace(/"/g, '&quot;')+ ',\'edit\')">编辑</span>';
					s += '<span> | </span>';
					s += '<span style="color:#2ca9f6;cursor: pointer" title="复制" onclick="editBudgetMonth(' + JSON.stringify(record).replace(/"/g, '&quot;')+ ',\'copy\')">复制</span>';
					s += '<span> | </span>';
					s += '<span style="color:#2ca9f6;cursor: pointer" title="删除" onclick="editBudgetMonth(' + JSON.stringify(record).replace(/"/g, '&quot;')+ ',\'del\')">删除</span>';
				}
			    else if(action == 'baoxiao'){
					s +='<span style="color:#2ca9f6;cursor: pointer" title="查看" onclick="detailBudgetMonthProcess(\''+record.subjectCode+'\',\''+record.subjectId+'\',\''+record.fyMonthExpect+'\',\''+record.zjMonthExpect+'\')">查看</span>';
					s += '<span> | </span>';
					s +='<span style="color:#2ca9f6;cursor: pointer" title="报销" onclick="showBudgetMonthProcess(\''+record.subjectCode+'\',\''+record.subjectId+'\',\''+record.fyMonthExpect+'\',\''+record.zjMonthExpect+'\')">报销</span>';
				}
			    else {
					s +='<span style="color: silver" title="编辑">编辑</span>';
					s += '<span> | </span>';
					s +='<span style="color: silver" title="复制">复制</span>';
					s += '<span> | </span>';
					s +='<span style="color: silver" title="删除">删除</span>';
				}
			}
			return s;
		}

        budgetMonthListGrid.on("drawcell",function(e){
            var record=e.record;
            if(record.type&&record.type=='totalSum') {
                e.cellStyle="font-weight: bold;";
			}
			if ((record.uid && record.uid != record.cid)||(record.cid && record.cid != record.bid)){
					e.cellStyle+="background-color: yellow;";
			}
		});

		processListGrid.on("cellcommitedit",function(e){
			var grid = e.sender,
					record = e.record;
			var value=e.value;
			if(e.field=='moneyNumBeforeRate' && value) {
				if (!checkIsNumber(value)) {
					grid.updateRow(record, {moneyNumBeforeRate:''});
					mini.alert("税前金额请填写数字！");
				} else {
					if(record.rate && checkIsNumber(record.rate)) {
						var result=(parseFloat(value)/parseFloat(record.rate)).toFixed(2);
						grid.updateRow(record, {moneyNum:result});
					}
				}
			}
			if(e.field=='rate' && value) {
				if (!checkIsNumber(value)) {
					grid.updateRow(record, {rate:''});
					mini.alert("税率请填写数字！");
				}
				else if(value<1 || value>=2){
					grid.updateRow(record, {rate:''});
					mini.alert("税率填写有误！");
				}
				else {
					if(record.moneyNumBeforeRate && checkIsNumber(record.moneyNumBeforeRate)) {
						var result=(parseFloat(record.moneyNumBeforeRate)/parseFloat(value)).toFixed(2);
						grid.updateRow(record, {moneyNum:result});
					}
				}
			}
		});

		function onStatusRenderer(e) {
			var record = e.record;
			var status = record.zjOrFy;
			var arr = [ {'key' : 'zj','value' : '付款'},
				{'key' : 'fy','value' : '挂账'},
				{'key' : 'dh','value' : '电汇'},
			];
			return $.formatItemValue(arr,status);
		}

	</script>
</body>
</html>
