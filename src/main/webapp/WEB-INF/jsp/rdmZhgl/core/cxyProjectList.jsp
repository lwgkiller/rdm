<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>产学研项目列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <style type="text/css">
    </style>
    <script src="${ctxPath}/scripts/rdmZhgl/cxyProjectList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">项目名称: </span>
                    <input class="mini-textbox" id="projectDesc" name="projectDesc"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">项目负责人:</span>
                    <input class="mini-textbox" id="responsibleName" name="responsibleName"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">项目负责部门:</span>
                    <input class="mini-textbox" id="responsibleDepName" name="responsibleDepName"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">项目开始时间:</span>
                    <input id="beginTime" name="beginTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">项目结束时间:</span>
                    <input id="endTime" name="endTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">项目所属类型: </span>
                    <input id="projectType" name="projectType"
                           class="mini-combobox" style="width:98%" showNullItem="true" nullItemText=""
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=cxyProjectType"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">技术研究方向: </span>
                    <input id="researchDirection" name="researchDirection"
                           class="mini-combobox" style="width:98%" showNullItem="true" nullItemText=""
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=cxyResearchDirection"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">项目性质: </span>
                    <input id="projectProperties" name="projectProperties"
                           class="mini-combobox" style="width:98%" showNullItem="true" nullItemText=""
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=cxyProjectProperties"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">是否延期: </span>
                    <input id="isDelay" name="isDelay" class="mini-combobox" style="width:150px;"
                           textField="key" valueField="value" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : '是','value' : '1'},{'key' : '否','value' : '0'}]"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">执行情况: </span>
                    <input id="implementation" name="implementation"
                           class="mini-combobox" style="width:98%" showNullItem="true"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=cxyImplementation"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addCxyProject()">新增</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeCxyProjectRow()">删除</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportBusiness()">导出</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>
<div class="mini-fit">
    <div id="cxyProjectListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" allowCellWrap="true" showCellTip="true"
         url="${ctxPath}/zhgl/core/cxy/dataListQuery.do" idField="id" allowCellEdit="true" allowCellSelect="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[5,10,30,50,100]" pageSize="10" allowAlternating="true"
         pagerButtons="#pagerButtons" autoLoad="false">
        <div property="columns" cellCls="mycell">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="40" headerAlign="center" align="center">序号</div>
            <div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="left" renderer="onActionRenderer" cellStyle="padding:0;">
                操作
            </div>
            <div field="projectDesc" width="100" headerAlign="center" align="center" renderer="render">项目名称</div>
            <div field="undertaker" width="100" headerAlign="center" align="center" renderer="render">承担单位</div>
            <div field="collaborator" width="100" headerAlign="center" align="center" renderer="render">合作单位</div>
            <div field="responsibleName" width="100" headerAlign="center" align="center" renderer="render">项目负责人</div>
            <div field="responsibleDepName" width="100" headerAlign="center" align="center" renderer="render">项目负责部门</div>
            <div field="beginTime" dateFormat="yyyy-MM-dd" align="center" width="90" headerAlign="center">开始时间</div>
            <div field="endTime" dateFormat="yyyy-MM-dd" align="center" width="90" headerAlign="center">结束时间</div>
            <div field="contractAmount" width="60" headerAlign="center" align="center" renderer="render">合同金额</div>
            <div field="paidContractAmount" width="100" headerAlign="center" align="center" renderer="render">已支付合同金额</div>
            <div field="remark" width="150" headerAlign="center" align="center" renderer="onRemarkRenderer">项目简介</div>
            <div field="projectType" width="100" headerAlign="center" align="center" renderer="render">项目所属类型</div>
            <div field="researchDirection" width="100" headerAlign="center" align="center" renderer="render">技术研究方向</div>
            <div field="projectProperties" width="100" headerAlign="center" align="center" renderer="render">项目性质</div>
            <div field="contractIndicators" width="150" headerAlign="center" align="center" renderer="onContractIndicatorsRenderer">合同上要求指标</div>
            <div field="completedIndicators" width="150" headerAlign="center" align="center" renderer="onCompletedIndicatorsRenderer">目前已完成指标</div>
            <div field="completionRate" width="100" headerAlign="center" align="center" renderer="render">项目完成率(%)</div>
            <div field="isDelay" width="65" headerAlign="center" align="center" renderer="onIsOkRenderer">是否延期</div>
            <div field="delayReason" width="150" headerAlign="center" align="center" renderer="onDelayReasonRenderer">延期原因</div>
            <div field="implementation" width="150" headerAlign="center" align="center" renderer="render">执行情况</div>
            <div field="FileNames" width="200" headerAlign="center" align="center" renderer="render">附件信息</div>
            <div field="creator" width="60" headerAlign="center" align="center" visible="false">创建人</div>
            <div field="CREATE_TIME_" width="90" headerAlign="center" align="center" dateFormat="yyyy-MM-dd HH:mm:ss" visible="false">创建时间</div>
            <div field="isSubmit" width="65" headerAlign="center" align="center" renderer="onIsOkRenderer" visible="false">是否提交</div>
        </div>
    </div>
</div>

<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/zhgl/core/cxy/exportList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var cxyProjectListGrid = mini.get("cxyProjectListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var responsibleDepName = "${responsibleDepName}";
    var implementation = "${implementation}";
    var projectProperties = "${projectProperties}";
    var beginTime = "${beginTime}";
    var endTime = "${endTime}";

    $(function () {
        mini.get("responsibleDepName").setValue(responsibleDepName);
        mini.get("implementation").setValue(implementation);
        mini.get("projectProperties").setValue(projectProperties);
        mini.get("beginTime").setValue(beginTime);
        mini.get("endTime").setValue(endTime);
        searchFrm();
    })

    function onActionRenderer(e) {
        var record = e.record;
        var cxyProjectId = record.id;
        var isSubmit = record.isSubmit;
        var isok = false;
        var s = record.responsibleUserId.split(",");
        for (i = 0; i < s.length; i++) {
            if (currentUserId == s[i]) {
                isok = true;
                break;
            }
        }
        ;
        var s = '<span  title="明细" onclick="detailCxyProjectRow(\'' + cxyProjectId + '\')">明细</span>';
        if ((isSubmit == '0' && isok == true) || currentUserNo == 'admin' || currentUserNo == 'zhujia') {
            s += '<span  title="编辑" onclick="editCxyProjectRow(\'' + cxyProjectId + '\')">编辑</span>';
        } else if ((isSubmit == '1' && isok == true)) {
            s += '<span  title="反馈" onclick="feedBackCxyProjectRow(\'' + cxyProjectId + '\')">反馈</span>';
        }
        //无删除权限的按钮变灰色
        if (currentUserNo == 'admin' || currentUserNo == 'zhujia' || (isSubmit == '0' && currentUserId == record.CREATE_BY_)) {
            s += '<span  title="删除" onclick="removeCxyProjectRow(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }

        return s;
    }

    cxyProjectListGrid.on("rowdblclick", function (e) {
        var record = e.record;
        var cxyProjectId = record.id;
        var isSubmit = record.isSubmit;
        var isok = false;
        var s = record.responsibleUserId.split(",");
        for (i = 0; i < s.length; i++) {
            if (currentUserId == s[i]) {
                isok = true;
                break;
            }
        }
        ;
        if ((isSubmit == '0' && isok == true) || currentUserNo == 'admin' || currentUserNo == 'zhujia') {
            editCxyProjectRow(cxyProjectId);
        } else if ((isSubmit == '1' && isok == true)) {
            feedBackCxyProjectRow(cxyProjectId);
        } else {
            detailCxyProjectRow(cxyProjectId);
        }
    });

    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
</script>
<redxun:gridScript gridId="cxyProjectListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>