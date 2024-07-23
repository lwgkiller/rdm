<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>装修手册归档</title>
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
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualFileList.name" />：</span>
                    <input class="mini-textbox" id="salesModel" name="salesModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualFileList.name1" />：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualFileList.name2" />：</span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualFileList.name3" />：</span>
                    <input class="mini-textbox" id="cpzgName" name="cpzgName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualFileList.name4" />：</span>
                    <input class="mini-textbox" id="manualDescription" name="manualDescription"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualFileList.name5" />：</span>
                    <input class="mini-textbox" id="manualLanguage" name="manualLanguage"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualFileList.name6" />：</span>
                    <input class="mini-textbox" id="manualCode" name="manualCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualFileList.name7" />：</span>
                    <input class="mini-textbox" id="manualType" name="manualType"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualFileList.name8" />：</span>
                    <input class="mini-textbox" id="manualVersion" name="manualVersion"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualFileList.name9" />：</span>
                    <input class="mini-textbox" id="manualEdition" name="manualEdition"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualFileList.name10" />：</span>
                    <input class="mini-textbox" id="manualPlanType" name="manualPlanType"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualFileList.name11" />：</span>
                    <input class="mini-textbox" id="keyUser" name="keyUser"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualFileList.name12" />：</span>
                    <input class="mini-textbox" id="manualStatus" name="manualStatus"/>
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="decorationManualFile-searchFrm" onclick="searchFrmThis()" showNoRight="false" style="margin-right: 5px"><spring:message code="page.decorationManualFileList.name13" /></f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearFormThis()"><spring:message code="page.decorationManualFileList.name14" /></a>
                    <f:a alias="decorationManualFile-addBusiness" onclick="addBusiness()" showNoRight="false" style="margin-right: 5px"><spring:message code="page.decorationManualFileList.name15" /></f:a>
                    <f:a alias="decorationManualFile-editBusiness" onclick="editBusiness()" showNoRight="false" style="margin-right: 5px"><spring:message code="page.decorationManualFileList.name16" /></f:a>
                    <f:a alias="decorationManualFile-removeBusiness" onclick="deleteBusiness()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.decorationManualFileList.name17" /></f:a>
                    <f:a alias="decorationManualFile-releaseBusiness" onclick="releaseBusiness()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.decorationManualFileList.name18" /></f:a>
                    <f:a alias="decorationManualFile-copyBusiness" onclick="copyBusiness()" showNoRight="false" style="margin-right: 5px"><spring:message code="page.decorationManualFileList.name19" /></f:a>
                    <f:a alias="decorationManualFile-exportBusinessThis" onclick="exportBusinessThis()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.decorationManualFileList.name20" /></f:a>
                    <f:a alias="decorationManualFile-previewBusiness" onclick="previewBusiness2()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.decorationManualFileList.name21" /></f:a>
                    <f:a alias="decorationManualFile-downloadBusiness" onclick="downloadBusiness2()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.decorationManualFileList.name22" /></f:a>
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
         url="${ctxPath}/serviceEngineering/core/decorationManualFile/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div cellCls="actionIcons" width="150" headerAlign="center" align="center" renderer="fileRenderer"
                 cellStyle="padding:0;"><spring:message code="page.decorationManualFileList.name23" />
            </div>
            <div field="salesModel" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualFileList.name" /></div>
            <div field="materialCode" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualFileList.name1" /></div>
            <div field="designModel" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualFileList.name2" /></div>
            <div field="cpzgName" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualFileList.name3" /></div>
            <div field="manualDescription" width="300" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message code="page.decorationManualFileList.name4" /></div>
            <div field="manualLanguage" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualFileList.name24" /></div>
            <div field="manualCode" width="300" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualFileList.name6" /></div>
            <div field="manualType" width="300" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualFileList.name7" /></div>
            <div field="manualVersion" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualFileList.name8" /></div>
            <div field="manualEdition" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualFileList.name9" /></div>
            <div field="manualPlanType" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualFileList.name10" /></div>
            <div field="keyUser" displayField="keyUser" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualFileList.name11" /></div>
            <div field="publishTime" width="120" headerAlign="center" align="center" dateFormat="yyyy-MM-dd" allowSort="true"><spring:message code="page.decorationManualFileList.name25" />
            </div>
            <div field="manualStatus" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualFileList.name12" /></div>
            <div field="remark" width="300" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualFileList.name26" /></div>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/decorationManualFile/exportList.do" method="post" target="excelIFrame">
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
    var manualType = "${manualType}";
    var decorationManualAdmin = "${decorationManualAdmin}";
    //..
    $(function () {
        mini.get("salesModel").setValue(salesModel);
        mini.get("designModel").setValue(designModel);
        mini.get("materialCode").setValue(materialCode);
        mini.get("manualLanguage").setValue(manualLanguage);
        mini.get("manualType").setValue(manualType);
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
        var existFile = record.existFile;
        var s = '<span  title=' + decorationManualFileList_name + ' onclick="detailBusiness(\'' + businessId + '\')">' + decorationManualFileList_name + '</span>';
        return s;
    }
    //..
    function previewBusiness(id, coverContent) {
        var previewUrl = jsUseCtxPath + "/serviceEngineering/core/decorationManualFile/preview.do?id=" + id;
        window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverContent) + "&file=" + encodeURIComponent(previewUrl));
    }
    function previewBusiness2() {
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert(decorationManualFileList_name1);
            return;
        }
        var id = row.id;
        var existFile = row.existFile;
        if (existFile) {
            previewBusiness(id, coverContent);
        } else {
            mini.alert(decorationManualFileList_name2);
            return;
        }
    }
    //..
    function downloadBusiness(id, description) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/decorationManualFile/download.do");
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
            mini.alert(decorationManualFileList_name1);
            return;
        }
        var id = row.id;
        var existFile = row.existFile;
        if (existFile) {
            downloadBusiness(id, row.manualDescription);
        } else {
            mini.alert(decorationManualFileList_name2);
            return;
        }
    }
    //..
    function addBusiness() {
        var url = jsUseCtxPath + "/serviceEngineering/core/decorationManualFile/editPage.do?id=&action=add";
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
            mini.alert(decorationManualFileList_name1);
            return;
        }
        else if (row.manualStatus != "编辑中" && currentUserId != '1' && currentUserNo != decorationManualAdmin) {
            mini.alert(decorationManualFileList_name3);
            return;
        }
        var id = row.id;
        var url = jsUseCtxPath + "/serviceEngineering/core/decorationManualFile/editPage.do?id=" + id + '&action=edit';
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
            mini.alert(decorationManualFileList_name1);
            return;
        } else if (row.manualStatus != "编辑中" && currentUserId != '1' && currentUserNo != decorationManualAdmin) {
            mini.alert(decorationManualFileList_name4);
            return;
        }
        mini.confirm(decorationManualFileList_name5, decorationManualFileList_name6, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var id = row.id;
                $.ajax({
                    url: jsUseCtxPath + "/serviceEngineering/core/decorationManualFile/deleteBusiness.do",
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
            mini.alert(decorationManualFileList_name1);
            return;
        } else if (row.manualStatus != "编辑中") {
            mini.alert(decorationManualFileList_name7);
            return;
        } else if (row.manualDescription == "" || !row.manualDescription) {
            mini.alert(decorationManualFileList_name8);
            return;
        } else if (row.manualVersion == "" || !row.manualVersion) {
            mini.alert(decorationManualFileList_name9);
            return;
        }
        mini.confirm(decorationManualFileList_name10, decorationManualFileList_name6, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var id = row.id;
                $.ajax({
                    url: jsUseCtxPath + "/serviceEngineering/core/decorationManualFile/releaseBusiness.do",
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
        var url = jsUseCtxPath + "/serviceEngineering/core/decorationManualFile/editPage.do?id=" + businessId + '&action=detail';
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
            mini.alert(decorationManualFileList_name1);
            return;
        } else if (row.manualStatus != "可转出") {
            mini.alert(decorationManualFileList_name11);
            return;
        }
        var id = row.id;
        var url = jsUseCtxPath + "/serviceEngineering/core/decorationManualFile/editPage.do?id=" + id + '&action=copy';
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
        if (false) {
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
        if (false) {
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
        mini.get("keyUser").setValue("");
        mini.get("manualStatus").setValue("");
        mini.get("cpzgName").setValue("");
        mini.get("manualVersion").setValue("");
        mini.get("manualType").setValue("");
        mini.get("manualEdition").setValue("");
        searchFrm();
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
