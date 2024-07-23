<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>名称列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/wwrz/wwrzTestPlanList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">部门: </span><input class="mini-textbox" style="width: 100px" name="deptName"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">型号: </span><input class="mini-textbox" style="width: 100px" name="productModel"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">产品主管: </span><input class="mini-textbox" style="width: 100px" name="chargerName"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">认证类别: </span>
                    <input id="certType" name="certType" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100px;height:34px" label="认证类别："  onvaluechanged="searchFrm()"
                           length="50"
                           only_read="false"allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=RZLB"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">计划状态: </span>
                    <input id="planStatus" name="planStatus" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100px;height:34px" label="计划状态："  onvaluechanged="searchFrm()"
                           length="50"
                           only_read="false"allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=JHZT"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em>展开</em>
						<i class="unfoldIcon"></i>
					</span>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button" style="margin-left: 5px" plain="true" onclick="exportExcel()">按条件导出</a>
                </li>
            </ul>
            <div id="moreBox">
                <ul>
                    <li style="margin-right: 15px"><span class="text" style="width:auto">年月起: </span><input
                            id="yearMonthStart" allowinput="false"  class="mini-monthpicker"
                            style="width: 100px" name="yearMonthStart"/></li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto">年月止: </span><input
                            id="yearMonthEnd" allowinput="false"  class="mini-monthpicker"
                            style="width: 100px" name="yearMonthEnd"/></li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto">执行状态: </span>
                        <input id="planExeStatus" name="planExeStatus" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100px;height:34px" label="执行状态："  onvaluechanged="searchFrm()"
                               length="50"
                               only_read="false"allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </li>
                </ul>
            </div>
        </form>
        <!--导出Excel相关HTML-->
        <form id="excelForm" action="${ctxPath}/wwrz/core/testPlan/exportExcel.do" method="post"
              target="excelIFrame">
            <input type="hidden" name="pageIndex" id="pageIndex"/>
            <input type="hidden" name="pageSize" id="pageSize"/>
            <input type="hidden" name="filter" id="filter"/>
        </form>
        <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
        <ul class="toolBtnBox">
            <div style="display: inline-block" class="separator"></div>
            <a id="addButton" class="mini-button" style="margin-right: 5px" plain="true" img="${ctxPath}/scripts/mini/miniui/res/images/add.png" onclick="addItems()">新增</a>
            <a id="delButton" class="mini-button"  img="${ctxPath}/scripts/mini/miniui/res/images/cancel.png" onclick="removeRow()">删除</a>
            <a id="deptApply" class="mini-button " style="margin-left: 10px;display: none" plain="true" onclick="createFlow()">自动提报</a>
            <span style="color: red;margin-left: 10px">注：每个季度末20号以后允许新增、删除，季度初1号自动创建审批流程</span>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/wwrz/core/testPlan/listData.do" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="30">序号</div>
            <div name="action" cellCls="actionIcons" width="60" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="planStatus" width="80" headerAlign="center" align="center" allowSort="true" renderer="onPlanStatusType">计划状态</div>
            <div field="applyId" width="80" headerAlign="center" align="center" allowSort="true" renderer="onExecuteStatus">执行状态</div>
            <div field="planCode" width="120" headerAlign="center" align="center" allowSort="true">计划编号</div>
            <div field="deptName" width="120" headerAlign="center" align="center" allowSort="true">部门</div>
            <div field="productModel" width="80" headerAlign="center" align="center" allowSort="true">型号</div>
            <div field="chargerName" width="80" headerAlign="center" align="center" allowSort="true">产品主管</div>
            <div field="certType" width="80" headerAlign="center" align="center" allowSort="true" renderer="onCertType">认证类别</div>
            <div field="testType" width="80" headerAlign="center" align="center" allowSort="true" renderer="onTestType">全新/补测</div>
            <div field="yearMonth" width="80" headerAlign="center" align="center" allowSort="true">预计时间</div>
            <div field="remark" width="120" headerAlign="center" align="center" allowSort="true">产品描述</div>
            <div field="UPDATE_TIME_" width="80" headerAlign="center" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" allowSort="true">更新时间</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var listGrid = mini.get("listGrid");
    var currentUserId = "${currentUser.userId}";
    var certTypeList = getDics("RZLB");
    var planStatusList = getDics("JHZT");
    var testTypeList = getDics("CSLB");
    function onExecuteStatus(e) {
        var record = e.record;
        var applyId = record.applyId;
        var resultText = '';
        var _html = '';
        var color = '';
        if (applyId) {
            color = 'green';
            resultText = "已执行";
        } else{
            color = 'red';
            resultText = "未执行";
        }
        _html = '<span style="color: ' + color + '">' + resultText + '</span>'
        return _html;
    }
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
