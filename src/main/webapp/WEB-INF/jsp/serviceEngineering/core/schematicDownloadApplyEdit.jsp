<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title>原理图下载申请表单</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()"><spring:message code="page.schematicDownloadApplyEdit.name" /></a>
        <a id="closeWindow1" class="mini-button" onclick="CloseWindow()"><spring:message code="page.schematicDownloadApplyEdit.name1" /></a>
    </div>
</div>

<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="schematicDownloadForm" method="post" style="height: 95%;width: 98%">
            <input class="mini-hidden" id="id" name="id"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <%--                基本信息--%>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.schematicDownloadApplyEdit.name2" />：</td>
                    <td style="min-width:170px">
                        <input id="applyNumber" name="applyNumber" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.schematicDownloadApplyEdit.name3" />：</td>
                    <td style="min-width:170px">
                        <input id="CREATE_BY_" name="CREATE_BY_" class="mini-textbox" style="display: none"/>
                        <input id="creatorName" name="creatorName" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.schematicDownloadApplyEdit.name4" />：</td>
                    <td style="min-width:170px">
                        <input name="creatorDeptId" class="mini-textbox" style="display: none"/>
                        <input id="creatorDeptName" name="creatorDeptName" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.schematicDownloadApplyEdit.name5" /></td>
                    <td colspan="3">
						<textarea id="useDesc" name="useDesc" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:100px;line-height:25px;"
                                  label="<spring:message code="page.schematicDownloadApplyEdit.name6" />" datatype="varchar" allowinput="true"
                                  emptytext="<spring:message code="page.schematicDownloadApplyEdit.name7" />..." mwidth="80" wunit="%" mheight="20000" hunit="px"></textarea>
                    </td>
                </tr>
            </table>
            <%----------------------------------------------------申请下载的原理图清单------------------------------------------------------%>
            <p style="font-size: 16px;font-weight: bold;margin-top: 20px"><spring:message code="page.schematicDownloadApplyEdit.name8" />
                <span style="font-size: 14px;color:red"><spring:message code="page.schematicDownloadApplyEdit.name9" /></span>
            </p>
            <div class="mini-toolbar" style="margin-bottom: 5px" id="demandListToolBar">
                <a class="mini-button" id="selfAddBtn" plain="true" onclick="addManualFile()"><spring:message code="page.schematicDownloadApplyEdit.name10" /></a>
                <a class="mini-button btn-red" id="delDemand" plain="true" onclick="delManualFile()"><spring:message code="page.schematicDownloadApplyEdit.name11" /></a>
            </div>
            <div id="demandGrid" class="mini-datagrid" allowResize="false" style="height:340px" autoload="true"
                 idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false"
                 multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
                 url="${ctxPath}/serviceEngineering/core/schematicDownloadApply/demandList.do?applyId=${applyId}">
                <div property="columns">
                    <div type="checkcolumn" width="50"></div>
                    <div type="indexcolumn" headerAlign="center" width="50"><spring:message code="page.schematicDownloadApplyEdit.name12" /></div>
                    <div field="materialCode" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.schematicDownloadApplyEdit.name13" /></div>
                    <div field="diagramCode" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.schematicDownloadApplyEdit.name14" /></div>
                    <div field="diagramType" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.schematicDownloadApplyEdit.name15" /></div>
                    <div field="repUserName" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.schematicDownloadApplyEdit.name16" /></div>
                    <div field="manualCode" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.schematicDownloadApplyEdit.name17" /></div>
                    <div field="manualDescription" width="300" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message code="page.schematicDownloadApplyEdit.name18" /></div>
                    <div field="manualLanguage" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.schematicDownloadApplyEdit.name19" /></div>
                    <div field="manualVersion" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.schematicDownloadApplyEdit.name20" /></div>
                    <div field="keyUser" displayField="keyUser" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.schematicDownloadApplyEdit.name21" /></div>
                    <div field="publishTime" width="120" headerAlign="center" align="center" dateFormat="yyyy-MM-dd" allowSort="true"><spring:message code="page.schematicDownloadApplyEdit.name22" /></div>
                    <div field="manualStatus" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.schematicDownloadApplyEdit.name23" /></div>
                    <div cellCls="actionIcons" width="110" headerAlign="center" align="center" renderer="manualFileRenderer" cellStyle="padding:0;">
                        <spring:message code="page.schematicDownloadApplyEdit.name26" />
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>
<%-----------------------------------------------------原理图选择页面-----------------------------------------------------%>
<div id="selectManualFileWindow" title="<spring:message code="page.schematicDownloadApplyEdit.name32" />" class="mini-window" style="width:1450px;height:700px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px" borderStyle="border-left:0;border-top:0;border-right:0;">
        <li style="margin-right: 15px">
            <span class="text" style="width:auto"><spring:message code="page.schematicDownloadApplyEdit.name13" />：</span>
            <input class="mini-textbox" id="materialCode" name="materialCode" style="width: 120px"/>
            <span class="text" style="width:auto"><spring:message code="page.schematicDownloadApplyEdit.name14" />：</span>
            <input class="mini-textbox" id="diagramCode" name="diagramCode" style="width: 120px"/>
            <span class="text" style="width:auto"><spring:message code="page.schematicDownloadApplyEdit.name15" />：</span>
            <input class="mini-textbox" id="diagramType" name="diagramType" style="width: 120px"/>
            <span class="text" style="width:auto"><spring:message code="page.schematicDownloadApplyEdit.name16" />：</span>
            <input class="mini-textbox" id="repUserName" name="repUserName" style="width: 120px"/>
            <span class="text" style="width:auto"><spring:message code="page.schematicDownloadApplyEdit.name17" />：</span>
            <input class="mini-textbox" id="manualCode" name="manualCode" style="width: 120px"/>
            <span class="text" style="width:auto"><spring:message code="page.schematicDownloadApplyEdit.name19" />：</span>
            <input class="mini-textbox" id="manualLanguage" name="manualLanguage" style="width: 120px"/>
            <br>
            <span class="text" style="width:auto">产品物料编码：</span>
            <input class="mini-textbox" id="productMaterialCode" name="productMaterialCode" style="width: 120px"/>
            <span class="text" style="width:auto">产品设计型号：</span>
            <input class="mini-textbox" id="productDesignModel" name="productDesignModel" style="width: 120px"/>
            <span class="text" style="width:auto">产品销售型号：</span>
            <input class="mini-textbox" id="productSaleModel" name="productSaleModel" style="width: 120px"/>
            <span class="text" style="width:auto">产品主管：</span>
            <input class="mini-textbox" id="productManager" name="productManager" style="width: 120px"/>
        </li>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchManualFile()"><spring:message code="page.schematicDownloadApplyEdit.name34" /></a>
        <span style="color: red;font-size: 15px"><spring:message code="page.schematicDownloadApplyEdit.name35" /></span>
    </div>
    <div class="mini-fit">
        <div id="manualFileListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" multiSelect="true"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/serviceEngineering/core/schematicDiagram/dataListQuery.do">
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="materialCode" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.schematicDownloadApplyEdit.name13" /></div>
                <div field="diagramCode" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.schematicDownloadApplyEdit.name14" /></div>
                <div field="diagramType" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.schematicDownloadApplyEdit.name15" /></div>
                <div field="repUserName" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.schematicDownloadApplyEdit.name16" /></div>
                <div field="manualCode" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.schematicDownloadApplyEdit.name17" /></div>
                <div field="manualDescription" width="300" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message code="page.schematicDownloadApplyEdit.name18" /></div>
                <div field="manualLanguage" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.schematicDownloadApplyEdit.name19" /></div>
                <div field="manualVersion" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.schematicDownloadApplyEdit.name20" /></div>
                <div field="keyUser" displayField="keyUser" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.schematicDownloadApplyEdit.name21" /></div>
                <div field="publishTime" width="120" headerAlign="center" align="center" dateFormat="yyyy-MM-dd" allowSort="true"><spring:message code="page.schematicDownloadApplyEdit.name22" /></div>
                <div field="manualStatus" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.schematicDownloadApplyEdit.name23" /></div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.schematicDownloadApplyEdit.name36" />" onclick="selectFileOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.schematicDownloadApplyEdit.name37" />" onclick="selectFileHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var nodeVarsStr = '${nodeVars}';
    var jsUseCtxPath = "${ctxPath}";
    var schematicDownloadForm = new mini.Form("#schematicDownloadForm");
    var fileListGrid = mini.get("fileListGrid");
    var demandGrid = mini.get("demandGrid");
    var manualFileListGrid = mini.get("manualFileListGrid");
    var selectManualFileWindow = mini.get("selectManualFileWindow");
    var action = "${action}";
    var status = "${status}";
    var applyId = "${applyId}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var stageName = "";

    //..
    $(function () {
        var url = jsUseCtxPath + "/serviceEngineering/core/schematicDownloadApply/getJson.do";
        $.post(
            url,
            {id: applyId},
            function (json) {
                schematicDownloadForm.setData(json);
            });
        if (action == 'detail') {
            schematicDownloadForm.setEnabled(false);
            mini.get("demandListToolBar").hide();
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        } else if (action == 'task') {
            taskActionProcess();
        }
    });
    //..
    function addManualFile() {
        //打开选择窗口，选择后插入本表中
        selectManualFileWindow.show();
        searchManualFile();
    }
    //..
    function selectFileOK() {
        var rows = manualFileListGrid.getSelecteds();
        if (rows.length > 0) {
            for (var index = 0; index < rows.length; index++) {
                var row = rows[index];
                if (row.keyUser) {
                    row.schematicFileId = row.id;
                    row.id = '';
                    demandGrid.addRow(row);
                }
            }
        }
        selectFileHide();
    }
    //..

    function selectFileHide() {
        mini.get('materialCode').setValue('');
        mini.get('diagramCode').setValue('');
        mini.get('diagramType').setValue('');
        mini.get('repUserName').setValue('');
        mini.get('manualCode').setValue('');
        mini.get('manualLanguage').setValue('');
        mini.get('productDesignModel').setValue('');
        mini.get('productSaleModel').setValue('');
        mini.get('productMaterialCode').setValue('');
        mini.get('productManager').setValue('');
        selectManualFileWindow.hide();
    }
    //..
    function delManualFile() {
        var selecteds = demandGrid.getSelecteds();
        demandGrid.removeRows(selecteds);
    }
    //..
    function getData() {
        var formData = _GetFormJsonMini("schematicDownloadForm");
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        if (formData.SUB_demandGrid) {
            delete formData.SUB_demandGrid;
        }
        if (demandGrid.getChanges().length > 0) {
            formData.changeDemandGrid = demandGrid.getChanges();
        }
        return formData;
    }
    //..保存草稿
    function saveDraft(e) {
        window.parent.saveDraft(e);
    }
    //..发起流程
    function startProcess(e) {
        var formValid = validApply();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }
    //..下一步审批
    function applyApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (stageName == 'start') {
            var formValid = validApply();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        //检查通过
        window.parent.approve();
    }
    //..暂存信息
    function saveInProcess() {
        var formData = getData();
        var json = mini.encode(formData);
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/schematicDownloadApply/saveInProcess.do',
            type: 'post',
            async: false,
            data: json,
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        window.location.reload();
                    } else {
                        mini.alert(schematicDownloadApplyEdit_name + data.message);
                    }
                }
            }
        });
    }
    //..
    function validApply() {
        var demandGridData = demandGrid.getData();
        if (demandGridData.length == 0) {
            return {"result": false, "message": schematicDownloadApplyEdit_name1};
        }
        return {"result": true};
    }
    //..
    function processInfo() {
        var instId = $("#instId").val();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: schematicDownloadApplyEdit_name2,
            width: 800,
            height: 600
        });
    }
    //..
    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();
        //获取环境变量
        if (!nodeVarsStr) {
            nodeVarsStr = "[]";
        }
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'stageName') {
                stageName = nodeVars[i].DEF_VAL_;
            }
        }
        if (stageName != 'start') {
            schematicDownloadForm.setEnabled(false);
            mini.get("demandListToolBar").hide();
        }

    }

    //..
    function searchManualFile() {
        var paramArray = [];
        paramArray.push({name: "materialCode", value: mini.get('materialCode').getValue()});
        paramArray.push({name: "diagramCode", value: mini.get('diagramCode').getValue()});
        paramArray.push({name: "diagramType", value: mini.get('diagramType').getValue()});
        paramArray.push({name: "repUserName", value: mini.get('repUserName').getValue()});
        paramArray.push({name: "manualCode", value: mini.get('manualCode').getValue()});
        paramArray.push({name: "manualLanguage", value: mini.get('manualLanguage').getValue()});
        paramArray.push({name: "designModel", value: mini.get('productDesignModel').getValue()});
        paramArray.push({name: "salesModel", value: mini.get('productSaleModel').getValue()});
        paramArray.push({name: "materialCodeProduct", value: mini.get('productMaterialCode').getValue()});
        paramArray.push({name: "productManager", value: mini.get('productManager').getValue()});
        paramArray.push({name: "manualStatus", value: "可转出"});
        var data = {};
        data.filter = mini.encode(paramArray);
        manualFileListGrid.load(data);
    }

    //..
    function manualFileRenderer(e) {
        var record = e.record;
        //需都可以预览，只有申请人在流程结束后，通过detail查看才能下载
        var cellHtml = '<span  title=' + schematicDownloadApplyEdit_name7 + ' style="color:#409EFF;cursor: pointer;" onclick="previewManualFile(\'' + record.schematicFileId + '\',\'' + coverContent + '\')">' + schematicDownloadApplyEdit_name7 + '</span>';
        var creatorId = mini.get("CREATE_BY_").getValue();
        if (action == 'detail' && status == 'SUCCESS_END' && currentUserId == creatorId) {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + schematicDownloadApplyEdit_name5 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downloadManualFile(\'' + record.schematicFileId + '\',\'' + record.manualDescription + '\')">' + schematicDownloadApplyEdit_name5 + '</span>';
        }

        return cellHtml;
    }
    //..
    function previewManualFile(id, coverContent) {
        var previewUrl = jsUseCtxPath + "/serviceEngineering/core/schematicDiagram/preview.do?id=" + id;
        window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverContent) + "&file=" + encodeURIComponent(previewUrl));
    }
    //..
    function downloadManualFile(id, description) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/schematicDiagram/download.do?id="+id);
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
    //..
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
</script>
</body>
</html>
