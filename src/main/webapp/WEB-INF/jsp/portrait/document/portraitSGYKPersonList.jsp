<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>三高一可列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/portrait/sgyk/portraitSGYKList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="sgykListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/portrait/sgyk/sgykPersonList.do?userId=${userId}&&reportYear=${reportYear}" idField="id" showPager="false"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="10" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="indexcolumn" align="center" headerAlign="center" width="30">序号</div>
            <div field="yearMonth" width="80" headerAlign="center" align="center" allowSort="true" >年月</div>
            <div field="indexName" width="120" headerAlign="center" align="center" allowSort="true" >指标名称</div>
            <div field="indexValue" width="120" headerAlign="center" align="center" allowSort="true" >目标值</div>
            <div field="indexScore" width="120" headerAlign="center" align="center" allowSort="true" >指标分值</div>
            <div field="finishRate" width="80" headerAlign="center" align="center" allowSort="true" >完成情况</div>
            <div field="weight" width="80" headerAlign="center" align="center" allowSort="true" >权重</div>
            <div field="score" width="80" headerAlign="center" align="center" allowSort="false">得分</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var sgykListGrid = mini.get("sgykListGrid");
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var s = '<span  title="查看" onclick="viewForm(\'' + id + '\',\'view\')">查看</span>';
            s += '<span  title="编辑" onclick="viewForm(\'' + id + '\',\'edit\')">编辑</span>';
        return s;
    }
</script>
<redxun:gridScript gridId="sgykListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
