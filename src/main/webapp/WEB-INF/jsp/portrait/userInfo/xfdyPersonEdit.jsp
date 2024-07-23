<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>新增</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <style>
        html, body {
            overflow: hidden;
            height: 100%;
            width: 100%;
            padding: 0;
            margin: 0;
        }

    </style>
</head>
<body>
<div id="detailToolBar" class="topToolBar">
    <div>
        <a id="save" name="save" class="mini-button" style="display: none" onclick="save()">保存</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 100%;">
        <form id="formXfdy" method="post">
            <input id="xfdyId" name="xfdyId" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 35%">姓名：</td>
                    <td style="width: 65%">
                        <input id="apply" name="applyId" textname="applyName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
                               onvaluechanged="setRespDept()"/>
                    </td>
                    <%--<td rowspan="2" id="addTd" style="text-align: center;width: 35%;display: none">--%>
                        <%--<a id="addFile"  class="mini-button" onclick="addPhoto()">添加照片</a>--%>
                    <%--</td>--%>
                    <%--<td rowspan="2" id="imgTd" style="text-align: center;width: 35%;display: none">--%>
                        <%--<img id="img" name="img" class="item-image" src="${ctxPath}/person/core/downloadShowFile.do?imgId=${imgId}"--%>
                        <%--/>--%>
                    <%--</td>--%>
                </tr>
                <tr>
                    <td style="text-align: center;width: 35%">部门：</td>
                    <td style="width: 65%">
                        <input id="dept" name="deptId" textname="deptName"
                               property="editor" class="mini-dep rxc" plugins="mini-dep"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 35%">简介：</td>
                    <td colspan="5" style="width: 65%;min-width:170px;font-size:14pt;height: 250px">
                        <input id="info" name="info" class="mini-textarea" style="width:98%;height: 250px"/>
                    </td>
                </tr>
                <tr id="photoFile">
                    <td style="text-align: center;width: 35%">党员照片：</td>
                    <td colspan="3">
                        <input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName" enabled="false"/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()" accept="image/*"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px" onclick="addPhoto()">选择文件</a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px" onclick="clearUploadFile()">清除</a>
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
    var xfdyId = "${xfdyId}";
    var formXfdy = new mini.Form("#formXfdy");
    var currentUserName = "${currentUserName}";
    var currentUserId = "${currentUserId}";
    var currentTime = "${currentTime}";



    $(function () {
        if(xfdyId) {
            var url = jsUseCtxPath + "/person/core/getXfdyDetail.do";
            $.post(
                url,
                {xfdyId: xfdyId},
                function (json) {
                    formXfdy.setData(json);
                });
        }
        if (action == 'detail') {
            formXfdy.setEnabled(false);
            $("#photoFile").hide();
            mini.get("uploadFileBtn").setEnabled(false);
            mini.get("clearFileBtn").setEnabled(false);
        }
        if (action == 'edit'||action == 'add') {
            $("#save").show();
        }
    });

    function setRespDept() {
        var userId=mini.get("apply").getValue();
        if(!userId) {
            mini.get("dept").setValue('');
            mini.get("dept").setText('');
            return;
        }
        $.ajax({
            url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/' + userId + '/getUserInfoById.do',
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                mini.get("dept").setValue(data.mainDepId);
                mini.get("dept").setText(data.mainDepName);
            }
        });
    }

    function save() {
        var formData = formXfdy.getData();
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
                            mini.alert(message, '提示信息', function (action) {
                                if (returnObj.success) {
                                    mini.get("xfdyId").setValue(returnObj.xfdyId);
                                }
                                CloseWindow();
                            });
                        }
                    }
                }
            };

            //开始上传
            xhr.open('POST', jsUseCtxPath + '/person/core/saveBusiness.do', false);
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

    function checkStandardEditRequired(formData) {
        if (!$.trim(formData.applyName)) {
            mini.alert('请填写姓名！');
            return false;
        }
        if (!$.trim(formData.deptName)) {
            mini.alert('请填写部门！');
            return false;
        }
        if (!$.trim(formData.info)) {
            mini.alert('请填写简介！');
            return false;
        }
        if (!$.trim(formData.fileName)) {
            mini.alert('请上传照片！');
            return false;
        }
        return true;
    }
    //..
    function addPhoto() {
        $("#inputFile").click();
    }
    //..
    function getSelectFile() {
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            var fileNameSuffix = fileList[0].name.split('.').pop();
            if (fileNameSuffix == 'jpg') {
                mini.get("fileName").setValue(fileList[0].name);
            }else if (fileNameSuffix == 'png') {
                mini.get("fileName").setValue(fileList[0].name);
            }else if (fileNameSuffix == 'bmp') {
                mini.get("fileName").setValue(fileList[0].name);
            }
            else {
                clearUploadFile();
                mini.alert('请上传图片类型文件！');
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