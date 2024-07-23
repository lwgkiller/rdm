
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
<title>专利申请交底书列表管理</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/rdmZhgl/zljsjdsList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
	 <div class="mini-toolbar" >
	 	<div class="searchBox">
			<form id="searchForm" class="search-form" style="margin-bottom: 25px">
				<ul >
					<li style="margin-right: 15px">
						<span class="text" style="width:auto">提案名称: </span>
						<input class="mini-textbox" id="zlName" name="zlName">
					</li>
					<li style="margin-right: 15px">
						<span class="text" style="width:auto">申请人: </span>
						<input id="applyUserId" name="applyUserId" textname="applyUserName" class="mini-user rxc"
							   plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="" length="50" maxlength="50"
							   mainfield="no"  single="true" />
					</li>
					<li style="margin-right: 15px">
						<span class="text" style="width:auto">申请人部门: </span>
						<input id="applyUserDeptId" name="applyUserDeptId" textname="applyUserDeptName" class="mini-dep rxc"
							   plugins="mini-dep" style="width:98%;height:34px;" allowinput="false" label="部门" length="500"
							   maxlength="500" minlen="0" single="true" required="false" initlogindep="false" showclose="false"
							   mwidth="160" wunit="px" mheight="34" hunit="px" />
					</li>
					<li style="margin-right: 15px"><span class="text" style="width:auto">认定状态: </span>
						<input id="instStatus" name="instStatus" class="mini-combobox" style="width:120px;"
							   textField="value" valueField="key" emptyText="请选择..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
							   data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '认定中'},{'key' : 'SUCCESS_END','value' : '通过认定'},
							   {'key' : 'DISCARD_END','value' : '未通过认定'}]"
						/>
					</li>
					<li style="margin-right: 15px"><span class="text" style="width:auto">专利类型: </span>
						<input id="zllx" name="zllx" class="mini-combobox" style="width:120px;"
							   textField="value" valueField="key" emptyText="请选择..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
							   data="[ {'key' : 'FM','value' : '发明'},{'key' : 'SYXX','value' : '实用新型'},{'key' : 'WGSJ','value' : '外观设计'}]"
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
						<a class="mini-button" style="margin-right: 5px" plain="true" onclick="addJsjds()">新增申请</a>
						<a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeJsjds()">删除</a>
						<a class="mini-button" style="margin-right: 5px" plain="true" onclick="mobanJsjds()">模板下载</a>
					</li>
				</ul>
				<div id="moreBox">
					<ul>
						<li style="margin-right: 15px">
							<span class="text" style="width:auto">审批完成时间 从 </span>:<input id="bmscwcTimeStart"  name="bmscwcTimeStart"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
						</li>
						<li style="margin-right: 15px">
							<span class="text-to" style="width:auto">至: </span><input  id="bmscwcTimeEnd" name="bmscwcTimeEnd" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
						</li>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto">当前任务: </span>
                            <input class="mini-textbox" id="taskName" name="taskName">
                        </li>
					</ul>
				</div>
			</form>	
		</div>
     </div>
	<div class="mini-fit" style="height: 100%;">
		<div id="jsjdsListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
			url="${ctxPath}/zhgl/core/jsjds/getJsjdsList.do" idField="jsjdsId"
			multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
			<div property="columns">
				<div type="checkcolumn" width="20"></div>
				<div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
				<div field="zlName"  sortField="zlName"  width="100" headerAlign="center" align="center" allowSort="true">提案名称</div>
				<div field="zllx"  width="50" headerAlign="center" align="center" allowSort="false"renderer="onLXRenderer">专利类型</div>
				<div field="ifgj" align="center" width="50" headerAlign="center" allowSort="false"renderer="onGJRenderer">是否国际申请</div>
				<div field="myfmsjName"  width="80" headerAlign="center" align="center" allowSort="false">我司发明（设计）人</div>
				<div field="creator"  width="70" headerAlign="center" align="center" allowSort="false">申请人</div>
				<div field="creatorDeptName"  width="80" headerAlign="center" align="center" allowSort="false">申请人部门</div>
				<div field="CREATE_TIME_"  width="70" headerAlign="center" align="center" allowSort="false">申请时间</div>
				<div field="bmscwcTime"  width="70" headerAlign="center" align="center" allowSort="false">审批完成时间</div>
				<div field="instStatus"  width="60" headerAlign="center" align="center" allowSort="true" renderer="onStatusRenderer">认定状态</div>
				<div field="allTaskUserNames"  sortField="currentProcessUser"  width="70" align="center" headerAlign="center" allowSort="false">当前处理人</div>
				<div field="taskName" width="80" align="center" headerAlign="center">当前任务</div>
			</div>
		</div>
	</div>

	<script type="text/javascript">
		mini.parse();
		var jsUseCtxPath="${ctxPath}";
        var jsjdsListGrid=mini.get("jsjdsListGrid");
        var currentUserId="${currentUserId}";
        var currentUserRoles=${currentUserRoles};
        var currentUserNo="${currentUserNo}";
        var isZlgcsUser=${isZlgcsUser};


		//行功能按钮
		function onActionRenderer(e) {
			var record = e.record;
			var jsjdsId = record.jsjdsId;
			var instId=record.instId;
            var s='<span  title="明细" onclick="jsjdsDetail(\'' + jsjdsId +'\',\''+record.instStatus+ '\')">明细</span>';
			if(record.instStatus=='DRAFTED' && record.CREATE_BY_==currentUserId) {
			    s+='<span  title="编辑" onclick="jsjdsEdit(\'' + jsjdsId +'\',\''+instId+ '\')">编辑</span>';
			} else {
                if (record.myTaskId) {
                    s += '<span  title="办理" onclick="jsjdsTask(\'' + record.myTaskId + '\')">办理</span>';
                }
            }
            if(isZlgcsUser||currentUserNo=='admin'){
                s+='<span  title="变更" onclick="jsjdsChange(\'' + jsjdsId +'\',\''+record.instStatus+ '\')">变更</span>';
            }
			var status=record.instStatus;
			if(currentUserNo!='admin') {
                if ((status == 'DRAFTED' && currentUserId ==record.CREATE_BY_)||(status == 'DISCARD_END' && currentUserId ==record.CREATE_BY_)) {
                    s+='<span  title="删除" onclick="removeJsjds('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
                }
            } else {
                s+='<span  title="删除" onclick="removeJsjds('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
			}
            return s;
		}

        function onStatusRenderer(e) {
            var record = e.record;
            var instStatus = record.instStatus;
			if (!instStatus || instStatus=='') {
                instStatus='SUCCESS_END';
			}
            var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
                {'key' : 'RUNNING','value' : '认定中','css' : 'green'},
                {'key' : 'SUCCESS_END','value' : '通过认定','css' : 'blue'},
                {'key' : 'DISCARD_END','value' : '未通过认定','css' : 'red'}
            ];

            return $.formatItemValue(arr,instStatus);
        }
        function onLXRenderer(e) {
            var record = e.record;
            var zllx = record.zllx;

            var arr = [ {'key' : 'FM','value' : '发明'},
                {'key' : 'SYXX','value' : '实用新型'},
                {'key' : 'WGSJ','value' : '外观设计'}
            ];
            return $.formatItemValue(arr,zllx);
        }
        function onGJRenderer(e) {
            var record = e.record;
            var ifgj = record.ifgj;

            var arr = [ {'key' : 'yes','value' : '是'},
                {'key' : 'no','value' : '否'}
            ];
            return $.formatItemValue(arr,ifgj);
        }

	</script>
	<redxun:gridScript gridId="jsjdsListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>