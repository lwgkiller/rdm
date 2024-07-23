<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>合同信息编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/htglEdit.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveContract" class="mini-button" style="display: none" onclick="saveContract()"><spring:message code="page.htglEdit.name"/></a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()"><spring:message code="page.htglEdit.name1"/></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="contractForm" method="post">
            <input class="mini-hidden" id="id" name="id"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <caption style="font-weight: bold;font-size: 20px !important;"><spring:message code="page.htglEdit.name2"/></caption>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.htglEdit.name3"/>：</td>
                    <td colspan="3">
                        <input id="contractNo" name="contractNo" class="mini-textbox" style="width:98%;" allowInput="false"/>
                    </td>
                <tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.htglEdit.name4"/>：</td>
                    <td colspan="3">
                        <input id="contractDesc" name="contractDesc" class="mini-textbox" style="width:98%;"/>
                    </td>
                <tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.htglEdit.name5"/>：</td>
                    <td style="min-width:170px">
                        <input id="signerUserId" name="signerUserId" textname="signerUserName"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                               label="<spring:message code="page.htglEdit.name5" />" length="50" mainfield="no" single="true"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.htglEdit.name6"/>：</td>
                    <td style="min-width:170px">
                        <input id="signerUserDepId" name="signerUserDepId" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:98%;height:34px" allowinput="false" label="<spring:message code="page.htglEdit.name6" />"
                               textname="signerUserDepName" length="500"
                               maxlength="500" minlen="0" single="true" required="false" initlogindep="false" showclose="false"
                               mwidth="160" wunit="px" mheight="34" hunit="px"/>
                    </td>
                <tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.htglEdit.name7"/>：</td>
                    <td style="min-width:170px">
                        <input id="signYear" name="signYear"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.htglEdit.name8"/>：</td>
                    <td style="min-width:170px">
                        <input id="signMonth" name="signMonth"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signMonth"
                               valueField="key" textField="value"/>
                    </td>
                <tr>
                <tr>
                    <td style="text-align: center;width: 20%">签订日期：</td>
                    <td style="min-width:170px">
                        <input id="signDate" name="signDate" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               valueType="string" showTime="false" showOkButton="false" showClearButton="true"
                               style="width:98%;height:34px;"/>
                    </td>
                    <td style="text-align: center;width: 20%">变更/解除情况：</td>
                    <td style="min-width:170px">
                        <input id="CAndTStatus" name="CAndTStatus"
                               class="mini-combobox" style="width:98%"
                               data="[{'key':'签订补充协议','value':'签订补充协议'},{'key':'合同解除','value':'合同解除'},{'key':'无','value':'无'}]"
                               valueField="key" textField="value"/>
                    </td>
                <tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.htglEdit.name9"/>：</td>
                    <td style="min-width:170px">
                        <input id="partA" name="partA" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.htglEdit.name10"/>：</td>
                    <td style="min-width:170px">
                        <input id="partB" name="partB" class="mini-textbox" style="width:98%;"/>
                    </td>
                <tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.htglEdit.name11"/>：</td>
                    <td style="min-width:170px">
                        <input id="partC" name="partC" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.htglEdit.name12"/>：</td>
                    <td style="min-width:170px">
                        <input id="partD" name="partD" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.htglEdit.name13"/>：</td>
                    <td style="min-width:170px">
                        <input id="isSingHonest" name="isSingHonest" class="mini-checkbox" style="width:98%;"
                               trueValue="1" falseValue="0"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.htglEdit.name14"/>：</td>
                    <td style="min-width:170px">
                        <input id="isRecord" name="isRecord" class="mini-checkbox" style="width:98%;"
                               trueValue="1" falseValue="0"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.htglEdit.name15"/>：</td>
                    <td style="min-width:170px">
                        <input id="isFile" name="isFile" class="mini-checkbox" style="width:98%;"
                               trueValue="1" falseValue="0"/>
                    </td>
                    <td style="text-align: center;width: 20%">是否作废：</td>
                    <td style="min-width:170px">
                        <input id="isDiscard" name="isDiscard" class="mini-checkbox" style="width:98%;"
                               trueValue="1" falseValue="0"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;height: 300px"><spring:message code="page.htglEdit.name16"/>：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="fileButtons" style="display: none">
                            <a id="uploadFile" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                               onclick="uploadContractFile"><spring:message code="page.htglEdit.name17"/></a>
                            <span style="color: red;">
                                1.签订日期填写后一周内，合同审批表、合同文本、廉洁承诺协议必须上传！2.立项审批表包括“招投标、询比价文件、特批表、会议纪要或其他审批表”等;
                            </span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                             idField="id" url="${ctxPath}/zhgl/core/contractfile/dataList.do?contractId=${contractId}"
                             multiSelect="false" showPager="true" showColumnsMenu="false" sizeList="[10,20,50,100]" pageSize="10"
                             allowAlternating="true" pagerButtons="#pagerButtons">
                            <div property="columns">
                                <div type="indexcolumn" headerAlign="center" align="center" width="20"><spring:message
                                        code="page.htglEdit.name18"/></div>
                                <div field="fileName" align="center" headerAlign="center" width="150"><spring:message
                                        code="page.htglEdit.name19"/></div>
                                <div field="fileType" align="center" headerAlign="center" width="150">文件类型</div>
                                <div field="fileSize" align="center" headerAlign="center" width="60"><spring:message
                                        code="page.htglEdit.name20"/></div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100"><spring:message
                                        code="page.htglEdit.name21"/></div>
                                <div field="action" width="80" headerAlign='center' align="center" renderer="operationRenderer"><spring:message
                                        code="page.htglEdit.name22"/></div>
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

    var jsUseCtxPath = "${ctxPath}";
    var contractForm = new mini.Form("#contractForm");
    var fileListGrid = mini.get("fileListGrid");
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;

    var contractId = "${contractId}";
    var action = "${action}";
    var currentUserMainGroupId = "${currentUserMainGroupId}";
    var currentUserMainGroupName = "${currentUserMainGroupName}";
    var currentUserId = "${currentUserId}";


    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnContractPreviewSpan(record.fileName, record.id, record.contractId);
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + htglEdit_name + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadContractFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.contractId + '\')">' + htglEdit_name + '</span>';
        //增加删除按钮
        if (record.CREATE_BY_ == currentUserId && action != 'detail') {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + htglEdit_name1 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteContractFile(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + htglEdit_name1 + '</span>';
        }
        return cellHtml;
    }

</script>
</body>
</html>
