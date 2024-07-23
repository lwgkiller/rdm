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
                    <span class="text" style="width:auto">目录名称: </span>
                    <input class="mini-textbox" id="dirName" name="dirName"/>
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
    <div id="jpzlListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         sizeList="[50,100,200]" pageSize="50"
         url="${ctxPath}/serviceEngineering/core/jpzl/queryJpzl.do"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <div name="action" cellCls="actionIcons" width="30" headerAlign="center" align="center"
                 renderer="onMessageActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="dirName" width="30" headerAlign='center' align='left'>目录名称</div>
            <div field="dirDesc" width="50" headerAlign="center" align="center" allowSort="true">备注说明</div>
            <div field="respUser"width="30" headerAlign='center' align='left'>归口人(部门)</div>
            <div field="dirLink" headerAlign='center' align='center' width="80" renderer="downloadAdress">网盘地址（需要登录网盘）
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var jpzlListGrid = mini.get("jpzlListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";


    jpzlListGrid.on("update", function (e) {
        handGridButtons(e.sender.el);
    });
    function addNew(id) {
        if (!id) {
            id = '';
        }
        var url = jsUseCtxPath + "/serviceEngineering/core/jpzl/editPage.do?id=" + id;
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
        var id = record.id;
        var s = '';
        if (currentUserNo == 'admin') {
            s += '<span  title="编辑" onclick="addNew(\'' + id + '\',\'' + id + '\')">编辑</span>';
            s += '<span  title="删除" onclick="removeJpzl(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }

    function removeJpzl(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = jpzlListGrid.getSelecteds();
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
                    url: jsUseCtxPath + "/serviceEngineering/core/jpzl/deleteJpzl.do",
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
        var url = record.dirLink;
        var url = url;
        var linkStr='<a href="#" style="color:#0044BB;" onclick="window.open(\'' + url+ '\')">'+url+'</a>';
        return linkStr;
    }
</script>
<redxun:gridScript gridId="jpzlListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
