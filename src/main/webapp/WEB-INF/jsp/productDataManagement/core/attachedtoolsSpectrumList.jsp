<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<%--工具栏--%>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">属具类型：</span>
                    <input id="attachedtoolsType" name="attachedtoolsType"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByTreeKeyTopOne.do?dicKey=attachedtoolsType"
                           valueField="key" textField="value" onvaluechanged="attachedtoolsTypeChanged"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">属具子类型：</span>
                    <input id="attachedtoolsType2" name="attachedtoolsType2"
                           class="mini-combobox" style="width:98%" showNullItem="true"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a id="addBusiness" class="mini-button" style="margin-right: 5px" plain="true" onclick="addBusiness()">新增</a>
                    <a id="deleteBusiness" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="deleteBusiness()">删除</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<%--列表视图--%>
<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" idField="id" class="mini-datagrid" style="width: 100%; height: 100%;"
         allowCellWrap="true" showCellTip="true" allowCellEdit="true" allowCellSelect="true"
         allowResize="true" allowAlternating="true" showColumnsMenu="false" multiSelect="false"
         sizeList="[50,100]" pageSize="50" pagerButtons="#pagerButtons" sortField="orderNo" sortOrder="asc"
         url="${ctxPath}/productDataManagement/core/attachedtoolsSpectrum/modelListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="40" trueValue="1" falseValue="0"></div>
            <div field="orderNo" width="40" headerAlign="center" align="center">顺序号</div>
            <div name="action" cellCls="actionIcons" width="85" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="attachedtoolsType" width="130" headerAlign="center" align="center">属具类型</div>
            <div field="attachedtoolsType2" width="130" headerAlign="center" align="center">属具子类型</div>
            <div field="functionIntroduction" headerAlign="center" align="center" renderer="render">功能简介</div>
        </div>
    </div>
</div>
<%--JS--%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var productGroupList = '${productGroupList}';
    var businessListGrid = mini.get("businessListGrid");
    var isAttachedtoolsSpectrumAdmin = "${isAttachedtoolsSpectrumAdmin}";
    //..
    $(function () {
        if (currentUserNo != "admin" && isAttachedtoolsSpectrumAdmin == "false") {
            mini.get("addBusiness").setEnabled(false);
            mini.get("deleteBusiness").setEnabled(false);
        }
    });
    //..
    function addBusiness() {
        var url = jsUseCtxPath + "/productDataManagement/core/attachedtoolsSpectrum/modelEditPage.do?businessId=&action=add";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
                }
            }
        }, 1000);
    }
    //..
    function editBusiness(businessId) {
        var url = jsUseCtxPath + "/productDataManagement/core/attachedtoolsSpectrum/modelEditPage.do?businessId=" + businessId + "&action=edit";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
                }
            }
        }, 1000);
    }
    //..
    function detailBusiness(businessId) {
        var url = jsUseCtxPath + "/productDataManagement/core/attachedtoolsSpectrum/modelEditPage.do?businessId=" + businessId + "&action=detail";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
                }
            }
        }, 1000);
    }
    //..
    function deleteBusiness(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = businessListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                mini.confirm("删除记录将清空所有明细信息，确定删除？", "提示", function (action) {
                    if (action != 'ok') {
                        return;
                    } else {
                        var rowIds = [];
                        for (var i = 0, l = rows.length; i < l; i++) {
                            var r = rows[i];
                            rowIds.push(r.id);
                        }
                        _SubmitJson({
                            url: jsUseCtxPath + "/productDataManagement/core/attachedtoolsSpectrum/deleteModel.do",
                            method: 'POST',
                            data: {ids: rowIds.join(',')},
                            postJson: false,
                            showMsg: false,
                            success: function (returnData) {
                                if (returnData.success) {
                                    mini.alert(returnData.message);
                                    searchFrm();
                                } else {
                                    mini.alert("删除失败:" + returnData.message);
                                }
                            },
                            fail: function (returnData) {
                                mini.alert("删除失败:" + returnData.message);
                            }
                        });
                    }
                });
            }
        });
    }
    //..
    function configBusiness(businessId) {
        var url = jsUseCtxPath + "/productDataManagement/core/attachedtoolsSpectrum/configPage.do?mainId=" + businessId + "&action=edit";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
                }
            }
        }, 1000);
    }
    //..
    function onActionRenderer(e) {
        var record = e.record;
        var businessId = record.id;
        var s = '<span  title="查看" onclick="detailBusiness(\'' + businessId + '\')">查看</span>';
        if (currentUserNo == 'admin' || isAttachedtoolsSpectrumAdmin == "true") {
            s += '<span  title="编辑" onclick="editBusiness(\'' + businessId + '\')">编辑</span>';
        }
        s += '<span  title="配置明细" onclick="configBusiness(\'' + businessId + '\')">配置明细</span>';
        return s;
    }
    //..
    function attachedtoolsTypeChanged() {
        var parent = mini.get('attachedtoolsType').getSelected();
        mini.get('attachedtoolsType2').setValue("");
        if (parent) {
            var url = '${ctxPath}/sys/core/sysDic/getByParentId.do?parentId=' + parent.dicId;
            mini.get('attachedtoolsType2').setUrl(url);
        }
    }
    //..
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>