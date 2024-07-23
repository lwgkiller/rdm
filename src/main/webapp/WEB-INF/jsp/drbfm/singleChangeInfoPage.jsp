<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>变化点</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <style>
        .mini-grid-rows-view {
            background: white !important;
        }

        .mini-grid-cell-inner {
            line-height: 40px !important;
            padding: 0;
        }

        .mini-grid-cell-inner {
            font-size: 14px !important;
        }
    </style>
</head>
<body>
<div id="loading" class="loading" style="display:none;text-align:center;"><img
        src="${ctxPath}/styles/images/loading.gif"></div>
<div class="topToolBar">
    <div>
        <%--<a class="mini-button" id="relUp" plain="true" style="float: left;display: none"--%>
        <a class="mini-button" id="addChange" plain="true" style="float: left;display: none"
        onclick="addChange()">新增变化点</a>
        <%--&lt;%&ndash;<a class="mini-button" id="relDown" plain="true" style="float: left;display: none"&ndash;%&gt;--%>
        <%--<a class="mini-button" id="relDown" plain="true" style="float: left"--%>
        <%--onclick="relDown()">向下关联</a>--%>
    </div>
</div>
<div class="mini-fit" style="width: 100%; height: 85%;" id="content">
    <div id="changeListGrid" class="mini-datagrid" allowResize="false" style="height: 100%" autoload="true"
         idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false"
         multiSelect="false" showColumnsMenu="false" allowAlternating="true" showPager="false"
         url="${ctxPath}/drbfm/single/getSingleChangeList.do?partId=${singleId}"
         allowCellWrap="false" showVGridLines="true">
        <div property="columns">
            <%--<div type="checkcolumn" width="30px"></div>--%>

            <div field="designModelName" headerAlign="center" width="30px" align="center">对比机型
            </div>
            <div field="dimensionType" headerAlign="center" width="30px" align="center">维度类型
            </div>
            <div field="changeDimension" headerAlign="center" width="30px" align="center">变化维度
            </div>
            <div field="changeDetail" headerAlign="center" width="100px" align="center">变化点
            </div>
            <div field="requestDesc" headerAlign="center" width="100px" align="center">关联特性要求
            </div>
            <div field="sxmsName" headerAlign="center" width="100px" align="center">关联失效模式
            </div>
        </div>
    </div>
</div>


<div id="changeWindow" title="变化点关联" class="mini-window" style="width:660px;height:320px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-fit">
        <div class="topToolBar" style="float: right;">
            <div style="position: relative!important;">
                <a id="saveChange" class="mini-button" onclick="saveChange()">保存</a>
                <a id="closeChange" class="mini-button btn-red" onclick="closeChange()">关闭</a>
            </div>
        </div>
        <input id="functionId" name="id" class="mini-hidden"/>
        <table class="table-detail" cellspacing="1" cellpadding="0" style="width: 99%">
            <tr>
                <td style="width: 15%">对比机型：<span style="color:red">*</span></td>
                <td style="width: 85%;">
                    <input id ="designModelName" textname="designModelName" style="width:98%;"
                           class="mini-buttonedit" showClose="false"
                           allowInput="false"
                           onbuttonclick="selectDesignModel()"/>
                </td>
            </tr>
            <tr>
                <td style="width: 15%;">变化维度：<span style="color:red">*</span></td>
                <td style="width: 85%;">
                    <input id = "changeName" textname="changeName" style="width:98%;"
                           class="mini-buttonedit" showClose="false"
                           allowInput="false"
                           onbuttonclick="selectChangeDimension()"/>
                </td>
            </tr>
            <tr>
                <td style="width: 15%;">变化点描述：<span style="color:red">*</span></td>
                <td style="width: 85%;">
                    <input id="changeDesc" style="width:98%;" class="mini-textbox"/>
                </td>
            </tr>
            <tr>
                <td style="width: 15%">关联失效模式<span style="color:red">*</span></td>
                <td style="width: 35%;">
                    <input id="relSxId" style="width:98%;" class="mini-buttonedit" showClose="true"
                           oncloseclick="selectSxmsHide()"
                           name="relSxId" textname="relSxName" allowInput="false"
                           onbuttonclick="selectSxmsClick()"/>
                    <input id="relTxId" style="width:98%;" visible="fasle" class="mini-buttonedit" showClose="true"
                           oncloseclick="selectSxmsHide()"
                           name="relTxId" textname="relTxName" allowInput="false"
                           onbuttonclick="selectSxmsClick()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<%--关联变化维度弹窗--%>
<div id="selectDimensionWindow" title="选择变化维度" class="mini-window" style="width:1050px;height:450px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span class="text" style="width:auto">维度类型: </span><input
            class="mini-textbox" style="width: 120px" id="dimensionType" name="dimensionType"/>
        <span class="text" style="width:auto">变化维度: </span><input
            class="mini-textbox" style="width: 120px" id="changeDimension" name="changeDimension"/>

        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchChangeDimension()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="dimensionListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false"
             allowAlternating="true" showPager="false"
             autoload="true"
             url="${ctxPath}/drbfm/single/getChangeDimensionList.do">
            <div property="columns">
                <div type="checkcolumn" width="20px"></div>
                <div type="indexcolumn" align="center" headerAlign="center" width="40px">序号</div>
                <div field="dimensionType" headerAlign="center" width="200px" align="left">维度类型
                </div>
                <div field="changeDimension" headerAlign="center" width="200px" align="left">变化维度
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 40px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectDimensionOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消"
                           onclick="selectDimensionHide()"/>
                </td>
            </tr>
        </table>
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
                <%--<div field="status" width="60" headerAlign="center" align="center" allowSort="true"--%>
                <%--renderer="onStatusRenderer">状态--%>
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

<%-- 关联失效模式要求弹窗 --%>
<div id="selectSxmsWindow" title="选择失效模式" class="mini-window" style="width:1300px;height:700px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span class="text" style="width:auto">特性要求: </span>
        <input class="mini-textbox" style="width: 120px" id="requestDesc" name="requestDesc"/>
        <span class="text" style="width:auto">失效模式: </span>
        <input class="mini-textbox" style="width: 120px" id="sxmsName" name="sxmsName"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchSelectSxms()">查询</a>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="clearForm()">清空</a>
    </div>
    <div class="mini-fit">
        <div id="selectSxmsListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false"
             allowAlternating="true"
        <%--showPager="false"--%>
             multiSelect="true"
             showPager="true"
             sizeList="[10,50,100,200]" pageSize="50"
             autoload="true"
        >
            <div property="columns">
                <div type="checkcolumn" width="20px"></div>
                <div type="indexcolumn" align="center" headerAlign="center" width="40px">序号</div>
                <div field="requestDesc" name="requestDesc" headerAlign="center" width="220px" align="center">特性要求
                </div>
                <div field="sxmsName" headerAlign="center" width="220px" align="center">失效模式
                </div>
                <div field="relDimensionNames" name="relDimensionNames" headerAlign="center" width="220px" align="center">所属维度
                </div>
                <div field="rqId" name="rqId" headerAlign="center" width="220px" align="center" visible="fasle">要求id
                </div>
                <div field="structName" name="structName" headerAlign="center" width="220px" align="center">部件名称
                </div>
<%--                <div field="riskDesc" name="riskDesc" headerAlign="center" width="200px" align="left">存在风险<span style="color:red">*</span>--%>
<%--                </div>--%>
<%--                <div field="relFunctionDesc" name="relFunctionDesc" headerAlign="center" width="400px" align="left">关联功能描述--%>
<%--                </div>--%>
<%--                <div field="requestChanges" name="requestChanges" headerAlign="center" width="400px" align="left">较上一代或相似产品变化点--%>
<%--                </div>--%>
<%--                <div field="relEffect" name="relEffect" headerAlign="center" width="200px" align="left">变化点带来的与该要求相关的客观影响--%>
<%--                </div>--%>
<%--                <div field="compareToJP" name="compareToJP" headerAlign="center" width="200px" align="left">与竞品对比--%>
<%--                </div>--%>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 40px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectSxmsOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消"
                           onclick="selectSxmsHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var singleId = "${singleId}";
    var action = "${action}";
    var relType = "";
    var stageName = "${stageName}";
    var startOrEnd = "${startOrEnd}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var fileListGrid = mini.get("fileListGrid");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var changeListGrid = mini.get("changeListGrid");
    var selectSxmsListGrid = mini.get("selectSxmsListGrid");
    var sxmsListGrid = mini.get("sxmsListGrid");
    var sxmsWindow = mini.get("sxmsWindow");
    var selectSxmsWindow = mini.get("selectSxmsWindow");
    var changeWindow = mini.get("changeWindow");
    var selectDimensionWindow = mini.get("selectDimensionWindow");
    var spectrumWindow = mini.get("spectrumWindow");
    var spectrumListGrid = mini.get("spectrumListGrid");
    var selectDemandListGrid = mini.get("selectDemandListGrid");
    var dimensionListGrid = mini.get("dimensionListGrid");
    var requestListGrid = mini.get("requestListGrid");

    selectSxmsListGrid.on("load",function(){
        selectSxmsListGrid.mergeColumns(["requestDesc","relDimensionNames","structName"])
    });

    $(function () {
        if (action == 'task' && stageName == 'bjfzrfxfx') {
            mini.get("addChange").show();
        } else if (action=='edit') {
            mini.get("addChange").show();
        }

    });


    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var CREATE_BY_ = record.CREATE_BY_;
        var s = '';
        s += '<span  style="color:#2ca9f6;cursor: pointer" title="查看" onclick="showNetDetail(\'' + id + '\')">查看并关联</span>';
        // if (action == 'task' && CREATE_BY_ == currentUserId && stageName == 'bjfzrfxfx' && record.demandType == '自增') {
        //     s += '<span style="display: inline-block" class="separator"></span>';
        //     s += '<span  style="color:#2ca9f6;cursor: pointer" title="编辑" onclick="editDeptDemand(\'' + id + '\')">编辑</span>';
        // }
        return s;
    }

    function sxmsActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var CREATE_BY_ = record.CREATE_BY_;
        var s = '';
        // s += '<span  style="color:#2ca9f6;cursor: pointer" title="查看" onclick="showNetDetail(\'' + id + '\')">查看并关联</span>';
        if (action=='edit'||(action == 'task' && stageName == 'bjfzrfxfx')) {
        // if (action == 'task' && stageName == 'bjfzrfxfx') {
            // s += '<span style="display: inline-block" class="separator"></span>';
            s += '<span  style="color:#2ca9f6;cursor: pointer" title="删除" onclick="deleteSxms(\'' + id + '\')">删除</span>';
        } else {
            s += '&nbsp;&nbsp;&nbsp;<span  title="删除" style="color: silver" > 删除 </span>';
        }
        return s;
    }

    function addChange() {
        // mini.get("interfaceRequestStructId").setEnabled(true);
        // mini.get("relDimensionKeys").setEnabled(true);
        // mini.get("relDeptDemandId").setEnabled(true);
        // mini.get("functionDesc").setEnabled(true);
        // mini.get("saveFunction").show();
        changeWindow.show();
    }

    function closeChange() {
        mini.get("designModelName").setValue('');
        mini.get("designModelName").setText('');
        mini.get("changeName").setValue('');
        mini.get("changeName").setText('');
        mini.get("changeDesc").setValue('');
        mini.get("relTxId").setValue('');
        mini.get("relTxId").setText('');
        mini.get("relSxId").setValue('');
        mini.get("relSxId").setText('');
        changeWindow.hide();
    }

    function validChange() {
        var designModelName = $.trim(mini.get("designModelName").getValue())
        if (!designModelName) {
            return {"result": false, "message": "请选择对比机型"};
        }
        var changeName = $.trim(mini.get("changeName").getValue())
        if (!changeName) {
            return {"result": false, "message": "请选择变化维度"};
        }
        var changeDesc = $.trim(mini.get("changeDesc").getValue())
        if (!changeDesc) {
            return {"result": false, "message": "请填写变化点"};
        }
        var relSxId = $.trim(mini.get("relSxId").getValue())
        if (!relSxId) {
            return {"result": false, "message": "请选择关联的失效模式！"};
        }
        return {"result": true};
    }
    function saveChange() {
        var formValid = validChange();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        var designModel = mini.get("designModelName").getValue();
        var changeName = mini.get("changeName").getValue();
        var changeDesc = mini.get("changeDesc").getValue();
        var relTxId = mini.get("relTxId").getValue();
        var relSxId = mini.get("relSxId").getValue();
        var data = {
            changeDimension: changeName, designModel: designModel, changeDetail: changeDesc,
            partId: singleId, relTxId: relTxId,relSxId:relSxId
        };
        var json = mini.encode(data);
        $.ajax({
            url: jsUseCtxPath + '/drbfm/single/saveChange.do',
            type: 'POST',
            data: json,
            async: false,
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            changeListGrid.reload();
                            closeChange();
                        }
                    });
                }
            }
        });
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
        } else {
            var selectModel = window.parent.selectRowNames;
            if (selectModel) {
                queryParam.push({name: "selectModel", value: selectModel});
            }
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
        var selectRow = spectrumListGrid.getSelected();
        if (!selectRow) {
            mini.alert("请选择产品型号！");
            return;
        }
        // var objrow = riskGrid.getSelected();
        // riskGrid.updateRow(objrow,
        //     {
        //         designModel: selectRow.id,
        //         designModelName: selectRow.designModel
        //
        //     });
        mini.get("designModelName").setValue(selectRow.id);
        mini.get("designModelName").setText(selectRow.designModel);
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

    function selectChangeDimension() {
        selectDimensionWindow.show();
        searchChangeDimension();
    }

    /**
     * 变化维度查询
     */
    function searchChangeDimension() {
        var queryParam = [];
        //其他筛选条件
        var dimensionType = $.trim(mini.get("dimensionType").getValue());
        if (dimensionType) {
            queryParam.push({name: "dimensionType", value: dimensionType});
        }
        var changeDimension = $.trim(mini.get("changeDimension").getValue());
        if (changeDimension) {
            queryParam.push({name: "changeDimension", value: changeDimension});
        }
        // queryParam.push({name: "instStatus", value: "SUCCESS_END"});

        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = dimensionListGrid.getPageIndex();
        data.pageSize = dimensionListGrid.getPageSize();
        data.sortField = dimensionListGrid.getSortField();
        data.sortOrder = dimensionListGrid.getSortOrder();
        //查询
        dimensionListGrid.load(data);
    }


    /**
     * 变化维度确定按钮
     */
    function selectDimensionOK() {
        var selectRow = dimensionListGrid.getSelected();
        if (!selectRow) {
            mini.alert("请选择变化维度！");
            return;
        }
        // var objrow = riskGrid.getSelected();
        // riskGrid.updateRow(objrow,
        //     {
        //         changeDimension: selectRow.changeDimension
        //     });
        mini.get("changeName").setValue(selectRow.changeDimension);
        mini.get("changeName").setText(selectRow.changeDimension);
        selectDimensionHide();
    }

    /**
     * 变化维度关闭按钮
     */
    function selectDimensionHide() {
        selectDimensionWindow.hide();
        mini.get("dimensionType").setValue('');
        mini.get("dimensionType").setText('');
        mini.get("changeDimension").setValue('');
        mini.get("changeDimension").setText('');
    }


    function selectSxmsOK() {
        var rows = selectSxmsListGrid.getSelecteds();
        var rowIds = [];
        var rowNames = [];
        var rowYqIds = [];
        var rowYqNames = [];
        if (!rows) {
            mini.alert("请选择关联失效模式！");
            return;
        }
        for (var i = 0, l = rows.length; i < l; i++) {
            var r = rows[i];
            rowIds.push(r.id);
            rowNames.push(r.sxmsName);
            rowYqIds.push(r.yqId);
            rowYqNames.push(r.requestDesc)
        }
        mini.get("relSxId").setValue(rowIds.join(","));
        mini.get("relSxId").setText(rowNames.join(","));
        mini.get("relTxId").setValue(rowYqIds.join(","));
        mini.get("relTxId").setText(rowYqNames.join(","));
        selectSxmsHide();
    }

    function selectSxmsClick() {
        selectSxmsWindow.show();
        searchSelectSxms();
    }

    function searchSelectSxms() {
        var url = "${ctxPath}/drbfm/single/getRequestAndSxmsList.do?partId=${singleId}";
        selectSxmsListGrid.setUrl(url);
        var queryParam = [];
        //其他筛选条件
        var sxmsName = $.trim(mini.get("sxmsName").getValue());
        if (sxmsName) {
            queryParam.push({name: "sxmsName", value: sxmsName});
        }
        var requestDesc = $.trim(mini.get("requestDesc").getValue());
        if (requestDesc) {
            queryParam.push({name: "requestDesc", value: requestDesc});
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        //查询
        selectSxmsListGrid.load(data);
    }

    function clearForm(){
        mini.get("sxmsName").setValue("");
        mini.get("requestDesc").setValue("");
        searchSelectSxms();
    }

    function selectSxmsHide() {
        selectSxmsWindow.hide();
        mini.get("sxmsName").setValue('');
    }

</script>
</body>
</html>
