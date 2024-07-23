<%--
  Created by IntelliJ IDEA.
  User: matianyu
  Date: 2021/2/23
  Time: 14:57
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>列表管理</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">用途说明: </span>
                    <input class="mini-textbox" id="usedirection" name="usedirection"/>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a id="addNew" class="mini-button " style="margin-right: 5px" plain="true"
                       onclick="addNew()">新增</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
			</span>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="toolListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         sizeList="[50,100,200]" pageSize="50"
         url="${ctxPath}/researchTool/tool/queryTool.do?type=fztool"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <div name="action" cellCls="actionIcons" width="30" headerAlign="center" align="center"
                 renderer="onMessageActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="name" width="50" headerAlign='center' align='left'>名称</div>
            <div field="size" width="30" headerAlign="center" align="center" allowSort="true">大小</div>
            <div field="usedirection" headerAlign='center' align='left'>用途说明</div>
            <div field="applicable" headerAlign='center' align='center' width="40">适用人员（部门）</div>
            <div field="reperson" width="25" headerAlign="center" align="center" allowSort="true">归口人（部门）
            </div>
            <div field="download" headerAlign='center' align='center' width="40" renderer="downloadAdress">下载地址（需要登录网盘）
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var toolListGrid = mini.get("toolListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";


    toolListGrid.on("update", function (e) {
        handGridButtons(e.sender.el);
    });
    if (currentUserNo != "admin") {
        mini.get("addNew").setEnabled(false);
    }

    function addNew(toolid) {
        if (!toolid) {
            toolid = '';
        }
        var url = jsUseCtxPath + "/researchTool/tool/editPage.do?toolid=" + toolid;
        mini.open({
            title: "编辑列表",
            url: url,
            width: 800,
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

    function onMessageActionRenderer(e) {
        var record = e.record;
        var toolid = record.toolid;
        var s = '';
        if (currentUserNo == 'admin') {
            s += '<span  title="编辑" onclick="addNew(\'' + toolid + '\',\'' + toolid + '\')">编辑</span>';
            s += '<span  title="删除" onclick="removeTool(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }

    function removeTool(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = toolListGrid.getSelecteds();
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
                    rowIds.push(r.toolid);
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/researchTool/tool/deleteTool.do",
                    method: 'POST',
                    showMsg: false,
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

    function downloadAdress(e) {
        var record = e.record;
        var url = record.download;
        var url = url;
        var linkStr='<a href="#" style="color:#0044BB;" onclick="window.open(\'' + url+ '\')">'+url+'</a>';
        return linkStr;
    }
</script>
<redxun:gridScript gridId="toolListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
