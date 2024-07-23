<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>售前文件列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/presaleDocuments/presaleDocumentsConst.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<%----%>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">文件类型：</span>
                    <input id="businessType" name="businessType" class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=PresaleDocumentType"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">关联销售型号：</span>
                    <input class="mini-textbox" id="saleModel" name="saleModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">关联设计型号：</span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">关联物料号：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">关联产品主管：</span>
                    <input class="mini-textbox" id="productManagerName" name="productManagerName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">负责人：</span>
                    <input class="mini-textbox" id="repUserName" name="repUserName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">语种：</span>
                    <input class="mini-textbox" id="docLanguage" name="docLanguage"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">销售区域：</span>
                    <input class="mini-textbox" id="salesArea" name="salesArea"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">发布时间：</span>
                    <input id="releaseTimeBegin" name="releaseTimeBegin" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">至：</span>
                    <input id="releaseTimeEnd" name="releaseTimeEnd" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">文件状态：</span>
                    <input id="businessStatus" name="businessStatus" class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=PresaleDocumentStatus"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="presaleDocuments-searchFrm" onclick="searchFrm()" showNoRight="false" style="margin-right: 5px">查询</f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <f:a alias="presaleDocuments-editBusiness" onclick="editBusiness()" showNoRight="false"
                         style="margin-right: 5px">编辑</f:a>
                    <f:a alias="presaleDocuments-addBusiness" onclick="addBusiness()" showNoRight="false"
                         style="margin-right: 5px">新增</f:a>
                    <f:a alias="presaleDocuments-copyBusiness" onclick="copyBusiness()" showNoRight="false"
                         style="margin-right: 5px">新增复制</f:a>
                    <f:a alias="presaleDocuments-cloneBusiness" onclick="cloneBusiness()" showNoRight="false"
                         style="margin-right: 5px">升版复制</f:a>
                    <f:a alias="presaleDocuments-languageBusiness" onclick="languageBusiness()" showNoRight="false"
                         style="margin-right: 5px">多语言复制</f:a>
                    <f:a alias="presaleDocuments-removeBusiness" onclick="deleteBusiness()" showNoRight="false"
                         style="margin-right: 5px">删除</f:a>
                    <f:a alias="presaleDocuments-releaseBusiness" onclick="releaseBusiness()" showNoRight="false"
                         style="margin-right: 5px">发布</f:a>
                    <f:a alias="presaleDocuments-exportBusiness" onclick="exportBusiness()" showNoRight="false"
                         style="margin-right: 5px">导出</f:a>
                    <f:a alias="presaleDocuments-downloadBusiness" onclick="downloadBusiness()" showNoRight="false"
                         style="margin-right: 5px">下载</f:a>
                    <f:a alias="presaleDocuments-refreshBusiness" onclick="refreshBusiness()" showNoRight="false" tag="red"
                         style="margin-right: 5px">发起变更</f:a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>
<%----%>
<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowUnselect="false"
         allowResize="false" allowCellWrap="false" showCellTip="true" idField="id"
         allowCellEdit="true" allowCellSelect="true" multiSelect="true" showColumnsMenu="false"
         sizeList="[50,100,200,500,1000,5000]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons"
         allowSortColumn="true" allowUnselect="true" autoLoad="false" sortMode="client" onrowdblclick="onrowdblclick"
         url="${ctxPath}/presaleDocuments/core/masterData/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="25"></div>
            <div field="businessType" width="120" headerAlign="center" align="center" allowSort="true">文件类型</div>
            <div field="repUserName" width="120" headerAlign="center" align="center" allowSort="true">负责人</div>
            <div field="salesArea" width="120" headerAlign="center" align="center" allowSort="true">销售区域</div>
            <div field="docLanguage" width="120" headerAlign="center" align="center" allowSort="true">语种</div>
            <div field="docVersion" width="120" headerAlign="center" align="center" allowSort="true">版本</div>
            <div field="businessStatus" width="120" headerAlign="center" align="center" allowSort="true">状态</div>
            <div field="systemType" width="120" headerAlign="center" align="center" allowSort="true">系统分类</div>
            <div field="saleModel" width="120" headerAlign="center" align="center" allowSort="true"
                 renderer="productRenderer">销售型号
            </div>
            <div field="designModel" width="120" headerAlign="center" align="center" allowSort="true"
                 renderer="productRenderer">设计型号
            </div>
            <div field="materialCode" width="120" headerAlign="center" align="center" allowSort="true"
                 renderer="productRenderer">物料编码
            </div>
            <div field="productManagerName" width="120" headerAlign="center" align="center" allowSort="true"
                 renderer="productRenderer">产品主管
            </div>
            <div field="applicabilityStatement" width="200" headerAlign="center" align="center" allowSort="true" renderer="render">适用性说明</div>
            <div field="releaseTime" width="120" headerAlign="center" align="center" allowSort="true">发布时间</div>
        </div>
    </div>
</div>
<%--关联产品小窗口--%>
<div id="productkWindow" title="关联产品" class="mini-window" style="width:950px;height:400px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-fit" style="font-size: 14px;margin-top: 4px">
        <div id="productListGrid" class="mini-datagrid" style="width: 100%; height: 98%" allowResize="false" allowCellWrap="true"
             showCellTip="true" idField="id" showPager="false" showColumnsMenu="false" allowcelledit="false"
             allowAlternating="true">
            <div property="columns">
                <%--id--%>
                <div field="saleModel_item" width="100" headerAlign="center" align="center">销售型号</div>
                <div field="designModel_item" width="100" headerAlign="center" align="center">设计型号</div>
                <div field="materialCode_item" width="100" headerAlign="center" align="center">物料编码</div>
                <%--productManagerId_item--%>
                <div field="productManagerName_item" width="100" headerAlign="center" align="center">产品主管</div>
            </div>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/presaleDocuments/core/masterData/exportBusiness.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var isPresaleDocumentsAdmin = "${isPresaleDocumentsAdmin}";
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var businessListGrid = mini.get("businessListGrid");
    var productkWindow = mini.get("productkWindow");
    var productListGrid = mini.get("productListGrid");
    //..
    $(function () {
    });
    //..
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..
    function productRenderer(e) {
        if (e.value != null && e.value != "") {
            var productSpectrum = e.productSpectrum;
            var html = '<span onclick="detailProduct()" style="color: blue;text-decoration: underline;cursor: pointer;">' + e.value + '</span>';
            return html;
        }
    }
    //..
    function detailProduct(productSpectrum) {
        var record = businessListGrid.getSelected();
        if (record != null) {
            var Array = JSON.parse(record.productSpectrum);
            productListGrid.setData(Array);
            productkWindow.show();
        }
    }
    //..
    function addBusiness() {
        var url = jsUseCtxPath + "/presaleDocuments/core/masterData/editPage.do?id=&action=add";
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
            mini.alert("请选且仅选中一条记录");
            return;
        }
        var url = jsUseCtxPath + "/presaleDocuments/core/masterData/editPage.do?id=" + rows[0].id + '&action=edit';
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
        var rows = businessListGrid.getSelecteds();
        if (rows.length != 1) {
            mini.alert("请选且仅选中一条记录");
            return;
        }
        var url = jsUseCtxPath + "/presaleDocuments/core/masterData/editPage.do?id=" + rows[0].id + '&action=copy';
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
    function cloneBusiness() {
        var rows = businessListGrid.getSelecteds();
        if (rows.length != 1) {
            mini.alert("请选且仅选中一条记录");
            return;
        }
        if (rows[0].businessStatus != PresaleDocumentStatus.BUSINESS_STATUS_YIFABU) {
            mini.alert("只有" + PresaleDocumentStatus.BUSINESS_STATUS_YIFABU + "状态的数据能升版复制");
            return;
        }
        if (rows[0].businessType == PresaleDocumentType.BUSINESS_TYPE_JISHUWENJIANFUJIAN) {
            mini.alert("技术文件资料附件类型不能进行升版复制");
            return;
        }
        if (rows[0].docLanguage != "中文" && rows[0].docLanguage != "中英文") {
            mini.alert("只有 中文或中英文 语言的数据能进行升版复制");
            return;
        }
        var url = jsUseCtxPath + "/presaleDocuments/core/masterData/editPage.do?id=" + rows[0].id + '&action=clone';
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
    function languageBusiness() {
        var rows = businessListGrid.getSelecteds();
        if (rows.length != 1) {
            mini.alert("请选且仅选中一条记录");
            return;
        }
        if (rows[0].businessStatus != PresaleDocumentStatus.BUSINESS_STATUS_YIFABU) {
            mini.alert("只有" + PresaleDocumentStatus.BUSINESS_STATUS_YIFABU + "状态的数据能进行多语言复制");
            return;
        }
        if (rows[0].businessType == PresaleDocumentType.BUSINESS_TYPE_JISHUWENJIANFUJIAN) {
            mini.alert("技术文件资料附件类型不能进行多语言复制");
            return;
        }
        if (rows[0].docLanguage != "中文" && rows[0].docLanguage != "中英文") {
            mini.alert("只有 中文或中英文 语言的数据能进行多语言复制");
            return;
        }
        var url = jsUseCtxPath + "/presaleDocuments/core/masterData/editPage.do?id=" + rows[0].id + '&action=language';
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
        var rows = businessListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
//        for (var i = 0, j = rows.length; i < j; i++) {
//            if (rows[i].businessStatus == PresaleDocumentStatus.BUSINESS_STATUS_YIFABU) {
//                mini.alert("不能删除 已发布 的记录");
//                return;
//            }
//        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.id);
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/presaleDocuments/core/masterData/deleteBusiness.do",
                    method: 'POST',
                    data: {ids: rowIds.join(',')},
                    postJson: false,
                    showMsg: false,
                    success: function (returnData) {
                        if (returnData.success) {
                            mini.alert(returnData.message);
                            searchFrm();
                        } else {
                            mini.alert("删除失败:" + returnData.message);
                        }
                    },
                    fail: function (returnData) {
                        mini.alert("删除失败:" + returnData.message);
                    }
                });
            }
        });
    }
    //..
    function releaseBusiness() {
        var rows = businessListGrid.getSelecteds();
        if (rows.length != 1) {
            mini.alert("请选且仅选中一条记录");
            return;
        }
        for (var i = 0, j = rows.length; i < j; i++) {
            if (rows[i].businessStatus != PresaleDocumentStatus.BUSINESS_STATUS_BIANJIZHONG) {
                mini.alert("只有" + PresaleDocumentStatus.BUSINESS_STATUS_BIANJIZHONG + "状态的数据能进行发布");
                return;
            }
        }
        mini.confirm("确定发布选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.id);
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/presaleDocuments/core/masterData/releaseBusiness.do",
                    method: 'POST',
                    data: {ids: rowIds.join(',')},
                    postJson: false,
                    showMsg: false,
                    success: function (returnData) {
                        if (returnData.success) {
                            mini.alert(returnData.message);
                            searchFrm();
                        } else {
                            mini.alert("发布失败:" + returnData.message);
                        }
                    },
                    fail: function (returnData) {
                        mini.alert("发布失败:" + returnData.message);
                    }
                });
            }
        });
    }
    //..
    function refreshBusiness() {
        var rows = businessListGrid.getSelecteds();
        if (rows.length != 1) {
            mini.alert("请选且仅选中一条记录");
            return;
        }
        if (rows[0].businessStatus != PresaleDocumentStatus.BUSINESS_STATUS_YIFABU) {
            mini.alert("只有" + PresaleDocumentStatus.BUSINESS_STATUS_YIFABU + "状态的数据能发起变更");
            return;
        }
        if (rows[0].docLanguage != "中文" && rows[0].docLanguage != "中英文") {
            mini.alert("只有 中文或中英文 语言的数据能发起变更");
            return;
        }
        var url = jsUseCtxPath + "/bpm/core/bpmInst/PresaleDocumentApply/start.do?masterDataId_=" +
            rows[0].id + "&businessType_=" + rows[0].businessType;
        window.open(url);
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
            mini.alert("请至少选中一条记录");
            return;
        }
        for (var i = 0, l = rows.length; i < l; i++) {
            if (rows[i].businessStatus != PresaleDocumentStatus.BUSINESS_STATUS_YIFABU) {
                mini.alert("只有" + PresaleDocumentStatus.BUSINESS_STATUS_YIFABU + "状态的数据能进行批量下载");
                return;
            }
        }
        var ids = "";
        var fileNames = "";
        var fileIds = "";
        var cnt = 0;
        for (var i = 0, l = rows.length; i < l; i++) {
            var r = rows[i];
            if (r.fileIds) {
                //这里注意，防止以后单个记录有多个文件
                var fileIdsArray = r.fileIds.split(',');
                var fileNamesArray = r.fileNames.split(',');
                for (var i2 = 0, l2 = fileIdsArray.length; i2 < l2; i2++) {
                    ids += r.id + ',';
                    fileIds += fileIdsArray[i2] + ',';
                    fileNames += fileNamesArray[i2] + ',';
                    cnt = cnt + 1;
                }
            }
        }
        if (cnt > 0) {
            ids = ids.substring(0, ids.length - 1);
            fileIds = fileIds.substring(0, fileIds.length - 1);
            fileNames = fileNames.substring(0, fileNames.length - 1);
        }
        var url = "/presaleDocuments/core/masterData/zipFileDownload.do";
        zipDownloadFile(fileNames, fileIds, ids, url);
    }
    //..
    function zipDownloadFile(fileName, fileId, formId, urlValue) {
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
    //..
    function onrowdblclick(e) {
        var grid = e.sender;
        var record = e.record;
        var url = jsUseCtxPath + "/presaleDocuments/core/masterData/editPage.do?id=" + record.id + '&action=detail';
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
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
