<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>外购件信息记录编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>

<body>
<div id="loading" class="loading" style="display:none;text-align:center;"><img src="${ctxPath}/styles/images/loading.gif"></div>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveBtn" class="mini-button" style="display: none" onclick="saveMaterielTypeF()">保存</a>
        <a id="typeFInfo" class="mini-button" onclick="typeFInfo()">提交</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit" id="content">
    <div class="form-container" style="margin: 0 auto; width: 100%">
        <form id="formMaterielTypeF" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <p style="font-size: 16px;font-weight: bold">申请人信息</p>
            <hr/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td colspan="2">申请人：</td>
                    <td colspan="2">
                        <input id="applyUserName" name="applyUserName" readonly class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td colspan="2">申请人部门：</td>
                    <td colspan="2">
                        <input id="applyDeptName" name="applyDeptName" readonly class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
            </table>
        </form>
        <p style="font-size: 16px;font-weight: bold;margin-top: 20px">物料信息</p>
        <hr/>
        <div style="margin-top: 5px;margin-bottom: 2px">
            <a id="addMaterielTypeFDetail" style="margin-right: 5px;display: none" class="mini-button"
               onclick="addMaterielTypeFDetail()">添加</a>
            <a id="editMaterielTypeFDetail" style="margin-right: 5px;display: none" class="mini-button"
               onclick="editMaterielTypeFDetail()">编辑</a>
            <a id="removeMaterielTypeFDetail" style="margin-right: 5px;display: none" class="mini-button"
               onclick="removeMaterielTypeFDetail()">删除</a>
            <span style="color: red">注：结果信息为"供应商与信息记录中的供应商不同;请调整基准单位和转换因子;请输入订单单位"间隔一段时候后重新提交即可</span>
        </div>
        <div id="detailListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
             allowCellWrap="true"
             idField="id" url="${ctxPath}/materielTypeF/core/getMaterielTypeFDetailList.do?belongId=${id}"
             autoload="true"
             multiSelect="false" showPager="false" showColumnsMenu="false" allowAlternating="true">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div field="supplier" width="60px" headerAlign="center" align="center">供应商代码</div>
                <div field="wlhm" width="50px" headerAlign="center" align="center">物料号码</div>
                <div field="wlms" headerAlign="center" align="center" width="70px">物料描述</div>
                <div field="gc" width="40px" headerAlign="center" align="center">工厂代码</div>
                <div field="xszz" width="40px" headerAlign="center" align="center">采购组织</div>
                <div field="materType" width="60px" headerAlign="center" align="center">采购信息记录类型</div>
                <%--<div field="productor" width="80px" headerAlign="center" align="center">制造厂商</div>--%>
                <div field="jhjhsj" width="50px" headerAlign="center" align="center">计划交货时间</div>
                <div field="cgz" width="30px" headerAlign="center" align="center">采购组</div>
                <%--<div field="limiSend" headerAlign='center' align='center' width="60">极限过度发货</div>--%>
                <div field="jg" headerAlign='center' align='center' width="40">净价</div>
                <div field="currency" headerAlign='center' align='center' width="30">币种</div>
                <div field="jgdw" width="30px" headerAlign="center" align="center">数量</div>
                <div field="dw" headerAlign='center' align='center' width="30">单位</div>
                <%--<div field="infiniteSend" headerAlign='center' align='center' width="60">无限制交货</div>--%>
                <div field="taxCode" headerAlign='center' align='center' width="30">税码</div>
                <div field="estimatePrice" headerAlign='center' align='center' width="50">是否估计价格</div>
                <div field="result" headerAlign='center' align='center' width="40" renderer="onStatusRenderer">结果代码</div>
                <div field="message" headerAlign='center' align='center' width="80">结果信息</div>
                <div field="infoNum" headerAlign='center' align='center' width="70">采购信息记录号</div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var deptName = "${deptName}";
    var detailListGrid = mini.get("detailListGrid");
    var formMaterielTypeF = new mini.Form("#formMaterielTypeF");
    var id = "${id}";

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.result;

        var arr = [
            {'key': 'S', 'value': '成功', 'css': 'green'},
            {'key': 'E', 'value': '失败', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }

    $(function () {
        if (id) {
            var url = jsUseCtxPath + "/materielTypeF/core/getMaterielTypeF.do";
            $.post(
                url,
                {id: id},
                function (json) {
                    formMaterielTypeF.setData(json);
                });
        }else {
            mini.get("applyDeptName").setValue(deptName);
            mini.get("applyUserName").setValue(currentUserName);
        }
        //明细入口
        if (action == 'detail') {
            formMaterielTypeF.setEnabled(false);
        } else if (action == 'edit' || action == 'add') {
            $("#saveBtn").show();
            $("#addMaterielTypeFDetail").show();
            $("#removeMaterielTypeFDetail").show();
            $("#editMaterielTypeFDetail").show();
        }
    });

    function valfirstId() {
        var applyUserName = $.trim(mini.get("applyUserName").getValue())
        if (!applyUserName) {
            return {"result": false, "message": "请填写申请人"};
        }
        var applyDeptName = $.trim(mini.get("applyDeptName").getValue())
        if (!applyDeptName) {
            return {"result": false, "message": "请填写申请部门"};
        }
        var detail = detailListGrid.getData();
        if (detail.length < 1) {
            return {"result": false, "message": "请添加物料信息"};
        } else {
            for (var i = 0; i < detail.length; i++) {
                if (detail[i].supplier == undefined || detail[i].supplier == "") {
                    return {"result": false, "message": "请填写供应商代码"};
                }
                if (detail[i].wlhm == undefined || detail[i].wlhm == "") {
                    return {"result": false, "message": "请填写物料号码"};
                }
                if (detail[i].wlms == undefined || detail[i].wlms == "") {
                    return {"result": false, "message": "请填写供应商代码"};
                }
                if (detail[i].materType == undefined || detail[i].materType == "") {
                    return {"result": false, "message": "请填写采购信息记录类型"};
                }
                if (detail[i].currency == undefined || detail[i].currency == "") {
                    return {"result": false, "message": "请填写币种"};
                }
                if (detail[i].taxCode == undefined || detail[i].taxCode == "") {
                    return {"result": false, "message": "请填写税码"};
                }
                if (detail[i].estimatePrice == undefined || detail[i].estimatePrice == "") {
                    return {"result": false, "message": "请填写是否估计价格"};
                }
            }
        }
        return {"result": true};
    }

    function saveMaterielTypeF() {
        // var formValfirstId = valfirstId();
        // if (!formValfirstId.result) {
        //     mini.alert(formValfirstId.message);
        //     return;
        // }
        var formData = _GetFormJsonMini("formMaterielTypeF");
        $.ajax({
            url: jsUseCtxPath + '/materielTypeF/core/saveMaterielTypeF.do?',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (returnObj) {
                if (returnObj) {
                    var message = "";
                    if (returnObj.success) {
                        message = "数据保存成功";
                        mini.alert(message, "提示信息", function () {
                            window.location.href = jsUseCtxPath + "/materielTypeF/core/editPage.do?action=edit&id=" + returnObj.data;
                        });
                    } else {
                        message = "数据保存失败，" + returnObj.message;
                        mini.alert(message, "提示信息");
                    }
                }
            }
        });
    }

    function showLoading() {
        $("#loading").css('display','');
        $("#content").css('display','none');
        $("#toolBar").css('display','none');
    }

    function hideLoading() {
        $("#loading").css('display','none');
        $("#content").css('display','');
        $("#toolBar").css('display','');
    }

    function addMaterielTypeFDetail() {
        var id = mini.get("id").getValue();
        if (!id) {
            mini.alert('请先点击‘保存’进行表单的保存！');
            return;
        }
        mini.open({
            title: "详细信息",
            url: jsUseCtxPath + "/materielTypeF/core/editMaterielTypeFDetail.do?id=" + id + "&action=add",
            width: 750,
            height: 550,
            showModal: true,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                if (detailListGrid) {
                    detailListGrid.load();
                }
            }
        });
    }

    function editMaterielTypeFDetail() {
        var id = mini.get("id").getValue();
        if (!id) {
            mini.alert('请先点击‘保存’进行表单的保存！');
            return;
        }
        var row = detailListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
        }
        if(row.result=='S'){
            mini.alert("创建成功的信息无法编辑!");
            return;
        }
        if (row) {
            var detailId = row.detailId;
            mini.open({
                title: "详细信息",
                url: jsUseCtxPath + "/materielTypeF/core/editMaterielTypeFDetail.do?detailId=" + detailId + "&action=edit&id=" + id,
                width: 750,
                height: 550,
                showModal: true,
                allowResize: true,
                onload: function () {
                },
                ondestroy: function (action) {
                    if (detailListGrid) {
                        detailListGrid.load();
                    }
                }
            });
        }
    }

    function removeMaterielTypeFDetail() {
        var id = mini.get("id").getValue();
        if (!id) {
            mini.alert('请先点击‘保存’进行表单的保存！');
            return;
        }
        var row = detailListGrid.getSelected();
        if(row.result=='S'){
            mini.alert("创建成功的信息无法删除!");
            return;
        }
        if (row) {
            var detailId = row.detailId;
            mini.confirm("确定删除选中记录？", "提示", function (action) {
                if (action != 'ok') {
                    return;
                } else {
                    _SubmitJson({
                        url: jsUseCtxPath + "/materielTypeF/core/deleteMaterielTypeFDetail.do?id=" + id,
                        method: 'POST',
                        showMsg: false,
                        data: {detailId: detailId},
                        success: function (data) {
                            if (data) {
                                mini.alert(data.message);
                                detailListGrid.reload();
                            }
                        }
                    });
                }
            });
        } else {
            mini.alert("请选中一条记录");
        }
    }

    function typeFInfo() {
        var nowDate=new Date();
        if(nowDate.getDate()=='1') {
            mini.alert("每月1号SAP ERP锁定，请稍后再试！");
            return;
        }
        var formValfirstId = valfirstId();
        if (!formValfirstId.result) {
            mini.alert(formValfirstId.message);
            return;
        }
        showLoading();
        var id = mini.get("id").getValue();
        $.ajax({
            url: jsUseCtxPath + "/materielTypeF/core/sync2SAP.do?id=" + id,
            type: "POST",
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提醒', function (action) {
                        detailListGrid.reload();
                    });
                }
            },
            complete: function () {
                hideLoading();
            }
        });
    }
</script>
</body>
</html>
