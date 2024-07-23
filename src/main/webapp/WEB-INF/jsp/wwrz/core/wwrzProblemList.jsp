<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>问题列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/wwrz/wwrzProblemList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">流程编号: </span><input
                        class="mini-textbox" style="width: 120px" name="mainId"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">型号: </span><input
                        class="mini-textbox" style="width: 120px" name="productModel"/></li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button" style="margin-left: 5px" plain="true" onclick="exportExcel()">按条件导出</a>
                </li>
            </ul>
        </form>
        <!--导出Excel相关HTML-->
        <form id="excelForm" action="${ctxPath}/wwrz/core/test/exportExcel.do" method="post"
              target="excelIFrame">
            <input type="hidden" name="pageIndex" id="pageIndex"/>
            <input type="hidden" name="pageSize" id="pageSize"/>
            <input type="hidden" name="filter" id="filter"/>
        </form>
        <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/wwrz/core/test/listProblemData.do" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="30">序号</div>
            <div field="mainId" width="120" headerAlign="center" align="center" allowSort="false">流程编号</div>
            <div field="productModel" width="80" headerAlign="center" align="center" allowSort="false">销售型号</div>
            <div field="productType" width="80" headerAlign="center" align="center" allowSort="false"
                 renderer="onProductType">产品类型
            </div>
            <div field="cabForm" width="80" headerAlign="center" align="center" allowSort="false" renderer="onCabForm">
                司机室形式
            </div>
            <div field="itemNames" width="80" headerAlign="center" align="center" allowSort="true">认证项目</div>
            <div field="problem" width="120" headerAlign="center" align="left" allowSort="true">认证问题</div>
            <div field="plan" width="120" headerAlign="center" align="left" allowSort="true">整改方案</div>
            <div field="chargerName" width="80" headerAlign="center" align="center" allowSort="true">方案编制着</div>
            <div field="finish_date" width="80" headerAlign="center" align="center" allowSort="true">整改完成时间</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var listGrid = mini.get("listGrid");
    var currentUserId = "${currentUser.userId}";
    var productTypeList = getDics("CPLX");
    var cabFormList = getDics("SJSXS");
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
