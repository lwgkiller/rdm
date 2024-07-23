<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
<title>挖掘机械研究院所长月度绩效列表管理</title>
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
						<a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="rmKpiLeaderMonthFlow()">删除</a>
						<li id="discard" style="float: right;visibility: hidden">
							<span class="text" style="width:auto">流程发起月份: </span>
							<input id="yearMonthStart" name="yearMonthStart" class="mini-monthpicker" onfocus="this.blur()" style="width:105px;height: 35px"/>
							<a id="createProcessBtn" class="mini-button" style="margin-left: 20px"  plain="true" onclick="createAllProcess()">流程发起</a>
						</li>
					</li>
				</ul>
			</form>
		</div>
     </div>
     <div id="loading" class="loading" style="display:none;text-align:center;"><img src="${ctxPath}/styles/images/loading.gif"></div>
	<div class="mini-fit" style="height: 100%;" id="content">
		<div id="kpiLeaderFlowListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" idField="id"
			url="${ctxPath}/kpiLeader/core/queryList.do"
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
				<div field="taskName" headerAlign='center' align='center' width="60">当前任务</div>
				<div field="allTaskUserNames" headerAlign='center' align='center' width="40">当前处理人</div>
				<div field="instStatus" sortField="instStatus" align="center" headerAlign="center"   width="60"  allowSort="true" renderer="onStatusRenderer">流程状态</div>
			</div>
		</div>
	</div>

	<script type="text/javascript">
		mini.parse();
		var jsUseCtxPath="${ctxPath}";
        var currentUserNo="${currentUserNo}";
        var currentUserId="${currentUserId}";
		var yearMonth="${yearMonth}";
		var szjxManager="${szjxManager}";
		var kpiLeaderFlowListGrid=mini.get("kpiLeaderFlowListGrid");

		$(function () {
			mini.get("yearMonth").setText(yearMonth);
			mini.get("yearMonth").setValue(yearMonth);
			mini.get("yearMonthStart").setText(yearMonth);
			mini.get("yearMonthStart").setValue(yearMonth);
			if (szjxManager == 'true'){
				$("#discard").attr("style","float: right;visibility:visible");
			}
			searchFrm();
		});

		//行功能按钮
		function onActionRenderer(e) {
			var record = e.record;
			var id = record.id;
			var instId=record.instId;
			var currentProcessUserId=record.currentProcessUserId;
			var instStatus='';
			if(record.instStatus) {
			    instStatus=record.instStatus;
			}
			if(instStatus=='NO_CREAT') {
			    return "";
			}
            var s = '<span title="查看" onclick="detailKpiLeaderMonthRow(\'' + id +'\')">查看</span>';
			if(record.instStatus=='DRAFTED' && currentUserId==record.CREATE_BY_) {
			    s+='<span title="编辑" onclick="editBudgetMonthRow(\'' +instId+ '\')">编辑</span>';
			}
			if(record.instStatus =='RUNNING'){
				if (record.myTaskId || currentUserNo=='admin') {
					s+='<span  title="办理" onclick="doKpiLeaderMonthTask(\'' + record.myTaskId + '\')">办理</span>';
				}
			}
			//无删除权限的按钮变灰色
            if ((instStatus == 'DRAFTED'||instStatus == 'DISCARD_END'||instStatus == 'ABORT_END')&& (currentUserId==record.CREATE_BY_||currentUserNo=='admin')) {
                s+='<span title="删除" onclick="rmKpiLeaderMonthFlow('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
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

		//新增流程（后台根据配置的表单进行跳转）
		function addBudgetMonthFlow() {
			var url = jsUseCtxPath + "/bpm/core/bpmInst/SZYDJX/start.do";
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

		//明细（直接跳转到详情的业务controller）
		function detailKpiLeaderMonthRow(id) {
			var action = "detail";
			var url = jsUseCtxPath + "/kpiLeader/core/editMonthPage.do?action=" + action + "&id=" + id;
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

		//跳转到任务的处理界面
		function doKpiLeaderMonthTask(taskId) {
			$.ajax({
				url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId,
				success: function (result) {
					if (!result.success) {
						top._ShowTips({
							msg: result.message
						});
					} else {
						handBudgetMonthTask(taskId);
					}
				}
			})
		}

		//流程任务的处理（后台根据流程方案对应的表单进行跳转）
		function handBudgetMonthTask(taskId) {
			var url = jsUseCtxPath + '/bpm/core/bpmTask/toStart.do?taskId=' + taskId;
			var winObj = openNewWindow(url, "handTask");
			var loop = setInterval(function () {
				if (winObj.closed) {
					clearInterval(loop);
					if (kpiLeaderFlowListGrid) {
						kpiLeaderFlowListGrid.reload();
					}
				}
			}, 1000);

		}

		//删除记录
		function rmKpiLeaderMonthFlow(record) {
			var rows = [];
			if (record) {
				rows.push(record);
			} else {
				rows = kpiLeaderFlowListGrid.getSelecteds();
			}
			if (rows.length <= 0) {
				mini.alert("请至少选中一条记录");
				return;
			}
			mini.confirm("删除当前流程，是否继续？", "提示", function (action) {
				if (action != 'ok') {
					return;
				} else {
					//判断该行是否已经提交流程
					var ids = [];
					var instIds = [];
					var existStartInst = false;
					for (var i = 0, l = rows.length; i < l; i++) {
						var r = rows[i];
						if ((r.instStatus == 'DRAFTED'||r.instStatus == 'DISCARD_END'||r.instStatus=='ABORT_END')&& (currentUserId==r.CREATE_BY_||currentUserNo=='admin')) {
							ids.push(r.id);
							instIds.push(r.instId);
						} else {
							existStartInst = true;
							continue;
						}
					}
					if (existStartInst) {
						mini.alert("仅草稿或废止状态数据可删除！");
					}
					if (ids.length > 0) {
						_SubmitJson({
							url: jsUseCtxPath + "/kpiLeader/core/delKpiLeaderMonthFlow.do",
							method: 'POST',
							data: {ids: ids.join(','), instIds: instIds.join(',')},
							success: function (text) {
								kpiLeaderFlowListGrid.reload();
							}
						});
					}
				}
			});
		}

		//流程群发起
		function createAllProcess(action){
			mini.confirm("按当前选择年月发起流程，是否继续？", "提示", function (action) {
				if (action != 'ok') {
					return;
				} else {
                    mini.get("createProcessBtn").setEnabled(false);
                    showLoading();
					var yearMonthStart = mini.get("yearMonthStart").getText();
					$.ajax({
						url: jsUseCtxPath + "/kpiLeader/core/createAllProcess.do?yearMonth=" + yearMonthStart,
						type: 'get',
						async: false,
						contentType: 'application/json',
						success: function (data) {
							if (data) {
                                mini.alert(data.message,'提示',function (action) {
                                    hideLoading();
                                    mini.get("createProcessBtn").setEnabled(true);
                                    kpiLeaderFlowListGrid.reload();
                                });
							}
						}
					});
				}
			});
		}

        function showLoading() {
            $("#loading").css('display','');
            $("#content").css('display','none');
        }

        function hideLoading() {
            $("#loading").css('display','none');
            $("#content").css('display','');
        }
	</script>
	 <redxun:gridScript gridId="kpiLeaderFlowListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>
