<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>售前文件编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/presaleDocuments/presaleDocumentsConst.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/presaleDocuments/presaleDocumentsFileUtil.js?version=${static_res_version}" type="text/javascript"></script>
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
            <input id="applyId" name="applyId" class="mini-hidden"/>
            <input id="REF_ID_" name="REF_ID_" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <caption style="font-weight: bold;font-size: 20px !important;">售前文件编辑</caption>
                <tr>
                    <td style="text-align: center;width: 15%">销售区域:<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="salesArea" name="salesArea" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringDecorationManualSalesArea"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 15%">语种:<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="docLanguage" name="docLanguage" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringLanguage"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">版本:<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="docVersion" name="docVersion" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">文件类型:<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="businessType" name="businessType" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=PresaleDocumentType"
                               valueField="key" textField="value" onvaluechanged="businessTypeChange()"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">负责人：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="repUserId" name="repUserId" textname="repUserName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="负责人" length="50"
                               mainfield="no" single="true"/>
                    </td>
                    <td style="text-align: center;width: 15%">发布时间:<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="releaseTime" name="releaseTime" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">状态:<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="businessStatus" name="businessStatus" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">系统分类:</td>
                    <td style="min-width:170px">
                        <input id="systemType" name="systemType" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=PresaleDocumentSystemType"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">适用性说明:<span style="color:red">*</span></td>
                    <td colspan="3">
                        <input id="applicabilityStatement" name="applicabilityStatement" class="mini-textarea"
                               style="width:99.1%;height:150px"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">备注:</td>
                    <td colspan="3">
                        <input id="remarks" name="remarks" class="mini-textarea" style="width:99.1%;height:150px"/>
                    </td>
                </tr>
                <%--关联产品--%>
                <tr>
                    <td style="text-align: center;height: 400px">关联产品：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="productButtons">
                            <a id="addProduct" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addProduct()">添加产品</a>
                            <a id="addProduct2" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addProduct2()">添加属具</a>
                            <a id="deleteProduct" class="mini-button btn-red" style="margin-bottom: 5px;margin-top: 5px"
                               onclick="deleteProduct()">删除</a>
                        </div>
                        <div id="productListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="true" allowCellWrap="false"
                             showCellTip="true" autoload="true" idField="id" multiSelect="true" showPager="false"
                             showColumnsMenu="false" allowcelledit="true" allowcellselect="true" allowAlternating="true">
                            <div property="columns">
                                <div type="checkcolumn" width="20"></div>
                                <div field="designModel_item" width="100" headerAlign="center" align="center">
                                    设计型号
                                </div>
                                <div field="saleModel_item" width="100" headerAlign="center" align="center">
                                    销售型号
                                </div>
                                <div field="materialCode_item" width="100" headerAlign="center" align="center">
                                    物料编码
                                </div>
                                <div field="productManagerName_item" width="100" headerAlign="center" align="center" width="30">
                                    产品主管
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <%--文件上传--%>
                <tr>
                    <td style="text-align: center;height: 400px">文件上传：</td>
                    <td colspan="3">
                        <div id="fileButtons" class="mini-toolbar">
                            <a id="addFile" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addFile()">添加文件</a>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false" allowCellWrap="false"
                             showCellTip="true" autoload="true" idField="id" multiSelect="true" showPager="false"
                             showColumnsMenu="false" allowcelledit="true" allowcellselect="true" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" headerAlign="center" width="20">序号</div>
                                <div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
                                <div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100">备注说明</div>
                                <div field="CREATE_BY_NAME" align="center" headerAlign="center" width="100">上传人</div>
                                <div field="createTime" align="center" headerAlign="center" width="100" dateFormat="yyyy-MM-dd HH:mm:ss">上传时间</div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="fileRenderer">操作</div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<%--关联产品型谱弹窗--%>
<div id="spectrumWindow" title="选择产品型号" class="mini-window" style="width:850px;height:500px;" showModal="true" showFooter="true" allowResize="true">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">设计型号: </span>
        <input class="mini-textbox" width="130" id="searchDesignModel" textField="designModel" valueField="designModel"
               style="margin-right: 15px"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchDesignModel()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="spectrumListGrid" class="mini-datagrid" style="width: 100%; height: 100%;"
             allowResize="false" allowCellWrap="false" showCellTip="true" idField="id"
             allowCellEdit="true" allowCellSelect="true" multiSelect="true" showColumnsMenu="false"
             sizeList="[50,100,200,500]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons"
             allowSortColumn="true" allowUnselect="true" autoLoad="false" onload="onload"
             url="${ctxPath}/world/core/productSpectrum/applyList.do">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div field="materialCode" width="120" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    <spring:message code="page.productSpectrumList.wlh"/>
                </div>
                <div field="designModel" width="150" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.sjxh"/>
                </div>
                <div field="departName" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.cps"/>
                </div>
                <div field="productManagerName" width="80" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    <spring:message code="page.productSpectrumList.cpzg"/>
                </div>
                <div field="saleModel" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.xsxh"/>
                </div>
                <div field="dischargeStage" width="120" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    <spring:message code="page.productSpectrumList.pfjd"/>
                </div>
                <div field="rdStatus" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.yfzt"/>
                </div>
                <div field="yfztqr" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                     allowSort="true">研发状态确认时间
                </div>
                <div field="productNotes" width="180" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    <spring:message code="page.productSpectrumList.cpsm"/>
                </div>
                <div field="tagNames" width="240" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.cpbq"/>
                </div>
                <div field="manuStatus" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.zzzt"/>
                </div>
                <div field="zzztqr" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                     allowSort="true">制造状态确认时间
                </div>
                <div field="saleStatus" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.xszt"/>
                </div>
                <div field="xsztqr" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                     allowSort="true">销售状态确认时间
                </div>
                <div field="abroad" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.ghnwxs"/>
                </div>
                <div field="region" width="180" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.ghxsqyhgj"/>
                </div>
                <div field="taskName" headerAlign='center' align='center' width="80">当前任务</div>
                <div field="allTaskUserNames" headerAlign='center' align='center' width="120">当前处理人</div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="okWindow()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="hideWindow()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<div id="spectrumWindow2" title="选择属具型号" class="mini-window" style="width:850px;height:500px;" showModal="true" showFooter="true" allowResize="true">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">设计型号: </span>
        <input class="mini-textbox" width="130" id="searchDesignModel2" style="margin-right: 15px"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchDesignModel2()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="spectrumListGrid2" class="mini-datagrid" style="width: 100%; height: 100%;"
             allowResize="false" allowCellWrap="false" showCellTip="true" idField="id"
             allowCellEdit="true" allowCellSelect="true" multiSelect="true" showColumnsMenu="false"
             sizeList="[50,100,200,500]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons"
             allowSortColumn="true" allowUnselect="true" autoLoad="false" onload="onload"
             url="${ctxPath}/productDataManagement/core/attachedtoolsSpectrum/itemListQueryWithOutParamProcess.do">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div field="materialCode" width="120" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    <spring:message code="page.productSpectrumList.wlh"/>
                </div>
                <div field="designModel" width="150" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.sjxh"/>
                </div>
                <div field="responsibleName" width="80" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    <spring:message code="page.productSpectrumList.cpzg"/>
                </div>
                <div field="salesModel" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.xsxh"/>
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="okWindow2()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="hideWindow2()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var businessId = "${businessId}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var isPresaleDocumentsAdmin = "${isPresaleDocumentsAdmin}";
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var formBusiness = new mini.Form("#formBusiness");
    var productListGrid = mini.get("productListGrid");
    var fileListGrid = mini.get("fileListGrid");
    var spectrumWindow = mini.get("spectrumWindow");
    var spectrumWindow2 = mini.get("spectrumWindow2");
    var spectrumListGrid = mini.get("spectrumListGrid");
    var spectrumListGrid2 = mini.get("spectrumListGrid2");
    //..
    $(function () {
        if (businessId) {
            var url = jsUseCtxPath + "/presaleDocuments/core/masterData/getDataById.do?businessId=" + businessId;
            $.ajax({
                url: url,
                method: 'get',
                success: function (json) {
                    formBusiness.setData(json);
                    var productSpectrumItems = JSON.parse(json.productSpectrum);
                    productListGrid.setData(productSpectrumItems);
                    var businessType = json.businessType;
                    //不同场景的处理
                    if (action == "edit") {
                        //编辑
                        if (businessType != PresaleDocumentType.BUSINESS_TYPE_CHANPINJIBENJIEGOUGONGNENGYUYUANLIJIESHAO) {
                            mini.get("systemType").setEnabled(false);
                        }
                        var url = "${ctxPath}/presaleDocuments/core/file/getFileListInfos.do?businessId=" + businessId;
                        fileListGrid.load(url);
                    } else if (action == "copy") {
                        //全新复制
                        mini.get("id").setValue("");//清除主键
                        mini.get("applyId").setValue("");//清除关联的申请单id
                        mini.get("REF_ID_").setValue("");//清除基因id
                        mini.get("docVersion").setValue("");//清除版本
                        mini.get("releaseTime").setValue("");//清除发布时间
                        if (businessType != PresaleDocumentType.BUSINESS_TYPE_CHANPINJIBENJIEGOUGONGNENGYUYUANLIJIESHAO) {
                            mini.get("systemType").setEnabled(false);
                        }
                        businessId = "";
                        var url = "${ctxPath}/presaleDocuments/core/file/getFileListInfos.do?businessId=" + businessId;
                        fileListGrid.load(url);
                    } else if (action == "clone") {
                        //升版复制
                        mini.get("id").setValue("");//清除主键
                        mini.get("applyId").setValue("");//清除关联的申请单id
                        mini.get("docVersion").setValue("");//清除版本
                        mini.get("releaseTime").setValue("");//清除发布时间
                        mini.get("salesArea").setEnabled(false);
                        mini.get("docLanguage").setEnabled(false);
                        mini.get("businessType").setEnabled(false);
                        mini.get("repUserId").setEnabled(false);
                        mini.get("applicabilityStatement").setEnabled(false);
                        mini.get("systemType").setEnabled(false);
                        businessId = "";
                        var url = "${ctxPath}/presaleDocuments/core/file/getFileListInfos.do?businessId=" + businessId;
                        fileListGrid.load(url);
                    } else if (action == "language") {
                        //多语言复制
                        mini.get("id").setValue("");//清除主键
                        mini.get("applyId").setValue("");//清除关联的申请单id
                        mini.get("docLanguage").setValue("");//清除语言
                        //mini.get("docVersion").setValue("");//todo:不清除版本!
                        mini.get("releaseTime").setValue("");//清除发布时间
                        mini.get("businessType").setEnabled(false);
                        mini.get("repUserId").setEnabled(false);
                        if (businessType != PresaleDocumentType.BUSINESS_TYPE_CHANPINJIBENJIEGOUGONGNENGYUYUANLIJIESHAO) {
                            mini.get("systemType").setEnabled(false);
                        }
                        businessId = "";
                        var url = "${ctxPath}/presaleDocuments/core/file/getFileListInfos.do?businessId=" + businessId;
                        fileListGrid.load(url);
                    } else {
                        formBusiness.setEnabled(false);
                        mini.get("saveStandard").setVisible(false);
                        mini.get("addProduct").setVisible(false);
                        mini.get("deleteProduct").setVisible(false);
                        mini.get("fileButtons").setVisible(false);
                        var url = "${ctxPath}/presaleDocuments/core/file/getFileListInfos.do?businessId=" + businessId;
                        fileListGrid.load(url);
                    }
                }
            });
        } else if (action == "add") {
            //新增
            if (businessType != PresaleDocumentType.BUSINESS_TYPE_CHANPINJIBENJIEGOUGONGNENGYUYUANLIJIESHAO) {
                mini.get("systemType").setEnabled(false);
            }
        }
    });
    //..
    function businessTypeChange() {
        var businessType = mini.get("businessType").getValue();
        if (businessType == PresaleDocumentType.BUSINESS_TYPE_CHANPINJIBENJIEGOUGONGNENGYUYUANLIJIESHAO) {
            mini.get("systemType").setEnabled(true);
        } else {
            mini.get("systemType").setEnabled(false);
            mini.get("systemType").setValue("");
        }
    }
    //..
    function addProduct() {
        spectrumWindow.show();
        searchDesignModel();
    }
    function addProduct2() {
        spectrumWindow2.show();
        searchDesignModel2();
    }
    //..
    function deleteProduct() {
        var row = productListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        delRowGrid("productListGrid");
    }
    //..
    function saveBusiness() {
        var postData = _GetFormJsonMini("formBusiness");
        var productSpectrumItems = productListGrid.getData();
        if (productSpectrumItems.length > 0) {
            postData.productSpectrum = productSpectrumItems;
        }
        //检查必填项
        var checkResult = saveValidCheck(postData);
        if (!checkResult.success) {
            mini.alert(checkResult.reason);
            return;
        }
        _SubmitJson({
            url: jsUseCtxPath + "/presaleDocuments/core/masterData/saveBusiness.do",
            method: 'POST',
            data: postData,
            postJson: true,
            showMsg: false,
            success: function (returnData) {
                if (returnData.success) {
                    mini.alert(returnData.message, "提醒", function () {
                        var url = jsUseCtxPath + "/presaleDocuments/core/masterData/editPage.do?id=" +
                            returnData.data + "&action=edit";
                        window.location.href = url;
                    });
                } else {
                    mini.alert("保存失败:" + returnData.message);
                }
            },
            fail: function (returnData) {
                mini.alert("保存失败:" + returnData.message);
            }
        });
    }
    //..
    function saveValidCheck(postData) {
        var checkResult = {};
        if (!postData.salesArea) {
            checkResult.success = false;
            checkResult.reason = '销售区域不能为空！';
            return checkResult;
        }
        if (!postData.docLanguage) {
            checkResult.success = false;
            checkResult.reason = '语种不能为空！';
            return checkResult;
        }
        if (!postData.businessType) {
            checkResult.success = false;
            checkResult.reason = '文件类型不能为空！';
            return checkResult;
        }
        if (!postData.repUserId) {
            checkResult.success = false;
            checkResult.reason = '负责人不能为空！';
            return checkResult;
        }
        if (!postData.systemType && postData.businessType == PresaleDocumentType.BUSINESS_TYPE_CHANPINJIBENJIEGOUGONGNENGYUYUANLIJIESHAO) {
            checkResult.success = false;
            checkResult.reason = '系统分类不能为空！';
            return checkResult;
        }
        if (!postData.applicabilityStatement) {
            checkResult.success = false;
            checkResult.reason = '适应性说明不能为空！';
            return checkResult;
        }
        if (productListGrid.getData().length == 0) {
            checkResult.success = false;
            checkResult.reason = '关联产品不能为空！';
            return checkResult;
        }
        checkResult.success = true;
        return checkResult;
    }
    //..
    function addFile() {
        if (!businessId) {
            mini.alert("请先进行表单的保存");
            return;
        }
        if (!mini.get("businessType").getValue()) {
            mini.alert("请选择文件类型");
            return;
        }
        if (fileListGrid.getData().length == 1) {
            mini.alert("只能上传一个文件！");
            return;
        }
        var urlCut = "/presaleDocuments/core/file/fileUpload.do";
        url = "/presaleDocuments/core/file/openFileUploadWindow.do?businessId=" + businessId +
            "&businessType=" + mini.get("businessType").getValue() + "&urlCut=" + urlCut +
            "&fileType=" + uploadFormat.UPLOAD_FORMAT_OTHER + "&isSingle=true";
        openFileUploadWindow(jsUseCtxPath + url, function (data) {
            fileListGrid.load();
        });
    }
    //..
    function fileRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        var previewUrls = {
            pdf: "/presaleDocuments/core/file/pdfPreview.do",
            office: "/presaleDocuments/core/file/officePreview.do",
            pic: "/presaleDocuments/core/file/imagePreview.do"
        };
        cellHtml = returnPreviewSpan(record.fileName, record.id, record.businessId, record.businessType, coverContent, previewUrls);
        //和pdf预览用的同一个url
        if (action != "detail") {
            var downLoadUrl = '/presaleDocuments/core/file/pdfPreview.do';
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.businessId + '\',\'' + record.businessType + '\',\'' + downLoadUrl + '\')">下载</span>';
        }
        //增加删除按钮
        if (action == "edit" || action == "copy" || action == "clone" || action == "language") {
            var deleteUrl = "/presaleDocuments/core/file/deleteFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.businessId + '\',\'' + record.businessType + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }
    function deleteFileCallback() {
        fileListGrid.load();
    }
    //..以下型谱相关
    function searchDesignModel() {
        var queryParam = [];
        //其他筛选条件
        var designModel = $.trim(mini.get("searchDesignModel").getValue());
        if (designModel) {
            queryParam.push({name: "designModel", value: designModel});
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = spectrumListGrid.getPageIndex();
        data.pageSize = spectrumListGrid.getPageSize();
        data.sortField = spectrumListGrid.getSortField();
        data.sortOrder = spectrumListGrid.getSortOrder();
        //查询
        spectrumListGrid.load(data);
    }
    function searchDesignModel2() {
        var queryParam = [];
        //其他筛选条件
        var designModel = $.trim(mini.get("searchDesignModel2").getValue());
        if (designModel) {
            queryParam.push({name: "designModel", value: designModel});
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = spectrumListGrid2.getPageIndex();
        data.pageSize = spectrumListGrid2.getPageSize();
        data.sortField = spectrumListGrid2.getSortField();
        data.sortOrder = spectrumListGrid2.getSortOrder();
        //查询
        spectrumListGrid2.load(data);
    }
    //..
    function okWindow() {
        var alreadyMap = new Map();
        var alreadyData = productListGrid.getData();
        for (var i = 0, j = alreadyData.length; i < j; i++) {
            alreadyMap.set(alreadyData[i].id, alreadyData[i]);
        }
        var selectRows = spectrumListGrid.getSelecteds();
        if (selectRows.length <= 0) {
            mini.alert("请至少选中一条记录！");
            return;
        }
        if (((alreadyMap.size > 0 && selectRows.length > 0) || (alreadyMap.size == 0 && selectRows.length > 1)) &&
            (mini.get("businessType").getValue() == PresaleDocumentType.BUSINESS_TYPE_JISHUGUIGESHU ||
            mini.get("businessType").getValue() == PresaleDocumentType.BUSINESS_TYPE_CHANPINBIAOXUANPEIBIAO ||
            mini.get("businessType").getValue() == PresaleDocumentType.BUSINESS_TYPE_DUOGONGNENGJIJUXITONGYALILIULIANGFANWEIBIAOZHUNZHIBIAO ||
            mini.get("businessType").getValue() == PresaleDocumentType.BUSINESS_TYPE_CHANPINQUANSHENGMINGZHOUQICHENGBENQINGDAN ||
            mini.get("businessType").getValue() == PresaleDocumentType.BUSINESS_TYPE_JISHUWENJIANFUJIAN)) {
            mini.alert("当前的文件类型只允许适配一个机型！");
            return;
        }
        for (var i = 0, l = selectRows.length; i < l; i++) {
            if (!alreadyMap.has(selectRows[i].id)) {
                var data = {};
                data.id = selectRows[i].id;
                data.saleModel_item = selectRows[i].saleModel;
                data.designModel_item = selectRows[i].designModel;
                data.materialCode_item = selectRows[i].materialCode;
                data.productManagerId_item = selectRows[i].productManagerId;
                data.productManagerName_item = selectRows[i].productManagerName;
                productListGrid.addRow(data, 0);
            } else {
                alreadyMap.get(selectRows[i].id).saleModel_item = selectRows[i].saleModel;
                alreadyMap.get(selectRows[i].id).designModel_item = selectRows[i].designModel;
                alreadyMap.get(selectRows[i].id).materialCode_item = selectRows[i].materialCode;
                alreadyMap.get(selectRows[i].id).productManagerId_item = selectRows[i].productManagerId;
                alreadyMap.get(selectRows[i].id).productManagerName_item = selectRows[i].productManagerName;
                productListGrid.updateRow(alreadyMap.get(selectRows[i].id));
            }
        }
        hideWindow()
    }
    function okWindow2() {
        var alreadyMap = new Map();
        var alreadyData = productListGrid.getData();
        for (var i = 0, j = alreadyData.length; i < j; i++) {
            alreadyMap.set(alreadyData[i].id, alreadyData[i]);
        }
        var selectRows = spectrumListGrid2.getSelecteds();
        if (selectRows.length <= 0) {
            mini.alert("请至少选中一条记录！");
            return;
        }
        if (((alreadyMap.size > 0 && selectRows.length > 0) || (alreadyMap.size == 0 && selectRows.length > 1)) &&
            (mini.get("businessType").getValue() == PresaleDocumentType.BUSINESS_TYPE_JISHUGUIGESHU ||
            mini.get("businessType").getValue() == PresaleDocumentType.BUSINESS_TYPE_CHANPINBIAOXUANPEIBIAO ||
            mini.get("businessType").getValue() == PresaleDocumentType.BUSINESS_TYPE_DUOGONGNENGJIJUXITONGYALILIULIANGFANWEIBIAOZHUNZHIBIAO ||
            mini.get("businessType").getValue() == PresaleDocumentType.BUSINESS_TYPE_CHANPINQUANSHENGMINGZHOUQICHENGBENQINGDAN ||
            mini.get("businessType").getValue() == PresaleDocumentType.BUSINESS_TYPE_JISHUWENJIANFUJIAN)) {
            mini.alert("当前的文件类型只允许适配一个机型！");
            return;
        }
        for (var i = 0, l = selectRows.length; i < l; i++) {
            if (!alreadyMap.has(selectRows[i].id)) {
                var data = {};
                data.id = selectRows[i].id;
                data.saleModel_item = selectRows[i].salesModel;
                data.designModel_item = selectRows[i].designModel;
                data.materialCode_item = selectRows[i].materialCode;
                data.productManagerId_item = selectRows[i].responsibleId;
                data.productManagerName_item = selectRows[i].responsibleName;
                productListGrid.addRow(data, 0);
            } else {
                alreadyMap.get(selectRows[i].id).saleModel_item = selectRows[i].salesModel;
                alreadyMap.get(selectRows[i].id).designModel_item = selectRows[i].designModel;
                alreadyMap.get(selectRows[i].id).materialCode_item = selectRows[i].materialCode;
                alreadyMap.get(selectRows[i].id).productManagerId_item = selectRows[i].responsibleId;
                alreadyMap.get(selectRows[i].id).productManagerName_item = selectRows[i].responsibleName;
                productListGrid.updateRow(alreadyMap.get(selectRows[i].id));
            }
        }
        hideWindow2()
    }
    //..
    function hideWindow() {
        spectrumWindow.hide();
        mini.get("searchDesignModel").setValue('');
        mini.get("searchDesignModel").setText('');
    }
    function hideWindow2() {
        spectrumWindow2.hide();
        mini.get("searchDesignModel2").setValue('');
        mini.get("searchDesignModel2").setText('');
    }
    //..
    function onload(e) {
        e.sender.clearSelect();
    }
</script>
</body>
</html>
