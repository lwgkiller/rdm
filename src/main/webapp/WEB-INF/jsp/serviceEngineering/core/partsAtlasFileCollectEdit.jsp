<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>零件图册归档</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveStandard" class="mini-button" onclick="saveBusiness()">保存</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;padding: 0">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="keyUserId" name="keyUserId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <caption style="font-weight: bold;font-size: 20px !important;">零件图册归档信息采集</caption>
                <tr>
                    <td style="text-align: center;width: 15%">销售型号:</td>
                    <td style="min-width:170px">
                        <input id="salesModel" name="salesModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">设计型号:</td>
                    <td style="min-width:170px">
                        <input id="designModel" name="designModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">整机编号:</td>
                    <td style="min-width:170px">
                        <input id="vinCode" name="vinCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">图册语言:</td>
                    <td style="min-width:170px">
                        <input id="languageType" name="languageType" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringLanguage"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">归档人:</td>
                    <td >
                        <input id="creatorName" name="creatorName" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">图册文档：</td>
                    <td colspan="3">
                        <input class="mini-textbox" style="width:80%;float: left" id="fileName" name="fileName" enabled="false"/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()" accept="application/pdf"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px" onclick="uploadFile">选择文件</a>
                        <%--<a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px" onclick="clearUploadFile">清除</a>--%>
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
    //..
    $(function () {
        formBusiness.setData(obj);
        if (action == 'detail') {
            formBusiness.setEnabled(false);
            mini.get("saveStandard").setEnabled(false);
            mini.get("uploadFileBtn").setEnabled(false);
            mini.get("clearFileBtn").setEnabled(false);
        }
        itemListGrid.load();
    });
    //..
    function saveBusiness() {
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
                            mini.alert(message, '提示信息', function (action) {
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
            xhr.open('POST', jsUseCtxPath + '/serviceEngineering/core/patsAtlasFileCollection/saveBusiness.do', false);
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
            mini.alert('请填写必填项！');
            return false;
        }
        if (!$.trim(formData.salesModel)) {
            mini.alert('销售型号不能为空！');
            return false;
        }
        if (!$.trim(formData.designModel)) {
            mini.alert('设计型号不能为空！');
            return false;
        }
        if (!$.trim(formData.vinCode)) {
            mini.alert('整机编号不能为空！');
            return false;
        }
        if (!$.trim(formData.languageType)) {
            mini.alert('手册语言不能为空！');
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
            if (fileNameSuffix == 'pdf') {
                mini.get("fileName").setValue(fileList[0].name);
            }
            else {
                clearUploadFile();
                mini.alert('请上传pdf文件！');
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
