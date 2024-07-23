<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>通报列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">所属部门: </span><input
                        class="mini-textbox" style="width: 150px" name="deptName"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">姓名: </span><input
                        class="mini-textbox" style="width: 150px" name="userName"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">岗位: </span><input
                        class="mini-textbox" style="width: 150px" name="post"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">职级: </span><input
                        class="mini-textbox" style="width: 150px" name="duty"/></li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
            </ul>
        </form>
<%--        <ul class="toolBtnBox">--%>
<%--            <div style="display: inline-block" class="separator"></div>--%>
<%--            <a class="mini-button" plain="true" img="${ctxPath}/scripts/mini/miniui/res/images/add.png"--%>
<%--               onclick="addRow()">新增</a>--%>
<%--            <a class="mini-button" style="margin-left: 10px" img="${ctxPath}/scripts/mini/miniui/res/images/cancel.png"--%>
<%--               onclick="removeRow()">删除</a>--%>
<%--            <a class="mini-button" id="importId" style="margin-left: 10px" img="${ctxPath}/scripts/mini/miniui/res/images/textfield_add.png"--%>
<%--               onclick="openImportStandard()">导入</a>--%>
<%--            <div style="display: inline-block" class="separator"></div>--%>
<%--        </ul>--%>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="userListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/portrait/userInfo/userList.do" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="30">序号</div>
            <div name="action" cellCls="actionIcons" width="60" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="deptName" width="80" headerAlign="center" align="center" allowSort="false">所属部门</div>
            <div field="userName" width="80" headerAlign="center" align="center" allowSort="false">姓名</div>
            <div field="post" width="80" headerAlign="center" align="center" allowSort="false" >岗位</div>
            <div field="duty" width="80" headerAlign="center" align="center" allowSort="false" >职级</div>
            <div field="education" width="80" headerAlign="center" align="center" allowSort="true" renderer="onWSwitchEducation">学历</div>
            <div field="major" width="80" headerAlign="center" align="center" allowSort="true">专业</div>
            <div field="expert" width="50" headerAlign="center" align="center" allowSort="true"
                 renderer="onWSwitchExpert">是否技术专家
            </div>
            <div field="qualification" width="120" headerAlign="center" align="center" allowSort="false">证书</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var userListGrid = mini.get("userListGrid");
    var educationList = getDics("education");
    var expertList = getDics("YESORNO");
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var userId = record.userId;
        var s = '<span  title="查看" onclick="viewForm(\'' + userId + '\',\'view\')">查看</span>';
        s += '<span  title="编辑" onclick="viewForm(\'' + userId + '\',\'edit\')">编辑</span>';
        return s;
    }

    function onWSwitchEducation(e) {
        var record = e.record;
        var educationType = record.education;
        var educationText = '';
        for (var i = 0; i < educationList.length; i++) {
            if (educationList[i].key_ == educationType) {
                educationText = educationList[i].text;
                break
            }
        }
        return educationText;
    }

    function onWSwitchExpert(e) {
        var record = e.record;
        var expert = record.expert;
        var expertText = '';
        for (var i = 0; i < expertList.length; i++) {
            if (expertList[i].key_ == expert) {
                expertText = expertList[i].text;
                break
            }
        }
        return expertText;
    }
    function getDics(dicKey) {
        let resultDate = [];
        $.ajax({
            async: false,
            url: __rootPath + '/sys/core/commonInfo/getDicItem.do?dicType=' + dicKey,
            type: 'GET',
            contentType: 'application/json',
            success: function (data) {
                if (data.code == 200) {
                    resultDate = data.data;
                }
            }
        });
        return resultDate;
    }
    function addRow() {
        let url = jsUseCtxPath + "/portrait/knowledge/knowledgeEditPage.do?action=add";
        mini.open({
            title: "新增",
            url: url,
            width: 800,
            height: 600,
            showModal: true,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                searchFrm();
            }
        });
    }
    //查看
    function viewForm(userId,action) {
        var url= jsUseCtxPath +"/portrait/userInfo/userEditPage.do?action="+action+"&userId="+userId;
        var title = "修改";
        if(action=='view'){
            title = "查看";
        }
        mini.open({
            title: title,
            url: url,
            width: 800,
            height: 600,
            showModal: true,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                searchFrm();
            }
        });
    }
</script>
<redxun:gridScript gridId="userListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
