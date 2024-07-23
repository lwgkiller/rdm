<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>操保手册topic列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.manualTopicList.name" />: </span>
                    <input class="mini-textbox" id="topicName" name="topicName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.manualTopicList.name1" />: </span>
                    <input class="mini-textbox" id="topicId" name="topicId"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.manualTopicList.name2" />: </span>
                    <input class="mini-textbox" id="topicType" name="topicType"/>
                </li>

                <li style="margin-right: 15px">

                    <span class="text" style="width:auto"><spring:message code="page.manualTopicList.name3" />: </span>
                    <input class="mini-textbox" id="version" name="version"/>
                </li>


                <li style="margin-right: 15px">

                    <span class="text" style="width:auto"><spring:message code="page.manualTopicList.name4" />: </span>
                    <input class="mini-textbox" id="productLine" name="productLine"/>
                </li>

                <li style="margin-right: 15px">

                    <span class="text" style="width:auto"><spring:message code="page.manualTopicList.name5" />: </span>
                    <input class="mini-textbox" id="productSeries" name="productSeries"/>
                </li>

                <li style="margin-right: 15px">

                    <span class="text" style="width:auto"><spring:message code="page.manualTopicList.name6" />: </span>
                    <input class="mini-textbox" id="region" name="region"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.manualTopicList.name7" />: </span>
                    <input class="mini-textbox" id="productSettings" name="productSettings"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.manualTopicList.name8" />: </span>
                    <input class="mini-textbox" id="creatorName" name="creatorName"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.manualTopicList.name9" />: </span>
                    <input class="mini-textbox" id="standardName" name="standardName"/>
                </li>
                <li style="margin-right: 15px">

                    <span class="text" style="width:auto"><spring:message code="page.manualTopicList.name10" />: </span>
                    <input id="versionStatus" name="versionStatus"
                           class="mini-combobox"
                           style="width:98%;"
                           textField="text" valueField="id"
                           required="false" allowInput="false" showNullItem="true"
                           nullItemText="<spring:message code="page.manualTopicList.name11" />..."
                           data="[{'id' : 'history','text' : '历史版本'}
                                       ,{'id' : 'current','text' : '有效'}
                                  ]"
                    />
                </li>
                <li style="margin-right: 15px">

                    <span class="text" style="width:auto"><spring:message code="page.manualTopicList.name12" />: </span>
                    <input id="status" name="status"
                           class="mini-combobox"
                           style="width:98%;"
                           textField="text" valueField="id"
                           required="false" allowInput="false" showNullItem="true"
                           nullItemText="<spring:message code="page.manualTopicList.name11" />..."
                           data="[
                           {'id' : 'DRAFT','text' : '草稿'}
                           ,{'id' : 'READY','text' : '待评审'}
                           ,{'id' : 'RUNNING','text' : '评审中'}
                           ,{'id' : 'SUCCESS_END','text' : '已评审'}
                                  ]"
                    />
                </li>


                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.manualTopicList.name13" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.manualTopicList.name14" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addBusiness()"><spring:message code="page.manualTopicList.name15" /></a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="editBusiness()"><spring:message code="page.manualTopicList.name16" /></a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="confirmTopic()"><spring:message code="page.manualTopicList.name17" /></a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="confirmTopicCancel()"><spring:message code="page.manualTopicList.name18" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                       onclick="removeTopic()"><spring:message code="page.manualTopicList.name19" /></a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportList()"><spring:message code="page.manualTopicList.name20" /></a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportFile()"><spring:message code="page.manualTopicList.name21" /></a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="topicCopy()"><spring:message code="page.manualTopicList.name22" /></a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         onlyCheckSelection="true"
         url="${ctxPath}/serviceEngineering/core/topic/applyList.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="45" headerAlign="center" align="center"><spring:message code="page.manualTopicList.name23" /></div>
            <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.manualTopicList.name24" />
            </div>
            <%--<div field="creatorName" headerAlign="center" align="center" allowSort="false">创建人</div>--%>
            <div field="topicName" headerAlign="center" align="center" allowSort="false"><spring:message code="page.manualTopicList.name25" /></div>
            <div field="topicId" headerAlign="center" align="center" allowSort="false"><spring:message code="page.manualTopicList.name1" /></div>
            <div field="topicTextName" headerAlign="center" align="center" allowSort="false"><spring:message code="page.manualTopicList.name26" /></div>
            <div field="topicType" headerAlign="center" align="center" allowSort="false"><spring:message code="page.manualTopicList.name2" /></div>
            <div field="region" headerAlign="center" align="center" allowSort="false"><spring:message code="page.manualTopicList.name27" /></div>
            <div field="productLine" headerAlign="center" align="center" allowSort="false"><spring:message code="page.manualTopicList.name4" /></div>
            <div field="productSeries" headerAlign="center" align="center" allowSort="false"><spring:message code="page.manualTopicList.name5" /></div>
            <div field="productSettings" headerAlign="center" align="center" allowSort="false"><spring:message code="page.manualTopicList.name7" /></div>
            <div field="remark" headerAlign="center" align="center" allowSort="false"><spring:message code="page.manualTopicList.name28" /></div>
            <div field="version" headerAlign="center" align="center" allowSort="true"><spring:message code="page.manualTopicList.name3" /></div>
            <div field="versionStatus" headerAlign="center" align="center" allowSort="true"
            renderer="onHistoryRenderer"><spring:message code="page.manualTopicList.name10" />
            </div>
            <div field="status" width="50" headerAlign="center" align="center" allowSort="true"
            renderer="onStatusRenderer"><spring:message code="page.manualTopicList.name12" />
            </div>
            <div field="creatorName" headerAlign="center" align="center" allowSort="false"><spring:message code="page.manualTopicList.name8" /></div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                 allowSort="true"><spring:message code="page.manualTopicList.name29" />
            </div>
        </div>
    </div>
</div>


<form id="excelForm" action="${ctxPath}/serviceEngineering/core/topic/exportData.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var isAdmin = "${isAdmin}";
    var applyId = "${applyId}";
    var currentUserName = "${currentUserName}";
    var currentUserNo = "${currentUserNo}";
    var currentTime = "${currentTime}";
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
    function onActionRenderer(e) {
        var record = e.record;
        var businessId = record.id;

        var s = '<span  title=' + manualTopicList_name + ' onclick="topicDetail(\'' + businessId + '\')">' + manualTopicList_name + '</span>';
        return s;
    }

    //..
    function previewBusiness(id, fileName, coverContent) {
        var previewUrl = jsUseCtxPath + "/serviceEngineering/core/zzzFile/Preview.do?id=" + id + "&fileName=" + fileName;
        window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverContent) + "&file=" + encodeURIComponent(previewUrl));
    }

    function previewBusiness2() {
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert(manualTopicList_name1);
            return;
        }
        var id = row.id;
        var fileName = row.fileName;
        if (fileName) {
            previewBusiness(id, fileName, coverContent);
        } else {
            mini.alert(manualTopicList_name2);
            return;
        }

    }

    //..
    function downloadBusiness(id, description) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/zzzFile/Download.do");
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
            mini.alert(manualTopicList_name1);
            return;
        }
        var id = row.id;
        var fileName = row.fileName;
        if (fileName) {
            // downloadBusiness(id, row.partsAtlasName);
            downloadBusiness(id, row.fileName);
        } else {
            mini.alert(manualTopicList_name2);
            return;
        }
    }

    //..
    function addBusiness() {
        var url = jsUseCtxPath + "/serviceEngineering/core/topic/applyEditPage.do?action=add";
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
            mini.alert(manualTopicList_name1);
            return;
        }
        // 本人 或特殊用户或管理员才能编辑

        if ((row.status!="DRAFT"|| row.CREATE_BY_ != currentUserId) &&currentUserNo != 'admin' && isAdmin != "true" ) {
            mini.alert(manualTopicList_name3);
            return;
        }

        var id = row.id;
        var url = jsUseCtxPath + "/serviceEngineering/core/topic/applyEditPage.do?applyId=" + id + '&action=edit';
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
            mini.alert(manualTopicList_name1);
            return;
        }
        if (row.CREATE_BY_ != currentUserId && currentUserNo != 'admin') {
            mini.alert(manualTopicList_name4);
            return;
        }

        mini.confirm(manualTopicList_name5, manualTopicList_name6, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var id = row.id;
                var fileName = row.fileName;
                $.ajax({
                    url: jsUseCtxPath + "/serviceEngineering/core/topic/deleteApply.do",
                    type: 'POST',
                    data: mini.encode({ids: id, fileName: fileName}),
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

    function topicDetail(applyId) {
        var action = "detail";
        var url = jsUseCtxPath + "/serviceEngineering/core/topic/applyEditPage.do?action=detail&applyId=" + applyId;
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


    function removeTopic(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = businessListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert(manualTopicList_name1);
            return;
        }
        mini.confirm(manualTopicList_name5, manualTopicList_name6, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                var existStartInst = false;
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    debugger;
                    if ((r.status == null || r.status == 'DRAFT' || r.status == "") && r.CREATE_BY_ == currentUserId) {

                        rowIds.push(r.id);

                    }
                    else {
                        existStartInst = true;
                        continue;
                    }
                }
                if (existStartInst) {
                    alert(manualTopicList_name7);
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/serviceEngineering/core/topic/deleteApply.do",
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

    function confirmTopic(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = businessListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert(manualTopicList_name1);
            return;
        }
        mini.confirm(manualTopicList_name8, manualTopicList_name6, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                var existStartInst = false;
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    debugger;
                    if ((r.status == null || r.status == 'DRAFT' || r.status == "")) {

                        rowIds.push(r.id);
                    }
                    else {
                        existStartInst = true;
                        continue;
                    }
                }
                if (existStartInst) {
                    alert(manualTopicList_name9);
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/serviceEngineering/core/topic/confirmApply.do",
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


    function confirmTopicCancel(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = businessListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert(manualTopicList_name1);
            return;
        }
        mini.confirm(manualTopicList_name10, manualTopicList_name6, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                var existStartInst = false;
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    debugger;
                    if ((r.status == null || r.status == 'READY' || r.status == "")) {

                        rowIds.push(r.id);
                    }
                    else {
                        existStartInst = true;
                        continue;
                    }
                }
                if (existStartInst) {
                    alert(manualTopicList_name11);
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/serviceEngineering/core/topic/confirmApplyCancel.do",
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

    //..
    //..
    //..


    //..
    //..


    function clearFormThis() {
        mini.get("salesModel").setValue("");
        mini.get("designModel").setValue("");
        mini.get("vinCode").setValue("");
        mini.get("partsAtlasName").setValue("");
        mini.get("languageType").setValue("");
        searchFrm();
    }

    function topicCopy() {
        debugger;
        var rows = businessListGrid.getSelecteds();
        if (rows.length != 1) {
            mini.alert(manualTopicList_name12);
            return;
        }
        mini.confirm(manualTopicList_name13, manualTopicList_name6, function (action) {
            if (action != 'ok') {
                return;
            }
            else {
                var action = "copy";
                var applyId = rows[0].id;
                // var url = jsUseCtxPath + "/bpm/core/bpmInst/ZXDPS/start.do?action=copy&pk="+applyId;
                var url = jsUseCtxPath + "/serviceEngineering/core/topic/applyEditPage.do?action=copy&applyId=" + applyId;
                var winObj = window.open(url);
                var loop = setInterval(function () {
                    if (winObj.closed) {
                        // 这里直接把版本改成历史版本
                        _SubmitJson({
                            url: jsUseCtxPath + "/serviceEngineering/core/topic/updateVersion.do",
                            method: 'POST',
                            showMsg: false,
                            data: {id: applyId},
                            success: function (data) {
                                if (data) {
                                    // mini.alert(data.message);
                                    searchFrm();
                                }
                            }
                        });
                        clearInterval(loop);
                        if (businessListGrid) {
                            businessListGrid.reload()
                        }
                    }
                }, 1000);


            }
        });


    }

    // 打包下载
    function exportFile() {
        debugger;
        var rows = businessListGrid.getSelecteds();

        if (rows.length <= 0) {
            mini.alert(manualTopicList_name1);
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
            mini.alert(manualTopicList_name14);
            return;
        }

        else {
            url = "/serviceEngineering/core/topic/zipFileDownload.do";
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

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [{'key': 'DRAFT', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '评审中', 'css': 'green'},
            {'key': 'READY', 'value': '待评审', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '已评审', 'css': 'blue'},
        ];

        return $.formatItemValue(arr, status);
    }

    function onHistoryRenderer(e) {
        var record = e.record;
        var versionStatus = record.versionStatus;

        var arr = [{'key': 'current', 'value': '有效', 'css': 'green'},
            {'key': 'history', 'value': '历史版本', 'css': 'red'},

        ];

        return $.formatItemValue(arr, versionStatus);
    }


</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
