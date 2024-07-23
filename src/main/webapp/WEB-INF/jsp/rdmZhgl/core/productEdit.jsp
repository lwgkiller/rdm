<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>新品试制信息</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/productEdit.js?version=${static_res_version}"
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
        <form id="productForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
                <caption>
                    新品试制信息
                </caption>
                <tbody>
                <tr class="firstRow displayTr">
                    <td align="center"></td>
                    <td align="left"></td>
                    <td align="center"></td>
                    <td align="left"></td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        产品类型<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="productType" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="产品类型："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=XPSZ-CPLX"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>

                    <td style="  width: 20%;text-align: center">产品设计型号（关联型谱）<span style="color: #ff0000">*</span>：</td>
                    <td style="width: 20%">
                        <input id="productId" style="width:98%;" class="mini-buttonedit" showClose="true"
                               oncloseclick="onRelModelCloseClick()" required="true"
                               name="productId" textname="productModelReal" allowInput="false"
                               onbuttonclick="selectModelClick()"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        设计型号名称<span style="color: #ff0000">*<br>（默认由型谱带出，暂无时<br>可手动填写临时名称）</span>：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="productModel" name="productModel" required="true" class="mini-textbox rxc"
                               style="width:100%;height:34px">
                    </td>
                    <td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
                        任务来源及进度要求：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="taskFrom" class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
                        预算产品型号：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="budgetProductModel" class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        上报集团，样机下线：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="downLine" name="downLine" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="样机下线："
                               length="50"
                               only_read="false" required="false" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        是否立项：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="isLx" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="是否立项："
                               length="50"
                               only_read="false" required="false" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        项目名称：
                    </td>
                    <td colspan="1">
                        <input name="projectName" class="mini-textbox rxc"  readonly style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        是否倒排：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="isInverted" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="是否倒排："
                               length="50"
                               only_read="false" required="false" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        重要度：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="important" name="important" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="计划级别："
                               length="50"
                               only_read="false" required="false" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=ZYD"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        产品主管<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="productLeader" textname="productLeaderName" class="mini-user rxc" required="true"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="姓名"
                               mainfield="no" single="true"/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        是否为新销售型号：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="isNewModel" name="isNewModel" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="样机状态："
                               length="50"
                               only_read="false" required="false" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                </tr>
                <tr>
                    <td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
                        产业化状态：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="productStatus" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="产业化状态："
                               length="50"
                               only_read="false" required="false" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=XPSZ-CYHZT"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        样机状态：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="modelStatus" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="样机状态："
                               length="50"
                               only_read="false" required="false" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YJZT"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        样机或小批量是否需要模具：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="isNeedModel" name="isNeedModel" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="样机或小批量是否需要模具："
                               length="50"
                               only_read="false" required="false" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
                        项目负责人：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="responseMan" textname="responseManName" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" label="姓名"
                               mainfield="no" single="true"/>
                    </td>
                <tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        计划管理人员<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="planAdmin" textname="planAdminName" class="mini-user rxc" required="true"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="姓名"
                               mainfield="no" single="true"/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        是否结束：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="finished" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="是否结束："
                               length="50"
                               only_read="false" required="false" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        所属年份<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="belongYear" name="belongYear" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px"  label="年度："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
                               textField="text" valueField="value" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getYearList.do"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        变更原因：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="changeReason" name="changeReason" class="mini-textbox rxc"
                               style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        备注：
                    </td>
                    <td align="center" colspan="3" rowspan="1">
                        <input name="remark" class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        产品主要配置：
                    </td>
                    <td align="center" colspan="3" rowspan="1">
                        <input name="mainConfig" class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        计划变更：
                    </td>
                    <td align="center" colspan="3" rowspan="1">
                        <input name="planChange" class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>

                </tbody>
            </table>
        </form>
    </div>
    <div id="selectProjectWindow" title="选择立项项目" class="mini-window" style="width:1000px;height:450px;"
         showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
        <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
             borderStyle="border-left:0;border-top:0;border-right:0;">
            <span style="font-size: 14px;color: #777">项目名称: </span>
            <input class="mini-textbox" width="130" id="projectName" style="margin-right: 15px"/>
            <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchProject()">查询</a>
        </div>
        <div class="mini-fit">
            <div id="projectListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
                 idField="id"  showColumnsMenu="false" sizeList="[5,10,20,50,100,200]" pageSize="5"
                 allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/rdmZhgl/core/ndkfjh/planDetail/listProject.do">
                <div property="columns">
                    <div type="checkcolumn" width="30"></div>
                    <div field="projectName" width="200" headerAlign="center" align="left" allowSort="false">项目名称</div>
                    <div field="startDate" width="80" headerAlign="center" align="center" allowSort="false">项目开始时间</div>
                    <div field="endDate" width="80" headerAlign="center" align="center" allowSort="false">项目结束时间</div>
                    <div field="currentStage" width="80" headerAlign="center" align="center" allowSort="false">当前阶段</div>
                    <div field="stageFinishDate" width="120" headerAlign="center" align="center" allowSort="false">当前阶段要求完成时间</div>
                </div>
            </div>
        </div>
        <div property="footer" style="padding:5px;height: 35px">
            <table style="width:100%;height: 100%">
                <tr>
                    <td style="width:120px;text-align:center;">
                        <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectProjectOK()"/>
                        <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectProjectHide()"/>
                    </td>
                </tr>
            </table>
        </div>
    </div>

    <div id="selectModelWindow" title="选择设计型号" class="mini-window" style="width:900px;height:550px;"
         showModal="true" showFooter="true" allowResize="true" showCloseButton="true">
        <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
             borderStyle="border-left:0;border-top:0;border-right:0;">
            <span class="text" style="width:auto">设计型号: </span><input
                class="mini-textbox" style="width: 120px" id="designModel" name="designModel"/>
            <span class="text" style="width:auto">销售型号: </span><input
                class="mini-textbox" style="width: 120px" id="saleModel" name="saleModel"/>
            <span class="text" style="width:auto">产品所: </span><input
                class="mini-textbox" style="width: 120px" id="departName" name="departName"/>
            <span class="text" style="width:auto">产品主管: </span><input
                class="mini-textbox" style="width: 120px" id="productManagerName" name="productManagerName"/>
            <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchModel()">查询</a>
        </div>
        <div class="mini-fit">
            <div id="selectModelListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
                 idField="id" showColumnsMenu="false"
                 allowAlternating="true" showPager="true" multiSelect="false"
                 url="${ctxPath}/world/core/productSpectrum/dataListQuery.do?">
                <div property="columns">
                    <div type="checkcolumn" width="20"></div>
                    <div field="designModel" width="150" headerAlign="center" align="center" renderer="render"
                         allowSort="true">
                        设计型号
                    </div>
                    <div field="departName" width="120" headerAlign="center" align="center" renderer="render"
                         allowSort="true">
                        产品所
                    </div>
                    <div field="productManagerName" width="80" headerAlign="center" align="center" renderer="render"
                         allowSort="true">
                        产品主管
                    </div>
                    <div field="saleModel" width="120" headerAlign="center" align="center" renderer="render"
                         allowSort="true">
                        销售型号
                    </div>
                </div>
            </div>
        </div>
        <div property="footer" style="padding:5px;height: 35px">
            <table style="width:100%;height: 100%">
                <tr>
                    <td style="width:120px;text-align:center;">
                        <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectModelOK()"/>
                        <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectModelHide()"/>
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
    var productForm = new mini.Form("#productForm");
    var permission = ${permission};
    var selectProjectWindow=mini.get("selectProjectWindow");
    var projectListGrid=mini.get("projectListGrid");
    var selectModelWindow = mini.get("selectModelWindow");
    var selectModelListGrid = mini.get("selectModelListGrid");

    if (!permission) {
        productForm.setEnabled(false);
        mini.get('isNewModel').setEnabled(true);
        mini.get('changeReason').setEnabled(true);
    }

    function selectModelClick() {
        selectModelWindow.show();
        searchModel();
    }

    function searchModel() {
        var queryParam = [];
        //其他筛选条件
        var designModel = $.trim(mini.get("designModel").getValue());
        if (designModel) {
            queryParam.push({name: "designModel", value: designModel});
        }
        var departName = $.trim(mini.get("departName").getValue());
        if (departName) {
            queryParam.push({name: "departName", value: departName});
        }
        var productManagerName = $.trim(mini.get("productManagerName").getValue());
        if (productManagerName) {
            queryParam.push({name: "productManagerName", value: productManagerName});
        }
        var saleModel = $.trim(mini.get("saleModel").getValue());
        if (saleModel) {
            queryParam.push({name: "saleModel", value: saleModel});
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        //查询
        selectModelListGrid.load(data);
    }

    function selectModelOK() {
        var rowSelected = selectModelListGrid.getSelected();
        if(!rowSelected) {
            mini.alert("请选择一行数据！");
            return;
        }
        mini.get("productId").setValue(rowSelected.id);
        mini.get("productId").setText(rowSelected.designModel);
        mini.get("productModel").setValue(rowSelected.designModel);
        selectModelHide();
        mini.get("productModel").setEnabled(false);
    }

    function selectModelHide() {
        selectModelWindow.hide();
        mini.get("saleModel").setValue('');
        mini.get("productManagerName").setValue('');
        mini.get("departName").setValue('');
        mini.get("designModel").setValue('');
        selectModelListGrid.clearSelect();
    }

    function onRelModelCloseClick() {
        var productId = mini.get("productId").getValue();
        if(productId) {
            mini.get("productId").setValue('');
            mini.get("productId").setText('');
            mini.get("productModel").setValue('');
            mini.get("productModel").setEnabled(true);
        }
    }
</script>
</body>
</html>
