<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>人员档案列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/portrait/document/portraitDocumentList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">所属部门: </span><input class="mini-textbox" style="width: 150px" name="deptName"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">姓名: </span>
                    <input id="recUserSelectId" name="userIds" textname="userName" class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="姓名"
                           mainfield="no"  single="false" />
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">岗位: </span><input class="mini-textbox" style="width: 150px" name="post"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">职级: </span><input class="mini-textbox" style="width: 150px" name="duty"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">年度: </span>
                    <input id="reportYear" name="reportYear" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100%;height:34px"  label="年度："
                           length="50"
                           only_read="false" required="true" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
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
            <a class="mini-button" plain="true" img="${ctxPath}/scripts/mini/miniui/res/images/add.png" onclick="compareShow">对照图</a>
            <span style="color: red">注：仅支持两条数据的对照</span>
            <div style="display: inline-block" class="separator"></div>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="documentListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/portrait/document/documentList.do" idField="id" showPager="false"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="10" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="30">序号</div>
            <div name="action" cellCls="actionIcons" width="60" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="deptName" width="80" headerAlign="center" align="center" allowSort="false">所属部门</div>
            <div field="userName" width="80" headerAlign="center" align="center" allowSort="false">姓名</div>
            <div field="post" width="120" headerAlign="center" align="center" allowSort="false" >岗位</div>
            <div field="duty" width="80" headerAlign="center" align="center" allowSort="false" >职级</div>
            <div field="techScore" width="80" headerAlign="center" align="center" allowSort="false">技术创新</div>
            <div field="teamWorkScore" width="80" headerAlign="center" align="center" allowSort="false" >技术协同</div>
            <div field="workScore" width="80" headerAlign="center" align="center" allowSort="false">敬业表现</div>
            <div field="employeeScore" width="80" headerAlign="center" align="center" allowSort="false">职业发展</div>
            <div field="totalScore" width="80" headerAlign="center" align="center" allowSort="false">总分</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var documentListGrid = mini.get("documentListGrid");
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var userId = record.userId;
        var s = '<span  title="详情" onclick="viewForm(\'' + userId + '\',\'view\')">详情</span>';
            s += '<span  title="画像" onclick="viewPortrait(\'' + userId + '\',\'view\')">画像</span>';
        return s;
    }
    function compareShow() {
        var rows = [];
        rows = documentListGrid.getSelecteds();
        if (rows.length != 2) {
            mini.alert("仅支持两条数据的对照，当前选中条数为"+rows.length);
            return;
        }
        var userId1 = rows[0].userId;
        var userId2 = rows[1].userId;
        var reportYear = mini.get('reportYear').getValue();
        var url= jsUseCtxPath +"/portrait/document/compareShowPage.do?userId1="+userId1+"&userId2="+userId2+"&reportYear="+reportYear;
        window.open(url);
    }
</script>
<redxun:gridScript gridId="documentListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
