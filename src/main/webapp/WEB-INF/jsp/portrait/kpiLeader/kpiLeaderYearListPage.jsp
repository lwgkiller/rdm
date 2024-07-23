
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
<title>所长年度KPI列表</title>
	<%@include file="/commons/list.jsp"%>
</head>
<body>
	<div class="mini-toolbar" >
		<div class="searchBox">
			<form id="searchForm" class="search-form" style="margin-bottom: 25px">
				<ul >
					<li style="margin-right: 15px">
						<span class="text" style="width:auto">年份: </span>
						<input id="createYear" name="year" class="mini-combobox" style="width:105px;height: 35px" onitemclick="searchFrm()"
							   textField="key" valueField="value" required="false" allowInput="false" showNullItem="false"/>
					</li>
					<li style="margin-right: 15px">
						<span class="text" style="width:auto">考核指标: </span>
						<input class="mini-textbox" id="metricDesc" name="metricDesc" />
					</li>
					<li style="margin-right: 15px">
						<span class="text" style="width:auto">执考人员: </span>
						<input class="mini-textbox" id="zkUserName" name="zkUserName" />
					</li>
					<li style="margin-right: 15px">
						<span class="text" style="width:auto">被考核人员: </span>
						<input class="mini-textbox" id="bkhUserName" name="bkhUserName" />
					</li>
					<li style="margin-left: 10px">
						<a class="mini-button" style="margin-right: 5px" iconCls="icon-search" plain="true" onclick="searchFrm()">查询</a>
						<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
						<div style="display: inline-block" class="separator"></div>
						<a id="addCompanyKpiYear" class="mini-button" style="margin-right: 5px" plain="true" onclick="addKpiYear()">新增KPI</a>
						<a id="rmCompanyKpiYear" tooltip="已经分解的KPI不允许删除" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeKpiYear()">删除</a>
					</li>
				</ul>
			</form>
		</div>
	</div>
	<div class="mini-fit" style="height: 100%;">
		<div id="kpiYearListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" allowCellWrap="true"
			 url="${ctxPath}/kpiLeader/core/getKpiYearList.do"
			 idField="id" multiSelect="true" showColumnsMenu="false"
			 sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
			<div property="columns">
				<div type="checkcolumn" width="15"></div>
				<div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
				<div name="action" cellCls="actionIcons" width="50" headerAlign="center" align="left" renderer="onActionRenderer">操作</div>
				<div field="metricDesc" width="50" headerAlign="center" align="center" allowSort="false">考核指标</div>
				<div field="targetDesc"  width="25" headerAlign="center" align="center" renderer="onTargetDescRenderer">目标值</div>
				<div field="targetLimit"  width="40" headerAlign="center" align="center" allowSort="false" renderer="onTargetLimitRenderer">目标上下限</div>
				<div field="weight"  width="20" headerAlign="center" align="center" allowSort="false" renderer="weightRenderer">权重</div>
				<div field="computeDesc"  width="200" headerAlign="center" align="left" allowSort="false">计算方法及考核规则</div>
				<div field="period"  width="25" headerAlign="center" align="center" allowSort="true">周期</div>
				<div field="zkUserName" align="center" headerAlign="center" width="40"  allowSort="false" >执考人员</div>
				<div field="bkhUserNames" align="center" headerAlign="center" width="150"  allowSort="false" >被考核人员</div>
			</div>
		</div>
	</div>

	<script type="text/javascript">
		mini.parse();
		var jsUseCtxPath="${ctxPath}";
        var kpiYearListGrid=mini.get("kpiYearListGrid");
        var currentUserNo="${currentUserNo}";
        var currentUserId="${currentUserId}";
        var companyId="${companyId}";
        var kpiYearDecomposeWindow=mini.get("kpiYearDecomposeWindow");
        var kpiYearDecomposeListGrid=mini.get("kpiYearDecomposeListGrid");

        $(function (){
			setCreateYear();
			searchFrm();
		});

		function setCreateYear() {
			var createYearData=generateYearSelect();
			mini.get("createYear").load(createYearData);
			var nowY=new Date().getFullYear();
			mini.get("createYear").setValue(nowY);
		}

		function generateYearSelect() {
			var data=[];
			var nowDate=new Date();
			var startY=nowDate.getFullYear()-10;
			var endY=nowDate.getFullYear()+1;
			for(var i=endY;i>=startY;i--) {
				var oneData={};
				oneData.key=i+'年';
				oneData.value=i;
				data.push(oneData);
			}
			return data;
		}

		function addKpiYear() {
			var selectYear=mini.get("createYear").getValue();
			mini.open({
				title: "新增KPI",
				url: jsUseCtxPath + "/kpiLeader/core/edit.do?action=add&scene=company&selectYear="+selectYear,
				width: 800,
				height: 550,
				showModal: true,
				allowResize: true,
				ondestroy: function (action) {
					searchFrm();
				}
			});
		}

		//行功能按钮
		function onActionRenderer(e) {
			var record = e.record;
			var kpiId = record.id;
			var year=record.year;
			var s = '<span  title="查看" onclick="detailKpiYear(\'' + kpiId + '\')">查看</span>';
                s+='<span  title="编辑" onclick="editKpiYear(\'' + kpiId+ '\')">编辑</span>';
                s+='<span  title="删除" title="已经分解的KPI不允许删除" onclick="removeKpiYear('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
            // }
			return s;
		}

        function onDecomposeActionRenderer(e) {
            var record = e.record;
            var kpiId = record.id;
            var year=record.year;
            var s = '<span  title="查看" style="color:#409eff;padding: 0 2px;cursor: pointer;" onclick="detailKpiYearDecompose(\'' + kpiId + '\')">查看</span>';
            s+='<span  title="编辑" style="color:#409eff;padding: 0 2px;cursor: pointer;" onclick="editKpiYearDecompose(\'' + kpiId+ '\')">编辑</span>';
            s+='<span  title="删除" style="color:#409eff;padding: 0 2px;cursor: pointer;" onclick="removeKpiYearDecompose('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
            return s;
        }

		//目标上限和下限都优先展示公式，最后拼接成"下限"~"上限"的形式
		function onTargetLimitRenderer(e) {
			var record=e.record;
			var lower='';
			if(record.targetLowerValue) {
				lower=record.targetLowerValue;
			}
			var upper='';
			if(record.targetUpperValue) {
				upper=record.targetUpperValue;
			}
			return lower+'~'+upper;
		}

        //目标值优先展示公式，公式为空展示值
        function onTargetDescRenderer(e) {
			var record=e.record;
			if(record.targetValue) {
				return record.targetValue;
			}
            return '';
        }

		function weightRenderer(e) {
            var record=e.record;
            return record.weight+'%';
        }
		function detailKpiYear(kpiId) {
			mini.open({
				title: "查看KPI",
				url: jsUseCtxPath + "/kpiLeader/core/edit.do?action=detail&scene=company&id="+kpiId,
				width: 800,
				height: 650,
				showModal: true,
				allowResize: true,
				ondestroy: function (action) {
					searchFrm();
				}
			});
		}

		function editKpiYear(kpiId) {
			mini.open({
				title: "编辑KPI",
				url: jsUseCtxPath + "/kpiLeader/core/edit.do?action=edit&scene=company&id="+kpiId,
				width: 800,
				height: 650,
				showModal: true,
				allowResize: true,
				ondestroy: function (action) {
					searchFrm();
				}
			});
		}

		function removeKpiYear(record) {
			var rows = [];
			if (record) {
				rows.push(record);
			} else {
				rows = kpiYearListGrid.getSelecteds();
			}

			if (rows.length <= 0) {
				mini.alert("请至少选中一条记录");
				return;
			}

			mini.confirm("确定删除选中记录？", "提示", function (action) {
				if (action != 'ok') {
					return;
				} else {
					//判断该行是否已经提交
					var ids = [];
					for (var i = 0, l = rows.length; i < l; i++) {
						var r = rows[i];
						ids.push(r.id);
					}
					if (ids.length > 0) {
						_SubmitJson({
							url: jsUseCtxPath + "/kpiLeader/core/delKpi.do",
							method: 'POST',
							data: {ids: ids.join(',')},
							success: function (data) {
								if(data && data.message) {
									mini.alert(data.message);
								}
								kpiYearListGrid.reload();
							}
						});
					}
				}
			});
		}

	</script>
	 <redxun:gridScript gridId="kpiYearListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>
