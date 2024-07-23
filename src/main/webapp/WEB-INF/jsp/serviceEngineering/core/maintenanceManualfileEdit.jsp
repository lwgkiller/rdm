<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>操保手册归档</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveStandard" class="mini-button" onclick="saveBusiness()"><spring:message code="page.maintenanceManualfileEdit.name"/></a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()"><spring:message code="page.maintenanceManualfileEdit.name1"/></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;padding: 0">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="keyUserId" name="keyUserId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <caption style="font-weight: bold;font-size: 20px !important;"><spring:message code="page.maintenanceManualfileEdit.name2"/></caption>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualfileEdit.name3"/>:</td>
                    <td style="min-width:170px">
                        <input id="salesModel" name="salesModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualfileEdit.name4"/>:</td>
                    <td style="min-width:170px">
                        <input id="designModel" name="designModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualfileEdit.name5"/>:</td>
                    <td style="min-width:170px">
                        <input id="materialCode" name="materialCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualfileEdit.name6"/>:</td>
                    <td style="min-width:170px">
                        <input id="manualDescription" name="manualDescription" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualfileEdit.name7"/>:</td>
                    <td style="min-width:170px">
                        <input id="manualLanguage" name="manualLanguage" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringLanguage"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualfileEdit.name8"/>:</td>
                    <td style="min-width:170px">
                        <input id="manualCode" name="manualCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualfileEdit.name9"/>:</td>
                    <td style="min-width:170px">
                        <input id="manualVersion" name="manualVersion" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualfileEdit.name10"/>:</td>
                    <td style="min-width:170px">
                        <input id="manualEdition" name="manualEdition" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringMaintenanceManualEdition"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualfileEdit.name11"/>:</td>
                    <td style="min-width:170px">
                        <input id="isCE" name="isCE" class="mini-combobox" style="width:120px;"
                               textField="value" valueField="key"
                               data="[{'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"
                        />
                    </td>
                    <td style="text-align: center;width: 15%">EC自声明状态:</td>
                    <td style="min-width:170px">
                        <input id="CEStatus" name="CEStatus" class="mini-combobox" style="width:120px;"
                               textField="value" valueField="key" showNullItem="true"
                               data="[{'key' : '已具备','value' : '已具备'},{'key' : '待更新','value' : '待更新'}]"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualfileEdit.name12"/>:</td>
                    <td>
                        <input id="cpzgId" name="cpzgId" showclose="true" textname="cpzgName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                               length="50" maxlength="50" mainfield="no" single="true"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualfileEdit.name13"/>:</td>
                    <td>
                        <input id="keyUser" name="keyUser" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>

                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualfileEdit.name14"/>:</td>
                    <td>
                        <input id="publishTime" name="publishTime" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualfileEdit.name15"/>:</td>
                    <td>
                        <input id="manualStatus" name="manualStatus" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualfileEdit.name16"/>:</td>
                    <td colspan="3">
                        <input id="remark" name="remark" class="mini-textbox" style="width:99.1%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualfileEdit.name17"/>：</td>
                    <td colspan="3">
                        <input class="mini-textbox" style="width:80%;float: left" id="fileName" name="fileName" readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()" accept="application/pdf"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px" onclick="uploadFile"><spring:message
                                code="page.maintenanceManualfileEdit.name18"/></a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px" onclick="clearUploadFile"><spring:message
                                code="page.maintenanceManualfileEdit.name19"/></a>
                    </td>
                </tr>
                <%--///////////////////////////////////////////////////--%>
                <tr>
                    <td style="text-align: center;height: 500px"><spring:message code="page.maintenanceManualfileEdit.name20"/>：</td>
                    <td colspan="3">
                        <div id="itemListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false" allowCellWrap="true"
                             idField="id" url="${ctxPath}/serviceEngineering/core/maintenanceManualfile/getLogList.do?businessId=${businessId}"
                             multiSelect="false" showPager="false" showColumnsMenu="false" allowcelledit="true" allowcellselect="true"
                             allowAlternating="true">
                            <div property="columns">
                                <div type="checkcolumn" width="20"></div>
                                <div type="indexcolumn" headerAlign="center" align="center" width="40"><spring:message
                                        code="page.maintenanceManualfileEdit.name21"/></div>
                                <div field="demandListNo" align="center" headerAlign="center" width="80" renderer="render"><spring:message
                                        code="page.maintenanceManualfileEdit.name22"/></div>
                                <div field="salesArea" align="center" headerAlign="center" width="80" renderer="render"><spring:message
                                        code="page.maintenanceManualfileEdit.name23"/></div>
                                <div field="quantity" align="center" headerAlign="center" width="80" renderer="render"><spring:message
                                        code="page.maintenanceManualfileEdit.name24"/></div>
                                <div field="applyTime" align="center" headerAlign="center" width="80" renderer="render"><spring:message
                                        code="page.maintenanceManualfileEdit.name25"/></div>
                                <div field="publishTime" align="center" headerAlign="center" width="80" renderer="render"><spring:message
                                        code="page.maintenanceManualfileEdit.name26"/></div>
                                <div field="applyUser" align="center" headerAlign="center" width="80" renderer="render"><spring:message
                                        code="page.maintenanceManualfileEdit.name27"/></div>
                                <div field="applyDep" align="center" headerAlign="center" width="80" renderer="render"><spring:message
                                        code="page.maintenanceManualfileEdit.name28"/></div>
                                <div field="Bconfirming" align="center" headerAlign="center" width="80" renderer="render"><spring:message
                                        code="page.maintenanceManualfileEdit.name29"/></div>
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
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var obj =${obj};
    var formBusiness = new mini.Form("#formBusiness");
    var itemListGrid = mini.get("itemListGrid");
    var businessId = "${businessId}";
    var currentUserId = "${currentUserId}"
    var currentUserNo = "${currentUserNo}";
    var maintenanceManualAdmin = "${maintenanceManualAdmin}";
    //..
    $(function () {
        formBusiness.setData(obj);
        if (action == 'detail') {
            formBusiness.setEnabled(false);
            mini.get("saveStandard").setEnabled(false);
            mini.get("uploadFileBtn").setEnabled(false);
            mini.get("clearFileBtn").setEnabled(false);
        } else if (action == "copy") {
            mini.get("id").setValue("");
            mini.get("manualVersion").setValue("");
            mini.get("keyUserId").setValue("");
            mini.get("keyUser").setValue("");
            mini.get("publishTime").setValue("");
            mini.get("manualDescription").setValue("");
            mini.get("manualStatus").setValue("编辑中");
        }
        debugger;
        if (currentUserId == "1" || currentUserNo == maintenanceManualAdmin) {
            mini.get("manualStatus").setEnabled(true);
        }
        itemListGrid.load();
    });
    //..
    function saveBusiness() {
        debugger;
        var formData = formBusiness.getData();
        var checkResult = checkEditRequired(formData);
        if (!checkResult) {
            return;
        }
        var file = null;
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            file = fileList[0];
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
                            mini.alert(message, maintenanceManualfileEdit_name, function (action) {
                                if (returnObj.success) {
                                    mini.get("id").setValue(returnObj.id);
                                }
                                CloseWindow();
                            });
                        }
                    }
                }
            };

            //开始上传
            xhr.open('POST', jsUseCtxPath + '/serviceEngineering/core/maintenanceManualfile/saveBusiness.do', false);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            var fd = new FormData();
            if (formData) {
                for (key in formData) {
                    fd.append(key, formData[key]);
                }
            }
            fd.append('businessFile', file);
            xhr.send(fd);
        }
    }
    //..
    function checkEditRequired(formData) {
        if (!formData) {
            mini.alert(maintenanceManualfileEdit_name1);
            return false;
        }
        if (!$.trim(formData.salesModel)) {
            mini.alert(maintenanceManualfileEdit_name2);
            return false;
        }
        if (!$.trim(formData.designModel)) {
            mini.alert(maintenanceManualfileEdit_name3);
            return false;
        }
        if (!$.trim(formData.materialCode)) {
            mini.alert(maintenanceManualfileEdit_name4);
            return false;
        }
        if (!$.trim(formData.manualLanguage)) {
            mini.alert(maintenanceManualfileEdit_name5);
            return false;
        }
        if (!$.trim(formData.manualCode)) {
            mini.alert(maintenanceManualfileEdit_name6);
            return false;
        }
        if (!$.trim(formData.manualEdition)) {
            mini.alert(maintenanceManualfileEdit_name7);
            return false;
        }
        if (!$.trim(formData.isCE)) {
            mini.alert(maintenanceManualfileEdit_name8);
            return false;
        }
        if (!$.trim(formData.cpzgId)) {
            mini.alert(maintenanceManualfileEdit_name9);
            return false;
        }
        if (action == "copy") {
            if (!$.trim(formData.manualVersion)) {
                mini.alert(maintenanceManualfileEdit_name10);
                return false;
            }
        }
        return true;
    }
    //..
    function uploadFile() {
        $("#inputFile").click();
    }
    //..
    function getSelectFile() {
        debugger;
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            var fileNameSuffix = fileList[0].name.split('.').pop();
            if (fileNameSuffix == 'pdf') {
                mini.get("fileName").setValue(fileList[0].name);
            }
            else {
                clearUploadFile();
                mini.alert(maintenanceManualfileEdit_name11);
            }
        }
    }
    //..
    function clearUploadFile() {
        $("#inputFile").val('');
        mini.get("fileName").setValue('');
    }
</script>
</body>
</html>
