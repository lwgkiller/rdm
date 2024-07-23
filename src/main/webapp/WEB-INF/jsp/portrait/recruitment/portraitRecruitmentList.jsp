<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>人才招聘</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/portrait/recruitment/portraitRecruitmentList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">推荐人: </span><input class="mini-textbox" style="width: 150px" name="userName"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">人才名称: </span><input class="mini-textbox" style="width: 150px" name="resumeName"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">简历状态: </span>
                    <input id="status" name="status" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100%;height:34px"  label="简历状态："
                           length="50" onvaluechanged="searchFrm"
                           only_read="false"  allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=resumeStatus"
                           nullitemtext="请选择..." emptytext="请选择...">
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
            </ul>
        </form>
        <!--导出Excel相关HTML-->
        <form id="excelForm" action="${ctxPath}/assetsManager/core/assetReceive/export.do" method="post"
              target="excelIFrame">
            <input type="hidden" name="pageIndex" id="pageIndex"/>
            <input type="hidden" name="pageSize" id="pageSize"/>
            <input type="hidden" name="filter" id="filter"/>
        </form>
        <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
        <ul class="toolBtnBox">
            <div style="display: inline-block" class="separator"></div>
            <a id="addButton" class="mini-button" plain="true" img="${ctxPath}/scripts/mini/miniui/res/images/add.png" onclick="addRow()">新增</a>
            <a id="delButton"  class="mini-button" style="margin-left: 10px"  img="${ctxPath}/scripts/mini/miniui/res/images/cancel.png" onclick="removeRow()">删除</a>
            <div style="display: inline-block" class="separator"></div>

        </ul>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/portrait/recruitment/dataList.do" idField="id" sortField="UPDATE_TIME_" sortOrder="desc"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="30">序号</div>
            <div field="" renderer="onFileRenderer" width="100px" headerAlign="center" align="center" allowSort="true">简历附件</div>
            <div field="status" width="80" headerAlign="center" align="center" allowSort="true" renderer="onWSwitchLevel">简历状态</div>
            <div field="resumeName" width="80" headerAlign="center" align="center" allowSort="true">人才姓名</div>
            <div field="major" width="80" headerAlign="center" align="center" allowSort="true" >人才专业</div>
            <div field="userName" width="80" headerAlign="center" align="center" allowSort="true">推荐人</div>
            <div field="deptName" width="80" headerAlign="center" align="center" allowSort="true">推荐人部门</div>
            <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" allowSort="false">添加日期</div>
            <div name="action" cellCls="actionIcons" width="60" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var listGrid = mini.get("listGrid");
    var permission = ${permission};
    var currentUserId="${currentUserId}";
    var levelList = getDics("resumeStatus");
    if(!permission){
        // mini.get("addButton").setEnabled(false);
        // mini.get("delButton").setEnabled(false);
    }
    function filterDept(e) {
        var record = e.record;
        if (!record.userDeptName) {
            return record.useDept;
        }
        return record.userDeptName;
    }
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var s = '';
        if(record.CREATE_BY_==currentUserId){
            s += '<span  title="编辑" onclick="viewForm(\'' + id + '\',\'edit\')">编辑</span>';
        }else{
            s += '<span  title="编辑" style="color: silver">编辑</span>';
        }
        if(permission){
            s += '<span  title="维护状态" onclick="viewForm(\'' + id + '\',\'manage\')">维护状态</span>';
        }
        return s;
    }
    function onWSwitchLevel(e) {
        var record = e.record;
        var status = record.status;
        var levelText = '';
        for(var i=0;i<levelList.length;i++){
            if(levelList[i].key_==status){
                levelText = levelList[i].text;
                break
            }
        }
        return levelText;
    }
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
