<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>售前文件下载申请</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow1" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<%----%>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="formBusiness" method="post" style="height: 95%;width: 98%">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="businessStatus" name="businessStatus" class="mini-hidden"/>
            <input id="applyUserId" name="applyUserId" class="mini-hidden"/>
            <input id="applyTime" name="applyTime" class="mini-hidden"/>
            <input id="endTime" name="endTime" class="mini-hidden"/>
            <input id="INST_ID_" name="INST_ID_" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    售前文件下载申请
                </caption>
                <tr>
                    <td style="text-align: center;width: 20%">申请编号：</td>
                    <td style="min-width:170px">
                        <input id="businessNo" name="businessNo" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">申请人：</td>
                    <td style="min-width:170px">
                        <input id="applyUserId" name="applyUserId" textname="applyUserName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="申请人" length="50"
                               mainfield="no" single="true" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">申请部门：</td>
                    <td style="min-width:170px">
                        <input id="applyUserDeptName" name="applyUserDeptName" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">用途说明：</td>
                    <td colspan="3">
                        <input id="remarks" name="remarks" class="mini-textarea" style="width:99.1%;height:150px"/>
                    </td>
                </tr>
            </table>
            <%----------------------------------------------------------------------------------------------------------%>
            <p style="font-size: 16px;font-weight: bold;margin-top: 20px">申请下载的售前文件清单
                <span style="font-size: 14px;color:red">（仅允许申请人在流程结束后下载）</span>
            </p>
            <div class="mini-toolbar" style="margin-bottom: 5px" id="demandListToolBar">
                <a id="addDocument" class="mini-button" plain="true" onclick="addDocument()">新增</a>
                <a id="delDocument" class="mini-button" plain="true" onclick="delDocument()">移除</a>
                <a id="downloadDocument" class="mini-button" plain="true" onclick="downloadDocument()" visible="false">下载</a>
            </div>
            <div id="documentListGrid" class="mini-datagrid" allowResize="false" style="height:400px" autoload="true"
                 idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false"
                 multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false" onrowdblclick="onrowdblclick"
                 url="${ctxPath}/presaleDocuments/core/downloadapply/getDownloadDocList.do?mainId=${businessId}">
                <div property="columns">
                    <div type="checkcolumn" width="25"></div>
                    <div type="indexcolumn" headerAlign="center" width="50">序号</div>
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

        </form>
    </div>
</div>
<%--选择售前文件窗口--%>
<div id="selectDocumentWindow" title="选择售前文件" class="mini-window" style="width:1450px;height:700px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px" borderStyle="border-left:0;border-top:0;border-right:0;">
        <li style="margin-right: 15px">
            <span class="text" style="width:auto">文件类型：</span>
            <input id="businessType" name="businessType" class="mini-combobox" style="width: 120px"
                   url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=PresaleDocumentType"
                   valueField="key" textField="value"/>
            <span class="text" style="width:auto">关联销售型号：</span>
            <input id="saleModel" name="saleModel" class="mini-textbox" style="width: 120px"/>
            <span class="text" style="width:auto">关联设计型号：</span>
            <input id="designModel" name="designModel" class="mini-textbox" style="width: 120px"/>
            <span class="text" style="width:auto">关联物料号：</span>
            <input id="materialCode" name="materialCode" class="mini-textbox" style="width: 120px"/>
            <span class="text" style="width:auto">关联产品主管：</span>
            <input id="productManagerName" name="productManagerName" class="mini-textbox" style="width: 120px"/>
            <span class="text" style="width:auto">语种：</span>
            <input id="docLanguage" name="docLanguage" class="mini-textbox" style="width: 120px"/>
        </li>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchDocument()">查询</a>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="clearDocument()">清空查询</a>
        <span style="color: red;font-size: 15px">（仅展示状态为“已发布”的数据）</span>
    </div>
    <div class="mini-fit">
        <div id="documentCandiListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" multiSelect="true"
             allowAlternating="true" pagerButtons="#pagerButtons" onrowdblclick="onrowdblclick" onload="onload"
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
                     renderer="productRenderer2">销售型号
                </div>
                <div field="designModel" width="120" headerAlign="center" align="center" allowSort="true"
                     renderer="productRenderer2">设计型号
                </div>
                <div field="materialCode" width="120" headerAlign="center" align="center" allowSort="true"
                     renderer="productRenderer2">物料编码
                </div>
                <div field="productManagerName" width="120" headerAlign="center" align="center" allowSort="true"
                     renderer="productRenderer2">产品主管
                </div>
                <div field="applicabilityStatement" width="200" headerAlign="center" align="center" allowSort="true" renderer="render">适用性说明</div>
                <div field="releaseTime" width="120" headerAlign="center" align="center" allowSort="true">发布时间</div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectHide()"/>
                </td>
            </tr>
        </table>
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
<%----%>
<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var businessId = "${businessId}";
    var nodeVarsStr = '${nodeVars}';
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var isService = "${isService}";//是否是服务所的人
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var formBusiness = new mini.Form("#formBusiness");
    var documentListGrid = mini.get("documentListGrid");
    var selectDocumentWindow = mini.get("selectDocumentWindow");
    var documentCandiListGrid = mini.get("documentCandiListGrid");
    var productkWindow = mini.get("productkWindow");
    var productListGrid = mini.get("productListGrid");
    var codeName = "";
    //..
    $(function () {
        var url = jsUseCtxPath + "/presaleDocuments/core/downloadapply/getDataById.do";
        $.post(
            url,
            {businessId: businessId},
            function (json) {
                formBusiness.setData(json);
                if (action == 'detail') {
                    formBusiness.setEnabled(false);
                    mini.get("demandListToolBar").hide();
                    $("#detailToolBar").show();
                    if (status != 'DRAFTED') {
                        $("#processInfo").show();
                    }
                    //..审批完成后打开下载按钮
                    if (json.businessStatus == "Z" && json.applyUserId == currentUserId) {
                        mini.get("demandListToolBar").show();
                        mini.get("addDocument").setVisible(false);
                        mini.get("delDocument").setVisible(false);
                        mini.get("downloadDocument").setVisible(true);
                    }
                } else if (action == 'task') {
                    taskActionProcess();
                }
            });
    });
    //..获取任务相关的环境变量，处理表单可见性
    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();
        //获取环境变量
        if (!nodeVarsStr) {
            nodeVarsStr = "[]";
        }
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'codeName') {
                codeName = nodeVars[i].DEF_VAL_;
            }
        }
        if (codeName != 'A') {
            formBusiness.setEnabled(false);
            mini.get("demandListToolBar").hide();
        }

    }
    //..流程引擎调用此方法进行表单数据的获取
    function getData() {
        var formData = _GetFormJsonMini("formBusiness");
        if (formData.SUB_documentListGrid) {
            delete formData.SUB_documentListGrid;
        }
        if (documentListGrid.getData().length > 0) {
            formData.documentListGrid = documentListGrid.getData();
        }
        return formData;
    }
    //..保存草稿
    function saveBusiness(e) {
        window.parent.saveDraft(e);
    }
    //..流程中暂存信息（如编制阶段）
    function saveBusinessInProcess() {
        var formData = getData();
        $.ajax({
            url: jsUseCtxPath + '/presaleDocuments/core/downloadapply/saveBusiness.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        message = "数据保存成功";
                    } else {
                        message = "数据保存失败" + data.message;
                    }
                    mini.alert(message, "提示信息", function () {
                        window.location.reload();
                    });
                }
            }
        });
    }
    //..启动流程
    function startBusinessProcess(e) {
        var formValid = validBusiness();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }
    //下一步审批
    function businessApprove() {
        //编制阶段和流程期间需要上传文件的节点，下一步需要校验表单必填字段
        if (codeName == "A") {
            var formValid = validBusiness();//各种类型的复杂情况都在里面了，除了关联产品的数量放在了okWindow里
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        window.parent.approve();
    }
    //..检验表单是否必填
    function validBusiness() {
        //校验文件，其数量在此校验
        if (documentListGrid.getData().length == 0) {
            return {"result": false, "message": "下载不能为空!"};
        }
        return {"result": true};
    }
    //..
    function addDocument() {
        selectDocumentWindow.show();
        searchDocument();
    }
    //..
    function delDocument() {
        var selecteds = documentListGrid.getSelecteds();
        documentListGrid.removeRows(selecteds);
    }
    //..
    function searchDocument() {
        var paramArray = [];
        paramArray.push({name: "businessType", value: mini.get('businessType').getValue()});
        paramArray.push({name: "saleModel", value: mini.get('saleModel').getValue()});
        paramArray.push({name: "designModel", value: mini.get('designModel').getValue()});
        paramArray.push({name: "materialCode", value: mini.get('materialCode').getValue()});
        paramArray.push({name: "productManagerName", value: mini.get('productManagerName').getValue()});
        paramArray.push({name: "docLanguage", value: mini.get('docLanguage').getValue()});
        paramArray.push({name: "businessStatus", value: "已发布"});
        var data = {};
        data.filter = mini.encode(paramArray);
        documentCandiListGrid.load(data);
    }
    //..
    function clearDocument() {
        mini.get('businessType').setValue('');
        mini.get('saleModel').setValue('');
        mini.get('designModel').setValue('');
        mini.get('materialCode').setValue('');
        mini.get('productManagerName').setValue('');
        mini.get('docLanguage').setValue('');
        searchDocument();
    }
    //..
    function downloadDocument() {
        var rows = documentListGrid.getData();
        if (rows.length > 0) {
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
    function selectOK() {
        var alreadyMap = new Map();
        var alreadyData = documentListGrid.getData();
        for (var i = 0, j = alreadyData.length; i < j; i++) {
            alreadyMap.set(alreadyData[i].id, alreadyData[i]);
        }
        var rows = documentCandiListGrid.getSelecteds();
        if (rows.length > 0) {
            for (var index = 0; index < rows.length; index++) {
                var row = rows[index];
                if (!alreadyMap.has(row.id)) {
                    documentListGrid.addRow(row);
                }
            }
        }
        selectHide();
    }
    //..
    function selectHide() {
        mini.get('businessType').setValue('');
        mini.get('saleModel').setValue('');
        mini.get('designModel').setValue('');
        mini.get('materialCode').setValue('');
        mini.get('productManagerName').setValue('');
        mini.get('docLanguage').setValue('');
        selectDocumentWindow.hide();
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
            }
        }, 1000);
    }
    //..
    function onload(e) {
        e.sender.clearSelect();
    }
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
        var record = documentListGrid.getSelected();
        if (record != null) {
            var Array = JSON.parse(record.productSpectrum);
            productListGrid.setData(Array);
            productkWindow.show();
        }
    }
    //..
    function productRenderer2(e) {
        if (e.value != null && e.value != "") {
            var productSpectrum = e.productSpectrum;
            var html = '<span onclick="detailProduct2()" style="color: blue;text-decoration: underline;cursor: pointer;">' + e.value + '</span>';
            return html;
        }
    }
    //..
    function detailProduct2(productSpectrum) {
        var record = documentCandiListGrid.getSelected();
        if (record != null) {
            var Array = JSON.parse(record.productSpectrum);
            productListGrid.setData(Array);
            productkWindow.show();
        }
    }
</script>
</body>
</html>
