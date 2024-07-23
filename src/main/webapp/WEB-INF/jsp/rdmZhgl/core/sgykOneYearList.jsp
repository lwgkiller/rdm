<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>业务交流维度配置</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">年份: </span>
                    <input id="signYear" name="signYear"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportExcel()">导出</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="sgykOneYearGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/zhgl/core/sgyk/sgykOneYearListQuery.do"
         idField="id" allowAlternating="true" showPager="false" multiSelect="false" allowCellEdit="true"
         allowCellSelect="true" editNextRowCell="true" showColumnsMenu="true">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div field="signYear" name="signYear" width="100" headerAlign="center" align="center">年份
            </div>
            <div field="normClass" name="normClass" width="100" headerAlign="center" align="center">指标类别
            </div>
            <div field="normDesp" name="normDesp" width="250" headerAlign="center" align="center">指标描述
            </div>
            <div field="normKey" name="normKey" width="100" headerAlign="center" align="center"
                 visible="false">指标编码
            </div>
            <div field="January" name="January" width="100" headerAlign="center" align="center">1月
            </div>
            <div field="February" name="February" width="100" headerAlign="center" align="center">2月
            </div>
            <div field="March" name="March" width="100" headerAlign="center" align="center">3月
            </div>
            <div field="April" name="April" width="100" headerAlign="center" align="center">4月
            </div>
            <div field="May" name="May" width="100" headerAlign="center" align="center">5月
            </div>
            <div field="June" name="June" width="100" headerAlign="center" align="center">6月
            </div>
            <div field="July" name="July" width="100" headerAlign="center" align="center">7月
            </div>
            <div field="August" name="August" width="100" headerAlign="center" align="center">8月
            </div>
            <div field="September" name="September" width="100" headerAlign="center" align="center">9月
            </div>
            <div field="October" name="October" width="100" headerAlign="center" align="center">10月
            </div>
            <div field="November" name="November" width="100" headerAlign="center" align="center">11月
            </div>
            <div field="December" name="December" width="100" headerAlign="center" align="center">12月
            </div>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/zhgl/core/sgyk/exportOneYearList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserNo = "${currentUserNo}";
    var sgykOneYearGrid = mini.get("sgykOneYearGrid");
    var currentYear = "";
    //..
    $(function () {
        var date = new Date();
        currentYear = date.getFullYear().toString();
        mini.get("signYear").setValue(currentYear);
        searchFrm()
    });
    //..
    function exportExcel() {
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
</script>
<redxun:gridScript gridId="sgykOneYearGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>