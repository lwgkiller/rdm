<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>物料列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">申请单号: </span>
                    <input name="applyId" class="mini-textbox rxc" allowInput="true" style="width:100%;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">物料预留号: </span>
                    <input name="materialCode" class="mini-textbox rxc" allowInput="true" style="width:100%;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">申请人: </span>
                    <input name="userName" class="mini-textbox rxc" allowInput="true" style="width:100%;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">申请部门: </span>
                    <input name="deptName" class="mini-textbox rxc" allowInput="true" style="width:100%;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">申请类型: </span>
                    <input id="applyType" name="applyType" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100%;height:34px" label="申请类型："
                           length="50" onvaluechanged="searchFrm()"
                           only_read="false" required="false" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=materialType"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
            </ul>
        </form>
        <ul class="toolBtnBox">
            <div style="display: inline-block" class="separator"></div>
            <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="doInvalid()">作废</a>
            <div style="display: inline-block" class="separator"></div>
            <a class="mini-button" style="margin-right: 5px" plain="true" onclick="asyncStatus()">同步状态</a>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height:  100%;">
    <div id="listGrid" class="mini-datagrid" allowResize="true" style="height:  100%;" sortField="UPDATE_TIME_"
         sortOrder="desc"
         url="${ctxPath}/rdmZhgl/core/material/listDetail.do" idField="id" showPager="true" allowCellWrap="false"
         multiSelect="true" showColumnsMenu="false" sizeList="[15,20,50,100,200]" pageSize="15" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" field="mainId" name="mainId" width="30px"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="40px">序号</div>
<%--            <div field="id" name="id" name="action" cellCls="actionIcons" width="80px" headerAlign="center"--%>
<%--                 align="center"--%>
<%--                 renderer="onActionRenderer" cellStyle="padding:0;">--%>
<%--                操作--%>
<%--            </div>--%>
            <div field="itemCode" name="itemCode" width="100px" headerAlign="center" align="center"
                 allowSort="false">物料号
            </div>
            <div field="totalNum" name="totalNum" width="100px" headerAlign="center" align="center"
                 allowSort="false">总数量
            </div>
            <div field="delaNum" name="delaNum" width="80px" headerAlign="center" align="center">未处理数量</div>
            <div field="finishFlagText" width="100px" headerAlign="center" align="center" allowSort="false">完成标志</div>
            <div field="delFlagText" width="100px" headerAlign="center" align="center" allowSort="false">删除标志</div>
            <div field="finalDate" name="finalDate" width="100px" headerAlign="center" align="center" allowSort="false">
                到期日期
            </div>
            <div field="diffDay" width="100px" headerAlign="center" align="center" allowSort="false"
                 renderer="onRiskRenderer">风险
            </div>
            <div field="lineNo" name="lineNo" width="100px" headerAlign="center" align="center" allowSort="false">
                SAP行号
            </div>
            <div field="materialCode" name="materialCode" width="100px" headerAlign="center" align="center"
                 allowSort="false">物料预留号
            </div>
            <div field="applyType" name="applyType" width="100px" headerAlign="center" align="center" allowSort="false"
                 renderer="onMaterialType">申请类型
            </div>
            <div field="userName" name="userName" width="80px" headerAlign="center" align="center" allowSort="false">
                申请人
            </div>
            <div field="deptName" name="deptName" width="100px" headerAlign="center" align="center" allowSort="false">
                申请部门
            </div>
            <div field="applyId" name="applyId" width="180px" headerAlign="center" align="center" allowSort="false">
                申请单号
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUser.userId}";
    var listGrid = mini.get("listGrid");
    var materialTypeList = getDics("materialType");
    var permission = ${permission};
    listGrid.on("load", function () {
        listGrid.mergeColumns(["id", "lineNo", "userName", "deptName", "applyId"]);
    });

    function doInvalid() {
        var rows = [];
        rows = listGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定作废选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var materialCodeList = [];
                var lineNoList = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if (r.finishFlag != '1' && r.delFlag != '1' && r.lineNo && r.materialCode){
                        materialCodeList.push(r.materialCode);
                        lineNoList.push(r.lineNo);
                    }
                }
                if (materialCodeList.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/rdmZhgl/core/material/invalidItems.do",
                        method: 'POST',
                        data: {materialCodes: materialCodeList.join(','),lineNos: lineNoList.join(',')},
                        success: function (text) {
                            searchFrm()
                        }
                    });
                }else{
                    mini.alert("已删除、已完成、物料预留号为空的不能作废");
                }
            }
        });
    }
    function asyncStatus() {
        var rows = [];
        rows = listGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定同步？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var materialCodeList = [];
                var lineNoList = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if (r.finishFlag != '1' && r.delFlag != '1' && r.lineNo && r.materialCode){
                        materialCodeList.push(r.materialCode);
                        lineNoList.push(r.lineNo);
                    }
                }
                if (materialCodeList.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/rdmZhgl/core/material/asyncStatus.do",
                        method: 'POST',
                        data: {materialCodes: materialCodeList.join(','),lineNos: lineNoList.join(',')},
                        success: function (text) {
                            searchFrm()
                        }
                    });
                }else{
                    mini.alert("已删除、已完成、物料预留号为空的不能同步");
                }
            }
        });
    }

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.mainId;
        var creator = record.CREATE_BY_;
        var finishFlag = record.finishFlag;
        var delFlag = record.delFlag;
        var s = '';
        if ((currentUserId != creator && !permission) || finishFlag == '1' || delFlag == '1') {
            s += '<span  title="作废" style="color: silver">作废</span>';
        } else {
            s = '<span  title="作废" onclick="doInvalid(\'' + id + '\')">作废</span>';
        }
        return s;
    }

    function onMaterialType(e) {
        var record = e.record;
        var applyType = record.applyType;
        var resultText = '';
        for (var i = 0; i < materialTypeList.length; i++) {
            if (materialTypeList[i].key_ == applyType) {
                resultText = materialTypeList[i].text;
                break
            }
        }
        return resultText;
    }

    function onRiskRenderer(e) {
        var record = e.record;
        var diffDay = record.diffDay;
        var finishFlag = record.finishFlag;
        var delFlag = record.delFlag;

        var color = '#32CD32';
        var title = '正常';
        if (finishFlag == '1' || delFlag == '1') {
            color = '#EEEE00';
            title = '物料已完成或者已删除';
        } else if (diffDay > 5) {
            color = '#32CD32';
            title = '正常';
        } else {
            color = '#fb0808';
            title = '物料延期超过5天';
        }

        var s = '<span title= "' + title + '" style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + color + '"></span>';
        return s;
    }

</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
