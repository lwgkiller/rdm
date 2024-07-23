<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-fit" style="height: 100%;">
    <div id="todoDatagrid" class="mini-datagrid" style="width: 100%; height: 100%;" sizeList="[6,15,50,100]" pageSize="6"
         allowResize="false" url="${ctxPath}/xcmgProjectManager/report/xcmgProject/projectDelayList.do?reportType=homedesk" autoLoad="false"
         idField="id" showPager="true" multiSelect="false" showColumnsMenu="true" allowAlternating="true">
        <div property="columns">
            <div field="projectName" width="390" headerAlign="center" align="left" allowSort="false" renderer="descRender">项目名称</div>
            <div field="delayDay"  headerAlign="center" align="center" >延期天数</div>
        </div>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var grid=mini.get("todoDatagrid");
    var currentUserId = "${currentUserId}";
    var permission = ${permission};
    $(function () {
        var data = {};
        //查询
        grid.load(data);
    });

    function descRender(e) {
        var record = e.record;
        var projectId=record.projectId;
        var userId = record.userId;
        var s = '';
        if(userId==currentUserId||permission){
            s ='<a href="#" style="cursor: pointer;color: #0a7ac6" onclick="detailProject(\'' + projectId+ '\')">' + record.projectName + '</a>';
        }else{
            s ='<span>' + record.projectName + '</span>';
        }
        return s;
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
            {'key' : 'RUNNING','value' : '运行中','css' : 'green'},
            {'key' : 'SUCCESS_END','value' : '成功结束','css' : 'blue'},
            {'key' : 'DISCARD_END','value' : '作废','css' : 'red'},
            {'key' : 'ABORT_END','value' : '异常中止结束','css' : 'red'},
            {'key' : 'PENDING','value' : '挂起','css' : 'gray'}
        ];

        return $.formatItemValue(arr,status);
    }
    function detailProject(projectId) {
        var action = "detail";
        var url=jsUseCtxPath+"/xcmgProjectManager/core/xcmgProject/edit.do?action="+action+"&projectId=" + projectId+"&status=RUNNING";
        window.open(url);
    }


</script>
</body>
</html>
