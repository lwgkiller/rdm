<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>部件验证项目基本信息</title>
    <%@include file="/commons/edit.jsp" %>
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
<div id="topToolBar" class="topToolBar" style="display: none">
    <div>
        <a class="mini-button" id="saveBaseInfo" plain="true" style="float: right"
           onclick="saveBaseInfo()">保存基本信息</a>
        <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle;float: right;">
            （注意：本页面操作后，请先点击“保存基本信息”按钮，再进行标签切换或下一步按钮的点击）
        </p>
    </div>
</div>

<div class="mini-fit" style="height: 95%;">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="verifyPlanForm" method="post" style="width: 100%">
            <input class="mini-hidden" id="id" name="id"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 8%">部件：<span style="color:red">*</span></td>
                    <td>
                        <input id="structType" name="structType" class="mini-combobox" style="width:98%;"
                               textField="structTypeName" valueField="structTypeId" emptyText="请选择..."
                               required="false" allowInput="false"
                               data="[{'structTypeName':'整机','structTypeId':'整机'},
                                    {'structTypeName':'新零部件','structTypeId':'新零部件'},
                                   {'structTypeName':'借用件','structTypeId':'借用件'}]"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 8%">校对室主任<span style="color:red">*</span></td>
                    <td style="width: 150px">
                        <input id="checkUserId" name="checkUserId" textname="checkUserName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label=""
                               length="50" maxlength="50"
                               mainfield="no" single="true"/>
                    </td>

                </tr>
                <tr>
                    <td style="text-align: center;width: 8%">挖掘机械研究院<br>内部校对会签人</td>
                    <td style="width: 150px">
                        <input id="innerCheckUserIds" name="innerCheckUserIds" textname="innerCheckUserNames" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label=""
                               length="500" maxlength="500"
                               mainfield="no" single="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 8%">外部门审核会签人<br>(工艺、质量)</td>
                    <td style="width: 150px">
                        <input id="outSHUserIds" name="outSHUserIds" textname="outSHUserNames" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label=""
                               length="500" maxlength="500"
                               mainfield="no" single="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 8%">风险分析件类型：</td>
                    <td>
                        <input id="riskAnalysisType" name="riskAnalysisType" class="mini-combobox" style="width:98%;"
                               textField="key" valueField="value" emptyText="请选择..."
                               required="false" allowInput="false"
                               data="[{'key':'风险分析件','value':'fxfxj'},
                                    {'key':'外购黑匣子件/底层自制件/整机','value':'wghxzj'},]"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 8%">变化点对比机型：</td>
                    <td>
                        <%--<input id="campareModel" name="campareModel" class="mini-textbox" style="width:98%;"/>--%>
                        <%--<input textname="campareModel" style="width:98%;"--%>
                               <%--class="mini-buttonedit" showClose="true"--%>
                               <%--allowInput="false"--%>
                               <%--onbuttonclick="selectModelClick()"/>--%>
                            <input id="model" style="width:98%;" class="mini-buttonedit" showClose="true"
                                   oncloseclick="onRelModelCloseClick()"
                                   name="modelId" textname="modelName" allowInput="false"
                                   onbuttonclick="selectModelClick()"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 8%">项目流水号</td>
                    <td style="width: 300px">
                        <input id="singleNumber" name="singleNumber" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 8%">部件名称</td>
                    <td style="width: 300px">
                        <input class="mini-hidden" id="structId" name="structId"/>
                        <input id="structName" name="structName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 8%">部件编号</td>
                    <td style="width: 300px">
                        <input id="structNumber" name="structNumber" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 8%">部件责任人</td>
                    <td style="width: 300px">
                        <input id="analyseUserName" name="analyseUserName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;">上一层级部件</td>
                    <td style="width: 150px">
                        <input id="parentStructName" name="parentStructName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;">设计型号</td>
                    <td style="width: 150px">
                        <input id="jixing" name="jixing" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;">所属总项目</td>
                    <td style="width: 300px">
                        <input id="analyseName" name="analyseName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;">FMEA类型</td>
                    <td style="width: 300px">
                        <input id="femaType" name="femaType" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key"
                               emptyText="请选择FMEA类型..."
                               required="false" allowInput="false"
                               multiSelect="false"
                               enabled="false"
                               data="[{'key' : 'product','value' : '产品FMEA'}
                                       ,{'key' : 'base','value' : '基础FMEA'}
                                       ]"
                        />
                    </td>
                </tr>



               <%-- <tr>
                    <td style="text-align: center;">关联的科技项目：</td>
                    <td>
                        <input id="projectName" name="projectId" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>--%>

            </table>
        </form>
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
             idField="id" showColumnsMenu="false" onload="onGridLoad"
             allowAlternating="true" showPager="true" multiSelect="true"
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



<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var singleId = "${singleId}";
    var action = "${action}";
    var stageName = "${stageName}";
    var verifyPlanForm = new mini.Form("#verifyPlanForm");
    var currentUserId = "${currentUserId}";
    var selectModelWindow = mini.get("selectModelWindow");
    var selectModelListGrid = mini.get("selectModelListGrid");
    var selectRowIds = [];
    var selectRowNames = [];



    $(function () {
        if (singleId) {
            var url = jsUseCtxPath + "/drbfm/single/getSingleBaseDetail.do";
            $.ajaxSettings.async = false;
            $.post(
                url,
                {id: singleId},
                function (json) {
                    verifyPlanForm.setData(json);
                });
            $.ajaxSettings.async = true;
        }
        var changeModel = mini.get("model").getText();
        if (changeModel) {
            if(isExitsVariable(window.parent.selectRowNames)) {
                window.parent.selectRowNames = changeModel;
                window.parent.selectRowIds = mini.get("model").getValue();
            }
        }

        if (action == 'task'&&stageName=='bjfzrfxfx') {
            $("#topToolBar").show();
        }else if (action == 'edit') {
            $("#topToolBar").show();
        } else {
            verifyPlanForm.setEnabled(false);
        }
    });

    function saveBaseInfo() {
        var formValid = validBase();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        //保存Frame中的变量
        if(isExitsVariable(window.parent.selectRowNames)) {
            window.parent.selectRowNames = mini.get("model").getText();

        } else {
            mini.alert("未找到变量selectRowNames")
        }
        var formData = new mini.Form("verifyPlanForm");
        var data = formData.getData();
        var json = mini.encode(data);
        $.ajax({
            url: jsUseCtxPath + '/drbfm/single/saveBaseInfo.do',
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
                                var url = jsUseCtxPath + "/drbfm/single/singleBaseInfoPage.do?" +
                                    "singleId=" + returnData.data + "&action="+action+"&stageName="+stageName;
                                window.location.href = url;
                            }
                        }
                    });
                }
            }
        });
    }

    function validBase() {
        var structType = $.trim(mini.get("structType").getValue());
        if (!structType) {
            return {"result": false, "message": "请填写零件类型"};
        }
        var checkUserId = $.trim(mini.get("checkUserId").getValue());
        if (!checkUserId) {
            return {"result": false, "message": "请填写校对室主任"};
        }
        // 内部校对会签人改为非必填
        // var innerCheckUserIds = $.trim(mini.get("innerCheckUserIds").getValue());
        // if (!innerCheckUserIds) {
        //     return {"result": false, "message": "请填写挖掘机械研究院内部校对会签人"};
        // }
        return {"result": true};
    }


    function selectModelClick() {
        var modelId = mini.get("model").getValue();
        var modelName = mini.get("model").getText();
        if(modelId){
            selectRowIds = modelId.split(',');
            selectRowNames = modelName.split(',');
        }
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
        if (selectRowIds) {
            mini.get("model").setValue(selectRowIds.toString());
            mini.get("model").setText(selectRowNames.toString());
            selectModelHide();
            //保存Frame中的变量
            if(isExitsVariable(window.parent.selectRowNames)) {
                window.parent.selectRowNames= mini.get("model").getText();
                window.parent.selectRowIds= mini.get("model").getValue();
            } else {
                mini.alert("未找到变量selectRowNames")
            }
        }
    }

    function selectModelHide() {
        selectModelWindow.hide();
        mini.get("saleModel").setValue('');
        mini.get("productManagerName").setValue('');
        mini.get("departName").setValue('');
        mini.get("designModel").setValue('');
        selectRowIds.splice(0, selectRowIds.length);
        selectRowNames.splice(0, selectRowNames.length);
        selectModelListGrid.clearSelect();
    }

    function onRelModelCloseClick() {
        mini.get("model").setValue('');
        mini.get("model").setText('');
        selectRowIds.splice(0, selectRowIds.length);
        selectRowNames.splice(0, selectRowNames.length);
        selectModelListGrid.clearSelect();
    }

    function onGridLoad(e) {
        if (selectRowIds.length>0) {
            for (var i = 0; i < selectModelListGrid.data.length; i++) {
                if (selectRowIds.indexOf(selectModelListGrid.data[i].id) != -1) {
                    selectModelListGrid.setSelected(selectModelListGrid.getRow(i));
                }
            }
        }
    }


    selectModelListGrid.on("select", function (e) {
        var rec = e.record;
        var designModel = rec.designModel;
        var id = rec.id;
        if (selectRowIds.indexOf(id) == -1) {
            selectRowIds.push(id);
            selectRowNames.push(designModel);
        }
    });

    selectModelListGrid.on("deselect", function (e) {
        var rec = e.record;
        var designModel = rec.designModel;
        var id = rec.id;
        delItem(designModel, selectRowNames);
        delItem(id, selectRowIds);
    });

    function delItem(item, list) {
        // 表示先获取这个元素的下标，然后从这个下标开始计算，删除长度为1的元素
        list.splice(list.indexOf(item), 1)
    }
    
</script>
</body>
</html>
