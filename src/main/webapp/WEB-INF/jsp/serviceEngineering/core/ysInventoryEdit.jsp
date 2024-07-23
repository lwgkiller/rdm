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
    <link href="${ctxPath}/scripts/mini/miniui/themes/icons.css" rel="stylesheet" type="text/css" />
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
        <form id="ysInventoryForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption>
                    易损件清单需求申请
                </caption>
                <tr>
                    <td style="text-align: center;width: 20%">编制人：</td>
                    <td style="min-width:170px">
                        <input id="apply" name="applyId" textname="applyName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                    <td style="text-align: center;width: 20%">流程编号(自动生成):</td>
                    <td colspan="1">
                        <input id="processNum" name="processNum" class="mini-textbox" readonly style="width:98%;"/>
                    </td>
                <tr>
                <tr>
                    <td style="text-align: center;width: 20%">设计型号：</td>
                    <td>
                        <input id="designModel" style="width:98%;" class="mini-buttonedit" showClose="true"
                               oncloseclick="onRelModelCloseClick()"
                               name="designModelId" textname="designModelName" allowInput="false"
                               onbuttonclick="selectModelClick()"/>
                    </td>
                    <td style="text-align: center;width: 12%">整机物料号：
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
                    <td style="text-align: center;width: 20%">产品主管：</td>
                    <td style="min-width:170px">
                        <input id="cpzg" name="cpzgId" textname="cpzgName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">服务工程编制人员：</td>
                    <td style="min-width:170px">
                        <input id="fwEdit" name="fwEditId" textname="fwEditName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
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
            <a id = "importYsInventory" class="mini-button" style="margin-right: 5px;display: none" plain="true" onclick="importYsInventory()">导入</a>
            <a id = "exportYsInventory" class="mini-button" style="margin-right: 5px;display: none" plain="true" onclick="exportYsInventory()">导出</a>
            <span style="color: red">注：导入会删除清单详情中所有的数据,请确保每次导入的数据为全部数据</span>
        </div>
        <div id="inventoryListGrid" class="mini-datagrid" style="height:500px" autoLoad="true"
               allowCellEdit="true" allowCellWrap="true"
             idField="id" url="${ctxPath}/serviceEngineer/core/YsInventory/queryDetail.do?belongId=${id}"
             allowResize="false" allowCellSelect="true" allowSortColumn="false" allowHeaderWrap="true"
             multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false" style="height: 500px"
              showVGridLines="true">
            <div property="columns">
                <div type="checkcolumn" width="40"></div>
                <div type="indexcolumn" width="50" headerAlign="center" align="center">序号</div>
                <div field="zcPart" width="200px" headerAlign="center" align="center">总成部件
                    <input property="editor" class="mini-textbox"/></div>
                <div field="materiel" width="200px" headerAlign="center" align="center">物料
                    <input property="editor" class="mini-textbox"/></div>
                <div field="zcMaterielCode" width="200px" headerAlign="center" align="center">总成物料编码
                    <input property="editor" class="mini-textbox"/></div>
                <div field="zcMaterielContent" width="200px" headerAlign="center" align="center">总成物料描述
                    <input property="editor" class="mini-textbox"/></div>
                <div field="materielCode" width="200px" headerAlign="center" align="center">物料编码
                    <input property="editor" class="mini-textbox"/></div>
                <div field="materielContent" width="150px" headerAlign="center" align="center">物料描述
                    <input property="editor" class="mini-textbox"/></div>
                <div field="num" width="100px" headerAlign="center" align="center">数量/台
                    <input property="editor" class="mini-textbox"/></div>
                <div field="note" headerAlign="center" align="center" width="100px">备注
                    <input property="editor" class="mini-textbox"/></div>
            </div>
        </div>
    </div>
</div>
<form id="excelForm" action="${ctxPath}/serviceEngineer/core/YsInventory/exportYsInventoryList.do?belongId=${id}" method="post" target="excelIFrame">
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
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">易损件清单导入模板.xlsx</a>
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
    var ysInventoryForm = new mini.Form("#ysInventoryForm");
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

    var stageName = "";
    $(function () {
        if (id) {
            var url = jsUseCtxPath + "/serviceEngineer/core/YsInventory/getYsInventoryDetail.do";
            $.post(
                url,
                {id: id},
                function (json) {
                    ysInventoryForm.setData(json);
                });
        }else {
            mini.get("apply").setValue(currentUserId);
            mini.get("apply").setText(currentUserName);
        }
        if (action == 'task') {
            taskActionProcess();
        } else if (action == "detail") {
            ysInventoryForm.setEnabled(false);
            inventoryListGrid.setAllowCellEdit(false);
            mini.get("addInventoryDetail").setEnabled(false);
            mini.get("removeInventoryDetail").setEnabled(false);
            $("#exportYsInventory").show();
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        } else if (action == "edit") {
            $("#importYsInventory").show();
            $("#exportYsInventory").show();
            $("#addInventoryDetail").show();
            $("#removeInventoryDetail").show();
            mini.get("addInventoryDetail").setEnabled(true);
            mini.get("removeInventoryDetail").setEnabled(true);
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
        ysInventoryForm.setEnabled(false);
        inventoryListGrid.setAllowCellEdit(false);
        mini.get("addInventoryDetail").setEnabled(false);
        mini.get("removeInventoryDetail").setEnabled(false);
        mini.get("importYsInventory").setEnabled(false);
        mini.get("exportYsInventory").setEnabled(false);
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'stageName') {
                stageName = nodeVars[i].DEF_VAL_;
            }
        }
        if (stageName == 'first') {
            $("#addInventoryDetail").show();
            $("#removeInventoryDetail").show();
            $("#importYsInventory").show();
            $("#exportYsInventory").show();
            ysInventoryForm.setEnabled(true);
            inventoryListGrid.setAllowCellEdit(true);
            mini.get("addInventoryDetail").setEnabled(true);
            mini.get("removeInventoryDetail").setEnabled(true);
            mini.get("importYsInventory").setEnabled(true);
            mini.get("exportYsInventory").setEnabled(true);
        } else {
            ysInventoryForm.setEnabled(false);
            inventoryListGrid.setAllowCellEdit(false);
            mini.get("addInventoryDetail").setEnabled(false);
            mini.get("removeInventoryDetail").setEnabled(false);
        }

    }


    //保存草稿
    function saveApplyInfo(e) {

        window.parent.saveDraft(e);
    }

    //流程引擎调用此方法进行表单数据的获取
    function getData() {
        var formData = _GetFormJsonMini("ysInventoryForm");
        formData.inventory = inventoryListGrid.getChanges();
        return formData;
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
        var cpzg = $.trim(mini.get("cpzg").getValue());
        if (!cpzg) {
            return {"result": false, "message": "请选择产品主管"};
        }
        var fwEdit = $.trim(mini.get("fwEdit").getValue());
        if (!fwEdit) {
            return {"result": false, "message": "请选择服务工程编制人员"};
        }
        var inventoryListGridData = inventoryListGrid.getData();
        if (inventoryListGridData.length == 0) {
            return {"result": false, "message": "请填写清单明细"};
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
            mini.get("cpzg").setValue(rowSelected.productManagerId);
            mini.get("cpzg").setText(rowSelected.productManagerName);
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
        mini.get("cpzg").setValue('');
        mini.get("cpzg").setText('');
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
            var row = selecteds[i];
            devareArr.push(row);
        }
        inventoryListGrid.removeRows(devareArr);
    }

    function exportYsInventory() {
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
        excelForm.submit();
    }

    //导入
    function importYsInventory() {
        if (!id) {
            mini.alert("请先保存草稿！");
            return
        }
        mini.confirm("导入会删除清单详情中所有的数据,请确保每次导入的数据为全部数据","提醒",function(action) {
            if (action != 'ok') {
                return;
            } else {
                importWindow.show();
            }
        });
    }

    //上传批量导入
    function importMaterial() {
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
            xhr.open('POST', jsUseCtxPath + '/serviceEngineer/core/YsInventory/importExcelYsInventory.do?id='+id, false);
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
            if (fileNameSuffix == 'xls'||fileNameSuffix == 'xlsx') {
                mini.get("fileName").setValue(fileList[0].name);
            }
            else {
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
        form.attr("action", jsUseCtxPath + "/serviceEngineer/core/YsInventory/importTemplateDownloadYsInventory.do");
        $("body").append(form);
        form.submit();
        form.remove();
    }


</script>
</body>
</html>
