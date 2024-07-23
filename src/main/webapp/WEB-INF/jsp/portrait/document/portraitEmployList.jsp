<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>技术创新列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-fit" style="height: 100%;">
    <span>考勤</span>
    <div id="attendanceListGrid" class="mini-datagrid" style="width: 100%; height:350px;" allowResize="false" showPager="false"
         url="${ctxPath}/portrait/attendance/attendancePersonList.do?userId=${userId}&&reportYear=${reportYear}" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="10" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="indexcolumn" align="center" headerAlign="center" width="20">序号</div>
            <div field="yearMonth" width="120" headerAlign="center" align="center" allowSort="true">年月</div>
            <div field="attendanceRank" width="80" headerAlign="center" align="center" allowSort="true"   renderer="onWSwitchAttendanceRank">排名</div>
            <div field="score" width="80" headerAlign="center" align="center" allowSort="true" >得分</div>
            <div field="CREATE_TIME_" width="50" headerAlign="center" align="center" allowSort="true" >创建日期</div>
        </div>
    </div>
    <span>通报</span>
    <div id="notificationListGrid" class="mini-datagrid" style="width: 100%; height:350px;" allowResize="false" showPager="false"
         url="${ctxPath}/portrait/notice/notificationPersonList.do?userId=${userId}&&reportYear=${reportYear}" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="10" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="indexcolumn" align="center" headerAlign="center" width="20">序号</div>
            <div field="noticeType" width="120" headerAlign="center" align="center" allowSort="true" renderer="onWSwitchNoticeType">通报类型</div>
            <div field="noticeNo" width="80" headerAlign="center" align="center" allowSort="true" >通报编号</div>
            <div field="noticeReason" width="80" headerAlign="center" align="center" allowSort="true"  >通报原因</div>
            <div field="score" width="50" headerAlign="center" align="center" allowSort="false">得分</div>
            <div field="CREATE_TIME_" width="50" headerAlign="center" align="center" allowSort="true" >创建日期</div>
        </div>
    </div>
    <span>月度绩效</span>
    <div id="performanceListGrid" class="mini-datagrid" style="width: 100%; height: 350px;" allowResize="false"
         url="${ctxPath}/portrait/performance/performancePersonList.do?userId=${userId}&&reportYear=${reportYear}" idField="id" showPager="false"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="10" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="indexcolumn" align="center" headerAlign="center" width="20">序号</div>
            <div field="yearMonth" width="120" headerAlign="center" align="center" allowSort="true">年份</div>
            <div field="ratio" width="80" headerAlign="center" align="center" allowSort="true" >绩效系数</div>
            <div field="score" width="80" headerAlign="center" align="center" allowSort="false">得分</div>
            <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" allowSort="false">添加日期</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var attendanceListGrid = mini.get('attendanceListGrid');
    var notificationListGrid = mini.get('notificationListGrid');
    var performanceListGrid = mini.get('performanceListGrid');
    attendanceListGrid.load();
    notificationListGrid.load();
    performanceListGrid.load();
    var attendanceRankList = getDics("attendanceRank");
    var noticeTypeList = getDics("noticeType");
    function onWSwitchAttendanceRank(e) {
        var record = e.record;
        var attendanceRank = record.attendanceRank;
        var rankText = '';
        for(var i=0;i<attendanceRankList.length;i++){
            if(attendanceRankList[i].key_==attendanceRank){
                rankText = attendanceRankList[i].text;
                break
            }
        }
        return rankText;
    }
    function onWSwitchNoticeType(e) {
        var record = e.record;
        var noticeType = record.noticeType;
        var typeText = '';
        for(var i=0;i<noticeTypeList.length;i++){
            if(noticeTypeList[i].key_==noticeType){
                typeText = noticeTypeList[i].text;
                break
            }
        }
        return typeText;
    }
    function getDics(dicKey) {
        let resultDate = [];
        $.ajax({
            async:false,
            url: __rootPath + '/sys/core/commonInfo/getDicItem.do?dicType='+dicKey,
            type: 'GET',
            contentType: 'application/json',
            success: function (data) {
                if (data.code==200) {
                    resultDate = data.data;
                }
            }
        });
        return resultDate;
    }
</script>
</body>
</html>
