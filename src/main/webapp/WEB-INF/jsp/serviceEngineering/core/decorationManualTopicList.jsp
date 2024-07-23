<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>装修手册Topic</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualTopicList.name" />：</span>
                    <input class="mini-textbox" id="businessNo" name="businessNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualTopicList.name1" />：</span>
                    <input class="mini-textbox" id="chapter" name="chapter"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualTopicList.name2" />：</span>
                    <input class="mini-textbox" id="system" name="system"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualTopicList.name3" />：</span>
                    <input class="mini-textbox" id="topicType" name="topicType"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualTopicList.name4" />：</span>
                    <input class="mini-textbox" id="productSerie" name="productSerie"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualTopicList.name5" />：</span>
                    <input class="mini-textbox" id="salesArea" name="salesArea"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualTopicList.name6" />：</span>
                    <input class="mini-textbox" id="manualVersion" name="manualVersion"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualTopicList.name7" />：</span>
                    <input class="mini-textbox" id="manualStatus" name="manualStatus"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualTopicList.name8" />：</span>
                    <input class="mini-textbox" id="isPS" name="isPS"/>
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="decorationManualTopic-searchFrm" onclick="searchFrm()" showNoRight="false" style="margin-right: 5px"><spring:message code="page.decorationManualTopicList.name9" /></f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.decorationManualTopicList.name10" /></a>
                    <f:a alias="decorationManualTopic-addBusiness" onclick="addBusiness()" showNoRight="false" style="margin-right: 5px"><spring:message code="page.decorationManualTopicList.name11" /></f:a>
                    <f:a alias="decorationManualTopic-editBusiness" onclick="editBusiness()" showNoRight="false" style="margin-right: 5px"><spring:message code="page.decorationManualTopicList.name12" /></f:a>
                    <f:a alias="decorationManualTopic-removeBusiness" onclick="deleteBusiness()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.decorationManualTopicList.name13" /></f:a>
                    <f:a alias="decorationManualTopic-releaseBusiness" onclick="releaseBusiness()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.decorationManualTopicList.name14" /></f:a>
                    <f:a alias="decorationManualTopic-copyBusiness" onclick="copyBusiness()" showNoRight="false" style="margin-right: 5px"><spring:message code="page.decorationManualTopicList.name15" /></f:a>
                    <f:a alias="decorationManualTopic-exportBusiness" onclick="exportBusiness()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.decorationManualTopicList.name16" /></f:a>
                    <f:a alias="decorationManualTopic-downloadBusiness" onclick="downloadBusiness()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.decorationManualTopicList.name17" /></f:a>
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
         sizeList="[50,100,200,500,1000,5000]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons"
         allowSortColumn="true" allowUnselect="false" autoLoad="false"
         url="${ctxPath}/serviceEngineering/core/decorationManualTopic/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div cellCls="actionIcons" width="80" headerAlign="center" align="center" renderer="fileRenderer"
                 cellStyle="padding:0;"><spring:message code="page.decorationManualTopicList.name18" />
            </div>
            <div field="businessNo" width="140" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicList.name" /></div>
            <div field="chapter" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicList.name1" /></div>
            <div field="system" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicList.name2" /></div>
            <div field="topicCode" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicList.name19" /></div>
            <div field="topicName" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicList.name20" /></div>
            <div field="topicType" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicList.name3" /></div>
            <div field="materialCode" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicList.name21" /></div>
            <div field="productSerie" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicList.name4" /></div>
            <div field="salesArea" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicList.name5" /></div>
            <div field="salesModel" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicList.name22" /></div>
            <div field="designModel" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicList.name23" /></div>
            <div field="remark" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicList.name24" /></div>
            <div field="manualVersion" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicList.name6" /></div>
            <div field="manualStatus" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicList.name7" /></div>
            <div field="isPS" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicList.name8" /></div>
            <div field="creatorName" width="80" headerAlign="center" align="center" allowSort="false"><spring:message code="page.decorationManualTopicList.name25" /></div>
            <div field="createTime" width="150" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center" allowSort="true"><spring:message code="page.decorationManualTopicList.name26" /></div>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/decorationManualTopic/exportList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<%----%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentTime = "${currentTime}";
    var coverContent = "${currentUserName}" + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var decorationManualTopicAdmin = "${decorationManualTopicAdmin}";
    //..
    $(function () {
        searchFrm();
    });
    //..
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..
    function fileRenderer(e) {
        var record = e.record;
        var businessId = record.id;
        var existFile = record.existFile;
        var s = '<span  title=' + decorationManualTopicList_name + ' onclick="detailBusiness(\'' + businessId + '\')">' + decorationManualTopicList_name + '</span>';
        return s;
    }
    //..
    function addBusiness() {
        var url = jsUseCtxPath + "/serviceEngineering/core/decorationManualTopic/editPage.do?id=&action=add";
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
    function editBusiness() {
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert(decorationManualTopicList_name1);
            return;
        }
        else if (row.manualStatus != "编辑中" && currentUserId != '1' && currentUserNo != schematicDiagramAdmin) {
            mini.alert(decorationManualTopicList_name2);
            return;
        }
        var id = row.id;
        var url = jsUseCtxPath + "/serviceEngineering/core/decorationManualTopic/editPage.do?id=" + id + '&action=edit';
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
    function deleteBusiness() {
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert(decorationManualTopicList_name1);
            return;
        } else if (row.manualStatus != "编辑中" && currentUserId != '1' && currentUserNo != decorationManualTopicAdmin) {
            mini.alert(decorationManualTopicList_name3);
            return;
        }
        mini.confirm(decorationManualTopicList_name4, decorationManualTopicList_name5, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var id = row.id;
                $.ajax({
                    url: jsUseCtxPath + "/serviceEngineering/core/decorationManualTopic/deleteBusiness.do",
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
    function releaseBusiness() {
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert(decorationManualTopicList_name1);
            return;
        } else if (row.manualStatus != "编辑中") {
            mini.alert(decorationManualTopicList_name6);
            return;
        } else if (row.manualVersion == "" || !row.manualVersion) {
            mini.alert(decorationManualTopicList_name7);
            return;
        }
        mini.confirm(decorationManualTopicList_name8, decorationManualTopicList_name5, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var id = row.id;
                $.ajax({
                    url: jsUseCtxPath + "/serviceEngineering/core/decorationManualTopic/releaseBusiness.do",
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
    function detailBusiness(businessId) {
        var url = jsUseCtxPath + "/serviceEngineering/core/decorationManualTopic/editPage.do?id=" + businessId + '&action=detail';
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
    function copyBusiness() {
        var row = businessListGrid.getSelected();
        if (!row) {
            mini.alert(decorationManualTopicList_name1);
            return;
        } else if (row.manualStatus != "可转出") {
            mini.alert(decorationManualTopicList_name9);
            return;
        }
        var id = row.id;
        var url = jsUseCtxPath + "/serviceEngineering/core/decorationManualTopic/editPage.do?id=" + id + '&action=copy';
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
    //..
    function downloadBusiness() {
        var rows = businessListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert(decorationManualTopicList_name10);
            return;
        }
        var cnt = 0;
        var resFileIds = "";
        var resFileNames = "";
        var ids = "";
        for (var i = 0, l = rows.length; i < l; i++) {
            var r = rows[i];
            if (r.id && r.isExistFile == "true") {
                //用form的方式去提交
                if (cnt > 0) {
                    ids += ',';
                }
                ids += r.id;
                cnt = cnt + 1;
            }
        }
        if (cnt == 0) {
            mini.alert(decorationManualTopicList_name11);
            return;
        }
        else {
            url = "/serviceEngineering/core/decorationManualTopic/zipFileDownload.do";
            downLoadFile(resFileNames, resFileIds, ids, url);
        }
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
