<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>月度绩效列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-fit" style="height: 100%;">
    <div id="performanceListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/portrait/performance/performancePersonList.do?userId=${userId}&&reportYear=${reportYear}" idField="id" showPager="false"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="10" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="indexcolumn" align="center" headerAlign="center" width="20">序号</div>
            <div field="yearMonth" width="120" headerAlign="center" align="center" allowSort="true">年份</div>
            <div field="ratio" width="80" headerAlign="center" align="center" allowSort="true" >绩效系数</div>
            <div field="score" width="80" headerAlign="center" align="center" allowSort="false">得分</div>
            <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" allowSort="false">添加日期</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var performanceListGrid = mini.get("performanceListGrid");
</script>
<redxun:gridScript gridId="performanceListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
