<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>操保手册归档</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <input class="mini-hidden" id="isComplex" name="isComplex"/>
            <ul>
                <li style="margin-left:15px;margin-right: 15px">
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualfileList.name" />：</span>
                    <input class="mini-textbox" id="salesModel" name="salesModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualfileList.name1" />：</span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualfileList.name2" />：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualfileList.name3" />：</span>
                    <input class="mini-textbox" id="manualDescription" name="manualDescription"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualfileList.name4" />：</span>
                    <input class="mini-textbox" id="manualLanguage" name="manualLanguage"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualfileList.name5" />：</span>
                    <input class="mini-textbox" id="manualCode" name="manualCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualfileList.name6" />：</span>
                    <input class="mini-textbox" id="isCE" name="isCE"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualfileList.name7" />：</span>
                    <input class="mini-textbox" id="keyUser" name="keyUser"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualfileList.name8" />：</span>
                    <input class="mini-textbox" id="manualStatus" name="manualStatus"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualfileList.name9" />：</span>
                    <input class="mini-textbox" id="demandListNo" name="demandListNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualfileList.name10" />：</span>
                    <input id="applyTimeBegin" name="applyTimeBegin" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto"><spring:message code="page.maintenanceManualfileList.name11" />：</span>
                    <input id="applyTimeEnd" name="applyTimeEnd" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualfileList.name12" />：</span>
                    <input id="publishTimeBegin" name="publishTimeBegin" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto"><spring:message code="page.maintenanceManualfileList.name11" />：</span>
                    <input id="publishTimeEnd" name="publishTimeEnd" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualfileList.name13" />：</span>
                    <input class="mini-textbox" id="cpzgName" name="cpzgName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualfileList.name14" />：</span>
                    <input class="mini-textbox" id="manualVersion" name="manualVersion"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualfileList.name15" />：</span>
                    <input class="mini-textbox" id="manualEdition" name="manualEdition"/>
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="maintenanceManualfile-searchFrm" onclick="searchFrmThis()" showNoRight="false" style="margin-right: 5px"><spring:message code="page.maintenanceManualfileList.name16" /></f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearFormThis()"><spring:message code="page.maintenanceManualfileList.name17" /></a>
                    <f:a alias="maintenanceManualfile-addBusiness" onclick="addBusiness()" showNoRight="false" style="margin-right: 5px"><spring:message code="page.maintenanceManualfileList.name18" /></f:a>
                    <f:a alias="maintenanceManualfile-editBusiness" onclick="editBusiness()" showNoRight="false" style="margin-right: 5px"><spring:message code="page.maintenanceManualfileList.name19" /></f:a>
                    <f:a alias="maintenanceManualfile-removeBusiness" onclick="deleteBusiness()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.maintenanceManualfileList.name20" /></f:a>
                    <f:a alias="maintenanceManualfile-releaseBusiness" onclick="releaseBusiness()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.maintenanceManualfileList.name21" /></f:a>
                    <f:a alias="maintenanceManualfile-refreshBusiness" onclick="refreshBusiness()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.maintenanceManualfileList.name22" /></f:a>
                    <f:a alias="maintenanceManualfile-copyBusiness" onclick="copyBusiness()" showNoRight="false" style="margin-right: 5px"><spring:message code="page.maintenanceManualfileList.name23" /></f:a>
                    <f:a alias="maintenanceManualfile-exportBusinessThis" onclick="exportBusinessThis()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.maintenanceManualfileList.name24" /></f:a>
                    <f:a alias="maintenanceManualfile-previewBusiness" onclick="previewBusiness2()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.maintenanceManualfileList.name25" /></f:a>
                    <f:a alias="maintenanceManualfile-downloadBusiness" onclick="downloadBusiness2()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.maintenanceManualfileList.name26" /></f:a>
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
         allowSortColumn="true" allowUnselect="true" autoLoad="false"
         url="${ctxPath}/serviceEngineering/core/maintenanceManualfile/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div cellCls="actionIcons" width="150" headerAlign="center" align="center" renderer="fileRenderer"
                 cellStyle="padding:0;"><spring:message code="page.maintenanceManualfileList.name27" />
            </div>
            <div field="salesModel" width="120" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message code="page.maintenanceManualfileList.name28" /></div>
            <div field="designModel" width="120" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message code="page.maintenanceManualfileList.name1" /></div>
            <div field="materialCode" width="120" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message code="page.maintenanceManualfileList.name29" /></div>
            <div field="manualDescription" width="300" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message code="page.maintenanceManualfileList.name3" /></div>
            <div field="cpzgName" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message code="page.maintenanceManualfileList.name13" /></div>
            <div field="manualLanguage" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message code="page.maintenanceManualfileList.name4" /></div>
            <div field="manualCode" width="300" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message code="page.maintenanceManualfileList.name5" /></div>
            <div field="manualVersion" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message code="page.maintenanceManualfileList.name14" /></div>
            <div field="manualEdition" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message code="page.maintenanceManualfileList.name15" /></div>
            <div field="isCE" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message code="page.maintenanceManualfileList.name30" /></div>
            <div field="CEStatus" width="100" headerAlign="center" align="center" renderer="render" allowSort="true">EC自声明状态</div>
            <div field="keyUser" displayField="keyUser" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message code="page.maintenanceManualfileList.name7" /></div>
            <div field="publishTime" width="120" headerAlign="center" align="center" dateFormat="yyyy-MM-dd" renderer="render" allowSort="true"><spring:message code="page.maintenanceManualfileList.name31" />
            </div>
            <div field="manualStatus" width="120" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message code="page.maintenanceManualfileList.name32" /></div>
            <div field="remark" width="300" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message code="page.maintenanceManualfileList.name33" /></div>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/maintenanceManualfile/exportList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentTime = "${currentTime}";
    var coverContent = "${currentUserName}" + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var salesModel = "${salesModel}";
    var designModel = "${designModel}";
    var materialCode = "${materialCode}";
    var manualLanguage = "${manualLanguage}";
    var isCE = "${isCE}";
    var maintenanceManualAdmin = "${maintenanceManualAdmin}";

    $(function () {
        mini.get("salesModel").setValue(salesModel);
        mini.get("designModel").setValue(designModel);
        mini.get("materialCode").setValue(materialCode);
        mini.get("manualLanguage").setValue(manualLanguage);
        mini.get("isCE").setValue(isCE);
        searchFrmThis();
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
        var manualDescription = record.manualDescription;
        var existFile = record.existFile;
//
//        if (existFile) {
//            var s = '<span title="预览"  onclick="previewBusiness(\'' + businessId + '\',\'' + coverContent + '\')">预览</span>';
//            s += '<span title="下载" onclick="downloadBusiness(\'' + businessId + '\',\'' + manualDescription + '\')">下载</span>';
//        } else {
//            var s = '<span title="预览"  style="color: silver">预览</span>';
//            s += '<span title="下载" style="color: silver">下载</span>';
//        }
        var s = '<span  title=' + maintenanceManualfileList_name + ' onclick="detailBusiness(\'' + businessId + '\')">' + maintenanceManualfileList_name + '</span>';
        return s;
    }
    //..
    function previewBusiness(id, coverContent) {
        var previewUrl = jsUseCtxPath + "/serviceEngineering/core/maintenanceManualfile/Preview.do?id=" + id;
        window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverContent) + "&file=" + encodeURIComponent(previewUrl));
    }
    function previewBusiness2() {
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert(maintenanceManualfileList_name1);
            return;
        }
        var id = row.id;
        var existFile = row.existFile;
        if (existFile) {
            previewBusiness(id, coverContent);
        } else {
            mini.alert(maintenanceManualfileList_name2);
            return;
        }
    }
    //..
    function downloadBusiness(id, description) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/maintenanceManualfile/Download.do");
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
    function downloadBusiness2() {
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert(maintenanceManualfileList_name1);
            return;
        }
        var id = row.id;
        var existFile = row.existFile;
        if (existFile) {
            downloadBusiness(id, row.manualDescription);
        } else {
            mini.alert(maintenanceManualfileList_name2);
            return;
        }
    }
    //..
    function addBusiness() {
        var url = jsUseCtxPath + "/serviceEngineering/core/maintenanceManualfile/EditPage.do?id=&action=add";
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
            mini.alert(maintenanceManualfileList_name1);
            return;
        }
//        else if (row.manualStatus != "编辑中" && currentUserId != '1') {
//            mini.alert("只有 编辑中 状态的数据能编辑");
//            return;
//        }
        var id = row.id;
        var url = jsUseCtxPath + "/serviceEngineering/core/maintenanceManualfile/EditPage.do?id=" + id + '&action=edit';
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
        debugger;
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert(maintenanceManualfileList_name1);
            return;
        } else if (row.manualStatus != "编辑中" && currentUserId != '1' && currentUserNo != maintenanceManualAdmin) {
            mini.alert(maintenanceManualfileList_name3);
            return;
        }
        mini.confirm(maintenanceManualfileList_name4, maintenanceManualfileList_name5, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var id = row.id;
                $.ajax({
                    url: jsUseCtxPath + "/serviceEngineering/core/maintenanceManualfile/deleteBusiness.do",
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
        debugger;
        if (row == null) {
            mini.alert(maintenanceManualfileList_name1);
            return;
        } else if (row.manualStatus != "编辑中") {
            mini.alert(maintenanceManualfileList_name3);
            return;
        } else if (row.manualDescription == "" || !row.manualDescription) {
            mini.alert(maintenanceManualfileList_name6);
            return;
        } else if (row.manualVersion == "" || !row.manualVersion) {
            mini.alert(maintenanceManualfileList_name7);
            return;
        }
        mini.confirm(maintenanceManualfileList_name8, maintenanceManualfileList_name5, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var id = row.id;
                $.ajax({
                    url: jsUseCtxPath + "/serviceEngineering/core/maintenanceManualfile/releaseBusiness.do",
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
    function refreshBusiness() {
        mini.confirm(maintenanceManualfileList_name9, maintenanceManualfileList_name5, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var loading = mini.loading(maintenanceManualfileList_name10, maintenanceManualfileList_name11);
                $.ajax({
                    url: jsUseCtxPath + "/serviceEngineering/core/maintenanceManualfile/refreshBusiness.do",
                    type: 'POST',
                    contentType: 'application/json',
                    success: function (data) {
                        mini.hideMessageBox(loading);
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
        var url = jsUseCtxPath + "/serviceEngineering/core/maintenanceManualfile/EditPage.do?id=" + businessId + '&action=detail';
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
            mini.alert(maintenanceManualfileList_name1);
            return;
        } else if (row.manualStatus != "可打印") {
            mini.alert(maintenanceManualfileList_name12);
            return;
        }
        var id = row.id;
        var url = jsUseCtxPath + "/serviceEngineering/core/maintenanceManualfile/EditPage.do?id=" + id + '&action=copy';
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
    //由于最后三个条件出现后，会调用不同的查询，来避免join的重复结果干扰主查询的结果
    //所以，加了一个隐藏参数isComplex，有最后三个条件就往后端传true，否则就往后端
    //传false，根据这个值来判断调用哪一个查询
    function searchFrmThis() {
        if (mini.get("demandListNo").getValue() != "" ||
            mini.get("applyTimeBegin").getValue() != "" ||
            mini.get("applyTimeEnd").getValue() != "" ||
            mini.get("publishTimeBegin").getValue() != "" ||
            mini.get("publishTimeEnd").getValue() != "") {
            mini.get("isComplex").setValue("true");
        } else {
            mini.get("isComplex").setValue("false");
        }
        searchFrm();
    }
    //由于最后三个条件出现后，会调用不同的查询，来避免join的重复结果干扰主查询的结果
    //所以，加了一个隐藏参数isComplex，有最后三个条件就往后端传true，否则就往后端
    //传false，根据这个值来判断调用哪一个查询
    function exportBusinessThis() {
        if (mini.get("demandListNo").getValue() != "" ||
            mini.get("applyTimeBegin").getValue() != "" ||
            mini.get("applyTimeEnd").getValue() != "" ||
            mini.get("publishTimeBegin").getValue() != "" ||
            mini.get("publishTimeEnd").getValue() != "") {
            mini.get("isComplex").setValue("true");
        } else {
            mini.get("isComplex").setValue("false");
        }
        exportBusiness();
    }
    //由于最后三个条件出现后，会调用不同的查询，来避免join的重复结果干扰主查询的结果
    //所以，加了一个隐藏参数isComplex，有最后三个条件就往后端传true，否则就往后端
    //传false，根据这个值来判断调用哪一个查询。但是原生的清空查询不会真正清空mini组件的值，因此也要改造
    function clearFormThis() {
        mini.get("isComplex").setValue("");
        mini.get("salesModel").setValue("");
        mini.get("designModel").setValue("");
        mini.get("materialCode").setValue("");
        mini.get("manualDescription").setValue("");
        mini.get("manualLanguage").setValue("");
        mini.get("manualCode").setValue("");
        mini.get("isCE").setValue("");
        mini.get("keyUser").setValue("");
        mini.get("manualStatus").setValue("");
        mini.get("demandListNo").setValue("");
        mini.get("applyTimeBegin").setValue("");
        mini.get("applyTimeEnd").setValue("");
        mini.get("publishTimeBegin").setValue("");
        mini.get("publishTimeEnd").setValue("");
        mini.get("manualVersion").setValue("");
        mini.get("manualEdition").setValue("");
        searchFrm();
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
