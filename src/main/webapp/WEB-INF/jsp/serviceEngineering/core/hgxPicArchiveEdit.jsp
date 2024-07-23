<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>图片库归档</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveStandard" class="mini-button" onclick="saveBusiness()"><spring:message code="page.hgxPicArchiveEdit.name" /></a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()"><spring:message code="page.hgxPicArchiveEdit.name1" /></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;padding: 0">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <caption style="font-weight: bold;font-size: 20px !important;height:60px;"><spring:message code="page.hgxPicArchiveEdit.name2" /></caption>
                <tr>

                    <td style="text-align: center;width: 20%"><spring:message code="page.hgxPicArchiveEdit.name3" />：</td>
                    <td style="min-width:170px">
                        <input id="picCode" name="picCode" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>

                    <td style="text-align: center;width: 15%"><spring:message code="page.hgxPicArchiveEdit.name4" />：</td>
                    <td colspan="3">
                        <input class="mini-textbox" style="width:70%;float: left" id="fileName" name="fileName"
                               enabled="false"/>
                        <%--<input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()" accept="application/pdf"/>--%>
                        <input id="inputFile"
                               style="display:none;"
                               type="file" onchange="getSelectFile()"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadFile"><spring:message code="page.hgxPicArchiveEdit.name5" /></a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clearUploadFile"><spring:message code="page.hgxPicArchiveEdit.name6" /></a>
                    </td>

                </tr>


                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.hgxPicArchiveEdit.name7" />：</td>
                    <td style="min-width:170px">
                        <input id="picType" name="picType" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="<spring:message code="page.hgxPicArchiveEdit.name8" />..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="<spring:message code="page.hgxPicArchiveEdit.name8" />..."
                               data="[{'key' : '安全类','value' : '安全类'}
                                    ,{'key' : '操作类','value' : '操作类'}
                                    ,{'key' : '维保类','value' : '维保类'}
                                    ,{'key' : '其他','value' : '其他'}]"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.hgxPicArchiveEdit.name9" />:</td>
                    <td>
                        <input id="creatorName" name="creatorName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>

                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.hgxPicArchiveEdit.name10" />：</td>
                    <td style="min-width:170px">
                        <input id="region" name="region" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="<spring:message code="page.hgxPicArchiveEdit.name8" />..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="<spring:message code="page.hgxPicArchiveEdit.name8" />..."
                               data="[{'key' : '履挖','value' : '履挖'}
                                    ,{'key' : '轮挖','value' : '轮挖'}
                                    ]"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.hgxPicArchiveEdit.name11" />:</td>
                    <td>
                        <input id="dataSrc" name="dataSrc" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="<spring:message code="page.hgxPicArchiveEdit.name8" />..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="<spring:message code="page.hgxPicArchiveEdit.name8" />..."
                               data="[{'key' : 'ISO标准','value' : 'ISO标准'}
                                    ,{'key' : '企业标准','value' : '企业标准'}
                                    ,{'key' : '集团图片库','value' : '集团图片库'}
                                    ]"/>
                    </td>
                </tr>

                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.hgxPicArchiveEdit.name12" />：</td>
                    <td style="min-width:170px">
                        <input id="applyInst" name="applyInst" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="<spring:message code="page.hgxPicArchiveEdit.name8" />..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="<spring:message code="page.hgxPicArchiveEdit.name8" />..."
                               data="[{'key' : '操保手册','value' : '操保手册'}
                                    ,{'key' : '装修手册','value' : '装修手册'}
                                    ]"/>
                    </td>
                    <%--<td style="text-align: center;width: 15%">版本状态:</td>--%>
                    <%--<td>--%>
                        <%--<input id="versionStatus" name="versionStatus" class="mini-textbox" style="width:98%;"--%>
                               <%--enabled="true"/>--%>
                    <%--</td>--%>
                </tr>

                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.hgxPicArchiveEdit.name13" /></td>

                        <%--<input id="remark" name="remark" class="mini-textbox" style="width:98%;height:300px" enabled="true"/>--%>
                    <td colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc" plugins="mini-textarea" style="width:99%;height:250px;line-height:25px;"
                                  label="<spring:message code="page.hgxPicArchiveEdit.name13" />" datatype="varchar" length="200" vtype="length:200" minlen="0" allowinput="true"
                                  emptytext="<spring:message code="page.hgxPicArchiveEdit.name14" />..." mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
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
        var formData = formBusiness.getData();
        // var checkResult = checkEditRequired(formData);
        // if (!checkResult) {
        //     return;
        // }
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
                            mini.alert(message, hgxPicArchiveEdit_name, function (action) {
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
            xhr.open('POST', jsUseCtxPath + '/serviceEngineering/core/picArchive/saveBusiness.do', false);
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
            mini.alert(hgxPicArchiveEdit_name1);
            return false;
        }
        if (!$.trim(formData.partsType)) {
            mini.alert(hgxPicArchiveEdit_name2);
            return false;
        }
        if (!$.trim(formData.partsModel)) {
            mini.alert(hgxPicArchiveEdit_name3);
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
        // 这里直接主动填picCode 无规则
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            var fileNameSuffix = fileList[0].name.split('.').pop();
            mini.get("fileName").setValue(fileList[0].name);
            // var code = fileList[0].name.split('.');
            // mini.get("picCode").setValue(code[0]);

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
