<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>编辑申请</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <link href="${ctxPath}/scripts/mini/miniui/themes/icons.css" rel="stylesheet" type="text/css"/>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" style="display: none" class="mini-button" onclick="processInfo()">流程信息</a>
        <a class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;">
        <form id="bjInventoryForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption>
                    备件推荐清单需求申请
                </caption>
                <tr>
                    <td style="text-align: center;width: 25%">申请人：</td>
                    <td style="width: 25%">
                        <input id="apply" name="applyId" textname="applyName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                    <td style="text-align: center;width: 25%">流程编号(自动生成):</td>
                    <td colspan="1">
                        <input id="processNum" name="processNum" class="mini-textbox" readonly style="width:98%;"/>
                    </td>
                <tr>
                <tr>
                    <td style="text-align: center;width: 25%">设计型号：</td>
                    <td>
                        <input id="designModel" style="width:98%;" class="mini-buttonedit" showClose="true"
                               oncloseclick="onRelModelCloseClick()"
                               name="designModelId" textname="designModelName" allowInput="false"
                               onbuttonclick="selectModelClick()"/>
                    </td>
                    <td style="text-align: center;width: 25%">整机物料号：
                    </td>
                    <td>
                        <input id="materielCode" name="materielCode" class="mini-textbox" readonly
                               style="width:99%"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 12%">销售型号：
                    </td>
                    <td>
                        <input id="saleModel" name="saleModel" class="mini-textbox" readonly style="width:99%"/>
                    </td>
                    <td style="text-align: center;width: 12%">整机编号：
                    </td>
                    <td>
                        <input id="machineNum" name="machineNum" class="mini-textbox" style="width:99%"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 12%">销售区域：
                    </td>
                    <td>
                        <input id="saleArea" name="saleArea" class="mini-textbox" style="width:99%"/>
                    </td>
                    <td style="width: 10%;text-align: center">需求翻译语言：</td>
                    <td style="width: 23%;min-width:170px;font-size:14pt">
                        <input property="editor" id="needLanguage" name="needLanguage" class="mini-combobox"
                               style="width:98%;"
                               data="[ {'id' : '无需翻译','text' : '无需翻译'},{'id' : '英文','text' : '英文'},{'id' : '俄文','text' : '俄文'},
                           {'id' : '葡文','text' : '葡文'},{'id' : '德文','text' : '德文'},
                           {'id' : '西文','text' : '西文'},{'id' : '法文','text' : '法文'},
                           {'id' : '意文','text' : '意文'},{'id' : '波兰语','text' : '波兰语'},
                           {'id' : '土耳其语','text' : '土耳其语'},{'id' : '瑞典语','text' : '瑞典语'},
                           {'id' : '丹麦文','text' : '丹麦文'},{'id' : '荷兰语','text' : '荷兰语'},
                           {'id' : '斯洛文尼亚语','text' : '斯洛文尼亚语'},{'id' : '罗马尼亚语','text' : '罗马尼亚语'},
                           {'id' : '繁体字','text' : '繁体字'},{'id' : '泰语','text' : '泰语'},
                           {'id' : '匈牙利语','text' : '匈牙利语'},{'id' : '挪威语','text' : '挪威语'},
                           {'id' : '韩语','text' : '韩语'},{'id' : '印尼语','text' : '印尼语'},
                           {'id' : '阿拉伯语','text' : '阿拉伯语'},{'id' : '日语','text' : '日语'}
                           ]"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 12%;text-align: center">需求时间：
                    </td>
                    <td>
                        <input id="needTime" name="needTime" class="mini-datepicker"
                               format="yyyy-MM-dd" showTime="false" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 12%">需求周期：
                    </td>
                    <td>
                        <input id="needCycle" name="needCycle" class="mini-textbox" style="width:99%"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 12%;text-align: center">备件推荐时长：
                    </td>
                    <td>
                        <input id="suggestTime" name="suggestTime" class="mini-textbox" style="width:99%"/>
                    </td>
                    <td style="text-align: center;width: 12%">备注：
                    </td>
                    <td>
                        <input id="note" name="note" class="mini-textbox" style="width:99%"/>
                    </td>
                </tr>
            </table>
        </form>
        <p style="font-size: 16px;font-weight: bold;margin-top: 20px">清单详情</p>
        <hr/>
        <div style="margin-top: 5px;margin-bottom: 2px">
            <a id="addInventoryDetail" style="margin-right: 5px;display: none" class="mini-button"
               onclick="addInventoryDetail()">添加</a>
            <a id="removeInventoryDetail" style="margin-right: 5px;display: none" class="mini-button"
               onclick="removeInventoryDetail()">删除</a>
            <a id="importBjInventory" class="mini-button" style="margin-right: 5px;display: none" plain="true"
               onclick="importBjInventory()">导入</a>
            <a id="exportBjInventoryInProcess" class="mini-button" style="margin-right: 5px;display: none" plain="true"
               onclick="exportBjInventory('inProcess')">导出</a>
            <a id="exportBjInventoryEndProcess" class="mini-button" style="margin-right: 5px;display: none" plain="true"
               onclick="exportBjInventory('endProcess')">导出备件推荐清单</a>
        </div>
        <div id="inventoryListGrid" class="mini-datagrid" style="height:500px" autoLoad="true"
             allowCellEdit="true" allowCellWrap="true" oncellbeginedit="OnCellBeginEditInventory"
             idField="id" url="${ctxPath}/serviceEngineer/core/BjInventory/queryDetail.do?belongId=${id}"
             allowResize="false" allowCellSelect="true" allowSortColumn="false" allowHeaderWrap="true"
             multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false" style="height: 500px"
             showVGridLines="true">
            <div property="columns">
                <div type="checkcolumn" width="40"></div>
                <div type="indexcolumn" width="50" headerAlign="center" align="center">序号</div>
                <div field="materielType" width="100px" headerAlign="center" align="center">物料属性
                    <input id="materielType" property="editor" class="mini-combobox" style="width:90%;"
                           textField="text" valueField="id" emptyText="请选择..."
                           data="[{id:'保养件',text:'保养件'},{id:'易损件',text:'易损件'},{id:'维修件',text:'维修件'}]"
                           allowInput="false" showNullItem="false" nullItemText="请选择..." required="true"/>
                </div>
                <div field="materielPlace" width="100px" headerAlign="center" align="center">位置
                    <input property="editor" class="mini-textbox"/></div>
                <div field="zcMaterielCode" width="100px" headerAlign="center" align="center">物料编码(总成)
                    <input property="editor" class="mini-textbox"/></div>
                <div field="zcMaterielName" width="200px" headerAlign="center" align="center">物料名称(总成)
                    <input property="editor" class="mini-textbox"/></div>
                <div field="materielCode" width="100px" headerAlign="center" align="center">物料编码
                    <input property="editor" class="mini-textbox"/></div>
                <div field="materielName" width="200px" headerAlign="center" align="center">物料名称
                    <input property="editor" class="mini-textbox"/></div>
                <div field="num" width="100px" headerAlign="center" align="center">装配数量/台
                    <input property="editor" class="mini-textbox"/></div>
                <div field="suggestNum" width="100px" headerAlign="center" align="center">推荐数量
                    <input property="editor" class="mini-textbox"/></div>
                <div field="byTime" width="100px" headerAlign="center" align="center">保养时长
                    <input property="editor" class="mini-textbox"/></div>
                <div field="note" headerAlign="center" align="center" width="100px">备注
                    <input property="editor" class="mini-textbox"/></div>
            </div>
        </div>
        <p style="font-size: 16px;font-weight: bold;margin-top: 20px">翻译附件</p>
        <hr/>
        <div>
            <tr>
                <td colspan="6">
                    <div style="margin-top:2px;margin-bottom: 2px" id="fileListToolBar">
                        <a id="addFile" class="mini-button" style="display: none" onclick="addFileUpload()">添加附件</a>
                    </div>
                    <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 300px"
                         allowResize="false" autoload="true"
                         idField="id" url="${ctxPath}/serviceEngineer/core/BjInventory/getBjInventoryFileList.do?belongId=${id}"
                         multiSelect="false" showPager="false" showColumnsMenu="false"
                         allowAlternating="true">
                        <div property="columns">
                            <div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
                            <div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
                            <div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
                            <div field="action" width="80" headerAlign='center' align="center"
                                 renderer="operationRenderer">操作
                            </div>
                        </div>
                    </div>
                </td>
            </tr>
        </div>
    </div>
</div>

<!--导出Excel相关HTML-->
<form id="excelForm" method="post" target="excelIFrame">
    <input type="hidden" name="pageIndex" id="pageIndex"/>
    <input type="hidden" name="pageSize" id="pageSize"/>
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

</div>
<div id="selectModelWindow" title="选择设计型号" class="mini-window" style="width:900px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="true">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span class="text" style="width:auto">设计型号: </span><input
            class="mini-textbox" style="width: 120px" id="selectdesignModel" name="selectdesignModel"/>
        <span class="text" style="width:auto">销售型号: </span><input
            class="mini-textbox" style="width: 120px" id="selectsaleModel" name="selectsaleModel"/>
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
                <div field="materialCode" width="150" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    物料号
                </div>
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
<div id="importWindow" title="导入窗口" class="mini-window" style="width:750px;height:280px;"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importMaterial()">导入</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%">上传模板：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">备件推荐清单导入模板.xlsx</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%">操作：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName"
                               readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()"
                               accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadFile">选择文件</a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clearUploadFile">清除</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var bjInventoryForm = new mini.Form("#bjInventoryForm");
    var nodeVarsStr = '${nodeVars}';
    var action = "${action}";
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var currentTime = "${currentTime}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var deptId = "${deptId}";
    var deptName = "${deptName}";
    var inventoryListGrid = mini.get("inventoryListGrid");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var id = "${id}";
    var selectModelWindow = mini.get("selectModelWindow");
    var selectModelListGrid = mini.get("selectModelListGrid");
    var importWindow = mini.get("importWindow");
    var fileListGrid=mini.get("fileListGrid");


    var stageName = "";
    $(function () {
        if (id) {
            var url = jsUseCtxPath + "/serviceEngineer/core/BjInventory/getBjInventoryDetail.do";
            $.post(
                url,
                {id: id},
                function (json) {
                    bjInventoryForm.setData(json);
                });
        } else {
            mini.get("apply").setValue(currentUserId);
            mini.get("apply").setText(currentUserName);
        }
        if (action == 'task') {
            taskActionProcess();
        } else if (action == "detail") {
            bjInventoryForm.setEnabled(false);
            inventoryListGrid.setAllowCellEdit(false);
            mini.get("addInventoryDetail").setEnabled(false);
            mini.get("removeInventoryDetail").setEnabled(false);
            $("#exportBjInventoryEndProcess").show();
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        } else if (action == "edit") {
            inventoryListGrid.setAllowCellEdit(false);
        }
    });

    //流程实例图
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


    //获取环境变量
    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();

        //获取环境变量
        if (!nodeVarsStr) {
            nodeVarsStr = "[]";
        }
        bjInventoryForm.setEnabled(false);
        inventoryListGrid.setAllowCellEdit(false);
        mini.get("addInventoryDetail").setEnabled(false);
        mini.get("removeInventoryDetail").setEnabled(false);
        mini.get("importBjInventory").setEnabled(false);
        mini.get("exportBjInventoryInProcess").setEnabled(false);
        mini.get("exportBjInventoryEndProcess").setEnabled(false);
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'stageName') {
                stageName = nodeVars[i].DEF_VAL_;
            }
        }
        if (stageName == 'first') {
            bjInventoryForm.setEnabled(true);
        } else if (stageName == 'second' || stageName == 'third') {
            $("#addInventoryDetail").show();
            $("#removeInventoryDetail").show();
            $("#importBjInventory").show();
            $("#exportBjInventoryInProcess").show();
            inventoryListGrid.setAllowCellEdit(true);
            mini.get("addInventoryDetail").setEnabled(true);
            mini.get("removeInventoryDetail").setEnabled(true);
            mini.get("importBjInventory").setEnabled(true);
            mini.get("exportBjInventoryInProcess").setEnabled(true);
        } else if (stageName == 'forth') {
            $("#addFile").show();
            $("#exportBjInventoryInProcess").show();
            mini.get("addFile").setEnabled(true);
            mini.get("exportBjInventoryInProcess").setEnabled(true);
        } else if (stageName == 'fifth') {
            $("#exportBjInventoryEndProcess").show();
            mini.get("exportBjInventoryEndProcess").setEnabled(true);
        }

    }


    //保存草稿
    function saveApplyInfo(e) {

        window.parent.saveDraft(e);
    }

    //流程引擎调用此方法进行表单数据的获取
    function getData() {
        var formData = _GetFormJsonMini("bjInventoryForm");
        formData.inventory = inventoryListGrid.getChanges();
        formData.bos=[];
        formData.vars=[{key:'designModel',val:formData.designModelName}];
        return formData;
    }

    function saveInfo() {
        var formData = _GetFormJsonMini("bjInventoryForm");
        formData.inventory = inventoryListGrid.getChanges();
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineer/core/BjInventory/saveBjInventory.do',
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

    //保存草稿或提交时数据是否有效
    function draftOrStartValid() {
        var apply = $.trim(mini.get("apply").getValue());
        if (!apply) {
            return {"result": false, "message": "请选择申请人"};
        }
        var designModel = $.trim(mini.get("designModel").getValue());
        if (!designModel) {
            return {"result": false, "message": "请选择设计型号"};
        }
        var machineNum = $.trim(mini.get("machineNum").getValue());
        if (!machineNum) {
            return {"result": false, "message": "请填写整机编号"};
        }
        var saleArea = $.trim(mini.get("saleArea").getValue());
        if (!saleArea) {
            return {"result": false, "message": "请填写销售区域"};
        }
        var needLanguage = $.trim(mini.get("needLanguage").getValue());
        if (!needLanguage) {
            return {"result": false, "message": "请填写需求翻译语言"};
        }
        var needTime = $.trim(mini.get("needTime").getValue());
        if (!needTime) {
            return {"result": false, "message": "请填写需求时间"};
        }
        var suggestTime = $.trim(mini.get("suggestTime").getValue());
        if (!suggestTime) {
            return {"result": false, "message": "请填写备件推荐时长"};
        }
        return {"result": true};
    }

    function ValidSecondOrThird() {
        var flag = false;
        var inventoryListGridData = inventoryListGrid.getData();
        if (inventoryListGridData.length > 0) {
            for (var i = 0; i < inventoryListGridData.length; i++) {
                if (inventoryListGridData[i].CREATE_BY_ == currentUserId) {
                    flag = true
                }
            }
        }
        return {"result": true};
        // if(flag){
        //     return {"result": true};
        // }else {
        //     return {"result": false, "message": "请添加清单详情"};
        // }
    }

    function ValidForth() {
        var fileListGridData = fileListGrid.getData();
        if (fileListGridData.length == 0) {
            return {"result": false, "message": "请上传翻译附件"};
            }
        return {"result": true};
    }

    //启动流程
    function startApplyProcess(e) {
        var formValid = draftOrStartValid();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }

    //审批或者下一步
    function applyApprove() {
        if (stageName == 'first') {
            var formValid = draftOrStartValid();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (stageName == 'second' || stageName == 'third') {
            var formValid = ValidSecondOrThird();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (stageName == 'forth') {
            var formValid = ValidForth();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        window.parent.approve();
    }

    function selectModelClick() {
        selectModelWindow.show();
        searchModel();
    }

    function searchModel() {
        var queryParam = [];
        //其他筛选条件
        var designModel = $.trim(mini.get("selectdesignModel").getValue());
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
        var saleModel = $.trim(mini.get("selectsaleModel").getValue());
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
        if (rowSelected) {
            mini.get("designModel").setValue(rowSelected.id);
            mini.get("designModel").setText(rowSelected.designModel);
            mini.get("materielCode").setValue(rowSelected.materialCode);
            mini.get("saleModel").setValue(rowSelected.saleModel);
            selectModelHide();
        }
    }

    function selectModelHide() {
        selectModelWindow.hide();
        mini.get("selectsaleModel").setValue('');
        mini.get("productManagerName").setValue('');
        mini.get("departName").setValue('');
        mini.get("selectdesignModel").setValue('');
        selectModelListGrid.clearSelect();
    }

    function onRelModelCloseClick() {
        mini.get("designModel").setValue('');
        mini.get("designModel").setText('');
        mini.get("materielCode").setValue('');
        mini.get("saleModel").setValue('');
        selectModelListGrid.clearSelect();
    }


    function addInventoryDetail() {
        var row = {};
        inventoryListGrid.addRow(row);
    }


    function removeInventoryDetail() {
        var selecteds = inventoryListGrid.getSelecteds();
        if (selecteds.length <= 0) {
            mini.alert("请选择一条记录");
            return;
        }
        var devareArr = [];
        for (var i = 0; i < selecteds.length; i++) {
            if ((stageName == 'second' || stageName == 'third') && selecteds[i].CREATE_BY_ != currentUserId) {
                mini.alert("只能删除自己导入或新增的数据");
                return;
            }
            var row = selecteds[i];
            devareArr.push(row);
        }
        inventoryListGrid.removeRows(devareArr);
    }
    function exportBjInventory(exportType) {
        var parent = $(".search-form");
        var inputAry = $("input", parent);
        var params = [];
        inputAry.each(function (i) {
            var el = $(this);
            var obj = {};
            obj.name = el.attr("name");
            if (!obj.name) return true;
            obj.value = el.val();
            params.push(obj);
        });
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        var url = jsUseCtxPath+"/serviceEngineer/core/BjInventory/exportBjInventoryList.do?belongId="+id+"&exportType="+exportType;
        excelForm.attr("action",url);
        excelForm.submit();
    }

    //导入
    function importBjInventory() {
        if (!id) {
            mini.alert("请先保存草稿！");
            return
        }
        if (stageName != 'second' && stageName != 'third') {
            mini.confirm("导入会清除当前节点已上传的数据,请确保每次导入的数据为全部的数据", "提醒", function (action) {
                if (action != 'ok') {
                    return;
                } else {
                    importWindow.show();
                }
            });
        } else {
            importWindow.show();
        }
    }

    //上传批量导入
    function importMaterial() {
        var needLanguage = mini.get("needLanguage").getValue();
        var file = null;
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            file = fileList[0];
        }
        if (!file) {
            mini.alert('请选择文件！');
            return;
        }
        //XMLHttpRequest方式上传表单
        var xhr = false;
        try {
            //尝试创建 XMLHttpRequest 对象，除 IE 外的浏览器都支持这个方法。
            xhr = new XMLHttpRequest();
        } catch (e) {
            //使用较新版本的 IE 创建 IE 兼容的对象（Msxml2.XMLHTTP）。
            xhr = ActiveXobject("Msxml12.XMLHTTP");
        }

        if (xhr.upload) {
            xhr.onreadystatechange = function (e) {
                if (xhr.readyState == 4) {
                    if (xhr.status == 200) {
                        if (xhr.responseText) {
                            var returnObj = JSON.parse(xhr.responseText);
                            var message = '';
                            if (returnObj.message) {
                                message = returnObj.message;
                            }
                            mini.alert(message);
                            inventoryListGrid.reload();
                        }
                    }
                }
            };

            // 开始上传
            xhr.open('POST', jsUseCtxPath + '/serviceEngineer/core/BjInventory/importExcelBjInventory.do?id=' + id + "&stageName=" + stageName+ "&needLanguage=" + needLanguage, false);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            var fd = new FormData();
            fd.append('importFile', file);
            xhr.send(fd);
        }
    }

    function closeImportWindow() {
        importWindow.hide();
        clearUploadFile();
        inventoryListGrid.reload();
    }


    //文件类型判断及文件名显示
    function getSelectFile() {
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            var fileNameSuffix = fileList[0].name.split('.').pop();
            if (fileNameSuffix == 'xls' || fileNameSuffix == 'xlsx') {
                mini.get("fileName").setValue(fileList[0].name);
            } else {
                clearUploadFile();
                mini.alert('请上传excel文件！');
            }
        }
    }

    //触发文件选择
    function uploadFile() {
        $("#inputFile").click();
    }

    //清空文件
    function clearUploadFile() {
        $("#inputFile").val('');
        mini.get("fileName").setValue('');
    }

    //导入模板下载
    function downImportTemplate() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineer/core/BjInventory/importTemplateDownloadBjInventory.do");
        $("body").append(form);
        form.submit();
        form.remove();
    }


    function OnCellBeginEditInventory(e) {
        var record = e.record, field = e.field;
        if ((stageName == 'second'||stageName == 'third') && (record.CREATE_BY_&&record.CREATE_BY_ != currentUserId)) {
            e.cancel = true;
        }
    }

    function addFileUpload() {
        var id = mini.get("id").getValue();
        if (!id) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }
        mini.open({
            title: "添加附件",
            url: jsUseCtxPath + "/serviceEngineer/core/BjInventory/openUploadWindow.do?id=" + id,
            width: 850,
            height: 550,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (fileListGrid) {
                    fileListGrid.load();
                }
            }
        });
    }

    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnBjInventoryPreviewSpan(record.fileName, record.fileId, record.belongId, coverContent);
        var downLoadUrl = '/serviceEngineer/core/BjInventory/bjInventoryPdfPreview.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\',\'' + downLoadUrl + '\')">下载</span>';
        //增加删除按钮
        if (record.CREATE_BY_ == currentUserId && action != 'detail') {
            var deleteUrl = "/serviceEngineer/core/BjInventory/deleteBjInventoryFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }

    function returnBjInventoryPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/serviceEngineer/core/BjInventory/bjInventoryPdfPreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/serviceEngineer/core/BjInventory/bjInventoryOfficePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/serviceEngineer/core/BjInventory/bjInventoryImagePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }
</script>
</body>
</html>
