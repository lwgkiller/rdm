<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>售前文件申请</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/presaleDocuments/presaleDocumentsConst.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/presaleDocuments/presaleDocumentsFileUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<%----%>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 100%;">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="businessStatus" name="businessStatus" class="mini-hidden"/>
            <input id="applyUserId" name="applyUserId" class="mini-hidden"/>
            <input id="applyTime" name="applyTime" class="mini-hidden"/>
            <input id="endTime" name="endTime" class="mini-hidden"/>
            <input id="masterDataId" name="masterDataId" class="mini-hidden"/>
            <input id="REF_ID_" name="REF_ID_" class="mini-hidden"/>
            <input id="INST_ID_" name="INST_ID_" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <%--转办用--%>
            <input id="myTaskId" name="myTaskId" class="mini-hidden"/>
            <%--还原原始人用--%>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    售前文件申请
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%">单号：</td>
                    <td style="min-width:170px">
                        <input id="businessNo" name="businessNo" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">销售区域:<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="salesArea" name="salesArea" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringDecorationManualSalesArea"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">语种:<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="docLanguage" name="docLanguage" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringLanguage"
                               valueField="key" textField="value" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">文件类型:<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="businessType" name="businessType" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=PresaleDocumentType"
                               valueField="key" textField="value" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">责任人：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="repUserId" name="repUserId" textname="repUserName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="责任人" length="50"
                               mainfield="no" single="true" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">系统分类:</td>
                    <td style="min-width:170px">
                        <input id="systemType" name="systemType" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=PresaleDocumentSystemType"
                               valueField="key" textField="value" enabled="false"/>
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
                <tr>
                    <td style="text-align: center;width: 20%">模板下载：</td>
                    <td colspan="3">
                        <a id="downTemplate" href="#" style="color:blue;text-decoration:underline;">${businessType}</a>
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
                <%--部件负责人--%>
                <tr id="buJianFuZeRen" style="display: none;">
                    <td style="text-align: center;height: 400px">部件负责人：</td>
                    <td colspan="3">
                        <div id="buJianFuZeRenListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="true"
                             allowCellWrap="false" oncellvalidation="buJianFuZeRenCellValidation"
                             showCellTip="true" autoload="true" idField="id" multiSelect="true" showPager="false"
                             showColumnsMenu="false" allowcelledit="false" allowcellselect="true" allowAlternating="true">
                            <div property="columns">
                                <div type="checkcolumn" width="20"></div>
                                <div field="zhuanYe_item" width="100" headerAlign="center" align="center">专业</div>
                                <div field="fuZeRenId_item" displayfield="fuZeRenName_item" width="100" headerAlign="center" align="center">负责人
                                    <input property="editor" class="mini-user" style="width:auto;" single="true" allowinput="false"/>
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
                             showColumnsMenu="false" allowcelledit="true" allowcellselect="true" allowAlternating="true"
                             url="${ctxPath}/presaleDocuments/core/file/getFileListInfos.do?businessId=${businessId}">
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
<%----%>
<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var businessId = "${businessId}";
    var masterDataId = "${masterDataId}";
    var businessType = "${businessType}";
    var nodeVarsStr = '${nodeVars}';
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var isPresaleDocumentsAdmin = "${isPresaleDocumentsAdmin}";
    var isService = "${isService}";//是否是服务所的人
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var formBusiness = new mini.Form("#formBusiness");
    var productListGrid = mini.get("productListGrid");
    var fileListGrid = mini.get("fileListGrid");
    var spectrumWindow = mini.get("spectrumWindow");
    var spectrumWindow2 = mini.get("spectrumWindow2");
    var spectrumListGrid = mini.get("spectrumListGrid");
    var spectrumListGrid2 = mini.get("spectrumListGrid2");
    var buJianFuZeRenListGrid = mini.get("buJianFuZeRenListGrid");
    var codeName = "";
    //..
    $(function () {
        //通过售前文件列表的变更自动打开，自动填写相应字段
        if (masterDataId && masterDataId != '') {
            var url = jsUseCtxPath + "/presaleDocuments/core/masterData/getDataById.do";
            $.post(
                url,
                {businessId: masterDataId},
                function (json) {
                    //售前文件列表的关联产品复制过来
                    var productSpectrumItems = JSON.parse(json.productSpectrum);
                    productListGrid.setData(productSpectrumItems);
                    //售前文件列表的其他属性复制过来
                    mini.get("salesArea").setValue(json.salesArea);
                    mini.get("docLanguage").setValue(json.docLanguage);
                    mini.get("applicabilityStatement").setValue(json.applicabilityStatement);
                    mini.get("repUserId").setValue(json.repUserId);
                    mini.get("repUserId").setText(json.repUserName);
                    mini.get("systemType").setValue(json.systemType);
                    mini.get("businessType").setValue(json.businessType);
                    if (businessType == PresaleDocumentType.BUSINESS_TYPE_CHANPINQUANSHENGMINGZHOUQICHENGBENQINGDAN) {
                        //如果类型是成本清单，则把部件负责人窗口显示出来
                        $("#buJianFuZeRen").show();
                        buJianFuZeRenListGrid.setAllowCellEdit(true);
                    }
                    mini.get("masterDataId").setValue(json.id);//其实就是{businessId: masterDataId}的masterDataId
                    mini.get("REF_ID_").setValue(json.REF_ID_);
                    if (action == 'detail') {
                        //自动打开action不可能为detail，但是保留代码的对称性，好读
                        formBusiness.setEnabled(false);
                        mini.get("addProduct").setEnabled(false);
                        mini.get("addProduct2").setEnabled(false);
                        mini.get("deleteProduct").setEnabled(false);
                        //超管浏览状态下可以操作文件
                        if (currentUserNo != 'admin' && isPresaleDocumentsAdmin != 'true') {
                            mini.get("addFile").setEnabled(false);
                        }
                        buJianFuZeRenListGrid.setAllowCellEdit(false);
                        $("#detailToolBar").show();
                        //非草稿放开流程信息查看按钮
                        if (status != 'DRAFTED') {
                            $("#processInfo").show();
                        }
                    } else if (action == 'task') {
                        taskActionProcess();
                    } else if (action == 'edit') {//正常没有，草稿和初始编辑状态也得限制才需要
                        //通过售前文件列表的变更生成的单据不需要放开特殊字段和特殊值赋值,一些值来源于售前文件列表
                    }
                    mini.get("salesArea").setEnabled(false);//发起变更时不允许修改
                    mini.get("docLanguage").setEnabled(false);//发起变更时不允许修改
                    mini.get("systemType").setEnabled(false);//发起变更时不允许修改
                    mini.get("businessType").setEnabled(false);//发起变更时不允许修改
                    //给下载标签增加事件
                    $('#downTemplate').click(function () {
                        downTemplate(businessType);
                    });
                    $('#downTemplate').text(businessType);
                });
        } else {//正常打开
            var url = jsUseCtxPath + "/presaleDocuments/core/apply/getDataById.do";
            $.post(
                url,
                {businessId: businessId},
                function (json) {
                    formBusiness.setData(json);
                    if (json.businessType) {//处理非新增的情况
                        businessType = json.businessType;
                    }
                    //排除新增的情况
                    if (json.productSpectrum) {
                        var productSpectrumItems = JSON.parse(json.productSpectrum);
                        productListGrid.setData(productSpectrumItems);
                    }
                    if (businessType == PresaleDocumentType.BUSINESS_TYPE_CHANPINQUANSHENGMINGZHOUQICHENGBENQINGDAN) {
                        //全生命成本清单要显示部件负责人分配窗口，并且初始化其值
                        $("#buJianFuZeRen").show();
                        buJianFuZeRenListGrid.setAllowCellEdit(true);
                        ///排除新增的情况
                        if (json.buJianFuZeRen) {
                            var buJianFuZeRenItems = JSON.parse(json.buJianFuZeRen);
                            buJianFuZeRenListGrid.setData(buJianFuZeRenItems);
                        }
                    }
                    if (action == 'detail') {
                        formBusiness.setEnabled(false);
                        mini.get("addProduct").setEnabled(false);
                        mini.get("addProduct2").setEnabled(false);
                        mini.get("deleteProduct").setEnabled(false);
                        //超管浏览状态下可以操作文件
                        if (currentUserNo != 'admin' && isPresaleDocumentsAdmin != 'true') {
                            mini.get("addFile").setEnabled(false);
                        }
                        buJianFuZeRenListGrid.setAllowCellEdit(false);
                        $("#detailToolBar").show();
                        //非草稿放开流程信息查看按钮
                        if (status != 'DRAFTED') {
                            $("#processInfo").show();
                        }
                    } else if (action == 'task') {
                        taskActionProcess();
                    } else if (action == 'edit') {//正常没有，草稿和初始编辑状态也得限制才需要
                        mini.get("businessType").setValue(businessType);
                        if (isService == "true" || currentUserNo == "admin") {//服务所的发起的可以选责任人
                            mini.get("repUserId").setEnabled(true);
                        }
                        if (businessType == PresaleDocumentType.BUSINESS_TYPE_CHANPINJIBENJIEGOUGONGNENGYUYUANLIJIESHAO) {
                            mini.get("systemType").setEnabled(true);
                        }
                        if (businessType == PresaleDocumentType.BUSINESS_TYPE_CHANPINBIAOXUANPEIBIAO ||
                            businessType == PresaleDocumentType.BUSINESS_TYPE_DUOGONGNENGJIJUXITONGYALILIULIANGFANWEIBIAOZHUNZHIBIAO ||
                            businessType == PresaleDocumentType.BUSINESS_TYPE_CHANPINQUANSHENGMINGZHOUQICHENGBENQINGDAN
                        ) {
                            mini.get("docLanguage").setValue("中英文");
                        } else {
                            mini.get("docLanguage").setValue("中文");
                        }
                        if (businessType == PresaleDocumentType.BUSINESS_TYPE_JISHUWENJIANFUJIAN) {
                            mini.get("salesArea").setValue("其他");
                            mini.get("salesArea").setEnabled(false);
                        }
                    }
                    //正常打开的是通过售前文件列表的变更生成的单据
                    if (json.masterDataId && json.masterDataId != '') {
                        mini.get("salesArea").setEnabled(false);//发起变更时不允许修改
                        mini.get("docLanguage").setEnabled(false);//发起变更时不允许修改
                        mini.get("systemType").setEnabled(false);//发起变更时不允许修改
                        mini.get("businessType").setEnabled(false);//发起变更时不允许修改
                    }
                    //给下载标签增加事件
                    $('#downTemplate').click(function () {
                        downTemplate(businessType);
                    });
                    $('#downTemplate').text(businessType);
                });
        }
    });
    //..获取任务相关的环境变量，处理表单可见性
    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();
        //获取环境变量
        if (!nodeVarsStr) {
            nodeVarsStr = "[]";
        }
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'codeName') {
                codeName = nodeVars[i].DEF_VAL_;
            }
        }
        if (codeName == "A" || codeName == "C1" || codeName == "D1") {
            //如果是任务重新回到了编辑节点
            if (isService == "true" || currentUserNo == "admin") {//服务所的发起的可以选责任人
                mini.get("repUserId").setEnabled(true);
            }
            if (businessType == PresaleDocumentType.BUSINESS_TYPE_CHANPINJIBENJIEGOUGONGNENGYUYUANLIJIESHAO) {
                mini.get("systemType").setEnabled(true);
            }
            if (businessType == PresaleDocumentType.BUSINESS_TYPE_CHANPINQUANSHENGMINGZHOUQICHENGBENQINGDAN) {
                //全生命成本清单要显示部件负责人分配窗口
                $("#buJianFuZeRen").show();
                buJianFuZeRenListGrid.setAllowCellEdit(true);
            }
            if (businessType == PresaleDocumentType.BUSINESS_TYPE_JISHUWENJIANFUJIAN) {
                mini.get("salesArea").setEnabled(false);
            }
        } else if (codeName == "D2" || codeName == "D3" || codeName == "D4" || codeName == "D5" ||
            codeName == "D6" || codeName == "D7" || codeName == "U") {
            //如果是任务重新回到了流程期间需要上传文件的节点
            formBusiness.setEnabled(false);
            mini.get("addProduct").setEnabled(false);
            mini.get("addProduct2").setEnabled(false);
            mini.get("deleteProduct").setEnabled(false);
        } else {
            //其余的只能审批的节点
            formBusiness.setEnabled(false);
            mini.get("addProduct").setEnabled(false);
            mini.get("addProduct2").setEnabled(false);
            mini.get("deleteProduct").setEnabled(false);
            mini.get("addFile").setEnabled(false);
        }
    }
    //..流程引擎调用此方法进行表单数据的获取
    function getData() {
        var formData = _GetFormJsonMini("formBusiness");
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        if (formData.SUB_productListGrid) {
            delete formData.SUB_productListGrid;
        }
        if (formData.SUB_buJianFuZeRenListGrid) {
            delete formData.SUB_buJianFuZeRenListGrid;
        }
        //@lwgkiller:想要正确进行标题解析，不能删
        formData.bos = [];
        formData.vars = [{key: 'businessType', val: formData.businessType}];
        var productSpectrumItems = productListGrid.getData();
        if (productSpectrumItems.length > 0) {
            formData.productSpectrum = productSpectrumItems;
        }
        var buJianFuZeRenItems = buJianFuZeRenListGrid.getData();
        if (buJianFuZeRenItems.length > 0) {
            formData.buJianFuZeRen = buJianFuZeRenItems;
        }
        return formData;
    }
    //..保存草稿
    function saveBusiness(e) {
        window.parent.saveDraft(e);
    }
    //..启动流程
    function startBusinessProcess(e) {
        var formValid = validBusiness();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }
    //..流程中暂存信息（如编制阶段）
    function saveBusinessInProcess() {
        var formData = getData();
        $.ajax({
            url: jsUseCtxPath + '/presaleDocuments/core/apply/saveBusiness.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        message = "数据保存成功";
                    } else {
                        message = "数据保存失败" + data.message;
                    }
                    mini.alert(message, "提示信息", function () {
                        window.location.reload();
                    });
                }
            }
        });
    }
    //..流程中的审批或者下一步
    function businessApprove() {
        //编制阶段和流程期间需要上传文件的节点，下一步需要校验表单必填字段
        if (codeName == "A" || codeName == "C1" || codeName == "D1" || codeName == "D2" ||
            codeName == "D3" || codeName == "D4" || codeName == "D5" || codeName == "D6" ||
            codeName == "D7" || codeName == "U") {
            var formValid = validBusiness();//各种类型的复杂情况都在里面了，除了关联产品的数量放在了okWindow里
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        window.parent.approve();
    }
    //..转办
    function doTransferDiy() {
        debugger;
        var url = __rootPath + '/bpm/core/bpmTask/transfer.do?taskIds=' + mini.get("myTaskId").getValue();
        _OpenWindow({
            title: '任务转办',
            url: url,
            height: 350,
            width: 1000,
            ondestroy: function (action) {
                if (action == "ok") {
                    CloseWindow('ok');
                }
            }
        });
    }
    //..检验表单是否必填
    function validBusiness() {
        var salesArea = $.trim(mini.get("salesArea").getValue());
        if (!salesArea) {
            return {"result": false, "message": "销售区域不能为空!"};
        }
        var docLanguage = $.trim(mini.get("docLanguage").getValue());
        if (!docLanguage) {
            return {"result": false, "message": "语种不能为空!"};
        }
        var businessType = $.trim(mini.get("businessType").getValue());
        if (!businessType) {
            return {"result": false, "message": "文件类型不能为空!"};
        }
        var repUserId = $.trim(mini.get("repUserId").getValue());
        if (!repUserId && (isService == "true" || currentUserNo == "admin")) {
            return {"result": false, "message": "服务所初始提交时，负责人不能为空!"};
        }
        var systemType = $.trim(mini.get("systemType").getValue());
        if (!systemType && businessType == PresaleDocumentType.BUSINESS_TYPE_CHANPINJIBENJIEGOUGONGNENGYUYUANLIJIESHAO) {
            return {"result": false, "message": "系统分类不能为空!"};
        }
        var applicabilityStatement = $.trim(mini.get("applicabilityStatement").getValue());
        if (!applicabilityStatement) {
            return {"result": false, "message": "适应性说明不能为空!"};
        }
        //校验关联产品，其数量1还是n在弹出小窗口的ok按钮里校验了
        if (productListGrid.getData().length == 0) {
            return {"result": false, "message": "关联产品不能为空!"};
        }
        if (businessType == PresaleDocumentType.BUSINESS_TYPE_CHANPINDAOGOUSHOUCE) {
            for (var i = 0, l = productListGrid.getData().length; i < l; i++) {
                if (!productListGrid.getData()[i].productManagerId_item) {
                    return {"result": false, "message": "参与会签的关联产品的产品主管不能为空！"};
                }
            }
        }
        //校验文件，其数量在此校验
        if (fileListGrid.getData().length == 0) {
            return {"result": false, "message": "文件不能为空!"};
        }
        if (businessType == PresaleDocumentType.BUSINESS_TYPE_CHANPINQUANSHENGMINGZHOUQICHENGBENQINGDAN) {
            if (codeName == 'D1' || codeName == 'D3' || codeName == 'D5' || codeName == 'D7') {
                if (fileListGrid.getData().length != 1) {
                    return {"result": false, "message": "产品主管汇总阶段，只能保留一个文件!"};
                }
            } else if (codeName == 'D2' || codeName == 'D4' || codeName == 'D6') {
                var fileDatas = fileListGrid.getData();
                var isFileOK = false;
                for (var i = 0, l = fileDatas.length; i < l; i++) {
                    if (fileDatas[i].CREATE_BY_ == currentUserId) {
                        isFileOK = true;
                        break;
                    }
                }
                if (!isFileOK) {
                    return {"result": false, "message": "参与汇总者必须上传一个自己的文件!"};
                }
            }
        } else if (businessType != PresaleDocumentType.BUSINESS_TYPE_JISHUWENJIANFUJIAN &&
            businessType != PresaleDocumentType.BUSINESS_TYPE_JISHUGUIGESHU) {
            if (fileListGrid.getData().length != 1) {
                return {"result": false, "message": "只能上传一个文件!"};
            }
        }
        //校验部件负责人
        if (businessType == PresaleDocumentType.BUSINESS_TYPE_CHANPINQUANSHENGMINGZHOUQICHENGBENQINGDAN &&
            isService == "false" && currentUserNo != "admin") {
            if (buJianFuZeRenListGrid.getData().length == 0) {
                return {"result": false, "message": "部件负责人不能为空!请保存草稿进行专业初始化！"};
            }
            buJianFuZeRenListGrid.validate();
            if (!buJianFuZeRenListGrid.isValid()) {
                var error = buJianFuZeRenListGrid.getCellErrors()[0];
                buJianFuZeRenListGrid.beginEditCell(error.record, error.column);
                return {"result": false, "message": error.column.header + error.errorText};
            }
        }
        return {"result": true};
    }
    //..
    function buJianFuZeRenCellValidation(e) {
        debugger
        if (e.field == 'fuZeRenId_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
    }
    //..流程信息浏览
    function processInfo() {
        var instId = $("#instId").val();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: "流程图实例",
            width: 800,
            height: 600
        });
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
        var urlCut = "/presaleDocuments/core/file/applyfileUpload.do";
        url = "/presaleDocuments/core/file/openFileUploadWindow.do?businessId=" + businessId +
            "&businessType=" + mini.get("businessType").getValue() + "&urlCut=" + urlCut +
            "&fileType=" + uploadFormat.UPLOAD_FORMAT_OTHER + "&isSingle=false";
        openFileUploadWindow(jsUseCtxPath + url, function (data) {
            fileListGrid.load();
        });
    }
    //..
    function fileRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        var previewUrls = {
            pdf: "/presaleDocuments/core/file/applypdfPreview.do",
            office: "/presaleDocuments/core/file/applyofficePreview.do",
            pic: "/presaleDocuments/core/file/applyimagePreview.do"
        };
        cellHtml = returnPreviewSpan(record.fileName, record.id, record.businessId, record.businessType, coverContent, previewUrls);
        //和pdf预览用的同一个url
        var downLoadUrl = '/presaleDocuments/core/file/applypdfPreview.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.businessId + '\',\'' + record.businessType + '\',\'' + downLoadUrl + '\')">下载</span>';
        //增加删除按钮，初始编辑状态，或者任务中的编辑节点和流程过程中需要产品主管上传文件的节点都放开
        if (action == 'edit' ||
            (action == 'task' && (codeName == 'A' || codeName == 'C1' || codeName == 'D1' || codeName == 'D3' || codeName == 'D5' || codeName == 'D7')) ||
            (action == 'detail' && (currentUserNo == 'admin' || isPresaleDocumentsAdmin == 'true'))) {
            var deleteUrl = "/presaleDocuments/core/file/applydeleteFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.businessId + '\',\'' + record.businessType + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }
    function deleteFileCallback() {
        fileListGrid.load();
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
    function downTemplate(type) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/presaleDocuments/core/apply/templateDownload.do?type=" + type);
        $("body").append(form);
        form.submit();
        form.remove();
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
        debugger;
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
