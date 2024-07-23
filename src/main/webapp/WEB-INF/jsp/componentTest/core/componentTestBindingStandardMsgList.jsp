<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>零部件试验标准绑定消息</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">消息内容: </span>
                    <input class="mini-textbox" id="content" name="content"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addMsg()">新增</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="msgListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/componentTest/core/kanban/queryBindingStandardMsgList.do?mainId=${mainId}" idField="id"
         multiSelect="false" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="content" width="50" headerAlign="center" align="center" allowSort="true">消息内容</div>
            <div field="recName" width="70" headerAlign="center" align="center" allowSort="true">接收人</div>
            <div field="status" width="70" headerAlign="center" align="center" allowSort="true">关联情况</div>
            <div field="userName" headerAlign='center' align='center' width="40">创建人</div>
            <div field="CREATE_TIME_" width="70" headerAlign="center" align="center" allowSort="false">创建时间</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var msgListGrid = mini.get("msgListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var mainId = "${mainId}";
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var CREATE_BY_ = record.CREATE_BY_;
        var s = '';
        s += '<span  title="明细" onclick="msgDetail(\'' + id + '\')">明细</span>';
        if (CREATE_BY_ == currentUserId && (record.status == "已关联" || record.status == "部分关联")) {
            s += '<span  title="删除" onclick="removeMsg(\'' + id + '\')">删除</span>';
        }
        if (record.recId == currentUserId && record.status == "未关联") {
            s += '<span  title="编辑" onclick="msgEdit(\'' + id + '\')">编辑</span>';
        }
        return s;
    }

    //
    function addMsg() {
        var url = jsUseCtxPath + "/componentTest/core/kanban/editBindingStandardMsgPage.do?&action=add&mainId=" + mainId;
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
    //
    function msgDetail(id) {
        var url = jsUseCtxPath + "/componentTest/core/kanban/editBindingStandardMsgPage.do?&action=detail&id=" + id;
        mini.open({
            title: "明细列表",
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
    //
    function msgEdit(id) {
        var url = jsUseCtxPath + "/componentTest/core/kanban/editBindingStandardMsgPage.do?&action=edit&id=" + id;
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
    //
    function removeMsg(id) {
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/componentTest/core/kanban/deleteBindingStandardMsg.do",
                    method: 'POST',
                    showMsg: false,
                    data: {id: id},
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
<redxun:gridScript gridId="msgListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>