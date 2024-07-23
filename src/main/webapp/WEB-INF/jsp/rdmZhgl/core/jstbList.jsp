<%--
  Created by IntelliJ IDEA.
  User: matianyu
  Date: 2021/2/23
  Time: 14:57
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>技术通报列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/jstbList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">标题: </span>
                    <input class="mini-textbox" id="jstbTitle" name="jstbTitle" onenter="test()"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">通报概述: </span>
                    <input class="mini-textbox" id="jstbContent" name="jstbContent" onenter="test()"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">发布时间 从 </span>:<input id="publishTimeStart"  name="publishTimeStart"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">至: </span><input  id="publishTimeEnd" name="publishTimeEnd" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a id="editMsg" class="mini-button " style="margin-right: 5px" plain="true"
                       onclick="addJstb()">发布通报</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="moban()">模板下载</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
			</span>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="jstbListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         sizeList="[50,100,200]" pageSize="50"
         url="${ctxPath}/rdmZhgl/Jstb/queryJstb.do"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <div name="action" cellCls="actionIcons" width="50" headerAlign="center" align="center"
                 renderer="onMessageActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="jstbTitle" headerAlign='center' align='left'>通报标题</div>
            <div field="numInfo" sortField="numInfo" width="80" headerAlign="center" align="center" allowSort="true">
                通报编号
            </div>
            <div field="jstbContent" headerAlign='center' align='left'>主题词或通报概述</div>
            <div field="creator" headerAlign='center' align='center' width="40">发布人</div>
            <div field="taskName" headerAlign='center' align='center' width="40">当前节点</div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="40">当前处理人</div>
            <div field="status" width="25" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer">状态
            </div>
            <div field="readStatus" headerAlign='center' align='center' width="40" renderer="readStatusRenderer">阅读情况
            </div>
            <div field="CREATE_TIME_" width="30" dateFormat="yyyy-MM-dd" headerAlign='center' align="center">发布时间</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var jstbListGrid = mini.get("jstbListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserDep = "${currentUserDep}";
    var currentUserRoles =${currentUserRoles};
    var currentUserNo = "${currentUserNo}";
    var isJsglbRespUser =${isJsglbRespUser};
    var isJSZXUser =${isJSZXUser};
    var isJSGLBUser =${isJSGLBUser};

    jstbListGrid.on("update", function (e) {
        handGridButtons(e.sender.el);
    });
    if (!isJSZXUser && currentUserNo != "admin") {
        mini.get("editMsg").setEnabled(false);
    }

    function test() {

    }

    function onMessageActionRenderer(e) {
        var record = e.record;
        var jstbId = record.jstbId;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        s += '<span  title="查看" onclick="jstbSee(\'' + jstbId + '\')">查看</span>';
        if (record.status == '草稿') {
            s += '<span  title="编辑" onclick="jstbEdit(\'' + jstbId + '\',\'' + instId + '\')">编辑</span>';
        }
        if (record.myTaskId) {
            s += '<span  title="办理" onclick="jstbTask(\'' + record.myTaskId + '\')">办理</span>';
        }
        if (status == '草稿' && currentUserId == record.CREATE_BY_) {
            s += '<span  title="删除" onclick="removeJstb(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        if (status == '发布' &&(currentUserNo=='admin' || isJSGLBUser)) {
            s += '<span  title="删除" onclick="removeJstb(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
            s += '<span  title="变更" onclick="jstbChange(\'' + jstbId + '\')">变更</span>';
            s += '<span  title="作废" onclick="cancelJstb(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">作废</span>';
        }
        return s;
    }

    function readStatusRenderer(e) {
        var record = e.record;
        var isSelf = record.jstbId;
        var result = '';
        if (record.status == "发布") {
            result = '<span style="color: green;cursor: pointer;text-decoration:underline;" title="通报阅读情况" onclick="seeReadStatus(\'' + record.jstbId + '\')">通报阅读情况</span>';
        }
        return result;
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;
        var result = '';
        if (status == "发布") {
            result = '<span style="color: deepskyblue;cursor: pointer;">发布</span>';
        }
        if (status == "草稿") {
            result = '<span style="color: gold;cursor: pointer;">草稿</span>';
        }
        if (status == "审批中") {
            result = '<span style="color: orange;cursor: pointer;">审批中</span>';
        }
        if (status == "作废") {
            result = '<span style="color: red;cursor: pointer;">作废</span>';
        }
        return result;
    }

    function seeReadStatus(jstbId) {
        mini.open({
            title: "通报阅读情况",
            url: jsUseCtxPath + "/rdmZhgl/Jstb/readStatusPage.do?jstbId=" + jstbId,
            width: 650,
            height: 550,
            allowResize: true,
            onload: function () {
            }
        });
    }
</script>
<redxun:gridScript gridId="jstbListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
