<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>零件图册归档</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <input class="mini-hidden" id="isComplex" name="isComplex"/>

            <ul>
                <f:a alias="partsAtlasArchivie-addBusiness" onclick="addFile()" showNoRight="false" style="margin-right: 5px">上传文件</f:a>
                <li style="margin-left:15px;margin-right: 15px">
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">整机编码：</span>
                    <input class="mini-textbox" id="vinCode" name="vinCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">图册名称：</span>
                    <input class="mini-textbox" id="partsAtlasName" name="partsAtlasName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">文件类型：</span>
                    <input id="fileType" name="fileType" class="mini-combobox"
                           textField="key" valueField="key" emptyText="请选择..."
                           allowInput="false" showNullItem="false" nullItemText="请选择..." data = "[{'key':'零件图册'},{'key' : '易损件清单'},{'key'  : '保养件清单'}]"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">语言类型：</span>
                    <input class="mini-textbox" id="languageType" name="languageType"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">归档人：</span>
                    <input class="mini-textbox" id="creatorName" name="creatorName"/>
                </li>


                <li style="margin-left: 10px">
                    <f:a alias="partsAtlasArchivie-searchFrm" onclick="searchFrm()" showNoRight="false" style="margin-right: 5px">查询</f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearFormThis()">清空查询</a>
<%--                    <f:a alias="partsAtlasArchivie-addBusiness" onclick="addBusiness()" showNoRight="false" style="margin-right: 5px">新增</f:a>--%>
<%--                    <f:a alias="partsAtlasArchivie-editBusiness" onclick="editBusiness()" showNoRight="false" style="margin-right: 5px">编辑</f:a>--%>
                    <f:a alias="partsAtlasArchivie-removeBusiness" onclick="deleteBusiness()" showNoRight="false"
                         style="margin-right: 5px">删除</f:a>

                    <f:a alias="partsAtlasArchivie-previewBusiness" onclick="previewBusiness2()" showNoRight="false"
                         style="margin-right: 5px">预览</f:a>
                    <f:a alias="partsAtlasArchivie-downloadBusiness" onclick="downloadBusiness2()" showNoRight="false"
                         style="margin-right: 5px">下载</f:a>
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
         url="${ctxPath}/serviceEngineering/core/patsAtlasFileCollection/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20" ></div>
            <div cellCls="actionIcons" width="0" headerAlign="center" align="center" renderer="render"
                 cellStyle="padding:0;">操作
            </div>
            <div field="vinCode" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">整机编号</div>
            <div field="partsAtlasName" width="300" headerAlign="center" align="center" renderer="render" allowSort="true">图册名称</div>
            <div field="fileType" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">文件类型</div>
            <div field="languageType" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">语言类型</div>
            <div field="creatorName" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">归档人</div>
            <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">归档时间</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentUserNo = "${currentUserNo}";
    var currentTime = "${currentTime}";
    var coverContent = "${currentUserName}" + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var vinCode = "${vinCode}";
    var languageType = "${languageType}";


    $(function () {
        mini.get("vinCode").setValue(vinCode);
        mini.get("languageType").setValue(languageType);
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

        var s = '<span  title="明细" onclick="detailBusiness(\'' + businessId + '\')">明细</span>';
        return s;
    }
    //..
    function previewBusiness(id, coverContent,fileNameStr) {
        var fileType = getFileType(fileNameStr);
        if (fileType == "pdf") {
            var previewUrl = jsUseCtxPath + "/serviceEngineering/core/patsAtlasFileCollection/Preview.do?id=" + id+"&fileName="+fileNameStr;
            window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverContent) + "&file=" + encodeURIComponent(previewUrl));
        }
        if (fileType == "office") {
            var previewUrl = jsUseCtxPath + "/serviceEngineering/core/patsAtlasFileCollection/previewOffice.do?id=" + id+"&fileName="+fileNameStr;
            var preWindow = window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverContent) + "&file=" + encodeURIComponent(previewUrl));
            preWindow.onload = function () {
                if (fileNameStr == '') {
                    fileNameStr = "文件预览";
                }
                preWindow.document.title = fileNameStr;
            };
        }
        var loop = setInterval(function () {
            if (preWindow.closed) {
                clearInterval(loop);
            }
            else {
                preWindow.document.title = fileNameStr;
            }
        }, 1000);
    }
    function previewBusiness2() {
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var fileNameStr = row.fileName;
        var id = row.id;
        var existFile = row.existFile;
        if(existFile){
            previewBusiness(id, coverContent,fileNameStr);
        }else{
            mini.alert("当前记录没有文件");
            return;
        }

    }
    //..
    function downloadBusiness(id, description) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/patsAtlasFileCollection/Download.do");
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
            mini.alert("请至少选中一条记录");
            return;
        }
        var id = row.id;
        var existFile = row.existFile;
        if(existFile){
            // downloadBusiness(id, row.partsAtlasName);
            downloadBusiness(id, row.fileName);
        }else{
            mini.alert("当前记录没有文件");
            return;
        }

    }
    //..
    function addBusiness() {
        var url = jsUseCtxPath + "/serviceEngineering/core/patsAtlasFileCollection/EditPage.do?id=&action=add";
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
    function addFile() {
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/serviceEngineering/core/patsAtlasFileCollection/openUploadWindow.do" ,
            width: 930,
            height: 450,
            showModal: false,
            allowResize: true,
            onload:function () {
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
        if(row.CREATE_BY_!=currentUserId && currentUserNo!='admin'){
            mini.alert("只有归档人能编辑");
            return;
        }

        var id = row.id;
        var url = jsUseCtxPath + "/serviceEngineering/core/patsAtlasFileCollection/EditPage.do?id=" + id + '&action=edit';
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
            mini.alert("请至少选中一条记录");
            return;
        }
        if(row.CREATE_BY_!=currentUserId&&currentUserNo!='admin'){
            mini.alert("只有归档人才能删除");
            return;
        }

        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var id = row.id;
                $.ajax({
                    url: jsUseCtxPath + "/serviceEngineering/core/patsAtlasFileCollection/deleteBusiness.do",
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
    //..
    //..
    function detailBusiness(businessId) {
        var url = jsUseCtxPath + "/serviceEngineering/core/patsAtlasFileCollection/EditPage.do?id=" + businessId + '&action=detail';
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
    //..

    function clearFormThis() {
        mini.get("vinCode").setValue("");
        mini.get("partsAtlasName").setValue("");
        mini.get("fileType").setValue("");
        mini.get("languageType").setValue("");
        searchFrm();
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
