<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>名称列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/wwrz/wwrzMoneyList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">型号: </span><input class="mini-textbox" style="width: 120px" name="productModel"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">产品主管: </span><input class="mini-textbox" style="width: 120px" name="chargerName"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">合同编号: </span><input class="mini-textbox" style="width: 120px" name="contractCode"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">发票编号: </span><input class="mini-textbox" style="width: 120px" name="invoiceCode"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">是否展示全部: </span>
                    <input id="showAll" name="showAll" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100px;height:34px" label="是否展示全部："
                           length="50" onvaluechanged="searchFrm()"
                           only_read="false" required="false" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button" style="margin-left: 5px" plain="true" onclick="exportExcel()">按条件导出</a>
                </li>
            </ul>
        </form>
        <!--导出Excel相关HTML-->
        <form id="excelForm" action="${ctxPath}/wwrz/core/money/exportExcel.do" method="post"
              target="excelIFrame">
            <input type="hidden" name="pageIndex" id="pageIndex"/>
            <input type="hidden" name="pageSize" id="pageSize"/>
            <input type="hidden" name="filter" id="filter"/>
        </form>
        <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
        <ul class="toolBtnBox">
            <div style="display: inline-block" class="separator"></div>
            <a id="addButton" class="mini-button" style="margin-right: 5px" plain="true" img="${ctxPath}/scripts/mini/miniui/res/images/add.png" onclick="addItems()">新增</a>
            <a id="delButton" class="mini-button"  img="${ctxPath}/scripts/mini/miniui/res/images/cancel.png" onclick="removeRow()">删除</a>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/wwrz/core/money/listData.do" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="40px"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="50px">序号</div>
            <div name="action" cellCls="actionIcons" width="150px" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="productModel" width="150px" headerAlign="center" align="center" allowSort="false">销售型号</div>
            <div field="productType" width="120px" headerAlign="center" align="center" allowSort="false" renderer="onProductType">产品类型</div>
            <div field="cabForm" width="120px" headerAlign="center" align="center" allowSort="false" renderer="onCabForm">司机室形式</div>
            <div field="chargerName" width="120px" headerAlign="center" align="center" allowSort="true"  >产品主管</div>
            <div field="itemNames" width="250px" headerAlign="center" align="left" allowSort="true" >认证项目</div>
            <div field="contractCode" width="150px" headerAlign="center" align="center" allowSort="true">合同编号</div>
            <div field="money" width="150px" headerAlign="center" align="center" allowSort="true">费用金额</div>
            <div field="invoiceCode" width="150px" headerAlign="center" align="center" allowSort="true">发票编号</div>
            <div field="paymentDate" width="150px" headerAlign="center" align="center" allowSort="true">付款日期</div>
            <div field="reportCode" width="150px" headerAlign="center" align="center" allowSort="true">报告</div>
            <div field="certCode" width="150px" headerAlign="center" align="center" allowSort="true">证书</div>
            <div field="documentCode" width="150px" headerAlign="center" align="center" allowSort="true">归档单号</div>
            <div field="companyCode" width="150px" headerAlign="center" align="center" allowSort="true">认证公司代号</div>
            <div field="creator" width="150px" headerAlign="center" align="center" allowSort="true">创建人</div>
            <div field="applyId" width="200px" headerAlign="center" align="center" allowSort="true">流程编号</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var listGrid = mini.get("listGrid");
    var currentUserId = "${currentUser.userId}";
    var permission = ${permission};
    var productTypeList = getDics("CPLX");
    var cabFormList = getDics("SJSXS");
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
