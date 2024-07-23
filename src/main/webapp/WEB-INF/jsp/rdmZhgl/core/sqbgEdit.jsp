<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>售前文件变更申请</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()"><spring:message
                code="page.sqbgEdit.name"/></a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.sqbgEdit.name1"/></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%">
        <form id="formSqbg" method="post">
            <input id="sqbgId" name="sqbgId" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="applyType" name="applyType" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold;font-size: 20px !important;">
                    <spring:message code="page.sqbgEdit.name2"/>
                </caption>
                <tr>
                    <td style="text-align: center;width: 7%"><spring:message code="page.sqbgEdit.name3"/></td>
                    <td>
                        <input id="saleId" name="saleId" textname="saleId" style="width:98%;" property="editor"
                               class="mini-buttonedit" showClose="true"
                               oncloseclick="onSelectSaleFileCloseClick" allowInput="false"
                               onbuttonclick="selectSaleFile()"/>
                    </td>
                    <td style="text-align: center;width: 7%"><spring:message code="page.sqbgEdit.name4"/></td>
                    <td>
                        <input id="fileType" name="fileType" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="<spring:message code="page.sqbgEdit.name4" />："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_"
                               emptyText="<spring:message code="page.sqbgEdit.name5" />..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=saleFile_WJFL"
                               nullitemtext="<spring:message code="page.sqbgEdit.name5" />..."
                               emptytext="<spring:message code="page.sqbgEdit.name5" />..."/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.sqbgEdit.name6"/></td>
                    <td colspan="1">
                        <input id="designModel" name="designModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.sqbgEdit.name7"/></td>
                    <td colspan="1">
                        <input id="saleModel" name="saleModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        <spring:message code="page.sqbgEdit.name10"/>
                    </td>
                    <td>
                        <input id="director" name="director" textname="directorName" enabled="true"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;"
                               allowinput="true"
                               label="<spring:message code="page.sqbgEdit.name10" />" length="50" mainfield="no"
                               single="true"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.sqbgEdit.name9"/></td>
                    <td colspan="1">
                        <input id="language" name="language" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="<spring:message code="page.sqbgEdit.name9" />："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_"
                               emptyText="<spring:message code="page.sqbgEdit.name5" />..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YZ"
                               nullitemtext="<spring:message code="page.sqbgEdit.name5" />..."
                               emptytext="<spring:message code="page.sqbgEdit.name5" />..."/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.sqbgEdit.name8"/></td>
                    <td colspan="1">
                        <input id="newVersion" name="newVersion" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width:15%;height: 300px"><spring:message
                            code="page.sqbgEdit.name12"/></td>
                    <td colspan="3">
                        <div style="margin-top: 5px;margin-bottom: 2px">
                            <a id="addDetail" class="mini-button" onclick="addDetail()"><spring:message
                                    code="page.sqbgEdit.name13"/></a>
                            <a id="removeDetail" class="mini-button" onclick="removeDetail()"><spring:message
                                    code="page.sqbgEdit.name14"/></a>
                            <span style="color: red"><spring:message code="page.sqbgEdit.name15"/></span>
                        </div>
                        <div id="detailListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                             allowResize="false" allowCellWrap="true"
                             idField="id" url="${ctxPath}/Sqbg/getDetailList.do?belongId=${sqbgId}" autoload="true"
                             allowCellEdit="true" allowCellSelect="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="checkcolumn" width="10"></div>
                                <div field="beforec" width="40" headerAlign="center" align="center"><spring:message
                                        code="page.sqbgEdit.name16"/>
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="afterc" width="40" headerAlign="center" align="center"><spring:message
                                        code="page.sqbgEdit.name17"/>
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;height: 300px"><spring:message code="page.sqbgEdit.name18"/></td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="addAppendixFile()"><spring:message
                                    code="page.sqbgEdit.name19"/></a>
                            <span style="color: red"><spring:message code="page.sqbgEdit.name20"/></span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                             allowResize="false"
                             idField="id" url="${ctxPath}/Sqbg/getSqbgFileList.do?belongId=${sqbgId}" autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20"><spring:message
                                        code="page.sqbgEdit.name21"/></div>
                                <div field="fileName" align="center" headerAlign="center" width="150"><spring:message
                                        code="page.sqbgEdit.name22"/></div>
                                <div field="fileSize" align="center" headerAlign="center" width="60"><spring:message
                                        code="page.sqbgEdit.name23"/></div>
                                <div field="action" width="100" headerAlign='center' align="center"
                                     renderer="operationRendererSq"><spring:message code="page.sqbgEdit.name24"/></div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<div id="selectZlWindow" title="<spring:message code="page.sqbgEdit.name25" />" class="mini-window"
     style="width:950px;height:450px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777"><spring:message code="page.sqbgEdit.name3"/>: </span>
        <input class="mini-textbox" width="130" id="changeApplyNo" style="margin-right: 15px"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchSaleFile()"><spring:message
                code="page.sqbgEdit.name26"/></a>
    </div>
    <div class="mini-fit">
        <div id="zlListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             onrowdblclick="onRowDblClick"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/rdmZhgl/core/saleFile/queryList.do?">
            <div property="columns">
                <div type="checkcolumn" width="25px"></div>
                <div field="id" sortField="id" width="100" headerAlign="center" align="center" allowSort="true">
                    <spring:message code="page.sqbgEdit.name3"/></div>
                <div field="designModel" width="50" headerAlign="center" align="center" allowSort="false">
                    <spring:message code="page.sqbgEdit.name6"/></div>
                <div field="saleModel" width="50" headerAlign="center" align="center" allowSort="false"><spring:message
                        code="page.sqbgEdit.name7"/></div>
                <div field="fileType" width="70" headerAlign="center" align="center" allowSort="false"
                     renderer="onWSwitchType"><spring:message code="page.sqbgEdit.name27"/></div>
                <div field="directorName" width="50" headerAlign="center" align="center" allowSort="false">
                    <spring:message code="page.sqbgEdit.name10"/></div>
                <div field="language" width="50" headerAlign="center" align="center" allowSort="false"><spring:message
                        code="page.sqbgEdit.name9"/></div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px"
                           value="<spring:message code="page.sqbgEdit.name28" />" onclick="selectStandardOK()"/>
                    <input type="button" style="height: 25px;width: 70px"
                           value="<spring:message code="page.sqbgEdit.name29" />" onclick="selectStandardHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var nodeVarsStr = '${nodeVars}';
    var zlListGrid = mini.get("zlListGrid");
    var action = "${action}";
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var selectZlWindow = mini.get("selectZlWindow");
    var currentTime = "${currentTime}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var detailListGrid = mini.get("detailListGrid");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var fileListGrid = mini.get("fileListGrid");
    var formSqbg = new mini.Form("#formSqbg");
    var sqbgId = "${sqbgId}";
    var applyType = "${applyType}";
    var typeList = getDics("saleFile_WJFL");
    var userRole = "${userRole}";
    mini.get("applyType").setValue(applyType);

    var first = "";

    $(function () {
        if (sqbgId) {
            var url = jsUseCtxPath + "/Sqbg/getSqbgDetail.do";
            $.post(
                url,
                {sqbgId: sqbgId},
                function (json) {
                    formSqbg.setData(json);
                });
        }
        if (action == 'task') {
            taskActionProcess();
        } else if (action == "detail") {
            formSqbg.setEnabled(false);
            mini.get("removeDetail").setEnabled(false);
            mini.get("addDetail").setEnabled(false);
            mini.get("addFile").setEnabled(false);
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        }
        mini.get("designModel").setEnabled(false);
        mini.get("saleModel").setEnabled(false);
        mini.get("fileType").setEnabled(false);
        mini.get("language").setEnabled(false);
    });

    function addDetail() {
        // var formId = mini.get("sqbgId").getValue();
        // if (!formId) {
        //     mini.alert('请先点击‘保存’进行表单的保存！');
        //     return;
        // } else {
        var row = {};
        detailListGrid.addRow(row);
    }

    function onWSwitchType(e) {
        var record = e.record;
        var fileType = record.fileType;
        var typeText = '';
        for (var i = 0; i < typeList.length; i++) {
            if (typeList[i].key_ == fileType) {
                typeText = typeList[i].text;
                break
            }
        }
        return typeText;
    }

    function removeDetail() {
        var selecteds = detailListGrid.getSelecteds();
        var deleteArr = [];
        for (var i = 0; i < selecteds.length; i++) {
            var row = selecteds[i];
            deleteArr.push(row);
        }
        detailListGrid.removeRows(deleteArr);
    }

    function getData() {
        var formData = _GetFormJsonMini("formSqbg");
        formData.detail = detailListGrid.getChanges();
        //此处用于向后台产生流程实例时替换标题中的参数时使用
        // formData.bos=[];
        // formData.vars=[{key:'companyName',val:formData.companyName}];
        return formData;
    }

    function saveSqbg(e) {
        var saleId = $.trim(mini.get("saleId").getText());
        if (!saleId) {
            mini.alert(sqbgEdit_name);
            return;
        }
        var version = $.trim(mini.get("newVersion").getValue());
        if (!version) {
            mini.alert(sqbgEdit_name1);
            return;
        }
        window.parent.saveDraft(e);
    }

    function startSqbgProcess(e) {
        var formValid = validSqbgFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }

    function validSqbgFirst() {
        var saleId = $.trim(mini.get("saleId").getText());
        if (!saleId) {
            return {"result": false, "message": sqbgEdit_name};
        }
        var version = $.trim(mini.get("newVersion").getValue());
        if (!version) {
            return {"result": false, "message": sqbgEdit_name1};
        }
        var director = $.trim(mini.get("director").getValue());
        if (!director) {
            return {"result": false, "message": saleFileApplyEdit_name12};
        }
        var fileArr = fileListGrid.getData();
        if (fileArr.length == 0) {
            return {"result": false, "message": sqbgEdit_name3};
        }
        return {"result": true};
    }

    function sqbgApprove() {
        //编制阶段的下一步需要校验表单必填字段

        var formValid = validSqbgFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        //检查通过
        window.parent.approve();
    }


    function processInfo() {
        var instId = $("#instId").val();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: sqbgEdit_name4,
            width: 800,
            height: 600
        });
    }


    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();

        //获取环境变量
        if (!nodeVarsStr) {
            nodeVarsStr = "[]";
        }
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'first') {
                first = nodeVars[i].DEF_VAL_;
            }
        }
        mini.get("designModel").setEnabled(false);
        mini.get("saleModel").setEnabled(false);
        mini.get("fileType").setEnabled(false);
        mini.get("language").setEnabled(false);
        if (first != 'yes') {
            formSqbg.setEnabled(false);
            mini.get("removeDetail").setEnabled(false);
            mini.get("addDetail").setEnabled(false);
            mini.get("addFile").setEnabled(false);
        }
    }

    function getPreHandleResult(handleResult, handleResultText) {
        var arr = [{'key': 'BACK', 'value': handleResultText, 'css': 'red'},
            {'key': 'BACK_TO_STARTOR', 'value': handleResultText, 'css': 'red'},
            {'key': 'AGREE', 'value': handleResultText, 'css': 'green'}
        ];
        return $.formatItemValue(arr, handleResult);
    }

    function selectSaleFile() {
        selectZlWindow.show();
        searchSaleFile();
    }

    //查询要变更的申请单
    function searchSaleFile() {
        var queryParam = [];
        //其他筛选条件
        var changeApplyNo = $.trim(mini.get("changeApplyNo").getValue());
        if (changeApplyNo) {
            queryParam.push({name: "applyId", value: changeApplyNo});
        }
        queryParam.push({name: "applyType", value: applyType});
        queryParam.push({name: "instStatus", value: "SUCCESS_END"});
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = zlListGrid.getPageIndex();
        data.pageSize = zlListGrid.getPageSize();
        data.sortField = zlListGrid.getSortField();
        data.sortOrder = zlListGrid.getSortOrder();
        //查询
        zlListGrid.load(data);
    }

    function onRowDblClick() {
        selectStandardOK();
    }

    function selectStandardOK() {
        var selectRow = zlListGrid.getSelected();
        if (!selectRow) {
            mini.alert(saleFileApplyList_name4);
            return;
        }
        //检查文件类型，【产品亮点介绍、市场推广文件、产品样品】，仅允许由两个营销公司下面的人员提交，其他类型的仅允许挖机研究院的人员提交
        if ((selectRow.fileType == 'cpyb' || selectRow.fileType == 'cpldjs' || selectRow.fileType == 'sctgwj')
            && userRole != 'gnyx' && userRole != 'hwyx') {
            mini.alert(saleFileApplyEdit_name4);
            return;
        }
        if ((selectRow.fileType == 'jcjszl' || selectRow.fileType == 'jsggs' || selectRow.fileType == 'qsmzqcbqd'
            || selectRow.fileType == 'sst'|| selectRow.fileType == 'blt'|| selectRow.fileType == 'qdsyt'
            || selectRow.fileType == 'qdnlb') && userRole != 'yjy') {
            mini.alert(saleFileApplyEdit_name5);
            return;
        }

        mini.get("saleId").setValue(selectRow.id);
        mini.get("saleId").setText(selectRow.id);
        mini.get("designModel").setValue(selectRow.designModel);
        mini.get("saleModel").setValue(selectRow.saleModel);
        mini.get("fileType").setValue(selectRow.fileType);
        mini.get("director").setValue(selectRow.director);
        mini.get("director").setText(selectRow.directorName);
        mini.get("language").setValue(selectRow.language);
        selectStandardHide();
    }


    function selectStandardHide() {
        selectZlWindow.hide();
        mini.get("changeApplyNo").setValue('');
    }

    function onSelectSaleFileCloseClick(e) {
        mini.get("saleId").setValue('');
        mini.get("saleId").setText('');
        mini.get("designModel").setValue('');
        mini.get("saleModel").setValue('');
        mini.get("fileType").setText('');
        mini.get("director").setValue('');
        mini.get("director").setText('');
        mini.get("language").setValue('');
    }

    function operationRendererSq(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnSqbgPreviewSpan(record.fileName, record.fileId, record.belongId, coverContent);
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + sqbgEdit_name5 + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadSqbgFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\')">' + sqbgEdit_name5 + '</span>';
        //增加删除按钮
        if (action != 'detail' && record.CREATE_BY_ == currentUserId) {
            var deleteUrl = "/Sqbg/deleteSqbgFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + sqbgEdit_name6 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteSqbgFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\',\'' + deleteUrl + '\')">' + sqbgEdit_name6 + '</span>';
        }
        return cellHtml;
    }

    function addAppendixFile() {
        var sqbgId = mini.get("sqbgId").getValue();
        if (!sqbgId) {
            mini.alert(sqbgEdit_name7);
            return;
        }
        mini.open({
            title: sqbgEdit_name8,
            url: jsUseCtxPath + "/Sqbg/openUploadWindow.do?sqbgId=" + sqbgId,
            width: 850,
            height: 550,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (fileListGrid) {
                    fileListGrid.load();
                }
            }
        });
    }

    function returnSqbgPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title=' + sqbgEdit_name9 + ' style="color: silver" >' + sqbgEdit_name9 + '</span>';
        } else if (fileType == 'pdf') {
            var url = '/Sqbg/sqbgPdfPreview';
            s = '<span  title=' + sqbgEdit_name9 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + sqbgEdit_name9 + '</span>';
        } else if (fileType == 'office') {
            var url = '/Sqbg/sqbgOfficePreview';
            s = '<span  title=' + sqbgEdit_name9 + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + sqbgEdit_name9 + '</span>';
        } else if (fileType == 'pic') {
            var url = '/Sqbg/sqbgImagePreview';
            s = '<span  title=' + sqbgEdit_name9 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + sqbgEdit_name9 + '</span>';
        }
        return s;
    }

    function downLoadSqbgFile(fileName, fileId, formId) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + '/Sqbg/sqbgPdfPreview.do?action=download');
        var inputFileName = $("<input>");
        inputFileName.attr("type", "hidden");
        inputFileName.attr("name", "fileName");
        inputFileName.attr("value", fileName);
        var inputstandardId = $("<input>");
        inputstandardId.attr("type", "hidden");
        inputstandardId.attr("name", "formId");
        inputstandardId.attr("value", formId);
        var inputFileId = $("<input>");
        inputFileId.attr("type", "hidden");
        inputFileId.attr("name", "fileId");
        inputFileId.attr("value", fileId);
        $("body").append(form);
        form.append(inputFileName);
        form.append(inputstandardId);
        form.append(inputFileId);
        form.submit();
        form.remove();
    }


    function deleteSqbgFile(fileName, fileId, formId, urlValue) {
        mini.confirm(sqbgEdit_name10, sqbgEdit_name11,
            function (action) {
                if (action == "ok") {
                    var url = jsUseCtxPath + urlValue;
                    var data = {
                        fileName: fileName,
                        id: fileId,
                        formId: formId
                    };
                    $.ajax({
                        url: url,
                        method: 'post',
                        contentType: 'application/json',
                        data: mini.encode(data),
                        success: function (json) {
                            if (fileListGrid) {
                                fileListGrid.load();
                            }
                        }
                    });
                }
            }
        );
    }


</script>
</body>
</html>
