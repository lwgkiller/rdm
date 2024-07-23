<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>通报列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/productConfigList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
            </ul>
        </form>
        <!--导出Excel相关HTML-->
        <form id="excelForm" action="${ctxPath}/rdmZhgl/core/monthWork/exportProjectPlanExcel.do" method="post" target="excelIFrame">
            <input type="hidden" name="pageIndex" />
            <input type="hidden" name="pageSize"/>
            <input type="hidden" name="filter" id="filter" />
        </form>
        <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
        <ul class="toolBtnBox">
            <a  class="mini-button" plain="true" onclick="addForm()">新增</a>
            <div style="display: inline-block" class="separator"></div>
            <a class="mini-button"  onclick="editForm()">编辑</a>
            <a class="mini-button btn-red" style="margin-left: 10px" plain="true" onclick="removeRow()">删除</a>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height:  100%;">
    <div id="planListGrid" class="mini-datagrid"  allowResize="true" style="height:  100%;"
         url="${ctxPath}/rdmZhgl/core/productConfig/list.do" idField="id" showPager="false" allowCellWrap="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]"  allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn"  field="mainId" name="mainId" width="20"></div>
            <div type="indexcolumn"   align="center" headerAlign="center" width="20">序号</div>
            <div field="deptName" name="deptName" width="100" headerAlign="center" align="center" allowSort="false">部门名称</div>
            <div field="mainSort" name="mainSort" width="50" headerAlign="center" align="center" allowSort="false">排序</div>
            <div field="itemName" name="itemName" width="100" headerAlign="center" align="center" allowSort="false">节点名称</div>
            <div field="itemCode" name="itemCode" width="100" headerAlign="center" align="center" allowSort="false">节点编码</div>
            <div field="deliveryName" name="deliveryName" width="100" headerAlign="center" align="center" allowSort="false">项目交付物</div>
            <div field="deptNames" name="deptNames" width="100" headerAlign="center" align="center" allowSort="false">负责部门</div>
            <div field="sort" name="sort" width="50" headerAlign="center" align="center" allowSort="false">节点排序</div>
            <div field="CREATE_TIME_"  width="100" headerAlign="center" align="center" allowSort="false">添加日期</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUser.userId}";
    var planListGrid = mini.get("planListGrid");
    planListGrid.on("load", function () {
        planListGrid.mergeColumns(["mainId","deptName","mainSort"]);
    });
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var s = '<span  title="删除" onclick="viewForm(\'' + id + '\',\'view\')">删除</span>';
            s += '<span  title="编辑" onclick="viewForm(\'' + id + '\',\'edit\')">编辑</span>';
        return s;
    }
</script>
<redxun:gridScript gridId="planListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
