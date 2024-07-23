
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
					<span class="text" style="width:auto">消息内容: </span>
					<input class="mini-textbox" id="content" name="content" />
				</li>
				<li style="margin-left: 10px">
					<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
					<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
					<div style="display: inline-block" class="separator"></div>
					<a class="mini-button" style="margin-right: 5px" plain="true" onclick="addMsg()">新增</a>
				</li>
			</ul>
		</form>
	</div>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="msgListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
		 url="${ctxPath}/jsbz/queryMsg.do?belongbj=${type}" idField="id"
		 multiSelect="false" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
		<div property="columns">
			<div type="checkcolumn" width="20"></div>
			<div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center" renderer="onMessageActionRenderer" cellStyle="padding:0;">操作</div>
			<div field="content"  width="50" headerAlign="center" align="center" allowSort="true">消息内容</div>
			<div field="recName"  width="70" headerAlign="center" align="center" allowSort="true">接收人</div>
			<div field="linkaction"  width="70" headerAlign="center" align="center" allowSort="true">关联情况</div>
			<div field="userName" headerAlign='center' align='center' width="40">创建人</div>
			<div field="CREATE_TIME_"  width="70" headerAlign="center" align="center" allowSort="false">创建时间</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var msgListGrid=mini.get("msgListGrid");
    var currentUserId="${currentUserId}";
    var type="${type}";
    var currentUserNo="${currentUserNo}";

    //行功能按钮
    function onMessageActionRenderer(e) {
        var record = e.record;
        var msgId = record.msgId;
        var CREATE_BY_ = record.CREATE_BY_;
        var s = '';
            s += '<span  title="明细" onclick="msgDetail(\'' + msgId + '\')">明细</span>';
        if (CREATE_BY_ == currentUserId&&record.linkaction=="已关联") {
            s += '<span  title="删除" onclick="removeMsg(\'' + msgId + '\')">删除</span>';
        }
        if(record.recId== currentUserId&&record.linkaction=="未关联"){
            s += '<span  title="编辑" onclick="msgEdit(\'' + msgId + '\')">编辑</span>';
		}
        return s;
    }

    function addMsg1() {
        var url = jsUseCtxPath + "/jsbz/editMsgPage.do?action=add&type="+type;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (msgListGrid) {
                    msgListGrid.reload()
                }
            }
        }, 1000);
    }

    //新增
    function addMsg(msgId) {
        if (!msgId) {
            msgId = '';
        }
        var url = jsUseCtxPath + "/jsbz/editMsgPage.do?msgId="+ msgId+"&action=add&type="+type;
        mini.open({
            title: "编辑列表",
            url: url,
            width: 1000,
            height: 700,
            showModal: true,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                searchFrm();
            }
        });
    }

    function msgDetail(msgId) {
        var action = "detail";
        var url = jsUseCtxPath + "/jsbz/editMsgPage.do?msgId=" + msgId+"&action="+action+"&type="+type;
        mini.open({
            title: "编辑列表",
            url: url,
            width: 1000,
            height: 700,
            showModal: true,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                searchFrm();
            }
        });
    }
    function msgEdit(msgId) {
        var action = "edit";
        var url = jsUseCtxPath + "/jsbz/editMsgPage.do?msgId=" + msgId+"&action="+action+"&type="+type;
        mini.open({
            title: "编辑列表",
            url: url,
            width: 1000,
            height: 700,
            showModal: true,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                searchFrm();
            }
        });
    }
    function removeMsg(msgId) {
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/jsbz/deleteMsg.do",
                    method: 'POST',
                    showMsg:false,
                    data: {id: msgId},
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
<redxun:gridScript gridId="msgListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>