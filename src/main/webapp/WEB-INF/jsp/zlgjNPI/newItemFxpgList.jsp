
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
<title>新品失效风险评估列表</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/zlgjNPI/newItemFxpgList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
	 <div class="mini-toolbar" >
	 	<div class="searchBox">
			<form id="searchForm" class="search-form" style="margin-bottom: 25px">
				<ul >
					<li style="margin-right: 15px"><span class="text" style="width:auto">系统分类: </span>
						<input id="xtfl" name="xtfl" class="mini-combobox" style="width:120px;"
							   textField="value" valueField="key" emptyText="请选择..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
							   data="[{'key' : 'DP','value' : '底盘'},{'key' : 'DQ','value' : '电气'},{'key' : 'DL','value' : '动力'},
                                   {'key' : 'FGJ','value' : '覆盖件'},{'key' : 'GZZZ','value' : '工作装置'},{'key' : 'YA','value' : '液压'},
                                   {'key' : 'ZT','value' : '转台'},{'key' : 'SC','value' : '刹车'},{'key' : 'CD','value' : '传动'}]"
						/>
					</li>
					<li style="margin-right: 15px"><span class="text" style="width:auto">认定状态: </span>
						<input id="instStatus" name="status" class="mini-combobox" style="width:120px;"
							   textField="value" valueField="key" emptyText="请选择..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
							   data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '认定中'},{'key' : 'SUCCESS_END','value' : '通过认定'},
							   {'key' : 'DISCARD_END','value' : '未通过认定'}]"
						/>
					</li>
					<%--<li style="margin-right: 15px"><span class="text" style="width:auto">机型: </span>
						<input class="mini-textbox" id="smallJiXing" name="smallJiXing">
					</li>
					<li style="margin-right: 15px"><span class="text" style="width:auto">故障零件: </span>
						<input class="mini-textbox" id="gzlj" name="gzlj">
					</li>--%>
					<%--<li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em>展开</em>
						<i class="unfoldIcon"></i>
					</span>
					</li>--%>
					<li style="margin-left: 10px">
						<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
						<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
						<div style="display: inline-block" class="separator"></div>
						<a id="addXpsx" class="mini-button" style="margin-right: 5px" plain="true" onclick="addXpsx()">新增</a>
						<a id="removeXpsx" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeXpsx()">删除</a>
					</li>
				</ul>

				<%--<div id="moreBox">
					<ul>

					</ul>
				</div>--%>
			</form>	
		</div>
     </div>
	<div class="mini-fit" style="height: 100%;">
		<div id="xpsxListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
			url="${ctxPath}/zhgl/core/fxpg/getXpsxList.do" idField="id" allowCellWrap="true"
			multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
			<div property="columns">
				<div type="checkcolumn" width="20"></div>
				<div name="action" cellCls="actionIcons" width="110" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
				<div field="xtfl"  width="80" headerAlign="center" align="center"  renderer="onXTFLRenderer">系统分类</div>
				<div field="lbj"  width="100" headerAlign="center" align="center"  >零部件</div>
				<div field="wtqd"  width="120" headerAlign="center" align="center" >问题清单</div>
				<div field="gscs"  width="150" headerAlign="center" align="center" >改善措施</div>
				<div field="gsrName"  width="80"  headerAlign="center" align="center" >改善责任人</div>
				<div field="zrrName" width="80" headerAlign="center" align="center" >责任人</div>
				<div field="sjsm" width="80" headerAlign="center" align="center" >设计寿命</div>
				<div field="status"  width="70" headerAlign="center" align="center"  renderer="onStatusRenderer">流程状态</div>
				<div field="currentProcessUser"  sortField="currentProcessUser"  width="70" align="center" headerAlign="center" >当前处理人</div>
				<div field="currentProcessTask" width="100" align="center" headerAlign="center">当前任务</div>
				<div field="applyUserName"  width="70" headerAlign="center" align="center" >创建人</div>
				<div field="CREATE_TIME_" sortField="CREATE_TIME_" width="90" headerAlign="center" align="center" >创建时间</div>
			</div>
		</div>
	</div>

	<script type="text/javascript">
		mini.parse();
		var jsUseCtxPath="${ctxPath}";
        var xpsxListGrid=mini.get("xpsxListGrid");
        var currentUserId="${currentUserId}";
        var currentUserRoles=${currentUserRoles};
        var currentUserNo="${currentUserNo}";
        var isJsglbRespUser=${isJsglbRespUser};

		//行功能按钮
		function onActionRenderer(e) {
			var record = e.record;
			var xpsxId = record.id;
			var instId=record.instId;
			var s = '';
			if(record.status=='SUCCESS_END') {
                //技术管理部负责人不能看，不是自己且不是技术管理部其他人的且知悉范围中没有自己的，已成功的秘密内容
                if(isJsglbRespUser && (currentUserId !=record.CREATE_BY_) && record.applyUserDeptName !='技术管理部' && record.readUserIds.indexOf(currentUserId)==-1) {
                    s+='<span  title="明细" style="color: silver">明细</span>';
				}
				 else {
                    s+='<span  title="明细" onclick="xpsxDetail(\'' + xpsxId +'\',\''+record.status+ '\')">明细</span>';
				}

			} else{
                s+='<span  title="明细" onclick="xpsxDetail(\'' + xpsxId +'\',\''+record.status+ '\')">明细</span>';
            }
			if(record.status=='DRAFTED') {
			    s+='<span  title="编辑" onclick="xpsxEdit(\'' + xpsxId +'\',\''+instId+ '\')">编辑</span>';
			} else {
			    var currentProcessUserId=record.currentProcessUserId;
                if(currentProcessUserId && currentProcessUserId.indexOf(currentUserId) !=-1) {
                    s+='<span  title="办理" onclick="xpsxTask(\'' + record.taskId + '\')">办理</span>';
                }
			}

			var status=record.status;
			if(currentUserNo!='admin') {
                if ((status == 'DRAFTED' && currentUserId ==record.CREATE_BY_)||(status == 'DISCARD_END' && currentUserId ==record.CREATE_BY_)) {
                    s+='<span  title="删除" onclick="removeXpsx('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
                }
            } else {
                s+='<span  title="删除" onclick="removeXpsx('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
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

        function onXTFLRenderer(e) {
            var record = e.record;
            var xtfl = record.xtfl;

            var arr = [ {'key' : 'DP','value' : '底盘'},{'key' : 'DQ','value' : '电气'},{'key' : 'DL','value' : '动力'},
                {'key' : 'FGJ','value' : '覆盖件'},{'key' : 'GZZZ','value' : '工作装置'},{'key' : 'YA','value' : '液压'},
                {'key' : 'ZT','value' : '转台'},{'key' : 'SC','value' : '刹车'},{'key' : 'CD','value' : '传动'}
            ];
            return $.formatItemValue(arr,xtfl);
        }

	</script>
	<redxun:gridScript gridId="xpsxListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>