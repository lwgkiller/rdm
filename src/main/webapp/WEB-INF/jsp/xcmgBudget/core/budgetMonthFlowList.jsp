<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
<title>挖掘机械研究院月度预算申报列表管理</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/xcmgbudget/budgetMonthFlowList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
	 <div class="mini-toolbar" >
	 	<div class="searchBox">
			<form id="searchForm" class="search-form" style="margin-bottom: 25px">
				<ul >
					<li style="margin-right: 15px">
						<span class="text" style="width:auto">申报月份: </span>
						<input id="flowYearMonth" name="flowYearMonth" class="mini-monthpicker" onfocus="this.blur()" style="width:98%;" />
					</li>

					<li id="deptLi" style="margin-right: 15px"><span class="text" style="width:auto">申报部门:</span>
						<input id="flowDeptId" name="flowDeptId" class="mini-dep rxc" plugins="mini-dep"
							   data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
							   style="width:160px;height:34px" allowinput="false" label="部门" textname="flowDeptName" length="500" maxlength="500" minlen="0" single="true" required="false" initlogindep="false"
							   mwidth="160" wunit="px" mheight="34" hunit="px"/>
					</li>
					<li style="margin-right: 15px"><span class="text" style="width:auto">申报人: </span>
						<input id="applyUserId" name="applyUserId" textname="applyUserName" class="mini-user rxc"
							   plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="" length="50" maxlength="50"
							   mainfield="no"  single="true" />
					</li>
					<li style="margin-right: 15px"><span class="text" style="width:auto">申报科目: </span>
						<input id="applySubject" name="applySubject" class="mini-combobox" style="width:98%;"
							   textField="value" valueField="key" emptyText="请选择..."
							   required="true" allowInput="false" showNullItem="true" nullItemText="请选择..."
							   data="[
                                           {'key' : '职工福利费','value' : '职工福利费'},{'key' : '差旅费','value' : '差旅费'},
                                           {'key' : '修理费','value' : '修理费'},{'key' : '物料消耗','value' : '物料消耗'},
                                           {'key' : '低值易耗品摊销','value' : '低值易耗品摊销'},{'key' : '新产品试制开发费','value' : '新产品试制开发费'},
                                           {'key' : '样机成本','value' : '样机成本'},{'key' : '科研开发费','value' : '科研开发费'},
                                           {'key' : '试验检测费','value' : '试验检测费'},{'key' : '研发资料费','value' : '研发资料费'},
                                           {'key' : '办公费-邮寄费','value' : '办公费-邮寄费'},{'key' : '出国费','value' : '出国费'},
                                           {'key' : '技术咨询费','value' : '技术咨询费'},{'key' : '专利标准费','value' : '专利标准费'},
                                           {'key' : '试验工装费','value' : '试验工装费'},{'key' : '固定资产','value' : '固定资产'}
                                           ]"
						/>
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
						<a class="mini-button" style="margin-right: 5px" plain="true" onclick="addBudgetMonthFlow()">新增</a>
						<a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="rmBudgetMonthFlow()">删除</a>
						<a id="discardB" class="mini-button btn-red" style="margin-right: 5px;visibility: hidden" plain="true" onclick="discardBudgetMonthFlow()">作废</a>
					</li>
				</ul>
			</form>
		</div>
     </div>
	<div class="mini-fit" style="height: 100%;">
		<div id="budgetMonthFlowListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" idField="budgetId"
			 url="${ctxPath}/xcmgBudget/core/budgetMonthFlow/queryList.do"
			multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
			<div property="columns">
				<div type="checkcolumn" width="20"></div>
				<div name="action" cellCls="actionIcons" width="70" headerAlign="center" align="center" renderer="onActionRenderer">操作</div>
				<div field="yearMonth" width="80" headerAlign="center" align="center" allowSort="false" >申报月份</div>
				<div field="budgetType" width="80" headerAlign="center" align="center" allowSort="false" renderer="onTypeRenderer">项目类型</div>
				<div field="deptName" width="100" headerAlign="center" align="center" allowSort="false" >申报部门</div>
				<div field="creator" width="70" headerAlign="center" align="center" allowSort="false" >申报人</div>
				<div field="subjectNames" width="70" headerAlign="center" align="center" allowSort="false" >申报科目</div>
				<div field="CREATE_TIME_" dateFormat="yyyy-MM-dd" align="center" headerAlign="center" >创建时间</div>
				<div field="currentProcessTask" align="center" headerAlign="center"  allowSort="false">当前任务</div>
				<div field="currentProcessUser"  width="80" align="center" headerAlign="center" allowSort="false">当前处理人</div>
				<div field="budgetNumber" width="50" align="center" headerAlign="center" allowSort="false" renderer="numberStatusRenderer">预算（报销）</div>
				<div field="instStatus" sortField="instStatus" align="center" headerAlign="center"   width="60"  allowSort="true" renderer="onStatusRenderer">流程状态</div>

			</div>
		</div>
	</div>


	<script type="text/javascript">
		mini.parse();
		var jsUseCtxPath="${ctxPath}";
		var currentMonth="${currentMonth}";
        var currentUserNo="${currentUserNo}";
        var currentUserId="${currentUserId}";
        var currentUserRoles=${currentUserRoles};
		var lastMonth="${lastMonth}";
        var isYsglry = ${isYsglry};
		var budgetMonthFlowListGrid=mini.get("budgetMonthFlowListGrid");
		//行功能按钮
		function onActionRenderer(e) {
			var record = e.record;
			var budgetId = record.budgetId;
			var instId=record.instId;
			var currentProcessUserId=record.currentProcessUserId;
			var instStatus='';
			if(record.instStatus) {
			    instStatus=record.instStatus;
			}
			if(instStatus=='NO_CREAT') {
			    return "";
			}
            var s = '<span title="查看" onclick="detailBudgetMonthRow(\'' + budgetId +'\',\''+instStatus+ '\')">查看</span>';
			if(record.instStatus=='DRAFTED' && currentUserId==record.CREATE_BY_) {
			    s+='<span title="编辑" onclick="editBudgetMonthRow(\'' +instId+ '\')">编辑</span>';
			} else {
                if(currentProcessUserId) {
                    var processUserIdArr=currentProcessUserId.split(',');
                    if(processUserIdArr.indexOf(currentUserId)!=-1) {
                        s+='<span title="办理" onclick="doBudgetMonthTask(\'' + record.taskId + '\')">办理</span>';
					}
                }
			}
			//预算管理人员修改归档流程预算信息
			if (isYsglry && instStatus == 'SUCCESS_END'){
				s+='<span title="修改" onclick="updateBudgetMonthRow(\'' + budgetId +'\',\''+instStatus+ '\')">修改</span>';
			}
			//预算申请人员报销提报
			if ((instStatus == 'SUCCESS_END')&& (currentUserId==record.CREATE_BY_||currentUserNo=='admin')){
				s+='<span title="报销" onclick="baoxiao(\'' + budgetId +'\',\''+instStatus+ '\')">报销</span>';
			}
			//无删除权限的按钮变灰色
            if ((instStatus == 'DRAFTED'||instStatus == 'DISCARD_END')&& (currentUserId==record.CREATE_BY_||currentUserNo=='admin' || (currentUserRoles.ysManager && currentUserRoles.ysManager=='yes'))) {
                s+='<span title="删除" onclick="rmBudgetMonthFlow('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
            } else {
                s+='<span title="删除" style="color: silver">删除</span>';
			}
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

		function onTypeRenderer(e) {
			var record = e.record;
			var budgetType = record.budgetType;
			var arr = [ {'key' : 'fxml','value' : '非项目类预算'},
				{'key' : 'xml','value' : '项目类预算'}
			];
			return $.formatItemValue(arr,budgetType);
		}

		function numberStatusRenderer(e) {
			var record = e.record;
			var budgetNumber = record.budgetNumber;
			var processNumber = record.processNumber;
			// var demandDetailId = record.id;
			// var demandId = record.demandId;
			var cellStr=budgetNumber+"("+processNumber+")";
			if(processNumber<budgetNumber || budgetNumber == '0') {
				return '<span style="color: darkred;text-decoration:underline;cursor: pointer;">'+cellStr+'</span>';
			} else {
				return '<span style="color: orange;text-decoration:underline;cursor: pointer;">'+cellStr+'</span>';
			}
		}
	</script>
	 <redxun:gridScript gridId="budgetMonthFlowListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>
