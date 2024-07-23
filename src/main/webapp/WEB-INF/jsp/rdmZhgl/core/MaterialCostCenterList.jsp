<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>成本中心列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">成本中心编码: </span><input
                        class="mini-textbox" style="width: 150px" name="deptCode"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">成本中心名称: </span><input
                        class="mini-textbox" style="width: 150px" name="deptName"/></li>
            </ul>
        </form>
        <ul class="toolBtnBox">
            <li>
                <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
            </li>
            <li>
                <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
            </li>
            <li>
                <a class="mini-button" style="margin-right: 5px" plain="true" onclick="asyncCostCenter()">同步成本中心</a>
            </li>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="costCenterListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/rdmZhgl/core/material/costCenter/costCenterList.do" idField="id"
         multiSelect="false" showColumnsMenu="false" sizeList="[15,20,50,100,200]" pageSize="15" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="30">序号</div>
            <div field="deptCode" width="120" headerAlign="center" align="center" allowSort="true">成本中心编码</div>
            <div field="deptName" width="150" headerAlign="center" align="center" allowSort="true">成本中心名称</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var costCenterListGrid = mini.get("costCenterListGrid");

    function asyncCostCenter() {
        var config = {
            url: jsUseCtxPath + "/rdmZhgl/core/material/costCenter/asyncCostCenter.do",
            method: 'POST',
            data: {},
            success: function (result) {
                searchFrm();
            }
        }
        _SubmitJson(config);
    }
</script>
<redxun:gridScript gridId="costCenterListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
