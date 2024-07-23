<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>资源台架编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <style type="text/css">
        .item-image {
            width: 200px;
            height: 190px;
            margin: auto;
            margin-top: 8px;
            display: block;
        }
    </style>
</head>
<body>
<%--工具栏--%>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveBusiness" class="mini-button" onclick="saveBusiness()">保存</a>
    </div>
</div>
<%--表单视图--%>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;padding: 0">
        <form id="businessForm" method="post">
            <input class="mini-hidden" id="id" name="id"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <tr>
                    <td style="text-align: center;width: 15%">顺序号：</td>
                    <td style="width: 23%">
                        <input id="orderNo" name="orderNo" class="mini-spinner" style="width:98%;" minValue="0" maxValue="99999999"/>
                    </td>
                    <td style="text-align: center;width: 20%">台架名称：</td>
                    <td style="min-width:170px">
                        <input id="platformName" name="platformName" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">月度利用率：</td>
                    <td style="width: 23%">
                        <input id="monthlyUtiRate" name="monthlyUtiRate" class="mini-spinner" style="width:98%;" minValue="0" maxValue="1"
                               format="p" increment="0.05"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">文字介绍：</td>
                    <td colspan="3">
                        <input id="remarks" name="remarks" class="mini-textarea" style="width:98%;height:150px;line-height:25px;">
                        </input>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">机型图片</td>
                    <td style="min-width:170px">
                        <input class="mini-textbox" style="width:100%;float: left" id="fileName" name="fileName" readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: right;" onclick="uploadFile()">选择文件</a>
                    </td>
                    <td style="text-align: center;width: 20%">图片预览：</td>
                    <td style="text-align: center;min-width:170px">
                        <img id="img" class="item-image" style="width:200px;height: 200px"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<
<%--JS--%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessId = "${businessId}";
    var action = "${action}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var isOverseaSalesCustomizationAdmins = "${isOverseaSalesCustomizationAdmins}";
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var businessForm = new mini.Form("#businessForm");
    //..
    $(function () {
        var url = jsUseCtxPath + "/resourcesPlatform/core/masterData/getMasterDataDataById.do?businessId=" + businessId;
        $.ajax({
            url: url,
            method: 'get',
            success: function (json) {
                businessForm.setData(json);
                //不同场景的处理
                if (action == "detail") {
                    businessForm.setEnabled(false);
                    mini.get("saveBusiness").setEnabled(false);
                    mini.get("uploadFileBtn").setEnabled(false);
                }
                var imgElement = document.getElementById("img");
                imgElement.src = "${ctxPath}/resourcesPlatform/core/masterData/imagePreview.do?fileId=${businessId}&fileName=" + json.fileName;
                if (isOverseaSalesCustomizationAdmins == "true" || currentUserNo == "admin") {
                    //不做限制
                }else {
                    //一般产品主管关闭授权和责任人指定权限
                    mini.get("responsibleUserId").setEnabled(false);
                }
            }
        });
    });

    //..
    function saveBusiness() {
        var formData = _GetFormJsonMini("businessForm");
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
                            mini.alert(message, "提示信息", function (action) {
                                if (returnObj.success) {
                                    var url = jsUseCtxPath + "/resourcesPlatform/core/masterData/masterDataEditPage.do?businessId=" +
                                        returnObj.data + "&action=edit";
                                    window.location.href = url;
                                }
                            });
                        }
                    }
                }
            };
            //开始上传
            xhr.open('POST', jsUseCtxPath + '/resourcesPlatform/core/masterData/saveMasterData.do', false);
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
    function getFileType(fileName) {
        var suffix = "";
        var suffixIndex = fileName.lastIndexOf('.');
        if (suffixIndex != -1) {
            suffix = fileName.substring(suffixIndex + 1).toLowerCase();
        }
        var pdfArray = ['pdf'];
        if (pdfArray.indexOf(suffix) != -1) {
            return 'pdf';
        }
        var officeArray = ['doc', 'docx', 'ppt', 'txt', 'xlsx', 'xls', 'pptx'];
        if (officeArray.indexOf(suffix) != -1) {
            return 'office';
        }
        var picArray = ['jpg', 'jpeg', 'jif', 'bmp', 'png', 'tif', 'gif'];
        if (picArray.indexOf(suffix) != -1) {
            return 'pic';
        }
        return 'other';
    }
    //..
    function getSelectFile() {
        debugger
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            var fileType = getFileType(fileList[0].name);
            if (fileType == 'pic') {
                mini.get("fileName").setValue(fileList[0].name);
            }
            else {
                clearUploadFile();
                mini.alert("请上传图片文件");
            }
        }
    }
    //..
    function uploadFile() {
        $("#inputFile").click();
    }

    //清空文件
    function clearUploadFile() {
        $("#inputFile").val('');
        mini.get("fileName").setValue('');
    }
</script>
</body>
</html>
