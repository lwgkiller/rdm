
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>改进计划列表管理</title>
	<%@include file="/commons/list.jsp"%>
</head>
<body>
<div class="mini-fit" style="height: 100%;">
	<div id="gzjlListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
		 url="${ctxPath}/gzjl/queryGzjl.do?belongbz=${belongbz}" idField="id"
		 multiSelect="false" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
		<div property="columns">
			<div type="checkcolumn" width="20"></div>
			<div name="action" cellCls="actionIcons" width="50" headerAlign="center" align="center" renderer="onMessageActionRenderer" cellStyle="padding:0;">操作</div>
			<div field="userName" width="50" headerAlign='center' align='center' width="40">更正人</div>
			<div field="CREATE_TIME_"  width="70" headerAlign="center" align="center" allowSort="false">更正时间</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var action="${action}";
    var gzjlListGrid=mini.get("gzjlListGrid");
    var currentUserId="${currentUserId}";
    var belongbz="${belongbz}";
    var currentUserNo="${currentUserNo}";

    //行功能按钮
    function onMessageActionRenderer(e) {
        var record = e.record;
        var updateId = record.updateId;
        var CREATE_BY_ = record.CREATE_BY_;
        var s = '';
            s += '<span  title="明细" onclick="gzjlDetail(\'' + updateId + '\')">明细</span>';
        if (CREATE_BY_ == currentUserId&&action=="edit") {
            s += '<span  title="编辑" onclick="gzjlEdit(\'' + updateId + '\')">编辑</span>';
            s += '<span  title="删除" onclick="removeGzjl(\'' + updateId + '\')">删除</span>';
        }
        return s;
    }


    function gzjlDetail(updateId) {
        var action = "detail";
        var url = jsUseCtxPath + "/gzjl/editPage.do?updateId=" + updateId+"&action="+action;
        mini.open({
            title: "编辑列表",
            url: url,
            width: 1000,
            height: 600,
            showModal: true,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                searchFrm();
            }
        });
    }
    function gzjlEdit(updateId) {
        var action = "edit";
        var url = jsUseCtxPath + "/gzjl/editPage.do?updateId=" + updateId+"&action="+action;
        mini.open({
            title: "编辑列表",
            url: url,
            width: 1000,
            height: 600,
            showModal: true,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                searchFrm();
            }
        });
    }
    function removeGzjl(updateId) {
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/gzjl/deleteGzjl.do",
                    method: 'POST',
                    showGzjl:false,
                    data: {id: updateId},
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
<redxun:gridScript gridId="gzjlListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>