<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">资料类型：</span>
                    <input id="dataType" name="dataType"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringPurchasedPartsDataType"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">物料编码：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">物料描述：</span>
                    <input class="mini-textbox" id="materialDescription" name="materialDescription"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">所属部门:</span>
                    <input id="departmentId"
                           name="departmentId"
                           class="mini-buttonedit icon-dep-button"
                           required="true" allowInput="false"
                           onbuttonclick="selectMainDep" style="width:98%"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">科室：</span>
                    <input class="mini-textbox" id="section" name="section"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计型号：</span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">供应商：</span>
                    <input class="mini-textbox" id="supplier" name="supplier"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addBusiness()">新增</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeBusiness()">删除</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="purchasedPartsListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[100,500]" pageSize="100" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/purchasedParts/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="35" headerAlign="center" align="center">序号</div>
            <div field="dataType" width="80" headerAlign="center" align="center">资料类型</div>
            <div field="materialCode" width="100" headerAlign="center" align="center">物料编码</div>
            <div field="materialDescription" width="300" headerAlign="center" align="center">物料描述</div>
            <div field="departmentName" width="100" headerAlign="center" align="center">所属部门</div>
            <div field="section" width="100" headerAlign="center" align="center">科室</div>
            <div field="designModel" width="80" headerAlign="center" align="center">设计型号</div>
            <div field="supplier" width="100" headerAlign="center" align="center">供应商</div>
            <div field="contact" width="100" headerAlign="center" align="center">联系人</div>
            <div field="contactInformation" width="100" headerAlign="center" align="center">联系方式</div>
            <div field="productOwner" width="100" headerAlign="center" align="center">产品所责任人</div>
            <div field="collectionInformationSubmissionDate" width="105" headerAlign="center" align="center">收集信息提交日期</div>
            <div field="serviceEngineeringOwner" width="100" headerAlign="center" align="center">服务工程责任人</div>
            <div field="firstLevelDeadline" width="105" headerAlign="center" align="center">一级响应限期提供时间</div>
            <div field="firstLevelProvided" width="105" headerAlign="center" align="center" renderer="onIsOkRenderer">一级响应是否提供</div>
            <div field="secondLevelDeadline" width="105" headerAlign="center" align="center">二级响应限期提供时间</div>
            <div field="secondLevelProvided" width="105" headerAlign="center" align="center" renderer="onIsOkRenderer">二级响应是否提供</div>
            <div field="isFiled" width="100" headerAlign="center" align="center" renderer="onIsOkRenderer">是否已归档</div>
            <div field="thirdLevelProvided" width="60" headerAlign="center" align="center" renderer="onIsOkRenderer">三级响应是否提供</div>
            <div field="isMade" width="60" headerAlign="center" align="center" renderer="onIsOkRenderer">是否已制作</div>
            <div field="isNeedInform" width="220" headerAlign="center" align="center" renderer="onIsOkRenderer">是否需要下通报</div>
            <div field="remark" width="100" headerAlign="center" align="center">备注</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var purchasedPartsListGrid = mini.get("purchasedPartsListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //选择主部门
    function selectMainDep(e) {
        var b = e.sender;
        _TenantGroupDlg('1', true, '', '1', function (g) {
            b.setValue(g.groupId);
            b.setText(g.name);
        }, false);

    }
    //..
    function removeBusiness(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = purchasedPartsListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if (r.id == "") {
                        purchasedPartsListGrid.removeRow(r, false);
                    } else {
                        rowIds.push(r.id);
                    }
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/serviceEngineering/core/purchasedParts/deleteData.do",
                        method: 'POST',
                        data: {ids: rowIds.join(',')},
                        success: function (data) {
                            if (data) {
                                searchFrm();
                            }
                        }
                    });
                }
            }
        });
    }
    //..
    function addBusiness() {
        var url = jsUseCtxPath + "/serviceEngineering/core/purchasedParts/editPage.do?businessId=&action=add";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (purchasedPartsListGrid) {
                    purchasedPartsListGrid.reload();
                }
            }
        }, 1000);
    }
    //..
    purchasedPartsListGrid.on("rowdblclick", function (e) {
        var record = e.record;
        var businessId = record.id;
        editBusiness(businessId);

    });
    //..
    function editBusiness(businessId) {
        var url = jsUseCtxPath + "/serviceEngineering/core/purchasedParts/editPage.do?businessId=" + businessId + "&action=edit";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (purchasedPartsListGrid) {
                    purchasedPartsListGrid.reload();
                }
            }
        }, 1000);
    }
    //渲染是否列
    function onIsOkRenderer(e) {
        return e.value == "true" ? "是" : "否";
    }
</script>
<redxun:gridScript gridId="purchasedPartsListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>