<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>工艺反馈信息说明细节编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveBtn" class="mini-button" onclick="savecs()">保存</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit" style="height: 800px">
    <div class="form-container" style="margin: 0 auto; width: 100%;height: 100%">
        <form id="formMaterielTypeFDetail" method="post">
            <input id="detailId" name="detailId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold;font-size: 20px !important;">
                    外购件信息记录
                </caption>
                <tr>
                    <td>供应商代码：</td>
                    <td>
                        <input id="supplier" name="supplier" class="mini-textbox"   style="width:98%;"/>
                    </td>
                    <td>物料号码：
                        <image src="${ctxPath}/styles/images/question2.png"
                               style="cursor: pointer;vertical-align: middle"
                               title="若物料已在RDM中扩充,输入物料号后点击回车将自动填入物料信息"/></td>
                    <td>
                        <input id="wlhm" name="wlhm" class="mini-textbox" onvaluechanged="wlhmChange()" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td>物料描述：</td>
                    <td>
                        <input id="wlms" name="wlms" class="mini-textbox"  style="width:98%;"/>
                    </td>
                    <td>工厂代码：</td>
                    <td>
                        <input id="gc" name="gc" class="mini-textbox"  style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td>采购组织：</td>
                    <td>
                        <input id="xszz" name="xszz" class="mini-textbox"  style="width:98%;"/>
                    </td>
                    <td>采购信息记录类型：</td>
                    <td>
                        <input  id="materType" name="materType" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[ {'key':'0','value':'0'},{'key' : '1','value' : '1'}
                               ,{'key' : '2','value' : '2'},{'key' : '3','value' : '3'},{'key' : 'P','value' : 'P'}]"
                        />
                    </td>
                </tr>
                <tr>
                    <td>制造厂商：</td>
                    <td>
                        <input id="productor" name="productor" class="mini-textbox" readonly style="width:98%;"/>
                    </td>
                    <td>计划交货时间：</td>
                    <td>
                        <input id="jhjhsj" name="jhjhsj" class="mini-textbox"  style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td>采购组：</td>
                    <td>
                        <input id="cgz" name="cgz" class="mini-textbox"  style="width:98%;"/>
                    </td>
                    <td>极限过度发货：</td>
                    <td>
                        <input id="limiSend" name="limiSend" class="mini-textbox" readonly style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td>净价：</td>
                    <td>
                        <input id="jg" name="jg" class="mini-textbox"  style="width:98%;"/>
                    </td>
                    <td>币种：</td>
                    <td>
                        <input id="currency" name="currency" class="mini-textbox" readonly style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td>数量：</td>
                    <td>
                        <input id="jgdw" name="jgdw" class="mini-textbox"  style="width:98%;"/>
                    </td>
                    <td>单位：</td>
                    <td>
                        <input id="dw" name="dw" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td>无限制交货：</td>
                    <td>
                        <input id="infiniteSend" name="infiniteSend" class="mini-textbox" readonly style="width:98%;"/>
                    </td>
                    <td>税码：</td>
                    <td>
                        <input id="taxCode" name="taxCode" class="mini-textbox" readonly style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td>是否估计价格：</td>
                    <td>
                        <input id="estimatePrice" name="estimatePrice" class="mini-textbox" readonly style="width:98%;"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<%--<div id="selectWlWindow" title="选择需更新的台账" class="mini-window" style="width:750px;height:450px;"--%>
     <%--showModal="true" showFooter="true" allowResize="true" showCloseButton="false">--%>
    <%--<div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"--%>
         <%--borderStyle="border-left:0;border-top:0;border-right:0;">--%>
        <%--<span class="text" style="width:auto">物料号码: </span><input--%>
            <%--class="mini-textbox" style="width: 120px" id="selectWlhm" name="selectWlhm"/>--%>
        <%--<span class="text" style="width:auto">配置信息识别码: </span><input--%>
            <%--class="mini-textbox" style="width: 120px" id="selectonlyNum" name="onlyNum"/>--%>
        <%--<a class="mini-button" iconCls="icon-search" plain="true" onclick="searchWl()">查询</a>--%>
    <%--</div>--%>
    <%--<div class="mini-fit">--%>
        <%--<div id="wlListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"--%>
             <%--onrowdblclick="onRowWlDblClick"--%>
             <%--idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"--%>
             <%--allowAlternating="true" pagerButtons="#pagerButtons"--%>
             <%--url="${ctxPath}/wwrz/core/CE/queryInfoList.do?wlStatus=0">--%>
            <%--<div property="columns">--%>
                <%--<div type="checkcolumn" width="20"></div>--%>
                <%--<div field="wlhm" width="50px" headerAlign="center" align="center">物料号码</div>--%>
                <%--<div field="wlms" headerAlign="center" align="center" width="70px">物料描述</div>--%>
                <%--<div field="gc" width="40px" headerAlign="center" align="center">工厂代码</div>--%>
                <%--<div field="xszz" width="40px" headerAlign="center" align="center">采购组织</div>--%>
                <%--<div field="jhjhsj" width="50px" headerAlign="center" align="center">计划交货时间</div>--%>
                <%--<div field="cgz" width="30px" headerAlign="center" align="center">采购组</div>--%>
                <%--<div field="jg" headerAlign='center' align='center' width="40">净价</div>--%>
                <%--<div field="jgdw" width="30px" headerAlign="center" align="center">数量</div>--%>
                <%--<div field="dw" headerAlign='center' align='center' width="30">单位</div>--%>
            <%--</div>--%>
        <%--</div>--%>
    <%--</div>--%>
    <%--<div property="footer" style="padding:5px;height: 35px">--%>
        <%--<table style="width:100%;height: 100%">--%>
            <%--<tr>--%>
                <%--<td style="width:120px;text-align:center;">--%>
                    <%--<input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectWlOK()"/>--%>
                    <%--<input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectWlHide()"/>--%>
                <%--</td>--%>
            <%--</tr>--%>
        <%--</table>--%>
    <%--</div>--%>
<%--</div>--%>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var formMaterielTypeFDetail = new mini.Form("#formMaterielTypeFDetail");
    var detailId = "${detailId}";
    var belongId = "${belongId}";
    var action = "${action}";
    var currentUserName = "${currentUserName}";
    var currentUserId = "${currentUserId}";
    var currentTime = "${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";
    // var selectWlWindow = mini.get("selectWlWindow");
    // var wlListGrid = mini.get("wlListGrid");

    function wlhmChange() {
        var wlhm = $.trim(mini.get("wlhm").getValue());
        var url = jsUseCtxPath + "/materielTypeF/core/setWlinfo.do?wlhm=" + wlhm;
        $.get(
            url,
            function (json) {
                if(json.id){
                    mini.get("wlhm").setValue(json.wlhm);
                    mini.get("wlms").setValue(json.wlms);
                    mini.get("gc").setValue(json.gc);
                    mini.get("xszz").setValue(json.xszz);
                    mini.get("jhjhsj").setValue(json.jhjhsj);
                    mini.get("cgz").setValue(json.cgz);
                    mini.get("jg").setValue(json.jg);
                    mini.get("jgdw").setValue(json.jgdw);
                    mini.get("dw").setValue(json.dw);
                }
            });
    }

    $(function () {
        if (detailId) {
            var url = jsUseCtxPath + "/materielTypeF/core/getMaterielTypeFDetail.do";
            $.post(
                url,
                {detailId: detailId},
                function (json) {
                    formMaterielTypeFDetail.setData(json);
                });
        }else {
            mini.get("materType").setValue("0");
            mini.get("currency").setValue("CNY");
            mini.get("taxCode").setValue("J5");
            mini.get("estimatePrice").setValue("X");
        }
        mini.get("materType").setEnabled(false);
        //变更入口
        if (action == "detail") {
            formMaterielTypeFDetail.setEnabled(false);
            mini.get("addFile").setEnabled(false);
        }
    });



    function validMaterielTypeF() {
        var supplier = $.trim(mini.get("supplier").getValue());
        if (!supplier) {
            return {"result": false, "message": "请填写供应商代码"};
        }
        var wlhm = $.trim(mini.get("wlhm").getValue());
        if (!wlhm) {
            return {"result": false, "message": "请填写物料号码"};
        }
        var wlms = $.trim(mini.get("wlms").getValue());
        if (!wlms) {
            return {"result": false, "message": "请填写物料描述"};
        }
        var gc = $.trim(mini.get("gc").getValue());
        if (!gc) {
            return {"result": false, "message": "请填写工厂代码"};
        }
        var xszz = $.trim(mini.get("xszz").getValue());
        if (!xszz) {
            return {"result": false, "message": "请填写采购组织"};
        }
        var materType = $.trim(mini.get("materType").getValue());
        if (!materType) {
            return {"result": false, "message": "请填写采购信息记录类型"};
        }
        var jhjhsj = $.trim(mini.get("jhjhsj").getValue());
        if (!jhjhsj) {
            return {"result": false, "message": "请填写计划交货时间"};
        }
        var cgz = $.trim(mini.get("cgz").getValue());
        if (!cgz) {
            return {"result": false, "message": "请填写采购组"};
        }
        var jg = $.trim(mini.get("jg").getValue());
        if (!jg) {
            return {"result": false, "message": "请填写净价"};
        }
        var currency = $.trim(mini.get("currency").getValue());
        if (!currency) {
            return {"result": false, "message": "请填写币种"};
        }
        var taxCode = $.trim(mini.get("taxCode").getValue());
        if (!taxCode) {
            return {"result": false, "message": "请填写税码"};
        }
        var jgdw = $.trim(mini.get("jgdw").getValue());
        if (!jgdw) {
            return {"result": false, "message": "请填写数量"};
        }
        var dw = $.trim(mini.get("dw").getValue());
        if (!dw) {
            return {"result": false, "message": "请填写单位"};
        }
        var estimatePrice = $.trim(mini.get("estimatePrice").getValue());
        if (!estimatePrice) {
            return {"result": false, "message": "请填写是否估计价格"};
        }
        return {"result": true};
    }

    function savecs() {
        // var formValid = validMaterielTypeF();
        // if (!formValid.result) {
        //     mini.alert(formValid.message);
        //     return;
        // }
        var formData = new mini.Form("formMaterielTypeFDetail");
        var data = formData.getData();
        var json = mini.encode(data);
        $.ajax({
            url: jsUseCtxPath + '/materielTypeF/core/saveMaterielTypeFDetail.do?belongId='+belongId,
            type: 'POST',
            data: json,
            async: false,
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    mini.alert(data.message, "提示消息", function (action) {
                        if (action == 'ok') {
                            CloseWindow();
                        }
                    });
                }
            }
        });
    }


    // function selectWl() {
    //     selectWlWindow.show();
    //     searchWl();
    // }
    //
    // //查询标准
    // function searchWl() {
    //     var queryParam = [];
    //     //其他筛选条件
    //     var saleModel = $.trim(mini.get("selectsaleModel").getValue());
    //     if (saleModel) {
    //         queryParam.push({name: "saleModel", value: saleModel});
    //     }
    //     var onlyNum = $.trim(mini.get("selectonlyNum").getValue());
    //     if (onlyNum) {
    //         queryParam.push({name: "onlyNum", value: onlyNum});
    //     }
    //     var zsNum = $.trim(mini.get("selectzsNum").getValue());
    //     if (zsNum) {
    //         queryParam.push({name: "zsNum", value: zsNum});
    //     }
    //     queryParam.push({name: "wlStatus", value: "0"});
    //     var data = {};
    //     data.filter = mini.encode(queryParam);
    //     data.pageIndex = wlListGrid.getPageIndex();
    //     data.pageSize = wlListGrid.getPageSize();
    //     data.sortField = wlListGrid.getSortField();
    //     data.sortOrder = wlListGrid.getSortOrder();
    //     //查询
    //     wlListGrid.load(data);
    // }
    //
    // function onRowWlDblClick() {
    //     selectWlOK();
    // }
    //
    // function selectWlOK() {
    //     var selectRow = wlListGrid.getSelected();
    //     formCeinfo.setData(selectRow);
    //     mini.get("replaceNo").setValue(selectRow.ceinfoId);
    //     mini.get("replaceNo").setText(selectRow.saleModel);
    //     mini.get("addupdate").setValue('更新');
    //     mini.get("ceinfoId").setValue('');
    //     selectWlHide();
    // }
    //
    // function selectWlHide() {
    //     selectWlWindow.hide();
    //     mini.get("productModel").setValue('');
    //     mini.get("reportCode").setValue('');
    //     mini.get("productManager").setValue('');
    // }
    //
    // function onSelectWlClick(e) {
    //     mini.get("replaceNo").setValue('');
    //     mini.get("replaceNo").setText('');
    // }
</script>
</body>
</html>
