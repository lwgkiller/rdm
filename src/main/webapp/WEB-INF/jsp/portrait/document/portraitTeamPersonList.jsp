<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>技术协同列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-fit" style="height: 100%;">
    <span>RDM论坛</span>
    <div id="bbsListGrid" class="mini-datagrid" style="width: 100%; height: 350px;" allowResize="false" autoload="true"
         url="${ctxPath}/portrait/bbs/bbsPersonList.do?userId=${userId}&&reportYear=${reportYear}" idField="id" showPager="false"
         multiSelect="false" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="10" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="indexcolumn" align="center" headerAlign="center" width="30">序号</div>
            <div field="title" width="120" headerAlign="center" align="center" allowSort="true">标题</div>
            <div field="direction" width="80" headerAlign="center" align="center" allowSort="true" >主题</div>
            <div field="score" width="80" headerAlign="center" align="center" allowSort="false">得分</div>
            <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" allowSort="false">同步日期</div>
        </div>
    </div>
    <span>专利解读</span>
    <div id="patentListGrid" class="mini-datagrid" style="width: 100%; height: 350px;" allowResize="false" autoload="true"
         url="${ctxPath}/portrait/patent/patentReadPersonList.do?userId=${userId}&&reportYear=${reportYear}" idField="id" showPager="false"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="10" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="indexcolumn" align="center" headerAlign="center" width="30">序号</div>
            <div field="patentPublicationNo" width="80" headerAlign="center" align="center" allowSort="true">专利公开（公告）号</div>
            <div field="patentName" width="100" headerAlign="center" align="left" allowSort="true" >专利名称</div>
            <div field="score" width="80" headerAlign="center" align="center" allowSort="false">得分</div>
            <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" allowSort="false">同步日期</div>
        </div>
    </div>
    <span>情报报告</span>
    <div id="informationListGrid" class="mini-datagrid" style="width: 100%; height:350px;" allowResize="false" autoload="true"
         url="${ctxPath}/portrait/information/informationPersonList.do?userId=${userId}&&reportYear=${reportYear}" idField="id" showPager="false"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="10" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="indexcolumn" align="center" headerAlign="center" width="30">序号</div>
            <div field="companyName" width="80" headerAlign="center" align="center" allowSort="true">公司</div>
            <div field="projectName" width="80" headerAlign="center" align="center" allowSort="true" >项目</div>
            <div field="qbgzType" width="80" headerAlign="center" align="center" allowSort="true" >类别</div>
            <div field="qbName" width="80" headerAlign="center" align="center" allowSort="true" >情报名称</div>
            <div field="score" width="80" headerAlign="center" align="center" allowSort="false">得分</div>
            <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" allowSort="false">同步日期</div>
        </div>
    </div>
    <span>分析改进</span>
    <div id="analysisImproveListGrid" class="mini-datagrid" style="width: 100%; height: 350px;" allowResize="false" autoload="true"
         url="${ctxPath}/portrait/analysisImprove/analysisImprovePersonList.do?userId=${userId}&&reportYear=${reportYear}" idField="id" showPager="false"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="10" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="indexcolumn" align="center" headerAlign="center" width="30">序号</div>
            <div field="score" width="40" headerAlign="center" align="center" allowSort="true">得分</div>
            <div field="cqnode" width="100" headerAlign="center" align="center" allowSort="true">审批阶段</div>
            <div field="delayDay" width="60" headerAlign="center" align="center" allowSort="true">延期天数</div>
            <div field="zjbh" width="100" headerAlign="center" align="center" >整机编号</div>
            <div field="wtms" width="250" headerAlign="center" align="left" >问题描述</div>
            <div field="applyYear" width="60" headerAlign="center" align="center" allowSort="true">申请年份</div>
            <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" allowSort="true">同步日期</div>
        </div>
    </div>
    <span>合同管理</span>
    <div id="contractListGrid" class="mini-datagrid" style="width: 100%;  height:350px;" allowResize="false" autoload="true"
         url="${ctxPath}/portrait/contract/contractPersonList.do?userId=${userId}&&reportYear=${reportYear}" idField="id" showPager="false"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="10" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="indexcolumn" align="center" headerAlign="center" width="30">序号</div>
            <div field="contractNo" width="60" headerAlign="center" align="center" allowSort="true">合同编号</div>
            <div field="contractDesc" width="250" headerAlign="center" align="left" allowSort="true" >合同名称</div>
            <div field="score" width="60" headerAlign="center" align="center" allowSort="false">得分</div>
            <div field="CREATE_TIME_" width="60" headerAlign="center" align="center" allowSort="false">同步日期</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
</script>
</body>
</html>
