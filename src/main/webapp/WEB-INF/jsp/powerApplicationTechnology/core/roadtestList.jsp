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
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">整机型号：</span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">整机编号：</span>
                    <input class="mini-textbox" id="pin" name="pin"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">位置：</span>
                    <input class="mini-textbox" id="location" name="location"/>
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="roadtest-searchFrm" onclick="searchFrm()" showNoRight="false" style="margin-right: 5px">查询</f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <f:a alias="roadtest-addBusiness" onclick="addBusiness()" showNoRight="false" style="margin-right: 5px">新增</f:a>
                    <f:a alias="roadtest-removeBusiness" onclick="removeBusiness()" showNoRight="false" style="margin-right: 5px">删除</f:a>
                    <f:a alias="roadtest-startBusiness" onclick="startBusiness()" showNoRight="false" style="margin-right: 5px">开启数据同步</f:a>
                    <f:a alias="roadtest-closeBusiness" onclick="closeBusiness()" showNoRight="false" style="margin-right: 5px">关闭数据同步</f:a>
                    <f:a alias="roadtest-calculateBusiness" onclick="calculateBusiness()" showNoRight="false" style="margin-right: 5px">计算</f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="test()">测试</a>
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
         allowCellWrap="false"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" oncellvalidation="onCellValidation"
         multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/powerApplicationTechnology/core/roadtest/dataListQuery.do" onrowdblclick="onRowDblClick">
        <div property="columns">
            <div type="checkcolumn" width="40"></div>
            <div name="action" cellCls="actionIcons" width="150" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div type="indexcolumn" width="40" headerAlign="center" align="center">序号</div>
            <div field="designModel" width="150" headerAlign="center" align="center">整机型号</div>
            <div field="pin" width="200" headerAlign="center" align="center">整机编号</div>
            <div field="engineModel" width="150" headerAlign="center" align="center">发动机型号</div>
            <div field="location" width="100" headerAlign="center" align="center">位置</div>
            <div field="roadtestStatus" width="100" headerAlign="center" align="center">路试状态</div>
            <div field="roadtestBeginDate" width="100" headerAlign="center" align="center">路试开始日期</div>
            <div field="roadtestEndDate" width="100" headerAlign="center" align="center">路试结束日期</div>
            <div field="roadtestType" width="100" headerAlign="center" align="center">路试类型</div>
            <div field="roadtestContent" width="200" headerAlign="center" align="center">路试内容</div>
            <div field="targetDailyAverageWorkingHours" width="150" headerAlign="center" align="center">日均工作小时(目标)</div>
            <div field="targetDailyAverageWorkingHoursRemaining" width="220" headerAlign="center" align="center">剩余天数预计日均工作小时(目标)</div>
            <div field="actualDailyAverageWorkingHours" width="150" headerAlign="center" align="center" renderer="renderColor">日均工作小时(实际)</div>
            <div field="cumulativeWorkingHours" width="120" headerAlign="center" align="center">累计工作时间(h)</div>
            <div field="cumulativeFuelConsumption" width="120" headerAlign="center" align="center">累计油耗(L)</div>
            <div field="cumulativeUreaConsumption" width="120" headerAlign="center" align="center">累计尿素消耗(L)</div>
            <div field="perhourFuelConsumption" width="120" headerAlign="center" align="center">每小时油耗(L)</div>
            <div field="perhourUreaConsumption" width="150" headerAlign="center" align="center">每小时尿素消耗(L)</div>
            <div field="calculateDate" width="100" headerAlign="center" align="center">计算日期</div>
            <div field="isClose" width="120" headerAlign="center" align="center">是否关闭</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var currentTime = new Date();
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    //..
    function onActionRenderer(e) {
        var record = e.record;
        var businessId = record.id;
        var isClose = record.isClose;
        var s = '<span  title="明细" onclick="businessDetail(\'' + businessId + '\')">明细</span>';
        if (isClose == '否') {
            s += '<span  title="编辑" onclick="businessEdit(\'' + businessId + '\')">编辑</span>';
        }
        //无删除权限的按钮变灰色
        if (currentUserNo == 'admin' || (isClose == '否' && currentUserId == record.CREATE_BY_)) {
            s += '<span  title="删除" onclick="removeBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }
    //..
    function onRowDblClick(e) {
        businessDetail(e.record.id);
    }
    //..
    function businessDetail(businessId) {
        var url = jsUseCtxPath + "/powerApplicationTechnology/core/roadtest/editPage.do?businessId=" + businessId + "&action=detail";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload()
                }
            }
        }, 1000);
    }
    //..
    function businessEdit(businessId) {
        var url = jsUseCtxPath + "/powerApplicationTechnology/core/roadtest/editPage.do?businessId=" + businessId + "&action=edit";
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
    function removeBusiness(record) {
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
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if (currentUserNo == 'admin' || (r.isClose == '否' && currentUserId == r.CREATE_BY_)) {
                        rowIds.push(r.id);
                    }
                }
                if (rowIds.length <= 0) {
                    mini.alert("仅可删除本人创建的尚未关闭的数据！");
                    return;
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/powerApplicationTechnology/core/roadtest/deleteBusiness.do",
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
    //..
    function addBusiness() {
        var url = jsUseCtxPath + "/powerApplicationTechnology/core/roadtest/editPage.do?businessId=&action=add";
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
    function startBusiness() {
        var rows = businessListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定开启数据同步？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.id);
                }
                if (rowIds.length <= 0) {
                    return;
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/powerApplicationTechnology/core/roadtest/start.do",
                    method: 'POST',
                    data: {ids: rowIds.join(',')},
                    showMsg: false,
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
    //..
    function closeBusiness() {
        var rows = businessListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定关闭数据同步？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.id);
                }
                if (rowIds.length <= 0) {
                    return;
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/powerApplicationTechnology/core/roadtest/close.do",
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
    //..
    function calculateBusiness() {
        var rows = businessListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定进行数据计算？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.id);
                }
                if (rowIds.length <= 0) {
                    return;
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/powerApplicationTechnology/core/roadtest/calculate.do",
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
    //..
    function renderColor(e) {
        debugger;
        if (parseFloat(e.value) < parseFloat(e.record.targetDailyAverageWorkingHours)) {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 30px;background-color: #ef1b01" >' + e.value + '</span>';
        } else {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 30px;background-color: #0bb20c" >' + e.value + '</span>';
        }
        return html;
    }
    //..
    function test() {
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert("请至选中一条记录");
            return;
        }
        _SubmitJson({
            url: jsUseCtxPath + "/powerApplicationTechnology/core/roadtest/test.do",
            method: 'POST',
            showMsg: false,
            data: {id: row.pin}
        });
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>