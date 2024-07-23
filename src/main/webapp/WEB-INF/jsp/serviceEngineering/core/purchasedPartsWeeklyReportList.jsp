<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>外购件图册收集及制作周报表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-left:15px;margin-right: 15px">
                    <span class="text" style="width:auto">年份: </span>
                    <input class="mini-textbox" id="signYear" name="signYear">
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="purchasedPartsWeeklyReport-searchFrm" onclick="searchFrm()" showNoRight="false">查询</f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <f:a alias="purchasedPartsWeeklyReport-addBusiness" onclick="addBusiness()" showNoRight="false">新增</f:a>
                    <f:a alias="purchasedPartsWeeklyReport-editBusiness" onclick="editBusiness()" showNoRight="false">编辑</f:a>
                    <f:a alias="purchasedPartsWeeklyReport-removeBusiness" onclick="removeBusiness()" showNoRight="false">删除</f:a>
                    <f:a alias="purchasedPartsWeeklyReport-downloadBusiness" onclick="downloadBusiness()" showNoRight="false">下载</f:a>
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
         url="${ctxPath}/serviceEngineering/core/purchasedParts/weeklyReportListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div field="signYear" width="50" headerAlign="center" align="center">年份</div>
            <div field="signMonth" width="50" headerAlign="center" align="center">月份</div>
            <div field="description" width="300" headerAlign="center" align="center">名称</div>
            <div field="releaseTime" width="100" headerAlign="center" align="center">发布时间</div>
            <div cellCls="actionIcons" width="60" headerAlign="center" align="center" renderer="fileRenderer"
                 cellStyle="padding:0;" hideable="true">操作
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var coverContent = "${currentUserName}" + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    //..
    function fileRenderer(e) {
        var record = e.record;
        var id = record.id;
        var description = record.description;
        var existFile = record.existFile;

        if (existFile) {
            var s = '<span title="预览"  onclick="previewBusiness(\'' + id + '\',\'' + coverContent + '\')">预览</span>';
//            s += '<span title="下载" onclick="downloadBusiness(\'' + id + '\',\'' + description + '\')">下载</span>';
            return s;
        } else {
            var s = '<span title="预览"  style="color: silver">预览</span>';
//            s += '<span title="下载" style="color: silver">下载</span>';
            return s;
        }
    }
    //..
    function previewBusiness(id, coverContent) {
        var previewUrl = jsUseCtxPath + "/serviceEngineering/core/purchasedParts/weeklyReportPreview.do?id=" + id;
        window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverContent) + "&file=" + encodeURIComponent(previewUrl));
    }
    //..
    function addBusiness() {
        mini.open({
            title: "添加",
            url: jsUseCtxPath + "/serviceEngineering/core/purchasedParts/weeklyReportEditPage.do?id=&action=add",
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
            mini.alert("请至少选中一条记录");
            return;
        }
        var id = row.id;
        mini.open({
            title: "添加",
            url: jsUseCtxPath + "/serviceEngineering/core/purchasedParts/weeklyReportEditPage.do?id=" + id + '&action=edit',
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
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var id = row.id;
                $.ajax({
                    url: jsUseCtxPath + "/serviceEngineering/core/purchasedParts/deleteWeeklyReport.do",
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
    //..
    function downloadBusiness() {
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var id = row.id;
        var description = row.description;
        download(id, description);
    }
    //..
    function download(id, description) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/purchasedParts/weeklyReportDownload.do");
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
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
