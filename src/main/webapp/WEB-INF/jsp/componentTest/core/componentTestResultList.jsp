<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">试验编号：</span>
                    <input class="mini-textbox" id="testNo" name="testNo" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">零部件名称：</span>
                    <input class="mini-textbox" id="componentName" name="componentName" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">零部件型号：</span>
                    <input class="mini-textbox" id="componentModel" name="componentModel" style="width:100%;"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">物料编码：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">试验类型：</span>
                    <input id="testType" name="testType"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testType"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">零部件类别：</span>
                    <input id="componentCategory" name="componentCategory"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=componentCategory"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">试验报告编号：</span>
                    <input class="mini-textbox" id="testReportNo" name="testReportNo" style="width:100%;"/>
                </li>
                <%--<li style="margin-right:15px">--%>
                    <%--<span class="text" style="width:auto">试验报告名称：</span>--%>
                    <%--<input class="mini-textbox" id="testReport" name="testReport" style="width:100%;"/>--%>
                <%--</li>--%>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">试验完成时间：</span>
                    <input id="completeTestMonth" name="completeTestMonth" class="mini-monthpicker" allowinput="false"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">承担单位：</span>
                    <input id="laboratory" name="laboratory"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=undertakeLaboratory"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请人：</span>
                    <input class="mini-textbox" id="applyUser" name="applyUser"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请部门：</span>
                    <input class="mini-textbox" id="applyDep" name="applyDep"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">试验负责人：</span>
                    <input class="mini-textbox" id="testLeader" name="testLeader"/>
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
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="true"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" oncellvalidation="onCellValidation"
         multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="50" allowAlternating="true"
         pagerButtons="#pagerButtons"
         url="${ctxPath}/componentTest/core/result/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="40" trueValue="1" falseValue="0"></div>
            <div type="indexcolumn" width="45" headerAlign="center" align="center" allowSort="true">序号</div>
            <div name="action" cellCls="actionIcons" width="40" headerAlign="center" align="left" renderer="onActionRenderer" cellStyle="padding:0;">
                操作
            </div>
            <div field="testNo" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">试验编号</div>
            <div field="componentName" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">零部件名称</div>
            <div field="componentModel" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">零部件型号</div>
            <div field="materialCode" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">物料编码</div>
            <div field="testReportNo" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">试验报告编号</div>
            <div field="applyUser" width="80" headerAlign="center" align="center" allowSort="true" renderer="render">申请人</div>
            <div field="testLeader" width="80" headerAlign="center" align="center" allowSort="true" renderer="render">试验负责人</div>
            <div field="completeTestMonth" width="80" headerAlign="center" align="center" allowSort="true" renderer="render">试验完成时间</div>
            <div field="testContract" width="150" headerAlign="center" align="center" allowSort="true" renderer="render">检测合同</div>
            <div name="actionContract" width="100" headerAlign='center' align="center" renderer="operationRendererContract">操作</div>
            <div field="testReport" width="150" headerAlign="center" align="center" allowSort="true" renderer="render">检测报告</div>
            <div name="actionReport" width="100" headerAlign='center' align="center" renderer="operationRendererReport">操作</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var coverContent = currentUserName + "<br/>" + new Date().format("yyyy-MM-dd hh:mm:ss");
    +"<br/>徐州徐工挖掘机械有限公司";

    function onActionRenderer(e) {
        var record = e.record;
        var businessId = record.id;
        var s = '<span  title="明细" onclick="detailBusiness(\'' + businessId + '\')">明细</span>';
        return s;
    }
    //..p
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..
    function detailBusiness(businessId) {
        var url = jsUseCtxPath + "/componentTest/core/kanban/editPage.do?businessId=" + businessId + "&action=detail";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
                }
            }
        }, 1000);
    }
    //..
    function operationRendererContract(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnPreviewSpanContract(record.testContract, record.testContractId, record.id, coverContent);
        var downLoadUrl = '/componentTest/core/kanban/pdfPreviewAndAllDownloadContract.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.testContract + '\',\'' + record.testContractId + '\',\'' + record.id + '\',\'' + downLoadUrl + '\')">下载</span>';
        return cellHtml;
    }
    //..
    function returnPreviewSpanContract(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/componentTest/core/kanban/pdfPreviewAndAllDownloadContract.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/componentTest/core/kanban/officePreviewContract.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/componentTest/core/kanban/imagePreviewContract.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }
    //..
    function operationRendererReport(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnPreviewSpanReport(record.testReport, record.testReportId, record.id, coverContent);
        var downLoadUrl = '/componentTest/core/kanban/pdfPreviewAndAllDownloadReport.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.testReport + '\',\'' + record.testReportId + '\',\'' + record.id + '\',\'' + downLoadUrl + '\')">下载</span>';
        return cellHtml;
    }
    //..
    function returnPreviewSpanReport(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/componentTest/core/kanban/pdfPreviewAndAllDownloadReport.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/componentTest/core/kanban/officePreviewReport.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/componentTest/core/kanban/imagePreviewReport.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>