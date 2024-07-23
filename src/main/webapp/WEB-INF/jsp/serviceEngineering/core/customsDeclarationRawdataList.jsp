<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<%--工具栏--%>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">批次号：</span>
                    <input class="mini-textbox" id="batchNo" name="batchNo" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">单据号：</span>
                    <input class="mini-textbox" id="businessNo" name="businessNo" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">提交人：</span>
                    <input class="mini-textbox" id="submitter" name="submitter" style="width:100%;"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">提交时间：</span>
                    <input name="submitTimeBegin" class="mini-datepicker" format="yyyy-MM-dd"
                           showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">至：</span>
                    <input name="submitTimeEnd" class="mini-datepicker" format="yyyy-MM-dd"
                           showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a id="addBusiness" class="mini-button" style="margin-right: 5px" plain="true" onclick="addBusiness()">新增</a>
                    <a id="deleteBusiness" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="deleteBusiness()">删除</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<%--列表视图--%>
<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" idField="id" class="mini-datagrid" style="width: 100%; height: 100%;"
         allowCellWrap="true" showCellTip="true" allowCellEdit="true" allowCellSelect="true"
         allowResize="true" allowAlternating="true" showColumnsMenu="false" multiSelect="true"
         sizeList="[50,100]" pageSize="50" pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/customsDeclarationRawdata/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="40" trueValue="1" falseValue="0"></div>
            <div type="indexcolumn" width="45" headerAlign="center" align="center" allowSort="true">序号</div>
            <div name="action" cellCls="actionIcons" width="85" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="batchNo" width="130" headerAlign="center" align="center" allowSort="true">批次号</div>
            <div field="businessNo" width="130" headerAlign="center" align="center" allowSort="true">单据号</div>
            <div field="submitter" width="130" headerAlign="center" align="center" allowSort="true">提交人</div>
            <div field="submitTime" width="100" headerAlign="center" align="center" allowSort="true" dateFormat="yyyy-MM-dd">提交时间</div>
            <div field="businessStatus" width="100" headerAlign="center" align="center" allowSort="true">状态</div>
            <%--虚字段--%>
            <div field="progress" width="80" headerAlign="center" align="center" allowSort="false" numberFormat="p"
                 name="progress" visible="true">完成率
            </div>
        </div>
    </div>
</div>
<%--JS--%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var isCustomsDeclarationAdmin = '${isCustomsDeclarationAdmin}';
    var businessListGrid = mini.get("businessListGrid");
    //..
    $(function () {
    });
    //..
    function addBusiness() {
        var url = jsUseCtxPath + "/serviceEngineering/core/customsDeclarationRawdata/editPage.do?businessId=&action=add";
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
        var url = jsUseCtxPath + "/serviceEngineering/core/customsDeclarationRawdata/editPage.do?businessId=" + businessId + "&action=edit";
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
    function detailBusiness(businessId) {
        var url = jsUseCtxPath + "/serviceEngineering/core/customsDeclarationRawdata/editPage.do?businessId=" + businessId + "&action=detail";
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
    function deleteBusiness(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = businessListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        for (var i = 0, j = rows.length; i < j; i++) {
            if (rows[i].businessStatus == '已提交' && currentUserNo != 'admin') {
                mini.alert("只有 未提交 的记录能删除");
                return;
            }
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
                    url: jsUseCtxPath + "/serviceEngineering/core/customsDeclarationRawdata/deleteBusiness.do",
                    method: 'POST',
                    data: {ids: rowIds.join(',')},
                    postJson: false,
                    showMsg: false,
                    success: function (returnData) {
                        if (returnData.success) {
                            mini.alert(returnData.message);
                            searchFrm();
                        } else {
                            mini.alert("删除失败:" + returnData.message);
                        }
                    },
                    fail: function (returnData) {
                        mini.alert("删除失败:" + returnData.message);
                    }
                });
            }
        });
    }
    //..操作渲染
    function onActionRenderer(e) {
        var record = e.record;
        var businessId = record.id;
        var s = '<span  title="查看" onclick="detailBusiness(\'' + businessId + '\')">查看</span>';
        if (currentUserNo == 'admin' || isCustomsDeclarationAdmin == "true" ||
            (record.submitterId == currentUserId && record.businessStatus == '编辑中')) {
            s += '<span  title="编辑" onclick="editBusiness(\'' + businessId + '\')">编辑</span>';
        }
        return s;
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>