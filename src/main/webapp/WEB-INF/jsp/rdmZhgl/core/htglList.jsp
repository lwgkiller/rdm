<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>合同列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <style type="text/css">
        .myrow {
            color: #CC0000;
            font-weight: bold;
        }
    </style>
    <script src="${ctxPath}/scripts/rdmZhgl/htglList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.htglList.name" />: </span>
                    <input class="mini-textbox" id="contractNo" name="contractNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.htglList.name1" />: </span>
                    <input class="mini-textbox" id="contractDesc" name="contractDesc"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.htglList.name2" />:</span>
                    <input id="signerUserId" name="signerUserId" textname="signerUserName"
                           class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="<spring:message code="page.htglList.name2" />" length="50"
                           mainfield="no" single="true"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.htglList.name3" />:</span>
                    <input id="signerUserDepId"
                           name="signerUserDepId"
                           class="mini-buttonedit icon-dep-button"
                           required="true" allowInput="false"
                           onbuttonclick="selectMainDep" style="width:98%"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.htglList.name4" />: </span>
                    <input class="mini-textbox" id="signYear" name="signYear"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.htglList.name5" />: </span>
                    <input class="mini-textbox" id="signMonth" name="signMonth"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.htglList.name6" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.htglList.name7" /></a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addContract()"><spring:message code="page.htglList.name8" /></a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="copyContract()"><spring:message code="page.htglList.name9" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeContractRow()"><spring:message code="page.htglList.name10" /></a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportBusiness()"><spring:message code="page.htglList.name11" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="discardContract()">作废</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
    <span style="color:#CC0000;font-weight:bold"><spring:message code="page.htglList.name12" /></span>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="contractListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" allowCellWrap="true" showCellTip="true"
         url="${ctxPath}/zhgl/core/htgl/dataListQuery.do" idField="id" allowCellEdit="true" allowCellSelect="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[15,30,50,100]" pageSize="15" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="40" headerAlign="center" align="center"><spring:message code="page.htglList.name13" /></div>
            <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="left" renderer="onActionRenderer" cellStyle="padding:0;">
                <spring:message code="page.htglList.name14" />
            </div>
            <div field="isDiscard" width="80" headerAlign="center" align="center" renderer="onIsOkRenderer">是否作废</div>
            <div field="contractNo" width="200" headerAlign="center" align="center" renderer="render"><spring:message code="page.htglList.name" /></div>
            <div field="contractDesc" width="200" headerAlign="center" align="center" renderer="render"><spring:message code="page.htglList.name15" /></div>
            <div field="signerUserName" width="100" headerAlign="center" align="center" renderer="render"><spring:message code="page.htglList.name2" /></div>
            <div field="signerUserDepName" width="100" headerAlign="center" align="center" renderer="render"><spring:message code="page.htglList.name3" /></div>
            <div field="signYear" width="100" headerAlign="center" align="center" renderer="render"><spring:message code="page.htglList.name4" /></div>
            <div field="signMonth" width="100" headerAlign="center" align="center" renderer="render"><spring:message code="page.htglList.name5" /></div>
            <div field="signDate" width="100" headerAlign="center" align="center" renderer="render">签订日期</div>
            <div field="CAndTStatus" width="100" headerAlign="center" align="center" renderer="render">变更/解除情况</div>
            <div field="partA" width="200" headerAlign="center" align="center" renderer="render"><spring:message code="page.htglList.name16" /></div>
            <div field="partB" width="200" headerAlign="center" align="center" renderer="render"><spring:message code="page.htglList.name17" /></div>
            <div field="partC" width="200" headerAlign="center" align="center" renderer="render"><spring:message code="page.htglList.name18" /></div>
            <div field="partD" width="200" headerAlign="center" align="center" renderer="render"><spring:message code="page.htglList.name19" /></div>
            <div field="isSingHonest" width="150" headerAlign="center" align="center" renderer="onIsOkRenderer"><spring:message code="page.htglList.name20" /></div>
            <div field="isRecord" width="80" headerAlign="center" align="center" renderer="onIsOkRenderer"><spring:message code="page.htglList.name21" /></div>
            <div field="isFile" width="80" headerAlign="center" align="center" renderer="onIsOkRenderer"><spring:message code="page.htglList.name22" /></div>
            <div field="FileNames" width="200" headerAlign="center" align="center" renderer="render"><spring:message code="page.htglList.name23" /></div>
            <div field="creator" width="60" headerAlign="center" align="center" visible="false"><spring:message code="page.htglList.name24" /></div>
            <div field="CREATE_TIME_" width="90" headerAlign="center" align="center" dateFormat="yyyy-MM-dd HH:mm:ss" visible="false"><spring:message code="page.htglList.name25" /></div>
        </div>
    </div>
</div>

<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/zhgl/core/htgl/exportList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var contractListGrid = mini.get("contractListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";

    function onActionRenderer(e) {
        var record = e.record;
        var contractId = record.id;
        var s = '<span  title=' + htglList_name + ' onclick="detailContractRow(\'' + contractId + '\')">' + htglList_name + '</span>';
        //无删除权限的按钮变灰色
        if (currentUserNo == 'admin' || currentUserNo == 'zhujia') {
            s += '<span  title=' + htglList_name1 + ' onclick="removeContractRow(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + htglList_name1 + '</span>';
        }
        return s;
    }

    contractListGrid.on("rowdblclick", function (e) {
        var record = e.record;
        var contractId = record.id;
        if (currentUserId == record.CREATE_BY_ || currentUserNo == 'admin' || currentUserNo == 'zhujia') {
            editContractRow(contractId);
        } else {
            detailContractRow(contractId);
        }
    });

    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }

    //选择主部门
    function selectMainDep(e){
        var b=e.sender;

        _TenantGroupDlg('1',true,'','1',function(g){
            b.setValue(g.groupId);
            b.setText(g.name);
        },false);

    }
</script>
<redxun:gridScript gridId="contractListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>