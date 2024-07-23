<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title>海外新开发配套实验申请单</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()"><spring:message code="page.ptsyEdit.processInfo" /></a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.ptsyEdit.closeWindow" /></a>
    </div>
</div>

<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="ptsyForm" method="post" style="height: 95%;width: 100%">
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="ptsyId" name="ptsyId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold;font-size: 20px !important;">
                    <spring:message code="page.ptsyEdit.osea" />
                </caption>
                <tr>
                    <td style="text-align: center;width: 25%"><spring:message code="page.ptsyEdit.ao" /></td>
                    <td style="width: 25%;min-width:170px">
                        <input id="dept" name="deptId" textname="deptName"
                               property="editor" class="mini-dep rxc" plugins="mini-dep"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                    <td style="text-align: center;width: 25%"><spring:message code="page.ptsyEdit.applicant" />：</td>
                    <td style="width: 25%;min-width:170px">
                        <input id="apply" name="applyId" textname="applyName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.ptsyEdit.stagas" />：</td>
                    <td>
                        <input id="CREATE_TIME_" name="CREATE_TIME_" readonly="readonly" class="mini-datepicker"
                               format="yyyy-MM-dd"
                               style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.ptsyEdit.suma" />：</td>
                    <td style="min-width:170px">
                        <input id="company" name="company" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.ptsyEdit.manu" />：</td>
                    <td style="min-width:170px">
                        <input id="adress" name="adress" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.ptsyEdit.conp" />：</td>
                    <td>
                        <input id="link" name="link" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.ptsyEdit.appli" />：</td>
                    <td>
                        <input id="syType" name="syType" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.ptsyEdit.name" />：</td>
                    <td>
                        <input id="ptName" name="ptName" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.ptsyEdit.time" />：</td>
                    <td>
                        <input style="width:98%;" class="mini-datepicker" id="timeNode" name="timeNode"
                               showTime="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.ptsyEdit.reason" />：</td>
                    <td colspan="3">
                        <input id="sqReason" name="sqReason" class="mini-textbox" style="width:100%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%;height: 250px "><spring:message code="page.ptsyEdit.attach" />：
                        <br><spring:message code="page.ptsyEdit.manufacture" />
                        <br><spring:message code="page.ptsyEdit.mate" />
                        <br><spring:message code="page.ptsyEdit.capa" />
                        <br><spring:message code="page.ptsyEdit.types" />
                        <br><spring:message code="page.ptsyEdit.distribution" />
                        <br><spring:message code="page.ptsyEdit.whether" />
                    </td>
                    <td colspan="3">
                        <div style="margin-top: 2px;margin-bottom: 2px">
                            <a id="addtjFile" class="mini-button" onclick="tjfjupload()"><spring:message code="page.ptsyEdit.addatta" /></a>
                        </div>
                        <div id="tjFileListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                             allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/rdm/core/ptsy/getPtsyFileList.do?fileType=tj&belongId=${ptsyId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="15"><spring:message code="page.ptsyEdit.index" /></div>
                                <div field="fileName" width="140" headerAlign="center" align="center"><spring:message code="page.ptsyEdit.fileName" /></div>
                                <div field="fileSize" width="80" headerAlign="center" align="center"><spring:message code="page.ptsyEdit.fileSize" /></div>
                                <div field="action" width="100" headerAlign='center' align="center"
                                     renderer="operationRendererF"><spring:message code="page.ptsyEdit.action" />
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 25%"><spring:message code="page.ptsyEdit.comments" />：</td>
                    <td style="width: 25%;min-width:170px">
                        <input id="gjsyy" name="gjsyy" class="mini-radiobuttonlist" style="width:100%;"
                               repeatItems="2" repeatLayout="table" repeatDirection="horizontal"
                               textfield="jgxsName" valuefield="jgxsId"
                               data="[ {'jgxsName' : 'objection','jgxsId' : 'yes'},{'jgxsName' : 'No objection','jgxsId' : 'no'}]"/>
                    </td>
                    <td style="text-align: center;width: 25%"><spring:message code="page.ptsyEdit.product" />：</td>
                    <td style="width: 25%;min-width:170px">
                        <input id="cpzg" name="cpzgId" textname="cpzgName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.ptsyEdit.reasons" />：</td>
                    <td colspan="3">
                        <input id="gjsReason" name="gjsReason" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.ptsyEdit.commentsproduct" />：</td>
                    <td colspan="3">
                        <input id="cpzgyy" name="cpzgyy" class="mini-radiobuttonlist" style="width:100%;"
                               repeatItems="2" repeatLayout="table" repeatDirection="horizontal"
                               textfield="jgxsName" valuefield="jgxsId"
                               data="[ {'jgxsName' : 'objection','jgxsId' : 'yes'},{'jgxsName' : 'No objection','jgxsId' : 'no'}]"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.ptsyEdit.whethertrial" />：</td>
                    <td colspan="3">
                        <input id="ifsz" name="ifsz" class="mini-radiobuttonlist" style="width:100%;"
                               repeatItems="2" repeatLayout="table" repeatDirection="horizontal"
                               textfield="jgxsName" valuefield="jgxsId"
                               data="[ {'jgxsName' : 'yes','jgxsId' : 'yes'},{'jgxsName' : 'no','jgxsId' : 'no'}]"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%;height: 250px "><spring:message code="page.ptsyEdit.sample" />：</td>
                    <td colspan="3">
                        <div style="margin-top: 2px;margin-bottom: 2px">
                            <a id="addjyFile" class="mini-button" onclick="jyfjupload()"><spring:message code="page.ptsyEdit.addatta" /></a>
                        </div>
                        <div id="jyFileListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                             allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/rdm/core/ptsy/getPtsyFileList.do?fileType=jy&belongId=${ptsyId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="15"><spring:message code="page.ptsyEdit.index" /></div>
                                <div field="fileName" width="140" headerAlign="center" align="center"><spring:message code="page.ptsyEdit.fileName" /></div>
                                <div field="fileSize" width="80" headerAlign="center" align="center"><spring:message code="page.ptsyEdit.fileSize" /></div>
                                <div field="action" width="100" headerAlign='center' align="center"
                                     renderer="operationRendererF"><spring:message code="page.ptsyEdit.action" />
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%;height: 250px "><spring:message code="page.ptsyEdit.technical" />：</td>
                    <td colspan="3">
                        <div style="margin-top: 2px;margin-bottom: 2px">
                            <a id="addxyFile" class="mini-button" onclick="xyfjupload()"><spring:message code="page.ptsyEdit.addatta" /></a>
                        </div>
                        <div id="xyFileListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                             allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/rdm/core/ptsy/getPtsyFileList.do?fileType=xy&belongId=${ptsyId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="15"><spring:message code="page.ptsyEdit.serialNumber" /></div>
                                <div field="fileName" width="140" headerAlign="center" align="center"><spring:message code="page.ptsyEdit.fileName" /></div>
                                <div field="fileSize" width="80" headerAlign="center" align="center"><spring:message code="page.ptsyEdit.fileSize" /></div>
                                <div field="action" width="100" headerAlign='center' align="center"
                                     renderer="operationRendererF"><spring:message code="page.ptsyEdit.action" />
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var nodeVarsStr = '${nodeVars}';
    var jsUseCtxPath = "${ctxPath}";
    var ptsyForm = new mini.Form("#ptsyForm");
    var ptsyId = "${ptsyId}";
    var action = "${action}";
    var status = "${status}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var deptId = "${deptId}";
    var deptName = "${deptName}";
    var tjFileListGrid = mini.get("tjFileListGrid");
    var jyFileListGrid = mini.get("jyFileListGrid");
    var xyFileListGrid = mini.get("xyFileListGrid");
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;


    var first = "";
    var second = "";
    var third = "";
    var forth = "";
    var fifth = "";
    $(function () {
        if (ptsyId) {
            var url = jsUseCtxPath + "/rdm/core/ptsy/getPtsy.do";
            $.post(
                url,
                {ptsyId: ptsyId},
                function (json) {
                    ptsyForm.setData(json);
                });
        } else {
            mini.get("apply").setValue(currentUserId);
            mini.get("apply").setText(currentUserName);
            mini.get("dept").setValue(deptId);
            mini.get("dept").setText(deptName);
        }
        mini.get("cpzg").setEnabled(false);
        mini.get("cpzgyy").setEnabled(false);
        mini.get("gjsyy").setEnabled(false);
        mini.get("gjsReason").setEnabled(false);
        mini.get("ifsz").setEnabled(false);
        mini.get("addxyFile").setEnabled(false);
        mini.get("addjyFile").setEnabled(false);
        //变更入口
        if (action == 'task') {
            taskActionProcess();
        } else if (action == 'detail') {
            ptsyForm.setEnabled(false);
            mini.get("addtjFile").setEnabled(false);
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        }
    });

    function getData() {
        var formData = _GetFormJsonMini("ptsyForm");
        //此处用于向后台产生流程实例时替换标题中的参数时使用
        // formData.bos=[];
        // formData.vars=[{key:'companyName',val:formData.companyName}];
        return formData;
    }
    function validZero() {
        var timeNode = $.trim(mini.get("timeNode").getValue());
        if (!timeNode) {
            return {"result": false, "message": ptsyEdit_please};
        }
        return {"result": true};
    }
    function savePtsy(e) {
        var formValid = validZero ();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.saveDraft(e);
    }

    function validFirst() {
        var dept = $.trim(mini.get("dept").getValue());
        if (!dept) {
            return {"result": false, "message": ptsyEdit_1};
        }
        var apply = $.trim(mini.get("apply").getValue());
        if (!apply) {
            return {"result": false, "message": ptsyEdit_2};
        }
        var CREATE_TIME_ = $.trim(mini.get("CREATE_TIME_").getValue());
        if (!CREATE_TIME_) {
            return {"result": false, "message": ptsyEdit_3};
        }
        var company = $.trim(mini.get("company").getValue());
        if (!company) {
            return {"result": false, "message": ptsyEdit_4};
        }
        var adress = $.trim(mini.get("adress").getValue());
        if (!adress) {
            return {"result": false, "message": ptsyEdit_5};
        }
        var link = $.trim(mini.get("link").getValue());
        if (!link) {
            return {"result": false, "message": ptsyEdit_6};
        }
        var syType = $.trim(mini.get("syType").getValue());
        if (!syType) {
            return {"result": false, "message": ptsyEdit_7};
        }
        var ptName = $.trim(mini.get("ptName").getValue());
        if (!ptName) {
            return {"result": false, "message": ptsyEdit_8};
        }
        var sqReason = $.trim(mini.get("sqReason").getValue());
        if (!sqReason) {
            return {"result": false, "message": ptsyEdit_9};
        }
        var timeNode = $.trim(mini.get("timeNode").getValue());
        if (!timeNode) {
            return {"result": false, "message": ptsyEdit_91};
        }
        var tjFileList = tjFileListGrid.getData();
        if (tjFileList.length < 1) {
            return {"result": false, "message": ptsyEdit_10};
        }
        return {"result": true};
    }

    function validSecond() {
        var gjsyy = $.trim(mini.get("gjsyy").getValue());
        if (!gjsyy) {
            return {"result": false, "message": ptsyEdit_11};
        }
        if (gjsyy == 'no') {
            var cpzg = $.trim(mini.get("cpzg").getValue());
            if (!cpzg) {
                return {"result": false, "message": ptsyEdit_12};
            }
        }
        if (gjsyy == 'yes') {
            var gjsReason = $.trim(mini.get("gjsReason").getValue());
            if (!gjsReason) {
                return {"result": false, "message": ptsyEdit_13};
            }
        }
        return {"result": true};
    }

    function validThird() {
        var cpzgyy = $.trim(mini.get("cpzgyy").getValue());
        if (!cpzgyy) {
            return {"result": false, "message": ptsyEdit_14};
        }
        return {"result": true};
    }

    function validForth() {
        var ifsz = $.trim(mini.get("ifsz").getValue());
        if (!ifsz) {
            return {"result": false, "message": ptsyEdit_15};
        }
        var jyFileList = jyFileListGrid.getData();
        if (jyFileList.length < 1) {
            return {"result": false, "message": ptsyEdit_16};
        }
        return {"result": true};
    }

    function validFifth() {
        var ifsz = $.trim(mini.get("ifsz").getValue());
        if (ifsz == "yes") {
            var xyFileList = xyFileListGrid.getData();
            if (xyFileList.length < 1) {
                return {"result": false, "message": ptsyEdit_17};
            }
        }
        return {"result": true};
    }

    function startPtsyProcess(e) {
        var formValid = validFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }

    function ptsyApprove() {
        if (first == 'yes') {
            var formValid = validFirst();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (second == 'yes') {
            var formValid = validSecond();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (third == 'yes') {
            var formValid = validThird();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (forth == 'yes') {
            var formValid = validForth();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (fifth == 'yes') {
            var formValid = validFifth();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        window.parent.approve();
    }

    function processInfo() {
        var instId = $("#instId").val();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: ptsyEdit_18,
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
            if (nodeVars[i].KEY_ == 'second') {
                second = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'third') {
                third = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'forth') {
                forth = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'fifth') {
                fifth = nodeVars[i].DEF_VAL_;
            }
        }
        ptsyForm.setEnabled(false);
        mini.get("addtjFile").setEnabled(false);
        mini.get("addjyFile").setEnabled(false);
        mini.get("addxyFile").setEnabled(false);
        if (first == 'yes') {
            ptsyForm.setEnabled(true);
            mini.get("addtjFile").setEnabled(true);
            mini.get("cpzgyy").setEnabled(false);
            mini.get("gjsyy").setEnabled(false);
            mini.get("gjsReason").setEnabled(false);
        }
        if (second == 'yes') {
            mini.get("gjsyy").setEnabled(true);
            mini.get("gjsReason").setEnabled(true);
            mini.get("cpzg").setEnabled(true);
        }
        if (third == 'yes') {
            mini.get("cpzgyy").setEnabled(true);
        }
        if (forth == 'yes') {
            mini.get("ifsz").setEnabled(true);
            mini.get("addjyFile").setEnabled(true);
        }
        if (fifth == 'yes') {
            mini.get("addxyFile").setEnabled(true);
        }
    }

    function operationRendererF(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnPtsyPreviewSpan(record.fileName, record.fileId, record.belongId, coverContent);
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + ptsyEdit_19 + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadPtsyFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\')"><spring:message code="page.ptsyEdit.download" /></span>';
        //增加删除按钮
        if (action != "detail" && record.CREATE_BY_ == currentUserId) {
            var deleteUrl = "/rdm/core/ptsy/deletePtsyFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + ptsyEdit_20 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deletePtsyFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\',\'' + deleteUrl + '\')"><spring:message code="page.ptsyEdit.delete" /></span>';
        }
        return cellHtml;
    }

    function tjfjupload() {
        var ptsyId = mini.get("ptsyId").getValue();
        if (!ptsyId) {
            mini.alert(ptsyEdit_21);
            return;
        }
        mini.open({
            title: ptsyEdit_22,
            url: jsUseCtxPath + "/rdm/core/ptsy/openUploadWindow.do?fileType=tj&ptsyId=" + ptsyId,
            width: 850,
            height: 550,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (tjFileListGrid) {
                    tjFileListGrid.load();
                }
            }
        });
    }

    function jyfjupload() {
        var ptsyId = mini.get("ptsyId").getValue();
        if (!ptsyId) {
            ptsyId.alert(ptsyEdit_21);
            return;
        }
        mini.open({
            title: ptsyEdit_22,
            url: jsUseCtxPath + "/rdm/core/ptsy/openUploadWindow.do?fileType=jy&ptsyId=" + ptsyId,
            width: 850,
            height: 550,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (jyFileListGrid) {
                    jyFileListGrid.load();
                }
            }
        });
    }

    function xyfjupload() {
        var ptsyId = mini.get("ptsyId").getValue();
        if (!ptsyId) {
            ptsyId.alert(ptsyEdit_21);
            return;
        }
        mini.open({
            title: ptsyEdit_22,
            url: jsUseCtxPath + "/rdm/core/ptsy/openUploadWindow.do?fileType=xy&ptsyId=" + ptsyId,
            width: 850,
            height: 550,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (xyFileListGrid) {
                    xyFileListGrid.load();
                }
            }
        });
    }

    function returnPtsyPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title=' + ptsyEdit_23 + ' style="color: silver" ><spring:message code="page.ptsyEdit.preview" /></span>';
        } else if (fileType == 'pdf') {
            var url = '/rdm/core/ptsy/ptsyPdfPreview';
            s = '<span  title=' + ptsyEdit_23 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')"><spring:message code="page.ptsyEdit.preview" /></span>';
        } else if (fileType == 'office') {
            var url = '/rdm/core/ptsy/ptsyOfficePreview';
            s = '<span  title=' + ptsyEdit_23 + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')"><spring:message code="page.ptsyEdit.preview" /></span>';
        } else if (fileType == 'pic') {
            var url = '/rdm/core/ptsy/ptsyImagePreview';
            s = '<span  title=' + ptsyEdit_23 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')"><spring:message code="page.ptsyEdit.preview" /></span>';
        }
        return s;
    }

    function downLoadPtsyFile(fileName, fileId, formId) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + '/rdm/core/ptsy/ptsyPdfPreview.do?action=download');
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


    function deletePtsyFile(fileName, fileId, formId, urlValue) {
        mini.confirm(ptsyEdit_24, ptsyEdit_25,
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
                            if (tjFileListGrid) {
                                tjFileListGrid.load();
                            }
                            if (jyFileListGrid) {
                                jyFileListGrid.load();
                            }
                            if (xyFileListGrid) {
                                xyFileListGrid.load();
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
