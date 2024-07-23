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
                    <span class="text" style="width:auto">整机编号：</span>
                    <input class="mini-textbox" id="pin" name="pin" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">异常类型：</span>
                    <input id="exceptionType" name="exceptionType" class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=exceptionType"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">部件类型：</span>
                    <input id="partsCategory" name="partsCategory" class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=partsCategory"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">异常节点：</span>
                    <input id="assemblyNode" name="assemblyNode" class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=exceptionNode"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">责任部门：</span>
                    <input class="mini-textbox" id="repDep" name="repDep" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">责任人：</span>
                    <input class="mini-textbox" id="repUser" name="repUser" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">反馈人：</span>
                    <input class="mini-textbox" id="feedbackPerson" name="feedbackPerson" style="width:100%;"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">反馈时间：</span>
                    <input id="feedbackTimeBegin" name="feedbackTimeBegin" class="mini-datepicker" format="yyyy-MM-dd"
                           showTime="false" showOkButton="true" showClearButton="true" style="width:98%;"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">至：</span>
                    <input id="feedbackTimeEnd" name="feedbackTimeEnd" class="mini-datepicker" format="yyyy-MM-dd"
                           showTime="false" showOkButton="true" showClearButton="true" style="width:98%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">是否闭环：</span>
                    <input id="isClear" name="isClear" class="mini-combobox" style="width:98%"
                           valueField="key" textField="value" data="[{key:'是',value:'是'},{key:'否',value:'否'}]"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportBusiness()">导出</a>
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
         url="${ctxPath}/newproductAssembly/core/kanban/exceptionSummaryListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="40" trueValue="1" falseValue="0"></div>
            <div type="indexcolumn" width="45" headerAlign="center" align="center" allowSort="true">序号</div>
            <div field="designModel" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">产品型号</div>
            <div field="testQuantity" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">试验台数</div>
            <div field="theExplain" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">说明</div>
            <div field="productCategory" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">产品种类</div>
            <div field="productDep" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">产品部门</div>
            <div field="projectLeader" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">项目负责人</div>
            <div field="planCategory" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">计划类型</div>
            <div field="orderReleaseTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">订单下达时间</div>
            <div field="pin" width="180" headerAlign="center" align="center" allowSort="true" renderer="render">整机编号</div>
            <div field="exceptionType" width="150" headerAlign="center" align="center" allowSort="true" renderer="render">异常类型</div>
            <div field="partsCategory" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">部件分类</div>
            <div field="exceptionPart" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">异常部件</div>
            <div field="exceptionDescription" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">异常描述</div>
            <div field="assemblyNode" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">异常节点</div>
            <div field="repDep" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">责任部门</div>
            <div field="repUser" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">第一责任人</div>
            <div field="feedbackPerson" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">反馈人</div>
            <div field="feedbackTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">反馈时间</div>
            <div field="disposalMethod" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">现场处置方法</div>
            <div field="improvementRequirements" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">改进要求</div>
            <div field="temporaryMeasures" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">临时处理措施</div>
            <div field="temporaryTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">临时处理时间</div>
            <div field="permanentMeasures" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">永久解决方案</div>
            <div field="permanentTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">永久解决方案时间</div>
            <div field="isClear" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">是否闭环</div>
            <div field="remark" width="200" headerAlign="center" align="center" allowSort="true" renderer="render">备注</div>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/newproductAssembly/core/kanban/exportExceptionSummaryList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }

    function exportBusiness() {
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