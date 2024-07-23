<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
<title>oa财务成本资料申请流程审批节点</title>
	<%@include file="/commons/list.jsp"%>
</head>
<body>
	 <div class="mini-toolbar" >
	 	<div class="searchBox">
			<form id="searchForm" class="search-form" style="margin-bottom: 25px">
				<ul >
					<li style="margin-right: 15px">
						<span class="text" style="width:auto">主题: </span>
						<input id="theme" name="theme" class="mini-textbox" style="width:98%;" />
					</li>
					<li style="margin-right: 15px">
						<span class="text" style="width:auto">申请人: </span>
						<input id="applyName" name="applyName" class="mini-textbox" style="width:98%;" />
					</li>
					<li style="margin-right: 15px"><span class="text" style="width:auto">流程状态: </span>
						<input id="submitType" name="submitType" class="mini-combobox" style="width:150px;"
							   textField="value" valueField="key" emptyText="请选择..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
							   data="[ {'key' : '未提交','value' : '未提交'},{'key' : '已提交','value' : '已提交'},{'key' : '驳回处理','value' : '驳回处理'}]"
						/>
					</li>

					<li style="margin-left: 10px">
						<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
						<a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
					</li>
				</ul>
			</form>
		</div>
     </div>
	<div class="mini-fit" style="height: 100%;">
		<div id="oaFinanceFlowListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" idField="id"
			 url="${ctxPath}/oa/oaFinance/queryList.do"
			multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
			<div property="columns">
				<div type="checkcolumn" width="20"></div>
				<div name="action" cellCls="actionIcons" width="70" headerAlign="center" align="center" renderer="onActionRenderer">操作</div>
				<div field="id" headerAlign="center" visible="false"></div>
				<div field="oaFlowId" headerAlign="center" visible="false"></div>
				<div type="indexcolumn" headerAlign="center">序号</div>
				<div field="theme" width="100" headerAlign="center" align="center" allowSort="false" >主题</div>
				<div field="deptName" width="70" headerAlign="center" align="center" allowSort="false" >申请部门</div>
				<div field="applyName" width="70" headerAlign="center" align="center" allowSort="false" >申请人</div>
				<div field="userName" width="70" headerAlign="center" align="center" allowSort="false" >流程处理人</div>
				<div field="applyText" width="70" headerAlign="center" align="center" allowSort="false" >申请内容及原因</div>
				<div field="CREATE_TIME_" dateFormat="yyyy-MM-dd" align="center" headerAlign="center" >创建时间</div>
				<div field="submitType" width="60" headerAlign="center" align="center" allowSort="false">流程状态</div>

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
		var oaFinanceFlowListGrid=mini.get("oaFinanceFlowListGrid");
		//行功能按钮
		function onActionRenderer(e) {
			var record = e.record;
			var id = record.id;
			var oaFlowId=record.oaFlowId;
            var s = '<span title="查看" onclick="detailOAFinanceRow(\'' + id +'\',\''+oaFlowId+ '\')">查看</span>';
			if(record.submitType!='已提交') {
			    s+='<span title="办理" onclick="editOAFinanceRow(\'' + id +'\',\''+oaFlowId+ '\')">编辑</span>';
			}
			return s;
		}

		$(function () {
			// 查询列表
			searchFrm();
		});

		//明细（直接跳转到详情的业务controller）
		function detailOAFinanceRow(id, oaFlowId) {
			var action = "detail";
			var url = jsUseCtxPath + "/oa/oaFinance/editPage.do?action=" + action + "&id=" + id + "&oaFlowId=" + oaFlowId;
			var winObj = window.open(url);
			var loop = setInterval(function () {
				if(!winObj) {
					clearInterval(loop);
				} else if (winObj.closed) {
					clearInterval(loop);
					if (oaFinanceFlowListGrid) {
						oaFinanceFlowListGrid.reload();
					}
				}
			}, 1000);
		}

		//明细（直接跳转到详情的业务controller）
		function editOAFinanceRow(id, oaFlowId) {
			var action = "edit";
			var url = jsUseCtxPath + "/oa/oaFinance/editPage.do?action=" + action + "&id=" + id + "&oaFlowId=" + oaFlowId;
			var winObj = window.open(url);
			var loop = setInterval(function () {
				if(!winObj) {
					clearInterval(loop);
				} else if (winObj.closed) {
					clearInterval(loop);
					if (oaFinanceFlowListGrid) {
						oaFinanceFlowListGrid.reload();
					}
				}
			}, 1000);
		}
	</script>
	 <redxun:gridScript gridId="oaFinanceFlowListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>
