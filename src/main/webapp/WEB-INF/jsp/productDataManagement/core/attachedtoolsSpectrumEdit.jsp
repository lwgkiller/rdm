<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>属具型谱类别</title>
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
                <caption style="font-weight: bold">
                    属具型谱类别
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%">属具类型：</td>
                    <td style="min-width:170px">
                        <input id="attachedtoolsType" name="attachedtoolsType"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByTreeKeyTopOne.do?dicKey=attachedtoolsType"
                               valueField="key" textField="value" onvaluechanged="attachedtoolsTypeChanged"/>
                    </td>
                    <td style="text-align: center;width: 15%">属具子类型：</td>
                    <td style="min-width:170px">
                        <input id="attachedtoolsType2" name="attachedtoolsType2"
                               class="mini-combobox" style="width:98%"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">顺序号：</td>
                    <td style="width: 23%">
                        <input id="orderNo" name="orderNo" class="mini-spinner" style="width:98%;" minValue="0" maxValue="99999999"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">功能简介：</td>
                    <td colspan="3">
                        <input id="functionIntroduction" name="functionIntroduction" class="mini-textarea" style="width:98%;height:200px"/>
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
                    <td style="min-width:170px">
                        <img id="img" class="item-image"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<%--JS--%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessId = "${businessId}";
    var action = "${action}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var isAttachedtoolsSpectrumAdmin = "${isAttachedtoolsSpectrumAdmin}";
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var businessForm = new mini.Form("#businessForm");
    //..
    $(function () {
        var url = jsUseCtxPath + "/productDataManagement/core/attachedtoolsSpectrum/getModelDataById.do?businessId=" + businessId;
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
                } else if (action == "edit") {
                    mini.get("attachedtoolsType").setEnabled(false);
                    mini.get("attachedtoolsType2").setEnabled(false);
                }
                var imgElement = document.getElementById("img");
                imgElement.src = "${ctxPath}/productDataManagement/core/attachedtoolsSpectrum/imageView.do?fileId=${businessId}&fileName=" + json.fileName;
            }
        });
    });
    //..
    function saveBusiness() {
        var formData = businessForm.getData();
        //检查必填项
        var checkResult = saveValidCheck(formData);
        if (!checkResult.success) {
            mini.alert(checkResult.reason);
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
                            mini.alert(message, "提示信息", function (action) {
                                if (returnObj.success) {
                                    var url = jsUseCtxPath + "/productDataManagement/core/attachedtoolsSpectrum/modelEditPage.do?businessId=" +
                                        returnObj.data + "&action=edit";
                                    window.location.href = url;
                                }
                            });
                        }
                    }
                }
            };
            //开始上传
            xhr.open('POST', jsUseCtxPath + '/productDataManagement/core/attachedtoolsSpectrum/saveModel.do', false);
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
    function saveValidCheck(postData) {
        var checkResult = {};
        if (!postData.attachedtoolsType) {
            checkResult.success = false;
            checkResult.reason = '属具类型不能为空！';
            return checkResult;
        }
        if (!postData.attachedtoolsType2) {
            checkResult.success = false;
            checkResult.reason = '属具子类型不能为空！';
            return checkResult;
        }
        if (!postData.orderNo) {
            checkResult.success = false;
            checkResult.reason = '顺序号不能为空！';
            return checkResult;
        }
        checkResult.success = true;
        return checkResult;
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
    //..
    function attachedtoolsTypeChanged() {
        var parent = mini.get('attachedtoolsType').getSelected();
        mini.get('attachedtoolsType2').setValue("");
        if (parent) {
            var url = '${ctxPath}/sys/core/sysDic/getByParentId.do?parentId=' + parent.dicId;
            mini.get('attachedtoolsType2').setUrl(url);
            mini.get('attachedtoolsType2').select(0);
        }
    }
</script>
</body>
</html>
