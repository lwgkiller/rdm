<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>年度绩效列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/portrait/document/portraitYearScoreList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">所属部门: </span><input class="mini-textbox" style="width: 150px" name="deptName"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">姓名: </span><input class="mini-textbox" style="width: 150px" name="userName"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">岗位: </span><input class="mini-textbox" style="width: 150px" name="post"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">职级: </span><input class="mini-textbox" style="width: 150px" name="duty"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">年度: </span>
                    <input id="reportYear" name="reportYear" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100%;height:34px"  label="年度："
                           length="50"
                           only_read="false" required="true" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false" onvaluechanged="searchFrm()"
                           textField="text" valueField="value" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getYearList.do"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
            </ul>
        </form>
        <ul class="toolBtnBox">
            <div style="display: inline-block" class="separator"></div>
            <a id="yearButton" class="mini-button" plain="true" img="${ctxPath}/scripts/mini/miniui/res/images/add.png" onclick="openEditWindow()">年度绩效生成</a>
            <span style="color: red">生成年度绩效</span>
            <div style="display: inline-block" class="separator"></div>
            <a class="mini-button" style="margin-left: 5px" plain="true" onclick="exportBtn()">导出</a>
        </ul>
    </div>
</div>
<form id="excelForm" action="${ctxPath}/portrait/yearScore/exportExcel.do"
      method="post" target="excelIFrame">
    <input type="hidden" name="pageIndex"/>
    <input type="hidden" name="pageSize"/>
    <input type="hidden" name="filter" id="filter">
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<div class="mini-fit" style="height: 100%;">
    <div id="yearScoreListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/portrait/yearScore/yearScoreList.do" idField="id" showPager="false"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="10" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="30">序号</div>
            <div field="deptName" width="80" headerAlign="center" align="center" allowSort="false">所属部门</div>
            <div field="userName" width="80" headerAlign="center" align="center" allowSort="false">姓名</div>
            <div field="post" width="120" headerAlign="center" align="center" allowSort="false" >岗位</div>
            <div field="duty" width="80" headerAlign="center" align="center" allowSort="false" >职级</div>
            <div field="yearTechScore" width="80" headerAlign="center" align="center" allowSort="false">技术创新</div>
            <div field="yearTeamWorkScore" width="80" headerAlign="center" align="center" allowSort="false" >技术协同</div>
            <div field="yearWorkScore" width="80" headerAlign="center" align="center" allowSort="false">敬业表现</div>
            <div field="yearEmployeeScore" width="80" headerAlign="center" align="center" allowSort="false">职业发展</div>
            <div field="yearTotalScore" width="80" headerAlign="center" align="center" allowSort="false">总分</div>
        </div>
    </div>
</div>
<div id="yearSelectedWindow" title="选择绩效年份" class="mini-window" style="width:500px;height:200px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="topToolBar" style="text-align:right;">
        <a  class="mini-button"  onclick="genYearScore()">生成</a>
    </div>
    <div class="mini-fit" style="font-size: 14px">
        <form id="yearForm" method="post">
            <table cellspacing="1" cellpadding="0" class="table-detail column-six grey">
                <tr>
                    <td align="center" style="width: 10%">绩效年份：</td>
                    <td align="center" style="width: 20%">
                        <input id="yearSelect" name="reportYear" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px"  label="年度："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
                               textField="text" valueField="value" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getYearList.do"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var yearScoreListGrid = mini.get("yearScoreListGrid");
    var yearCombobox = mini.get("reportYear");
    var yearSelectedWindow=mini.get("yearSelectedWindow");
    var yearForm = new mini.Form("#yearForm");
    var permission = ${permission};
    if(!permission){
        mini.get("yearButton").setEnabled(false);
    }
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var userId = record.userId;
        var s = '<span  title="详情" onclick="viewForm(\'' + userId + '\',\'view\')">详情</span>';
            s += '<span  title="画像" onclick="viewPortrait(\'' + userId + '\',\'view\')">画像</span>';
        return s;
    }
    function exportBtn(){
        var params=[];
        var parent=$(".search-form");
        var inputAry=$("input",parent);
        inputAry.each(function(i){
            var el=$(this);
            var obj={};
            obj.name=el.attr("name");
            if(!obj.name) return true;
            obj.value=el.val();
            params.push(obj);
        });
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }
</script>
<redxun:gridScript gridId="yearScoreListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
