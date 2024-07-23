<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>知识产权列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.gkgfDetailList.name1" />: </span>
                    <input class="mini-textbox" name="userName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.gkgfDetailList.name2" />: </span>
                    <input class="mini-textbox" name="deptName"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.gkgfDetailList.name3" />: </span><input
                        class="mini-textbox" style="width: 150px" name="model"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.gkgfDetailList.name4" />: </span><input
                        class="mini-textbox" style="width: 150px" name="workType"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.gkgfDetailList.name5" />: </span><input
                        class="mini-textbox" style="width: 150px" name="applyId"/></li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.gkgfDetailList.name6" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.gkgfDetailList.name7" /></a>
                </li>
            </ul>
        </form>
        <ul class="toolBtnBox">
        </ul>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="dataListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/gkgf/core/apply/listDetailData.do?applyType=${applyType}" idField="id"
         sortField="UPDATE_TIME_" sortOrder="desc"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="indexcolumn" align="center" headerAlign="center" width="60px"><spring:message code="page.gkgfDetailList.name8" /></div>
            <div field="model" name="model" width="120px" headerAlign="center" align="center" allowSort="true"><spring:message code="page.gkgfDetailList.name3" /></div>
            <div field="workType" width="100px" headerAlign="center" align="center" allowSort="true"><spring:message code="page.gkgfDetailList.name4" /></div>
            <div field="tool" width="100px" headerAlign="center" align="center" allowSort="true"><spring:message code="page.gkgfDetailList.name9" /></div>
            <div field="workItem" width="100px" headerAlign="center" align="center" allowSort="true"><spring:message code="page.gkgfDetailList.name10" /></div>
            <div field="carModel" width="100px" headerAlign="center" align="center" allowSort="true"><spring:message code="page.gkgfDetailList.name11" /></div>
            <div field="selfVideoUrl" width="120px" headerAlign="center" align="center" allowSort="true"><spring:message code="page.gkgfDetailList.name12" /></div>
            <div field="" renderer="onPicRenderer" width="100px" headerAlign="center" align="center" allowSort="true"><spring:message code="page.gkgfDetailList.name13" /></div>
            <div field="bgVideoUrl" width="120px" headerAlign="center" align="center" allowSort="true"><spring:message code="page.gkgfDetailList.name14" /></div>
            <div field="" renderer="onVideoRenderer" width="100px" headerAlign="center" align="center" allowSort="true"><spring:message code="page.gkgfDetailList.name15" /></div>
            <div field="" renderer="onReportRenderer" width="100px" headerAlign="center" align="center" ><spring:message code="page.gkgfDetailList.name16" /></div>
            <div field="userName" name="userName" sortField="userName" width="80px" headerAlign="center" align="center" allowSort="true">
                <spring:message code="page.gkgfDetailList.name1" />
            </div>
            <div field="deptName" name="deptName" sortField="userName" width="120px" headerAlign="center" align="center" allowSort="true">
                <spring:message code="page.gkgfDetailList.name2" />
            </div>
        </div>
    </div>
</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var dataListGrid = mini.get("dataListGrid");
    dataListGrid.on("load", function () {
        dataListGrid.mergeColumns(["userName","deptName","model", "applyId"]);
    });
    function onPicRenderer(e) {
        var record = e.record;
        var detailId = record.id;
        var s = '';
        s += '<span  title=' + gkgfDetailList_name + ' style="cursor: pointer;color: #0a7ac6" onclick="showFilePage(\'' + detailId + '\',\'pic\')">' + gkgfDetailList_name + '</span>';
        return s;
    }
    function onVideoRenderer(e) {
        var record = e.record;
        var detailId = record.id;
        var s = '';
        s += '<span  title=' + gkgfDetailList_name1 + ' style="cursor: pointer;color: #0a7ac6" onclick="showFilePage(\'' + detailId + '\',\'video\')">' + gkgfDetailList_name1 + '</span>';
        return s;
    }
    function onReportRenderer(e) {
        var record = e.record;
        var detailId = record.id;
        var s = '';
        s += '<span  title=' + gkgfDetailList_name2 + ' style="cursor: pointer;color: #0a7ac6" onclick="showFilePage(\'' + detailId + '\',\'report\')">' + gkgfDetailList_name2 + '</span>';
        return s;
    }
    function showFilePage(detailId,fileType) {
        mini.open({
            title: gkgfDetailList_name3,
            url: jsUseCtxPath + "/gkgf/core/apply/fileWindow.do?detailId=" + detailId+"&fileType="+fileType+"&editable=false",
            width: 1000,
            height: 500,
            showModal: false,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                // searchDoc();
            }
        });
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var instStatus = record.status;
        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
            {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
            {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
        ];

        return $.formatItemValue(arr, instStatus);
    }
</script>
<redxun:gridScript gridId="dataListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
