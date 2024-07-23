<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>标准执行性自查列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/standardManager/standardDoCheckApplyList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardDoCheckApplyList.name1" />: </span>
                    <input class="mini-textbox" id="standardNumber" name="standardNumber"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardDoCheckApplyList.name2" />: </span>
                    <input class="mini-textbox" id="standardName" name="standardName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardDoCheckApplyList.name3" />: </span>
                    <input class="mini-textbox" id="firstWriterName" name="firstWriterName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardDoCheckApplyList.name4" />: </span>
                    <input class="mini-textbox" id="djrUserName" name="djrUserName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardDoCheckApplyList.name5" />: </span>
                    <input class="mini-textbox" id="createYear" name="createYear"/>
                </li>
                <%--@mh 增加自查状态筛选--%>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardDoCheckApplyList.name6" />: </span>
                    <input id="zcStatus" name="zcStatus" class="mini-combobox" style="width:90px;" multiSelect="false"
                           textField="value" valueField="key" emptyText="<spring:message code="page.standardDoCheckApplyList.name7" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.standardDoCheckApplyList.name7" />..."
                           data="[ {'key' : '自查中','value' : '自查中'},{'key' : '整改中','value' : '整改中'},{'key' : '自查完成','value' : '自查完成'}]"
                    />
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardDoCheckApplyList.name8" />: </span>
                    <input id="checkResult" name="checkResult" class="mini-combobox" style="width:140px;"
                           multiSelect="false"
                           textField="value" valueField="key" emptyText="<spring:message code="page.standardDoCheckApplyList.name7" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.standardDoCheckApplyList.name7" />..."
                           data="[ {'key' : '未完全按标准执行','value' : '未完全按标准执行'},{'key' : '完全按标准执行','value' : '完全按标准执行'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardDoCheckApplyList.name9" />: </span>
                    <input id="status" name="status" class="mini-combobox" style="width:120px;" multiSelect="false"
                           textField="value" valueField="key" emptyText="<spring:message code="page.standardDoCheckApplyList.name7" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.standardDoCheckApplyList.name7" />..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '审批中'},{'key' : 'SUCCESS_END','value' : '批准'},{'key' : 'DISCARD_END','value' : '作废'}]"
                    />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.standardDoCheckApplyList.name10" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.standardDoCheckApplyList.name11" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addApply()"><spring:message code="page.standardDoCheckApplyList.name12" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeApply()"><spring:message code="page.standardDoCheckApplyList.name13" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="exportStandardDoCheckList()"><spring:message code="page.standardDoCheckApplyList.name14" /></a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="doCheckGridList" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         onlyCheckSelection="true"
         url="${ctxPath}/standard/core/doCheck/applyList.do" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[20,50,100]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20px"></div>
            <div name="action" cellCls="actionIcons" width="120px" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.standardDoCheckApplyList.name15" />
            </div>
            <div field="standardNumber" width="150px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.standardDoCheckApplyList.name1" /></div>
            <div field="standardName" width="250px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.standardDoCheckApplyList.name2" /></div>
            <div field="checkResult" width="140px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.standardDoCheckApplyList.name8" /></div>
            <div field="firstWriterName" width="65px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.standardDoCheckApplyList.name3" /></div>
            <div field="djrUserName" width="80px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.standardDoCheckApplyList.name4" /></div>
            <div field="taskName" headerAlign='center' align='center' width="200px"><spring:message code="page.standardDoCheckApplyList.name16" /></div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="80px"><spring:message code="page.standardDoCheckApplyList.name17" /></div>
            <div field="zcStatus" headerAlign='center' align='center' width="50px"><spring:message code="page.standardDoCheckApplyList.name6" /></div>
            <div field="status" width="50px" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer"><spring:message code="page.standardDoCheckApplyList.name9" />
            </div>
            <div field="createYear" width="70px" align="center" headerAlign="center" allowSort="true"><spring:message code="page.standardDoCheckApplyList.name5" /></div>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/standard/core/doCheck/exportStandardDoCheckList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var doCheckGridList = mini.get("doCheckGridList");

    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED') {
            s += '<span  title=' + standardDoCheckApplyList_ck + ' onclick="doCheckDetail(\'' + applyId + '\',\'' + status + '\')">' + standardDoCheckApplyList_ck + '</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title=' + standardDoCheckApplyList_bj + ' onclick="doCheckEdit(\'' + applyId + '\',\'' + instId + '\')">' + standardDoCheckApplyList_bj + '</span>';
        }
        if (record.status == 'RUNNING') {
            if (record.myTaskId) {
                s += '<span  title=' + standardDoCheckApplyList_bl + ' onclick="doCheckTask(\'' + record.myTaskId + '\')">' + standardDoCheckApplyList_bl + '</span>';
            }
        }
        if (status == 'DRAFTED' || currentUserNo == 'admin') {
            s += '<span  title=' + standardDoCheckApplyList_sc + ' onclick="removeApply(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + standardDoCheckApplyList_sc + '</span>';
        }
        return s;
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '审批中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '批准', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }

    doCheckGridList.on("drawcell", function (e) {
        var grid = e.sender,
            record = e.record;
        if (e.field == "checkResult") {
            var checkResult = e.value;
            if (checkResult == '未完全按标准执行') {
                e.cellHtml = '<span style="color:red">' + checkResult + '</span>';
            }
            if (checkResult == '完全按标准执行') {
                e.cellHtml = '<span style="color:forestgreen">' + checkResult + '</span>';
            }
        }
    });

</script>
<redxun:gridScript gridId="doCheckGridList" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>

