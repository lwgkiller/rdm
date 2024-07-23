<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>荣誉管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/portrait/honor/portraitHonorList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">获奖团队/个人: </span><input class="mini-textbox" style="width: 150px" name="teamWork"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">奖项名称: </span><input class="mini-textbox" style="width: 150px" name="rewardName"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">奖项类型: </span>
                    <input id="rewardType" name="rewardType" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100%;height:34px"  label="奖项类型："
                           length="50" onvaluechanged="searchFrm"
                           only_read="false"  allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=rewardType"
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
         url="${ctxPath}/portrait/honor/dataList.do" idField="id" sortField="UPDATE_TIME_" sortOrder="desc"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="30">序号</div>
            <div field="" renderer="onFileRenderer" width="100px" headerAlign="center" align="center" allowSort="true">荣誉附件</div>
            <div field="teamWork" width="80" headerAlign="center" align="center" allowSort="true">获奖团队/个人</div>
            <div field="certNo" width="80" headerAlign="center" align="center" allowSort="true">身份证号</div>
            <div field="rewardType" width="80" headerAlign="center" align="center" allowSort="true" renderer="onWSwitchLevel">奖项类型</div>
            <div field="rewardName" width="80" headerAlign="center" align="center" allowSort="true">奖项名称</div>
            <div field="rewardDate" width="80" headerAlign="center" align="center" allowSort="true">获奖时间</div>
            <div field="judgeUnit" width="80" headerAlign="center" align="center" allowSort="true">评选单位</div>
            <div field="remark" width="150" headerAlign="center" align="center" allowSort="true">备注</div>
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
    var levelList = getDics("rewardType");
    if(!permission){
        mini.get("addButton").setEnabled(false);
        mini.get("delButton").setEnabled(false);
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
        return s;
    }
    function onWSwitchLevel(e) {
        var record = e.record;
        var rewardType = record.rewardType;
        var levelText = '';
        for(var i=0;i<levelList.length;i++){
            if(levelList[i].key_==rewardType){
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
