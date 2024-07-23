<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>通讯录</title>
	<%@include file="/commons/list.jsp"%>
</head>
<body>
<div class="mini-toolbar">
	<div class="searchBox">
		<form id="searchForm" class="search-form" style="margin-bottom: 25px">
			<ul>
				<li style="margin-left: 10px">
					<span class="text" style="width:auto">公司名称: </span>
					<input id="companyName" name="companyName" class="mini-combobox" style="width:150px;"
						   textField="companyName" valueField="companyName" emptyText="请选择..."
						   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
							onvaluechanged="companyChange"
					/>
				</li>
				<li style="margin-left: 10px">
					<span class="text" style="width:auto">一级部门: </span>
					<input id="deptNameLevelOne" name="deptNameLevelOne" class="mini-combobox" style="width:150px;"
						   textField="deptNameLevelOne" valueField="deptNameLevelOne" emptyText="请选择..."
						   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
						   onvaluechanged="deptNameLevelOneChange"
					/>
				</li>
				<li style="margin-left: 10px">
					<span class="text" style="width:auto">二级部门: </span>
					<input id="deptNameLevelTwo" name="deptNameLevelTwo" class="mini-combobox" style="width:150px;"
						   textField="deptNameLevelTwo" valueField="deptNameLevelTwo" emptyText="请选择..."
						   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
					/>
				</li>
				<li style="margin-left: 10px">
					<span class="text" style="width:auto">名称: </span>
					<input class="mini-textbox" style="width: 150px" id="txlName"  name="txlName" onenter="searchTxl()"/>
				</li>
				<li style="margin-left: 10px">
					<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchTxl()">查询</a>
					<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearSearchTxl()">清空查询</a>
					<div style="display: inline-block" class="separator"></div>
					<a class="mini-button" style="margin-right: 5px" plain="true" id="addTxlBtn" onclick="editTxl()">新增</a>
					<a class="mini-button btn-red" style="margin-right: 5px" plain="true" id="rmTxlBtn" onclick="removeTxl()">删除</a>
				</li>
			</ul>
		</form>
	</div>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="txlListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
		 url="${ctxPath}/zhgl/core/txl/txlList.do" idField="id" autoload="false" allowCellWrap="true"
		 multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
		<div property="columns">
			<div type="checkcolumn" width="20"></div>
			<div name="action" cellCls="actionIcons" width="50" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
			<div field="companyName"  sortField="jsmmNumber"  width="100" headerAlign="center" align="center" allowSort="true">公司名称</div>
			<div field="deptNameLevelOne"  width="100" headerAlign="center" align="center" allowSort="false">一级部门</div>
			<div field="deptNameLevelTwo" align="center" width="100" headerAlign="center" allowSort="false">二级部门</div>
			<div field="txlName"  width="100" headerAlign="center" align="center" allowSort="false">名称</div>
			<div field="txlZj"  width="100" headerAlign="center" align="center" allowSort="false">固定电话</div>
			<div field="txlSj"  width="100" headerAlign="center" align="center" allowSort="false">手机号码</div>
			<div field="office"  width="100" headerAlign="center" align="center" allowSort="false">办公地点</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var txlListGrid=mini.get("txlListGrid");
    var isMgrUser = ${isMgrUser};
    var currentUserNo="${currentUserNo}";
    var searchFormTxl=new mini.Form('searchForm');
    txlListGrid.on("update",function(e){
        handGridButtons(e.sender.el);
    });
    $(function () {
        if(!isMgrUser) {
            mini.get("addTxlBtn").setEnabled(false);
            mini.get("rmTxlBtn").setEnabled(false);
        }

        $.ajax({
            url: "${ctxPath}/zhgl/core/txl/queryCompany.do",
            type: "get",
            success: function (result) {
                if (result) {
                    mini.get("companyName").setData(result);
                }
            }
        });

        searchTxl();
    });

    function companyChange() {
        mini.get("deptNameLevelOne").setData([]);
        mini.get("deptNameLevelOne").setValue('');
        mini.get("deptNameLevelTwo").setData([]);
        mini.get("deptNameLevelTwo").setValue('');
        $.ajax({
            url: "${ctxPath}/zhgl/core/txl/queryDeptNameLevelOne.do?companyName="+mini.get("companyName").getValue(),
            type: "get",
            success: function (result) {
                if (result) {
                    mini.get("deptNameLevelOne").setData(result);
                }
            }
        });
    }
    
    function deptNameLevelOneChange() {
        mini.get("deptNameLevelTwo").setData([]);
        mini.get("deptNameLevelTwo").setValue('');
        $.ajax({
            url: "${ctxPath}/zhgl/core/txl/queryDeptNameLevelTwo.do?companyName="+mini.get("companyName").getValue()+"&deptNameLevelOne="+mini.get("deptNameLevelOne").getValue(),
            type: "get",
            success: function (result) {
                if (result) {
                    mini.get("deptNameLevelTwo").setData(result);
                }
            }
        });
    }
    
    function searchTxl() {
        var queryParam = [];
        //其他筛选条件
        var companyName = $.trim(mini.get("companyName").getValue());
        if (companyName) {
            queryParam.push({name: "companyName", value: companyName});
        }
        var deptNameLevelOne = $.trim(mini.get("deptNameLevelOne").getValue());
        if (deptNameLevelOne) {
            queryParam.push({name: "deptNameLevelOne", value: deptNameLevelOne});
        }
        var deptNameLevelTwo = $.trim(mini.get("deptNameLevelTwo").getValue());
        if (deptNameLevelTwo) {
            queryParam.push({name: "deptNameLevelTwo", value: deptNameLevelTwo});
        }
        var txlName = $.trim(mini.get("txlName").getValue());
        if (txlName) {
            queryParam.push({name: "txlName", value: txlName});
        }

        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = txlListGrid.getPageIndex();
        data.pageSize = txlListGrid.getPageSize();
        data.sortField = txlListGrid.getSortField();
        data.sortOrder = txlListGrid.getSortOrder();
        //查询
        txlListGrid.load(data);
    }

    function clearSearchTxl() {
        searchFormTxl.setData({});
        mini.get("deptNameLevelOne").setData([]);
        mini.get("deptNameLevelTwo").setData([]);
        searchTxl();
    }

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var txlId = record.id;
        var s = '';
        if(isMgrUser) {
            s+='<span  title="编辑" onclick="editTxl(\'' + txlId + '\')">编辑</span>';
        } else {
            s += '<span  title="编辑" style="color: silver">编辑</span>';
        }
        return s;
    }
    
    function editTxl(txlId) {
        if(!txlId) {
            txlId='';
		}
        mini.open({
            title: "通讯录编辑",
            url: jsUseCtxPath + "/zhgl/core/txl/txlEditPage.do?id="+txlId,
            width: 1000,
            height: 550,
            showModal: true,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                searchTxl();
            }
        });
    }

    function removeTxl() {
        var rows  = txlListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }

        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var ids = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
					ids.push(r.id);
                }
                if(ids.length<=0) {
                    return;
                }
                $.ajax({
                    url: jsUseCtxPath + "/zhgl/core/txl/delete.do",
                    type: 'POST',
                    data: mini.encode({ids: ids.join(',')}),
                    contentType: 'application/json',
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            searchTxl();
                        }
                    }
                });
            }
        });
    }
</script>
</body>
</html>