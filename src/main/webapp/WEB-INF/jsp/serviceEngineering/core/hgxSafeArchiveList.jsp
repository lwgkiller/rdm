<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>标准安全库列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>

</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <input class="mini-hidden" id="isComplex" name="isComplex"/>
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.hgxSafeArchiveList.name" />：</span>
                    <input class="mini-textbox" id="safeCode" name="safeCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.hgxSafeArchiveList.name1" />：</span>
                    <input class="mini-textbox" id="safeType" name="safeType"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.hgxSafeArchiveList.name2" />：</span>
                    <input class="mini-textbox" id="region" name="region"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.hgxSafeArchiveList.name3" />：</span>
                    <input class="mini-textbox" id="fileName" name="fileName"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.hgxSafeArchiveList.name4" />：</span>
                    <input class="mini-textbox" id="creatorName" name="creatorName"/>
                </li>


                <li style="margin-left: 10px">
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.hgxSafeArchiveList.name5" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.hgxSafeArchiveList.name6" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addBusiness()"><spring:message code="page.hgxSafeArchiveList.name7" /></a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="editBusiness()"><spring:message code="page.hgxSafeArchiveList.name8" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                       onclick="deleteBusiness()"><spring:message code="page.hgxSafeArchiveList.name9" /></a>
                <%--<a class="mini-button" style="margin-right: 5px" plain="true" onclick="previewBusiness2()">预览</a>--%>
                <%--<a class="mini-button" style="margin-right: 5px" plain="true" onclick="downloadBusiness2()">下载</a>--%>
                <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportList()"><spring:message code="page.hgxSafeArchiveList.name10" /></a>
                <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportFile()"><spring:message code="page.hgxSafeArchiveList.name11" /></a>
                <a class="mini-button" style="margin-right: 5px" plain="true" onclick="safeCopy()"><spring:message code="page.hgxSafeArchiveList.name12" /></a>

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
         url="${ctxPath}/serviceEngineering/core/safeArchive/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div cellCls="actionIcons" width="80" headerAlign="center" align="center" renderer="fileRenderer"
                 cellStyle="padding:0;"><spring:message code="page.hgxSafeArchiveList.name13" />
            </div>
            <div field="safeCode" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.hgxSafeArchiveList.name" />
            </div>
            <div field="fileName" width="150" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.hgxSafeArchiveList.name3" />
            </div>
            <div field="safeType" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.hgxSafeArchiveList.name1" />
            </div>
            <div field="region" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.hgxSafeArchiveList.name2" />
            </div>
            <div field="version" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.hgxSafeArchiveList.name14" />
            </div>
            <div field="versionStatus" headerAlign="center" align="center" allowSort="true"
                 renderer="onHistoryRenderer"><spring:message code="page.hgxSafeArchiveList.name15" />
            </div>

            <div field="creatorName" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.hgxSafeArchiveList.name4" />
            </div>
            <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.hgxSafeArchiveList.name16" />
            </div>
            <div field="existFile" width="80" headerAlign="center" align="center" renderer="fileStatusRenderer" allowSort="true">
                <spring:message code="page.hgxSafeArchiveList.name17" />
            </div>
            <div cellCls="actionIcons" width="120" headerAlign="center" align="center" renderer="operationRenderer"
                 cellStyle="padding:0;"><spring:message code="page.hgxSafeArchiveList.name18" />
            </div>
        </div>
    </div>
</div>


<form id="excelForm" action="${ctxPath}/serviceEngineering/core/safeArchive/exportData.do" method="post" target="excelIFrame">
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
    var coverContent = "${currentUserName}" + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;



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

        var s = '<span  title=' + hgxSafeArchiveList_name + ' onclick="detailBusiness(\'' + businessId + '\')">' + hgxSafeArchiveList_name + '</span>';
        return s;
    }

    //..
    function previewBusiness(id, fileName, coverContent) {
        var previewUrl = jsUseCtxPath + "/serviceEngineering/core/safeArchive/Preview.do?id=" + id + "&fileName=" + fileName;
        window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverContent) + "&file=" + encodeURIComponent(previewUrl));
    }

    function previewBusiness2() {
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert(hgxSafeArchiveList_name1);
            return;
        }
        var id = row.id;
        var fileName = row.fileName;
        if (fileName) {
            previewBusiness(id, fileName, coverContent);
        } else {
            mini.alert(hgxSafeArchiveList_name2);
            return;
        }

    }

    //..
    function downloadBusiness(id, description) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/safeArchive/Download.do");
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
            mini.alert(hgxSafeArchiveList_name1);
            return;
        }
        var id = row.id;
        var fileName = row.fileName;
        if (fileName) {
            // downloadBusiness(id, row.partsAtlasName);
            downloadBusiness(id, row.fileName);
        } else {
            mini.alert(hgxSafeArchiveList_name2);
            return;
        }

    }

    //..
    function addBusiness() {
        var url = jsUseCtxPath + "/serviceEngineering/core/safeArchive/EditPage.do?id=&action=add";
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
        if (rows.length!=1 ) {
            mini.alert(hgxSafeArchiveList_name3);
            return;
        }
        var row = rows[0];
        if (row.CREATE_BY_ != currentUserId && currentUserNo != 'admin') {
            mini.alert(hgxSafeArchiveList_name4);
            return;
        }

        var id = row.id;
        var url = jsUseCtxPath + "/serviceEngineering/core/safeArchive/EditPage.do?id=" + id + '&action=edit';
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
        if (rows.length!=1 ) {
            mini.alert(hgxSafeArchiveList_name3);
            return;
        }
        var row = rows[0];
        if (row.CREATE_BY_ != currentUserId && currentUserNo != 'admin') {
            mini.alert(hgxSafeArchiveList_name5);
            return;
        }

        mini.confirm(hgxSafeArchiveList_name6, hgxSafeArchiveList_name7, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var id = row.id;
                var fileName = row.fileName;
                $.ajax({
                    url: jsUseCtxPath + "/serviceEngineering/core/safeArchive/deleteBusiness.do",
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
        var url = jsUseCtxPath + "/serviceEngineering/core/safeArchive/EditPage.do?id=" + businessId + '&action=detail';
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

    function operationRenderer(e) {
        var record = e.record;
        //预览、下载和删除
        if (record.existFile != true) {
            return;
        }
        var cellHtml = returnPreviewSpan(record.fileName, record.id, record.id, coverContent);
        var downloadUrl = '/serviceEngineering/core/safeArchive/fileDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + hgxSafeArchiveList_name8 + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.id + '\',\'' + downloadUrl + '\')">' + hgxSafeArchiveList_name8 + '</span>';

        return cellHtml;
    }

    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title=' + hgxSafeArchiveList_name9 + ' style="color: silver" >' + hgxSafeArchiveList_name9 + '</span>';
        } else {
            var url = '/serviceEngineering/core/safeArchive/preview.do?fileType=' + fileType;
            s = '<span  title=' + hgxSafeArchiveList_name9 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf6(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\',\'' + fileType + '\')">' + hgxSafeArchiveList_name9 + '</span>';
        }
        return s;
    }

    //..
    //..

    // 打包下载
    function exportFile() {
        debugger;
        var rows = businessListGrid.getSelecteds();

        if (rows.length <= 0) {
            mini.alert(hgxSafeArchiveList_name1);
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
            // 仅有文件的去下载
            if (r.id && r.existFile) {
                fileData.push({"id": r.id});
                // 用form的方式去提交
                if (cnt > 0) {
                    ids += ',';
                    resFileNames += ',';
                }
                ids += r.id;
                resFileNames += r.fileName;
                cnt = cnt + 1;
            }

        }
        if (cnt == 0) {
            mini.alert(hgxSafeArchiveList_name10);
            return;
        }
        else {
            url = "/serviceEngineering/core/safeArchive/zipFileDownload.do";
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


    function safeCopy() {
        debugger;
        var rows = businessListGrid.getSelecteds();
        if (rows.length != 1) {
            mini.alert(hgxSafeArchiveList_name11);
            return;
        }
        mini.confirm(hgxSafeArchiveList_name12, hgxSafeArchiveList_name7, function (action) {
            if (action != 'ok') {
                return;
            }
            else {
                var action = "copy";
                var applyId = rows[0].id;
                var url = jsUseCtxPath + "/serviceEngineering/core/safeArchive/EditPage.do?action=copy&id=" + applyId;
                var winObj = window.open(url);
                var loop = setInterval(function () {
                    if (winObj.closed) {
                        // 这里直接把版本改成历史版本
                        _SubmitJson({
                            url: jsUseCtxPath + "/serviceEngineering/core/safeArchive/updateVersion.do",
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

    function onHistoryRenderer(e) {
        var record = e.record;
        var versionStatus = record.versionStatus;

        var arr = [{'key': 'current', 'value': '有效', 'css': 'green'},
            {'key': 'history', 'value': '历史版本', 'css': 'red'},

        ];

        return $.formatItemValue(arr, versionStatus);
    }

    function fileStatusRenderer(e) {
        var record = e.record;
        var existFile = record.existFile;

        var arr = [{'key': true, 'value': '是', 'css': 'green'},
            {'key': false, 'value': '否', 'css': 'red'},

        ];

        return $.formatItemValue(arr, existFile);
    }




</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
