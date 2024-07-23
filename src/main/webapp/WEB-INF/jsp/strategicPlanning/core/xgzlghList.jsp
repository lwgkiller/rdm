<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>战略规划列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/xcmgProjectManager/xgzlghList.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
</head>
<body>

<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">战略规划名称: </span>
                    <input class="mini-textbox" id="zlghName" name="zlghName">
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">规划主管部门: </span>
                    <input class="mini-textbox" id="ghzgbmName" name="ghzgbmName">
                </li>
                <li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em>展开</em>
						<i class="unfoldIcon"></i>
					</span>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearFormUpgrade()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a id="addId" class="mini-button" style="margin-right: 5px" plain="true"
                       onclick="openZlghEditWindow('add','','${type}')">新增</a>
                    <a id="deletedId" class="mini-button btn-red" style="margin-right: 5px" plain="true"
                       onclick="removeZlgh()">删除</a>
                </li>
            </ul>
            <div id="moreBox">
                <ul>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">规划负责人: </span>
                        <input class="mini-textbox" id="ghfzrName" name="ghfzrName">
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">规划年份: </span>
                        <input class="mini-textbox" id="ghnf" name="ghnf">
                    </li>
                </ul>
            </div>
        </form>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="zlghListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         onlyCheckSelection="true" allowCellWrap="true"
         url="${ctxPath}/strategicPlanning/core/xgzlgh/getZlghList.do?type=${type}" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="90" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div name="zlghName" field="zlghName" width="90" headerAlign="center" align="center" allowSort="true">
                战略规划名称
            </div>
            <div name="ghnr" field="ghnr" width="80" headerAlign="center" align="center" allowSort="true">
                主要规划内容
            </div>
            <div name="ghzgbmName" field="ghzgbmName" width="80" headerAlign="center" align="center" allowSort="true">
                规划主管部门
            </div>
            <div name="ghfzrName" field="ghfzrName" width="80" headerAlign="center" align="center" allowSort="true">
                规划负责人
            </div>
            <div name="ghnf" field="ghnf" width="60" headerAlign="center" align="center" allowSort="true">
                规划年份
            </div>
            <div name="ghwcTime" field="ghwcTime" width="80" headerAlign="center" align="center" allowSort="true">
                规划完成时间
            </div>
            <div name="ghbb" field="ghbb" width="80" headerAlign="center" align="center" allowSort="true">
                规划版本
            </div>
            <div name="ghyxqs" field="ghyxqs" width="80" headerAlign="center" align="center" allowSort="true">
                规划有效周期开始
            </div>
            <div name="ghyxqe" field="ghyxqe" width="80" headerAlign="center" align="center" allowSort="true">
                规划有效周期结束
            </div>
            <div name="xbbmName" field="xbbmName" width="80" headerAlign="center" align="center" allowSort="true">
                协办部门
            </div>
            <div name="xbbmfzrName" field="xbbmfzrName" width="80" headerAlign="center" align="center" allowSort="true">
                协办部门负责人
            </div>
            <div name="remark" field="remark" width="80" headerAlign="center" align="center" allowSort="true">
                备注
            </div>
            <div field="meetingPlanCompletion" align="center" headerAlign="center" width="50" renderer="renderAttachFile">附件
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var zlghListGrid = mini.get("zlghListGrid");
    var currentUserId = "${currentUserId}";

    var currentUserNo = "${currentUserNo}";
    var updateJlWindow = mini.get("updateJlWindow");
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    //添加列表
    var quoteLbjWindow = mini.get("quoteLbjWindow");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var importWindow = mini.get("importWindow");
    var currentUserRoles=${currentUserRoles};
    var type="${type}";

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.zlghId;
        var s = '';
        s += '<span  title="查看" onclick="openZlghEditWindow(\'detail\',\'' + id + '\')">查看</span>';
        if(record.CREATE_BY_==currentUserId){
            s += '<span  title="修改" onclick="openZlghEditWindow(\'update\',\'' + id + '\')">编辑</span>';

            s += '<span  title="删除" onclick="removeZlgh(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }else {
            s+='<span  title="修改" style="color: silver">编辑</span>';
            s+='<span  title="删除" style="color: silver">删除</span>';
        }


        return s;
    }

    function searchFrm() {
        var parent = $(".search-form");
        var inputAry = $("input", parent);
        searchByInput(inputAry);
    }

    function renderAttachFile(e) {
        var record = e.record;
        var zlghId = record.zlghId;
        if(!zlghId) {
            zlghId='';
        }
        var action='detail';
        if (record.CREATE_BY_==currentUserId){
            action='edit';
        }
        var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" ' +
            'onclick="addZlghFile(\'' + zlghId + '\',\''+action+ '\')">附件</a>';
        return s;
    }

    /**
     *清空查询
     */
    function clearFormUpgrade() {
        mini.get("zlghName").setValue("");
        mini.get("ghzgbmName").setValue("");
        mini.get("ghfzrName").setValue("");
        mini.get("ghnf").setValue("");
        zlghListGrid.load();
    }

</script>
<redxun:gridScript gridId="zlghListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>