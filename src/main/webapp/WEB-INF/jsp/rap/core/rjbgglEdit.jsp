<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>软件变更管理</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
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

        .table-detail > tbody > tr > td {
            border: 1px solid #eee;
            background-color: #fff;
            white-space: normal;
            word-break: break-all;
            color: rgb(85, 85, 85);
            font-weight: normal;
            padding: 4px;
            height: 40px;
            min-height: 40px;
            box-sizing: border-box;
            font-size: 15px;
        }
    </style>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formRjbggl" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold;font-size: 20px !important;">
                    发动机软件变更管理
                </caption>
                <tr>
                    <td style="  width: 20%;text-align: center">流程编号(提交后自动生成)：</td>
                    <td style="width: 20%">
                        <input id="noticeNo" name="noticeNo" class="mini-textbox" readonly style="  width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%">申请时间：
                    </td>
                    <td>
                        <input id="CREATE_TIME_" name="CREATE_TIME_" readonly="readonly" class="mini-datepicker"
                               format="yyyy-MM-dd"
                               style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;text-align: center">申请人：</td>
                    <td>
                        <input id="apply" name="applyId" textname="applyName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                    <td style="width: 20%;text-align: center">申请部门：</td>
                    <td>
                        <input id="appDept" name="appDeptId" class="mini-dep rxc" plugins="mini-dep"
                               style="width:98%;height:34px"
                               allowinput="false" textname="appDeptName" single="true" initlogindep="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">联系方式：
                    </td>
                    <td>
                        <input id="phone" name="phone" class="mini-textbox" style="  width:98%;"/>
                    </td>
                    <td style="width: 20%;text-align: center">部件名称及型号：</td>
                    <td style="width: 20%">
                        <input id="bjName" name="bjName" class="mini-textbox" style="  width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="  width: 20%;text-align: center">物料号：</td>
                    <td style="width: 20%">
                        <input id="wlNumber" name="wlNumber" class="mini-textbox" style="  width:98%;"/>
                    </td>
                    <td style="width: 20%;text-align: center">供应商公司名称：</td>
                    <td style="width: 20%">
                        <input id="supplier" name="supplier" class="mini-textbox" style="  width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="  width: 20%;text-align: center">供应商联系人：</td>
                    <td style="width: 20%">
                        <input id="supplierPer" name="supplierPer" class="mini-textbox" style="  width:98%;"/>
                    </td>
                    <td style="  width: 20%;text-align: center">对接室主任：</td>
                    <td style="width: 20%">
                        <input id="djId" name="djId" textname="djName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;text-align: center">变更原因：</td>
                    <td colspan="3">
                        <input id="reason" name="reason" class="mini-textarea"
                               style="width:98%;height: 100px"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;text-align: center">变更内容：</td>
                    <td colspan="3">
                        <input id="content" name="content" class="mini-textarea"
                               style="width:98%;height: 100px"/>
                    </td>
                </tr>
                <tr>
                    <td style="  width: 20%;text-align: center">对接室主任确认是否添加其他室主任审核：</td>
                    <td style="width: 20%">
                        <input id="ifszr" name="ifszr" class="mini-combobox" style="width:98%;"
                               textField="key" valueField="value" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[ {'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"
                        />
                    </td>
                    <td style="  width: 20%;text-align: center">对接室主任确认是否选择技术会签人员：</td>
                    <td style="width: 20%">
                        <input id="ifdjjsy" name="ifdjjsy" class="mini-combobox" style="width:98%;"
                               textField="key" valueField="value" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[ {'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="width:15%;height: 300px;text-align: center">其他室主任：</td>
                    <td colspan="3">
                        <div style="margin-bottom: 2px">
                            <a id="addSzr" class="mini-button" onclick="addSzr()">添加</a>
                            <a id="removeSzr" class="mini-button" onclick="removeSzr()">删除</a>
                        </div>
                        <div id="szrListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                             allowResize="false" allowCellWrap="true"
                             idField="id" url="${ctxPath}/environment/core/Rjbggl/querySzr.do?belongId=${id}"
                             autoload="true" allowCellEdit="true" allowCellSelect="true"
                             oncellbeginedit="OnCellBeginEditSzr" multiSelect="true" showPager="false"
                             showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="checkcolumn" width="10"></div>
                                <div type="indexcolumn" align="center" headerAlign="center" width="20">序号</div>
                                <div field="szrId" displayField="szrName" width="30" headerAlign="center"
                                     align="center">室主任
                                    <input property="editor" class="mini-user rxc" plugins="mini-user"
                                           style="width:90%;height:34px;" allowinput="false" length="50" maxlength="50"
                                           mainfield="no" single="true" name="szrId" textname="szrName"/></div>
                                <div field="ifjsy" width="40" headerAlign="center" align="center">其他室主任确认是否选择技术人员会签
                                    <input property="editor" class="mini-combobox" style="width:98%;"
                                           textField="value" valueField="key" emptyText="请选择..."
                                           required="false" allowInput="false" showNullItem="true"
                                           nullItemText="请选择..."
                                           data="[{'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"
                                    /></div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="width:15%;height: 300px;text-align: center">实施机型：</td>
                    <td colspan="3">
                        <div style="margin-bottom: 2px">
                            <a id="addModel" class="mini-button" onclick="addModel()">添加</a>
                            <a id="removeModel" class="mini-button" onclick="removeModel()">删除</a>
                        </div>
                        <div id="modelListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                             allowResize="false" allowCellWrap="true"
                             idField="id" url="${ctxPath}/environment/core/Rjbggl/queryModel.do?belongId=${id}"
                             autoload="true" allowCellEdit="true" allowCellSelect="true"
                             oncellbeginedit="OnCellBeginEditModel" multiSelect="true" showPager="false"
                             showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="checkcolumn" width="10"></div>
                                <div type="indexcolumn" align="center" headerAlign="center" width="20">序号</div>
                                <div field="modelId" displayField="modelName" width="60" headerAlign="center"
                                     align="center">设计型号
                                    <input property="editor" style="width:98%;" class="mini-buttonedit" showClose="true"
                                           oncloseclick="onRelModelCloseClick(e)"
                                           name="modelId" textname="modelName" allowInput="false"
                                           onbuttonclick="selectModelClick()"/></div>
                                <div field="solution" width="40" headerAlign="center" align="center">整改方式
                                    <input property="editor" class="mini-combobox" style="width:98%;"
                                           textField="value" valueField="key" emptyText="请选择..."
                                           required="false" allowInput="false" showNullItem="true"
                                           nullItemText="请选择..."
                                           data="[{'key' : '主动整改','value' : '主动整改'},{'key' : '被动整改','value' : '被动整改'}]"
                                    /></div>
                                <div field="cpzgId" displayField="cpzgName" width="30" headerAlign="center"
                                     align="center">产品主管
                                    <input property="editor" class="mini-user rxc" plugins="mini-user"
                                           style="width:90%;height:34px;" allowinput="false" length="50" maxlength="50"
                                           mainfield="no" single="true" name="cpzgId" textname="cpzgName"/></div>
                                <div field="userName" width="20" headerAlign="center" align="center" allowSort="true">
                                    创建人
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="width:15%;height: 300px;text-align: center">技术人员选择：</td>
                    <td colspan="3">
                        <div style="margin-bottom: 2px">
                            <a id="addJsy" class="mini-button" onclick="addJsy()">添加</a>
                            <a id="removeJsy" class="mini-button" onclick="removeJsy()">删除</a>
                        </div>
                        <div id="jsyListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                             allowResize="false" allowCellWrap="true"
                             idField="id" url="${ctxPath}/environment/core/Rjbggl/queryJsy.do?belongId=${id}"
                             autoload="true" allowCellEdit="true" allowCellSelect="true"
                             oncellbeginedit="OnCellBeginEditJsy" multiSelect="true" showPager="false"
                             showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="checkcolumn" width="10"></div>
                                <div type="indexcolumn" align="center" headerAlign="center" width="20">序号</div>
                                <div field="jsyId" displayField="jsyName" width="30" headerAlign="center"
                                     align="center">技术会签人员
                                    <input property="editor" class="mini-user rxc" plugins="mini-user"
                                           style="width:90%;height:34px;" allowinput="false" length="50" maxlength="50"
                                           mainfield="no" single="true" name="jsyId" textname="jsyName"/></div>
                                <div field="ifchange" width="40" headerAlign="center" align="center">是否同步变更
                                    <input property="editor" class="mini-combobox" style="width:98%;"
                                           textField="value" valueField="key" emptyText="请选择..."
                                           required="false" allowInput="false" showNullItem="true"
                                           nullItemText="请选择..."
                                           data="[{'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"
                                    /></div>
                                <div field="changeNum" width="50" headerAlign="center" align="center">同步变更单号
                                    <input property="editor" class="mini-textbox"/></div>
                                <div field="userName" width="20" headerAlign="center" align="center" allowSort="true">
                                    创建人
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
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
<script type="text/javascript">
    mini.parse();
    var status = "${status}";
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var id = "${id}";
    var formRjbggl = new mini.Form("#formRjbggl");
    var selectModelWindow = mini.get("selectModelWindow");
    var selectModelListGrid = mini.get("selectModelListGrid");
    var modelListGrid = mini.get("modelListGrid");
    var szrListGrid = mini.get("szrListGrid");
    var jsyListGrid = mini.get("jsyListGrid");
    var currentUserName = "${currentUserName}";
    var nodeVarsStr = '${nodeVars}';
    var currentUserId = "${currentUserId}";
    var deptName = "${deptName}";
    var currentUserMainDepId = "${currentUserMainDepId}";
    var stageName = "";
    $(function () {
        if (id) {
            var url = jsUseCtxPath + "/environment/core/Rjbggl/getRjbggl.do";
            $.post(
                url,
                {id: id},
                function (json) {
                    formRjbggl.setData(json);
                });
        } else {
            mini.get("apply").setValue(currentUserId);
            mini.get("apply").setText(currentUserName);
            mini.get("appDept").setValue(currentUserMainDepId);
            mini.get("appDept").setText(deptName);
        }
        mini.get("CREATE_TIME_").setEnabled(false);
        //变更入口
        if (action == 'task') {
            taskActionProcess();
        } else if (action == "detail") {
            formRjbggl.setEnabled(false);
            szrListGrid.setAllowCellEdit(false);
            modelListGrid.setAllowCellEdit(false);
            jsyListGrid.setAllowCellEdit(false);
            mini.get("addModel").setEnabled(false);
            mini.get("removeModel").setEnabled(false);
            mini.get("addJsy").setEnabled(false);
            mini.get("removeJsy").setEnabled(false);
            mini.get("addSzr").setEnabled(false);
            mini.get("removeSzr").setEnabled(false);
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        } else if (action == "edit") {
            szrListGrid.setAllowCellEdit(false);
            modelListGrid.setAllowCellEdit(false);
            jsyListGrid.setAllowCellEdit(false);
            mini.get("ifszr").setEnabled(false);
            mini.get("ifdjjsy").setEnabled(false);
            mini.get("addModel").setEnabled(false);
            mini.get("removeModel").setEnabled(false);
            mini.get("addJsy").setEnabled(false);
            mini.get("removeJsy").setEnabled(false);
            mini.get("addSzr").setEnabled(false);
            mini.get("removeSzr").setEnabled(false);
        }
    });

    function getData() {
        debugger
        var formData = _GetFormJsonMini("formRjbggl");
        formData.model = modelListGrid.getChanges();
        formData.szr = szrListGrid.getChanges();
        formData.jsy = jsyListGrid.getChanges();
        formData.allszr = szrListGrid.getData();
        formData.alljsy = jsyListGrid.getData();
        formData.allmodel = modelListGrid.getData();
        return formData;
    }

    function saveRjbggl(e) {
        // var formValid = validFirst();
        // if (!formValid.result) {
        //     mini.alert(formValid.message);
        //     return;
        // }
        window.parent.saveDraft(e);
    }

    function saveChange(id) {
        var formData = getData();
        $.ajax({
            url: jsUseCtxPath + '/environment/core/Rjbggl/saveRjbggl.do?id=' + id,
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


    function startRjbgglProcess(e) {
        var formValid = validFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }

    function validFirst() {
        var apply = $.trim(mini.get("apply").getValue());
        if (!apply) {
            return {"result": false, "message": "请填写申请人"};
        }
        var appDept = $.trim(mini.get("appDept").getValue());
        if (!appDept) {
            return {"result": false, "message": "请填写申请部门"};
        }
        var phone = $.trim(mini.get("phone").getValue());
        if (!phone) {
            return {"result": false, "message": "请填写联系方式"};
        }
        var bjName = $.trim(mini.get("bjName").getValue());
        if (!bjName) {
            return {"result": false, "message": "请填写部件名称及型号"};
        }
        var wlNumber = $.trim(mini.get("wlNumber").getValue());
        if (!wlNumber) {
            return {"result": false, "message": "请填写物料号"};
        }
        var supplier = $.trim(mini.get("supplier").getValue());
        if (!supplier) {
            return {"result": false, "message": "请填写供应商公司名称"};
        }
        var supplierPer = $.trim(mini.get("supplierPer").getValue());
        if (!supplierPer) {
            return {"result": false, "message": "请填写供应商公司名称联系人"};
        }
        var reason = $.trim(mini.get("reason").getValue());
        if (!reason) {
            return {"result": false, "message": "请填写变更原因"};
        }
        var content = $.trim(mini.get("content").getValue());
        if (!content) {
            return {"result": false, "message": "请填写变更内容"};
        }
        var djId = $.trim(mini.get("djId").getValue());
        if (!djId) {
            return {"result": false, "message": "请选择对接室主任"};
        }
        return {"result": true};
    }

    function validSecond() {
        var ifszr = $.trim(mini.get("ifszr").getValue());
        if (!ifszr) {
            return {"result": false, "message": "请选择是否添加其他室主任审核"};
        }
        var ifdjjsy = $.trim(mini.get("ifdjjsy").getValue());
        if (!ifdjjsy) {
            return {"result": false, "message": "请选择是否添加技术会签人员"};
        }
        var szr = szrListGrid.getData();
        debugger
        if (szr.length < 1 && ifszr == '是') {
            return {"result": false, "message": "请选择其他室主任"};
        } else {
            for (var i = 0; i < szr.length; i++) {
                if (szr[i].szrId == undefined || szr[i].szrId == "") {
                    return {"result": false, "message": "请选择其他室主任"};
                }
            }
        }
        var jsy = jsyListGrid.getData();
        if (jsy.length < 1 && ifdjjsy == '是') {
            return {"result": false, "message": "请选择技术会签人员"};
        } else {
            for (var i = 0; i < jsy.length; i++) {
                if (jsy[i].jsyId == undefined || jsy[i].jsyId == "") {
                    return {"result": false, "message": "请选择技术会签人员"};
                }
            }
        }
        var model = modelListGrid.getData();
        if (model.length < 1) {
            return {"result": false, "message": "请添加实施机型"};
        } else {
            for (var i = 0; i < model.length; i++) {
                if (model[i].modelId == undefined || model[i].modelId == "") {
                    return {"result": false, "message": "请选择设计型号"};
                }
                if (model[i].solution == undefined || model[i].solution == "") {
                    return {"result": false, "message": "请选择整改方式"};
                }
            }
        }
        return {"result": true};
    }

    function validThird() {
        var jsyChange = jsyListGrid.getChanges();
        if(jsyChange.length>0){
            return {"result": false, "message": "技术人员选择数据有变动,请先点击暂存"};
        }
        var szr = szrListGrid.getData();
        for (var i = 0; i < szr.length; i++) {
            if ((szr[i].ifjsy == undefined || szr[i].ifjsy == "") && szr[i].szrId == currentUserId) {
                return {"result": false, "message": "请选择是否选择技术人员会签"};
            }
            if (szr[i].ifjsy == '是' && szr[i].szrId == currentUserId) {
                var jsy = jsyListGrid.getData();
                var noJsy = 'true';
                for (var i = 0; i < jsy.length; i++) {
                    if (jsy[i].jsyId == undefined || jsy[i].jsyId == "") {
                        return {"result": false, "message": "请选择技术会签人员"};
                    }
                    if(jsy[i].CREATE_BY_ == currentUserId){
                        noJsy = 'false';
                    }
                }
            }
        }
        if(noJsy=='true'){
            return {"result": false, "message": "请添加技术会签人员"};
        }
        var model = modelListGrid.getData();
        for (var i = 0; i < model.length; i++) {
            if (model[i].modelId == undefined || model[i].modelId == "") {
                return {"result": false, "message": "请选择设计型号"};
            }
            if (model[i].solution == undefined || model[i].solution == "") {
                return {"result": false, "message": "请选择整改方式"};
            }

        }

        return {"result": true};
    }

    function validForth() {
        var jsy = jsyListGrid.getData();
        for (var i = 0; i < jsy.length; i++) {
            if ((jsy[i].ifchange == undefined || jsy[i].ifchange == "") && jsy[i].jsyId == currentUserId) {
                return {"result": false, "message": "请选择是否同步变更"};
            }
            if (jsy[i].ifchange == '是' && jsy[i].jsyId == currentUserId && (jsy[i].changeNum == undefined || jsy[i].changeNum == "")) {
                return {"result": false, "message": "请填写同步变更单号"};
            }
        }
        return {"result": true};
    }


    function rjbgglApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (stageName == 'first') {
            var formValid = validFirst();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (stageName == 'second') {
            var formValid = validSecond();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (stageName == 'third') {
            var formValid = validThird();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (stageName == 'forth') {
            var formValid = validForth();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        //检查通过
        window.parent.approve();
    }


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

    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();

        //获取环境变量
        if (!nodeVarsStr) {
            nodeVarsStr = "[]";
        }
        formRjbggl.setEnabled(false);
        szrListGrid.setAllowCellEdit(false);
        modelListGrid.setAllowCellEdit(false);
        jsyListGrid.setAllowCellEdit(false);
        mini.get("addModel").setEnabled(false);
        mini.get("removeModel").setEnabled(false);
        mini.get("addJsy").setEnabled(false);
        mini.get("removeJsy").setEnabled(false);
        mini.get("addSzr").setEnabled(false);
        mini.get("removeSzr").setEnabled(false);
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'stageName') {
                stageName = nodeVars[i].DEF_VAL_;
            }
        }
        if (stageName == 'first') {
            formRjbggl.setEnabled(true);
            mini.get("ifszr").setEnabled(false);
            mini.get("ifdjjsy").setEnabled(false);
        }
        if (stageName == 'second') {
            mini.get("ifdjjsy").setEnabled(true);
            mini.get("ifszr").setEnabled(true);
            szrListGrid.setAllowCellEdit(true);
            modelListGrid.setAllowCellEdit(true);
            jsyListGrid.setAllowCellEdit(true);
            mini.get("addModel").setEnabled(true);
            mini.get("removeModel").setEnabled(true);
            mini.get("addJsy").setEnabled(true);
            mini.get("removeJsy").setEnabled(true);
            mini.get("addSzr").setEnabled(true);
            mini.get("removeSzr").setEnabled(true);
        }
        if (stageName == 'third') {
            szrListGrid.setAllowCellEdit(true);
            modelListGrid.setAllowCellEdit(true);
            jsyListGrid.setAllowCellEdit(true);
            mini.get("addModel").setEnabled(true);
            mini.get("removeModel").setEnabled(true);
            mini.get("addJsy").setEnabled(true);
            mini.get("removeJsy").setEnabled(true);
        }
        if (stageName == 'forth') {
            jsyListGrid.setAllowCellEdit(true);
        }
    }

    function addSzr() {
        var formId = mini.get("id").getValue();
        if (!formId) {
            mini.alert("请先点击‘保存草稿’进行表单创建!");
            return;
        } else {
            var row = {};
            szrListGrid.addRow(row);
        }
    }

    function removeSzr() {
        var selecteds = szrListGrid.getSelecteds();
        if (selecteds.length <= 0) {
            mini.alert("请选择一条记录");
            return;
        }
        var deleteArr = [];
        for (var i = 0; i < selecteds.length; i++) {
            var row = selecteds[i];
            deleteArr.push(row);
        }
        szrListGrid.removeRows(deleteArr);
    }

    function addJsy() {
        var formId = mini.get("id").getValue();
        if (!formId) {
            mini.alert("请先点击‘保存草稿’进行表单创建!");
            return;
        } else {
            var row = {};
            jsyListGrid.addRow(row);
        }
    }

    function removeJsy() {
        var selecteds = jsyListGrid.getSelecteds();
        if (selecteds.length <= 0) {
            mini.alert("请选择一条记录");
            return;
        }
        for (var i = 0; i < selecteds.length; i++) {
            if (selecteds[i].CREATE_BY_ != null && selecteds[i].CREATE_BY_ != currentUserId) {
                mini.alert("非自己创建的不能删除");
                return;
            }
        }
        var deleteArr = [];
        for (var i = 0; i < selecteds.length; i++) {
            var row = selecteds[i];
            deleteArr.push(row);
        }
        jsyListGrid.removeRows(deleteArr);
    }

    function addModel() {
        var formId = mini.get("id").getValue();
        if (!formId) {
            mini.alert("请先点击‘保存草稿’进行表单创建!");
            return;
        } else {
            var row = {};
            modelListGrid.addRow(row);
        }
    }

    function removeModel() {
        var selecteds = modelListGrid.getSelecteds();
        if (selecteds.length <= 0) {
            mini.alert("请选择一条记录");
            return;
        }
        for (var i = 0; i < selecteds.length; i++) {
            if (selecteds[i].CREATE_BY_ != null && selecteds[i].CREATE_BY_ != currentUserId) {
                mini.alert("非自己创建的不能删除");
                return;
            }
        }
        var deleteArr = [];
        for (var i = 0; i < selecteds.length; i++) {
            var row = selecteds[i];
            deleteArr.push(row);
        }
        modelListGrid.removeRows(deleteArr);
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
        var selectRow = selectModelListGrid.getSelected();
        var row = modelListGrid.getSelected();
        debugger
        if (selectRow) {
            modelListGrid.updateRow(row, {
                modelId: selectRow.id,
                modelName: selectRow.designModel,
                cpzgId: selectRow.productManagerId,
                cpzgName: selectRow.productManagerName
            });
        }
        selectModelHide();
    }

    function selectModelHide() {
        selectModelWindow.hide();
        mini.get("saleModel").setValue('');
        mini.get("productManagerName").setValue('');
        mini.get("departName").setValue('');
        mini.get("designModel").setValue('');
    }

    function onRelModelCloseClick() {
        var row = modelListGrid.getSelected();
        modelListGrid.updateRow(row, {modelId: "", modelName: "", cpzgId: "", cpzgName: ""});
        this.setText("");
        this.setValue("");
    }

    function OnCellBeginEditModel(e) {
        var record = e.record, field = e.field;
        var CREATE_BY_ = record.CREATE_BY_;
        e.cancel = true;
        if ((field == "modelId" || field == "solution") && (CREATE_BY_ == currentUserId || currentUserId == '1' || !CREATE_BY_)) {
            e.cancel = false;
        }
    }

    function OnCellBeginEditSzr(e) {
        var record = e.record, field = e.field;
        var szrId = record.szrId;
        var CREATE_BY_ = record.CREATE_BY_;
        e.cancel = true;
        if (field == "ifjsy" && (szrId == currentUserId || currentUserId == '1')) {
            e.cancel = false;
        }
        if (field == "szrId" && (CREATE_BY_ == currentUserId || currentUserId == '1' || !CREATE_BY_)) {
            e.cancel = false;
        }
    }

    function OnCellBeginEditJsy(e) {
        debugger
        var record = e.record, field = e.field;
        var jsyId = record.jsyId;
        var CREATE_BY_ = record.CREATE_BY_;
        e.cancel = true;
        if ((field == "ifchange" || field == "changeNum") && (jsyId == currentUserId || currentUserId == '1')) {
            e.cancel = false;
        }
        if ((field == "jsyId") && (CREATE_BY_ == currentUserId || currentUserId == '1' || !CREATE_BY_)) {
            e.cancel = false;
        }
    }

</script>
</body>
</html>