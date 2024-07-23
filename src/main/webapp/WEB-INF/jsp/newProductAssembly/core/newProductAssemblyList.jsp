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
                    <span class="text" style="width:auto">产品型号：</span>
                    <input class="mini-textbox" id="designModel" name="designModel" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">整机编号：</span>
                    <input class="mini-textbox" id="pin" name="pin" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">产品种类：</span>
                    <input id="productCategory" name="productCategory"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=productCategory"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">产品部门：</span>
                    <input class="mini-textbox" id="productDep" name="productDep" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">项目负责人：</span>
                    <input class="mini-textbox" id="projectLeader" name="projectLeader" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">计划类型：</span>
                    <input id="planCategory" name="planCategory"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=planCategory"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">订单下达时间：</span>
                    <input id="orderReleaseTimeBegin" name="orderReleaseTimeBegin" class="mini-datepicker" format="yyyy-MM-dd"
                           showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">至：</span>
                    <input id="orderReleaseTimeEnd" name="orderReleaseTimeEnd" class="mini-datepicker" format="yyyy-MM-dd"
                           showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">样机上线时间：</span>
                    <input id="prototypeOnLineTimeBegin" name="prototypeOnLineTimeBegin" class="mini-datepicker" format="yyyy-MM-dd"
                           showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">至：</span>
                    <input id="prototypeOnLineTimeEnd" name="prototypeOnLineTimeEnd" class="mini-datepicker" format="yyyy-MM-dd"
                           showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">样机下线时间：</span>
                    <input id="prototypeOutLineTimeBegin" name="prototypeOutLineTimeBegin" class="mini-datepicker" format="yyyy-MM-dd"
                           showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">至：</span>
                    <input id="prototypeOutLineTimeEnd" name="prototypeOutLineTimeEnd" class="mini-datepicker" format="yyyy-MM-dd"
                           showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                </li>
                <%--<li style="margin-right: 15px">--%>
                    <%--<span class="text" style="width:auto">物料部装时间：</span>--%>
                    <%--<input id="materialDepLoadingTimeBegin" name="materialDepLoadingTimeBegin" class="mini-datepicker" format="yyyy-MM-dd"--%>
                           <%--showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>--%>
                <%--</li>--%>
                <%--<li style="margin-right: 15px">--%>
                    <%--<span class="text-to" style="width:auto">至：</span>--%>
                    <%--<input id="materialDepLoadingTimeEnd" name="materialDepLoadingTimeEnd" class="mini-datepicker" format="yyyy-MM-dd"--%>
                           <%--showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>--%>
                <%--</li>--%>
                <%--<li style="margin-right: 15px">--%>
                    <%--<span class="text" style="width:auto">下车装配时间：</span>--%>
                    <%--<input id="downAssemblyTimeBegin" name="downAssemblyTimeBegin" class="mini-datepicker" format="yyyy-MM-dd"--%>
                           <%--showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>--%>
                <%--</li>--%>
                <%--<li style="margin-right: 15px">--%>
                    <%--<span class="text-to" style="width:auto">至：</span>--%>
                    <%--<input id="downAssemblyTimeEnd" name="downAssemblyTimeEnd" class="mini-datepicker" format="yyyy-MM-dd"--%>
                           <%--showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>--%>
                <%--</li>--%>
                <%--<li style="margin-right: 15px">--%>
                    <%--<span class="text" style="width:auto">上车装配时间：</span>--%>
                    <%--<input id="upAssemblyTimeBegin" name="upAssemblyTimeBegin" class="mini-datepicker" format="yyyy-MM-dd"--%>
                           <%--showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>--%>
                <%--</li>--%>
                <%--<li style="margin-right: 15px">--%>
                    <%--<span class="text-to" style="width:auto">至：</span>--%>
                    <%--<input id="upAssemblyTimeEnd" name="upAssemblyTimeEnd" class="mini-datepicker" format="yyyy-MM-dd"--%>
                           <%--showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>--%>
                <%--</li>--%>
                <%--<li style="margin-right: 15px">--%>
                    <%--<span class="text" style="width:auto">合车装配时间：</span>--%>
                    <%--<input id="combinedAssemblyTimeBegin" name="combinedAssemblyTimeBegin" class="mini-datepicker" format="yyyy-MM-dd"--%>
                           <%--showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>--%>
                <%--</li>--%>
                <%--<li style="margin-right: 15px">--%>
                    <%--<span class="text-to" style="width:auto">至：</span>--%>
                    <%--<input id="combinedAssemblyTimeEnd" name="combinedAssemblyTimeEnd" class="mini-datepicker" format="yyyy-MM-dd"--%>
                           <%--showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>--%>
                <%--</li>--%>
                <%--<li style="margin-right: 15px">--%>
                    <%--<span class="text" style="width:auto">工作装置装配时间：</span>--%>
                    <%--<input id="workingDeviceAssemblyTimeBegin" name="workingDeviceAssemblyTimeBegin" class="mini-datepicker" format="yyyy-MM-dd"--%>
                           <%--showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>--%>
                <%--</li>--%>
                <%--<li style="margin-right: 15px">--%>
                    <%--<span class="text-to" style="width:auto">至：</span>--%>
                    <%--<input id="workingDeviceAssemblyTimeEnd" name="workingDeviceAssemblyTimeEnd" class="mini-datepicker" format="yyyy-MM-dd"--%>
                           <%--showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>--%>
                <%--</li>--%>
                <%--<li style="margin-right: 15px">--%>
                    <%--<span class="text" style="width:auto">整机调试时间：</span>--%>
                    <%--<input id="wholeCommissionTimeBegin" name="wholeCommissionTimeBegin" class="mini-datepicker" format="yyyy-MM-dd"--%>
                           <%--showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>--%>
                <%--</li>--%>
                <%--<li style="margin-right: 15px">--%>
                    <%--<span class="text-to" style="width:auto">至：</span>--%>
                    <%--<input id="wholeCommissionTimeEnd" name="wholeCommissionTimeEnd" class="mini-datepicker" format="yyyy-MM-dd"--%>
                           <%--showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>--%>
                <%--</li>--%>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">样机转序时间：</span>
                    <input id="prototypeSequenceTimeBegin" name="prototypeSequenceTimeBegin" class="mini-datepicker" format="yyyy-MM-dd"
                           showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">至：</span>
                    <input id="prototypeSequenceTimeEnd" name="prototypeSequenceTimeEnd" class="mini-datepicker" format="yyyy-MM-dd"
                           showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">实际计划年份：</span>
                    <input id="realYear" name="realYear"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="newproductAssembly-searchFrm" onclick="searchFrm()" showNoRight="false" style="margin-right: 5px">查询</f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <f:a alias="newproductAssembly-addBusiness" onclick="addBusiness()" showNoRight="false" style="margin-right: 5px">新增</f:a>
                    <f:a alias="newproductAssembly-copyBusiness" onclick="copyBusiness()" showNoRight="false" style="margin-right: 5px">复制</f:a>
                    <f:a alias="newproductAssembly-editBusiness" onclick="editBusiness()" showNoRight="false" style="margin-right: 5px">编辑</f:a>
                    <f:a alias="newproductAssembly-removeBusiness" onclick="removeBusiness()" showNoRight="false" style="margin-right: 5px">删除</f:a>
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
         allowCellWrap="true"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" oncellvalidation="onCellValidation"
         multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="50" allowAlternating="true"
         pagerButtons="#pagerButtons"
         url="${ctxPath}/newproductAssembly/core/kanban/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="40" trueValue="1" falseValue="0"></div>
            <div type="indexcolumn" width="45" headerAlign="center" align="center" allowSort="true">序号</div>
            <div name="action" cellCls="actionIcons" width="85" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">
                操作
            </div>
            <div field="designModel" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">产品型号</div>
            <div field="testQuantity" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">试制台数</div>
            <div field="theExplain" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">说明</div>
            <div field="pin" width="180" headerAlign="center" align="center" allowSort="true" renderer="render">整机编号</div>
            <div field="productCategory" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">产品种类</div>
            <div field="productDep" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">产品部门</div>
            <div field="projectLeader" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">项目负责人</div>
            <div field="planCategory" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">计划类型</div>
            <div field="orderReleaseTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="renderColor">订单下达时间</div>
            <div field="prototypeOnLineTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="renderColor">样机上线时间</div>
            <div field="prototypeOutLineTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="renderColor">样机下线时间</div>
            <%--<div field="materialDepLoadingTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="renderColor">物料部装时间</div>--%>
            <%--<div field="downAssemblyTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="renderColor">下车装配时间</div>--%>
            <%--<div field="upAssemblyTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="renderColor">上车装配时间</div>--%>
            <%--<div field="combinedAssemblyTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="renderColor">合车装配时间</div>--%>
            <%--<div field="workingDeviceAssemblyTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="renderColor">工作装置装配时间</div>--%>
            <%--<div field="wholeCommissionTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="renderColor">整机调试时间</div>--%>
            <div field="prototypeSequenceTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="renderColor">样机转序时间</div>
            <div field="realYear" width="100" headerAlign="center" align="center" allowSort="true">实际计划年份</div>
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
        if (record.testLeaderId == currentUserId) {
            s += '<span  title="编辑" onclick="editBusiness(\'' + businessId + '\')">编辑</span>';
        }
        return s;
    }
    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..
    function addBusiness() {
        var url = jsUseCtxPath + "/newproductAssembly/core/kanban/editPage.do?businessId=&action=add";
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
    function editBusiness() {
        var row = businessListGrid.getSelected();
        if (!row) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var url = jsUseCtxPath + "/newproductAssembly/core/kanban/editPage.do?businessId=" + row.id + "&action=edit";
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
    function copyBusiness() {
        var row = businessListGrid.getSelected();
        if (!row) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var url = jsUseCtxPath + "/newproductAssembly/core/kanban/editPage.do?businessId=" + row.id + "&action=copy";
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
                    rowIds.push(r.id);
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/newproductAssembly/core/kanban/deleteBusiness.do",
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
        var url = jsUseCtxPath + "/newproductAssembly/core/kanban/editPage.do?businessId=" + businessId + "&action=detail";
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
        var url = jsUseCtxPath + "/newproductAssembly/core/kanban/editPage.do?businessId=" + businessId + "&action=edit";
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
    function editBusiness() {
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var url = jsUseCtxPath + "/newproductAssembly/core/kanban/editPage.do?businessId=" + row.id + "&action=edit";
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
    function renderColor(e) {
        if (e.value != null && e.value != '') {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 30px;background-color: #0bb20c" >' + e.value + '</span>';
        }
        return html;
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>