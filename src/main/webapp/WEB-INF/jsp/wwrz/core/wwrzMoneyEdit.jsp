<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>委外费用管理</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/wwrz/wwrzMoneyEdit.js?version=${static_res_version}"
            type="text/javascript"></script>
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
    <div class="form-container" style="margin:0 auto; width: 100%;">
        <form id="moneyForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
                <caption>
                    委外费用表单
                </caption>
                <tbody>
                <tr class="firstRow displayTr">
                    <td align="center"></td>
                    <td align="left"></td>
                    <td align="center"></td>
                    <td align="left"></td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">产品型号：</td>
                    <td align="center" rowspan="1">
                        <input id="applyId" style="width:98%;" class="mini-buttonedit" showClose="true"
                               name="applyId" textname="productModel" allowInput="false" onbuttonclick="selectPlanName()"/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        产品类型：
                    </td>
                    <td align="center" rowspan="1">
                        <input id="productType" name="productType" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="产品类型："
                               length="50" readonly
                               only_read="false" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=CPLX"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        司机室形式：
                    </td>
                    <td align="center" rowspan="1">
                        <input id="cabForm" name="cabForm" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="司机室形式："
                               length="50" readonly
                               only_read="false"  allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=SJSXS"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        产品主管<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="1" >
                        <input id="productLeader" name="productLeader" textname="productLeaderName"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;"  readonly
                               allowinput="false"
                               label="产品主管" length="50" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        认证项目<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" rowspan="1">
                        <input id="items" name="items" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="认证项目："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="true"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=RZXM"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td style="text-align: center;width: 20%">合同编号：</td>
                    <td colspan="1">
                        <input id="contractCode"  name="contractCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">费用金额（万元）：</td>
                    <td colspan="1">
                        <input id="money"  name="money" class="mini-spinner" style="width:98%;" allowLimitValue="false" allowNull="true"/>
                    </td>
                    <td style="text-align: center;width: 20%">发票编号：</td>
                    <td colspan="1">
                        <input id="invoiceCode"  name="invoiceCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">付款日期：</td>
                    <td colspan="1">
                        <input id="paymentDate" name="paymentDate" class="mini-datepicker" allowInput="false" style="width:100%;height:34px" >
                    </td>
                    <td style="text-align: center;width: 20%">报告：</td>
                    <td colspan="1">
                        <input id="reportCode" style="width:98%;" class="mini-buttonedit" showClose="true"
                               name="reportCode" textname="reportCode"  allowInput="false" onbuttonclick="selectCertName('report')"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">证书：</td>
                    <td colspan="1">
                        <input id="certCode" style="width:98%;" class="mini-buttonedit" showClose="true"
                               name="certCode" textname="certCode"  allowInput="false" onbuttonclick="selectCertName('cert')"/>
                    </td>
                    <td style="text-align: center;width: 20%">归档单号：</td>
                    <td colspan="1">
                        <input id="documentCode"  name="documentCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        备注：
                    </td>
                    <td align="left" colspan="1">
                        <input id="remark" name="remark" class="mini-textbox rxc" plugins="mini-textbox"
                                only_read="false" allowinput="true" value=""
                               style="width:100%;height:34px"/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        认证公司代号：
                    </td>
                    <td align="left" colspan="1">
                        <input id="companyCode" name="companyCode" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="认证公司代号："
                               length="50"
                               only_read="false"  allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="companyCode" valueField="companyCode" emptyText="请选择..."
                               url="${ctxPath}/wwrz/core/company/listCompany.do"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                </tr>
                </tbody>
            </table>
        </form>
    </div>
    <div id="selectPlanWindow" title="选择认证流程" class="mini-window" style="width:800px;height:450px;"
         showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
        <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
             borderStyle="border-left:0;border-top:0;border-right:0;">
            <span class="text" style="width:auto">型号: </span><input class="mini-textbox" style="width: 80px" name="planModel" id="planModel"/>
            <span class="text" style="width:auto">产品主管: </span><input class="mini-textbox" style="width: 80px" name="chargerName" id="chargerName"/>
            <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchPlanList()">查询</a>
        </div>
        <div class="mini-fit">
            <div id="planListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onrowdblclick="onRowDblClick"
                 idField="id"  showColumnsMenu="false" sizeList="[5,10,20,50,100,200]" pageSize="5"
                 allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/wwrz/core/test/listApplyData.do">
                <div property="columns">
                    <div type="checkcolumn" width="30"></div>
                    <div field="moneyId" width="60" headerAlign="center" align="center" allowSort="false" renderer="onStatus">绑定状态</div>
                    <div field="productModel" width="80" headerAlign="center" align="center" allowSort="false">型号</div>
                    <div field="productType" width="80" headerAlign="center" align="center" allowSort="true" renderer="onProductType">产品类型</div>
                    <div field="cabForm" width="80" headerAlign="center" align="center" allowSort="true" renderer="onCabForm">司机室形式</div>
                    <div field="chargerName" width="80" headerAlign="center" align="center" allowSort="false">产品主管</div>
                    <div field="itemNames" width="80" headerAlign="center" align="center" allowSort="true">认证项目</div>
                </div>
            </div>
        </div>
        <div property="footer" style="padding:5px;height: 35px">
            <table style="width:100%;height: 100%">
                <tr>
                    <td style="width:120px;text-align:center;">
                        <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectPlanOK()"/>
                        <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectPlanHide()"/>
                    </td>
                </tr>
            </table>
        </div>
    </div>
    <div id="selectCertWindow" title="选择报告、证书" class="mini-window" style="width:800px;height:450px;"
         showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
        <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
             borderStyle="border-left:0;border-top:0;border-right:0;">
            <span class="text" style="width:auto">编号: </span><input class="mini-textbox" style="width: 150px" name="reportCertCode" id="reportCertCode"/>
            <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchCertList()">查询</a>
        </div>
        <div class="mini-fit">
            <div id="reportCertGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
                 idField="id"  showColumnsMenu="false" sizeList="[5,10,20,50,100,200]" pageSize="5"
                 allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/wwrz/core/file/reportFiles.do">
                <div property="columns">
                    <div type="checkcolumn" width="30"></div>
                    <div field="fileName" width="80" headerAlign="center" align="left" allowSort="false">文件名</div>
                    <div field="reportCode" width="80" headerAlign="center" align="center" allowSort="true">编号</div>
                </div>
            </div>
        </div>
        <div property="footer" style="padding:5px;height: 35px">
            <table style="width:100%;height: 100%">
                <tr>
                    <td style="width:120px;text-align:center;">
                        <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectCertOK()"/>
                        <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectCertHide()"/>
                    </td>
                </tr>
            </table>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var applyObj = ${applyObj};
    var action = '${action}';
    var moneyForm = new mini.Form("#moneyForm");
    var selectPlanWindow=mini.get("selectPlanWindow");
    var planListGrid=mini.get("planListGrid");
    var selectCertWindow=mini.get("selectCertWindow");
    var reportCertGrid=mini.get("reportCertGrid");
    var productTypeList = getDics("CPLX");
    var cabFormList = getDics("SJSXS");
    var reportType = "";
    $(function () {
        moneyForm.setData(applyObj);
    })

    function saveData() {
        moneyForm.validate();
        if (!moneyForm.isValid()) {
            return;
        }
        var formData = moneyForm.getData();
        formData.itemNames = mini.get('items').getText();
        var config = {
            url: jsUseCtxPath + "/wwrz/core/money/save.do",
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

    /**
     * 选择
     */
    function selectPlanName(){
        selectPlanWindow.show();
        searchPlanList();
    }
    function searchPlanList() {
        var queryParam = [];
        //其他筛选条件
        var planModel = $.trim(mini.get("planModel").getValue());
        if (planModel) {
            queryParam.push({name: "productModel", value: planModel});
        }
        var chargerName = $.trim(mini.get("chargerName").getValue());
        if (chargerName) {
            queryParam.push({name: "chargerName", value: chargerName});
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = planListGrid.getPageIndex();
        data.pageSize = planListGrid.getPageSize();
        data.sortField = planListGrid.getSortField();
        data.sortOrder = planListGrid.getSortOrder();
        //查询
        planListGrid.load(data);
    }
    function selectPlanOK() {
        var selectRow = planListGrid.getSelected();
        if (selectRow) {
            mini.get("applyId").setValue(selectRow.id);
            mini.get("applyId").setText(selectRow.productModel);
            mini.get("productType").setValue(selectRow.productType);
            mini.get("cabForm").setValue(selectRow.cabForm);
            mini.get("productLeader").setText(selectRow.chargerName);
            mini.get("items").setValue(selectRow.items);
            mini.get("items").setText(selectRow.itemNames);
        }
        selectPlanHide();
    }

    function selectPlanHide() {
        selectPlanWindow.hide();
        mini.get("planModel").setValue('');
        mini.get("chargerName").setValue('');
    }

    /**
     * 选择
     */
    function selectCertName(_type){
        selectCertWindow.show();
        reportType = _type;
        searchCertList();
    }
    function searchCertList() {
        var queryParam = [];
        //其他筛选条件
        var reportCertCode = $.trim(mini.get("reportCertCode").getValue());
        if (reportCertCode) {
            queryParam.push({name: "reportCode", value: reportCertCode});
        }
        if(reportType=='cert'){
            queryParam.push({name: "reportType", value: '证书'});
        }else if(reportType=='report'){
            queryParam.push({name: "reportType", value: '报告'});
        }
        queryParam.push({name: "fileType", value: 'report'});
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = planListGrid.getPageIndex();
        data.pageSize = planListGrid.getPageSize();
        data.sortField = planListGrid.getSortField();
        data.sortOrder = planListGrid.getSortOrder();
        //查询
        reportCertGrid.load(data);
    }
    function selectCertOK() {
        var selectRow = reportCertGrid.getSelected();
        if (selectRow) {
            if(reportType=='cert'){
                mini.get("certCode").setValue(selectRow.reportCode);
                mini.get("certCode").setText(selectRow.reportCode);
            }else if(reportType=='report'){
                mini.get("reportCode").setValue(selectRow.reportCode);
                mini.get("reportCode").setText(selectRow.reportCode);
            }
        }
        selectCertHide();
    }

    function selectCertHide() {
        selectCertWindow.hide();
        mini.get("reportCertCode").setValue('');
    }


    function onStatus(e) {
        var record = e.record;
        var moneyId = record.moneyId
        var _html = '';
        var color = '';
        var resultText = "";
        if(moneyId){
            resultText = "已绑定";
            color = 'red'
        }else{
            resultText = "未绑定";
            color = 'green'
        }
        _html = '<span style="color: ' + color + '">' + resultText + '</span>'
        return _html;
    }
</script>
</body>
</html>
