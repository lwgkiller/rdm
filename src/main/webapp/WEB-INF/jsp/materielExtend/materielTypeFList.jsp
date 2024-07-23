
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>物料扩充信息记录</title>
	<%@include file="/commons/list.jsp"%>
</head>
<body>
<div class="mini-toolbar" >
	<div class="searchBox">
		<form id="searchForm" class="search-form" style="margin-bottom: 25px">
			<ul >
				<li style="margin-left:15px;margin-right: 15px">
					<span class="text" style="width:auto">申请单号: </span>
					<input class="mini-textbox" name="applyNo"/>
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">申请人: </span>
					<input class="mini-textbox" name="applyUserName" style="width: 100px"/>
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">物料号码: </span>
					<input class="mini-textbox" name="wlhm">
				</li>
				<li style="margin-left: 10px">
					<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
					<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
					<div style="display: inline-block" class="separator"></div>
					<a id="addMaterielTypeF" class="mini-button" style="margin-right: 5px" plain="true" onclick="addMaterielTypeF()">新增</a>
				</li>
			</ul>
		</form>
	</div>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="materielTypeFListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
		 url="${ctxPath}/materielTypeF/core/queryMaterielTypeF.do?" idField="id"
		 multiSelect="false" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
		<div property="columns">
			<div type="checkcolumn" width="20"></div>
			<div name="action"  cellCls="actionIcons" width="60" headerAlign="center" align="center" renderer="onMessageActionRenderer">操作</div>
			<div field="applyType" renderer="onApplyTypeRenderer" width="60" headerAlign="center" align="center" allowSort="true">
				创建方式
			</div>
			<div field="applyNo"  renderer="onApplyNoRenderer" width="110" headerAlign="center" align="center" allowSort="true">
				申请单号
			</div>
			<div field="applyUserName"  headerAlign="center" align="center"
				 allowSort="true">申请人
			</div>
			<div field="applyDeptName" headerAlign="center" align="center"
				 allowSort="true">申请人部门
			</div>
			<div field="CREATE_TIME_"  width="70" headerAlign="center" align="center" allowSort="false">创建时间</div>
			<div field="infoStatus" headerAlign="center" align="center"
				 allowSort="true" renderer="onStatusRenderer">记录状态
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var materielTypeFListGrid=mini.get("materielTypeFListGrid");
    var currentUserId="${currentUserId}";

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.infoStatus;

        var arr = [
            {'key': '记录未提交', 'value': '记录未提交', 'css': 'orange'},
            {'key': '有错误记录', 'value': '有错误记录', 'css': 'red'},
            {'key': '记录创建成功', 'value': '记录创建成功', 'css': 'blue'},
        ];

        return $.formatItemValue(arr, status);
    }

    function onApplyTypeRenderer(e) {
        var record = e.record;
        var applyNo = record.applyNo;
        var s = '';
        if (applyNo){
            s += '<span  title="自动创建" >自动创建</span>';
        }else {
            s += '<span  title="手动创建" >手动创建</span>';
		}
        return s;
    }

    function onApplyNoRenderer(e) {
        var record = e.record;
        var applyNo = record.applyNo;
        var s = '';
        if (applyNo){
            s += '<span  title="自动创建" >'+applyNo+'</span>';
        }else {
            s += '<span  title="手动创建" >无关联的物料扩充单号</span>';
        }
        return s;
    }

    $(function () {
        searchFrm();
    });

    //行功能按钮
    function onMessageActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var CREATE_BY_ = record.CREATE_BY_;
        var s = '';
            s += '<span  title="明细" onclick="materielTypeFDetail(\'' + id + '\')">明细</span>';
        if (CREATE_BY_ == currentUserId||currentUserId=='1') {
            s += '<span  title="编辑" onclick="materielTypeFEdit(\'' + id + '\')">编辑</span>';
            s += '<span  title="删除" onclick="removeMaterielTypeF(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }

    function addMaterielTypeF() {
        var url = jsUseCtxPath + "/materielTypeF/core/editPage.do?action=add";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (materielTypeFListGrid) {
                    materielTypeFListGrid.reload()
                }
            }
        }, 1000);
    }
    function materielTypeFDetail(id) {
        var action = "detail";
        var url = jsUseCtxPath + "/materielTypeF/core/editPage.do?id=" + id+ "&action=" + action ;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (materielTypeFListGrid) {
                    materielTypeFListGrid.reload()
                }
            }
        }, 1000);
    }
    function materielTypeFEdit(id) {
        var action = "edit";
        var url = jsUseCtxPath + "/materielTypeF/core/editPage.do?id=" + id+ "&action=" + action ;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (materielTypeFListGrid) {
                    materielTypeFListGrid.reload()
                }
            }
        }, 1000);
    }
    function removeMaterielTypeF(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = materielTypeFListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.id);
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/materielTypeF/core/deleteMaterielTypeF.do",
                    method: 'POST',
                    showMsg:false,
                    data: {ids: rowIds.join(',')},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            searchFrm();
                        }
                    }
                });
            }
        });
    }

</script>
<redxun:gridScript gridId="materielTypeFListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>