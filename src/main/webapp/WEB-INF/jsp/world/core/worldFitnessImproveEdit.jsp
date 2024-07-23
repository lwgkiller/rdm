<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title>适应性改进</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/world/worldFitnessImporveEdit.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/embedsoft/cxUtil.js?version=${static_res_version}" type="text/javascript"></script>

</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo1()"><spring:message code="page.worldFitnessImproveEdit.lcxx" /></a>
        <a id="closeWindow1" class="mini-button" onclick="CloseWindow()"><spring:message code="page.worldFitnessImproveEdit.gb" /></a>
    </div>
</div>

<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="currentForm" method="post" style="height: 95%;width: 98%">
            <input class="mini-hidden" id="id" name="id"/>
            <input class="mini-hidden" id="CREATE_BY_" name="CREATE_BY_"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <p style="font-size: 24px;font-weight: bold;margin-top: 15px; text-align: center;margin-bottom: 20px;">
                <spring:message code="page.worldFitnessImproveEdit.fit1" /></p>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.worldFitnessImproveEdit.fit2" />：</td>
                    <td style="width: 36%;">
                        <input id="productModel" name="productModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.worldFitnessImproveEdit.fit3" />：</td>
                    <td style="min-width:170px">
                        <input id="CREATE_TIME_" name="CREATE_TIME_" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.worldFitnessImproveEdit.fit4" />：</td>
                    <td style="min-width:170px">
                        <input id="region" name="regionKey" property="editor" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="<spring:message code="page.worldFitnessImproveEdit.fit30" />..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="<spring:message code="page.worldFitnessImproveEdit.fit30" />..."
                               data="[
										   {'key' : 'Europe','value' : 'Europe'}
										   ,{'key' : 'America','value' : 'America'}
										   ,{'key' : 'Australia','value' : 'Australia'}
										   ,{'key' : 'India','value' : 'India'}
										   ,{'key' : 'Brazil','value' : 'Brazil'}
										   ,{'key' : 'General area','value' : 'General area'}]"
                        />
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.worldFitnessImproveEdit.fit5" />：</td>
                    <td style="min-width:170px">
                        <input id="creatorName" name="creatorName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.worldFitnessImproveEdit.fit6" />：</td>

                    <td style="min-width:170px">
                        <input id="improveType" name="improveType" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=SYXGJLX"
                               valueField="key" textField="value"/>
                    </td>

                    <td style="text-align: center;width: 20%"><spring:message code="page.worldFitnessImproveEdit.fit7" />：</td>
                    <td style="min-width:170px">
                        <input id="improveSource" name="improveSource" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=SYXGJLY"
                               valueField="key" textField="value"/></td>
                </tr>

                <tr>
                    <td style="text-align: center"><spring:message code="page.worldFitnessImproveEdit.fit8" />：<span style="color:red">*</span></td>
                    <td colspan="3">
						<textarea id="reqDesc" name="reqDesc" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;"
                                  label="reqDesc" datatype="varchar" length="2000" vtype="length:2000" minlen="0"
                                  allowinput="true"
                                  emptytext="<spring:message code="page.worldFitnessImproveEdit.fit31" />"
                                  mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.worldFitnessImproveEdit.fit9" />：</td>
                    <td>
                        <input id="reqDescUrl" name="reqDescUrl" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.worldFitnessImproveEdit.fit10" />：</td>
                    <td>
                        <a id="reqDescFile" class="mini-button" style="margin-bottom: 5px;margin-top: 5px;"
                           onclick="addPicFile('req',baseFileEdit)"><spring:message code="page.worldFitnessImproveEdit.fit11" /></a>
                    </td>
                </tr>

                <tr>
                    <td style="text-align: center"><spring:message code="page.worldFitnessImproveEdit.fit12" />：<span style="color:red">*</span></td>
                    <td colspan="3">
						<textarea id="competitorDesc" name="competitorDesgin" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;"
                                  label="competitorDesc" datatype="varchar" length="2000" vtype="length:2000" minlen="0"
                                  allowinput="true"
                                  emptytext="<spring:message code="page.worldFitnessImproveEdit.fit32" />"
                                  mwidth="80" wunit="%" mheight="150"
                                  hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.worldFitnessImproveEdit.fit13" />：</td>
                    <td>
                        <input id="competitorUrl" name="competitorDesginUrl" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.worldFitnessImproveEdit.fit14" />：</td>
                    <td>
                        <a id="competitorFile" class="mini-button" style="margin-bottom: 5px;margin-top: 5px;"
                           onclick="addPicFile('com',baseFileEdit)"><spring:message code="page.worldFitnessImproveEdit.fit11" /></a>
                    </td>

                </tr>


                <tr>
                    <td style="text-align: center"><spring:message code="page.worldFitnessImproveEdit.fit15" />：<span style="color:red">*</span></td>
                    <td colspan="3">
						<textarea id="improveDesc" name="improveDesc" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;"
                                  label="improveDesc" datatype="varchar" length="2000" vtype="length:2000" minlen="0"
                                  allowinput="true"
                                  emptytext="<spring:message code="page.worldFitnessImproveEdit.fit34" />"
                                  mwidth="80" wunit="%"
                                  mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.worldFitnessImproveEdit.fit16" />：</td>
                    <td>
                        <input id="improveDescUrl" name="improveDescUrl" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.worldFitnessImproveEdit.fit17" />：</td>
                    <td>
                        <a id="improveFile" class="mini-button" style="margin-bottom: 5px;margin-top: 5px;"
                           onclick="addPicFile('imp',baseFileEdit)"><spring:message code="page.worldFitnessImproveEdit.fit11" /></a>
                    </td>

                </tr>


                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.worldFitnessImproveEdit.fit18" />：</td>
                    <td id="sfgj">
                        <input id="hwjdJudge" name="hwjdJudge" class="mini-radiobuttonlist" style="width:100%;"
                               repeatItems="2" repeatLayout="table" repeatDirection="horizontal"
                               textfield="hwjdName" valuefield="hwjdJudge"
                               data="[ {'hwjdName' : 'Unknown issue','hwjdJudge' : 'yes'},{'hwjdName' : 'Known Issue','hwjdJudge' : 'no'}]"/>
                    </td>

                    <td style="text-align: center;width: 20%"><spring:message code="page.worldFitnessImproveEdit.fit19" />：<span style="color:red;">*</span></td>
                    <td>
                        <input id="productManagerId" name="productManager" textname="productManagerName"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;"
                               allowinput="false" label="项目负责人" length="50" maxlength="50" mainfield="no" single="true"
                               onvaluechanged="setRespDept()"/>
                    </td>

                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.worldFitnessImproveEdit.fit20" />：</td>
                    <td>
                        <input id="pmJudge" name="pmJudge" class="mini-radiobuttonlist" style="width:100%;"
                               repeatItems="2" repeatLayout="table" repeatDirection="horizontal"
                               textfield="pmJudgeName" valuefield="pmJudge" onvaluechanged="ifChange"
                               data="[ {'pmJudgeName' : 'Can Improve','pmJudge' : 'yes'},{'pmJudgeName' : 'Can Not Improve','pmJudge' : 'no'}]"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.worldFitnessImproveEdit.fit21" />：</td>
                    <td>
                        <input id="technical" name="technical" property="editor" class="mini-combobox"
                               style="width:98%;"
                               textField="value" valueField="key" emptyText="<spring:message code="page.worldFitnessImproveEdit.fit30" />..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="<spring:message code="page.worldFitnessImproveEdit.fit30" />..."
                               data="[{'key' : 'Yes','value' : 'Yes'} ,{'key' : 'No','value' : 'No'}]"
                        />
                    </td>


                </tr>
                <tr id="canNotImprove" style="display:none">
                    <td style="text-align: center"><spring:message code="page.worldFitnessImproveEdit.fit22" />：<span style="color:red">*</span></td>
                    <td colspan="3">
                        <textarea id="cantImproveDesc" name="noImproveDesc" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:250px;line-height:25px;"
                                  label="不改进原因" datatype="varchar" length="2000" vtype="length:2000" minlen="0"
                                  allowinput="true"
                                  emptytext="<spring:message code="page.worldFitnessImproveEdit.fit35" />..." mwidth="80" wunit="%" mheight="150"
                                  hunit="px"></textarea>

                    </td>
                </tr>
                <tr id="canImprove" style="display:none">
                    <td style="text-align: center"><spring:message code="page.worldFitnessImproveEdit.fit23" />：<span style="color:red">*</span></td>
                    <td colspan="3">
						<textarea id="improveProjcetDesc" name="improveProjcetDesc" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:250px;line-height:25px;"
                                  label="改进方案" datatype="varchar" length="2000" vtype="length:2000" minlen="0"
                                  allowinput="true"
                                  emptytext="<spring:message code="page.worldFitnessImproveEdit.fit36" />..." mwidth="80" wunit="%" mheight="150"
                                  hunit="px"></textarea>
                        <a id="improveProjcetDescFile" class="mini-button" style="margin-bottom: 5px;margin-top: 5px;"
                           onclick="addPicFile('pla',projectFileEdit)"><spring:message code="page.worldFitnessImproveEdit.fit11" /></a>
                    </td>

                </tr>
                <tr>
                    <td style="text-align: center"><spring:message code="page.worldFitnessImproveEdit.fit24" />：<span style="color:red">*</span></td>
                    <td style="width: 20%;">
                        <a id="projectConfrimFile" class="mini-button" style="margin-bottom: 5px;margin-top: 5px;"
                           onclick="addPicFile('con',projectVerifyEdit)"><spring:message code="page.worldFitnessImproveEdit.fit11" /></a>
                    </td>
                    <td style="text-align: center"><spring:message code="page.worldFitnessImproveEdit.fit25" />：<span style="color:red">*</span></td>
                    <td>
                        <input id="planActTime" name="planActTime" class="mini-datepicker" format="yyyy-MM-dd"
                               allowInput="false"
                               showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var nodeVarsStr = '${nodeVars}';
    var currentForm = new mini.Form("#currentForm");
    var id = mini.get("id");
    var action = "${action}";
    var status = "${status}";
    var applyId = "${applyId}";
    var instId = "${instId}";
    // 文件权限
    var baseFileEdit = "false";
    var projectFileEdit = "false";
    var projectVerifyEdit = "false";
    // 用户信息
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;

    function feedbackFileRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        var detailId = record.id;
        if (detailId) {
            cellHtml = '<span  title=' + worldFitnessImproveEdit_wjlb + ' style="color:#409EFF;cursor: pointer;" onclick="feedbackFileClick(\'' + detailId + '\',\'' + record.judge + '\')"><spring:message code="page.worldFitnessImproveEdit.fit26" /></span>';
        }
        return cellHtml;
    }

    function operationRenderer(e) {
        var record = e.record;
        //预览、下载和删除
        var cellHtml = returnPreviewSpan(record.fileName, record.id, record.baseInfoId, coverContent);
        var downloadUrl = '/embedsoft/core/confirmFunction/fileDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + worldFitnessImproveEdit_xz + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.baseInfoId + '\',\'' + downloadUrl + '\')">' + worldFitnessImproveEdit_xz + '</span>';
        if (action == 'task' && (GNQR_STATUS == '3' || GZQR_STATUS == '3')) {
            var deleteUrl = "/embedsoft/core/confirmFunction/deleteFiles.do";
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + worldFitnessImproveEdit_shac + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.baseInfoId + '\',\'' + deleteUrl + '\')">' + worldFitnessImproveEdit_shac + '</span>';
        }
        return cellHtml;
    }

    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        debugger;
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title=' + worldFitnessImproveEdit_yl + ' style="color: silver" ><spring:message code="page.worldFitnessImproveEdit.fit29" /></span>';
        } else {
            var url = '/embedsoft/core/confirmFunction/preview.do?fileType=' + fileType;
            s = '<span  title=' + worldFitnessImproveEdit_yl + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf6(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\',\'' + fileType + '\')"><spring:message code="page.worldFitnessImproveEdit.fit29" /></span>';
        }
        return s;
    }


    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }

</script>
</body>
</html>
