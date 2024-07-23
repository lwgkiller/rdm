<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>标准图片库列表</title>
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
                    <span class="text" style="width:auto"><spring:message code="page.hgxPicArchiveList.name" />：</span>
                    <input class="mini-textbox" id="picCode" name="picCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.hgxPicArchiveList.name1" />：</span>
                    <input class="mini-textbox" id="picType" name="picType"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.hgxPicArchiveList.name2" />：</span>
                    <input class="mini-textbox" id="region" name="region"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.hgxPicArchiveList.name3" />：</span>
                    <input class="mini-textbox" id="dataSrc" name="dataSrc"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.hgxPicArchiveList.name4" />：</span>
                    <input class="mini-textbox" id="applyInst" name="applyInst"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.hgxPicArchiveList.name5" />：</span>
                    <input class="mini-textbox" id="picFileName" name="picFileName"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.hgxPicArchiveList.name6" />：</span>
                    <input class="mini-textbox" id="creatorName" name="creatorName"/>
                </li>


                <li style="margin-left: 10px">
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.hgxPicArchiveList.name7" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.hgxPicArchiveList.name8" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addBusiness()"><spring:message code="page.hgxPicArchiveList.name9" /></a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="editBusiness()"><spring:message code="page.hgxPicArchiveList.name10" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                       onclick="deleteBusiness()"><spring:message code="page.hgxPicArchiveList.name11" /></a>
                <%--<a class="mini-button" style="margin-right: 5px" plain="true" onclick="previewBusiness2()">预览</a>--%>
                <%--<a class="mini-button" style="margin-right: 5px" plain="true" onclick="downloadBusiness2()">下载</a>--%>
                <%--<a class="mini-button" id="importId" style="margin-left: 10px" onclick="openImportWindow()">导入</a>--%>
                <a class="mini-button" id="importId" style="margin-right: 5px" plain="true" onclick="openImportWindow()"><spring:message code="page.hgxPicArchiveList.name12" /></a>
                <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportList()"><spring:message code="page.hgxPicArchiveList.name13" /></a>
                <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addPicFile()"><spring:message code="page.hgxPicArchiveList.name14" /></a>
                <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportFile()"><spring:message code="page.hgxPicArchiveList.name15" /></a>

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
         url="${ctxPath}/serviceEngineering/core/picArchive/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div cellCls="actionIcons" width="80" headerAlign="center" align="center" renderer="fileRenderer"
                 cellStyle="padding:0;"><spring:message code="page.hgxPicArchiveList.name16" />
            </div>
            <div field="picCode" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.hgxPicArchiveList.name" />
            </div>
            <div field="fileName" width="150" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.hgxPicArchiveList.name5" />
            </div>
            <div field="picType" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.hgxPicArchiveList.name1" />
            </div>
            <div field="region" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.hgxPicArchiveList.name2" />
            </div>
            <div field="dataSrc" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.hgxPicArchiveList.name3" />
            </div>
            <div field="applyInst" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.hgxPicArchiveList.name4" />
            </div>
            <div field="creatorName" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.hgxPicArchiveList.name6" />
            </div>
            <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.hgxPicArchiveList.name17" />
            </div>
            <div field="existFile" width="80" headerAlign="center" align="center" renderer="fileStatusRenderer" allowSort="false">
                <spring:message code="page.hgxPicArchiveList.name18" />
            </div>
            <%--<div field="versionStatus" width="80" headerAlign="center" align="center" renderer="render"--%>
                 <%--allowSort="true">--%>
                <%--版本状态--%>
            <%--</div>--%>
            <div cellCls="actionIcons" width="120" headerAlign="center" align="center" renderer="operationRenderer"
                 cellStyle="padding:0;"><spring:message code="page.hgxPicArchiveList.name19" />
            </div>
        </div>
    </div>
</div>

<div id="importWindow" title="<spring:message code="page.hgxPicArchiveList.name20" />" class="mini-window" style="width:750px;height:280px;"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importBusiness()"><spring:message code="page.hgxPicArchiveList.name12" /></a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeImportWindow()"><spring:message code="page.hgxPicArchiveList.name21" /></a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%"><spring:message code="page.hgxPicArchiveList.name22" />：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()"><spring:message code="page.hgxPicArchiveList.name23" />.xls</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%"><spring:message code="page.hgxPicArchiveList.name16" />：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName"
                               readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()"
                               accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadFile"><spring:message code="page.hgxPicArchiveList.name24" /></a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clearUploadFile"><spring:message code="page.hgxPicArchiveList.name25" /></a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>


<form id="excelForm" action="${ctxPath}/serviceEngineering/core/picArchive/exportData.do" method="post" target="excelIFrame">
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
    var importWindow = mini.get("importWindow");



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

        var s = '<span  title=' + hgxPicArchiveList_name + ' onclick="detailBusiness(\'' + businessId + '\')">' + hgxPicArchiveList_name + '</span>';
        return s;
    }

    //..
    function previewBusiness(id, fileName, coverContent) {
        var previewUrl = jsUseCtxPath + "/serviceEngineering/core/picArchive/Preview.do?id=" + id + "&fileName=" + fileName;
        window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverContent) + "&file=" + encodeURIComponent(previewUrl));
    }

    function previewBusiness2() {
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert(hgxPicArchiveList_name1);
            return;
        }
        var id = row.id;
        var fileName = row.fileName;
        if (fileName) {
            previewBusiness(id, fileName, coverContent);
        } else {
            mini.alert(hgxPicArchiveList_name2);
            return;
        }

    }

    //..
    function downloadBusiness(id, description) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/picArchive/Download.do");
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
            mini.alert(hgxPicArchiveList_name1);
            return;
        }
        var id = row.id;
        var fileName = row.fileName;
        if (fileName) {
            // downloadBusiness(id, row.partsAtlasName);
            downloadBusiness(id, row.fileName);
        } else {
            mini.alert(hgxPicArchiveList_name2);
            return;
        }

    }

    //..
    function addBusiness() {
        var url = jsUseCtxPath + "/serviceEngineering/core/picArchive/EditPage.do?id=&action=add";
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
            mini.alert(hgxPicArchiveList_name3);
            return;
        }
        var row = rows[0];
        if (row.CREATE_BY_ != currentUserId && currentUserNo != 'admin') {
            mini.alert(hgxPicArchiveList_name4);
            return;
        }

        var id = row.id;
        var url = jsUseCtxPath + "/serviceEngineering/core/picArchive/EditPage.do?id=" + id + '&action=edit';
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
            mini.alert(hgxPicArchiveList_name3);
            return;
        }
        var row = rows[0];
        if (row.CREATE_BY_ != currentUserId && currentUserNo != 'admin') {
            mini.alert(hgxPicArchiveList_name5);
            return;
        }

        mini.confirm(hgxPicArchiveList_name6, hgxPicArchiveList_name7, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var id = row.id;
                var fileName = row.fileName;
                $.ajax({
                    url: jsUseCtxPath + "/serviceEngineering/core/picArchive/deleteBusiness.do",
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
        var url = jsUseCtxPath + "/serviceEngineering/core/picArchive/EditPage.do?id=" + businessId + '&action=detail';
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
        var downloadUrl = '/serviceEngineering/core/picArchive/fileDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + hgxPicArchiveList_name8 + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.id + '\',\'' + downloadUrl + '\')">' + hgxPicArchiveList_name8 + '</span>';

        return cellHtml;
    }

    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title=' + hgxPicArchiveList_name9 + ' style="color: silver" >' + hgxPicArchiveList_name9 + '</span>';
        } else {
            var url = '/serviceEngineering/core/picArchive/preview.do?fileType=' + fileType;
            s = '<span  title=' + hgxPicArchiveList_name9 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf6(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\',\'' + fileType + '\')">' + hgxPicArchiveList_name9 + '</span>';
        }
        return s;
    }

    //..
    //..


    // function clearFormThis() {
    //     mini.get("picCode").setValue("");
    //     mini.get("designModel").setValue("");
    //     mini.get("vinCode").setValue("");
    //     mini.get("partsAtlasName").setValue("");
    //     mini.get("languageType").setValue("");
    //     searchFrm();
    // }

    // 打包下载
    function exportFile() {
        debugger;
        var rows = businessListGrid.getSelecteds();

        if (rows.length <= 0) {
            mini.alert(hgxPicArchiveList_name1);
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
            mini.alert(hgxPicArchiveList_name10);
            return;
        }
        else {
            url = "/serviceEngineering/core/picArchive/zipFileDownload.do";
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

    function openImportWindow() {
        importWindow.show();
    }

    function closeImportWindow() {
        importWindow.hide();
        clearUploadFile();
        searchFrm();
    }

    function clearUploadFile() {
        $("#inputFile").val('');
        mini.get("fileName").setValue('');
    }

    function importBusiness() {
        var file = null;
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            file = fileList[0];
        }
        if (!file) {
            mini.alert(hgxPicArchiveList_name11);
            return;
        }
        //XMLHttpRequest方式上传表单
        var xhr = false;
        try {
            //尝试创建 XMLHttpRequest 对象，除 IE 外的浏览器都支持这个方法。
            xhr = new XMLHttpRequest();
        } catch (e) {
            //使用较新版本的 IE 创建 IE 兼容的对象（Msxml2.XMLHTTP）。
            xhr = ActiveXobject("Msxml12.XMLHTTP");
        }

        if (xhr.upload) {
            xhr.onreadystatechange = function (e) {
                if (xhr.readyState == 4) {
                    if (xhr.status == 200) {
                        if (xhr.responseText) {
                            var returnObj = JSON.parse(xhr.responseText);
                            var message = '';
                            if (returnObj.message) {
                                message = returnObj.message;
                            }
                            mini.alert(message);
                        }
                    }
                }
            };

            // 开始上传
            xhr.open('POST', jsUseCtxPath + '/serviceEngineering/core/picArchive/importExcel.do', false);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            var fd = new FormData();
            fd.append('importFile', file);
            xhr.send(fd);
        }
    }
    //..
    function downImportTemplate() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/picArchive/importTemplateDownload.do");
        $("body").append(form);
        form.submit();
        form.remove();
    }
    //..
    function uploadFile() {
        $("#inputFile").click();
    }
    //..

    //..
    function getSelectFile() {
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            var fileNameSuffix = fileList[0].name.split('.').pop();
            if (fileNameSuffix == 'xls' || fileNameSuffix == 'xlsx') {
                mini.get("fileName").setValue(fileList[0].name);
            }
            else {
                clearUploadFile();
                mini.alert(hgxPicArchiveList_name12);
            }
        }
    }

    function addPicFile() {

        mini.open({
            title: hgxPicArchiveList_name13,
            url: jsUseCtxPath + "/serviceEngineering/core/picArchive/openUploadWindow.do",
            width: 900,
            height: 350,
            showModal: true,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var passParams = {};
                // passParams.applyId = applyId;
                // passParams.detailId = applyId;
                var data = {passParams: passParams};  //传递上传参数
                iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {
                businessListGrid.load();
            }
        });
    }

    function fileStatusRenderer(e) {
        var record = e.record;
        var existFile = record.existFile;

        var arr = [{'key': true, 'value': '是', 'css': 'green'},
            {'key': false, 'value': '否', 'css': 'red'},

        ];

        return $.formatItemValue(arr, existFile);
    }

    function getFileType(fileName) {
        var suffix="";
        var suffixIndex=fileName.lastIndexOf('.');
        if(suffixIndex!=-1) {
            suffix=fileName.substring(suffixIndex+1).toLowerCase();
        }
        var pdfArray = ['pdf'];
        if(pdfArray.indexOf(suffix)!=-1){
            return 'pdf';
        }
        var officeArray = ['doc','docx','ppt','txt','xlsx','xls','pptx'];
        if(officeArray.indexOf(suffix)!=-1){
            return 'office';
        }
        var picArray = ['jpg','jpeg','jif','bmp','png','tif','gif','svg'];
        if(picArray.indexOf(suffix)!=-1){
            return 'pic';
        }
        return 'other';
    }

</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
