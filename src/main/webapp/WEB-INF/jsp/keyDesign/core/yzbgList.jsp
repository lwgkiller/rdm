
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>改进计划列表管理</title>
	<%@include file="/commons/list.jsp"%>
</head>
<body>
<div class="mini-toolbar" >
	<div class="searchBox">
		<form id="searchForm" class="search-form" style="margin-bottom: 25px">
			<ul >
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">（子）零部件图号/型号: </span>
					<input class="mini-textbox" id="code" name="code" />
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">零部件名称: </span>
					<input class="mini-textbox" id="codeName" name="codeName" />
				</li>
				<li style="margin-left: 10px">
					<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
					<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
					<div style="display: inline-block" class="separator"></div>
					<a id="addYzbg" class="mini-button" style="margin-right: 5px" plain="true" onclick="addYzbg()">新增</a>
				</li>
			</ul>
		</form>
	</div>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="yzbgListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
		 url="${ctxPath}/yzbg/queryYzbg.do?belongbj=${type}" idField="id"
		 multiSelect="false" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
		<div property="columns">
			<div type="checkcolumn" width="20"></div>
			<div name="action" cellCls="actionIcons" width="60" headerAlign="center" align="center" renderer="onMessageActionRenderer" cellStyle="padding:0;">操作</div>
			<div field="code"  width="70" headerAlign="center" align="center" allowSort="true">（子）零部件图号/型号</div>
			<div field="codeName"  width="70" headerAlign="center" align="center" allowSort="true">零部件名称</div>
			<div field="model"  width="70" headerAlign="center" align="center" allowSort="true">适用机型</div>
			<div field="supplier"  width="70" headerAlign="center" align="center" allowSort="true">生产供应商</div>
			<div field="userName" headerAlign='center' align='center' width="40">创建人</div>
			<div field="CREATE_TIME_"  width="70" headerAlign="center" align="center" allowSort="false">创建时间</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var yzbgListGrid=mini.get("yzbgListGrid");
    var currentUserId="${currentUserId}";
    var type="${type}";
    var currentUserNo="${currentUserNo}";
    var isGlr=${isGlr};

    // if(isGlr){
    //     $("#addYzbg").show();
    // }

    //行功能按钮
    function onMessageActionRenderer(e) {
        var record = e.record;
        var bgId = record.bgId;
        var CREATE_BY_ = record.CREATE_BY_;
        var s = '';
            s += '<span  title="明细" onclick="yzbgDetail(\'' + bgId + '\')">明细</span>';
        if (CREATE_BY_ == currentUserId) {
            s += '<span  title="编辑" onclick="yzbgEdit(\'' + bgId + '\')">编辑</span>';
            s += '<span  title="删除" onclick="removeYzbg(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }

    function addYzbg() {
        var url = jsUseCtxPath + "/yzbg/editPage.do?action=add&type="+type;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (yzbgListGrid) {
                    yzbgListGrid.reload()
                }
            }
        }, 1000);
    }
    function yzbgDetail(bgId) {
        var action = "detail";
        var url = jsUseCtxPath + "/yzbg/editPage.do?bgId=" + bgId+ "&action=" + action ;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (yzbgListGrid) {
                    yzbgListGrid.reload()
                }
            }
        }, 1000);
    }
    function yzbgEdit(bgId) {
        var action = "edit";
        var url = jsUseCtxPath + "/yzbg/editPage.do?bgId=" + bgId+ "&action=" + action ;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (yzbgListGrid) {
                    yzbgListGrid.reload()
                }
            }
        }, 1000);
    }
    function removeYzbg(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = yzbgListGrid.getSelecteds();
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
                    rowIds.push(r.bgId);
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/yzbg/deleteYzbg.do",
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
<redxun:gridScript gridId="yzbgListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>