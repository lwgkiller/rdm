<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">被拜访公司：</span>
                    <input class="mini-textbox" id="companyVisited" name="companyVisited" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">拜访目的：</span>
                    <input class="mini-textbox" id="purposeVisited" name="purposeVisited" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">编制人：</span>
                    <input class="mini-textbox" id="creator" name="creator" style="width:100%;"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">任务执行人：</span>
                    <input class="mini-textbox" id="taskExecutor" name="taskExecutor"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addBusiness()">新增</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="removeBusiness()">删除</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="executeBusiness()">执行</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="true" showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="50" allowAlternating="true"
         pagerButtons="#pagerButtons" url="${ctxPath}/world/core/customerVisitRecord/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="40" trueValue="1" falseValue="0"></div>
            <div name="action" cellCls="actionIcons" width="85" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">
                操作
            </div>
            <div field="companyVisited" width="150" headerAlign="center" align="center" allowSort="true" renderer="render">被拜访公司</div>
            <div field="dateVisitedBegin" width="80" headerAlign="center" align="center" allowSort="true" renderer="render">访问开始日期</div>
            <div field="dateVisitedEnd" width="80" headerAlign="center" align="center" allowSort="true" renderer="render">访问结束日期</div>
            <div field="purposeVisited" width="300" headerAlign="center" align="center" allowSort="true" renderer="render">拜访目的</div>
            <div field="creator" width="80" headerAlign="center" align="center" allowSort="true" renderer="render">编制人</div>
            <div field="taskExecutor" width="80" headerAlign="center" align="center" allowSort="true" renderer="render">任务执行人</div>
            <div field="businessStatus" width="80" headerAlign="center" align="center" allowSort="true" renderer="render">状态</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    //..
    function onActionRenderer(e) {
        var record = e.record;
        var businessId = record.id;
        var s = '<span  title="明细" onclick="detailBusiness(\'' + businessId + '\')">明细</span>';

        if (record.CREATE_BY_ == currentUserId || record.taskExecutorId == currentUserId ||
            currentUserNo == "admin") {
            s += '<span  title="编辑" onclick="editBusiness(\'' + businessId + '\')">编辑</span>';
        }
        return s;

    }
    //..
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..
    function addBusiness() {
        var url = jsUseCtxPath + "/world/core/customerVisitRecord/editPage.do?businessId=&action=add";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
                }
            }
        }, 1000);
    }
    //..
    function removeBusiness() {
        var rows = businessListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var isok = true;
        for (var i = 0; i < rows.length; i++) {
            if (rows[i].CREATE_BY_ != currentUserId || rows[i].businessStatus != "编辑中") {
                isok = false;
                break;
            }
        }
        if (currentUserNo == "admin") {
            isok = true;
        }
        if (!isok) {
            mini.alert("只有自己创建的，且处于编辑中的记录能够删除！");
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
                    url: jsUseCtxPath + "/world/core/customerVisitRecord/deleteBusiness.do",
                    method: 'POST',
                    data: {ids: rowIds.join(',')},
                    success: function (data) {
                        if (data) {
                            searchFrm();
                        }
                    }
                });
            }
        });
    }
    //..
    function detailBusiness(businessId) {
        var url = jsUseCtxPath + "/world/core/customerVisitRecord/editPage.do?businessId=" + businessId + "&action=detail";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
                }
            }
        }, 1000);
    }
    //..
    function editBusiness(businessId) {
        var url = jsUseCtxPath + "/world/core/customerVisitRecord/editPage.do?businessId=" + businessId + "&action=edit";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
                }
            }
        }, 1000);
    }
    //..
    function executeBusiness() {
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert("请至少选中一条记录");
            return;
        }
        if (row.CREATE_BY_ != currentUserId || row.businessStatus != "编辑中") {
            mini.alert("只有自己创建的，且处于编辑中的记录能够执行！");
            return;
        }
        _SubmitJson({
            url: jsUseCtxPath + "/world/core/customerVisitRecord/executeBusiness.do",
            method: 'POST',
            data: {id: row.id},
            success: function (data) {
                if (data) {
                    searchFrm();
                }
            }
        });
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>