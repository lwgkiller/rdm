<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>报告证书修改</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
</head>
<body>
<div id="ToolBar" class="topToolBar" style="display: block">
    <div>
        <a id="save" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/add.png"
           onclick="saveData()">保存</a>
        <a id="closeWindow" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/system_close.gif"
           onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container">
        <form id="infoForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
                <caption>
                    报告证书修改
                </caption>
                <tbody>
                <tr class="firstRow displayTr">
                    <td align="center"></td>
                    <td align="left"></td>
                    <td align="center"></td>
                    <td align="left"></td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        销售型号：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="productModel"   class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        产品主管：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="productManager" name="productManagerId" textname="productManager"
                               property="editor" class="mini-user rxc" plugins="mini-user" required="true"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        文件类型：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="reportType" name="reportType" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="文件类型："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="text" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=BGLX"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        文件编号：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="reportCode"  class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        文件日期：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="reportDate" name="reportDate" class="mini-datepicker" allowInput="false"
                                style="width:100%;height:34px">
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        有效期：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="reportValidity" name="reportValidity" class="mini-datepicker" allowInput="false"
                                style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        文件名称：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="reportName" name="reportName" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="文件类型："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="text" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=WWRZ-WJMC"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        状态：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="valid" name="valid" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="文件类型："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=WWRZ-ZSZT"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        设计型号：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="designModel"   class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr >
                    <td align="center" style="width: 14%">附件信息：</td>
                    <td colspan="3">
                        <input class="mini-textbox" style="width:70%;float: left" id="fileName" name="fileName" readonly />
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()" accept="application/pdf" />
                        <a id="uploadFileBtn" class="mini-button"  style="float: left;margin-left: 10px" onclick="uploadFile">选择文件</a>
                        <a id="clearFileBtn" class="mini-button"  style="float: left;margin-left: 10px" onclick="clearUploadFile">清除</a>
                    </td>
                </tr>
                </tbody>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var applyObj = ${applyObj};
    var infoForm = new mini.Form("#infoForm");
    infoForm.setData(applyObj);
    //清空文件
    function clearUploadFile() {
        $("#inputFile").val('');
        mini.get("fileName").setValue('');
    }
    //触发文件选择
    function uploadFile() {
        var id = mini.get('id').getValue();
        if(!id){
            mini.alert("先保存后再上传附件！");
            return;
        }
        $("#inputFile").click();
    }

    //文件类型判断及文件名显示
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


    function saveData() {
        infoForm.validate();
        if (!infoForm.isValid()) {
            return;
        }
        //如果附件信息有改动则保存附件信息
        var fileName =  mini.get("fileName").getValue();
        if(fileName&&fileName!=applyObj.fileName){
            saveAttachFile()
        }

        var formData = infoForm.getData();
        var config = {
            url: jsUseCtxPath + "/wwrz/core/file/save.do",
            method: 'POST',
            data: formData,
            success: function (result) {
                //如果存在自定义的函数，则回调
                var result = mini.decode(result);
                if (result.success) {
                    CloseWindow('ok');
                } else {
                }
                ;
            }
        }
        _SubmitJson(config);
    }
    function saveAttachFile() {
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
                            mini.alert(message);
                        }
                    }
                }
            };

            // 开始上传
            xhr.open('POST', jsUseCtxPath + '/wwrz/core/file/dealReportFile.do?fileId='+applyObj.id, false);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            var fd = new FormData();
            fd.append('reportFile', file);
            xhr.send(fd);
        }
    }
</script>
</body>
</html>
