<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>名称列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">作业类别: </span>
                    <input name="workTypeId" class="mini-combobox" onvaluechanged="searchFrm()"
                           style="width:150px;" emptyText="请选择..." showNullItem="true" nullItemText="请选择..."
                           textField="workName" valueField="workId" emptyText="请选择..."
                           url="${ctxPath}/gkgf/core/workType/dictWorkType.do"
                           allowInput="false"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">板块: </span><input class="mini-textbox" style="width: 150px" name="plateName"/></li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
            </ul>
        </form>
        <!--导出Excel相关HTML-->
        <form id="excelForm" action="${ctxPath}/assetsManager/core/assetReceive/export.do" method="post"
              target="excelIFrame">
            <input type="hidden" name="pageIndex" id="pageIndex"/>
            <input type="hidden" name="pageSize" id="pageSize"/>
            <input type="hidden" name="filter" id="filter"/>
        </form>
        <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
        <ul class="toolBtnBox">
            <div style="display: inline-block" class="separator"></div>
            <a id="addButton" class="mini-button" style="margin-right: 5px" plain="true" img="${ctxPath}/scripts/mini/miniui/res/images/add.png" onclick="addItems()">新增</a>
            <a id="delButton" class="mini-button"  img="${ctxPath}/scripts/mini/miniui/res/images/cancel.png" onclick="removeRow()">删除</a>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/gkgf/core/plate/listData.do" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="30">序号</div>
            <div name="action" cellCls="actionIcons" width="60" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="workName" name="workName" width="120" headerAlign="center" align="center" allowSort="false">作业类别</div>
            <div field="plateName" width="120" headerAlign="center" align="center" allowSort="false">板块</div>
            <div field="plateCode" width="120" headerAlign="center" align="center" allowSort="false">板块编码</div>
            <div field="responseUserNames" width="80" headerAlign="center" align="center" allowSort="false">责任人</div>
            <div field="updateName" width="80" headerAlign="center" align="center" allowSort="true">更新人</div>
            <div field="UPDATE_TIME_" width="80" headerAlign="center" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" allowSort="true">更新时间</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var listGrid = mini.get("listGrid");
    var isAdmin = ${isAdmin};
    listGrid.on("load", function () {
        listGrid.mergeColumns(["workName"]);
    });
    if (!isAdmin) {
        mini.get("addButton").setEnabled(false);
        mini.get("delButton").setEnabled(false);
    }
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var s = '';
        if(isAdmin){
            s += '<span  title="编辑" onclick="viewForm(\'' + id + '\',\'edit\')">编辑</span>';
        }else{
            s += '<span  title="编辑" style="color: silver">编辑</span>';
        }

        return s;
    }
    function addItems() {
        var action = "add";
        let url = jsUseCtxPath + "/gkgf/core/plate/editPage.do?action="+action;
        var title = "新增";
        mini.open({
            title: title,
            url: url,
            width: 800,
            height: 500,
            showModal: true,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                searchFrm();
            }
        });
    }
    //查看
    function viewForm(id,action) {
        var url= jsUseCtxPath +"/gkgf/core/plate/editPage.do?action="+action+"&id="+id;
        var title = "修改";
        if(action=='view'){
            title = "查看";
        }
        mini.open({
            title: title,
            url: url,
            width: 1000,
            height: 800,
            showModal: true,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                searchFrm();
            }
        });
    }
    //删除记录
    function removeRow() {
        var rows = [];
        rows = listGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定取消选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var ids = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    ids.push(r.id);
                }
                if (ids.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/gkgf/core/plate/remove.do",
                        method: 'POST',
                        data: {ids: ids.join(',')},
                        success: function (text) {
                            searchFrm();
                        }
                    });
                }

            }
        });
    }
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
