<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>机型基本信息</title>
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
                    机型基本信息
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%">机型大类：</td>
                    <td style="min-width:170px">
                        <input id="productGroup" name="productGroup"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=overseaSalesCustomizationProductGroup"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 15%">销售型号：</td>
                    <td style="min-width:170px">
                        <input id="salsesModel" name="salsesModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">设计型号：</td>
                    <td style="min-width:170px">
                        <input id="productTypeSpectrumId" name="productTypeSpectrumId" class="mini-buttonedit"
                               showClose="true" allowInput="false"
                               value="productTypeSpectrumId" textName="designModel" style="width:98%;"
                               onbuttonclick="onButtonClick" oncloseclick="onCloseClick"/>
                    </td>
                    <td style="text-align: center;width: 15%">物料号：</td>
                    <td style="min-width:170px">
                        <input id="materialCode" name="materialCode" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">出口区域：</td>
                    <td style="min-width:170px">
                        <input id="saleArea" name="saleArea"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=reviewSaleArea"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 15%">出口国家：</td>
                    <td style="min-width:170px">
                        <input id="saleCountry" name="saleCountry"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=saleCountry"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">发动机型号：</td>
                    <td style="min-width:170px">
                        <input id="engine" name="engine" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%">额定功率：</td>
                    <td style="min-width:170px">
                        <input id="ratedPower" name="ratedPower" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">铲斗容量：</td>
                    <td style="min-width:170px">
                        <input id="bucketCapacity" name="bucketCapacity" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%">工作质量：</td>
                    <td style="min-width:170px">
                        <input id="operatingMass" name="operatingMass" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">责任人：</td>
                    <td style="width: 23%">
                        <input id="responsibleUserId" name="responsibleUserId" textname="responsibleUser" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;" allowinput="false" label="责任人" length="50"
                               mainfield="no" single="true" enabled="true"/>
                    </td>
                    <td style="text-align: center;width: 15%">顺序号：</td>
                    <td style="width: 23%">
                        <input id="orderNo" name="orderNo" class="mini-spinner" style="width:98%;" minValue="0" maxValue="99999999"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">代理商授权：</td>
                    <td colspan="3">
                        <input id="groupIds" name="groupIds" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'false','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:98%;height:34px" allowinput="false" label="代理商授权" textname="groupNames" length="500"
                               maxlength="500" minlen="0" single="false" required="false" initlogindep="false" showclose="false"
                               mwidth="160" wunit="px" mheight="34" hunit="px" enabled="true"/>
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
<%--产品型谱用窗口--%>
<div id="selectProductTypeSpectrumWindow" title="产品型谱" class="mini-window" style="width:1300px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-fit">
        <div id="productTypeSpectrumGrid" idField="id" class="mini-datagrid" style="width: 100%; height: 100%;"
             allowResize="false" multiSelect="false" showColumnsMenu="false" showPager="false" showFilterRow="true"
             url="${ctxPath}/world/core/overseaSalesCustomization/productTypeSpectrumListQuery.do">
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="designModel" width="60" headerAlign="center" align="center" allowSort="false">设计型号
                    <input id="designModelFilter" property="filter" class="mini-textbox" style="width:100%;"
                           onvaluechanged="onFilterChanged"/>
                </div>
                <div field="saleModel" width="60" headerAlign="center" align="center" allowSort="false">销售型号</div>
                <div field="materialCode" width="60" headerAlign="center" align="center" allowSort="false">物料号</div>
                <%--<div field="productManagerName" width="60" headerAlign="center" align="center" allowSort="false">产品主管</div>--%>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectProductTypeSpectrumOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectProductTypeSpectrumHide()"/>
                </td>
            </tr>
        </table>
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
    var isOverseaSalesCustomizationAdmins = "${isOverseaSalesCustomizationAdmins}";
    var isOverseaSalesCustomizationModelAdmin = "${isOverseaSalesCustomizationModelAdmin}";
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var businessForm = new mini.Form("#businessForm");
    var selectProductTypeSpectrumWindow = mini.get("selectProductTypeSpectrumWindow");
    var productTypeSpectrumGrid = mini.get("productTypeSpectrumGrid");
    //..
    $(function () {
        var url = jsUseCtxPath + "/world/core/overseaSalesCustomization/getModelDataById.do?businessId=" + businessId;
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
                mini.get("groupIds").setText(json.groupNames);
                var imgElement = document.getElementById("img");
                imgElement.src = "${ctxPath}/world/core/overseaSalesCustomizationClient/imageView.do?fileId=${businessId}&fileName=" + json.fileName;
                if (isOverseaSalesCustomizationAdmins == "true" || currentUserNo == "admin") {
                    //不做限制
                } else if (isOverseaSalesCustomizationModelAdmin == "true") {
                    //只给授权的权限
                    businessForm.setEnabled(false);
                    mini.get("groupIds").setEnabled(true);
                    mini.get("uploadFileBtn").setEnabled(false);
                } else {
                    //一般产品主管关闭授权和责任人指定权限
                    mini.get("groupIds").setEnabled(false);
                    mini.get("responsibleUserId").setEnabled(false);
                }
            }
        });
    });
    //..
    function selectCanGroups() {
        var groupIds = mini.get('groupIds');
        _GroupCanDlg({
            tenantId: '1',
            single: false,
            width: 900,
            height: 500,
            title: '用户组',
            callback: function (groups) {
                var uIds = [];
                var uNames = [];
                for (var i = 0; i < groups.length; i++) {
                    uIds.push(groups[i].groupId);
                    uNames.push(groups[i].name);
                }
                if (groupIds.getValue() != '') {
                    uIds.unshift(groupIds.getValue().split(','));
                }
                if (groupIds.getText() != '') {
                    uNames.unshift(groupIds.getText().split(','));
                }
                groupIds.setValue(uIds.join(','));
                groupIds.setText(uNames.join(','));
            }
        });
    }
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
                                    var url = jsUseCtxPath + "/world/core/overseaSalesCustomization/modelEditPage.do?businessId=" +
                                        returnObj.data + "&action=edit";
                                    window.location.href = url;
                                }
                            });
                        }
                    }
                }
            };
            //开始上传
            xhr.open('POST', jsUseCtxPath + '/world/core/overseaSalesCustomization/saveModel.do', false);
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
        if (!postData.productGroup) {
            checkResult.success = false;
            checkResult.reason = '机型大类不能为空！';
            return checkResult;
        }
        if (!postData.salsesModel) {
            checkResult.success = false;
            checkResult.reason = '销售型号不能为空！';
            return checkResult;
        }
        if (!postData.engine) {
            checkResult.success = false;
            checkResult.reason = '发动机型号不能为空！';
            return checkResult;
        }
        if (!postData.ratedPower) {
            checkResult.success = false;
            checkResult.reason = '额定功率不能为空！';
            return checkResult;
        }
        if (!postData.bucketCapacity) {
            checkResult.success = false;
            checkResult.reason = '铲斗容量不能为空！';
            return checkResult;
        }
        if (!postData.operatingMass) {
            checkResult.success = false;
            checkResult.reason = '工作质量不能为空！';
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
    //..以下型谱
    function onButtonClick() {
        selectProductTypeSpectrumWindow.show();
        productTypeSpectrumGrid.load();
    }
    //..
    function onCloseClick() {
        mini.get("productTypeSpectrumId").setValue("");
        mini.get("productTypeSpectrumId").setText("");
        mini.get("materialCode").setValue("");
    }
    //..
    function selectProductTypeSpectrumOK() {
        var selectRow = productTypeSpectrumGrid.getSelected();
        if (selectRow) {
            mini.get("productTypeSpectrumId").setValue(selectRow.id);
            mini.get("productTypeSpectrumId").setText(selectRow.designModel);
            mini.get("materialCode").setValue(selectRow.materialCode);
            selectProductTypeSpectrumHide();
        } else {
            mini.alert("请选择一条数据！");
            return;
        }
    }
    //..
    function selectProductTypeSpectrumHide() {
        selectProductTypeSpectrumWindow.hide();
        mini.get("designModelFilter").setValue("");
    }
    //..
    function onFilterChanged(e) {
        var designModelFilter = mini.get("designModelFilter");
        var designModel = designModelFilter.getValue().toLowerCase();
        productTypeSpectrumGrid.filter(function (row) {
            var r1 = true;
            if (designModel) {
                r1 = String(row.designModel).toLowerCase().indexOf(designModel) != -1;
            }
            return r1;
        });
    }
</script>
</body>
</html>
