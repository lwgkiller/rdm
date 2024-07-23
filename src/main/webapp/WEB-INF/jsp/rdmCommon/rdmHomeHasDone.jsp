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
         allowResize="false" url="${ctxPath}/oa/personal/bpmInst/myAttendsData.do" autoLoad="false"
         idField="id" showPager="true" multiSelect="false" showColumnsMenu="true" allowAlternating="true">
        <div property="columns">
            <div field="subject" width="390" headerAlign="center" align="left" allowSort="false" renderer="descRender">事项</div>
            <div field="status"  headerAlign="center" align="left" renderer="onStatusRenderer">当前状态</div>
        </div>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var grid=mini.get("todoDatagrid");

    $(function () {
        var data = {};
        //查询
        grid.load(data);
    });

    function descRender(e) {
        var record = e.record;
        var instId=record.instId;
        var belongSubSysKey=record.belongSubSysKey;
        if (!belongSubSysKey) {
            belongSubSysKey='';
        }
        var s ='<a href="#" style="cursor: pointer;" onclick="detailShow(\'' + instId+'\',\''+belongSubSysKey+ '\')">' + record.subject + '</a>';
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

    function detailShow(instId,belongSubSysKey) {
        var pageUrl = "/rdmHome/core/bpmInstDetail.do?instId=" + instId;
        var url = "${ctxPath}/pageJumpRedirect.do?targetSubSysKey="+belongSubSysKey+"&targetUrl="+encodeURIComponent(pageUrl);
        var winObj = window.open(url);
    }

</script>
</body>
</html>
