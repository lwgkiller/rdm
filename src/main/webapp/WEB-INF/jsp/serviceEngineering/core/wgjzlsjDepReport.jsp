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
                <li style="margin-right: 15px;display: none">
                    <span class="text" style="width:auto">资料类型：</span>
                    <input id="dataType" name="dataType" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[{key:'wxscl',value:'维修手册类'},{key:'ljtcl',value:'零件图册类'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">产品所：</span>
                    <input class="mini-textbox" id="materialDepartment" name="materialDepartment"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="wgjzlsjDepGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="true"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/wgjzlsj/departmentReportListQuery.do">
        <div property="columns">
            <div type="indexcolumn" width="35" headerAlign="center" align="center">序号</div>
            <div field="materialDepartment" name="materialDepartment" headerAlign="center" align="center">产品所</div>
            <div field="dataType" headerAlign="center" align="center" width="150">资料类型</div>
            <div field="total" headerAlign="center" align="center">需求总数</div>
            <div field="ysjTotal" headerAlign="center" align="center">已收集</div>
            <div field="ysjLv" headerAlign="center" align="center">收集完成率(%)</div>
            <div field="yzzTotal" headerAlign="center" align="center">已制作</div>
            <div field="yzzLv" headerAlign="center" align="center">制作完成率(%)</div>
            <div field="firstToal" headerAlign="center" align="center">一级响应总数</div>
            <div field="secondTotal" headerAlign="center" align="center">二级响应总数</div>
            <div field="thirdTotal" headerAlign="center" align="center">三级响应总数</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var wgjzlsjDepGrid = mini.get("wgjzlsjDepGrid");

    //必须为要合并的列增加name属性
    wgjzlsjDepGrid.on("load", function () {
        wgjzlsjDepGrid.mergeColumns(["materialDepartment"]);
    })
    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }

    function dataTypeRenderer(e) {
        var record = e.record;
        var arr = [{key:'wxscl',value:'维修手册类'},{key:'ljtcl',value:'零件图册类'}];
        return $.formatItemValue(arr, record.dataType);
    }
</script>
<redxun:gridScript gridId="wgjzlsjDepGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>