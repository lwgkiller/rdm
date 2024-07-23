<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>出口产品零件图册需求通知单列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <input class="mini-hidden" id="isComplex" name="isComplex"/>
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">需求通知单编号：</span>
                    <input class="mini-textbox" name="demandNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">出口国家：</span>
                    <input class="mini-textbox" name="exportCountryName"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">编制日期：</span>
                    <input id="applyDateBegin" name="applyDateBegin" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                           valueType="string" showTime="false" showOkButton="true" showClearButton="false" style="width:98%;"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">至：</span>
                    <input id="applyDateEnd" name="applyDateEnd" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                           valueType="string" showTime="false" showOkButton="true" showClearButton="false" style="width:98%;"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">编制人：</span>
                    <input class="mini-textbox" name="creatorName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">整机分配状态：</span>
                    <input name="finishStatus" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : 'no','value' : '未完成'},{'key' : 'yes','value' : '已完成'}]"
                    />
                </li>

                <li style="margin-left: 10px">
                    <a class="mini-button" onclick="searchFrm()" style="margin-right: 5px">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportBusinessThis()">导出</a>
                </li>
            </ul>
        </form>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="true" showCellTip="true" idField="id"  allowHeaderWrap="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/exportPartsAtlas/demandListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="35" headerAlign="center" align="center">序号</div>
            <div name="action" cellCls="actionIcons" width="150" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="demandNo" width="200" headerAlign="center" align="center" allowSort="true" >需求通知单编号</div>
            <div field="exportCountryName" width="100" headerAlign="center" align="center" allowSort="true" >出口国家</div>
            <div field="creatorName" width="100" headerAlign="center" align="center" allowSort="true" >编制人</div>
            <div field="CREATE_TIME_" width="100" headerAlign="center" align="center" allowSort="true" dateFormat="yyyy-MM-dd">编制日期</div>
            <div width="200" headerAlign="center" align="center" renderer="machineFinishNumRenderer">整机分配数量（已分配/总数）</div>
            <div width="200" headerAlign="center" align="center" renderer="machineFinishStatusRenderer">整机分配状态</div>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/exportPartsAtlas/exportList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";


    //操作按钮
    function onActionRenderer(e) {
        var record = e.record;
        var demandId = record.id;
        var s = '';
        s += '<span  title="查看" onclick="demandInfoCheck(\'' + demandId + '\')">查看</span>';

        return s;
    }


    //查看
    function demandInfoCheck(demandId) {
        var url = jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/demandEditPage.do?demandId=" + demandId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
                }
            }
        }, 1000);
    }

    //完成数量
    function machineFinishNumRenderer(e) {
        var record = e.record;
        var str=record.doneNumber+"/"+record.totalNumber;
        if(record.finishStatus=='yes') {
            return '<span style="color: blue">'+str+'</span>';
        } else {
            return '<span style="color: red">'+str+'</span>';
        }
    }

    //完成状态
    function machineFinishStatusRenderer(e) {
        var record = e.record;
        if(record.finishStatus=='yes') {
            return '<span style="color: blue">已完成</span>';
        } else {
            return '<span style="color: red">未完成</span>';
        }
    }

    function exportBusinessThis() {
        var parent = $(".search-form");
        var inputAry = $("input", parent);
        var params = [];
        inputAry.each(function (i) {
            var el = $(this);
            var obj = {};
            obj.name = el.attr("name");
            if (!obj.name) return true;
            obj.value = el.val();
            params.push(obj);
        });
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }

</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>