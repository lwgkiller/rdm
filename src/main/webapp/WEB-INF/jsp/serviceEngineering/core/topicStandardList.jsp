<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>再制造文档列表</title>
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
                    <span class="text" style="width:auto"><spring:message code="page.topicStandardList.name" />：</span>
                    <input class="mini-textbox" id="categoryName" name="categoryName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.topicStandardList.name1" />：</span>
                    <input class="mini-textbox" id="standardIdNumber" name="standardIdNumber"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.topicStandardList.name2" />：</span>
                    <input class="mini-textbox" id="region" name="region"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.topicStandardList.name3" />：</span>
                    <input id="guanbiao" name="guanbiao"
                           class="mini-combobox"
                           style="width:98%;"
                           textField="text" valueField="id"
                           required="false" allowInput="false" showNullItem="true"
                           nullItemText="<spring:message code="page.topicStandardList.name4" />..."
                           data="[{'id' : '贯标中','text' : '贯标中'}
                                       ,{'id' : '已贯标','text' : '已贯标'}
                                       ,{'id' : '未贯标','text' : '未贯标'}
                                  ]"
                    />
                </li>



                <li style="margin-left: 10px">
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.topicStandardList.name5" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.topicStandardList.name6" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addBusiness()"><spring:message code="page.topicStandardList.name7" /></a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="editBusiness()"><spring:message code="page.topicStandardList.name8" /></a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="confirmComplete()"><spring:message code="page.topicStandardList.name9" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                       onclick="deleteBusiness()"><spring:message code="page.topicStandardList.name10" /></a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportList()"><spring:message code="page.topicStandardList.name11" /></a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportFile()"><spring:message code="page.topicStandardList.name12" /></a>
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
         allowCellEdit="true" allowCellSelect="true" multiSelect="true" showColumnsMenu="false"
         sizeList="[50,100,200,500]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons"
         allowSortColumn="true" allowUnselect="true" autoLoad="false"
         url="${ctxPath}/serviceEngineering/core/topicStandard/applyList.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div cellCls="actionIcons" width="150" headerAlign="center" align="center" renderer="fileRenderer"
                 cellStyle="padding:0;"><spring:message code="page.topicStandardList.name13" />
            </div>
            <div field="categoryName" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.topicStandardList.name" />
            </div>

            <div field="standardIdNumber" width="120" headerAlign="center" align="center" renderer="render"
                 allowSort="true">
                <spring:message code="page.topicStandardList.name1" />
            </div>
            <div field="standardName" width="120" headerAlign="center" align="center" renderer="render"
                 allowSort="true">
                <spring:message code="page.topicStandardList.name14" />
            </div>
            <div field="year" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.topicStandardList.name15" />
            </div>
            <div field="region" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.topicStandardList.name2" />
            </div>
            <div field="isApplied" width="80" headerAlign="center" align="center" renderer="onGuanbiaoRenderer"
                 allowSort="true">
                <spring:message code="page.topicStandardList.name3" />
            </div>
            <div field="creatorName" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.topicStandardList.name16" />
            </div>
            <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.topicStandardList.name17" />
            </div>
        </div>
    </div>
</div>

<form id="excelForm" action="${ctxPath}/serviceEngineering/core/topicStandard/exportData.do" method="post"
      target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentUserNo = "${currentUserNo}";
    var currentTime = "${currentTime}";
    var isAdmin = "${isAdmin}";
    var coverContent = "${currentUserName}" + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var salesModel = "${salesModel}";
    var menuType = "${menuType}";
    var designModel = "${designModel}";
    var vinCode = "${vinCode}";
    var launguageType = "${launguageType}";


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

        var s = '<span  title=' + topicStandardList_name + ' onclick="detailBusiness(\'' + businessId + '\')">' + topicStandardList_name + '</span>';
        return s;
    }

    //..
    function previewBusiness(id, fileName, coverContent) {
        var previewUrl = jsUseCtxPath + "/serviceEngineering/core/topicStandard/Preview.do?id=" + id + "&fileName=" + fileName;
        window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverContent) + "&file=" + encodeURIComponent(previewUrl));
    }

    function previewBusiness2() {
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert(topicStandardList_name1);
            return;
        }
        var id = row.id;
        var fileName = row.fileName;
        if (fileName) {
            previewBusiness(id, fileName, coverContent);
        } else {
            mini.alert(topicStandardList_name2);
            return;
        }

    }

    //..
    function downloadBusiness(id, description) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/topicStandard/Download.do");
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
            mini.alert(topicStandardList_name1);
            return;
        }
        var id = row.id;
        var fileName = row.fileName;
        if (fileName) {
            // downloadBusiness(id, row.partsAtlasName);
            downloadBusiness(id, row.fileName);
        } else {
            mini.alert(topicStandardList_name2);
            return;
        }

    }

    //..
    function addBusiness() {
        var url = jsUseCtxPath + "/serviceEngineering/core/topicStandard/EditPage.do?id=&action=add";
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

        var rows = businessListGrid.getSelecteds();
        if (rows.length != 1) {
            mini.alert(topicStandardList_name3);
            return;
        }
        var row = rows[0];

        if ((row.isApplied!="未贯标"|| row.CREATE_BY_ != currentUserId) &&currentUserNo != 'admin' && isAdmin != "true" ) {
            mini.alert(topicStandardList_name4);
            return;
        }

        var id = row.id;
        var url = jsUseCtxPath + "/serviceEngineering/core/topicStandard/EditPage.do?applyId=" + id + '&action=edit';
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
        // var row = businessListGrid.getSelected();
        // if (row == null) {
        //     mini.alert("请至少选中一条记录");
        //     return;
        // }

        var rows = businessListGrid.getSelecteds();
        if (rows.length != 1) {
            mini.alert(topicStandardList_name3);
            return;
        }
        var row = rows[0];
        if (row.CREATE_BY_ != currentUserId && currentUserNo != 'admin'&&  isAdmin != "true") {
            mini.alert(topicStandardList_name6);
            return;
        }
        if (row.isApplied == "贯标中" && row.isComplete == "1") {
            if (currentUserNo != 'admin' && isAdmin != "true") {
                mini.alert(topicStandardList_name7);
                return;
            }
        }


        mini.confirm(topicStandardList_name8, topicStandardList_name9, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var id = row.id;
                var fileName = row.fileName;
                $.ajax({
                    url: jsUseCtxPath + "/serviceEngineering/core/topicStandard/deleteBusiness.do",
                    type: 'POST',
                    data: mini.encode({id: id, fileName: fileName}),
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
    //..
    //..
    function detailBusiness(businessId) {
        var url = jsUseCtxPath + "/serviceEngineering/core/topicStandard/EditPage.do?applyId=" + businessId + '&action=detail';
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

    function topicDetail(applyId) {
        var action = "detail";
        var url = jsUseCtxPath + "/serviceEngineering/core/topicStandard/applyEditPage.do?action=detail&applyId=" + applyId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload()
                }
            }
        }, 1000);
    }

    //..
    //..


    function clearFormThis() {
        mini.get("standardIdNumber").setValue("");
        mini.get("standardName").setValue("");
        mini.get("type").setValue("");
        searchFrm();
    }

    function exportFile() {
        var rows = businessListGrid.getSelecteds();

        if (rows.length <= 0) {
            mini.alert(topicStandardList_name1);
            return;
        }
        var fileData = [];
        var obj = {};
        var cnt = 0;
        var resFileIds = "";
        var resFileNames = "";
        var ids = "";
        for (var i = 0, l = rows.length; i < l; i++) {
            var r = rows[i];
            // todo 这里要考虑可以下载的状态
            if (r.id) {
                // if (r.resFileId && r.status == "SUCCESS_END") {
                fileData.push({"id": r.id});
                // 用form的方式去提交
                if (cnt > 0) {
                    ids += ',';
                }
                ids += r.id;
                cnt = cnt + 1;
            }

        }
        if (cnt == 0) {
            mini.alert(topicStandardList_name10);
            return;
        }

        else {
            url = "/serviceEngineering/core/topicStandard/zipFileDownload.do";
            downLoadFile(resFileNames, resFileIds, ids, url);

        }


    }

    function exportList() {
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

    function downLoadFile(fileName, fileId, formId, urlValue) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + urlValue);
        var inputFileName = $("<input>");
        inputFileName.attr("type", "hidden");
        inputFileName.attr("name", "fileName");
        inputFileName.attr("value", fileName);
        var inputFileId = $("<input>");
        inputFileId.attr("type", "hidden");
        inputFileId.attr("name", "fileId");
        inputFileId.attr("value", fileId);
        var inputFormId = $("<input>");
        inputFormId.attr("type", "hidden");
        inputFormId.attr("name", "formId");
        inputFormId.attr("value", formId);
        $("body").append(form);
        form.append(inputFileName);
        form.append(inputFileId);
        form.append(inputFormId);
        form.submit();
        form.remove();
    }

    function onGuanbiaoRenderer(e) {
        var record = e.record;
        var isApplied = record.isApplied;
        var arr = [{'key': '贯标中', 'value': '贯标中', 'css': 'green'},
            {'key': '未贯标', 'value': '未贯标', 'css': 'red'},

        ];
        if (record.isComplete == '1') {
            arr = [{'key': '贯标中', 'value': '已贯标', 'css': 'blue'},]
        }


        return $.formatItemValue(arr, isApplied);
    }

    function confirmComplete(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = businessListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert(topicStandardList_name1);
            return;
        }
        mini.confirm(topicStandardList_name11, topicStandardList_name9, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                var existStartInst = false;
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if ((r.isApplied =="贯标中"&&r.isComplete!="1")) {
                        rowIds.push(r.id);
                    }
                    else {
                        existStartInst = true;
                        continue;
                    }
                }
                if (existStartInst) {
                    alert(topicStandardList_name12);
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/serviceEngineering/core/topicStandard/confirmApply.do",
                        method: 'POST',
                        showMsg: false,
                        data: {ids: rowIds.join(',')},
                        success: function (data) {
                            if (data) {
                                mini.alert(data.message);
                                searchFrm();
                            }
                        }
                    });
                }
            }
        });
    }

</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
