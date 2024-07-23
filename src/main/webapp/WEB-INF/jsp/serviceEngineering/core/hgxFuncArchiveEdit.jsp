<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>功能模块库归档</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveStandard" class="mini-button" onclick="saveBusiness()"><spring:message code="page.hgxFuncArchiveEdit.name" /></a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()"><spring:message code="page.hgxFuncArchiveEdit.name1" /></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;padding: 0">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <caption style="font-weight: bold;font-size: 20px !important;"><spring:message code="page.hgxFuncArchiveEdit.name2" /></caption>
                <tr>
                    <%--<td style="text-align: center;width: 20%">零部件类型：<span style="color:red">*</span></td>--%>

                    <td style="text-align: center;width: 20%"><spring:message code="page.hgxFuncArchiveEdit.name3" />：</td>
                    <td style="min-width:170px">
                        <input id="funcCode" name="funcCode" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>

                    <td style="text-align: center;width: 15%"><spring:message code="page.hgxFuncArchiveEdit.name4" />：</td>
                    <td colspan="2">
                        <input class="mini-textbox" style="width:75%;float: left" id="fileName" name="fileName"
                               enabled="false"/>
                        <%--<input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()" accept="application/pdf"/>--%>
                        <input id="inputFile"
                               style="display:none;"
                               type="file" onchange="getSelectFile()"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadFile"><spring:message code="page.hgxFuncArchiveEdit.name5" /></a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clearUploadFile"><spring:message code="page.hgxFuncArchiveEdit.name6" /></a>
                    </td>

                </tr>


                <tr>

                    <td style="text-align: center;width: 15%"><spring:message code="page.hgxFuncArchiveEdit.name7" />:</td>
                    <td>
                        <input id="materialCode" name="materialCode" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.hgxFuncArchiveEdit.name8" />:</td>
                    <td>
                        <input id="creatorName" name="creatorName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>
                <tr>

                    <td style="text-align: center;width: 15%"><spring:message code="page.hgxFuncArchiveEdit.name9" />:</td>
                    <td>
                        <input id="configDesc" name="configDesc" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.hgxFuncArchiveEdit.name10" />:</td>
                    <td>
                        <input id="gssVersion" name="gssVersion" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                </tr>

                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.hgxFuncArchiveEdit.name11" />：</td>
                    <%--<td style="min-width:170px">--%>
                        <%--<input id="region" name="region" class="mini-combobox" style="width:98%;"--%>
                               <%--textField="value" valueField="key" emptyText="请选择..."--%>
                               <%--required="false" allowInput="false" showNullItem="true"--%>
                               <%--nullItemText="请选择..."--%>
                               <%--data="[{'key' : '销售型号','value' : '销售型号'}--%>
                                    <%--,{'key' : '设计型号','value' : '设计型号'}--%>
                                    <%--,{'key' : '物料编码','value' : '物料编码'}--%>
                                    <%--]"/>--%>
                    <%--</td>--%>
                    <td>
                        <input id="fitSalesModel" name="fitSalesModel" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.hgxFuncArchiveEdit.name12" />：</td>
                    <td>
                        <input id="fitDesignModel" name="fitDesignModel" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>

                </tr>

                <tr>

                    <td style="text-align: center;width: 15%"><spring:message code="page.hgxFuncArchiveEdit.name13" />:</td>
                    <td>
                        <input id="fitCode" name="fitCode" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>

                </tr>



                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.hgxFuncArchiveEdit.name14" /></td>
                    <td colspan="3">
                        <input id="remark" name="remark" class="mini-textbox" style="width:98%;" enabled="true"/>
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
    var businessId = "${businessId}";
    //..
    $(function () {

        formBusiness.setData(obj);
        if (action == 'detail') {
            formBusiness.setEnabled(false);
            mini.get("saveStandard").setEnabled(false);
            mini.get("uploadFileBtn").setEnabled(false);
            mini.get("clearFileBtn").setEnabled(false);
        }
    });

    //..
    function saveBusiness() {
        if (action = "copy") {
            mini.get("id").setValue("");
        }
        var formData = formBusiness.getData();

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
                            mini.alert(message, hgxFuncArchiveEdit_name, function (action) {
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
            xhr.open('POST', jsUseCtxPath + '/serviceEngineering/core/funcArchive/saveBusiness.do', false);
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
            mini.alert(hgxFuncArchiveEdit_name1);
            return false;
        }
        if (!$.trim(formData.partsType)) {
            mini.alert(hgxFuncArchiveEdit_name2);
            return false;
        }
        if (!$.trim(formData.partsModel)) {
            mini.alert(hgxFuncArchiveEdit_name3);
            return false;
        }
        // if (!$.trim(formData.fileName)) {
        //     mini.alert('文件不能为空！');
        //     return false;
        // }


        return true;
    }

    //..
    function uploadFile() {
        $("#inputFile").click();
    }

    //..
    function getSelectFile() {
        debugger;
        //todo 上传的文件名格式验证需要是xxx-xxx.xxx
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            var fileNameSuffix = fileList[0].name.split('.').pop();
            mini.get("fileName").setValue(fileList[0].name);
            // if (fileNameSuffix == 'pdf') {
            //     mini.get("fileName").setValue(fileList[0].name);
            // }
            // else {
            //     clearUploadFile();
            //     mini.alert('请上传pdf文件！');
            // }
        }
    }

    //..
    function clearUploadFile() {
        $("#inputFile").val('');
        mini.get("fileName").setValue('');
    }

    // function remarkRenderer(e) {
    //     if (menuType == "gzk") {
    //         return '故障现象';
    //     }
    //     else if (menuType == "gjk") {
    //         return '工具类型';
    //     }
    //     return '123';
    // }
</script>
</body>
</html>
