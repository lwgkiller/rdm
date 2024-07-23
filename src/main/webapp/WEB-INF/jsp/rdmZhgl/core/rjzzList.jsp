
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
<title>软件著作列表管理</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/rdmZhgl/rjzzList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
	 <div class="mini-toolbar" >
	 	<div class="searchBox">
			<form id="searchForm" class="search-form" style="margin-bottom: 25px">
				<ul >
					<li style="margin-right: 15px">
						<span class="text" style="width:auto">软件著作编号: </span>
						<input class="mini-textbox" id="rjzzNum" name="rjzzNum">
					</li>
					<li style="margin-right: 15px">
                        <span class="text" style="width:auto">名称: </span>
                        <input class="mini-textbox" id="rjmqc" name="rjmqc" />
                    </li>
					<li style="margin-right: 15px">
						<span class="text" style="width:auto">申请人: </span>
						<input id="applyUserId" name="applyUserId" textname="applyUserName" class="mini-user rxc"
							   plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="" length="50" maxlength="50"
							   mainfield="no"  single="true" />
					</li>
					<li style="margin-right: 15px"><span class="text" style="width:auto">申请部门:</span>
						<input id="applyDepId" name="applyDepId" class="mini-dep rxc" plugins="mini-dep"
							   data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
							   style="width:160px;height:34px"
							   allowinput="false" label="部门" textname="bm_name" length="500" maxlength="500" minlen="0" single="true" required="false" initlogindep="false"
							   level="" refconfig="" grouplevel="" groupid="" mwidth="160" wunit="px" mheight="34" hunit="px"/>
					</li>
					<li style="margin-right: 15px"><span class="text" style="width:auto">认定状态: </span>
						<input id="instStatus" name="status" class="mini-combobox" style="width:120px;"
							   textField="value" valueField="key" emptyText="请选择..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
							   data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '认定中'},{'key' : 'SUCCESS_END','value' : '通过认定'},
							   {'key' : 'DISCARD_END','value' : '未通过认定'}]"
						/>
					</li>
					<li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em>展开</em>
						<i class="unfoldIcon"></i>
					</span>
					</li>

					<li style="margin-left: 10px">
						<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
						<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
						<div style="display: inline-block" class="separator"></div>
						<a class="mini-button" style="margin-right: 5px" plain="true" onclick="addRjzz()">新增申请</a>
						<a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeRjzz()">删除</a>
						<a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportRjzz()">导出</a>
						<a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportRjzzDetail()">导出明细</a>
					</li>
				</ul>
				<div id="moreBox">
					<ul>
						<li style="margin-right: 15px">
							<span class="text" style="width:auto">申请时间 从 </span>:<input id="CREATE_TIME_StartTime"  name="CREATE_TIME_StartTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
						</li>
						<li style="margin-right: 15px">
							<span class="text-to" style="width:auto">至: </span><input  id="CREATE_TIME_EndTime" name="CREATE_TIME_EndTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
						</li>
						<li style="margin-right: 15px">
							<span class="text" style="width:auto">登记时间 从 </span>:<input id="djTimeStartTime"  name="djTimeStartTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
						</li>
						<li style="margin-right: 15px">
							<span class="text-to" style="width:auto">至: </span><input  id="djTimeEndTime" name="djTimeEndTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
						</li>
					</ul>
				</div>
			</form>	
		</div>
     </div>
	<div class="mini-fit" style="height: 100%;">
		<div id="rjzzListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
			url="${ctxPath}/zhgl/core/rjzz/getRjzzList.do" idField="rjzzId"
			multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
			<div property="columns">
				<div type="checkcolumn" width="20"></div>
				<div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
				<div field="rjzzNum"  sortField="rjzzNum"  width="80" headerAlign="center" align="center" allowSort="true">软件著作编号</div>
				<div field="rjmqc"  width="100" headerAlign="center" align="center" allowSort="false">软件名全称</div>
				<div field="djNum" align="center" width="60" headerAlign="center" allowSort="false">登记号</div>
				<div field="zsNum"  width="60" headerAlign="center" align="center" allowSort="false">证书号</div>
				<div field="djTime" width="60"  allowSort="true" headerAlign="center" align="center" >登记时间</div>
				<div field="applyUserName"  width="70" headerAlign="center" align="center" allowSort="false">申请人</div>
				<div field="applyUserDeptName"  width="70" headerAlign="center" align="center" allowSort="false">申请人部门</div>
				<div field="CREATE_TIME_"  width="70" headerAlign="center" align="center" allowSort="false">申请时间</div>
				<div field="status"  width="60" headerAlign="center" align="center" allowSort="true" renderer="onStatusRenderer">认定状态</div>
				<div field="currentProcessUser"  sortField="currentProcessUser"  width="70" align="center" headerAlign="center" allowSort="false">当前处理人</div>
				<div field="currentProcessTask" width="80" align="center" headerAlign="center">当前任务</div>
			</div>
		</div>
	</div>

	 <!--导出Excel相关HTML-->
	 <form id="excelForm" action="${ctxPath}/zhgl/core/rjzz/exportRjzzList.do" method="post" target="excelIFrame">
		 <input type="hidden" name="filter" id="filter"/>
	 </form>
	 <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
	 <form id="excelForm1" action="${ctxPath}/zhgl/core/rjzz/exportRjzzDetail.do" method="post" target="excelIFrame">
		 <input type="hidden" name="filter1" id="filter1"/>
	 </form>
	 <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>


	<script type="text/javascript">
		mini.parse();
		var jsUseCtxPath="${ctxPath}";
        var rjzzListGrid=mini.get("rjzzListGrid");
        var currentUserId="${currentUserId}";
        var currentUserRoles=${currentUserRoles};
        var currentUserNo="${currentUserNo}";
        var isZlgcsUser=${isZlgcsUser};

		//行功能按钮
		function onActionRenderer(e) {
			var record = e.record;
			var rjzzId = record.rjzzId;
			var instId=record.instId;
			var s = '';
			if(record.status=='SUCCESS_END') {
				s+='<span  title="明细" onclick="rjzzDetail(\'' + rjzzId +'\',\''+record.status+ '\')">明细</span>';
			} else{
                s+='<span  title="明细" onclick="rjzzDetail(\'' + rjzzId +'\',\''+record.status+ '\')">明细</span>';
            }
			if(record.status=='DRAFTED') {
			    s+='<span  title="编辑" onclick="rjzzEdit(\'' + rjzzId +'\',\''+instId+ '\')">编辑</span>';
			} else {
			    var currentProcessUserId=record.currentProcessUserId;
                if(currentProcessUserId && currentProcessUserId.indexOf(currentUserId) !=-1) {
                    s+='<span  title="办理" onclick="rjzzTask(\'' + record.taskId + '\')">办理</span>';
                }
			}
			var status=record.status;
			if(currentUserNo!='admin') {
                if (status == 'DRAFTED' && currentUserId ==record.CREATE_BY_) {
                    s+='<span  title="删除" onclick="removeRjzz('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
                }
            } else {
                s+='<span  title="删除" onclick="removeRjzz('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
			}
            return s;
		}

        function onStatusRenderer(e) {
            var record = e.record;
            var status = record.status;

            var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
                {'key' : 'RUNNING','value' : '认定中','css' : 'green'},
                {'key' : 'SUCCESS_END','value' : '通过认定','css' : 'blue'},
                {'key' : 'DISCARD_END','value' : '未通过认定','css' : 'red'}
            ];

            return $.formatItemValue(arr,status);
        }

        function test() {
            $.ajax({
                url: jsUseCtxPath +'/zhgl/core/rjzz/test.do',
                success:function (data) {
                    if(data) {

                    }
                }
            });
        }

	</script>
	<redxun:gridScript gridId="rjzzListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>