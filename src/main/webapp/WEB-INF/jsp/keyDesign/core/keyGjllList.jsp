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
                    <span class="text" style="width:auto">零部件代号/型号: </span>
                    <input class="mini-textbox" id="model" name="model" />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a id="editMsg" class="mini-button " style="margin-right: 5px" plain="true"
                       onclick="addNew()">新增</a>
                    <a id="addGjll" class="mini-button" style="margin-right: 5px" plain="true" onclick="addGjll()">关联改进履历</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
			</span>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="gjllListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         sizeList="[50,100,200]" pageSize="50"
         url="${ctxPath}/Gjll/queryList.do?belongbj=${type}"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <div type="indexcolumn" align="center" width="10">序号</div>
            <div name="action" cellCls="actionIcons" width="25" headerAlign="center" align="center"
                 renderer="onMessageActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="model" width="15" headerAlign="center" align="center" allowSort="true">零部件代号/型号</div>
            <div field="supplier" width="15" headerAlign="center" align="center" allowSort="true">生产供应商
            </div>
            <div field="problem" width="25" dateFormat="yyyy-MM-dd" headerAlign='center' align="center">问题描述</div>
            <div field="solve" width="20" headerAlign="center" align="center" allowSort="true">改善措施</div>
            <div field="changeFile" width="20" headerAlign="center" align="center" allowSort="true">改进文件</div>
            <div field="userName" headerAlign='center' align='center' width="20">创建人</div>
            <div field="changeTime" sortField="qhTime"  width="20" align="center" headerAlign="center" allowSort="true" dateFormat="yyyy-MM-dd HH:mm:ss">改进时间</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var importWindow = mini.get("importWindow");
    var type="${type}";
    var jsUseCtxPath = "${ctxPath}";
    var gjllListGrid = mini.get("gjllListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserDep = "${currentUserDep}";
    var currentUserNo = "${currentUserNo}";

    function onMessageActionRenderer(e) {
        var record = e.record;
        var gjId = record.gjId;
        var s = '';
        s += '<span  title="查看" onclick="gjllDetail(\'' + gjId + '\')">查看</span>';
        if (currentUserId == record.CREATE_BY_) {
            s += '<span  title="编辑" onclick="gjllEdit(\'' + gjId + '\')">编辑</span>';
            s += '<span  title="删除" onclick="removeGjll(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }

    $(function () {
        searchFrm();
    });



    function addNew() {
        mini.open({
            title: "新增",
            url: jsUseCtxPath + "/Gjll/edit.do?action=add&type="+type,
            width: 1050,
            height: 800,
            allowResize: true,
            onload: function () {
                searchFrm();
            },
            ondestroy:function () {
                searchFrm();
            }
        });
    }

    function gjllEdit(gjId) {
        mini.open({
            title: "新增",
            url: jsUseCtxPath + "/Gjll/edit.do?action=edit&gjId=" + gjId+ "&type="+type,
            width: 1050,
            height: 800,
            allowResize: true,
            onload: function () {
                searchFrm();
            },
            ondestroy:function () {
                searchFrm();
            }
        });
    }

    function gjllDetail(gjId) {
        var action = "detail";
        mini.open({
            title: "明细",
            url: jsUseCtxPath + "/Gjll/edit.do?action=" + action + "&gjId=" + gjId+ "&type="+type,
            width: 1050,
            height: 800,
            allowResize: true,
            onload: function () {
                searchFrm();
            }
        });
    }


    function removeGjll(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = gjllListGrid.getSelecteds();
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
                    rowIds.push(r.gjId);
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/Gjll/deleteGjll.do",
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

    function addGjll() {
        var url = jsUseCtxPath + "/zlgjNPI/core/Gjll/list.do?type=keyDesign&belongbj=" + type;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (gjllListGrid) {
                    gjllListGrid.reload()
                }
            }
        }, 1000);
    }
</script>
<redxun:gridScript gridId="gjllListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
