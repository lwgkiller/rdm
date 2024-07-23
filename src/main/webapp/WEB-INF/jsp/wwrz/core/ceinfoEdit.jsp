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
        <form id="formCeinfo" method="post">
            <input id="ceinfoId" name="ceinfoId" class="mini-hidden"/>
            <input id="linkWwrz" name="linkWwrz" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 10%">台账类型：</td>
                    <td style="width: 23%;min-width:170px;font-size:14pt">
                        <input property="editor" id="addupdate" name="addupdate" class="mini-combobox"
                               style="width:98%;" onvaluechanged="typeChange()"
                               data="[{id:'新增',text:'新增'},{id:'更新',text:'更新'}]"/>
                    </td>
                    <td style="width: 10%">需更新的台账：</td>
                    <td style="width: 23%;min-width:170px;font-size:14pt">
                        <input id="replaceNo" name="replaceNo" textname="oldsaleModel" style="width:98%;" property="editor"
                               class="mini-buttonedit" showClose="true"
                               oncloseclick="onSelectNoteClick" allowInput="false"
                               onbuttonclick="selectNote()"/>
                    </td>
                    <td style="width: 10%">配置信息识别码：</td>
                    <td style="width: 23%;min-width:170px;font-size:14pt">
                        <input id="onlyNum" name="onlyNum" class="mini-textbox" readonly style="width:98%;"/>
                    </td>

                </tr>
                <tr>
                    <td style="width: 10%">销售型号：</td>
                    <td style="width: 23%;min-width:170px;font-size:14pt">
                        <input id="saleModel" name="saleModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 10%">设计型号：</td>
                    <td style="width: 23%;min-width:170px;font-size:14pt">
                        <input id="designModel" name="designModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 10%">物料号：</td>
                    <td style="width: 23%;min-width:170px;font-size:14pt">
                        <input id="materialNum" name="materialNum" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">配置说明：</td>
                    <td style="width: 23%;min-width:170px;font-size:14pt">
                        <input id="explains" name="explains" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td>排放标准：</td>
                    <td align="center">
                        <input id="emission" name="emission" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="排放标准："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="text" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=emission"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                </tr>
                <tr>
                    <td>噪声指令证书:</td>
                    <td>
                        <input id="zsId" name="zsId" textname="zsNum" style="width:98%;" property="editor"
                               class="mini-buttonedit" showClose="true"
                               oncloseclick="onSelectReportClick('噪声指令证书')" allowInput="false"
                               onbuttonclick="selectReport('噪声指令证书')"/>
                    </td>
                    <td>文件日期:</td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="zsStartDate" name="zsStartDate" class="mini-datepicker" allowInput="false"
                               style="width:100%;height:34px">
                    </td>
                    <td>有效期:</td>
                    <td id="zsEndDateTd">
                        <input id="zsEndDate" name="zsEndDate" class="mini-datepicker" allowInput="false"
                               style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">认证机构名称：</td>
                    <td style="width: 23%;min-width:170px;font-size:14pt">
                        <input id="certName" name="certName"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=certName"
                               valueField="key" textField="value" onvaluechanged="setCertAdress()"/>
                    </td>
                    <td style="width: 10%">认证机构地址：</td>
                    <td style="width: 23%;min-width:170px;font-size:14pt">
                        <input id="certAdress" name="certAdress" readonly class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 10%">噪声测量值：</td>
                    <td style="width: 23%;min-width:170px;font-size:12pt">
                        <input id="noiseReal" name="noiseReal" class="mini-textbox" style="width:70%;"/>dB(A)
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">噪声承诺值：</td>
                    <td style="width: 23%;min-width:170px;font-size:12pt">
                        <input id="noisePlan" name="noisePlan" class="mini-textbox" style="width:70%;"/>dB(A)
                    </td>
                    <td style="width: 10%">发动机功率/转速：</td>
                    <td style="width: 23%;min-width:170px;font-size:12pt">
                        <input id="enginePower" name="enginePower" class="mini-textbox" style="width:60%;"/>kW/r/min
                    </td>

                </tr>
                <tr>
                    <td>机械指令证书:</td>
                    <td>
                        <input id="jxId" name="jxId" textname="jxNum" style="width:98%;" property="editor"
                               class="mini-buttonedit" showClose="true"
                               oncloseclick="onSelectReportClick('机械指令证书')" allowInput="false"
                               onbuttonclick="selectReport('机械指令证书')"/>
                    </td>
                    <td>文件日期:</td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="jxStartDate" name="jxStartDate" class="mini-datepicker" allowInput="false"
                               style="width:100%;height:34px">
                    </td>
                    <td>有效期:</td>
                    <td id="jxEndDateTd">
                        <input id="jxEndDate" name="jxEndDate" class="mini-datepicker" allowInput="false"
                               style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td>电磁兼容指令证书:</td>
                    <td>
                        <input id="dcId" name="dcId" textname="dcNum" style="width:98%;" property="editor"
                               class="mini-buttonedit" showClose="true"
                               oncloseclick="onSelectReportClick('电磁兼容指令证书')" allowInput="false"
                               onbuttonclick="selectReport('电磁兼容指令证书')"/>
                    </td>
                    <td>文件日期:</td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="dcStartDate" name="dcStartDate" class="mini-datepicker" allowInput="false"
                               style="width:100%;height:34px">
                    </td>
                    <td>有效期:</td>
                    <td id="dcEndDateTd">
                        <input id="dcEndDate" name="dcEndDate" class="mini-datepicker" allowInput="false"
                               style="width:100%;height:34px">
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<div id="selectReportWindow" title="选择证书" class="mini-window" style="width:750px;height:450px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <input name="zsInput" id="zsInput" class="mini-hidden"/>
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span class="text" style="width:auto">产品型号: </span><input
            class="mini-textbox" style="width: 120px" id="productModel" name="productModel"/>
        <span class="text" style="width:auto">文件编号: </span><input
            class="mini-textbox" style="width: 120px" id="reportCode" name="reportCode"/>
        <span class="text" style="width:auto">产品主管: </span><input
            class="mini-textbox" style="width: 120px" id="productManager" name="productManager"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchReport()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="reportListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             onrowdblclick="onRowReortDblClick"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons"
             url="${ctxPath}/wwrz/core/file/reportFiles.do?fileType=report">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div type="indexcolumn" align="center" headerAlign="center" width="30">序号</div>
                <div field="productModel" name="productModel" width="80" headerAlign="center" align="center"
                     allowSort="true">产品型号
                </div>
                <div field="reportCode" width="120" headerAlign="center" align="center" allowSort="true">文件编号</div>
                <div field="reportDate" width="80" headerAlign="center" align="center" allowSort="true">文件日期</div>
                <div field="reportValidity" width="80" headerAlign="center" align="center" allowSort="true">有效期</div>
                <div field="productManager" width="80" headerAlign="center" align="center" allowSort="true">产品主管</div>
                <div field="valid" width="80" headerAlign="center" align="center" allowSort="true"
                     renderer="onValidStatus">状态
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectReportOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectReportHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<div id="selectNoteWindow" title="选择需更新的台账" class="mini-window" style="width:750px;height:450px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <input name="tzInput" id="tzInput" class="mini-hidden"/>
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span class="text" style="width:auto">销售型号: </span><input
            class="mini-textbox" style="width: 120px" id="selectsaleModel" name="saleModel"/>
        <span class="text" style="width:auto">配置信息识别码: </span><input
            class="mini-textbox" style="width: 120px" id="selectonlyNum" name="onlyNum"/>
        <span class="text" style="width:auto">噪声指令证书编号: </span><input
            class="mini-textbox" style="width: 120px" id="selectzsNum" name="selectzsNum"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchNote()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="noteListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             onrowdblclick="onRowNoteDblClick"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons"
             url="${ctxPath}/wwrz/core/CE/queryInfoList.do?noteStatus=0">
            <div property="columns">
                <div type="checkcolumn" width="10"></div>
                <div type="indexcolumn" headerAlign="center" align="center" width="10">序号</div>
                <div field="onlyNum" width="15" headerAlign="center" align="center" allowSort="true">配置信息识别码</div>
                <div field="saleModel" width="15" headerAlign="center" align="center" allowSort="true">销售型号</div>
                <div field="zsNum" width="20" headerAlign="center" align="center" allowSort="true">噪声指令证书编号</div>
                <div field="zsStartDate" width="20" align="center" headerAlign="center" allowSort="true"
                     dateFormat="yyyy-MM-dd">证书签发日期
                </div>
                <div field="zsEndDate" width="20" align="center" headerAlign="center" allowSort="true"
                     dateFormat="yyyy-MM-dd">证书有效期
                </div>
                <div field="userName" headerAlign='center' align='center' width="20">创建人</div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectNoteOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectNoteHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var autoData = ${autoData};
    var jsUseCtxPath = "${ctxPath}";
    var ceinfoListGrid = mini.get("ceinfoListGrid");
    var ceinfoId = "${ceinfoId}";
    var formCeinfo = new mini.Form("#formCeinfo");
    var currentUserName = "${currentUserName}";
    var currentUserId = "${currentUserId}";
    var currentTime = "${currentTime}";
    var selectReportWindow = mini.get("selectReportWindow");
    var reportListGrid = mini.get("reportListGrid");
    var selectNoteWindow = mini.get("selectNoteWindow");
    var noteListGrid = mini.get("noteListGrid");


    function setCertAdress(e) {
        var certKey=mini.get("certName").getValue();
        var url =jsUseCtxPath+"/sys/core/sysDic/getByDicKey.do?dicKey=certName"
        $.ajaxSettings.async = false;
        $.get(
            url,
            function (jsonAry) {
                for(i=0;i<jsonAry.length;i++){
                    var json = jsonAry[i];
                    if(certKey==json.key){
                        mini.get("certAdress").setValue(json.descp);
                    }
                }
            });
        $.ajaxSettings.async = true;
    }

    //证书选择框
    function onValidStatus(e) {
        var record = e.record;
        var valid = record.valid;
        var _html = '';
        var color = '';
        var text = '';
        if (valid == '0') {
            color = '#2edfa3';
            text = "有效";
        } else if (valid == '1') {
            color = 'red';
            text = "作废";
        }
        _html = '<span style="color: ' + color + '">' + text + '</span>'
        return _html;
    }

    function selectReport(inputScene) {
        mini.get("zsInput").setValue(inputScene);
        selectReportWindow.show();
        searchReport();
    }

    //查询标准
    function searchReport() {
        var queryParam = [];
        //其他筛选条件
        var reportName = $.trim(mini.get("zsInput").getValue());
        if (reportName) {
            queryParam.push({name: "reportName", value: reportName});
        }
        var productModel = $.trim(mini.get("productModel").getValue());
        if (productModel) {
            queryParam.push({name: "productModel", value: productModel});
        }
        var reportCode = $.trim(mini.get("reportCode").getValue());
        if (reportCode) {
            queryParam.push({name: "reportCode", value: reportCode});
        }
        var productManager = $.trim(mini.get("productManager").getValue());
        if (productManager) {
            queryParam.push({name: "productManager", value: productManager});
        }
        queryParam.push({name: "valid", value: '0'});
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = reportListGrid.getPageIndex();
        data.pageSize = reportListGrid.getPageSize();
        data.sortField = reportListGrid.getSortField();
        data.sortOrder = reportListGrid.getSortOrder();
        //查询
        reportListGrid.load(data);
    }

    function onRowReortDblClick() {
        selectReportOK();
    }

    function selectReportOK() {
        var selectRow = reportListGrid.getSelected();
        var reportName = $.trim(mini.get("zsInput").getValue());
        if (reportName == '噪声指令证书') {
            mini.get("zsId").setValue(selectRow.id);
            mini.get("zsId").setText(selectRow.reportCode);
            mini.get("zsStartDate").setValue(selectRow.reportDate);
            mini.get("zsEndDate").setValue(selectRow.reportValidity);
        } else if (reportName == '机械指令证书') {
            mini.get("jxId").setValue(selectRow.id);
            mini.get("jxId").setText(selectRow.reportCode);
            mini.get("jxStartDate").setValue(selectRow.reportDate);
            mini.get("jxEndDate").setValue(selectRow.reportValidity);
        } else if (reportName == '电磁兼容指令证书') {
            mini.get("dcId").setValue(selectRow.id);
            mini.get("dcId").setText(selectRow.reportCode);
            mini.get("dcStartDate").setValue(selectRow.reportDate);
            mini.get("dcEndDate").setValue(selectRow.reportValidity);
        }
        selectReportHide();
    }

    function selectReportHide() {
        selectReportWindow.hide();
        mini.get("productModel").setValue('');
        mini.get("reportCode").setValue('');
        mini.get("productManager").setValue('');
    }

    function onSelectReportClick(e) {
        if (e == '噪声指令证书') {
            mini.get("zsId").setValue('');
            mini.get("zsId").setText('');
            mini.get("zsStartDate").setValue('');
            mini.get("zsEndDate").setValue('');
        } else if (e == '机械指令证书') {
            mini.get("jxId").setValue('');
            mini.get("jxId").setText('');
            mini.get("jxStartDate").setValue('');
            mini.get("jxEndDate").setValue('');
        } else if (e == '电磁兼容指令证书') {
            mini.get("dcId").setValue('');
            mini.get("dcId").setText('');
            mini.get("dcStartDate").setValue('');
            mini.get("dcEndDate").setValue('');
        }
    }


    //台账选择框
    function selectNote() {
        selectNoteWindow.show();
        searchNote();
    }

    //查询标准
    function searchNote() {
        var queryParam = [];
        //其他筛选条件
        var saleModel = $.trim(mini.get("selectsaleModel").getValue());
        if (saleModel) {
            queryParam.push({name: "saleModel", value: saleModel});
        }
        var onlyNum = $.trim(mini.get("selectonlyNum").getValue());
        if (onlyNum) {
            queryParam.push({name: "onlyNum", value: onlyNum});
        }
        var zsNum = $.trim(mini.get("selectzsNum").getValue());
        if (zsNum) {
            queryParam.push({name: "zsNum", value: zsNum});
        }
        queryParam.push({name: "noteStatus", value: "0"});
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = noteListGrid.getPageIndex();
        data.pageSize = noteListGrid.getPageSize();
        data.sortField = noteListGrid.getSortField();
        data.sortOrder = noteListGrid.getSortOrder();
        //查询
        noteListGrid.load(data);
    }

    function onRowNoteDblClick() {
        selectNoteOK();
    }

    function selectNoteOK() {
        var selectRow = noteListGrid.getSelected();
        if(mini.get("linkWwrz").getValue()){
            selectRow.linkWwrz = mini.get("linkWwrz").getValue();
        }
        formCeinfo.setData(selectRow);
        mini.get("replaceNo").setValue(selectRow.ceinfoId);
        mini.get("replaceNo").setText(selectRow.saleModel);
        mini.get("addupdate").setValue('更新');
        mini.get("ceinfoId").setValue('');
        selectNoteHide();
    }

    function selectNoteHide() {
        selectNoteWindow.hide();
        mini.get("productModel").setValue('');
        mini.get("reportCode").setValue('');
        mini.get("productManager").setValue('');
    }

    function onSelectNoteClick(e) {
        mini.get("replaceNo").setValue('');
        mini.get("replaceNo").setText('');
    }

    $(function () {
        if (ceinfoId) {
            var url = jsUseCtxPath + "/wwrz/core/CE/getCeinfoDetail.do";
            $.ajaxSettings.async = false;
            $.post(
                url,
                {ceinfoId: ceinfoId},
                function (json) {
                    formCeinfo.setData(json);
                });
            $.ajaxSettings.async = true;
        }
        mini.get("onlyNum").setEnabled(false);
        mini.get("explains").setEnabled(false);
        mini.get("replaceNo").setEnabled(false);
        mini.get("jxStartDate").setEnabled(false);
        mini.get("dcStartDate").setEnabled(false);
        mini.get("zsStartDate").setEnabled(false);
        mini.get("jxEndDate").setEnabled(false);
        mini.get("dcEndDate").setEnabled(false);
        mini.get("zsEndDate").setEnabled(false);
        typeChange();
        if (action == 'detail') {
            formCeinfo.setEnabled(false);
        }
        if (action == 'edit' || action == 'add') {
            $("#save").show();
        }
        if(action == 'auto'){
            $("#save").show();
            mini.get("saleModel").setEnabled(false);
            formCeinfo.setData(autoData);
        }
        expireChange();
    });


    function save() {
        var formValid = validCeinfo();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        var formData = new mini.Form("formCeinfo");
        var data = formData.getData();
        var json = mini.encode(data);
        $.ajax({
            url: jsUseCtxPath + '/wwrz/core/CE/saveCeinfo.do',
            type: 'POST',
            data: json,
            async: false,
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            if(action=="auto"){
                                CloseWindow();
                            }else {
                                var url = jsUseCtxPath + "/wwrz/core/CE/edit.do?ceinfoId=" + returnData.data + "&action=edit";
                                window.location.href = url;
                            }
                        }
                    });
                }
            }
        });
    }

    function validCeinfo() {
        var addupdate = $.trim(mini.get("addupdate").getValue())
        if (!addupdate) {
            return {"result": false, "message": "请选择台账类型"};
        }
        if (addupdate == '更新') {
            var replaceNo = $.trim(mini.get("replaceNo").getValue())
            if (!replaceNo) {
                return {"result": false, "message": "请选择需更新的台账"};
            }
        }
        var saleModel = $.trim(mini.get("saleModel").getValue())
        if (!saleModel) {
            return {"result": false, "message": "请填写销售型号"};
        }
        var designModel = $.trim(mini.get("designModel").getValue())
        if (!designModel) {
            return {"result": false, "message": "请填写设计型号"};
        }
        var materialNum = $.trim(mini.get("materialNum").getValue())
        if (!materialNum) {
            return {"result": false, "message": "请填写物料号"};
        }
        var emission = $.trim(mini.get("emission").getValue())
        if (!emission) {
            return {"result": false, "message": "请填写排放标准"};
        }
        var zsId = $.trim(mini.get("zsId").getValue())
        if (!zsId) {
            return {"result": false, "message": "请选择噪声指令证书"};
        }
        var certName = $.trim(mini.get("certName").getValue())
        if (!certName) {
            return {"result": false, "message": "请填写认证机构名称"};
        }
        var certAdress = $.trim(mini.get("certAdress").getValue())
        if (!certAdress) {
            return {"result": false, "message": "请填写认证机构地址"};
        }
        var noiseReal = $.trim(mini.get("noiseReal").getValue())
        if (!noiseReal) {
            return {"result": false, "message": "请填写噪声测量值"};
        }
        if (isNaN(noiseReal)) {
            return {"result": false, "message": "“噪声测量值”不是数字"};
        }
        var noisePlan = $.trim(mini.get("noisePlan").getValue())
        if (!noisePlan) {
            return {"result": false, "message": "请填写噪声承诺值"};
        }
        if (isNaN(noisePlan)) {
            return {"result": false, "message": "“噪声承诺值”不是数字"};
        }
        var enginePower = $.trim(mini.get("enginePower").getValue())
        if (!enginePower) {
            return {"result": false, "message": "请填写发动机功率/转速"};
        }
        // if (isNaN(enginePower)) {
        //     return {"result": false, "message": "“发动机功率/转速”不是数字"};
        // }
        var explains = $.trim(mini.get("explains").getValue())
        if (!explains) {
            return {"result": false, "message": "请填写配置说明"};
        }
        // var jxId=$.trim(mini.get("jxId").getValue())
        // if(!jxId) {
        //     return {"result": false, "message": "请选择机械指令证书"};
        // }
        // var dcId=$.trim(mini.get("dcId").getValue())
        // if(!dcId) {
        //     return {"result": false, "message": "请选择电磁兼容指令证书"};
        // }
        return {"result": true};
    }

    function typeChange() {
        var addupdate=mini.get("addupdate").getValue();
        if(addupdate=='更新') {
            mini.get("replaceNo").setEnabled(true);
            mini.get("onlyNum").setEnabled(false);
            mini.get("explains").setEnabled(false);
        } else{
            mini.get("explains").setEnabled(true);
            mini.get("replaceNo").setEnabled(false);
            mini.get("replaceNo").setValue('');
            mini.get("replaceNo").setText('');
        }
    }
    function expireChange() {
        var zsEndDate = new Date(mini.get("zsEndDate").getValue());
        var dcEndDate = new Date(mini.get("dcEndDate").getValue());
        var jxEndDate = new Date(mini.get("jxEndDate").getValue());
        var nowDate = new Date();
        if (zsEndDate.getTime()<nowDate.getTime()) {
            $("#zsEndDateTd").css("background-color", "rgb(226 53 53)");
        }
        if (dcEndDate.getTime()<nowDate.getTime()) {
            $("#dcEndDateTd").css("background-color", "rgb(226 53 53)");
        }
        if (jxEndDate.getTime()<nowDate.getTime()) {
            $("#jxEndDateTd").css("background-color", "rgb(226 53 53)");
            // $("#jxEndDate input").css("background-color", "rgb(226 53 53)");
        }
    }
</script>
</body>
</html>
