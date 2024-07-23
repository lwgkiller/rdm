<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>X-GSS系统监测报告</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-left:15px;margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualWeeklyReportList.name" />: </span>
                    <input class="mini-textbox" id="signYear" name="signYear">
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="maintenanceManualWeeklyReport-searchFrm" onclick="searchFrm()" showNoRight="false" style="margin-right: 5px"><spring:message code="page.maintenanceManualWeeklyReportList.name1" /></f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.maintenanceManualWeeklyReportList.name2" /></a>
                    <f:a alias="maintenanceManualWeeklyReport-addBusiness" onclick="addBusiness()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.maintenanceManualWeeklyReportList.name3" /></f:a>
                    <f:a alias="maintenanceManualWeeklyReport-editBusiness" onclick="editBusiness()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.maintenanceManualWeeklyReportList.name4" /></f:a>
                    <f:a alias="maintenanceManualWeeklyReport-removeBusiness" onclick="removeBusiness()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.maintenanceManualWeeklyReportList.name5" /></f:a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;"
         allowResize="false" allowCellWrap="false" showCellTip="true" idField="id"
         allowCellEdit="true" allowCellSelect="true" multiSelect="false" showColumnsMenu="false"
         sizeList="[1000,5000]" pageSize="1000" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/maintenanceManualWeeklyReport/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div field="signYear" width="50" headerAlign="center" align="center"><spring:message code="page.maintenanceManualWeeklyReportList.name" /></div>
            <div field="signMonth" width="50" headerAlign="center" align="center"><spring:message code="page.maintenanceManualWeeklyReportList.name6" /></div>
            <div field="description" width="300" headerAlign="center" align="center"><spring:message code="page.maintenanceManualWeeklyReportList.name7" /></div>
            <div field="releaseTime" width="100" headerAlign="center" align="center"><spring:message code="page.maintenanceManualWeeklyReportList.name8" /></div>
            <div cellCls="actionIcons" width="60" headerAlign="center" align="center" renderer="fileRenderer"
                 cellStyle="padding:0;" hideable="true"><spring:message code="page.maintenanceManualWeeklyReportList.name9" />
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentTime = "${currentTime}";
    var coverContent = "${currentUserName}" + "<br/>" + currentTime + "<br/>";

    //..
    function fileRenderer(e) {
        var record = e.record;
        var id = record.id;
        var description = record.description + ".xlsx";
        var existFile = record.existFile;
        var formId = "";

        if (existFile) {
//            var s = '<span title="预览"  onclick="previewBusiness(\'' + id + '\',\'' + coverContent + '\')">预览</span>';
            var url = '/serviceEngineering/core/maintenanceManualWeeklyReport/OfficePreview.do';
            s = '<span  title=' + maintenanceManualWeeklyReportList_name + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + description + '\',\'' + id + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + maintenanceManualWeeklyReportList_name + '</span>';
            s += '<span title=' + maintenanceManualWeeklyReportList_name1 + ' onclick="downloadBusiness(\'' + id + '\',\'' + description + '\')">' + maintenanceManualWeeklyReportList_name1 + '</span>';
            return s;
        } else {
            var s = '<span title=' + maintenanceManualWeeklyReportList_name + '  style="color: silver">' + maintenanceManualWeeklyReportList_name + '</span>';
            s += '<span title=' + maintenanceManualWeeklyReportList_name1 + ' style="color: silver">' + maintenanceManualWeeklyReportList_name1 + '</span>';
            return s;
        }
    }
    //..
    function previewBusiness(id, coverContent) {
        var previewUrl = jsUseCtxPath + "/serviceEngineering/core/maintenanceManualWeeklyReport/preview.do?id=" + id;
        window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverContent) + "&file=" + encodeURIComponent(previewUrl));
    }
    //..
    function downloadBusiness(id, description) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/maintenanceManualWeeklyReport/download.do");
        var idAttr = $("<input>");
        idAttr.attr("type", "hidden");
        idAttr.attr("name", "id");
        idAttr.attr("value", id);
        var descriptionAttr = $("<input>");
        descriptionAttr.attr("type", "hidden");
        descriptionAttr.attr("name", "description");
        descriptionAttr.attr("value", description);
        $("body").append(form);
        form.append(idAttr);
        form.append(descriptionAttr);
        form.submit();
        form.remove();
    }
    //..
    function addBusiness() {
        mini.open({
            title: maintenanceManualWeeklyReportList_name2,
            url: jsUseCtxPath + "/serviceEngineering/core/maintenanceManualWeeklyReport/editPage.do?id=&action=add",
            width: 800,
            height: 450,
            showModal: true,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                searchFrm();
            }
        });
    }
    //..
    function editBusiness() {
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert(maintenanceManualWeeklyReportList_name3);
            return;
        }
        var id = row.id;
        mini.open({
            title: maintenanceManualWeeklyReportList_name2,
            url: jsUseCtxPath + "/serviceEngineering/core/maintenanceManualWeeklyReport/editPage.do?id=" + id + '&action=edit',
            width: 800,
            height: 450,
            showModal: true,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                searchFrm();
            }
        });
    }
    //..
    function removeBusiness() {
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert(maintenanceManualWeeklyReportList_name3);
            return;
        }
        mini.confirm(maintenanceManualWeeklyReportList_name4, maintenanceManualWeeklyReportList_name5, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var id = row.id;
                $.ajax({
                    url: jsUseCtxPath + "/serviceEngineering/core/maintenanceManualWeeklyReport/delete.do",
                    type: 'POST',
                    data: mini.encode({id: id}),
                    contentType: 'application/json',
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            searchFrm();
                        }
                    }
                });
            }
        });
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
