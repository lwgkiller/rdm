<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/serviceEngineering/standardvalueShipmentnotmadeList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto; padding-left: 15px;">产品所：</span>
                    <input class="mini-textbox" id="department" name="department"/>
                </li>
                <li style="margin-left:15px">
                    <span class="text" style="width:auto">物料编码：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto;padding-left: 15px;">设计型号：</span>
                    <input class="mini-textbox" id="materialName" name="materialName"/>
                </li>
                <br/>
                <li style="margin-right: 15px; padding-left: 15px;">
                    <span class="text" style="width:auto">版本类型：</span>
                    <input id="versionType" name="versionType" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[{key:'cgb',value:'常规版'},{key:'csb',value:'测试版'},{key:'wzb',value:'完整版'}]"
                    />
                </li>
                <li style="margin-right: 15px;">
                    <span class="text" style="width:auto">完成情况：</span>
                    <%--<input class="mini-textbox" id="betaCompletion" name="betaCompletion" value="dzz"/>--%>
                    <input id="betaCompletion" name="betaCompletion" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..." value="dzz"
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[{key: 'dzz',value:'待制作'},{key: 'zzing',value:'制作中'},{key: 'zzwc',value:'制作完成'}]"
                    />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearQuery()">清空查询</a>
                </li>
            </ul>
        </form>
        <%--<span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">--%>
				<%--<i class="icon-sc-lower"></i>--%>
        <%--</span>--%>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="jxbzzbshxzwlbmGrid" class="mini-datagrid" style="width: 100%; height: 100%;"
         showCellTip="true" idField="id" allowCellSelect="true"
         showColumnsMenu="false" sizeList="[20,50,100]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/standardvalue/shipmentnotmade/dataListQuery.do" >
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="35" headerAlign="center" align="center">序号</div>
            <div field="department" width="80" headerAlign="center" align="center">产品所</div>
            <div field="salesModel" name="salesModel" width="120" headerAlign="center" align="center">销售型号</div>
            <div field="materialCode" name="materialCode" width="120" headerAlign="center" align="center">物料编码</div>
            <div field="materialName" name="materialName" width="120" headerAlign="center" align="center">设计型号</div>
            <div field="versionType" headerAlign="center" align="center" allowSort="false" renderer="versionTypeRenderer">版本类型</div>
            <%--<div field="pinFour" name="pinFour" width="120" headerAlign="center" align="center">适用第四位PIN码</div>--%>
            <div field="principal" name="principal" width="120" headerAlign="center" align="center">产品主管编制负责人</div>
            <div field="betaCompletion" name="betaCompletion" width="120" headerAlign="center" align="center" renderer="completionRenderer">完成情况</div>
            <%--<div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center" allowSort="true">创建时间</div>--%>
        </div>
    </div>
</div>
<div class="mini-toolbar" style="text-align: center;padding-top: 8px;padding-bottom: 8px;">
    <a class="mini-button" onclick="onOk()">确定</a>
    <a class="mini-button btn-red" onclick="onCancel()">取消</a>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var jxbzzbshxzwlbmGrid = mini.get("jxbzzbshxzwlbmGrid");
    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    $(function () {
        searchFrm();
    });

    function completionRenderer(e) {
        var record = e.record;
        switch(record.betaCompletion) {
            case 'dzz':
                return '<span style="color:orange">待制作</span>';
            case 'zzing':
                return '<span style="color:#4BFF19">制作中</span>';
            case 'zzwc':
                return '<span style="color:#177BFF">制作完成</span>';
        }
    }

    function onOk(){
        var rows = getMaterialCode();
        if(!rows){
            alert("请选择数据!")
            return;
        }
        CloseWindow('ok');
    }

    function onCancel(){
        CloseWindow('cancel');
    }

    function getMaterialCode() {
        var rows = jxbzzbshxzwlbmGrid.getSelected();
        return rows;
    }
    function clearQuery() {
        mini.get("materialCode").setValue("");
        mini.get("materialName").setValue("");
        mini.get("versionType").setValue("");
        mini.get("betaCompletion").setValue("");
        searchFrm();
    }
    function versionTypeRenderer(e) {
        var record = e.record;
        var arr = [{key:'cgb',value:'常规版'},{key:'csb',value:'测试版'},{key:'wzb',value:'完整版'}];
        return $.formatItemValue(arr,record.versionType);
    }
</script>
<redxun:gridScript gridId="jxbzzbshxzwlbmGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>