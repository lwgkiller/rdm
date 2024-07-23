<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>标准编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveStandard" class="mini-button" onclick="saveBusiness()"><spring:message code="page.maintenanceManualWeeklyReportEdit.name1" /></a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()"><spring:message code="page.maintenanceManualWeeklyReportEdit.name2" /></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 10%"><spring:message code="page.maintenanceManualWeeklyReportEdit.name3" />：</td>
                    <td style="width: 40%">
                        <input id="signYear" name="signYear"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="width: 10%"><spring:message code="page.maintenanceManualWeeklyReportEdit.name4" />：</td>
                    <td style="width: 40%">
                        <input id="signMonth" name="signMonth"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signMonth"
                               valueField="key" textField="value"/>
                    </td>
                </tr>

                <tr>
                    <td style="width: 10%"><spring:message code="page.maintenanceManualWeeklyReportEdit.name5" />:</td>
                    <td style="width: 40%">
						<textarea id="description" name="description" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                  datatype="varchar" allowinput="true" mwidth="80" wunit="%" mheight="200" hunit="px">
                        </textarea>
                    </td>
                    <td style="width: 10%"><spring:message code="page.maintenanceManualWeeklyReportEdit.name6" />：</td>
                    <td style="width: 40%">
                        <input id="releaseTime" name="releaseTime" class="mini-datepicker" format="yyyy-MM-dd"
                               showOkButton="true" showClearButton="false" style="width:98%" valueType="string"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%"><spring:message code="page.maintenanceManualWeeklyReportEdit.name7" />：</td>
                    <td colspan="3">
                        <input class="mini-textbox" style="width:70%;float: left" id="fileName" name="fileName" readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()" accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px" onclick="uploadFile"><spring:message code="page.maintenanceManualWeeklyReportEdit.name8" /></a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px" onclick="clearUploadFile"><spring:message code="page.maintenanceManualWeeklyReportEdit.name9" /></a>
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
    //..
    $(function () {
        formBusiness.setData(obj);
    });
    //..
    function saveBusiness() {
        var formData = formBusiness.getData();
        var checkResult = checkStandardEditRequired(formData);
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
                            mini.alert(message, maintenanceManualWeeklyReportEdit_name, function (action) {
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
            xhr.open('POST', jsUseCtxPath + '/serviceEngineering/core/maintenanceManualWeeklyReport/save.do', false);
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
    function checkStandardEditRequired(formData) {
        if (!formData) {
            mini.alert(maintenanceManualWeeklyReportEdit_name1);
            return false;
        }
        if (!$.trim(formData.signYear)) {
            mini.alert(maintenanceManualWeeklyReportEdit_name2);
            return false;
        }
        if (!$.trim(formData.signMonth)) {
            mini.alert(maintenanceManualWeeklyReportEdit_name3);
            return false;
        }
        if (!$.trim(formData.description)) {
            mini.alert(maintenanceManualWeeklyReportEdit_name4);
            return false;
        }
        if (!$.trim(formData.releaseTime)) {
            mini.alert(maintenanceManualWeeklyReportEdit_name5);
            return false;
        }
        return true;
    }
    //..
    function uploadFile() {
        $("#inputFile").click();
    }
    //..
    function getSelectFile() {
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            var fileNameSuffix = fileList[0].name.split('.').pop();
            if (fileNameSuffix == 'xlsx') {
                mini.get("fileName").setValue(fileList[0].name);
            }
            else {
                clearUploadFile();
                mini.alert(maintenanceManualWeeklyReportEdit_name6);
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
