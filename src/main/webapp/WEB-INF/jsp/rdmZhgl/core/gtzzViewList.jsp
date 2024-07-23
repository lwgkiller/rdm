<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>重大项目列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/gtzzViewList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">创建部门: </span>
                    <input id="deptId" name="deptId" class="mini-dep rxc" plugins="mini-dep"
                           style="width:300px;height:34px" onvaluechanged="searchList()"
                           allowinput="false" textname="deptName" length="200" maxlength="200" minlen="0" single="true" initlogindep="false"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">项目名称: </span>
                    <input  name="projectName" class="mini-textbox rxc"   allowInput="true" style="width:100%;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">年度: </span>
                    <input id="projectYear" name="projectYear" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100%;height:34px"  label="年度："
                           length="50"
                           only_read="false" required="true" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false" onvaluechanged="searchFrm()"
                           textField="text" valueField="value" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getNearYearList.do"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height:  100%;">
    <div id="planListGrid" class="mini-datagrid"  allowResize="true" style="height:  100%;" sortField="UPDATE_TIME_" sortOrder="desc"
         url="${ctxPath}/rdmZhgl/core/gtzz/list.do" idField="id" showPager="true" allowCellWrap="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]"  allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn"  field="mainId" name="mainId" width="20px"></div>
            <div type="indexcolumn"   align="center" headerAlign="center" width="30px">序号</div>
            <div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">
                操作
            </div>
            <div field="projectName" name="projectName" width="200px" headerAlign="center" align="center" allowSort="false">项目名称</div>
            <div field="currentStage" name="currentStage" width="200px" headerAlign="center" align="center" allowSort="false">当前阶段</div>
            <div field="finishProcess" name="finishProcess" width="200px" headerAlign="center" align="center"renderer="onFinishProcess">完成进度</div>
            <div field="creator" name="finishProcess" width="200px" headerAlign="center" align="center" allowSort="false">负责人</div>
            <div field="deptName" name="deptName" width="200px" headerAlign="center" align="center" allowSort="false">负责部门</div>
            <div field="CREATE_TIME_"  width="200px" headerAlign="center" align="center" allowSort="false">创建日期</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUser.userId}";
    var planListGrid = mini.get("planListGrid");
    var finishProcessList = getDics("WCJD");
    // planListGrid.on("load", function () {
    //     planListGrid.mergeColumns(["mainId","deptName"]);
    // });
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var s = '<span  title="总览" onclick="viewForm(\'' + id + '\')">总览</span>';
        return s;
    }
    function onFinishProcess(e) {
        var record = e.record;
        var resultValue = record.finishProcess;
        var resultText = '';
        for (var i = 0; i < finishProcessList.length; i++) {
            if (finishProcessList[i].key_ == resultValue) {
                resultText = finishProcessList[i].text;
                break
            }
        }
        return resultText;
    }
</script>
<redxun:gridScript gridId="planListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
