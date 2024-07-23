<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>装修手册资料完整性登记</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveBtn" class="mini-button"  onclick="saveForm()">保存</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 90%;" >
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="applyUserId" name="applyUserId" class="mini-hidden"/>
            <input id="applyDepId" name="applyDepId" class="mini-hidden"/>
            <input class="mini-hidden" id="totalNum" name="totalNum"/>
            <input class="mini-hidden" id="useNum" name="useNum"/>
            <input class="mini-hidden" id="repairNum" name="repairNum"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0"  >
                <caption style="font-weight: bold">
                    装修手册资料完整性登记
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%">单据编号(自动生成)：</td>
                    <td style="min-width:170px">
                        <input id="businessNo" name="businessNo" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">设计型号：</td>
                    <td colspan="1">
                        <input id="designModel" required name="designModel" style="width:99%;" class="mini-buttonedit" name="feedbackId"
                               textname="designModel"
                               allowInput="false" onbuttonclick="selectDesignModel()"/>

                    </td>
                </tr>

                <tr>
                    <td style="text-align: center;width: 15%">物料编码:</td>
                    <td style="min-width:170px">
                        <input id="materialCode" name="materialCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">销售型号：</td>
                    <td style="min-width:170px">
                        <input id="salesModel" name="salesModel" class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>

                <tr>
                    <td align="center" style="width:15%;">产品主管：</td>
                    <td style="min-width:170px">
                        <input id="productLeader" name="productLeader" textname="productLeaderName"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;"
                               allowinput="false" required="true"
                               label="产品主管" length="50" mainfield="no" single="true"/>
                    </td>
                    <td align="center" style="width:15%;">手册责任人：</td>
                    <td style="min-width:170px">
                        <input id="manualMan" name="manualMan" textname="manualManName"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;"
                               allowinput="false" required="true"
                               label="手册责任人" length="50" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">创建时间(自动生成)：</td>
                    <td style="min-width:170px">
                        <input id="CREATE_TIME_" name="CREATE_TIME_" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               valueType="string" showTime="false" showOkButton="true" showClearButton="false" style="width:98%;"enabled="false"/>
                    </td>
                </tr>

                <tr>
                    <td style="text-align: center;width: 15%">备注：</td>
                    <td colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="640"
                                  vtype="length:640" minlen="0" allowinput="true" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;height: 500px">资料完整性登记：</td>

                    <td colspan="3">
                        <div class="mini-toolbar" id="itemButtons">
                            <a id="addItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addItem" >添加</a>
                            <a id="deleteItem" class="mini-button btn-red" style="margin-bottom: 5px;margin-top: 5px" onclick="deleteItem">删除</a>
                        </div>
                        <div id="itemListGrid" class="mini-datagrid" style="height: 95%" allowResize="true" allowCellWrap="false"
                             showCellTip="true" autoload="true" idField="id" multiSelect="true" showPager="false"
                             showColumnsMenu="false" allowcelledit="true" allowcellselect="true" allowAlternating="true"
                             allowCellWrap="true"
                             oncellendedit="OnCellEndEdit"
                             oncellvalidation="onCellValidation"
                             idField="id"
                             url="${ctxPath}/serviceEngineering/core/decorationManualIntegrity/getItemList.do?businessId=${businessId}">
                            <div property="columns">
                                <div type="checkcolumn" width="25"></div>
                                <div type="indexcolumn" width="30" headerAlign="center" align="center">序号</div>
                                <div field="materialCode" name="materialCode" width="60" headerAlign="center" align="center" renderer="render">外购件物料号
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="materialName" name="materialName" width="80" headerAlign="center" align="center" renderer="render">物料名称
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="materialDesc" name="materialDesc" width="100" headerAlign="center" align="center" renderer="render" >物料描述
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="useDescBook" name="useDescBook" width="50" headerAlign="center" align="center" renderer="render">使用说明书
                                    <input property="editor"  class="mini-combobox" emptyText="请选择" style="width:98%;"
                                           textField="value" valueField="key" emptyText="请选择"
                                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择"
                                           data="[{'key':'是','value':'是'},{'key':'否','value':'否'}]"/>
                                </div>
                                <div field="useTopicCode" name="useTopicCode" width="100" headerAlign="center" align="center" renderer="render">Topic编码
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="useBpmStatus" name="useBpmStatus" width="100" headerAlign="center" align="center" renderer="render">RDM流程状态
                                </div>
                                <div field="repairBook" name="repairBook" width="50" headerAlign="center" align="center" renderer="render">维修要领书
                                    <input property="editor"  class="mini-combobox" emptyText="请选择" style="width:98%;"
                                           textField="value" valueField="key" emptyText="请选择"
                                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择"
                                           data="[{'key':'是','value':'是'},{'key':'否','value':'否'}]"/>
                                </div>
                                <div field="repairTopicCode" name="repairTopicCode" width="100" headerAlign="center" align="center" renderer="render">Topic编码
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="repairBpmStatus" name="repairBpmStatus" width="100" headerAlign="center" align="center" renderer="render">RDM流程状态
                                </div>
                            </div>
                        </div>
                    </td>

                </tr>
            </table>
        </form>
    </div>
</div>


<%--导入窗口--%>
<div id="importWindow" title="导入窗口" class="mini-window" style="width:750px;height:280px;" showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importItem()">导入</a>
        <a id="closeImportWindow" class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%">上传模板：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">模板下载.xlsx</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%">操作：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName" readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()" accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px" onclick="uploadFile()">选择文件</a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px" onclick="clearUploadFile()">清除</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<%--关联产品型谱弹窗--%>
<div id="spectrumWindow" title="选择产品型号"
     class="mini-window" style="width:850px;height:600px;"
     showModal="true" showFooter="true" allowResize="true">
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
             allowCellEdit="true" allowCellSelect="true" multiSelect="false" showColumnsMenu="false"
             sizeList="[50,100,200,500]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons"
             allowSortColumn="true" allowUnselect="true" autoLoad="false"
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
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                 allowSort="true">创建时间
            </div>

        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px"
                           value="确定" onclick="okWindow()"/>
                    <input type="button" style="height: 25px;width: 70px"
                           value="取消"
                           onclick="hideWindow()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var itemListGrid = mini.get("itemListGrid");
    var formBusiness = new mini.Form("#formBusiness");
    var businessId = "${businessId}";
    var itemsButtons = mini.get("itemButtons");
    var currentTime = "${currentTime}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var importWindow = mini.get("importWindow");
//增加设计型号选择窗口
    var spectrumWindow = mini.get("spectrumWindow");
    var spectrumListGrid = mini.get("spectrumListGrid");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;

    //..
    $(function () {
        var url = jsUseCtxPath + "/serviceEngineering/core/decorationManualIntegrity/getDetail.do";
        $.post(
            url,
            {businessId: businessId},
            function (json) {
                formBusiness.setData(json);
                if (action == 'detail') {
                    formBusiness.setEnabled(false);
                    mini.get("addItem").setEnabled(false);
                    itemsButtons.hide();

                } else if (action == 'add') {
                    mini.get("addItem").setEnabled(true);
                    // $("saveBtn").show();
                }else if (action == 'edit'){
                        itemsButtons.show();
                }
                    });
    });

    //..保存
    function saveForm() {
        var formVal = validBusiness();
        if (!formVal.result) {
            mini.alert(formVal.message);
            return;
        }
        //明细表单验证
        itemListGrid.validate();
        if (!itemListGrid.isValid()) {
            var error = itemListGrid.getCellErrors()[0];
            itemListGrid.beginEditCell(error.record, error.column);
            mini.alert(error.column.header + error.errorText);
            return;
        }
        var formData = _GetFormJsonMini("formBusiness");
        var items = itemListGrid.getChanges();
        if (items.length > 0) {
            formData.items = items;
        }
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/decorationManualIntegrity/saveBusiness.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (returnObj) {
                if (returnObj) {
                    var message = "";
                    if (returnObj.success) {
                        message = "数据保存成功";
                        mini.alert(message);
                        var url = jsUseCtxPath + "/serviceEngineering/core/decorationManualIntegrity/editPage.do?businessId=" +
                            returnObj.data + "&action=edit";
                        window.location.href = url;

                    } else {
                        message = "数据保存失败，" + returnObj.message;
                        mini.alert(message);
                    }

                }
            }
        });
    }



    //添加行
    //..添加
    function addItem() {
        var newRow = {}
        itemListGrid.addRow(newRow, itemListGrid.total - 1);
    }
    //..
    function deleteItem() {
        var rows = itemListGrid.getSelecteds();
        if (rows.length == 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        delRowGrid("itemListGrid");
    }

    //..检验表单是否必填
    function validBusiness() {

        var salesModel = $.trim(mini.get("salesModel").getValue());
        if (!salesModel) {
            return {"result": false, "message": "请填写销售型号"};
        }
        var designModel = $.trim(mini.get("designModel").getValue());
        if (!designModel) {
            return {"result": false, "message": "请填写设计型号"};
        }
        var materialCode = $.trim(mini.get("materialCode").getValue());
        if (!materialCode) {
            return {"result": false, "message":"请物料编码"};
        }

        return {"result": true};
    }

    function onCellValidation(e) {
        if (e.field == 'materialCode' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }


        // if (e.field == 'useTopicCode' && (!e.value || e.value == '')) {
        //     e.isValid = false;
        //     e.errorText = '使用说明书Topic编码不能为空！';
        // }

        

    }

    //..常规渲染
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }


    /**
     * 关联设计型号弹窗
     */
    function selectDesignModel() {
        spectrumWindow.show();
        searchDesignModel();
    }

    /**
     * 关联设计型号查询
     */
    function searchDesignModel() {
        var queryParam = [];
        //其他筛选条件
        var designModel = $.trim(mini.get("searchDesignModel").getValue());
        if (designModel) {
            queryParam.push({name: "designModel", value: designModel});
        }
        // queryParam.push({name: "instStatus", value: "SUCCESS_END"});

        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = spectrumListGrid.getPageIndex();
        data.pageSize = spectrumListGrid.getPageSize();
        data.sortField = spectrumListGrid.getSortField();
        data.sortOrder = spectrumListGrid.getSortOrder();
        //查询
        spectrumListGrid.load(data);
    }


    /**
     * 产品型号确定按钮
     */
    function okWindow() {
        var selectRows = spectrumListGrid.getSelecteds();
        if (!selectRows) {
            mini.alert("请选择产品型号！");
            return;
        }
        var designModels = [];
        var salesModels = [];
        var productManagerName =[];
        var materialCode = [];
        for (var i = 0, l = selectRows.length; i < l; i++) {
            var r = selectRows[i];
            designModels.push(r.designModel);
            salesModels.push(r.saleModel);
            productManagerName.push(r.productManagerName);
            materialCode.push(r.materialCode);
        }
        mini.get("materialCode").setValue(materialCode.join(','));
        mini.get("materialCode").setText(materialCode.join(','));
        mini.get("designModel").setValue(designModels.join(','));
        mini.get("designModel").setText(designModels.join(','));
        mini.get("salesModel").setValue(salesModels.join(','));
        mini.get("salesModel").setText(salesModels.join(','));
        mini.get("productLeader").setValue(productManagerName.join(','));
        mini.get("productLeader").setText(productManagerName.join(','));
        hideWindow()
    }

    /**
     * 产品型号关闭按钮
     */
    function hideWindow() {
        spectrumWindow.hide();
        mini.get("searchDesignModel").setValue('');
        mini.get("searchDesignModel").setText('');
    }

    //物料号输入完自动补全信息
    function OnCellEndEdit(e){
        var record = e.record;
        materialCode =record.materialCode;
        if(e.field == "materialCode" && materialCode !='')
        {
            $.ajax({
                url: jsUseCtxPath + '/serviceEngineering/core/decorationManualIntegrity/getMaterialCodeDetail.do?materialCode='+materialCode,
                type: 'post',
                async: false,
                data: {materialCode:materialCode},
                contentType: 'application/json',
                success: function (data) {
                    if (data) {
                            itemListGrid.updateRow(record,data);
                        }
                    }
            });
        }

    }
</script>
</body>
</html>
