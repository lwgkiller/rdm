<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 10px">
            <ul >
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">项目名称: </span>
                    <input class="mini-textbox" id="projectName" name="projectName" style="width: 300px;max-width: 300px"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="deliveryDatagrid" class="mini-datagrid" style="width: 100%; height: 100%;"
         allowResize="false" url="${ctxPath}/rdmHome/core/rdmHomeTabContent.do?name=delivery" autoLoad="true"
         idField="id" showPager="true" multiSelect="false" showColumnsMenu="true" allowAlternating="true" sizeList="[7,15,50,100]" pageSize="7"
    >
        <div property="columns">
            <div name="action" cellCls="actionIcons" headerAlign="center" align="center" width="65" renderer="onActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="projectName" sortField="CREATE_TIME_" width="200" headerAlign="center" align="left" allowSort="true">
                项目名称
            </div>
            <div field="deliveryName" headerAlign="center"  width="150"align="left">项目交付物</div>
            <div field="productNames" headerAlign="center"  width="100"align="left">设计型号</div>
            <div field="itemName" headerAlign="center"  width="100"align="left">新品计划节点</div>
            <div field="planTime" dateFormat="yyyy-MM-dd" headerAlign="center" align="left">计划完成时间</div>
        </div>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var grid = mini.get("deliveryDatagrid");


    function onActionRenderer(e) {
        var record = e.record;
            s = '<span style="color: #409EFF;cursor: pointer " title="交付物管理" onclick="editProjectDelivery(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">交付物管理</span>';
        return s;
    }

    //交付物管理
    function editProjectDelivery(record) {
        var categoryId = record.categoryId;
        var currentUserProjectRoleName="";
        //只有项目当前处理人是项目负责人的情况下，并且当前用户是项目成员，才允许操作
        if(record.status =='RUNNING' && record.allTaskUserIds.indexOf(record.respManId)!=-1) {
            //    判断当前登录人是否为该项目成员
            $.ajax({
                url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/judgeProjectMember.do?projectId='+record.projectId,
                type: 'get',
                contentType: 'application/json',
                success: function (data) {
                    if (!data || !data.result) {
                        mini.alert('只有当前登录用户为项目成员，且项目状态为“运行中”、当前任务处理人为项目负责人时，才允许操作交付物！');
                        return;
                    } else {
                        currentUserProjectRoleName=data.roleName;
                        var action = "editDelivery";
                        var url = "";
                        if(categoryId=='02'){
                            url = jsUseCtxPath + "/xcmgProjectManager/core/xcmgProject/productEditPage.do?action=" + action + "&projectId=" + record.projectId + "&status=" + record.status+"&currentUserProjectRoleName="+currentUserProjectRoleName;
                        }else{
                            url = jsUseCtxPath + "/xcmgProjectManager/core/xcmgProject/edit.do?action=" + action + "&projectId=" + record.projectId + "&status=" + record.status+"&currentUserProjectRoleName="+currentUserProjectRoleName;
                        }
                        var winObj = window.open(url);
                        var loop = setInterval(function () {
                            if (winObj.closed) {
                                clearInterval(loop);
                                if (grid) {
                                    grid.reload()
                                }
                                ;
                            }
                        }, 1000);
                    }
                }
            });
        } else {
            mini.alert('只有当前登录用户为项目成员，且项目状态为“运行中”、当前任务处理人为项目负责人时，才允许操作交付物！');
            return;
        }
    }
</script>
</body>
</html>
