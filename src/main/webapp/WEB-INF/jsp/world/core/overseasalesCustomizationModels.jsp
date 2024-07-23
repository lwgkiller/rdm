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
                    <span class="text" style="width:auto">机型大类：</span>
                    <input id="productGroup" name="productGroup"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=overseaSalesCustomizationProductGroup"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">设计型号：</span>
                    <input class="mini-textbox" id="designModel" name="designModel" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">整机物料号：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">销售型号：</span>
                    <input class="mini-textbox" id="salsesModel" name="salsesModel" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">出口区域：</span>
                    <input class="mini-textbox" id="saleArea" name="saleArea" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">出口国家：</span>
                    <input class="mini-textbox" id="saleCountry" name="saleCountry" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">发动机型号：</span>
                    <input class="mini-textbox" id="engine" name="engine" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">额定功率：</span>
                    <input class="mini-textbox" id="ratedPower" name="ratedPower" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">铲斗容量：</span>
                    <input class="mini-textbox" id="bucketCapacity" name="bucketCapacity" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">工作质量：</span>
                    <input class="mini-textbox" id="operatingMass" name="operatingMass" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">责任人：</span>
                    <input class="mini-textbox" id="responsibleUser" name="responsibleUser" style="width:100%;"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a id="addBusiness" class="mini-button" style="margin-right: 5px" plain="true" onclick="addBusiness()">新增</a>
                    <a id="deleteBusiness" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="deleteBusiness()">删除</a>
                    <a id="copyBusiness" class="mini-button" style="margin-right: 5px" plain="true" onclick="copyBusiness()">复制</a>
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
         url="${ctxPath}/world/core/overseaSalesCustomization/modelListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="40" trueValue="1" falseValue="0"></div>
            <div field="orderNo" width="60" headerAlign="center" align="center">顺序号</div>
            <div name="action" cellCls="actionIcons" width="85" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="productGroup" width="130" headerAlign="center" align="center" renderer="productGroupRender">机型大类</div>
            <div field="designModel" width="130" headerAlign="center" align="center">设计型号</div>
            <div field="materialCode" width="130" headerAlign="center" align="center">整机物料号</div>
            <div field="salsesModel" width="130" headerAlign="center" align="center">销售型号</div>
            <div field="saleArea" width="130" headerAlign="center" align="center">出口区域</div>
            <div field="saleCountry" width="130" headerAlign="center" align="center">出口国家</div>
            <div field="engine" width="150" headerAlign="center" align="center">发动机型号</div>
            <div field="ratedPower" width="130" headerAlign="center" align="center">额定功率</div>
            <div field="bucketCapacity" width="130" headerAlign="center" align="center">铲斗容量</div>
            <div field="operatingMass" width="130" headerAlign="center" align="center">工作质量</div>
            <div field="responsibleUser" width="130" headerAlign="center" align="center">责任人</div>
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
    var isOverseaSalesCustomizationAdmins = "${isOverseaSalesCustomizationAdmins}";
    var isOverseaSalesCustomizationModelAdmin = "${isOverseaSalesCustomizationModelAdmin}";
    //..
    $(function () {
        if (currentUserNo != "admin") {
            //admin 不做限制
            if (isOverseaSalesCustomizationAdmins == "true") {
                //业务管理员 不做限制
            } else {
                //锁 删除
                mini.get("deleteBusiness").setEnabled(false);
            }
        }
    });
    //..
    function addBusiness() {
        var url = jsUseCtxPath + "/world/core/overseaSalesCustomization/modelEditPage.do?businessId=&action=add";
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
        var url = jsUseCtxPath + "/world/core/overseaSalesCustomization/modelEditPage.do?businessId=" + businessId + "&action=edit";
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
        var url = jsUseCtxPath + "/world/core/overseaSalesCustomization/modelEditPage.do?businessId=" + businessId + "&action=detail";
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
                mini.confirm("删除记录将清空所有BOM配置和客户信息，确定删除？", "提示", function (action) {
                    if (action != 'ok') {
                        return;
                    } else {
                        var rowIds = [];
                        for (var i = 0, l = rows.length; i < l; i++) {
                            var r = rows[i];
                            rowIds.push(r.id);
                        }
                        _SubmitJson({
                            url: jsUseCtxPath + "/world/core/overseaSalesCustomization/deleteModel.do",
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
    function copyBusiness() {
        var row = businessListGrid.getSelected();
        if (!row) {
            mini.alert("请至少选中一条记录");
            return;
        }
        _SubmitJson({
            url: jsUseCtxPath + "/world/core/overseaSalesCustomization/copyModel.do?id=" + row.id,
            method: 'GET',
            postJson: false,
            showMsg: false,
            success: function (returnData) {
                if (returnData.success) {
                    mini.alert(returnData.message);
                    searchFrm();
                } else {
                    mini.alert("复制失败:" + returnData.message);
                }
            },
            fail: function (returnData) {
                mini.alert("复制失败:" + returnData.message);
            }
        });
    }
    //..
    function configBusiness(businessId) {
        var url = jsUseCtxPath + "/world/core/overseaSalesCustomization/configPage.do?businessId=" + businessId + "&action=edit";
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
        if (currentUserNo == 'admin' || isOverseaSalesCustomizationAdmins == "true" ||
            currentUserId == record.responsibleUserId || isOverseaSalesCustomizationModelAdmin == "true") {
            s += '<span  title="编辑" onclick="editBusiness(\'' + businessId + '\')">编辑</span>';
            s += '<span  title="配置BOM" onclick="configBusiness(\'' + businessId + '\')">配置BOM</span>';
        }
        return s;
    }
    //..节点类型渲染
    function productGroupRender(e) {
        return dicKeyValueRender(e, productGroupList);
    }
    //..
    function dicKeyValueRender(e, keyValueList) {
        var record = e.record;
        var key = record[e.field];
        var arr = mini.decode(keyValueList);
        return $.formatItemValue(arr, key);
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>